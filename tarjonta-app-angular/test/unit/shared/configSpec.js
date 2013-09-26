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

describe('config', function() {
    var CONFIG_ENV_MOCK = {
        "env": {
            "key-env-1": "mock-value-env-1",
            "key-env-2": "mock-value-env-2"
        }, "app": {
            "key-app-1": "mock-value-app-1"
        }
    }
    
    //set mock data to module by using the value-method,
    var mockModule = angular.module('test.module', []);
    mockModule.value('globalConfig', CONFIG_ENV_MOCK);

    beforeEach(module('test.module')); //mock module with the mock data
    beforeEach(module('config'));

    describe('Config', function() {
        var config, global;

        beforeEach(function() {
            //inject your service for testing.
            inject(function(globalConfig, Config) {
                global = globalConfig; //the mock data
                config = Config; //the factory we are trying to test
            });
        });

        it('should have object instances', inject(function() {
            expect(config).toBeDefined();
            expect(config.env).toBeDefined();
            expect(config.app).toBeDefined();
        }));

        it('should return env and app values by given keys', inject(function() {
            expect(config.env["key-env-1"]).toBe("mock-value-env-1");
            expect(config.env["key-env-2"]).toBe(CONFIG_ENV_MOCK.env[ "key-env-2"]);
            expect(config.env["key-env-3"]).toBe(undefined);
            expect(config.app["key-app-1"]).toBe(CONFIG_ENV_MOCK.app[ "key-app-1"]);
        }));
    });
});
