var app = angular.module('app.kk.search.valintaperustekuvaus.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Kuvaus','auth','config','ResultsTable']);

app.controller('ValintaperusteSearchController', function($scope,$rootScope,$route,$q,LocalisationService,Koodisto,Kuvaus,AuthService,$location,dialogService) {




    var oppilaitosTyyppi = $route.current.params.oppilaitosTyyppi;

    console.log('GOT OPPILAITOSTYYPPI : ', oppilaitosTyyppi);

    var oppilaitosKoodistoUri = "oppilaitostyyppi";

    var kausiKoodistoUri = "kausi";

    $scope.model = {};

    $scope.model.searchSpec = {};

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

    var localizeKuvausNames = function(kuvaukses) {

        angular.forEach(kuvaukses,function(kuvaus){

            for (var prop in kuvaus.kuvauksenNimet) {
                if (kuvaus.kuvauksenNimet.hasOwnProperty(prop)) {

                    if (prop.indexOf($scope.model.userLang)) {
                        kuvaus.kuvauksenNimi = kuvaus.kuvauksenNimet[prop];
                    }

                }
            }

        });

    };

    var removeKuvausFromArray = function(kuvaus) {




        if (kuvaus.tyyppi === $scope.model.kuvaustyyppis[1])  {
            var index = $scope.model.sorat.indexOf(kuvaus);
            $scope.model.sorat.splice(index,1);
        } else if (kuvaus.tyyppi === $scope.model.kuvaustyyppis[0]) {
            var index = $scope.model.valintaperusteet.indexOf(kuvaus);
            $scope.model.valintaperusteet.splice(index,1);
        }


    };

    var removeKuvaus = function(kuvaus) {


        var removedKuvausPromise = Kuvaus.removeKuvausWithId(kuvaus.tunniste);
        removedKuvausPromise.then(function(removedKuvaus){
            if (removedKuvaus.status === "OK")  {

                removeKuvausFromArray(kuvaus);
            }

        });
    };

    /*

        ----------> Controller "initialization functions"

     */

     //getKuvaukses();

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

    $scope.removeKuvaus = function(kuvaus) {

        var texts = {
            title: LocalisationService.t("valintaperuste.list.remove.title"),
            description: LocalisationService.t("valintaperuste.list.remove.desc"),
            ok: LocalisationService.t("ok"),
            cancel: LocalisationService.t("cancel")
        };
        var d = dialogService.showDialog(texts);

        d.result.then(function(data){
            if ("ACTION" === data) {
                removeKuvaus(kuvaus);
            }
        });



    }

    $scope.search = function() {

        angular.forEach($scope.model.kuvaustyyppis,function(tyyppi){

            var searchPromise = Kuvaus.findKuvauksesWithSearchSpec($scope.model.searchSpec,tyyppi);

            searchPromise.then(function(resultData){

                if (resultData.status === "OK") {
                    if (tyyppi === $scope.model.kuvaustyyppis[0]) {

                        $scope.model.valintaperusteet = [];

                        $scope.model.valintaperusteet.push.apply($scope.model.valintaperusteet,resultData.result);

                        localizeKuvausNames($scope.model.valintaperusteet);

                    } else if (tyyppi === $scope.model.kuvaustyyppis[1]) {

                        $scope.model.sorat = [];

                        $scope.model.sorat.push.apply($scope.model.sorat,resultData.result);

                        localizeKuvausNames($scope.model.sorat);

                    }
                };

            });

        });




    }

    $scope.valintaPerusteOptions = function() {
        var ret = [];

        ret.push({  url : "#", title : "Test" });

        return ret;
    }

});
