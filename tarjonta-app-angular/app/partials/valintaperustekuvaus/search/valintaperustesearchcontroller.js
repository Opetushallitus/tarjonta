var app = angular.module('app.kk.search.valintaperustekuvaus.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Kuvaus','auth','config']);

app.controller('ValintaperusteSearchController', function($scope,$rootScope,$route,$q,LocalisationService,Koodisto,Kuvaus,AuthService,$location,dialogService,OrganisaatioService,CommonUtilService,$modal,$log) {

    var oppilaitosKoodistoUri = "oppilaitostyyppi";

    var kausiKoodistoUri = "kausi";

    $scope.selection = {
    		valintaperusteet: [],
    		sorat: []
    };

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

    function koodistoToMap(res) {
    	var ret = {};
    	for (var i in res) {
    		var k = res[i];
    		ret[k.koodiUri] = k.koodiNimi;
    		ret[k.koodiUri+"#"+k.koodiVersio] = k.koodiNimi;
    	}
    	return ret;
    }

    var resolveKausi = function(kuvaukset, doAfter) {

        var resolvedKuvaukset = [];

        Koodisto.getAllKoodisWithKoodiUri(kausiKoodistoUri, $scope.model.userLang).then(function(res){
        	//console.log("KAUDET",kuvaukset);
        	var koodisto = koodistoToMap(res);
        	for (var i in kuvaukset) {
        		var v = kuvaukset[i];
        		v.kausiNimi = koodisto[v.kausi] || v.kausi;
        	}
            doAfter();
        });

    };

    var resolveOppilaitosTyyppi = function(kuvaukset, doAfter) {

        Koodisto.getAllKoodisWithKoodiUri(oppilaitosKoodistoUri, $scope.model.userLang).then(function(res){
        	//console.log("OPPILAITOSTYYPIT",kuvaukset);
        	var koodisto = koodistoToMap(res);
        	for (var i in kuvaukset) {
        		var v = kuvaukset[i];
        		v.organisaatioTyyppiNimi = koodisto[v.organisaatioTyyppi] || v.organisaatioTyyppi;
        	}
            doAfter();
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

    var removeKuvaus = function(kuvaus, doAfter) {

        var modalInstance = $modal.open({
            templateUrl: 'partials/valintaperustekuvaus/remove/poista.html',
            controller: 'PoistaValintaperustekuvausCtrl',
            resolve: {
                selectedKuvaus: function() {
                    return kuvaus;
                }
            }
        });

        modalInstance.result.then(function () {
            doAfter();
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

            //$scope.model.valintaperusteet = [];
            //$scope.model.sorat = [];

            searchPromise.then(function(resultData){

                $log.info('GOT KUVAUS RESULT : ', resultData);

                if (resultData.status === "OK") {

                	localizeKuvausNames(resultData.result);
                    resolveOppilaitosTyyppi(resultData.result, function(){
                        resolveKausi(resultData.result, function(){
                            if (tyyppi === $scope.model.kuvaustyyppis[0]) {
                                $scope.model.valintaperusteet = resultData.result;
                            } else if (tyyppi === $scope.model.kuvaustyyppis[1]) {
                                $scope.model.sorat = resultData.result;
                            }
                        });
                    });
                };

            });

        });




    }

    $scope.kuvauksetGetContent = function(row, col) {
    	console.log("GET CONTENT "+col, row);
    	switch (col) {
    	case 'kausi':
    		return row.kausiNimi + " " + row.vuosi;
    	case 'oppilaitostyyppi':
    		return row.organisaatioTyyppiNimi;
		default:
			return row.kuvauksenNimi;
    	}
    }

    $scope.kuvauksetGetIdentifier = function(row) {
    	return row.kuvauksenTunniste;
    }

    $scope.kuvauksetGetLink = function(row) {
    	return "#/valintaPerusteKuvaus/edit/" +$scope.model.userOrgTypes[0] + "/"+row.kuvauksenTyyppi +"/"+row.kuvauksenTunniste;
    }

    $scope.kuvauksetGetOptions = function(row) {
    	return [{
    		title: LocalisationService.t("tarjonta.toiminnot.muokkaa"),
    		href: $scope.kuvauksetGetLink(row)
    	},{
    		title: LocalisationService.t("tarjonta.toiminnot.poista"),
    		action: function() {
    			removeKuvaus(row, row.$delete);
    		}
    	},{
    		title: LocalisationService.t("tarjonta.toiminnot.kopioi"),
    		action: function() {
    			copyKuvaus(row);
    		}
    	}
    	];
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
