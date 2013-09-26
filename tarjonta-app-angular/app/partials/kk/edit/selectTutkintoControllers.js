'use strict';

/* Controllers */

var app = angular.module('app.kk.edit.ctrl');

app.controller('SelectTutkintoOhjelmaController', ['$scope','$modalInstance', 'Koodisto', '$q', function($scope, $modalInstance, Koodisto, $q) {
	
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
		var tempTutkinnot = [];
		if (!$scope.stoModel.tutkinnotFetched) {
			console.log("FETCHING TUTKINNOT NOW");
			$scope.getKkTutkinnot();
		} else if ($scope.stoModel.koulutusala.koodiUri != undefined && $scope.stoModel.koulutusala.koodiUri.length > 0){
			console.log("FILTERING BY KOULUTUSALA");
			console.log("Koulutusalauri: " + $scope.stoModel.koulutusala.koodiUri);
    		
    		console.log("Doing koodistorelation things");
    		var hakutulosPromise = Koodisto.getYlapuolisetKoodit($scope.stoModel.koulutusala.koodiUri,'FI');
    		hakutulosPromise.then(function(koodisParam) {
    			tempTutkinnot = koodisParam.filter(function (koodi) {
    									//console.log("is koodi in kk: " + $scope.stoModel.korkeakoulututkinnot[koodi.koodiUri]);
    									//console.log("boolean: " + ($scope.stoModel.korkeakoulututkinnot[koodi.koodiUri] != undefined));
    									return $scope.stoModel.korkeakoulututkinnot[koodi.koodiUri] != undefined;//koodi.koodiKoodisto === 'koulutus';
    			});
    			$scope.performStringSearch(tempTutkinnot);
    		});
		} else {
			console.log("PURE STRING SEARCH!!!");
			/*angular.forEach($scope.stoModel.korkeakoulututkinnot, function(value, key) {
				$scope.stoModel.hakutulokset.push(value);
			});*/
			for (var k in $scope.stoModel.korkeakoulututkinnot) {
				if ($scope.stoModel.korkeakoulututkinnot.hasOwnProperty(k))
				tempTutkinnot.push($scope.stoModel.korkeakoulututkinnot[k]);
			}
			$scope.performStringSearch(tempTutkinnot);
		}
	};
	
	$scope.performStringSearch = function(tutkinnot) {
		 console.log("PERFORMING STRING SEARCH");
		 console.log(tutkinnot);
		 console.log("hakulause: " + $scope.stoModel.hakulause);
	     $scope.stoModel.hakutulokset = tutkinnot.filter(function (element) {
	return (element.koodiNimi.toLowerCase().indexOf($scope.stoModel.hakulause.toLowerCase()) > -1) || (element.koodiArvo.indexOf($scope.stoModel.hakulause) > -1);
	});
	     };
	
	$scope.clearCriteria = function() {
		$scope.stoModel.hakulause = '';
		$scope.stoModel.koulutusala = {};
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