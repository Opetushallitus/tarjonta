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

// Apinoitu valintaperusteet / auth.js


/**
 * Authentication module.
 * NOTE: data (pre)loaded at server startup in index.hrml to Config.env["cas.userinfo"]
 */

var app = angular.module("auth", ['ngResource', 'config', 'Logging']);

var USER = "USER_";
var READ = "_READ";
var UPDATE = "_READ_UPDATE";
var CRUD = "_CRUD";
var OPH_ORG = "xxx";

app.factory('MyRolesModel', function($http, $log, Config) {

    $log = $log.getInstance("MyRolesModel");

    $log.info("MyRolesModel()");

    OPH_ORG = Config.env["root.organisaatio.oid"];

    var factory = (function() {

        var instance = {};
        // instance.organisaatiot=[];

        // Roles + organisation oid array stored in this map
        instance.rolesToOrgsMap={};

        var defaultUserInfo = {
            lang:"fi",
            groups:[]
        };

        instance.userinfo = Config.env.cas!==undefined ? Config.env.cas.userinfo || defaultUserInfo:defaultUserInfo;
        instance.myroles = instance.userinfo.groups;

        /**
         * prosessoi roolilistan läpi ja poimii tietoja, esim organisaatiot
         */
        var processRoleList=function(roolit) {
            // $log.info("processRoleList()", roolit);

            // Regexp to match/split roles and organisations
            // "APP_XXX_1.2.3.4" -> ["APP_XXX_1.2.3.4" "APP_XXX", "1.2.3.4"]

        	if(roolit!==undefined) {
        		for(var i=0;i<roolit.length;i++) {

                    // Matchaa roolit + organisaatiot
                    var r = /^(.*)_([\d|.]+)$/g;
                    var m = r.exec(roolit[i]);
                    if (m && m.length == 3) {
                        var role = m[1];
                        var org = m[2];

                        if (angular.isDefined(instance.rolesToOrgsMap[role])) {
                            // role already exists
                        } else {
                            // Create place for the role
                            instance.rolesToOrgsMap[role] = [];
                        }

                        // Add organisatio to roles map if not there already
                        if (instance.rolesToOrgsMap[role].indexOf(org) == -1) {
                            instance.rolesToOrgsMap[role].push(org);
                        }
                    } else {
                        $log.info("SKIPPING: '" + roolit[i] + "'");
                    }
        		}

                $log.info("AuthService: ROLES TO ORGANISATIONS MAP: ", instance.rolesToOrgsMap);
        	}
        };

//        $log.debug("myroles:", instance.myroles);

      	processRoleList(instance.myroles);

        return instance;
    })();

    return factory;
});

app.factory('AuthService', function($q, $http, $timeout, $log, MyRolesModel, Config) {

    $log = $log.getInstance("AuthService");

    var ORGANISAATIO_URL_BASE;

	if(undefined!==Config.env){
		ORGANISAATIO_URL_BASE=Config.env["organisaatio.api.rest.url"];
	}

	//$log.debug("prefix:", ORGANISAATIO_URL_BASE);

    // CRUD ||UPDATE || READ
    var readAccess = function(service, org) {
        //$log.info("readAccess()", service, org);
    	return MyRolesModel.myroles.indexOf(service + READ + "_" + org) > -1 ||
               MyRolesModel.myroles.indexOf(service + UPDATE + "_" + org) > -1 ||
               MyRolesModel.myroles.indexOf(service + CRUD + "_" + org) > -1;
    };

    // CRUD ||UPDATE
    var updateAccess = function(service, org) {
      if(org) {
        //$log.info("updateAccess()", service, org, MyRolesModel);
        return MyRolesModel.myroles.indexOf(service + UPDATE + "_" + org) > -1 ||
                MyRolesModel.myroles.indexOf(service + CRUD + "_" + org) > -1;
      } else {
        return MyRolesModel.myroles.indexOf(service + UPDATE) > -1 ||
        MyRolesModel.myroles.indexOf(service + CRUD) > -1;
      }
    };

    // CRUD
    var crudAccess = function(service, org) {
//        $log.info("crudAccess()", service, org);
        return MyRolesModel.myroles.indexOf(service + CRUD + "_" + org) > -1;
    };

    //async call, returns promise!
    var accessCheck = function(service, orgOid, accessFunction) {
//        $log.info("accessCheck(), service,org,fn:", service, orgOid, accessFunction);

        if(orgOid===undefined || (orgOid.length && orgOid.length==0)) {
        	throw "missing org oid!";
        }
        var deferred = $q.defer();
//        $log.debug("accessCheck().check()", service, orgOid, accessFunction);
      	var url = ORGANISAATIO_URL_BASE + "organisaatio/" + orgOid + "/parentoids";
//       	$log.debug("getting url:", url);

      	$http.get(url,{cache:true}).then(function(result) {
//        $log.debug("got:", result);

        var ooids = result.data.split("/");

        for(var i=0;i<ooids.length;i++) {
            if (accessFunction(service, ooids[i])) {
                deferred.resolve(true);
                return;
            }
        }
        deferred.resolve(false);
        }, function(){ //failure funktio
//           	$log.debug("could not get url:", url);
            deferred.resolve(false);
        });

        return deferred.promise;
    };

    return {
        getUsername: function() {
        	return Config.env.cas.userinfo.uid;
        },
        isLoggedIn: function() {
        	return Config.env.cas.userinfo.uid!==undefined;
        },
        /**
         * onko käyttäjällä lukuoikeus, palauttaa promisen
         * @param service
         * @param orgOid
         * @returns
         */
        readOrg: function(orgOid, service) {
            return accessCheck(service||'APP_TARJONTA', orgOid, readAccess);
        },
        /**
         * onko käyttäjällä päivitysoikeus, palauttaa promisen
         * @param service
         * @param orgOid
         * @returns
         */
        updateOrg: function(orgOid, service) {
            return accessCheck(service||'APP_TARJONTA', orgOid, updateAccess);
        },
        /**
         * onko käyttäjällä crud oikeus, palauttaa promisen
         * @param orgOid
         * @param service
         * @returns
         */
        crudOrg: function(orgOid, service) {
//        	$log.debug("crudorg", orgOid, service);
            return accessCheck(service||'APP_TARJONTA', orgOid, crudAccess);
        },
        /**
         * Palauttaa käyttäjän kielen
         */
        getLanguage: function(){
        	return MyRolesModel.userinfo.lang;
        },
        /**
         * Palauttaa käyttäjän oidin
         */
        getUserOid: function(){
        	return MyRolesModel.userinfo.oid;
        },

        /**
         * Palauttaa käyttäjän etunimen
         */
        getFirstName: function(){
        	return MyRolesModel.userinfo.firstName;
        },

        /**
         * Palauttaa käyttäjän etunimen
         */
        getLastName: function(){
        	return MyRolesModel.userinfo.lastName;
        },

        isUserOph : function() {

            var ophUser = false;

            angular.forEach(MyRolesModel.organisaatiot,function(orgOid){

                if (orgOid === Config.env['root.organisaatio.oid']) {
                    ophUser = true;
                }
            });

            return ophUser;

        },

        /**
         * Palauttaa käyttäjän organisaatiot joihin muokkaus/luontioikeudet.
         *
         * Parametrina lista rooleista joiden organisaatioista ollaan kiinostuneita.
         * OLETUKSENA (jos ei annata mitään) käytetään ["APP_TARJONTA_CRUD", "APP_TARJONTA_UPDATE"].
         */
        getOrganisations: function(roles){
            // Default roles if not defined are
            roles = roles ? roles : ["APP_TARJONTA_CRUD", "APP_TARJONTA_UPDATE"];

            // Force parameter to be array
            if (!(roles instanceof Array)) {
                roles = [roles];
            }

            var result = [];
            angular.forEach(roles, function(role) {
                var orgs = MyRolesModel.rolesToOrgsMap[role];
                angular.forEach(orgs, function(org) {
                    if (result.indexOf(org) == -1) {
                        result.push(org);
                    }
                });
            });

            $log.debug("AuthService.getOrganisations()", roles, result);

            return result;
        },

    };
});



/**
 * Enhance logging output to contain datestamp + possible location information.
 *
 * If this module is loded the "$log" is enchanced with "getInstance(CLASS_NAME)" method
 * and the all log entries logged with that logger has that "ClASS_NAME" displayed.
 * Helps to locate logged lines in the code.
 */

app = angular.module("Logging", []);

app.config(["$provide", function($provide) {

        console.log("auth.js.Logging.config - enhance logging...");

        function formatDate(date) {
            var result = "";
            if (date) {
                var d = date; // new Date();

                result = result + ((d.getHours() < 10) ? "0" : "") + d.getHours();
                result = result + ((d.getMinutes() < 10) ? "0" : "") + d.getMinutes();
                result = result + ((d.getSeconds() < 10) ? "0" : "") + d.getSeconds();
            }

            return result;
        }


        $provide.decorator('$log', ["$delegate", function($delegate)
            {
                var _$log = (function($log)
                {
                    return {
                        log: $log.log,
                        info: $log.info,
                        warn: $log.warn,
                        debug: $log.debug,
                        error: $log.error
                    };
                })($delegate);

                var prepareLogFn = function(logFn, logLevel, logClass) {
                    var enhanced = function() {
                        var args = [].slice.call(arguments),
                                now = new Date();

                        // Prepend timestamp, level, class + actual result
                        args[0] = formatDate(now) + " - " + logLevel + " - " + logClass + " :: " + args[0];

                        // Call the original with the output prepended with formatted timestamp
                        logFn.apply(null, args);
                    };

                    // Special... only needed to support angular-mocks expectations
                    enhanced.logs = [ ];

                    return enhanced;
                };

                // Default implementations, no class name
                $delegate.log = prepareLogFn(_$log.log, "?", "?");
                $delegate.info = prepareLogFn(_$log.info, "I", "?");
                $delegate.warn = prepareLogFn(_$log.warn, "W", "?");
                $delegate.debug = prepareLogFn(_$log.debug, "D", "?");
                $delegate.error = prepareLogFn(_$log.error, "E", "?");

                // Class spesific implementations
                $delegate.getInstance = function(logClass) {
                    return {
                        log : prepareLogFn(_$log.log, "?", logClass),
                        info : prepareLogFn(_$log.info, "I", logClass),
                        warn : prepareLogFn(_$log.warn, "W", logClass),
                        debug : prepareLogFn(_$log.debug, "D", logClass),
                        error : prepareLogFn(_$log.error, "E", logClass)
                    }
                };

                return $delegate;
            }]);

        console.log("auth.js.Logging.config - enhance logging... done.");

}]);
