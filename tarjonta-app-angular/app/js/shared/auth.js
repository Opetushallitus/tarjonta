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
 * NOTE: data (pre)loaded at server startup in index.hrml to Config.env["cas.myroles"]
 */


var app = angular.module("auth", ['ngResource', 'config']);

var USER = "USER_";
var READ = "_READ";
var UPDATE = "_READ_UPDATE";
var CRUD = "_CRUD";
var OPH_ORG = "xxx";

app.factory('MyRolesModel', function($http, $log, Config) {

    console.log("MyRolesModel()");
    OPH_ORG = Config.env["root.organisaatio.oid"];

    var factory = (function() {
        console.log("MyRolesModel.factory()");

        var instance = {};
        instance.organisaatiot=[];
        instance.myroles = Config.env["cas.myroles"] || [];
        
        /**
         * prosessoi roolilistan läpi ja poimii tietoja, esim kieli, organisaatiot
         */
        var processRoleList=function(roolit) {
        	if(roolit!==undefined) {
        		for(var i=0;i<roolit.length;i++) {
        			var oidList = roolit[i].match(/_[0-9\.]+$/g);
        			if(oidList && oidList.length>0) {
        				//poimi tarjonta roolit
        				if(roolit[i].indexOf("APP_TARJONTA")==0) {
        					var org = oidList[0].substring(1);
                			console.log("adding org:", org);
        					instance.organisaatiot.push(org);
        				}
        			}
        			
        			if(roolit[i].indexOf('LANG_')==0) {
        				instance.lang = roolit[i].substring(5);
        				console.log("setting lang:", instance.lang);
        			}
        		}
        	}
        
        };
        
      	processRoleList(instance.myroles);

        instance.refresh = function() {
            // TODO some timeout for the cache?
            if (instance.myroles.length == 0) {
                $http.get(Config.env.casUrl)
                        .success(function(roolit) {
                    console.log("MyRolesModel.factory() - roles loaded successfully from: " + Config.env.casUrl);
                    instance.myroles = roolit;
                  	processRoleList(instance.myroles);

                })
                        .error(function(data, status, headers, config) {
                    console.log("MyRolesModel.factory() - FAILED to load roles from: " + Config.env.casUrl);
                    console.log("MyRolesModel.factory() - data: " + data);
                    console.log("MyRolesModel.factory() - status: " + status);
                    console.log("MyRolesModel.factory() - headers: " + headers);
                    console.log("MyRolesModel.factory() - config: " + config);
                });
            }
        };

        instance.debug = function() {
            console.log("MyRolesModel.debug():");
            console.log("  roles: ", instance);
        };

        return instance;
    })();

    return factory;
});

app.factory('AuthService', function($q, $http, $timeout, $log, MyRolesModel) {

    var _startsWith = function(str, startWith) {
        return str.slice(0, startWith.length) === startWith;
    };

    var _restOf = function(str, startWith) {
        if (_startsWith(str, startWith)) {
            return str.slice(startWith.length);
        } else {
            return str;
        }
    };

    var _endsWith = function(str, endsWith) {
        return str.slice(-endsWith.length) === endsWith;
    };

    var _beginningOf = function(str, endsWith) {
        if (_endsWith(str, endsWith)) {
            return str.slice(0, str.length - endsWith.length);
        } else {
            return str;
        }
    };



    var isLoggedIn = function() {
        $log.info("isLoggedIn()");
        if (MyRolesModel.myroles.length > 0) {
            return true;
        }
    };

    var getUsername = function() {
        $log.info("username()");
        var entry = _.find(MyRolesModel.myroles, function(x) {
            return _startsWith(x, USER);
        });

        if (entry) {
            return _restOf(entry, USER);
        } else {
            return undefined;
        }
    };


    // organisation check
    var readAccess = function(service, org) {
        $log.info("readAccess()", service, org);
        if (MyRolesModel.myroles.indexOf(service + READ + "_" + org) > -1 ||
                MyRolesModel.myroles.indexOf(service + UPDATE + "_" + org) > -1 ||
                MyRolesModel.myroles.indexOf(service + CRUD + "_" + org) > -1) {
            return true;
        }
    };

    var updateAccess = function(service, org) {
        $log.info("updateAccess()", servcice, org);
        if (MyRolesModel.myroles.indexOf(service + UPDATE + "_" + org) > -1 ||
                MyRolesModel.myroles.indexOf(service + CRUD + "_" + org) > -1) {
            return true;
        }
    };

    var crudAccess = function(service, org) {
        $log.info("crudAccess()", servcice, org);
        if (MyRolesModel.myroles.indexOf(service + CRUD + "_" + org) > -1) {
            return true;
        }
    };

    var accessCheck = function(service, orgOid, accessFunction) {
        $log.info("accessCheck()", service, orgOid, accessFunction);
        var deferred = $q.defer();
        var waitTime = 10;

        var check = function() {
            $log.info("accessCheck().check()", service, orgOid, accessFunction);
            MyRolesModel.refresh();
            waitTime = waitTime + 500;
            if (!isLoggedIn()) {
                $timeout(check, waitTime);
            } else {
                // OK, is logged in - check organisations
                $http.get(ORGANISAATIO_URL_BASE + "organisaatio/" + orgOid + "/parentoids").success(function(result) {
                    var found = false;
                    result.split("/").forEach(function(org) {
                        if (accessFunction(service, org)) {
                            found = true;
                        }
                    });
                    if (found) {
                        deferred.resolve();
                    } else {
                        deferred.reject();
                    }
                });
            }
        };

        $timeout(check, waitTime);

        return deferred.promise;
    };

    return {
        getUsername: function() {
            return getUsername();
        },
        isLoggedIn: function() {
            return isLoggedIn();
        },
        readOrg: function(service, orgOid) {
            return accessCheck(service, orgOid, readAccess);
        },
        updateOrg: function(service, orgOid) {
            return accessCheck(service, orgOid, updateAccess);
        },
        crudOrg: function(service, orgOid) {
            return accessCheck(service, orgOid, crudAccess);
        },
        /**
         * Palauttaa käyttäjän kielen
         */
        getLanguage: function(){
        	//TODO palauta kopio?
        	return MyRolesModel.lang;
        },

        /**
         * Palauttaa käyttäjän organisaatiot (tarjonta-app)
         */
        getOrganisations: function(){
        	//TODO palauta kopio?
        	return MyRolesModel.organisaatiot;
        	
        }

    };
});

