var app = angular.module('app.kk.search.valintaperustekuvaus.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Kuvaus','auth','config','ResultsTable']);

app.controller('ValintaperusteSearchController', function($scope,$rootScope,$route,$q,LocalisationService,Koodisto,Kuvaus,AuthService,$location,dialogService,OrganisaatioService,CommonUtilService,$modal,$log) {


    var oppilaitosKoodistoUri = "oppilaitostyyppi";

    var kausiKoodistoUri = "kausi";

    $scope.model = {};

    $scope.model.searchSpec = {};

    $scope.model.kuvaustyyppis = ["valintaperustekuvaus","SORA"];

    $scope.model.valintaperusteet = [];

    $scope.model.sorat = [];

    $scope.model.years = [];

    $scope.model.userLang  =  AuthService.getLanguage();

    $scope.model.userOrgTypes = [];

    $scope.valintaperusteColumns =['kuvauksenNimi','organisaatioTyyppi','vuosikausi'];

    var oppilaitosTyyppiPromises = [];

    /*

        -------------> "Inner" functions and "Helper" function declarations

     */

    var getUserOrgs = function() {
        $log.info('AUTH SERVICE : ', AuthService.getUsername());
        $log.info('AUTH SERVICE FIRST NAME : ', AuthService.getFirstName());
        if (!AuthService.isUserOph())   {

            $log.info('USER IS NOT OPH, GETTING ORGANIZATIONS....');
            $log.info('USER ORGANIZATIONS : ' , AuthService.getOrganisations());
            OrganisaatioService.etsi({oidRestrictionList:AuthService.getOrganisations()})
                .then(function(data){
                  getOppilaitosTyyppis(data.organisaatiot);

                });
        }


    };


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

        if ($scope.model.searchSpec.vuosi === undefined) {
            $scope.model.searchSpec.vuosi = currentYear;
        }
        $scope.model.years.sort();

    };


    var checkForUserOrgs = function() {

        //If user has not selected anything for oppilaitostyyppi and has oppilaitostyyppis in userOrgTypes, then
        //it is safe to assume that user is not ophadmin so restrict the query with first available oppilaitostyyppi
        $log.info('SEARCH SPEC OPPILAITOSTYYPPI : ', $scope.model.searchSpec.oppilaitosTyyppi);
        if ($scope.model.searchSpec.oppilaitosTyyppi === undefined && $scope.model.userOrgTypes.length  > 0 ) {
            $scope.model.searchSpec.oppilaitosTyyppi =  $scope.model.userOrgTypes[0];
            $log.info(' USER ORG TYPE SET :  ', $scope.model.userOrgTypes[0]);
        }

    }

    var showCreateNewDialog = function(vpkTyyppiParam) {

          var modalInstance = $modal.open({
            templateUrl: 'partials/valintaperustekuvaus/search/uusi-valintaperuste-dialog.html',
            controller: 'LuoUusiValintaPerusteDialog',
            windowClass: 'valintakoe-modal',
            resolve: {
                filterUris : function() {
                    return $scope.model.userOrgTypes;
                },
                selectedUri : function() {
                    return $scope.model.searchSpec.oppilaitosTyyppi;
                },
                Tyyppi : function() {
                    return vpkTyyppiParam;
                }
            }
        });

        modalInstance.result.then(function(selectedOrgType){
             if (selectedOrgType !== undefined) {
                 var kuvausEditUri = "/valintaPerusteKuvaus/edit/" +selectedOrgType + "/"+vpkTyyppiParam +"/NEW";
                 $location.path(kuvausEditUri);
             }
        });
    };

    var resolveKausi = function(kuvaukset) {

        var resolvedKuvaukset = [];

        angular.forEach(kuvaukset,function(kuvaus){
            var kausiKoodiPromise = Koodisto.getKoodi(kausiKoodistoUri,kuvaus.kausi,$scope.model.userLang);
            kausiKoodiPromise.then(function(kausiKoodi){
                 kuvaus.vuosikausi =    kuvaus.vuosi + " " + kausiKoodi.koodiNimi;

            });

        });

    };

    var resolveOppilaitosTyyppi = function(kuvaukset) {

       angular.forEach(kuvaukset,function(kuvaus){
           var oppilaitosTyyppiKoodiPromise = Koodisto.getKoodi(oppilaitosKoodistoUri,kuvaus.organisaatioTyyppi,$scope.model.userLang);
           oppilaitosTyyppiKoodiPromise.then(function(oppilaitosTyyppiKoodi){
               kuvaus.organisaatioTyyppi = oppilaitosTyyppiKoodi.koodiNimi;
           });
       });

    };

    var getOppilaitosTyyppis = function(organisaatiot) {

        angular.forEach(organisaatiot, function(organisaatio) {
            var oppilaitosTyypitPromise = CommonUtilService.haeOppilaitostyypit(organisaatio);
            oppilaitosTyyppiPromises.push(oppilaitosTyypitPromise);

        });


        //Resolve all promises and filter oppilaitostyyppis with user types
        $q.all(oppilaitosTyyppiPromises).then(function(data){

            $scope.model.userOrgTypes =  removeHashAndVersion(data);
        });
    };

    var removeHashAndVersion = function(oppilaitosTyyppis) {

        var oppilaitosTyyppisWithOutVersion = new buckets.Set();

        angular.forEach(oppilaitosTyyppis,function(oppilaitosTyyppiUri) {
            angular.forEach(oppilaitosTyyppiUri,function(oppilaitosTyyppiUri){
                var splitStr = oppilaitosTyyppiUri.split("#");
                oppilaitosTyyppisWithOutVersion.add(splitStr[0]);
            });

        });
       return oppilaitosTyyppisWithOutVersion.toArray();
    };

    var localizeKuvausNames = function(kuvaukses) {

        angular.forEach(kuvaukses,function(kuvaus){

            for (var prop in kuvaus.kuvauksenNimet) {

                if (kuvaus.kuvauksenNimet.hasOwnProperty(prop)) {

                    if (prop.indexOf($scope.model.userLang)) {
                        if (kuvaus.kuvauksenNimet[prop] && kuvaus.kuvauksenNimet[prop].trim().length > 1) {
                            kuvaus.kuvauksenNimi = kuvaus.kuvauksenNimet[prop];
                        }

                    }

                }
            }



        });

    };

    var removeKuvausFromArray = function(kuvaus) {




        if (kuvaus.kuvauksenTyyppi === $scope.model.kuvaustyyppis[1])  {
            var index = $scope.model.sorat.indexOf(kuvaus);
            $scope.model.sorat.splice(index,1);
        } else if (kuvaus.kuvauksenTyyppi === $scope.model.kuvaustyyppis[0]) {
            var index = $scope.model.valintaperusteet.indexOf(kuvaus);
            $scope.model.valintaperusteet.splice(index,1);
        }


    };

    var removeKuvaus = function(kuvaus) {


        var removedKuvausPromise = Kuvaus.removeKuvausWithId(kuvaus.kuvauksenTunniste);
        removedKuvausPromise.then(function(removedKuvaus){
            console.log('REMOVED KUVAUS: ' , removedKuvaus);

            if (removedKuvaus.status === "OK")  {

                removeKuvausFromArray(kuvaus);
            }

        });
    };

    /*

        ----------> Controller "initialization functions"

     */

     //getKuvaukses();
    getUserOrgs();
    getYears();

    /*

        ----------> Controller "event handlers and listeners" a

     */

    $scope.selectKuvaus = function(kuvaus) {

        console.log('KUVAUS : ', kuvaus);

        if (kuvaus.organisaatioTyyppi !== undefined) {
            console.log('KUVAUS: ', kuvaus);
            var kuvausEditUri = "/valintaPerusteKuvaus/edit/" +kuvaus.organisaatioTyyppi + "/"+kuvaus.kuvauksenTyyppi+"/"+kuvaus.kuvauksenTunniste;
            console.log('KUVAUS EDIT URI : ', kuvausEditUri);
            $location.path(kuvausEditUri);
        }

    };

    $scope.createNew = function(kuvausTyyppi) {

        if ($scope.model.userOrgTypes.length > 0 && $scope.model.userOrgTypes.length < 2) {
            var kuvausEditUri = "/valintaPerusteKuvaus/edit/" +$scope.model.userOrgTypes[0] + "/"+kuvausTyyppi +"/NEW";
            $location.path(kuvausEditUri);
        } else {
            showCreateNewDialog(kuvausTyyppi);
        }


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
            if (data) {
                removeKuvaus(kuvaus);
            }
        });



    }

    $scope.copyKuvaus = function(kuvaus) {

        var kuvausEditUri = "/valintaPerusteKuvaus/edit/" +$scope.model.userOrgTypes[0] + "/"+kuvaus.kuvauksenTyyppi +"/"+kuvaus.kuvauksenTunniste+"/COPY";
        $location.path(kuvausEditUri);
    }

    $scope.search = function() {

        checkForUserOrgs();
        angular.forEach($scope.model.kuvaustyyppis,function(tyyppi){

            $log.info('SEARCHING KUVAUKSES WITH : ', tyyppi);

            var searchPromise = Kuvaus.findKuvauksesWithSearchSpec($scope.model.searchSpec,tyyppi);

            $scope.model.valintaperusteet = [];

            $scope.model.sorat = [];

            searchPromise.then(function(resultData){

                $log.info('GOT KUVAUS RESULT : ', resultData);

                if (resultData.status === "OK") {
                    if (tyyppi === $scope.model.kuvaustyyppis[0]) {



                        $scope.model.valintaperusteet.push.apply($scope.model.valintaperusteet,resultData.result);

                        localizeKuvausNames($scope.model.valintaperusteet);

                        resolveOppilaitosTyyppi($scope.model.valintaperusteet);

                        resolveKausi($scope.model.valintaperusteet);


                    } else if (tyyppi === $scope.model.kuvaustyyppis[1]) {



                        $scope.model.sorat.push.apply($scope.model.sorat,resultData.result);

                        localizeKuvausNames($scope.model.sorat);

                        resolveOppilaitosTyyppi($scope.model.sorat);

                        resolveKausi($scope.model.sorat);

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

app.controller('LuoUusiValintaPerusteDialog',function($scope,$modalInstance,LocalisationService,Koodisto,filterUris, selectedUri, Tyyppi){

    /*

        ------> Variable declaration etc.

     */


    $scope.dialog = {
        type : Tyyppi
    };

    $scope.dialog.userOrgTypes = [];

    /*

        ------> Private functions

     */

    var initializeDialog = function() {

           if (filterUris !== undefined && filterUris.length > 0) {

               $scope.dialog.userOrgTypes = filterUris;

           }

           if (selectedUri !== undefined) {

               $scope.dialog.selectedType = selectedUri;

           }
    };

    /*

     ----------> Controller "event handlers and listeners"

     */

    $scope.onCancel = function() {
        $modalInstance.dismiss('cancel');
    };

    $scope.onOk = function() {
        $modalInstance.close($scope.dialog.selectedType);
    };

    /*

        -----> Controller initialization functions

     */
    initializeDialog();

});
