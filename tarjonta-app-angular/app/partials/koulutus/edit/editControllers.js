
var app = angular.module('app.edit.ctrl', ['Koodisto', 'Yhteyshenkilo', 'ngResource', 'ngGrid', 'imageupload', 'MultiSelect', 'OrderByNumFilter', 'localisation', 'MonikielinenTextField', 'ControlsLayout']);
app.controller('BaseEditController',
        ['$route', '$timeout', '$scope', '$location', '$log', 'TarjontaService', 'Config', '$routeParams', 'OrganisaatioService', 'LocalisationService',
            '$window', 'KoulutusConverterFactory', 'Koodisto', '$modal', 'PermissionService', 'dialogService',
            function BaseEditController($route, $timeout, $scope, $location, $log, tarjontaService, cfg, $routeParams, organisaatioService, LocalisationService,
                    $window, converter, koodisto, $modal, PermissionService, dialogService) {
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

                    uiModel.selectedKieliUri = "" //tab language
                    converter.createUiModels(uiModel);


                    /*
                     * HANDLE EDIT / CREATE NEW ROUTING
                     */
                    if (!angular.isUndefined($routeParams.id) && $routeParams.id !== null && $routeParams.id.length > 0) {
                        /*
                         * LOAD KOULUTUS BY GIVEN KOMOTO OID
                         */
                        $scope.controlFormMessages(uiModel, "LOAD");
                        $scope.lisatiedot = converter.KUVAUS_ORDER;
                        model = $route.current.locals.koulutusModel.result;

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
                        /*
                         * CREATE NEW KOULUTUS BY ORG OID AND KOULUTUSKOODI
                         */
                        $scope.controlFormMessages(uiModel, "INIT");
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

                    // lisätietokielivalinnat
                    uiModel.lisatietoKielet = angular.copy(uiModel.opetuskielis.uris);
                    for (var ki in model.kuvausKomo) {
                        for (var lc in model.kuvausKomo[ki].tekstis) {
                            if (uiModel.lisatietoKielet.indexOf(lc) == -1) {
                                uiModel.lisatietoKielet.push(lc);
                            }
                        }
                    }

                    uiModel.lisatietoKielet.sort();

                    /*
                     * INIT SCOPES FOR RENDERER
                     */
                    $scope.uiModel = uiModel;
                    $scope.model = model;
                };

                function deleteLisatiedot(lc) {
                    var lcp = $scope.uiModel.lisatietoKielet.indexOf(lc);
                    if (lcp == -1) {
                        return;
                    }
                    $scope.uiModel.lisatietoKielet.splice(lcp, 1);

                    for (var ki in $scope.model.kuvausKomo) {
                        for (var lc in $scope.model.kuvausKomo[ki].tekstis) {
                            $scope.model.kuvausKomo[ki].tekstis[lc] = undefined;
                        }
                    }
                }

                $scope.onLisatietoLangSelection = function() {
                    for (var ki in $scope.model.kuvausKomo) {
                        for (var lc in $scope.model.kuvausKomo[ki].tekstis) {
                            if ($scope.uiModel.lisatietoKielet.indexOf(lc) == -1
                                    && $scope.model.kuvausKomo[ki].tekstis[lc] && $scope.model.kuvausKomo[ki].tekstis[lc].trim().length > 0) {
                                // palautetaan listaan jottei angular digestoi ennen dialogia
                                $scope.uiModel.lisatietoKielet.push(lc);
                                $scope.uiModel.lisatietoKielet.sort();

                                if ($scope.uiModel.opetuskielis.uris.indexOf(lc) == -1) {
                                    // ei opetuskieli -> varmista poisto dialogilla
                                    dialogService.showDialog({
                                        ok: LocalisationService.t("tarjonta.poistovahvistus.koulutus.lisatieto.poista"),
                                        title: LocalisationService.t("tarjonta.poistovahvistus.koulutus.lisatieto.title"),
                                        description: LocalisationService.t("tarjonta.poistovahvistus.koulutus.lisatieto", [$scope.langs[lc]])
                                    }).result.then(function(ret) {
                                        deleteLisatiedot(lc);
                                    });
                                }
                            }
                        }
                    }
                }

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

                    if ($scope.koulutusForm.$invalid || !$scope.koulutusForm.$valid || ($scope.koulutusForm.$pristine && !$scope.isLoaded())) {
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
                $scope.controlFormMessages = function(uiModel, action, errorDetailType, apiErrors) {
                    switch (action) {
                        case 'LOAD':
                            //continue to init
                        case 'INIT':
                            uiModel.showErrorCheckField = false;
                            uiModel.showValidationErrors = false;
                            uiModel.showError = false;
                            uiModel.showSuccess = false;
                            uiModel.validationmsgs = [];
                            break;
                        case 'CLEAR':
                            $scope.formControls.notifs.errorDetail = [];
                            $scope.koulutusForm.$dirty = true;
                            $scope.koulutusForm.$invalid = false;
                            uiModel.validationmsgs = [];
                            uiModel.showValidationErrors = false;
                            uiModel.showError = false;
                            uiModel.showSuccess = false;
                            break;
                        case 'SAVED':
                            uiModel.showErrorCheckField = false;
                            uiModel.showError = false;
                            uiModel.showValidationErrors = false;
                            uiModel.hakukohdeTabsDisabled = false;
                            uiModel.validationmsgs = [];
                            //Form
                            $scope.koulutusForm.$dirty = false;
                            $scope.koulutusForm.$invalid = false;
                            uiModel.showSuccess = true;
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

                $scope.isLoaded = function() {
                    return  !angular.isUndefined($scope.model.oid) && $scope.model.oid !== null && $scope.model.oid.length > 0;
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
