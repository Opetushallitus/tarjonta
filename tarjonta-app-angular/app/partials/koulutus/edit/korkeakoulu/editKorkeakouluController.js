var app = angular.module('app.edit.ctrl.kk', ['Koodisto', 'Yhteyshenkilo', 'ngResource', 'ngGrid', 'imageupload', 'MultiSelect', 'OrderByNumFilter', 'localisation', 'MonikielinenTextField', 'ControlsLayout']);
app.controller('EditKorkeakouluController',
    ['$route', '$timeout', '$scope', '$location', '$log', 'TarjontaService', 'Config', '$routeParams', 'OrganisaatioService', 'LocalisationService',
        '$window', 'KoulutusConverterFactory', 'Koodisto', '$modal', 'PermissionService', 'dialogService', 'CommonUtilService',
        function EditKorkeakouluController($route, $timeout, $scope, $location, $log, TarjontaService, cfg, $routeParams, organisaatioService, LocalisationService,
            $window, converter, koodisto, $modal, PermissionService, dialogService, CommonUtilService) {
            $log = $log.getInstance("EditKorkeakouluController");
            $scope.tutkintoDialogModel = {};

            $scope.init = function() {
                $log.debug("init");

                /*
                 * INITIALIZE PAGE CONFIG
                 */
                $scope.commonCreatePageConfig($routeParams, $route.current.locals.koulutusModel.result);

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
                    $scope.commonLoadModelHandler($scope.koulutusForm, model, uiModel, $scope.CONFIG.TYYPPI);

                    /*
                     * CUSTOM LOGIC
                     */
                    $scope.loadRelationKoodistoData(model, uiModel, model.koulutuskoodi.uri);

                    /*
                     * Load data to multiselect fields
                     * remove version data from the list
                     */
                    angular.forEach(converter.STRUCTURE[$scope.CONFIG.TYYPPI].MCOMBO, function(value, key) {
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
                    $scope.commonNewModelHandler($scope.koulutusForm, model, uiModel, $scope.CONFIG.TYYPPI);
                    $scope.loadRelationKoodistoData(model, uiModel, $routeParams.koulutuskoodi);
                } else {
                    converter.throwError('unsupported $routeParams.type : ' + $routeParams.type + '.');
                }

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
                $scope.setUiModel(uiModel);
                $scope.setModel(model);
            };

            $scope.loadRelationKoodistoData = function(apiModel, uiModel, koulutuskoodi) {
                TarjontaService.getKoulutuskoodiRelations({
                    uri: koulutuskoodi,
                    koulutustyyppi: $scope.CONFIG.KOULUTUSTYYPPI,
                    defaults: "koulutustyyppi:" + $scope.CONFIG.KOULUTUSTYYPPI,
                    languageCode: $scope.koodistoLocale}, function(data) {
                    var restRelationData = data.result;
                    angular.forEach(converter.STRUCTURE[$scope.CONFIG.TYYPPI].RELATION, function(value, key) {
                        apiModel[key] = restRelationData[key];
                    });

                    angular.forEach(converter.STRUCTURE[$scope.CONFIG.TYYPPI].RELATIONS, function(value, key) {
                        if (angular.isDefined(restRelationData[key])) {
                            uiModel[key].meta = restRelationData[key].meta;

                            if (angular.isUndefined(value.skipApiModel) && !angular.isUndefined(apiModel[key]) && !angular.isUndefined(apiModel[key].uris)) {
                                uiModel[key].uris = _.keys(apiModel[key].uris); //load uris
                            }
                        } else {
                            $log.error("Error, koodisto not found : '" + key + "' = '" + angular.toJson(value) + " " + angular.toJson(restRelationData));
                        }
                    });
                });
            };
            /**
             * Save koulutus data to tarjonta-service database.
             * TODO: strict data validation, exception handling and optimistic locking
             */
            $scope.saveLuonnos = function() {
                $scope.saveByStatus('LUONNOS', $scope.koulutusForm, $scope.CONFIG.TYYPPI, $scope.customCallbackAfterSave);
            };
            $scope.saveValmis = function() {
                $scope.saveByStatus('VALMIS', $scope.koulutusForm, $scope.CONFIG.TYYPPI, $scope.customCallbackAfterSave);
            };

            $scope.customCallbackAfterSave = function(saveResponse) {
                if (saveResponse.status === 'OK') {
                    $scope.$broadcast("onImageUpload", ""); //save images
                    $scope.getLisatietoKielet( $scope.model,  $scope.uiModel, true);
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
