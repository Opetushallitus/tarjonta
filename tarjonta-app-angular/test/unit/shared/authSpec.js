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
        env: {
            "key-env-1": "mock-value-env-1",
            "key-env-2": "mock-value-env-2",
            "tarjonta.localisations": [],
            "casUrl" : "cas_myroles_tiimi221",
            cas:{userinfo:{
            	  "uid":"tiimi2",
            	  "oid":"1.2.246.562.24.67912964565",
            	  "firstName":"etu",
            	  "lastName":"suku",
            	  "groups":["APP_ANOMUSTENHALLINTA","APP_ANOMUSTENHALLINTA_READ","APP_ORGANISAATIOHALLINTA","APP_ORGANISAATIOHALLINTA_READ_UPDATE","APP_HENKILONHALLINTA","APP_HENKILONHALLINTA_READ","APP_KOODISTO","APP_KOODISTO_READ","APP_KOOSTEROOLIENHALLINTA","APP_KOOSTEROOLIENHALLINTA_READ","APP_OID","APP_OID_READ","APP_OMATTIEDOT","APP_OMATTIEDOT_READ_UPDATE","APP_TARJONTA","APP_TARJONTA_CRUD","VIRKAILIJA","LANG_sv","APP_OMATTIEDOT_READ_UPDATE_1.2.246.562.10.51053050251","APP_KOOSTEROOLIENHALLINTA_READ_1.2.246.562.10.51053050251","APP_TARJONTA_CRUD_1.2.246.562.10.51053050251","APP_OID_READ_1.2.246.562.10.51053050251","APP_ANOMUSTENHALLINTA_READ_1.2.246.562.10.51053050251","APP_KOODISTO_READ_1.2.246.562.10.51053050251","APP_ORGANISAATIOHALLINTA_READ_UPDATE_1.2.246.562.10.51053050251","APP_HENKILONHALLINTA_READ_1.2.246.562.10.51053050251"],
            	  "lang":"sv"
            	}}

        }, app: {
            "key-app-1": "mock-value-app-1"
        }
    };

    beforeEach(module('Organisaatio'));
    beforeEach(module('SharedStateService'));
    beforeEach(module('Logging'));

    describe('AuthService', function() {

        beforeEach(function(){
            module(function ($provide) {
                $provide.value('Config', CONFIG_ENV_MOCK);
            });
        });

        it('should return user', inject(function(AuthService) {
           expect("tiimi2").toEqual(AuthService.getUsername());
        }));

        it('should return oid', inject(function(AuthService) {
            expect("1.2.246.562.24.67912964565").toEqual(AuthService.getUserOid());
        }));

        it('should return firstname', inject(function(AuthService) {
            expect("etu").toEqual(AuthService.getFirstName());
        }));

        it('should return lastname', inject(function(AuthService) {
            expect("suku").toEqual(AuthService.getLastName());
        }));

        it('should return user organisations', inject(function(AuthService) {
            expect(1).toEqual(AuthService.getOrganisations().length);
            expect("1.2.246.562.10.51053050251").toEqual(AuthService.getOrganisations()[0]);

         }));

        it('should return user language', inject(function(AuthService) {
            expect("sv").toEqual(AuthService.getLanguage());
         }));

    });

    beforeEach(module('auth'));

    describe('MyRolesModel', function() {

        beforeEach(function(){
            module(function ($provide) {
                $provide.value('Config', CONFIG_ENV_MOCK);
            });
        });


        it('MyRolesModel tests should have some meaningful asserts!', inject(function(MyRolesModel) {
            expect(true).toEqual(true);
//            console.log("myroles:", MyRolesModel.myroles);
        }));

    });
});
