angular.module('Tarjonta', ['ngResource', 'config']).factory('TarjontaService', function($resource, $log, $q, Config, LocalisationService, Koodisto) {

    var hakukohdeHaku = $resource(Config.env.tarjontaRestUrlPrefix + "hakukohde/search");
    var koulutusHaku = $resource(Config.env.tarjontaRestUrlPrefix + "koulutus/search");
    var tutkintoHaku = $resource('partials/kk/edit/koulutusData.json');

    function localize(txt) {
    	// TODO käyttäjän localen mukaan
    	if (txt.fi!=undefined) {
    		return txt.fi;
    	} else if (txt.se!=undefined) {
    		return txt.se;
    	} else if (txt.en!=undefined) {
    		return txt.en;
    	} else {
            return txt+""; // tostring
    	}
    }
    
    function compareByName(a, b) {
    	var an = a.nimi;
    	var bn = b.nimi;
    	return an.localeCompare(bn);
    }

    var dataFactory = {};
    
    dataFactory.tilat = function() {
    	// TODO rest-kutsu joka hakee tarjontatilat- ja siirtymät TarjontaTila-enumista
    	return {
	    	LUONNOS: {mutable: true, removable: true, cancellable: false},
	    	VALMIS: {mutable: true, removable: true, cancellable: false},
	    	JULKAISTU: {mutable: true, removable: true, cancellable: false},
	    	PERUTTU: {mutable: true, removable: true, cancellable: false},
	    	KOPIOITU: {mutable: true, removable: true, cancellable: false}
    	}
    };
    /*
 public boolean isMutable() {
    	return this==LUONNOS || this==KOPIOITU;
    }
    
    public boolean isRemovable() {
    	return this==LUONNOS || this==KOPIOITU || this==VALMIS;
    }
    
    public boolean isCancellable() {
    	return this==JULKAISTU || this==VALMIS;
    }

     */

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
                    r.tila = LocalisationService.t("tarjonta.tila." + r.tila);
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
                    r.tila = LocalisationService.t("tarjonta.tila." + r.tila);
                }
                t.tulokset.sort(compareByName);
            }
            result.tulokset.sort(compareByName);
            ret.resolve(result);
        });
        return ret.promise;
    }

    dataFactory.insertTutkinto = function(cust) {
        return $http.post(urlBase, cust);
    };

    dataFactory.updateTutkinto = function(cust) {
        return $http.put(urlBase + '/' + cust.ID, cust)
    };

    dataFactory.deleteTutkinto = function(id) {
        return $http.delete(urlBase + '/' + id);
    };

    dataFactory.getTutkinto = function(arg, func) {
        console.log("getTutkinto()");
        return tutkintoHaku.get(func);
    };

    return dataFactory;
})
