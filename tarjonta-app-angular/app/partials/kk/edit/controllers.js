'use strict';

/* Controllers */
var app = angular.module('app.kk.edit.ctrl', ['Koodisto', 'Yhteyshenkilo', 'ngResource', 'ngGrid']);

app.controller('KKEditController', ['$scope', 'TarjontaService', 'Config', '$routeParams', 'OrganisaatioService', '$window', 'TarjontaConverterFactory', 'Koodisto',
    function FormTutkintoController($scope, tarjontaService, cfg, $routeParams, organisaatioService, $window, converter, koodisto) {
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
            converter.createAPIModel($scope.model);
            converter.createUiModels($scope.uiModel);

            /*
             * LOAD KOODISTO DATA
             */

            $scope.searchKoodisByKoodistoUri(cfg.env['koodisto-uris.opintojenLaajuusarvo'], $scope.uiModel.opintojenLaajuus, $scope.locale);
            $scope.searchKoodisByKoodistoUri(cfg.env['koodisto-uris.suunniteltuKesto'], $scope.uiModel.suunniteltuKesto, $scope.locale);
            $scope.searchKoodisByKoodistoUri(cfg.env['koodisto-uris.kieli'], $scope.uiModel.opetuskielis, $scope.locale);
            $scope.searchKoodisByKoodistoUri(cfg.env['koodisto-uris.opetusmuoto'], $scope.uiModel.opetusmuodos, $scope.locale);
            $scope.searchKoodisByKoodistoUri(cfg.env['koodisto-uris.pohjakoulutusvaatimus'], $scope.uiModel.pohjakoulutusvaatimukset, $scope.locale);
            $scope.searchKoodisByKoodistoUri(cfg.env['koodisto-uris.teemat'], $scope.uiModel.teemas, $scope.locale);

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

                $scope.loadKoodistoData();

            } else if ($routeParams.type === 'load') {
                //load data for edit
                $scope.search();
            } else {
                converter.throwError('unsupported $routeParams.type : ' + $routeParams.type + '.');
            }
        };

        $scope.loadKoodistoData = function() {
            tarjontaService.getKoulutuskoodiRelations({koulutuskoodiUri: $routeParams.koulutuskoodi}, function(data) {
                var koodistoData = angular.copy(data);
                console.log("loadKoodistoData()", koodistoData);

                $scope.model.koulutuskoodi = koodistoData.koulutuskoodi;
                $scope.model.koulutusaste = koodistoData.koulutusaste;
                $scope.model.koulutusala = koodistoData.koulutusala;
                $scope.model.opintoala = koodistoData.opintoala;
                $scope.model.tutkinto = koodistoData.tutkinto;
                $scope.model.tutkintonimike = koodistoData.tutkintonimike;
                $scope.model.eqf = koodistoData.eqf;
            });
        };

        /**
         * Insert koulutus data to tarjonta-service database. 
         */
        $scope.insert = function() {
            console.log("insert", $scope.uiModel);
            tarjontaService.insertKoulutus($scope.saveModelConverter());
            //tarjontaService.insertKoulutus(TEST);
        };

        $scope.saveModelConverter = function() {
            var apiModel = angular.copy($scope.model);
            var uiModel = angular.copy($scope.uiModel);

            $scope.validateOutputData(apiModel);
            /*
             * DATA CONVERSIONS FROM UI MODEL TO API MODEL
             * Convert person object to back-end object format.
             */

            apiModel.organisaatio = converter.getOrganisationApiModel(apiModel, null);
            apiModel.yhteyshenkilos = converter.convertPersonsUiModelToDto([uiModel.contactPerson, uiModel.ectsCoordinator]);

            /*
             * Convert koodisto komponent object to back-end object format.
             */
            //single select data
            apiModel.suunniteltuKesto = converter.convertKoodistoCombo(apiModel.suunniteltuKesto.arvo, uiModel.suunniteltuKesto.data);
            apiModel.opintojenLaajuus = converter.convertKoodistoCombo(null, uiModel.opintojenLaajuus.data);

            //multi select data
            apiModel.teemas.meta = converter.convertKoodistoMultiToKoodiUiDTOs(uiModel.teemas);
            apiModel.opetuskielis.meta = converter.convertKoodistoMultiToKoodiUiDTOs(uiModel.opetuskielis);
            apiModel.pohjakoulutusvaatimukset.meta = converter.convertKoodistoMultiToKoodiUiDTOs(uiModel.pohjakoulutusvaatimukset);
            apiModel.opetusmuodos.meta = converter.convertKoodistoMultiToKoodiUiDTOs(uiModel.opetusmuodos);

            console.log(JSON.stringify(apiModel));
            return apiModel;
        };

        $scope.search = function() {
            console.log("search()", tarjontaService);

            tarjontaService.getKoulutus({oid: $routeParams.komoto}, function(data) {
                console.log("data loaded()", data);

                $scope.model = angular.copy(data);
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

        $scope.updateMultiSelectKoodistoData = function(uiModel, apiModel) {
            $scope.updateKoodiUrisToUiModel(uiModel.teemas, apiModel.teemas);
            $scope.updateKoodiUrisToUiModel(uiModel.opetuskielis, apiModel.opetuskielis);
            $scope.updateKoodiUrisToUiModel(uiModel.pohjakoulutusvaatimukset, apiModel.pohjakoulutusvaatimukset);
            $scope.updateKoodiUrisToUiModel(uiModel.opetusmuodos, apiModel.opetusmuodos);
        };

        $scope.updateKoodiUrisToUiModel = function(uiModel, apiModel) {
            angular.forEach(apiModel.meta, function(value, key) {
                this.push(value.koodi.uri);
            }, uiModel.uris);
        };

        $scope.validateOutputData = function(m) {
            //Remove meta data objects
            converter.deleteMetaField(m.tutkintonimike);
            converter.deleteMetaField(m.koulutusaste);
            converter.deleteMetaField(m.koulutuskoodi);
            converter.deleteMetaField(m.tutkintonimike);

            //TODO: Missing koodito data relations.
            m.eqf = {arvo: "missing data", koodi: {uri: 'eqf_1', versio: '1', arvo: "eqf_1"}};
            m.tutkintonimike = {arvo: "missing data", koodi: {uri: 'tutkintonimikkeet_10005', versio: '1', arvo: "tutkintonimikkeet_10005"}};
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

        $scope.searchKoodisByKoodistoUri = function(koodistouri, uiModel, locale) {
            var koodisPromise = koodisto.getAllKoodisWithKoodiUri(koodistouri, locale);
            koodisPromise.then(function(koodisParam) {
                console.log(koodisParam);
                uiModel.data = koodisParam;
            });
        };

        $scope.searchKoodiByKoodiUri = function(koodiUri, uiModel) {
            var i = 0;
            var koodiObjects = uiModel.data;

            for (; i < koodiObjects.length; i++) {
                if (koodiObjects[i].koodiUri === koodiUri) {
                    return koodiObjects[i];
                }
            }
        };

        $scope.removeKoodiByKoodiUri = function(koodiUri, uiModel) {
            var i = 0;
            var koodiObjects = uiModel.uris;

            for (; i < koodiObjects.length; i++) {
                if (koodiObjects[i] === koodiUri) {
                    koodiObjects.splice(i, 1);
                }
            }
        };

        $scope.init(); //initialization
    }]);
