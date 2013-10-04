'use strict';

/* Controllers */

var app = angular.module('app.kk.edit.ctrl', ['Koodisto', 'ngResource', 'ngGrid', 'ui.autocomplete']);

app.controller('KKEditController', ['$scope', 'TarjontaService', 'Config', '$routeParams',
    function FormTutkintoController($scope, tarjontaService, cfg, $routeParams) {
        $scope.searchByOid = "1.2.246.562.5.2013091114080489552096";
        $scope.opetuskieli = 'kieli_fi';
        $scope.model = {};
        $scope.uiModel = {contactPerson: {}, ectsCoordinator: {}, env: cfg.env};

        $scope.init = function() {
            console.log("foobar", $routeParams);

            if ($routeParams.type === 'new') {
                //create new koulutus
                $scope.loadKoodistoData();
            } else if ($routeParams.type === 'load') {
                //load data for edit
                $scope.search();
            } else {
                throw 'Unsupported data type : ' + $routeParams.type + '.';

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
            var m = angular.copy($scope.model);
            var uim = angular.copy($scope.uiModel);


            m.yhteyshenkilos = $scope.convertPersonsUiModelToDto([uim.contactPerson, uim.ectsCoordinator]);

            //Convert koodisto komponent object to back-end object format .
            m.suunniteltuKestoTyyppi = $scope.convertKoodistoComboToKoodiUiDTO({}); //TODO: fix this
            m.suunniteltuKesto = $scope.convertKoodistoComboToKoodiUiDTO(uim.suunniteltuKesto);
            m.teemas.meta = $scope.convertKoodistoMultiToKoodiUiDTOs(uim.teemas);
            m.opetuskielis.meta = $scope.convertKoodistoMultiToKoodiUiDTOs(uim.opetuskielis);
            m.pohjakoulutusvaatimukset.meta = $scope.convertKoodistoMultiToKoodiUiDTOs(uim.pohjakoulutusvaatimukset);
            m.opetusmuodos.meta = $scope.convertKoodistoMultiToKoodiUiDTOs(uim.opetusmuodos);

            //TODO:
            m.eqf = {arvo: "missing data", koodi: {uri: 'eqf_1', versio: '1', arvo: "eqf_1"}};
            m.tutkintonimikkeet = {arvo: "missing data", koodi: {uri: 'tutkintonimikkeet_10005', versio: '1', arvo: "tutkintonimikkeet_10005"}};

            //Delete all not needed data objects, before http post/put.
            delete  m.koulutusala.meta;
            delete  m.koulutusaste.meta;
            delete  m.koulutuskoodi.meta;
            delete  m.tutkintonimike.meta;


            console.log(JSON.stringify(m));


            return m;
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
                        throw 'Undefined henkilotyyppi : ' + value;
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
            if (person === null || typeof person === 'undefined') {
                throw 'Contact percon cannot be null';
            }
            person.nimet = person.etunimet + ' ' + person.sukunimi;
            return person; //dummy
        }

        $scope.convertKoodistoComboToKoodiUiDTO = function(kbObj) {
            if (kbObj === null || typeof kbObj === 'undefined') {
                return {}; //return an empty object;
            }
            var uri = kbObj.koodiUri;

            return {uri: {"koodi": {"arvo": null, "uri": uri, "versio": kbObj.koodiVersio}}};
        };

        $scope.convertKoodistoMultiToKoodiUiDTOs = function(kbObjects) {
            var arr = [];
            var i = 0;
            console.log(kbObjects);

            for (; i < kbObjects.length; i++) {
                console.log(i, kbObjects[i]);
                arr.push($scope.convertKoodistoComboToKoodiUiDTO(kbObjects[i]));
            }

            return arr;
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

        $scope.init(); //initialization
    }]);
