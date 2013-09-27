angular.module('Tarjonta', [ 'ngResource', 'config' ])

//"organisaatioservice"
.factory('TarjontaService', function ($resource, $log, $q, Config) {
	
	var hakukohdeHaku = $resource(Config.env.tarjontaRestUrlPrefix + "hakukohde/search");
	var koulutusHaku = $resource(Config.env.tarjontaRestUrlPrefix + "koulutus/search");

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
				ret.resolve(result);
			});
			return ret.promise;
		}
		
	};
})
