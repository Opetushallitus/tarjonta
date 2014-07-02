'use strict';

/* Controllers */

var app = angular.module('app.edit.ctrl.kk');

app.controller('SelectTutkintoOhjelmaController', ['$scope', 'targetFilters', '$modalInstance', 'Koodisto', '$q', 'Config', 'TarjontaService', 'LocalisationService',
    function($scope, targetFilters, $modalInstance, Koodisto, $q, config, TarjontaService, LocalisationService) {
         var KOULUTUSTYYPPI = 'koulutustyyppi_3';

        //filtterisivun malli
        $scope.stoModel = {koulutusalaKoodistoUri: config.env["koodisto-uris.koulutusala"],
            tutkinnotFetched: false,
            korkeakoulututkinnot: [],
            hakutulokset: [],
            active: {},
            hakulause: '',
            koulutusala: '',
            itemSelected: false,
            searched: false,
            errors: []};


        //ng-grid malli
        $scope.gridOptions = {data: 'stoModel.hakutulokset',
            columnDefs: [
                {field: 'koodiArvo', displayName: 'Koodi', width: "20%"},
                {field: 'koodiNimi', displayName: 'Nimi', width: "80%"}],
            multiSelect: false,
            beforeSelectionChange: function(rowItem, event) {
                $scope.clearErrors();
//                console.log("HERE IS THE BEFORE SELECTION CALLBACK" + rowItem.entity.koodiUri);
                $scope.stoModel.active = rowItem.entity;
                TarjontaService.getKoulutuskoodiRelations({uri: rowItem.entity.koodiUri, koulutustyyppi : KOULUTUSTYYPPI, languageCode: $scope.koodistoLocale}, function(response) {
                    $scope.stoModel.itemSelected = false;
                    if (response.status === 'OK') {
                        var relation = response.result;
                        
                        if (!$scope.isRelationAvailable(relation.eqf) ||
                                !$scope.isRelationAvailable(relation.koulutusala) ||
                                !$scope.isRelationAvailable(relation.koulutusaste) ||
                                !$scope.isRelationAvailable(relation.koulutuskoodi) ||
                                !$scope.isRelationAvailable(relation.opintoala) ||
                                !$scope.isRelationAvailable(relation.opintojenLaajuusyksikko) ||
                                !$scope.isRelationAvailables(relation.opintojenLaajuusarvos) ||
                                !$scope.isRelationAvailables(relation.tutkintonimikes)
                                ) {
                            $scope.stoModel.errors.push({msg: LocalisationService.t('koulutuskoodi.puuttuu-koodisto-relaatio')});
                            return;
                        }
                        $scope.stoModel.itemSelected = true;
                    }
                });

                $scope.gridOptions.selectedItems = [];
                return true;
            }};

        $scope.isRelationAvailable = function(obj) {
            return !angular.isUndefined(obj) && !angular.isUndefined(obj.uri);
        };

        $scope.isRelationAvailables = function(obj) {
            return !angular.isUndefined(obj) && !angular.isUndefined(obj.uris) && Object.keys(obj.uris).length > 0;
        };

        $scope.clearErrors = function() {
            $scope.stoModel.errors = [];
        };

        //Korkeakoulututukintojen haku koodistosta (kaytetaan relaatioita koulutusastekoodeihin) 
        //Kutsutaan haun yhteydessa jos kk tutkintoja ei viela haettu
        $scope.getKkTutkinnot = function() {
            //Muodostetaan nippu promiseja, jolloin voidaan toimia sitten kun kaikki promiset taytetty
            var promises = [];
            angular.forEach(targetFilters, function(value, key) {
                promises.push(Koodisto.getYlapuolisetKoodit(value, 'FI'));
            });
            
            // KJOH-777 häkki
            promises.push(Koodisto.getKoodi("koulutus","koulutus_511999", 'FI').then(function(koodi){
              return [koodi];
              }
            ));
            
            var koulutuskooditHaettu = $q.all(promises);
            koulutuskooditHaettu.then(function(koodisParam) {

                //laitetaan korkeakoulututkinnot koodiuri: koodi -mappiin
                angular.forEach(koodisParam, function(koodis, key) {
                    angular.forEach(koodis, function(koodi, key) {
                        if (koodi.koodiKoodisto === config.env["koodisto-uris.koulutus"]) {
                            $scope.stoModel.korkeakoulututkinnot[koodi.koodiUri] = koodi;
                        }
                    });

                });
                //sitten aloitetaan varsinainen haku
                $scope.stoModel.tutkinnotFetched = true;
                $scope.searchTutkinnot();

            });
        };

        //Haun suorittaminen
        $scope.searchTutkinnot = function() {
//            console.log("Selected koulutusala: " + $scope.stoModel.koulutusala);
            $scope.stoModel.itemSelected = false;
            var tempTutkinnot = [];
            //Jos kk-tutkintoja ei haettu ne haetaan ensin
            if (!$scope.stoModel.tutkinnotFetched) {
                $scope.getKkTutkinnot();
                //Jos koulutusalavalittu filtteroidaan koulutusala -> koulutusrelaation avulla minka jalkeen string-haku
            } else if ($scope.stoModel.koulutusala.length > 0) {
//                console.log("Koulutusalauri: " + $scope.stoModel.koulutusala);
                var hakutulosPromise = Koodisto.getYlapuolisetKoodit($scope.stoModel.koulutusala, 'FI');
                hakutulosPromise.then(function(koodisParam) {
                    tempTutkinnot = koodisParam.filter(function(koodi) {
                        return $scope.stoModel.korkeakoulututkinnot[koodi.koodiUri] !== undefined;
                    });
                    $scope.performStringSearch(tempTutkinnot);
                });
                //Muuten kaikki kk-tutkinnot ok suoritetaan vain string-haku
            } else {
                for (var k in $scope.stoModel.korkeakoulututkinnot) {
                    if ($scope.stoModel.korkeakoulututkinnot.hasOwnProperty(k))
                        tempTutkinnot.push($scope.stoModel.korkeakoulututkinnot[k]);
                }
                $scope.performStringSearch(tempTutkinnot);
            }
        };

        //string-haun suorittaminen
        $scope.performStringSearch = function(tutkinnot) {
//            console.log("Performing string search");
            $scope.stoModel.hakutulokset = tutkinnot.filter(function(element) {
                $scope.stoModel.searched = true;
                return (element.koodiNimi.toLowerCase().indexOf($scope.stoModel.hakulause.toLowerCase()) > -1) || (element.koodiArvo.indexOf($scope.stoModel.hakulause) > -1);
            });
        };

        //Hakukriteerien tyhjennys
        $scope.clearCriteria = function() {
            $scope.stoModel.hakulause = '';
            $scope.stoModel.koulutusala = '';
        };

        //dialogin sulkeminen ok-napista, valitun hakutuloksen palauttaminen
        $scope.ok = function() {
//            console.log("CLOSING WITH SELECTION: " + $scope.stoModel.active);
            $modalInstance.close($scope.stoModel.active);
        };

        //dialogin sulkeminen peruuta-napista
        $scope.cancel = function() {
            $modalInstance.dismiss();
        };

    }])
        .controller('TutkintoOhjelmaSelectOpenerCtrl', ['$scope', '$modal', function($scope, $modal) {
                $scope.model = {};

                $scope.open = function() {
                    var modalInstance = $modal.open({
                        templateUrl: 'partials/koulutus/edit/korkeakoulu/selectTutkintoOhjelma.html',
                        controller: 'SelectTutkintoOhjelmaController',
                        resolve: {
                            targetFilters: function() {
                                return [];
                            }
                        }
                    });

                    modalInstance.result.then(function(selectedItem) {
//                        console.log('Ok, dialog closed: ' + selectedItem.koodiNimi);
                        if (selectedItem.koodiUri != null) {
                            $scope.model.selected = selectedItem;
                        } else {
                            $scope.model.selected = null;
                        }
                    }, function() {
                        $scope.model.selected = null;
//                        console.log('Cancel, dialog closed');
                    });
                };
            }]);