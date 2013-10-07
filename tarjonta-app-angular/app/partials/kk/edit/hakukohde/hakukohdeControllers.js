'use strict';

/* Controllers */

var app = angular.module('app.kk.edit.hakukohde.ctrl',[]);


app.controller('HakukohdeEditController', ['$scope', 'Koodisto', '$q',  'Config', function($scope, Koodisto, $q, config) {

       console.log('HAKUKOHDE CONTROLLERS');

      $scope.helloMsg = "HELLO HAKUKOHDE";
}]);