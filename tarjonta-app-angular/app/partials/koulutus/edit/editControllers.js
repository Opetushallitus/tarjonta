
var app = angular.module('app.edit.ctrl', []);

app.controller('BaseEditController', ['$scope', '$location', '$log', 'TarjontaService', '$routeParams', 'LocalisationService', '$modal',
    function BaseEditController($scope, $location, $log, tarjontaService, $routeParams, LocalisationService, $modal) {
        $log.info("BaseEditController()");

        $scope.model = {
            routeParams: $routeParams,
            collapse: {
                model: true
            },
            koulutus: $scope.koulutusx, // preloaded in route resolve
            foo: "bar"
        };

    }]);
