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

    var uri = Config.env.tarjontaLocalisationRestUrl;
    $log.info("Localisations() - uri = ", uri);

    return $resource(uri + ":locale/:key", {
        key: '@key',
        locale: '@locale',
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
app.service('LocalisationService', function($log, $q, Localisations, Config) {
    $log.log("LocalisationService()");

    // Singleton state, default current locale
    // TODO is there some other gobal / app location for this?
    this.locale = "fi";

    this.getLocale = function() {
        return this.locale;
    }
    this.setLocale = function(value) {
        this.locale = value;
    }


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
        $log.log("getTranslation(key, params)", key, params);

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

            this.createMissingTranslation(key, this.locale, "[" + key + " " + this.locale + "]")
                    .then(function(newEntry) {
                $log.info("  created: ", newEntry);
                Config.env["tarjonta.localisations"].push(newEntry);
            }, function(value) {
                $log.error("  FAILED TO CREATE ", value);
            });

            // Create temporary placeholder for next requests
            this.localisationMapByLocaleAndKey = this.localisationMapByLocaleAndKey || {};
            this.localisationMapByLocaleAndKey[this.locale] = this.localisationMapByLocaleAndKey[this.locale] || {};
            this.localisationMapByLocaleAndKey[this.locale][key] = {key: key, locale: this.locale, value: "[" + key + "]"};

            result = "[" + key + "]";
        }

        // result = result + "-" + new Date();
        // $log.log(new Date() + ": getTranslation(" + key + ") --> " + result);
        return result;
    };


    this.isEmpty = function(str) {
        return (!str || 0 === str.length);
    };

    this.isBlank = function(str) {
        return (!str || /^\s*$/.test(str));
    };


    /**
     * Delete a translation.
     *
     * @param {translation} newEntry
     * @returns {Promise}
     */
    this.delete = function(entry) {
        $log.info("delete()", entry);

        var deferred = $q.defer();

        // TODO update in memory storage

        var parent = this;
        Localisations.delete(entry, function(data, status, headers, config) {
            $log.log("delete() - OK", data, status, headers, config);
            parent.updateLookupMap();
            deferred.resolve(entry);
        }, function(data, status, headers, config) {
            $log.error("save() - ERROR", data, status, headers, config, entry);
            deferred.reject(entry);
        });

        return  deferred.promise;
    };

    /**
     * Translation storage.
     *
     * @param {String} newEntry
     * @returns {Promise} a promise
     */
    this.save = function(newEntry) {
        $log.log("save()", newEntry);
        var deferred = $q.defer();

        if (!newEntry || this.isBlank(newEntry.key) || this.isBlank(newEntry.locale)) {
            deferred.reject({errors: "INVALID LOCALISATIN, null, empty key and/or localisation", value: newEntry});
        } else {
            var parent = this;

            // Is this new translation?
            if (!this.localisationMapByLocaleAndKey[newEntry.locale] || !this.localisationMapByLocaleAndKey[newEntry.locale][newEntry.key]) {
                // Update in memory storage
                Config.env["tarjonta.localisations"].push(newEntry);
            }

            // Try to save to the server
            Localisations.save(newEntry, function(data, status, headers, config) {
                $log.log("save() - OK", data, status, headers, config);
                parent.updateLookupMap();
                deferred.resolve(newEntry);
            }, function(data, status, headers, config) {
                $log.error("save() - ERROR", data, status, headers, config, newEntry);
                deferred.reject(newEntry);
            });
        }

        return deferred.promise;
    };

    /**
     * Create new translation.
     *
     * @param {String} key
     * @param {String} locale
     * @param {String} value
     * @returns promise whic will be filled with the create "entry" {key, locale, value} object when save was succesfull.
     */
    this.createMissingTranslation = function(key, locale, value) {
        $log.info("createMissingTranslation()", key, locale, value);
        return this.save({key: key, locale: locale, value: value});
    };

    /**
     * Reload translations from REST.
     * Stores fetched translations to global 'Config.env["tarjonta.localisations"]' array AND recreates lookup map.
     *
     * @returns {undefined}
     */
    this.reload = function() {
        $log.log("reload()");
        var parent = this;

        var deferred = $q.defer();

        // TODO ERROR HANDLING!
        Localisations.query({}, function(data) {
            $log.log("reload successfull! data = ", data);
            Config.env["tarjonta.localisations"] = data;
            parent.updateLookupMap();
            deferred.resolve(data);
        }, function(data) {
            $log.error("LocalisationService.reload() FAILED", data);
            deferred.reject(data);
        });

        return deferred.promise;
    }

    /**
     * Get list of currently loaded translations.
     *
     * @returns global APP_LOCALISATION_DATA, array of {key, locale, value} objects.
     */
    this.getTranslations = function() {
        return Config.env["tarjonta.localisations"];
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

        $log.log("===> result ", this.localisationMapByLocaleAndKey);
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

    $log.info("LocalisationService - initialising...");

    // Bootstrap
    this.updateLookupMap();

});

/**
 * LocalisationCtrl - a localisation controller.
 * An easy way to bind "t" function to gobal scope.
 */
app.controller('LocalisationCtrl', function($scope, LocalisationService, $log, Config) {
    $log.info("LocalisationCtrl()");

    $scope.CONFIG = Config;
    $scope.showTheSheisse = false;

    // Returns translation if it exists
    $scope.t = function(key, params) {
        $log.log("t(): " + key + ", " + params);
        return LocalisationService.t(key, params);
    };
});
