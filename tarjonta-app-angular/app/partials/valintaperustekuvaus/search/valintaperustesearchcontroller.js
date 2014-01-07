var app = angular.module('app.kk.search.valintaperustekuvaus.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Kuvaus','auth','config','ResultsTable']);

app.controller('ValintaperusteSearchController', function($scope,$rootScope,$route,$q,LocalisationService,Koodisto,Kuvaus,AuthService,$location,dialogService) {




    var oppilaitosTyyppi = "oppilaitostyyppi_41";

    var oppilaitosKoodistoUri = "oppilaitostyyppi";

    var kausiKoodistoUri = "kausi";

    $scope.model = {};

        $scope.model.kuvaustyyppis = ["valintaperustekuvaus","SORA"];

    $scope.model.valintaperusteet = [];

    $scope.model.sorat = [];

    $scope.model.userLang  =  AuthService.getLanguage();

    $scope.valintaperusteColumns =['kuvauksenNimi','organisaatioTyyppi','vuosikausi'];

    /*

        -------------> "Inner" functions and "Helper" function declarations

     */

    var findKuvausInformation = function(kuvausTyyppi) {
        var kuvausPromise = Kuvaus.findKuvausBasicInformation( kuvausTyyppi,oppilaitosTyyppi);

        kuvausPromise.then(function(data){


            angular.forEach(data.result,function(resultObj){

                var vpkObj = {};

                vpkObj.tyyppi = kuvausTyyppi;
                vpkObj.tunniste = resultObj.kuvauksenTunniste;

                for (var prop in resultObj.kuvauksenNimet) {
                    if (resultObj.kuvauksenNimet.hasOwnProperty(prop)) {

                        if (prop.indexOf($scope.model.userLang)) {
                            vpkObj.kuvauksenNimi = resultObj.kuvauksenNimet[prop];
                        }

                    }
                }
                var oppilaitosTyyppiKoodiPromise = Koodisto.getKoodi(oppilaitosKoodistoUri,resultObj.organisaatioTyyppi,$scope.model.userLang);

                oppilaitosTyyppiKoodiPromise.then(function(oppilaitosTyyppiKoodi){
                    vpkObj.organisaatioTyyppi = oppilaitosTyyppiKoodi.koodiNimi;
                });



                var kausiKoodiPromise = Koodisto.getKoodi(kausiKoodistoUri,resultObj.kausi,$scope.model.userLang);
                kausiKoodiPromise.then(function(kausiKoodi){
                    vpkObj.vuosikausi =  resultObj.vuosi + " " + kausiKoodi.koodiNimi;
                });
                if (kuvausTyyppi === $scope.model.kuvaustyyppis[0]) {
                    $scope.model.valintaperusteet.push(vpkObj);
                } else if (kuvausTyyppi === $scope.model.kuvaustyyppis[1]) {
                    $scope.model.sorat.push(vpkObj);
                }


            });

        });
    }

    var getKuvaukses = function() {

        angular.forEach($scope.model.kuvaustyyppis,function(kuvaustyyppi){
           findKuvausInformation(kuvaustyyppi);
        });

    };

    /*

        ----------> Controller "initialization functions"

     */

     getKuvaukses();

    /*

        ----------> Controller "event handlers and listeners" a

     */

    $scope.selectKuvaus = function(kuvaus) {

        var kuvausEditUri = "/valintaPerusteKuvaus/edit/" +oppilaitosTyyppi + "/"+kuvaus.tyyppi+"/"+kuvaus.tunniste;
        $location.path(kuvausEditUri);
    };

    $scope.createNew = function(kuvausTyyppi) {
        var kuvausEditUri = "/valintaPerusteKuvaus/edit/" +oppilaitosTyyppi + "/"+kuvausTyyppi +"/NEW";
        $location.path(kuvausEditUri);
    };

    $scope.valintaPerusteOptions = function() {
        var ret = [];

        ret.push({  url : "#", title : "Test" });

        return ret;
    }

});
