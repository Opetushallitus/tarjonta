var app = angular.module('app.test.controllers', ['app.services']);
app.controller('KoodistoTestController', function($scope, $route) {
    $scope.testKoodisto = function() {
        console.log('Following selected: ');
        console.log($scope.koodiuri);
    };
    //$scope.multiselectKoodiUris = [];
    $scope.multiselectKoodiUris = [
        'koulutusohjelmaamm_16452',
        'koulutusohjelmaamm_1550',
        'koulutusohjelmaamm_1600'
    ];
    $scope.koodistofilter = 'hakukohteet';
    $scope.koodiuri = '';
    $scope.$watch('parenturi', function(newVal, oldVal) {
        console.log('Parent uri change : ' + oldVal + ' new : ' + newVal);
    });
    $scope.testThat = function() {
        console.log('Got called');
        console.log($scope.parenturi);
    };
    $scope.testcallback = function(selected) {
        console.log('Test onchangecallback called');
        console.log(selected);
        $scope.parenturi = selected;
        console.log('Parent uri:');
        console.log($scope.parenturi);
    };
    $scope.testit = function() {
        console.log('Got following koodis: ', $scope.multiselectKoodiUris);
        alert($scope.multiselectKoodiUris);
    };
});