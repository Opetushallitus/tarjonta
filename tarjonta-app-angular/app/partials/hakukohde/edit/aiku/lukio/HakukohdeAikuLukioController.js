var app = angular.module('app.kk.edit.hakukohde.ctrl');
app.controller('HakukohdeAikuLukioEditController', function($scope, $q, $log, LocalisationService, OrganisaatioService,
                    Koodisto, Hakukohde, AuthService, HakuService, $route, $modal, Config, $location, $timeout,
                    TarjontaService, Kuvaus, CommonUtilService) {
    var validateAikuHakukohde = function() {
        var errors = [];
        if (!$scope.model.hakukohde.hakukohteenNimiUri
            || $scope.model.hakukohde.hakukohteenNimiUri.trim().length < 1) {
            $scope.model.nimiValidationFailed = true;
            errors.push({
                errorMessageKey: 'hakukohde.edit.nimi.missing'
            });
        }
        if (!$scope.model.hakukohde.hakuOid || $scope.model.hakukohde.hakuOid.trim().length < 1) {
            errors.push({
                errorMessageKey: 'hakukohde.edit.haku.missing'
            });
        }
        if (!$scope.status.validateValintakokeet()) {
            errors.push({
                errorMessageKey: 'hakukohde.edit.valintakokeet.errors'
            });
        }
        $scope.validateIsHakuEisahkoistaHakuaRadioButtonSelected(errors);
        if (errors.length < 1) {
            return true;
        }
        else {
            $scope.showError(errors);
            return false;
        }
    };
    var filterHakus = function(hakus) {
        var targetUri = 'haku.kohdejoukko.aiku.uri';

        if ($scope.model.hakukohde.toteutusTyyppi === 'AIKUISTEN_PERUSOPETUS') {
            targetUri = 'haku.kohdejoukko.aikuistenPerusopetus.uri';
        }

        return $scope.filterHakusWithOrgs(
            $scope.filterHakuWithKohdejoukko(hakus, targetUri)
        );
    };
    /**
    *
    * Controller initialization function which is called when controller
    * loads
    *
    */
    var init = function() {
        $scope.model.userLang = AuthService.getLanguage();
        if ($scope.model.userLang === undefined) {
            $scope.model.userLang = 'FI';
        }
        $scope.loadKoulutukses(filterHakus);
        $scope.haeTarjoajaOppilaitosTyypit();
        $scope.model.continueToReviewEnabled = $scope.checkJatkaBtn($scope.model.hakukohde);
        $scope.checkIsCopy();
        /* $scope.updateTilaModel($scope.model.hakukohde); */
        if ($scope.model.hakukohde.hakukelpoisuusVaatimusKuvaukset === undefined) {
            $scope.model.hakukohde.hakukelpoisuusVaatimusKuvaukset = {};
        }
        if ($scope.model.hakukohde.kaytetaanJarjestelmanValintaPalvelua === undefined) {
            $scope.model.hakukohde.kaytetaanJarjestelmanValintaPalvelua = true;
        }
        $scope.enableOrDisableTabs();
    };
    init();
    $scope.model.canSaveAsLuonnos = function() {
        return CommonUtilService.canSaveAsLuonnos($scope.model.hakukohde.tila);
    };
    $scope.saveAikuLukioAsLuonnos = function() {
        $scope.model.saveParent('LUONNOS', validateAikuHakukohde);
    };
    $scope.saveAikuLukioAsValmis = function() {
        $scope.model.saveParent('VALMIS', validateAikuHakukohde);
    };
    $scope.model.hakuChanged = function() {
        if ($scope.model.hakukohde.hakuOid !== undefined) {
            $scope.model.hakuaikas.splice(0, $scope.model.hakuaikas.length);
            var haku = $scope.getHakuWithOid($scope.model.hakukohde.hakuOid);
            if (haku.hakuaikas.length > 1) {
                angular.forEach(haku.hakuaikas, function(hakuaika) {
                    var formattedStartDate = $scope.createFormattedDateString(hakuaika.alkuPvm);
                    var formattedEndDate = $scope.createFormattedDateString(hakuaika.loppuPvm);
                    hakuaika.formattedNimi = resolveLocalizedValue(hakuaika.nimet)
                                                + ', ' + formattedStartDate + ' - ' + formattedEndDate;
                    $scope.model.hakuaikas.push(hakuaika);
                });
                $scope.model.showHakuaikas = true;
            }
            else {
                var hakuaika = _.first(haku.hakuaikas);
                $scope.model.hakuaikas.push(hakuaika);
                $scope.model.hakukohde.hakuaikaId = hakuaika.hakuaikaId;
                $scope.model.showHakuaikas = false;
            }
        }
    };
    var resolveLocalizedValue = function(key) {
        var userKieliUri = LocalisationService.getKieliUri();
        return key[userKieliUri] || key.kieli_fi || key.kieli_sv || key.kieli_en || '[Ei nime\xE4]';
    };
});