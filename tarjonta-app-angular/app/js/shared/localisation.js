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
 * global scope to configuration object:
 * </b>Config.env["tarjonta.localisations"]</b>
 *
 * See "index.html" how the pre-loading is done to following global variable:
 * <b>window.CONFIG.env["tarjonta.localisations"]</b>
 *
 * @see index.html for preload implementation
 *
 * @author mlyly
 */
var app = angular.module('localisation', ['ngResource', 'config']);

/**
 * "Localisations" factory, returns resource for operating on localisations.
 */
app.factory('Localisations', function($log, $resource, Config) {

    var uri = Config.env.tarjontaLocalisationRestUrl;
    $log.info("Localisations() - uri = ", uri);

    return $resource(uri + "/:id", {
        id: '@id'
    }, {
        update: {
            method: 'PUT',
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        save: {
            method: 'POST',
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        }
    });

});

/**
 * UI-directive for using translations.
 *
 * usage:
 * <pre>
 *   &lt;div tt="this.is.translation.key">foo</div&gt;
 *
 *   {{ t("this.is.another.key" }}
 *   {{ t("this.is.another.key", ["param", "param too"]) }}
 *
 *   {{ tl("this.is.another.key", "sv" }}
 *   {{ tl("this.is.anotker.key", "sv", ["param", "param too"]) }}
 * </pre>
 */
app.directive('tt', ['LocalisationService', '$timeout', function(LocalisationService) {
        return {
            restrict: 'EA',
            replace: true,
            //template: '<div>TT TEMPLATE</div>',
            scope: false,
            compile: function(tElement, tAttrs, transclude) {
                var t = LocalisationService.t(tAttrs["tt"]);
                tElement.html(t);

                return function postLink(scope, iElement, iAttrs, controller) {
                    // $timeout(scope.$destroy.bind(scope), 0);
                };
            }
        };
    }]);

/**
 * Singleton service for localisations.
 *
 * Usage:
 * <pre>
 * LocalisationService.t("this.is.the.key")  == localized value in users locale
 * LocalisationService.t("this.is.the.key2", ["array", "of", "values"])  == localized value in users locale
 *
 * LocalisationService.tl("this.is.the.key", "fi")  == localized value in given locale
 * LocalisationService.tl("this.is.the.key2", "fi", ["array", "of", "values"])  == localized value in given locale
 * </pre>
 */
app.service('LocalisationService', function($log, $q, Localisations, Config, AuthService) {
    $log.log("LocalisationService()");

    // Singleton state, default current locale for the user
    this.locale = AuthService.getLanguage();

    $log.info("  user locale = " + this.locale);

    /**
     * Get users locale OR default locale "fi".
     *
     * @returns {String}
     */
    this.getLocale = function() {
        // Default fallback
        if (this.locale === undefined) {
            $log.warn("  aha! undefined locale - using fi!");
            this.locale = "fi";
        }
        return this.locale;
    };

    this.setLocale = function(value) {
        this.locale = value;
    };

    // Localisations: MAP[locale][key] = {key, locale, value};
    // This map is used for quick access to the localisation (which are in list)
    // see this.updateLookupMap() how it is filled
    this.localisationMapByLocaleAndKey = {};

    /**
     * Get translation, fill in possible parameters.
     *
     * @param {String} key
     * @param {String} locale, if undefined get it vie getLocale()
     * @param {Array} params
     * @returns {String} translation value, parameters replaced
     */
    this.getTranslation = function(key, locale, params) {
        // $log.info("getTranslation(key, locale, params), key=" + key + ", l=" +  locale + ",params=" + params);

        // Use default locale if not specified
        if (locale === undefined) {
            locale = this.getLocale();
        }

        // Get translations by locale
        var v0 = this.localisationMapByLocaleAndKey[locale];

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

            // Save this to server
            var newEntry = {category: "tarjonta", key: key, locale: locale, value: "[" + key + "-" + locale + "]"};

            // Create temporary placeholder for next requests
            this.localisationMapByLocaleAndKey = this.localisationMapByLocaleAndKey || {};
            this.localisationMapByLocaleAndKey[locale] = this.localisationMapByLocaleAndKey[locale] || {};
            this.localisationMapByLocaleAndKey[locale][key] = newEntry;

            // Update in memory storage for local translations
            this.getTranslations().push(newEntry);

//            // Then create missing translation to the server side
            Localisations.save(newEntry,
                    function(data) {
                        $log.info("  created new translation to server side! data = ", data);
                    },
                    function(data, status, headers, config) {
                        $log.warn("  FAILED to created new translation to server side! ", data, status, headers, config);
                    });

            result = newEntry.value;
        }

        return result;
    };

    this.isEmpty = function(str) {
        return (!str || 0 === str.length);
    };

    this.isBlank = function(str) {
        return (!str || /^\s*$/.test(str));
    };

    /**
     * Get list of currently loaded translations.
     *
     * @returns global APP_LOCALISATION_DATA, array of {key, locale, value} objects.
     */
    this.getTranslations = function() {
        Config.env["tarjonta.localisations"] = Config.env["tarjonta.localisations"] || [];
        return Config.env["tarjonta.localisations"];
    };


    /**
     * Loop over all translations, create new lookup map to store translations to
     * MAP[locale][translation_key] == {key, locale, value};
     *
     * @returns created lookup map and stores it to local services variable
     */
    this.updateLookupMap = function() {
        $log.info("updateLookupMap()");

        var tmp = {};

        for (var localisationIndex in Config.env["tarjonta.localisations"]) {
            var localisation = Config.env["tarjonta.localisations"][localisationIndex];
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
     * Get translation value. Assumes use of current UI locale (LocalisationService.getLocale())
     *
     * If translation with current locale and key is not found then new translation entry will be created.
     *
     * @param {String} key
     * @param {Array} params
     * @returns {String} value for translation
     */
    this.t = function(key, params) {
        return this.getTranslation(key, this.getLocale(), params);
    };

    /**
     * Get translation in given locale.
     *
     * @param {type} key
     * @param {type} locale
     * @param {type} params
     *
     * @returns Resolved translation
     */
    this.tl = function(key, locale, params) {
        return this.getTranslation(key, locale, params);
    };

    $log.info("LocalisationService - initialising...");

    // Bootstrap in memory lookup table
    this.updateLookupMap();
});

/**
 * LocalisationCtrl - a localisation controller.
 * An easy way to bind "t" function to global scope. (now attached in "body")
 */
app.controller('LocalisationCtrl', function($scope, LocalisationService, $log, Config) {
    $log.info("LocalisationCtrl()");

    $scope.CONFIG = Config;

    // Returns translation if it exists
    $scope.t = function(key, params) {
        return LocalisationService.t(key, params);
    };

    // Get translation in given locale
    $scope.tl = function(key, locale, params) {
        return LocalisationService.tl(key, locale, params);
    };

});
