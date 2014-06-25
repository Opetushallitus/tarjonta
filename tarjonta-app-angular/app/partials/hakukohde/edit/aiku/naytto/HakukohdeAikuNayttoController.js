/**
 * Created by tuomas on 17.6.2014.
 */

var app = angular.module('app.kk.edit.hakukohde.ctrl');
app.controller('HakukohdeAikuNayttoEditController',
    function($scope,
             $q,
             $log,
             LocalisationService,
             OrganisaatioService,
             Koodisto,
             Hakukohde,
             AuthService,
             HakuService,
             $route ,
             $modal ,
             Config,
             $location,
             $timeout,
             TarjontaService,
             Kuvaus,
             CommonUtilService,
             PermissionService) {


        $log = $log.getInstance("HakukohdeAikuNayttoEditController");


        $scope.ui = {

            showPlaces : true

        };

        $scope.osaamisalat = ['osaamisala_1625','osaamisala_1626'];


        //TODO: Add naytto specific haku filtering logic
        var filterHakus = function(hakus) {
            var filteredHakus = $scope.filterHakusWithOrgs($scope.filterHakuWithKohdejoukko( $scope.filterPoistettuHaku(hakus), 'haku.kohdejoukko.aiku.uri'));

            return filteredHakus;
        };

        /**

         Controller initialization function which is called when controller loads

         */
        var init = function() {

            $scope.model.userLang  =  AuthService.getLanguage();

            if ($scope.model.userLang === undefined) {
                $scope.model.userLang = "FI";
            }
            $scope.loadKoulutukses(filterHakus);
            $scope.haeTarjoajaOppilaitosTyypit();
            $scope.model.continueToReviewEnabled = $scope.checkJatkaBtn($scope.model.hakukohde);
            $scope.checkIsCopy();

            /*$scope.updateTilaModel($scope.model.hakukohde);*/

            if ($scope.model.hakukohde.hakukelpoisuusVaatimusKuvaukset === undefined) {
                $scope.model.hakukohde.hakukelpoisuusVaatimusKuvaukset = {};
            }

            if ($scope.model.hakukohde.kaytetaanJarjestelmanValintaPalvelua === undefined) {
                $scope.model.hakukohde.kaytetaanJarjestelmanValintaPalvelua = true;
            }

            $scope.enableOrDisableTabs();

        };

        init();

        $scope.model.checkSelectedHaku = function() {



            var jatkuvaHakuKoodi = "hakutapa_03";
            angular.forEach($scope.model.hakus, function (haku) {

                if (haku.oid === $scope.model.hakukohde.hakuOid) {
                    if ($scope.aContainsB(haku.hakutapaUri,jatkuvaHakuKoodi)) {
                        $scope.ui.showPlaces = false;
                    } else {
                        $scope.ui.showPlaces = true;
                    }
                }


            });



        };

    });