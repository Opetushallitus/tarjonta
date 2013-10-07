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

angular.module('Haku', [ 'ngResource', 'config' ])
.factory('HakuService',function($resource,$q,Config){


        var hakuUri = Config.env["host.base-uri"] + Config.env["haku.uri"];

        return {

            getAllHakus : function(locale) {

                var promiseOfPromise = $q.defer();



                var resource = $resource(hakuUri);

               resource.get(function(data){

                    var promisesArray = [];
                    angular.forEach(data,function(oidItem){


                          var promise = $q.defer();
                           //TBD
                           var hakuOidUri = Config.env["host.base-uri"] + "";

                           var secondResource = $resource(hakuOidUri);

                            secondResource.get(function(hakuDto){
                                promise.resolve(hakuDto);
                            });

                          promisesArray.push(promise);

                    });
                    $q.all(promisesArray).then(function(){
                        promiseOfPromise.resolve();
                    });

                });

                return promiseOfPromise.promise;

            }


        };

    });