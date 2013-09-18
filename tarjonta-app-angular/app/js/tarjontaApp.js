'use strict';
/*******************************************************/
/* Define all project sub-modules with no dependencies */
/*******************************************************/
angular.module('kkTutkintoApp',
        [
            'kkTutkintoApp.directives',
            'kkTutkintoApp.filters',
            'kkTutkintoApp.services',
            'kkTutkintoApp.controllers',
            'tarjontaApp.services',
            'ngRoute'
        ]);

/*******************************************************/
/* Main module dependecies                             */
/*******************************************************/
angular.module('tarjontaApp',
        [
            'tarjontaApp.directives',
            'tarjontaApp.filters',
            'tarjontaApp.services',
            'tarjontaApp.controllers',
            'kkTutkintoApp',
            'ngRoute',
            'ngResource'
        ]);

angular.module('tarjontaApp').config(['$routeProvider', function($routeProvider)
    {
        $routeProvider.when('/view1', {templateUrl: 'partials/partial1.html', controller: 'MyCtrl1'});
        $routeProvider.when('/view2', {templateUrl: 'partials/partial2.html', controller: 'MyCtrl2'});
        $routeProvider.when('/luoKorkeakoulu',
                {
                    templateUrl: 'partials/kkTutkinto/formTutkinto.html',
                    action: 'tarjontaApp.FormTutkintoController'
                }
        );
        $routeProvider.otherwise({redirectTo: '/view1'});

    }]);
