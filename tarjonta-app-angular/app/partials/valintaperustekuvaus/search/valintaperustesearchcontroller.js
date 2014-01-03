var app = angular.module('app.kk.search.valintaperustekuvaus.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Kuvaus','auth','config','ResultsTable']);

app.controller('ValintaperusteSearchController', function($scope,$rootScope,$route,$q,LocalisationService,Koodisto,Kuvaus) {


    $scope.model = {};


    $scope.valintaperusteColumns =['kuvauksenNimet','organisaatioTyyppi','vuosikausi'];

    $scope.model.test = "HELLO FROM CONTROLLER";


});
