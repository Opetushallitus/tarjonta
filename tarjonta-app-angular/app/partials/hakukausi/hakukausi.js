'use strict';
// routing
angular.module('app').config([
    '$routeProvider', function($routeProvider) {
        console.log('adding hakukausi routes');
        $routeProvider.when('/hakukausi', {
            templateUrl: 'partials/hakukausi/hakukausi-select.html'
        });
        $routeProvider.when('/hakukausi/:kausi/:vuosi', {
            templateUrl: 'partials/hakukausi/hakukausi.html',
            //controls layout ei osaa päivittää tietojaan joten data pitää hakea ensin...
            resolve: {
                dto: function($q, $route, ParameterService) {
                    var kausi = $route.current.params.kausi;
                    var vuosi = $route.current.params.vuosi;
                    var deferred = $q.defer();
                    //päivitetty päivämäärää varten haetaan parametri
                    ParameterService.haeParametrit({
                        path: 'PHK_',
                        name: kausi + vuosi
                    }).then(function(params) {
                        if (params && params.length > 0) {
                            deferred.resolve({
                                tila: 'VALMIS',
                                modified: params[0].modified
                            });
                        }
                        else {
                            deferred.resolve({
                                tila: 'KESKEN'
                            });
                        }
                    });
                    return deferred.promise;
                }
            }
        });
    }
])
//form & model directive patching (https://github.com/angular/angular.js/issues/1404)
.config(function($provide) {
    $provide.decorator('ngModelDirective', function($delegate) {
        var ngModel = $delegate[0];
        var controller = ngModel.controller;
        ngModel.controller = [
            '$scope',
            '$element',
            '$attrs',
            '$injector', function(scope, element, attrs, $injector) {
                var $interpolate = $injector.get('$interpolate');
                attrs.$set('name', $interpolate(attrs.name || '')(scope));
                $injector.invoke(controller, this, {
                    '$scope': scope,
                    '$element': element,
                    '$attrs': attrs
                });
            }
        ];
        return $delegate;
    });
    $provide.decorator('formDirective', function($delegate) {
        var form = $delegate[0];
        var controller = form.controller;
        form.controller = [
            '$scope',
            '$element',
            '$attrs',
            '$injector', function(scope, element, attrs, $injector) {
                var $interpolate = $injector.get('$interpolate');
                attrs.$set('name', $interpolate(attrs.name || attrs.ngForm || '')(scope));
                $injector.invoke(controller, this, {
                    '$scope': scope,
                    '$element': element,
                    '$attrs': attrs
                });
            }
        ];
        return $delegate;
    });
}) // controller
.controller('HakukausiController', [
    'Koodisto',
    '$scope',
    'ParameterService',
    '$location',
    '$routeParams',
    '$route', function HakukausiController(Koodisto, $scope, Parameter, $location, $routeParams, $route) {
        console.log('RP:', $routeParams);
        console.log('scope:', $scope);
        var getKausiVuosiIdentifier = function() {
            return $scope.kausivuosi.kausi + $scope.kausivuosi.vuosi;
        };
        //validation pattern, used in the form
        $scope.timePattern = /^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$/;
        $scope.model = {
            parameter: {},
            formControls: {},
            showError: false,
            showSuccess: false,
            validationmsgs: [],
            collapse: {
                model: true
            },
            dto: $route.current.locals.dto
        };
        if ($route.current.locals.dto == undefined) {
            $scope.model.dto = {};
        }
        //vuosi & kausi provided
        if ($routeParams.kausi && $routeParams.vuosi) {
            $scope.kausivuosi = {
                kausi: $routeParams.kausi,
                vuosi: parseInt($routeParams.vuosi)
            };
            var kausivuosi = getKausiVuosiIdentifier();
            console.log('loading data', kausivuosi);
            $scope.saved = false;
            $scope.model.parameter = {};
            Parameter.haeHakukaudenParametrit(kausivuosi, $scope.model.parameter);
            $scope.model.showError = false;
            $scope.model.showSuccess = false;
            //"viimeksi muokattu" pvm haku:
            Parameter.haeParametrit({
                path: 'PHK_',
                name: kausivuosi
            }).then(function(params) {
                console.log('parameter fetched?');
                if (params && params.length > 0) {
                    console.log('modified:', $scope.model.dto.modified);
                    $scope.model.dto.modified = params[0].modified;
                    $scope.model.dto.tila = 'VALMIS';
                }
                else {
                    $scope.model.dto.modified = undefined;
                    $scope.model.dto.tila = 'UUSI';
                    console.log('undefined!');
                }
            });
        }
        else {
            $scope.kausivuosi = {};
        }
        console.log($scope.kausivuosi);
        //kausi-vuoden vuodet, kaksi vuotta taaksepäin
        $scope.vuodet = [];
        var vuosi = new Date().getFullYear();
        for (var v = vuosi - 2; v < vuosi + 10; v++) {
            $scope.vuodet.push(v);
        }
        var isVuosiKausiValid = function() {
            return $scope.kausivuosi.vuosi && $scope.kausivuosi.kausi;
        };
        $scope.isVuosiKausiValid = isVuosiKausiValid;
        var saveParameters = function() {
            $scope.saved = true;
            console.log('saving!!!, form:', $scope.hakukausiForm);
            if (!$scope.hakukausiForm.$valid) {
                console.log('invalid data, exiting');
                $scope.model.showError = true;
                return;
            }
            var kausivuosi = getKausiVuosiIdentifier();
            Parameter.tallennaHakukaudenParametrit(kausivuosi, $scope.model.parameter).then(function() {
                $scope.model.showError = false;
                $scope.model.showSuccess = true;
            });
        };
        $scope.saveParameters = saveParameters;
        $scope.vuosiChanged = function(data) {
            if (isVuosiKausiValid()) {
                setLocation();
            }
        };
        $scope.kausiChanged = function(koodi) {
            $scope.kausivuosi.kausi = koodi.koodiUri;
            if (isVuosiKausiValid()) {
                setLocation();
            }
        };
        var setLocation = function() {
            var path = '/hakukausi/' + $scope.kausivuosi.kausi + '/' + $scope.kausivuosi.vuosi;
            console.log('changing path:', path);
            $location.path(path);
        };
    }
]) // directives
/**
* Päivämäärän editointi rivi
*/
.directive('tParamEditDate', function() {
    return {
        restrict: 'A',
        scope: true,
        templateUrl: 'partials/hakukausi/edit-date.html',
        link: function(scope, element, attrs) {
            scope.name = attrs.name;
            scope.nameb = attrs.name + 'AM'; //"aina valintojen..."
        }
    };
})
/**
 * Päivämäärävälin editointi rivi
 */
.directive('tParamEditDateRange', function() {
    return {
        restrict: 'A',
        scope: true,
        templateUrl: 'partials/hakukausi/edit-date-range.html',
        link: function(scope, element, attrs) {
            scope.names = attrs.name + '_S';
            scope.namee = attrs.name + '_E';
            scope.nameb = attrs.name + 'M'; //"aina valintojen muuttuessa..."
        }
    };
})
/**
 * Väliotsikko rivi
*/
.directive('tSubTitle', function() {
    return {
        restrict: 'A',
        scope: true,
        templateUrl: 'partials/hakukausi/title.html',
        link: function(scope, element, attrs) {}
    };
})
/**
 * Helppi rivi
 */
.directive('tHelp', function() {
    return {
        restrict: 'A',
        scope: true,
        templateUrl: 'partials/hakukausi/help.html',
        link: function(scope, element, attrs) {}
    };
})
/**
 * Lokalisoinnin oletusarvo
 */
.directive('tUseDefaultTt', function() {
    return {
        restrict: 'A',
        scope: true,
        link: function(scope, element, attrs) {
            scope.tUseDefaultTt = attrs.tUseDefaultTt;
            scope.tUseTtKey = attrs.tUseTtKey;
        }
    };
})
/**
 * Required
*/
.directive('tIsRequired', function() {
    return {
        restrict: 'A',
        scope: true,
        link: function(scope, element, attrs) {
            scope.tIsRequired = 'true' === attrs.tIsRequired;
        }
    };
})
/**
 * Aina valintojen muuttuessa
 */
.directive('tAlways', function() {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            scope.always = true;
            scope.tUseTtKey = attrs.tUseTtKey;
        }
    };
})
/**
 * Help key
 */
.directive('tHelp', function() {
    return {
        restrict: 'A',
        scope: true,
        link: function(scope, element, attrs) {
            scope.help = attrs.tHelp;
        }
    };
});