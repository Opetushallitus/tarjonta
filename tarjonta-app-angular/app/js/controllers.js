

var app = angular.module('tarjontaApp.controllers', ['tarjontaApp.services'])

app.controller('MyCtrl1', [function() {
        console.log("MyCtrl1()");
    }]);

app.controller('KoodistoTestController', function($scope, Koodisto) {

    $scope.locale = 'FI';
    $scope.koodistoUri = 'hakukohteetkk';

        //$scope.koodis = Koodisto.getAllKoodisWithKoodiUri($scope.koodistoUri,$scope.locale);

        var koodiPromise = Koodisto.getKoodistoWithKoodiUri($scope.koodistoUri,$scope.locale);
        koodiPromise.then(function(data){

            console.log('Got data');
            console.log(data);
            $scope.koodis = data;

    });

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
