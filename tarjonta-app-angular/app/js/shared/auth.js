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
 *
 * @type @exp;angular@call;module
 */
var app = angular.module("tarjontaApp.auth", ['ngResource', 'config']);

// datafile

var READ = "_READ";
var UPDATE = "_READ_UPDATE";
var CRUD = "_CRUD";

app.factory('MyRolesModel', function($http, $log, Config) {

    console.log("MyRolesModel()");

    var casUrl = Config.env.casUrl;

    var factory = (function() {
        console.log("MyRolesModel.factory()");

        var instance = {};
        instance.myroles = [];

        instance.refresh = function() {
            if (instance.myroles.length == 0) {
                $http.get(Config.env.casUrl)
                        .success(function(result) {
                    console.log("MyRolesModel.factory() - roles loaded successfully from: " + Config.env.casUrl);
                    instance.myroles = result;
                })
                        .error(function(data, status, headers, config) {
                    console.log("MyRolesModel.factory() - FAILED to load roles from: " + Config.env.casUrl);
                    console.log("MyRolesModel.factory() - status: " + status);
                    console.log("MyRolesModel.factory() - headers: " + headers);
                });
            }
        };

        instance.debug = function () {
            console.log("MyRolesModel.debug():");
            console.log("  roles: " + instance);
        };

        return instance;
    })();

    return factory;
});

app.factory('AuthService', function($q, $http, $timeout, $log, MyRolesModel) {

    var isLoggedIn = function() {
        $log.info("isLoggedIn()");
        if (MyRolesModel.myroles.length > 0) {
            return true;
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

    // OPH check -- voidaan ohittaa organisaatioiden haku
    var ophRead = function(service) {
        return (MyRolesModel.myroles.indexOf(service + READ + "_" + OPH_ORG) > -1
                || MyRolesModel.myroles.indexOf(service + UPDATE + "_" + OPH_ORG) > -1
                || MyRolesModel.myroles.indexOf(service + CRUD + "_" + OPH_ORG) > -1);
    };

    var ophUpdate = function(service) {
        return (MyRolesModel.myroles.indexOf(service + UPDATE + "_" + OPH_ORG) > -1
                || MyRolesModel.myroles.indexOf(service + CRUD + "_" + OPH_ORG) > -1);
    };

    var ophCrud = function(service) {
        return (MyRolesModel.myroles.indexOf(service + CRUD + "_" + OPH_ORG) > -1);
    };

    var ophAccessCheck = function(service, accessFunction) {
        var deferred = $q.defer();
        var waitTime = 10;

        var check = function() {
            MyRolesModel.refresh();
            waitTime = waitTime + 500;
            if (MyRolesModel.myroles.length === 0) {
                $timeout(check, waitTime);
            } else {
                if (accessFunction(service)) {
                    deferred.resolve();
                } else {
                    deferred.reject();
                }
            }
        }

        $timeout(check, waitTime);

        return deferred.promise;
    }

    return {
        readOrg: function(service, orgOid) {
            return accessCheck(service, orgOid, readAccess);
        },
        updateOrg: function(service, orgOid) {
            return accessCheck(service, orgOid, updateAccess);
        },
        crudOrg: function(service, orgOid) {
            return accessCheck(service, orgOid, crudAccess);
        },
        readOph: function(service) {
            return ophAccessCheck(service, ophRead);
        },
        updateOph: function(service) {
            return ophAccessCheck(service, ophUpdate);
        },
        crudOph: function(service) {
            return ophAccessCheck(service, ophCrud);
        },
    };
});

