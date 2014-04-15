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
            'app.edit.ctrl.alkamispaiva',
            'app.edit.ctrl.tutkintonimike',
            'app.edit.ctrl.laajuus',
            'app.komo.ctrl',
            'app.review.ctrl',
            'app.hakukohde.ctrl',
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
            'ResultsTable',
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
            'debounce',
            'Parameter',
            'Logging'
        ]);

angular.module('app').value("globalConfig", window.CONFIG);


angular.module('app').factory(
        "errorLogService",
        function($log, $window, Config) {
            
            var serviceUrl = Config.env["tarjontaRestUrlPrefix"] + "permission/recordUiStacktrace";

            $log.info("*** errorLogService ***", serviceUrl);

            // Log error to console (as normal) AND to the remote server
            function log(exception, cause) {
                // Default behaviour, log to console
                $log.error.apply($log, arguments);

                // Try to send stacktrace event to server
                try {
                    $log.debug("logging error to server side...");
                    
                    var errorMessage = exception.toString();
                    var stackTrace = exception.stack.toString();
                    
//                    // Log the JavaScript error to the server.
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
                            cause: (cause || "")
                        })
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

       var resolveHakukohde = function(Hakukohde, $log, $route, SharedStateService) {
           $log.info("/hakukohde/ID", $route);
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
               //Initialize model and arrays inside it

               return new Hakukohde({
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



           } else {

               var deferredHakukohde = Hakukohde.get({oid: $route.current.params.id});

               return deferredHakukohde.$promise;

           }
       };

       var resolveCanEditHakukohde = function(Hakukohde, $log, $route, $q, PermissionService) {

           if ($route.current.params.id !== "new") {
               var deferredPermission = $q.defer();
               Hakukohde.get({oid: $route.current.params.id}, function(data) {

                   var canEditVar = PermissionService.canEdit(data.result.tarjoajaOids[0]);

                   //deferredPermission.resolve(canEditVar);
                   canEditVar.then(function(permission) {

                       deferredPermission.resolve(permission);

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
                        koulutusModel: function(TarjontaService, $log, $route) {
                            $log.info("/koulutus/ID", $route);
                            return TarjontaService.getKoulutus({oid: $route.current.params.id}).$promise;
                        }
                    }
                })
                .when('/koulutus/:id/edit', {
                    action: "koulutus.edit",
                    controller: 'KoulutusRoutingController',
                    resolve: {
                        koulutusModel: function(TarjontaService, $log, $route) {
                            $log.info("/koulutus/ID/edit", $route);
                            return TarjontaService.getKoulutus({oid: $route.current.params.id}).$promise;
                        }
                    }
                })
                .when('/koulutus/:koulutusastetyyppi/edit/:org/:koulutuskoodi', {
                    action: "koulutus.edit",
                    controller: 'KoulutusRoutingController',
                    resolve: {
                        koulutusModel: function($log, $route) {
                            $log.info("/koulutus/ID/edit", $route);
                            return {'result': {koulutusasteTyyppi: $route.current.params.koulutusastetyyppi}};
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
                        hakukohdex: function(Hakukohde, $log, $route) {
                            $log.info("/hakukohde/ID", $route);
                            return Hakukohde.get({oid: $route.current.params.id}).$promise;
                        }
                    }
                })
            .when('/hakukohde/:id/edit/copy', {
                action: "hakukohde.edit",
                controller: 'HakukohdeRoutingController',
                resolve: {
                    isCopy : function() {
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
                        hakukohdex: resolveHakukohde
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
