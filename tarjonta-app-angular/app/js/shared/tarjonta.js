var app = angular.module('Tarjonta', [
    'ngResource',
    'config',
    'Logging'
]);
app.factory('TarjontaService', function($resource, $http, Config, LocalisationService, Koodisto,
                                        CacheService, $q, $log, OrganisaatioService, AuthService) {
    $log = $log.getInstance('TarjontaService');
    var hakukohdeHaku = $resource(Config.env.tarjontaRestUrlPrefix + 'hakukohde/search');
    var koulutusHaku = $resource(Config.env.tarjontaRestUrlPrefix + 'koulutus/search');
    function localize(txt) {
        if (txt === undefined || txt === null) {
            return txt;
        }
        var userLocale = LocalisationService.getLocale();
        if (txt[userLocale]) {
            return txt[userLocale];
        }
        else if (txt.fi) {
            return txt.fi;
        }
        else if (txt.sv) {
            return txt.sv;
        }
        else if (txt.en) {
            return txt.en;
        }
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
        var resource = $resource(Config.env.tarjontaRestUrlPrefix + 'hakukohde/ryhmat/operate', {}, {
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
            organisaatioRyhmaOid: args.hakukohderyhma
        };
        $log.debug('haeHakukohteet()', params);
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
                        r.nimi = localize(r.nimi);
                    }
                    r.koulutuslaji = localize(r.koulutuslaji);
                    r.hakutapa = localize(r.hakutapa);
                    r.tilaNimi = LocalisationService.t('tarjonta.tila.' + r.tila);
                }
                t.tulokset.sort(compareByName);
            }
            result.tulokset.sort(compareByName);
            $log.info('haeHakukohteet() params, result', params, result);
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
            hakukohderyhma: args.hakukohderyhma
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
                        r.nimi = localize(r.nimi);
                        if (!_.contains(['LUKIOKOULUTUS',
                                         'LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA',
                                         'AIKUISTEN_PERUSOPETUS',
                                         'AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA',
                                         'AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER'], r.toteutustyyppiEnum)
                            && r.pohjakoulutusvaatimus) {
                            r.nimi += ', ' + localize(r.pohjakoulutusvaatimus);
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
        var tila = $resource(Config.env.tarjontaRestUrlPrefix + type + '/' + oid + '/tila?state=' + (publish ?
            'JULKAISTU' :
            'PERUTTU'), {}, {
            update: {
                method: 'POST',
                withCredentials: true
            }
        });
        tila.update(function(nstate) {
            $log.debug('resolving:', nstate);
            ret.resolve(nstate.result);
        });
        return ret.promise;
    };
    function cleanAndLocalizeArray(arr) {
        var ret = [];
        for (var i in arr) {
            if (arr[i].oid) {
                ret[i] = {
                    oid: arr[i].oid,
                    nimi: localize(arr[i].nimi)
                };
            }
        }
        return ret;
    }
    dataFactory.getKoulutuksenHakukohteet = function(oid) {
        var ret = $q.defer();
        var koulutus = $resource(Config.env.tarjontaRestUrlPrefix + 'koulutus/' + oid + '/hakukohteet')
                        .get({}, function(res) {
            ret.resolve(cleanAndLocalizeArray(res.result));
        });
        return ret.promise;
    };
    dataFactory.getHakukohteenKoulutukset = function(oid) {
        var ret = $q.defer();
        var koulutus = $resource(Config.env.tarjontaRestUrlPrefix + 'hakukohde/' + oid + '/koulutukset')
                        .get({}, function(res) {
            ret.resolve(cleanAndLocalizeArray(res.result));
        });
        return ret.promise;
    };
    dataFactory.koulutus = function(oid) {
        return $resource(Config.env.tarjontaRestUrlPrefix + 'koulutus/', {}, {
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
                url: Config.env.tarjontaRestUrlPrefix + 'koulutus/:oid',
                method: 'DELETE',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            copyAndMove: {
                url: Config.env.tarjontaRestUrlPrefix + 'koulutus/' + oid + '/siirra',
                method: 'POST',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            copyAndMoveMultiple: {
                url: Config.env.tarjontaRestUrlPrefix + 'koulutus/siirra',
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
        var KomoTekstis = new $resource(Config.env.tarjontaRestUrlPrefix + 'koulutus/:oid/tekstis', {
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
        var resource = new $resource(Config.env.tarjontaRestUrlPrefix + 'komo/:oid/tekstis', {
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
        $resource(Config.env.tarjontaRestUrlPrefix + 'hakukohde/ui/' + id, function(result) {
            ret.resolve(result);
        });
        return ret.promise;
    };
    dataFactory.deleteHakukohde = function(id) {
        $log.debug('deleteHakukohde(): ', id);
        var ret = $q.defer();
        $resource(Config.env.tarjontaRestUrlPrefix + 'hakukohde/' + id).remove({}, function(res) {
            ret.resolve(res);
        });
        return ret.promise;
    };
    dataFactory.getKoulutus = function(arg, func) {
        //param meta=false filter all meta fields
        var koulutus = $resource(Config.env.tarjontaRestUrlPrefix + 'koulutus/:oid?img=true', {
            oid: '@oid'
        });
        return koulutus.get(arg, func);
    };
    //hakee koulutuksen, palauttaa promisen
    dataFactory.getKoulutusPromise = function(oid) {
        return $resource(Config.env.tarjontaRestUrlPrefix + 'koulutus/' + oid + '?img=true').get().$promise;
    };
    dataFactory.getKoulutuskoodiRelations = function(arg, func) {
        $log.debug('getKoulutuskoodiRelations()');
        var koulutus = $resource(Config.env.tarjontaRestUrlPrefix +
            'koulutus/koodisto/:uri/:koulutustyyppi?meta=false&lang=:languageCode', {
            koulutustyyppi: '@koulutustyyppi',
            uri: '@uri',
            defaults: '@defaults',
            //optional data : string like 'object-field1:uri, object-field2:uri, ...';
            languageCode: '@languageCode'
        });
        return koulutus.get(arg, func);
    };
    dataFactory.getJarjestettavatKoulutukset = function(tarjoajanKoulutusOid, jarjestajat) {
        var deferred = $q.defer();
        var koulutus = $resource(Config.env.tarjontaRestUrlPrefix + 'koulutus/'
                            + tarjoajanKoulutusOid + '/jarjestettavatKoulutukset');

        // Koulutukset tallennetaan mappiin, jossa avaimena toimii organisaation oid
        var map = {};

        var getOrphan = typeof jarjestajat !== 'undefined';

        // Lista koulutuksista, jotka on jo ehditty järjestää, mutta järjestämisoikeus on
        // tämän jälkeen poistettu
        var orphanKoulutukset = [];

        koulutus.get().$promise.then(function(data) {
            var promises = [];
            _.each(data.result, function(koulutus) {
                _.each(koulutus.tarjoajat, function(tarjoaja) {
                    var deferred = $q.defer();
                    OrganisaatioService.byOid(tarjoaja).then(function(org) {
                        _.each(org.oidAndParentOids, function(orgOid) {
                            if (orgOid !== Config.env['root.organisaatio.oid']) {
                                map[orgOid] = koulutus;
                            }
                        });
                        if (getOrphan && _.intersection(jarjestajat, org.oidAndParentOids).length === 0) {
                            orphanKoulutukset.push(_.extend({}, koulutus, {
                                tarjoajaNimi: org.nimi
                            }));
                        }
                        deferred.resolve();
                    });
                    promises.push(deferred.promise);
                });
            });
            $q.all(promises).then(function() {
                if (getOrphan) {
                    deferred.resolve({
                        map: map,
                        orphans: orphanKoulutukset
                    });
                }
                else {
                    deferred.resolve(map);
                }
            });
        });
        return deferred.promise;
    };
    dataFactory.resourceKomoKuvaus = function(komotoOid) {
        return $resource(Config.env.tarjontaRestUrlPrefix + 'koulutus/:oid/tekstis/komo', {
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
            $http.post(Config.env.tarjontaRestUrlPrefix + 'koulutus/' + komotoOid + '/kuva', apiImg, {
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
        var ResourceImge = $resource(Config.env.tarjontaRestUrlPrefix + 'koulutus/:oid/kuva/:uri', {
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
    dataFactory.resourceLink = $resource(Config.env.tarjontaRestUrlPrefix + 'link/:oid', {}, {
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
            url: Config.env.tarjontaRestUrlPrefix + 'link/test',
            headers: {
                'Content-Type': 'application/json; charset=UTF-8'
            },
            method: 'POST'
        },
        parents: {
            url: Config.env.tarjontaRestUrlPrefix + 'link/:oid/parents',
            isArray: false,
            method: 'GET'
        },
        remove: {
            method: 'DELETE',
            url: Config.env.tarjontaRestUrlPrefix + 'link/:parent/:child'
        },
        removeMany: {
            method: 'DELETE',
            url: Config.env.tarjontaRestUrlPrefix + 'link/:parent'
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
        return $resource(Config.env.tarjontaRestUrlPrefix + 'komo/import/' + koulutusUri, {}, {
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
        return $resource(Config.env.tarjontaRestUrlPrefix + 'komo/:oid', {}, {
            update: {
                method: 'POST',
                withCredentials: true,
                url: Config.env.tarjontaRestUrlPrefix + 'komo/:oid',
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            insert: {
                method: 'POST',
                withCredentials: true,
                url: Config.env.tarjontaRestUrlPrefix + 'komo',
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            import: {
                method: 'POST',
                withCredentials: true,
                url: Config.env.tarjontaRestUrlPrefix + 'komo/import/:koulutusUri',
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            get: {
                method: 'GET'
            },
            search: {
                method: 'GET',
                url: Config.env.tarjontaRestUrlPrefix + 'komo/search?koulutuskoodi=:koulutuskoodi'
            },
            searchModules: {
                method: 'GET',
                url: Config.env.tarjontaRestUrlPrefix + 'komo/search/:koulutustyyppi/:moduuli'
            },
            tekstis: {
                method: 'GET',
                url: Config.env.tarjontaRestUrlPrefix + 'komo/:oid/tekstis'
            }
        });
    };
    //
    // OHJAUSPARAMETRIT
    //
    dataFactory.ohjausparametritCache = {};
    dataFactory.reloadParameters = function() {
        $log.info('reloadParameters()');
        var uri = Config.env.tarjontaOhjausparametritRestUrlPrefix;
        if (!angular.isDefined(uri)) {
            throw '\'tarjontaOhjausparametritRestUrlPrefix\' is not defined! Cannot proceed.';
        }
        uri = uri + '/api/v1/rest/parametri/ALL';
        $resource(uri, {}, {
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
        });
    };
    // LOAD PARAMETERS FROM OHJAUSPARAMETRIT
    dataFactory.reloadParameters();
    dataFactory.getParameter = function(target, name, type, defaultValue) {
        var result;
        var cache = dataFactory.ohjausparametritCache;
        if (angular.isDefined(cache[target])) {
            result = cache[target][name];
        }
        // Conversion to date
        if ('DATE' == type && angular.isDefined(result)) {
            result = new Date(result);
        }
        if (false && !angular.isDefined(result)) {
            result = defaultValue;
        }
        // $log.debug("getParameter()", target, name, result, cache[target]);
        return result;
    };
    dataFactory.parameterCanEditHakukohde = function(hakuOid) {
        if (AuthService.isUserOph()) {
            return true;
        }
        var now = new Date().getTime();
        var ph_hklpt = dataFactory.getParameter(hakuOid, 'PH_HKLPT', 'LONG') || now;
        var ph_hkmt = dataFactory.getParameter(hakuOid, 'PH_HKMT', 'LONG') || now;
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
        var ph_hkmt = dataFactory.getParameter(hakuOid, 'PH_HKMT', 'LONG', now);
        if (ph_hkmt) {
            var result = now <= ph_hkmt;
            $log.debug('parameterCanEditHakukohdeLimited: ', hakuOid, ph_hkmt, result);
            return result;
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
