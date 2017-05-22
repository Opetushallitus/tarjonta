/**
 * All methods return promise, when fulfilled the actual result will be stored inside promise under key "data"
 */
angular.module('TarjontaPermissions', [
    'ngResource',
    'config',
    'Tarjonta',
    'Logging',
    'Koodisto'
]).factory('PermissionService', function($resource, $log, $q, Config, AuthService, TarjontaService, HakuV1) {
    $log = $log.getInstance('PermissionService');
    var ophOid = Config.env['root.organisaatio.oid'];
    var resolveData = function(promise) {
        if (promise === undefined) {
            throw 'need a promise';
        }
        //fills promise.data with the actual value when it resolves.
        promise.then(function(data) {
            $log.debug('resolvedata', data);
            promise.data = data;
        }, function() {
                //error function
                promise.data = false;
            });
    };
    var _canCreate = function(orgOid) {
        var oidArray = angular.isArray(orgOid) ? orgOid : [orgOid];
        var deferred = $q.defer();
        var promises = [];
        for (var i = 0; i < oidArray.length; i++) {
            var result = AuthService.crudOrg(oidArray[i]);
            promises.push(result);
            resolveData(result);
        }
        $q.all(promises).then(function() {
            var result = true;
            for (var i = 0; i < promises.length; i++) {
                result = result && promises[i].data;
            }
            deferred.resolve(result);
        });
        return deferred.promise;
    };
    var _canEditKoulutusMulti = function(koulutusOid, searchParams) {
        $log.debug('can edit hakukohde multi');
        var deferred = $q.defer();
        promises = [];
        for (var i = 0; i < koulutusOid.length; i++) {
            var promise = _canEditKoulutus(koulutusOid[i], searchParams);
            promises.push(promise);
            resolveData(promise);
        }
        $q.all(promises).then(function() {
            var result = true;
            for (var i = 0; i < promises.length; i++) {
                result = result && promises[i].data;
            }
            deferred.resolve(result);
        });
        return deferred.promise;
    };
    var _canEditKoulutus = function(koulutusOid, searchParams, extraParams) {
        var defer = $q.defer();
        //hae koulutus
        searchParams = searchParams || {};
        extraParams = extraParams || {};
        angular.extend(searchParams, {
            koulutusOid: koulutusOid
        });
        var result = TarjontaService.haeKoulutukset(searchParams);
        //tarkista permissio tarjoajaoidilla
        result.then(function(hakutulos) {
            if (hakutulos.tuloksia === 0 || hakutulos.tulokset[0].tulokset[0].tila === 'POISTETTU') {
                //do not show buttons, if koulutus status is removed
                defer.resolve(false);
                return;
            }
            resolveData(defer.promise);
            if (hakutulos.tulokset !== undefined && hakutulos.tulokset.length == 1) {
                var koulutuksetByOrg = hakutulos.tulokset[0];

                AuthService.updateOrg(koulutuksetByOrg.oid).then(function(result) {
                    defer.resolve(result);
                }, function() {
                    defer.resolve(false);
                });
            }
            else {
                defer.resolve(false);
            }
        });
        return defer.promise;
    };

    var _canDeleteKoulutus = function(koulutusOid) {
        $log.debug('can delete');
        var defer = $q.defer();
        var deferJarjestetyt = $q.defer();
        var deferOrganisaatio = $q.defer();

        // Tarkista että koulutuksella ei ole järjestettyjä alikoulutuksia
        TarjontaService.getJarjestettavatKoulutuksetPromise(koulutusOid).then(function (response) {
            deferJarjestetyt.resolve(noneOfJarjestettyKoulutusIsJulkaistu(response.result));
        });

        //tarkista permissio tarjoajaoidilla
        TarjontaService.haeKoulutukset({
            koulutusOid: koulutusOid
        }).then(function(hakutulos) {
            if (hakutulos.tulokset !== undefined && hakutulos.tulokset.length === 1) {
                AuthService.crudOrg(hakutulos.tulokset[0].oid).then(function(result) {
                    deferOrganisaatio.resolve(result);
                }, function() {
                    deferOrganisaatio.resolve(false);
                });
            }
            else {
                deferOrganisaatio.resolve(false);
            }
        });

        // Poistaminen onnistuu vain jos molemmat kutsut palauttavat true
        $q.all([deferJarjestetyt.promise, deferOrganisaatio.promise])
            .then(function (results) {
                defer.resolve(results[0] && results[1]);
            });

        return defer.promise;
    };

    var julkaistutTilat = ['JULKAISTU', 'VALMIS', 'LUONNOS', 'PERUTTU'];
    function noneOfJarjestettyKoulutusIsJulkaistu(jarjestettavatKoulutukset) {
        if (jarjestettavatKoulutukset) {
            for (var i = 0; i < jarjestettavatKoulutukset.length; i++) {
                var jarjestettyKoulutus = jarjestettavatKoulutukset[i];
                if (jarjestettyKoulutus && julkaistutTilat.indexOf(jarjestettyKoulutus.tila) > -1) {
                    return false;
                }
            }
        }
        return true;
    }




    var _canDeleteKoulutusMulti = function(koulutusOids) {
        var deferred = $q.defer();
        promises = [];
        for (var i = 0; i < koulutusOids.length; i++) {
            var promise = _canDeleteKoulutus(koulutusOids[i]);
            promises.push(promise);
            resolveData(promise);
        }
        $q.all(promises).then(function() {
            var result = true;
            for (var i = 0; i < promises.length; i++) {
                result = result && promises[i].data;
            }
            deferred.resolve(result);
        });
        return deferred.promise;
    };
    var _canEditHakukohde = function(hakukohdeOid) {
        var defer = $q.defer();
        //hae koulutus
        var result = TarjontaService.haeHakukohteet({
            hakukohdeOid: hakukohdeOid
        });
        //tarkista permissio tarjoajaoidilla
        result = result.then(function(hakutulos) {
            //			$log.debug("hakutulos:", hakutulos);
            if (hakutulos.tulokset !== undefined && hakutulos.tulokset.length == 1) {
                AuthService.updateOrg(hakutulos.tulokset[0].oid).then(function(result) {
                    defer.resolve(result);
                }, function() {
                        defer.resolve(false);
                    });
            }
            else {
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
        $q.all(promises).then(function() {
            var result = true;
            for (var i = 0; i < promises.length; i++) {
                result = result && promises[i].data;
            }
            deferred.resolve(result);
        });
        return deferred.promise;
    };
    var _canDeleteHakukohde = function(hakukohdeOid) {
        var defer = $q.defer();
        //hae koulutus
        var result = TarjontaService.haeHakukohteet({
            hakukohdeOid: hakukohdeOid
        });
        //tarkista permissio tarjoajaoidilla
        result = result.then(function(hakutulos) {
            //			$log.debug("hakutulos:", hakutulos);
            if (hakutulos.tulokset !== undefined && hakutulos.tulokset.length == 1) {
                AuthService.crudOrg(hakutulos.tulokset[0].oid).then(function(result) {
                    defer.resolve(result);
                }, function() {
                        defer.resolve(false);
                    });
            }
            else {
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
        $q.all(promises).then(function() {
            var result = true;
            for (var i = 0; i < promises.length; i++) {
                result = result && promises[i].data;
            }
            deferred.resolve(result);
        });
        return deferred.promise;
    };
    function canUpdateHaku() {
        return function(org) {
            return AuthService.updateOrg(org, 'APP_HAKUJENHALLINTA');
        };
    }
    function canCRUDHaku() {
        return function(org) {
            return AuthService.crudOrg(org, 'APP_HAKUJENHALLINTA');
        };
    }
    function hasHakuPermission(hakuOrHakuOid, permissionf) {
        var defer = $q.defer();
        if (typeof hakuOrHakuOid === 'string') {
            HakuV1.get({
                oid: hakuOrHakuOid
            }).$promise.then(function(response) {
                checkPermission(response.result);
            });
        }
        else {
            checkPermission(hakuOrHakuOid);
        }
        function checkPermission(haku) {
            var orgs = haku.tarjoajaOids || [];
            if (orgs.length === 0) {
                //organisaatiota ei kerrottu, pitää olla oph?
                permissionf(ophOid).then(function(result) {
                    defer.resolve(result);
                });
            }
            else {
                // onko oikeus haun johonkin organisaatioon
                var promises = [];
                var hasAccess = {
                    access: false
                };
                _.each(orgs, function(org) {
                    promises.push(permissionf(org).then(function(result) {
                        hasAccess.access = hasAccess.access || result;
                    }));
                });
                $q.all(promises).then(function() {
                    defer.resolve(hasAccess.access);
                });
            }
        }
        return defer.promise;
    }
    var canCRUDValintaperustekuvausToinenAste = function() {
        var organisations = AuthService.getOrganisations([
            'APP_VALINTAPERUSTEKUVAUSTENHALLINTA_CRUD'
        ]);
        return organisations.length > 0;
    };
    var canCRUDValintaperustekuvausKK = function() {
        var organisations = AuthService.getOrganisations([
            'APP_VALINTAPERUSTEKUVAUSTENHALLINTA_KK_CRUD'
        ]);
        return organisations.length > 0;
    };
    var canUpdateValintaperustekuvausToinenAste = function() {
        var organisations = AuthService.getOrganisations([
            'APP_VALINTAPERUSTEKUVAUSTENHALLINTA_CRUD',
            'APP_VALINTAPERUSTEKUVAUSTENHALLINTA_RU'
        ]);
        return organisations.length > 0;
    };
    var canUpdateValintaperustekuvausKK = function() {
        var organisations = AuthService.getOrganisations([
            'APP_VALINTAPERUSTEKUVAUSTENHALLINTA_KK_CRUD',
            'APP_VALINTAPERUSTEKUVAUSTENHALLINTA_KK_RU'
        ]);
        return organisations.length > 0;
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
            * @param {type} orgOid organisaation oidi tai lista oideja
            */
            canCreate: function(orgOid) {
                return _canCreate(orgOid);
            },
            canMoveOrCopy: function(koulutusOid) {
                $log.debug('canMoveOrCopy koulutus');
                var deferred = $q.defer();
                var promise = _canEditKoulutus(
                    koulutusOid,
                    {
                        defaultTarjoaja: AuthService.getUserDefaultOid()
                    },
                    {
                        moveOrCopy: true
                    }
                );
                promise.then(function(result) {
                    deferred.resolve(result);
                });
                return deferred.promise;
            },
            canPreview: function(orgOid) {
                if (orgOid === undefined) {
                    $log.debug('koulutus.canPreview', orgOid);
                    return false;
                }
                // TODO
                $log.debug('TODO koulutus.canPreview', orgOid);
                return true;
            },
            /**
            * Saako käyttäjä muokata koulutusta
            * @param {type} koulutusOid koulutuksen oid
            * @param {type} searchParams
            */
            canEdit: function(koulutusOid, searchParams) {
                var koulutusoidit = angular.isArray(koulutusOid) ? koulutusOid : [koulutusOid];
                if (koulutusoidit.length === 0) {
                    return {
                        data: false
                    };
                }
                return _canEditKoulutusMulti(koulutusoidit, searchParams);
            },
            canTransition: function(koulutusOid, from, to) {
                var koulutusoidit = angular.isArray(koulutusOid) ? koulutusOid : [koulutusOid];
                if (koulutusoidit.length === 0) {
                    return {
                        data: false
                    };
                }
                return _canEditKoulutusMulti(koulutusoidit);
            },
            /**
            * Saako käyttäjä poistaa koulutuksen
            */
            canDelete: function(koulutusOid) {
                var koulutusoidit = angular.isArray(koulutusOid) ? koulutusOid : [koulutusOid];
                return _canDeleteKoulutusMulti(koulutusoidit);
            }
        },
        hakukohde: {
            /**
            * Saako käyttäjä luoda hakukohteen
            */
            canCreate: function(orgOid) {
                return _canCreate(orgOid);
            },
            canPreview: function(orgOid) {
                // TODO
                $log.debug('TODO hakukohde.canPreview', orgOid);
                return $q.when(true);
            },
            /**
            * Saako käyttäjä muokata hakukohdetta
            */
            canEdit: function(hakukohdeOid) {
                $log.debug('can edit hakukohde', hakukohdeOid);
                var hakukohdeoidit = angular.isArray(hakukohdeOid) ? hakukohdeOid : [hakukohdeOid];
                return _canEditHakukohdeMulti(hakukohdeoidit);
            },
            canTransition: function(hakukohdeOid, from, to) {
                $log.debug('can transition', hakukohdeOid, from, to);
                var hakukohdeoidit = angular.isArray(hakukohdeOid) ? hakukohdeOid : [hakukohdeOid];
                return _canEditHakukohdeMulti(hakukohdeoidit);
            },
            /**
            * Saako käyttäjä poistaa hakukohteen
            */
            canDelete: function(hakukohdeOid) {
                var hakukohdeoidit = angular.isArray(hakukohdeOid) ? hakukohdeOid : [hakukohdeOid];
                return _canDeleteHakukohdeMulti(hakukohdeoidit);
            }
        },
        haku: {
            canCreate: function() {
                return $q.when(true);
            },
            canEdit: function(hakuOrHakuOid) {
                return hasHakuPermission(hakuOrHakuOid, canUpdateHaku());
            },
            canDelete: function(hakuOrHakuOid) {
                return hasHakuPermission(hakuOrHakuOid, canCRUDHaku());
            }
        },
        kuvaus: {
            canCreateToinenAste: function() {
                return canCRUDValintaperustekuvausToinenAste();
            },
            canCreateKK: function() {
                return canCRUDValintaperustekuvausKK();
            },
            canUpdateToinenAste: function() {
                return canUpdateValintaperustekuvausToinenAste();
            },
            canUpdateKK: function() {
                return canUpdateValintaperustekuvausKK();
            },
            canDeleteToinenAste: function() {
                return canCRUDValintaperustekuvausToinenAste();
            },
            canDeleteKK: function() {
                return canCRUDValintaperustekuvausKK();
            },
            canCopyToinenAste: function() {
                return canCRUDValintaperustekuvausToinenAste();
            },
            canCopyKK: function() {
                return canCRUDValintaperustekuvausKK();
            }
        },
        permissionResource: function() {
            return $resource(window.url("tarjonta-service.permission.authorize"), {}, {
                authorize: {
                    method: 'GET',
                    withCredentials: true,
                    headers: {
                        'Content-Type': 'application/json; charset=UTF-8'
                    }
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
            var permissionsUrl = window.urls().noEncode().url("tarjonta-service.permission.get", ":type", ":target");
            var permissions = $resource(permissionsUrl, {}, {
                cache: false,
                get: {
                    method: 'GET',
                    withCredentials: true,
                    isArray: false
                }
            });
            var ret = $q.defer();
            permissions.get({
                'target': target,
                'type': type
            }, function(result) {
                    $log.info('GOT PERMISSIONS: ', permissionsUrl, type, target, result);
                    ret.resolve(result);
                }, function(err) {
                    $log.warn('FAILED TO GET PERMISSIONS: ', permissionsUrl, type, target, err);
                    ret.resolve({
                        error: err
                    });
                });
            return ret.promise;
        }
    };
});