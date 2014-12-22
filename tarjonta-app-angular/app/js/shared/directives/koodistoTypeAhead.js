'use strict';
var app = angular.module('KoodistoTypeAhead', [
    'ngResource',
    'Logging'
]);
app.directive('koodistotypeahead', function(Koodisto, $log) {
    $log = $log.getInstance('<koodistotypeahead>');
    var filterKoodis = function(koodistoFilterUri, koodisParam) {
        var filteredkoodis = [];
        angular.forEach(koodisParam, function(koodi) {
            if (koodi.koodiKoodisto === koodistoFilterUri) {
                filteredkoodis.push(koodi);
            }
        });
        return filteredkoodis;
    };
    var findKoodiWithUri = function(koodis, koodiUriToSearch) {
        var foundKoodi;
        angular.forEach(koodis, function(koodi) {
            if (koodi.koodiUri === koodiUriToSearch) {
                foundKoodi = koodi;
            }
        });
        return foundKoodi;
    };
    var checkForExistingKoodiInUris = function(koodis, koodiToCheck) {
        var isFound = false;
        angular.forEach(koodis, function(koodiUri) {
            if (koodiUri === koodiToCheck) {
                isFound = true;
            }
        });
        return isFound;
    };
    return {
        restrict: 'EA',
        replace: true,
        templateUrl: 'js/shared/directives/koodistoTypeAhead.html',
        scope: {
            koodistouri: '=',
            koodiuris: '=',
            locale: '=',
            isdependent: '=',
            filterwithkoodistouri: '=',
            allkoodis: '=',
            parentkoodiuri: '=',
            removearvocallback: '=',
            isalakoodi: '=',
            onchangecallback: '='
        },
        controller: function($scope, Koodisto) {
            $scope.selectedkoodiuris = [];
            //Check if koodiuris is not defined if so then expect that 'client' is just interested in callbacks
            if ($scope.koodiuris === undefined) {
                $scope.koodiuris = [];
            }
            if ($scope.isdependent) {
                if ($scope.parentkoodiuri !== undefined) {
                    //Default behaviour is to get alakoodis
                    if ($scope.isalakoodi === undefined) {
                        $log.info('isalakoodi was undefined');
                        $scope.isalakoodi = true;
                    }
                    if ($scope.isalakoodi) {
                        var koodisPromise = Koodisto.getAlapuolisetKoodit($scope.parentkoodiuri, $scope.locale);
                        koodisPromise.then(function(koodisParam) {
                            $scope.koodis = koodisParam;
                        });
                    }
                    else {
                        var koodisPromise = Koodisto.getYlapuolisetKoodit($scope.parentkoodiuri, $scope.locale);
                        koodisPromise.then(function(koodisParam) {
                            $scope.koodis = koodisParam;
                            angular.forEach(koodisParam, function(koodi) {
                                $scope.allKoodis.push(koodi);
                            });
                        });
                    }
                }
            }
            else {
                var koodisPromise = Koodisto.getAllKoodisWithKoodiUri($scope.koodistouri, $scope.locale);
                koodisPromise.then(function(koodisParam) {
                    $scope.koodis = koodisParam;
                    angular.forEach(koodisParam, function(koodi) {
                        if ($scope.allkoodis !== undefined) {
                            $scope.allkoodis.push(koodi);
                        }
                    });
                    $log.debug('KOODISTO URI : ', $scope.koodistouri);
                    $log.debug('FOUND KOODIS : ', $scope.allkoodis.length);
                    //Check if 'client' has given uris that need to be selected
                    if ($scope.koodiuris !== undefined && $scope.koodiuris.length > 0) {
                        angular.forEach($scope.koodiuris, function(koodiURI) {
                            $log.debug('Trying to find koodi with uri: ', koodiURI);
                            var foundKoodi = findKoodiWithUri($scope.koodis, koodiURI);
                            $scope.selectedkoodiuris.push(foundKoodi);
                        });
                    }
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
                        var koodisPromise = Koodisto.getAlapuolisetKoodit($scope.parentkoodiuri, $scope.locale);
                        koodisPromise.then(function(koodisParam) {
                            if ($scope.filterwithkoodistouri !== undefined) {
                                $scope.koodis = filterKoodis($scope.filterwithkoodistouri, koodisParam);
                            }
                            else {
                                $scope.koodis = koodisParam;
                            }
                        });
                    }
                    else {
                        var koodisPromise = Koodisto.getYlapuolisetKoodit($scope.parentkoodiuri, $scope.locale);
                        koodisPromise.then(function(koodisParam) {
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
            $scope.removeSelection = function(selectedValue) {
                $log.info('Removing object : ');
                $log.info(selectedValue);
                $scope.selectedkoodiuris = _($scope.selectedkoodiuris).select(function(koodi) {
                    if (selectedValue.$$hashKey === koodi.$$hashKey) {
                        return false;
                    }
                    else {
                        return true;
                    }
                });
                var specifiedElementIndx = $scope.koodiuris.indexOf(selectedValue);
                $scope.koodiuris.splice(specifiedElementIndx, 1);
                if ($scope.removearvocallback !== undefined) {
                    $scope.removearvocallback(selectedValue.koodiUri);
                }
                $log.debug('Removed element : ', selectedValue.koodiUri);
                $log.debug('Remaining: ', $scope.koodiuris);
            };
            $scope.itemSelected = function(item) {
                $log.info('Item selected');
                $log.info(item);
                $scope.koodiuris.push(item);
                $scope.koodiuris = _.uniq($scope.koodiuris);
            };
            $scope.onKoodistoComboChange = function() {
                $log.debug('SELECTED KOODI : ', $scope.koodi);
                if ($scope.koodi !== undefined) {
                    if ($scope.koodiuris !== undefined && $scope.koodiuris.length > 0) {
                        //Check that were not going add existing uri to collection
                        var doesExist = checkForExistingKoodiInUris($scope.koodiuris, $scope.koodi.koodiUri);
                        if (!doesExist) {
                            $scope.koodiuris.push($scope.koodi.koodiUri);
                            $scope.selectedkoodiuris.push(findKoodiWithUri($scope.koodis, $scope.koodi.koodiUri));
                        }
                    }
                    else {
                        $scope.koodiuris.push($scope.koodi.koodiUri);
                        $scope.selectedkoodiuris.push(findKoodiWithUri($scope.koodis, $scope.koodi.koodiUri));
                    }
                }
                $log.info('Koodi uris');
                $log.info($scope.koodiuris);
                if ($scope.onchangecallback !== undefined) {
                    $scope.onchangecallback($scope.koodi.koodiUri);
                }
                else {
                    $log.info('No onchangecallback defined');
                }
            };
        }
    };
});