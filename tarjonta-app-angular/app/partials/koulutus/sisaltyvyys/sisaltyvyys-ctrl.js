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

app.controller('SisaltyvyysCtrl', ['$scope', '$location', '$log', 'Config', 'Koodisto', 'LocalisationService', 'TarjontaService', '$q', '$modalInstance', 'targetKomoOid', 'organisaatioOid',
    function SisaltyvyysCtrl($scope, $log, $location, config, koodisto, LocalisationService, TarjontaService, $q, $modalInstance, targetKomoOid, organisaatio) {
        /*
         * Select koulutus data objects.
         */
        $scope.model = {
            text: {
                hierarchy: LocalisationService.t('sisaltyvyys.tab.hierarkia'),
                list: LocalisationService.t('sisaltyvyys.tab.lista')},
            organisaatio: organisaatio,
            treeOids: [],
            selectedOid: [targetKomoOid], //directive needs an array
            searchKomoOids: [],
            newOids: [], // a parent (selectedOid) will have new childs (newOids)
            reviewOids: [],
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
                oid: organisaatio.oid,
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
            koulutuskoodiMap: {} //key : koulutuskoodi uri : tutkintotyypit
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
                var promise = koodisto.getYlapuolisetKoodit(value, $scope.koodistoLocale);
                promises.push(promise);
                promise.then(function(res) {
                    for (var i = 0; i < res.length; i++) {
                        if (res[i].koodiKoodisto === config.env["koodisto-uris.koulutus"]) {
                            $scope.other.koulutuskoodiMap[res[i].koodiUri] = value;
                        }
                    }
                });
            });

            $q.all(promises).then(function(koodisParam) {
                $scope.searchTutkinnot();
            });
        };

        //ng-grid malli
        $scope.gridOptions = {
            data: 'model.hakutulos',
            selectedItems: $scope.model.newOids,
            // checkboxCellTemplate: '<div class="ngSelectionCell"><input tabindex="-1" class="ngSelectionCheckbox" type="checkbox" ng-checked="row.selected" /></div>',
            columnDefs: [
                {field: 'koulutuskoodi', displayName: LocalisationService.t('sisaltyvyys.hakutulos.arvo', $scope.koodistoLocale), width: "20%"},
                {field: 'nimi', displayName: LocalisationService.t('sisaltyvyys.hakutulos.nimi', $scope.koodistoLocale), width: "50%"},
                {field: 'tarjoaja', displayName: LocalisationService.t('sisaltyvyys.hakutulos.tarjoaja', $scope.koodistoLocale), width: "50%"},
            ],
            showSelectionCheckbox: true,
            multiSelect: true}

        //Hakukriteerien tyhjennys
        $scope.clearCriteria = function() {
            $scope.model.tutkinto.uri = '';
            $scope.model.tutkinto.hakulause = '';
        };

        $scope.ok = function() {
            angular.forEach($scope.model.newOids, function(val) {
                TarjontaService.saveResourceLink($scope.model.selectedOid, val.oid, function(res) {
                    console.log(res);
                });
            });

            $modalInstance.close();
            $location.path("/koulutus/" + $scope.model.koulutus.oid);
        };

        //dialogin sulkeminen peruuta-napista
        $scope.cancel = function() {
            $modalInstance.dismiss();
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
                        var koulutuskoodiUri = result.tulokset[i].tulokset[c].koulutuskoodi.split("#")[0];

                        //console.log($scope.other.koulutuskoodiMap[koulutuskoodiUri] === $scope.model.tutkinto.uri)
                        //console.log($scope.other.koulutuskoodiMap[koulutuskoodiUri])
                        if ($scope.model.tutkinto.uri.length === 0 || $scope.other.koulutuskoodiMap[koulutuskoodiUri] === $scope.model.tutkinto.uri) {
                            $scope.model.searchKomoOids.push(result.tulokset[i].tulokset[c].komoOid);

                            arr.push({
                                koulutuskoodi: koulutuskoodiUri,
                                nimi: result.tulokset[i].tulokset[c].nimi,
                                tarjoaja: result.tulokset[i].nimi,
                                oid: result.tulokset[i].tulokset[c].komoOid});
                        }
                    }
                }

                angular.forEach(arr, function(value) {
                    var koodisPromise = koodisto.getKoodi(config.env["koodisto-uris.koulutus"], value.koulutuskoodi, $scope.koodistoLocale);

                    koodisPromise.then(function(koodi) {
                        value.koulutuskoodi = koodi.koodiArvo;

                    });
                });

                $scope.model.hakutulos = arr;
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
            var oids = [];
            for (var i = 0; i < $scope.model.newOids.length; i++) {
                oids.push($scope.model.newOids[i].oid);
            }
            $scope.model.reviewOids = oids;
            $scope.model.html = 'partials/koulutus/sisaltyvyys/liita-koulutuksia-review.html';
        };

        /*
         * 2TAB tree click handler
         */
        $scope.selectTreeHandler = function(obj, event) {
            if (event === 'SELECTED') {
                for (var i = 0; i < $scope.model.hakutulos.length; i++) {
                    if ($scope.model.hakutulos[i].oid === obj.oid) {
                        $scope.model.newOids.push($scope.model.hakutulos[i]);
                        break;
                    }
                }
            } else {
                for (var i = 0; i < $scope.model.newOids.length; i++) {
                    if ($scope.model.newOids[i].oid === obj.oid) {
                        $scope.model.newOids.splice(i, 1);
                        break;
                    }
                }
            }
        };

        $scope.removeItem = function(obj) {
            var selected = null;
            for (var i = 0; i < $scope.model.newOids.length; i++) {
                if ($scope.model.newOids[i].oid === obj.oid) {
                    selected = obj;
                    $scope.model.newOids.splice(i, 1);
                    break;
                }
            }

            if (selected !== null) {
                $scope.gridOptions.selectItem($scope.model.hakutulos.indexOf(selected), false);
            }
        };

        $scope.getKkTutkinnot();
    }]);
