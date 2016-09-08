var app = angular.module('app.haku.ctrl');

app.controller('GenerateLomakeController', function($modalInstance, hakuOid, $http, $scope, $injector) {
    'use strict';

    $scope.close = function() {
        $modalInstance.dismiss();
    };

    $scope.generateLomake = function(oid) {
        $scope.loading = true;

        $http.get('/haku-app/generatelomake/ping')
            .then(function success(response) {
                $http.post('/haku-app/generatelomake/one/' + oid)
                    .then(function success(response) {
                        $scope.loading = false;
                        $scope.success = response.status == 200;
                        $scope.error = !$scope.success;
                    }, handleError(response))
            }, handleError(response));
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