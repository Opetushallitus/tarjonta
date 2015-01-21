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
app.factory('SisaltyvyysUtil', function($resource, $log, $q, Config, LocalisationService, $rootScope) {
    $log = $log.getInstance('SisaltyvyysUtil');
    return function() {
        var factoryScope = {};
        factoryScope.searchErrorNimi = function(errorParams, selected) {
            var arr = [];
            angular.forEach(errorParams, function(oid) {
                angular.forEach(selected, function(row) {
                    if (oid === row.oid) {
                        arr.push(row.nimi);
                    }
                });
            });
            return arr;
        };
        factoryScope.handleResult = function(targetKomo, response, selectedRowData, modalInstance) {
            var arrErrors = [];
            if (response.status === 'OK') {
                $rootScope.$broadcast('sisaltyvyysChanges');
                modalInstance.close();
            }
            else {
                $log.debug('save cancelled', response);
                angular.forEach(response.errors, function(error) {
                    //add additional information to the error data object
                    var arr = [];
                    switch (error.errorMessageKey) {
                        case 'LINKING_PARENT_HAS_NO_CHILDREN':
                            arr = [targetKomo.nimi];
                            break;
                        case 'LINKING_CHILD_OID_NOT_FOUND':
                        case 'LINKING_OID_HAS_CHILDREN':
                        case 'LINKING_CANNOT_CREATE_LOOP':
                            arr = factoryScope.searchErrorNimi(error.errorMessageParameters, selectedRowData);
                            break;
                    }
                    error.msg = LocalisationService.t('sisaltyvyys.error.' + error.errorMessageKey, arr);
                    arrErrors.push(error);
                });
            }
            return arrErrors;
        };
        return factoryScope;
    };
});
//singleton service
app.service('TreeHandlers', function() {
    var singleton = {
        scope: null
    };
    singleton.setScope = function(scope) {
        singleton.scope = scope;
    };
    /*
       * 2TAB tree click handler
       */
    singleton.selectTreeHandler = function(obj, event) {
        var i;
        if (event === 'SELECTED') {
            for (i = 0; i < singleton.scope.model.hakutulos.length; i++) {
                if (singleton.scope.model.hakutulos[i].oid === obj.oid) {
                    singleton.scope.model.selectedRowData.push(singleton.scope.model.hakutulos[i]);
                    break;
                }
            }
        }
        else {
            for (i = 0; i < singleton.scope.model.selectedRowData.length; i++) {
                if (singleton.scope.model.selectedRowData[i].oid === obj.oid) {
                    singleton.scope.model.selectedRowData.splice(i, 1);
                    break;
                }
            }
        }
    };
    singleton.removeItem = function(obj) {
        var i;
        for (i = 0; i < singleton.scope.model.selectedRowData.length; i++) {
            if (singleton.scope.model.selectedRowData[i].oid === obj.oid) {
                singleton.scope.model.selectedRowData.splice(i, 1);
                break;
            }
        }
        for (i = 0; i < singleton.scope.model.hakutulos.length; i++) {
            if (singleton.scope.model.hakutulos[i].oid == obj.oid) {
                singleton.scope.gridOptions.selectItem(i, false);
                break;
            }
        }
    };
    return singleton;
});
app.service('sisaltyvyysColumnDefs', ['LocalisationService', function(LocalisationService) {
    return function(koulutusLaji) {
        if (koulutusLaji === 'OPINTO') {
            return [{
                field: 'nimi',
                displayName: LocalisationService.t('sisaltyvyys.hakutulos.opinto'),
                width: '50%'
            },
            {
                field: 'tarjoaja',
                displayName: LocalisationService.t('sisaltyvyys.hakutulos.organisaatio'),
                width: '50%'
            }];
        }
        else {
            return [{
                field: 'koulutuskoodi',
                displayName: LocalisationService.t('sisaltyvyys.hakutulos.arvo'),
                width: '20%'
            },
            {
                field: 'nimi',
                displayName: LocalisationService.t('sisaltyvyys.hakutulos.nimi'),
                width: '50%'
            },
            {
                field: 'tarjoaja',
                displayName: LocalisationService.t('sisaltyvyys.hakutulos.tarjoaja'),
                width: '30%'
            }];
        }
    };
}]);
app.controller('LiitaSisaltyvyysCtrl', [
    '$scope',
    'Config',
    'Koodisto',
    'LocalisationService',
    'TarjontaService',
    '$q',
    '$modalInstance',
    'targetKomo',
    'organisaatioOid',
    'SisaltyvyysUtil',
    'sisaltyvyysColumnDefs',
    'TreeHandlers',
    'AuthService',
    '$log', function LiitaSisaltyvyysCtrl($scope, config, koodisto, LocalisationService, TarjontaService, $q,
                  $modalInstance, targetKomo, organisaatio, SisaltyvyysUtil, sisaltyvyysColumnDefs, TreeHandlers,
                  AuthService, $log) {
        /*
             * Select koulutus data objects.
             */
        $scope.model = {
            errors: [],
            text: {
                headLabel: LocalisationService.t('sisaltyvyys.liitoksen-luonti-teksti.' + targetKomo.toteutustyyppi, [
                    targetKomo.nimi,
                    organisaatio.nimi
                ]),
                hierarchy: LocalisationService.t('sisaltyvyys.tab.hierarkia'),
                list: LocalisationService.t('sisaltyvyys.tab.lista')
            },
            organisaatio: organisaatio,
            treeOids: [],
            selectedOid: [targetKomo.oid],
            //directive needs an array
            searchKomoOids: [],
            selectedRowData: [],
            // a parent (selectedOid) will have new childs (selectedRowData)
            reviewOids: [],
            tutkinto: {
                uri: '',
                koodis: [],
                hakulause: ''
            },
            hakutulos: [],
            valitut: {
                //selected koulutus items
                oids: [],
                //only row oids
                data: [] //only row objects
            },
            spec: {
                //search parameter object
                oid: AuthService.getOrganisations(),
                terms: '',
                //search words
                year: targetKomo.vuosi,
                season: targetKomo.kausi.uri + '#' + targetKomo.kausi.versio,
                koulutustyyppi: ['koulutustyyppi_3'],
                type: config.app['tarjonta.koulutuslajiModuulityypit'][targetKomo.koulutusLaji]
            },
            html: 'partials/koulutus/sisaltyvyys/liita-koulutuksia-select.html',
            skipOids: [targetKomo.oid]
        };
        $scope.other = {
            tutkintotyypit: [
                //tutkintotyyppi koodisto koodit
                config.app['koodisto-uri.tutkintotyyppi.alempiKorkeakoulututkinto'],
                //kandi
                config.app['koodisto-uri.tutkintotyyppi.ylempiKorkeakoulututkinto'] //maisteri
            ],
            koulutuskoodiMap: {} //key : koulutuskoodi uri : tutkintotyypit
        };
        $scope.koodistoLocale = LocalisationService.getLocale();
        $scope.isOpinto = function() {
            return targetKomo.koulutusLaji === 'OPINTO';
        };
        var koodisPromise =
            koodisto.getAllKoodisWithKoodiUri(config.app['koodisto-uris.tutkintotyyppi'], $scope.koodistoLocale);
        koodisPromise.then(function(koodis) {
            for (var i = 0; i < koodis.length; i++) {
                for (var c = 0; c < $scope.other.tutkintotyypit.length; c++) {
                    if (koodis[i].koodiUri === $scope.other.tutkintotyypit[c]) {
                        $scope.model.tutkinto.koodis.push(koodis[i]);
                    }
                }
            }
        });
        /*
             * Filter all komos by selected tutkintotyyppi koodi.
             */
        $scope.updateTutkintotyyppiFilters = function() {
            var promises = [];
            angular.forEach($scope.other.tutkintotyypit, function(value) {
                var promise = koodisto.getYlapuolisetKoodit(value, $scope.koodistoLocale);
                promises.push(promise);
                promise.then(function(res) {
                    for (var i = 0; i < res.length; i++) {
                        if (res[i].koodiKoodisto === config.env['koodisto-uris.koulutus']) {
                            $scope.other.koulutuskoodiMap[res[i].koodiUri] = value;
                        }
                    }
                });
            });
            var deferred = $q.defer();
            promises.push(deferred.promise);
            TarjontaService.resourceLink.get({
                oid: targetKomo.oid
            }).$promise.then(function(res) {
                _.each(res.result, function(resultOid) {
                    $scope.model.skipOids.push(resultOid);
                });
                deferred.resolve();
            });
            $q.all(promises).then(function(koodisParam) {
                $scope.searchKomos();
            });
        };
        /*
             * Ng-grid component
             */
        $scope.gridOptions = {
            data: 'model.hakutulos',
            selectedItems: $scope.model.selectedRowData,
            // checkboxCellTemplate: '<div class="ngSelectionCell"><input tabindex="-1" class="ngSelectionCheckbox" type="checkbox" ng-checked="row.selected" /></div>',
            columnDefs: sisaltyvyysColumnDefs(targetKomo.koulutusLaji),
            showSelectionCheckbox: true,
            multiSelect: true
        };
        /*
             * Clear selected data from the search fields.
             */
        $scope.clearCriteria = function() {
            $scope.model.tutkinto.uri = '';
            $scope.model.tutkinto.hakulause = '';
        };
        /**
             * Search komos.
             */
        $scope.searchKomos = function() {
            TarjontaService.haeKoulutukset($scope.model.spec).then(function(result) {
                $scope.model.hakutulos = [];
                $scope.model.searchKomoOids = [];
                var arr = [];
                for (var i = 0; i < result.tulokset.length; i++) {
                    for (var c = 0; c < result.tulokset[i].tulokset.length; c++) {
                        var node = {
                            nimi: result.tulokset[i].tulokset[c].nimi,
                            tarjoaja: result.tulokset[i].nimi,
                            oid: result.tulokset[i].tulokset[c].komoOid
                        };
                        var koulutuskoodi = result.tulokset[i].tulokset[c].koulutuskoodi;
                        if (koulutuskoodi) {
                            var koulutuskoodiUri = koulutuskoodi.split('#')[0];
                            if ($scope.model.tutkinto.uri.length !== 0 &&
                                $scope.other.koulutuskoodiMap[koulutuskoodiUri] !== $scope.model.tutkinto.uri) {
                                continue;
                            }
                            node.koulutuskoodi = koulutuskoodiUri;
                        } else {
                            if (targetKomo.koulutusLaji === 'TUTKINTO') {
                                $log.error('koulutus without koulutuskoodi:', result.tulokset[i].tulokset[c]);
                            }
                        }
                        $scope.model.searchKomoOids.push(result.tulokset[i].tulokset[c].komoOid);
                        arr.push(node);
                    }
                }
                angular.forEach(arr, function(value) {
                    if (value.koulutuskoodi) {
                        var koodisPromise = koodisto.getKoodi(config.env['koodisto-uris.koulutus'], value.koulutuskoodi,
                                $scope.koodistoLocale);
                        koodisPromise.then(function(koodi) {
                            value.koulutuskoodi = koodi.koodiArvo;
                        });
                    }
                });
                arr = _.filter(arr, function(koulutus) {
                    return !_.contains($scope.model.skipOids, koulutus.oid);
                });
                $scope.model.hakutulos = arr;
            });
        };
        $scope.clearErrors = function() {
            $scope.model.errors = [];
        };
        /*
             * 2TAB tree event handlers
             */
        TreeHandlers.setScope($scope);
        $scope.selectTreeHandler = TreeHandlers.selectTreeHandler;
        $scope.removeItem = TreeHandlers.removeItem;
        /*
             * Save and close the dialog.
             */
        $scope.clickSave = function() {
            $scope.clearErrors();
            var oids = [];
            angular.forEach($scope.model.selectedRowData, function(val) {
                oids.push(val.oid);
            });
            TarjontaService.saveResourceLink(targetKomo.oid, oids, function(response) {
                var su = new SisaltyvyysUtil();
                $scope.model.errors = su.handleResult(targetKomo, response, $scope.model.selectedRowData,
                    $modalInstance);
            });
        };
        /*
             * Cancel and close the dialog.
             */
        $scope.clickCancel = function() {
            $modalInstance.dismiss();
        };
        /*
             * go back to select dialog.
             */
        $scope.clickSelectDialogi = function() {
            //aseta esivalittu organisaatio
            $scope.clearErrors();
            $scope.model.html = 'partials/koulutus/sisaltyvyys/liita-koulutuksia-select.html';
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
            $scope.model.html = 'partials/koulutus/sisaltyvyys/liita-koulutuksia-review.html';
        };
        /*
             * INIT FILTERS
             */
        $scope.updateTutkintotyyppiFilters();
    }
]);