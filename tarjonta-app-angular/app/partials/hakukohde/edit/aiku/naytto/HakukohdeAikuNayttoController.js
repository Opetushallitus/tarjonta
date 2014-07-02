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
             PermissionService,
             SharedStateService) {

        $log = $log.getInstance("HakukohdeAikuNayttoEditController");

        $scope.ui = {
            showPlaces : true
        };

        console.log("setting canSave");
        $scope.model.canSaveAsLuonnos=CommonUtilService.canSaveAsLuonnos;
      
        
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

          if (errors.length < 1 ) {
              return true;
          } else {
              $scope.showError(errors);

              return false;
          }

      };
        
      $scope.saveAsLuonnos = function() {
          $scope.model.saveLuonnosParent(validateAikuHakukohde);
      };

      $scope.saveAsValmis = function () {
          $scope.model.saveValmisParent(validateAikuHakukohde);
      };

      
        
        var readOsaamisAlat = function () {

           var koulutukses =  SharedStateService.getFromState('SelectedKoulutukses');

           //koulutusohjelmasta tai koulutuskoodista populoidaan tämä
           $scope.osaamisalat = [];//'osaamisala_1625','osaamisala_1626'];

           var koulutusPromiset = [];
           for(var i=0;i<koulutukses.length;i++){
             var promise = TarjontaService.getKoulutus({oid:koulutukses[i]}).$promise;
             koulutusPromiset.push(promise);
           }


           $q.all(koulutusPromiset).then(function(results){
             for(var j=0;j<results.length;j++) {
               var tmp=[];
               var koulutus = results[j].result; 
               var arvo = koulutus.koulutuskoodi?koulutus.koulutuskoodi.uri:koulutus.koulutusohjelma;
               tmp.push(arvo);
               return tmp;
             }
           }).then(function(array){
             $scope.osaamisalat =array;
           });


        };
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

            $scope.model.hakukohde.toteutusTyyppi=
            
            $scope.enableOrDisableTabs();
            readOsaamisAlat();
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