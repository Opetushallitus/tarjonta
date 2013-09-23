'use strict';

/* Controllers */

angular.module('kkTutkintoApp.controllers', []).controller('FormTutkintoController', ['$scope', 'TarjontaService',
    function FormTutkintoController($scope, tarjontaService) {
        $scope.searchByOid = "1.2.246.562.5.2013091114080489552096";
        $scope.opetuskieli = 'kieli_fi';
        $scope.model = {};

        $scope.search = function() {
            console.info("search()");
            tarjontaService.get({oid: $scope.searchByOid}, function(data) {
                $scope.model = data;
                $scope.model.koulutuksenAlkamisPvm = Date.parse(data.koulutuksenAlkamisPvm);
                console.info($scope.model)
            });
        };

        $scope.search();
    }]);
