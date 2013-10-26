var app =  angular.module('app.kk.edit.hakukohde.ctrl')

app.controller('ValintakokeetController', function($scope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde,Valintakoe, HakuService, $modal ,Config,$location) {


   var kieliSet = new buckets.Set();
   $scope.model.hakukohdeOid  =  $scope.model.hakukohde.oid;

   $scope.model.kielet = [];

   $scope.model.valintakokees = [];

   var valintaKokeetResource = Valintakoe.getAll({ oid : $scope.model.hakukohdeOid });
    console.log('LOADING VALINTAKOKEES');

    var valintaKokeetPromise  = valintaKokeetResource.$promise;
   valintaKokeetPromise.then(function(valintakokees){
       console.log('GOT KOKEES : ', valintakokees);

           angular.forEach(valintakokees.result,function(valintakoe){
              if (valintakoe !== undefined) {

                      kieliSet.add(valintakoe.kieliNimi);
                      $scope.model.valintakokees.push(valintakoe);

              }
           });
           $scope.model.kielet = kieliSet.toArray();
           console.log('KIELET : ', $scope.model.kielet);


   });


});