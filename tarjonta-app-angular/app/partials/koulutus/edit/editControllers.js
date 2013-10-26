
var app = angular.module('app.edit.ctrl', ['Koodisto', 'Yhteyshenkilo', 'ngResource', 'ngGrid', 'imageupload', 'MultiSelect']);
app.controller('BaseEditController',
        ['$scope', '$location', '$log', 'TarjontaService', 'Config', '$routeParams', 'OrganisaatioService',
            '$window', 'TarjontaConverterFactory', 'Koodisto', '$modal',
            function BaseEditController($scope, $location, $log, tarjontaService, cfg, $routeParams, organisaatioService, $window, converter, koodisto, $modal) {
                $log.info("BaseEditController()");
                // TODO maybe fix this, model, xmodel, uiModel, ... all to "model", "model.uimodel", "model.locale", model.xxx ?
                $scope.model = {
                };
                $scope.xmodel = {
                    routeParams: $routeParams,
                    collapse: {
                        model: true
                    },
                    koulutus: $scope.koulutusx, // preloaded in route resolve
                    foo: "bar"
                };
                $scope.opetuskieli = 'kieli_fi';
                $scope.uiModel = {};
                $scope.config = {env: cfg.env, app: cfg.app, 'locationPath': $location.path()};
                $scope.locale = "FI"


                $scope.init = function() {

                    /*
                     * INITIALISE DATA MODELS
                     */

                    converter.createAPIModel($scope.model, cfg.app.userLanguages);
                    converter.createUiModels($scope.uiModel);
                    /*
                     * Init language texts, like 'suomi' 'englanti' etc.
                     */
                    angular.forEach($scope.model.koulutusohjelma.meta, function(val, key) {
                        if (angular.isUndefined(val.koodi.kaannos)) {
                            $scope.searchKoodi(val, cfg.env['koodisto-uris.kieli'], key, $scope.locale);
                        }
                    });
                    /*
                     * LOAD KOODISTO DATA
                     */
                    angular.forEach(converter.STRUCTURE.COMBO, function(value, key) {
                        $scope.searchKoodisByKoodistoUri($scope.uiModel[key], cfg.env[value.koodisto], $scope.locale);
                    });
                    angular.forEach(converter.STRUCTURE.MCOMBO, function(value, key) {
                        $scope.searchKoodisByKoodistoUri($scope.uiModel[key], cfg.env[value.koodisto], $scope.locale);
                    });
                    /*
                     * HANDLE ROUTING
                     */
                    if (!angular.isUndefined($routeParams.id)) {
                        //load data for edit
                        $scope.search();
                    } else if (!angular.isUndefined($routeParams.org)) {
                        var orgOid = $scope.getOrganisaatioOid({});
                        $scope.model.organisaatio = $scope.getOrganisationApiModel({}, "");
                        var promiseOrg = organisaatioService.nimi(orgOid);
                        promiseOrg.then(function(vastaus) {
                            console.log("result returned, hits:", vastaus);
                            $scope.model.organisaatio.nimi = vastaus;
                        });
                        $scope.loadRelationKoodistoData();
                    } else {
                        converter.throwError('unsupported $routeParams.type : ' + $routeParams.type + '.');
                    }
                };
                $scope.loadRelationKoodistoData = function() {
                    tarjontaService.getKoulutuskoodiRelations({koulutuskoodiUri: $routeParams.koulutuskoodi}, function(data) {
                        var koodistoData = angular.copy(data);
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

                    var apiModel = $scope.saveModelConverter(tila);

                    if (converter.isNull($scope.uiModel.oid) || $routeParams.type === 'new') {
                        tarjontaService.insertKoulutus(apiModel, function(resp) {
                            //Callback
                            console.log("Insert data response from POST: %j", resp);
                            $scope.model.oid = resp.oid;
                            $location.path('/koulutus/' + $scope.model.oid + '/edit/');
                        });
                    } else {
                        console.log("trying to update...");
                        tarjontaService.updateKoulutus(apiModel, function(resp) {
                            //Callback
                            console.log("Update data response from PUT: %j", resp);
                        });

                        var KomoKuvaus = tarjontaService.resourceKomoKuvaus($scope.model.oid);
                        KomoKuvaus.save({tekstis: $scope.uiModel.tekstis}, function(res) {
                            console.log("save success kuvaus", res);
                        });

                    }
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
                $scope.search = function() {
                    console.log("search()", tarjontaService);
                    var komotoOid = $routeParams.id;

                    tarjontaService.getKoulutus({oid: komotoOid}, function(data) {
                        console.log("data loaded()", data);
                        $scope.model = angular.copy(data);
                        converter.createMetaLanguages($scope.model.koulutusohjelma, cfg.app.userLanguages);
                        angular.forEach($scope.model.koulutusohjelma.meta, function(val, key) {
                            if (angular.isUndefined(val.koodi.kaannos)) {
                                $scope.searchKoodi(val, cfg.env['koodisto-uris.kieli'], key, $scope.locale);
                            }
                        });

                        $scope.updateMultiSelectKoodistoData($scope.uiModel, $scope.model);
                        $scope.model.koulutuksenAlkamisPvm = Date.parse(data.koulutuksenAlkamisPvm);
                        angular.forEach($scope.model.yhteyshenkilos, function(value, key) {
                            if (value.henkiloTyyppi === 'YHTEYSHENKILO') {
                                $scope.uiModel.contactPerson = converter.converPersonObjectForUi(value);
                            } else if (value.henkiloTyyppi === 'ECTS_KOORDINAATTORI') {
                                $scope.uiModel.ectsCoordinator = converter.converPersonObjectForUi(value);
                            } else {
                                converter.throwError('Undefined henkilotyyppi : ', value);
                            }
                        })
                    });

                    var KomoKuvaus = tarjontaService.resourceKomoKuvaus();
                    KomoKuvaus.get({oid: komotoOid}, function(res) {
                        console.log("success kuvaus", res);
                        if (!angular.isUndefined(res) && !angular.isUndefined(res.tekstis)) {
                            for (var i = 0; i < res.tekstis.length; i++) {
                                $scope.uiModel.tekstis = res.tekstis[i];
                            }
                        }

                    });
                };
                /**
                 * Handle data load for koodisto data combo boxes.
                 *
                 * @param {type} uiModel
                 * @param {type} apiModel
                 */
                $scope.updateMultiSelectKoodistoData = function(uiModel, apiModel) {
                    angular.forEach(converter.STRUCTURE.COMBO, function(value, key) {
                        console.log(key, value);
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
                    console.log(apiModel);
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
                            console.log(converter.STRUCTURE.MLANG, "!==", key)
                            //MLANG objects needs the meta fields
                            angular.forEach(value, function(value, key) {

                                converter.deleteMetaField(m[key]);
                            });
                        }
                    });
                };
                $scope.getOrganisaatioOid = function(m) {
                    var paramOid = $routeParams.org;
                    var apiOid = m.organisaatioOid;
                    if (!converter.isNull(paramOid)) {
                        return paramOid;
                    } else if (!converter.isNull(apiOid)) {
                        return apiOid;
                    } else {
                        converter.throwError('Tarjonta application error - no organisaation OID available.');
                    }
                };
                $scope.getOrganisationApiModel = function(apiModel, nimi) {
                    if (converter.isNull(apiModel)) {
                        throw 'API model must be object, or empty object';
                    }
                    //organisation OID selector logic : update -> model oid, create -> param oid
                    var orgOid = $scope.getOrganisaatioOid(apiModel);
                    //fetch org name and OID from Organisation service
                    return {"oid": orgOid, "nimi": nimi};
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
                    $location.path("/koulutus/" + $scope.model.oid);
                };
                console.log("preloaded data!", $scope.xmodel.koulutus);
                $scope.init();
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

                $scope.getKuvausByKeyAndLanguageUri = function(key, kieliuri) {
                    if (!angular.isUndefined(kieliuri) && !angular.isUndefined(key) && !angular.isUndefined($scope.uiModel.tekstis) && !angular.isUndefined($scope.uiModel.tekstis[key])) {
                        return $scope.uiModel.tekstis[key].meta[kieliuri].koodi.arvo;
                    }
                    return '';
                };

            }]);