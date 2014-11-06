'use strict';
/*******************************************************/
/* Define all project sub-modules with no dependencies */
/*******************************************************/
angular.module('app.kk',
        [
            'app.kk.edit.hakukohde.ctrl',
            'app.kk.edit.hakukohde.review.ctrl',
            'app.kk.edit.valintaperustekuvaus.ctrl',
            'app.kk.search.valintaperustekuvaus.ctrl',
            'app.edit.ctrl',
            'app.edit.ctrl.kk',
            'app.edit.ctrl.lukio',
            'app.edit.ctrl.amm',
            'app.edit.ctrl.alkamispaiva',
            'app.edit.ctrl.tutkintonimike',
            'app.edit.ctrl.laajuus',
            'app.komo.ctrl',
            'app.review.ctrl',
            'app.hakukohde.ctrl',
            'app.hakukohde.dialog.ctrl',
            'app.haku.ctrl',
            'ui.bootstrap',
            'ngRoute',
            'config',
            'auth',
            'KoulutusConverter',
            'imageupload'
        ]);

/*******************************************************
 * Main module dependecies                             *
 *******************************************************/
angular.module('app',
        [
            'app.directives',
            'app.filters',
            'app.services',
            'app.search.controllers',
            'app.test.controllers',
            'app.kk',
            'app.koulutus.ctrl',
            'app.koulutus.sisaltyvyys.ctrl',
            'app.koulutus.remove.ctrl',
            'app.koulutus.kuvausRemove.ctrl',
            'app.koulutus.copy.ctrl',
            'app.dialog',
            'ngRoute',
            'ngResource',
            'ngSanitize',
            'ngAnimate',
            'ui.bootstrap',
            'pasvaz.bindonce',
            'loading',
            'localisation',
            'Koodisto',
            'Organisaatio',
            'TarjontaPermissions',
            'TarjontaCache',
            'Tarjonta',
            'KoodistoCombo',
            'KoodistoArvoCombo',
            'SharedStateService',
            'CommonUtilServiceModule',
            'DateTimePicker',
            'Hakukohde',
            'Kuvaus',
            'KoodistoMultiSelect',
            'KoodistoTypeAhead',
            'orgAngularTreeview',
            'ResultsTreeTable',
            'imageupload',
            'MultiSelect',
            'OrderByNumFilter',
            'StartsWithFilter',
            'CommonDirectives',
            'MonikielinenTextField',
            'ImageDirective',
            'RichTextArea',
            'MonikielinenTextArea',
            'MonikielinenText',
            'MonikielinenTabs',
            'MultiLangSimpleTextArea',
            'ControlsLayout',
            'ShowErrors',
            'angularTreeview',
            'DateFormat',
            'TreeFieldDirective',
            'AiheetJaTeematChooser',
            'TarjontaDateTime',
            'TarjontaOsoiteField',
            'ExportToParent',
            'debounce',
            'Parameter',
            'Logging'
        ]);

angular.module('app').value("globalConfig", window.CONFIG);


angular.module('app').factory(
        "errorLogService",
        function($log, $window, Config) {

            var serviceUrl = Config.env["tarjontaRestUrlPrefix"] + "permission/recordUiStacktrace";
            var errorsLoggingTimeout = Config.env["errorlog.timeout"] || 60000;
            var errorsLoggingSuspended = false;
            var loggedErrors = [];

            $log.info("*** errorLogService ***", serviceUrl);

            function get_browser() {
                var N = navigator.appName, ua = navigator.userAgent, tem;
                var M = ua.match(/(opera|chrome|safari|firefox|msie)\/?\s*(\.?\d+(\.\d+)*)/i);
                if (M && (tem = ua.match(/version\/([\.\d]+)/i)) != null)
                    M[2] = tem[1];
                M = M ? [M[1], M[2]] : [N, navigator.appVersion, '-?'];
                return M[0];
            }

            function get_browser_version() {
                var N = navigator.appName, ua = navigator.userAgent, tem;
                var M = ua.match(/(opera|chrome|safari|firefox|msie)\/?\s*(\.?\d+(\.\d+)*)/i);
                if (M && (tem = ua.match(/version\/([\.\d]+)/i)) != null)
                    M[2] = tem[1];
                M = M ? [M[1], M[2]] : [N, navigator.appVersion, '-?'];
                return M[1];
            }


            // Log error to console (as normal) AND to the remote server
            function log(exception, cause) {
                // Default behaviour, log to console
                $log.error.apply($log, arguments);

                if (Config.env["errorlog.disabled"] || errorsLoggingSuspended) {
                    return;

                }
                // Try to send stacktrace event to server
                try {
                    $log.debug("logging error to server side...");

                    var errorMessage = exception.toString();
                    var stackTrace = exception.stack.toString();

                    var errorId = errorMessage + "---" + stackTrace;
                    if (loggedErrors.indexOf(errorId) != -1) {
                        // älä lähetä, jos jo lokitettu tai lokitus keskeytetty
                        return;
                    }

                    var browserInfo = {
                        browser: get_browser(),
                        browserVersion: get_browser_version()
                    };

                    // Log the JavaScript error to the server.
                    $.ajax({
                        type: "POST",
                        url: serviceUrl,
                        contentType: "application/json",
                        xhrFields: {
                            withCredentials: true
                        },
                        data: angular.toJson({
                            errorUrl: $window.location.href,
                            errorMessage: errorMessage,
                            stackTrace: stackTrace,
                            cause: (cause || ""),
                            browserInfo: browserInfo
                        }),
                        success: function() {
                            loggedErrors.push(errorId);
                        },
                        error: function() {
                            errorsLoggingSuspended = true;
                            $log.debug("error logging suspended for " + errorsLoggingTimeout + " ms.");
                            setTimeout(function() {
                                $log.debug("error logging resumed.");
                                errorsLoggingSuspended = false;
                            }, errorsLoggingTimeout);
                        }
                    });

                } catch (loggingError) {
                    // For Developers - log the log-failure.
                    $log.warn("Error logging to server side failed");
                    $log.log(loggingError);
                }
            }

            // Return the logging function.
            return(log);
        }
);

angular.module('app').provider(
        "$exceptionHandler",
        {
            $get: function(errorLogService) {
                return(errorLogService);
            }
        }
);



angular.module('app').config(['$routeProvider', function($routeProvider) {

        /**
         *
         * Helper functions for hakukohde resolvers
         *
         * */


        var getHakukohdeKoulutukses = function(Hakukohde, $log, $route, SharedStateService, TarjontaService) {

            var koulutusSet = new buckets.Set();

            var selectedKoulutusOids;

            if (angular.isArray(SharedStateService.getFromState('SelectedKoulutukses'))) {
                selectedKoulutusOids = SharedStateService.getFromState('SelectedKoulutukses');
            } else {
                selectedKoulutusOids = [SharedStateService.getFromState('SelectedKoulutukses')];
            }

            var spec = {
                koulutusOid: selectedKoulutusOids
            };

            return TarjontaService.haeKoulutukset(spec);

        };
        var resolveHakukohde = function(Hakukohde, $log, $route, SharedStateService, $q, OrganisaatioService, TarjontaService) {

            if ("new" === $route.current.params.id) {

                var selectedTarjoajaOids;
                var selectedKoulutusOids;

                if (angular.isArray(SharedStateService.getFromState('SelectedOrgOid'))) {
                    selectedTarjoajaOids = SharedStateService.getFromState('SelectedOrgOid');
                } else {
                    selectedTarjoajaOids = [SharedStateService.getFromState('SelectedOrgOid')];
                }

                if (angular.isArray(SharedStateService.getFromState('SelectedKoulutukses'))) {
                    selectedKoulutusOids = SharedStateService.getFromState('SelectedKoulutukses');
                } else {
                    selectedKoulutusOids = [SharedStateService.getFromState('SelectedKoulutukses')];
                }

                var deferred = $q.defer();


                //Initialize model and arrays inside it
                var hakukohde = new Hakukohde({
                    liitteidenToimitusOsoite: {
                    },
                    tarjoajaOids: selectedTarjoajaOids,
                    hakukohteenNimet: {},
                    hakukelpoisuusvaatimusUris: [],
                    hakukohdeKoulutusOids: selectedKoulutusOids,
                    hakukohteenLiitteet: [],
                    valintakokeet: [],
                    lisatiedot: {},
                    valintaperusteKuvaukset: {},
                    soraKuvaukset: {}
                });

                TarjontaService.getKoulutusPromise(selectedKoulutusOids[0]).then(function(res) {
                    var multipleOwners = false;
                    try {
                        multipleOwners = res.result.opetusTarjoajat.length > 1;
                    }
                    catch(e) {
                        // Should'nt happen
                    }
                    hakukohde.multipleOwners = multipleOwners;
                    hakukohde.opetusKielet = Object.keys(res.result.opetuskielis.uris);
                    deferred.resolve(hakukohde);
                });

                return deferred.promise;
            }

            else {
                var deferred = $q.defer();

                Hakukohde.get({oid: $route.current.params.id}).$promise.then(function(res) {
                    var tarjoajat = [];

                    angular.forEach(res.result.koulutusmoduuliToteutusTarjoajatiedot, function(tiedot) {
                        angular.forEach(tiedot.tarjoajaOids, function(oid) {
                            if (tarjoajat.indexOf(oid) === -1) {
                                tarjoajat.push(oid);
                            }
                        });
                    });

                    if (tarjoajat.length) {
                        OrganisaatioService.getPopulatedOrganizations(tarjoajat).then(function(orgs) {
                            res.result.uniqueTarjoajat = orgs;
                            deferred.resolve(res);
                        });
                    }
                    else {
                        deferred.resolve(res);
                    }
                });

                return deferred.promise;
            }
        };

        var resolveCanEditHakukohde = function(Hakukohde, $log, $route, $q, PermissionService) {

            if ($route.current.params.id !== "new") {
                var deferredPermission = $q.defer();
                Hakukohde.get({oid: $route.current.params.id}, function(data) {

                    var promises = [];
                    var canEdit = false;

                    function checkCanEdit(tarjoaja) {
                        var defer = $q.defer();

                        PermissionService.canEdit(tarjoaja).then(function(permission) {
                            if (permission) {
                                canEdit = true;
                            }
                            defer.resolve();
                        });

                        return defer.promise;
                    }

                    angular.forEach(data.result.koulutusmoduuliToteutusTarjoajatiedot, function(tarjoajat, komotoId) {
                        angular.forEach(tarjoajat.tarjoajaOids, function(tarjoaja) {
                            promises.push(checkCanEdit(tarjoaja));
                        });
                    });

                    $q.all(promises).then(function() {
                        return deferredPermission.resolve(canEdit);
                    });

                });

                return deferredPermission.promise;

            } else {
                return undefined;
            }
        };

        var resolveCanCreateHakukohde = function(Hakukohde, $log, $route, SharedStateService, PermissionService) {

            var selectedTarjoajaOids;

            if (angular.isArray(SharedStateService.getFromState('SelectedOrgOid'))) {
                selectedTarjoajaOids = SharedStateService.getFromState('SelectedOrgOid');
            } else {
                selectedTarjoajaOids = [SharedStateService.getFromState('SelectedOrgOid')];
            }

            if (selectedTarjoajaOids !== undefined && selectedTarjoajaOids.length > 0 && selectedTarjoajaOids[0] !== undefined) {
                $log.debug('CHECKING FOR CREATE : ', selectedTarjoajaOids);
                var canCreateVar = PermissionService.canCreate(selectedTarjoajaOids[0]);
                $log.debug('CREATE VAR : ', canCreateVar);
                return canCreateVar;
            } else {
                return undefined;
            }
        };


        /**
         * Resolve org groups, for "hakukohde" usage.
         *
         * Returns promise which will
         * be resolved with "[{key: "oid", value: "name fi"}, ...]"
         *
         * @param $log
         * @param OrganisaatioService
         */
        var resolveOrganisationGroups = function($log, OrganisaatioService) {
            $log.info("resolveOrganisationGroups()");

            // Return the promise
            return OrganisaatioService.getRyhmat().then(function(ryhmat) {
                $log.info("resolveOrganisationGroups() --> got ryhmat!", ryhmat);

                var result = [];

                angular.forEach(ryhmat, function(ryhma) {
                    $log.warn("--> -->", ryhma.oid, ryhma.nimi.fi);
                    result.push({
                        key: ryhma.oid,
                        value: ryhma.nimi.fi
                    });
                });

                $log.info("resolveOrganisationGroups() --> --> got ryhmat processed", result);
                return result;
            });
        };

        function resolveKoulutus(TarjontaService, OrganisaatioService, $log, $route, $q) {
            $log.info("/koulutus/ID", $route);
            var defer = $q.defer();

            TarjontaService.getKoulutus({oid: $route.current.params.id}).$promise.then(function(res) {

                OrganisaatioService.getPopulatedOrganizations(
                    res.result.opetusTarjoajat,
                    res.result.organisaatio.oid
                ).then(function(orgs) {
                    res.result.organisaatiot = orgs;
                    var nimet = "";
                    angular.forEach(orgs, function(org) {
                        nimet += " | " + org.nimi;
                    });

                    res.result.organisaatioidenNimet = nimet.substring(3);

                    defer.resolve(res);
                });
            });

            return defer.promise;
        }

        $routeProvider
                .when("/etusivu", {
                    action: "home.default",
                    reloadOnSearch: false
                })
                .when("/error", {
                    action: "error"//,
                })
                .when("/index", {
                    action: "index",
                    reloadOnSearch: false
                })
                .when("/etusivu/:oid", {
                    action: "home.default",
                    reloadOnSearch: false
                })
                .when('/helpers/localisations', {
                    action: "helpers.localisations"
                })

                .when("/kk/edit/hakukohde", {
                    action: "kk.hakukohde.create"
                })

                .when('/koulutus/:id', {
                    action: "koulutus.review",
                    controller: 'KoulutusRoutingController',
                    resolve: {
                        koulutusModel: resolveKoulutus
                    }
                })
                .when('/koulutus/:id/edit', {
                    action: "koulutus.edit",
                    controller: 'KoulutusRoutingController',
                    resolve: {
                        koulutusModel: resolveKoulutus
                    }
                })
                .when('/koulutus/:toteutustyyppi/:koulutustyyppi/edit/:org/:koulutuskoodi', {
                    action: "koulutus.edit",
                    controller: 'KoulutusRoutingController',
                    resolve: {
                        koulutusModel: function($log, $route) {
                            $log.info("/koulutus/ID/edit", $route);
                            return {
                                'result': {
                                    toteutustyyppi: $route.current.params.toteutustyyppi,
                                    koulutustyyppi: $route.current.params.koulutustyyppi,
                                    isNew: true
                                }
                            };
                        }
                    }
                })
                .when('/koulutus/:toteutustyyppi/:koulutustyyppi/:koulutuslaji/edit/:org/:koulutuskoodi', {
                    action: "koulutus.edit",
                    controller: 'KoulutusRoutingController',
                    resolve: {
                        koulutusModel: function($log, $route) {
                            $log.info("/koulutus/ID/edit", $route);
                            return {
                                'result': {
                                    toteutustyyppi: $route.current.params.toteutustyyppi,
                                    koulutustyyppi: $route.current.params.koulutustyyppi,
                                    isNew: true
                                }
                            };
                        }
                    }
                })
                .when('/valintaPerusteKuvaus/edit/:oppilaitosTyyppi/:kuvausTyyppi/NEW', {
                    action: "valintaPerusteKuvaus.edit",
                    controller: 'ValintaperusteEditController'


                })
                .when('/valintaPerusteKuvaus/edit/:oppilaitosTyyppi/:kuvausTyyppi/:kuvausId', {
                    action: "valintaPerusteKuvaus.edit",
                    controller: 'ValintaperusteEditController',
                    resolve: {
                        resolvedValintaPerusteKuvaus: function($route, Kuvaus) {
                            if ($route.current.params.kuvausId !== undefined && $route.current.params.kuvausId !== "NEW") {

                                var kuvausPromise = Kuvaus.findKuvausWithId($route.current.params.kuvausId);

                                return kuvausPromise;
                            }

                        }
                    }

                })
                .when('/valintaPerusteKuvaus/edit/:oppilaitosTyyppi/:kuvausTyyppi/:kuvausId/COPY', {
                    action: "valintaPerusteKuvaus.edit",
                    controller: 'ValintaperusteEditController',
                    resolve: {
                        resolvedValintaPerusteKuvaus: function($route, Kuvaus) {
                            if ($route.current.params.kuvausId !== undefined && $route.current.params.kuvausId !== "NEW") {

                                var kuvausPromise = Kuvaus.findKuvausWithId($route.current.params.kuvausId);

                                return kuvausPromise;
                            }

                        },
                        action: function() {
                            return 'COPY';
                        }
                    }

                })

                .when('/valintaPerusteKuvaus/search', {
                    action: "valintaPerusteKuvaus.search",
                    controller: 'ValintaperusteSearchController'

                })
                .when('/hakukohde/:id', {
                    action: "hakukohde.review",
                    controller: 'HakukohdeRoutingController',
                    resolve: {
                        hakukohdex: resolveHakukohde
                    }
                })
                .when('/hakukohde/:id/edit/copy', {
                    action: "hakukohde.edit",
                    controller: 'HakukohdeRoutingController',
                    resolve: {
                        isCopy: function() {
                            return true;
                        },
                        canEdit: resolveCanEditHakukohde,
                        canCreate: resolveCanCreateHakukohde,
                        hakukohdex: resolveHakukohde
                    }
                })
                .when('/hakukohde/:id/edit', {
                    action: "hakukohde.edit",
                    controller: 'HakukohdeRoutingController',
                    resolve: {
                        canEdit: resolveCanEditHakukohde,
                        canCreate: resolveCanCreateHakukohde,
                        hakukohdeKoulutuksesx: getHakukohdeKoulutukses,
                        hakukohdex: resolveHakukohde,
                        organisationGroups: resolveOrganisationGroups
                    }
                })

                .when('/haku', {
                    action: "haku.list",
                    controller: 'HakuRoutingController',
                    resolve: {
                        hakus: function($log, $route) {
                            $log.info("/haku", $route);
                            return ["foo", "bar", "zyzzy"];
                        }
                    }
                })

                .when('/haku/NEW', {
                    action: "haku.edit",
                    controller: 'HakuRoutingController',
                    resolve: {
                        hakux: function($log, HakuV1Service) {
                            $log.debug("/haku/NEW");
                            return HakuV1Service.createNewEmptyHaku();
                        }
                    }
                })

                .when('/haku/:id', {
                    action: "haku.review",
                    controller: 'HakuRoutingController',
                    resolve: {
                        hakux: function($log, $route, HakuV1) {
                            $log.debug("/haku/ID", $route);
                            return HakuV1.get({oid: $route.current.params.id}).$promise;
                        }
                    }
                })

                .when('/haku/:id/edit', {
                    action: "haku.edit",
                    controller: 'HakuRoutingController',
                    resolve: {
                        hakux: function($log, $route, HakuV1) {
                            $log.debug("/haku/ID/edit", $route);
                            return HakuV1.get({oid: $route.current.params.id}).$promise;
                        }
                    }
                })


                .when('/koodistoTest', {action: 'koodistoTest'})

                .when('/komo', {
                    action: "komo",
                    controller: 'KomoController'

                })

                .otherwise({redirectTo: "/etusivu"});


    }]);


angular.module('app').controller('AppRoutingCtrl', ['$scope', '$route', '$routeParams', '$log', 'PermissionService',
    function($scope, $route, $routeParams, $log, PermissionService) {

        $log = $log.getInstance("AppRoutingCtrl");

        $log.debug("init");

        $scope.count = 0;

        PermissionService.permissionResource().authorize({}, function(response) {
            $log.debug("Authorization check : " + response.result);
        });

        var render = function() {
            $log.debug("render()");

            var renderAction = $route.current.action;
            var renderPath = renderAction ? renderAction.split(".") : [];

            // Store the values in the model.
            $scope.renderAction = renderAction;
            $scope.renderPath = renderPath;
            $scope.routeParams = $routeParams ? $routeParams : {};
            $scope.count++;

            $log.debug("  renderAction: ", $scope.renderAction);
            $log.debug("  renderPath: ", $scope.renderPath);
            $log.debug("  routeParams: ", $scope.routeParams);
            $log.debug("  count: ", $scope.count);
        };

        $scope.$on(
                "$routeChangeSuccess",
                function($currentRoute, $previousRoute) {
                    $log.debug("$routeChangeSuccess : from, to = ", $currentRoute, $previousRoute);
                    render();
                }
        );
    }]);



//
// "Production" mode
//
angular.module('app').config(function($logProvider) {
    $logProvider.debugEnabled(true);
});
