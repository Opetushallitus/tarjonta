var app = angular.module('Tarjonta', [
    'ngResource',
    'config',
    'Logging'
]);
app.factory('TarjontaService', function($resource, $http, Config, LocalisationService, Koodisto,
                                        CacheService, $q, $log, OrganisaatioService, AuthService, dialogService) {
    var plainUrls = window.urls().noEncode();
    $log = $log.getInstance('TarjontaService');
    var hakukohdeHaku = $resource(window.url("tarjonta-service.hakukohde.haku"));
    var koulutusHaku = $resource(window.url("tarjonta-service.koulutus.haku"));

    function localize(txt, opetuskielet) {
        if (txt === undefined || txt === null) {
            return txt;
        }
        var userLocale = LocalisationService.getLocale();
        var preferredLang = _.findWhere(opetuskielet, 'kieli_' + userLocale) || _.first(opetuskielet);
        preferredLang = (preferredLang || '').replace('kieli_', '');
        return txt[preferredLang] || txt[userLocale] || txt.fi || txt.sv || txt.en;
    }
    function compareByName(a, b) {
        var an = a.nimi;
        var bn = b.nimi;
        if (!an) {
            $log.debug('cannot compare ', a, ' with ', b);
            return -1;
        }
        /*
             * if a.nimi is null/undefined : 'Cannot call method 'localeCompare' of undefined'
             */
        return an.localeCompare(bn);
    }
    function searchCacheKey(prefix, args) {
        return {
            key: prefix + '/?' + 'oid=' + args.oid + '&' + 'terms=' + escape(args.terms) + '&' +
                'state=' + escape(args.state) + '&' + 'season=' + escape(args.season) + '&' +
                'komoOid=' + escape(args.komoOid) + '&' + 'kooulutusOid=' + escape(args.koulutusOid) +
                '&' + 'hakukohdeOid=' + escape(args.hakukohdeOid) + '&' + 'hakuOid=' + escape(args.hakuOid) +
                '&' + 'year=' + escape(args.year) +
                '&type=' + escape(args.type),
            expires: 60000,
            pattern: prefix + '/.*'
        };
    }
    function getTilat() {
        return window.CONFIG.env['tarjonta.tila'];
    }
    /**
       * Kutsuu "/hakukohde/ryhmat/operate" POST metodia parametreilla:
       * <pre>
       * [
       *   {
       *      hakukohdeOid: "1.2.3.4",
       *      ryhmaOid: "5.6.7.8",
       *      toiminto: "LISAA"
       *   },
       *   {
       *      hakukohdeOid: "1.2.3.4",
       *      ryhmaOid: "5.6.7.8",
       *      toiminto: "POISTA"
       *   },
       *   ...
       * ]
       * </pre>
       *
       * @param {type} params
       * @returns promise
       */
    function hakukohdeRyhmaOperaatiot(params) {
        $log.debug('hakukohdeRyhmaOperaatiot()');
        var resource = $resource(window.url("tarjonta-service.hakukohde.ryhmaoperaatiot"), {}, {
            post: {
                method: 'POST',
                withCredentials: true
            }
        });
        return resource.post(params).$promise;
    }
    /**
       * Poista ryhmä hakukohteelta
       *
       */
    function poistaHakukohderyhma(hakukohdeOid, ryhmaOid) {
        var data = [{
            toiminto: 'POISTA',
            hakukohdeOid: hakukohdeOid,
            ryhmaOid: ryhmaOid
        }];
        return hakukohdeRyhmaOperaatiot(data);
    }
    var dataFactory = {
        getTilat: getTilat,
        hakukohdeRyhmaOperaatiot: hakukohdeRyhmaOperaatiot,
        poistaHakukohderyhma: poistaHakukohderyhma
    };
    dataFactory.acceptsTransition = function(from, to) {
        var s = window.CONFIG.env['tarjonta.tila'][from];
        return s !== null && s.transitions.indexOf('to') >= 0;
    };
    dataFactory.haeHakukohteet = function(args) {
        var params = {
            searchTerms: args.terms,
            organisationOid: args.oid,
            tila: args.state,
            hakukohdeOid: args.hakukohdeOid,
            alkamisKausi: args.season,
            alkamisVuosi: args.year,
            hakuOid: args.hakuOid,
            defaultTarjoaja: args.defaultTarjoaja,
            koulutustyyppi: args.koulutustyyppi ? args.koulutustyyppi : null,
            koulutusmoduuliTyyppi: args.type,
            hakutapa: args.hakutapa,
            hakutyyppi: args.hakutyyppi ? args.hakutyyppi : null,
            koulutuslaji: args.koulutuslaji ? args.koulutuslaji : null,
            kohdejoukko: args.kohdejoukko ? args.kohdejoukko : null,
            oppilaitostyyppi: args.oppilaitostyyppi ? args.oppilaitostyyppi : null,
            kunta: args.kunta ? args.kunta : null,
            opetuskielet: args.kieli ? args.kieli : null,
            organisaatioRyhmaOid: args.hakukohderyhma,
            koulutusOid: args.koulutusOid
        };
        _.each(['offset', 'limit'], function(key) {
            if (angular.isDefined(args[key])) {
                params[key] = args[key];
            }
        });
        return CacheService.lookupResource(searchCacheKey('hakukohde', args), hakukohdeHaku, params, function(result) {
            result = result.result;
            // unwrap v1
            for (var i in result.tulokset) {
                var t = result.tulokset[i];
                t.nimi = localize(t.nimi);
                for (var j in t.tulokset) {
                    var r = t.tulokset[j];
                    if (t.nimi === null || typeof t.nimi === 'undefined') {
                        r.nimi = t.oid;
                    }
                    else {
                        r.nimi = localize(r.nimi, r.opetuskielet);
                    }
                    r.koulutuslaji = localize(r.koulutuslaji);
                    r.hakutapa = localize(r.hakutapa);
                    r.tilaNimi = LocalisationService.t('tarjonta.tila.' + r.tila);
                }
                t.tulokset.sort(compareByName);
            }
            result.tulokset.sort(compareByName);
            return result;
        });
    };
    dataFactory.haeKoulutukset = function(args) {
        var params = {
            searchTerms: args.terms,
            organisationOid: args.oid,
            koulutusOid: args.koulutusOid,
            komoOid: args.komoOid,
            tila: args.state,
            alkamisKausi: args.season,
            alkamisVuosi: args.year,
            koulutustyyppi: args.koulutustyyppi ? args.koulutustyyppi : null,
            koulutusmoduuliTyyppi: args.type,
            hakutapa: args.hakutapa ? args.hakutapa : null,
            hakutyyppi: args.hakutyyppi ? args.hakutyyppi : null,
            koulutuslaji: args.koulutuslaji ? args.koulutuslaji : null,
            opetuskielet: args.kieli ? args.kieli : null,
            kohdejoukko: args.kohdejoukko ? args.kohdejoukko : null,
            oppilaitostyyppi: args.oppilaitostyyppi ? args.oppilaitostyyppi : null,
            kunta: args.kunta ? args.kunta : null,
            hakukohderyhma: args.hakukohderyhma,
            hakukohdeOid: args.hakukohdeOid,
            toteutustyyppi: args.toteutustyyppi
        };
        if (args.defaultTarjoaja) {
            params.defaultTarjoaja = args.defaultTarjoaja;
        }
        if (args.opetusJarjestajat) {
            params.opetusJarjestajat = args.opetusJarjestajat;
        }
        return CacheService.lookupResource(searchCacheKey('koulutus', args), koulutusHaku, params, function(result) {
            result = result.result;
            //unwrap v1
            for (var i in result.tulokset) {
                var validKoulutukset = [];
                var t = result.tulokset[i];
                t.nimi = t.nimi ? localize(t.nimi) : t.oid;
                for (var j in t.tulokset) {
                    var r = t.tulokset[j];
                    if (t.nimi) {
                        r.nimi = localize(r.nimi, r.opetuskielet);
                        if (!_.contains(['LUKIOKOULUTUS',
                                         'LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA',
                                         'EB_RP_ISH',
                                         'AIKUISTEN_PERUSOPETUS',
                                         'AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA',
                                         'AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER',
                                         'VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS'], r.toteutustyyppiEnum)
                            && r.pohjakoulutusvaatimus) {
                            r.nimi += ', ' + localize(r.pohjakoulutusvaatimus, r.opetuskielet);
                        }
                    }
                    else {
                        r.nimi = r.oid;
                    }
                    r.tilaNimi = LocalisationService.t('tarjonta.tila.' + r.tila);
                    r.koulutuslaji = localize(r.koulutuslaji);
                    validKoulutukset.push(r);
                }
                t.tulokset = validKoulutukset;
                t.tulokset.sort(compareByName);
            }
            result.tulokset.sort(compareByName);
            return result;
        });
    };
    dataFactory.evictHakutulokset = function() {
        CacheService.evict({
            pattern: 'hakutulos/.*'
        });
        CacheService.evict({
            pattern: 'koulutus/.*'
        });
    };
    /**
       * Asettaa koulutuksen tai hakukohteen julkaisun tilan.
       * @param {string} type "koulutus" | "hakukohde"
       * @param {string} oid kohteen oid
       * @param {boolean} publish tosi, jos julkaistaan, epätosi jos perutaan julkaisu
       * @return promise, jonka arvo on kohteen tila on (muutoksen jälkeen) sama kuin publish-parametrilla annettu
       */
    dataFactory.togglePublished = function(type, oid, publish) {
        var ret = $q.defer();
        var tila = $resource(plainUrls.url("tarjonta-service.togglePublished", type, oid, (publish ? 'JULKAISTU' : 'PERUTTU')),
            {}, {
            update: {
                method: 'POST',
                withCredentials: true
            }
        });
        tila.update(function(response) {
            var error = _.first(response.errors);
            if (error) {
                dialogService.showDialog({
                    ok: LocalisationService.t('ok'),
                    cancel: null,
                    title: LocalisationService.t(error.errorMessageKey + '.title'),
                    description: LocalisationService.t(error.errorMessageKey + '.description')
                });
                ret.reject();
            }
            else {
                ret.resolve(response.result);
            }
        });
        return ret.promise;
    };
    function flattenSearchResults(results) {
        return _.chain(results).map(function(resultsForTarjoaja) {
            return _.map(resultsForTarjoaja.tulokset, function(row) {
                return _.extend(row, {
                    tarjoaja: {
                        oid: resultsForTarjoaja.oid,
                        nimi: resultsForTarjoaja.nimi
                    }
                });
            });
        }).flatten(true).value();
    }
    dataFactory.getKoulutuksenHakukohteet = function(koulutusOid) {
        var deferred = $q.defer();
        dataFactory.haeHakukohteet({
            koulutusOid: koulutusOid
        }).then(function(data) {
            deferred.resolve(
                flattenSearchResults(data.tulokset)
            );
        });
        return deferred.promise;
    };
    dataFactory.getHakukohteenKoulutukset = function(hakukohdeOid) {
        var deferred = $q.defer();
        dataFactory.haeKoulutukset({
            hakukohdeOid: hakukohdeOid
        }).then(function(data) {
            deferred.resolve(
                flattenSearchResults(data.tulokset)
            );
        });
        return deferred.promise;
    };
    dataFactory.koulutus = function(oid) {
        return $resource(window.url("tarjonta-service.koulutus.byOid", ""), {}, {
            update: {
                method: 'POST',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            save: {
                method: 'POST',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            remove: {
                url: plainUrls.url("tarjonta-service.koulutus.byOid", ":oid"),
                method: 'DELETE',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            copyAndMove: {
                url: window.url("tarjonta-service.koulutus.siirraByOid", oid),
                method: 'POST',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            copyAndMoveMultiple: {
                url: window.url("tarjonta-service.koulutus.siirra"),
                method: 'POST',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            }
        });
    };
    dataFactory.loadKuvausTekstis = function(oid) {
        $log.debug('save KomoTekstis(): ', oid);
        var ret = $q.defer();
        var KomoTekstis = new $resource(plainUrls.url("tarjonta-service.koulutus.tekstis", ":oid"), {
            'oid': '@oid'
        });
        KomoTekstis.get({
            'oid': oid
        }, function(res) {
            ret.resolve(res);
        });
        return ret.$promise;
    };
    dataFactory.saveKomoTekstis = function(komoOid, params) {
        var resource = new $resource(plainUrls.url("tarjonta-service.komo.tekstis", ":oid"), {
            'oid': komoOid
        }, {
            post: {
                method: 'POST',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            }
        });
        return resource.post(params).$promise;
    };
    dataFactory.getHakukohde = function(id) {
        $log.debug('getHakukohde(): id = ', id);
        var ret = $q.defer();
        $resource(window.url("tarjonta-service.hakukohde.getHakukohde", id), function(result) {
            ret.resolve(result);
        });
        return ret.promise;
    };
    dataFactory.deleteHakukohde = function(id) {
        $log.debug('deleteHakukohde(): ', id);
        var ret = $q.defer();
        $resource(window.url("tarjonta-service.hakukohde.byOid", id)).remove({}, function(res) {
            ret.resolve(res);
        });
        return ret.promise;
    };
    dataFactory.getKoulutus = function(arg, func) {
        //param meta=false filter all meta fields
        var koulutus = $resource(plainUrls.url("tarjonta-service.koulutus.byOid", ":oid", {img:true}), {
            oid: '@oid'
        });
        return koulutus.get(arg, func);
    };
    //hakee koulutuksen, palauttaa promisen
    dataFactory.getKoulutusPromise = function(oid) {
        return $resource(plainUrls.url("tarjonta-service.koulutus.byOid", oid, {img:true})).get().$promise;
    };

    function includesOwnOrganizations(ownOrganizations, candidateOrganizations) {
        return _.intersection(ownOrganizations, candidateOrganizations).length > 0;
    }

    dataFactory.getJarjestajaCandidates = function(oid) {
        var deferred = $q.defer();

        dataFactory.getKoulutus({
            oid: oid
        }).$promise.then(function(response) {
            var koulutus = response.result;
            var orgOids = koulutus.opetusJarjestajat;
            var ownOrganizations = AuthService.getOrganisations();

            var promises = _.map(orgOids, function(orgOid) {
                var deferred = $q.defer();

                OrganisaatioService.byOid(orgOid).then(function(org) {
                    deferred.resolve(includesOwnOrganizations(ownOrganizations, org.oidAndParentOids));
                });

                return deferred.promise;
            });

            $q.all(promises).then(function(orgPermissions) {
                deferred.resolve(orgOids.filter(function(value, index) {
                    return orgPermissions[index];
                }));
            });
        });

        return deferred.promise;
    };

    dataFactory.getKoulutuskoodiRelations = function(arg, func) {
        $log.debug('getKoulutuskoodiRelations()');
        var koulutus = $resource(plainUrls.url("tarjonta-service.koulutus.koulutuskoodiRelations", ":uri", ":koulutustyyppi", ":languageCode"), {
            koulutustyyppi: '@koulutustyyppi',
            uri: '@uri',
            defaults: '@defaults',
            //optional data : string like 'object-field1:uri, object-field2:uri, ...';
            languageCode: '@languageCode'
        });
        return koulutus.get(arg, func);
    };

    function createEmptyKoulutus(oid) {
        return OrganisaatioService.byOid(oid).then(function(org) {
            return [{
                org: org,
                tila: 'EI_JARJESTETTY'
            }];
        });
    }

    function createKoulutusListing(koulutukset, jarjestajat) {
        var deferred = $q.defer();

        var listingPromises = _.map(jarjestajat, function(jarjestajaOrgOid) {
                var jarjestajanKoulutukset = _.filter(koulutukset, function(koulutus) {
                    return _.contains(koulutus.org.oidAndParentOids, jarjestajaOrgOid);
                });
                if (jarjestajanKoulutukset.length === 0) {
                    jarjestajanKoulutukset = createEmptyKoulutus(jarjestajaOrgOid);
                }
                return $q.when(jarjestajanKoulutukset);
            });

        $q.all(listingPromises).then(function(groupedKoulutukset) {
            deferred.resolve(_.flatten(groupedKoulutukset));
        });

        return deferred.promise;
    }

    dataFactory.getJarjestettavatKoulutukset = function(tarjoajanKoulutusOid, jarjestajat) {
        var deferred = $q.defer();
        var jarjestettavatKoulutukset = $resource(window.url("tarjonta-service.koulutus.jarjestettavatKoulutukset", tarjoajanKoulutusOid));

        jarjestettavatKoulutukset.get().$promise.then(function(data) {
            var koulutukset = data.result;
            var orgPromises = _.map(koulutukset, function(koulutus) {
                return OrganisaatioService.byOid(koulutus.tarjoajat[0]);
            });

            $q.all(orgPromises).then(function(orgs) {
                return _.map(koulutukset, function(koulutus, i) {
                    koulutus.org = orgs[i];
                    return koulutus;
                });
            }).then(function() {
                _.each(koulutukset, function(koulutus) {
                    koulutus.isOrphan = _.intersection(jarjestajat, koulutus.org.oidAndParentOids).length === 0;
                });
                createKoulutusListing(koulutukset, jarjestajat).then(function(koulutusListing) {
                    deferred.resolve({
                        koulutukset: koulutusListing,
                        orphanKoulutukset: _.where(koulutukset, {isOrphan: true})
                    });
                });
            });
        });

        return deferred.promise;
    };
    dataFactory.resourceKomoKuvaus = function(komotoOid) {
        return $resource(plainUrls.url("tarjonta-service.koulutus.komotekstis", ":oid"), {
            'oid': komotoOid
        }, {
            update: {
                method: 'PUT',
                withCredentials: true,
                isArray: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            save: {
                method: 'POST',
                withCredentials: true,
                isArray: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            get: {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            }
        });
    };
    dataFactory.saveImage = function(komotoOid, kieliuri, image, fnSuccess, fnError) {
        if (angular.isUndefined(komotoOid) || komotoOid === null) {
            throw new Error('Komoto OID cannot be undefined or null.');
        }
        if (angular.isUndefined(kieliuri) || kieliuri === null) {
            throw new Error('Language URI cannot be undefined or null.');
        }
        if (angular.isUndefined(image) || image === null) {
            return;
        }
        if (!angular.isUndefined(image.file) && !angular.isUndefined(image.file.type) &&
                !angular.isUndefined(image.dataURL)) {
            var apiImg = {
                kieliUri: kieliuri
            };
            if (!angular.isUndefined(image.file.name)) {
                apiImg.filename = image.file.name;
            }
            else {
                apiImg.filename = '';
            }
            apiImg.mimeType = image.file.type;
            apiImg.base64data = image.dataURL;
            //TODO: remove data:image/xxx stuff from the raw image data.
            //curently data cleaning is done in service
            //var b = window.atob(img.base64data);
            $http.post(window.url("tarjonta-service.koulutus.kuva", komotoOid), apiImg, {
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            }).success(fnSuccess).error(fnError);
        }
    };
    dataFactory.resourceImage = function(komotoOid, kieliuri) {
        if (angular.isUndefined(komotoOid) || komotoOid === null) {
            throw new Error('Komoto OID cannot be undefined or null.');
        }
        if (angular.isUndefined(kieliuri) || kieliuri === null) {
            throw new Error('Language URI cannot be undefined or null.');
        }
        var ResourceImge = $resource(plainUrls.url("tarjonta-service.koulutus.kuvaLang", ":oid", ":uri"), {
            'oid': komotoOid,
            'uri': kieliuri
        }, {
            get: {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            'delete': {
                method: 'DELETE',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            }
        });
        return ResourceImge;
    };
    dataFactory.saveResourceLink = function(parent, child, fnSuccess, fnError) {
        $log.debug('save resourceLink called!');
        dataFactory.resourceLink.save({
            parent: parent,
            children: angular.isArray(child) ? child : [child]
        }, fnSuccess, fnError);
    };
    /**
       * Linkityspalvelu -resurssi (palauttaa promisen)
       *
       * -get: listaa lapset (vain oidit)
       *    param: {oid:"oid"}
       * -save: tee liitos
       *    param: {parent:"oid", children:["oid", "oid2"]}
       * -test: testaa liitos
       *    param: {parent:"oid", children:["oid", "oid2"]}
       * -parents: listaa parentit (vain oidit)
       *    param: {child:"oid"}
       * -delete: poista liitos
       *    param: {parent:"oid", child:"oid"}
       *
       * </pre>
       */
    dataFactory.resourceLink = $resource(plainUrls.url("tarjonta-service.link.link", ":oid"), {}, {
        checkput: {
            headers: {
                'Content-Type': 'application/json; charset=UTF-8'
            }
        },
        save: {
            headers: {
                'Content-Type': 'application/json; charset=UTF-8'
            },
            method: 'POST'
        },
        test: {
            url: plainUrls.url("tarjonta-service.link.link", "test"),
            headers: {
                'Content-Type': 'application/json; charset=UTF-8'
            },
            method: 'POST'
        },
        parents: {
            url: plainUrls.url("tarjonta-service.link.parents", ":oid"),
            isArray: false,
            method: 'GET'
        },
        remove: {
            method: 'DELETE',
            url: plainUrls.url("tarjonta-service.link.remove", ":parent", ":child")
        },
        removeMany: {
            method: 'DELETE',
            url: plainUrls.url("tarjonta-service.link.link", ":parent")
        }
    });
    /**
    * Hakee koulutukset, palauttaa promisen joka täytetään koulutuslistalla
    * oidRetrievePromise = promise joka resolvautuu oidilistalla (ks getParentKoulutukset, getChildKoulutukset).
    */
    dataFactory.getKoulutuksetPromise = function(oidRetrievePromise) {
        var deferred = $q.defer();

        function getKoulutukset(oidList) {
            var promises = [];
            var koulutukset = [];
            _.each(oidList, function(parentOid) {
                var promise = dataFactory.haeKoulutukset({
                    komoOid: parentOid
                }).then(function(result) {
                    if (result.tulokset && result.tulokset.length > 0) {
                        if (koulutukset.indexOf(result.tulokset[0]) == -1) {
                            koulutukset.push(result.tulokset[0]);
                        }
                    }
                });
                promises.push(promise);
            });
            $q.all(promises).then(function() {
                deferred.resolve(koulutukset);
            });
        }

        if (angular.isArray(oidRetrievePromise)) {
            getKoulutukset(oidRetrievePromise);
        }
        else {
            oidRetrievePromise.then(function(parentOids) {
                getKoulutukset(parentOids.result);
            });
        }

        return deferred.promise;
    };
    /**
    * Hakee alapuoliset koulutukset, palauttaa promisen joka täytetään koulutusoid-listalla
    */
    dataFactory.getChildKoulutuksetPromise = function(koulutusoid) {
        return dataFactory.getKoulutuksetPromise(dataFactory.resourceLink.get({
            oid: koulutusoid
        }).$promise);
    };
    /**
    * Hakee yläpuoliset koulutukset, palauttaa promisen joka täytetään koulutusoid-listalla
    */
    dataFactory.getParentKoulutuksetPromise = function(koulutusoid) {
        return dataFactory.getKoulutuksetPromise(dataFactory.resourceLink.parents({
            oid: koulutusoid
        }).$promise);
    };
    dataFactory.komoImport = function(koulutusUri) {
        return $resource(plainUrls.url("tarjonta-service.komo.import", koulutusUri), {}, {
            import: {
                method: 'POST',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            }
        });
    };
    dataFactory.komo = function() {
        return $resource(plainUrls.url("tarjonta-service.komo.byOid", ":oid"), {}, {
            update: {
                method: 'POST',
                withCredentials: true,
                url: plainUrls.url("tarjonta-service.komo.byOid", ":oid"),
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            insert: {
                method: 'POST',
                withCredentials: true,
                url: plainUrls.url("tarjonta-service.komo.byOid", ":oid"),
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            import: {
                method: 'POST',
                withCredentials: true,
                url: plainUrls.url("tarjonta-service.komo.import", ":koulutusUri"),
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            get: {
                method: 'GET'
            },
            search: {
                method: 'GET',
                url: plainUrls.url("tarjonta-service.komo.search", ":koulutuskoodi")
            },
            searchModules: {
                method: 'GET',
                url: plainUrls.url("tarjonta-service.komo.searchModules", ":koulutustyyppi", ":moduuli")
            },
            tekstis: {
                method: 'GET',
                url: plainUrls.url("tarjonta-service.komo.tekstis", ":oid")
            }
        });
    };
    //
    // OHJAUSPARAMETRIT
    //
    dataFactory.ohjausparametritCache = {};
    dataFactory.reloadParametersIfEmpty = function() {
        var deferred = $q.defer();
        if (Object.keys(dataFactory.ohjausparametritCache).length === 0) {
            dataFactory.reloadParameters().then(function() {
                deferred.resolve();
            });
        } else {
            deferred.resolve();
        }
        return deferred.promise;
    };
    dataFactory.reloadParameters = function() {
        var deferred = $q.defer();
        $log.info('reloadParameters()');
        $resource(window.url("ohjausparametrit-service.all"), {}, {
            get: {
                cache: false,
                isArray: false
            }
        }).get(function(results) {
            // NOTE: now "ALL" parameters is an object like:
            // {
            //   target1: {
            //     param1: { date: xxxx },
            //     param2: { date: yyy }
            //   },
            // }
            //
            // FIXME *** Only "date" value cached... *** Use the server side permission checker!
            var cache = dataFactory.ohjausparametritCache;
            var targetNames = Object.keys(results);
            // $log.debug("Processing targets: ", targetNames);
            angular.forEach(targetNames, function(targetName) {
                var pt = results[targetName];
                // $log.debug("  Processing target parameters for : ", targetName, pt);
                if (angular.isDefined(pt) && typeof pt === 'object') {
                    cache[targetName] = cache[targetName] ? cache[targetName] : {};
                    var paramNames = Object.keys(pt);
                    angular.forEach(paramNames, function(paramName) {
                        var p = results[targetName][paramName];
                        // TODO only single dates used as of now!!!!
                        cache[targetName][paramName] = p.date; //$log.debug("    STORED ", targetName, paramName, p.date);
                    });
                }
                else {
                    $log.debug('  NOT OBJECT: ', targetName, pt);
                }
            });
            dataFactory.ohjausparametritCache = cache;
            $log.info('Processed \'ohjausparametrit\' - now cached.');
            deferred.resolve();
        });
        return deferred.promise;
    };
    // LOAD PARAMETERS FROM OHJAUSPARAMETRIT
    dataFactory.reloadParameters();
    dataFactory.getParameter = function(target, name, defaultValue, errorValue) {
        var cache = dataFactory.ohjausparametritCache;
        // If ohjausparametrit failed loading for some reason
        if (Object.keys(cache).length === 0) {
            return errorValue;
        }
        if (cache[target] && cache[target][name]) {
            return cache[target][name];
        }
        return defaultValue;
    };
    dataFactory.parameterCanEditHakukohde = function(hakuOid) {
        if (AuthService.isUserOph()) {
            return true;
        }
        var now = new Date().getTime();
        var ph_hklpt = dataFactory.getParameter(hakuOid, 'PH_HKLPT', now, 0);
        var ph_hkmt = dataFactory.getParameter(hakuOid, 'PH_HKMT', now, 0);
        if (ph_hklpt < now || ph_hkmt < now) {
            return false;
        }
        return true;
    };
    dataFactory.parameterCanEditHakukohdeLimited = function(hakuOid) {
        if (AuthService.isUserOph()) {
            return true;
        }
        var now = new Date().getTime();
        var ph_hkmt = dataFactory.getParameter(hakuOid, 'PH_HKMT', now, 0);
        if (ph_hkmt < now) {
            return false;
        }
        else {
            return true;
        }
    };
    dataFactory.parameterCanAddHakukohdeToHaku = function(hakuOid) {
        return dataFactory.parameterCanEditHakukohde(hakuOid);
    };
    dataFactory.parameterCanRemoveHakukohdeFromHaku = function(hakuOid) {
        return dataFactory.parameterCanEditHakukohde(hakuOid);
    };
    return dataFactory;
});
