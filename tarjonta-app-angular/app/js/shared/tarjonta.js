angular.module('Tarjonta', ['ngResource', 'config']).factory('TarjontaService', function($resource, $log, $q, Config, LocalisationService, Koodisto) {

    var hakukohdeHaku = $resource(Config.env.tarjontaRestUrlPrefix + "hakukohde/search");
    var koulutusHaku = $resource(Config.env.tarjontaRestUrlPrefix + "koulutus/search");
    var tilaResource = $resource(Config.env.tarjontaRestUrlPrefix + "tila");
    var tilaCache = null;
    
    
    function localize(txt) {
    	// TODO käyttäjän localen mukaan
    	if (txt.fi!=undefined) {
    		return txt.fi;
    	} else if (txt.sv!=undefined) {
    		return txt.sv;
    	} else if (txt.en!=undefined) {
    		return txt.en;
    	} else {
            return "?";
    	}
    }
    
    function compareByName(a, b) {
    	var an = a.nimi;
    	var bn = b.nimi;
    	return an.localeCompare(bn);
    }

    var dataFactory = {};
    
    dataFactory.getTilat = function() {
    	return window.CONFIG.env["tarjonta.tila"];
    };

    dataFactory.acceptsTransition = function(from, to) {
    	var s = window.CONFIG.env["tarjonta.tila"][from];
    	return s!=null && s.transitions.indexOf("to")>=0;
    };

    dataFactory.haeHakukohteet = function(args) {
        var ret = $q.defer();
        hakukohdeHaku.get({
            searchTerms: args.terms,
            organisationOid: args.oid,
            tila: args.state,
            alkamisKausi: args.season,
            alkamisVuosi: args.year
        }, function(result) {
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
            ret.resolve(result);
        });
        return ret.promise;
    };

    dataFactory.haeKoulutukset = function(args) {
        var ret = $q.defer();
        koulutusHaku.get({
            searchTerms: args.terms,
            organisationOid: args.oid,
            tila: args.state,
            alkamisKausi: args.season,
            alkamisVuosi: args.year
        }, function(result) {
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
            ret.resolve(result);
        });
        return ret.promise;
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
})
