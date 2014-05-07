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

/**
 *
 * This controller acts as routing and parent controller of all hakukohdes,
 * it contains all common controller variables and functions
 * @type {module|*}
 */

var app = angular.module('app.hakukohde.ctrl', []);

app.controller('HakukohdeRoutingController', ['$scope',
    '$log',
    '$routeParams',
    '$route',
    '$q',
    '$modal',
    '$location',
    'Hakukohde' ,
    'Koodisto',
    'AuthService',
    'HakuService',
    'LocalisationService',
    'OrganisaatioService',
    'SharedStateService',
    'TarjontaService',
    'Kuvaus',
    'CommonUtilService',
    'PermissionService',
    function ($scope,
                                        $log,
                                        $routeParams,
                                        $route,
                                        $q,
                                        $modal ,
                                        $location,
                                        Hakukohde,
                                        Koodisto,
                                        AuthService,
                                        HakuService,
                                        LocalisationService,
                                        OrganisaatioService,
                                        SharedStateService,
                                        TarjontaService,
                                        Kuvaus,
                                        CommonUtilService,
                                        PermissionService) {




        $log.info("HakukohdeRoutingController()", $routeParams);
        $log.info("$route: ", $route);
        $log.info("$route action: ", $route.current.$$route.action);
        $log.info("SCOPE: ", $scope);
        $log.info("CAN EDIT : ", $route.current.locals.canEdit);
        $log.info("CAN CREATE : ", $route.current.locals.canCreate);



        if ($route.current.locals.isCopy !== undefined) {

            $scope.isCopy = $route.current.locals.isCopy;
        } else {
            $scope.isCopy = false;
        }

        $scope.formControls = {reloadDisplayControls: function() {
        }}; // controls-layouttia varten


        $scope.canCreate = $route.current.locals.canCreate;
        $scope.canEdit =  $route.current.locals.canEdit;


        if ($route.current.locals.hakukohdex.result === undefined) {

            $scope.model = {
                collapse: {
                    model : true
                },
                hakukohdeTabsDisabled : true,
                hakukohde : {
                    valintaperusteKuvaukset : {},
                    soraKuvaukset : {},
                    kaytetaanJarjestelmanValintaPalvelua: true

                }
            }


            $scope.model.hakukohde = $route.current.locals.hakukohdex;




        } else {
            var hakukohdeResource = new Hakukohde( $route.current.locals.hakukohdex.result);

            if (hakukohdeResource.valintaperusteKuvaukset === undefined) {
                hakukohdeResource.valintaperusteKuvaukset = {};
            }

            if (hakukohdeResource.soraKuvaukset === undefined) {
                hakukohdeResource.soraKuvaukset = {};
            }

            $scope.model = {
                collapse: {
                    model : true
                },
                hakukohdeTabsDisabled : false,
                hakukohde : hakukohdeResource
            }

        }





        $scope.hakukohdex = $route.current.locals.hakukohdex;
        $log.info("  --> hakukohdex == ", $scope.hakukohdex);

        /*
         *
         * Common hakukohde controller variables
         *
         */


        $scope.modifiedObj = {

            modifiedBy : '',
            modified : 0,
            tila : ''

        };

        $scope.model.showSuccess = false;
        $scope.model.showError = false;
        $scope.model.validationmsgs = [];
        $scope.model.hakukohdeTabsDisabled = false;
        $scope.model.koulutusnimet = [];
        $scope.model.continueToReviewEnabled = false;
        $scope.model.organisaatioNimet = [];
        $scope.model.hakukohdeOppilaitosTyyppis = [];
        $scope.model.nimiValidationFailed = false;
        $scope.model.hakukelpoisuusValidationErrMsg = false;
        $scope.model.tallennaValmiinaEnabled = true;
        $scope.model.tallennaLuonnoksenaEnabled = true;
        $scope.model.liitteidenToimitusOsoite = {};
        var deferredOsoite = $q.defer();
        $scope.model.liitteenToimitusOsoitePromise = deferredOsoite.promise;
        $scope.model.liitteidenToimitusPvm = new Date();
        $scope.userLangs = window.CONFIG.app.userLanguages; // liitteiden ja valintakokeiden kielien esijärjestystä varten
        $scope.model.defaultLang = 'kieli_fi';
        $scope.model.showHakuaikas = false;
        $scope.model.collapse.model = true;
        $scope.model.hakus = [];
        $scope.model.hakuaikas = [];

        var deferredOsoite = $q.defer();
        var parentOrgOids = new buckets.Set();
        var orgSet = new buckets.Set();


        //All kieles is received from koodistomultiselect
        $scope.model.allkieles = [];
        $scope.model.selectedKieliUris = [];
        var koulutusKausiUri;
        $scope.model.koulutusVuosi;
        $scope.model.integerval=/^\d*$/;

        $scope.koulutusKausiUri;

        $scope.julkaistuVal = "JULKAISTU";

        $scope.luonnosVal = "LUONNOS";

        $scope.valmisVal = "VALMIS";

        $scope.peruttuVal = "PERUTTU";

        $scope.showSuccess = function() {
            $scope.model.showSuccess = true;
            $scope.model.showError = false;
            $scope.model.validationmsgs = [];
            $scope.model.hakukohdeTabsDisabled = false;
        };

        $scope.showError = function(errorArray) {

            $scope.model.validationmsgs.splice(0,$scope.model.validationmsgs.length);

            angular.forEach(errorArray,function(error) {


                $scope.model.validationmsgs.push(error.errorMessageKey);


            });
            $scope.model.showError = true;
            $scope.model.showSuccess = false;
        };

        /**
         *
         * This is the routing function which determines which hakukohde page to render
         *
         */

        $scope.getHakukohdePartialUri = function() {

            //var korkeakoulutusHakukohdePartialUri = "partials/hakukohde/edit/korkeakoulu/editKorkeakoulu.html";
            var korkeakoulutusHakukohdePartialUri = "partials/hakukohde/edit/korkeakoulu/editKorkeakoulu.html";
            var aikuLukioHakukohdePartialUri = "partials/hakukohde/edit/aiku/lukio/editAiku.html";
            var korkeakouluTyyppi = "KORKEAKOULUTUS";
            var lukioTyyppi = "LUKIOKOULUTUS";
            //If hakukohdex is defined then we are updating it
            //otherwise try to get selected koulutustyyppi from shared state
            if($route.current.locals && $route.current.locals.hakukohdex.result) {
                    $log.info('ROUTING HAKUKOHDE: ' , $route.current.locals.hakukohdex.result);
                    $log.info('WITH KOULUTUSTYYPPI : ', $route.current.locals.hakukohdex.result.koulutusAsteTyyppi);
                    if ($route.current.locals.hakukohdex.result.koulutusAsteTyyppi === korkeakouluTyyppi) {
                        return korkeakoulutusHakukohdePartialUri;
                        //TODO : This is commented out on testing remove comments after testing
                    }  else if ($route.current.locals.hakukohdex.result.koulutusAsteTyyppi === "LUKIOKOULUTUS"/*&& $route.current.locals.hakukohdex.result.koulutuslaji === "A"*/ ) {
                        return aikuLukioHakukohdePartialUri;
                    }

            } else {
                var koulutusTyyppi = SharedStateService.getFromState('SelectedKoulutusTyyppi');
                $log.info('KOULUTUSTYYPPI IS: ' , koulutusTyyppi);
                if (koulutusTyyppi.trim() === korkeakouluTyyppi) {
                    return korkeakoulutusHakukohdePartialUri;
                } else if (koulutusTyyppi.trim() === lukioTyyppi) {
                    return aikuLukioHakukohdePartialUri;
                } else {
                    $log.info('KOULUTUSTYYPPI WAS: ' , koulutusTyyppi);
                }

                //TODO: if not "KORKEAKOULUTUS" then check for "koulutuslaji" to determine if koulutus if "AIKU" or not
            }

        };


        $scope.validateNameLengths = function(hakukohteenNimet) {

            var retval = true;

            angular.forEach(hakukohteenNimet, function(hakukohdeNimi){

                if (hakukohdeNimi.length > 225) {
                    retval = false;
                }

            });

            return retval;

        };

        $scope.checkJatkaBtn =   function(hakukohde) {

            if (hakukohde === undefined || hakukohde.oid === undefined) {
                $log.debug('HAKUKOHDE OR HAKUKOHDE OID UNDEFINED');

                return false;
            } else {
                return true;
            }

        };

        $scope.updateTilaModel = function(hakukohde) {

            if (hakukohde) {
                $scope.modifiedObj.modifiedBy = hakukohde.modifiedBy;
                $scope.modifiedObj.modified = hakukohde.modified;
                $scope.modifiedObj.tila = hakukohde.tila;
            }
            console.log('FORM CONTROLS : ', $scope.formControls);
            if ($scope.formControls && $scope.formControls.reloadDisplayControls) {
                $log.debug('RELOADING FORM CONTROLS : ', $scope.formControls);
                $scope.formControls.reloadDisplayControls();
            }


        };

        $scope.emptyErrorMessages = function() {

            $scope.model.validationmsgs.splice(0,$scope.model.validationmsgs.length);

            $scope.model.showError = false;

        };

        $scope.checkCanCreateOrEditHakukohde = function(hakukohde) {

            if (hakukohde.oid !== undefined) {

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
        };


        $scope.checkIfSavingCopy = function(hakukohde) {

            if ($scope.model.isCopy) {

                if (hakukohde.oid !== undefined) {

                    $scope.model.isCopy = false;

                    $location.path('/hakukohde/'+hakukohde.oid +'/edit');
                }




            }

        };

        $scope.showCommonUnknownErrorMsg = function() {

            var errors = [];

            var error = {};

            error.errorMessageKey =  commonExceptionMsgKey;

            errors.push(error);

            $scope.showError(errors);

        };

        $scope.checkIsCopy = function(tilaParam) {

            //If scope or route has isCopy parameter defined as true remove oid,
            //so that new hakukohde will be created


            if ($route.current.locals && $route.current.locals.isCopy) {
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

        };

        $scope.createFormattedDateString = function(date) {

            return moment(date).format('DD.MM.YYYY HH:mm');

        };

        $scope.haeTarjoajaOppilaitosTyypit = function() {


            OrganisaatioService.etsi({oidRestrictionList:$scope.model.hakukohde.tarjoajaOids})
                .then(function(data){

                    getOppilaitosTyyppis(data.organisaatiot);

                });

        };

        $scope.validateHakukohde = function() {

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

            if (!$scope.validateNameLengths($scope.model.hakukohde.hakukohteenNimet)) {

                var err = {};
                err.errorMessageKey = 'hakukohde.edit.nimi.too.long';

                errors.push(err);
            }


            if (errors.length < 1 ) {
                return true;
            } else {
                $scope.showError(errors);
                return false;
            }


        };


        /*

         ------>  Load hakukohde koulutusnames

         */

        $scope.loadKoulutukses = function(hakuFilterFunction){



            var koulutusSet = new buckets.Set();

            var spec = {
                koulutusOid : $scope.model.hakukohde.hakukohdeKoulutusOids
            };

            TarjontaService.haeKoulutukset(spec).then(function(data){

                var tarjoajaOidsSet = new buckets.Set();


                if (data !== undefined) {

                    angular.forEach(data.tulokset,function(tulos){
                        if (tulos !== undefined && tulos.tulokset !== undefined) {

                            tarjoajaOidsSet.add(tulos.oid);

                            angular.forEach(tulos.tulokset,function(toinenTulos){

                                $scope.koulutusKausiUri = toinenTulos.kausiUri;
                                $scope.model.koulutusVuosi = toinenTulos.vuosi;

                                koulutusSet.add(toinenTulos.nimi);

                            });

                        }

                    });


                    $scope.model.koulutusnimet = koulutusSet.toArray();


                    $scope.model.hakukohde.tarjoajaOids = tarjoajaOidsSet.toArray();

                    $scope.getTarjoajaParentPaths($scope.model.hakukohde.tarjoajaOids,hakuFilterFunction);

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
                                    $scope.tryGetParentsApplicationOffice(data);
                                }
                            }

                            counter++;

                        });
                        $scope.model.organisaatioNimet = orgSet.toArray();

                        $log.debug('ORGANISAATIO NIMET : ', $scope.model.organisaatioNimet);
                    });
                }
            });
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
        };

        $scope.getKoulutustenNimetKey = function() {
            return $scope.model.koulutusnimet.length==1 ? 'hakukohde.edit.header.single' : 'hakukohde.edit.header.multi';
        };


        $scope.checkIfFirstArraysOneElementExistsInSecond = function(array1,array2) {

            angular.forEach(array1,function(element){
               if (array2.indexOf(element) != -1) {
                   return true;
               }
            });

        };


        var checkForOph = function(hakuOrgs) {

            angular.forEach(hakuOrgs,function(hakuOrg){

               if (hakuOrg === window.CONFIG.app['haku.hakutapa.erillishaku.uri']) {
                   return true;
               }

            });

        }

        /*

         -----> Retrieve all hakus

         */
        $scope.retrieveHakus = function(filterHakuFunction) {
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

                var filteredHakus = filterHakuFunction(hakuDatas);

                angular.forEach(filteredHakus,function(haku){
                    $scope.model.hakus.push(haku);
                });

                if ($scope.model.hakukohde.hakuOid !== undefined) {
                    $scope.model.hakuChanged();
                }
            });
        };

        $scope.filterHakusWithOrgs = function(hakus) {

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







        $scope.getTarjoajaParentPaths = function(tarjoajaOids,hakufilterFunction) {

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
                $scope.retrieveHakus(hakufilterFunction);

            });

        };


        $scope.tryGetParentsApplicationOffice = function(currentOrg) {

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

        };


        $scope.removeEmptyKuvaukses = function() {

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

        };

        $scope.removeLisatieto = function(koodi){

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

        };


        $scope.naytaHaeValintaperusteKuvaus = function(type) {

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

        $scope.loadHakukelpoisuusVaatimukset = function () {
            $scope.model.hakukelpoisuusVaatimusPromise = Koodisto.getAllKoodisWithKoodiUri('pohjakoulutusvaatimuskorkeakoulut',AuthService.getLanguage());
        };

        $scope.enableOrDisableTabs = function () {

            if ($scope.model.hakukohde !== undefined && $scope.model.hakukohde.oid !== undefined) {
                $scope.model.hakukohdeTabsDisabled = false;
            } else {
                $scope.model.hakukohdeTabsDisabled = true;
            }



        };

        $scope.model.takaisin = function() {
            $location.path('/etusivu');
        };

        $scope.model.tarkastele = function() {

            $location.path('/hakukohde/'+$scope.model.hakukohde.oid);


        };

        $scope.haeValintaPerusteKuvaus = function(){

            $scope.naytaHaeValintaperusteKuvaus('valintaperustekuvaus');

        };

        $scope.haeSora = function() {

            $scope.naytaHaeValintaperusteKuvaus('SORA');

        };

        /*
         ----->  Helper functions
         */


        var validateNames  = function() {
            for(var i in $scope.model.hakukohde.hakukohteenNimet){ return true;}
            return false;
        };







        $scope.getHakuWithOid = function(hakuOid) {

            var foundHaku;

            angular.forEach($scope.model.hakus,function(haku){
                if (haku.oid === hakuOid) {
                    foundHaku = haku;
                }
            });

            return foundHaku;

        };

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

        };


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

        $scope.findKoodiWithUri = function(koodi,koodis)  {


            var foundKoodi;

            angular.forEach(koodis,function(koodiLoop){
                if (koodiLoop.koodiUri === koodi){
                    foundKoodi = koodiLoop;
                }
            });


            return foundKoodi;
        };


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

        $scope.splitUri = function(uri) {

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

    }
]);
