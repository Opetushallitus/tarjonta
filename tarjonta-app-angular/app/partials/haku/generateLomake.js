var app = angular.module('app.haku.ctrl');

app.controller('GenerateLomakeController', function($modalInstance, hakuOid, $http, $scope, $injector) {
    'use strict';

    $scope.close = function() {
        $modalInstance.dismiss();
    };

    $scope.generateLomake = function(oid) {
        $scope.loading = true;

        $http.post('/haku-app/generatelomake/one/' + oid)
            .then(function success(response) {
                $scope.loading = false;
                $scope.success = response.status == 200;
                $scope.error = !$scope.success;
            }, function error(response) {
                var loadingService = $injector.get('loadingService');
                loadingService.onErrorHandled();

                $scope.loading = false;
                $scope.error = true;
                $scope.permissionDenied = response.status == 403;
            });
    };

    $scope.generateLomake(hakuOid);

});