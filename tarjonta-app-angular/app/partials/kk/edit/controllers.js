'use strict';

var TEST = {
    "organisaatioOid" : "1.2.246.562.10.900951416610",
    "koulutuskoodi": {
        "koodi": {
            "uri": "koulutus_351203",
            "versio": "1",
            "arvo": 671118
        },
        "arvo": "Talotekniikan perustutkinto"
    },
    "koulutusmoduuliTyyppi": "TUTKINTO_OHJELMA",
    "koulutusaste": {
        "koodi": {
            "uri": "koulutusasteoph2002_62",
            "versio": "1",
            "arvo": "62"
        },
        "arvo": "Ammattikorkeakoulututkinto"
    },
    "koulutusala": {
        "koodi": {
            "uri": "koulutusalaoph2002_7",
            "versio": "1",
            "arvo": "7"
        },
        "arvo": "Sosiaali-, terveys- ja liikunta-ala"
    },
    "opintoala": {
        "koodi": {
            "uri": "opintoalaoph2002_705",
            "versio": "1",
            "arvo": "705"
        },
        "arvo": "Kuntoutus ja liikunta"
    },
    "tutkinto": {
        "koodi": {
            "uri": "tutkinto_547",
            "versio": "1",
            "arvo": "547"
        },
        "arvo": "Sosiaali- ja terveysalan ammattikorkeakoulututkinto, Kuntoutuksen ohjaaja (AMK)"
    },
    "tutkintonimike": {
        "koodi": {
            "uri": "tutkintonimikkeet_10147",
            "versio": "1",
            "arvo": "10147"
        },
        "arvo": "Rakennuspeltiseppä"
    },
    "eqf": {
        "arvo": "4#1"
    },
    "tekstis": {
        "PATEVYYS": {
            "tekstis": [
            ]
        }
    },
    "yhteyshenkilos": [
        {
            "etunimet": "Päivi",
            "sukunimi": "Hiltunen",
            "titteli": "opinto-ohjaaja",
            "sahkoposti": "etunimi.sukunimi@omnia.fi",
            "puhelin": "0468515025",
            "henkiloTyyppi": "YHTEYSHENKILO"
        }
    ],
    "koulutusohjelma": {
        "koodi": {
            "uri": "koulutusohjelmaamm_16481",
            "versio": "1",
            "arvo": "16481"
        },
        "arvo": "Eristyksen ja rakennuspeltiasennuksen koulutusohjelma, rakennuspeltiseppä",
        "tekstis": [
            {
                "koodi": {
                    "uri": "kieli_sv",
                    "versio": "-1",
                    "arvo": "SV"
                },
                "arvo": "Utbildningsprogram för isolering och montering av byggnadsplåt, byggnadsplåtslagare"
            },
            {
                "koodi": {
                    "uri": "kieli_fi",
                    "versio": "-1",
                    "arvo": "FI"
                },
                "arvo": "Eristyksen ja rakennuspeltiasennuksen koulutusohjelma, rakennuspeltiseppä"
            }
        ]
    },
    "suunniteltuKesto": {
        "arvo": null,
        "koodi": {
            "arvo": null,
            "uri": "suunniteltukesto_02",
            "versio": 1
        }
    },
    "suunniteltuKestoTyyppi": {
        "arvo": "Vuosi",
        "koodi": {
            "arvo": null,
            "uri": "kieli_fi", //dummy data
            "versio": 1
        }
    },
    "opetuskielis": {
        "tekstis": [
            {
                "arvo": null,
                "koodi": {
                    "arvo": null,
                    "uri": "kieli_gg",
                    "versio": 1
                }
            }
        ]
    },
    "opetusmuodos": {
        "tekstis": [
            {
                "arvo": null,
                "koodi": {
                    "arvo": null,
                    "uri": "opetusmuoto_p",
                    "versio": 1
                }
            },
            {
                "arvo": null,
                "koodi": {
                    "arvo": null,
                    "uri": "opetusmuoto_l",
                    "versio": 1
                }
            }
        ]
    },
    "opintojenMaksullisuus": false,
    "pohjakoulutusvaatimukset": {
        "tekstis": [
        ]
    },
    "teemas": {
        "tekstis": [
            {
                "arvo": null,
                "koodi": {
                    "arvo": null,
                    "uri": "teemat_2",
                    "versio": 1
                }
            },
            {
                "arvo": null,
                "koodi": {
                    "arvo": null,
                    "uri": "teemat_5",
                    "versio": 1
                }
            }
        ]
    },
    "opintojenLaajuus": {
        "arvo": "empty"
    },
    "koulutuksenAlkamisPvm": 1389052800000,
    "api_VERSION": 4,
    "$promise": {
    },
    "$resolved": true,
    "tunniste": "aaaaaa"
};



/* Controllers */

var app = angular.module('app.kk.edit.ctrl', ['Koodisto', 'ngResource', 'ngGrid']);

app.controller('KKEditController', ['$scope', 'TarjontaService', 'Config',  '$routeParams',
    function FormTutkintoController($scope, tarjontaService, cfg, $routeParams) {
        $scope.searchByOid = "1.2.246.562.5.2013091114080489552096";
        $scope.opetuskieli = 'kieli_fi';
        $scope.model = {};
        $scope.uiModel = {contactPerson: {}, ectsCoordinator: {}, env: cfg.env};
        
        console.log("foobar", $routeParams);
  
        console.log($routeParams.orgOid);

        $scope.loadKoodistoData = function() {
            tarjontaService.getKoulutuskoodiRelations({koulutuskoodiUri: $scope.model.koulutuskoodi.koodi.arvo}, function(data) {
                console.log("loadKoodistoData()");
                var koodistoData = angular.copy(data);
                console.log(koodistoData);
                $scope.model.koulutusaste = koodistoData.koulutusaste;
                $scope.model.koulutusala = koodistoData.koulutusala;
                $scope.model.opintoala = koodistoData.opintoala;
                $scope.model.tutkinto = koodistoData.tutkinto;
            });
        };

        $scope.save = function() {
            console.log("Save");
            console.log($scope.uiModel);
            //tarjontaService.insertKoulutus($scope.saveModelConverter());
            tarjontaService.insertKoulutus(TEST);

        };

        $scope.saveModelConverter = function() {
            var m = angular.copy($scope.model);
            var uim = angular.copy($scope.uiModel);

            delete  m.koulutusala.tekstis;
            delete  m.koulutusaste.tekstis;
            delete  m.koulutuskoodi.tekstis;
            delete  m.tutkintonimike.tekstis;

            m.yhteyshenkilos = $scope.convertPersonsUiModelToDto([uim.contactPerson, uim.ectsCoordinator]);

            //Convert koodisto komponent object to back-end object format .
            m.suunniteltuKestoTyyppi = $scope.convertKoodistoComboToKoodiUiDTO({}); //TODO: fix this
            m.suunniteltuKesto = $scope.convertKoodistoComboToKoodiUiDTO(uim.suunniteltuKesto);
            m.teemas.tekstis = $scope.convertKoodistoMultiToKoodiUiDTOs(uim.teemas);
            m.opetuskielis.tekstis = $scope.convertKoodistoMultiToKoodiUiDTOs(uim.opetuskielis);
            m.pohjakoulutusvaatimukset.tekstis = $scope.convertKoodistoMultiToKoodiUiDTOs(uim.pohjakoulutusvaatimukset);
            m.opetusmuodos.tekstis = $scope.convertKoodistoMultiToKoodiUiDTOs(uim.opetusmuodos);

            console.log(JSON.stringify(m));

            return m;
        };

        $scope.search = function() {
            console.log("search()");
            console.log(tarjontaService);

            tarjontaService.getKoulutus({oid: $scope.searchByOid}, function(data) {
                console.log("data loaded()");
                console.log(data);
                $scope.model = angular.copy(data);
                $scope.model.koulutuksenAlkamisPvm = Date.parse(data.koulutuksenAlkamisPvm);
                $scope.model.koulutuskoodi.koodi.arvo = 671118
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
        $scope.search();

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

            return {arvo: null, koodi: {arvo: null, uri: kbObj.koodiUri, versio: kbObj.koodiVersio}};
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

    }]);
