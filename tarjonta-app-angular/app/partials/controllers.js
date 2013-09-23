

var app = angular.module('app.controllers', ['app.services'])

app.controller('MyCtrl1', [function() {
        console.log("MyCtrl1()");
    }]);

app.controller('KoodistoTestController', function($scope, Koodisto) {

    $scope.locale = 'FI';
    $scope.koodistouri = 'hakukohteetkk';
    $scope.koodiuri = '';


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
