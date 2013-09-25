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

angular.module('app').config(['$routeProvider', function($routeProvider)
    {

        $routeProvider
        		// etusivu / tarjontatiedon haku
        		.when("/", {templateUrl: 'partials/search/search.html', controller: 'SearchController', reloadOnSearch:false})
        
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
