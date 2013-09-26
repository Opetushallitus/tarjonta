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

angular.module('config', []).factory('Config', function(globalConfig) {
    var factoryObj = (function() {
        var ENVINRONMENT_CONFIGURATION = 'env'; //system properties, service uris etc.
        var APPLICATION_CONFIGURATION = 'app'; //angular application properties

        if (globalConfig === null)
            throw "Configuration variable cannot be null.";

        if (globalConfig[ENVINRONMENT_CONFIGURATION] === null)
            throw "Environment data cannot be null.";

        if (globalConfig[APPLICATION_CONFIGURATION] === null)
            throw "Angular application data cannot be null.";

        var output = {};
        output[ENVINRONMENT_CONFIGURATION] = angular.copy(globalConfig[ENVINRONMENT_CONFIGURATION]);
        output[APPLICATION_CONFIGURATION] = angular.copy(globalConfig[APPLICATION_CONFIGURATION]);
        return output;
    }());
    return factoryObj;
});

