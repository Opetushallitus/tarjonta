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
            'app.kk.services',
            'ui.bootstrap',
            'ngRoute',
            'config'
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
            'ngRoute',
            'ngResource',
            'ngSanitize',
            'ui.bootstrap',
            'loading',
            'localisation',
            'Koodisto',
            'Organisaatio',
            'KoodistoCombo',
            'KoodistoMultiSelect',
            'zippy',
            'angularTreeview',
            'angularTreeview',
            'config'
        ]);

angular.module('app').value("globalConfig", window.CONFIG);

//angular.module('app').config(['$routeProvider', function($routeProvider)
//    {
//
//        $routeProvider
//        		// etusivu / tarjontatiedon haku
//        		.when("/", {templateUrl: 'partials/search/search.html', controller: 'SearchController', reloadOnSearch:false})
//
//                .when('/view2', {templateUrl: 'partials/partial2.html', controller: 'MyCtrl2'})
//
//
//                //Remove this when done Tuomas
//                .when('/koodistoTest', {templateUrl: 'partials/koodistoTest.html', controller: 'KoodistoTestController'})
//
//
//                //
//                // EDIT
//                //
//                .when('/kk/edit/:id',
//                {
//                    templateUrl: 'partials/kk/edit/edit.html',
//                    controller: 'KKEditController'
//                })
//                .when('/kk/edit/:id/:view',
//                {
//                    templateUrl: 'partials/kk/edit/edit.html',
//                    controller: 'KKEditController'
//                })
//
//                //
//                // REVIEW
//                //
//                .when('/kk/review/:id',
//                {
//                    templateUrl: 'partials/kk/review/review.html',
//                    controller: 'KKReviewController'
//                })
//                .when('/kk/review/:id/:view',
//                {
//                    templateUrl: 'partials/kk/review/review.html',
//                    controller: 'KKReviewController'
//                })
//
//                .when('/kk/tutkintoOhjelma', {templateUrl: 'partials/kk/edit/selectTutkintoOhjelmaOpener.html'})
//
//
//                .otherwise({redirectTo: '/etusivu'});
//
//    }]);


angular.module('app').config(['$routeProvider', function($routeProvider)
    {

        $routeProvider.when("/etusivu", {
            action: "home.default"
        })
        $routeProvider.when("/etusivu/:id", {
            action: "home.default"
        })
                .when("/kk/edit/:id", {
            action: "kk.edit"
        })
                .when("/kk/edit/:id/:part", {
            action: "kk.edit"
        })

                .when('/kk/review/:id', {
            action: "kk.review"
        })
                .when('/kk/review/:id/:part', {
            action: "kk.review"
        })
                .otherwise({redirectTo: "/etusivu"});
    }]);


angular.module('app').controller('AppRoutingCtrl', function($scope, $route, $routeParams) {

    console.log("app.AppRoutingCtrl()");

    var render = function() {
        console.log("app.AppRoutingCtrl.render()");

        var renderAction = $route.current.action;
        var renderPath = renderAction ? renderAction.split( "." ) : [];

        // Store the values in the model.
        $scope.renderAction = renderAction;
        $scope.renderPath = renderPath;
        $scope.routeParams = $routeParams ? $routeParams : {};
        $scope.count++;

        console.log("  renderAction" + $scope.renderAction);
        console.log("  renderPath" + $scope.renderPath);
        console.log("  routeParams" + $scope.routeParams);
        console.log("  count" + $scope.count);
    };

    $scope.$on(
            "$routeChangeSuccess",
            function($currentRoute, $previousRoute) {
                console.log("app.AppRoutingCtrl.$routeChangeSuccess");
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
