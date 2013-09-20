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

describe('localisation', function() {
    beforeEach(module('localisation'));

    describe('LocalisationCtrl', function() {
        var scope, controller;

        beforeEach(inject(function ($rootScope, $controller, $log) {
            scope = $rootScope.$new();
            controller = $controller("LocalisationCtrl", { $scope: scope});
        }));

        it('unknown keys should be wrapped to []', inject(function() {
            var key1 = "this.is.a.test";
            expect(scope.t(key1)).toEqual("[" + key1 + "]");

            var key2 = "this.is.a.test.too";
            expect(scope.t(key2)).toEqual("[" + key2 + "]");
        }));


        it('known translation should be returned', inject(function() {

            var key = "tarjonta.organisaationHaku.hae";
            var data = {
                "key": key,
                "locale": "fi",
                "value": "Tee haku",
                "values": {
                    "fi": "Tee haku",
                    "en": "Perform the search",
                    "sv": "Sök - sök!"
                }
            };

            scope.locale = "fi";
            scope.localisations[key] = data;

            expect(scope.t(key)).toEqual("Tee haku");
        }));

    });
});

