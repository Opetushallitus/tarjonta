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
            "key-env-1": "mock-value-env-1",
            "key-env-2": "mock-value-env-2"
        }, "app": {
            "key-app-1": "mock-value-app-1",
            "userLanguages": ['kieli_fi', 'kieli_sv', 'kieli_en']
        }
    }

    //set mock data to module by using the value-method,
    var mockModule = angular.module('test.module', []);
    mockModule.value('globalConfig', CONFIG_ENV_MOCK);

    beforeEach(module('test.module')); //mock module with the mock data
    beforeEach(module('TarjontaConverter'));
    beforeEach(module('imageupload'));
    beforeEach(module('app.edit.ctrl'));
    beforeEach(module('config'));
    var $scope, $modalInstance;
    beforeEach(inject(function($rootScope) {
        $scope = $rootScope.$new();
        $modalInstance = {
            $scope: $scope,
            templateUrl: 'partials/koulutus/edit/selectTutkintoOhjelma.html',
            controller: 'SelectTutkintoOhjelmaController'
        };
    }));
    it('Testing the SelectTutkintoOhjelmaController initial values', inject(function($controller) {
        $controller('SelectTutkintoOhjelmaController', {
            $scope: $scope,
            $modalInstance: $modalInstance

        });
        expect($scope.stoModel.hakutulokset).toEqual([]);
        expect($scope.stoModel.koulutusala).toEqual('');
        expect($scope.stoModel.active).toEqual({});

    }));
    it('Testing the SelectTutkintoOhjelmaController clearCriteria', inject(function($controller) {
        $controller('SelectTutkintoOhjelmaController', {
            $scope: $scope,
            $modalInstance: $modalInstance
        });

        $scope.stoModel.hakulause = 'AMK';
        $scope.clearCriteria();
        expect($scope.stoModel.hakulause).toEqual('');
    }));
    it('Testing the EditYhteyshenkiloCtrl clearYh', inject(function($controller) {
        $controller('EditYhteyshenkiloCtrl', {
            $scope: $scope,
        });

        $scope.uiModel = {};

        $scope.uiModel.contactPerson = {};
        $scope.uiModel.contactPerson.nimet = 'Testi nimi';
        $scope.uiModel.contactPerson.sahkoposti = 'test@oph.fi';
        $scope.uiModel.contactPerson.titteli = 'Herra';
        $scope.uiModel.contactPerson.puhelin = '050432134534';
        $scope.uiModel.contactPerson.etunimet = 'Testi';
        $scope.uiModel.contactPerson.sukunimi = 'nimi';

        $scope.editYhModel.clearYh();
        expect($scope.uiModel.contactPerson.nimet).toEqual(undefined);
    }));
    it('Testing the EditYhteyshenkiloCtrl selectHenkilo', inject(function($controller) {
        $controller('EditYhteyshenkiloCtrl', {
            $scope: $scope,
        });

        $scope.editYhModel.searchPersonMap = {};

        $scope.editYhModel.searchPersonMap['Testi nimi'] = {etunimet: 'Testi', sukunimi: 'nimi', puhelin: '05043210', titteli: 'Herra', sahkoposti: 'test@oph.fi'};
        $scope.editYhModel.searchPersonMap['Pekka Pekkola'] = {etunimet: 'Pekka', sukunimi: 'Pekkola', puhelin: '050345345', titteli: 'lehtori', sahkoposti: 'test2@oph.fi'};

        $scope.uiModel = {};

        $scope.uiModel.contactPerson = {};

        $scope.uiModel.contactPerson.nimet = 'Pekka Pekkola';

        expect($scope.uiModel.contactPerson.etunimet).toEqual(undefined);

        $scope.editYhModel.selectHenkilo();

        expect($scope.uiModel.contactPerson.etunimet).toEqual('Pekka');

    }));

});

describe('Edit koulutus insert/edit/load', function() {
    beforeEach(module('ngGrid'));
    beforeEach(module('ngRoute'));
    beforeEach(module('ngResource'));

    beforeEach(function() {
        module(function($provide) {
            $provide.value('globalConfig', {
                env: {},
                app: {"userLanguages": ['kieli_fi', 'kieli_sv', 'kieli_en']}});
        });
    });


    beforeEach(module('config'));
    beforeEach(module('imageupload'));
    beforeEach(module('ui.bootstrap'));
    beforeEach(module('localisation'));
    beforeEach(module('TarjontaCache'));

    beforeEach(module('Tarjonta'));
    beforeEach(module('TarjontaConverter'));
    beforeEach(module('Organisaatio'));
    beforeEach(module('app.edit.ctrl'));

    var scope, localisationService, routeParams, tarjontaService, cfg, organisaatioService;

    beforeEach(inject(function($rootScope, LocalisationService, TarjontaService, Config, $routeParams, OrganisaatioService) {
        scope = $rootScope.$new();
        tarjontaService = TarjontaService;
        localisationService = LocalisationService;
        routeParams = $routeParams;
        routeParams.id = null;
        routeParams.org = 'org-oid-1';
        cfg = Config;

        organisaatioService = OrganisaatioService;

    }));

    var EMPTY_UI_MODEL = {arvo: null, koodi: {uri: null, versio: null}};
    var EMPTY_META_UI_MODEL = {arvo: null, koodi: {uri: null, versio: null}, meta: {}};
    var EMPTY_META_UI_MODEL_KOULUTUOHJELMA = {arvo: null, koodi: {uri: null, versio: '-1'}, meta: {
            kieli_fi: {koodi: {arvo: '', uri: 'kieli_fi', versio: -1}},
            kieli_sv: {koodi: {arvo: '', uri: 'kieli_sv', versio: -1}},
            kieli_en: {koodi: {arvo: '', uri: 'kieli_en', versio: -1}}}}

    it('Testing the BaseEditController.init', inject(function($controller) {
        $controller('BaseEditController', {
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
        expect(scope.model.tutkintonimike).toEqual(EMPTY_UI_MODEL);
        expect(scope.model.eqf).toEqual(EMPTY_UI_MODEL);
        expect(scope.model.suunniteltuKesto).toEqual(EMPTY_UI_MODEL); //arvo = 'kymmenen', koodi.uri = kesto_uri
        expect(scope.model.tunniste).toEqual('');

        console.log(EMPTY_META_UI_MODEL_KOULUTUOHJELMA);
        console.log(scope.model.koulutusohjelma);

        expect(scope.model.koulutusohjelma).toEqual(EMPTY_META_UI_MODEL_KOULUTUOHJELMA);
        expect(scope.model.teemas).toEqual(EMPTY_META_UI_MODEL);
        expect(scope.model.opetuskielis).toEqual(EMPTY_META_UI_MODEL);
        expect(scope.model.pohjakoulutusvaatimukset).toEqual(EMPTY_META_UI_MODEL);
        expect(scope.model.opetusmuodos).toEqual(EMPTY_META_UI_MODEL);

        expect(scope.model.koulutusmoduuliTyyppi).toEqual('TUTKINTO');
        expect(scope.model.koulutusasteTyyppi).toEqual('KORKEAKOULUTUS');
        expect(scope.model.tila).toEqual('LUONNOS');

        expect(scope.uiModel.contactPerson).toEqual({henkiloTyyppi: 'YHTEYSHENKILO'});
        expect(scope.uiModel.ectsCoordinator).toEqual({henkiloTyyppi: 'ECTS_KOORDINAATTORI'});
    }));
});
