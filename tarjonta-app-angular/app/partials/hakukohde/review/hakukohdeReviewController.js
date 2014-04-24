var app = angular.module('app.kk.edit.hakukohde.review.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Hakukohde','auth','config','MonikielinenTextArea','MonikielinenText']);


app.controller('HakukohdeReviewController', 
    function($scope, 
             $q, 
             $log,
             LocalisationService, 
             OrganisaatioService, 
             Koodisto, 
             Hakukohde, 
             AuthService, 
             dialogService, 
             HakuService, 
             $modal, 
             Config, 
             $location, 
             $timeout, 
             TarjontaService, 
             HakukohdeKoulutukses, 
             dialogService, 
             SisaltyvyysUtil, 
             TreeHandlers, 
             PermissionService) {
                 
      $log = $log.getInstance("HakukohdeReviewController");
      $log.debug("init...");
       
      //edit buttons are active when mutable
      $scope.isMutable=false;
      
      $log.debug("scope.model:", $scope.model)
      
      //käyttöoikeudet
      PermissionService.hakukohde.canEdit($scope.model.hakukohde.oid).then(function(data){
        $scope.isMutable=data;
         if ($scope.model.hakukohde.koulutusAsteTyyppi === 'LUKIOKOULUTUS') {
                //TODO: poista tama kun nuorten lukiokoulutus on toteutettu!
                $scope.isMutable = false;
                
            }
      });
  
      $log.debug('HAKUKOHDE REVIEW:  ', $scope.model.hakukohde);

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

      // form controls
      $scope.formControls = {};

      $scope.model.validationmsgs = [];

      $scope.model.showError = false;

      $scope.model.organisaatioNimet = [];


      var orgSet = new buckets.Set();

      $scope.goBack = function(event) {
          window.history.back();
      };


     $scope.getHakukohteenJaOrganisaationNimi = function() {


         console.log('ORGANISAATION NIMET : ', $scope.model.organisaatioNimet);

         var ret = "";
         var ja = LocalisationService.t("tarjonta.yleiset.ja");

         for (var i in $scope.model.hakukohde.hakukohteenNimet) {
             if (i>0) {
                 ret = ret + ((i==$scope.model.hakukohde.hakukohteenNimet.length-1) ? " "+ja+" " : ", ");
             }
             ret = ret + "<b>" + $scope.model.hakukohde.hakukohteenNimet[i] + "</b>";
         }

         if ($scope.model.organisaatioNimet.length < 2 && $scope.model.organisaatioNimet.length > 0)  {

             var organisaatiolleMsg = LocalisationService.t("tarjonta.hakukohde.title.org");

             ret  = ret + ". " + organisaatiolleMsg + " : <b>" + $scope.model.organisaatioNimet[0] + " </b>";

         } else {
             var counter = 0;
             var organisaatioilleMsg = LocalisationService.t("tarjonta.hakukohde.title.orgs");
             angular.forEach($scope.model.organisaatioNimet,function(organisaatioNimi) {


                 if (counter === 0) {


                     ret  = ret + ". " + organisaatioilleMsg + " : <b>" + organisaatioNimi + " </b>";


                 } else {


//                    ret = ret + ((counter===$scope.model.organisaatioNimet.length-1) ? " " : ", ");

                     ret = ret + ", <b>" + organisaatioNimi + "</b>";

                 }
                 counter++;

             });

         }

         return ret;

     };
      
      $scope.getHakukohteenNimi = function() {


          if ($scope.model==undefined || $scope.model.hakukohde==undefined || $scope.model.hakukohde.hakukohteenNimet==undefined) {
              return null;
          }
          var lc = $scope.model.hakukohde.hakukohteenNimet[kieliKoodistoUri+"_"+$scope.model.userLang.toLowerCase()];
          if (lc) {
              return lc;
          }

          for (var i in $scope.model.hakukohde.hakukohteenNimet) {
              return $scope.model.hakukohde.hakukohteenNimet[i];
          }
          return null;

      }
      
      // liitteiden / valintakokeiden kielet
      function aggregateLangs(items) {
    	  var ret = [];
    	  for (var i in items) {
    		  var kieli = items[i].kieliUri;
    		  if (ret.indexOf(kieli)==-1) {
    			  ret.push(kieli);
    		  }
    	  }
    	  return ret;
      }
      
      $scope.model.valintakoeKielet = aggregateLangs($scope.model.hakukohde.valintakokeet);
      $scope.model.liiteKielet = aggregateLangs($scope.model.hakukohde.hakukohteenLiitteet);
      
      $log.debug('HAKUKOHDE : ' , $scope.model.hakukohde);

      /*
        ----------------------------> Helper functions  < ----------------------------
       */
     /*

      ----------> This functions loops through hakukohde names and lisätiedot to get hakukohdes languages

      */


     var convertValintaPalveluValue = function() {


        if ( $scope.model.hakukohde.kaytetaanJarjestelmanValintaPalvelua ) {
            $scope.model.kaytetaanJarjestelmanValintaPalveluaArvo = LocalisationService.t('hakukohde.review.perustiedot.jarjestelmanvalinta.palvelu.kylla');
        } else {
            $scope.model.kaytetaanJarjestelmanValintaPalveluaArvo = LocalisationService.t('hakukohde.review.perustiedot.jarjestelmanvalinta.palvelu.ei');
        }

     }


      var loadKielesSetFromHakukohde = function() {

          var koodiPromises = [];



          if ($scope.model.allkieles === undefined || $scope.model.allkieles.length < 1) {
              var allKieles = new buckets.Set();

              for (var kieliUri in $scope.model.hakukohde.hakukohteenNimet) {

                  allKieles.add(kieliUri);
              }

              for (var kieliUri in $scope.model.hakukohde.lisatiedot) {
                  allKieles.add(kieliUri);
              }

              angular.forEach($scope.model.hakukohde.valintakokeet,function(valintakoe) {

                  allKieles.add(valintakoe.kieliUri);

              });

              angular.forEach($scope.model.hakukohde.hakukohteenLiitteet,function(liite){

                  allKieles.add(liite.kieliUri);

              });

              $scope.model.allkieles = allKieles.toArray();
          }

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


    var checkIsOkToRemoveKoulutus = function() {
        if ($scope.model.koulutukses.length > 1) {

            return true;
        }  else {
            return false;
        }

    }


    var createFormattedDateString = function(date) {

        return moment(date).format('DD.MM.YYYY HH:mm');

    }

    var filterNewKoulutukses = function(koulutukses) {

        var newKoulutukses = [];

        angular.forEach(koulutukses,function(koulutusOid){
            var wasFound = false;


            angular.forEach($scope.model.koulutukses,function(koulutus){
                if (koulutus.oid === koulutusOid) {
                    wasFound = true;
                }
            });

            if (!wasFound) {
                newKoulutukses.push(koulutusOid);
            }
        });
       return newKoulutukses;
    };

    var filterRemovedKoulutusFromHakukohde = function(koulutusOid) {

      angular.forEach($scope.model.hakukohde.hakukohdeKoulutusOids,function(hakukohdeKoulutusOid){

          if (hakukohdeKoulutusOid === koulutusOid) {
              var index = $scope.model.hakukohde.hakukohdeKoulutusOids.indexOf(hakukohdeKoulutusOid);
              $scope.model.hakukohde.hakukohdeKoulutusOids.splice(index,1);
          }

      });

    };

    var filterKoulutuksesToBeRemoved = function(newKoulutusOidArray) {

        var koulutuksesToRemove = [];

        angular.forEach($scope.model.koulutukses,function(koulutus){
             var koulutusFound = false;
            angular.forEach(newKoulutusOidArray,function(newKoulutusOid){
                if (koulutus.oid === newKoulutusOid) {
                    koulutusFound = true;
                }
            });

            if (!koulutusFound) {
                koulutuksesToRemove.push(koulutus.oid);
            }
        });


        return koulutuksesToRemove;

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
            $log.debug('HAKU: ', haku);
            for (var kieliUri in haku.nimi) {
                var upperCaseKieliUri = kieliUri.toUpperCase();
                var upperUserLang = $scope.model.userLang.toUpperCase();
                if (upperCaseKieliUri.indexOf(upperUserLang) != -1) {
                    $scope.model.hakuNimi = haku.nimi[kieliUri];
                }
            }

            if (haku.hakuaikas !== undefined && haku.hakuaikas.length > 0 && $scope.model.hakukohde.hakuaikaId !== undefined) {

                var valittuHakuAika = undefined;
                angular.forEach(haku.hakuaikas,function(hakuaika){

                      if (hakuaika.hakuaikaId === $scope.model.hakukohde.hakuaikaId) {
                          valittuHakuAika = hakuaika;
                      }

                });

                if (valittuHakuAika !== undefined) {

                    var prefix = valittuHakuAika.nimi !== undefined ? valittuHakuAika.nimi + " : " : "";



                    $scope.model.hakuNimi = $scope.model.hakuNimi + "  ( " + prefix  + createFormattedDateString(valittuHakuAika.alkuPvm) + " - " + createFormattedDateString(valittuHakuAika.loppuPvm) + " ) ";

                }

            }

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
            valintakokeet : true,
            liitteet : true,
            valintaperusteet : true,
            sorakuvaukset : true,
            koulutukset : false,
            model : true
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

                  var tarjoajaOidsSet = new buckets.Set();

                  if(data.tulokset !== undefined) {
                      $scope.model.koulutukses.splice(0,$scope.model.koulutukses.length);
                      angular.forEach(data.tulokset,function(tulos){
                          tarjoajaOidsSet.add(tulos.oid);
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

                      $scope.model.hakukohde.tarjoajaOids = tarjoajaOidsSet.toArray();


                      var orgQueryPromises = [];

                      angular.forEach($scope.model.hakukohde.tarjoajaOids,function(tarjoajaOid){

                          orgQueryPromises.push(OrganisaatioService.byOid(tarjoajaOid));

                      });




                      $q.all(orgQueryPromises).then(function(orgs){

                          angular.forEach(orgs,function(data){

                              orgSet.add(data.nimi);

                          });


                          $scope.model.organisaatioNimet = orgSet.toArray();

                      });


                  }

              });

          }

    }

     /*

        -----------> Controller "initialization" part where initialization functions are run  <--------------

      */
    convertValintaPalveluValue();
    loadKielesSetFromHakukohde();
    loadHakuInformation();
    modelInit();
    loadHakukelpoisuusVaatimukses();
    loadKoulutukses();

    /*

        -----------> Controller event/click handlers etc. <------------------

     */

    $scope.doEdit = function(event, targetPart) {
        $log.debug("doEdit()...", event, targetPart);
        var navigationUri = "/hakukohde/"+$scope.model.hakukohde.oid+"/edit";
        $location.path(navigationUri);
    };


    $scope.goBack = function(event) {
//        window.history.back();
        $location.path('/etusivu');
    };

    $scope.doCopy = function(event) {

        $location.path('/hakukohde/'+$scope.model.hakukohde.oid+'/edit/copy');

    }

    $scope.doDelete = function() {

        var texts = {
            title: LocalisationService.t("hakukohde.review.remove.title"),
            description: LocalisationService.t("hakukohde.review.remove.desc"),
            ok: LocalisationService.t("ok"),
            cancel: LocalisationService.t("cancel")
        };

        var d = dialogService.showDialog(texts);
        d.result.then(function(data){
            if (data) {

                var hakukohdeResource = new Hakukohde($scope.model.hakukohde);
                var resultPromise =  hakukohdeResource.$delete();
                resultPromise.then(function(result){

                    $log.debug('GOT RESULT : ' , result);

                    if (result.status === "OK") {

                        //TKatva, 18.3.2014. Commented confirmation dialog away, if needed return it.
/*
                        var confTexts = {
                            title: LocalisationService.t("hakukohde.review.remove.confirmation.title"),
                            description: LocalisationService.t("hakukohde.review.remove.confirmation.desc"),
                            ok: LocalisationService.t("ok")};

                        var dd = dialogService.showDialog(confTexts);

                        dd.result.then(function(daatta){
                            $location.path('/etusivu');
                        });*/
                        $location.path('/etusivu');




                    }  else {
                        //TODO: Show some error message
                    }

                });

            }
        });

    }

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

    var removeKoulutusRelationsFromHakukohde = function(koulutuksesArray) {

        HakukohdeKoulutukses.removeKoulutuksesFromHakukohde($scope.model.hakukohde.oid,koulutuksesArray);

        if ($scope.model.koulutukses.length > 0) {

            angular.forEach($scope.model.koulutukses,function(koulutusIndex){
                angular.forEach(koulutuksesArray,function(koulutus){
                    if (koulutusIndex.oid === koulutus) {
                        var index = $scope.model.koulutukses.indexOf(koulutusIndex);
                        $scope.model.koulutukses.splice(index,1);
                    }
                });
                });


        } else {

            $location.path("/etusivu");

        }
    };

    var reallyRemoveKoulutusFromHakukohde = function(koulutus){


        var koulutuksesArray = [];

        if (angular.isArray(koulutus)) {

            koulutuksesArray = koulutus;

            angular.forEach(koulutus,function(koulutusOid){
                filterRemovedKoulutusFromHakukohde(koulutusOid);
            });

        } else {

            koulutuksesArray.push(koulutus.oid);

            filterRemovedKoulutusFromHakukohde(koulutus.oid);

        }

        removeKoulutusRelationsFromHakukohde(koulutuksesArray);

    };

    $scope.showKoulutusHakukohtees = function(koulutus){


        var hakukohdePromise =  HakukohdeKoulutukses.getKoulutusHakukohdes(koulutus.oid);


        hakukohdePromise.then(function(hakukohteet){


            var modalInstance = $modal.open({
                templateUrl: 'partials/hakukohde/review/showKoulutusHakukohtees.html',
                controller: 'ShowKoulutusHakukohtees',
                windowClass: 'liita-koulutus-modal',
                resolve: {
                    hakukohtees: function () {
                        return hakukohteet.result;
                    } ,

                    selectedLocale : function() {
                        return $scope.model.userLang;
                    }

                }
            });
        });

    };

    $scope.removeKoulutusFromHakukohde = function(koulutus){

        if (checkIsOkToRemoveKoulutus()) {

            var texts = {
                title: LocalisationService.t("hakukohde.review.remove.koulutus.title"),
                description: LocalisationService.t("hakukohde.review.remove.koulutus.desc"),
                ok: LocalisationService.t("ok"),
                cancel: LocalisationService.t("cancel")
            };

            var d = dialogService.showDialog(texts);
            d.result.then(function(data){
                if (data) {
                    reallyRemoveKoulutusFromHakukohde(koulutus);
                }
            });

        }  else {

            $scope.model.validationmsgs.push('hakukohde.review.remove.koulutus.exp.msg');
            $scope.model.showError = true;

        }

    };

    $scope.getLiitteenKuvaus = function(liite,kieliUri) {
        return liite.liitteenKuvaukset[kieliUri];
    };

    $scope.getValintaperusteKuvaus = function(kieliUri) {

        if ($scope.model.hakukohde.valintaperusteKuvaukset !== undefined) {
            return $scope.model.hakukohde.valintaperusteKuvaukset[kieliUri];
        }


    };

    $scope.getSoraKuvaus = function(kieliUri) {

        if ($scope.model.hakukohde.soraKuvaukset !== undefined) {
            return $scope.model.hakukohde.soraKuvaukset[kieliUri];
        }



    };

    $scope.openLiitaKoulutusModal = function() {



        var modalInstance = $modal.open({
            templateUrl: 'partials/hakukohde/review/hakukohdeLiitaKoulutus.html',
            controller: 'HakukohdeLiitaKoulutusModalCtrl',
            windowClass: 'liita-koulutus-modal',
            resolve: {
                organisaatioOids: function () {
                   return $scope.model.hakukohde.tarjoajaOids;
                },
                selectedLocale : function() {
                    return $scope.model.userLang;
                },
                selectedKoulutukses : function() {
                    return $scope.model.hakukohde.hakukohdeKoulutusOids;
                }

            }
        });


        //First remove all existing relations and then add selected relations
        modalInstance.result.then(function(liitettavatKoulutukset){
            //TODO: figure out which koulutukses to remove and which to add
             var koulutuksesToRemove =  filterKoulutuksesToBeRemoved(liitettavatKoulutukset);

            if (koulutuksesToRemove.length > 0) {
                 reallyRemoveKoulutusFromHakukohde(koulutuksesToRemove);
            }

            var koulutuksesToAdd =  filterNewKoulutukses(liitettavatKoulutukset);

            angular.forEach(koulutuksesToAdd,function(koulutusOidToAdd){
                $scope.model.hakukohde.hakukohdeKoulutusOids.push(koulutusOidToAdd);
            });

             var liitaPromise = HakukohdeKoulutukses.addKoulutuksesToHakukohde($scope.model.hakukohde.oid,koulutuksesToAdd);
             liitaPromise.then(function(data){
                 if (data) {
                     $log.debug('RETURN DATA : ', data);
                     loadKoulutukses();
                 } else{
                   $log.debug('UNSUCCESFUL : ',data);
                 }
             });
        });

    };

});


/*

    ----------------> Show koulutus hakukohdes modal controller definition <-----------------

 */


app.controller('ShowKoulutusHakukohtees',
    function($scope,
             $log,
             $modalInstance,
             LocalisationService,
             hakukohtees,
             selectedLocale) {
                 
    $log = $log.getInstance("ShowKoulutusHakukohtees");
    $log.debug("init...");

    $scope.model = {};

    $scope.model.locale = selectedLocale;

    $scope.model.hakukohteet = hakukohtees;

    $scope.model.translations = {

        title : LocalisationService.t('hakukohde.review.koulutus.hakukohteet.title'),
        okBtn : LocalisationService.t('hakukohde.review.koulutus.hakukohteet.ok.btn')

    };


    $scope.model.cancel = function() {
        $log.debug("cancel");
        $modalInstance.dismiss('cancel');
    };



});

/*

    ----------------> Liita koulutus modal controller definition  <------------------


 */

app.controller('HakukohdeLiitaKoulutusModalCtrl',
    function($scope,
             $log,
             $modalInstance,
             LocalisationService,
             Config,
             TarjontaService,
             organisaatioOids,
             selectedLocale,
             selectedKoulutukses) {

    $log = $log.getInstance("HakukohdeLiitaKoulutusModalCtrl");
    $log.debug("init...");

    /*

        ----------> Init controller variables etc. <--------------

     */



    $scope.model = {};

    $scope.model.helper = {

        functions : {},
        allKoulutuksesMap : {}

    };

    $scope.model.translations = {

          title : LocalisationService.t('hakukohde.review.liita.koulutus.title'),
          poistaBtn : LocalisationService.t('hakukohde.review.liita.koulutus.poistaBtn'),
          cancelBtn : LocalisationService.t('tarjonta.hakukohde.liite.modal.peruuta.button'),
          saveBtn : LocalisationService.t('tarjonta.hakukohde.liite.modal.tallenna.button')

    };

    $scope.model.koodistoLocale = selectedLocale;

    $scope.model.selectedKoulutukses = [];

    $scope.model.searchKomoOids = [];

    $scope.model.hakutulos = [];

    /*

        ----------> Define "initialization functions <------------

     */


    var loadKomotos = function() {

        $scope.model.spec =  {//search parameter object
            oid: organisaatioOids,
            terms: '', //search words
            state: null,
            year: null,
            season: null
        };

    /*

        -----> Helper functions

    */

        $scope.model.helper.functions.checkSelectedKoulutukses = function(selectedKoulutusOids,orgKoulutukses){


          var matchingKoulutukses = [];


         angular.forEach(orgKoulutukses,function(koulutus){

             angular.forEach(selectedKoulutusOids,function(koulutusOid){

                 if (koulutus.komotoOid === koulutusOid) {
                     matchingKoulutukses.push(koulutus);

                 }

             });

         });



         return matchingKoulutukses;

     };


      $scope.model.helper.functions.checkIfKoulutusIsSelected = function(koulutus,koulutukses) {

            var isSelected = false;

            angular.forEach(koulutukses,function(i){
                if (i.komotoOid === koulutus.komotoOid) {
                    isSelected = true;
                }
            });

            return isSelected;

      }


        /*

            -----> Get koulutukses with given koulutus oids

         */


        TarjontaService.haeKoulutukset($scope.model.spec).then(function(result) {



            var tempArray = [];

            angular.forEach(result.tulokset,function(results){



                angular.forEach(results.tulokset,function(tulos){


                    var koulutuskoodiUri = tulos.koulutuskoodi.split("#")[0];

                    tempArray.push(tulos.komoOid);

                    var koulutusObj =   {
                        koulutuskoodi: koulutuskoodiUri,
                        nimi: tulos.nimi,
                        tarjoaja: tulos.nimi,
                        oid: tulos.komoOid,
                        komotoOid : tulos.oid
                    };

                    $scope.model.hakutulos.push(koulutusObj);
                    $scope.model.helper.allKoulutuksesMap[koulutusObj.oid] = koulutusObj;


                })
            });

            $scope.model.searchKomoOids = tempArray;

            $scope.model.selectedKoulutukses = $scope.model.helper.functions.checkSelectedKoulutukses(selectedKoulutukses,$scope.model.hakutulos);

        });

    };


    /*

        --------> Call "initialization" functions <---------------

     */

    loadKomotos();


    /*

        ------> Define "event handlers" etc. <-------------------

     */

    $scope.selectTreeHandler = function(selectedObject,event) {


        var selectedKoulutusObj =  $scope.model.helper.allKoulutuksesMap[selectedObject.oid];

        if (!$scope.model.helper.functions.checkIfKoulutusIsSelected(selectedKoulutusObj,$scope.model.selectedKoulutukses)) {
            $scope.model.selectedKoulutukses.push(selectedKoulutusObj);
        }


    };

    $scope.removeItem = function(selectedKoulutus){
        $log.debug("removeItem()", selectedKoulutus);

        var index = $scope.model.selectedKoulutukses.indexOf(selectedKoulutus);

        $scope.model.selectedKoulutukses.splice(index,1);

    };

    $scope.model.cancel = function() {
        $log.debug("cancel()");
        $modalInstance.dismiss('cancel');
    };


    $scope.model.save = function() {
        $log.debug("save()");

        var selectedKoulutusOids = [];

        angular.forEach($scope.model.selectedKoulutukses,function(koulutus){

            selectedKoulutusOids.push(koulutus.komotoOid);

        });

        $modalInstance.close(selectedKoulutusOids);
    };


});