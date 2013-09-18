'use strict';

/* Controllers */

var app = angular.module('tarjontaApp.controllers', ['tarjontaApp.services']);

app.controller('MyCtrl1', [function() {
        console.log("MyCtrl1()");
    }]);

app.controller('MyCtrl2', function($scope, instagram) {
    console.log("MyCtrl2()");

    $scope.pics = [];
    $scope.page = 0;
    $scope.pageSize = 2;

    instagram.fetchPopular(function(data) {
        $scope.pics = data;
    });


});
