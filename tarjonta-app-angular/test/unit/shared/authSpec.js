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
        }, app: {
            "key-app-1": "mock-value-app-1"
        }
    };


    describe('AuthService', function() {
    	
        beforeEach(function(){
            module(function ($provide) {
                $provide.value('Config', CONFIG_ENV_MOCK);
            });
        });

        it('should return user', inject(function(AuthService) {
           expect("tiimi2").toEqual(AuthService.getUsername());
        }));

        it('should return user organisations', inject(function(AuthService) {
            expect(1).toEqual(AuthService.getOrganisations().length);
            expect("1.2.246.562.10.44562157436").toEqual(AuthService.getOrganisations()[0]);
            
         }));

        it('should return user language', inject(function(AuthService) {
            expect("fi").toEqual(AuthService.getLanguage());
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
            MyRolesModel.debug();
//            MyRolesModel.refresh();
            MyRolesModel.debug();
            expect(true).toEqual(true);
            console.log("myroles:", MyRolesModel.myroles);
        }));

    });
});
