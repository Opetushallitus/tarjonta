var app = angular.module('KoodistoArvoCombo', [
    'ngResource',
    'Logging'
]);
app.directive('koodistoarvocombo', function(Koodisto, $log) {
    'use strict';
    $log = $log.getInstance('<koodistoarvocombo>');
    var filterKoodis = function(koodistoFilterUri, koodisParam) {
        var filteredkoodis = [];
        angular.forEach(koodisParam, function(koodi) {
            if (koodi.koodiKoodisto === koodistoFilterUri) {
                filteredkoodis.push(koodi);
            }
        });
        return filteredkoodis;
    };
    var findKoodiWithArvo = function(koodi, koodis) {
        var foundKoodi;
        angular.forEach(koodis, function(koodiLoop) {
            if (koodiLoop.koodiArvo === koodi) {
                foundKoodi = koodiLoop;
            }
        });
        return foundKoodi;
    };
    var findKoodiWithUri = function(koodi, koodis) {
        var foundKoodi;
        angular.forEach(koodis, function(koodiLoop) {
            if (koodiLoop.koodiUri === koodi) {
                foundKoodi = koodiLoop;
            }
        });
        return foundKoodi;
    };
    return {
        restrict: 'EA',
        replace: true,
        templateUrl: 'js/shared/directives/koodistoArvoCombo.html',
        scope: {
            koodistouri: '=',
            koodiuri: '=',
            locale: '=',
            isdependent: '=',
            filterwithkoodistouri: '=',
            isdisabled: '=',
            parentkoodiuri: '=',
            prompt: '=',
            isalakoodi: '=',
            onchangecallback: '='
        },
        controller: function($scope, Koodisto) {
            if ($scope.isdependent) {
                if ($scope.parentkoodiuri !== undefined) {
                    //Default behaviour is to get alakoodis
                    if ($scope.isalakoodi === undefined) {
                        $log.info('isalakoodi was undefined');
                        $scope.isalakoodi = true;
                    }
                    if ($scope.isalakoodi) {
                        Koodisto.getAlapuolisetKoodit($scope.parentkoodiuri, $scope.locale)
                        .then(function(koodisParam) {
                            $scope.koodis = koodisParam;
                        });
                    }
                    else {
                        Koodisto.getYlapuolisetKoodit($scope.parentkoodiuri, $scope.locale)
                        .then(function(koodisParam) {
                            $scope.koodis = koodisParam;
                        });
                    }
                }
            }
            else {
                Koodisto.getAllKoodisWithKoodiUri($scope.koodistouri, $scope.locale)
                .then(function(koodisParam) {
                    $scope.koodis = koodisParam;
                });
            }
            $scope.$watch('parentkoodiuri', function() {
                $log.info('Parent koodi uri changed');
                if ($scope.parentkoodiuri !== undefined) {
                    //Default behaviour is to get alakoodis
                    if ($scope.isalakoodi === undefined) {
                        $log.info('isalakoodi was undefined');
                        $scope.isalakoodi = true;
                    }
                    if ($scope.isalakoodi) {
                        Koodisto.getAlapuolisetKoodit($scope.parentkoodiuri, $scope.locale)
                        .then(function(koodisParam) {
                            if ($scope.filterwithkoodistouri !== undefined) {
                                $scope.koodis = filterKoodis($scope.filterwithkoodistouri, koodisParam);
                            }
                            else {
                                $scope.koodis = koodisParam;
                            }
                        });
                    }
                    else {
                        Koodisto.getYlapuolisetKoodit($scope.parentkoodiuri, $scope.locale)
                        .then(function(koodisParam) {
                            if ($scope.filterwithkoodistouri !== undefined) {
                                $scope.koodis = filterKoodis($scope.filterwithkoodistouri, koodisParam);
                            }
                            else {
                                $scope.koodis = koodisParam;
                            }
                        });
                    }
                }
            });
            $scope.onKoodistoComboChange = function() {
                if ($scope.onchangecallback !== undefined) {
                    $log.info('Select koodiarvo ');
                    $log.info($scope.koodiarvo);
                    var koodi = findKoodiWithArvo($scope.koodiarvo, $scope.koodis);
                    $log.info('Found koodi : ', koodi);
                    $scope.onchangecallback(koodi);
                    $scope.koodiuri = koodi.koodiUri;
                }
                else {
                    $log.info('No onchangecallback defined');
                }
            };
        }
    };
});