'use strict';
/*******************************************************/
/* Define all project sub-modules with no dependencies */
/*******************************************************/
angular.module('app.kk',
        [
            'app.kk.directives',
            'app.kk.filters',
            'app.kk.services',
            'app.kk.edit.hakukohde.ctrl',
            'app.kk.edit.hakukohde.review.ctrl',
            'app.kk.edit.valintaperustekuvaus.ctrl',
            'app.kk.search.valintaperustekuvaus.ctrl',
            'app.kk.services',
            'app.edit.ctrl',
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
            'app.controllers',
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
            'Parameter'
        ]);

angular.module('app').value("globalConfig", window.CONFIG);

angular.module('app').config(['$routeProvider', function($routeProvider) {

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

                .when('/koulutus/edit/:org/:koulutuskoodi', {
                    action: "koulutus.edit",
                    controller: 'KoulutusRoutingController',
                    resolve: {
                        koulutusModel: function(TarjontaService, $log, $route) {
                            $log.info("/koulutus/ID/edit", $route);
                            return {'result': {koulutusasteTyyppi: "KORKEAKOULUTUS"}};
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

                            //return TarjontaService.getHakukohde({oid: $route.current.params.id});
                            var deferredHakukohde = Hakukohde.get({oid: $route.current.params.id});

                            return deferredHakukohde.$promise;
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

                    canEdit: function(Hakukohde, $log, $route, $q, SharedStateService, PermissionService) {

                        if ($route.current.params.id !== "new") {
                            var deferredPermission = $q.defer();
                            Hakukohde.get({oid: $route.current.params.id}, function(data) {
                                console.log("GOT HAKUKOHDE DATA: ", data);



                                var canEditVar = PermissionService.canEdit(data.result.tarjoajaOids[0]);

                                //deferredPermission.resolve(canEditVar);
                                canEditVar.then(function(permission) {

                                    console.log('GOT PERMISSION DATA ', permission);
                                    deferredPermission.resolve(permission);

                                });

                            });

                            return deferredPermission.promise;

                        } else {
                            return undefined;
                        }


                    },
                    canCreate: function(Hakukohde, $log, $route, SharedStateService, PermissionService) {

                        var selectedTarjoajaOids;

                        if (angular.isArray(SharedStateService.getFromState('SelectedOrgOid'))) {
                            selectedTarjoajaOids = SharedStateService.getFromState('SelectedOrgOid');
                        } else {
                            selectedTarjoajaOids = [SharedStateService.getFromState('SelectedOrgOid')];
                        }

                        if (selectedTarjoajaOids !== undefined && selectedTarjoajaOids.length > 0 && selectedTarjoajaOids[0] !== undefined) {
                            console.log('CHECKING FOR CREATE : ', selectedTarjoajaOids);
                            var canCreateVar = PermissionService.canCreate(selectedTarjoajaOids[0]);
                            console.log('CREATE VAR : ', canCreateVar);
                            return canCreateVar;
                        } else {
                            return undefined;
                        }


                    },
                    hakukohdex: function(Hakukohde, $log, $route, SharedStateService) {
                        $log.info("/hakukohde/ID", $route);
                        if ("new" === $route.current.params.id) {
                            $log.info("CREATING NEW HAKUKOHDE: ", $route.current.params.id);
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

                            //  SharedStateService.removeState('SelectedKoulutukses');

                        } else {

                            var deferredHakukohde = Hakukohde.get({oid: $route.current.params.id});

                            return deferredHakukohde.$promise;

                            /*var deferredHakukohde = $q.defer();
                             Hakukohde.get({oid: $route.current.params.id},function(result){

                             deferredHakukohde.resolve(result);
                             });
                             //return deferredHakukohde.$promise;
                             return deferredHakukohde.promise;  */

                        }
                    }
                }
            })
                .when('/hakukohde/:id/edit', {
                    action: "hakukohde.edit",
                    controller: 'HakukohdeRoutingController',
                    resolve: {
                        canEdit: function(Hakukohde, $log, $route, $q, SharedStateService, PermissionService) {

                            if ($route.current.params.id !== "new") {
                                var deferredPermission = $q.defer();
                                Hakukohde.get({oid: $route.current.params.id}, function(data) {
                                    console.log("GOT HAKUKOHDE DATA: ", data);



                                    var canEditVar = PermissionService.canEdit(data.result.tarjoajaOids[0]);

                                    //deferredPermission.resolve(canEditVar);
                                    canEditVar.then(function(permission) {

                                        console.log('GOT PERMISSION DATA ', permission);
                                        deferredPermission.resolve(permission);

                                    });

                                });

                                return deferredPermission.promise;

                            } else {
                                return undefined;
                            }


                        },
                        canCreate: function(Hakukohde, $log, $route, SharedStateService, PermissionService) {

                            var selectedTarjoajaOids;

                            if (angular.isArray(SharedStateService.getFromState('SelectedOrgOid'))) {
                                selectedTarjoajaOids = SharedStateService.getFromState('SelectedOrgOid');
                            } else {
                                selectedTarjoajaOids = [SharedStateService.getFromState('SelectedOrgOid')];
                            }

                            if (selectedTarjoajaOids !== undefined && selectedTarjoajaOids.length > 0 && selectedTarjoajaOids[0] !== undefined) {
                                console.log('CHECKING FOR CREATE : ', selectedTarjoajaOids);
                                var canCreateVar = PermissionService.canCreate(selectedTarjoajaOids[0]);
                                console.log('CREATE VAR : ', canCreateVar);
                                return canCreateVar;
                            } else {
                                return undefined;
                            }


                        },
                        hakukohdex: function(Hakukohde, $log, $route, SharedStateService) {
                            $log.info("/hakukohde/ID", $route);
                            if ("new" === $route.current.params.id) {
                                $log.info("CREATING NEW HAKUKOHDE: ", $route.current.params.id);
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

                                //  SharedStateService.removeState('SelectedKoulutukses');

                            } else {

                                var deferredHakukohde = Hakukohde.get({oid: $route.current.params.id});

                                return deferredHakukohde.$promise;

                                /*var deferredHakukohde = $q.defer();
                                 Hakukohde.get({oid: $route.current.params.id},function(result){

                                 deferredHakukohde.resolve(result);
                                 });
                                 //return deferredHakukohde.$promise;
                                 return deferredHakukohde.promise;  */

                            }
                        }
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
                        hakux: function($log, $route, HakuV1) {
                            $log.info("/haku/NEW", $route);
                            // Fake the loading of Haku
                            return {
                                "status" : "OK",
                                "result" : {
                                    "hakukausiUri" : "",
                                    "hakutapaUri" : "",
                                    "hakukausiVuosi" : 1900 + new Date().getYear(),
                                    "hakutyyppiUri" : "",
                                    "kohdejoukkoUri" : "",
                                    "koulutuksenAlkamisVuosi" : 1900 + new Date().getYear(),
                                    "koulutuksenAlkamiskausiUri" : "",
                                    "tila" : "LUONNOS",
                                    "sijoittelu" : false,
                                    "hakuaikas" : [ {
                                      "nimi" : "",
                                      "alkuPvm" : new Date().getTime(),
                                      "loppuPvm" : new Date().getTime()
                                    } ],
                                    "hakukohdeOids" : [ ],
                                    "modified" : new Date().getTime(),
                                    "modifiedBy" : "NA",
                                    "nimi" : {
                                      "kieli_fi" : "",
                                      "kieli_sv" : "",
                                      "kieli_en" : ""
                                    },
                                    "maxHakukohdes" : 0
                                    // "hakulomakeUri" : "http://www.hut.fi",
                                }
                            };
                        }
                    }
                })

                .when('/haku/:id', {
                    action: "haku.review",
                    controller: 'HakuRoutingController',
                    resolve: {
                        hakux: function($log, $route, HakuV1) {
                            $log.info("/haku/ID", $route);
                            return HakuV1.get({oid: $route.current.params.id}).$promise;
                        }
                    }
                })

                .when('/haku/:id/edit', {
                    action: "haku.edit",
                    controller: 'HakuRoutingController',
                    resolve: {
                        hakux: function($log, $route, HakuV1) {
                            $log.info("/haku/ID/edit", $route);
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

        $log.debug("app.AppRoutingCtrl()");

        $scope.count = 0;


        PermissionService.permissionResource().authorize({}, function(response) {
            console.log("Authorization check : " + response.result);
        });


        var render = function() {
            $log.debug("app.AppRoutingCtrl.render()");

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
                    $log.debug("app.AppRoutingCtrl.$routeChangeSuccess : from, to = ", $currentRoute, $previousRoute);
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
