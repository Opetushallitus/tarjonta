angular.module('Yhteyshenkilo', [ 'ngResource', 'config' ])

//"henkiloservice"
.factory('YhteyshenkiloService', function ($resource, $log, $q, Config, CacheService) {
	
	var baseUrl = Config.env['authentication-service.rest.url'];
	var urlEtsi = baseUrl + "henkilo?count=2000&index=0&ht=VIRKAILIJA";
	var urlHaeTiedot =baseUrl + "henkilo/:oid";
	var urlHaeOrganisaatioHenkiloTiedot = baseUrl + "henkilo/:oid/organisaatiohenkilo";

	var henkHaku = $resource(urlEtsi,{},{cache:true,get:{method:"GET", withCredentials:true}});
	var henkilo = $resource(urlHaeTiedot,{},{cache:true,get:{method:"GET", withCredentials:true}});
	var organisaatioHenkilo = $resource(urlHaeOrganisaatioHenkiloTiedot,{},{cache:true,get:{isArray: true, method:"GET", withCredentials:true}});

	return {

	   /**
	    * Etsii henkilöitä
	    * @returns promise
	    */
	   etsi: function(hakuehdot){
		   var ret = $q.defer();
	       $log.debug('haetaan yhteyshenkiot, q:', hakuehdot);
	       henkHaku.get(hakuehdot, function(result){
	    	   ret.resolve(result);
	       }, function(err){
	    	   console.error("Error loading data", err);
	       });
	       return ret.promise;
	   },

	   /**
	    * Hakee henkilon tiedot(yhteystiedot)
	    * @returns promise
	    */
	   haeHenkilo: function(oid){
		   var hakuehdot={oid:oid};
		   var ret = $q.defer();
	       $log.debug('haetaan henkilon tiedot, q:', hakuehdot);
	       henkilo.get(hakuehdot, function(result){
	    	   ret.resolve(result);
	       }, function(err){
	    	   console.error("Error loading data", err);
	       });
	       return ret.promise;
	   },

	   /**
	    * Hakee organisaatiohenkilon tiedot(tehtavanimike)
	    * @returns promise
	    */
	   haeOrganisaatiohenkilo: function(oid){
		   var hakuehdot={oid:oid};
		   var ret = $q.defer();
	       $log.info('haetaan organisaatiohenkilon tiedot, q:', hakuehdot);
	       organisaatioHenkilo.get(hakuehdot, function(result){
	    	   ret.resolve(result);
	       }, function(err){
	    	   console.error("Error loading data", err);
	       });
	       return ret.promise;
	   }

	
	};
});