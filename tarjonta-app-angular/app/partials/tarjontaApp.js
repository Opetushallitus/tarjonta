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
            'KoodistoCombo',
            'zippy',
            'app.organisaatiohaku',
            'angularTreeview'
        ]);


angular.module('app').config(['$routeProvider', function($routeProvider)
    {

        $routeProvider
        
        		// tarjontatiedon haku
        		.when("/search/:oid", {templateUrl: 'partials/search/search.html', controller: 'SearchController'})
        
        		.when('/etusivu', {templateUrl: 'partials/etusivu.html'})

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
                    controller: 'KKReviewController'
                })
                .when('/kk/review/:id/:view',
                {
                    templateUrl: 'partials/kk/review/review.html',
                    controller: 'KKReviewController'
                })

                .when('/kk/tutkintoOhjelma', {templateUrl: 'partials/kk/edit/selectTutkintoOhjelmaOpener.html'})

        		
                .otherwise({redirectTo: '/etusivu'});

    }]);




//
// "Production" mode
//
//angular.module('app').config(function($logProvider) {
//    $logProvider.debugEnabled(false);
//});
