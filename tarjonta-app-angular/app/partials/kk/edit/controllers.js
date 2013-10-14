'use strict';

/* Controllers */
var app = angular.module('app.kk.edit.ctrl', ['Koodisto', 'Yhteyshenkilo', 'ngResource', 'ngGrid']);

app.controller('KKEditController', ['$scope', 'TarjontaService', 'Config', '$routeParams', 'OrganisaatioService', '$window', 'TarjontaConverterFactory', 'Koodisto', '$modal',
    function FormTutkintoController($scope, tarjontaService, cfg, $routeParams, organisaatioService, $window, converter, koodisto, $modal) {
        $scope.opetuskieli = 'kieli_fi';
        $scope.model = {};
        $scope.uiModel = {contactPerson: {}, ectsCoordinator: {}};
        $scope.config = {env: cfg.env};
        $scope.locale = "FI"

        $scope.init = function() {
            console.log("$routeParams", $routeParams);

            /*
             * INITIALISE DATA MODELS
             */
            converter.createAPIModel($scope.model, cfg.app.userLanguages);
            converter.createUiModels($scope.uiModel);

            angular.forEach($scope.model.koulutusohjelma.meta, function(val, key) {
                $scope.searchKoodi(val, cfg.env['koodisto-uris.kieli'], key, $scope.locale);
            });

            /*
             * LOAD KOODISTO DATA
             */
            //missing koodisto data:
            angular.forEach(converter.STRUCTURE.COMBO, function(value, key) {
                $scope.searchKoodisByKoodistoUri($scope.uiModel[key], cfg.env[value.koodisto], $scope.locale);
            });

            angular.forEach(converter.STRUCTURE.MCOMBO, function(value, key) {
                $scope.searchKoodisByKoodistoUri($scope.uiModel[key], cfg.env[value.koodisto], $scope.locale);
            });

            /*
             * HANDLE ROUTING
             */
            if ($routeParams.type === 'new') {
                var orgOid = $scope.getOrganisaatioOid({});
                $scope.model.organisaatio = $scope.getOrganisationApiModel({}, "");
                var promiseOrg = organisaatioService.nimi(orgOid);
                promiseOrg.then(function(vastaus) {
                    console.log("result returned, hits:", vastaus);
                    $scope.model.organisaatio.nimi = vastaus;
                });

                $scope.loadRelationKoodistoData();

            } else if ($routeParams.type === 'load') {
                //load data for edit
                $scope.search();
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
        $scope.save = function() {
            if (converter.isNull($scope.uiModel.oid) || $routeParams.type === 'new') {
                $scope.insert();
            } else {
                $scope.update();
            }
        };

        $scope.insert = function() {
            console.log("trying to insert...");
            tarjontaService.insertKoulutus($scope.saveModelConverter(), function(resp) {
                //Callback
                console.log("Insert data response from POST: %j", resp);
                $scope.model.oid = resp.oid;
            });
        };

        $scope.update = function() {
            console.log("trying to update...");
            arjontaService.updateKoulutus($scope.saveModelConverter(), function(resp) {
                //Callback
                console.log("Update data response from PUT: %j", resp);
            });
        };

        $scope.saveModelConverter = function() {
            var apiModel = angular.copy($scope.model);
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


            tarjontaService.getKoulutus({oid: $routeParams.komoto}, function(data) {
                console.log("data loaded()", data);
                $scope.model = angular.copy(data);
                converter.createMetaLanguages($scope.model.koulutusohjelma, cfg.app.userLanguages);

                if (converter.isNull($scope.model.koulutusohjelma)) {
                    angular.forEach(data.meta, function(value, key) {
                    });
                }

                $scope.updateMultiSelectKoodistoData($scope.uiModel, $scope.model);
                $scope.model.koulutuksenAlkamisPvm = Date.parse(data.koulutuksenAlkamisPvm);
                angular.forEach($scope.model.yhteyshenkilos, function(value, key) {
                    if (value.henkiloTyyppi === 'YHTEYSHENKILO') {
                        converter.uiModel.contactPerson = $scope.converPersonObjectForUi(value);
                    } else if (value.henkiloTyyppi === 'ECTS_KOORDINAATTORI') {
                        converter.uiModel.ectsCoordinator = $scope.converPersonObjectForUi(value);
                    } else {
                        converter.throwError('Undefined henkilotyyppi : ' + value);
                    }
                })
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
                apiModel.koodi.versio = data.koodiVersion;
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
                templateUrl: 'partials/kk/edit/selectTutkintoOhjelma.html',
                controller: 'SelectTutkintoOhjelmaController'
            });

            modalInstance.result.then(function(selectedItem) {
                console.log('Ok, dialog closed: ' + selectedItem.koodiNimi);
                console.log('Koodiarvo is: ' + selectedItem.koodiArvo);
                if (selectedItem.koodiUri != null) {
                    //$scope.model.koulutuskoodi = selectedItem;
                    $scope.model.koulutuskoodi.koodi = selectedItem;
                }
            }, function() {
                console.log('Cancel, dialog closed');
            });
        };

        //initialization
        $scope.init();
    }]);
