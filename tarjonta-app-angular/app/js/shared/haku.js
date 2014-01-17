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

'use strict';

var app = angular.module('Haku', ['ngResource', 'config']);


app.factory('HakuService', function($http, $q, Config) {


            var hakuUri = Config.env["tarjontaRestUrlPrefix"] + "haku/findAll";

            return {
                getAllHakus: function(locale) {

                    var hakuPromise = $q.defer();



                    $http({method: 'GET', url: hakuUri}).success(function(data, status, headers, config) {
                        // this callback will be called asynchronously

                        hakuPromise.resolve(data.result);
                        // when the response is available
                    }).error(function(data, status, headers, config) {

                        console.log('ERROR OCCURRED GETTING HAKUS: ', status);
                        // called asynchronously if an error occurs
                        // or server returns response with an error status.
                    });



                    return hakuPromise.promise;

                },
                getHakuWithOid: function(oid) {

                    var hakuPromise = $q.defer();

                    var hakuOidUri = Config.env["tarjontaRestUrlPrefix"] + "haku/" + oid;

                    $http({method: 'GET', url: hakuOidUri}).success(function(data, status, headers, config) {

                        hakuPromise.resolve(data.result);

                    }).error(function(data, status, headers, config) {

                        console.log('ERROR GETTING HAKU WITH OID');
                    }
                    );

                    return hakuPromise.promise;

                }





            };

        });


app.factory('HakuV1', function($resource, $log, Config) {
        $log.info("HakuV1()");

        var serviceUrl = Config.env.tarjontaRestUrlPrefix + "haku/:oid";

        return $resource(serviceUrl, {oid: '@oid'}, {
            update: {
                method: 'PUT',
                headers: {'Content-Type': 'application/json; charset=UTF-8'}
            },
            insert: {
                method: 'POST',
                headers: {'Content-Type': 'application/json; charset=UTF-8'}
            },
            get: {
                method: 'GET',
                headers: {'Content-Type': 'application/json; charset=UTF-8'}
            },
            getAll: {
                method: 'GET',
                headers: {'Content-Type': 'application/json; charset=UTF-8'}
            },
            remove: {
                method: 'DELETE',
                headers: {'Content-Type': 'application/json; charset=UTF-8'}
            }
        });

    });
