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

app.controller('SisaltyvyysCtrl', ['$scope', '$log', '$routeParams', '$route', 'Config', 'Koodisto', 'LocalisationService', 'TarjontaService', '$timeout', '$q',
    function SisaltyvyysCtrl($scope, $log, $routeParams, $route, config, koodisto, LocalisationService, TarjontaService, $timeout, $q) {
        /*
         * 1.2.246.562.5.2013111913140576761720
         */


        /*
         * Tree data objects.
         */
        $scope.tree = {
            map: {}, //obj[oid].oids[]
            activePromises: [],
            treedata: [],
        };
        /*
         * Select koulutus data objects.
         */
        $scope.model = {
            selectedOid: '',
            tutkinto: {
                uri: '',
                koodis: [],
                hakulause: ''
            },
            hakutulos: [],
            search: {count: 0},
            test: [],
            valitut: {//selected koulutus items
                oids: [],
                data: []
            },
            spec: {//search parameters
                // oid: '1.2.246.562.10.56753942459', //selected org oid
                oid: null,
                terms: null,
                state: null,
                year: null,
                season: null
            },
            html: 'partials/koulutus/sisaltyvyys/liita-koulutuksia-select.html'
        };

        $scope.koodistoLocale = LocalisationService.getLocale();

        var koodisPromise = koodisto.getAllKoodisWithKoodiUri(config.app["koodisto-uris.tutkintotyyppi"], $scope.koodistoLocale);
        koodisPromise.then(function(koodis) {
            for (var i = 0; i < koodis.length; i++)
                if (koodis[i].koodiUri === config.app["koodisto-uri.tutkintotyyppi.alempiKorkeakoulututkinto"] ||
                        koodis[i].koodiUri === config.app["koodisto-uri.tutkintotyyppi.ylempiKorkeakoulututkinto"]) {
                    $scope.model.tutkinto.koodis.push(koodis[i]);
                }
        });

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
                if (angular.isUndefined(result.tulokset)) {
                    return -1;
                }

                var arr = [];

                for (var i = 0; i < result.tulokset.length; i++) {
                    //tulokset is by organisation
                    for (var c = 0; c < result.tulokset[i].tulokset.length; c++) {
                        arr.push({
                            koulutuskoodi: result.tulokset[i].tulokset[c].koulutuskoodi,
                            nimi: result.tulokset[i].tulokset[c].nimi,
                            tarjoaja: result.tulokset[i].nimi,
                            komoOid: result.tulokset[i].tulokset[c].komoOid});
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
         * REVIEW FUNCTIONS
         * 
         */

        /*
         * Open a review dialog.
         * 
         */
        $scope.reviewDialogi = function() {
            $scope.clearTreeData();
            $scope.tree.activePromises.push($scope.getParentsByKomoOid($scope.model.selectedOid));

            $timeout(function() {
                //a hack timeout, remove when the promise problem have been resolved.
                $q.all($scope.tree.activePromises).then(function() {
                    console.log("SUCCESS");
                    var parent = $scope.tree.map['PARENT'];

                    angular.forEach(parent.childs, function(val, key) {
                        $scope.getCreateChildren(key, $scope.tree.treedata, val);
                    });
                });
            }, 500);

            $scope.model.html = 'partials/koulutus/sisaltyvyys/liita-koulutuksia-review.html';
        };

        /*
         * Find tree parents and store the items to a map.
         * 
         * @param {string} komoOid
         * @returns promise
         */
        $scope.getParentsByKomoOid = function(komoOid) {
            var resource = TarjontaService.resourceLink.parents({oid: komoOid});

            return resource.$promise.then(function(res) {
                if (res.result.length === 0) {
                    /*
                     * PARENT(s) one recursive loop end
                     * tree can have one or many parents...
                     */
                    if (angular.isUndefined($scope.tree.map['PARENT'])) {
                        $scope.tree.map['PARENT'] = {childs: {}};
                    }
                    $scope.tree.map['PARENT'].childs[komoOid] = {selected: $scope.model.selectedOid === komoOid};
                } else {
                    /*
                     * go closer to root
                     */
                    angular.forEach(res.result, function(result) {
                        if (angular.isUndefined($scope.tree.map[result])) {
                            $scope.tree.map[result] = {childs: {}};
                        }
                        $scope.tree.map[result].childs[komoOid] = {selected: $scope.model.selectedOid === komoOid};
                        $scope.tree.activePromises.push($scope.getParentsByKomoOid(result));
                    });
                }
            });
        };

        /*
         * Create a tree item.
         */
        $scope.getCreateChildren = function(oid, tree, options) {
            var obj = {nimi: oid, oid: oid, children: [], selected: options.selected};
            tree.push(obj);

            if (!angular.isUndefined($scope.tree.map[oid])) {
                angular.forEach($scope.tree.map[oid].childs, function(val, key) {
                    $scope.getCreateChildren(key, obj.children, val);
                });
            }

            if (options.selected) {
                angular.forEach($scope.model.valitut.data, function(val) {
                    $scope.getCreateChildren(val.komoOid, obj.children, {selected: null});
                });
            }
        };

        $scope.clearTreeData = function() {
            $scope.tree = {
                map: {}, //obj[oid].oids[]
                activePromises: [],
                treedata: [],
            };
        };
    }]);
