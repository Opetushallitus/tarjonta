var app = angular.module('app.kk.edit.hakukohde.review.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Hakukohde','auth','config','MonikielinenTextArea']);


app.controller('HakukohdeReviewController', function($scope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde,AuthService, HakuService, $modal ,Config,$location,$timeout,TarjontaService,HakukohdeKoulutukses,dialogService) {

      console.log('HAKUKOHDE REVIEW:  ', $scope.model.hakukohde);

      /*

        ---------> Internal variable declarations and scope "helper" variable declarations  <--------------

      */



      var kieliKoodistoUri = "kieli";

      $scope.model.hakukohteenKielet = [];

      $scope.model.koulutukses = [];

      $scope.model.hakukelpoisuusVaatimukses = [];

      //Try to get the user language and if for some reason it can't be retrieved, use FI as default
      $scope.model.userLang  =  AuthService.getLanguage();

      if ($scope.model.userLang === undefined) {
            $scope.model.userLang = "FI";
      }


      /*
        ---------> Helper functions  <----------
       */


     /*

      ----------> This functions loops through hakukohde names and lisätiedot to get hakukohdes languages

      */


      var loadKielesSetFromHakukohde = function() {

          var koodiPromises = [];

          if ($scope.model.allkieles!== undefined) {

          angular.forEach($scope.model.allkieles,function(hakukohdeKieli) {

               var koodi = Koodisto.getKoodi(kieliKoodistoUri,hakukohdeKieli,$scope.model.userLang);
               koodiPromises.push(koodi);
            });
          }

          angular.forEach(koodiPromises,function(koodiPromise){
              koodiPromise.then(function(koodi){
                  var hakukohteenKieli = {
                      kieliUri : koodi.koodiUri,
                      kieliNimi : koodi.koodiNimi
                  };
                  $scope.model.hakukohteenKielet.push(hakukohteenKieli);
              });
          });


      };

    /*

        ------------> Function to get koodisto koodis and call resultHandler to process those results

     */

    var getKoodisWithKoodisto = function(koodistoUri,resultHandlerFunction ) {


        var koodistoPromise = Koodisto.getAllKoodisWithKoodiUri(koodistoUri,$scope.model.userLang);

        koodistoPromise.then(function(koodis){
            resultHandlerFunction(koodis);
        });

    };

    /*

        --------> Function to get specific koodi information and call result handler to process that


     */
     var getKoodiWithUri = function(koodistoUri,koodiUri,resultHandlerFunction){



         var koodiPromise = Koodisto.getKoodi(koodistoUri,koodiUri,$scope.model.userLang);

         koodiPromise.then(function(koodi){
             resultHandlerFunction(koodi);
         });

     };

     /*

        ------------> This function retrieves haku and it's name so that it can be shown

      */

     var loadHakuInformation = function() {

         var hakuPromise = HakuService.getHakuWithOid($scope.model.hakukohde.hakuOid);
         hakuPromise.then(function(haku){
            angular.forEach(haku.nimi,function(nimi){

                if (nimi.arvo.toLowerCase() === $scope.model.userLang) {
                     $scope.model.hakukohde.hakuNimi = nimi.teksti;
                 }
            });
         });

     };

    /*

        ---------> This function retrieves hakukelpoisuusVaatimukses and adds results to model

     */

    var loadHakukelpoisuusVaatimukses = function() {

        angular.forEach($scope.model.hakukohde.hakukelpoisuusvaatimusUris,function(hakukelpoisuusVaatimusUri){

            getKoodiWithUri('pohjakoulutusvaatimuskorkeakoulut',hakukelpoisuusVaatimusUri,function(hakukelpoisuusVaatimusKoodi){
                   $scope.model.hakukelpoisuusVaatimukses.push(hakukelpoisuusVaatimusKoodi.koodiNimi);

               });

        });

    };

    var modelInit = function() {


        $scope.model.collapse = {
            perusTiedot: false ,
            valintakokeet : false,
            liitteet : false,
            valintaperusteet : false,
            sorakuvaukset : false,
            koulutukset : false
        };
    };

    /*

        ---------> Load koulutukses to show hakukohde related koulutukses

     */

    var loadKoulutukses = function() {

          if ($scope.model.hakukohde.hakukohdeKoulutusOids !== undefined) {

              var spec = {
                  koulutusOid : $scope.model.hakukohde.hakukohdeKoulutusOids
              };

              TarjontaService.haeKoulutukset(spec).then(function(data){

                  if(data.tulokset !== undefined) {
                      angular.forEach(data.tulokset,function(tulos){

                          if (tulos.tulokset !== undefined) {
                              angular.forEach(tulos.tulokset,function(lopullinenTulos){
                                  var koulutus = {
                                      nimi : lopullinenTulos.nimi,
                                      oid : lopullinenTulos.oid
                                  };
                                  $scope.model.koulutukses.push(koulutus);
                              });
                          }

                      });
                  }

              });

          }

    }

     /*

        -----------> Controller "initialization" part where initialization functions are run  <--------------

      */

    loadKielesSetFromHakukohde();
    loadHakuInformation();
    modelInit();
    loadHakukelpoisuusVaatimukses();
    loadKoulutukses();

    /*

        -----------> Controller event/click handlers etc. <------------------

     */

    $scope.doEdit = function(event, targetPart) {
        $log.info("doEdit()...", event, targetPart);
    };


    $scope.goBack = function(event) {

    };

    $scope.doDelete = function(event) {
        $log.info("doDelete()...");

    };

    $scope.getHakukelpoisuusVaatimusKuvaus = function(kieliUri) {
        return $scope.model.hakukohde.hakukelpoisuusVaatimusKuvaukset[kieliUri];
    };

        $scope.getLisatiedot = function(kieliUri) {
        return $scope.model.hakukohde.lisatiedot[kieliUri];
    };

    $scope.getLocalizedValintakoe = function(kieliUri) {

        var localizedValintakokeet = [];

        angular.forEach($scope.model.hakukohde.valintakokeet,function(valintakoe){

            if (valintakoe.kieliUri === kieliUri) {
                localizedValintakokeet.push(valintakoe);
            }

        });

        return localizedValintakokeet;

    };

    $scope.getLocalizedLiitteet = function(kieliUri) {

        var localizedLiitteet = [];

        angular.forEach($scope.model.hakukohde.hakukohteenLiitteet,function(liite){
            if (liite.kieliUri === kieliUri) {
                localizedLiitteet.push(liite);
            }
        });

        return localizedLiitteet;

    };

    var reallyRemoveKoulutusFromHakukohde = function(koulutus){

        var koulutuksesArray = [];

        koulutuksesArray.push(koulutus.oid);

        HakukohdeKoulutukses.removeKoulutuksesFromHakukohde($scope.model.hakukohde.oid,koulutuksesArray);

        if ($scope.model.koulutukses.length > 1) {

            angular.forEach($scope.model.koulutukses,function(koulutusIndex){
                if (koulutusIndex.oid === koulutus.oid) {
                    var index = $scope.model.koulutukses.indexOf(koulutusIndex);
                    $scope.model.koulutukses.splice(index,1);
                }
            });

        } else {

            $location.path("/etusivu");

        }

    };

    $scope.removeKoulutusFromHakukohde = function(koulutus){

        var texts = {
            title: LocalisationService.t("hakukohde.review.remove.koulutus.title"),
            description: LocalisationService.t("hakukohde.review.remove.koulutus.desc"),
            ok: LocalisationService.t("ok"),
            cancel: LocalisationService.t("cancel")
        };

        var d = dialogService.showDialog(texts);
        d.result.then(function(data){
            if ("ACTION" === data) {
                reallyRemoveKoulutusFromHakukohde(koulutus);
            }
        });

    };

    $scope.getLiitteenKuvaus = function(liite,kieliUri) {
        return liite.liitteenKuvaukset[kieliUri];
    };

    $scope.getValintaperusteKuvaus = function(kieliUri) {

        return $scope.model.hakukohde.valintaperusteKuvaukset[kieliUri];

    };

    $scope.getSoraKuvaus = function(kieliUri) {

        return $scope.model.hakukohde.soraKuvaukset[kieliUri];

    };

});