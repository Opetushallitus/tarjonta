angular.module('Tarjonta', [ 'ngResource', 'config' ])

//"organisaatioservice"
.factory('TarjontaService', function ($resource, $log, $q, Config, LocalisationService, Koodisto) {
	
	var hakukohdeHaku = $resource(Config.env.tarjontaRestUrlPrefix + "hakukohde/search");
	var koulutusHaku = $resource(Config.env.tarjontaRestUrlPrefix + "koulutus/search");

	function localize(txt) {
		return txt.fi == undefined ? txt : txt.fi;
	}
	
	return {

		haeHakukohteet: function(args) {
			var ret = $q.defer();
			hakukohdeHaku.get({
				searchTerms:args.terms,
				organisationOid:args.oid,
				tila:args.state,
				alkamisKausi:args.season,
				alkamisVuosi:args.year
			}, function(result){
				for (var i in result.tulokset) {
					var t = result.tulokset[i];
					t.nimi = localize(t.nimi);
					for (var j in t.tulokset) {
						var r = t.tulokset[j];
						r.nimi = localize(r.nimi);
						r.koulutusLaji = localize(r.koulutusLaji);
						r.hakutapa = localize(r.hakutapa);
						r.tila = LocalisationService.t("tarjonta.tila."+r.tila);
					}
				}
				ret.resolve(result);
			});
			return ret.promise;
		},
		haeKoulutukset: function(args) {
			var ret = $q.defer();
			koulutusHaku.get({
				searchTerms:args.terms,
				organisationOid:args.oid,
				tila:args.state,
				alkamisKausi:args.season,
				alkamisVuosi:args.year
			}, function(result){
				for (var i in result.tulokset) {
					var t = result.tulokset[i];
					t.nimi = localize(t.nimi);
					for (var j in t.tulokset) {
						var r = t.tulokset[j];
						r.nimi = localize(r.nimi);
						r.tila = LocalisationService.t("tarjonta.tila."+r.tila);
					}
				}
				ret.resolve(result);
			});
			return ret.promise;
		}
		
	};
})
