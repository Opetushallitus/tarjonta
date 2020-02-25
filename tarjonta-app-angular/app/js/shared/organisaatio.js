
angular.module('Organisaatio', [
    'ngResource',
    'config',
    'Logging'
]) // "organisaatioservice"
    .factory('OrganisaatioService', function($resource, $log, $q, Config, $http, Koodisto, AuthService) {
        $log = $log.getInstance('OrganisaatioService');
        var orgHaku = $resource(window.url("organisaatio-service.search"), null, {
            get: {
                cache: true
            }
        });
        var orgLuku = $resource(window.urls().noEncode().url("organisaatio-service.byOid", ":oid"), null, {
            get: {
                cache: true
            }
        });
        /**
         * Hakee organisaatiopalvelusta ryhmät ja filtteroi niistä vain "hakukohde" käyttöön tarkoitetut ryhmät.
         * Eli jos "ryhmatyypit"-array sisältää "hakukohde" stringing.
         *
         * Esimerkkidataa:
         * https://itest-virkailija.oph.ware.fi/organisaatio-service/rest/organisaatio/v2/ryhmat
         *
         * @returns {$q@call;defer.promise}
         */
        function getRyhmat(tyyppi) {
            tyyppi = tyyppi || 'hakukohde';
            var ret = $q.defer();
            var request = $http({
                url: window.url("organisaatio-service.ryhmat"),
                method: 'GET',
                withCredentials: true,
                cache: true
            });
            request.then(function(result) {
                ret.resolve(
                    // Filter correct type
                    _.filter(result.data, function(group) {
                        return group.ryhmatyypit && group.ryhmatyypit.indexOf(tyyppi) >= 0;
                    })
                );
            });
            request.catch(function(err) {
                $log.error('got error', err);
                ret.reject(err);
            });
            return ret.promise;
        }
        function localize(organisaatio) {
            // TODO olettaa että käyttäjä suomenkielinen
            organisaationimi = organisaatio.nimi.fi || organisaatio.nimi.sv || organisaatio.nimi.en;
            organisaatio.nimi = organisaationimi;
            organisaatio.sortNimi = organisaationimi.toLowerCase();
            if (organisaatio.children) {
                localizeAll(organisaatio.children);
            }
            return organisaatio;
        }
        function localizeAll(organisaatioarray) {
            angular.forEach(organisaatioarray, localize);
            organisaatioarray.sort(function(a, b) {
                ret = a.sortNimi.localeCompare(b.sortNimi);
                return ret === 0 ? a.nimi.localeCompare(b.nimi) : ret;
            });
            return organisaatioarray;
        }
        function etsi(hakuehdot) {
            var ret = $q.defer();
            // $log.info('searching organisaatiot, q:', hakuehdot);
            orgHaku.get(hakuehdot, function(result) {
                // $log.info("resolving promise with hit count:" +
                // result.numHits);
                localizeAll(result.organisaatiot);
                ret.resolve(result);
            });
            // $log.info('past query now, returning promise...:');
            return ret.promise;
        }
        /*
         * Lisää organisaation oppilaitostyyppin (koodin uri) arrayhin jos se !=
         * undefined ja ei jo ole siinä
         */
        function addTyyppi(organisaatio, oppilaitostyypit) {
            if (organisaatio.oppilaitostyyppi !== undefined &&
                oppilaitostyypit.indexOf(organisaatio.oppilaitostyyppi) == -1) {
                oppilaitostyypit.push(organisaatio.oppilaitostyyppi);
            }
        }
        /**
         * Koulutustoimija, kerää oppilaitostyypit lapsilta (jotka oletetaan
         * olevan oppilaitoksia)
         */
        function getTyypitFromChildren(organisaatio, deferred) {
            var oppilaitostyypit = [];
            if (organisaatio.organisaatiotyypit.indexOf('KOULUTUSTOIMIJA') != -1 &&
                organisaatio.children !== undefined) {
                for (var i = 0; i < organisaatio.children.length; i++) {
                    addTyyppi(organisaatio.children[i], oppilaitostyypit);
                }
                deferred.resolve(oppilaitostyypit);
            }
            return deferred.promise;
        }

        const ophOid = '1.2.246.562.10.00000000001';

        function keraaOrganisaatioidenTyypit(organisaatios) {
            var keratytTyypit = [];
            angular.forEach(organisaatios, function(organisaatio) {
                if (organisaatio.organisaatiotyypit.indexOf('KOULUTUSTOIMIJA') !== -1) {
                    var lastenTyypit = keraaOrganisaatioidenTyypit(organisaatio.children);
                    angular.forEach(lastenTyypit, function(tyyppi) {
                       if (keratytTyypit.indexOf(tyyppi) == -1) {
                           keratytTyypit.push(tyyppi);
                       }
                    });
                }
                if (organisaatio.organisaatiotyypit.indexOf('OPPILAITOS') !== -1) {
                    if (organisaatio.oppilaitostyyppi !== undefined) {
                        var tyyppi = organisaatio.oppilaitostyyppi;
                        if (keratytTyypit.indexOf(tyyppi) == -1) {
                            keratytTyypit.push(tyyppi);
                        }
                    } else {
                        console.log('WARN oppilaitoksella ei vaikuta olevan tyyppiä! ', organisaatio)
                    }
                }
            });
            return keratytTyypit;
        }

        /*
         * Hakee oppilaitostyypit organisaatiolle, koulutustoimijalle haetaan
         * allaolevista oppilaitoksista, oppilaitoksen tyypit tulee
         * oppilaitokselta, toimipisteen tyyppi typee ylemmän tason
         * oppilaitokselta. TODO lisää testi
         */
        function haeOppilaitostyypit(organisaatioOid) {
            console.log('haeOppilaitostyypit', organisaatioOid);
            var deferred = $q.defer();
            // hae org (ja sen alapuoliset)
            etsi({
                aktiiviset: true,
                suunnitellut: true,
                oidRestrictionList: organisaatioOid
            }).then(function(data) {
                if (organisaatioOid === ophOid) {
                    console.log('handling top organization, collecting and returning.');
                    var keratytTyypit = keraaOrganisaatioidenTyypit(data.organisaatiot);
                    console.log('keratyt tyypit: ', keratytTyypit);
                    deferred.resolve(keratytTyypit);
                    return deferred.promise;
                }  else {
                    console.log('got organisaatiot ', data);
                    console.log('organisaatiot size: ', data.organisaatiot.length);
                    var organisaatio = data.organisaatiot[0]; //fixme, tämä ei toimi useissa keisseissä oikein.
                    console.log('chose organisaatio ', organisaatio);
                    if (organisaatio.organisaatiotyypit.indexOf('KOULUTUSTOIMIJA') != -1) {
                        console.log('getting tyypit from children for organisaatio', organisaatio);
                        return getTyypitFromChildren(organisaatio, deferred);
                    }
                    if (organisaatio.organisaatiotyypit.indexOf('OPPILAITOS') != -1) {
                        // oppilaitos, palauta tyyppi tästä
                        var oppilaitostyypit = [];
                        addTyyppi(organisaatio, oppilaitostyypit);
                        deferred.resolve(oppilaitostyypit);
                        console.log('returning tyypit ', oppilaitostyypit);
                        return deferred.promise;
                    }
                    if (organisaatio.organisaatiotyypit.indexOf('TOIMIPISTE') != -1) {
                        $log.debug('toimipiste or varhaiskasvatuksen_jarjestaja, recurse...');
                        deferred.resolve(haeOppilaitostyypit(organisaatio.parentOid));
                    }
                    if (organisaatio.organisaatiotyypit.indexOf('VARHAISKASVATUKSEN_JARJESTAJA') != -1) {
                        console.log('varhaiskasvatuksen jarjestaja, returning empty set');
                        deferred.resolve([]);
                    }

                }
            });
            console.log('returning promise... ', deferred.promise);
            return deferred.promise;
        }

        function getAllowedKoulutustyypit(orgOids) {
            console.log('getAllowedKoulutustyypit ', orgOids);
            var deferred = $q.defer();

            var promises = [];
            _.each(orgOids, function(orgOid) {
                promises.push(haeOppilaitostyypit(orgOid));
            });

            $q.all(promises).then(function(oppilaitostyypit) {
                console.log('oppilaitostyypit ', oppilaitostyypit);
                oppilaitostyypit = _.chain(oppilaitostyypit).flatten().compact().uniq().value();
                console.log('oppilaitostyypit, after ', oppilaitostyypit);
                Koodisto.getAlapuolisetKoodiUrit(oppilaitostyypit, 'koulutustyyppi').then(function(response) {
                    deferred.resolve(_.chain(response.uris).flatten().compact().uniq().value());
                });
                console.log('oppilaitostyypit, after alapuoliset ', oppilaitostyypit);

            });

            return deferred.promise;
        }

        function byOid(oid) {
            return orgLuku.get({
                oid: oid
            }).$promise.then(function(result) {
                    // Muodosta oidista ja parentOidPathista taulukko
                    // tätä tietoa käytetään monessa paikassa käyttöoikeuksia varten
                    var oids = [result.oid];
                    if (result.parentOidPath) {
                        oids = _.chain(oids).union(result.parentOidPath.split('|')).compact().value();
                    }
                    result.oidAndParentOids = oids;
                    return localize(result);
                });
        }

        /**
         * Palauta hakijapalveluiden yhteystiedot. Tiedot haetaan yläorganisaatiosta
         * siinä tapauksessa, että ne puuttuvat organisaatiolta.
         * @param {string} oid
         */
        function getHakijapalveluidenYhteystiedot(oid) {
            var deferred = $q.defer();

            byOid(oid).then(function(org) {
                org.metadata = org.metadata || {};
                if (org.metadata.yhteystiedot && org.metadata.yhteystiedot.length > 0) {
                    _.each(org.metadata.hakutoimistonNimi, function(nimi, kieli) {
                        org.metadata.yhteystiedot.push({
                            kieli: kieli,
                            hakutoimistonNimi: nimi
                        });
                    });
                    deferred.resolve(org.metadata.yhteystiedot);
                }
                else {
                    if (!org.parentOid || org.parentOid === Config.env['root.organisaatio.oid']) {
                        deferred.resolve([]);
                    }
                    else {
                        getHakijapalveluidenYhteystiedot(org.parentOid).then(function(yhteystiedot) {
                            deferred.resolve(yhteystiedot);
                        });
                    }
                }
            });

            return deferred.promise;
        }

        return {
            haeOppilaitostyypit: haeOppilaitostyypit,
            /**
             * query (hakuehdot)
             *
             * @param hakuehdot,
             *                muodossa: (java OrganisaatioSearchCriteria
             *                -luokka) { "searchStr" : "", "organisaatiotyyppi" :
             *                "", "oppilaitostyyppi" : "", "lakkautetut" :
             *                false, "suunnitellut" : false }
             *
             * @returns promise
             */
            etsi: etsi,
            /**
             * Suomentaa organisaation
             */
            localize: localize,
            /**
             * Hakee organisaatiolle voimassaolevan localen mukaisen nimen.
             *
             * @param {string} oid
             * @returns promise
             */
            nimi: function(oid) {
                return orgLuku.get({
                    oid: oid
                }).$promise.then(function(result) {
                    var nimi = localize(result).nimi;
                    return nimi;
                });
            },
            byOid: byOid,
            getHakijapalveluidenYhteystiedot: getHakijapalveluidenYhteystiedot,
            /**
             * Palauttaa ECTS yhteyshenkilön organisaatiolle (promise)
             */
            getECTS: function(oid) {
                return orgLuku.get({
                    oid: oid
                }).$promise.then(function(result) {
                    return localize(result);
                });
            },
            /**
             * Palauttaa organisaatioryhmat promisen. (organisaatioita)
             */
            getRyhmat: getRyhmat,
            oidToOrgMap: {},
            getPopulatedOrganizations: function(organizationOids, firstOrganizationOidInList) {
                var defer = $q.defer();
                var promises = [];
                var organizations = [];
                var that = this;
                function getOrganizationName(oid) {
                    var defer = $q.defer();
                    if (that.oidToOrgMap[oid]) {
                        organizations.push(that.oidToOrgMap[oid]);
                        defer.resolve();
                    }
                    else {
                        that.byOid(oid).then(function(org) {
                            that.oidToOrgMap[oid] = org;
                            organizations.push(org);
                            defer.resolve();
                        });
                    }
                    return defer.promise;
                }
                angular.forEach(organizationOids, function(oid) {
                    promises.push(getOrganizationName(oid));
                });
                $q.all(promises).then(function() {
                    if (firstOrganizationOidInList) {
                        // Sort organizations so that firstOrganizationOidInList is first in the array
                        var index = 0;
                        angular.forEach(organizations, function(org, i) {
                            if (org.oid === firstOrganizationOidInList) {
                                index = i;
                            }
                        });
                        var shouldBeFirst = organizations.splice(index, 1);
                        organizations.unshift(shouldBeFirst[0]);
                    }
                    return defer.resolve(organizations);
                });
                return defer.promise;
            },
            getAllowedKoulutustyypit: getAllowedKoulutustyypit,
            buildOrganizationSelectionDialog: function(oidRestrictionList) {
                var deferred = $q.defer();

                etsi({
                    aktiiviset: true,
                    suunnitellut: true,
                    oidRestrictionList: oidRestrictionList || AuthService.getOrganisations()
                }).then(function(response) {
                    var organizationMap = {};
                    function buildOrganizationMap(organizations) {
                        _.each(organizations, function(organization) {
                            organizationMap[organization.oid] = organization;
                            if (organization.children) {
                                buildOrganizationMap(organization.children);
                            }
                        });
                    }
                    buildOrganizationMap(response.organisaatiot);

                    deferred.resolve({
                        organizations: response.organisaatiot,
                        organizationMap: organizationMap
                    });
                });

                return deferred.promise;
            }
        };
    });