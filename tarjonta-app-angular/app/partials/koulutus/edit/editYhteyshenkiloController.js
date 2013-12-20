'use strict';

/* Controllers */

var app = angular.module('app.edit.ctrl');

app.controller('EditYhteyshenkiloCtrl', ['$scope', '$compile', 'YhteyshenkiloService', 'KoulutusConverterFactory', 'debounce', function($scope, $compile, YhteyshenkiloService, converter, debounce) {

        $scope.editYhModel = {data: [],
            henkilotFetched: false};

        /*
         * Clearing of the contact person data.
         */
        $scope.editYhModel.clearYh = function() {
            $scope.uiModel.contactPerson = {};
        };

        /*
         * Clearing of the ects coordinator data.
         */
        $scope.editYhModel.clearEctsYh = function() {
            $scope.uiModel.ectsCoordinator = {};
        };

//
//
//        /*
//         * Method that watches the search field of the contact person.
//         * Fetches users for the current organisation if those have not been fetced yet.
//         */
//        $scope.$watch('uiModel.contactPerson.nimet', function() {
//            $scope.editYhModel.fetchHenkilot();
//        });

//        /*
//         * Method that watches the search field of the ects coordinator.
//         * Fetches users for the current organisation if those have not been fetced yet.
//         */
//        $scope.$watch('uiModel.ectsCoordinator.nimet', function() {
//            $scope.editYhModel.fetchHenkilot();
//        });

        /*
         * Sets the contact person to be the one that the user selected from the autocomplete field.
         */
        $scope.editYhModel.selectHenkilo = function() {

            if ($scope.editYhModel.searchPersonMap != undefined
                    && $scope.editYhModel.searchPersonMap[$scope.uiModel.contactPerson.nimet] != undefined) {
                var selectedUser = $scope.editYhModel.searchPersonMap[$scope.uiModel.contactPerson.nimet];
                $scope.uiModel.contactPerson.sahkoposti = selectedUser.sahkoposti;
                $scope.uiModel.contactPerson.titteli = selectedUser.titteli;
                $scope.uiModel.contactPerson.puhelin = selectedUser.puhelin;
                $scope.uiModel.contactPerson.etunimet = selectedUser.etunimet;
                $scope.uiModel.contactPerson.sukunimi = selectedUser.sukunimi;

            } else {
                $scope.uiModel.contactPerson = {};
            }
        };

        /*
         * Sets the ects coordinator to be the one that the user selected from the autocomplete field.
         */
        $scope.editYhModel.selectEctsHenkilo = function() {

            if ($scope.editYhModel.searchPersonMap != undefined
                    && $scope.editYhModel.searchPersonMap[$scope.uiModel.ectsCoordinator.nimet] != undefined) {
                var selectedUser = $scope.editYhModel.searchPersonMap[$scope.uiModel.ectsCoordinator.nimet];
                $scope.uiModel.ectsCoordinator.titteli = selectedUser.titteli;
                $scope.uiModel.ectsCoordinator.puhelin = selectedUser.puhelin;
                $scope.uiModel.ectsCoordinator.etunimet = selectedUser.etunimet;
                $scope.uiModel.ectsCoordinator.sukunimi = selectedUser.sukunimi;

            } else {
                $scope.uiModel.ectsCoordinator = {};
            }
        };

        $scope.doFiltering = function() {
            var scope = this;
            debounce("filterYhteyshenkilo", function() {
                var hakuehdot = {
                    organisaatioOid: '1.2.246.562.10.00000000001',
                    terms: scope.uiModel.contactPerson.nimet
                };
                var resource = YhteyshenkiloService.resourceYhteyshenkilo.search(hakuehdot);

                return resource.$promise.then(function(response) {
                    console.log("Saatiin tulos: ");
                    console.log(response.result);
                    if (response.status === 'OK') {
                        var results = response.result;
                        $scope.editYhModel.data = [];
                        $scope.editYhModel.searchPersonMap = {};
                        angular.forEach(results, function(value, key) {
                            var curNimet = value.etunimet + ' ' + value.sukunimi;
                            $scope.editYhModel.data.push(curNimet);
                            $scope.editYhModel.searchPersonMap[curNimet] = value;
                        });

                    } else {
                        console.log("Error in contact person service : " + response.status);
                    }
                });
            }, 300);
        };

    }]);