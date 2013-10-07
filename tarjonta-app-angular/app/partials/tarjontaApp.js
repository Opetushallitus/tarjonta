'use strict';
/*******************************************************/
/* Define all project sub-modules with no dependencies */
/*******************************************************/
angular.module('app.kk',
        [
            'app.kk.directives',
            'app.kk.filters',
            'app.kk.services',
            'app.kk.edit.ctrl',
            'app.kk.review.ctrl',
            'app.kk.edit.hakukohde.ctrl',
            'app.kk.services',
            'ui.bootstrap',
            'ngRoute',
            'config',
            'auth'
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
            'app.helpers',
            'ngRoute',
            'ngResource',
            'ngSanitize',
            'ui.bootstrap',
            'loading',
            'localisation',
            'Koodisto',
            'Organisaatio',
            'TarjontaPermissions',
            'TarjontaCache',
            'Tarjonta',
            'KoodistoCombo',
            'Hakukohde',
            'KoodistoMultiSelect',
            'angularTreeview'
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
              action : "kk.hakukohde.create"
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
