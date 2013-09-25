/**
 * Organisaatio hakulomakkeen controlleri.
 */
angular.module('app.organisaatiohaku', [ 'app.services', 'angularTreeview','localisation' ])
.config(function($httpProvider) { $httpProvider.defaults.useXDomain = true;}) 

.controller('OrganisaatioHakuFormCtrl', function(LocalisationService, $scope, organisaatioService) {

	/**
	 * oletus hakuehdot
	 */
	$scope.hakuehdot = $scope.defaultHakuehdot = {
		"tekstihaku" : "axxe",
		"organisaatiotyyppi" : "",
		"oppilaitostyyppi" : "",
		"lakkautetut" : false,
		"suunnitellut" : false
	};
	
	//watchi valitulle organisaatiolle, tästä varmaan lähetetään "organisaatio valittu" eventti jonnekkin?
	$scope.$watch( 'organisaatio.currentNode', function( newObj, oldObj ) {
	    if( $scope.organisaatio && angular.isObject($scope.organisaatio.currentNode) ) {
	        console.log( 'Organisaatio valittu!' );
	        console.log( $scope.organisaatio.currentNode );
	    }
	}, false);
	
	
	
//	$scope.hakuehdot = angular.copy($scope.defaultHakuehdot);

	// hae jostain
	$scope.organisaatiotyypit = [{
		nimi : LocalisationService.t("organisaatiotyyppi.koulutustoimija"),
		koodi : 'KOULUTUSTOIMIJA'

	}, {
		nimi : LocalisationService.t("organisaatiotyyppi.oppilaitos"),
		koodi : "OPPILAITOS"

	}, {
		nimi : LocalisationService.t("organisaatiotyyppi.toimipiste"),
		koodi : "TOIMIPISTE"

	}, {
		nimi : LocalisationService.t("organisaatiotyyppi.oppisopimustoimipiste"),
		koodi : "OPPISOPIMUSTOIMIPISTE"

	}];
	
	
	/**
	 * Kutsutaan formin submitissa, käynnistää haun
	 */
	$scope.submit = function() {
		console.log("organisaatiosearch clicked!" + angular.toJson($scope.hakuehdot));
		hakutulos = organisaatioService.etsi($scope.hakuehdot.tekstihaku);
		hakutulos.then(function(vastaus){
			console.log("result returned, hits:" + vastaus.numHits);
			$scope.tulos=vastaus.organisaatiot;
			console.log("result returned, hits:" + angular.toJson(vastaus.organisaatiot));
		});
    };

	/**
	 * Kutsutaan formin resetissä, palauttaa default syötteet modeliin
	 */
    $scope.reset = function() {
		console.log("reset clicked!" + angular.toJson($scope.hakuehdot));
    	$scope.hakuehdot = angular.copy($scope.defaultHakuehdot);
    };
	
})

	//"organisaatioservice"
	.factory('organisaatioService', function ($resource, $log, $q) {
		var OrganisaatioService = $resource('https://itest-virkailija.oph.ware.fi/organisaatio-service/rest/organisaatio/hae?searchStr=:query');
		
   return {
	   
	   /**
	    * query (hakuehdot)
	    * @param query
	    * @returns
	    */
	   etsi: function(query){
		   var ret = $q.defer();
           $log.info('searching organisaatiot, q:' + query);
           
           OrganisaatioService.get({'query':query},function(result){
               $log.info("resolving promise with hit count:" + result.numHits);
        	  ret.resolve(result);
           }
        	   
           );
           $log.info('past query now, returning promise...:');
           return ret.promise;
	   }
   };
});

