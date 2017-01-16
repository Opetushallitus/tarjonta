var app = angular.module('app.edit.ctrl.kko', ['Koodisto', 'Yhteyshenkilo', 'ngResource', 'ngGrid', 'imageupload',
    'MultiSelect', 'OrderByNumFilter', 'localisation', 'MonikielinenTextField', 'ControlsLayout']);
app.controller('EditKorkeakouluOpintoController', function EditKorkeakouluOpintoController($route, $scope,
       Koodisto) {

    $scope.loadOpinnonTyypit = function(apiModel, uiModel) {
        Koodisto.getAllKoodisWithKoodiUri('opinnontyyppi', $scope.koodistoLocale, false).then(function(tyypit) {
            uiModel.opinnonTyypit = tyypit;
        });
    };
    $scope.loadKoulutuksenLaajuudet = function(apiModel, uiModel) {
        Koodisto.getSubKoodiValuesWithKoodiUri('koulutustyyppifasetti', 'et01.05', $scope.koodistoLocale, false).then(function(tyypit) {
            uiModel.koulutuksenLaajuudet = tyypit;
        });
    };

    /**
     * Save koulutus data to tarjonta-service database.
     * TODO: strict data validation, exception handling and optimistic locking
     */
    $scope.saveLuonnos = function() {
        $scope.saveByStatus('LUONNOS', $scope.koulutusForm, $scope.CONFIG.TYYPPI,
            $scope.customCallbackAfterSave);
    };
    $scope.saveValmis = function() {
        $scope.saveByStatus('VALMIS', $scope.koulutusForm, $scope.CONFIG.TYYPPI,
            $scope.customCallbackAfterSave);
    };

    $scope.customCallbackAfterSave = function(saveResponse) {
        if (saveResponse.status === 'OK') {
            $scope.$broadcast('onImageUpload', ''); //save images
            $scope.getLisatietoKielet($scope.model,  $scope.uiModel, true);
        }
    };

    $scope.init({
        childScope: $scope
    }, function() {
        $scope.loadOpinnonTyypit($scope.model, $scope.uiModel);
        $scope.loadKoulutuksenLaajuudet($scope.model, $scope.uiModel);

        // lisätietokielivalinnat
        $scope.getLisatietoKielet($scope.model, $scope.uiModel, true);
    });
});
