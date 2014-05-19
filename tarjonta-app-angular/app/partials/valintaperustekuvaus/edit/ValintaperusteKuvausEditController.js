var app = angular.module('app.kk.edit.valintaperustekuvaus.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Kuvaus','auth','config','MonikielinenTextArea']);


app.controller('ValintaperusteEditController', function($scope,$rootScope,$route,$q, LocalisationService, OrganisaatioService ,Koodisto,Kuvaus,AuthService, $modal ,Config,$location,$timeout,YhteyshenkiloService) {

  /*

        --------------> Variable initializations

     */

    var commonExceptionMsgKey = "tarjonta.common.unexpected.error.msg";

  $scope.model = {};

 $scope.model.years = [];

  $scope.model.validationmsgs = [];

  $scope.model.valintaperustekuvaus = {};

  $scope.model.valintaperustekuvaus.kuvauksenTyyppi = $route.current.params.kuvausTyyppi;

  $scope.model.valintaperustekuvaus.organisaatioTyyppi  = $route.current.params.oppilaitosTyyppi;

  //var kuvausId = $route.current.params.kuvausId;

  $scope.model.valintaperustekuvaus.kuvaukset = {};

  $scope.formControls = {}; // controls-layouttia varten

  $scope.model.userLang;

  $scope.model.showError = false;

  $scope.model.showSuccess = false;

  $scope.model.nimiValidationFailed = false;



  /*

        -----------------> Helper and initialization functions etc.

     */

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

      if ($scope.model.valintaperustekuvaus.vuosi === undefined) {
          $scope.model.valintaperustekuvaus.vuosi = currentYear;
      }
      $scope.model.years.sort();

  };

    var removeEmptyKuvaukses = function() {

      for (var langKey in $scope.model.valintaperustekuvaus.kuvaukset) {

          if ($scope.model.valintaperustekuvaus.kuvaukset[langKey].length < 1) {

              delete $scope.model.valintaperustekuvaus.kuvaukset[langKey];

          }

      }

    };

    var validateForm= function() {

        var retVal = true;
        var errorMsgs = [];

        if(!isNamesValid()) {

            errorMsg = {
                errorMessageKey : "valintaperustekuvaus.validation.name.missing.exception"
            };
            errorMsgs.push(errorMsg);

            $scope.model.nimiValidationFailed = true;

            retVal = false;

        }

        if (!isKuvauksesValid()) {

            errorMsg = {
                errorMessageKey : "valintaperustekuvaus.validation.kuvaus.missing.exception"
            };
            errorMsgs.push(errorMsg);

            $scope.model.nimiValidationFailed = true;

            retVal = false;

        }

        if (!$scope.valintaPerusteForm.$valid) {
            retVal = false;
        }

        if (errorMsgs.length > 0) {
            showError(errorMsgs);
        }

        return retVal;
    };

    var resetErrorMsgs = function() {


        $scope.model.validationmsgs = [];


        $scope.model.nimiValidationFailed = false;
        $scope.model.showError = false;

    };

    var isNamesValid  = function() {

      ;

        for(var i in $scope.model.valintaperustekuvaus.kuvauksenNimet){

            if ($scope.model.valintaperustekuvaus.kuvauksenNimet[i] && $scope.model.valintaperustekuvaus.kuvauksenNimet[i].trim().length > 1) {
                return true;
            }


        }
        return false;
    };

    var isKuvauksesValid = function() {

        for (var langKey in $scope.model.valintaperustekuvaus.kuvaukset) {

            if ($scope.model.valintaperustekuvaus.kuvaukset[langKey] && $scope.model.valintaperustekuvaus.kuvaukset[langKey].trim().length > 1) {
                return true;
            }

        }

        return false;

    };


    var showError = function(errorArray) {
        angular.forEach(errorArray,function(error) {

        	var p = $scope.model.validationmsgs.indexOf(error.errorMessageKey);
        	if (p==-1) {
                $scope.model.validationmsgs.push(error.errorMessageKey);
        	}
            
        });

    }


    var showCommonUnknownErrorMsg = function() {

        var errors = [];

        var error = {};

        error.errorMessageKey =  commonExceptionMsgKey;

        errors.push(error);

        showError(errors);

    }

    var createFormattedDateString = function(date) {

        return moment(date).format('DD.MM.YYYY HH:mm');

    }

  var initialializeForm = function() {


      $scope.model.userLang = AuthService.getLanguage();


      if ($route.current.locals.resolvedValintaPerusteKuvaus !== undefined ) {

         $scope.model.valintaperustekuvaus =  $route.current.locals.resolvedValintaPerusteKuvaus.result;

          if ($route.current.locals.action !== undefined && $route.current.locals.action === "COPY") {

              $scope.model.valintaperustekuvaus.kuvauksenTunniste = undefined;
          }

         if ($scope.model.valintaperustekuvaus.modifiedBy !== undefined) {



             if ($scope.model.valintaperustekuvaus.created === undefined) {
                 $scope.model.valintaperustekuvaus.created  = $scope.model.valintaperustekuvaus.modified;
             }


         }

      }  else {
          console.log('DID NOT GET VALINTAPERUSTEKUVAUS');
      }

  };

  /*
        ------------------> Run initialization functions
  */

  getYears();
  initialializeForm();

  /*

        ---------------->  Click etc. handlers

     */

    $scope.model.saveValmis = function(){



        resetErrorMsgs();


        if (validateForm()) {

            removeEmptyKuvaukses();

            $scope.model.valintaperustekuvaus.modifiedBy = AuthService.getUserOid();




            if ($scope.model.valintaperustekuvaus.kuvauksenTunniste === undefined) {

                var resultPromise = Kuvaus.insertKuvaus($scope.model.valintaperustekuvaus.kuvauksenTyyppi,$scope.model.valintaperustekuvaus);
                resultPromise.then(function(data){
                    if (data.status === "OK") {

                        $scope.model.valintaperustekuvaus = data.result;
                        $scope.model.showSuccess = true;
                        $scope.model.valintaperustekuvaus.modified  = data.result.modified;
                    } else {
                        showError(data.errors);
                        console.log('DID NOT GET OK : ', data);
                    }
                },function(error) {
                   showCommonUnknownErrorMsg();
                });
            } else {

                var resultPromise = Kuvaus.updateKuvaus($scope.model.valintaperustekuvaus.kuvauksenTyyppi,$scope.model.valintaperustekuvaus);
                resultPromise.then(function(data){

                    if (data.status === "OK") {
                        $scope.model.valintaperustekuvaus.modified  = data.result.modified;
                        $scope.model.showSuccess = true;
                    } else {
                        showError(data.errors);
                    }
                },function(error) {
                    showCommonUnknownErrorMsg();
                });

            }


        }   else {
            $scope.model.showError = true;
            $scope.model.showSuccess = false;
        }




    };


    $scope.model.takaisin = function() {

        window.history.back();
       // var oriUri = "/valintaPerusteKuvaus/search";
       // $location.path(oriUri);

    };

    $scope.model.canSaveVpk = function() {

        if ($scope.valintaPerusteForm !== undefined) {
            return $scope.valintaPerusteForm.$valid && validateForm();
        } else {
            return false;
        }

    };

    $scope.getNimetKey = function() {
        return $scope.model.valintaperustekuvaus.kuvauksenTunniste === undefined  ? 'valintaperustekuvaus.edit.create.msg' : 'valintaperustekuvaus.edit.update.msg';
    }

    $scope.getNimet = function() {
        return "";
    }

});