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
            'app.kk.services',
            'app.edit.ctrl',
            'app.edit.ctrl.alkamispaiva',
            'app.review.ctrl',
            'app.hakukohde.ctrl',
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
            'DateTimePicker',
            'Hakukohde',
            'KoodistoMultiSelect',
            'KoodistoTypeAhead',
            'orgAngularTreeview',
            'ResultsTable',
            'imageupload',
            'MultiSelect',
            'OrderByNumFilter',
            'CommonDirectives',
            'MonikielinenTextField',
            'ImageDirective',
            'RichTextArea',
            'MonikielinenTextArea',
            'ControlsLayout',
            'angularTreeview',
            'DateFormat',
            'TreeFieldDirective',
            'AiheetJaTeematChooser',
            'debounce'
        ]);

angular.module('app').value("globalConfig", window.CONFIG);

angular.module('app').config(['$routeProvider', function($routeProvider) {

        $routeProvider
                .when("/etusivu", {
                    action: "home.default",
                    reloadOnSearch: false
                })
                .when("/foo", {
                    action: "foo"//,
                })
                .when("/index", {
                    action: "index",
                    reloadOnSearch: false
                })
                .when("/etusivu/:oid", {
                    action: "home.default",
                    reloadOnSearch: false
                })
                .when("/kk/edit/:orgOid/:komotoOid", {
                    action: "kk.edit"
                })
                .when("/kk/edit/:type/:part/:org/:komoto/:koulutuskoodi", {
                    action: "kk.edit"
                })
                .when('/kk/review/:id', {
                    action: "kk.review"
                })
                .when('/kk/review/:id/:part', {
                    action: "kk.review"
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
                .when('/valintaPerusteKuvaus',{

                    action : "valintaPerusteKuvaus.edit",
                    controller: 'ValintaperusteEditController'


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
                .when('/hakukohde/:id/edit', {
                    action: "hakukohde.edit",
                    controller: 'HakukohdeRoutingController',
                    resolve: {
                        hakukohdex: function(Hakukohde, $log, $route, SharedStateService) {
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


                .when('/koodistoTest', {action: 'koodistoTest'})

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
