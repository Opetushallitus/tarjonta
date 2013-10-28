angular.module('Organisaatio', [ 'ngResource', 'config' ])

//"organisaatioservice"
.factory('OrganisaatioService', function ($resource, $log, $q, Config) {
	
	var orgHaku = $resource(Config.env["organisaatio.api.rest.url"] + "organisaatio/hae");
	var orgLuku = $resource(Config.env["organisaatio.api.rest.url"] + "organisaatio/:oid");
	
	function localize(organisaatio){
		//TODO olettaa että käyttäjä suomenkielinen
		organisaationimi=organisaatio.nimi.fi||organisaatio.nimi.sv||organisaatio.nimi.en;
		organisaatio.nimi=organisaationimi;
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
	    * @param hakuehdot, muodossa: (java OrganisaatioSearchCriteria -luokka)
	    * {
		*   "searchStr" : "",
		*   "organisaatiotyyppi" : "",
		*   "oppilaitostyyppi" : "",
		*   "lakkautetut" : false,
		*   "suunnitellut" : false
	    * } 
	    * 
	    * 
	    * @returns promise
	    */
	   etsi: function(hakuehdot){
		   var ret = $q.defer();
//	       $log.info('searching organisaatiot, q:', hakuehdot);

	       orgHaku.get(hakuehdot,function(result){
//	           $log.info("resolving promise with hit count:" + result.numHits);
	           localizeAll(result.organisaatiot);
	           ret.resolve(result);
	       });
	       
//	       $log.info('past query now, returning promise...:');
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
	   },

        byOid: function(oid) {
            var ret = $q.defer();
            orgLuku.get({oid: oid}, function(result){
                ret.resolve(localize(result));
            });
            return ret.promise;
        }
	
	
	};
})
