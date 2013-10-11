var app = angular.module('Tarjonta', ['ngResource', 'config', 'auth']);

app.factory('TarjontaService', function($resource, Config, LocalisationService, Koodisto, AuthService, CacheService, $q) {

    var hakukohdeHaku = $resource(Config.env.tarjontaRestUrlPrefix + "hakukohde/search");
    var koulutusHaku = $resource(Config.env.tarjontaRestUrlPrefix + "koulutus/search");

    function localize(txt) {
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
            pattern: prefix+"/.*"
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
            for (var i in result.tulokset) {
                var t = result.tulokset[i];
                t.nimi = localize(t.nimi);
                for (var j in t.tulokset) {
                    var r = t.tulokset[j];
                    r.nimi = localize(r.nimi);
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
            for (var i in result.tulokset) {
                var t = result.tulokset[i];
                t.nimi = localize(t.nimi);
                for (var j in t.tulokset) {
                    var r = t.tulokset[j];
                    r.nimi = localize(r.nimi) + (r.pohjakoulutusvaatimus!==undefined?", " + localize(r.pohjakoulutusvaatimus):"");
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
        var url = Config.env.tarjontaRestUrlPrefix + type + "/" + oid + "/tila?state=" + (publish ? "JULKAISTU" : "PERUTTU");

        jQuery.ajax(url, {
            method: "POST",
            dataType: "text",
            success: function(nstate) {
                ret.resolve(nstate);

                // TODO päivitä cache

            }
        });

        return ret.promise;
    };

    /**
     * POST: Insert new KOMOTO + KOMO. API object must be valid.
     * 
     * @param json data in JSON format.
     * @param func callback function, returns {oid : <komoto-oid>, version: <number> }
     * @returns {undefined}
     */
    dataFactory.insertKoulutus = function(json, func) {
        console.log("insertKoulutus", json);
        var koulutus = $resource(Config.env.tarjontaRestUrlPrefix + "koulutus/");
        koulutus.save(json, func);
    };

    /**
     * PUT: Update KOMOTO + KOMO data objects. API object must be valid.
     * 
     * @param json data in JSON format.
     * @param func callback function, returns {oid : <komoto-oid>, version: <number> }
     * @returns {undefined}
     */
    dataFactory.updateKoulutus = function(json, func) {
        var koulutus = $resource(Config.env.tarjontaRestUrlPrefix + "koulutus/", {}, {
            update: {method: 'PUT'}
        });
        koulutus.save(json, func);
    };

    dataFactory.deleteKoulutus = function(id) {
    	var ret = $q.defer();
        $resource(Config.env.tarjontaRestUrlPrefix + "koulutus/" + id).remove({}, function(res){
        	ret.resolve(res);
        });
        return ret.promise;
    };

    dataFactory.deleteHakukohde = function(id) {
    	var ret = $q.defer();
        $resource(Config.env.tarjontaRestUrlPrefix + "hakukohde/" + id).remove({}, function(res){
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

    return dataFactory;
});
