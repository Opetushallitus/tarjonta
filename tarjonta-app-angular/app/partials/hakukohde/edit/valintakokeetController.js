var app =  angular.module('app.kk.edit.hakukohde.ctrl')

app.controller('ValintakokeetController', function($scope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde,Valintakoe, HakuService, $modal ,Config,$location) {


   var kieliSet = new buckets.Set();
   $scope.model.hakukohdeOid  =  $scope.model.hakukohde.oid;

   $scope.model.kielet = [];

   $scope.model.valintakokees = [];

   var valintaKokeetResource = Valintakoe.getAll({ hakukohdeOid : $scope.model.hakukohdeOid });


    var valintaKokeetPromise  = valintaKokeetResource.$promise;
   valintaKokeetPromise.then(function(valintakokees){

           angular.forEach(valintakokees.result,function(valintakoe){
              if (valintakoe !== undefined) {

                      kieliSet.add(valintakoe.kieliNimi);
                      $scope.model.valintakokees.push(valintakoe);

              }
           });
           $scope.model.kielet = kieliSet.toArray();



   });

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

        var index =  $scope.model.valintakokees.indexOf(valintakoe);
        $scope.model.valintakokees.splice(index,1);
        valintakoe.hakukohdeOid = $scope.model.hakukohdeOid;
        valintakoe.valintakoeOid = valintakoe.oid;
        console.log('REMOVING VALINTAKOE :',valintakoe);
        var valintakoeResource = new Valintakoe(valintakoe);
        valintakoeResource.$delete();

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

              selectedItem.hakukohdeOid =  $scope.model.hakukohdeOid;
              console.log('SELECTED VALINTAKOE : ', selectedItem);
              var valintakoeResource = new Valintakoe(selectedItem);
           if (selectedItem.oid === undefined) {
              valintakoeResource.$save();
           } else {
               valintakoeResource.$update();
           }


       }, function () {
           $log.info('Modal dismissed at: ' + new Date());
       });
   };


   $scope.model.newValintakoe = function(){

       $scope.model.muokaaValintakoetta();

   };

});


//Valintakoe modal controller
app.controller('ValintakoeModalInstanceController', function($scope, $modalInstance,LocalisationService,Koodisto,valintakoe) {

    $scope.model = {};

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

    $scope.lisaaTiedot = function() {

        $scope.model.valintakoe.valintakoeAjankohtas.push($scope.model.selectedAjankohta);

        $scope.model.selectedAjankohta = {
            osoite : {}
        };

    };


    $scope.save = function() {
        if (selectedKieli !== undefined) {
            $scope.model.valintakoe.kieliNimi = selectedKieli.koodiNimi;
            $scope.model.valintakoe.valintakokeenKuvaus.nimi = selectedKieli.koodiNimi;
            $scope.model.valintakoe.valintakokeenKuvaus.arvo  = selectedKieli.koodiArvo;
            $scope.model.valintakoe.valintakokeenKuvaus.versio = selectedKieli.koodiVersio;
        }

        $scope.model.valintakoe.valintakokeenKuvaus.uri = $scope.model.valintakoe.kieliUri;
        $modalInstance.close($scope.model.valintakoe);
    };


});