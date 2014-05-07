var app = angular.module('app.edit.ctrl.kk', ['Koodisto', 'Yhteyshenkilo', 'ngResource', 'ngGrid', 'imageupload', 'MultiSelect', 'OrderByNumFilter', 'localisation', 'MonikielinenTextField', 'ControlsLayout']);
app.controller('EditKorkeakouluController',
        ['$route', '$timeout', '$scope', '$location', '$log', 'TarjontaService', 'Config', '$routeParams', 'OrganisaatioService', 'LocalisationService',
            '$window', 'KoulutusConverterFactory', 'Koodisto', '$modal', 'PermissionService', 'dialogService', 'CommonUtilService',
            function EditKorkeakouluController($route, $timeout, $scope, $location, $log, TarjontaService, cfg, $routeParams, organisaatioService, LocalisationService,
                    $window, converter, koodisto, $modal, PermissionService, dialogService, CommonUtilService) {

                var ENUM_KORKEAKOULUTUS = 'KORKEAKOULUTUS';
                $log = $log.getInstance("EditKorkeakouluController");
                $scope.tutkintoDialogModel = {};

                $scope.init = function() {
                    $log.debug("init");
                    var model = {};

                    var uiModel = {
                        //add you custom ui stuff.
                    };

                    /*
                     * HANDLE EDIT / CREATE NEW ROUTING
                     */
                    if (!angular.isUndefined($routeParams.id) && $routeParams.id !== null && $routeParams.id.length > 0) {
                        /*
                         *  SHOW KOULUTUS BY GIVEN KOMOTO OID
                         *  Look more info from koulutusController.js.
                         */
                        model = $route.current.locals.koulutusModel.result;
                        $scope.commonLoadModelHandler($scope.koulutusForm, model, uiModel, ENUM_KORKEAKOULUTUS);

                        /*
                         * CUSTOM LOGIC
                         */
                        $scope.loadRelationKoodistoData(model, uiModel, model.koulutuskoodi.uri);

                        /*
                         * Load data to multiselect fields
                         * remove version data from the list
                         */
                        angular.forEach(converter.STRUCTURE[ENUM_KORKEAKOULUTUS].MCOMBO, function(value, key) {
                            if (angular.isDefined(model[key]) && angular.isDefined(model[key].uris)) {
                                uiModel[key].uris = _.keys(model[key].uris);
                            } else {
                                console.error("invalid key mapping : ", key);
                            }
                        });

                    } else if (!angular.isUndefined($routeParams.org)) {
                        /*
                         * CREATE NEW KOULUTUS BY ORG OID AND KOULUTUSKOODI
                         * Look more info from koulutusController.js.
                         */
                        $scope.commonNewModelHandler($scope.koulutusForm, model, uiModel, ENUM_KORKEAKOULUTUS);
                        $scope.loadRelationKoodistoData(model, uiModel, $routeParams.koulutuskoodi);
                    } else {
                        converter.throwError('unsupported $routeParams.type : ' + $routeParams.type + '.');
                    }

                    /*
                     * SHOW ALL KOODISTO KOODIS
                     */
                    $scope.commonKoodistoLoadHandler(uiModel, ENUM_KORKEAKOULUTUS);

                    /*
                     * CUSTOM LOGIC
                     */
                    // lisätietokielivalinnat
                    uiModel.lisatietoKielet = angular.copy(uiModel.opetuskielis.uris);
                    for (var ki in model.kuvausKomo) {
                        for (var lc in model.kuvausKomo[ki].tekstis) {
                            if (uiModel.lisatietoKielet.indexOf(lc) == -1) {
                                uiModel.lisatietoKielet.push(lc);
                            }
                        }
                    }

                    /*
                     * INIT SCOPES FOR RENDERER IN koulutusController.js
                     */
                    $scope.setUiModel(uiModel);
                    $scope.setModel(model);

                };

                $scope.loadRelationKoodistoData = function(apiModel, uiModel, koulutuskoodi) {
                    TarjontaService.getKoulutuskoodiRelations({koulutuskoodiUri: koulutuskoodi, languageCode: $scope.koodistoLocale}, function(data) {
                        var restRelationData = data.result;
                        angular.forEach(converter.STRUCTURE[ENUM_KORKEAKOULUTUS].RELATION, function(value, key) {
                            apiModel[key] = restRelationData[key];
                        });

                        angular.forEach(converter.STRUCTURE[ENUM_KORKEAKOULUTUS].RELATIONS, function(value, key) {
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
                $scope.saveLuonnos = function() {
                    $scope.saveByStatus('LUONNOS', $scope.koulutusForm, ENUM_KORKEAKOULUTUS, $scope.customCallbackAfterSave);
                };
                $scope.saveValmis = function() {
                    $scope.saveByStatus('VALMIS', $scope.koulutusForm, ENUM_KORKEAKOULUTUS, $scope.customCallbackAfterSave);
                };

                $scope.customCallbackAfterSave = function(saveResponse) {
                    if (saveResponse.status === 'OK') {
                        $scope.$broadcast("onImageUpload", ""); //save images
                    }
                };

                $scope.tutkintoDialogModel.open = function() {
                    var modalInstance = $modal.open({
                        scope: $scope,
                        templateUrl: 'partials/koulutus/edit/korkeakoulu/selectTutkintoOhjelma.html',
                        controller: 'SelectTutkintoOhjelmaController'
                    });
                    modalInstance.result.then(function(selectedItem) {
                        $log.debug('Ok, dialog closed: ' + selectedItem.koodiNimi);
                        $log.debug('Koodiarvo is: ' + selectedItem.koodiArvo);
                        if (!converter.isNull(selectedItem)) {
                            //$scope.model.koulutuskoodi = selectedItem;
                            $scope.model.koulutuskoodi.koodi.arvo = selectedItem.koodiArvo;
                        }
                    }, function() {
                        $log.debug('Cancel, dialog closed');
                    });
                };

                //LISATIEDOT PAGE FUNCTIONS

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

                $scope.removeKandidaatinKoulutuskoodi = function(koodi) {
                    $scope.model.kandidaatinKoulutuskoodi = {};
                };

                $scope.createSelectKoulutuskoodiModalDialog = function(koodi) {
                    var modalInstance = $modal.open({
                        templateUrl: 'partials/koulutus/edit/korkeakoulu/selectTutkintoOhjelma.html',
                        controller: 'SelectTutkintoOhjelmaController',
                        resolve: {
                            targetFilters: function() {
                                return [cfg.app["koodisto-uri.tutkintotyyppi.alempiKorkeakoulututkinto"]];
                            }
                        }
                    });

                    modalInstance.result.then(function(result) {/* close */
                        $scope.model.kandidaatinKoulutuskoodi = {
                            arvo: result.koodiArvo,
                            uri: result.koodiUri,
                            versio: result.koodiVersio,
                            nimi: result.koodiNimi
                        };
                    }, function() { /* dismissed */
                    });
                };

                /*
                 * WATCHES
                 */

                $scope.onMaksullisuusChanged = function() {
                    if (!$scope.model.hinta) {
                        return;
                    }
                    var p = $scope.model.hinta.indexOf(',');
                    while (p != -1) {
                        $scope.model.hinta = $scope.model.hinta.substring(0, p) + "." + $scope.model.hinta.substring(p + 1);
                        p = $scope.model.hinta.indexOf(',', p);
                    }
                }

                $scope.$watch("model.opintojenMaksullisuus", function(valNew, valOld) {
                    if (!valNew && valOld) {
                        //clear price data field
                        $scope.model.hinta = '';
                    }
                });

                $scope.isKandiUri = function() {
                    var kandiObj = $scope.model.kandidaatinKoulutuskoodi;
                    return angular.isDefined(kandiObj) && kandiObj !== null && angular.isDefined(kandiObj.uri) && kandiObj.uri.length > 0;
                };

                $scope.init();
            }]);
