'use strict';

/* Controllers */

var app = angular.module('app.kk.edit.ctrl');

app.controller('EditYhteyshenkiloCtrl', ['$scope', '$compile', 'YhteyshenkiloService', function($scope, $compile, YhteyshenkiloService) {	
	
	$scope.editYhModel = {matchedUser: "",
						 data: []};
	
    $scope.changeClass = function (options) {
        var widget = options.methods.widget();
        // remove default class, use bootstrap style
        widget.removeClass('ui-menu ui-corner-all ui-widget-content').addClass('dropdown-menu');
    };
	
	/*
	 * Clearing of the contact person data.
	 */
	$scope.editYhModel.clearYh = function() {
		$scope.uiModel.contactPerson = {};
		$scope.editYhModel.matchedUser = "";
		$scope.editYhModel.searchPersonMap = {};
	};
	
	/*
	 * Method that watches the search field of the contact person.
	 * If the string in the field matches a person it updates the rest of the
	 * contact person in the form.
	 */
	$scope.$watch('uiModel.contactPerson.nimet', function() {
		console.log('watching: ' + $scope.uiModel.contactPerson.nimet);
		var hakuehdot = {searchTerm: $scope.uiModel.contactPerson.nimet};
    	
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
	});
	
	
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