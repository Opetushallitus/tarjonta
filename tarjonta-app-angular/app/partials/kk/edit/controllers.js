'use strict';

/* Controllers */

var app = angular.module('app.kk.edit.ctrl', []);

app.controller('KKEditController', ['$scope', 'TarjontaService', 'Config',
    function FormTutkintoController($scope, tarjontaService, Config) {
        $scope.searchByOid = "1.2.246.562.5.2013091114080489552096";
        $scope.opetuskieli = 'kieli_fi';
        $scope.model = {};

        $scope.search = function() {
            tarjontaService.get({oid: $scope.searchByOid}, function(data) {
                $scope.model = data;
                $scope.model.koulutuksenAlkamisPvm = Date.parse(data.koulutuksenAlkamisPvm);
                console.info($scope.model)
            });
        };

        $scope.search();
    }])
        .controller('SelectTutkintoOhjelmaController', ['$scope', '$modalInstance', function($scope, $modalInstance) {

        $scope.rawData = [{nimi: 'Tanssinopettaja AMK', tkKoodi: '611201', uri: 'koulutus_1'},
            {nimi: 'Taideteollisuusopiston tutkinto', tkKoodi: '622951', uri: 'koulutus_2'},
            {nimi: 'Klarinettiopettaja AMK', tkKoodi: '611202', uri: 'koulutus_3'},
            {nimi: 'Huiluopettaja', tkKoodi: '611203', uri: 'koulutus_4'},
            {nimi: 'Pianonopettaja AMK', tkKoodi: '611204', uri: 'koulutus_5'},
            {nimi: 'Viuluopettaja', tkKoodi: '611205', uri: 'koulutus_6'}];

        $scope.stoModel = {koulutusala: 'Humanistinen ja kasvatusala',
            hakutulokset: [],
            active: {},
            hakulause: ''};


        $scope.toggleItem = function(hakutulos) {
            console.log(hakutulos.uri);
            $scope.stoModel.active = hakutulos;
        };

        $scope.isActive = function(hakutulos) {
            console.log(hakutulos.uri == $scope.stoModel.active.uri);
            return hakutulos.uri == $scope.stoModel.active.uri;
        };

        $scope.searchTutkinnot = function() {
            $scope.stoModel.hakutulokset = $scope.rawData.filter(function(element) {
                return element.nimi.toLowerCase().indexOf($scope.stoModel.hakulause.toLowerCase()) > -1;
            });
        };

        $scope.clearCriteria = function() {
            $scope.stoModel.hakulause = '';
        };

        $scope.ok = function() {
            console.log('Dialog ok pressed');
            $modalInstance.close($scope.stoModel.active);
        };

        $scope.cancel = function() {
            console.log('Dialog cancel pressed');
            $modalInstance.dismiss();
        };

    }])
        .controller('TutkintoOhjelmaSelectOpenerCtrl', ['$scope', '$modal', function($scope, $modal) {
        $scope.model = {};

        $scope.open = function() {

            var modalInstance = $modal.open({
                scope: $scope,
                templateUrl: 'partials/kk/edit/selectTutkintoOhjelma.html',
                controller: 'SelectTutkintoOhjelmaController'
            });

            modalInstance.result.then(function(selectedItem) {
                console.log('Ok, dialog closed: ' + selectedItem);
                if (selectedItem.uri != null) {
                    $scope.model.selected = selectedItem;
                } else {
                    $scope.model.selected = null;
                }
            }, function() {
                $scope.model.selected = null;
                console.log('Cancel, dialog closed');
            });

        };
    }]);
