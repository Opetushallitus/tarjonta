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
var app = angular.module('auth', [
    'ngResource',
    'config',
    'Logging'
]);
var USER = 'USER_';
var READ = '_READ';
var UPDATE = '_READ_UPDATE';
var CRUD = '_CRUD';
var OPH_ORG = 'xxx';
app.factory('MyRolesModel', function($http, $log, Config) {
    $log = $log.getInstance('MyRolesModel');
    $log.info('MyRolesModel()');
    OPH_ORG = Config.env['root.organisaatio.oid'];
    var factory = function() {
        var instance = {};
        // instance.organisaatiot=[];
        // Roles + organisation oid array stored in this map
        instance.rolesToOrgsMap = {};
        var defaultUserInfo = {
            lang: 'fi',
            groups: []
        };
        instance.userinfo = Config.env.cas && Config.env.cas.userinfo || defaultUserInfo;
        instance.myroles = instance.userinfo.groups;
        /**
             * prosessoi roolilistan läpi ja poimii tietoja, esim organisaatiot
             */
        var processRoleList = function(roolit) {
            // $log.info("processRoleList()", roolit);
            // Regexp to match/split roles and organisations
            // "APP_XXX_1.2.3.4" -> ["APP_XXX_1.2.3.4" "APP_XXX", "1.2.3.4"]
            if (roolit !== undefined) {
                for (var i = 0; i < roolit.length; i++) {
                    // Matchaa roolit + organisaatiot
                    var r = /^(.*)_([\d|.]+)$/g;
                    var m = r.exec(roolit[i]);
                    if (m && m.length == 3) {
                        var role = m[1];
                        var org = m[2];
                        if (angular.isDefined(instance.rolesToOrgsMap[role])) {
                        }
                        else {
                            // Create place for the role
                            instance.rolesToOrgsMap[role] = [];
                        }
                        // Add organisatio to roles map if not there already
                        if (instance.rolesToOrgsMap[role].indexOf(org) == -1) {
                            instance.rolesToOrgsMap[role].push(org);
                        }
                    }
                }
            }
        };
        //        $log.debug("myroles:", instance.myroles);
        processRoleList(instance.myroles);
        return instance;
    }();
    return factory;
});
app.factory('AuthService', function($q, $http, $timeout, $log, MyRolesModel, Config, SharedStateService) {
    $log = $log.getInstance('AuthService');
    //$log.debug("prefix:", ORGANISAATIO_URL_BASE);
    // CRUD ||UPDATE || READ
    var readAccess = function(service, org) {
        //$log.info("readAccess()", service, org);
        return MyRolesModel.myroles.indexOf(service + READ + '_' + org) > -1 ||
            MyRolesModel.myroles.indexOf(service + UPDATE + '_' + org) > -1 ||
            MyRolesModel.myroles.indexOf(service + CRUD + '_' + org) > -1;
    };
    // CRUD ||UPDATE
    var updateAccess = function(service, org) {
        //      $log.debug("checking updateaccess on", service, org);
        if (org) {
            $log.info('updateAccess()', service, org, MyRolesModel);
            var updateKey = service + UPDATE + '_' + org;
            var crudKey = service + CRUD + '_' + org;
            //        console.log("looking for:", updateKey, crudKey)
            return MyRolesModel.myroles.indexOf(updateKey) > -1 || MyRolesModel.myroles.indexOf(crudKey) > -1;
        }
        else {
            return MyRolesModel.myroles.indexOf(service + UPDATE) > -1 ||
                MyRolesModel.myroles.indexOf(service + CRUD) > -1;
        }
    };
    // CRUD
    var crudAccess = function(service, org) {
        //        $log.info("crudAccess()", service, org);
        return MyRolesModel.myroles.indexOf(service + CRUD + '_' + org) > -1;
    };
    //async call, returns promise!
    var accessCheck = function(service, orgOid, accessFunction) {
        //        $log.info("accessCheck(), service,org,fn:", service, orgOid, accessFunction);
        if (orgOid === undefined || orgOid.length && orgOid.length === 0) {
            throw 'missing org oid!';
        }
        var deferred = $q.defer();
        //        $log.debug("accessCheck().check()", service, orgOid, accessFunction);
        if (orgOid === Config.env['root.organisaatio.oid']) {
            //      	  console.log("oph speciaali");
            deferred.resolve(accessFunction(service, orgOid));
        }
        else {
            $http.get(window.url("organisaatio-service.parentOids", orgOid), {
                cache: true
            }).then(function(result) {
                // $log.debug("got:", result);
                var ooids = result.data.split('/');
                for (var i = 0; i < ooids.length; i++) {
                    if (accessFunction(service, ooids[i])) {
                        deferred.resolve(true);
                        return;
                    }
                }
                deferred.resolve(false);
            }, function() {
                    //failure funktio
                    //           	$log.debug("could not get url:", url);
                    deferred.resolve(false);
                });
        }
        return deferred.promise;
    };
    return {
        getUsername: function() {
            return Config.env.cas.userinfo.uid;
        },
        isLoggedIn: function() {
            return Config.env.cas.userinfo.uid !== undefined;
        },
        /**
         * onko käyttäjällä lukuoikeus, palauttaa promisen
         */
        readOrg: function(orgOid, service) {
            return accessCheck(service || 'APP_TARJONTA', orgOid, readAccess);
        },
        /**
         * onko käyttäjällä päivitysoikeus, palauttaa promisen
         */
        updateOrg: function(orgOid, service) {
            return accessCheck(service || 'APP_TARJONTA', orgOid, updateAccess);
        },
        /**
         * onko käyttäjällä crud oikeus, palauttaa promisen
         */
        crudOrg: function(orgOid, service) {
            //        	$log.debug("crudorg", orgOid, service);
            return accessCheck(service || 'APP_TARJONTA', orgOid, crudAccess);
        },
        /**
         * Palauttaa käyttäjän kielen, tai oletuksena suomi jos ei määritelty.
         */
        getLanguage: function() {
            return MyRolesModel.userinfo.lang || 'FI';
        },
        /**
         * Palauttaa käyttäjän oidin
         */
        getUserOid: function() {
            return MyRolesModel.userinfo.oid;
        },
        /**
         * Palauttaa oletus käyttäjän tarjonnalle
         */
        getUserDefaultOid: function() {
            var orgs = this.getOrganisations([
                'APP_TARJONTA_CRUD',
                'APP_TARJONTA_UPDATE',
                'APP_TARJONTA_READ'
            ]);
            var selectedOrg = SharedStateService.getFromState('SelectedOrgOid');
            if (selectedOrg) {
                if (angular.isArray(selectedOrg)) {
                    selectedOrg = selectedOrg[0];
                }
                return selectedOrg;
            }
            //käyttäjän oletusorganisaatio
            if (orgs && orgs.length > 0) {
                return orgs[0];
            }
        },
        /**
         * Palauttaa käyttäjän etunimen
         */
        getFirstName: function() {
            return MyRolesModel.userinfo.firstName;
        },
        /**
         * Palauttaa käyttäjän etunimen
         */
        getLastName: function() {
            return MyRolesModel.userinfo.lastName;
        },
        isUserOph: function() {
            return this.isUserInAnyOfRolesInOneOfOrganisations(undefined, [Config.env['root.organisaatio.oid']]);
        },
        /**
         * Returns true IFF organisations of user has in given "roles" contains any of given "organisationOids".
         */
        isUserInAnyOfRolesInOneOfOrganisations: function(roles, organisationOids) {
            var result = false;
            // Default roles if not defined are
            roles = roles ? roles : [
                'APP_TARJONTA_CRUD',
                'APP_TARJONTA_UPDATE'
            ];
            // Force parameter to be array
            if (!(roles instanceof Array)) {
                roles = [roles];
            }
            // Default org == OPH
            organisationOids = organisationOids ? organisationOids : [Config.env['root.organisaatio.oid']];
            // Force parameter to be array
            if (!(organisationOids instanceof Array)) {
                organisationOids = [organisationOids];
            }
            // Loop over orgs user has in given roles
            var organisations = this.getOrganisations(roles);
            angular.forEach(organisations, function(roleOrg) {
                angular.forEach(organisationOids, function(requiredOrg) {
                    if (requiredOrg == roleOrg) {
                        // OK, user has one of required role in one of required organisation
                        result = true;
                    }
                });
            });
            return result;
        },
        /**
         * Return "true" joss käyttäjällä on OPH organisaatio missään annetussa roolissa
         */
        isUserOphInAnyOfRoles: function(roles) {
            return this.isUserInAnyOfRolesInOneOfOrganisations(roles, [Config.env['root.organisaatio.oid']]);
        },
        /**
         * Palauttaa käyttäjän organisaatiot joihin muokkaus/luontioikeudet.
         *
         * Parametrina lista rooleista joiden organisaatioista ollaan kiinostuneita.
         * OLETUKSENA (jos ei annata mitään) käytetään ["APP_TARJONTA_CRUD", "APP_TARJONTA_UPDATE"].
         */
        getOrganisations: function(roles) {
            // Default roles if not defined are
            roles = roles ? roles : [
                'APP_TARJONTA_CRUD',
                'APP_TARJONTA_UPDATE'
            ];
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
            return result;
        }
    };
});