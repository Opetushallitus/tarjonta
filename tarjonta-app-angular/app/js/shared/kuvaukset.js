var app = angular.module('Kuvaus', [
    'ngResource',
    'config',
    'Logging',
    'TarjontaPermissions'
]);
app.factory('Kuvaus', function($http, Config, $q, $log, PermissionService) {
    $log = $log.getInstance('Kuvaus');
    var kuvausUriPrefix = 'kuvaus/';
    return {
        removeKuvausWithId: function(kuvausTunniste) {
            var promise = $q.defer();
            if (kuvausTunniste !== undefined) {
                var kuvausGetUri = Config.env.tarjontaRestUrlPrefix + kuvausUriPrefix + kuvausTunniste;
                $http.delete(kuvausGetUri, {
                    withCredentials: true
                }).success(function(data) {
                    promise.resolve(data);
                }).error(function(data) {
                    promise.resolve(data);
                });
            }
            else {
                promise.resolve();
            }
            return promise.promise;
        },
        findWithVuosiOppilaitostyyppiTyyppiVuosi: function(oppilaitosTyyppi, tyyppi, vuosi) {
            var promise = $q.defer();
            var queryUri = Config.env.tarjontaRestUrlPrefix + kuvausUriPrefix + tyyppi + '/' +
                oppilaitosTyyppi + '/' + vuosi + '/' + 'kuvaustenTiedot';
            $http.get(queryUri).success(function(data) {
                promise.resolve(data);
            }).error(function(data) {
                promise.resolve(data);
            });
            return promise.promise;
        },
        findKuvausWithId: function(kuvausTunniste) {
            var promise = $q.defer();
            if (kuvausTunniste !== undefined) {
                var kuvausGetUri = Config.env.tarjontaRestUrlPrefix + kuvausUriPrefix + kuvausTunniste;
                $http.get(kuvausGetUri).success(function(data) {
                    promise.resolve(data);
                }).error(function(data) {
                    promise.resolve(data);
                });
            }
            else {
                promise.resolve();
            }
            return promise.promise;
        },
        insertKuvaus: function(tyyppi, kuvaus) {
            var promise = $q.defer();
            PermissionService.permissionResource().authorize({}, function(authResponse) {
                console.log('AUTH RESPONSE : ', authResponse);
                if (kuvaus !== undefined && tyyppi !== undefined) {
                    var kuvausPostUri = Config.env.tarjontaRestUrlPrefix + kuvausUriPrefix + tyyppi;
                    $http.post(kuvausPostUri, kuvaus, {
                        withCredentials: true,
                        headers: {
                            'Content-Type': 'application/json; charset=UTF-8'
                        }
                    }).success(function(data) {
                        promise.resolve(data);
                    }).error(function(data) {
                        promise.resolve(data);
                    });
                }
                else {
                    promise.resolve();
                }
            });
            return promise.promise;
        },
        updateKuvaus: function(tyyppi, kuvaus) {
            var promise = $q.defer();
            PermissionService.permissionResource().authorize({}, function(authResponse) {
                if (kuvaus !== undefined && tyyppi !== undefined) {
                    var kuvausPostUri = Config.env.tarjontaRestUrlPrefix + kuvausUriPrefix + tyyppi;
                    $http.put(kuvausPostUri, kuvaus, {
                        withCredentials: true,
                        headers: {
                            'Content-Type': 'application/json; charset=UTF-8'
                        }
                    }).success(function(data) {
                        promise.resolve(data);
                    }).error(function(data) {
                        promise.resolve(data);
                    });
                }
                else {
                    promise.resolve();
                }
            });
            return promise.promise;
        },
        findKuvausWithTyyppiNimiOppilaitos: function(tyyppi, nimi, oppilaitosTyyppi) {
            var promise = $q.defer();
            if (tyyppi !== undefined && nimi !== undefined && oppilaitosTyyppi !== undefined) {
                var kuvausQueriUri = Config.env.tarjontaRestUrlPrefix + kuvausUriPrefix + tyyppi +
                    oppilaitosTyyppi + nimi;
                $http.get(kuvausQueriUri).success(function(data) {
                    promise.resolve(data);
                }).error(function(data) {
                    promise.resolve(data);
                });
            }
            else {
                promise.resolve();
            }
            return promise.promise;
        },
        findKuvausBasicInformation: function(tyyppi, oppilaitosTyyppi) {
            var promise = $q.defer();
            if (tyyppi !== undefined && oppilaitosTyyppi !== undefined) {
                var queryUri = Config.env.tarjontaRestUrlPrefix + kuvausUriPrefix + tyyppi + '/' +
                    oppilaitosTyyppi + '/kuvaustenTiedot';
                $http.get(queryUri).success(function(data) {
                    promise.resolve(data);
                }).error(function(data) {
                    promise.resolve();
                });
            }
            else {
                promise.resolve();
            }
            return promise.promise;
        },
        findKuvauksesWithSearchSpec: function(searchSpec, tyyppi) {
            var promise = $q.defer();
            if (searchSpec !== undefined && tyyppi !== undefined) {
                var queryUri = Config.env.tarjontaRestUrlPrefix + kuvausUriPrefix + tyyppi + '/search';
                $log.info('KUVAUS SEARCH SPEC URI : ', queryUri);
                searchSpec.avain = searchSpec.valintaperustekuvausryhma || searchSpec.sorakuvaus;
                $http.post(queryUri, searchSpec, {
                    headers: {
                        'Content-Type': 'application/json; charset=UTF-8'
                    }
                }).success(function(data) {
                    promise.resolve(data);
                }).error(function(errorData) {
                    promise.resolve(errorData);
                });
            }
            else {
                promise.resolve(null);
            }
            return promise.promise;
        },
        findAllNimesWithType: function(tyyppi) {
            var promise = $q.defer();
            if (tyyppi !== undefined) {
                var nimiQueryUri = Config.env.tarjontaRestUrlPrefix + kuvausUriPrefix + tyyppi + '/nimet';
                $http.get(nimiQueryUri).success(function(data) {
                    promise.resolve(data);
                }).error(function(data) {
                    promise.resolve(data);
                });
            }
            return promise.promise;
        }
    };
});