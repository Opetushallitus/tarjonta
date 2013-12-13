var app =  angular.module('app.kk.edit.hakukohde.ctrl');

app.controller('LiitteetListController',function($scope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde,Liite, dialogService , HakuService, $modal ,Config,$location) {

     var kieliSet = new buckets.Set();



     $scope.model.liitteenkielet = [];

     $scope.model.liitteet = [];

    var liitteetResource = Liite.get({hakukohdeOid: $scope.model.hakukohde.oid});

    var liitteetPromise = liitteetResource.$promise;

    liitteetPromise.then(function(liitteet){

        console.log('LIITTEET GOT: ',liitteet);

        angular.forEach(liitteet.result,function(liite){
            addLiiteToLiitteet(liite);
        });


    });

    var addLiiteToLiitteet = function(liite) {
        console.log('ADDING LIITE : ', liite);
        if (liite !== undefined) {

            checkForExistingLiite(liite);

            kieliSet.add(liite.kieliNimi);

            $scope.model.liitteet.push(liite);
            console.log('LIITTEET: ' , $scope.model.liitteet);
            $scope.model.liitteenkielet = kieliSet.toArray();
            console.log('LIITTEEN KIELET: ' , $scope.model.liitteenkielet);
        }

    } ;

    var checkKieles = function() {

        kieliSet.clear();
        angular.forEach($scope.model.liitteet,function(liite){
            kieliSet.add(liite.kieliNimi);
        });
        $scope.model.liitteenkielet = kieliSet.toArray();

    }


    var removeLiiteFromList = function(liite) {
        var index = $scope.model.liitteet.indexOf(liite);
        $scope.model.liitteet.splice(index,1);
        liite.hakukohdeOid = $scope.model.hakukohde.oid;
        liite.liiteId = liite.oid;

        var liiteResource = new Liite(liite);
        liiteResource.$delete();
        checkKieles();
    };

    var checkForExistingLiite = function(liite) {

        var foundLiite;

        angular.forEach($scope.model.liitteet,function(loopLiite){

            if (loopLiite.oid === liite.oid) {
                foundLiite = loopLiite;
            }

        });

        if (foundLiite !== undefined) {

            var index = $scope.model.liitteet.indexOf(foundLiite);

            $scope.model.liitteet.splice(index,1);

        }

    };

    $scope.model.muokkaaLiitetta = function(liite) {


        var organisaationOsoite = {
            osoiterivi1 : $scope.model.hakukohde.liitteidenToimitusOsoite.osoiterivi1 ,
            postinumero : $scope.model.hakukohde.liitteidenToimitusOsoite.postinumero,
            postitoimipaikka : $scope.model.hakukohde.liitteidenToimitusOsoite.postitoimipaikka
        };

        if (liite === undefined) {
            liite = {
                liitteenToimitusOsoite : {

                    osoiterivi1 : $scope.model.hakukohde.liitteidenToimitusOsoite.osoiterivi1 ,
                    postinumero : $scope.model.hakukohde.liitteidenToimitusOsoite.postinumero,
                    postitoimipaikka : $scope.model.hakukohde.liitteidenToimitusOsoite.postitoimipaikka

                },
                liitteenKuvaus : {}
            };
        }

        var modalInstance = $modal.open({
            templateUrl: 'partials/hakukohde/edit/liiteEditModal.html',
            controller: 'LiiteModalController',
            windowClass: 'valintakoe-modal',
            resolve: {
                liite: function () {
                    return liite;
                },
                organisaationOsoite : function() {
                    return organisaationOsoite;
                }
            }
        });

        modalInstance.result.then(function(selectedItem){
             liite.hakukohdeOid = $scope.model.hakukohde.oid;
            console.log('GOT LIITE: ', selectedItem);
             var liiteResurssi  = new Liite(selectedItem);
             if (selectedItem.oid === undefined) {

                 var returnResource = liiteResurssi.$save();

                 returnResource.then(function(liiteResponse){
                     console.log('LIITE RESPONSE : ', liiteResponse);
                    console.log('LIITE TO ADD : ', liiteResponse.result);
                    addLiiteToLiitteet(liiteResponse.result);
                });

             } else {
                 var liiteResourcePromise = liiteResource.$update();
                 liiteResourcePromise.then(function(liiteResult){
                     addLiiteToLiitteet(liiteResult.result);
                 });
             }
            //$scope.model.liitteet.push(liite);
        });

    };

    $scope.model.uusiLiite = function() {
       $scope.model.muokkaaLiitetta(undefined);
    };

    $scope.model.poistaLiite = function(liite) {


        var texts = {
            title: LocalisationService.t("hakukohde.liitteet.list.remove.title"),
            description: LocalisationService.t("hakukohde.liitteet.list.remove.desc"),
            ok: LocalisationService.t("ok"),
            cancel: LocalisationService.t("cancel")
        };

        var d = dialogService.showDialog(texts);
        d.result.then(function(data){
            if ("ACTION" === data) {
                removeLiiteFromList(liite);
            }
        });

    };





});

/*


    ---------> Hakukohde liite modal controller  <-------------


 */


app.controller('LiiteModalController', function($scope,$modalInstance,LocalisationService,Koodisto,liite,organisaationOsoite) {

    $scope.model = {};

    var selectedKieli = undefined;

    $scope.model.liite = liite;

    $scope.model.validationmsgs = [];

    $scope.model.showAlert = false;


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

        if ($scope.model.liite.liitteenToimitusOsoite.postinumero !== undefined) {

            var koodi = findKoodiWithUri($scope.model.liite.liitteenToimitusOsoite.postinumero,$scope.model.koodis);

            $scope.model.liite.liitteenToimitusOsoite.postinumeroArvo =  koodi.koodiArvo;
        }

    });

    $scope.model.koodistoComboCallback  = function(kieli) {
        selectedKieli = kieli;
    };

    var validateLiite = function() {


        $scope.model.validationmsgs.splice(0,$scope.model.validationmsgs.length);

        if (selectedKieli === undefined) {
            $scope.model.validationmsgs.push(LocalisationService.t('tarjonta.hakukohde.liite.modal.kieli.req.msg'));
        }

        if ($scope.model.liite.toimitettavaMennessa === undefined ) {
            $scope.model.validationmsgs.push(LocalisationService.t('tarjonta.hakukohde.liite.modal.toimitettavaMennessa.req.msg'));
        }

        if ($scope.model.validationmsgs.length > 0) {
            return false;
        }

        return true;
    };

    $scope.model.onKieliTypeAheadChange = function() {
        var koodi = findKoodiWithArvo($scope.model.liite.liitteenToimitusOsoite.postinumeroArvo,$scope.model.koodis);

        $scope.model.liite.liitteenToimitusOsoite.postinumero = koodi.koodiUri;
        $scope.model.liite.liitteenToimitusOsoite.postitoimipaikka = koodi.koodiNimi;
    };

    $scope.model.cancel = function() {
        $modalInstance.dismiss('cancel');
    };

    $scope.model.save = function() {
        $scope.model.showAlert = false;
       if (validateLiite()) {
           $scope.model.liite.liitteenKuvaus.nimi =  selectedKieli.koodiNimi;
           $scope.model.liite.liitteenKuvaus.arvo = selectedKieli.koodiArvo;
           $scope.model.liite.liitteenKuvaus.versio = selectedKieli.koodiVersio;
           $scope.model.liite.liitteenKuvaus.uri = $scope.model.liite.kieliUri;


           $modalInstance.close($scope.model.liite);


       } else {

           $scope.model.showAlert = true;
       }

    };



    $scope.model.kaytaOrganisaationPostiOsoitetta = function() {

        var koodi =   findKoodiWithUri(organisaationOsoite.postinumero,$scope.model.koodis);
        $scope.model.liite.liitteenToimitusOsoite.postinumeroArvo =  koodi.koodiArvo;
        $scope.model.liite.liitteenToimitusOsoite.osoiterivi1 = organisaationOsoite.osoiterivi1;
        $scope.model.liite.liitteenToimitusOsoite.postinumero = organisaationOsoite.postinumero;
        $scope.model.liite.liitteenToimitusOsoite.postitoimipaikka = organisaationOsoite.postitoimipaikka;

    };


    $scope.model.translations = {

        title : LocalisationService.t('tarjonta.hakukohde.liite.modal.otsikko'),
        kuvausKieli : LocalisationService.t('tarjonta.hakukohde.liite.modal.kuvauskieli'),
        liitteenNimi : LocalisationService.t('tarjonta.hakukohde.liite.modal.nimi'),
        liitteenKuvaus : LocalisationService.t('tarjonta.hakukohde.liite.modal.kuvaus'),
        toimitettavaMennessa  : LocalisationService.t('tarjonta.hakukohde.liite.modal.toimitettavaMennessa'),
        toimitusosoite : LocalisationService.t('tarjonta.hakukohde.liite.modal.toimitusosoite'),
        kaytetaanOrganisaationPostiOsoitetta : LocalisationService.t('tarjonta.hakukohde.liite.modal.kaytetaanOrganisaationPostiosoitetta'),
        kaytetaanMuutaOsoitetta : LocalisationService.t('tarjonta.hakukohde.liite.modal.kaytetaanMuutaOsoitetta'),
        voidaanToimittaaSahkoisesti : LocalisationService.t('tarjonta.hakukohde.liite.modal.voidaanToimittaaSahkoisesti'),
        peruuta : LocalisationService.t('tarjonta.hakukohde.liite.modal.peruuta.button'),
        tallenna : LocalisationService.t('tarjonta.hakukohde.liite.modal.tallenna.button')
    }
});