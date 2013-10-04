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

describe('auth', function() {

    var CONFIG_ENV_MOCK = {
        "env": {
            "key-env-1": "mock-value-env-1",
            "key-env-2": "mock-value-env-2",
            "tarjonta.localisations": [],
            "casUrl" : "cas_myroles_tiimi2",
        }, "app": {
            "key-app-1": "mock-value-app-1"
        },
    };

    //set mock data to module by using the value-method,
    var mockModule = angular.module('test.module', []);
    mockModule.value('globalConfig', CONFIG_ENV_MOCK);


    beforeEach(module('auth'));
    beforeEach(module('config'));
    beforeEach(module('test.module'));

    describe('MyRolesModel', function() {
        var scope, controller;

        beforeEach(inject(function($rootScope, $controller) {
            scope = $rootScope.$new();
        }));

        it('xxx', inject(function(MyRolesModel) {
            MyRolesModel.debug();
            MyRolesModel.refresh();
            MyRolesModel.debug();
            expect(true).toEqual(true);
        }));

    });
});
