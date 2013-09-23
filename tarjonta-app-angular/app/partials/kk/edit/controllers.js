'use strict';

/* Controllers */

angular.module('app.kk.edit.ctrl', []).controller('EditController', ['$scope', 'TarjontaService',
    function FormTutkintoController($scope, tarjontaService) {
        $scope.searchByOid = "1.2.246.562.5.2013091114080489552096";
        $scope.opetuskieli = 'kieli_fi';
        $scope.model = {};

        $scope.search = function() {
            console.info("search()");
            tarjontaService.get({oid: $scope.searchByOid}, function(data) {
                $scope.model = data;
                $scope.model.koulutuksenAlkamisPvm = Date.parse(data.koulutuksenAlkamisPvm);
                console.info($scope.model)
            });
        };

        $scope.search();
    }])
    .controller('SelectTutkintoOhjelmaController', ['$scope', function($scope) {
    	
    	$scope.stoModel = {koulutusala: 'Humanistinen ja kasvatusala', 
    						hakuteksti: '',
    						hakutulokset: [{nimi: 'Tanssinopettaja AMK', tkKoodi: '611201', uri: 'koulutus_1'},
    							               {nimi: 'Taideteollisuusopiston tutkinto', tkKoodi: '622951', uri: 'koulutus_2'}],
    						active: {}};
    	
    	
    	$scope.toggleItem = function(hakutulos) {
    		console.log(hakutulos.uri);
    		$scope.stoModel.active = hakutulos;
    	};
    	
    	$scope.isActive = function(hakutulos) {
    		console.log(hakutulos.uri==$scope.stoModel.active.uri);
    		return hakutulos.uri==$scope.stoModel.active.uri;
    	}
    }]);
