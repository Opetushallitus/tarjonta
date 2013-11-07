
var app = angular.module('app.edit.ctrl', ['Koodisto', 'Yhteyshenkilo', 'ngResource', 'ngGrid', 'imageupload', 'MultiSelect', 'OrderByNumFilter', 'localisation']);
app.controller('BaseEditController',
        ['$scope', '$location', '$log', 'TarjontaService', 'Config', '$routeParams', 'OrganisaatioService', 'LocalisationService',
            '$window', 'TarjontaConverterFactory', 'Koodisto', '$modal',
            function BaseEditController($scope, $location, $log, tarjontaService, cfg, $routeParams, organisaatioService, LocalisationService, $window, converter, koodisto, $modal) {
                $log.info("BaseEditController()");
                // TODO maybe fix this, model, xmodel, uiModel, ... all to "model", "model.uimodel", "model.locale", model.xxx ?
                $scope.opetuskieli = cfg.app.userLanguages[0]; //index 0 = fi uri
                $scope.koodistoLocale = LocalisationService.getLocale();//"FI";
                $scope.config = {env: cfg.env, app: cfg.app, 'locationPath': $location.path()};
                $scope.uiModel = null;
                $scope.model = null;
                                
                // TODO servicestä joka palauttaa KomoTeksti- ja KomotoTeksti -enumien arvot
                $scope.lisatiedot = 
                	[
             		"TAVOITTEET",
                	"LISATIETOA_OPETUSKIELISTA",
                    "PAAAINEEN_VALINTA",
                	"MAKSULLISUUS",
                	"SIJOITTUMINEN_TYOELAMAAN",
            		"PATEVYYS",
            		"JATKOOPINTO_MAHDOLLISUUDET",
                	"SISALTO",
             		"KOULUTUKSEN_RAKENNE",
                	"LOPPUKOEVAATIMUKSET", // leiskassa oli "lopputyön kuvaus"
                	"KANSAINVALISTYMINEN",
                	"YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA",
                	"TUTKIMUKSEN_PAINOPISTEET",
                	
                	"ARVIOINTIKRITEERIT",
                	"PAINOTUS",
                	"KOULUTUSOHJELMAN_VALINTA",
                	"KUVAILEVAT_TIEDOT"
                	];

                $scope.init = function() {
                    var uiModel = {};
                    var model = {};

                    converter.createUiModels(uiModel);
                    converter.createAPIModel(model, cfg.app.userLanguages);

                    /*
                     * HANDLE ROUTING
                     */
                    if (!angular.isUndefined($routeParams.id) && $routeParams.id !== null && $routeParams.id.length > 0) {
                        //DATA WAS LOADED BY KOMOTO OID
                        model = $scope.koulutusModel.result;

                        angular.forEach(model.yhteyshenkilos, function(value, key) {
                            if (value.henkiloTyyppi === 'YHTEYSHENKILO') {
                                $scope.uiModel.contactPerson = converter.converPersonObjectForUi(value);
                            } else if (value.henkiloTyyppi === 'ECTS_KOORDINAATTORI') {
                                $scope.uiModel.ectsCoordinator = converter.converPersonObjectForUi(value);
                            } else {
                                converter.throwError('Undefined henkilotyyppi : ', value);
                            }
                        });

                        converter.createMetaLanguages(model.koulutusohjelma, cfg.app.userLanguages);
                        angular.forEach(model.koulutusohjelma.meta, function(val, key) {
                            if (angular.isUndefined(val.koodi.kaannos)) {
                                $scope.searchKoodi(val, cfg.env['koodisto-uris.kieli'], key, $scope.koodistoLocale);
                            }
                        });

                        $scope.updateMultiSelectKoodistoData(uiModel, model);

                    } else if (!angular.isUndefined($routeParams.org)) {
                        //CREATE NEW KOULUTUS
                        $scope.loadRelationKoodistoData();
                        var promiseOrg = organisaatioService.nimi($routeParams.org);
                        promiseOrg.then(function(vastaus) {
                            converter.updateOrganisationApiModel(model, $routeParams.org, vastaus);
                        });
                    } else {
                        converter.throwError('unsupported $routeParams.type : ' + $routeParams.type + '.');
                    }

                    /*
                     * INITIALISE DATA MODELS
                     */



                    /*
                     * Init language texts, like 'suomi' 'englanti' etc.
                     */
                    angular.forEach(model.koulutusohjelma.meta, function(val, key) {
                        if (angular.isUndefined(val.koodi.kaannos)) {
                            $scope.searchKoodi(val, cfg.env['koodisto-uris.kieli'], key, $scope.koodistoLocale);
                        }
                    });

                    /*
                     * LOAD ALL KOODISTO KOODIS
                     */
                    angular.forEach(converter.STRUCTURE.COMBO, function(value, key) {
                        $scope.searchKoodisByKoodistoUri(uiModel[key], cfg.env[value.koodisto], $scope.koodistoLocale);
                    });
                    angular.forEach(converter.STRUCTURE.MCOMBO, function(value, key) {
                        $scope.searchKoodisByKoodistoUri(uiModel[key], cfg.env[value.koodisto], $scope.koodistoLocale);
                    });


                    /*
                     * INIT SCOPES FOR RENDERER
                     */
                    $scope.uiModel = uiModel;
                    $scope.model = model;
                };
                $scope.loadRelationKoodistoData = function() {
                    tarjontaService.getKoulutuskoodiRelations({koulutuskoodiUri: $routeParams.koulutuskoodi}, function(data) {
                        var koodistoData = data.result;
                        angular.forEach(converter.STRUCTURE.RELATION, function(value, key) {
                            $scope.model[key] = koodistoData[key];
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
                    if (angular.isUndefined(tila)) {
                        converter.throwError('Undefined tila');
                    }

                    var KoulutusRes = tarjontaService.koulutus();
                    var apiModelReadyForSve = $scope.saveModelConverter(tila);

                    KoulutusRes.save(apiModelReadyForSve, function(response) {
                        var model = response.result;
                        //Callback
                        console.log("Insert data response from POST: %j", response);
                        $scope.model.oid = model.oid;
                        $location.path('/koulutus/' + $scope.model.oid + '/edit/');
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
                        console.log(apiModel[key], key);
                        if (converter.isNull(apiModel[key] && converter.isNull(apiModel[key]['arvo']))) {
                            apiModel[key] = converter.convertKoodistoComboToKoodiUiDTO(null, uiModel[key]);
                        } else {
                            apiModel[key] = converter.convertKoodistoComboToKoodiUiDTO(apiModel[key].arvo, uiModel[key]);
                        }

                    });
                    //multi select models
                    angular.forEach(converter.STRUCTURE.MCOMBO, function(value, key) {
                        apiModel[key].meta = converter.convertKoodistoMultiToKoodiUiDTOs(uiModel[key]);
                    });
                    console.log(JSON.stringify(apiModel));
                    return apiModel;
                };

                /**
                 * Handle data load for koodisto data combo boxes.
                 *
                 * @param {type} uiModel
                 * @param {type} apiModel
                 */
                $scope.updateMultiSelectKoodistoData = function(uiModel, apiModel) {
                    angular.forEach(converter.STRUCTURE.COMBO, function(value, key) {
                        $scope.updateKoodiUriToUiModel(uiModel[key], apiModel[key]);
                    });
                    //multi select models
                    angular.forEach(converter.STRUCTURE.MCOMBO, function(value, key) {
                        $scope.updateKoodiUrisToUiModel(uiModel[key], apiModel[key]);
                    });
                };
                $scope.updateKoodiUrisToUiModel = function(uiModel, apiModel) {
                    angular.forEach(apiModel.meta, function(value, key) {
                        this.push(value.koodi.uri);
                    }, uiModel.uris);
                };
                $scope.updateKoodiUriToUiModel = function(uiModel, apiModel) {
                    if (converter.isNull(apiModel) || converter.isNull(apiModel.koodi)) {
                        console.warn("<api-model>.koodi.uri is missing.", apiModel);
                    } else {
                        uiModel.uri = apiModel.koodi.uri;
                    }
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

                $scope.searchKoodi = function(apiModel, koodistouri, uri, locale) {
                    var promise = koodisto.getKoodi(koodistouri, uri, locale);
                    promise.then(function(data) {
                        console.log("KOODI", data);
                        apiModel.koodi.kaannos = data.koodiNimi;
                        apiModel.koodi.versio = data.koodiVersio;
                    });
                };
                $scope.searchKoodisByKoodistoUri = function(uiModel, koodistouri, locale) {
                    var koodisPromise = koodisto.getAllKoodisWithKoodiUri(koodistouri, locale);
                    koodisPromise.then(function(koodisParam) {
                        uiModel.data = koodisParam;
                    });
                };
                //add factory functions to ui template
                $scope.searchKoodiByKoodiUri = converter.searchKoodiByKoodiUri;
                $scope.removeKoodiByKoodiUri = converter.removeKoodiByKoodiUri;
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
                $scope.goToReview = function(event) {
                    $log.info("goBack()...");
                    $location.path("/koulutus/" + $scope.model.komotoOid);
                };

                $scope.single = function(image) {
                    var formData = new FormData();
                    formData.append('image', image, image.name);
                    $http.post('upload', formData, {
                        headers: {'Content-Type': false},
                        transformRequest: angular.identity}).success(function(result) {
                        $scope.uploadedImgSrc = result.src;
                        $scope.sizeInBytes = result.size;
                    });
                };

                $scope.setTabLang = function(langUri) {
                    if (angular.isUndefined(langUri) || langUri === null) {
                        $scope.uiModel.tabLang = cfg.app.userLanguages[0]; //fi uri I guess;
                    } else {
                        $scope.uiModel.tabLang = langUri;
                    }
                };

                $scope.getKuvausApiModelLanguageUri = function(kuvaus, key, kieliuri) {
                    if (angular.isUndefined(kuvaus) || angular.isUndefined(kuvaus.tekstis)) {
                        converter.throwError("Description text object cannot be null.");
                    }

                    if (angular.isUndefined(kuvaus.tekstis[key])) {
                        //both key and lang uri are missing
                        converter.addLangForDescUiField(kuvaus, key, kieliuri);
                    } else if (angular.isUndefined(kuvaus.tekstis[key].meta[kieliuri])) {
                        //key is available, but lang uri is missing
                        converter.addLangForDescUiField(kuvaus, key, kieliuri);
                    }

                    return kuvaus.tekstis[key].meta[kieliuri].koodi;
                };

                $scope.getKuvausApiModelLanguageUris = function(tekstis, kieliuri) {
                    var koodis = [];

                    angular.forEach(tekstis, function(val, key) {
                        var kieli = val.meta[kieliuri];
                        if (!angular.isUndefined(kieli)) {
                            koodis.push(kieli.koodi);
                        } else {
                            converter.addMetaLanguage(val, kieliuri);
                            var koodi = val.meta[kieliuri].koodi;
                            koodi.arvo = '';
                            koodis.push(koodi);
                        }
                    });
                    return koodis;
                };
                
                // TODO omaksi direktiivikseen tjsp..
                $scope.kieliFromKoodi = function(koodi) {
                	var kd = $scope.uiModel.opetuskielis.data;
                	for (var i in kd) {
                		if (koodi==kd[i].koodiUri) {
                			return kd[i].koodiNimi;
                		}
                	}
                	return koodi;
                }

                $scope.init();
            }]);