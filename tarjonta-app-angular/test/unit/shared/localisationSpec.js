/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 */


/**
 * Tests for Localisation.
 */
describe('localisation', function() {
    var THE_OFFICIAL_TEST_VALUE_FI = "83989859385938593857ksdjfhskjfhskjhf_fi";

    var CONFIG_ENV_MOCK = {
        "env": {
            "key-env-1": "mock-value-env-1",
            "key-env-2": "mock-value-env-2",
            "tarjonta.localisations": [{ key: "this.is.a.test.too_XXX", value: THE_OFFICIAL_TEST_VALUE_FI, locale: "fi"}],
            "casUrl" : "cas_myroles_tiimi2",
            "cas.myroles" : 
            	["USER_tiimi2", "APP_ANOMUSTENHALLINTA", "APP_ANOMUSTENHALLINTA_CRUD", "APP_ORGANISAATIOHALLINTA",
            	    "APP_ORGANISAATIOHALLINTA_CRUD", "APP_HENKILONHALLINTA", "APP_HENKILONHALLINTA_CRUD", "APP_KOODISTO",
            	    "APP_KOODISTO_CRUD", "APP_KOOSTEROOLIENHALLINTA", "APP_KOOSTEROOLIENHALLINTA_CRUD", "APP_OID",
            	    "APP_OID_CRUD", "APP_OMATTIEDOT", "APP_OMATTIEDOT_CRUD", "APP_TARJONTA", "APP_TARJONTA_CRUD",
            	    "VIRKAILIJA", "APP_KOOSTEROOLIENHALLINTA_CRUD_1.2.246.562.10.44562157436",
            	    "APP_ORGANISAATIOHALLINTA_CRUD_1.2.246.562.10.44562157436", "APP_OID_CRUD_1.2.246.562.10.44562157436",
            	    "APP_ANOMUSTENHALLINTA_CRUD_1.2.246.562.10.44562157436", "APP_KOODISTO_CRUD_1.2.246.562.10.44562157436",
            	    "APP_OMATTIEDOT_CRUD_1.2.246.562.10.44562157436", "APP_TARJONTA_CRUD_1.2.246.562.10.44562157436",
            	    "APP_HENKILONHALLINTA_CRUD_1.2.246.562.10.44562157436", "LANG_fi"]

        }, "app": {
            "key-app-1": "mock-value-app-1"
        },
    };

    //set mock data to module by using the value-method,
    var mockModule = angular.module('test.module', []);
    mockModule.value('globalConfig', CONFIG_ENV_MOCK);

    beforeEach(module('localisation'));
    beforeEach(module('test.module'));
    beforeEach(module('auth'));

    describe('LocalisationCtrl', function() {
        var scope, controller;

        // Create controller with scope
        beforeEach(inject(function($rootScope, $controller, $log) {
            $log.debug = function(args) {
                $log.info(args);
            };

            scope = $rootScope.$new();
            controller = $controller("LocalisationCtrl", {$scope: scope});
        }));

        // When an unknown translation is referred to - a "["+key+"]" value should be returned
        it('unknown keys should be wrapped to []', inject(function() {
            var key1 = "this.is.a.test";
            expect(scope.t(key1)).toEqual("[" + key1 + "]");

            var key2 = "this.is.a.test.too";
            expect(scope.t(key2)).toEqual("[" + key2 + "]");

            var key3 = "this.is.a.test.too_XXX";
            expect(scope.t(key3)).toEqual(THE_OFFICIAL_TEST_VALUE_FI);
        }));

    });
});

