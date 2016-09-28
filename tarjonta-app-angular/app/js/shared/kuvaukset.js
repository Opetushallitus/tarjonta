var app = angular.module('Kuvaus', [
    'ngResource',
    'Logging',
    'TarjontaPermissions'
]);
app.factory('Kuvaus', function($http, $q, $log, PermissionService) {
    $log = $log.getInstance('Kuvaus');
    var kuvausUriPrefix = 'kuvaus/';
    return {
        removeKuvausWithId: function(kuvausTunniste) {
            var promise = $q.defer();
            if (kuvausTunniste !== undefined) {
                $http.delete(window.url("tarjonta-service.kuvaus.byTunniste", kuvausTunniste), {
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
            $http.get(window.url("tarjonta-service.kuvaus.findWithVuosiOppilaitostyyppiTyyppiVuosi", tyyppi, oppilaitosTyyppi, vuosi)).success(function(data) {
                promise.resolve(data);
            }).error(function(data) {
                promise.resolve(data);
            });
            return promise.promise;
        },
        findKuvausWithId: function(kuvausTunniste) {
            var promise = $q.defer();
            if (kuvausTunniste !== undefined) {
                $http.get(window.url("tarjonta-service.kuvaus.byTunniste", kuvausTunniste)).success(function(data) {
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
                    $http.post(window.url("tarjonta-service.kuvaus.byTunniste", tyyppi), kuvaus, {
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
                    $http.put(window.url("tarjonta-service.kuvaus.byTunniste", tyyppi), kuvaus, {
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
                $http.get(window.url("tarjonta-service.kuvaus.findKuvausWithTyyppiNimiOppilaitos", tyyppi , oppilaitosTyyppi, nimi)
                ).success(function(data) {
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
                $http.get(window.url("tarjonta-service.kuvaus.findKuvausBasicInformation", tyyppi, oppilaitosTyyppi)
                ).success(function(data) {
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
                var queryUri = window.url("tarjonta-service.kuvaus.search", tyyppi);
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
                $http.get(window.url("tarjonta-service.kuvaus.nimet", tyyppi)).success(function(data) {
                    promise.resolve(data);
                }).error(function(data) {
                    promise.resolve(data);
                });
            }
            return promise.promise;
        }
    };
});