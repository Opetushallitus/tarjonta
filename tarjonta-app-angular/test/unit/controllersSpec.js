'use strict';

/* jasmine specs for controllers go here */

// Esimerkki:
//describe('controllers', function(){
//  beforeEach(module('app.controllers'));
//
//
//  it('should ....', inject(function() {
//    //spec body
//  }));
//
//  it('should ....', inject(function() {
//    //spec body
//  }));
//});

describe('Edit koulutus testeja', function() {

    beforeEach(module('ngGrid'));
    var CONFIG_ENV_MOCK = {
        "env": {
            "tarjontaOhjausparametritRestUrlPrefix": "PARAMETRIT",
        }, "app": {
            "key-app-1": "mock-value-app-1",
            "userLanguages": ['kieli_fi', 'kieli_sv', 'kieli_en']
        }
    };

//    //set mock data to module by using the value-method,
//    var mockModule = angular.module('test.module', []);
//    mockModule.value('globalConfig', CONFIG_ENV_MOCK);

    beforeEach(module('app.dialog'));
    beforeEach(module('test.module')); //mock module with the mock data
    beforeEach(module('Organisaatio'));
    beforeEach(module('Tarjonta'));
    beforeEach(module('SharedStateService'));
    beforeEach(module('KoulutusConverter'));
    beforeEach(module('imageupload'));
    beforeEach(module('TarjontaCache'));
    beforeEach(module('app.edit.ctrl.kk'));
    beforeEach(module('app.edit.ctrl'));
    beforeEach(module('debounce'));
    beforeEach(module('ngRoute'));
    beforeEach(module('Logging'));
    beforeEach(module('TarjontaPermissions'));
    beforeEach(module('Haku'));


    beforeEach(function() {
        module(function($provide) {
            $provide.value('Config', CONFIG_ENV_MOCK);
            $provide.value('targetFilters', []);
        });
    });

    var $scope, $modalInstance;
    beforeEach(inject(function($rootScope) {
        $scope = $rootScope.$new();
        $modalInstance = {
            $scope: $scope,
            templateUrl: 'partials/koulutus/edit/korkeakoulu/selectTutkintoOhjelma.html',
            controller: 'SelectTutkintoOhjelmaController',
            targetFilters: function() {
                return [];
            }
        };
    }));

    it('Testing the SelectTutkintoOhjelmaController initial values', inject(function($controller) {
        $controller('SelectTutkintoOhjelmaController', {
            $scope: $scope,
            $modalInstance: $modalInstance,
            resolve: {
                targetFilters: function() {
                    return [];
                }
            }

        });
        expect($scope.stoModel.hakutulokset).toEqual([]);
        expect($scope.stoModel.koulutusala).toEqual('');
        expect($scope.stoModel.active).toEqual({});

    }));

    it('Testing the SelectTutkintoOhjelmaController clearCriteria', inject(function($controller) {
        $controller('SelectTutkintoOhjelmaController', {
            $scope: $scope,
            $modalInstance: $modalInstance,
            targetFilters: function() {
                return [];
            }
        });

        $scope.stoModel.hakulause = 'AMK';
        $scope.clearCriteria();
        expect($scope.stoModel.hakulause).toEqual('');
    }));

    it('Testing the EditYhteyshenkiloCtrl selectHenkilo', inject(function($controller, $httpBackend) {

        var $route = {current: {locals: {koulutusModel: {result: {organisaatio: {oid: "org-oid-1.2.3.4"}}}}}}; // this is where the ctrl reads org oid

        var $scope = {};

        $controller('EditYhteyshenkiloCtrl', {
            $route: $route,
            $scope: $scope
        });

        //mock backend call results
        var henkilo1 = {
            "etunimet": "Testeri",
            "oidHenkilo": "1.2.246.562.24.91121139885",
            "sukunimi": "1194-Kuormitus"
        };

        var response = {
            "last": true,
            "number": 0,
            "numberOfElements": 1,
            "results": [henkilo1,
                {
                    "etunimet": "Testeri",
                    "hetu": "010101-1234",
                    "kutsumanimi": "Testeri",
                    "oidHenkilo": "1.2.246.562.24.91121139885",
                    "sukunimi": "1194-Kuormitus"
                }
            ],
            "size": 1
        };

        var henkilo = {
            "oidHenkilo": "1.2.246.562.24.91121139885",
            "hetu": "260610-6151",
            "passivoitu": false,
            "henkiloTyyppi": "VIRKAILIJA",
            "etunimet": "Testeri",
            "kutsumanimi": "Testeri",
            "sukunimi": "1194-Kuormitus",
            "aidinkieli": null,
            "asiointiKieli": {
                "kieliKoodi": "fi",
                "kieliTyyppi": "suomi"
            },
            "kielisyys": [
                {
                    "kieliKoodi": "fi",
                    "kieliTyyppi": "suomi"
                }
            ],
            "kansalaisuus": [
                {
                    "kansalaisuusKoodi": "246"
                }
            ],
            "kasittelijaOid": null,
            "syntymaaika": "1910-06-26",
            "sukupuoli": "1",
            "passinnumero": null,
            "oppijanumero": null,
            "turvakielto": false,
            "eiSuomalaistaHetua": false,
            "yksiloity": false,
            "yksiloityVTJ": true,
            "yksilointiYritetty": false,
            "duplicate": false,
            "created": -7200000,
            "modified": null,
            "vtjsynced": null,
            "huoltaja": null,
            "yhteystiedotRyhma": [
                {
                    "id": 92137,
                    "ryhmaKuvaus": "yhteystietotyyppi7",
                    "ryhmaAlkuperaTieto": null,
                    "readOnly": false,
                    "yhteystieto": [
                        {
                            "yhteystietoTyyppi": "YHTEYSTIETO_PUHELINNUMERO",
                            "yhteystietoArvo": "012343"
                        },
                        {
                            "yhteystietoTyyppi": "YHTEYSTIETO_POSTINUMERO",
                            "yhteystietoArvo": "00001"
                        },
                        {
                            "yhteystietoTyyppi": "YHTEYSTIETO_KATUOSOITE",
                            "yhteystietoArvo": "jokukatu 1"
                        },
                        {
                            "yhteystietoTyyppi": "YHTEYSTIETO_KUNTA",
                            "yhteystietoArvo": "Helsinki"
                        },
                        {
                            "yhteystietoTyyppi": "YHTEYSTIETO_KAUPUNKI",
                            "yhteystietoArvo": "Helsinki"
                        },
                        {
                            "yhteystietoTyyppi": "YHTEYSTIETO_SAHKOPOSTI",
                            "yhteystietoArvo": "email@foo.bar"
                        }
                    ]
                }
            ]
        }

        var organisaatiohenkilo = [{
                "id": 29258,
                "organisaatioOid": "1.2.246.562.10.82388989657",
                "tehtavanimike": "testaaja",
                "passivoitu": false
            }];

        $httpBackend.whenGET('/oppijanumerorekisteri-service/henkilo?page=1&count=2000&passivoitu=false&duplikaatti=false&tyyppi=VIRKAILIJA&organisaatioOids=org-oid-1.2.3.4').respond(response);
        $httpBackend.whenGET('/oppijanumerorekisteri-service/henkilo/1.2.246.562.24.91121139885').respond(henkilo);
        $httpBackend.whenGET('/kayttooikeus-service/henkilo/1.2.246.562.24.91121139885/organisaatiohenkilo').respond(organisaatiohenkilo);

        $scope.uiModel = {};
        $scope.init($scope.uiModel);

        $scope.uiModel.contactPerson = {};

        expect($scope.uiModel.contactPerson.etunimet).toEqual(undefined);

        $scope.editYhModel.selectHenkilo(henkilo1);
        $httpBackend.flush();
        expect($scope.uiModel.contactPerson.nimi).toEqual('Testeri 1194-Kuormitus');
        expect($scope.uiModel.contactPerson.puhelin).toEqual('012343');
        expect($scope.uiModel.contactPerson.sahkoposti).toEqual('email@foo.bar');

    }));
});

describe('Edit koulutus insert/edit/load', function() {
    beforeEach(module('ngGrid'));
    beforeEach(module('ngRoute'));
    beforeEach(module('ngResource'));
    beforeEach(module('ngAnimate'));
    beforeEach(module('ngSanitize'));
    beforeEach(module('app.dialog'));
    beforeEach(module('TarjontaPermissions'));
    beforeEach(module('Haku'));
    beforeEach(module('Hakukohde'));

    beforeEach(function() {
        module(function($provide) {
            $provide.value('globalConfig', {
                env: {
                    "tarjontaOhjausparametritRestUrlPrefix": "/",
                    "koodisto-uris.pohjakoulutusvaatimus": "",
                    "koodisto-uris.postinumero": "",
                    "koodisto-uris.suunniteltuKesto": "",
                    "koodisto-uris.tarjontakoulutustyyppi": "",
                    "koodisto-uris.teemat": "",
                    "koodisto-uris.tutkinto": "",
                    "koodisto-uris.koulutus": "",
                    "koodisto-uris.tutkintonimike": "",
                    "koodisto-uris.valintakokeentyyppi": "",
                    "koodisto-uris.valintaperustekuvausryhma": "",
                    "koodisto-uris.tutkintonimike_kk": "",
                    "koodisto-uris.pohjakoulutusvaatimus_kk": "",
                    "koodisto-uris.eqf-luokitus": "",
                    "koodisto-uris.aiheet": "",
                    "koodisto-uris.opetusmuotokk": "",
                    "koodisto-uris.opetusaika": "",
                    "koodisto-uris.opetuspaikka": "",
                    "koodisto-uris.kieli": "",
                    "koodisto-uris.ammattinimikkeet": "",
                    "koodisto-uris.arvo": ""

                },
                app: {"userLanguages": ['kieli_fi', 'kieli_sv', 'kieli_en']}});
        });
    });



    beforeEach(module('config'));
    beforeEach(module('imageupload'));
    beforeEach(module('ui.bootstrap'));
    beforeEach(module('localisation'));
    beforeEach(module('TarjontaCache'));
    beforeEach(module('CommonUtilServiceModule'));
    beforeEach(module('Tarjonta'));
    beforeEach(module('KoulutusConverter'));
    beforeEach(module('Organisaatio'));
    beforeEach(module('app.edit.ctrl'));
    beforeEach(module('app.edit.ctrl.kk'));
    beforeEach(module('TarjontaPermissions'));
    beforeEach(module('SharedStateService'));


    var scope, localisationService, routeParams, tarjontaService, cfg, organisaatioService;

    beforeEach(inject(function($rootScope, LocalisationService, TarjontaService, Config, $routeParams, OrganisaatioService) {
        scope = $rootScope.$new();
        tarjontaService = TarjontaService;
        localisationService = LocalisationService;
        routeParams = $routeParams;
        routeParams.id = null;
        routeParams.org = 'org-oid-1';
        routeParams.toteutustyyppi = "KORKEAKOULUTUS";
        routeParams.koulutustyyppi = "KORKEAKOULUTUS";
        cfg = Config;

        organisaatioService = OrganisaatioService;

    }));

    var EMPTY_UI_MODEL = {uri: '', versio: -1};
    var EMPTY_META_UI_MODEL = {uris: {}};
    var EMPTY_META_UI_MODEL_KOULUTUOHJELMA = {tekstis: {
            kieli_fi: '',
            kieli_sv: '',
            kieli_en: ''}};



    it('Testing the BaseEditController.init', inject(function($controller) {
        var $route = {current: {locals: {koulutusModel: {result: {
                            organisaatio: {oid: "org-oid-1.2.3.4"}
                        }}}}}; // this is where the ctrl reads org oid


        var a = $controller('BaseEditController', {
            $route: $route,
            $scope: scope
        });

        $controller('EditKorkeakouluController', {
            $route: $route,
            "$scope": scope,
            "tarjontaService": tarjontaService,
            "cfg": cfg,
            "$routeParams": routeParams,
            "organisaatioService": organisaatioService
        });

        scope.init();
        expect(scope.model.koulutuskoodi).toEqual(EMPTY_UI_MODEL);
        expect(scope.model.koulutusaste).toEqual(EMPTY_UI_MODEL);
        expect(scope.model.koulutusala).toEqual(EMPTY_UI_MODEL);
        expect(scope.model.opintoala).toEqual(EMPTY_UI_MODEL);
        expect(scope.model.tutkinto).toEqual(EMPTY_UI_MODEL);
        //TODO -> JANI KORJAA?
        //expect(scope.model.tutkintonimike).toEqual(EMPTY_UI_MODEL);
        expect(scope.model.eqf).toEqual(EMPTY_UI_MODEL);
        expect(scope.model.suunniteltuKestoTyyppi).toEqual(EMPTY_UI_MODEL); //arvo = 'kymmenen', koodi.uri = kesto_uri
        expect(scope.model.suunniteltuKestoArvo).toEqual('');
        expect(scope.model.tunniste).toEqual('');

        console.log(EMPTY_META_UI_MODEL_KOULUTUOHJELMA);
        console.log(scope.model.koulutusohjelma);

        expect(scope.model.koulutusohjelma).toEqual(EMPTY_META_UI_MODEL_KOULUTUOHJELMA);
        expect(scope.model.aihees).toEqual(EMPTY_META_UI_MODEL);
        expect(scope.model.opetuskielis).toEqual(EMPTY_META_UI_MODEL);
        expect(scope.model.opetusmuodos).toEqual(EMPTY_META_UI_MODEL);

        expect(scope.model.koulutusmoduuliTyyppi).toEqual('TUTKINTO');
        expect(scope.model.toteutustyyppi).toEqual('KORKEAKOULUTUS');
        expect(scope.model.tila).toEqual('LUONNOS');

        expect(scope.uiModel.contactPerson).toEqual({henkiloTyyppi: 'YHTEYSHENKILO'});
        expect(scope.uiModel.ectsCoordinator).toEqual({henkiloTyyppi: 'ECTS_KOORDINAATTORI'});
    }));
});
