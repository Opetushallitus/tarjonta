var app = angular.module('app.kk.search.valintaperustekuvaus.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Kuvaus','auth','config','ResultsTable']);

app.controller('ValintaperusteSearchController', function($scope,$rootScope,$route,$q,LocalisationService,Koodisto,Kuvaus,AuthService) {


    var tyyppi = "SORA";

    var oppilaitosTyyppi = "Oppilaitos";

    $scope.model = {};

    $scope.model.valintaperusteet = [];

    $scope.model.userLang  =  AuthService.getLanguage();

    console.log('LANG : ', $scope.model.userLang);


    $scope.valintaperusteColumns =['kuvauksenNimi','organisaatioTyyppi','vuosikausi'];

    var kuvausPromise = Kuvaus.findKuvausBasicInformation(tyyppi,oppilaitosTyyppi);

    kuvausPromise.then(function(data){
        console.log('GOT FOLLOWING DATA : ', data);

        angular.forEach(data.result,function(resultObj){

            var vpkObj = {};

            for (var prop in resultObj.kuvauksenNimet) {
                if (resultObj.kuvauksenNimet.hasOwnProperty(prop)) {

                    if (prop.indexOf($scope.model.userLang)) {
                        vpkObj.kuvauksenNimi = resultObj.kuvauksenNimet[prop];
                    }

                }
            }

            vpkObj.organisaatioTyyppi = resultObj.organisaatioTyyppi;
            vpkObj.vuosikausi =  resultObj.vuosi + " " + resultObj.kausi;
            $scope.model.valintaperusteet.push(vpkObj);

        });

    });

    $scope.valintaPerusteOptions = function() {
        var ret = [];

        ret.push({  url : "#", title : "Test" });

        return ret;
    }

});
