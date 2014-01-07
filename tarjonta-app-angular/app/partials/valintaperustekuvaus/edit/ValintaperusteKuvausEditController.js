var app = angular.module('app.kk.edit.valintaperustekuvaus.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Kuvaus','auth','config','MonikielinenTextArea']);


app.controller('ValintaperusteEditController', function($scope,$rootScope,$route,$q, LocalisationService, OrganisaatioService ,Koodisto,Kuvaus,AuthService, $modal ,Config,$location,$timeout) {

  /*

        --------------> Variable initializations

     */

  $scope.model = {};

  $scope.model.years = [];

  $scope.model.valintaperustekuvaus = {};

  $scope.model.valintaperustekuvaus.kuvauksenTyyppi = $route.current.params.kuvausTyyppi;

  $scope.model.valintaperustekuvaus.organisaatioTyyppi  = $route.current.params.oppilaitosTyyppi;

  var kuvausId = $route.current.params.kuvausId;

  $scope.model.valintaperustekuvaus.kuvaukset = {};

  $scope.formControls = {}; // controls-layouttia varten


  $scope.model.showError = false;

  $scope.model.showSuccess = false;



  /*

        -----------------> Helper and initialization functions etc.

     */

  var getYears = function() {

      var today = new Date();

      var currentYear = today.getFullYear();

      $scope.model.years.push(currentYear);

      var incrementYear = currentYear;

      var decrementYear = currentYear;

      for (var i = 0; i < 10;i++) {


          incrementYear++;

          if (i < 2) {
              decrementYear--;
              $scope.model.years.push(decrementYear);
          }



          $scope.model.years.push(incrementYear);



      }

      if ($scope.model.valintaperustekuvaus.vuosi === undefined) {
          $scope.model.valintaperustekuvaus.vuosi = currentYear;
      }
      $scope.model.years.sort();

  };

  var initialializeForm = function() {

      if (kuvausId !== undefined && kuvausId !== "NEW") {
          var kuvausPromise = Kuvaus.findKuvausWithId(kuvausId);
          kuvausPromise.then(function(kuvausResult){
              if (kuvausResult.status === "OK" ){
                  console.log("FOUND KUVAUS : ", kuvausResult.result);
                  $scope.model.valintaperustekuvaus = kuvausResult.result;
              }


          });
      }

  };

  /*
        ------------------> Run initialization functions
  */

  getYears();
  initialializeForm();

  /*

        ---------------->  Click etc. handlers

     */

    $scope.model.saveValmis = function(){


       if ($scope.model.valintaperustekuvaus.kuvauksenTunniste === undefined) {
           var resultPromise = Kuvaus.insertKuvaus($scope.model.valintaperustekuvaus.kuvauksenTyyppi,$scope.model.valintaperustekuvaus);
           resultPromise.then(function(data){
               if (data.status === "OK") {
                   $scope.model.showSuccess = true;
               } else {
                   //TODO: Do what ?
               }
           });
       } else {

           var resultPromise = Kuvaus.updateKuvaus($scope.model.valintaperustekuvaus.kuvauksenTyyppi,$scope.model.valintaperustekuvaus);
           resultPromise.then(function(data){
               if (data.status === "OK") {
                   $scope.model.showSuccess = true;
               } else {
                   //TODO: Do what ?
               }
           });

       }



    };



    $scope.model.canSaveVpk = function() {
      return true;
    };





});