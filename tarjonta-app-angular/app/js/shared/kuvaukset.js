var app = angular.module('Kuvaus', ['ngResource','config']);

app.factory('Kuvaus',function($http,Config,$q){

    var kuvausUriPrefix = "kuvaus/";

    return {

        findKuvausWithId : function(kuvausTunniste) {


            var promise = $q.defer();

            if (kuvausTunniste !== undefined) {

                var kuvausGetUri = Config.env.tarjontaRestUrlPrefix+kuvausUriPrefix+kuvausTunniste;

                $http.get(kuvausGetUri)
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

        insertKuvaus : function(tyyppi,kuvaus) {

            var promise = $q.defer();

            if (kuvaus !== undefined && tyyppi !== undefined) {

                var kuvausPostUri = Config.env.tarjontaRestUrlPrefix+kuvausUriPrefix+tyyppi;
                $http.post(kuvausPostUri,kuvaus)
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

        updateKuvaus : function(tyyppi,kuvaus) {

            var promise = $q.defer();

            if (kuvaus !== undefined && tyyppi !== undefined) {

                var kuvausPostUri = Config.env.tarjontaRestUrlPrefix+kuvausUriPrefix+tyyppi;
                $http.put(kuvausPostUri,kuvaus)
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

        findKuvausWithTyyppiNimiOppilaitos : function(tyyppi, nimi, oppilaitosTyyppi) {

            var promise = $q.defer();

            if (tyyppi !== undefined && nimi !== undefined && oppilaitosTyyppi !== undefined) {

                var kuvausQueriUri = Config.env.tarjontaRestUrlPrefix+kuvausUriPrefix+tyyppi
                +oppilaitosTyyppi+nimi;

                $http.get(kuvausQueriUri)
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

        },

        findAllNimesWithTyyppi : function(tyyppi) {

            var promise = $q.defer();

            if (tyyppi !== undefined )  {

                var nimiQueryUri = Config.env.tarjontaRestUrlPrefix+kuvausUriPrefix+tyyppi+"/nimet";

                $http.get(nimiQueryUri)
                    .success(function(data){
                       promise.resolve(data);
                    })
                    .error(function(data){
                       promise.resolve(data);
                    });

            }

            return promise.promise;

        }

    };

})