var app = angular.module('app.kk.edit.valintaperustekuvaus.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Kuvaus','auth','config','MonikielinenTextArea']);


app.controller('ValintaperusteEditController', function($scope,$rootScope,$route,$q, LocalisationService, OrganisaatioService ,Koodisto,Kuvaus,AuthService, $modal ,Config,$location,$timeout,YhteyshenkiloService) {

  /*

        --------------> Variable initializations

     */

  $scope.model = {};

  $scope.model.years = [];

  $scope.model.valintaperustekuvaus = {};

  $scope.model.valintaperustekuvaus.kuvauksenTyyppi = $route.current.params.kuvausTyyppi;

  $scope.model.valintaperustekuvaus.organisaatioTyyppi  = $route.current.params.oppilaitosTyyppi;

  //var kuvausId = $route.current.params.kuvausId;

  $scope.model.valintaperustekuvaus.kuvaukset = {};

  $scope.formControls = {}; // controls-layouttia varten


  $scope.model.showError = false;

  $scope.model.showSuccess = false;



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

    var createFormattedDateString = function(date) {

        return moment(date).format('DD.MM.YYYY HH:mm');

    }

  var initialializeForm = function() {

      if ($route.current.locals.resolvedValintaPerusteKuvaus !== undefined ) {

         $scope.model.valintaperustekuvaus =  $route.current.locals.resolvedValintaPerusteKuvaus.result;



         if ($scope.model.valintaperustekuvaus.modifiedBy !== undefined) {



             if ($scope.model.valintaperustekuvaus.created === undefined) {
                 $scope.model.valintaperustekuvaus.created  = $scope.model.valintaperustekuvaus.modified;
             }




             //console.log('VIIM PAIVITYS INFO : ', $scope.model.);

             var usrPromise =  YhteyshenkiloService.haeHenkilo($scope.model.valintaperustekuvaus.viimPaivittajaOid);

             usrPromise.then(function(data){

                 if (data !== undefined) {

                     var paivittaja = data.etunimet + " " + data.sukunimi;

                     $scope.model.valintaperustekuvaus.modifiedBy = paivittaja;

                     console.log('GOT DATA FROM HENKILOSERVICE : ', data);
                 }  else {

                     $scope.model.valintaperustekuvaus.modifiedBy = undefined;

                 }


             });
         }

      }  else {
          console.log('DID NOT GET VALINTAPERUSTEKUVAUS');
      }

     /* if (kuvausId !== undefined && kuvausId !== "NEW") {
          var kuvausPromise = Kuvaus.findKuvausWithId(kuvausId);
          kuvausPromise.then(function(kuvausResult){
              if (kuvausResult.status === "OK" ){
                  console.log("FOUND KUVAUS : ", kuvausResult.result);
                  $scope.model.valintaperustekuvaus = kuvausResult.result;
              }


          });
      }  */

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


       $scope.model.valintaperustekuvaus.modifiedBy = AuthService.getUserOid();




       if ($scope.model.valintaperustekuvaus.kuvauksenTunniste === undefined) {

           var resultPromise = Kuvaus.insertKuvaus($scope.model.valintaperustekuvaus.kuvauksenTyyppi,$scope.model.valintaperustekuvaus);
           resultPromise.then(function(data){
               if (data.status === "OK") {
                   $scope.model.showSuccess = true;
                   $scope.model.valintaperustekuvaus.modified  = data.result.modified;
               } else {
                   //TODO: Do what ?
               }
           });
       } else {

           var resultPromise = Kuvaus.updateKuvaus($scope.model.valintaperustekuvaus.kuvauksenTyyppi,$scope.model.valintaperustekuvaus);
           resultPromise.then(function(data){
               console.log('GOT DATA : ', data);
               if (data.status === "OK") {
                   $scope.model.valintaperustekuvaus.modified  = data.result.modified;
                   $scope.model.showSuccess = true;
               } else {
                   //TODO: Do what ?
               }
           });

       }



    };



    $scope.model.canSaveVpk = function() {
      return true;
    };





});