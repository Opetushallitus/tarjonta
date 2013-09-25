angular.module('Organisaatio', [ 'ngResource' ])
//.config(function($httpProvider) { $httpProvider.defaults.useXDomain = true;}) 

//"organisaatioservice"
.factory('OrganisaatioService', function ($resource, $log, $q) {
	var orgHaku = $resource('https://itest-virkailija.oph.ware.fi/organisaatio-service/rest/organisaatio/hae?searchStr=:query');
	var orgLuku = $resource('https://itest-virkailija.oph.ware.fi/organisaatio-service/rest/organisaatio/:oid');
	
	function localize(organisaatio){
		//TODO olettaa että käyttäjä suomenkielinen
		organisaationimi=organisaatio.nimi.fi||organisaatio.nimi.sv||organisaatio.nimi.en;
		organisaatio.nimi=organisaationimi
		if(organisaatio.children){
			localizeAll(organisaatio.children);
		}
		return organisaatio;
	}
    
	function localizeAll(organisaatioarray){
		angular.forEach(organisaatioarray, localize);
		return organisaatioarray;
    }
	
	return {
	   
	   /**
	    * query (hakuehdot)
	    * @param query
	    * @returns promise
	    */
	   etsi: function(query){
		   var ret = $q.defer();
	       $log.info('searching organisaatiot, q:' + query);
	       
	       orgHaku.get({'query':query},function(result){
	           $log.info("resolving promise with hit count:" + result.numHits);
	           localizeAll(result.organisaatiot);
	           ret.resolve(result);
	       });
	       
	       $log.info('past query now, returning promise...:');
	       return ret.promise;
	   },
	   
	   /**
	    * Hakee organisaatiolle voimassaolevan localen mukaisen nimen.
	    * 
	    * @param oid
	    * @returns promise
	    */
	   nimi: function(oid) {
		   var ret = $q.defer();
		   orgLuku.get({oid: oid}, function(result){
			   ret.resolve(localize(result).nimi);
		   });
		   return ret.promise;
	   }
	
	
	};
})