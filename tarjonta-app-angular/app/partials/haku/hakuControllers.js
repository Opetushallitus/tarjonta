
'use strict';

var app = angular.module('app.haku.ctrl', ['app.haku.list.ctrl', 'app.haku.review.ctrl', 'app.haku.edit.ctrl', 'app.haku.edit.organisations.ctrl']);

app.controller('HakuRoutingController', ['$scope', '$log', '$routeParams', '$route',
    function HakukohdeRoutingController($scope, $log, $routeParams, $route) {
        $log = $log.getInstance("HakuRoutingController");

        $log.info("HakuRoutingController()", $routeParams);
        $log.info("$route: ", $route);
        $log.info("$route action: ", $route.current.$$route.action);
        $log.info("SCOPE: ", $scope);
    }
]);
