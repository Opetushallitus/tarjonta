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

/*
 * Help: 
 * Add service factory(/js/shared/config.js) to your module.
 * Module name : 'config'.
 * Factory name : 'Config'.
 * 
 * FAQ:
 * How to get an environment variable by a key: <factory-object>.env[<string-key>].
 * How to get AngularJS application variable by a key: <factory-object>.app[<string-key>].
 * 
 * Example:
 * cfg.env["koodi-uri.koulutuslaji.nuortenKoulutus"];
 * result value : "koulutuslaji_n"
 */
angular.module('config', []).factory('Config', function(globalConfig) {
    var factoryObj = (function() {
        var ENV_CONF = 'env'; //system properties from common properties files, service uris etc.
        var APP_CONF = 'app'; //AngularJS application properties
        var DEV_CONF = 'developer.config.location'; //AngularJS developer properties

        if (globalConfig === null || typeof globalConfig === 'undefined')
            throw "Configuration variable cannot be null.";

        if (globalConfig[ENV_CONF] === null || typeof globalConfig[ENV_CONF] === 'undefined')
            throw "Environment data cannot be null.";

        if (globalConfig[APP_CONF] === null || typeof globalConfig[APP_CONF] === 'undefined')
            throw "Angular application data cannot be null.";

        var output = {};
        output[ENV_CONF] = angular.copy(globalConfig[ENV_CONF]);
        output[APP_CONF] = angular.copy(globalConfig[APP_CONF]);

        if (Boolean(globalConfig[APP_CONF][DEV_CONF])) {
            /*
             * Try to load a developer config file:
             * Overwrites ENVIRONMENT object values with developer config object values, 
             * also adds developer config values if non existent in environment object.
             * 
             * How to configure:
             * 
             * 1. Add property like {'developer.config.location' : 'dev/dev-configuration.json'} to your APPLICATION property file.
             * 2. Create a file to the location : <angular-project-location>/env/dev-configuration.json
             * 3. Add your own properties to JSON object:
             * {{tarjonta.admin.webservice.url.backend": "localhost:1234/tarjonta-service/rest/"},{ 'A':'1'}, {...}}
             */
            $.get(globalConfig[APP_CONF][DEV_CONF]).done(function(data) {
                console.log("------------------ DEVELOPER CONFIG LOADED ------------------");
                console.log(data);
                console.log("-------------------------------------------------------------");
                output[ENV_CONF] = $.extend({}, output[ENV_CONF], data);
            }).fail(function() {
                //A silent error occured, no developer config loaded.
            })
        }

        return output;
    }());
    return factoryObj;
});

