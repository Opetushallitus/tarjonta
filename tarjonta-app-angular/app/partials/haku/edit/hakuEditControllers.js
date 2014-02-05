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

var app = angular.module('app.haku.edit.ctrl', []);

/**
 * Haku edit controllers.
 *
 * Note: current haku is preloaded in "tarjontaApp.js" route definitions. Extracted in "init()"-method.
 *
 * @param {type} param1
 * @param {type} param2
 */
app.controller('HakuEditController',
        ['$q', '$route', '$scope', '$location', '$log', '$routeParams', '$window', '$modal', 'LocalisationService', 'HakuV1', 'ParameterService',
            function HakuEditController($q, $route, $scope, $location, $log, $routeParams, $window, $modal, LocalisationService, HakuV1, ParameterService) {
                $log.info("HakuEditController()", $scope);

                //parameter prefixes loaded/saved
                var prefixes=["PH_"];
                
                var hakuOid = $route.current.params.id;
                
                // TODO preloaded / resolved haku is where?
                // $route.local.xxx
                $scope.model = null;

                $scope.getLocale = function() {
                    return 'FI';
                };

                $scope.doRemoveHakuaika = function(hakuaika, index) {
                    $log.info("doRemoveHakuaika()", hakuaika, index);
                    if ($scope.model.hakux.result.hakuaikas.length > 1) {
                        $scope.model.hakux.result.hakuaikas.splice(index, 1);
                    }
                };

                $scope.doAddNewHakuaika = function() {
                    $log.info("doAddNewHakuaika()");
                    $scope.model.hakux.result.hakuaikas.push({nimi: "", alkuPvm: new Date().getTime(), loppuPvm: new Date().getTime()});
                };

                $scope.goBack = function(event) {
                    $log.info("goBack()");
                };

                $scope.saveLuonnos = function(event) {
                    var haku = $scope.model.hakux.result;

                    $log.info("saveLuonnos()", haku);

                    HakuV1.update(haku, function(result) {
                        $log.info("saveLuonnos() - OK", result);

                        $scope.model.showError = true;
                        $scope.model.validationmsgs = result.errors;

                    }, function (error) {
                        $log.info("saveLuonnos() - FAILED", error);

                        $scope.model.showError = true;
                    });

                    // $scope.model.showError = !$scope.model.showError;
                    // $scope.model.showSuccess = !$scope.model.showError;
                    // $log.info("saveLuonnos()");


                    console.log("->saveparameters");
                    $scope.saveParameters();
		    console.log("saveparameters->");

                };

                $scope.saveValmis = function(event) {
                    $log.info("saveValmis()");
                    $scope.saveParameters();
                };

                $scope.goToReview = function(event) {
                    $log.info("goToReview()");
                };

                $scope.onStartDateChanged = function(element, hakuaika) {
                    $log.info("onStartDateChanged: " + element + " - " + hakuaika);
                };

                $scope.onEndDateChanged = function(element, hakuaika) {
                    $log.info("onEndDateChanged: " + element + " - " + hakuaika);
                };

                $scope.onDateChanged = function(hakuaika) {
                    $log.info("onDateChanged: " + hakuaika);
                };


                $scope.checkHaunNimiValidity = function() {
                    // Count number of keys that have content
                    var numKeys = 0;

                    var result = true;
                    angular.forEach($scope.model.hakux.result.nimi, function (value, key) {
                        numKeys++;

                        result = result && !value;

                        // $log.info("  " + key + " == " + value + " --> result = " + result);

                        // regexp check for empty / whitespace
                        // $log.info("key: " + key + " -- value: " + value);
                    });

                    if (numKeys == 0) {
                        result = true;
                    }

                    // TODO check that at leas kieli_fi is defined?
                    // $log.info("checkHaunNimiValidity() : " + result);

                    return result;
                };
                
                
                $scope.saveParameters= function() {
                	console.log("save parameters->");
                	ParameterService.tallenna(hakuOid, $scope.model.parameter);
                };

                $scope.init = function() {
                    $log.info("init...");
                    
                    
                    var model = {
                        formControls: {},
                        showError: false,
                        showSuccess: false,
                        validationmsgs: [],
                        collapse: {
                            model: true
                        },

                        // Preloaded Haku result
                        hakux : $route.current.locals.hakux,

                        haku: {
                            hakuaikas: [
                                {nimi: null, alkaa: new Date(), loppuu: new Date()}
                            ],

                            date1 : new Date(),
                            date2 : 1380081600000,

                            // State of the checkbox for "oma hakulomake" - if uri is given the use it
                            hakulomakeKaytaJarjestemlmanOmaa: !!$route.current.locals.hakux.hakulomakeUri
                        },

                        parameter: {
                            // Tarjonnan julkaisu ja hakuaika
//                            PH_TJT : new Date(),
//                            PH_HKLPT : new Date(),
//                            PH_HKMT : new Date(),
//
//                            // Valinnat ja sijoittelu
//                            PH_KKM_S : new Date(),
//                            PH_KKM_E : new Date(),
//                            PH_HVVPTP : new Date(),
//                            PH_KTT_S : new Date(),
//                            PH_KTT_E : new Date(),
//                            PH_OLVVPKE_S : new Date(),
//                            PH_OLVVPKE_E : new Date(),
//                            PH_VLS_S : new Date(),
//                            PH_VLS_E : new Date(),
//                            PH_SS_S : new Date(),
//                            PH_SS_E : new Date(),
//                            PH_SSAVTM : true,
//                            PH_SST : 48,
//                            PH_SSKA : "23:59",
//                            PH_VTSSV : new Date(), // kk
//                            PH_VSSAV : new Date(), // kk
//
//                            // Tulokset ja paikan vastaanotto
//                            PH_JKLIP : new Date(),
//                            PH_HKP : new Date(),
//                            PH_VTJH_S : new Date(),
//                            PH_VTJH_E : new Date(),
//                            PH_EVR : new Date(),
//                            PH_OPVP : new Date(),
//                            PH_HPVOA : 7,
//
//                            // Lisähaku
//                            PH_HKTA : new Date(),
//                            // PH_HKP : new Date(),
//
//                            // Hakukauden parametrit
//                            PHK_PLPS_S : new Date(),
//                            PHK_PLPS_E : new Date(),
//                            PHK_PLAS_S : new Date(),
//                            PHK_PLAS_E : new Date(),
//                            PHK_LPAS_S : new Date(),
//                            PHK_LPAS_E : new Date(),
//
//                            // Tiedonsiirto
//                            PHK_KTTS : new Date(),
//                            PHK_TAVS_S : new Date(),
//                            PHK_TAVS_E : new Date(),
//                            PHK_TAVSM : true,
//                            PHK_KAVS_S : new Date(),
//                            PHK_KAVS_E : new Date(),
//                            PHK_KAVSM : true,
//                            PHK_VTST : 2,
//                            PHK_VTSAK : "23:59",

                        }


                    };

                    $log.info("init... done.");
                    $scope.model = model;
                    
				    //lataa parametrit
                    $scope.paramTemplates={}
                    for(var i=0;i<prefixes.length;i++) {
                    	
                   		(function(prefix){
                   			var p = ParameterService.haeTemplatet({path:prefix}).then(function(params){
                   				for(var i=0;i<params.length;i++) {
               						//var path = params[i].path;
               						//model.parameter[path]=undefined;
                   					$scope.paramTemplates[params[i].path]=params[i];
                   				};
                   			});
                        
                   			p.then(function(){
                   				ParameterService.haeParametrit({path:prefix, name:hakuOid}).then(function(params){
                   					for(var i=0;i<params.length;i++) {
                   						var path = params[i].path;
                   						var type = $scope.paramTemplates[path].type;
                   						var value = params[i].value;
                   						console.log("path, type", path, type);
//                   						if("DATE"===type) {
//                   							value=new Date(value);
//                   						}
                   						
                   						model.parameter[path]=value;
                   					}
                   				});
                   			});
                    	})(prefixes[i]);
                    }
                };
                $scope.init();
            }]);
