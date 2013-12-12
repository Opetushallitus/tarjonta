/**
 * All methods return promise, when fulfilled the actual result will be stored inside promise under key "data"
 */
angular.module('TarjontaPermissions', ['ngResource', 'config', 'Tarjonta']).factory('PermissionService', function($resource, $log, $q, Config, AuthService, TarjontaService) {

    var resolveData = function(promise) {
        if (promise === undefined) {
            throw "need a promise";
        }
        //fills promise.data with the actual value when it resolves.
        promise.then(function(data) {
//			console.log("resolvedata", data);
            promise.data = data;
        }, function() { //error function
            promise.data = false;
        });
    };

    var _canCreate = function(orgOid) {
//		console.log("can create:", orgOid);
        var oidArray = angular.isArray(orgOid) ? orgOid : [orgOid];

        var deferred = $q.defer();
        var promises = [];
        for (var i = 0; i < oidArray.length; i++) {
            var result = AuthService.crudOrg(oidArray[i]);
            resolveData(result);
            promises.push(result);
        }

        $q.all(promises).then(function() {
            var result = true;
            for (var i = 0; i < promises.length; i++) {
//				console.log("processing promise", i, "result:", promises[i].data);

                result = result && promises[i].data;
            }
            deferred.resolve(result);
        });

        return deferred.promise;
    };


    var _canEditKoulutusMulti = function(koulutusOid) {
        var deferred = $q.defer();

        promises = [];
        for (var i = 0; i < koulutusOid.length; i++) {
            var promise = _canEditKoulutus(koulutusOid[i]);
            promises.push(promise);
            resolveData(promise);
        }

        var result = true;

        $q.all(promises).then(function() {
            for (var i = 0; i < promises.length; i++) {
//				console.log("processing list:", promises[i].data);
                result = result && promises[i].data;
            }
//			console.log("final result:", result);

            deferred.resolve(result);
        });

        return deferred.promise;
    };


    var _canEditKoulutus = function(koulutusOid) {

        var defer = $q.defer();

        //hae koulutus
        var result = TarjontaService.haeKoulutukset({koulutusOid: koulutusOid});

        //tarkista permissio tarjoajaoidilla
        result = result.then(function(hakutulos) {
//			console.log("hakutulos:", hakutulos);
            resolveData(defer.promise);

            if (hakutulos.tulokset != undefined && hakutulos.tulokset.length == 1) {
                AuthService.updateOrg(hakutulos.tulokset[0].oid).then(function(result) {
//					console.log("resolving ", result);
                    defer.resolve(result);
                }, function() {
//					console.log("resolving false");
                    defer.resolve(false);
                });
            } else {
//				console.log("resolving false");
                defer.resolve(false);
            }
        });
        return defer.promise;
    };


    var _canDeleteKoulutus = function(koulutusOid) {

        console.log("can delete");

        var defer = $q.defer();

        //hae koulutus
        var result = TarjontaService.haeKoulutukset({koulutusOid: koulutusOid});

        //tarkista permissio tarjoajaoidilla
        result = result.then(function(hakutulos) {
//			console.log("hakutulos:", hakutulos);
            if (hakutulos.tulokset != undefined && hakutulos.tulokset.length == 1) {
                AuthService.crudOrg(hakutulos.tulokset[0].oid).then(function(result) {
                    defer.resolve(result);
                }, function() {
                    defer.resolve(false);
                });
            } else {
                defer.resolve(false);
            }
        });
        return defer.promise;
    };


    var _canDeleteKoulutusMulti = function(koulutusOids) {
        var deferred = $q.defer();

        promises = [];
        for (var i = 0; i < koulutusOids.length; i++) {
            var promise = _canDeleteKoulutus(koulutusOids[i]);
            promises.push(promise);
            resolveData(promise);
        }

        var result = true;

        $q.all(promises).then(function() {
            for (var i = 0; i < promises.length; i++) {
//				console.log("processing list:", promises[i].data);
                result = result && promises[i].data;
            }
//			console.log("final result:", result);

            deferred.resolve(result);
        });

        return deferred.promise;
    };


    var _canEditHakukohde = function(hakukohdeOid) {
        var defer = $q.defer();

        //hae koulutus
        var result = TarjontaService.haeHakukohteet({hakukohdeOid: hakukohdeOid});

        //tarkista permissio tarjoajaoidilla
        result = result.then(function(hakutulos) {
//			console.log("hakutulos:", hakutulos);
            if (hakutulos.tulokset != undefined && hakutulos.tulokset.length == 1) {
                AuthService.updateOrg(hakutulos.tulokset[0].oid).then(function(result) {
                    defer.resolve(result);
                }, function() {
                    defer.resolve(false);
                });
            } else {
                defer.resolve(false);
            }
        });
        return defer.promise;
    };

    var _canEditHakukohdeMulti = function(hakukohdeOids) {
        var deferred = $q.defer();

        promises = [];
        for (var i = 0; i < hakukohdeOids.length; i++) {
            var promise = _canEditHakukohde(hakukohdeOids[i]);
            promises.push(promise);
            resolveData(promise);
        }

        var result = true;

        $q.all(promises).then(function() {
            for (var i = 0; i < promises.length; i++) {
//				console.log("processing list:", promises[i].data);
                result = result && promises[i].data;
            }
//			console.log("final result:", result);

            deferred.resolve(result);
        });

        return deferred.promise;
    };

    var _canDeleteHakukohde = function(hakukohdeOid) {
        var defer = $q.defer();

        //hae koulutus
        var result = TarjontaService.haeHakukohteet({hakukohdeOid: hakukohdeOid});

        //tarkista permissio tarjoajaoidilla
        result = result.then(function(hakutulos) {
//			console.log("hakutulos:", hakutulos);
            if (hakutulos.tulokset != undefined && hakutulos.tulokset.length == 1) {
                AuthService.crudOrg(hakutulos.tulokset[0].oid).then(function(result) {
                    defer.resolve(result);
                }, function() {
                    defer.resolve(false);
                });
            } else {
                defer.resolve(false);
            }
        });
        return defer.promise;
    };

    var _canDeleteHakukohdeMulti = function(hakukohdeOids) {
        var deferred = $q.defer();

        promises = [];
        for (var i = 0; i < hakukohdeOids.length; i++) {
            var promise = _canDeleteHakukohde(hakukohdeOids[i]);
            promises.push(promise);
            resolveData(promise);
        }

        var result = true;

        $q.all(promises).then(function() {
            for (var i = 0; i < promises.length; i++) {
//				console.log("processing list:", promises[i].data);
                result = result && promises[i].data;
            }
//			console.log("final result:", result);

            deferred.resolve(result);
        });

        return deferred.promise;
    };


    return {
        /**
         * funktiot jotka ottavat organisaatio oidin ovat yhteisiä molemmille (hk + k)!:
         */

        canDelete: function(orgOid) {
            _canDelete(orgOid);
        },
        canCreate: function(orgOid) {
            _canCreate(orgOid);
        },
        canEdit: function(orgOid) {
            var result = AuthService.updateOrg(orgOid);
            resolveData(result);
            return result;
        },
        koulutus: {
            /**
             * Saako käyttäjä luoda koulutuksen
             * @param orgOid organisaation oidi tai lista oideja
             * @returns
             */
            canCreate: function(orgOid) {
                return _canCreate(orgOid);
            },
            canMoveOrCopy: function(orgOid) {
                console.log("TODO koulutus.canMoveOrCopy", orgOid);
                return true;
            },
            canPreview: function(orgOid) {
                if (orgOid === undefined) {
                    console.log("koulutus.canMoveOrCopy", orgOid);
                    return false;
                }
                // TODO
                console.log("TODO koulutus.canPreview", orgOid);
                return true;
            },
            /**
             * Saako käyttäjä muokata koulutusta
             * @param koulutusOid koulutuksen oid
             * @returns
             */
            canEdit: function(koulutusOid) {

                var koulutusoidit = angular.isArray(koulutusOid) ? koulutusOid : [koulutusOid];
                if (koulutusoidit.length == 0) {
                    return {data: false};
                }

                return _canEditKoulutusMulti(koulutusoidit);
            },
            canTransition: function(oid, from, to) {
                // TODO
                console.log("TODO koulutus.canTransition " + from + "->" + to, oid);
                return true;
            },
            /**
             * Saako käyttäjä poistaa koulutuksen
             * @param koulutusOid
             * @returns
             */
            canDelete: function(koulutusOid) {
                var koulutusoidit = angular.isArray(koulutusOid) ? koulutusOid : [koulutusOid];
                return _canDeleteKoulutusMulti(koulutusoidit);
            }
        },
        hakukohde: {
            /**
             * Saako käyttäjä luoda hakukohteen
             * @param orgOid organisaatio oid tai array oideja
             * @returns
             */
            canCreate: function(orgOid) {
                return _canCreate(orgOid);
            },
            canPreview: function(orgOid) {
                // TODO
                console.log("TODO hakukohde.canPreview", orgOid);
                return true;
            },
            /**
             * Saako käyttäjä muokata hakukohdetta
             * @param hakukohdeOid
             * @returns
             */
            canEdit: function(hakukohdeOid) {
                var hakukohdeoidit = angular.isArray(hakukohdeOid) ? hakukohdeOid : [hakukohdeOid];
                return _canEditHakukohdeMulti(hakukohdeoidit);
            },
            canTransition: function(oid, from, to) {
                // TODO
                console.log("TODO hakukohde.canTransition " + from + "->" + to, oid);
                return true;
            },
            /**
             * Saako käyttäjä poistaa hakukohteen
             * @param hakukohdeOid
             * @returns
             */
            canDelete: function(hakukohdeOid) {
                var hakukohdeoidit = angular.isArray(hakukohdeOid) ? hakukohdeOid : [hakukohdeOid];
                return _canDeleteHakukohdeMulti(hakukohdeoidit);
            },
        },
        permissionResource: function() {
                return $resource(Config.env.tarjontaRestUrlPrefix + "permission/authorize", {}, {
                    authorize: {
                        method: 'GET',
                        withCredentials: true,
                        headers: {'Content-Type': 'application/json; charset=UTF-8'}
                    }
                });
            }
    };
});