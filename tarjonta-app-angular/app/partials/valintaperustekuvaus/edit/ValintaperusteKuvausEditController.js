var app = angular.module('app.kk.edit.valintaperustekuvaus.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Hakukohde','auth','config','MonikielinenTextArea']);


app.controller('ValintaperusteEditController', function($scope,$rootScope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde,AuthService, HakuService, $modal ,Config,$location,$timeout,TarjontaService) {

  /*

        --------------> Variable initializations

     */

  $scope.model = {};

  $scope.model.valintaperustekuvaus = {};

  $scope.model.valintaperustekuvaus.kuvaukset = {};

  $scope.formControls = {}; // controls-layouttia varten


  $scope.model.showError = false;

  $scope.model.showSuccess = false;

  $scope.model.types = [

      {
       title : "valintaperustekuvaus.type.title",
       type : "valintaperustekuvaus"
      },
      {
          title : "sora.type.title",
          type : "SORA"
      }
  ];

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
    //TODO: Find out why you can in the template refer variable vpkType.type but in controller if you refer to it as $scope.vpkType.type it does not work
    //if this can be solved then following workaround function can be removed
    $scope.model.setType = function(type) {
        $scope.model.valintaperustekuvaus.kuvauksenTyyppi = type;

    }



});