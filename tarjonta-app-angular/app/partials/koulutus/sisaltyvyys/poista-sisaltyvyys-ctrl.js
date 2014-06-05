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

//location of the base module : liita-sisaltyvyys-ctrl.js
var app = angular.module('app.koulutus.sisaltyvyys.ctrl');

app.controller('PoistaSisaltyvyysCtrl', ['$scope', 'Config', 'Koodisto', 'LocalisationService', 'TarjontaService', '$q', '$modalInstance', 'targetKomo', 'organisaatioOid', 'SisaltyvyysUtil', 'TreeHandlers', '$log',
    function PoistaSisaltyvyysCtrl($scope, config, koodisto, LocalisationService, TarjontaService, $q, $modalInstance, targetKomo, organisaatio, SisaltyvyysUtil, TreeHandlers,$log) {
        /*
         * Select koulutus data objects.
         */
        $scope.model = {
            errors: [],
            text: {
                headLabel: LocalisationService.t('sisaltyvyys.liitoksen-poisto-teksti', [targetKomo.nimi, organisaatio.nimi]),
                hierarchy: LocalisationService.t('sisaltyvyys.tab.hierarkia'),
                list: LocalisationService.t('sisaltyvyys.tab.lista')},
            organisaatio: organisaatio,
            treeOids: [],
            selectedOid: [targetKomo.oid], //directive needs an array
            searchKomoOids: [targetKomo.oid],
            selectedRowData: [], // a parent (selectedOid) will have new childs (selectedRowData)
            reviewOids: [],
            hakutulos: [],
            search: {count: 0},
            valitut: {//selected koulutus items
                oids: [], //only row oids
                data: [] //only row objects
            },
            html: 'partials/koulutus/sisaltyvyys/poista-koulutuksia-select.html'
        };

        $scope.koodistoLocale = LocalisationService.getLocale();

        /*
         * ng-grid for selecting nodes (with select/remove mode)
         */
        $scope.selectGridOptions = {
            data: 'model.hakutulos',
            selectedItems: $scope.model.selectedRowData,
            columnDefs: [
                {field: 'koulutuskoodi', displayName: LocalisationService.t('sisaltyvyys.hakutulos.arvo', $scope.koodistoLocale), width: "20%"},
                {field: 'nimi', displayName: LocalisationService.t('sisaltyvyys.hakutulos.nimi', $scope.koodistoLocale), width: "50%"},
                {field: 'tarjoaja', displayName: LocalisationService.t('sisaltyvyys.hakutulos.tarjoaja', $scope.koodistoLocale), width: "30%"}
            ],
            showSelectionCheckbox: true,
            multiSelect: true};

        /*
         * ng-grid for review tab (only selected items without select/remove mode)
         */
        $scope.reviewGridOptions = {
            data: 'model.selectedRowData',
            columnDefs: [
                {field: 'koulutuskoodi', displayName: LocalisationService.t('sisaltyvyys.hakutulos.arvo', $scope.koodistoLocale), width: "20%"},
                {field: 'nimi', displayName: LocalisationService.t('sisaltyvyys.hakutulos.nimi', $scope.koodistoLocale), width: "50%"},
                {field: 'tarjoaja', displayName: LocalisationService.t('sisaltyvyys.hakutulos.tarjoaja', $scope.koodistoLocale), width: "30%"}
            ],
            showSelectionCheckbox: false,
            multiSelect: false};

        /*
         * 2TAB tree event handlers (look more info from a file liita-sisaltyvyys-ctrl.js)
         */
        TreeHandlers.setScope($scope);
        $scope.treeItemsLoaded = function(map) {
            var oids = {};
            angular.forEach(map, function(parentVal, keyOid) {
                if (keyOid !== 'ROOT' && keyOid === targetKomo.oid) {
                    //we need only the children rows
                    angular.forEach(parentVal.childs, function(val, keyOid) {
                        oids[keyOid] = {};
                    });
                }
            });

            /*
             * Load all selectable (childen)rows to the ng-grid component.
             */
            angular.forEach(oids, function(val, keyOid) {
                var addNew = true;
                
                //allow only one unique oid in the hakutulos list
                angular.forEach($scope.model.hakutulos, function(parentVal) {
                    if (parentVal.oid === keyOid) {
                        addNew = false;
                    }
                });

                if (addNew) {
                    var promise = $scope.searchBySpec({//search parameter object
                        komoOid: keyOid,
                        oid: null,
                        terms: null,
                        state: null,
                        year: null,
                        season: null
                    });

                    promise.then(function(arr) {
                        $scope.model.hakutulos.push(arr[0]);
                    });
                }
            });
        };
        $scope.selectTreeHandler = TreeHandlers.selectTreeHandler;
        $scope.removeItem = TreeHandlers.removeItem;

        /**
         * Search komos.
         * 
         * @returns {undefined}
         */
        $scope.searchKomos = function() {
            var spec = {//search parameter object
                komoOid: targetKomo.oid,
                oid: null,
                terms: null,
                state: null,
                year: null,
                season: null
            };

            var promise = $scope.searchBySpec(spec);
            promise.then(function(arr) {
                var oids = [];
                for (var i = 0; i < arr.length; i++) {
                    oids.push(arr[i].oid);
                }
                $scope.model.searchKomoOids = oids;
            });
        };

        /*
         * Search komos by spec-object
         */
        $scope.searchBySpec = function(spec) {
            var deferred = $q.defer();
            TarjontaService.haeKoulutukset(spec).then(function(result) {
                var searchResult = [];
                if (angular.isUndefined(result.tulokset)) {
                    return -1;
                }
                for (var i = 0; i < result.tulokset.length; i++) {
                    //tulokset is by organisation
                    for (var c = 0; c < result.tulokset[i].tulokset.length; c++) {
                      if(result.tulokset[i].tulokset[c].koulutuskoodi) {
                        var koulutuskoodiUri = result.tulokset[i].tulokset[c].koulutuskoodi.split("#")[0];
                        var item = {
                            koulutuskoodi: '',
                            nimi: result.tulokset[i].tulokset[c].nimi,
                            tarjoaja: result.tulokset[i].nimi,
                            oid: result.tulokset[i].tulokset[c].komoOid
                        };

                        var koodisPromise = koodisto.getKoodi(config.env["koodisto-uris.koulutus"], koulutuskoodiUri, $scope.koodistoLocale);
                        koodisPromise.then(function(koodi) {
                            item.koulutuskoodi = koodi.koodiArvo;
                        });
                        searchResult.push(item);
                      } else {
                        $log.error("koulutus without koodi:", result.tulokset[i].tulokset[c]);
                      }
                    }
                }

                deferred.resolve(searchResult);
            });

            return deferred.promise;
        };

        $scope.clearErrors = function() {
            $scope.model.errors = [];
        };

        /*
         * Save and close the dialog.
         */
        $scope.clickSave = function() {
            $scope.clearErrors();
            var oids = [];
            angular.forEach($scope.model.selectedRowData, function(row) {
                oids.push(row.oid);
            });

            var removeMany = TarjontaService.resourceLink.removeMany({
                parent: targetKomo.oid,
                childs: oids
            });

            removeMany.$promise.then(function(response) {
                var su = new SisaltyvyysUtil(); //look more info from a file liita-sisaltyvyys-ctrl.js
                $scope.model.errors = su.handleResult(targetKomo, response, $scope.model.selectedRowData, $modalInstance);
            });
        };

        /*
         * Cancel and close the dialog.
         */
        $scope.clickCancel = function() {
            $modalInstance.dismiss();
        };

        /*
         * Go back to select rows dialog.
         */
        $scope.clickSelectDialogi = function() {
            $scope.clearErrors();
            $scope.model.html = 'partials/koulutus/sisaltyvyys/poista-koulutuksia-select.html';
        };

        /*
         * Open a review dialog.
         */
        $scope.clickReviewDialogi = function() {
            $scope.clearErrors();
            var oids = [];
            for (var i = 0; i < $scope.model.selectedRowData.length; i++) {
                oids.push($scope.model.selectedRowData[i].oid);
            }
            $scope.model.reviewOids = oids;
            $scope.model.html = 'partials/koulutus/sisaltyvyys/poista-koulutuksia-review.html';
        };
    }
]);
