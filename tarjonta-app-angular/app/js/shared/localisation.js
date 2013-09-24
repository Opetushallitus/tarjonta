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
 * Singleton service for localisations.
 *
 * Saves localisations to this.
 *
 * Usage:
 * <pre>
 * LocalisationService.t("this.is.the.key")  == localized value
 * </pre>
 */
app.service('LocalisationService', function(Localisation) {

    console.log("LocalisationService()");

    // Singleton state
    this.locale = "fi";
    this.localisations = [];

    console.log("  loading()...");
    Localisation.query(function(data) {
        console.log("  loading()... done: " + data);
        this.localisations = data;
    });

    this.t = function(key, params) {
        // Get translation
        var v = this.localisations[key];
        var result;

        if (v != undefined) {
            // Extract result and replace parameters if any
            result = v.value;

            if (params != undefined) {
                result = result.replace(/{(\d+)}/g, function(match, number) {
                    return typeof params[number] != 'undefined' ? params[number] : match;
                });
            }
        } else {
            // Unknown translation, maybe create placeholder for it?
            console.log("UNKNOWN TRANSLATION: " + key);

            // TODO Fake "creation", really call service to create the translation placeholder for real
            v = {
                value : "[" + key + "]"
            };
            this.localisations[key] = v;

            result = v.value;
        }

        return result;
    };

});

/**
 * LocalisationCtrl - a localisation controller.
 * An easy way to bind "t" function to gobal scope.
 */
app.controller('LocalisationCtrl', function($scope, LocalisationService) {
    console.log("LocalisationCtrl()");

    // Returns translation if it exists
    $scope.t = function(key, params) {
        console.log("LocalisationCtrl.t");
        return LocalisationService.t(key, params);
    };
});

//
// TODO Add directive "t" since {{}} cause too many? bindings to be done.
//
