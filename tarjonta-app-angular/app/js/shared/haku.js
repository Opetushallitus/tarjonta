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
            save: {
                method: 'POST',
                withCredentials: true,
                headers: {'Content-Type': 'application/json; charset=UTF-8'}
            },
            get: {
                method: 'GET',
                withCredentials: true,
                headers: {'Content-Type': 'application/json; charset=UTF-8'}
            },
            findAll: {
                method: 'GET',
                withCredentials: true,
                params: {oid : 'findAll'},
                isArray: false,
                headers: {'Content-Type': 'application/json; charset=UTF-8'}
            },
            mget:{
              url:Config.env.tarjontaRestUrlPrefix + 'haku/multi',
              method: 'GET',
              withCredentials: true,
              isArray: false,
            },
            search: {
              method: 'GET',
              withCredentials: true,
              isArray: false,
              headers: {'Content-Type': 'application/json; charset=UTF-8'}
            },
            remove: {
                method: 'DELETE',
                withCredentials: true,
                headers: {'Content-Type': 'application/json; charset=UTF-8'}
            }
        });

    });

/**
 * Haku Service
 */
app.factory('HakuV1Service', function($q, HakuV1, LocalisationService) {

  var userKieliUri = LocalisationService.getKieliUri();

  /**
   * Palauttaa haun nimen käyttäjän kielellä, tai fallback fi,sv,en tai "[Ei nimeä]"
   */
  var resolveNimi = function(haku) {
    return haku.nimi[userKieliUri]||haku.nimi["kieli_fi"]||haku.nimi["kieli_sv"]||haku.nimi["kieli_en"]||"[Ei nimeä]";
  };

  /**
   * palauttaa promisen hakutulokseen, resolvaa nimen valmiiksi
   */
  var mget = function(oids){
    console.log("multiget:", oids);
      var defer = $q.defer();
      HakuV1.mget({oid:oids}).$promise.then(function(haut){
        console.log("haut:", haut.result);
        angular.forEach(haut.result, function(haku, key){
          haku.nimi=resolveNimi(haku);
        });
        console.log("resolving haut");
        defer.resolve(haut.result);
      });

      return defer.promise;
   };


  return {
    /**
     * Hae hakuja määritellyillä hakuehdoilla
     */
    search:function(parameters){
      console.log("Searching with: ", parameters);


      var defer = $q.defer();

      return HakuV1.search(parameters).$promise.then(function(data){
//        var haut=[];
        return mget(data.result);
//        for(var i=0;i<data.result.length;i++) {
//          promises.push(HakuV1.get(data.result[i]).$promise.then(function(hakuresult){
//            var haku=hakuresult.result;
//            haut.push(haku);
//            haku.nimi= resolveNimi(haku);
//          }));
//        }
//        $q.all(promises).then(function(){
//          defer.resolve(haut);
//        });
      });
//
//      return defer.promise;
    }


  };

});
