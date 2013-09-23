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
            'app.kk.services',
            'ngRoute'
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
            'app.kk',
            'ngRoute',
            'ngResource',
            'ui.bootstrap',
            'loading',
            'localisation',
            'Koodisto',
            'KoodistoCombo'
        ]);

angular.module('app').config(['$routeProvider', function($routeProvider)
    {

        $routeProvider
                .when('/view1', {templateUrl: 'partials/partial1.html', controller: 'MyCtrl1'})

                .when('/view2', {templateUrl: 'partials/partial2.html', controller: 'MyCtrl2'})


                //Remove this when done Tuomas
                .when('/koodistoTest', {templateUrl: 'partials/koodistoTest.html', controller: 'KoodistoTestController'})

                //
                // EDIT
                //
                .when('/kk/edit/:id',
                {
                    templateUrl: 'partials/kk/edit/edit.html',
                    controller: 'KKEditController'
                })
                .when('/kk/edit/:id/:view',
                {
                    templateUrl: 'partials/kk/edit/edit.html',
                    controller: 'KKEditController'
                })

                //
                // REVIEW
                //
                .when('/kk/review/:id',
                {
                    templateUrl: 'partials/kk/review/review.html',
                    action: 'app.kk.review.ctrl.ReviewController'
                })
                .when('/kk/review/:id/:view',
                {
                    templateUrl: 'partials/kk/review/review.html',
                    action: 'app.kk.review.ctrl.ReviewController'
                })

                .otherwise({redirectTo: '/view1'});

    }]);
