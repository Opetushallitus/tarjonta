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
            "tarjontaRestUrlPrefix": "/",
            "tarjontaOhjausparametritRestUrlPrefix" : "PARAMETRIT",
            "tarjonta.localisations": [ {
            	  "value" : "Organisations typ",
            	  "key" : "tarjonta.tila.LUONNOS",
            	  "locale" : "sv",
            	  "category" : "tarjonta"
            	}],
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

    beforeEach(module('app.dialog'));
    beforeEach(module('Haku'));
    beforeEach(module('auth'));
    beforeEach(module('Organisaatio'));
    beforeEach(module('Tarjonta'));
    beforeEach(module('SharedStateService'));
    beforeEach(module('TarjontaCache'));
    beforeEach(module('Logging'));

    beforeEach(function(){
        module(function ($provide) {
            $provide.value('Config', CONFIG_ENV_MOCK);
            var noop = function(){};
            $provide.value('LocalisationService', {getLocale:noop,t:noop});
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

    	var koulutushaku=function(oid){
    		return {
          	  "result" : {
          		    "tulokset" : [ {
          		      "oid" : oid,
          		      "version" : 0,
          		      "nimi" : {
          		        "fi" : "Aalto-korkeakoulusäätiö"
          		      },
          		      "tulokset" : [ {
          		        "oid" : "1.2.246.562.5.2013112010332461416361",
          		        "nimi" : {
          		          "fi" : "Finnish name",
          		          "sv" : "swedish name",
          		          "en" : "English name"
          		        },
          		        "kausi" : {
          		          "fi" : "Syksy",
          		          "sv" : "Höst",
          		          "en" : "Autumn"
          		        },
          		        "vuosi" : 2013,
          		        "tila" : "LUONNOS",
          		        "koulutusasteTyyppi" : "KORKEAKOULUTUS",
          		        "koulutuslaji" : {
          		        }
          		      } ]
          		    } ],
          		    "tuloksia" : 1
          		  },
          		  "status" : "OK"
          		};
    	};

    	var hakukohdehaku=function(oid){
    		return {
    			  "result" : {
    				    "tulokset" : [ {
    				      "oid" : oid,
    				      "version" : 0,
    				      "nimi" : {
    				        "fi" : "Optima, Jakobstad, Trädgårdsgatan",
    				        "sv" : "Optima, Jakobstad, Trädgårdsgatan"
    				      },
    				      "tulokset" : [ {
    				        "oid" : "1.2.246.562.5.2013092415391057849157",
    				        "nimi" : {
    				          "fi" : "Tuotteen suunnittelun ja valmistuksen koulutusohjelma, artesaani",
    				          "sv" : "Utbildningsprogram för produktplanering och -tillverkning, artesan",
    				          "en" : "Tuotteen suunnittelun ja valmistuksen koulutusohjelma, artesaani"
    				        },
    				        "kausi" : {
    				          "fi" : "Syksy",
    				          "sv" : "Höst",
    				          "en" : "Autumn"
    				        },
    				        "vuosi" : 2014,
              		        "tila" : "LUONNOS",
    				        "koulutusasteTyyppi" : "AMMATILLINEN_PERUSKOULUTUS",
    				        "pohjakoulutusvaatimus" : {
    				          "fi" : "PK",
    				          "sv" : "GR",
    				          "en" : "PK"
    				        },
    				        "koulutuslaji" : {
    				          "fi" : "Nuorten koulutus",
    				          "sv" : "Utbildning för unga",
    				          "en" : "Education and training for young"
    				        }
    				      } ]
    				    } ],
    				    "tuloksia" : 1,
    				  "status" : "OK"
    			  },
    				};
    	};

        $httpBackend.whenGET('/organisaatio-service/rest/organisaatio/1.2.3/parentoids').respond("1/1.2/1.2.3");
        $httpBackend.whenGET('/organisaatio-service/rest/organisaatio/1.2.3.4/parentoids').respond("1/1.2/1.2.3/1.2.3.4");
        $httpBackend.whenGET('/organisaatio-service/rest/organisaatio/1.2.4/parentoids').respond("1/1.2/1.2.4");
        $httpBackend.whenGET('/organisaatio-service/rest/organisaatio/1.2.5/parentoids').respond("1/1.2/1.2.5");

        $httpBackend.whenGET('/tarjonta-service/rest/v1/koulutus/search?koulutusOid=koulutus.1.2.5').respond(koulutushaku('1.2.5'));
        $httpBackend.whenGET('/tarjonta-service/rest/v1/koulutus/search?koulutusOid=koulutus.1.2.4').respond(koulutushaku('1.2.4'));
        $httpBackend.whenGET('/tarjonta-service/rest/v1/koulutus/search?koulutusOid=koulutus.1.2.3').respond(koulutushaku('1.2.3'));
        $httpBackend.whenGET('/tarjonta-service/rest/v1/koulutus/search?koulutusOid=koulutus.1.2.3.4').respond(koulutushaku('1.2.3'));
        $httpBackend.whenGET('/tarjonta-service/rest/v1/hakukohde/search?hakukohdeOid=hakukohde.1.2.5').respond(hakukohdehaku('1.2.5'));
        $httpBackend.whenGET('/tarjonta-service/rest/v1/hakukohde/search?hakukohdeOid=hakukohde.1.2.4').respond(hakukohdehaku('1.2.4'));
        $httpBackend.whenGET('/tarjonta-service/rest/v1/hakukohde/search?hakukohdeOid=hakukohde.1.2.3').respond(hakukohdehaku('1.2.3'));
        $httpBackend.whenGET('/tarjonta-service/rest/v1/hakukohde/search?hakukohdeOid=hakukohde.1.2.3.4').respond(hakukohdehaku('1.2.3'));

        // Parameters
        var parameterResponse = {
            "1.2.3.4" : {
                "PH_TJT" : { date : 1400149187429 },
                "PH_HKMT" : { date : 1399973779271 },
                "PH_HKLPT" : { date : 1399887372781 }
            }
        };

        $httpBackend.whenGET('/ohjausparametrit-service/api/v1/rest/parametri/ALL').respond(parameterResponse);
        $httpBackend.whenGET('/lomake-editori/buildversion.txt').respond('1234567890');
    };

    describe('Permission service shold answer ', function() {

    	/** executes test, called by test() */
    	var doTest = function(promise, $httpBackend) {
        	var result = undefined;

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
//            	console.log("parameter oid:", orgoid);
//            	console.log("testFn:", testFn);
            	result = doTest(testFn(PermissionService, orgoid), $httpBackend);
//            	console.log("testing access to " + orgoid + " expected result:" + expected + " actual result:" + result);
            	expect(expected).toEqual(result);
            }));
        };

    	//create koulutus
    	var testFn=function(PermissionService, orgOid){
    		return PermissionService.koulutus.canCreate(orgOid);
    	};

    	test(true, " for create koulutus when user has CRUD permission", "1.2.3", testFn);
    	test(false, " for create koulutus when user has RU permission","1.2.4", testFn);
    	test(false, " for create koulutus when user has R permission", "1.2.5", testFn);
    	test(false, " for create koulutus when user has CRUD permission (multi)", ["1.2.3","1.2.4"], testFn);
    	test(true, " for create koulutus when user has CRUD permission (multi)", ["1.2.3","1.2.3.4"], testFn);

    	//edit koulutus
    	testFn=function(PermissionService, oid){
    		return PermissionService.koulutus.canEdit(oid);
    	};

    	test(true, " for edit koulutus when user has CRUD permission", "koulutus.1.2.3", testFn);
    	test(true, " for edit koulutus when user has RU permission", "koulutus.1.2.4", testFn);
    	test(false, " for edit koulutus when user has R permission", "koulutus.1.2.5", testFn);
    	test(false, " for edit koulutus when user has R permission (multi)", ["koulutus.1.2.5", "koulutus.1.2.4"], testFn);
    	test(true, " for edit koulutus when user has R permission (multi)", ["koulutus.1.2.3", "koulutus.1.2.4"], testFn);

    	//delete koulutus
    	testFn=function(PermissionService, oid){
    		return PermissionService.koulutus.canDelete(oid);
    	};

    	test(true, " for delete koulutus when user has CRUD permission", "koulutus.1.2.3", testFn);
    	test(false, " for delete koulutus when user has RU permission", "koulutus.1.2.4", testFn);
    	test(false, " for delete koulutus when user has R permission", "koulutus.1.2.5", testFn);
    	test(false, " for delete koulutus when user has R permission (multi)", ["koulutus.1.2.5","koulutus.1.2.4"], testFn);
    	test(true, " for delete koulutus when user has R permission (multi)", ["koulutus.1.2.3","koulutus.1.2.3.4"], testFn);

    	//create hakukohde
    	var testFn=function(PermissionService, orgOid){
    		return PermissionService.hakukohde.canCreate(orgOid);
    	};

    	test(true, " for create hakukohde when user has CRUD permission", "1.2.3", testFn);
    	test(false, " for create hakukohde when user has RU permission", "1.2.4", testFn);
    	test(false, " for create hakukohde when user has R permission", "1.2.5", testFn);

    	//edit hakukohde
    	testFn=function(PermissionService, oid){
    		return PermissionService.hakukohde.canEdit(oid);
    	};

    	test(true, " for edit hakukohde when user has CRUD permission", "hakukohde.1.2.3", testFn);
    	test(true, " for edit hakukohde when user has RU permission", "hakukohde.1.2.4", testFn);
    	test(false, " for edit hakukohde when user has R permission", "hakukohde.1.2.5", testFn);
    	test(false, " for edit hakukohde when user has R permission (multi)", ["hakukohde.1.2.5","hakukohde.1.2.4"], testFn);
    	test(true, " for edit hakukohde when user has R permission (multi)", ["hakukohde.1.2.3","hakukohde.1.2.3.4"], testFn);

    	//delete hakukohde
    	testFn=function(PermissionService, oid){
    		return PermissionService.hakukohde.canDelete(oid);
    	};

    	test(true, " for delete hakukohde when user has CRUD permission", "hakukohde.1.2.3", testFn);
    	test(false, " for delete hakukohde when user has RU permission", "hakukohde.1.2.4", testFn);
    	test(false, " for delete hakukohde when user has R permission", "hakukohde.1.2.5", testFn);
    	test(false, " for delete hakukohde when user has R permission (multi)", ["hakukohde.1.2.5", "hakukohde.1.2.3"] , testFn);
    	test(true, " for delete hakukohde when user has R permission (multi)", ["hakukohde.1.2.3.4", "hakukohde.1.2.3"] , testFn);

    });

});
