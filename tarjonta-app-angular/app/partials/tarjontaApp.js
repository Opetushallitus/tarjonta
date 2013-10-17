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
            'app.kk.services',
            'app.edit.ctrl',
            'app.review.ctrl',
            'app.hakukohde.ctrl',
            'ui.bootstrap',
            'ngRoute',
            'config',
            'auth',
            'TarjontaConverter'
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
            'app.helpers',
            'app.dialog',
            'ngRoute',
            'ngResource',
            'ngSanitize',
            'ngAnimate',
            'ui.bootstrap',
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
            'TinyMceRichText',
            'Hakukohde',
            'KoodistoMultiSelect',
            'angularTreeview',
            'TarjontaConverter'
        ]);

angular.module('app').value("globalConfig", window.CONFIG);

angular.module('app').config(['$routeProvider', function($routeProvider)
    {

        $routeProvider
                .when("/etusivu", {
            action: "home.default",
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
                koulutusx: function(TarjontaService, $log, $route) {
                    $log.info("/koulutus/ID", $route);
                    return TarjontaService.getKoulutus({oid: $route.current.params.id});
                }
            }
        })
                .when('/koulutus/:id/edit', {
            action: "koulutus.edit",
            controller: 'KoulutusRoutingController',
            resolve: {
                koulutusx: function(TarjontaService, $log, $route) {
                    $log.info("/koulutus/ID/edit", $route);
                    return TarjontaService.getKoulutus({oid: $route.current.params.id});
                }
            }
        })

                .when('/koulutus/edit/:org/:koulutuskoodi', {
            action: "koulutus.edit",
            controller: 'KoulutusRoutingController',
            resolve: {
                koulutusx: function(TarjontaService, $log, $route) {
                    $log.info("/koulutus/ID/edit", $route);
                    return {koulutusasteTyyppi: "AMMATTIKORKEAKOULUTUS"}
                }
            }
        })

                .when('/hakukohde/:id', {
            action: "hakukohde.review",
            controller: 'HakukohdeRoutingController',
            resolve: {
                hakukohdex: function(TarjontaService, $log, $route) {
                    $log.info("/hakukohde/ID", $route);
                    return TarjontaService.getHakukohde({oid: $route.current.params.id});
                }
            }
        })
                .when('/hakukohde/:id/edit', {
            action: "hakukohde.edit",
            controller: 'HakukohdeRoutingController',
            resolve: {
                hakukohdex: function(Hakukohde, $log, $route,SharedStateService) {
                    $log.info("/hakukohde/ID", $route);
                    if ("new" === $route.current.params.id) {
                        console.log('KOULUTUKSES FROM SHARED SERVICE : ' , SharedStateService.getFromState('SelectedKoulutukses'))
                        //Initialize model and arrays inside it
                        //TODO: retrieve koulutukses
                        return new Hakukohde({

                            liitteidenToimitusOsoite : {

                            },
                            hakukohteenNimet : [
                                {
                                    "uri": "kieli_fi",
                                    "nimi": "suomi",

                                    "teksti": ""
                                }
                            ],
                            hakukelpoisuusvaatimusUris : [],
                            hakukohdeKoulutusOids : [SharedStateService.getFromState('SelectedKoulutukses')],
                            hakukohteenLiitteet : [],
                            valintakokeet : [],

                            lisatiedot : [

                            ]
                        });

                      //  SharedStateService.removeState('SelectedKoulutukses');

                    } else {


                      return Hakukohde.get({oid: $route.current.params.id});
                    }
                }
            }
        })


                .when('/koodistoTest', {action: 'koodistoTest'})

                .otherwise({redirectTo: "/etusivu"});
    }]);


angular.module('app').controller('AppRoutingCtrl', function($scope, $route, $routeParams, $log) {

    $log.debug("app.AppRoutingCtrl()");

    $scope.count = 0;

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

});



//
// "Production" mode
//
angular.module('app').config(function($logProvider) {
    $logProvider.debugEnabled(false);
});
