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

describe('TarjontaPermissions', function() {

    var CONFIG_ENV_MOCK = {
        env: {
            "tarjonta.localisations": [],
            "organisaatio.api.rest.url":"/",
            cas:{userinfo:{
            	  uid:"tiimi2",
            	  oid:"1.2.246.562.24.67912964565",
            	  firstName:"etu",
            	  lastName:"suku",
            	  groups:["APP_TARJONTA_CRUD_1.2.3", "APP_TARJONTA_READ_UPDATE_1.2.4", "APP_TARJONTA_READ_1.2.5"],
            	  lang:"sv"
            	}
            },
            "key-app-1": "mock-value-app-1"
        }
    };

    
    
    beforeEach(module('auth'));

    beforeEach(function(){
        module(function ($provide) {
            $provide.value('Config', CONFIG_ENV_MOCK);
        });
    });

    beforeEach(module('TarjontaPermissions'));
    
    var mockHttp = function($httpBackend) {
    	/*
    	 * 
    	 *following structure:
    	
    	1-
    	 |-1.2
    	     |-1.2.3  (crud)
    	     |-1.2.4  (ru)
    	     |-1.2.5  (read)
    	*/
    	     
        $httpBackend.whenGET('/organisaatio/1.2.3/parentoids').respond("1/1.2/1.2.3");
        $httpBackend.whenGET('/organisaatio/1.2.4/parentoids').respond("1/1.2/1.2.4");
        $httpBackend.whenGET('/organisaatio/1.2.5/parentoids').respond("1/1.2/1.2.5");
        //console.log("$httpBackend:", $httpBackend);
    };
    

    
        
    describe('Permission service shold answer ', function($injector) {
        
    	/** executes test, called by test() */
    	var doTest = function(promise, $httpBackend) {
        	var result;
        	
        	promise.then(function(data){
        		result=data;
        	}, function(){
        		result=false;
        	});
        	
        	$httpBackend.flush();
        	
//        	console.log("returning:" + result);
        	return result;
        };

        /**
         * specify test, expected = expected result, orgoid org to test with
         */
    	var test = function(expected, message, orgoid, testFn){
            it(expected + message, inject(function(PermissionService, $httpBackend) {
            	mockHttp($httpBackend);
//            	console.log("orgoid:", orgoid);
            	//console.log("testFn:", testFn);
            	result = doTest(testFn(PermissionService, orgoid), $httpBackend);
//            	console.log("testing access to " + orgoid + " expected result:" + expected + " actual result:" + result)
            	expect(expected).toEqual(result);
            }));
        }


    	//create koulutus
    	var testFn=function(PermissionService, orgOid){
    		return PermissionService.koulutus.canCreate(orgOid);
    	};
    	
    	test(true, " for create when user has CRUD permission", "1.2.3", testFn);
    	test(false, " for create when user has RU permission", "1.2.4", testFn);
    	test(false, " for create when user has R permission", "1.2.5", testFn);

    	//edit koulutus
    	testFn=function(PermissionService, orgOid){
    		return PermissionService.koulutus.canEdit(orgOid);
    	};
    	
    	test(true, " for edit when user has CRUD permission", "1.2.3", testFn);
    	test(true, " for edit when user has RU permission", "1.2.4", testFn);
    	test(false, " for edit when user has R permission", "1.2.5", testFn);

    	//delete koulutus
    	testFn=function(PermissionService, orgOid){
    		return PermissionService.koulutus.canDelete(orgOid);
    	};
    	
    	test(true, " for edit when user has CRUD permission", "1.2.3", testFn);
    	test(false, " for edit when user has RU permission", "1.2.4", testFn);
    	test(false, " for edit when user has R permission", "1.2.5", testFn);

    	
    	//create hakukohde
    	var testFn=function(PermissionService, orgOid){
    		return PermissionService.hakukohde.canCreate(orgOid);
    	};
    	
    	test(true, " for create when user has CRUD permission", "1.2.3", testFn);
    	test(false, " for create when user has RU permission", "1.2.4", testFn);
    	test(false, " for create when user has R permission", "1.2.5", testFn);

    	//edit hakukohde
    	testFn=function(PermissionService, orgOid){
    		return PermissionService.hakukohde.canEdit(orgOid);
    	};
    	
    	test(true, " for edit when user has CRUD permission", "1.2.3", testFn);
    	test(true, " for edit when user has RU permission", "1.2.4", testFn);
    	test(false, " for edit when user has R permission", "1.2.5", testFn);

    	//delete hakukohde
    	testFn=function(PermissionService, orgOid){
    		return PermissionService.hakukohde.canDelete(orgOid);
    	};
    	
    	test(true, " for edit when user has CRUD permission", "1.2.3", testFn);
    	test(false, " for edit when user has RU permission", "1.2.4", testFn);
    	test(false, " for edit when user has R permission", "1.2.5", testFn);

    });

});
