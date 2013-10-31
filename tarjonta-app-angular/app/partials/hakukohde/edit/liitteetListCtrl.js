var app =  angular.module('app.kk.edit.hakukohde.ctrl');

app.controller('LiitteetListController',function($scope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde,Liite, HakuService, $modal ,Config,$location) {

     var kieliSet = new buckets.Set();



     $scope.model.liitteenkielet = [];

     $scope.model.liitteet = [];

    var liitteetResource = Liite.get({hakukohdeOid: $scope.model.hakukohde.oid});

    var liitteetPromise = liitteetResource.$promise;

    liitteetPromise.then(function(liitteet){

        console.log('LIITTEET GOT: ',liitteet);

        angular.forEach(liitteet.result,function(liite){
           kieliSet.add(liite.kieliNimi);

          $scope.model.liitteet.push(liite);
        });
        $scope.model.liitteenkielet = kieliSet.toArray();

    });

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
            controller: LiiteModalInstanceController,
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

        modalInstance.result.then(function(liite){
             liite.hakukohdeOid = $scope.model.hakukohdeOid;
            console.log('GOT LIITE: ', liite);
             var liiteResource  = new Liite(liite);
             if (liite.oid === undefined) {
                 liiteResource.$save();
             } else {
                 liiteResource.$update();
             }
            $scope.model.liitteet.push(liite);
        });

    };

    $scope.model.uusiLiite = function() {
       $scope.model.muokkaaLiitetta(undefined);
    };

    $scope.model.poistaLiite = function(liite) {

           var index = $scope.model.liitteet.indexOf(liite);
           $scope.model.liitteet.splice(index,1);
           liite.hakukohdeOid = $scope.model.hakukohdeOid;
           liite.liiteId = liite.oid;

        var liiteResource = new Liite(liite);
        liiteResource.$delete();



    };


    //Hakukohdeliite modal controller

    var LiiteModalInstanceController = function($scope,$modalInstance,LocalisationService,Koodisto,liite,organisaationOsoite) {

        $scope.model = {};

        var selectedKieli = undefined;

        $scope.model.liite = liite;




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

        $scope.model.onKieliTypeAheadChange = function() {
            var koodi = findKoodiWithArvo($scope.model.liite.liitteenToimitusOsoite.postinumeroArvo,$scope.model.koodis);

            $scope.model.liite.liitteenToimitusOsoite.postinumero = koodi.koodiUri;
            $scope.model.liite.liitteenToimitusOsoite.postitoimipaikka = koodi.koodiNimi;
        };

        $scope.model.cancel = function() {
            $modalInstance.dismiss('cancel');
        };

        $scope.model.save = function() {
             if (selectedKieli !== undefined) {
                 $scope.model.liite.liitteenKuvaus.nimi =  selectedKieli.koodiNimi;
                 $scope.model.liite.liitteenKuvaus.arvo = selectedKieli.koodiArvo;
                 $scope.model.liite.liitteenKuvaus.versio = selectedKieli.koodiVersio;
             }

            $scope.model.liite.liitteenKuvaus.uri = $scope.model.liite.kieliUri;
            $modalInstance.close($scope.model.liite);
        };

        $scope.model.kaytaOrganisaationPostiOsoitetta = function() {

            var koodi =   findKoodiWithUri(organisaationOsoite.postinumero,$scope.model.koodis);
            $scope.model.liite.liitteenToimitusOsoite.postinumeroArvo =  koodi.koodiArvo;
            $scope.model.liite.liitteenToimitusOsoite.osoiterivi1 = organisaationOsoite.osoiterivi1;
            $scope.model.liite.liitteenToimitusOsoite.postinumero = organisaationOsoite.postinumero;
            $scope.model.liite.liitteenToimitusOsoite.postitoimipaikka = organisaationOsoite.postitoimipaikka;

        };

        $scope.model.canSave = function() {
           return $scope.liiteModalForm.$valid;
        }

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
    };

});