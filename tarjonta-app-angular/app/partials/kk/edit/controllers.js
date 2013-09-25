'use strict';

/* Controllers */

var app = angular.module('app.kk.edit.ctrl', ['Koodisto']);

app.controller('KKEditController', ['$scope', 'TarjontaService',
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
    .controller('SelectTutkintoOhjelmaController', ['$scope','$modalInstance', 'Koodisto', function($scope, $modalInstance, Koodisto) {
    	
    	$scope.stoModel = { koulutusalaKoodistoUri: 'koulutusalaoph2002',//CONFIG.env['koodisto-uris.koulutusala'],
    						hakutulokset: [],
    						active: {},
    						hakulause: '',
    						koulutusala: {}};
    	
    	$scope.toggleItem = function(hakutulos) {
    		console.log(hakutulos.koodiUri);
    		$scope.stoModel.active = hakutulos;
    	};
    	
    	$scope.isActive = function(hakutulos) {
    		console.log(hakutulos.koodiUri==$scope.stoModel.active.koodiUri);
    		return hakutulos.koodiUri==$scope.stoModel.active.koodiUri;
    	};
    	
    	$scope.searchTutkinnot = function() {
    		console.log("Koulutusalauri: " + $scope.stoModel.koulutusala.koodiUri);
    		//console.log(CONFIG);
    		//console.log(CONFIG.env['koodisto-uris.tutkinto']);
    		if($scope.stoModel.koulutusala.koodiUri.length > 0) {
    			console.log("Doing koodistorelation things");
    			var hakutulosPromise = Koodisto.getYlapuolisetKoodit($scope.stoModel.koulutusala.koodiUri,'FI');
    			hakutulosPromise.then(function(koodisParam) {
    				var prelHakutulokset = koodisParam.filter(function (koodi) {
    																	return koodi.koodiKoodisto === 'koulutus';
    				});
    				$scope.performStringSearch(prelHakutulokset);
    			});
    		} else {
    			console.log("Doing pure search on tutkinnot");
    			var tutkinnotPromise = Koodisto.getAllKoodisWithKoodiUri('koulutus','FI');
    	        tutkinnotPromise.then(function(koodisParam){
    	            var allTutkinnot = koodisParam;
    	            $scope.performStringSearch(allTutkinnot);
    	        });
    		}
    		/*$scope.stoModel.hakutulokset = $scope.rawData.filter(function (element) {
    												return element.nimi.toLowerCase().indexOf($scope.stoModel.hakulause.toLowerCase()) > -1;
    											});*/
    	};
    	
    	$scope.performStringSearch = function(tutkinnot) {
    		$scope.stoModel.hakutulokset = tutkinnot.filter(function (element) {
				return element.koodiNimi.toLowerCase().indexOf($scope.stoModel.hakulause.toLowerCase()) > -1;
			});
    	};
    	
    	
    	
    	$scope.clearCriteria = function() {
    		$scope.stoModel.hakulause = '';
    	};
		
		$scope.ok = function() {
			console.log('Dialog ok pressed');
    		$modalInstance.close($scope.stoModel.active);
    	};
    	
    	$scope.cancel = function() {
    		console.log('Dialog cancel pressed');
    		$modalInstance.dismiss();
    	};
    	
    }])
    .controller('TutkintoOhjelmaSelectOpenerCtrl', ['$scope', '$modal', function($scope, $modal) {	
    	$scope.model = {};
    	
    	$scope.open = function() {
    		
    			var modalInstance = $modal.open({
    				scope: $scope,
    				templateUrl: 'partials/kk/edit/selectTutkintoOhjelma.html',
    				controller: 'SelectTutkintoOhjelmaController'
    			});
    		
    			modalInstance.result.then(function(selectedItem) {
    				console.log('Ok, dialog closed: ' + selectedItem.koodiUri);
    				if (selectedItem.koodiUri != null) {
    					$scope.model.selected = selectedItem;
    				} else {
    					$scope.model.selected = null;
    				}
    			}, function() {
    				$scope.model.selected = null;
    				console.log('Cancel, dialog closed');
    			});

    	};
    }]);
