'use strict';

/* Controllers */

var app = angular.module('app.kk.edit.ctrl');

app.controller('EditYhteyshenkiloCtrl', ['$scope', '$compile', function($scope, $compile) {	
	
	$scope.editYhModel = {};
	
	$scope.myOption = {
            options: {
                html: true,
                minLength: 1,
                onlySelect: true,
                outHeight: 50,
                source: function (request, response) {
                    var data = [
                            "Asp",
                            "BASIC",
                            "C",
                            "C++",
                            "Clojure",
                            "COBOL",
                            "ColdFusion",
                            "Erlang",
                            "Fortran",
                            "Groovy",
                            "Haskell",
                            "Java",
                            "JavaScript",
                            "Lisp",
                            "Perl",
                            "PHP",
                            "Python",
                            "Ruby",
                            "Scala",
                            "Scheme"
                    ];
                    data = $scope.myOption.methods.filter(data, request.term);

                    if (!data.length) {
                        data.push({
                            label: 'not found',
                            value: null
                        });
                    }
                    // add "Add Language" button to autocomplete menu bottom
                    /*data.push({
                        label: $compile('<a class="ui-menu-add" ng-click="add()">Add Language</a>')($scope),
                        value: null
                    });
                    response(data);*/
                }
            }
        };
	
	$scope.editYhModel.testPersons = ["Pekka Pekkola", "Matti Virtanen", "Pekko Paavolainen"];
	
	$scope.editYhModel.clearYh = function() {
		$scope.contactPerson.nimet = '';
		$scope.contactPerson.sahkoposti = '';
        $scope.contactPerson.titteli = '';
        $scope.contactPerson.puhelin = '';
        $scope.contactPerson.etunimet = '';
        $scope.contactPerson.sukunimi = '';
	};
	
	$scope.$watch('contactPerson.nimet', function() {
		//console.log("Hei there is change, now nimet is: " + $scope.contactPerson.nimet);
	});
	
}]);