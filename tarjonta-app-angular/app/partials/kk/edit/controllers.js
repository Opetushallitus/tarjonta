'use strict';

/* Controllers */

var app = angular.module('app.kk.edit.ctrl', ['Koodisto', 'ngResource']);

app.controller('KKEditController', ['$scope', 'TarjontaService', 'Config',
    function FormTutkintoController($scope, tarjontaService,  config) {
        $scope.searchByOid = "1.2.246.562.5.2013091114080489552096";
        $scope.opetuskieli = 'kieli_fi';
        $scope.model = {};

        console.log(config.env["accessRight.webservice.url.backend"]);

        $scope.search = function() {
            console.info("search()");
            tarjontaService.get({oid: $scope.searchByOid}, function(data) {
                $scope.model = data;
                $scope.model.koulutuksenAlkamisPvm = Date.parse(data.koulutuksenAlkamisPvm);
                console.info($scope.model);
            });
        };

        $scope.search();
    }])
    .controller('SelectTutkintoOhjelmaController', ['$scope','$modalInstance', 'Koodisto', '$q', function($scope, $modalInstance, Koodisto, $q) {
    	
    	$scope.stoModel = { koulutusalaKoodistoUri: 'koulutusalaoph2002',
    						tutkinnotFetched: false,
    						korkeakoulututkinnot: [],
    						hakutulokset: [],
    						active: {},
    						hakulause: '',
    						koulutusala: {}};
    	
    	$scope.getKkTutkinnot = function() {
    		var koulutusasteet = ["koulutusasteoph2002_60", "koulutusasteoph2002_61", "koulutusasteoph2002_62", "koulutusasteoph2002_63", "koulutusasteoph2002_70", "koulutusasteoph2002_71", "koulutusasteoph2002_72", "koulutusasteoph2002_73", "koulutusasteoph2002_80", "koulutusasteoph2002_81", "koulutusasteoph2002_82", "koulutusasteoph2002_90"];
    		var promises = [];
    		angular.forEach(koulutusasteet, function(value, key) {
    			promises.push(Koodisto.getYlapuolisetKoodit(value,'FI'));
    		});
    		
    		var koulutuskooditHaettu = $q.all(promises);
    		koulutuskooditHaettu.then(function(koodisParam) {
    			
    		angular.forEach(koodisParam, function(koodis, key) {
    			//console.log("koodis: " + koodis.length);
    			angular.forEach(koodis, function(koodi, key) {
    				//console.log("Koodi: " + koodi);
    				if (koodi.koodiKoodisto === 'koulutus') {
    					//console.log("Adding: " + koodi.koodiUri);
    					$scope.stoModel.korkeakoulututkinnot[koodi.koodiUri] = koodi;	
    				}
    			});
    			
    		});
    		$scope.stoModel.tutkinnotFetched = true;
    		$scope.searchTutkinnot();
    		
    		});
    	};
    	
    	$scope.toggleItem = function(hakutulos) {
    		console.log(hakutulos.koodiUri);
    		$scope.stoModel.active = hakutulos;
    	};
    	
    	$scope.isActive = function(hakutulos) {
    		console.log(hakutulos.koodiUri==$scope.stoModel.active.koodiUri);
    		return hakutulos.koodiUri==$scope.stoModel.active.koodiUri;
    	};
    	
    	$scope.searchTutkinnot = function() {
    		if (!$scope.stoModel.tutkinnotFetched) {
    			console.log("Fetching tutkinnot now");
    			$scope.getKkTutkinnot();
    		} else if ($scope.stoModel.koulutusala.koodiUri.length > 0){
    			console.log("Koulutusalauri: " + $scope.stoModel.koulutusala.koodiUri);
        		
        		console.log("Doing koodistorelation things");
        		var hakutulosPromise = Koodisto.getYlapuolisetKoodit($scope.stoModel.koulutusala.koodiUri,'FI');
        		hakutulosPromise.then(function(koodisParam) {
        			$scope.hakutulokset = koodisParam.filter(function (koodi) {
        									//console.log("is koodi in kk: " + $scope.stoModel.korkeakoulututkinnot[koodi.koodiUri]);
        									//console.log("boolean: " + ($scope.stoModel.korkeakoulututkinnot[koodi.koodiUri] != undefined));
        									return $scope.stoModel.korkeakoulututkinnot[koodi.koodiUri] != undefined;//koodi.koodiKoodisto === 'koulutus';
        			});
        		});
    		}
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
