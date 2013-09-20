angular.module('tarjontaApp.controllers', ['tarjontaApp.services'])

.controller('MyCtrl1', [function() {
        console.log("MyCtrl1()");
    }])


.controller('KoodistoTestController', function($scope,KoodiService){

        $scope.locale = 'FI';
        $scope.koodistoUri = 'hakukohteetkk';


        //$scope.koodis = KoodiService.getAllKoodisWithKoodiUri($scope.koodistoUri,$scope.locale);

        var koodiPromise = KoodiService.getKoodistoWithKoodiUri($scope.koodistoUri,$scope.locale);
        koodiPromise.then(function(data){

            console.log('Promise got : ');
            console.log(data);

            $scope.koodi = data;


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


