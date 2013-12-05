/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 */


'use strict';

/* Controllers */


var app = angular.module('app.kk.edit.hakukohde.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Hakukohde','auth','config','MonikielinenTextArea']);


app.controller('HakukohdeEditController', function($scope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde,AuthService, HakuService, $modal ,Config,$location,$timeout,TarjontaService) {


    //Initialize all variables and scope object in the beginning
    var postinumero = undefined;

	$scope.formControls = {}; // controls-layouttia varten

    //All kieles is received from koodistomultiselect
    $scope.model.allkieles = [];
    $scope.model.selectedKieliUris = [];
	
    $scope.model.userLang  =  AuthService.getLanguage();

    if ($scope.model.userLang === undefined) {
        $scope.model.userLang = "FI";
    }

    $scope.model.showError = false;

    $scope.model.showHakuaikas = false;

    $scope.model.showSuccess = false;

    $scope.model.collapse.model = true;

    $scope.model.koulutusnimet = [];

    $scope.model.validationmsgs = [];

    $scope.model.hakus = [];

    $scope.model.hakuaikas = [];

    $scope.model.liitteidenToimitusPvm = new Date();

    var koulutusSet = new buckets.Set();

    /*
        ----->  Helper functions
     */

    var showSuccess = function() {
        $scope.model.showSuccess = true;
        $scope.model.showError = false;
        $scope.model.validationmsgs = [];
        $scope.model.hakukohdeTabsDisabled = false;
    }


    var validateHakukohde = function() {

        var errors = [];

        if ($scope.model.hakukohde.hakukelpoisuusvaatimusUris === undefined || $scope.model.hakukohde.hakukelpoisuusvaatimusUris.length < 1) {


            var error = {};
            error.errorMessageKey = 'tarjonta.hakukohde.hakukelpoisuusvaatimus.missing';
            errors.push(error);


        }

        if (errors.length < 1 ) {
            return true;
        } else {
            showError(errors);
            return false;
        }


    }

    var getHakuWithOid = function(hakuOid) {

        var foundHaku;

        angular.forEach($scope.model.hakus,function(haku){
           if (haku.oid === hakuOid) {
               foundHaku = haku;
           }
        });

        return foundHaku;

    }

    var showError = function(errorArray) {
    	
    	$scope.model.validationmsgs = [];

        angular.forEach(errorArray,function(error) {


            $scope.model.validationmsgs.push(error.errorMessageKey);


        });
        $scope.model.showError = true;
        $scope.model.showSuccess = false;
    }

    var removeLisatieto = function(koodi){

        var foundLisatieto;
        angular.forEach($scope.model.hakukohde.lisatiedot,function(lisatieto) {
            if (lisatieto.uri === koodi) {
                foundLisatieto = lisatieto;
            }
        });

        if (foundLisatieto !== undefined) {
            var index = $scope.model.hakukohde.lisatiedot.indexOf(foundLisatieto);
            $scope.model.hakukohde.lisatiedot.splice(index,1);
        }

    }

    /*

     ------>  Koodisto helper methods

     */
    var findKoodiWithArvo = function(koodi,koodis)  {


        var foundKoodi;

        angular.forEach(koodis,function(koodiLoop){
            if (koodiLoop.koodiArvo === koodi){
                foundKoodi = koodiLoop;
            }
        });


        return foundKoodi;
    };

    var findKoodiWithUri = function(koodi,koodis)  {


        var foundKoodi;

        angular.forEach(koodis,function(koodiLoop){
            if (koodiLoop.koodiUri === koodi){
                foundKoodi = koodiLoop;
            }
        });


        return foundKoodi;
    };


    /*

        ----> Scope function to express whether hakukohde can be saved or not

     */

    $scope.model.canSaveHakukohde = function() {
        if ($scope.editHakukohdeForm !== undefined) {
            return $scope.editHakukohdeForm.$valid;
        } else {
            return false;
        }

    }



    if ($scope.model.hakukohde.lisatiedot !== undefined) {
        angular.forEach($scope.model.hakukohde.lisatiedot,function(lisatieto){

            $scope.model.selectedKieliUris.push(lisatieto.uri);
        });
    }

    //Placeholder for multiselect remove when refactored
    $scope.model.temp = {};


    /*

        ------>  Load hakukohde koulutusnames

     */
    var spec = {
        koulutusOid : $scope.model.hakukohde.hakukohdeKoulutusOids
    };
    koulutusSet.clear();
    TarjontaService.haeKoulutukset(spec).then(function(data){


        var tarjoajaOidsSet = new buckets.Set();


        if (data !== undefined) {

            angular.forEach(data.tulokset,function(tulos){
                if (tulos !== undefined && tulos.tulokset !== undefined) {

                    tarjoajaOidsSet.add(tulos.oid);

                    angular.forEach(tulos.tulokset,function(toinenTulos){
                        koulutusSet.add(toinenTulos.nimi);

                    });

                }

            });
            $scope.model.koulutusnimet = koulutusSet.toArray();


                $scope.model.hakukohde.tarjoajaOids = tarjoajaOidsSet.toArray();

                var orgPromise =  OrganisaatioService.byOid($scope.model.hakukohde.tarjoajaOids[0]);
                //When organisaatio is loaded set the liitteiden toimitusosoite on the model
                orgPromise.then(function(data){
                    if (data.postiosoite !== undefined) {


                        $scope.model.hakukohde.liitteidenToimitusOsoite.osoiterivi1 = data.postiosoite.osoite;
                        $scope.model.hakukohde.liitteidenToimitusOsoite.postinumero = data.postiosoite.postinumeroUri;
                        $scope.model.hakukohde.liitteidenToimitusOsoite.postitoimipaikka = data.postiosoite.postitoimipaikka;
                        postinumero = data.postiosoite.postinumeroUri;
                    }
                });



        }




    });



    $scope.model.hakukelpoisuusVaatimusPromise = Koodisto.getAllKoodisWithKoodiUri('pohjakoulutusvaatimuskorkeakoulut',AuthService.getLanguage());


    /*

        -----> Retrieve all hakus

     */

    var hakuPromise = HakuService.getAllHakus();

    hakuPromise.then(function(hakuDatas) {
        console.log('GOT HAKUS ', hakuDatas.length);
        angular.forEach(hakuDatas,function(haku){


            angular.forEach(haku.nimi,function(nimi){

                if (nimi.arvo !== undefined && nimi.arvo.toUpperCase() === $scope.model.userLang.toUpperCase() ) {
                    haku.lokalisoituNimi = nimi.teksti;
                }

            });

            $scope.model.hakus.push(haku);
        });

        if ($scope.model.hakukohde.hakuOid !== undefined) {
            $scope.model.hakuChanged();
        }
    });


    //$scope.model.koodiuriPromise = $q.defer();

    /*
        ----> Retrieve all postinumeros
     */

    $scope.model.postinumeroarvo = {

    };

    var koodistoPromise = Koodisto.getAllKoodisWithKoodiUri('posti',$scope.model.userLang);

    koodistoPromise.then(function(koodisParam){
      $scope.model.koodis = koodisParam;

      if (postinumero !== undefined) {
          console.log('Changing arvo : ', postinumero);
          var koodi =  findKoodiWithUri(postinumero,$scope.model.koodis);
          console.log('TO : ', koodi);
          $scope.model.postinumeroarvo.arvo = koodi.koodiArvo;
      }
    });

   /*

        -----> Get selected postinumero from and set the nimi to postitoimipaikka text

    */

    $scope.model.onKieliTypeAheadChange = function() {
       var koodi = findKoodiWithArvo($scope.model.postinumeroarvo.arvo,$scope.model.koodis);

       $scope.model.hakukohde.liitteidenToimitusOsoite.postinumero = koodi.koodiUri;
       $scope.model.hakukohde.liitteidenToimitusOsoite.postitoimipaikka = koodi.koodiNimi;

    };


    /*

        ---> If creating new hakukohde then tabs are disabled, when hakukohde has oid then
        tabs are enabled

     */

    if ($scope.model.hakukohde !== undefined && $scope.model.hakukohde.oid !== undefined) {
        $scope.model.hakukohdeTabsDisabled = false;
    } else {
        $scope.model.hakukohdeTabsDisabled = true;
    }





    $scope.model.kieliCallback = function(kieliUri) {
        if ($scope.model.allkieles !== undefined) {
            var lisatietoFound = false;
            //Check that selected kieli does not exist in list
            angular.forEach($scope.model.hakukohde.lisatiedot,function(lisatieto) {
                 if (lisatieto.uri === kieliUri) {
                     lisatietoFound = true;
                 }
            });
            if (!lisatietoFound) {
                var foundKoodi = findKoodiWithUri(kieliUri,$scope.model.allkieles);
                var newLisatieto = {
                    "uri" : foundKoodi.koodiUri,
                    "nimi" : foundKoodi.koodiNimi,
                    "teksti": ""
                };

                $scope.model.hakukohde.lisatiedot.push(newLisatieto);
            }

        }
    };

    $scope.model.kieliRemoveCallback = function(kieliUri) {
      removeLisatieto(kieliUri);
    };

    $scope.model.postinumeroCallback = function(selectedPostinumero) {
       console.log('Postinumero callback : ', selectedPostinumero);

       $scope.model.hakukohde.liitteidenToimitusOsoite.postitoimipaikka = selectedPostinumero.koodiNimi;
    };

    /*

        -----> Checkbox change listener to retrieve hakus end time if selected

     */

    $scope.model.checkboxChange = function() {



        if ($scope.model.hakukohde.kaytetaanHaunPaattymisenAikaa) {
            var haku = getHakuWithOid($scope.model.hakukohde.hakuOid);

            var hakuPaattymisAika;

            angular.forEach(haku.hakuaikas,function(hakuaika){
                if (hakuPaattymisAika === undefined) {
                    hakuPaattymisAika = hakuaika.loppuPvm;
                } else {
                    if (hakuPaattymisAika < hakuaika.loppuPvm) {
                        hakuPaattymisAika = hakuaika.loppuPvm;
                    }
                }

            });

            if (hakuPaattymisAika !== undefined) {
                $scope.model.hakukohde.liitteidenToimitusPvm = hakuPaattymisAika;

            }


        }





    };


    /*

        ------> Haku combobox listener -> listens to selected haku to check whether it contains inner application periods

     */


    $scope.model.hakuChanged = function() {


        if ($scope.model.hakukohde.hakuOid !== undefined) {

            $scope.model.hakuaikas.splice(0,$scope.model.hakuaikas.length);
            var haku = getHakuWithOid($scope.model.hakukohde.hakuOid);

            if (haku.hakuaikas !== undefined && haku.hakuaikas.length > 1) {

                angular.forEach(haku.hakuaikas,function(hakuaika){
                    $scope.model.hakuaikas.push(hakuaika);
                });

                console.log('HAKUAIKAS : '  ,$scope.model.hakuaikas);

                $scope.model.showHakuaikas = true;

            } else {

                $scope.model.showHakuaikas = false;

            }

        }


    };

    /*

        ------> Hakukohde save functions

     */

    $scope.model.saveValmis = function() {

        if ($scope.model.canSaveHakukohde() && validateHakukohde()) {
        $scope.model.showError = false;
        $scope.model.hakukohde.tila = "VALMIS";
        if ($scope.model.hakukohde.oid === undefined) {

             console.log('MODEL: ', $scope.model.hakukohde);
           var returnResource =   $scope.model.hakukohde.$save();
           returnResource.then(function(hakukohde){

               if (hakukohde.errors === undefined || hakukohde.errors.length < 1) {
               $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                   $scope.model.hakukohdeOid = $scope.model.hakukohde.oid;
                   showSuccess();
               } else {
                   $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                   showError(hakukohde.errors);
               }
               if ($scope.model.hakukohde.valintaperusteKuvaukset === undefined) {
                   $scope.model.hakukohde.valintaperusteKuvaukset = {};
               }
               if ($scope.model.hakukohde.soraKuvaukset === undefined) {
                   $scope.model.hakukohde.soraKuvaukset = {};
               }


           });

        } else {

            console.log('UPDATE MODEL : ', $scope.model.hakukohde);
            var returnResource = $scope.model.hakukohde.$update();
            returnResource.then(function(hakukohde){
                if (hakukohde.errors === undefined || hakukohde.errors.length < 1) {
                $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                    showSuccess();
                } else {
                    $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                    showError(hakukohde.errors);
                }

                if ($scope.model.hakukohde.valintaperusteKuvaukset === undefined) {
                    $scope.model.hakukohde.valintaperusteKuvaukset = {};
                }
                if ($scope.model.hakukohde.soraKuvaukset === undefined) {
                    $scope.model.hakukohde.soraKuvaukset = {};
                }
            });

        }
        }
    };

    $scope.model.saveLuonnos = function() {

        if ($scope.model.canSaveHakukohde() && validateHakukohde()) {
        $scope.model.showError = false;
        $scope.model.hakukohde.tila = "LUONNOS";
        if ($scope.model.hakukohde.oid === undefined) {

            console.log('LISATIEDOT : ' , $scope.model.hakukohde.lisatiedot);

            console.log('MODEL: ', $scope.model.hakukohde);
           var returnResource =  $scope.model.hakukohde.$save();
            returnResource.then(function(hakukohde) {
               if (hakukohde.errors === undefined || hakukohde.errors.length < 1) {
                   $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                   $scope.model.hakukohdeOid = $scope.model.hakukohde.oid;
                   showSuccess();
               } else {
                   $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                  showError(hakukohde.errors);
               }
                if ($scope.model.hakukohde.valintaperusteKuvaukset === undefined) {
                    $scope.model.hakukohde.valintaperusteKuvaukset = {};
                }
                if ($scope.model.hakukohde.soraKuvaukset === undefined) {
                    $scope.model.hakukohde.soraKuvaukset = {};
                }
                console.log('SAVED MODEL : ', $scope.model.hakukohde);
            });

        } else {
            console.log('UPDATE MODEL : ', $scope.model.hakukohde);
            var returnResource =  $scope.model.hakukohde.$update();
            returnResource.then(function(hakukohde){
                if (hakukohde.errors === undefined || hakukohde.errors.length < 1) {
                $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                showSuccess();
                } else {
                    $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                    showError(hakukohde.errors);

                }
                if ($scope.model.hakukohde.valintaperusteKuvaukset === undefined) {
                    $scope.model.hakukohde.valintaperusteKuvaukset = {};
                }
                if ($scope.model.hakukohde.soraKuvaukset === undefined) {
                    $scope.model.hakukohde.soraKuvaukset = {};
                }
            });
        }
        }
    };



    $scope.model.takaisin = function() {
        $location.path('/etusivu');
    };


    $scope.getKoulutustenNimet = function() {
    	var ret = "";
    	var ja = LocalisationService.t("tarjonta.yleiset.ja");
    	
    	for (var i in $scope.model.koulutusnimet) {
    		if (i>0) {
    			ret = ret + ((i==$scope.model.koulutusnimet.length-1) ? " "+ja+" " : ", ");
    		}
    		ret = ret + "<b>" + $scope.model.koulutusnimet[i] + "</b>";
    	}
    	
    	return ret;
    }

    $scope.getKoulutustenNimetKey = function() {
    	return $scope.model.koulutusnimet.length==1 ? 'hakukohde.edit.header.single' : 'hakukohde.edit.header.multi';
    }

});
