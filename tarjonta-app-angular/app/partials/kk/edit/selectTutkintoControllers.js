'use strict';

/* Controllers */

var app = angular.module('app.kk.edit.ctrl');

app.controller('SelectTutkintoOhjelmaController', ['$scope','$modalInstance', 'Koodisto', '$q',  'Config', function($scope, $modalInstance, Koodisto, $q, config) {
	
	//filtterisivun malli
	$scope.stoModel = { koulutusalaKoodistoUri: config.env["koodisto-uris.koulutusala"],
						tutkinnotFetched: false,
						korkeakoulututkinnot: [],
						hakutulokset: [],
						active: {},
						hakulause: '',
						koulutusala: {}};
	
	/*$scope.myData = [{name: "Moroni", age: 50},
	                 {name: "Tiancum", age: 43},
	                 {name: "Jacob", age: 27},
	                 {name: "Nephi", age: 29},
	                 {name: "Enos", age: 34}];*/
	
	$scope.gridOptions = { data: 'stoModel.hakutulokset',
			columnDefs: [{field: 'koodiArvo', displayName: 'Koodi'}, {field:'koodiNimi', displayName: 'Nimi'}]};
	
	//Korkeakoulututukintojen haku koodistosta (kaytetaan relaatioita koulutusastekoodeihin) 
	//Kutsutaan haun yhteydessa jos kk tutkintoja ei viela haettu
	$scope.getKkTutkinnot = function() {
		var koulutusasteet = config.app["tarjonta.koulutusaste.korkeakoulu-uris"];
		//Muodostetaan nippu promiseja, jolloin voidaan toimia sitten kun kaikki promiset taytetty
		var promises = [];
		angular.forEach(koulutusasteet, function(value, key) {
			promises.push(Koodisto.getYlapuolisetKoodit(value,'FI'));
		});
		var koulutuskooditHaettu = $q.all(promises);
		koulutuskooditHaettu.then(function(koodisParam) {
			
			//laitetaan korkeakoulututkinnot koodiuri: koodi -mappiin
			angular.forEach(koodisParam, function(koodis, key) {
				angular.forEach(koodis, function(koodi, key) {
					if (koodi.koodiKoodisto === config.env["koodisto-uris.tutkinto"]) {
						$scope.stoModel.korkeakoulututkinnot[koodi.koodiUri] = koodi;	
					}
				});
			
			});
			//sitten aloitetaan varsinainen haku
			$scope.stoModel.tutkinnotFetched = true;
			$scope.searchTutkinnot();
		
		});
	};
	
	//Tulosrivin valinta
	$scope.toggleItem = function(hakutulos) {
		console.log(hakutulos.koodiUri);
		$scope.stoModel.active = hakutulos;
	};
	
	//Onko hakutulosrivi valittu
	$scope.isActive = function(hakutulos) {
		console.log(hakutulos.koodiUri==$scope.stoModel.active.koodiUri);
		return hakutulos.koodiUri==$scope.stoModel.active.koodiUri;
	};
	
	//Haun suorittaminen
	$scope.searchTutkinnot = function() {
		var tempTutkinnot = [];
		//Jos kk-tutkintoja ei haettu ne haetaan ensin
		if (!$scope.stoModel.tutkinnotFetched) {
			$scope.getKkTutkinnot();
		//Jos koulutusalavalittu filtteroidaan koulutusala -> koulutusrelaation avulla minka jalkeen string-haku
		} else if ($scope.stoModel.koulutusala.koodiUri != undefined && $scope.stoModel.koulutusala.koodiUri.length > 0){
			console.log("Koulutusalauri: " + $scope.stoModel.koulutusala.koodiUri);
    		var hakutulosPromise = Koodisto.getYlapuolisetKoodit($scope.stoModel.koulutusala.koodiUri,'FI');
    		hakutulosPromise.then(function(koodisParam) {
    			tempTutkinnot = koodisParam.filter(function (koodi) {
    									return $scope.stoModel.korkeakoulututkinnot[koodi.koodiUri] != undefined;
    			});
    			$scope.performStringSearch(tempTutkinnot);
    		});
    	//Muuten kaikki kk-tutkinnot ok suoritetaan vain string-haku
		} else {
			for (var k in $scope.stoModel.korkeakoulututkinnot) {
				if ($scope.stoModel.korkeakoulututkinnot.hasOwnProperty(k))
				tempTutkinnot.push($scope.stoModel.korkeakoulututkinnot[k]);
			}
			$scope.performStringSearch(tempTutkinnot);
		}
	};
	
	//string-haun suorittaminen
	$scope.performStringSearch = function(tutkinnot) {
		 console.log("Performing string search");
	     $scope.stoModel.hakutulokset = tutkinnot.filter(function (element) {
	return (element.koodiNimi.toLowerCase().indexOf($scope.stoModel.hakulause.toLowerCase()) > -1) || (element.koodiArvo.indexOf($scope.stoModel.hakulause) > -1);
	});
	     };
	
	//Hakukriteerien tyhjennys
	$scope.clearCriteria = function() {
		$scope.stoModel.hakulause = '';
		$scope.stoModel.koulutusala = {};
	};
	
	//dialogin sulkeminen ok-napista, valitun hakutuloksen palauttaminen
	$scope.ok = function() {
		$modalInstance.close($scope.stoModel.active);
	};
	
	//dialogin sulkeminen peruuta-napista
	$scope.cancel = function() {
		$modalInstance.dismiss();
	};
	
}])
.controller('TutkintoOhjelmaSelectOpenerCtrl', ['$scope', '$modal', function($scope, $modal) {	
	$scope.model = {};
	
	$scope.myData = [{name: "Moroni", age: 50},
	                 {name: "Tiancum", age: 43},
	                 {name: "Jacob", age: 27},
	                 {name: "Nephi", age: 29},
	                 {name: "Enos", age: 34}];
	
	$scope.gridOptions = { data: 'myData' };
	
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