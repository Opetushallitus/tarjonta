var app = angular.module('app.edit.ctrl.kko', ['Koodisto', 'Yhteyshenkilo', 'ngResource', 'ngGrid', 'imageupload',
    'MultiSelect', 'OrderByNumFilter', 'localisation', 'MonikielinenTextField', 'ControlsLayout']);
app.controller('EditKorkeakouluOpintoController', function EditKorkeakouluOpintoController($route, $scope,
       Koodisto) {

    // Get organisation groups, see "tarjontaApp.js" routing resolve for hakukohde/id/edit
    var koulutusGroups = $route.current.locals.koulutusGroups || [];

    $scope.loadOpinnonTyypit = function(apiModel, uiModel) {
        Koodisto.getAllKoodisWithKoodiUri('opinnontyyppi', $scope.koodistoLocale, false).then(function(tyypit) {
            uiModel.opinnonTyypit = tyypit;
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

    $scope.init({
        childScope: $scope
    }, function() {
        $scope.loadOpinnonTyypit($scope.model, $scope.uiModel);

        // lisätietokielivalinnat
        $scope.getLisatietoKielet($scope.model, $scope.uiModel, true);

        // Mahdolliset Organisaatiopalvelun koulutusryhmät, joissa koulutus voi olla, routerissa resolvattuna
        // [{ key: XXX, value: YYY}, ...]
        $scope.uiModel.koulutusRyhmat = koulutusGroups;

        // Alusta ryhmälista tyhjäksi jos ei valintoja
        if (!$scope.model.koulutusRyhmaOids) {
            $scope.model.koulutusRyhmaOids = [];
        }
    });
});
