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
var app = angular.module('ataru', [
    'ngResource',
    'Logging'
]);

app.factory('AtaruService', function($resource, $q, $http, AuthService) {
    'use strict';
    var ataruRoles = [
        'APP_HAKULOMAKKEENHALLINTA',
        'APP_ATARU_EDITORI'
    ];
    var config = {
        withCredentials: true,
        headers: { 'Content-Type': 'application/json; charset=UTF-8' }
    };
    return {
        getAtaruAuthorisation: function() {
            var oid = AuthService.getUserDefaultOid();
            return $q.all(ataruRoles.map(function(role) {
                return AuthService.crudOrg(oid, role);
            })).then(function(authorised) {
                var isAuthorized = authorised.indexOf(true) > -1;
                return isAuthorized;
            });
        },
        preWarming: function() {
            return $http.get(window.url('ataru-service.editor'));
        },
        getForms: function() {
            return $http.get(window.url('ataru-service.rest.forms'), config).then(function(response) {
                return (response.data.forms) ? response.data.forms : [];
            });
        }
    };
});
