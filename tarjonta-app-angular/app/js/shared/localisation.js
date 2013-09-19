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

/**
 * Localisation support for Angular apps.
 * + registers module "localisation".
 * + Requires "ngResource".
 *
 * @author mlyly
 */
var app = angular.module('localisation', ['ngResource'])

/**
 * "Localisation" factory, returns resource for operating on localisations.
 */
app.factory('Localisation', function($resource) {
    return $resource('localisation.json', {}, {
        query: {method: 'GET', headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }}
    });
});

/**
 * LocalisationCtrl - a localisation controller.
 */
app.controller('LocalisationCtrl', function($scope, Localisation) {
    console.log("LocalisationCtrl()");

    // TODO how to get browser locale ? use locale?
    $scope.locale = "fi";
    $scope.localisations = [];

    Localisation.query(function(data) {
        console.log("Loaded: " + data);
        $scope.localisations = data;
    });

    // Returns translation if it exists
    $scope.t = function(key, params) {
        // console.log("t(" + key + ", " + params + ")");
        var v = $scope.localisations[key];

        if (v != undefined) {
            var result = v.value;

            if (params != undefined) {
                result = result.replace(/{(\d+)}/g, function(match, number) {
                    return typeof params[number] != 'undefined' ? params[number] : match;
                });
            }

            return result;
        } else {
            // Unknown translation, maybe create placeholder for it?
            console.log("UNKNOWN TRANSLATION: " + key);
            return "[" + key + "]";
        }
    };
});

