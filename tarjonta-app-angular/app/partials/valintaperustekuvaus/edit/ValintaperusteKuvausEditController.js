var app = angular.module('app.kk.edit.valintaperustekuvaus.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Hakukohde','auth','config','MonikielinenTextArea']);


app.controller('ValintaperusteEditController', function($scope,$rootScope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde,AuthService, HakuService, $modal ,Config,$location,$timeout,TarjontaService) {

  /*

        --------------> Variable initializations

     */


  var soraType = "SORA";

  var vpkType = "valintaperustekuvaus";

  $scope.model = {};

  $scope.model.vpks = {};

  $scope.model.valintaperustekuvaus = {};

  $scope.model.valintaperustekuvaus.kuvaukset = {};

  $scope.formControls = {}; // controls-layouttia varten


  $scope.model.showError = false;

  $scope.model.showSuccess = false;

  $scope.model.types = [

      {
       title : "valintaperustekuvaus.type.title",
       type : vpkType
      },
      {
          title : "sora.type.title",
          type : soraType
      }
  ];

  /*

        -----------------> Helper functions etc.

     */

  var getStringFromType = function(type){

       if (type.type === soraType) {
         return soraType;
       }  else if (type.type === vpkType) {
         return vpkType;
       }

  };

  /*

        ---------------->  Click etc. handlers

     */

    $scope.model.saveLuonnos = function(){

        //$scope.model.valintaperustekuvaus.organisaatioTyyppi = $scope.parent.type.type;
        console.log('DATA TO SAVE : ', $scope.model.valintaperustekuvaus);
    };

    $scope.model.canSaveVpk = function() {
      return true;
    };


    $scope.model.typeChange = function(type) {

       var newType = getStringFromType(type);

       var currentModel =  $scope.model.vpks[newType];



       if ($scope.model.valintaperustekuvaus.kuvauksenTyyppi !== undefined) {



          if ($scope.model.valintaperustekuvaus.kuvauksenTyyppi === soraType) {

             $scope.model.vpks[soraType] = $scope.model.valintaperustekuvaus;
          } else if ($scope.model.valintaperustekuvaus.kuvauksenTyyppi === vpkType) {

              $scope.model.vpks[vpkType] = $scope.model.valintaperustekuvaus;
          }



       }

       if (currentModel === undefined) {

           currentModel = {};
           currentModel.kuvaukset = {};
           $scope.model.valintaperustekuvaus = currentModel;
           $scope.model.valintaperustekuvaus.kuvauksenTyyppi = type.type;

       } else {
           $scope.model.valintaperustekuvaus = currentModel;
       }


    }



});