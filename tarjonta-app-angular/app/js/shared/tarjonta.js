var app = angular.module('Tarjonta', ['ngResource', 'config', 'auth']);

app.factory('TarjontaService', function($resource, $http, Config, LocalisationService, Koodisto, AuthService, CacheService, $q) {

    var hakukohdeHaku = $resource(Config.env.tarjontaRestUrlPrefix + "hakukohde/search");
    var koulutusHaku = $resource(Config.env.tarjontaRestUrlPrefix + "koulutus/search");

    function localize(txt) {
        if (txt == undefined || txt == null) {
            return txt;
        }
        var userLocale = LocalisationService.getLocale();
        if (txt[userLocale]) {
            return txt[userLocale];
        } else if (txt.fi) {
            return txt.fi;
        } else if (txt.sv) {
            return txt.sv;
        } else if (txt.en) {
            return txt.en;
        }
    }

    function compareByName(a, b) {
        var an = a.nimi;
        var bn = b.nimi;
        /*
         * if a.nimi is null/undefined : 'Cannot call method 'localeCompare' of undefined'
         */
        return an.localeCompare(bn);
    }

    function searchCacheKey(prefix, args) {
        return {
            key: prefix + "/?" +
                    "oid=" + args.oid + "&" +
                    "terms=" + escape(args.terms) + "&" +
                    "state=" + escape(args.state) + "&" +
                    "season=" + escape(args.season) + "&" +
                    "year=" + escape(args.year),
            expires: 60000,
            pattern: prefix + "/.*"
        };
    }

    var dataFactory = {};

    dataFactory.getTilat = function() {
        return window.CONFIG.env["tarjonta.tila"];
    };

    dataFactory.acceptsTransition = function(from, to) {
        var s = window.CONFIG.env["tarjonta.tila"][from];
        return s != null && s.transitions.indexOf("to") >= 0;
    };

    dataFactory.haeHakukohteet = function(args) {
        var params = {
            searchTerms: args.terms,
            organisationOid: args.oid,
            tila: args.state,
            alkamisKausi: args.season,
            alkamisVuosi: args.year
        };

        return CacheService.lookupResource(searchCacheKey("hakukohde", args), hakukohdeHaku, params, function(result) {
            result = result.result // unwrap v1
            for (var i in result.tulokset) {
                var t = result.tulokset[i];
                t.nimi = localize(t.nimi);
                for (var j in t.tulokset) {
                    var r = t.tulokset[j];

                    if (t.nimi === null || typeof t.nimi === 'undefined') {
                        r.nimi = t.oid;
                    } else {
                        r.nimi = localize(r.nimi);
                    }

                    r.koulutuslaji = localize(r.koulutuslaji);
                    r.hakutapa = localize(r.hakutapa);
                    r.tilaNimi = LocalisationService.t("tarjonta.tila." + r.tila);
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
            tila: args.state,
            alkamisKausi: args.season,
            alkamisVuosi: args.year
        };

        return CacheService.lookupResource(searchCacheKey("koulutus", args), koulutusHaku, params, function(result) {
            result = result.result;  //unwrap v1
            for (var i in result.tulokset) {
                var t = result.tulokset[i];

                if (t.nimi === null || typeof t.nimi === 'undefined') {
                    t.nimi = t.oid;
                } else {
                    t.nimi = localize(t.nimi);
                }

                for (var j in t.tulokset) {
                    var r = t.tulokset[j];
                    if (t.nimi === null || typeof t.nimi === 'undefined') {
                        r.nimi = r.oid;
                    } else {
                        r.nimi = localize(r.nimi) + (r.pohjakoulutusvaatimus !== undefined ? ", " + localize(r.pohjakoulutusvaatimus) : "");
                    }

                    r.tilaNimi = LocalisationService.t("tarjonta.tila." + r.tila);
                    r.koulutuslaji = localize(r.koulutuslaji);
                }
                t.tulokset.sort(compareByName);
            }
            result.tulokset.sort(compareByName);
            return result;
        });
    }

    dataFactory.evictHakutulokset = function() {
        CacheService.evict({pattern: "hakutulos/.*"});
        CacheService.evict({pattern: "koulutus/.*"});
    }

    /**
     * Asettaa koulutuksen tai hakukohteen julkaisun tilan.
     * @param type "koulutus" | "hakukohde"
     * @param oid kohteen oid
     * @param publish tosi, jos julkaistaan, epätosi jos perutaan julkaisu
     * @return promise, jonka arvo on kohteen tila on (muutoksen jälkeen) sama kuin publish-parametrilla annettu
     */
    dataFactory.togglePublished = function(type, oid, publish) {
        var ret = $q.defer();
        var tila = $resource(Config.env.tarjontaRestUrlPrefix + type + "/" + oid + "/tila?state=" + (publish ? "JULKAISTU" : "PERUTTU"), {}, {
            update: {method: 'POST'}
        });

        tila.update(function(nstate) {
            console.log("resolving:", nstate);
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
        var koulutus = $resource(Config.env.tarjontaRestUrlPrefix + "koulutus/" + oid + "/hakukohteet").get({}, function(res) {
            ret.resolve(cleanAndLocalizeArray(res.result));
        });
        return ret.promise;
    }

    dataFactory.getHakukohteenKoulutukset = function(oid) {
        var ret = $q.defer();
        var koulutus = $resource(Config.env.tarjontaRestUrlPrefix + "hakukohde/" + oid + "/koulutukset").get({}, function(res) {
            ret.resolve(cleanAndLocalizeArray(res.result));
        });
        return ret.promise;
    }

    /**
     * POST: Insert new KOMOTO + KOMO. API object must be valid.
     *
     * @param json data in JSON format.
     * @param func callback function, returns {oid : <komoto-oid>, version: <number> }
     * @returns {undefined}
     */
    dataFactory.koulutus = function() {
        return $resource(Config.env.tarjontaRestUrlPrefix + "koulutus/KORKEAKOULUTUS/", {}, {
            update: {
                method: 'POST',
                //  withCredentials: true,
                //isArray: true,
                headers: {'Content-Type': 'application/json; charset=UTF-8'}
            },
            save: {
                method: 'POST',
                //  withCredentials: true,
                // isArray: true,
                headers: {'Content-Type': 'application/json; charset=UTF-8'}
            }
        });
    };

    dataFactory.deleteKoulutus = function(id) {
        console.log("deleteKoulutus(): ", id);
        var ret = $q.defer();
        $resource(Config.env.tarjontaRestUrlPrefix + "koulutus/" + id).remove({}, function(res) {
            ret.resolve(res);
        });
        return ret.promise;
    };
    dataFactory.loadKuvausTekstis = function(oid) {
        console.log("save KomoTekstis(): ", oid);
        var ret = $q.defer();
        var KomoTekstis = new $resource(Config.env.tarjontaRestUrlPrefix + "koulutus/:oid/tekstis", {'oid': '@oid'});
        var Tekstis = KomoTekstis.get({'oid': oid}, function(res) {
            ret.resolve(res);
        });

        return ret.$promise;
    };
    dataFactory.insertHakukohde = function(hakukohde, func) {
        console.log('Inserting hakukohde : ', hakukohde);

        return ret.promise;
    }

    dataFactory.saveKomoTekstis = function(oid) {
        var KomoTekstis = new $resource(Config.env.tarjontaRestUrlPrefix + "koulutus/:oid/komo/tekstis", {'oid': '@oid'});
        var Tekstis = KomoTekstis.get({'oid': oid}, function() {
        });

        return Tekstis;
    };

    dataFactory.insertHakukohde = function(hakukohde, func) {
        $log.info('Inserting hakukohde : ', hakukohde);
    };

    dataFactory.getHakukohde = function(id) {
        console.log("getHakukohde(): id = ", id);

        var ret = $q.defer();

        $resource(Config.env.tarjontaRestUrlPrefix + "hakukohde/ui/" + id, function(result) {
            ret.resolve(result);
        });

        return ret.promise;
    };

    dataFactory.deleteHakukohde = function(id) {
        console.log("deleteHakukohde(): ", id);
        var ret = $q.defer();
        $resource(Config.env.tarjontaRestUrlPrefix + "hakukohde/" + id).remove({}, function(res) {
            ret.resolve(res);
        });
        return ret.promise;
    };

    dataFactory.getKoulutus = function(arg, func) {
        console.log("getKoulutus()");
        var koulutus = $resource(Config.env.tarjontaRestUrlPrefix + "koulutus/:oid", {oid: '@oid'});
        return koulutus.get(arg, func);
    };

    dataFactory.getKoulutuskoodiRelations = function(arg, func) {
        console.log("getKoulutuskoodiRelations()");
        var koulutus = $resource(Config.env.tarjontaRestUrlPrefix + "koulutus/koulutuskoodi/:koulutuskoodiUri", {koulutuskoodiUri: '@koulutuskoodiUri'});
        return koulutus.get(arg, func);
    };

    dataFactory.resourceKomoKuvaus = function(komotoOid) {
        return $resource(Config.env.tarjontaRestUrlPrefix + "koulutus/:oid/komo/tekstis", {'oid': komotoOid}, {
            update: {
                method: 'PUT',
                //withCredentials: true,
                isArray: true,
                headers: {'Content-Type': 'application/json; charset=UTF-8'}
            },
            save: {
                method: 'POST',
                // withCredentials: true,
                isArray: true,
                headers: {'Content-Type': 'application/json; charset=UTF-8'}
            },
            get: {
                method: 'GET',
                headers: {'Content-Type': 'application/json; charset=UTF-8'}
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
            throw new Error('Image object cannot be undefined or null.');
        }

        var formData = new FormData();
        formData.append('image', image.file, image.name);
        $http.post(Config.env.tarjontaRestUrlPrefix + 'koulutus/' + komotoOid + '/kuva/' + kieliuri, formData, {
            headers: {'Content-Type': 'multipart/form-data'},
            transformRequest: angular.identity
        }).success(fnSuccess).error(fnError);
    };

    return dataFactory;
});
