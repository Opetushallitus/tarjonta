var app = angular.module('Tarjonta', ['ngResource', 'config', 'auth']);

app.factory('TarjontaService', function($resource, $log, $q, Config, LocalisationService, Koodisto, AuthService, CacheService) {

    var hakukohdeHaku = $resource(Config.env.tarjontaRestUrlPrefix + "hakukohde/search");
    var koulutusHaku = $resource(Config.env.tarjontaRestUrlPrefix + "koulutus/search");

    function localize(txt) {

        var userLocale = LocalisationService.getLocale();

        if ("fi" === userLocale) {
            return txt.fi;
        } else if ("en" === userLocale) {
            return txt.en;
        } else if ("sv" === userLocale) {
            return txt.sv;
        } else {
            return "(TUNTEMATON LOCALE = '"+ userLocale + "', palautetaan suomalainen sisältö) " + txt.fi;
        }
    }

    function compareByName(a, b) {
        var an = a.nimi;
        var bn = b.nimi;
        return an.localeCompare(bn);
    }
    
    function searchCacheKey(prefix, args) {
    	return prefix+"/?"+
    		"oid="+args.oid+"&"+
    		"terms="+escape(args.terms)+"&"+
    		"state="+escape(args.state)+"&"+
    		"season="+escape(args.season)+"&"+
    		"year="+escape(args.year);
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
                    r.koulutusLaji = localize(r.koulutusLaji);
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
                    r.nimi = localize(r.nimi);
                    r.tilaNimi = LocalisationService.t("tarjonta.tila." + r.tila);
                }
                t.tulokset.sort(compareByName);
            }
            result.tulokset.sort(compareByName);
    		return result;
    	});
    }

    dataFactory.insertKoulutus = function(json) {
        console.log("insertKoulutus");
        console.log(json);
        var koulutus = $resource(Config.env.tarjontaRestUrlPrefix + "koulutus/");
        koulutus.save(json);
    };

    dataFactory.updateKoulutus = function(cust) {
        return null;
    };

    dataFactory.deleteKoulutus = function(id) {
        return $http.delete(urlBase + '/' + id);
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
