'use strict';

/* Controllers */

var app = angular.module('app.kk.edit.ctrl');

app.controller('EditYhteyshenkiloCtrl', ['$scope', '$compile', function($scope, $compile) {	
	
	$scope.editYhModel = {matchedUser: ""};
	
	//This is test data
	$scope.editYhModel.searchNames = ["Pekka Pekkola", "Matti Virtanen", "Pekko Paavolainen"];
	$scope.editYhModel.searchPersonMap = [];
	$scope.editYhModel.searchPersonMap["Pekka Pekkola"] = {nimet: "Pekka Pekkola", sahkoposti: "pepe@oph.fi", titteli: "munkki", puhelin: "1234567", etunimet: "Pekka", sukunimi: "Pekkola"};
	$scope.editYhModel.searchPersonMap["Matti Virtanen"] = {nimet: "Matti Virtanen", sahkoposti: "mavi@oph.fi", titteli: "apotti", puhelin: "1234568", etunimet: "Matti", sukunimi: "Virtanen"};
	$scope.editYhModel.searchPersonMap["Pekko Paavolainen"] = {nimet: "Pekko Paavolainen", sahkoposti: "pepa@oph.fi", titteli: "paavi", puhelin: "1234569", etunimet: "Pekko", sukunimi: "Paavolainen"};
	
	
    $scope.changeClass = function (options) {
        var widget = options.methods.widget();
        // remove default class, use bootstrap style
        widget.removeClass('ui-menu ui-corner-all ui-widget-content').addClass('dropdown-menu');
    };
	
	$scope.myOption = {
            options: {
                html: true,
                minLength: 1,
                onlySelect: '',
                outHeight: 50,
                source: function (request, response) {
                	var data = [];
                	data = $scope.myOption.methods.filter($scope.editYhModel.searchNames, $scope.editYhModel.matchedUser);
                    response(data);
                }
				
            }
            
        };

	
	
	
	/*
	 * Clearing of the contact person data.
	 */
	$scope.editYhModel.clearYh = function() {
		$scope.uiModel.contactPerson = {};
		$scope.editYhModel.matchedUser = "";
	};
	
	/*
	 * Method that watches the search field of the contact person.
	 * If the string in the field matches a person it updates the rest of the
	 * contact person in the form.
	 */
	$scope.$watch('editYhModel.matchedUser', function() {
		console.log("Changed: " + $scope.editYhModel.matchedUser);
		if ($scope.editYhModel.searchPersonMap[$scope.editYhModel.matchedUser] != undefined) {
			
			var selectedUser = $scope.editYhModel.searchPersonMap[$scope.editYhModel.matchedUser];
			$scope.uiModel.contactPerson.nimet = selectedUser.nimet;
			$scope.uiModel.contactPerson.sahkoposti = selectedUser.sahkoposti;
	        $scope.uiModel.contactPerson.titteli = selectedUser.titteli;
	        $scope.uiModel.contactPerson.puhelin = selectedUser.puhelin;
	        $scope.uiModel.contactPerson.etunimet = selectedUser.etunimet;
	        $scope.uiModel.contactPerson.sukunimi = selectedUser.sukunimi;
	        //$scope.editYhModel.matchedUser = selectedUser.nimet;
		} else {
			$scope.uiModel.contactPerson = {};
		}
	});
	
}]);