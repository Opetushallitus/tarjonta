angular.module('Yhteyshenkilo', [ 'ngResource', 'config' ])

//"organisaatioservice"
.factory('YhteyshenkiloService', function ($resource, $log, $q, Config) {
	
	var henkHaku = $resource("/yhteyshenkilot.json");

	return {

	   /**
	    * @returns promise
	    */
	   etsi: function(hakuehdot){
		   var ret = $q.defer();
	       $log.info('searching yhteyshenkiot, q:', hakuehdot);

	       henkHaku.get(hakuehdot,function(result){
	           $log.info("resolving promise");
	           ret.resolve(result);
	       });
	       
	       $log.info('past query now, returning promise...:');
	       return ret.promise;
	   }
	
	
	};
});