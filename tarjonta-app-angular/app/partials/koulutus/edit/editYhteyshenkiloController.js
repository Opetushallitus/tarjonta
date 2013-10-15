'use strict';

/* Controllers */

var app = angular.module('app.edit.ctrl');

app.controller('EditYhteyshenkiloCtrl', ['$scope', '$compile', 'YhteyshenkiloService', 'TarjontaConverterFactory', function($scope, $compile, YhteyshenkiloService, converter) {	
	
	$scope.editYhModel = {data: [],
						 henkilotFetched: false};
	
	/*
	 * Clearing of the contact person data.
	 */
	$scope.editYhModel.clearYh = function() {
		$scope.uiModel.contactPerson = {};
	};
	
	/*
	 * Clearing of the ects coordinator data.
	 */
	$scope.editYhModel.clearEctsYh = function() {
		$scope.uiModel.ectsCoordinator = {};
	};
	
	/*
	 * Fetches henkilos belonging to the tarjoaja organisation
	 */
	$scope.editYhModel.fetchHenkilot = function() {
		if (!$scope.editYhModel.henkilotFetched && !converter.isNull($scope.model.organisaatio)) {
			$scope.editYhModel.henkilotFetched = true;
			console.log('Going to fetch yhteyshenkilot for organisaatio: ' + $scope.model.organisaatio.oid);
			var hakuehdot = {organisaatio: $scope.model.organisaatio.oid};
    	
			var yhteyshenkiloHaku = YhteyshenkiloService.etsi(hakuehdot);
    	
			yhteyshenkiloHaku.then(function(result) {
				console.log("Saatiin tulos: ");
				console.log(result);
    		
				var results = result.result;
    		
				$scope.editYhModel.data = [];
				$scope.editYhModel.searchPersonMap = {};
				angular.forEach(results, function(value, key) {
					var curNimet = value.etunimet + ' ' + value.sukunimi;
					$scope.editYhModel.data.push(curNimet);
					$scope.editYhModel.searchPersonMap[curNimet] = value;
				});
			});
		}
	}
	
	/*
	 * Method that watches the search field of the contact person.
	 * Fetches users for the current organisation if those have not been fetced yet.
	 */
	$scope.$watch('uiModel.contactPerson.nimet', function() {
		$scope.editYhModel.fetchHenkilot();
	});
	
	/*
	 * Method that watches the search field of the ects coordinator.
	 * Fetches users for the current organisation if those have not been fetced yet.
	 */
	$scope.$watch('uiModel.ectsCoordinator.nimet', function() {
		$scope.editYhModel.fetchHenkilot();
	});
	
	/*
	 * Sets the contact person to be the one that the user selected from the autocomplete field.
	 */
	$scope.editYhModel.selectHenkilo = function() {
		
		if ($scope.editYhModel.searchPersonMap != undefined 
			&& $scope.editYhModel.searchPersonMap[$scope.uiModel.contactPerson.nimet] != undefined) {
			var selectedUser = $scope.editYhModel.searchPersonMap[$scope.uiModel.contactPerson.nimet];
			$scope.uiModel.contactPerson.sahkoposti = selectedUser.sahkoposti;
			$scope.uiModel.contactPerson.titteli = selectedUser.titteli;
			$scope.uiModel.contactPerson.puhelin = selectedUser.puhelin;
			$scope.uiModel.contactPerson.etunimet = selectedUser.etunimet;
			$scope.uiModel.contactPerson.sukunimi = selectedUser.sukunimi;
       
		} else {
			$scope.uiModel.contactPerson = {};
		}
	};
	
	/*
	 * Sets the ects coordinator to be the one that the user selected from the autocomplete field.
	 */
	$scope.editYhModel.selectEctsHenkilo = function() {
		
		if ($scope.editYhModel.searchPersonMap != undefined 
			&& $scope.editYhModel.searchPersonMap[$scope.uiModel.ectsCoordinator.nimet] != undefined) {
			var selectedUser = $scope.editYhModel.searchPersonMap[$scope.uiModel.ectsCoordinator.nimet];
			$scope.uiModel.ectsCoordinator.titteli = selectedUser.titteli;
			$scope.uiModel.ectsCoordinator.puhelin = selectedUser.puhelin;
			$scope.uiModel.ectsCoordinator.etunimet = selectedUser.etunimet;
			$scope.uiModel.ectsCoordinator.sukunimi = selectedUser.sukunimi;
       
		} else {
			$scope.uiModel.ectsCoordinator = {};
		}
	};
	
}]);