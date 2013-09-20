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
    beforeEach(module('tarjontaApp.auth'));

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
