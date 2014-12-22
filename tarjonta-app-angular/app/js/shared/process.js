/*
 * Copyright (c) 2014 The Finnish Board of Education - Opetushallitus
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
'use strict';
console.log('process-load');
angular.module('Process', [
    'ngResource',
    'config',
    'Logging'
]) /**
 * Process resource
 */ .factory('ProcessV1', function($resource, $log, Config) {
    $log = $log.getInstance('ProcessV1');
    $log.info('ProcessV1()');
    var serviceUrl = Config.env.tarjontaRestUrlPrefix + 'process/:id';
    return $resource(serviceUrl, {}, {
        get: {
            method: 'GET',
            withCredentials: true,
            headers: {
                'Content-Type': 'application/json; charset=UTF-8'
            }
        },
        list: {
            url: Config.env.tarjontaRestUrlPrefix + 'process/',
            method: 'GET',
            withCredentials: true,
            isArray: true,
            headers: {
                'Content-Type': 'application/json; charset=UTF-8'
            }
        }
    });
}) /**
 * Process Service
 */ .factory('ProcessV1Service', function($log, $q, ProcessV1, LocalisationService, AuthService) {
    $log = $log.getInstance('ProcessV1Service');
    var defaultPollingTime = 10000;
    var polls = {};
    function getProcessStatus(id) {
        return ProcessV1.get({
            id: id
        }).$promise;
    }
    return {
        listProcesses: function() {
            return ProcessV1.list().$promise;
        },
        getProcessStatus: getProcessStatus,
        startPolling: function(id, pollingTime, callback) {
            // Check to make sure poller doesn't already exist
            if (!polls[id]) {
                var poller = function() {
                    console.log('polling polling polling...');
                    getProcessStatus(id).then(callback);
                };
                poller();
                polls[id] = setInterval(poller, pollingTime || defaultPollingTime);
            }
        },
        stopPolling: function(id) {
            clearInterval(polls[id]);
            delete polls[id];
        }
    };
});
console.log('process-load-ok');