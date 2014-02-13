/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 */


'use strict';

angular.module('app.haku',
        [
            'ngRoute',
            'ngResource',
            'ngSanitize',
            'ngAnimate',
            'ui.bootstrap',
            // Internal "library" dependencies - common with tarjonta app
            'imageupload',
            'loading',
            'localisation',
            'config',
            'auth',
            'DateTimePicker',
            'DateFormat',
            'Parameter'
        ]);

angular.module('app',
        [
            'app.haku',
            'app.haku.directives',
            'app.haku.filters',
            'app.haku.services',
            'app.haku.controllers'
        ]);

angular.module('app').value("globalConfig", window.CONFIG);

angular.module('app').config(['$routeProvider', function($routeProvider) {

        $routeProvider
                .when("/etusivu", {
                    action: "home.default",
                    reloadOnSearch: false
                })
                .when("/haku/create", {
                    action: "haku.create",
                    resolve: {
                        hakux: function($log, $route) {
                            $log.info("/haku/create", $route);
                            return {oid: "NEW", name: "create, load NOT IMPLEMENTED!"};
                        }
                    }
                })
                .when("/haku/:oid", {
                    action: "haku.review",
                    reloadOnSearch: false,
                    controller: 'HakuReviewController',
                    resolve: {
                        hakux: function($log, $route) {
                            $log.info("/haku/ID", $route);
                            return {oid: $route.current.params.oid, name: "review, load NOT IMPLEMENTED!"};
                        }
                    }
                })
                .when("/haku/:oid/edit", {
                    action: "haku.edit",
                    reloadOnSearch: false,
                    controller: 'HakuEditController',
                    resolve: {
                        hakux: function($log, $route) {
                            $log.info("/haku/ID/edit", $route);
                            return {oid: $route.current.params.oid, name: "edit, load NOT IMPLEMENTED!"};
                        }
                    }
                })
                .otherwise({redirectTo: "/etusivu"});
    }]);


angular.module('app').controller('HakuRoutingCtrl', ['$scope', '$route', '$routeParams', '$log',
    function($scope, $route, $routeParams, $log) {

        $log.debug("app.AppRoutingCtrl()");

        $scope.count = 0;

//        PermissionService.permissionResource().authorize({}, function(response) {
//            console.log("Authorization check : %j", response);
//        });

        /**
         * Called everytime action is selected
         *
         * @returns {undefined}
         */
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
                    $log.debug("app.AppRoutingCtrl.$routeChangeSuccess : from / to == ", $currentRoute, $previousRoute);
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
