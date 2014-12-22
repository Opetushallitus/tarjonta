'use strict';
/* Controllers */
var app = angular.module('app.hakukohde.dialog.ctrl', []);
app.controller('ValitseKoulutusDialogCtrl', [
    '$modalInstance',
    'LocalisationService',
    '$q',
    '$scope',
    'input',
    'HakukohdeKoulutukses', function($modalInstance, LocalisationService, $q, $scope, input, HakukohdeKoulutukses) {
        $scope.model = {
            errors: [],
            komotos: [],
            selectedOids: {},
            lang: input.locale
        };
        if (input.response.result.oidConflictingWithOids) {
            var obj = {};
            angular.forEach(input.response.result.oidConflictingWithOids, function(val, key) {
                obj[key] = true;
            });
            $scope.model.selectedOids = obj;
        }
        function reload() {
            $scope.model.komotos = input.response.result.names;
            var arrErrors = [];
            if (input.response.errors && input.response.errors.length > 0) {
                for (var i = 0; i < input.response.errors.length; i++) {
                    arrErrors.push({
                        msg: LocalisationService.t(input.response.errors[i].errorMessageKey, [])
                    });
                }
                $scope.model.errors = arrErrors;
            }
        }
        $scope.getNameByLang = function(obj) {
            if (!obj && obj.nimi) {
                return '';
            }
            if (obj.nimi[$scope.model.lang]) {
                return obj.nimi[$scope.model.lang];
            }
            else if (_.keys(obj.nimi)) {
                var keys = _.keys(obj.nimi);
                for (var i = 0; keys.length; i++) {
                    if (obj.nimi[keys[i]]) {
                        return obj.nimi[keys[i]];
                    }
                }
            }
            else {
                return obj.oid;
            }
        };
        /**
             * Peruuta nappulaa klikattu, sulje dialogi
             */
        $scope.peruuta = function() {
            $modalInstance.dismiss();
        };
        $scope.getSelectedOids = function() {
            var arr = [];
            if ($scope.model.selectedOids) {
                angular.forEach($scope.model.selectedOids, function(val, key) {
                    if (val) {
                        arr.push(key);
                    }
                });
            }
            return arr;
        };
        $scope.isSelected = function() {
            return $scope.model.selectedOids && $scope.getSelectedOids().length > 0;
        };
        $scope.emptyResult = function() {
            return $scope.model.komotos.length === 0;
        };
        $scope.jatka = function() {
            var komotoOids = $scope.getSelectedOids();
            if (komotoOids && komotoOids.length > 0) {
                HakukohdeKoulutukses.geValidateHakukohdeKomotos(komotoOids).then(function(response) {
                    if (response.status === 'OK') {
                        $modalInstance.close({
                            toteutustyyppi: response.result.toteutustyyppis[0],
                            oids: komotoOids
                        });
                    }
                    else {
                        $scope.model.komotos = response.result.names;
                    }
                });
            }
        };
        $scope.toggleSelection = function(oid) {
            if ($scope.model.selectedOids[oid]) {
                $scope.model.selectedOids[oid] = false;
            }
            else {
                $scope.model.selectedOids[oid] = true;
            }
        };
        reload();
    }
]);