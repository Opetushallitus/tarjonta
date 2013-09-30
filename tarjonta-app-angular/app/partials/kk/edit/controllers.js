'use strict';

/* Controllers */

var app = angular.module('app.kk.edit.ctrl', ['Koodisto', 'ngResource', 'ngGrid']);

app.controller('KKEditController', ['$scope', 'TarjontaService', 'Config',
    function FormTutkintoController($scope, tarjontaService, cfg) {
        $scope.searchByOid = "1.2.246.562.5.2013091114080489552096";
        $scope.opetuskieli = 'kieli_fi';
        $scope.model = {};
        $scope.env = cfg.env;
        $scope.contactPerson = {};
        $scope.ectsCoordinator = {};

        console.log(cfg.env["accessRight.webservice.url.backend"]);


        $scope.search = function() {
            console.log("search()");
            console.log(tarjontaService);

            tarjontaService.getTutkinto({oid: $scope.searchByOid}, function(data) {
                console.log("data loaded()");
                $scope.model = data;
                $scope.model.koulutuksenAlkamisPvm = Date.parse(data.koulutuksenAlkamisPvm);

                angular.forEach($scope.model.yhteyshenkilos, function(value, key) {

                    if (value.henkiloTyyppi === 'YHTEYSHENKILO') {
                        $scope.contactPerson = $scope.converPersonObjectForUi(value);
                    } else if (value.henkiloTyyppi === 'ECTS_KOORDINAATTORI') {
                        $scope.ectsCoordinator = $scope.converPersonObjectForUi(value);
                    } else {
                        throw 'Undefined henkilotyyppi : ' + value;
                    }
                })
            });

        };
        $scope.search();

        /**
         * Convert person data to UI format.
         * 
         * @param {type} person
         * @returns {person}
         */
        $scope.converPersonObjectForUi = function(person) {
            if (person === null || typeof person === 'undefined') {
                throw 'Contact percon cannot be null';
            }
            person.nimet = person.etunimet + ' ' + person.sukunimi;
            return person; //dummy
        }

    }]);
