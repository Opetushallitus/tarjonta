
'use strict';

var app = angular.module('app.haku.ctrl', []);

app.controller('HakuRoutingController', ['$scope', '$log', '$routeParams', '$route',
    function HakukohdeRoutingController($scope, $log, $routeParams, $route) {
        $log.info("HakuRoutingController()", $routeParams);
        $log.info("$route: ", $route);
        $log.info("$route action: ", $route.current.$$route.action);
        $log.info("SCOPE: ", $scope);

    }
]);
