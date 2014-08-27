/**
 * All methods return promise, when fulfilled the actual result will be stored inside promise under key "data"
 */
angular.module('TarjontaPermissions', ['ngResource', 'config', 'Tarjonta', 'Logging', 'Koodisto'])
        .factory('PermissionService', function($resource, $log, $q, Config, AuthService, TarjontaService, HakuV1, KoodistoURI) {

            $log = $log.getInstance("PermissionService");

            var ophOid = Config.env['root.organisaatio.oid'];
            var resolveData = function(promise) {
                if (promise === undefined) {
                    throw "need a promise";
                }
                //fills promise.data with the actual value when it resolves.
                promise.then(function(data) {
                    $log.debug("resolvedata", data);
                    promise.data = data;
                }, function() { //error function
                    promise.data = false;
                });
            };

            var _canCreate = function(orgOid) {
//		$log.debug("can create:", orgOid);
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
//				$log.debug("processing promise", i, "result:", promises[i].data);

                        result = result && promises[i].data;
                    }
                    deferred.resolve(result);
                });

                return deferred.promise;
            };


            var _canEditKoulutusMulti = function(koulutusOid) {
                $log.debug("can edit hakukohde multi");
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
//				$log.debug("processing list:", promises[i].data);
                        result = result && promises[i].data;
                    }
//			$log.debug("final result:", result);

                    deferred.resolve(result);
                });

                return deferred.promise;
            };

            var _canEditKoulutus = function(koulutusOid) {

                var defer = $q.defer();

                //hae koulutus
                var result = TarjontaService.haeKoulutukset({koulutusOid: koulutusOid});

                //tarkista permissio tarjoajaoidilla
                result.then(function(hakutulos) {

                    if (hakutulos.tuloksia === 0 || hakutulos.tulokset[0].tulokset[0].tila === 'POISTETTU') {
                        //do not show buttons, if koulutus status is removed
                        defer.resolve(false);
                        return;
                    }

                    //$log.debug("hakutulos:", hakutulos);
                    resolveData(defer.promise);

                    if (hakutulos.tulokset != undefined && hakutulos.tulokset.length == 1) {
                        AuthService.updateOrg(hakutulos.tulokset[0].oid).then(function(result) {
//					$log.debug("resolving ", result);
                            defer.resolve(result);
                        }, function() {
//					$log.debug("resolving false");
                            defer.resolve(false);
                        });
                    } else {
//				$log.debug("resolving false");
                        defer.resolve(false);
                    }
                });
                return defer.promise;
            };


            var _canDeleteKoulutus = function(koulutusOid) {

                $log.debug("can delete");

                var defer = $q.defer();

                //hae koulutus
                var result = TarjontaService.haeKoulutukset({koulutusOid: koulutusOid});

                //tarkista permissio tarjoajaoidilla
                result = result.then(function(hakutulos) {
//			$log.debug("hakutulos:", hakutulos);
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
//				$log.debug("processing list:", promises[i].data);
                        result = result && promises[i].data;
                    }
//			$log.debug("final result:", result);

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
//			$log.debug("hakutulos:", hakutulos);
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
//				$log.debug("processing list:", promises[i].data);
                        result = result && promises[i].data;
                    }
//			$log.debug("final result:", result);

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
//			$log.debug("hakutulos:", hakutulos);
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
//				$log.debug("processing list:", promises[i].data);
                        result = result && promises[i].data;
                    }
//			$log.debug("final result:", result);

                    deferred.resolve(result);
                });

                return deferred.promise;
            };


            function canUpdateHaku() {
                return function(org) {
//        console.log("canUpdateHaku:", org);
                    return AuthService.updateOrg(org, "APP_HAKUJENHALLINTA");
                };
            }

            function canCRUDHaku() {
                return function(org) {
                    return AuthService.crudOrg(org, "APP_HAKUJENHALLINTA");
                };
            }

            function hasHakuPermission(hakuOid, permissionf) {
//      console.log("has haku permission, f:", permissionf);
                var defer = $q.defer();

                //hae haku
                HakuV1.get({oid: hakuOid}).$promise.then(function(haku) {
//        console.log("haku:", haku.result);
                    var haku = haku.result;
                    var orgs = haku.tarjoajaOids ? haku.tarjoajaOids : [];

                    if (orgs.length == 0) {
//          console.log("speciaalikeissi, ei organisaatioita, assuming oph", ophOid);
                        //organisaatiota ei kerrottu, pitää olla oph?
                        permissionf(ophOid).then(function(result) {
//            console.log("resolving speciaali:", result);
                            defer.resolve(result);
                        });
                    } else {

                        // onko oikeus haun johonkin organisaatioon
                        var promises = [];
                        var hasAccess = {access: false};

                        function orAccess(result) {
//            console.log("or access result:", result);
                            hasAccess.access = hasAccess.access || result;
                        }

                        for (var i = 0; i < orgs.length; i++) {
                            promises.push(permissionf(orgs[i]).then(orAccess));
                        }
                        $q.all(promises).then(function() {
                            defer.resolve(hasAccess.access);
                        });
                    }

                });

                return defer.promise;
            }

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
                    canMoveOrCopy: function(koulutusOid) {
                        $log.debug("canMoveOrCopy koulutus");
                        var deferred = $q.defer();
                        var promise = _canEditKoulutus(koulutusOid);
                        promise.then(function(result) {
                            deferred.resolve(result);
                        });

                        return deferred.promise;
                    },
                    canPreview: function(orgOid) {
                        if (orgOid === undefined) {
                            $log.debug("koulutus.canPreview", orgOid);
                            return false;
                        }
                        // TODO
                        $log.debug("TODO koulutus.canPreview", orgOid);
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
                    canTransition: function(koulutusOid, from, to) {
                        var koulutusoidit = angular.isArray(koulutusOid) ? koulutusOid : [koulutusOid];
                        if (koulutusoidit.length == 0) {
                            return {data: false};
                        }

                        return _canEditKoulutusMulti(koulutusoidit);
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
                        $log.debug("TODO hakukohde.canPreview", orgOid);
                        return $q.when(true);
                    },
                    /**
                     * Saako käyttäjä muokata hakukohdetta
                     * @param hakukohdeOid
                     * @returns
                     */
                    canEdit: function(hakukohdeOid) {
                        $log.debug("can edit hakukohde", hakukohdeOid);
                        var hakukohdeoidit = angular.isArray(hakukohdeOid) ? hakukohdeOid : [hakukohdeOid];
                        return _canEditHakukohdeMulti(hakukohdeoidit);
                    },
                    canTransition: function(hakukohdeOid, from, to) {
                        $log.debug("can transition", hakukohdeOid, from, to);
                        var hakukohdeoidit = angular.isArray(hakukohdeOid) ? hakukohdeOid : [hakukohdeOid];
                        return _canEditHakukohdeMulti(hakukohdeoidit);
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
                haku: {
                    canCreate: function() {
                        //tarkista rooli
//            console.log("can create haku");
                        return $q.when(true);
                    },
                    /**
                     * Onko käyttäjällä oikeus muokata hakua.
                     */
                    canEdit: function(hakuOid) {
//            console.log("canEdit", hakuOid);
                        return hasHakuPermission(hakuOid, canUpdateHaku());
                    },
                    canDelete: function(hakuOid) {
                        return hasHakuPermission(hakuOid, canCRUDHaku());
                    }

                },
                permissionResource: function() {
                    return $resource(Config.env.tarjontaRestUrlPrefix + "permission/authorize", {}, {
                        authorize: {
                            method: 'GET',
                            withCredentials: true,
                            headers: {'Content-Type': 'application/json; charset=UTF-8'}
                        }
                    });
                },
                /**
                 * Palauttaa json olion, johon kerätty erilaisia oikeuksia.
                 * <pre>
                 * TODO esimerkki tuloksesta tähän!
                 * </pre>
                 *
                 * @param {type} type esim. "haku", "hakukohde"
                 * @param {type} target oid
                 * @returns {$q@call;defer.promise}
                 */
                getPermissions: function(type, target) {
                    var permissionsUrl = Config.env.tarjontaRestUrlPrefix + "permission/permissions/:type/:target";
                    var permissions = $resource(permissionsUrl, {}, {
                        cache: false,
                        get: {
                            method: "GET",
                            withCredentials : true,
                            isArray: false
                        }
                    });

                    var ret = $q.defer();

                    permissions.get({"target": target, "type": type},
                        function(result) {
                            $log.info("GOT PERMISSIONS: ", permissionsUrl, type, target, result);
                            ret.resolve(result);
                        },
                        function(err) {
                            $log.warn("FAILED TO GET PERMISSIONS: ", permissionsUrl, type, target, err);
                            ret.resolve({error: err});
                        });

                    return ret.promise;
                }
            };
        });