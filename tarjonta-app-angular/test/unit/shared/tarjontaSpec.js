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

describe('Tarjonta', function() {

    var CONFIG_ENV_MOCK = {
        env: {
            "key-env-1": "mock-value-env-1",
            "key-env-2": "mock-value-env-2",
            "tarjonta.localisations": [],
            "casUrl" : "cas_myroles_tiimi221",
            tarjontaRestUrlPrefix:"/",
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
    
    beforeEach(module('auth'));
    beforeEach(module('Tarjonta'));
    beforeEach(module('localisation'));
    beforeEach(module('Koodisto'));
    beforeEach(module('TarjontaCache'));

    var mockHttp = function($httpBackend){
    	var response = {status:true, data:["a","b","c","d"]};
        $httpBackend.whenGET('/link/parent-oid-1.2.3.4.5.6.7?').respond(response);
        $httpBackend.whenGET('/link/parents/parent-oid-1.2.3.4.5.6.7?').respond(response);
        $httpBackend.whenPOST('/link/parent-oid-1.2.3.4.5.6.7/child-oid-1.2.3.4.5.6.7').respond(response);
        $httpBackend.whenPOST('/link/parent-oid-1.2.3.4.5.6.7/child-oid-1.2.3.4.5.6.7,child-oid-1.2.3.4.5.6.8').respond(response);
        $httpBackend.whenDELETE('/link/parent-oid-1.2.3.4.5.6.7/child-oid-1.2.3.4.5.6.7').respond(response);
    };    

    describe('TarjontaService', function($injector) {
    	
        beforeEach(function(){
            module(function ($provide) {
                $provide.value('Config', CONFIG_ENV_MOCK);
            });
        });

        it('should declare resourcelink service with known api', inject(function($httpBackend, TarjontaService) {
        	var resourceLink = TarjontaService.resourceLink;
        	expect(resourceLink).toNotEqual(undefined);
        	expect(resourceLink.save).toNotEqual(undefined);
        	expect(resourceLink.remove).toNotEqual(undefined);
        	expect(resourceLink.get).toNotEqual(undefined);
        	expect(resourceLink.parents).toNotEqual(undefined);
        }));
        
        it('should call the known rest api', inject(function($httpBackend, TarjontaService) {
        	mockHttp($httpBackend);
        	var parentId = "parent-oid-1.2.3.4.5.6.7";
        	var childId = "child-oid-1.2.3.4.5.6.7";
        	var childId2 = "child-oid-1.2.3.4.5.6.8";
        	var resourceLink = TarjontaService.resourceLink;
        	resourceLink.get({oid:parentId});
        	resourceLink.parents({oid:parentId});
        	resourceLink.save({parent:parentId, child:childId});
        	resourceLink.save({parent:parentId, child:[childId,childId2]});
        	resourceLink.remove({parent:parentId, child:childId});
        	$httpBackend.flush();
        }));
        
    });
});
