var app = angular.module('app.kk.edit.valintaperustekuvaus.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Kuvaus','auth','config','MonikielinenTextArea']);


app.controller('ValintaperusteEditController', function($scope,$rootScope,$route,$q, LocalisationService, OrganisaatioService ,Koodisto,Kuvaus,AuthService, $modal ,Config,$location,$timeout) {

  /*

        --------------> Variable initializations

     */




  $scope.model = {};



  $scope.model.valintaperustekuvaus = {};

  $scope.model.valintaperustekuvaus.kuvauksenTyyppi = $route.current.params.kuvausTyyppi;

  $scope.model.valintaperustekuvaus.organisaatioTyyppi  = $route.current.params.oppilaitosTyyppi;

  $scope.model.valintaperustekuvaus.kuvaukset = {};

  $scope.formControls = {}; // controls-layouttia varten


  $scope.model.showError = false;

  $scope.model.showSuccess = false;



  /*

        -----------------> Helper functions etc.

     */



  /*

        ---------------->  Click etc. handlers

     */

    $scope.model.saveValmis = function(){

        var resultPromise = Kuvaus.insertKuvaus($scope.model.valintaperustekuvaus.kuvauksenTyyppi,$scope.model.valintaperustekuvaus);
        resultPromise.then(function(data){
          if (data.status === "OK") {
              $scope.model.showSuccess = true;
          } else {
              //TODO: Do what ?
          }
        });

    };



    $scope.model.canSaveVpk = function() {
      return true;
    };





});