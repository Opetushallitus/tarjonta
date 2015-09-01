angular.module('Organisaatio', [
    'ngResource',
    'config',
    'Logging'
]) // "organisaatioservice"
    .factory('OrganisaatioService', function($resource, $log, $q, Config, $http, Koodisto) {
        $log = $log.getInstance('OrganisaatioService');
        var orgHaku = $resource(Config.env['organisaatio.api.rest.url'] + 'organisaatio/hae', null, {
            get: {
                cache: true
            }
        });
        var orgLuku = $resource(Config.env['organisaatio.api.rest.url'] + 'organisaatio/:oid', null, {
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
                url: Config.env['organisaatio.api.rest.url'] + 'organisaatio/v2/ryhmat',
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
        /*
         * Hakee oppilaitostyypit organisaatiolle, koulutustoimijalle haetaan
         * allaolevista oppilaitoksista, oppilaitoksen tyypit tulee
         * oppilaitokselta, toimipisteen tyyppi typee ylemmän tason
         * oppilaitokselta. TODO lisää testi
         */
        function haeOppilaitostyypit(organisaatioOid) {
            var deferred = $q.defer();
            // hae org (ja sen alapuoliset)
            etsi({
                oidRestrictionList: organisaatioOid
            }).then(function(data) {
                var organisaatio = data.organisaatiot[0];
                if (organisaatio.organisaatiotyypit.indexOf('KOULUTUSTOIMIJA') != -1) {
                    return getTyypitFromChildren(organisaatio, deferred);
                }
                if (organisaatio.organisaatiotyypit.indexOf('OPPILAITOS') != -1) {
                    // oppilaitos, palauta tyyppi tästä
                    var oppilaitostyypit = [];
                    addTyyppi(organisaatio, oppilaitostyypit);
                    deferred.resolve(oppilaitostyypit);
                    return deferred.promise;
                }
                if (organisaatio.organisaatiotyypit.indexOf('TOIMIPISTE') != -1) {
                    $log.debug('toimipiste, recurse...');
                    deferred.resolve(haeOppilaitostyypit(organisaatio.parentOid));
                }
            });
            return deferred.promise;
        }

        function getAllowedKoulutustyypit(orgOids) {
            var deferred = $q.defer();

            var promises = [];
            _.each(orgOids, function(orgOid) {
                promises.push(haeOppilaitostyypit(orgOid));
            });

            $q.all(promises).then(function(oppilaitostyypit) {
                oppilaitostyypit = _.chain(oppilaitostyypit).flatten().compact().uniq().value();

                Koodisto.getAlapuolisetKoodiUrit(oppilaitostyypit, 'koulutustyyppi').then(function(response) {
                    deferred.resolve(_.chain(response.uris).flatten().compact().uniq().value());
                });
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
            getAllowedKoulutustyypit: getAllowedKoulutustyypit
        };
    });