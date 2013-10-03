'use strict';

/* Controllers */

var app = angular.module('app.kk.edit.ctrl');

app.controller('EditYhteyshenkiloCtrl', ['$scope', function($scope) {	
	
	$scope.editYhModel = {};
	
	$scope.editYhModel.clearYh = function() {
		$scope.contactPerson.nimet = '';
		$scope.contactPerson.sahkoposti = '';
        $scope.contactPerson.titteli = '';
        $scope.contactPerson.puhelin = '';
        $scope.contactPerson.etunimet = '';
        $scope.contactPerson.sukunimi = '';
	};
	
	$scope.$watch('contactPerson.nimet', function() {
		console.log("Hei there is change, now nimet is: " + $scope.contactPerson.nimet);
	});
	
}]);