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
    'HakukohdeKoulutukses',
    function BaseEditController($scope, $log, Config,
        $routeParams, $route, $location,
        KoulutusConverterFactory, TarjontaService, PermissionService,
        organisaatioService, Koodisto, KoodistoURI, LocalisationService,
        dialogService, CacheService, $modal, OrganisaatioService, AuthService,
        HakukohdeKoulutukses) {

        $log = $log.getInstance("BaseEditController");

        var ENUMS = KoulutusConverterFactory.ENUMS;

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
            clear: function () {
                throw new Error("Component command link failed : ref not assigned!");
            }
        }; //clear

        /*
         * ALL ABSTRACT FUNCTIONS FOR KOULUTUS EDIT PAGES
         * LUKIO, KORKEAKOULU etc.
         */
        $scope.setModel = function (m) {
            $scope.model = m;
        };

        $scope.getModel = function () {
            return $scope.model;
        };

        $scope.setUiModel = function (m) {
            $scope.uiModel = m;
        };

        $scope.getUiModel = function () {
            return $scope.uiModel;
        };

        $scope.commonCreatePageConfig = function (routeParams, result) {
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


        $scope.canSaveAsLuonnos = function () {
            return _.contains(['LUONNOS', 'KOPIOITU'], $scope.getModel().tila) && $scope.uiModel.isMutable;
        };

        $scope.canSaveAsValmis = function () {
            if ($scope.getModel().tila === 'POISTETTU') {
                return false;
            }

            return $scope.uiModel.isMutable;
        };

        $scope.goBack = function (event, form) {
            if ($scope.isDirty()) {
                dialogService.showModifedDialog().result.then(function (result) {
                    if (result) {
                        $scope.navigateBack();
                    }
                });
            } else {
                $scope.navigateBack();
            }
        };

        $scope.navigateBack = function () {
            $log.debug("navigateBack()...");
            $location.path("/");
        };

        $scope.isDirty = function() {
            var currentModel = KoulutusConverterFactory.saveModelConverter(
                angular.copy($scope.model),
                angular.copy($scope.uiModel),
                $scope.CONFIG.TYYPPI
            );

            return $scope.modelInitialState && !_.isEqual(currentModel, $scope.modelInitialState);
        };


        /**
         * Tallenna modelin tila ennen käyttäjän tekemiä muutoksia, jotta
         * voidaan tarvittaessa ilmoittaa tallentamattomista tiedoista jne.
         */
        $scope.setDirtyListener = function() {
            $('form[name="koulutusForm"]').on('focus click', '*', function(e) {
                e.stopPropagation();
                if (!$scope.modelInitialState) {
                    $scope.modelInitialState = KoulutusConverterFactory.saveModelConverter(
                        angular.copy($scope.model),
                        angular.copy($scope.uiModel),
                        $scope.CONFIG.TYYPPI
                    );
                }
            });
        };

        $scope.goToReview = function (event, boolInvalid, validationmsgs, form) {
            $log.debug("goToReview()");

            if (angular.isDefined(boolInvalid) && boolInvalid) {
                //ui errors
                return;
            }

            if (angular.isDefined(validationmsgs) && validationmsgs > 0) {
                //server errors
                return;
            }

            if ($scope.isDirty()) {
                dialogService.showModifedDialog().result.then(function (result) {
                    if (result) {
                        $scope.navigateReview();
                    }
                });
            } else {
                $scope.navigateReview();
            }
        };

        $scope.navigateReview = function () {
            $log.debug("navigateReview()");
            $location.path("/koulutus/" + $scope.model.oid);
        };

        $scope.getKuvausApiModelLanguageUri = function (boolIsKomo, textEnum, kieliUri) {
            if (!kieliUri) {
                return {};
            }
            var kuvaus = null;
            if (typeof boolIsKomo !== 'boolean') {
                KoulutusConverterFactory.throwError('An invalid boolean variable : ' + boolIsKomo);
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
        $scope.kieliFromKoodi = function (koodi) {
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
        $scope.controlFormMessages = function (koulutusForm, uiModel, action, errorDetailType, apiErrors) {
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

                default:
                case 'ERROR':
                    uiModel.showErrorCheckField = errorDetailType === 'UI_ERRORS';
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

        $scope.isLoaded = function () {
            return  !angular.isUndefined($scope.model.oid) && $scope.model.oid !== null && $scope.model.oid.length > 0;
        };

        $scope.getLang = function (tekstis) {
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

        $scope.saveByStatus = function (tila, form, tyyppi, fnCustomCallbackAfterSave) {
            $scope.saveApimodelByStatus(angular.copy($scope.model), tila, form, tyyppi, fnCustomCallbackAfterSave);
        };

        $scope.saveApimodelByStatus = function (apiModel, tila, form, tyyppi, fnCustomCallbackAfterSave) {
            if (angular.isUndefined(tila)) {
                KoulutusConverterFactory.throwError('Undefined tila');
            }

            if (apiModel.tila !== "JULKAISTU") { //julkaistua tallennettaessa tila ei muutu
                apiModel.tila = tila;
            }

            $scope.saveByStatusAndApiObject(
                form,
                tyyppi,
                fnCustomCallbackAfterSave,
                KoulutusConverterFactory.saveModelConverter(apiModel, $scope.uiModel, tyyppi)
                );

            if ($scope.uiModel.isMutable && $scope.model.oid) {
                var hakukohdePromise = HakukohdeKoulutukses.getKoulutusHakukohdes($scope.model.oid);
                hakukohdePromise.then(function(hakukohteet) {
                	if (hakukohteet.result && hakukohteet.result.length > 0) {
                		$scope.setMinMax(true);
                	} else {
                		$scope.setMinMax(false);
                	}
                }, function() {
                	$scope.setMinMax(false);
                });
			}
        };

        $scope.saveByStatusAndApiObject = function (form, tyyppi, fnCustomCallbackAfterSave, apiModelReadyForSave) {
            $scope.controlFormMessages(form, $scope.uiModel, "CLEAR");


            if (form.$invalid || !form.$valid || (form.$pristine && !$scope.isLoaded())) {
                //invalid form data
                $scope.controlFormMessages(form, $scope.uiModel, "ERROR", "UI_ERRORS");
                return;
            }

            PermissionService.permissionResource().authorize({}, function (authResponse) {
                $log.debug("Authorization check : " + authResponse.result);

                if (authResponse.status !== 'OK') {
                    //not authenticated
                    $scope.controlFormMessages(form, $scope.uiModel, "ERROR", ["koulutus.error.auth"]);
                    return;
                }

                var KoulutusRes = TarjontaService.koulutus();

                KoulutusRes.save(apiModelReadyForSave, function (saveResponse) {
                    var model = saveResponse.result;

                    if (saveResponse.status === 'OK') {

                        // Reset form to "pristine" ($dirty = false)
                        // WTF? where have all the "form.$setPristine()"s gone?
                        form.$dirty = false;
                        form.$pristine = true;

                        // Tyhjennä initial state, jotta sille asetettaisiin
                        // uusi arvo kun käyttäjä tekee jotain formille
                        $scope.modelInitialState = null;

                        $scope.model = model;

                        //$scope.updateFormStatusInformation($scope.model);
                        $scope.controlFormMessages(form, $scope.uiModel, "SAVED");
                        $scope.uiModel.tabs.lisatiedot = false;
                        // OVT-7421 / etusivun hakutuloskakun tyhjentäminen jotta muutokset näkyvät varmasti hakutuloslissa
                        // - parempi ratkaisu olisi toki tallentaa muutokset kakutettuihin hakutuloksiin, jos sellaisia on
                        CacheService.evict(new RegExp("/koulutus/.*"));
                    } else {
                        $scope.controlFormMessages(form, $scope.uiModel, "ERROR", null, saveResponse.errors);
                    }

                    if (fnCustomCallbackAfterSave) {
                        fnCustomCallbackAfterSave(saveResponse);
                    }
                });
            });
        };

        $scope.commonKoodistoLoadHandler = function (uiModel, tyyppi) {
            angular.forEach(KoulutusConverterFactory.STRUCTURE[tyyppi].COMBO, function (value, key) {
                if (angular.isUndefined(value.skipUiModel)) {
                    var koodisPromise = Koodisto.getAllKoodisWithKoodiUri(Config.env[value.koodisto], $scope.koodistoLocale);
                    uiModel[key].promise = koodisPromise;
                    koodisPromise.then(function (result) {
                        uiModel[key].koodis = result;
                    });
                }
            });
            angular.forEach(KoulutusConverterFactory.STRUCTURE[tyyppi].MCOMBO, function (value, key) {
                if (angular.isUndefined(Config.env[value.koodisto])) {
                    throw new Error("No koodisto URI for key : " + key + ", property : '" + value.koodisto + "'");
                }

                var koodisPromise = Koodisto.getAllKoodisWithKoodiUri(Config.env[value.koodisto], $scope.koodistoLocale);
                uiModel[key].promise = koodisPromise;

                koodisPromise.then(function (result) {
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

        $scope.commonNewModelHandler = function (form, model, uiModel, tyyppi) {

            if (angular.isUndefined(model) || model === null) {
                KoulutusConverterFactory.throwError("Model object cannot be null or undefined");
            }

            if (angular.isUndefined(uiModel) || uiModel === null) {
                KoulutusConverterFactory.throwError("UI model object cannot be null or undefined");
            }

            if (angular.isUndefined(tyyppi) || tyyppi === null) {
                KoulutusConverterFactory.throwError("KoulutusasteTyyppi string enum cannot be null or undefined");
            }

            uiModel.isMutable = false;
            uiModel.selectedKieliUri = undefined; // pitää olla undefined koska mktabs (ks. api)

            KoulutusConverterFactory.createUiModels(uiModel, tyyppi);
            uiModel.isMutable = true;
            model.isNew = true;
            $scope.controlFormMessages(form, uiModel, "INIT");
            KoulutusConverterFactory.createAPIModel(model, Config.app.userLanguages, tyyppi);

            if ($routeParams.opetusTarjoajat) {
                model.opetusTarjoajat = $routeParams.opetusTarjoajat.split(',');
                $scope.initOpetustarjoajat(model);
            }

            if (angular.isDefined($routeParams.org) || (angular.isDefined(model.organisaatio) && angular.isDefined(model.organisaatio.oid))) {
                var promiseOrg = OrganisaatioService.nimi(angular.isDefined($routeParams.org) ? $routeParams.org : model.organisaatio.oid);
                promiseOrg.then(function (vastaus) {
                    KoulutusConverterFactory.updateOrganisationApiModel(model, $routeParams.org, vastaus);
                });
            }
        };

        $scope.commonLoadModelHandler = function (form, model, uiModel, tyyppi) {
            if (angular.isUndefined(model) || model === null) {
                KoulutusConverterFactory.throwError("Model object cannot be null or undefined");
            }

            if (angular.isUndefined(uiModel) || uiModel === null) {
                KoulutusConverterFactory.throwError("UI model object cannot be null or undefined");
            }

            if (angular.isUndefined(tyyppi) || tyyppi === null) {
                KoulutusConverterFactory.throwError("Tyyppi string enum cannot be null or undefined");
            }

            uiModel.isMutable = false;
            uiModel.selectedKieliUri = undefined; // pitää olla undefined koska mktabs (ks. api)

            KoulutusConverterFactory.createUiModels(uiModel, tyyppi);
            $scope.controlFormMessages(form, uiModel, "SHOW");

            if (angular.isUndefined(model) || model === null) {
                $location.path("/error");
                return;
            }

            if (model.tila === 'POISTETTU') {
                uiModel.isMutable = false;
            }

            if ( model.oid ) {
                PermissionService.koulutus.canEdit(model.oid, {
                    defaultTarjoaja: AuthService.getUserDefaultOid()
                }).then(function (data) {
                    uiModel.isMutable = data;

                    if (uiModel.isMutable) {
                        model.isNew = false;

                        var hakukohdePromise = HakukohdeKoulutukses.getKoulutusHakukohdes(model.oid);
                        hakukohdePromise.then(function(hakukohteet) {
                            if (hakukohteet.result && hakukohteet.result.length > 0) {
                                $scope.setMinMax(true);
                            } else {
                                $scope.setMinMax(false);
                            }
                        }, function() {
                            $scope.setMinMax(false);
                        });
                    } // if (uiModel.isMutable)
                }); // koulutus.canEdit.then
            }

            uiModel.tabs.lisatiedot = false; //activate lisatiedot tab
            //$scope.updateFormStatusInformation(model);

            angular.forEach(model.yhteyshenkilos, function (value, key) {
                if (value.henkiloTyyppi === 'YHTEYSHENKILO') {
                    uiModel.contactPerson = KoulutusConverterFactory.converPersonObjectForUi(value);
                } else if (value.henkiloTyyppi === 'ECTS_KOORDINAATTORI') {
                    uiModel.ectsCoordinator = KoulutusConverterFactory.converPersonObjectForUi(value);
                } else {
                    KoulutusConverterFactory.throwError('Undefined henkilotyyppi : ', value);
                }
            });

            /*
             * Load data to mltiselect fields
             * remove version data from the list
             */
            angular.forEach(KoulutusConverterFactory.STRUCTURE[tyyppi].MCOMBO, function (value, key) {
                if (angular.isDefined(model[key])) {
                    if (angular.isDefined(value.types)) {
                        uiModel[key] = {};
                        angular.forEach(value.types, function (type) {
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
        $scope.getLisatietoKielet = function (model, uiModel, requireKomoTexts) {
            var arrLanguageUris = [];
            if (model.kuvausKomoto) {
                angular.forEach(model.kuvausKomoto, function (tekstis, key) {
                    angular.forEach(tekstis, function (value, key) {
                        if (key === 'tekstis') {
                            arrLanguageUris = arrLanguageUris.concat(_.keys(value));
                        }
                    });
                });
            }

            if (requireKomoTexts && model.kuvausKomo) {
                angular.forEach(model.kuvausKomo, function (tekstis, key) {
                    angular.forEach(tekstis, function (value, key) {
                        if (key === 'tekstis') {
                            arrLanguageUris = arrLanguageUris.concat(_.keys(value));
                        }
                    });
                });
            }

            if (model.opetuskielis && model.opetuskielis.uris) {
                arrLanguageUris = arrLanguageUris.concat(_.keys(model.opetuskielis.uris));
            }

            if (!angular.isDefined(uiModel.lisatietoKielet)) {
                uiModel.lisatietoKielet = [];
            }

            uiModel.lisatietoKielet = _.uniq(uiModel.lisatietoKielet.concat(arrLanguageUris));

            return uiModel.lisatietoKielet;
        };

        $scope.getRakenneKuvaModel = function (kieliUri) {
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

        $scope.onLisatietoLangSelection = function (uris, tyyppi) {
            if (uris.removed && $scope.uiModel.opetuskielis.uris) {
                // ei opetuskieli -> varmista poisto dialogilla
                dialogService.showDialog({
                    ok: LocalisationService.t("tarjonta.poistovahvistus.koulutus.lisatieto.poista"),
                    title: LocalisationService.t("tarjonta.poistovahvistus.koulutus.lisatieto.title"),
                    description: LocalisationService.t("tarjonta.poistovahvistus.koulutus.lisatieto", [$scope.langs[uris.removed]])
                }).result.then(function (ret) {
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

        $scope.isTutkintoOhjelmaKoodisto = function (tarjontaKoodistoObj) {
            return window.CONFIG.env["koodisto-uris.koulutusohjelma"] === tarjontaKoodistoObj.koodiKoodisto ||
                window.CONFIG.env["koodisto-uris.lukiolinja"] === tarjontaKoodistoObj.koodiKoodisto ||
                window.CONFIG.env["koodisto-uris.osaamisala"] === tarjontaKoodistoObj.koodiKoodisto;
        };

        $scope.deleteKuvausByStructureType = function (tyyppi, kieliUri) {
            angular.forEach(KoulutusConverterFactory.STRUCTURE[tyyppi].KUVAUS_ORDER, function (value, key) {
                //null = text delete flag in service
                if (value.isKomo) {
                    $scope.model.kuvausKomo[value.type].tekstis[kieliUri] = null;
                } else {
                    $scope.model.kuvausKomoto[value.type].tekstis[kieliUri] = null;
                }
            });
        };

        /**
         * Refaktorointi: aiempien, melkein identtisten koulutusControllerien toiminnallisuus
         * siirrettiin tänne ja jätettiin yksittäisiin controllereihin vain niille spesifiset koodit
         */


        /**
         * Tämä funktio palauttaa koodiston arvoista vain ne, joista
         * on olemassa koulutusmoduuli tietokannassa.
         */
        function filterByKomos(koodistoResult, komos, compareField) {
            var tutkintoModules = {};

            angular.forEach(komos.result, function (komo) {
                var key = komo[compareField];

                if (koodistoResult.map[key]) {
                    tutkintoModules[key] = angular.extend(koodistoResult.map[key], {
                        oid: komo.oid,
                        koulutuskoodi: komo.koulutuskoodiUri
                    });
                }
            });

            return tutkintoModules;
        }

        $scope.init = function (initValues, initFunction) {
            $log.debug("init");

            initValues = initValues || {};

            /*
             * INITIALIZE PAGE CONFIG
             */
            $scope.commonCreatePageConfig($routeParams, $route.current.locals.koulutusModel.result);

            var model = initValues.model || {};
            var uiModel = initValues.uiModel || {};

            // Need to access the child scope in some situations
            $scope.childScope = initValues.childScope || $log.error("childScope not initialized");

            var koulutusStructure = KoulutusConverterFactory.STRUCTURE[$scope.CONFIG.TYYPPI];
            $scope.koulutusStructure = koulutusStructure;

            if (koulutusStructure.params && koulutusStructure.params.onlyOneOpetuskieli) {
                $scope.$watch('uiModel.opetuskielis.uris.length', function (count) {
                    if (count <= 1) {
                        return;
                    }
                    dialogService.showDialog({
                        ok: LocalisationService.t("ok"),
                        cancel: '',
                        title: LocalisationService.t("koulutus.edit.opetuskieli.vainYksi.dialog.title"),
                        description: LocalisationService.t("koulutus.edit.opetuskieli.vainYksi.dialog.kuvaus")
                    });
                    $scope.uiModel.opetuskielis.uris.splice(1,1);
                });
            }

            /*
             * HANDLE EDIT / CREATE NEW ROUTING
             */
            if ($routeParams.id) {
                /*
                 *  SHOW KOULUTUS BY GIVEN KOMOTO OID
                 *  Look more info from koulutusController.js.
                 */
                model = $route.current.locals.koulutusModel.result;
                uiModel.loadedKoulutuslaji = angular.copy(model.koulutuslaji);
                $scope.commonLoadModelHandler($scope.koulutusForm, model, uiModel, $scope.CONFIG.TYYPPI);
                $scope.loadKomoKuvausTekstis(null, model.kuvausKomo);
            }
            else if ($routeParams.org) {
                /*
                 * CREATE NEW KOULUTUS BY ORG OID AND KOULUTUSKOODI
                 * Look more info from koulutusController.js.
                 */
                $scope.commonNewModelHandler($scope.koulutusForm, model, uiModel, $scope.CONFIG.TYYPPI);

                /*
                 * CUSTOM LOGIC : LOAD KOULUTUSKOODI + LUKIOLINJA KOODI OBJECTS
                 */
                var resource = TarjontaService.komo();
                var tutkintoPromise = Koodisto.getYlapuolisetKoodiUrit([$scope.CONFIG.KOULUTUSTYYPPI], 'koulutus', $scope.koodistoLocale);
                tutkintoPromise.then(function (koodistoResult) {
                    resource.searchModules({
                        koulutustyyppi: $scope.CONFIG.KOULUTUSTYYPPI,
                        moduuli: ENUMS.ENUM_KOMO_MODULE_TUTKINTO
                    }, function (komos) {
                        uiModel.tutkintoModules = filterByKomos(koodistoResult, komos, 'koulutuskoodiUri');
                        uiModel.tutkinto = _.map(uiModel.tutkintoModules, function (num, key) {
                            return num;
                        });
                    });
                });
            }
            else {
                KoulutusConverterFactory.throwError('unsupported $routeParams.type : ' + $routeParams.type + '.');
            }

            $scope.lisatiedot = koulutusStructure.KUVAUS_ORDER;

            var koulutuskoodiUri = (model.koulutuskoodi || {}).uri || $routeParams.koulutuskoodi;
            koulutuskoodiUri && $scope.loadRelationKoodistoData(
                model,
                uiModel,
                koulutuskoodiUri,
                ENUMS.ENUM_KOMO_MODULE_TUTKINTO
            );

            (model.koulutusohjelma || {}).uri && $scope.loadRelationKoodistoData(
                model,
                uiModel,
                model.koulutusohjelma.uri,
                ENUMS.ENUM_KOMO_MODULE_TUTKINTO_OHJELMA
            );

            /*
             * SHOW ALL KOODISTO KOODIS
             */
            $scope.commonKoodistoLoadHandler(uiModel, $scope.CONFIG.TYYPPI);

            /*
             * CUSTOM LOGIC
             */
            // lisätietokielivalinnat
            $scope.getLisatietoKielet(model, uiModel, true);

            /*
             * INIT SCOPES FOR RENDERER IN koulutusController.js
             */
            model.toteutustyyppi = $scope.CONFIG.TYYPPI;

            angular.extend(uiModel, $scope.uiModel);
            $scope.setUiModel(uiModel);
            $scope.setModel(model);

            $scope.setDirtyListener();

            // Child edit controllerit voivat suorittaa tarvittaessa oman init-funktion
            if (initFunction) {
                initFunction();
            }

            // Myös koulutusConverterissa voi ajaa initin. Tämä hack tarvittiin
            // valmentavaa ja kuntouttavaa opetusta varten. Tästä olisi hyvä päästä eroon,
            // ja tehdä koodista tämän myötä selkeämpi.
            if (koulutusStructure.initFunction) {
                koulutusStructure.initFunction($scope);
            }
        };

        $scope.loadKomoKuvausTekstis = function (komoOid, kuvausKomoto) {
            function setUiModelTexts(komoKomoto) {
                var mapping = {
                    kuvausTavoite: 'TAVOITTEET',
                    kuvausOpintojenRakenne: 'KOULUTUKSEN_RAKENNE',
                    jatkoOpintomahdollisuudet: 'JATKOOPINTO_MAHDOLLISUUDET',
                    koulutusohjelmanTavoitteet: 'KOULUTUSOHJELMAN_TAVOITTEET'
                };

                angular.forEach(mapping, function (keyLabelInApiResponse, key) {
                    if (angular.isDefined(komoKomoto[keyLabelInApiResponse])) {
                        $scope.uiModel[key] = $scope.getLang(komoKomoto[keyLabelInApiResponse].tekstis);
                    }
                });
            }

            if (angular.isDefined(kuvausKomoto) && komoOid === null && kuvausKomoto) {
                setUiModelTexts(kuvausKomoto);
            }
            else {
                TarjontaService.komo().tekstis({oid: komoOid}, function (komoTekstisResponse) {
                    setUiModelTexts(komoTekstisResponse.result);
                });
            }
        };

        $scope.loadRelationKoodistoData = function (apiModel, uiModel, koodiUri, tutkintoTyyppi) {

            var koulutusStructure = KoulutusConverterFactory.STRUCTURE[$scope.CONFIG.TYYPPI];

            var defaults = angular.extend({
                koulutustyyppi: $scope.CONFIG.KOULUTUSTYYPPI
            }, koulutusStructure.koodistoDefaults);

            var overrideFromRouteParamsOrApiModel = [
                'pohjakoulutusvaatimus',
                'koulutuslaji'
            ];
            angular.forEach(overrideFromRouteParamsOrApiModel, function (key) {
                if ($routeParams[key]) {
                    defaults[key] = $routeParams[key];
                }
                else if (apiModel[key] && apiModel[key].uri) {
                    defaults[key] = apiModel[key].uri;
                }
            });

            TarjontaService.getKoulutuskoodiRelations(
                {
                    koulutustyyppi: $scope.CONFIG.KOULUTUSTYYPPI,
                    uri: koodiUri,
                    defaults: _.reduce(defaults, function (memo, value, key) {
                        return memo + "," + key + ":" + value;
                    }, '').substring(1),
                    languageCode: $scope.koodistoLocale
                },
                function (data) {
                    var restRelationData = data.result;

                    angular.forEach(koulutusStructure.RELATION, function (value, key) {

                        var isTutkintoOrTutkintoOhjelma = [
                            ENUMS.ENUM_KOMO_MODULE_TUTKINTO,
                            ENUMS.ENUM_KOMO_MODULE_TUTKINTO_OHJELMA
                        ].indexOf(tutkintoTyyppi) !== -1;

                        if (isTutkintoOrTutkintoOhjelma && restRelationData[key] && restRelationData[key].uri !== ""
                            && (angular.isUndefined(value.module) || tutkintoTyyppi === value.module )) {

                            apiModel[key] = restRelationData[key];
                        }
                    });

                    angular.forEach(koulutusStructure.RELATIONS, function(value, key) {
                        if(restRelationData[key] && restRelationData[key].meta) {
                            uiModel[key].meta = restRelationData[key].meta;
                            uiModel[key].uris = apiModel[key] ? _.keys(apiModel[key].uris) : [];
                        }
                    });
                }
            );
        };

        $scope.callbackAfterSave = function (saveResponse) {
            var resultModel = saveResponse.result;
            $scope.loadRelationKoodistoData(
                $scope.model,
                $scope.uiModel,
                resultModel.koulutuskoodi.uri,
                ENUMS.ENUM_KOMO_MODULE_TUTKINTO
            );
            $scope.loadRelationKoodistoData(
                $scope.model,
                $scope.uiModel,
                resultModel.koulutusohjelma.uri,
                ENUMS.ENUM_KOMO_MODULE_TUTKINTO_OHJELMA
            );
            $scope.getLisatietoKielet($scope.model, $scope.uiModel, true);
        };

        $scope.saveLuonnos = function () {
            $scope.saveByStatus('LUONNOS', $scope.childScope.koulutusForm, $scope.CONFIG.TYYPPI, $scope.callbackAfterSave);
        };

        $scope.saveValmis = function () {
            $scope.saveByStatus('VALMIS', $scope.childScope.koulutusForm, $scope.CONFIG.TYYPPI, $scope.callbackAfterSave);
        };

        /*
         * WATCHES
         */
        $scope.$watch("model.koulutusohjelma.uri", function (uri, oUri) {
            if (angular.isDefined(uri) && uri !== null && oUri != uri) {
                $scope.model.koulutuksenTavoitteet = null; // tyhjennä varmuuden vuoksi, jotta ei näytetä väärän koulutuksen tekstejä
                if (angular.isDefined($scope.uiModel.koulutusohjelmaModules[uri])) {
                    $scope.model.komoOid = $scope.uiModel.koulutusohjelmaModules[uri].oid;
                    $scope.loadRelationKoodistoData(
                        $scope.model,
                        $scope.uiModel,
                        uri,
                        ENUMS.ENUM_KOMO_MODULE_TUTKINTO_OHJELMA
                    );

                    /**
                     * Osalla koulutuksista on omia KOMO-tekstejä lapsi KOMOISSA, jotka pitää hakea
                     * erikseen tässä. Tällä hetkellä ainoa teksti on koulutuksenTavoitteet.
                     */
                    TarjontaService.komo().tekstis({oid: $scope.model.komoOid}, function (komoTekstisResponse) {
                        if (komoTekstisResponse.result && komoTekstisResponse.result.TAVOITTEET) {
                            $scope.model.koulutuksenTavoitteet = komoTekstisResponse.result.TAVOITTEET.tekstis;
                        }
                    });
                }
                else {
                    $log.error("missing koulutus by " + uri);
                }
            }
        });

        $scope.$watch("model.koulutuskoodi.uri", function (uriNew, uriOld) {
            if (angular.isDefined(uriNew) && uriNew != null && uriOld != uriNew) {
                $scope.uiModel.koulutusohjelmaModules = {};
                $scope.uiModel.koulutusohjelma = [];
                if($scope.model.koulutusohjelma) {
                    $scope.model.koulutusohjelma.uri = null;
                }
                $scope.model.koulutuksenTavoitteet = null; // tyhjennä varmuuden vuoksi, jotta ei näytetä väärän koulutuksen tekstejä

                $scope.loadRelationKoodistoData($scope.model, $scope.uiModel, uriNew, ENUMS.ENUM_KOMO_MODULE_TUTKINTO);

                if(!$scope.uiModel.tutkintoModules) {
                    return;
                }

                $scope.loadKomoKuvausTekstis($scope.uiModel.tutkintoModules[uriNew].oid);
                var resource = TarjontaService.komo();

                $scope.uiModel.tutkinto = _.map($scope.uiModel.tutkintoModules, function (num) {
                    return num;
                });

                var promise = Koodisto.getAlapuolisetKoodiUrit([uriNew], null, $scope.koodistoLocale);
                promise.then(function (koodistoResult) {
                    resource.searchModules({
                        koulutus: uriNew,
                        koulutustyyppi: $scope.CONFIG.KOULUTUSTYYPPI,
                        moduuli: ENUMS.ENUM_KOMO_MODULE_TUTKINTO_OHJELMA
                    }, function (komos) {
                        $scope.uiModel.koulutusohjelmaModules = filterByKomos(koodistoResult, komos, 'ohjelmaUri');
                        $scope.uiModel.koulutusohjelma = _.map($scope.uiModel.koulutusohjelmaModules, function (num) {
                            return num;
                        });

                        // Hack, lukiokoulutuksella ei saa näyttää aikuisten lukiokoulutus valintaa.
                        // Tämä relaatio olisi parempi saada koodistoon, mutta nyt joudutaan tekemään näin
                        if ( $scope.CONFIG.TYYPPI === "LUKIOKOULUTUS" ) {
                            $scope.uiModel.koulutusohjelma = _.filter($scope.uiModel.koulutusohjelma, function(uri) {
                                return uri.koodiArvo !== "0086";
                            });
                        }

                        $scope.uiModel.enableOsaamisala = $scope.uiModel.koulutusohjelma.length > 0;

                        if (!$scope.uiModel.enableOsaamisala) {
                            $scope.model.komoOid = $scope.uiModel.tutkintoModules[uriNew].oid;
                        }
                    });
                });
            }
        });

        /**
         * tarjonta-service ei pidä yllä tarjoajien järjestystä, mistä syystä
         * tästä pidetään huolta alla olevan $watchin avulla (eli varmistetaan,
         * että se organisaatio joka loi koulutuksen, on aina taulukon ensimmäisenä).
         */
        $scope.$watch('model.opetusTarjoajat', function () {
            $scope.initOpetustarjoajat();
        });

        $scope.initOpetustarjoajat = function (model) {
            model = model || $scope.model;

            var shouldBeFirst = null;
            if (model.organisaatio) {
                shouldBeFirst = model.organisaatio.oid;
            }
            else if (model.opetusTarjoajat) {
                shouldBeFirst = model.opetusTarjoajat[0];
            }

            OrganisaatioService.getPopulatedOrganizations(model.opetusTarjoajat, shouldBeFirst)
                .then(function (orgs) {
                    $scope.model.organisaatiot = orgs;
                    var nimet = "";
                    angular.forEach(orgs, function (org) {
                        nimet += " | " + org.nimi;
                    });

                    $scope.model.organisaatioidenNimet = nimet.substring(3);
                });
        };

        $scope.editOrganizations = function () {
            $scope.selectedOrganizations = [];
            angular.forEach($scope.model.organisaatiot, function (org) {
                $scope.selectedOrganizations.push(org);
            });
            $scope.organizationSelectionDialog = $modal.open({
                scope: $scope,
                templateUrl: 'partials/koulutus/organization-selection.html',
                controller: 'OrganizationSelectionController'
            });
        };

        $scope.setMinMax = function(restricted) {
        	$scope.model.isMinmax = true;
        	$scope.restricted = restricted;
            if (restricted) { // true, jos koulutukseen liittyy vähintään yksi hakukohde
            	// jos koulutukselle on määritelty vähintään yksi tarkka alkamisPvm
            	if ($scope.model.koulutuksenAlkamisPvms && $scope.model.koulutuksenAlkamisPvms.length > 0) {
            		var alkamisPvm = new Date($scope.model.koulutuksenAlkamisPvms[0]);
            		// jos alkamisPvm on nykyisten min- ja max-rajojen sisällä
            		if (!$scope.outOfMinMax(alkamisPvm)) {
        				var vuosi = alkamisPvm.getFullYear();
        				// asetetaan alkamispäivämääräkentälle ja kalenterille rajat
        				if (alkamisPvm.getMonth() < 7) {
        					$scope.min = new Date(vuosi, 0, 1, 0, 0, 0, 0);
        					$scope.max = new Date(vuosi, 6, 31, 23, 59, 59, 0);
        					$scope.model.koulutuksenAlkamiskausi.uri = "kausi_k";
    					} else {
        					$scope.min = new Date(vuosi, 7, 1, 0, 0, 0, 0);
        					$scope.max = new Date(vuosi, 11, 31, 23, 59, 59, 0);
        					$scope.model.koulutuksenAlkamiskausi.uri = "kausi_s";
    					}
        				// lasketaan vuosikentän rajat (asetetaan alkamispaiva-ja-kausi.js:ssä)
    					$scope.minYear = $scope.min.getFullYear();
    					$scope.maxYear = $scope.minYear;
					}
				} else {
    				// jos koulutukselle on määritelty alkamiskausi ja -vuosi
    				if ($scope.model.koulutuksenAlkamiskausi && $scope.model.koulutuksenAlkamiskausi.uri && /^\+?(0|[1-9]\d*)$/.test($scope.model.koulutuksenAlkamisvuosi)) {
    					var vuosi = $scope.model.koulutuksenAlkamisvuosi;
        				// asetetaan alkamispäivämääräkentälle ja kalenterille rajat
        				if ($scope.model.koulutuksenAlkamiskausi.uri.indexOf("_k") > -1) {
        					$scope.min = new Date(vuosi, 0, 1, 0, 0, 0, 0);
        					$scope.max = new Date(vuosi, 6, 31, 23, 59, 59, 0);
    					} else if ($scope.model.koulutuksenAlkamiskausi.uri.indexOf("_s") > -1) {
        					$scope.min = new Date(vuosi, 7, 1, 0, 0, 0, 0);
        					$scope.max = new Date(vuosi, 11, 31, 23, 59, 59, 0);
    					} else {
            				return;
    					}
        				// lasketaan vuosikentän rajat (asetetaan alkamispaiva-ja-kausi.js:ssä)
    					$scope.minYear = $scope.min.getFullYear();
    					$scope.maxYear = $scope.minYear;
					} else {
					}
				}
			} // if (restricted)

            // lähetetään viesti, jolla disabloidaan tarvittaessa kausi-&vuosikentät oikean scopen kautta (ks. alkamispaiva-ja-kausi.js)
    		$scope.$broadcast('restricted', restricted);
        };

        $scope.outOfMinMax = function(alkamisPvm) {
        	if ($scope.min) {
				var minTime = $scope.min.getTime();
				if (alkamisPvm.getTime() < minTime) {
					return true;
				}
			}
        	
        	if ($scope.max) {
				var maxTime = $scope.max.getTime();
				if (alkamisPvm.getTime() > maxTime) {
					return true;
				}
			}
        	
        	return false;
        };

        // tällä voidaan asettaa alkamispäivämääräkentälle ja kalenterille oletusrajat (ks. alkamispaiva-ja-kausi.js)
        $scope.setDefault = function(minY, maxY) {
        	if (!$scope.restricted) {
    			$scope.min = minY;
    			$scope.max = maxY;
			}
        };
        
        return $scope;
    }
]);
