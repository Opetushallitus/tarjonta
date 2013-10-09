'use strict';

/* Controllers */

var app = angular.module('app.kk.edit.ctrl');

app.controller('EditYhteyshenkiloCtrl', ['$scope', '$compile', 'YhteyshenkiloService', function($scope, $compile, YhteyshenkiloService) {	
	
	$scope.editYhModel = {data: [],
						 henkilotFetched: false};
	
	/*
	 * Clearing of the contact person data.
	 */
	$scope.editYhModel.clearYh = function() {
		$scope.uiModel.contactPerson = {};
	};
	
	/*
	 * Method that watches the search field of the contact person.
	 * Fetches users for the current organisation if those have not been fetced yet.
	 */
	$scope.$watch('uiModel.contactPerson.nimet', function() {
		if (!$scope.editYhModel.henkilotFetched) {
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
	
}]);