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

app.controller('PoistaSisaltyvyysCtrl', ['$scope', '$location', '$log', 'Config', 'Koodisto', 'LocalisationService', 'TarjontaService', '$q', '$modalInstance', 'targetKomo', 'organisaatioOid',
    function PoistaSisaltyvyysCtrl($scope, $log, $location, config, koodisto, LocalisationService, TarjontaService, $q, $modalInstance, targetKomo, organisaatio) {
        /*
         * Select koulutus data objects.
         */
        $scope.model = {
            headLabel: LocalisationService.t('sisaltyvyys.liitoksen-luonti-teksti', [targetKomo.nimi, organisaatio.nimi]),
            errors: [],
            text: {
                hierarchy: LocalisationService.t('sisaltyvyys.tab.hierarkia'),
                list: LocalisationService.t('sisaltyvyys.tab.lista')},
            organisaatio: organisaatio,
            treeOids: [],
            selectedOid: [targetKomo.oid], //directive needs an array
            searchKomoOids: [targetKomo.oid],
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
            html: 'partials/koulutus/sisaltyvyys/poista-koulutuksia-select.html'
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

        //ng-grid for selecting nodes (with edit)
        $scope.selectGridOptions = {
            data: 'model.hakutulos',
            selectedItems: $scope.model.newOids,
            // checkboxCellTemplate: '<div class="ngSelectionCell"><input tabindex="-1" class="ngSelectionCheckbox" type="checkbox" ng-checked="row.selected" /></div>',
            columnDefs: [
                {field: 'koulutuskoodi', displayName: LocalisationService.t('sisaltyvyys.hakutulos.arvo', $scope.koodistoLocale), width: "20%"},
                {field: 'nimi', displayName: LocalisationService.t('sisaltyvyys.hakutulos.nimi', $scope.koodistoLocale), width: "50%"},
                {field: 'tarjoaja', displayName: LocalisationService.t('sisaltyvyys.hakutulos.tarjoaja', $scope.koodistoLocale), width: "30%"}
            ],
            showSelectionCheckbox: true,
            multiSelect: true};

        //ng-grid for selected items (without edit)
        $scope.reviewGridOptions = {
            data: 'model.newOids',
            // checkboxCellTemplate: '<div class="ngSelectionCell"><input tabindex="-1" class="ngSelectionCheckbox" type="checkbox" ng-checked="row.selected" /></div>',
            columnDefs: [
                {field: 'koulutuskoodi', displayName: LocalisationService.t('sisaltyvyys.hakutulos.arvo', $scope.koodistoLocale), width: "20%"},
                {field: 'nimi', displayName: LocalisationService.t('sisaltyvyys.hakutulos.nimi', $scope.koodistoLocale), width: "50%"},
                {field: 'tarjoaja', displayName: LocalisationService.t('sisaltyvyys.hakutulos.tarjoaja', $scope.koodistoLocale), width: "30%"}
            ],
            showSelectionCheckbox: false,
            multiSelect: false};

        $scope.save = function() {
            $scope.clearErrors();
            var oids = [];
            angular.forEach($scope.model.newOids, function(val) {
                oids.push(val.oid);
            });

            var res = TarjontaService.resourceLink;
            var remove = res.removeMany({
                parent: targetKomo.oid,
                childs: oids
            });

            remove.$promise.then(function(response) {
                if (response.status === 'OK') {
                    console.log("success", response);
                    $modalInstance.close();
                } else {
                    console.log("save cancelled", response);
                    angular.forEach(response.errors, function(error) {
                        //add additional information to the error data object
                        var arr = [];

                        if (error.errorMessageKey === 'LINKING_PARENT_HAS_NO_CHILDREN') {
                            arr = [targetKomo.nimi];
                        } else if (error.errorMessageKey === 'LINKING_CHILD_OID_NOT_FOUND') {
                            arr = $scope.searchErrorNimi(error.errorMessageParameters, $scope.model.newOids);
                        } else if (error.errorMessageKey === 'LINKING_OID_HAS_CHILDREN') {
                            arr = $scope.searchErrorNimi(error.errorMessageParameters, $scope.model.newOids);
                        }
                        error.msg = LocalisationService.t("sisaltyvyys.error." + error.errorMessageKey, arr);
                        $scope.model.errors.push(error);
                    });

                }
            });
        };

        $scope.searchErrorNimi = function(errorParams, selected) {
            var arr = [];
            angular.forEach(errorParams, function(oid) {
                angular.forEach(selected, function(row) {
                    if (oid === row.oid) {
                        arr.push(row.nimi);
                    }
                });
            });

            return arr;
        }

        //dialogin sulkeminen peruuta-napista
        $scope.cancel = function() {
            $modalInstance.dismiss();
        };

        $scope.selectDialogi = function() {
            //aseta esivalittu organisaatio
            $scope.clearErrors();
            $scope.model.html = 'partials/koulutus/sisaltyvyys/poista-koulutuksia-select.html';
        };

        /*
         * Open a review dialog.
         * 
         */
        $scope.reviewDialogi = function() {
            $scope.clearErrors();
            var oids = [];
            for (var i = 0; i < $scope.model.newOids.length; i++) {
                oids.push($scope.model.newOids[i].oid);
            }
            $scope.model.reviewOids = oids;
            $scope.model.html = 'partials/koulutus/sisaltyvyys/poista-koulutuksia-review.html';
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

        /*
         * 2TAB tree loaded handler
         */
        $scope.treeItemsLoaded = function(map) {
            var oids = {};
            angular.forEach(map, function(parentVal, keyOid) {
                if (keyOid !== 'ROOT' && keyOid !== targetKomo.oid) {
                    angular.forEach(parentVal.childs, function(val, keyOid) {
                        oids[keyOid] = {};
                    });
                }
            });

            angular.forEach(oids, function(val, keyOid) {

                var add = true;
                for (var i = 0; i < $scope.model.hakutulos.length; i++) {
                    if ($scope.model.hakutulos[i].oid === keyOid) {
                        add = false;
                        break;
                    }
                }

                if (add) {
                    var promise = $scope.searchBySpec({//search parameter object
                        komoOid: keyOid,
                        oid: null,
                        terms: null,
                        state: null,
                        year: null,
                        season: null
                    });

                    promise.then(function(arr) {
                        console.log(arr);
                        $scope.model.hakutulos.push(arr[0]);
                    });
                }
            });
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
                $scope.selectGridOptions.selectItem($scope.model.hakutulos.indexOf(selected), false);
            }
        };

        /**
         * Search koulutus data to dialog by given parameters.
         * 
         * @returns {undefined}
         */
        $scope.searchTutkinnot = function() {
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
                $scope.model.hakutulos = arr;
                for (var i = 0; i < arr.length; i++) {
                    oids.push(arr[i].oid);
                }
                $scope.model.searchKomoOids = oids;
            });
        };

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
                    }
                }

                deferred.resolve(searchResult);
            });

            return deferred.promise;
        };

        $scope.clearErrors = function() {
            $scope.model.errors = [];
        };
    }]);
