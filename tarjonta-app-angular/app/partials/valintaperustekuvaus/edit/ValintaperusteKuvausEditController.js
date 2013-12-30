var app = angular.module('app.kk.edit.valintaperustekuvaus.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Hakukohde','auth','config','MonikielinenTextArea']);


app.controller('ValintaperusteEditController', function($scope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde,AuthService, HakuService, $modal ,Config,$location,$timeout,TarjontaService) {

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

});