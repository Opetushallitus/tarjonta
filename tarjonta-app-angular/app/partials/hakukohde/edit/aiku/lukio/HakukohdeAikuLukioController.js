
var app = angular.module('app.kk.edit.hakukohde.ctrl');
app.controller('HakukohdeAikuLukioEditController',
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



        $log = $log.getInstance("HakukohdeAikuLukioEditController");




        var filterHakuWithKohdejoukko = function(hakus) {
            console.log('FILTERING HAKUS : ', hakus);
            var filteredHakus = [];
            angular.forEach(hakus,function(haku){
                // rajaus kk-hakukohteisiin; ks. OVT-6452
                // TODO selvitä uri valitun koulutuksen perusteella

                var kohdeJoukkoUriNoVersion = $scope.splitUri(haku.kohdejoukkoUri);

                if (kohdeJoukkoUriNoVersion==window.CONFIG.app['haku.kohdejoukko.aiku.uri']) {

                        filteredHakus.push(haku);



                }
            });

            console.log('FILTERED HAKUS : ', filteredHakus);
            return filteredHakus;

        };

        var filterHakuWithHakutapa = function(hakus) {

            var filteredHakus = [];

            angular.forEach(hakus,function(haku) {

                 if (haku.hakutapaUri.indexOf(window.CONFIG.app['haku.hakutapa.erillishaku.uri']) != -1) {

                     if (haku.koulutuksenAlkamiskausiUri === $scope.koulutusKausiUri && haku.koulutuksenAlkamisVuosi === $scope.model.koulutusVuosi) {
                         filteredHakus.push(haku);
                     }

                 } else {
                     filteredHakus.push(haku);
                 }

            });

            return filteredHakus;

        };

        var validateAikuHakukohde = function() {

            var errors = [];

            console.log('AIKU HAKUKOHDE : ' , $scope.model.hakukohde);
            if (!$scope.model.hakukohde.hakukohteenNimiUri || $scope.model.hakukohde.hakukohteenNimiUri.trim().length <1 ) {
                var err = {};
                err.errorMessageKey = 'hakukohde.edit.nimi.missing';
                $scope.model.nimiValidationFailed = true;
                errors.push(err);
            }

            if (!$scope.model.hakukohde.hakuOid || $scope.model.hakukohde.hakuOid.trim().length < 1) {
                var err = {};
                err.errorMessageKey = 'hakukohde.edit.haku.missing';

                errors.push(err);
            }

            if(!$scope.model.hakukohde.aloituspaikatLkm || $scope.model.hakukohde.aloituspaikatLkm < 1) {
                var err = {};
                err.errorMessageKey = 'hakukohde.edit.aloituspaikat.missing';

                errors.push(err);
            }

            if (errors.length < 1 ) {
                return true;
            } else {
                $scope.showError(errors);

                return false;
            }

        };

        var filterHakus = function(hakus) {
            return  $scope.filterHakusWithOrgs(filterHakuWithKohdejoukko(hakus));

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
            $scope.updateTilaModel($scope.model.hakukohde);

            if ($scope.model.hakukohde.hakukelpoisuusVaatimusKuvaukset === undefined) {
                $scope.model.hakukohde.hakukelpoisuusVaatimusKuvaukset = {};
            }

            if ($scope.model.hakukohde.kaytetaanJarjestelmanValintaPalvelua === undefined) {
                $scope.model.hakukohde.kaytetaanJarjestelmanValintaPalvelua = true;
            }

            $scope.enableOrDisableTabs();

        };


        init();


        $scope.saveAikuLukioAsLuonnos = function() {


            $scope.model.saveLuonnosParent(validateAikuHakukohde);

        };

        $scope.saveAikuLukioAsValmis = function () {

            $scope.model.saveValmisParent(validateAikuHakukohde);

        };

    });