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

    $scope.userLangs = window.CONFIG.app.userLanguages; // liitteiden ja valintakokeiden kielien esijärjestystä varten
    $scope.model.defaultLang = 'kieli_fi';
    
	$scope.formControls = {}; // controls-layouttia varten

    //All kieles is received from koodistomultiselect
    $scope.model.allkieles = [];
    $scope.model.selectedKieliUris = [];

    $scope.model.userLang  =  AuthService.getLanguage();

    if ($scope.model.userLang === undefined) {
        $scope.model.userLang = "FI";
    }


    $scope.model.hakukohdeOppilaitosTyyppis = [];

    var koulutusKausiUri;

    $scope.model.koulutusVuosi;

    $scope.model.showError = false;

    $scope.model.showHakuaikas = false;

    $scope.model.showSuccess = false;

    $scope.model.collapse.model = true;

    $scope.model.koulutusnimet = [];

    $scope.model.organisaatioNimet = [];

    $scope.model.validationmsgs = [];

    var deferredOsoite = $q.defer();

    $scope.model.liitteidenToimitusOsoite = {}

    $scope.model.liitteenToimitusOsoitePromise = deferredOsoite.promise;

    $scope.model.hakus = [];

    $scope.model.hakuaikas = [];

    $scope.model.modifiedObj = {

        modifiedBy : '',
        modified : '',
        tila : ''

    };

    $scope.model.liitteidenToimitusPvm = new Date();

    $scope.model.continueToReviewEnabled = false;

    $scope.model.integerval=/^\d*$/;

    $scope.model.nimiValidationFailed = false;

    $scope.model.hakukelpoisuusValidationErrMsg = false;

    $scope.model.tallennaValmiinaEnabled = true;

    $scope.model.tallennaLuonnoksenaEnabled = true;

    var koulutusSet = new buckets.Set();

    var parentOrgOids = new buckets.Set();

    var orgSet = new buckets.Set();

    var julkaistuVal = "JULKAISTU";

    var luonnosVal = "LUONNOS";

    var valmisVal = "VALMIS";

    var peruttuVal = "PERUTTU";

    /*
        ----->  Helper functions
     */

    var showSuccess = function() {
        $scope.model.showSuccess = true;
        $scope.model.showError = false;
        $scope.model.validationmsgs = [];
        $scope.model.hakukohdeTabsDisabled = false;
    }


    var emptyErrorMessages = function() {

        $scope.model.validationmsgs.splice(0,$scope.model.validationmsgs.length);

        $scope.model.showError = false;

    }


    var checkIsCopy = function(tilaParam) {

        //If scope or route has isCopy parameter defined as true remove oid,
        //so that new hakukohde will be created
        $log.debug('IS THIS COPY ROUTE : ',$route.current.locals.isCopy);

        if ($route.current.locals.isCopy) {
            $log.debug('HAKUKOHDE IS COPY, SETTING OID UNDEFINED');
            $scope.model.hakukohde.oid = undefined;
            $scope.model.hakukohde.tila = tilaParam;

        }

        $log.debug('IS COPY : ' , $scope.isCopy);
        if ($scope.isCopy !== undefined && $scope.isCopy) {
            $scope.model.hakukohde.oid = undefined;
            $scope.model.hakukohde.tila = tilaParam;

        }


        $scope.model.isCopy = true;

    }


    var validateNameLengths = function() {

        var retval = true;

        angular.forEach($scope.model.hakukohde.hakukohteenNimet, function(hakukohdeNimi){

            if (hakukohdeNimi.length > 225) {
                retval = false;
            }

        });

        return retval;

    }


    var updateTilaModel = function(hakukohde) {
        if (hakukohde) {
            $scope.model.modifiedObj.modifiedBy = hakukohde.modifiedBy;
            $scope.model.modifiedObj.modified = hakukohde.modified;
            $scope.model.modifiedObj.tila = hakukohde.tila;
        }


    };

    var validateNames  = function() {
        for(var i in $scope.model.hakukohde.hakukohteenNimet){ return true;}
        return false;
    };

    var checkCanCreateOrEditHakukohde = function() {

        if ($scope.model.hakukohde.oid !== undefined) {

            if ($scope.canEdit !== undefined) {
                return $scope.canEdit;
            } else {
                return true;
            }


        } else {

            if ($scope.canCreate !== undefined) {

                return $scope.canCreate;

            } else {

                return true;

            }



        }



    }



    var checkJatkaBtn =   function() {

        if ($scope.model.hakukohde === undefined || $scope.model.hakukohde.oid === undefined) {
            $log.debug('HAKUKOHDE OR HAKUKOHDE OID UNDEFINED');

            $scope.model.continueToReviewEnabled = false;
        } else {
            $scope.model.continueToReviewEnabled  = true;
        }

    }


    var checkIfSavingCopy = function() {

        if ($scope.model.isCopy) {

            if ($scope.model.hakukohde.oid !== undefined) {

                $scope.model.isCopy = false;

                $location.path('/hakukohde/'+$scope.model.hakukohde.oid +'/edit');
            }




        }

    }

    var showCommonUnknownErrorMsg = function() {

        var errors = [];

        var error = {};

        error.errorMessageKey =  commonExceptionMsgKey;

        errors.push(error);

        showError(errors);

    }

    var validateHakukohde = function() {

        var errors = [];

        if ($scope.model.hakukohde.hakukelpoisuusvaatimusUris === undefined || $scope.model.hakukohde.hakukelpoisuusvaatimusUris.length < 1) {


            var error = {};
            error.errorMessageKey = 'tarjonta.hakukohde.hakukelpoisuusvaatimus.missing';
            $scope.model.hakukelpoisuusValidationErrMsg = true;
            errors.push(error);


        }

        if (!validateNames())  {

            var err = {};
            err.errorMessageKey = 'hakukohde.edit.nimi.missing';
            $scope.model.nimiValidationFailed = true;
            errors.push(err);

        }

        if (!validateNameLengths()) {

            var err = {};
            err.errorMessageKey = 'hakukohde.edit.nimi.too.long';

            errors.push(err);
        }


        if (errors.length < 1 ) {
            return true;
        } else {
            showError(errors);
            return false;
        }


    }

    var naytaHaeValintaperusteKuvaus = function(type) {

        var modalInstance = $modal.open({

            templateUrl: 'partials/hakukohde/edit/haeValintaPerusteKuvausDialog.html',
            controller: 'ValitseValintaPerusteKuvausDialog',
            windowClass: 'valintakoe-modal',
            resolve :  {
                koulutusVuosi : function() {
                  return $scope.model.koulutusVuosi;
                },

                oppilaitosTyypit : function() {

                    return $scope.model.hakukohdeOppilaitosTyyppis;
                },
                tyyppi : function() {
                    return type;
                }
            }

        });

        modalInstance.result.then(function(kuvaukset){

            $log.debug('GOT KUVAUKSET : ', kuvaukset);
            if ($scope.model.hakukohde.valintaPerusteKuvausKielet === undefined) {
                $scope.model.hakukohde.valintaPerusteKuvausKielet = [];

            }

            if ($scope.model.hakukohde.soraKuvausKielet === undefined) {
                $scope.model.hakukohde.soraKuvausKielet = [];
            }

                angular.forEach(kuvaukset,function(kuvaus){

                    if (type === "valintaperustekuvaus") {

                        $scope.model.hakukohde.valintaperusteKuvaukset[kuvaus.kieliUri] = kuvaus.teksti;
                        $scope.model.hakukohde.valintaPerusteKuvausKielet.push(kuvaus.kieliUri);

                        if (kuvaus.toimintoTyyppi === "link") {
                            $scope.model.hakukohde.valintaPerusteKuvausTunniste = kuvaus.tunniste;
                        } else if (kuvaus.toimintoTyyppi === "copy") {
                            $scope.model.hakukohde.valintaPerusteKuvausTunniste = undefined;
                        }


                    } else if (type === "SORA") {

                        $scope.model.hakukohde.soraKuvaukset[kuvaus.kieliUri] = kuvaus.teksti;
                        $scope.model.hakukohde.soraKuvausKielet.push(kuvaus.kieliUri);

                        if (kuvaus.toimintoTyyppi === "link") {
                            $scope.model.hakukohde.soraKuvausTunniste = kuvaus.tunniste;
                        }  else if (kuvaus.toimintoTyyppi === "copy") {
                            $scope.model.hakukohde.soraKuvausTunniste = undefined;
                        }


                    } else {
                    	throw ("'valintaperustekuvaus' | 'SORA' != "+type);
                    }

                });




        });

    }




    var createFormattedDateString = function(date) {

        return moment(date).format('DD.MM.YYYY HH:mm');

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


    var removeEmptyKuvaukses = function() {

          for (var langKey in $scope.model.hakukohde.valintaperusteKuvaukset) {

               if ($scope.model.hakukohde.valintaperusteKuvaukset[langKey].length < 1) {
                   delete  $scope.model.hakukohde.valintaperusteKuvaukset[langKey];
               }

          }

          for (var langKey in $scope.model.hakukohde.soraKuvaukset) {

              if ($scope.model.hakukohde.soraKuvaukset[langKey].length < 1) {
                  delete  $scope.model.hakukohde.soraKuvaukset[langKey];
              }

          }

    }

    var showError = function(errorArray) {
    	
    	$scope.model.validationmsgs.splice(0,$scope.model.validationmsgs.length);

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


            return $scope.editHakukohdeForm.$valid && checkCanCreateOrEditHakukohde();
        } else {
            return false;
        }

    }


    $scope.model.canSaveAsLuonnos = function() {

        return CommonUtilService.canSaveAsLuonnos($scope.model.hakukohde.tila);

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

    var loadKoulutukses = function(){

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

                            koulutusKausiUri = toinenTulos.kausiUri;
                            $scope.model.koulutusVuosi = toinenTulos.vuosi;

                            koulutusSet.add(toinenTulos.nimi);

                        });

                    }

                });


                $scope.model.koulutusnimet = koulutusSet.toArray();


                $scope.model.hakukohde.tarjoajaOids = tarjoajaOidsSet.toArray();

                getTarjoajaParentPaths($scope.model.hakukohde.tarjoajaOids);

                var orgQueryPromises = [];

                angular.forEach($scope.model.hakukohde.tarjoajaOids,function(tarjoajaOid){

                    orgQueryPromises.push(OrganisaatioService.byOid(tarjoajaOid));

                });

                $q.all(orgQueryPromises).then(function(orgs){

                    var counter = 0;

                    angular.forEach(orgs,function(data){

                        orgSet.add(data.nimi);

                        if (counter === 0) {
                            var wasHakutoimistoFound = checkAndAddHakutoimisto(data);

                            if (wasHakutoimistoFound) {
                                deferredOsoite.resolve($scope.model.liitteidenToimitusOsoite);
                            } else {
                                tryGetParentsApplicationOffice(data);
                            }
                        }

                        counter++;

                    });
                    $scope.model.organisaatioNimet = orgSet.toArray();

                    $log.debug('ORGANISAATIO NIMET : ', $scope.model.organisaatioNimet);
                });


            }




        });


    }


    var checkAndAddHakutoimisto = function(data) {
         var hakutoimistoFound = false;
        if (data.metadata !== undefined && data.metadata.yhteystiedot !== undefined) {

            angular.forEach(data.metadata.yhteystiedot,function(yhteystieto)  {

                if (yhteystieto.osoiteTyyppi !== undefined && yhteystieto.osoiteTyyppi === "posti") {
                    var kieliUris = yhteystieto.kieli.split('#');
                    var kieliUri = kieliUris[0];
                    $scope.model.liitteidenToimitusOsoite[kieliUri] = {};
                    $scope.model.liitteidenToimitusOsoite[kieliUri].osoiterivi1 = yhteystieto.osoite;
                    $scope.model.liitteidenToimitusOsoite[kieliUri].postinumero = yhteystieto.postinumeroUri;
                    $scope.model.liitteidenToimitusOsoite[kieliUri].postitoimipaikka = yhteystieto.postitoimipaikka;
                    //$scope.model.hakukohde.liitteidenToimitusOsoite.osoiterivi1 = yhteystieto.osoite;
                    //$scope.model.hakukohde.liitteidenToimitusOsoite.postinumero = yhteystieto.postinumeroUri;
                    //$scope.model.hakukohde.liitteidenToimitusOsoite.postitoimipaikka = yhteystieto.postitoimipaikka;
                    hakutoimistoFound = true;

                }

            });


        }

        return hakutoimistoFound;

    }


    var tryGetParentsApplicationOffice = function(currentOrg) {

        var isOppilaitos = false;

        var isKoulutusToimija = false;

        var oppilaitosTyyppi = "Oppilaitos";

        var koulutusToimijaTyyppi = "Koulutustoimija";

        angular.forEach(currentOrg.tyypit,function(tyyppi){

            if (tyyppi === oppilaitosTyyppi) {
                isOppilaitos = true;
            }
            if (tyyppi === koulutusToimijaTyyppi) {
                isKoulutusToimija = true;
            }

        });

        if (!isOppilaitos && !isKoulutusToimija) {
            if (currentOrg.parentOid !== undefined) {

                var anotherOrgPromise =  OrganisaatioService.byOid(currentOrg.parentOid);
                anotherOrgPromise.then(function(data) {

                    var wasHakutoimistoFoundNow = checkAndAddHakutoimisto(data);
                    if (wasHakutoimistoFoundNow) {
                        deferredOsoite.resolve($scope.model.liitteidenToimitusOsoite);
                    } else {
                        deferredOsoite.resolve($scope.model.liitteidenToimitusOsoite);
                    }


                });

            } else {

                deferredOsoite.resolve($scope.model.liitteidenToimitusOsoite);

            }


        } else {
            deferredOsoite.resolve($scope.model.liitteidenToimitusOsoite);
        }

    }


    var removeHashAndVersion = function(oppilaitosTyyppis) {

        var oppilaitosTyyppisWithOutVersion = [];

        angular.forEach(oppilaitosTyyppis,function(oppilaitosTyyppiUri) {
            angular.forEach(oppilaitosTyyppiUri,function(oppilaitosTyyppiUri){
                var splitStr = oppilaitosTyyppiUri.split("#");
                oppilaitosTyyppisWithOutVersion.push(splitStr[0]);
            });

        });
        return oppilaitosTyyppisWithOutVersion;
    };


    var haeTarjoajaOppilaitosTyypit = function() {


        OrganisaatioService.etsi({oidRestrictionList:$scope.model.hakukohde.tarjoajaOids})
            .then(function(data){

                getOppilaitosTyyppis(data.organisaatiot);

            });

    };

    var splitUri = function(uri) {

        var tokenizedArray = uri.split("#");
        return tokenizedArray[0];

    };

    var getOppilaitosTyyppis = function(organisaatiot) {

        var oppilaitosTyyppiPromises = [];

        angular.forEach(organisaatiot, function(organisaatio) {

            var oppilaitosTyypitPromise = CommonUtilService.haeOppilaitostyypit(organisaatio);
            oppilaitosTyyppiPromises.push(oppilaitosTyypitPromise);

        });




        //Resolve all promises and filter oppilaitostyyppis with user types
        $q.all(oppilaitosTyyppiPromises).then(function(data){
            $log.debug('RESOLVED OPPILAITOSTYYPPI : ', data);
            $scope.model.hakukohdeOppilaitosTyyppis = removeHashAndVersion(data);

        });


    };

    var init = function() {
        loadKoulutukses();
        haeTarjoajaOppilaitosTyypit();
        checkJatkaBtn();
        checkIsCopy();
        updateTilaModel($scope.model.hakukohde);
    };

    init();



    $scope.model.hakukelpoisuusVaatimusPromise = Koodisto.getAllKoodisWithKoodiUri('pohjakoulutusvaatimuskorkeakoulut',AuthService.getLanguage());


    var filterHakus = function(hakus) {
        return  filterHakusWithAika(filterHakusWithOrgs(hakus));

    };

    var getTarjoajaParentPaths = function(tarjoajaOids) {

        var orgPromises = [];

        angular.forEach(tarjoajaOids,function(tarjoajaOid){

            var orgPromise = CommonUtilService.haeOrganisaationTiedot(tarjoajaOid);
            orgPromises.push(orgPromise);
        });

        $q.all(orgPromises).then(function(orgs){
            angular.forEach(orgs,function(org) {
                if (org.parentOidPath) {
                    angular.forEach(org.parentOidPath.split("|"),function(parentOid) {
                        if (parentOid.length > 1) {
                            parentOrgOids.add(parentOid);
                        }

                    });
                }
            });
            retrieveHakus();

        });

    };


    var getParentOrgMap = function(parentOrgSet) {

        var parentOrgMap = {};
        angular.forEach(parentOrgSet,function(parentOrg){
            parentOrgMap[parentOrg] = 'X';
        });
        return parentOrgMap;
    }

    var checkIfOrgMatches = function(haku) {

        var hakuOrganisaatioOids = haku.organisaatioOids;
        var orgMatches = false;
        var parentOrgMap = getParentOrgMap(parentOrgOids);

        angular.forEach(hakuOrganisaatioOids,function(hakuOrganisaatioOid){


            if(parentOrgMap[hakuOrganisaatioOid]) {
                orgMatches = true;
            }

        });

        return orgMatches;

    };

    var filterHakusWithOrgs = function(hakus) {

        var filteredHakuArray = [];


        angular.forEach(hakus,function(haku){

            if (haku.organisaatioOids && haku.organisaatioOids.length > 0) {

                if (checkIfOrgMatches(haku)) {
                    filteredHakuArray.push(haku);
                }

            } else {
                filteredHakuArray.push(haku);
            }

        });


        return filteredHakuArray;
    };

    var filterHakusWithAika = function(hakus) {

        var filteredHakus = [];
        angular.forEach(hakus,function(haku){
            // rajaus kk-hakukohteisiin; ks. OVT-6452
            // TODO selvitä uri valitun koulutuksen perusteella

            var kohdeJoukkoUriNoVersion = splitUri(haku.kohdejoukkoUri);

            if (kohdeJoukkoUriNoVersion==window.CONFIG.app['haku.kohdejoukko.kk.uri']) {

                //OVT-6800 --> Rajataan koulutuksen alkamiskaudella ja vuodella
                if (haku.koulutuksenAlkamiskausiUri === koulutusKausiUri && haku.koulutuksenAlkamisVuosi === $scope.model.koulutusVuosi) {
                    filteredHakus.push(haku);
                }


            }
        });
        return filteredHakus;
    }

    /*

        -----> Retrieve all hakus

     */
    var retrieveHakus = function() {
        var hakuPromise = HakuService.getAllHakus();

        hakuPromise.then(function(hakuDatas) {
            $scope.model.hakus = [];
            angular.forEach(hakuDatas,function(haku){


                var userLang = AuthService.getLanguage();



                var hakuLang = userLang !== undefined ? userLang : $scope.model.defaultLang;

                for (var kieliUri in haku.nimi) {

                    if (kieliUri.indexOf(hakuLang) != -1 ) {
                        haku.lokalisoituNimi = haku.nimi[kieliUri];
                    }

                }



            });

            var filteredHakus = filterHakus(hakuDatas);

            angular.forEach(filteredHakus,function(haku){
                $scope.model.hakus.push(haku);
            });

            if ($scope.model.hakukohde.hakuOid !== undefined) {
                $scope.model.hakuChanged();
            }
        });
    }



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
    }
    /*

        ------> Haku combobox listener -> listens to selected haku to check whether it contains inner application periods

     */


    $scope.model.hakuChanged = function() {


        if ($scope.model.hakukohde.hakuOid !== undefined) {

            $scope.model.hakuaikas.splice(0,$scope.model.hakuaikas.length);
            var haku = getHakuWithOid($scope.model.hakukohde.hakuOid);

            if (haku && haku.hakuaikas !== undefined && haku.hakuaikas.length > 1) {

                angular.forEach(haku.hakuaikas,function(hakuaika){

                    var formattedStartDate = createFormattedDateString(hakuaika.alkuPvm);

                    var formattedEndDate = createFormattedDateString(hakuaika.loppuPvm);

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
        emptyErrorMessages();
        if ($scope.model.canSaveHakukohde() && validateHakukohde()) {
        $scope.model.showError = false;
        if ($scope.model.hakukohde.tila !== julkaistuVal) {
            $scope.model.hakukohde.tila = valmisVal;
        }

        $scope.model.hakukohde.modifiedBy = AuthService.getUserOid();
        removeEmptyKuvaukses();

            /*if ($scope.model.hakukohde.valintaPerusteKuvausTunniste !== undefined) {
                $scope.model.hakukohde.valintaperusteKuvaukset = {};
            }

            if ($scope.model.hakukohde.soraKuvausTunniste !== undefined) {
                $scope.model.hakukohde.soraKuvaukset = {};
            }*/
        if ($scope.model.hakukohde.oid === undefined) {

             $log.debug('SAVE VALMIS MODEL : ', $scope.model.hakukohde);
           var returnResource =   $scope.model.hakukohde.$save();
           returnResource.then(function(hakukohde){

               if (hakukohde.errors === undefined || hakukohde.errors.length < 1) {
               $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                   $scope.model.hakukohdeOid = $scope.model.hakukohde.oid;
                   updateTilaModel($scope.model.hakukohde);
                   showSuccess();
                   checkIfSavingCopy();
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
               $scope.canEdit = true;
               $scope.model.continueToReviewEnabled = true;

           },function(error){


               showCommonUnknownErrorMsg();
           });

        } else {

            $log.debug('UPDATE MODEL : ', $scope.model.hakukohde);

            var returnResource = $scope.model.hakukohde.$update();
            returnResource.then(function(hakukohde){
                if (hakukohde.errors === undefined || hakukohde.errors.length < 1) {
                $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                    console.log('HAKUKOHDE RESULT : ', hakukohde);
                    updateTilaModel($scope.model.hakukohde);
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
            },function (error) {
               showCommonUnknownErrorMsg();
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
        emptyErrorMessages();

        if ($scope.model.canSaveHakukohde() && validateHakukohde()) {
        $scope.model.showError = false;
            if ($scope.model.hakukohde.tila === undefined || $scope.model.hakukohde.tila === luonnosVal) {
            $scope.model.hakukohde.tila = luonnosVal;
            }

        $scope.model.hakukohde.modifiedBy = AuthService.getUserOid();
        removeEmptyKuvaukses();

        //Check if hakukohde is copy, then remove oid and save hakukohde as new
        checkIsCopy(luonnosVal);
        if ($scope.model.hakukohde.oid === undefined) {

            $log.debug('LISATIEDOT : ' , $scope.model.hakukohde.lisatiedot);

            $log.debug('INSERTING MODEL: ', $scope.model.hakukohde);
           var returnResource =  $scope.model.hakukohde.$save();
            returnResource.then(function(hakukohde) {
               if (hakukohde.errors === undefined || hakukohde.errors.length < 1) {
                   $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                   $scope.model.hakukohdeOid = $scope.model.hakukohde.oid;
                   updateTilaModel($scope.model.hakukohde);
                   showSuccess();
                   checkIfSavingCopy();
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
                $scope.canEdit = true;
                $scope.model.continueToReviewEnabled = true;
                $log.debug('SAVED MODEL : ', $scope.model.hakukohde);
            },function(error) {
                $log.debug('ERROR INSERTING HAKUKOHDE : ', error);
                showCommonUnknownErrorMsg();

            });

        } else {
            $log.debug('UPDATE MODEL : ', $scope.model.hakukohde);
            var returnResource =  $scope.model.hakukohde.$update();
            returnResource.then(function(hakukohde){
                if (hakukohde.errors === undefined || hakukohde.errors.length < 1) {
                $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                updateTilaModel($scope.model.hakukohde);
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
            }, function(error) {

                $log.debug('EXCEPTION UPDATING HAKUKOHDE AS LUONNOS : ', error);
                showCommonUnknownErrorMsg();
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

        naytaHaeValintaperusteKuvaus('valintaperustekuvaus');

    };

    $scope.haeSora = function() {

       naytaHaeValintaperusteKuvaus('SORA');

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
    }

    $scope.getKoulutustenNimetKey = function() {
    	return $scope.model.koulutusnimet.length==1 ? 'hakukohde.edit.header.single' : 'hakukohde.edit.header.multi';
    }

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
