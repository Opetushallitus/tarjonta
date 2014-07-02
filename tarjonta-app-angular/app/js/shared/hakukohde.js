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

var app = angular.module('Hakukohde', ['ngResource','config', 'Logging']);

//TODO: after refactoring the rest to v1 change this
app.factory('Hakukohde',function($resource, Config){

    var hakukohdeUri = Config.env.tarjontaRestUrlPrefix+"hakukohde/:oid";

    return $resource(hakukohdeUri,{oid:'@oid'},{
        update: {
            method: 'PUT',
            withCredentials: true,
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        remove : {
          method: 'DELETE',
          withCredentials: true,
          headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        save: {
            method: 'POST',
            withCredentials: true,
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        checkStateChange: {
          url:Config.env.tarjontaRestUrlPrefix + 'hakukohde/:oid/stateChangeCheck',
          method: 'GET',
          withCredentials: true,
        },
        get : {
            method: 'GET',
            withCredentials: true,
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        }
        
    });

});

app.factory('Liite',function($resource, Config) {
    var hakukohdeLiiteUri = Config.env.tarjontaRestUrlPrefix+"hakukohde/:hakukohdeOid/liite/:liiteId";

    return $resource(hakukohdeLiiteUri,{hakukohdeOid:'@hakukohdeOid',liiteId:'@liiteId'},{
        update: {
            method: 'PUT',
            withCredentials: true,
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        save: {
            method: 'POST',
            withCredentials: true,
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        getAll : {
            method: 'GET',
            withCredentials: true,
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        remove : {
            method: 'DELETE',
            withCredentials: true,
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        }
    });

});


app.factory('Valintakoe',function($resource, $log,$q, Config) {

   var hakukohdeValintakoeUri = Config.env.tarjontaRestUrlPrefix+"hakukohde/:hakukohdeOid/valintakoe/:valintakoeOid";

    return $resource(hakukohdeValintakoeUri,{hakukohdeOid:'@hakukohdeOid',valintakoeOid:'@valintakoeOid'},{
        update: {
            method: 'PUT',
            withCredentials: true,
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        save: {
            method: 'POST',
            withCredentials: true,
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        getAll : {
            method: 'GET',
            withCredentials: true,
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        remove : {
            method: 'DELETE',
            withCredentials: true,
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        }

    });

});

app.factory('HakukohdeKoulutukses',function($http,Config,$q){

    return {
        removeKoulutuksesFromHakukohde : function(hakukohdeOid,koulutusOids){

            if (hakukohdeOid !== undefined && koulutusOids !== undefined) {
                var hakukohdeKoulutusUri =  Config.env.tarjontaRestUrlPrefix+"hakukohde/"+hakukohdeOid+"/koulutukset";

                $http.post(hakukohdeKoulutusUri,koulutusOids,{
                    withCredentials: true,
                    headers : {'Content-Type': 'application/json; charset=UTF-8'}
                }).success(function(data){
                    return data;
                }).error(function(data){
                        return false;
                    });
            }
        },

        addKoulutuksesToHakukohde : function(hakukohdeOid, koulutusOids) {
            var promise = $q.defer();
            if (hakukohdeOid !== undefined && koulutusOids !== undefined) {
                var hakukohdeKoulutusUri = Config.env.tarjontaRestUrlPrefix+"hakukohde/"+hakukohdeOid+"/koulutukset/lisaa";
                $http.post(hakukohdeKoulutusUri,koulutusOids,{
                    withCredentials: true,
                    headers : {'Content-Type': 'application/json; charset=UTF-8'}
                }).success(function(data){
                      promise.resolve("OK"===data.status); 
                }).error(function(data){
                    promise.resolve(false);
                    });
            }
           return promise.promise;
        } ,

        getKoulutusHakukohdes : function(koulutusOid) {
            var promise = $q.defer();
            if (koulutusOid !== undefined) {
                var getKoulutusHakukohdesUri = Config.env.tarjontaRestUrlPrefix+"koulutus/"+koulutusOid+"/hakukohteet";
                $http.get(getKoulutusHakukohdesUri)
                    .success(function(data){
                        promise.resolve(data);
                    })
                    .error(function(data){
                        promise.resolve(data);
                    });
            } else {
                promise.resolve();
            }
            return promise.promise;
        } ,

        getHakukohdeKoulutukses : function(hakukohdeOid) {
            if (hakukohdeOid !== undefined) {
                var promise = $q.defer();
                var getHakukohdeKoulutuksesUri = Config.env.tarjontaRestUrlPrefix+"hakukohde/"+hakukohdeOid+"/koulutukset";
                $http.get(getHakukohdeKoulutuksesUri)
                    .success(function(data){
                       promise.resolve(data);
                    })
                    .error(function(data){
                      promise.resolve(data);
                    });
                return promise.promise;

            } else {
                return undefined;
            }
        }
    };

});