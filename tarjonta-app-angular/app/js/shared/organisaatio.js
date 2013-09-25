angular.module('Organisaatio', [ 'ngResource' ])

//"organisaatioservice"
.factory('OrganisaatioService', function ($resource, $log, $q) {
	var orgHaku = $resource('https://itest-virkailija.oph.ware.fi/organisaatio-service/rest/organisaatio/hae?searchStr=:query');
		
	return {
	   
	   /**
	    * query (hakuehdot)
	    * @param query
	    * @returns
	    */
	   etsi: function(query){
		   var ret = $q.defer();
	       $log.info('searching organisaatiot, q:' + query);
	       
	       orgHaku.get({'query':query},function(result){
	           $log.info("resolving promise with hit count:" + result.numHits);
	    	  ret.resolve(result);
	       }
	    	   
	       );
	       $log.info('past query now, returning promise...:');
	       return ret.promise;
	   }
	};
})