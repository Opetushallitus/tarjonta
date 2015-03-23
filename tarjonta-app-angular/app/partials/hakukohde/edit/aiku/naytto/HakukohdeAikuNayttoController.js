/**
 * Created by tuomas on 17.6.2014.
 */
var app = angular.module('app.kk.edit.hakukohde.ctrl');
app.controller('HakukohdeAikuNayttoEditController', function($scope, $q, $log, LocalisationService,
        OrganisaatioService, Koodisto, Hakukohde, AuthService, HakuService, $route, $modal, Config,
        $location, $timeout, TarjontaService, Kuvaus, CommonUtilService, PermissionService,
        SharedStateService) {
    // koulutusohjelmasta tai koulutuskoodista populoidaan tämä
    $scope.osaamisalat = [];
    $scope.model.canSaveAsLuonnos = function() {
        return CommonUtilService.canSaveAsLuonnos($scope.model.hakukohde.tila);
    };
    $scope.saveAsLuonnos = function() {
        if (CommonUtilService.canSaveAsLuonnos($scope.model.hakukohde.tila)) {
            $scope.model.saveParent('LUONNOS');
        }
    };
    $scope.saveAsValmis = function() {
        $scope.model.saveParent('VALMIS');
    };
    var readOsaamisAlat = function() {
        var koulutukses = [];
        if ($scope.model.hakukohde.oid) {
            koulutukses = $scope.model.hakukohde.hakukohdeKoulutusOids;
        }
        else {
            koulutukses = SharedStateService.getFromState('SelectedKoulutukses');
        }
        var koulutusPromiset = [];
        for (var i = 0; i < koulutukses.length; i++) {
            var promise = TarjontaService.getKoulutus({
                oid: koulutukses[i]
            }).$promise;
            koulutusPromiset.push(promise);
        }
        $q.all(koulutusPromiset).then(function(results) {
            // koulutuskoodit/koulutusohjelmat
            var context = {
                arvot: [],
                koulutukset: []
            };
            var map = {};
            for (var j = 0; j < results.length; j++) {
                map[results[j].result.koulutuskoodi.uri] = {};
                map[results[j].result.koulutusohjelma.uri] = {};
            }
            context.arvot = _.keys(map);
            return context;
        }).then(function(context) {
            $scope.osaamisalat = context.arvot;
        });
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
        $scope.loadKoulutukses();
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
        readOsaamisAlat();
    };
    init();
});