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
app.controller('EditKorkeakouluController', function EditKorkeakouluController($scope, Config,
            KoulutusConverterFactory, $modal) {

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
            if (!KoulutusConverterFactory.isNull(selectedItem)) {
                //$scope.model.koulutuskoodi = selectedItem;
                $scope.model.koulutuskoodi.koodi.arvo = selectedItem.koodiArvo;
            }
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
                    return [Config.app['koodisto-uri.tutkintotyyppi.alempiKorkeakoulututkinto']];
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
    $scope.isKandiUri = function() {
        var kandiObj = $scope.model.kandidaatinKoulutuskoodi;
        return angular.isDefined(kandiObj) && kandiObj !== null && angular.isDefined(kandiObj.uri) &&
            kandiObj.uri.length > 0;
    };
    $scope.init({
        childScope: $scope
    });
});