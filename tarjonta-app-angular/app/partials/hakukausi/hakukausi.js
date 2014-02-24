'use strict';

angular.module('app').config([ '$routeProvider', function($routeProvider) {
  console.log("adding hakukausi routes");
  // hakukausi routes
  $routeProvider.when("/hakukausi", {
    templateUrl : "partials/hakukausi/hakukausi.html"
  });
} ])

.controller("HakukausiController",
    [ "$scope", function HakukausiController($scope) {
      console.log("hello hakukausi");

      $scope.model = {
        parameters : {tila:"foo"},
        formControls : {},
        showError : false,
        showSuccess : false,
        validationmsgs : [],
        collapse : {
          model : true
        }
      }
    } ]);
