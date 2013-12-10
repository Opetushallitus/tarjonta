/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 */
var app = angular.module('app.koulutus.sisaltyvyys.ctrl', []);

app.controller('SisaltyvyysCtrl', ['$scope', '$log', 'Config', 'Koodisto', 'LocalisationService', 'TarjontaService', '$q', 'targetKomoOid', 'organisaatioOid',
    function SisaltyvyysCtrl($scope, $log, config, koodisto, LocalisationService, TarjontaService, $q, targetKomoOid, organisaatioOid) {
        /*
         * Select koulutus data objects.
         */
        $scope.model = {
            treeOids: [],
            tmp: [],
            selectedOid: [targetKomoOid], //directive need an array
            searchKomoOids: [],
            tutkinto: {
                uri: '',
                koodis: [],
                hakulause: ''
            },
            hakutulos: [],
            search: {count: 0},
            valitut: {//selected koulutus items
                oids: [], //only row oids
                data: [] //only row objects
            },
            spec: {//search parameter object
                oid: organisaatioOid,
                terms: '', //search words
                state: null,
                year: null,
                season: null
            },
            html: 'partials/koulutus/sisaltyvyys/liita-koulutuksia-select.html'
        };

        $scope.other = {
            tutkintotyypit: [
                //tutkintotyyppi koodisto koodit
                config.app["koodisto-uri.tutkintotyyppi.alempiKorkeakoulututkinto"], //kandi
                config.app["koodisto-uri.tutkintotyyppi.ylempiKorkeakoulututkinto"] //maisteri
            ],
            koulutusUris: {} //loaded koulutuskoodi uris in a map. do not show a row item, if it's not in the map. 
        }

        $scope.koodistoLocale = LocalisationService.getLocale();

        var koodisPromise = koodisto.getAllKoodisWithKoodiUri(config.app["koodisto-uris.tutkintotyyppi"], $scope.koodistoLocale);
        koodisPromise.then(function(koodis) {
            for (var i = 0; i < koodis.length; i++) {
                for (var c = 0; c < $scope.other.tutkintotyypit.length; c++) {
                    if (koodis[i].koodiUri === $scope.other.tutkintotyypit[c]) {
                        $scope.model.tutkinto.koodis.push(koodis[i]);
                    }
                }
            }
        });

        $scope.getKkTutkinnot = function() {
            //Muodostetaan nippu promiseja, jolloin voidaan toimia sitten kun kaikki promiset taytetty
            var promises = [];
            angular.forEach($scope.other.tutkintotyypit, function(value) {
                console.log(value);
                promises.push(koodisto.getYlapuolisetKoodit(value, $scope.koodistoLocale));
            });
            var koulutuskooditHaettu = $q.all(promises);
            koulutuskooditHaettu.then(function(koodisParam) {
                //laitetaan korkeakoulututkinnot koodiuri: koodi -mappiin
                angular.forEach(koodisParam, function(koodis) {
                    angular.forEach(koodis, function(koodi) {
                        // console.log(koodi.koodiUri);

                        if (koodi.koodiKoodisto === config.env["koodisto-uris.koulutus"]) {
                            $scope.other.koulutusUris[koodi.koodiUri] = koodi;
                        }
                    });
                });
                //sitten aloitetaan varsinainen haku          
                $scope.searchTutkinnot();
            });
        };

        //ng-grid malli
        $scope.gridOptions = {
            data: 'model.hakutulos',
            selectedItems: $scope.model.valitut.data,
            // checkboxCellTemplate: '<div class="ngSelectionCell"><input tabindex="-1" class="ngSelectionCheckbox" type="checkbox" ng-checked="row.selected" /></div>',
            columnDefs: [
                {field: 'koulutuskoodi', displayName: LocalisationService.t('sisaltyvyys.hakutulos.arvo', $scope.koodistoLocale), width: "20%"},
                {field: 'nimi', displayName: LocalisationService.t('sisaltyvyys.hakutulos.nimi', $scope.koodistoLocale), width: "50%"},
                {field: 'tarjoaja', displayName: '', width: "50%"},
            ],
            multiSelect: true}

        //Hakukriteerien tyhjennys
        $scope.clearCriteria = function() {
            $scope.model.tutkinto.uri = '';
            $scope.model.tutkinto.hakulause = '';
        };

        //dialogin sulkeminen ok-napista, valitun hakutuloksen palauttaminen
        $scope.ok = function() {
            console.log("TRY TO SAVE");
            var promises = [];
            angular.forEach($scope.model.valitut.data, function(val) {
                TarjontaService.saveResourceLink($scope.model.selectedOid, val.komoOid, function(res) {
                    console.log(res);
                });
            });

            //$modalInstance.close($scope.stoModel.active);
        };

        //dialogin sulkeminen peruuta-napista
        $scope.cancel = function() {
            // $modalInstance.dismiss();
        };

        /**
         * Search koulutus data to dialog by given parameters.
         * 
         * @returns {undefined}
         */
        $scope.searchTutkinnot = function() {
            // valinnat
            TarjontaService.haeKoulutukset($scope.model.spec).then(function(result) {
                $scope.model.hakutulos = [];
                $scope.model.searchKomoOids = [];
                if (angular.isUndefined(result.tulokset)) {
                    return -1;
                }

                var arr = [];

                for (var i = 0; i < result.tulokset.length; i++) {
                    //tulokset is by organisation
                    for (var c = 0; c < result.tulokset[i].tulokset.length; c++) {
                        $scope.model.searchKomoOids.push(result.tulokset[i].tulokset[c].komoOid);

                        arr.push({
                            koulutuskoodi: result.tulokset[i].tulokset[c].koulutuskoodi,
                            nimi: result.tulokset[i].tulokset[c].nimi,
                            tarjoaja: result.tulokset[i].nimi,
                            oid: result.tulokset[i].tulokset[c].komoOid});
                    }
                }

                angular.forEach(arr, function(value) {
                    var koodisPromise = koodisto.getKoodi(config.env["koodisto-uris.koulutus"], value.koulutuskoodi.split("#")[0], $scope.koodistoLocale);

                    koodisPromise.then(function(koodi) {
                        value.koulutuskoodi = koodi.koodiArvo;

                    });
                });

                $scope.model.hakutulos = arr;

                console.log($scope.model.hakutulos);
            });
        };

        $scope.selectDialogi = function() {
            //aseta esivalittu organisaatio
            $scope.model.html = 'partials/koulutus/sisaltyvyys/liita-koulutuksia-select.html';
        };

        /*
         * Open a review dialog.
         * 
         */
        $scope.reviewDialogi = function() {
            $scope.model.html = 'partials/koulutus/sisaltyvyys/liita-koulutuksia-review.html';
        };

        $scope.selectTreeHandler = function(obj, event) {
            console.log("selected/deselected");
            if (event === 'SELECTED') {
                for (var i = 0; i < $scope.model.hakutulos.length; i++) {
                    if ($scope.model.hakutulos[i].oid === obj.oid) {
                        $scope.model.valitut.data.push($scope.model.hakutulos[i]);
                        break;
                    }
                }
            } else {
                for (var i = 0; i < $scope.model.valitut.data.length; i++) {
                    if ($scope.model.valitut.data[i].oid === obj.oid) {
                        $scope.model.valitut.data.splice(i, 1);
                        break;
                    }
                }
            }

        };

        $scope.getKkTutkinnot();
    }]);
