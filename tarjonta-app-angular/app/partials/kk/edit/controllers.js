'use strict';

/* Controllers */
var app = angular.module('app.kk.edit.ctrl', ['Koodisto', 'ngResource', 'ngGrid', 'ui.autocomplete']);

app.controller('KKEditController', ['$scope', 'TarjontaService', 'Config', '$routeParams', 'OrganisaatioService',
    function FormTutkintoController($scope, tarjontaService, cfg, $routeParams, organisaatioService) {
        $scope.opetuskieli = 'kieli_fi';
        $scope.model = {};
        $scope.uiModel = {contactPerson: {}, ectsCoordinator: {}, env: cfg.env};

        $scope.init = function() {
            console.log("foobar", $routeParams);

            if ($routeParams.type === 'new') {
                // model default json structure.
                $scope.createEmptyAPIModel($scope.model);
                //create new koulutus
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
                $scope.throwError('unsupported $routeParams.type : ' + $routeParams.type + '.');
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

            apiModel.organisaatio = $scope.getOrganisationApiModel(apiModel, null);
            apiModel.yhteyshenkilos = $scope.convertPersonsUiModelToDto([uiModel.contactPerson, uiModel.ectsCoordinator]);

            /*
             * Convert koodisto komponent object to back-end object format.
             */

            //single select data

            apiModel.suunniteltuKesto = $scope.convertKoodistoComboSuunniteltuKesto(apiModel.suunniteltuKesto, uiModel.suunniteltuKestoTyyppi);

            //multi select data
            apiModel.teemas.meta = $scope.convertKoodistoMultiToKoodiUiDTOs(uiModel.teemas);
            apiModel.opetuskielis.meta = $scope.convertKoodistoMultiToKoodiUiDTOs(uiModel.opetuskielis);
            apiModel.pohjakoulutusvaatimukset.meta = $scope.convertKoodistoMultiToKoodiUiDTOs(uiModel.pohjakoulutusvaatimukset);
            apiModel.opetusmuodos.meta = $scope.convertKoodistoMultiToKoodiUiDTOs(uiModel.opetusmuodos);

            console.log(JSON.stringify(apiModel));
            return apiModel;
        };

        $scope.search = function() {
            console.log("search()", tarjontaService);

            tarjontaService.getKoulutus({oid: $routeParams.komoto}, function(data) {
                console.log("data loaded()", data);

                $scope.model = angular.copy(data);
                $scope.model.koulutuksenAlkamisPvm = Date.parse(data.koulutuksenAlkamisPvm);
                angular.forEach($scope.model.yhteyshenkilos, function(value, key) {
                    if (value.henkiloTyyppi === 'YHTEYSHENKILO') {
                        $scope.uiModel.contactPerson = $scope.converPersonObjectForUi(value);
                    } else if (value.henkiloTyyppi === 'ECTS_KOORDINAATTORI') {
                        $scope.uiModel.ectsCoordinator = $scope.converPersonObjectForUi(value);
                    } else {
                        $scope.throwError('Undefined henkilotyyppi : ' + value);
                    }
                })
            });
        };
        /**
         * Convert person data to UI format.
         * 
         * @param {type} person
         * @returns {person}
         */
        $scope.converPersonObjectForUi = function(person) {
            if ($scope.isNull(person)) {
                throw 'Contact percon cannot be null';
            }
            person.nimet = person.etunimet + ' ' + person.sukunimi;
            return person; //dummy
        }

        /**
         * Convert koodisto component data model to API meta model.
         * 
         * @param {type} json object map
         * @param {type} koodisto component object
         */
        $scope.convertKoodistoComboToMetaDTO = function(metaMap, kbObj) {
            if ($scope.isNull(kbObj)) {
                return {}; //return an empty object;
            }
            metaMap[kbObj.koodiUri] = $scope.convertKoodistoComboToKoodiDTO(kbObj);
        };

        /**
         * Convert koodisto component data model to API koodi model.
         * 
         * @param {type} koodisto component object
         */
        $scope.convertKoodistoComboToKoodiDTO = function(kbObj) {
            if ($scope.isNull(kbObj)) {
                return {}; //return an empty object;
            }
            return {"koodi": $scope.apiModelUri(kbObj.koodiUri, kbObj.koodiVersio)};
        };

        $scope.apiModelUri = function(uri, versio) {
            return {"uri": uri, "versio": versio};
        }

        $scope.convertKoodistoComboSuunniteltuKesto = function(apiModel, kbObj) {
            apiModel.koodi = $scope.apiModelUri(kbObj.koodiUri, kbObj.koodiVersio);
            return apiModel;
        };

        /**
         * Convert multi select koodisto component data model to API (json map) meta model.
         * 
         * @param {type} koodisto component objects
         */
        $scope.convertKoodistoMultiToKoodiUiDTOs = function(kbObjects) {
            var kcIndex = 0;
            var metaMap = {};

            for (; kcIndex < kbObjects.length; kcIndex++) {
                console.log("index", kcIndex, kbObjects[kcIndex], kbObjects);
                $scope.convertKoodistoComboToMetaDTO(metaMap, kbObjects[kcIndex]);
            }
            console.log("meta : ", metaMap, kbObjects);

            return metaMap;
        };

        $scope.convertPersonsUiModelToDto = function(arrPersons) {
            var arrOutputPersons = [];
            var i = 0;
            for (; i < arrPersons.length; i++) {
                var henkilo = arrPersons[i];
                if (Boolean(henkilo) && Boolean(henkilo.sahkoposti) && Boolean(henkilo.titteli) && Boolean(henkilo.puhelin)) {
                    if (Boolean(henkilo.nimet)) {
                        var lastname = henkilo.nimet.slice(henkilo.length - 1, henkilo.length);
                        var firstnames = henkilo.nimet.slice(henkilo.nimet, fruits.indexOf(lastname) - 1);
                        henkilo.etunimet = firstnames.join(' ');
                        henkilo.sukunimi = lastname;
                    }

                    delete henkilo.nimet;
                    delete henkilo.kielet;
                    arrOutputPersons.push(henkilo)
                }
            }

            return arrOutputPersons;
        };

        $scope.addMetaField = function(obj) {
            if ($scope.isNull(obj)) {
                return {"meta": {}};
            } else {
                obj.meta = {};
                return obj;
            }
        };

        $scope.deleteMetaField = function(obj) {
            if (!$scope.isNull(obj) && !$scope.isNull(obj.meta)) {
                delete obj.meta;
            }
        };

        $scope.createEmptyAPIModel = function(apiModel) {
            //Make sure that object has meta data object.

            apiModel.koulutuskoodi = $scope.createBaseUiField(null, null, null);
            apiModel.koulutusaste = $scope.createBaseUiField(null, null, null);
            apiModel.koulutusala = $scope.createBaseUiField(null, null, null);
            apiModel.opintoala = $scope.createBaseUiField(null, null, null);
            apiModel.tutkinto = $scope.createBaseUiField(null, null, null);
            apiModel.tutkintonimike = $scope.createBaseUiField(null, null, null);
            apiModel.eqf = $scope.createBaseUiField(null, null, null);

            apiModel.tunniste = '';
            apiModel.koulutuksenAlkamisPvm = new Date();

            //base koodi uri object
            apiModel.suunniteltuKesto = $scope.createBaseUiField(null, null, null);
            apiModel.koulutusohjelma = $scope.createBaseUiField(null, null, null);
            apiModel.teemas = $scope.createBaseUiField(null, null, null);
            apiModel.opetuskielis = $scope.createBaseUiField(null, null, null);
            apiModel.pohjakoulutusvaatimukset = $scope.createBaseUiField(null, null, null);
            apiModel.opetusmuodos = $scope.createBaseUiField(null, null, null);

            //base multilang meta data object;
            apiModel.koulutusohjelma = $scope.addMetaField(apiModel.koulutusohjelma);
            apiModel.teemas = $scope.addMetaField(apiModel.teemas);
            apiModel.opetuskielis = $scope.addMetaField(apiModel.opetuskielis);
            apiModel.pohjakoulutusvaatimukset = $scope.addMetaField(apiModel.pohjakoulutusvaatimukset);
            apiModel.opetusmuodos = $scope.addMetaField(apiModel.opetusmuodos);

            if ($scope.isNull(apiModel.koulutusmoduuliTyyppi)) {
                apiModel.opintojenMaksullisuus = false;
            }

            if ($scope.isNull(apiModel.koulutusmoduuliTyyppi)) {
                apiModel.koulutusmoduuliTyyppi = 'TUTKINTO';
            }

            if ($scope.isNull(apiModel.koulutusasteTyyppi)) {
                //TODO: currently only one type of objects.
                apiModel.koulutusasteTyyppi = 'AMMATTIKORKEAKOULUTUS';
            }

            if ($scope.isNull(apiModel.tila)) {
                apiModel.tila = 'LUONNOS';
            }
        }

        $scope.validateOutputData = function(m) {
            //Remove meta data objects
            $scope.deleteMetaField(m.tutkintonimike);
            $scope.deleteMetaField(m.koulutusaste);
            $scope.deleteMetaField(m.koulutuskoodi);
            $scope.deleteMetaField(m.tutkintonimike);

            //TODO: Missing koodito data relations.
            m.eqf = {arvo: "missing data", koodi: {uri: 'eqf_1', versio: '1', arvo: "eqf_1"}};
            m.tutkintonimike = {arvo: "missing data", koodi: {uri: 'tutkintonimikkeet_10005', versio: '1', arvo: "tutkintonimikkeet_10005"}};
        };

        $scope.getOrganisaatioOid = function(m) {
            var paramOid = $routeParams.org;
            var apiOid = m.organisaatioOid;

            if (!$scope.isNull(paramOid)) {
                return paramOid;
            } else if (!$scope.isNull(apiOid)) {
                return apiOid;
            } else {
                $scope.throwError('Tarjonta application error - no organisaation OID available.');
            }
        };

        $scope.createBaseUiFieldArvo = function(arvo) {
            return {"arvo": arvo};
        };

        $scope.createBaseUiField = function(uri, versio, arvo) {
            return {"arvo": arvo, "koodi": {"uri": uri, "versio": versio}};
        };

        $scope.getOrganisationApiModel = function(apiModel, nimi) {
            if ($scope.isNull(apiModel)) {
                throw 'API model must be object, or empty object';
            }
            //organisation OID selector logic : update -> model oid, create -> param oid 
            var orgOid = $scope.getOrganisaatioOid(apiModel);

            //fetch org name and OID from Organisation service
            return {"oid": orgOid, "nimi": nimi};
        };

        $scope.isNull = function(obj) {
            if (obj === null || typeof obj === 'undefined') {
                return true;
            } else {
                return false;
            }
        };

        $scope.throwError = function(msg) {
            throw 'Tarjonta application error - ' + msg;
        };

        $scope.init(); //initialization


    }]);
