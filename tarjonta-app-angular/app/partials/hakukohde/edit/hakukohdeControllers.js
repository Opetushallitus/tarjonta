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


var app = angular.module('app.kk.edit.hakukohde.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Hakukohde','auth','config','MonikielinenTextArea','MultiSelect','ngGrid','TarjontaOsoiteField']);


app.controller('HakukohdeEditController', 
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

    $log = $log.getInstance("HakukohdeEditController");

    var commonExceptionMsgKey = "tarjonta.common.unexpected.error.msg";

    //Initialize all variables and scope object in the beginning
    var postinumero = undefined;



    $scope.model.userLang  =  AuthService.getLanguage();

    if ($scope.model.userLang === undefined) {
        $scope.model.userLang = "FI";
    }


    /*

        ----> Scope function to express whether hakukohde can be saved or not

     */

    $scope.model.canSaveHakukohde = function() {

        if ($scope.editHakukohdeForm !== undefined) {


            return $scope.editHakukohdeForm.$valid && $scope.checkCanCreateOrEditHakukohde($scope.model.hakukohde);
        } else {
            return false;
        }

    };


    $scope.model.canSaveAsLuonnos = function() {

        return CommonUtilService.canSaveAsLuonnos($scope.model.hakukohde.tila);

    };

    if ($scope.model.hakukohde.lisatiedot !== undefined) {
        angular.forEach($scope.model.hakukohde.lisatiedot,function(lisatieto){

            $scope.model.selectedKieliUris.push(lisatieto.uri);
        });
    }

    //Placeholder for multiselect remove when refactored
    $scope.model.temp = {};

    var init = function() {
        $scope.loadKoulutukses();
        $scope.haeTarjoajaOppilaitosTyypit();
        $scope.model.continueToReviewEnabled = $scope.checkJatkaBtn($scope.model.hakukohde);
        $scope.checkIsCopy();
        $scope.updateTilaModel($scope.model.hakukohde);

    };

    init();



    $scope.model.hakukelpoisuusVaatimusPromise = Koodisto.getAllKoodisWithKoodiUri('pohjakoulutusvaatimuskorkeakoulut',AuthService.getLanguage());




    //$scope.model.koodiuriPromise = $q.defer();

    /*

        ---> If creating new hakukohde then tabs are disabled, when hakukohde has oid then
        tabs are enabled

     */

    if ($scope.model.hakukohde !== undefined && $scope.model.hakukohde.oid !== undefined) {
        $scope.model.hakukohdeTabsDisabled = false;
    } else {
        $scope.model.hakukohdeTabsDisabled = true;
    }


    if ($scope.model.hakukohde.hakukelpoisuusVaatimusKuvaukset === undefined) {
        $scope.model.hakukohde.hakukelpoisuusVaatimusKuvaukset = {};
    }
    
    if ($scope.model.hakukohde.kaytetaanJarjestelmanValintaPalvelua === undefined) {
    	$scope.model.hakukohde.kaytetaanJarjestelmanValintaPalvelua = true;
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
                var foundKoodi = $scope.findKoodiWithUri(kieliUri,$scope.model.allkieles);
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
      $scope.removeLisatieto(kieliUri);
    };


    /*

        -----> Checkbox change listener to retrieve hakus end time if selected

     */

    $scope.model.checkboxChange = function() {



        if ($scope.model.hakukohde.kaytetaanHaunPaattymisenAikaa) {
            var haku = $scope.getHakuWithOid($scope.model.hakukohde.hakuOid);

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

    $scope.model.isSoraEditable = function() {

        var retval = true;

        if ($scope.model.hakukohde !== undefined  && $scope.model.hakukohde.soraKuvausTunniste !== undefined) {
            retval = false;
        }


        return retval;

    };

    $scope.model.isValintaPerusteEditable = function() {

        var retval = true;

        if ($scope.model.hakukohde !== undefined  && $scope.model.hakukohde.valintaPerusteKuvausTunniste !== undefined) {
            retval = false;
        }


        return retval;
    };

    /*

        ------> Haku combobox listener -> listens to selected haku to check whether it contains inner application periods

     */


    $scope.model.hakuChanged = function() {


        if ($scope.model.hakukohde.hakuOid !== undefined) {

            $scope.model.hakuaikas.splice(0,$scope.model.hakuaikas.length);
            var haku = $scope.getHakuWithOid($scope.model.hakukohde.hakuOid);

            if (haku && haku.hakuaikas !== undefined && haku.hakuaikas.length > 1) {

                angular.forEach(haku.hakuaikas,function(hakuaika){

                    var formattedStartDate = $scope.createFormattedDateString(hakuaika.alkuPvm);

                    var formattedEndDate = $scope.createFormattedDateString(hakuaika.loppuPvm);

                    hakuaika.formattedNimi = hakuaika.nimi + ", " + formattedStartDate + " - " + formattedEndDate;

                    $scope.model.hakuaikas.push(hakuaika);
                });

                $log.debug('HAKUAIKAS : '  ,$scope.model.hakuaikas);

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
        $scope.model.showError = false;
        PermissionService.permissionResource().authorize({}, function(authResponse) {
        $scope.emptyErrorMessages();
        if ($scope.model.canSaveHakukohde() && $scope.validateHakukohde()) {
        $scope.model.showError = false;
        if ($scope.model.hakukohde.tila !== $scope.julkaistuVal) {
            $scope.model.hakukohde.tila = $scope.valmisVal;
        }

        $scope.model.hakukohde.modifiedBy = AuthService.getUserOid();
        $scope.removeEmptyKuvaukses();

        if ($scope.model.hakukohde.oid === undefined) {

             $log.debug('SAVE VALMIS MODEL : ', $scope.model.hakukohde);
           var returnResource =   $scope.model.hakukohde.$save();
           returnResource.then(function(hakukohde){

               if (hakukohde.errors === undefined || hakukohde.errors.length < 1) {
               $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                   $scope.model.hakukohdeOid = $scope.model.hakukohde.oid;
                   $scope.updateTilaModel($scope.model.hakukohde);
                   $scope.showSuccess();
                   $scope.checkIfSavingCopy($scope.model.hakukohde);
               } else {
                   $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                   $scope.showError(hakukohde.errors);
               }
               if ($scope.model.hakukohde.valintaperusteKuvaukset === undefined) {
                   $scope.model.hakukohde.valintaperusteKuvaukset = {};
               }
               if ($scope.model.hakukohde.soraKuvaukset === undefined) {
                   $scope.model.hakukohde.soraKuvaukset = {};
               }
               $scope.canEdit = true;
               $scope.model.continueToReviewEnabled = true;

           },function(error){


               $scope.showCommonUnknownErrorMsg();
           });

        } else {

            $log.debug('UPDATE MODEL : ', $scope.model.hakukohde);

            var returnResource = $scope.model.hakukohde.$update();
            returnResource.then(function(hakukohde){
                if (hakukohde.errors === undefined || hakukohde.errors.length < 1) {
                $scope.model.hakukohde = new Hakukohde(hakukohde.result);

                    $scope.updateTilaModel($scope.model.hakukohde);
                    $scope.showSuccess();
                } else {
                    $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                    $scope.showError(hakukohde.errors);
                }

                if ($scope.model.hakukohde.valintaperusteKuvaukset === undefined) {
                    $scope.model.hakukohde.valintaperusteKuvaukset = {};
                }
                if ($scope.model.hakukohde.soraKuvaukset === undefined) {
                    $scope.model.hakukohde.soraKuvaukset = {};
                }
            },function (error) {
               $scope.showCommonUnknownErrorMsg();
            });

        }
        } else {
            $scope.model.showError = true;
        }
    })
    };

    $scope.model.saveLuonnos = function() {

        $scope.model.showError = false;
        PermissionService.permissionResource().authorize({}, function(authResponse) {

        $log.debug('GOT AUTH RESPONSE : ' , authResponse);
        $scope.emptyErrorMessages();

        if ($scope.model.canSaveHakukohde() && $scope.validateHakukohde()) {
        $scope.model.showError = false;
            if ($scope.model.hakukohde.tila === undefined || $scope.model.hakukohde.tila === $scope.luonnosVal) {
            $scope.model.hakukohde.tila = $scope.luonnosVal;
            }

        $scope.model.hakukohde.modifiedBy = AuthService.getUserOid();
        $scope.removeEmptyKuvaukses();

        //Check if hakukohde is copy, then remove oid and save hakukohde as new
        $scope.checkIsCopy($scope.luonnosVal);
        if ($scope.model.hakukohde.oid === undefined) {

            $log.debug('LISATIEDOT : ' , $scope.model.hakukohde.lisatiedot);

            $log.debug('INSERTING MODEL: ', $scope.model.hakukohde);
           var returnResource =  $scope.model.hakukohde.$save();
            returnResource.then(function(hakukohde) {
               if (hakukohde.errors === undefined || hakukohde.errors.length < 1) {
                   $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                   $scope.model.hakukohdeOid = $scope.model.hakukohde.oid;
                   $scope.updateTilaModel($scope.model.hakukohde);
                   $scope.showSuccess();
                   $scope.checkIfSavingCopy($scope.model.hakukohde);
               } else {
                   $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                  $scope.showError(hakukohde.errors);
               }
                if ($scope.model.hakukohde.valintaperusteKuvaukset === undefined) {
                    $scope.model.hakukohde.valintaperusteKuvaukset = {};
                }
                if ($scope.model.hakukohde.soraKuvaukset === undefined) {
                    $scope.model.hakukohde.soraKuvaukset = {};
                }
                $scope.canEdit = true;
                $scope.model.continueToReviewEnabled = true;
                $log.debug('SAVED MODEL : ', $scope.model.hakukohde);
            },function(error) {
                $log.debug('ERROR INSERTING HAKUKOHDE : ', error);
                $scope.showCommonUnknownErrorMsg();

            });

        } else {
            $log.debug('UPDATE MODEL : ', $scope.model.hakukohde);
            var returnResource =  $scope.model.hakukohde.$update();
            returnResource.then(function(hakukohde){
                if (hakukohde.errors === undefined || hakukohde.errors.length < 1) {
                $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                $scope.updateTilaModel($scope.model.hakukohde);
                $scope.showSuccess();
                } else {
                    $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                    $scope.showError(hakukohde.errors);

                }
                if ($scope.model.hakukohde.valintaperusteKuvaukset === undefined) {
                    $scope.model.hakukohde.valintaperusteKuvaukset = {};
                }
                if ($scope.model.hakukohde.soraKuvaukset === undefined) {
                    $scope.model.hakukohde.soraKuvaukset = {};
                }
            }, function(error) {

                $log.debug('EXCEPTION UPDATING HAKUKOHDE AS LUONNOS : ', error);
                $scope.showCommonUnknownErrorMsg();
            });
        }
        } else {
            $scope.model.showError = true;
            $log.debug('WHAAT : ' , $scope.model.showError && $scope.editHakukohdeForm.aloituspaikatlkm.$invalid)

        }
    })
    };



    $scope.model.takaisin = function() {
        $location.path('/etusivu');
    };

    $scope.model.tarkastele = function() {

        $location.path('/hakukohde/'+$scope.model.hakukohde.oid);


    }

    $scope.haeValintaPerusteKuvaus = function(){

        $scope.naytaHaeValintaperusteKuvaus('valintaperustekuvaus');

    };

    $scope.haeSora = function() {

       $scope.naytaHaeValintaperusteKuvaus('SORA');

    };




});

app.controller('ValitseValintaPerusteKuvausDialog',
    function($scope,
             $q,
             $log,
             $modalInstance,
             LocalisationService,
             Kuvaus,
             Koodisto,
             oppilaitosTyypit,
             tyyppi,
             koulutusVuosi,
             AuthService) {

    $log = $log.getInstance("ValitseValintaPerusteKuvausDialog");

    var koodistoKieliUri = "kieli";

    var defaultKieliUri = "kieli_fi";

    $scope.dialog = {};

    $scope.dialog.kuvaukset = [];

    var kaikkiVpkKielet = {};

    var kaikkiKuvaukset = {};

    $scope.valittuKuvaus = null;

    $scope.dialog.kuvauksenKielet = {};

    $scope.dialog.valitutKuvauksenKielet = [];

    $scope.dialog.copySelection = "link";

    $scope.showKieliSelectionCheckboxDisabled = true;

    $scope.showKieliSelection = false;

    $scope.dialog.titles = {};

    $scope.dialog.titles.toimintoTitle =  LocalisationService.t('tarjonta.valintaperustekuvaus.valinta.dialog.toiminto.title');
    $scope.dialog.titles.tableValintaRyhma = LocalisationService.t('tarjonta.valintaperustekuvaus.valinta.dialog.table.valintaryhma.title');
    $scope.dialog.titles.tableKuvauskielet = LocalisationService.t('tarjonta.valintaperustekuvaus.valinta.dialog.table.kuvauskielet.title');
    $scope.dialog.titles.tuoMyosMuutkieletTitle = LocalisationService.t('tarjonta.valintaperustekuvaus.valinta.dialog.toiminto.muutkielet.title');
    $scope.dialog.titles.okBtn = LocalisationService.t('tarjonta.valintaperustekuvaus.valinta.dialog.btn.ok');
    $scope.dialog.titles.cancelBtn = LocalisationService.t('tarjonta.valintaperustekuvaus.valinta.dialog.btn.cancel');
    $scope.dialog.titles.kopioiHelp =  LocalisationService.t('tarjonta.valintaperustekuvaus.valinta.dialog.toiminto.kopioi.help');
    $scope.dialog.titles.linkkausHelp =  LocalisationService.t('tarjonta.valintaperustekuvaus.valinta.dialog.toiminto.linkkaus.help');



    var getYear = function() {

        if (koulutusVuosi) {
            return koulutusVuosi;
        } else {

            var today = new Date();

            return today.getFullYear();

        }


    }

    var getTitle = function(){
        if (tyyppi === "valintaperustekuvaus") {

            $scope.dialog.titles.title = LocalisationService.t('tarjonta.valintaperustekuvaus.valinta.dialog.title');

            $scope.dialog.titles.kopioTitle = LocalisationService.t('tarjonta.valintaperustekuvaus.valinta.dialog.toiminto.kopioi.title');

            $scope.dialog.titles.kopioiHelp =  LocalisationService.t('tarjonta.valintaperustekuvaus.valinta.dialog.toiminto.kopioi.help');

            $scope.dialog.titles.linkkausTitle = LocalisationService.t('tarjonta.valintaperustekuvaus.valinta.dialog.toiminto.linkkaus.title');

            $scope.dialog.titles.linkkausHelp =  LocalisationService.t('tarjonta.valintaperustekuvaus.valinta.dialog.toiminto.linkkaus.help');


        } else {
            $scope.dialog.titles.title = LocalisationService.t('tarjonta.valintaperustekuvaus.valinta.dialog.sora.title');

            $scope.dialog.titles.kopioTitle = LocalisationService.t('tarjonta.valintaperustekuvaus.valinta.dialog.toiminto.kopioi.sora.title');

            $scope.dialog.titles.kopioiHelp =  LocalisationService.t('tarjonta.valintaperustekuvaus.valinta.dialog.toiminto.kopioi.sora.help');

            $scope.dialog.titles.linkkausTitle = LocalisationService.t('tarjonta.valintaperustekuvaus.valinta.dialog.toiminto.linkkaus.sora.title');

            $scope.dialog.titles.linkkausHelp =  LocalisationService.t('tarjonta.valintaperustekuvaus.valinta.dialog.toiminto.linkkaus.sora.help');
        }
    }

    var haeValintaPerusteet = function() {

        //TODO: refactor this to more smaller functions and separate concerns

        $log.info('VALINTAPERUSTEET OPPILAITOSTYYPIT : ', oppilaitosTyypit);

        angular.forEach(oppilaitosTyypit,function(oppilaitosTyyppi){


            var valintaPerustePromise =  Kuvaus.findWithVuosiOppilaitostyyppiTyyppiVuosi(oppilaitosTyyppi,tyyppi,getYear());

            valintaPerustePromise.then(function(valintaperusteet){

                 $log.info('VALINTAPERUSTEET : ', valintaperusteet);

                 var userLang = AuthService.getLanguage();

                $log.info('VALINTAPERUSTE USER LANGUAGE : ', userLang);
                 // All different kieli promises
                var kieliPromises = {};

                var kieliPromiseArray = [];

                //Loop through valintaperusteet and get all different kieli promises
                angular.forEach(valintaperusteet.result,function(valintaPeruste){

                    $log.debug('VALINTAPERUSTE : ', valintaPeruste);

                    kaikkiKuvaukset[valintaPeruste.kuvauksenTunniste] = valintaPeruste;

                    var valintaPerusteObj = {};

                    valintaPerusteObj.kielet = "";

                    valintaPerusteObj.kieliUris = [];

                    valintaPerusteObj.tunniste = valintaPeruste.kuvauksenTunniste;

                    for (var kieli in valintaPeruste.kuvaukset) {


                       if(kieli.toString().indexOf(userLang) != -1) {

                           valintaPerusteObj.nimi = valintaPeruste.kuvauksenNimet[kieli];

                        }

                        if (valintaPerusteObj.nimi === undefined) {

                            valintaPerusteObj.nimi = valintaPeruste.kuvauksenNimet[defaultKieliUri];

                        }

                        valintaPerusteObj.kieliUris.push(kieli);
                        if (kieliPromises[kieli] === undefined) {
                            var kieliPromise = Koodisto.getKoodi(koodistoKieliUri,kieli,userLang);
                            kieliPromises[kieli] = kieli;
                            kieliPromiseArray.push(kieliPromise);
                        }

                    }
                    $scope.dialog.kuvaukset.push(valintaPerusteObj);

                });

                //Wait all promises to complete and add those values to objects
                $q.all(kieliPromiseArray).then(function(kieliKoodis){
                    $log.info('KIELIKOODIS: ', kieliKoodis);
                    angular.forEach(kieliKoodis,function(kieliKoodi){

                        if (kaikkiVpkKielet[kieliKoodi.koodiUri] === undefined) {
                            kaikkiVpkKielet[kieliKoodi.koodiUri] = kieliKoodi.koodiNimi;
                        }

                    });

                    //Loop through kuvaukses and find suitable name for language from object
                    angular.forEach($scope.dialog.kuvaukset,function(kuvaus){

                        for (var i = 0; i < kuvaus.kieliUris.length; i++) {

                            var counter = kuvaus.kieliUris.length - i;

                            if (counter != 1)  {
                                kuvaus.kielet = kuvaus.kielet + kaikkiVpkKielet[kuvaus.kieliUris[i]] + ",";
                            } else {
                                kuvaus.kielet = kuvaus.kielet + kaikkiVpkKielet[kuvaus.kieliUris[i]];
                            }


                        };

                    });

                });
            });

        });

    };

    getTitle();
    haeValintaPerusteet();

    $scope.selectedKuvaus = [];
    
    $scope.kuvausGrid = {
    	data: "dialog.kuvaukset",
    	multiSelect: false,
    	selectedItems: $scope.selectedKuvaus,
    	afterSelectionChange: function(row, event){
    		if ($scope.selectedKuvaus[0]) {
    			$scope.selectKuvaus($scope.selectedKuvaus[0]);
    		}
    	},
    	columnDefs:
    		[{field:"nimi", displayName: $scope.dialog.titles.tableValintaRyhma, width: "75%" },
    		 {field:"kielet", displayName: $scope.dialog.titles.tableKuvauskielet, width: "25%" }]
    };
    
    $scope.isOk = function() {
    	return $scope.valittuKuvaus && $scope.dialog.valitutKuvauksenKielet.length>0
    }

    $scope.selectKuvaus = function(kuvaus) {
        $log.debug("SELECT ",kuvaus);

        $scope.showKieliSelectionCheckboxDisabled = false;

        $scope.dialog.kuvauksenKielet = [];

        if ($scope.valittuKuvaus) {
        	$scope.valittuKuvaus.selected = false;
        }
        $scope.valittuKuvaus = kuvaus;
    	$scope.valittuKuvaus.selected = true;

       // $scope.dialog.kuvauksenKielet = {};

        angular.forEach(kuvaus.kieliUris,function(kuvausKieliUri){

            var kieliNimi = kaikkiVpkKielet[kuvausKieliUri];


            //$scope.dialog.kuvauksenKielet[kuvausKieliUri] = kieliNimi;
            var kieliObj = {
                uri : kuvausKieliUri,
                nimi : kieliNimi
            };

            $scope.dialog.kuvauksenKielet.push(kieliObj);

        });


    };

    $scope.onKieliValittu = function() {



        angular.forEach($scope.dialog.kuvauksenKielet,function(kieliObj){

            if (kieliObj.uri === $scope.dialog.valittuKuvausKieli ) {
                    $scope.dialog.valitutKuvauksenKielet.push(kieliObj);


            }

        });

    };

    $scope.toggle = function(kuvaus) {

        angular.forEach($scope.dialog.valitutKuvauksenKielet,function(valittuKuvaus){

            if (kuvaus.uri === valittuKuvaus.uri) {

               var index =   $scope.dialog.valitutKuvauksenKielet.indexOf(valittuKuvaus);
               $scope.dialog.valitutKuvauksenKielet.splice(index,1);

            };

        });

    };

    $scope.onCancel = function() {
        $modalInstance.dismiss('cancel');
    };

    $scope.onOk = function() {

        var valitutKuvaukset = [];

        angular.forEach($scope.dialog.valitutKuvauksenKielet,function(valittuKieli){

            if ($scope.valittuKuvaus !== undefined) {
               $log.debug('VALITTU KUVAUS: ' , $scope.valittuKuvaus);

                var valittuKokoKuvaus = kaikkiKuvaukset[$scope.valittuKuvaus.tunniste];

                var kuvaus = {
                    toimintoTyyppi : $scope.dialog.copySelection,
                    tunniste :  valittuKokoKuvaus.kuvauksenTunniste,
                    teksti : valittuKokoKuvaus.kuvaukset[valittuKieli],
                    kieliUri : valittuKieli

                }

                valitutKuvaukset.push(kuvaus);
            }


        });

        $modalInstance.close(valitutKuvaukset);
    }

});
