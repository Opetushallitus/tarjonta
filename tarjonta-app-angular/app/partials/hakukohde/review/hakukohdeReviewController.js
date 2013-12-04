var app = angular.module('app.kk.edit.hakukohde.review.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Hakukohde','auth','config','MonikielinenTextArea']);


app.controller('HakukohdeReviewController', function($scope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde,AuthService, HakuService, $modal ,Config,$location,$timeout,TarjontaService) {

      console.log('HAKUKOHDE REVIEW:  ', $scope.model.hakukohde);

});