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

var app = angular.module('app.koulutus.ctrl', []);

app.controller('KoulutusRoutingController', ['$scope', '$log', '$routeParams', '$route', '$location', 'KoulutusConverterFactory', 'TarjontaService', 'PermissionService', 'OrganisaatioService', 'Koodisto',
    function KoulutusRoutingController($scope, $log, $routeParams, $route, $location, converter, TarjontaService, PermissionService, organisaatioService, Koodisto) {
        $log = $log.getInstance("KoulutusRoutingController");
        $scope.resultPageUri;
        $scope.langs = {};
        $scope.model = {};
        $scope.uiModel = {};
        $scope.controlModel = {
            formStatus: {
                modifiedBy: '',
                modified: null,
                tila: ''
            },
            formControls: {reloadDisplayControls: function() {
                }}
        };

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

        $scope.resolvePath = function(actionType) {
            if (!angular.isUndefined($route.current.locals.koulutusModel.result)) {
                var type = $route.current.locals.koulutusModel.result.koulutusasteTyyppi;
                var patt = new RegExp("(LUKIOKOULUTUS|KORKEAKOULUTUS)");
                // var patt = new RegExp("(KORKEAKOULUTUS)");
                if (patt.test(type)) {
                    $scope.resultPageUri = "partials/koulutus/" + actionType + "/" + type + ".html";
                } else {
                    $scope.resultPageUri = "partials/koulutus/" + actionType + "/UNKNOWN.html";
                }
            } else {
                $location.path("/error");
            }
        };

        $scope.getKoulutusPartialName = function(actionType) {
            $scope.resolvePath(actionType, $scope.koulutusModel);
        };


        /*
         * ABSTRACT FUNCTIONS FOR KOULUTUS EDIT PAGES
         */

        $scope.goBack = function(event) {
            $log.info("goBack()...");
            $location.path("/");
        };
        $scope.goToReview = function(event, boolInvalid, validationmsgs) {
            if (!angular.isUndefined(boolInvalid) && boolInvalid) {
                //ui errors
                return;
            }

            if (!angular.isUndefined(validationmsgs) && validationmsgs > 0) {
                //server errors
                return;
            }

            $location.path("/koulutus/" + $scope.model.oid);
        };

        $scope.setTabLang = function(langUri) {
            if (angular.isUndefined(langUri) || langUri === null) {
                $scope.uiModel.tabLang = window.CONFIG.app.userLanguages[0]; //fi uri I guess;
            } else {
                $scope.uiModel.tabLang = langUri;
            }
        };

        $scope.selectKieli = function(kieliUri) {
            $scope.uiModel.selectedKieliUri = kieliUri;
        }

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
            switch (action) {
                case 'SHOW':
                    uiModel.showErrorCheckField = false;
                    uiModel.showValidationErrors = true;
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
                    $scope.controlModel.formControls.notifs.errorDetail = [];
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

        $scope.updateFormStatusInformation = function(model) {
            //look more info from KoulutusRoutingController and controlLayouts.js
            $scope.controlModel.formStatus = {
                modifiedBy: model.modifiedBy,
                modified: model.modified,
                tila: model.tila
            };

            //force reload
            $scope.controlModel.formControls.reloadDisplayControls();
        };

        $scope.saveByStatus = function(tila, form, koulutusasteTyyppi, fnCustomCallbackAfterSave) {
            $scope.controlFormMessages(form, $scope.uiModel, "CLEAR");

            if (angular.isUndefined(tila)) {
                converter.throwError('Undefined tila');
            }

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
                var apiModelReadyForSave = converter.saveModelConverter(tila, $scope.model, $scope.uiModel, koulutusasteTyyppi);

                KoulutusRes.save(apiModelReadyForSave, function(saveResponse) {
                    var model = saveResponse.result;

                    if (saveResponse.status === 'OK') {
                        $scope.model = model;
                        $scope.updateFormStatusInformation($scope.model);
                        $scope.controlFormMessages(form, $scope.uiModel, "SAVED");
                        $scope.uiModel.tabs.lisatiedot = false;
                        $scope.lisatiedot = converter.STRUCTURE[koulutusasteTyyppi].KUVAUS_ORDER;
                    } else {
                        $scope.controlFormMessages(form, $scope.uiModel, "ERROR", null, saveResponse.errors);
                    }

                    fnCustomCallbackAfterSave(saveResponse);
                });
            });
        };

        $scope.commonKoodistoLoadHandler = function(uiModel, koulutusasteTyyppi) {
            angular.forEach(converter.STRUCTURE[koulutusasteTyyppi].COMBO, function(value, key) {
                if (angular.isUndefined(value.skipUiModel)) {
                    var koodisPromise = Koodisto.getAllKoodisWithKoodiUri(window.CONFIG.env[value.koodisto], $scope.koodistoLocale);
                    uiModel[key].promise = koodisPromise;
                    koodisPromise.then(function(result) {
                        uiModel[key].koodis = result;
                    });
                }
            });
            angular.forEach(converter.STRUCTURE[koulutusasteTyyppi].MCOMBO, function(value, key) {
                if (angular.isUndefined(window.CONFIG.env[value.koodisto])) {
                    throw new Error("No koodisto URI for key : " + key + ", property : '" + value.koodisto + "'");
                }

                var koodisPromise = Koodisto.getAllKoodisWithKoodiUri(window.CONFIG.env[value.koodisto], $scope.koodistoLocale);
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

        $scope.commonNewModelHandler = function(form, model, uiModel, koulutusasteTyyppi) {
//            if (angular.isUndefined(form) || form === null) {
//                converter.throwError("Form object cannot be null or undefined");
//            }

            if (angular.isUndefined(model) || model === null) {
                converter.throwError("Model object cannot be null or undefined");
            }

            if (angular.isUndefined(uiModel) || uiModel === null) {
                converter.throwError("UI model object cannot be null or undefined");
            }

            if (angular.isUndefined(koulutusasteTyyppi) || koulutusasteTyyppi === null) {
                converter.throwError("KoulutusasteTyyppi string enum cannot be null or undefined");
            }

            uiModel.isMutable = false;
            uiModel.selectedKieliUri = "";

            converter.createUiModels(uiModel, koulutusasteTyyppi);
            uiModel.isMutable = true;
            $scope.controlFormMessages(form, uiModel, "INIT");
            converter.createAPIModel(model, window.CONFIG.app.userLanguages, koulutusasteTyyppi);

            var promiseOrg = organisaatioService.nimi($routeParams.org);
            promiseOrg.then(function(vastaus) {
                converter.updateOrganisationApiModel(model, $routeParams.org, vastaus);
            });

        };

        $scope.commonLoadModelHandler = function(form, model, uiModel, koulutusasteTyyppi) {
//            if (angular.isUndefined(form) || form === null) {
//                converter.throwError("Form object cannot be null or undefined");
//            }

            if (angular.isUndefined(model) || model === null) {
                converter.throwError("Model object cannot be null or undefined");
            }

            if (angular.isUndefined(uiModel) || uiModel === null) {
                converter.throwError("UI model object cannot be null or undefined");
            }

            if (angular.isUndefined(koulutusasteTyyppi) || koulutusasteTyyppi === null) {
                converter.throwError("KoulutusasteTyyppi string enum cannot be null or undefined");
            }

            uiModel.isMutable = false;
            uiModel.selectedKieliUri = "";

            converter.createUiModels(uiModel, koulutusasteTyyppi);
            $scope.controlFormMessages(form, uiModel, "SHOW");

            if (angular.isUndefined(model) || model === null) {
                $location.path("/error");
                return;
            }

            if (model.tila === 'POISTETTU') {
                uiModel.isMutable = false;
            }

            PermissionService.koulutus.canEdit(model.oid).then(function(data) {
                $log.debug("setting mutable to:", data);
                uiModel.isMutable = data;
            });

            uiModel.tabs.lisatiedot = false; //activate lisatiedot tab
            $scope.updateFormStatusInformation(model);

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
            angular.forEach(converter.STRUCTURE[koulutusasteTyyppi].MCOMBO, function(value, key) {
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
        };

        return $scope;
    }
]);
