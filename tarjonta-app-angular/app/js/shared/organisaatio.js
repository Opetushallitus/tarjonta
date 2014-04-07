angular.module('Organisaatio', [ 'ngResource', 'config', 'Logging' ])

//"organisaatioservice"
.factory('OrganisaatioService', function ($resource, $log, $q, Config) {

    $log = $log.getInstance("OrganisaatioService");

    var orgHaku = $resource(Config.env["organisaatio.api.rest.url"] + "organisaatio/hae");
	var orgLuku = $resource(Config.env["organisaatio.api.rest.url"] + "organisaatio/:oid");

	function localize(organisaatio){
		//TODO olettaa että käyttäjä suomenkielinen
		organisaationimi=organisaatio.nimi.fi||organisaatio.nimi.sv||organisaatio.nimi.en;
		organisaatio.nimi=organisaationimi;
		organisaatio.sortNimi = organisaationimi.toLowerCase();
		if(organisaatio.children){
			localizeAll(organisaatio.children);
		}
		return organisaatio;
	}

	function localizeAll(organisaatioarray){
		angular.forEach(organisaatioarray, localize);
		organisaatioarray.sort(function(a,b){
		  ret = a.sortNimi.localeCompare(b.sortNimi);
		  return ret==0?a.nimi.localeCompare(b.nimi):ret;
		});
		return organisaatioarray;
        }

	function etsi(hakuehdot){
          var ret = $q.defer();
          //    $log.info('searching organisaatiot, q:', hakuehdot);

          orgHaku.get(hakuehdot,function(result){
            //        $log.info("resolving promise with hit count:" + result.numHits);
            localizeAll(result.organisaatiot);
            ret.resolve(result);
          });

          //    $log.info('past query now, returning promise...:');
          return ret.promise;
	}


        /*
         * Lisää organisaation oppilaitostyyppin (koodin uri) arrayhin jos se != undefined ja ei jo ole siinä
         */
        function addTyyppi(organisaatio, oppilaitostyypit){
            if(organisaatio.oppilaitostyyppi!==undefined && oppilaitostyypit.indexOf(organisaatio.oppilaitostyyppi)==-1){
                oppilaitostyypit.push(organisaatio.oppilaitostyyppi);
            }
        };

        /**
         * Koulutustoimija, kerää oppilaitostyypit lapsilta (jotka oletetaan olevan oppilaitoksia)
         */
        function getTyypitFromChildren(organisaatio, deferred) {
          var oppilaitostyypit = [];

          if (organisaatio.organisaatiotyypit.indexOf("KOULUTUSTOIMIJA") != -1
              && organisaatio.children !== undefined) {
            for ( var i = 0; i < organisaatio.children.length; i++) {
              addTyyppi(organisaatio.children[i], oppilaitostyypit);
            }
            deferred.resolve(oppilaitostyypit);
          }
          return deferred.promise;
        }



	/*
         * Hakee oppilaitostyypit organisaatiolle, koulutustoimijalle haetaan
         * allaolevista oppilaitoksista, oppilaitoksen tyypit tulee
         * oppilaitokselta, toimipisteen tyyppi typee ylemmän tason
         * oppilaitokselta. TODO lisää testi
         */
        function haeOppilaitostyypit(organisaatioOid) {

          var deferred = $q.defer();

          //hae org (ja sen alapuoliset)
          etsi({oidRestrictionList:organisaatioOid}).then(function(data) {

            var organisaatio = data.organisaatiot[0];
            if(organisaatio.organisaatiotyypit.indexOf("KOULUTUSTOIMIJA")!=-1) {
              return getTyypitFromChildren(organisaatio, deferred);
            }

            if(organisaatio.organisaatiotyypit.indexOf("OPPILAITOS")!=-1) {
              //oppilaitos, palauta tyyppi tästä
              var oppilaitostyypit=[];
              addTyyppi(organisaatio, oppilaitostyypit);
              deferred.resolve(oppilaitostyypit);
              return deferred.promise;
            }

            if(organisaatio.organisaatiotyypit.indexOf("OPETUSPISTE")!=-1) {
              $log.debug("toimipiste, recurse...");
              deferred.resolve(haeOppilaitostyypit(organisaatio.parentOid));
            };
          });

          return deferred.promise;

        };


	return {

	  haeOppilaitostyypit: haeOppilaitostyypit,

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
	   etsi: etsi,

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
