var app = angular.module('app.edit.ctrl.kk', [
    'Koodisto',
    'Yhteyshenkilo',
    'ngResource',
    'ngGrid',
    'imageupload',
    'MultiSelect',
    'OrderByNumFilter',
    'localisation',
    'MonikielinenTextField',
    'ControlsLayout'
]);
app.controller('EditKorkeakouluController', [
    '$route',
    '$timeout',
    '$scope',
    '$location',
    '$log',
    'TarjontaService',
    'Config',
    '$routeParams',
    'OrganisaatioService',
    'LocalisationService',
    '$window',
    'KoulutusConverterFactory',
    'Koodisto',
    '$modal',
    'PermissionService',
    'dialogService',
    'CommonUtilService', function EditKorkeakouluController($route, $timeout, $scope, $location, $log, TarjontaService, cfg, $routeParams, organisaatioService, LocalisationService, $window, converter, koodisto, $modal, PermissionService, dialogService, CommonUtilService) {
        $log = $log.getInstance('EditKorkeakouluController');
        $scope.tutkintoDialogModel = {};
        /**
                 * Save koulutus data to tarjonta-service database.
                 * TODO: strict data validation, exception handling and optimistic locking
                 */
        $scope.saveLuonnos = function() {
            $scope.saveByStatus('LUONNOS', $scope.koulutusForm, $scope.CONFIG.TYYPPI, $scope.customCallbackAfterSave);
        };
        $scope.saveValmis = function() {
            $scope.saveByStatus('VALMIS', $scope.koulutusForm, $scope.CONFIG.TYYPPI, $scope.customCallbackAfterSave);
        };
        $scope.customCallbackAfterSave = function(saveResponse) {
            if (saveResponse.status === 'OK') {
                $scope.$broadcast('onImageUpload', '');
                //save images
                $scope.getLisatietoKielet($scope.model, $scope.uiModel, true);
            }
        };
        $scope.tutkintoDialogModel.open = function() {
            var modalInstance = $modal.open({
                scope: $scope,
                templateUrl: 'partials/koulutus/edit/korkeakoulu/selectTutkintoOhjelma.html',
                controller: 'SelectTutkintoOhjelmaController'
            });
            modalInstance.result.then(function(selectedItem) {
                $log.debug('Ok, dialog closed: ' + selectedItem.koodiNimi);
                $log.debug('Koodiarvo is: ' + selectedItem.koodiArvo);
                if (!converter.isNull(selectedItem)) {
                    //$scope.model.koulutuskoodi = selectedItem;
                    $scope.model.koulutuskoodi.koodi.arvo = selectedItem.koodiArvo;
                }
            }, function() {
                    $log.debug('Cancel, dialog closed');
                });
        };
        $scope.removeKandidaatinKoulutuskoodi = function(koodi) {
            $scope.model.kandidaatinKoulutuskoodi = {};
        };
        $scope.createSelectKoulutuskoodiModalDialog = function(koodi) {
            var modalInstance = $modal.open({
                templateUrl: 'partials/koulutus/edit/korkeakoulu/selectTutkintoOhjelma.html',
                controller: 'SelectTutkintoOhjelmaController',
                resolve: {
                    targetFilters: function() {
                        return [cfg.app['koodisto-uri.tutkintotyyppi.alempiKorkeakoulututkinto']];
                    }
                }
            });
            modalInstance.result.then(function(result) {
                /* close */
                $scope.model.kandidaatinKoulutuskoodi = {
                    arvo: result.koodiArvo,
                    uri: result.koodiUri,
                    versio: result.koodiVersio,
                    nimi: result.koodiNimi
                };
            }, function() {});
        };
        /*
                 * WATCHES
                 */
        $scope.onMaksullisuusChanged = function() {
            if (!$scope.model.hinta) {
                return;
            }
            var p = $scope.model.hinta.indexOf(',');
            while (p != -1) {
                $scope.model.hinta = $scope.model.hinta.substring(0, p) + '.' + $scope.model.hinta.substring(p + 1);
                p = $scope.model.hinta.indexOf(',', p);
            }
        };
        $scope.$watch('model.opintojenMaksullisuus', function(valNew, valOld) {
            if (!valNew && valOld) {
                //clear price data field
                $scope.model.hinta = '';
            }
        });
        $scope.isKandiUri = function() {
            var kandiObj = $scope.model.kandidaatinKoulutuskoodi;
            return angular.isDefined(kandiObj) && kandiObj !== null && angular.isDefined(kandiObj.uri) && kandiObj.uri.length > 0;
        };
        $scope.init({
            childScope: $scope
        });
    }
]);