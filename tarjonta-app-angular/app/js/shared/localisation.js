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
 *
 * LocalisationService.reload()
 * createMissingTranslation(key, locale, value)
 * getTranslations
 * </pre>
 */
app.service('LocalisationService', function($log, Localisations) {
    $log.debug("LocalisationService()");

    // Singleton state, default current locale
    // TODO is there some other gobal / app location for this?
    this.locale = "fi";

    // Localisations: MAP[locale][key] = {key, locale, value};
    this.localisationMapByLocaleAndKey = {};

    /**
     * Get translation, fill in possible parameters.
     *
     * @param {String} key
     * @param {Array} params
     * @returns {String} translation value, parameters replaced
     */
    this.getTranslation = function(key, params) {
        $log.debug("getTranslation(key, params)", key, params);

        // Get translations by locale
        var v0 = this.localisationMapByLocaleAndKey[this.locale];

        // Get translations by key
        var v = v0 ? v0[key] : undefined;
        var result;

        if (v) {
            // Found translation, replace possible parameters
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

            var newEntry = this.createMissingTranslation(key, this.locale, "[" + key + "]");

            // TODO Fake "creation", really call service to create the translation placeholder for real
            APP_LOCALISATION_DATA[key] = newEntry;

            result = APP_LOCALISATION_DATA[key].value;
        }

        // result = result + "-" + new Date();
        // $log.debug(new Date() + ": getTranslation(" + key + ") --> " + result);
        return result;
    };

    /**
     * Create new translation.
     *
     * @param {String} key
     * @param {String} locale
     * @param {String} value
     * @returns Object of {key, locale, value}
     */
    this.createMissingTranslation = function(key, locale, value) {
        $log.info("createMissingTranslation()", key, locale, value);

        var parent = this;

        // Create new translation
        var newEntry = {
            "key": key,
            "locale": locale,
            "value": value
        };

        // Update in memory storage
        APP_LOCALISATION_DATA.push(newEntry);

        // Try to save to the server
        Localisations.save(newEntry, function(data, status, headers, config) {
            $log.info("1 createMissingTranslation result: ", data);
            $log.info("2 createMissingTranslation status: ", status);
            $log.info("3 createMissingTranslation headers: ", headers);
            $log.info("4 createMissingTranslation config: ", config);
        }, function(data, status, headers, config) {
            $log.info("5 created new translation to server", data, status, headers, config);
        });

        this.updateLookupMap();

        return newEntry;
    };

    /**
     * Reload translations from REST.
     * Stores fetched translations to global 'APP_LOCALISATION_DATA' array AND recreates lookup map.
     *
     * TODO return a promise!
     *
     * @returns {undefined}
     */
    this.reload = function() {
        $log.info("reload()");
        var parent = this;

        Localisations.query({}, function(data) {
            $log.info("reload successfull! data = ", data);
            APP_LOCALISATION_DATA = data;

            parent.updateLookupMap();
        });

        // TODO return a promise!
    }

    /**
     * Get list of currently loaded translations.
     *
     * @returns global APP_LOCALISATION_DATA, array of {key, locale, value} objects.
     */
    this.getTranslations = function() {
        return APP_LOCALISATION_DATA;
    };


    /**
     * Loop over all translations, create new lookup map to store translations to
     * MAP[locale][translation_key] == {key, locale, value};
     *
     * @returns created lookup map
     */
    this.updateLookupMap = function() {
        $log.info("updateLookupMap()");

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

        $log.info("===> result ", this.localisationMapByLocaleAndKey);
        return this.localisationMapByLocaleAndKey;
    };

    /**
     * Get translation value.
     *
     * If translation with current locale and key is not found then new translation entry will be created.
     *
     * @param {String} key
     * @param {Array} params
     * @returns {String} value for translation
     */
    this.t = function(key, params) {
        return this.getTranslation(key, params);
    };


    $log.info("LocalisationService - initialize()");

    // Bootstrap
    this.updateLookupMap();

});

/**
 * LocalisationCtrl - a localisation controller.
 * An easy way to bind "t" function to gobal scope.
 */
app.controller('LocalisationCtrl', function($scope, LocalisationService, $log) {
    $log.info("LocalisationCtrl()");

    // Returns translation if it exists
    $scope.t = function(key, params) {
        $log.debug("t(): " + key + ", " + params);
        return LocalisationService.t(key, params);
    };
});
