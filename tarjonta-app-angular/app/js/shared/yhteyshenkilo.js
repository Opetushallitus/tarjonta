angular.module('Yhteyshenkilo', [ 'ngResource', 'config', 'Logging' ])

//"henkiloservice"
.factory('YhteyshenkiloService', function ($resource, $log, $q, Config, CacheService) {

    $log = $log.getInstance("YhteyshenkiloService");

	var baseUrl = Config.env['authentication-service.henkilo.rest.url'];
	var urlEtsi = baseUrl +  Config.env['authentication-service.henkilo.search.params'];
	var urlHaeTiedot =baseUrl + "/:oid";
	var urlHaeOrganisaatioHenkiloTiedot = baseUrl + "/:oid/organisaatiohenkilo";

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

           $log.warn("etsi() - DISABLED", hakuehdot);

		   //, XXX disabloitu koska henkilöhakupalvelu ei kerkiä mukaan
//	       $log.debug('haetaan yhteyshenkiot, q:', hakuehdot);
//	       henkHaku.get(hakuehdot, function(result){
//	    	   ret.resolve(result);
//	       }, function(err){
//	    	   $log.debug("Error loading data", err);
//	       });
		   ret.resolve({});
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
               // TODO add loadingService disable error dialog!
	    	   $log.error("Error loading data", err);
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
               // TODO add loadingService disable error dialog!
	    	   $log.error("Error loading data", err);
	       });
	       return ret.promise;
	   }


	};
});