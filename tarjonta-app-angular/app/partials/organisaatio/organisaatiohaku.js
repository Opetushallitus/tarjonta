/**
 * Organisaatio hakulomakkeen controlleri.
 */
angular.module('app.organisaatiohaku', [ 'app.services', 'angularTreeview' ])
.config(function($httpProvider) { $httpProvider.defaults.useXDomain = true;}) 

.controller('OrganisaatioHakuFormCtrl', function($scope, organisaatioService) {

	/**
	 * oletus hakuehdot
	 */
	$scope.defaultHakuehdot = {
		"tekstihaku" : "axxe",
		"organisaatiotyyppi" : "",
		"oppilaitostyyppi" : "",
		"lakkautetut" : false,
		"suunnitellut" : false
	};
	
	$scope.hakuehdot = angular.copy($scope.defaultHakuehdot);

	// hae jostain
	var organisaatiotyypit = [{
		nimi : "organisaatiotyyppi1",
		koodi : "koodi1"

	}, {
		nimi : "organisaatiotyyppi2",
		koodi : "koodi2"

	}, {
		nimi : "organisaatiotyyppi2",
		koodi : "koodi2"

	}, {
		nimi : "organisaatiotyyppi2",
		koodi : "koodi2"

	}, {
		nimi : "organisaatiotyyppi2",
		koodi : "koodi2"

	}, {
		nimi : "organisaatiotyyppi2",
		koodi : "koodi2"

	}, {
		nimi : "organisaatiotyyppi2",
		koodi : "koodi2"

	}, {
		nimi : "organisaatiotyyppi2",
		koodi : "koodi2"

	}, {
		nimi : "organisaatiotyyppi2",
		koodi : "koodi2"

	}, {
		nimi : "organisaatiotyyppi2",
		koodi : "koodi2"

	}, {
		nimi : "organisaatiotyyppi2",
		koodi : "koodi2"

	}];
	
    angular.forEach(organisaatiotyypit, function(value, key) {
    	console.log("processing: " + angular.toJson(value));

    	Object.defineProperty(value, "value", {
    	    value: 101,
    	    writable: true,
    	    enumerable: true,
    	    configurable: true
    	});

    	console.log("processed: " + angular.toJson(value));

		});
	
    $scope.organisaatiotyypit=angular.copy(organisaatiotyypit);
	
	// hae koodistosta
	$scope.oppilaitostyypit = [ 
	    {
	    	nimi : "arvo1",
	    	koodi : "koodi1"
		}, 
		{
			nimi : "arvo2",
			koodi : "koodi2"

		}
		];
	
	$scope.tulos=[];
	
	
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

