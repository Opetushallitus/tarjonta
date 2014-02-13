var app =  angular.module('app.kk.edit.hakukohde.ctrl')

app.controller('ValintakokeetController', function($scope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde,Valintakoe,dialogService, HakuService, $modal ,Config,$location) {


   var kieliSet = new buckets.Set();




   $scope.model.kielet = [];

   $scope.model.valintakokees = [];

   $scope.model.validationmsgs = [];

   if ($scope.model.hakukohde.oid !== undefined) {

       var valintaKokeetResource = Valintakoe.getAll({ hakukohdeOid : $scope.model.hakukohde.oid });


       var valintaKokeetPromise  = valintaKokeetResource.$promise;
       valintaKokeetPromise.then(function(valintakokees){

           angular.forEach(valintakokees.result,function(valintakoe){
               console.log('GOT VALINTAKOE: ' , valintakoe);
               addValintakoeToList(valintakoe);
           });




       });

   }



  var addValintakoeToList = function(valintakoe) {
      if (valintakoe !== undefined) {
          checkForExistingValintaKoe(valintakoe);
          kieliSet.add(valintakoe.kieliNimi);
          $scope.model.valintakokees.push(valintakoe);

      }
      $scope.model.kielet = kieliSet.toArray();
  };

  var checkForExistingValintaKoe = function(valintakoe) {
      var foundValintakoe;
      angular.forEach($scope.model.valintakokees,function(loopValintakoe){

          if (loopValintakoe.oid === valintakoe.oid) {
             foundValintakoe = loopValintakoe;
          }

      });

      if (foundValintakoe !== undefined) {
          var index = $scope.model.valintakokees.indexOf(foundValintakoe);
          $scope.model.valintakokees.splice(index,1);
      }
  };

  var addToValintakokees = function(valintakoe) {

      var valintakoeFound = false;
      var foundValintakoe = undefined;
      angular.forEach($scope.model.valintakokees,function(loopValintakoe){
           if (loopValintakoe.oid === valintakoe.oid) {
               valintakoeFound = true;
               foundValintakoe = valintakoeFound;
           }
      });

      if (!valintakoeFound) {
          $scope.model.valintakokees.push(valintakoe);
      } else {
          var index  = $scope.model.valintakokees.indexOf(foundValintakoe);
          $scope.model.valintakokees.splice(index,1);
          $scope.model.valintakokees.push(valintakoe);

      }


  };

    $scope.model.poistaValintakoe = function(valintakoe) {

        var texts = {
            title: LocalisationService.t("hakukohde.valintakokeet.list.remove.title"),
            description: LocalisationService.t("hakukohde.valintakokeet.list.remove.desc"),
            ok: LocalisationService.t("ok"),
            cancel: LocalisationService.t("cancel")
        };

        var d = dialogService.showDialog(texts);

        d.result.then(function(data){
            if ("ACTION" === data) {
                var index =  $scope.model.valintakokees.indexOf(valintakoe);
                $scope.model.valintakokees.splice(index,1);
                valintakoe.hakukohdeOid = $scope.model.hakukohde.oid;
                valintakoe.valintakoeOid = valintakoe.oid;
                console.log('REMOVING VALINTAKOE :',valintakoe);
                var valintakoeResource = new Valintakoe(valintakoe);
                valintakoeResource.$delete();
            }
        });

       /* var index =  $scope.model.valintakokees.indexOf(valintakoe);
        $scope.model.valintakokees.splice(index,1);
        valintakoe.hakukohdeOid = $scope.model.hakukohde.oid;
        valintakoe.valintakoeOid = valintakoe.oid;
        console.log('REMOVING VALINTAKOE :',valintakoe);
        var valintakoeResource = new Valintakoe(valintakoe);
        valintakoeResource.$delete();
        */
    };


   $scope.model.muokaaValintakoetta = function(valintakoe) {

       var modalInstance = $modal.open({
           templateUrl: 'partials/hakukohde/edit/valintakoeModal.html',
           controller: 'ValintakoeModalInstanceController',
           windowClass: 'valintakoe-modal',
           resolve: {
               valintakoe: function () {
                   return valintakoe;
               }
           }
       });

       modalInstance.result.then(function (selectedItem) {

              selectedItem.hakukohdeOid =  $scope.model.hakukohde.oid;
              console.log('SELECTED VALINTAKOE : ', selectedItem);
              var valintakoeResource = new Valintakoe(selectedItem);
           if (selectedItem.oid === undefined) {
              var returnResource = valintakoeResource.$save();
              returnResource.then(function(valintakoe){

                  addValintakoeToList(valintakoe.result);



              });
           } else {
               var returnResource =  valintakoeResource.$update();
               returnResource.then(function(valintakoe) {
                   addValintakoeToList(valintakoe.result);

               });
           }


       }, function () {
           $log.info('Modal dismissed at: ' + new Date());
       });
   };


   $scope.model.newValintakoe = function(){

       $scope.model.muokaaValintakoetta();

   };

});

/*
 *
 *
 * Valintakoe modal controller
 *
 *
 */

app.controller('ValintakoeModalInstanceController', function($scope, $modalInstance,LocalisationService,Koodisto,valintakoe) {

    $scope.model = {};

    $scope.model.validationmsgs = [];

    $scope.model.showAlert = false;

    $scope.model.selectedAjankohta = {
        osoite : {}
    };


    var selectedKieli = undefined;

    //Koodisto helper methods
    var findKoodiWithArvo = function(koodi,koodis)  {


        console.log('Trying to find with : ',koodi);
        console.log('From :', koodis.length);
        var foundKoodi;

        angular.forEach(koodis,function(koodiLoop){
            if (koodiLoop.koodiArvo === koodi){
                foundKoodi = koodiLoop;
            }
        });


        return foundKoodi;
    };

    var findKoodiWithUri = function(koodi,koodis)  {


        var foundKoodi;

        angular.forEach(koodis,function(koodiLoop){
            if (koodiLoop.koodiUri === koodi){
                foundKoodi = koodiLoop;
            }
        });


        return foundKoodi;
    };

    var koodistoPromise = Koodisto.getAllKoodisWithKoodiUri('posti','FI');

    koodistoPromise.then(function(koodisParam){
        $scope.model.koodis = koodisParam;

        if ($scope.model.selectedAjankohta.osoite.postinumero !== undefined) {

            var koodi =  findKoodiWithUri(postinumero,$scope.model.koodis);

            $scope.model.postinumeroarvo.arvo = koodi.koodiArvo;
        }
    });

    $scope.model.onKieliTypeAheadChange = function() {
        var koodi = findKoodiWithArvo($scope.model.selectedAjankohta.osoite.postinumeroArvo,$scope.model.koodis);

        $scope.model.selectedAjankohta.osoite.postinumeroArvo = koodi.koodiArvo;
        $scope.model.selectedAjankohta.osoite.postinumero = koodi.koodiUri;
        $scope.model.selectedAjankohta.osoite.postitoimipaikka = koodi.koodiNimi;

    };


    $scope.model.translations = {
        title : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.otsikko'),
        kuvausKieli : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.kuvauskieli'),
        valintakoeNimi : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.valintakoenimi'),
        valintakoeKuvaus : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.kuvaus'),
        valintakoeAjankohtaSijainti : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.sijainti'),
        valintakoeAjankohtaAika : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.aika'),
        valintakoeAjankohtaLisatieto : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.lisatieto'),
        valintakoeAjankohtaLisaa : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.lisaa'),
        valintakoeAjankohtaTauluTitle : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.table.title'),
        valintakoeAjankohtaTauluSijainti : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.table.sijaint'),
        valintakoeAjankohtaTauluAjankohta : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.table.aika'),
        valintakoeAjankohtaTauluLisatiedot : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.table.lisatietoja'),
        valintakoeAjankohtaTauluMuokkaaBtn : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.table.muokkaa'),
        valintakoeAjankohtaTauluPoistaBtn : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.table.poista'),
        ok : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ok'),
        cancel : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.cancel')
    }


    $scope.model.koodistoComboCallback  = function(kieli) {
        selectedKieli = kieli;
    };

    if (valintakoe !== undefined) {
        $scope.model.valintakoe = valintakoe;
    } else {
        $scope.model.valintakoe = {
            valintakoeAjankohtas : [],
            valintakokeenKuvaus : {

            }
        };
    }

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.model.muokkaaAjankohtaa = function(valintakoeAjankohta) {
        $scope.model.removeAjankohtaFromArray(valintakoeAjankohta);
         $scope.model.selectedAjankohta = valintakoeAjankohta;
    }

    $scope.model.removeAjankohtaFromArray = function(valintakoeAjankohta) {

        var index = $scope.model.valintakoe.valintakoeAjankohtas.indexOf(valintakoeAjankohta);
        $scope.model.valintakoe.valintakoeAjankohtas.splice(index,1);

    }

    $scope.lisaaTiedot = function() {

        $scope.model.valintakoe.valintakoeAjankohtas.push($scope.model.selectedAjankohta);

        $scope.model.selectedAjankohta = {
            osoite : {}
        };

    };

   var validateValintakoe = function(){
       $scope.model.validationmsgs.splice(0,$scope.model.validationmsgs.length);
       if (selectedKieli === undefined) {
           $scope.model.validationmsgs.push(LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.kieli.req.msg'));

       }

       if ($scope.model.valintakoe.valintakoeAjankohtas === undefined || $scope.model.valintakoe.valintakoeAjankohtas.length < 1) {
           $scope.model.validationmsgs.push(LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.yksi.valintakoeaika.req.msg'));

       }

       if ($scope.model.validationmsgs.length > 0) {
           return false;
       }

       return true;
   };


    $scope.save = function() {
            if (validateValintakoe()) {

                $scope.model.valintakoe.kieliNimi = selectedKieli.koodiNimi;
                $scope.model.valintakoe.valintakokeenKuvaus.nimi = selectedKieli.koodiNimi;
                $scope.model.valintakoe.valintakokeenKuvaus.arvo  = selectedKieli.koodiArvo;
                $scope.model.valintakoe.valintakokeenKuvaus.versio = selectedKieli.koodiVersio;


                $scope.model.valintakoe.valintakokeenKuvaus.uri = $scope.model.valintakoe.kieliUri;
                $modalInstance.close($scope.model.valintakoe);
            } else {
                $scope.model.showAlert = true;
            }

    };


});