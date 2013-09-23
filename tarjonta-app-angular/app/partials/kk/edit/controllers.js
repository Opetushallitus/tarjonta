'use strict';

/* Controllers */

var app_kk_edit_ctrl = angular.module('app.kk.edit.ctrl', []);

app_kk_edit_ctrl.controller('KKEditController', ['$scope', 'TarjontaService',
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
