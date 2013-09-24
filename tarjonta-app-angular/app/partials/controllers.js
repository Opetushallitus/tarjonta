

var app = angular.module('app.controllers', ['app.services'])

app.controller('MyCtrl1', [function() {
        console.log("MyCtrl1()");
    }]);

app.controller('KoodistoTestController', function($scope,$route) {



    $scope.testKoodisto = function() {
        console.log('Following selected: ');
        console.log($scope.koodiuri);

    };



   $scope.selectedKoodis = [];

    $scope.koodistofilter='hakukohteet';

    $scope.koodiuri = '';

    $scope.$watch('parenturi',function(newVal,oldVal) {
       console.log('Parent uri change : ' + oldVal + ' new : ' + newVal);

    } );

    $scope.testThat = function() {
      console.log('Got called');
      console.log($scope.parenturi);
    };

    $scope.testcallback = function(selected) {
      console.log('Test onchangecallback called');
      console.log(selected);
      $scope.parenturi = selected.koodiUri;
      console.log('Parent uri:');
      console.log($scope.parenturi);


    };

    $scope.testit = function() {
      alert ($scope.selectedKoodis);
    };


});

app.controller('MyCtrl2', function($scope, instagram) {
    console.log("MyCtrl2()");
//    console.log("MyCtrl2() - as = " + AuthService);
//    console.log("MyCtrl2() - mr = " + MyRoles);

    $scope.pics = [];
    $scope.page = 0;
    $scope.pageSize = 2;

    instagram.fetchPopular(function(data) {
        $scope.pics = data;
    });

});
