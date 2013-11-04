angular.module('Yhteyshenkilo', [ 'ngResource', 'config' ])

//"organisaatioservice"
.factory('YhteyshenkiloService', function ($resource, $log, $q, Config, CacheService) {
	
	// TODO rest-palvelu
	var henkHaku = $resource("/tarjonta-app/yhteyshenkilot.json");

	return {

	   /**
	    * @returns promise
	    */
	   etsi: function(hakuehdot){
		   var ret = $q.defer();
	       //$log.info('searching yhteyshenkiot, q:', hakuehdot);
	       return CacheService.lookupResource("yhteyshenkilot/*", henkHaku);
	   }
	
	
	};
});