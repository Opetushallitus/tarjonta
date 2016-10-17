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
    'config',
    'Logging'
]);

app.factory('Ataru', function($resource, Config) {
    'use strict';
    var serviceUrl = Config.env.ataruRestUrl;
    return $resource(serviceUrl, {}, {
        getForms: {
            url: '/lomake-editori/api/forms',
            method: 'GET',
            withCredentials: true,
            headers: {
                'Content-Type': 'application/json; charset=UTF-8'
            }
        }
    });
});

app.factory('AtaruService', function(Ataru) {
    'use strict';
    return {
        getForms: function() {
            return Ataru.getForms().$promise.then(function(response) {
                return response.forms;
            });
        }
    };
});
