var app = angular.module('app.haku.ctrl');

app.controller('GenerateLomakeController', function($modalInstance, hakuOid, $http, $scope, $injector) {
    'use strict';

    $scope.close = function() {
        $modalInstance.dismiss();
    };

    $scope.generateLomake = function(oid) {
        $scope.loading = true;

        $http.get(window.url("haku-app.pingLomake", oid))
            .then(function success(response) {
               $scope.callGenerateLomake(oid);
            }, $scope.handleError);
    };

    $scope.callGenerateLomake = function(oid) {
        $http.post(window.url("haku-app.generateLomake", oid))
            .then(function success(response) {
                $scope.loading = false;
                $scope.success = response.status == 200;
                $scope.error = !$scope.success;
            }, $scope.handleError);
    };

    $scope.handleError = function(response) {
        var loadingService = $injector.get('loadingService');
        loadingService.onErrorHandled();

        $scope.loading = false;
        $scope.error = true;
        $scope.permissionDenied = response.status == 403;
    };

    $scope.generateLomake(hakuOid);

});