angular.module('tarjontaApp.controllers', ['tarjontaApp.services'])

.controller('MyCtrl1', [function() {
        console.log("MyCtrl1()");
    }])


.controller('KoodistoTestController', function($scope,Koodisto){

        $scope.locale = 'FI';
        $scope.koodistoUri = 'hakukohteetkk';


        //$scope.koodis = Koodisto.getAllKoodisWithKoodiUri($scope.koodistoUri,$scope.locale);

        var koodiPromise = Koodisto.getKoodistoWithKoodiUri($scope.koodistoUri,$scope.locale);
        koodiPromise.then(function(data){

            console.log('Got data');
            console.log(data);
            $scope.koodis = data;


        });

    })

.controller('MyCtrl2', function($scope, instagram) {
    console.log("MyCtrl2()");

    $scope.pics = [];
    $scope.page = 0;
    $scope.pageSize = 2;

    instagram.fetchPopular(function(data) {
        $scope.pics = data;
    });
});


