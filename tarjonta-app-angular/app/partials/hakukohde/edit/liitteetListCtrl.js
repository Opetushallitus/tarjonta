var app =  angular.module('app.kk.edit.hakukohde.ctrl');

app.controller('LiitteetListController',function($scope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde,Liite, HakuService, $modal ,Config,$location) {

     var kieliSet = new buckets.Set();



     $scope.model.liitteenkielet = [];

     $scope.model.liitteet = [];

    var liitteetResource = Liite.get({hakukohdeOid: $scope.model.hakukohde.oid});

    var liitteetPromise = liitteetResource.$promise;

    liitteetPromise.then(function(liitteet){

        console.log('LIITTEET GOT: ',liitteet);

        angular.forEach(liitteet.result,function(liite){
           kieliSet.add(liite.kieliNimi);

          $scope.model.liitteet.push(liite);
        });
        $scope.model.liitteenkielet = kieliSet.toArray();

    });

});