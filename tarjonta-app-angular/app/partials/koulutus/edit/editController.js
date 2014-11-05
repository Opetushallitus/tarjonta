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

var app = angular.module('app.edit.ctrl', ['Koodisto', 'Yhteyshenkilo', 'ngResource', 'ngGrid', 'imageupload', 'MultiSelect', 'OrderByNumFilter', 'localisation', 'MonikielinenTextField', 'ControlsLayout']);

app.controller('BaseEditController', [
    '$scope', '$log', 'Config',
    '$routeParams', '$route', '$location',
    'KoulutusConverterFactory', 'TarjontaService', 'PermissionService',
    'OrganisaatioService', 'Koodisto', 'KoodistoURI', 'LocalisationService',
    'dialogService', 'CacheService', '$modal', 'OrganisaatioService', 'AuthService',
    function BaseEditController($scope, $log, Config,
        $routeParams, $route, $location,
        converter, TarjontaService, PermissionService,
        organisaatioService, Koodisto, KoodistoURI, LocalisationService,
        dialogService, CacheService, $modal, OrganisaatioService, AuthService) {
        $log = $log.getInstance("BaseEditController");

        /*
         * ALL ABSTRACT DATA MODELS FOR KOULUTUS EDIT PAGES
         * LUKIO, KORKEAKOULU etc.
         */

        $scope.koodistoLocale = LocalisationService.getLocale();//"FI";
        $scope.userLanguages = Config.app.userLanguages; // opetuskielien esijärjestystä varten
        $scope.opetuskieli = Config.app.userLanguages[0]; //index 0 = fi uri
        $scope.tmp = {};
        $scope.langs = {};
        $scope.model = {};
        $scope.uiModel = {};
        $scope.lisatiedot = null;
        $scope.now = new Date();
        $scope.controlModel = {
            /*formStatus: {
             modifiedBy: '',
             modified: null,
             tila: ''
             },
             formControls: {}*/
        };

        $scope.controlModelCommandApi = {
            active: false,
            clear: function() {
                throw new Error("Component command link failed : ref not assigned!");
            }
        }; //clear

        /*
         * ALL ABSTRACT FUNCTIONS FOR KOULUTUS EDIT PAGES
         * LUKIO, KORKEAKOULU etc.
         */
        $scope.setModel = function(m) {
            $scope.model = m;
        };

        $scope.getModel = function() {
            return $scope.model;
        };

        $scope.setUiModel = function(m) {
            $scope.uiModel = m;
        };

        $scope.getUiModel = function() {
            return $scope.uiModel;
        };

        $scope.commonCreatePageConfig = function(routeParams, result) {
            if (angular.isDefined(routeParams) && angular.isDefined(routeParams.toteutustyyppi)) {
                //create new
                $scope.CONFIG = {
                    TYYPPI: routeParams.toteutustyyppi,
                    KOULUTUSTYYPPI: routeParams.koulutustyyppi
                };
            } else if (angular.isDefined(result.toteutustyyppi)) {
                //page load
                $scope.CONFIG = {
                    TYYPPI: result.toteutustyyppi,
                    KOULUTUSTYYPPI: result.koulutustyyppi.uri
                };
            } else {
                // error, missing required page type
                throw new Error('Tarjonta application error - missing the page configuration data!');
            }
        };


        $scope.canSaveAsLuonnos = function() {
            if ($scope.getModel().tila !== 'LUONNOS' || $scope.getModel().tila === 'POISTETTU') {
                return false;
            }

            return $scope.uiModel.isMutable;
        };

        $scope.canSaveAsValmis = function() {
            if ($scope.getModel().tila === 'POISTETTU') {
                return false;
            }

            return $scope.uiModel.isMutable;
        };

        $scope.goBack = function(event, form) {
            var dirty = angular.isDefined(form.$dirty) ? form.$dirty : false;

            if (dirty) {
                dialogService.showModifedDialog().result.then(function(result) {
                    if (result) {
                        $scope.navigateBack();
                    }
                });
            } else {
                $scope.navigateBack();
            }
        };

        $scope.navigateBack = function() {
            $log.debug("navigateBack()...");
            $location.path("/");
        };


        $scope.goToReview = function(event, boolInvalid, validationmsgs, form) {
            $log.debug("goToReview()");

            if (angular.isDefined(boolInvalid) && boolInvalid) {
                //ui errors
                return;
            }

            if (angular.isDefined(validationmsgs) && validationmsgs > 0) {
                //server errors
                return;
            }

            var dirty = angular.isDefined(form.$dirty) ? form.$dirty : false;

            if (dirty) {
                dialogService.showModifedDialog().result.then(function(result) {
                    if (result) {
                        $scope.navigateReview();
                    }
                });
            } else {
                $scope.navigateReview();
            }
        };

        $scope.navigateReview = function() {
            $log.debug("navigateReview()");
            $location.path("/koulutus/" + $scope.model.oid);
        };

        $scope.getKuvausApiModelLanguageUri = function(boolIsKomo, textEnum, kieliUri) {
            if (!kieliUri) {
                return {};
            }
            var kuvaus = null;
            if (typeof boolIsKomo !== 'boolean') {
                converter.throwError('An invalid boolean variable : ' + boolIsKomo);
            }

            if (boolIsKomo) {
                kuvaus = $scope.model.kuvausKomo;
            } else {
                kuvaus = $scope.model.kuvausKomoto;
            }

            if (angular.isUndefined(kuvaus) || angular.isUndefined(kuvaus[textEnum])) {
                kuvaus[textEnum] = {tekstis: {}};
                if (!angular.isUndefined(kieliUri)) {
                    kuvaus[textEnum].tekstis[kieliUri] = '';
                }
            }

            return kuvaus[textEnum].tekstis;
        };

        // TODO omaksi direktiivikseen tjsp..
        $scope.kieliFromKoodi = function(koodi) {
            if (angular.isUndefined(koodi) || koodi === null && koodi.length === 0) {
                console.error("invalid language key : '" + koodi + "'");
            }

            return $scope.langs[koodi];
        };

        /**
         * Control page messages.
         *
         * @param {type} uiModel
         * @param {type} action
         * @param {type} errorDetailType
         * @returns {undefined}
         */
        $scope.controlFormMessages = function(koulutusForm, uiModel, action, errorDetailType, apiErrors) {
            if ($scope.controlModelCommandApi.active) {
                $scope.controlModelCommandApi.clear();
            }

            switch (action) {
                case 'SHOW':
                    uiModel.showErrorCheckField = false;
                    uiModel.showValidationErrors = false;
                    uiModel.showError = false;
                    uiModel.showSuccess = false;
                    uiModel.validationmsgs = [];
                    break;
                case 'INIT':
                    uiModel.showErrorCheckField = false;
                    uiModel.showValidationErrors = false;
                    uiModel.showError = false;
                    uiModel.showSuccess = false;
                    uiModel.validationmsgs = [];
                    break;
                case 'CLEAR':
                    //$scope.controlModel.formControls.notifs.errorDetail = [];
                    koulutusForm.$dirty = true;
                    koulutusForm.$invalid = false;
                    uiModel.validationmsgs = [];
                    uiModel.showValidationErrors = false;
                    uiModel.showError = false;
                    uiModel.showSuccess = false;
                    break;
                case 'SAVED':
                    uiModel.showErrorCheckField = false;
                    uiModel.showError = false;
                    uiModel.showValidationErrors = true;
                    uiModel.hakukohdeTabsDisabled = false;
                    uiModel.validationmsgs = [];
                    //Form
                    koulutusForm.$dirty = false;
                    koulutusForm.$invalid = false;
                    uiModel.showSuccess = true;
                    break;
                case 'ERROR':
                default:
                    uiModel.showErrorCheckField = errorDetailType === 'UI_ERRORS'
                    uiModel.showValidationErrors = true;
                    uiModel.showError = true;
                    uiModel.showSuccess = false;

                    if (!angular.isUndefined(apiErrors)) {
                        for (var i = 0; i < apiErrors.length; i++) {
                            uiModel.validationmsgs.push(apiErrors[i].errorMessageKey);
                        }
                    }
                    break;
            }
        };

        $scope.isLoaded = function() {
            return  !angular.isUndefined($scope.model.oid) && $scope.model.oid !== null && $scope.model.oid.length > 0;
        };

        $scope.getLang = function(tekstis) {
            if (angular.isUndefined(tekstis) || tekstis === null) {
                return "";
            }

            var fallbackFi = "";

            for (var key in tekstis) {
                if (key.indexOf($scope.koodistoLocale) !== -1) {
                    return tekstis[key];
                }

                if (key.indexOf("fi") !== -1) {
                    fallbackFi = tekstis[key];
                }
            }

            return fallbackFi;
        };

        $scope.saveByStatus = function(tila, form, tyyppi, fnCustomCallbackAfterSave) {
            $scope.saveApimodelByStatus(angular.copy($scope.model), tila, form, tyyppi, fnCustomCallbackAfterSave);
        };

        $scope.saveApimodelByStatus = function(apiModel, tila, form, tyyppi, fnCustomCallbackAfterSave) {
            if (angular.isUndefined(tila)) {
                converter.throwError('Undefined tila');
            }

            if (apiModel.tila !== "JULKAISTU") { //julkaistua tallennettaessa tila ei muutu
                apiModel.tila = tila;
            }

            $scope.saveByStatusAndApiObject(
                form,
                tyyppi,
                fnCustomCallbackAfterSave,
                converter.saveModelConverter(apiModel, $scope.uiModel, tyyppi)
                );
        };

        $scope.saveByStatusAndApiObject = function(form, tyyppi, fnCustomCallbackAfterSave, apiModelReadyForSave) {
            $scope.controlFormMessages(form, $scope.uiModel, "CLEAR");


            if (form.$invalid || !form.$valid || (form.$pristine && !$scope.isLoaded())) {
                //invalid form data
                $scope.controlFormMessages(form, $scope.uiModel, "ERROR", "UI_ERRORS");
                return;
            }

            PermissionService.permissionResource().authorize({}, function(authResponse) {
                $log.debug("Authorization check : " + authResponse.result);

                if (authResponse.status !== 'OK') {
                    //not authenticated
                    $scope.controlFormMessages(form, $scope.uiModel, "ERROR", ["koulutus.error.auth"]);
                    return;
                }

                var KoulutusRes = TarjontaService.koulutus();

                KoulutusRes.save(apiModelReadyForSave, function(saveResponse) {
                    var model = saveResponse.result;

                    if (saveResponse.status === 'OK') {

                        // Reset form to "pristine" ($dirty = false)
                        // WTF? where have all the "form.$setPristine()"s gone?
                        form.$dirty = false;
                        form.$pristine = true;

                        $scope.model = model;

                        //$scope.updateFormStatusInformation($scope.model);
                        $scope.controlFormMessages(form, $scope.uiModel, "SAVED");
                        $scope.uiModel.tabs.lisatiedot = false;
                        $scope.lisatiedot = converter.STRUCTURE[tyyppi].KUVAUS_ORDER;
                        // OVT-7421 / etusivun hakutuloskakun tyhjentäminen jotta muutokset näkyvät varmasti hakutuloslissa
                        // - parempi ratkaisu olisi toki tallentaa muutokset kakutettuihin hakutuloksiin, jos sellaisia on
                        CacheService.evict(new RegExp("/koulutus/.*"));
                    } else {
                        $scope.controlFormMessages(form, $scope.uiModel, "ERROR", null, saveResponse.errors);
                    }

                    fnCustomCallbackAfterSave(saveResponse);
                });
            });
        };

        $scope.commonKoodistoLoadHandler = function(uiModel, tyyppi) {
            angular.forEach(converter.STRUCTURE[tyyppi].COMBO, function(value, key) {
                if (angular.isUndefined(value.skipUiModel)) {
                    var koodisPromise = Koodisto.getAllKoodisWithKoodiUri(Config.env[value.koodisto], $scope.koodistoLocale);
                    uiModel[key].promise = koodisPromise;
                    koodisPromise.then(function(result) {
                        uiModel[key].koodis = result;
                    });
                }
            });
            angular.forEach(converter.STRUCTURE[tyyppi].MCOMBO, function(value, key) {
                if (angular.isUndefined(Config.env[value.koodisto])) {
                    throw new Error("No koodisto URI for key : " + key + ", property : '" + value.koodisto + "'");
                }

                var koodisPromise = Koodisto.getAllKoodisWithKoodiUri(Config.env[value.koodisto], $scope.koodistoLocale);
                uiModel[key].promise = koodisPromise;

                koodisPromise.then(function(result) {
                    //store all koodisto koodi objects for save
                    uiModel[key].koodis = result;
                    if (value.koodisto === 'koodisto-uris.kieli') {
                        //store the language uris to map object
                        for (var i in result) {
                            $scope.langs[result[i].koodiUri] = result[i].koodiNimi;
                        }
                    }
                });
            });
        };

        $scope.commonNewModelHandler = function(form, model, uiModel, tyyppi) {

            if (angular.isUndefined(model) || model === null) {
                converter.throwError("Model object cannot be null or undefined");
            }

            if (angular.isUndefined(uiModel) || uiModel === null) {
                converter.throwError("UI model object cannot be null or undefined");
            }

            if (angular.isUndefined(tyyppi) || tyyppi === null) {
                converter.throwError("KoulutusasteTyyppi string enum cannot be null or undefined");
            }

            uiModel.isMutable = false;
            uiModel.selectedKieliUri = undefined; // pitää olla undefined koska mktabs (ks. api)
            $scope.lisatiedot = converter.STRUCTURE[tyyppi].KUVAUS_ORDER;

            converter.createUiModels(uiModel, tyyppi);
            uiModel.isMutable = true;
            model.isNew = true;
            $scope.controlFormMessages(form, uiModel, "INIT");
            converter.createAPIModel(model, Config.app.userLanguages, tyyppi);

            if ( $routeParams.opetusTarjoajat ) {
                model.opetusTarjoajat = $routeParams.opetusTarjoajat.split(',');
                $scope.initOpetustarjoajat(model);
            }

            if (angular.isDefined($routeParams.org) || (angular.isDefined(model.organisaatio) && angular.isDefined(model.organisaatio.oid))) {
                var promiseOrg = organisaatioService.nimi(angular.isDefined($routeParams.org) ? $routeParams.org : model.organisaatio.oid);
                promiseOrg.then(function(vastaus) {
                    converter.updateOrganisationApiModel(model, $routeParams.org, vastaus);
                });
            }
        };

        $scope.commonLoadModelHandler = function(form, model, uiModel, tyyppi) {
            if (angular.isUndefined(model) || model === null) {
                converter.throwError("Model object cannot be null or undefined");
            }

            if (angular.isUndefined(uiModel) || uiModel === null) {
                converter.throwError("UI model object cannot be null or undefined");
            }

            if (angular.isUndefined(tyyppi) || tyyppi === null) {
                converter.throwError("Tyyppi string enum cannot be null or undefined");
            }

            uiModel.isMutable = false;
            uiModel.selectedKieliUri = undefined; // pitää olla undefined koska mktabs (ks. api)
            $scope.lisatiedot = converter.STRUCTURE[tyyppi].KUVAUS_ORDER;

            converter.createUiModels(uiModel, tyyppi);
            $scope.controlFormMessages(form, uiModel, "SHOW");

            if (angular.isUndefined(model) || model === null) {
                $location.path("/error");
                return;
            }

            if (model.tila === 'POISTETTU') {
                uiModel.isMutable = false;
            }

            PermissionService.koulutus.canEdit(model.oid, {
                defaultTarjoaja: AuthService.getUserDefaultOid()
            }).then(function(data) {
                $log.debug("setting mutable to:", data);
                uiModel.isMutable = data;

                if (model.toteutustyyppi === 'LUKIOKOULUTUS') {
                    //TODO: poista tama kun nuorten lukiokoulutus on toteutettu!
                    if (angular.isDefined(uiModel.loadedKoulutuslaji) &&
                        KoodistoURI.compareKoodi(
                            uiModel.loadedKoulutuslaji.uri,
                            Config.env['koodi-uri.koulutuslaji.nuortenKoulutus'], true)) {

                        uiModel.isMutable = false;
                        uiModel.isRemovable = false;
                    }
                }
            });

            uiModel.tabs.lisatiedot = false; //activate lisatiedot tab
            //$scope.updateFormStatusInformation(model);

            angular.forEach(model.yhteyshenkilos, function(value, key) {
                if (value.henkiloTyyppi === 'YHTEYSHENKILO') {
                    uiModel.contactPerson = converter.converPersonObjectForUi(value);
                } else if (value.henkiloTyyppi === 'ECTS_KOORDINAATTORI') {
                    uiModel.ectsCoordinator = converter.converPersonObjectForUi(value);
                } else {
                    converter.throwError('Undefined henkilotyyppi : ', value);
                }
            });

            /*
             * Load data to mltiselect fields
             * remove version data from the list
             */
            angular.forEach(converter.STRUCTURE[tyyppi].MCOMBO, function(value, key) {
                if (angular.isDefined(model[key])) {
                    if (angular.isDefined(value.types)) {
                        uiModel[key] = {};
                        angular.forEach(value.types, function(type) {
                            uiModel[key][type] = {uris: []};
                            if (angular.isDefined(model[key][type])) {
                                uiModel[key][type].uris = _.keys(model[key][type].uris);
                            }
                        });
                    } else if (angular.isDefined(model[key].uris)) {
                        uiModel[key].uris = _.keys(model[key].uris);
                    }
                } else {
                    console.error("invalid key mapping : ", key);
                }
            });

            $scope.initOpetustarjoajat(model);
        };

        /*
         * LISATIEDOT PAGE FUNCTIONS
         */


        /**
         * Try to find all language uris for textarea objects.
         * Set founded uris to uiModel.lisatietoKielet property.
         *
         * @param {type} model
         * @param {type} uiModel
         */
        $scope.getLisatietoKielet = function(model, uiModel, requireKomoTexts) {
            var arrLanguageUris = [];
            if (model.kuvausKomoto) {
                angular.forEach(model.kuvausKomoto, function(tekstis, key) {
                    angular.forEach(tekstis, function(value, key) {
                        if (key === 'tekstis') {
                            arrLanguageUris = arrLanguageUris.concat(_.keys(value));
                        }
                    });
                });
            }

             if (requireKomoTexts && model.kuvausKomo) {
                angular.forEach(model.kuvausKomo, function(tekstis, key) {
                    angular.forEach(tekstis, function(value, key) {
                        if (key === 'tekstis') {
                            arrLanguageUris = arrLanguageUris.concat(_.keys(value));
                        }
                    });
                });
            }

            if (model.opetuskielis && model.opetuskielis.uris) {
                arrLanguageUris = arrLanguageUris.concat(_.keys(model.opetuskielis.uris));
            }

            if(!angular.isDefined(uiModel.lisatietoKielet)){
                uiModel.lisatietoKielet = [];
            }

            return uiModel.lisatietoKielet = _.uniq(uiModel.lisatietoKielet.concat(arrLanguageUris));
        };

        $scope.getRakenneKuvaModel = function(kieliUri) {
            if (kieliUri === null || angular.isUndefined(kieliUri) || kieliUri === Object(kieliUri)) {
                return kieliUri;
            }

            var ret = $scope.model.opintojenRakenneKuvas[kieliUri];
            if (!ret) {
                ret = {};
                $scope.model.opintojenRakenneKuvas[kieliUri] = ret;
            }

            return ret;
        };

        $scope.onLisatietoLangSelection = function(uris, tyyppi) {
            if (uris.removed && $scope.uiModel.opetuskielis.uris) {
                // ei opetuskieli -> varmista poisto dialogilla
                dialogService.showDialog({
                    ok: LocalisationService.t("tarjonta.poistovahvistus.koulutus.lisatieto.poista"),
                    title: LocalisationService.t("tarjonta.poistovahvistus.koulutus.lisatieto.title"),
                    description: LocalisationService.t("tarjonta.poistovahvistus.koulutus.lisatieto", [$scope.langs[uris.removed]])
                }).result.then(function(ret) {
                    if (ret) {
                        $scope.deleteKuvausByStructureType(tyyppi, uris.removed);
                    } else {
                        //cancel remove, put back the uri
                        if ($scope.uiModel.lisatietoKielet.indexOf(uris.removed) === -1) {
                            $scope.uiModel.lisatietoKielet.push(uris.removed);
                        }
                    }
                });
            } else if (uris.added && $scope.uiModel.lisatietoKielet) {
                if ($scope.uiModel.lisatietoKielet.indexOf(uris.added) === -1) {
                    $scope.uiModel.lisatietoKielet.push(uris.added);
                }
            }
        };

        $scope.isTutkintoOhjelmaKoodisto = function(tarjontaKoodistoObj) {
            return window.CONFIG.env["koodisto-uris.koulutusohjelma"] === tarjontaKoodistoObj.koodiKoodisto ||
                window.CONFIG.env["koodisto-uris.lukiolinja"] === tarjontaKoodistoObj.koodiKoodisto ||
                window.CONFIG.env["koodisto-uris.osaamisala"] === tarjontaKoodistoObj.koodiKoodisto;
        };

        $scope.deleteKuvausByStructureType = function(tyyppi, kieliUri) {
            angular.forEach(converter.STRUCTURE[tyyppi].KUVAUS_ORDER, function(value, key) {
                //null = text delete flag in service
                if (value.isKomo) {
                    $scope.model.kuvausKomo[value.type].tekstis[kieliUri] = null;
                } else {
                    $scope.model.kuvausKomoto[value.type].tekstis[kieliUri] = null;
                }
            });
        };

        /**
         * tarjonta-service ei pidä yllä tarjoajien järjestystä, mistä syystä
         * tästä pidetään huolta alla olevan $watchin avulla (eli varmistetaan,
         * että se organisaatio joka loi koulutuksen, on aina taulukon ensimmäisenä).
         */
        $scope.$watch('model.opetusTarjoajat', function() {
           $scope.initOpetustarjoajat();
        });

        $scope.initOpetustarjoajat = function(model) {
            model = model || $scope.model;

            var shouldBeFirst = null;
            if (model.organisaatio) {
                shouldBeFirst = model.organisaatio.oid;
            }
            else if (model.opetusTarjoajat) {
                shouldBeFirst = model.opetusTarjoajat[0];
            }

            OrganisaatioService.getPopulatedOrganizations(model.opetusTarjoajat, shouldBeFirst)
            .then(function(orgs) {
                $scope.model.organisaatiot = orgs;
                var nimet = "";
                angular.forEach(orgs, function(org) {
                    nimet += " | " + org.nimi;
                });

                $scope.model.organisaatioidenNimet = nimet.substring(3);
            });
        };

        $scope.editOrganizations = function() {
            $scope.selectedOrganizations = [];
            angular.forEach($scope.model.organisaatiot, function(org) {
                $scope.selectedOrganizations.push(org);
            });
            $scope.organizationSelectionDialog = $modal.open({
                scope: $scope,
                templateUrl: 'partials/koulutus/organization-selection.html',
                controller: 'OrganizationSelectionController'
            });
        };

        return $scope;
    }
]);
