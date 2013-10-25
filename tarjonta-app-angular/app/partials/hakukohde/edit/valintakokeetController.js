var app =  angular.module('app.kk.edit.hakukohde.ctrl')

app.controller('ValintakokeetController', function($scope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde,Valintakoe, HakuService, $modal ,Config,$location) {


   $scope.model.hakukohdeOid  =  $scope.model.hakukohde.oid;

   $scope.model.valintakokees = [];

   var valintaKokeetResource = Valintakoe.getAll({ oid : $scope.model.hakukohdeOid });
    console.log('LOADING VALINTAKOKEES');

    var valintaKokeetPromise  = valintaKokeetResource.$promise;
   valintaKokeetPromise.then(function(valintakokees){
       console.log('GOT KOKEES : ', valintakokees);
       if (valintakokees !== undefined) {
           angular.forEach(valintakokees,function(valintakoe){
              if (valintakoe !== undefined) {

                      $scope.model.valintakokees.push(valintakoe);

              }
           });
       }

   });


});