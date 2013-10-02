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
 *
 * NOTE: this module assumes that all the translations are PRELOADED in
 * global scope to object: "APP_LOCALISATION_DATA".
 *
 * @see index.html for implementation
 *
 * @author mlyly
 */
var app = angular.module('localisation', ['ngResource', 'config']);

app.factory('Localisations', function($log, $resource, Config) {

    var uri = Config.env.tarjontaRestUrlPrefix + "localisation";
    $log.info("Localisations() - uri = ", uri);

    return $resource(uri + "/:locale/:key", {
        key: '@key',
        locale: '@locale',
    }, {
        update: {method: 'PUT'}
    });

});


/**
 * "Localisation" factory, returns resource for operating on localisations.
 */

app.filter('tt', ['LocalisationService', function(LocalisationService) {
        return function(text) {
            return LocalisationService.t(text);
        };
    }]);

app.directive('tt', ['LocalisationService', '$timeout', function(LocalisationService) {
        return {
            restrict: 'EA',
            replace: true,
            template: '<div>TT TEMPLATE</div>',
            scope: false,
            compile: function(tElement, tAttrs, transclude) {
                // console.log("TT COMPILE, tt=" + tAttrs["tt"] + " - date=" + new Date());

                var t = LocalisationService.t(tAttrs["tt"]);
                tElement.text(t);

                return function postLink(scope, iElement, iAttrs, controller) {
                    // $timeout(scope.$destroy.bind(scope), 0);
                }
            }
        }
    }]);

/**
 * Singleton service for localisations.
 *
 * Saves localisations to this.
 *
 * Usage:
 * <pre>
 * LocalisationService.t("this.is.the.key")  == localized value
 * LocalisationService.t("this.is.the.key2", ["array", "of", "values"])  == localized value
 * </pre>
 */
app.service('LocalisationService', function($log, Localisations) {
    $log.debug("LocalisationService()");

    // Singleton state
    this.locale = "fi";

    this.localisationMapByLocaleAndKey = {};

    /**
     * Get translation, fill in possible parameters.
     *
     * @param {type} key
     * @param {type} params
     * @returns {unresolved}
     */
    this.getTranslation = function(key, params) {

        var v0 = this.localisationMapByLocaleAndKey[this.locale];
        var v = v0 ? v0[key] : undefined;
        var result;

        if (v) {
            // Extract result and replace parameters if any
            result = v.value;

            // Expand parameters
            if (params != undefined) {
                result = result.replace(/{(\d+)}/g, function(match, number) {
                    return typeof params[number] != 'undefined' ? params[number] : match;
                });
            }
        } else {
            // Unknown translation, maybe create placeholder for it?
            $log.warn("UNKNOWN TRANSLATION: key='" + key + "'");

            result = this.createMissingTranslation(key, this.locale, "[" + key + "]");

            // TODO Fake "creation", really call service to create the translation placeholder for real
            APP_LOCALISATION_DATA[key] = newEntry;

            result = APP_LOCALISATION_DATA[key].value;
        }

        // result = result + "-" + new Date();
        // $log.debug(new Date() + ": getTranslation(" + key + ") --> " + result);
        return result;
    };

    this.createMissingTranslation = function(key, locale, value) {
        console.log("createMissingTranslation()", key, locale, value);

        var newEntry = {
            "key": key,
            "locale": locale,
            "value": value
        };

        // in memory storage
        APP_LOCALISATION_DATA.push(newEntry);

        // Try to save to the server?
        Localisations.save(newEntry, function(data, status, headers, config) {
            console.log("createMissingTranslation 1FAILURE?", data);
            console.log("createMissingTranslation 2FAILURE?", status);
            console.log("createMissingTranslation 3FAILURE?", headers);
            console.log("createMissingTranslation 4FAILURE?", config);
        }, function(data, status, headers, config) {
            console.log("created new translation to server", data, status, headers, config);
        });

        this.updateLookupMap();
    };

    this.reload = function() {
        console.log("reload()");
        Localisations.query({}, function(data) {
            console.log("reload successfull! data = ", data);
            APP_LOCALISATION_DATA = data;

            this.updateLookupMap();
        });
    }

    this.updateLookupMap = function() {
        console.log("updateLookupMap() - STORE TRANSLATIONS TO MAP FOR FASTER ACCESS!");

        var tmp = {};

        for (var localisationIndex in APP_LOCALISATION_DATA) {
            var localisation = APP_LOCALISATION_DATA[localisationIndex];
            var mapByLocale = tmp[localisation.locale];
            if (!mapByLocale) {
                tmp[localisation.locale] = {};
                mapByLocale = tmp[localisation.locale];
            }
            mapByLocale[localisation.key] = localisation;
        }

        this.localisationMapByLocaleAndKey = tmp;

        console.log("===> result ", this.localisationMapByLocaleAndKey);
    };

    this.t = function(key, params) {
        return this.getTranslation(key, params);
    };

});

/**
 * LocalisationCtrl - a localisation controller.
 * An easy way to bind "t" function to gobal scope.
 */
app.controller('LocalisationCtrl', function($scope, LocalisationService, $log) {
    $log.debug("LocalisationCtrl()");

    // Returns translation if it exists
    $scope.t = function(key, params) {
        $log.debug("t(): " + key + ", " + params);
        return LocalisationService.t(key, params);
    };
});
