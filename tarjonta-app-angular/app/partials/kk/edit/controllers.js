'use strict';

/* Controllers */

var app = angular.module('app.kk.edit.ctrl', ['Koodisto', 'ngResource', 'ngGrid']);

app.controller('KKEditController', ['$scope', 'TarjontaService', 'Config',
    function FormTutkintoController($scope, tarjontaService,  cfg) {
        $scope.searchByOid = "1.2.246.562.5.2013091114080489552096";
        $scope.opetuskieli = 'kieli_fi';
        $scope.model = {};
        $scope.env = cfg.env;

        console.log(cfg.env["accessRight.webservice.url.backend"]);

        $scope.search = function() {
            console.info("search()");
            tarjontaService.get({oid: $scope.searchByOid}, function(data) {
                $scope.model = data;
                $scope.model.koulutuksenAlkamisPvm = Date.parse(data.koulutuksenAlkamisPvm);
                console.info($scope.model);
            });
        };

        $scope.search();
    }]);
