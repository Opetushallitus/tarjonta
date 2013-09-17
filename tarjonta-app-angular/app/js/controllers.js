'use strict';

/* Controllers */

var app = angular.module('tarjontaApp.controllers', []);

app.controller('MyCtrl1', [function() {
        console.log("MyCtrl1");
    }]);

app.controller('MyCtrl2', ['$scope', 'instagram', function($scope, instagram) {

        console.log("MyCtrl2");

        $scope.foo = "FOO";
        $scope.pics = [];

        // Use the instagram service and fetch a list of the popular pics
        instagram.fetchPopular(function(data) {

            // Assigning the pics array will cause the view
            // to be automatically redrawn by Angular.
            $scope.pics = data;
        });

    }]);
