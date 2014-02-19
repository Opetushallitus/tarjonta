
var app = angular.module('app.edit.ctrl', ['Koodisto', 'Yhteyshenkilo', 'ngResource', 'ngGrid', 'imageupload', 'MultiSelect', 'OrderByNumFilter', 'localisation', 'MonikielinenTextField', 'ControlsLayout']);
app.controller('BaseEditController',
        ['$route', '$timeout', '$scope', '$location', '$log', 'TarjontaService', 'Config', '$routeParams', 'OrganisaatioService', 'LocalisationService',
            '$window', 'KoulutusConverterFactory', 'Koodisto', '$modal', 'PermissionService',
            function BaseEditController($route, $timeout, $scope, $location, $log, tarjontaService, cfg, $routeParams, organisaatioService, LocalisationService,
                    $window, converter, koodisto, $modal, PermissionService) {
                $scope.userLanguages = cfg.app.userLanguages; // opetuskielien esijärjestystä varten
                $scope.opetuskieli = cfg.app.userLanguages[0]; //index 0 = fi uri
                $scope.koodistoLocale = LocalisationService.getLocale();//"FI";
                $scope.uiModel = null;
                $scope.model = null;
                $scope.tmp = {};
                $scope.langs = {};
                $scope.formControls = {};

                // TODO servicestä joka palauttaa KomoTeksti- ja KomotoTeksti -enumien arvot
                $scope.lisatiedot = [];

                $scope.init = function() {
                    var uiModel = {};
                    var model = {};
                    $scope.controlFormMessages(uiModel, "INIT");
                    uiModel.selectedKieliUri = "" //tab language
                    converter.createUiModels(uiModel);


                    /*
                     * HANDLE EDIT / CREATE NEW ROUTING
                     */
                    if (!angular.isUndefined($routeParams.id) && $routeParams.id !== null && $routeParams.id.length > 0) {
                        //DATA WAS LOADED BY KOMOTO OID
                        $scope.lisatiedot = converter.KUVAUS_ORDER;
                        model = $scope.koulutusModel.result;

                        if (angular.isUndefined(model)) {
                            $location.path("/error");
                            return;
                        }

                        angular.forEach(model.yhteyshenkilos, function(value, key) {
                            if (value.henkiloTyyppi === 'YHTEYSHENKILO') {
                                uiModel.contactPerson = converter.converPersonObjectForUi(value);
                            } else if (value.henkiloTyyppi === 'ECTS_KOORDINAATTORI') {
                                uiModel.ectsCoordinator = converter.converPersonObjectForUi(value);
                            } else {
                                converter.throwError('Undefined henkilotyyppi : ', value);
                            }
                        });

                        $scope.loadRelationKoodistoData(model, uiModel, model.koulutuskoodi.uri);

                        /*
                         * remove version data from the list 
                         */
                        angular.forEach(converter.STRUCTURE.MCOMBO, function(value, key) {
                            uiModel[key].uris = _.keys(model[key].uris);
                        });

                        uiModel.tabs.lisatiedot = false; //activate lisatiedot tab
                    } else if (!angular.isUndefined($routeParams.org)) {
                        //CREATE NEW KOULUTUS
                        converter.createAPIModel(model, cfg.app.userLanguages);
                        $scope.loadRelationKoodistoData(model, uiModel, $routeParams.koulutuskoodi);
                        var promiseOrg = organisaatioService.nimi($routeParams.org);
                        promiseOrg.then(function(vastaus) {
                            converter.updateOrganisationApiModel(model, $routeParams.org, vastaus);
                        });
                    } else {
                        converter.throwError('unsupported $routeParams.type : ' + $routeParams.type + '.');
                    }

                    /*
                     * LOAD ALL KOODISTO KOODIS
                     */
                    angular.forEach(converter.STRUCTURE.COMBO, function(value, key) {
                        if (angular.isUndefined(value.skipUiModel)) {
                            var koodisPromise = koodisto.getAllKoodisWithKoodiUri(cfg.env[value.koodisto], $scope.koodistoLocale);
                            uiModel[key].promise = koodisPromise;
                            koodisPromise.then(function(result) {
                                uiModel[key].koodis = result;
                            });
                        }
                    });
                    angular.forEach(converter.STRUCTURE.MCOMBO, function(value, key) {
                        if (angular.isUndefined(cfg.env[value.koodisto])) {
                            throw new Error("No koodisto URI for key : " + key + ", property : '" + value.koodisto + "'");
                        }

                        var koodisPromise = koodisto.getAllKoodisWithKoodiUri(cfg.env[value.koodisto], $scope.koodistoLocale);
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

                    /*
                     * INIT SCOPES FOR RENDERER
                     */
                    $scope.uiModel = uiModel;
                    $scope.model = model;
                };
                $scope.loadRelationKoodistoData = function(apiModel, uiModel, koulutuskoodi) {
                    tarjontaService.getKoulutuskoodiRelations({koulutuskoodiUri: koulutuskoodi, languageCode: $scope.koodistoLocale}, function(data) {
                        var restRelationData = data.result;
                        angular.forEach(converter.STRUCTURE.RELATION, function(value, key) {
                            apiModel[key] = restRelationData[key];
                        });

                        angular.forEach(converter.STRUCTURE.RELATIONS, function(value, key) {
                            uiModel[key].meta = restRelationData[key].meta;

                            if (angular.isUndefined(value.skipApiModel) && !angular.isUndefined(apiModel[key]) && !angular.isUndefined(apiModel[key].uris)) {
                                uiModel[key].uris = _.keys(apiModel[key].uris); //load uris
                            }
                        });
                    });
                };
                /**
                 * Save koulutus data to tarjonta-service database.
                 * TODO: strict data validation, exception handling and optimistic locking
                 */
                $scope.saveLuonnos = function(tila) {
                    $scope.saveByStatus('LUONNOS');
                };
                $scope.saveValmis = function(tila) {
                    $scope.saveByStatus('VALMIS');
                };
                $scope.saveByStatus = function(tila) {
                    $scope.controlFormMessages($scope.uiModel, "CLEAR");

                    if (angular.isUndefined(tila)) {
                        converter.throwError('Undefined tila');
                    }

                    if ($scope.koulutusForm.$invalid || $scope.koulutusForm.$pristine) {
                        //invalid form data
                        $scope.controlFormMessages($scope.uiModel, "ERROR", "UI_ERRORS");
                        return;
                    }

                    PermissionService.permissionResource().authorize({}, function(authResponse) {
                        console.log("Authorization check : " + authResponse.result);

                        if (authResponse.status !== 'OK') {
                            //not authenticated
                            $scope.controlFormMessages($scope.uiModel, "ERROR", "AUTH");
                            return;
                        }

                        var KoulutusRes = tarjontaService.koulutus();
                        var apiModelReadyForSave = $scope.saveModelConverter(tila);

                        KoulutusRes.save(apiModelReadyForSave, function(saveResponse) {
                            var model = saveResponse.result;

                            if (saveResponse.status === 'OK') {
                                $scope.model = model;
                                $scope.controlFormMessages($scope.uiModel, "SAVED");
                                $scope.uiModel.tabs.lisatiedot = false;
                                $scope.lisatiedot = converter.KUVAUS_ORDER;
                                $scope.$broadcast("onImageUpload", ""); //save images
                            } else {
                                $scope.controlFormMessages($scope.uiModel, "ERROR", null, saveResponse.errors);
                            }
                        });
                    });
                };

                $scope.saveModelConverter = function(tila) {
                    var apiModel = angular.copy($scope.model);
                    apiModel.tila = tila;
                    var uiModel = angular.copy($scope.uiModel);
                    $scope.validateOutputData(apiModel);
                    /*
                     * DATA CONVERSIONS FROM UI MODEL TO API MODEL
                     * Convert person object to back-end object format.
                     */

                    apiModel.yhteyshenkilos = converter.convertPersonsUiModelToDto([uiModel.contactPerson, uiModel.ectsCoordinator]);
                    /*
                     * Convert koodisto komponent object to back-end object format.
                     */
                    //single select nodels
                    angular.forEach(converter.STRUCTURE.COMBO, function(value, key) {
                        //search version information for list of uris;

                        var koodis = $scope.uiModel[key].koodis;
                        for (var i in koodis) {
                            if (koodis[i].koodiUri === apiModel[key].uri) {
                                apiModel[key] = {
                                    uri: koodis[i].koodiUri,
                                    versio: koodis[i].koodiVersio
                                };
                                break;
                            }
                        }
                    });

                    angular.forEach(converter.STRUCTURE.RELATIONS, function(value, key) {
                        if (angular.isUndefined(value.skipApiModel)) {
                            apiModel[key] = {'uris': {}};
                            //search version information for list of uris;
                            var map = {};
                            var meta = $scope.uiModel[key].meta;
                            for (var i in meta) {
                                map[meta[i].uri] = meta[i].versio;
                            }
                            angular.forEach(uiModel[key].uris, function(uri) {
                                apiModel[key].uris[uri] = map[uri];
                            });
                        }
                    });

                    //multi-select models, add version to the koodi 
                    angular.forEach(converter.STRUCTURE.MCOMBO, function(value, key) {
                        apiModel[key] = {'uris': {}};
                        //search version information for list of uris;
                        var map = {};
                        var koodis = $scope.uiModel[key].koodis;
                        for (var i in koodis) {
                            map[koodis[i].koodiUri] = koodis[i].koodiVersio;
                        }
                        angular.forEach(uiModel[key].uris, function(uri) {
                            apiModel[key].uris[uri] = map[uri];
                        });

                    });

                    console.log(JSON.stringify(apiModel));
                    return apiModel;
                };

                $scope.validateOutputData = function(m) {
                    if (converter.isNull(m.organisaatio) || converter.isNull(m.organisaatio.oid)) {
                        converter.throwError("Organisation OID is missing.");
                    }

                    //remove all meta data fields, if any
                    angular.forEach(converter.STRUCTURE, function(value, key) {
                        if ('MLANG' !== key) {
                            //MLANG objects needs the meta fields
                            angular.forEach(value, function(value, key) {
                                converter.deleteMetaField(m[key]);
                            });
                        }
                    });
                };

                $scope.tutkintoDialogModel = {};
                $scope.tutkintoDialogModel.open = function() {

                    var modalInstance = $modal.open({
                        scope: $scope,
                        templateUrl: 'partials/koulutus/edit/selectTutkintoOhjelma.html',
                        controller: 'SelectTutkintoOhjelmaController'
                    });
                    modalInstance.result.then(function(selectedItem) {
                        console.log('Ok, dialog closed: ' + selectedItem.koodiNimi);
                        console.log('Koodiarvo is: ' + selectedItem.koodiArvo);
                        if (!converter.isNull(selectedItem)) {
                            //$scope.model.koulutuskoodi = selectedItem;
                            $scope.model.koulutuskoodi.koodi.arvo = selectedItem.koodiArvo;
                        }
                    }, function() {
                        console.log('Cancel, dialog closed');
                    });
                };
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

                    $log.info("goBack()...");
                    $route.current.locals.koulutusModel.result = $scope.model;
                    $location.path("/koulutus/" + $scope.model.oid);
                };

                $scope.setTabLang = function(langUri) {
                    if (angular.isUndefined(langUri) || langUri === null) {
                        $scope.uiModel.tabLang = cfg.app.userLanguages[0]; //fi uri I guess;
                    } else {
                        $scope.uiModel.tabLang = langUri;
                    }
                };

                $scope.selectKieli = function(kieliUri) {
                    $scope.uiModel.selectedKieliUri = kieliUri;
                }

                $scope.getKuvausApiModelLanguageUri = function(boolIsKomo, textEnum, kieliUri) {
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
                $scope.controlFormMessages = function(uiModel, action, errorDetailType, apiErrors) {
                    switch (action) {
                        case 'INIT':
                            uiModel.showErrorCheckField  = false;
                            uiModel.showValidationErrors = false;
                            uiModel.showError = false;
                            uiModel.showSuccess = false;
                            uiModel.validationmsgs = [];
                            break;
                        case 'CLEAR':
                            $scope.formControls.notifs.errorDetail = [];
                            $scope.koulutusForm.$dirty = true;
                            $scope.koulutusForm.$invalid = false;
                            uiModel.showValidationErrors = false;
                            uiModel.showError = false;
                            uiModel.showSuccess = false;
                            break;
                        case 'SAVED':
                            uiModel.showValidationErrors = false;
                            uiModel.showSuccess = true;
                            uiModel.showError = false;
                            uiModel.hakukohdeTabsDisabled = false;
                            uiModel.validationmsgs = [];
                            //Form
                            $scope.koulutusForm.$dirty = false;
                            $scope.koulutusForm.$invalid = false;
                            break;
                        case 'ERROR':
                        default:
                            uiModel.showErrorCheckField = errorDetailType === 'UI_ERRORS'
                            uiModel.showValidationErrors = errorDetailType === 'UI_ERRORS';
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

                /*
                 * WATCHES
                 */
                $scope.$watch("model.opintojenMaksullisuus", function(valNew, valOld) {
                    if (!valNew && valOld) {
                        //clear price data field
                        $scope.model.hinta = '';
                    }
                });

                $scope.init();
            }]);
