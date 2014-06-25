var app = angular.module('app.edit.ctrl.amm', ['Koodisto', 'Yhteyshenkilo', 'ngResource', 'ngGrid', 'imageupload', 'MultiSelect', 'OrderByNumFilter', 'localisation', 'MonikielinenTextField', 'ControlsLayout']);
app.controller('EditNayttotutkintoController',
        ['$q', '$route', '$timeout', '$scope', '$location', '$log', 'TarjontaService', 'Config', '$routeParams', 'OrganisaatioService', 'LocalisationService',
            '$window', 'KoulutusConverterFactory', 'Koodisto', '$modal', 'PermissionService', 'dialogService', 'CommonUtilService',
            function EditLukioController($q, $route, $timeout, $scope, $location, $log, TarjontaService, cfg, $routeParams, organisaatioService, LocalisationService,
                    $window, converter, Koodisto, $modal, PermissionService, dialogService, CommonUtilService) {

                var ENUM_KOMO_MODULE_TUTKINTO = 'TUTKINTO';
                var ENUM_KOMO_MODULE_TUTKINTO_OHJELMA = 'TUTKINTO_OHJELMA';
                var ENUM_OPTIONAL_TOTEUTUS = 'AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA_VALMISTAVA';
                $log = $log.getInstance("EditNayttotutkintoController");

                $scope.init = function() {
                    $log.debug("init");

                    /*
                     * INITIALIZE PAGE CONFIG
                     */
                    $scope.commonCreatePageConfig($routeParams, $route.current.locals.koulutusModel.result);
                    var model = {
                        valmistavaKoulutus: null
                    };
                    var uiModel = {
                        //custom stuff
                        toggleTabs: false,
                        cbShowValmistavaKoulutus: false,
                        koulutusohjelma: [],
                        tutkintoModules: {},
                        koulutusohjelmaModules: {}
                    };

                    //valmistava koulutus
                    var vkUiModel = {};

                    /*
                     * HANDLE EDIT / CREATE NEW ROUTING
                     */
                    if (!angular.isUndefined($routeParams.id) && $routeParams.id !== null && $routeParams.id.length > 0) {
                        /*
                         *  SHOW KOULUTUS BY GIVEN KOMOTO OID
                         *  Look more info from koulutusController.js.
                         */
                        model = $route.current.locals.koulutusModel.result;
                        if (angular.isDefined(model.valmistavaKoulutus) && model.valmistavaKoulutus !== null) {
                            $scope.commonLoadModelHandler($scope.koulutusForm, model.valmistavaKoulutus, vkUiModel, ENUM_OPTIONAL_TOTEUTUS);
                            $scope.commonKoodistoLoadHandler(vkUiModel, ENUM_OPTIONAL_TOTEUTUS);
                            vkUiModel.showValidationErrors = true;
                        }

                        $scope.commonLoadModelHandler($scope.koulutusForm, model, uiModel, $scope.CONFIG.TYYPPI);
                        /*
                         * CUSTOM LOGIC : LOAD KOULUTUSKOODI + LUKIOLINJA KOODI OBJECTS
                         */
                        $scope.lisatiedot = converter.STRUCTURE[$scope.CONFIG.TYYPPI].KUVAUS_ORDER;
                        $scope.loadKomoKuvausTekstis(null, uiModel, model.kuvausKomo);
                        $scope.loadRelationKoodistoData(model, uiModel, model.koulutuskoodi.uri, ENUM_KOMO_MODULE_TUTKINTO);
                        $scope.loadRelationKoodistoData(model, uiModel, model.koulutusohjelma.uri, ENUM_KOMO_MODULE_TUTKINTO_OHJELMA);
                        
                    } else if (!angular.isUndefined($routeParams.org)) {
                        /*
                         * CREATE NEW KOULUTUS BY ORG OID AND KOULUTUSKOODI
                         * Look more info from koulutusController.js.
                         */
                        $scope.commonNewModelHandler($scope.koulutusForm, model, uiModel, $scope.CONFIG.TYYPPI);


                        /*
                         * CUSTOM LOGIC : LOAD KOULUTUSKOODI + LUKIOLINJA KOODI OBJECTS
                         */
                        var resource = TarjontaService.komo();
                        var tutkintoPromise = Koodisto.getYlapuolisetKoodiUrit([$scope.CONFIG.KOULUTUSTYYPPI], 'koulutus', $scope.koodistoLocale);
                        tutkintoPromise.then(function(kRes) {
                            resource.searchModules({koulutustyyppi: $scope.CONFIG.KOULUTUSTYYPPI, moduuli: ENUM_KOMO_MODULE_TUTKINTO}, function(tRes) {
                                for (var i = 0; i < kRes.uris.length; i++) {
                                    for (var c = 0; c < tRes.result.length; c++) {
                                        if (!angular.isDefined(uiModel['tutkintoModules'][ kRes.uris[i] ]) && kRes.uris[i] === tRes.result[c].koulutuskoodiUri) {
                                            uiModel['tutkintoModules'][ kRes.uris[i]] = kRes.map[ kRes.uris[i]];
                                            uiModel['tutkintoModules'][ kRes.uris[i]].oid = tRes.result[c].oid;
                                        }
                                    }
                                }
                                uiModel.tutkinto = _.map(uiModel['tutkintoModules'], function(num, key) {
                                    return num;
                                });
                            });
                        });
                    } else {
                        converter.throwError('unsupported $routeParams.type : ' + $routeParams.type + '.');
                    }

                    /*
                     * SHOW ALL KOODISTO KOODIS
                     */
                    $scope.commonKoodistoLoadHandler(uiModel, $scope.CONFIG.TYYPPI);

                    /*
                     * CUSTOM LOGIC
                     */
                    // lisätietokielivalinnat
                    uiModel.lisatietoKielet = _.keys(model.opetuskielis.uris);
                    vkUiModel.lisatietoKielet = _.keys(model.opetuskielis.uris);

                    for (var ki in model.kuvausKomo) {
                        if (angular.isDefined(model.kuvausKomo[ki])) {
                            for (var lc in model.kuvausKomo[ki].tekstis) {
                                if (uiModel.lisatietoKielet.indexOf(lc) == -1) {
                                    uiModel.lisatietoKielet.push(lc);
                                }
                            }
                        }
                    }

                    /*
                     * INIT SCOPES FOR RENDERER IN koulutusController.js
                     */
                    $scope.setModel(model);

                    //Ui model for editPerustiedot and editLisatiedot pages (normal case)
                    $scope.setUiModel(uiModel);
                    //Ui model for editValmistavaKoulutusPerustiedot and eeditValmistavaKoulutusLisatiedot pages (special case)
                    $scope.vkUiModel = vkUiModel;

                    if (angular.isDefined(model.valmistavaKoulutus) && model.valmistavaKoulutus !== null) {
                        $scope.uiModel.cbShowValmistavaKoulutus = true;
                        $scope.uiModel.toggleTabs = true;
                    } else {
                        $scope.uiModel.cbShowValmistavaKoulutus = false;
                        $scope.uiModel.toggleTabs = false;
                    }
                };

                $scope.loadRelationKoodistoData = function(apiModel, uiModel, uri, tutkintoTyyppi) {
                    var strSearchKoulutuslaji = '';

                    if (tutkintoTyyppi === ENUM_KOMO_MODULE_TUTKINTO) {
                        strSearchKoulutuslaji = 'koulutuslaji:' + (angular.isDefined($routeParams.koulutuslaji) ? $routeParams.koulutuslaji : apiModel.koulutuslaji.uri);
                    }

                    TarjontaService.getKoulutuskoodiRelations({
                        koulutustyyppi: $scope.CONFIG.TYYPPI,
                        uri: uri,
                        languageCode: $scope.koodistoLocale,
                        //there is no real reation to koulutuslaji, so we will add it when module is 'TUTKINTO'
                        defaults: strSearchKoulutuslaji
                    }, function(response) {
                        var restRelationData = response.result;
                        angular.forEach(converter.STRUCTURE[$scope.CONFIG.TYYPPI].RELATION, function(value, key) {
                            if (angular.isDefined(value.module) && tutkintoTyyppi === ENUM_KOMO_MODULE_TUTKINTO && tutkintoTyyppi === value.module) {
                                apiModel[key] = restRelationData[key];
                            } else if (angular.isDefined(value.module) && tutkintoTyyppi === ENUM_KOMO_MODULE_TUTKINTO_OHJELMA && tutkintoTyyppi === value.module) {
                                apiModel[key] = restRelationData[key];
                            }
                        });
                    });
                };
                /**
                 * Save koulutus data to tarjonta-service database.
                 * TODO: strict data validation, exception handling and optimistic locking
                 */

                /* handle */
                $scope.customCallbackAfterSave = function(saveResponse) {
                    var resultModel = saveResponse.result;
                    $scope.loadRelationKoodistoData($scope.model, $scope.uiModel, resultModel.koulutuskoodi.uri, ENUM_KOMO_MODULE_TUTKINTO);
                    $scope.loadRelationKoodistoData($scope.model, $scope.uiModel, resultModel.koulutusohjelma.uri, ENUM_KOMO_MODULE_TUTKINTO_OHJELMA);
                }

                $scope.loadKomoKuvausTekstis = function(komoOid, uiModel, kuvausKomoto) {
                    if (angular.isDefined(kuvausKomoto) && komoOid === null && kuvausKomoto) {
                        if (angular.isDefined(kuvausKomoto['TAVOITTEET'])) {
                            uiModel.kuvausTavoite = $scope.getLang(kuvausKomoto['TAVOITTEET'].tekstis);
                        }
                        if (angular.isDefined(kuvausKomoto['KOULUTUKSEN_RAKENNE'])) {
                            uiModel.kuvausOpintojenRakenne = $scope.getLang(kuvausKomoto['KOULUTUKSEN_RAKENNE'].tekstis);
                        }
                        if (angular.isDefined(kuvausKomoto['JATKOOPINTO_MAHDOLLISUUDET'])) {
                            uiModel.jatkoOpintomahdollisuudet = $scope.getLang(kuvausKomoto['JATKOOPINTO_MAHDOLLISUUDET'].tekstis);
                        }
                    } else {
                        TarjontaService.komo().tekstis({oid: komoOid}, function(res) {
                            console.log(res.result);
                            $scope.uiModel.kuvausTavoite = $scope.getLang(res.result['TAVOITTEET'].tekstis);
                            $scope.uiModel.kuvausOpintojenRakenne = $scope.getLang(res.result['KOULUTUKSEN_RAKENNE'].tekstis);
                            $scope.uiModel.jatkoOpintomahdollisuudet = $scope.getLang(res.result['JATKOOPINTO_MAHDOLLISUUDET'].tekstis);
                        });
                    }
                };

                /*
                 * WATCHES
                 */
                $scope.$watch("model.koulutusohjelma.uri", function(uri, oUri) {
                    if (angular.isDefined(uri) && uri != null && oUri != uri) {

                        if (angular.isDefined($scope.uiModel.koulutusohjelmaModules[uri])) {
                            $scope.model.komoOid = $scope.uiModel.koulutusohjelmaModules[uri].oid;
                            $scope.loadRelationKoodistoData($scope.model, $scope.uiModel, uri, ENUM_KOMO_MODULE_TUTKINTO_OHJELMA);
                        } else {
                            $log.error("missing koulutus by " + uri);
                        }
                    }
                });

                $scope.$watch("model.koulutuskoodi.uri", function(uriNew, uriOld) {
                    if (angular.isDefined(uriNew) && uriNew != null && uriOld != uriNew) {
                        $scope.uiModel.koulutusohjelmaModules = {};
                        $scope.uiModel.koulutusohjelma = [];
                        if (angular.isDefined($scope.model.koulutusohjelma)) {
                            $scope.model.koulutusohjelma.uri = null;
                        }

                        $scope.loadRelationKoodistoData($scope.model, $scope.uiModel, uriNew, ENUM_KOMO_MODULE_TUTKINTO);
                        $scope.loadKomoKuvausTekstis($scope.uiModel.tutkintoModules[uriNew].oid);
                        var resource = TarjontaService.komo();

                        var listOfTutkintoModules = _.map($scope.uiModel['tutkintoModules'], function(num, key) {
                            return num.koodiUri;
                        });

                        $scope.uiModel.tutkinto = _.map($scope.uiModel['tutkintoModules'], function(num, key) {
                            return num;
                        });

                        var koulutusohjelma = Koodisto.getAlapuolisetKoodiUrit(listOfTutkintoModules, null, $scope.koodistoLocale);
                        koulutusohjelma.then(function(kRes) {
                            resource.searchModules(
                                    {
                                        koulutus: uriNew,
                                        koulutustyyppi: $scope.CONFIG.KOULUTUSTYYPPI,
                                        moduuli: ENUM_KOMO_MODULE_TUTKINTO_OHJELMA
                                    }, function(tRes) {
                                for (var il = 0; il < kRes.uris.length; il++) {

                                    for (var cl = 0; cl < tRes.result.length; cl++) {
                                        if (!angular.isDefined($scope.uiModel.koulutusohjelmaModules [ kRes.uris[il] ]) && kRes.uris[il] === tRes.result[cl].koulutusohjelmaUri) {
                                            $scope.uiModel.koulutusohjelmaModules [ kRes.uris[il]] = kRes.map[ kRes.uris[il]];
                                            $scope.uiModel.koulutusohjelmaModules [ kRes.uris[il]].oid = tRes.result[cl].oid;
                                            $scope.uiModel.koulutusohjelmaModules [ kRes.uris[il]].koulutuskoodi = tRes.result[cl].koulutuskoodiUri;
                                        }
                                    }
                                    $scope.uiModel.koulutusohjelma = _.map($scope.uiModel.koulutusohjelmaModules, function(num, key) {
                                        return num;
                                    });
                                }
                            });
                        });
                    }
                });

                $scope.saveLuonnos = function() {
                    $scope.saveByStatus('LUONNOS');
                };
                $scope.saveValmis = function() {
                    $scope.saveByStatus('VALMIS');
                };

                $scope.saveByStatus = function(tila) {
                    $scope.vkUiModel.showValidationErrors = true;
                    var apiModel = angular.copy($scope.model);

                    if (angular.isDefined(apiModel.valmistavaKoulutus) && apiModel.valmistavaKoulutus !== null) {
                        apiModel.valmistavaKoulutus = converter.saveModelConverter(apiModel.valmistavaKoulutus, $scope.vkUiModel, ENUM_OPTIONAL_TOTEUTUS);
                    }
                    $scope.saveApimodelByStatus(apiModel, tila, $scope.koulutusForm, $scope.CONFIG.TYYPPI, $scope.customCallbackAfterSave);
                };

                $scope.onMaksullisuusChanged = function(model) {
                    if (!model.hinta) {
                        return;
                    }
                    var p = model.hinta.indexOf(',');
                    while (p != -1) {
                        model.hinta = model.hinta.substring(0, p) + "." + model.hinta.substring(p + 1);
                        p = model.hinta.indexOf(',', p);
                    }
                };


                $scope.getValmistavaKuvausApiModelLanguageUri = function(textEnum, kieliUri) {
                    if (!kieliUri) {
                        return {};
                    }

                    if (!$scope.uiModel.toggleTabs || $scope.model.valmistavaKoulutus == null) {
                        return {};
                    }

                    var kuvaus = $scope.model.valmistavaKoulutus.kuvaus;

                    if (angular.isUndefined(kuvaus[textEnum])) {
                        kuvaus[textEnum] = {tekstis: {}};
                        if (!angular.isUndefined(kieliUri)) {
                            kuvaus[textEnum].tekstis[kieliUri] = '';
                        }
                    }

                    return kuvaus[textEnum].tekstis;
                };


                $scope.getValmistavaLisatietoKielet = function() {
                    for (var i in $scope.vkUiModel.opetuskielis.uris) {
                        var lc = $scope.vkUiModel.opetuskielis.uris[i];
                        if ($scope.vkUiModel.lisatietoKielet.indexOf(lc) == -1) {
                            $scope.vkUiModel.lisatietoKielet.push(lc);
                        }
                    }
                    return $scope.vkUiModel.lisatietoKielet;
                };

                $scope.getEditValmistavaKoulutusPerustiedot = function() {
                    return '/partials/koulutus/edit/amm/editValmistavaKoulutusPerustiedot.html';
                };

                $scope.getEditValmistavaKoulutusPerustiedot = function() {
                    return '/partials/koulutus/edit/amm/editValmistavaKoulutusPerustiedot.html';
                };

                $scope.openJarjestajaDialog = function() {
                    var copyModalDialog = $modal.open({
                        templateUrl: 'partials/koulutus/edit/amm/jarjestaja.html',
                        controller: 'JarjestajaCtrl',
                        resolve: {
                            targetOrganisaatio: function() {
                                return  {}
                            }
                        }
                    });

                    copyModalDialog.result.then(function(organisaatio) {
                        /* ok */
                        $scope.model.jarjestavaOrganisaatio = organisaatio;
                    }, function() {
                        /* dismissed */
                    });
                };

                $scope.$watch("model.opintojenMaksullisuus", function(valNew, valOld) {
                    if (!valNew && valOld) {
                        //clear price data field
                        $scope.model.hinta = '';
                    }
                });

                $scope.$watch("model.valmistavaKoulutus.opintojenMaksullisuus", function(valNew, valOld) {
                    if (!valNew && valOld && angular.isDefined($scope.model.valmistavaKoulutus) && $scope.model.valmistavaKoulutus != null) {
                        //clear price data field
                        $scope.model.valmistavaKoulutus.hinta = '';
                    }
                });

                $scope.clickShowValmistavaKoulutus = function() {
                    if ($scope.uiModel.cbShowValmistavaKoulutus) {

                        var model = {};
                        $scope.commonNewModelHandler($scope.koulutusForm, model, $scope.vkUiModel, ENUM_OPTIONAL_TOTEUTUS);
                        $scope.model.valmistavaKoulutus = model;
                        $scope.commonKoodistoLoadHandler($scope.vkUiModel, ENUM_OPTIONAL_TOTEUTUS);
                        $scope.vkUiModel.selectedKieliUri = "kieli_fi";
                        $scope.vkUiModel.showValidationErrors = false;

                        $scope.uiModel.toggleTabs = true;
                    } else {
                        var modalInstance = $modal.open({
                            scope: $scope,
                            templateUrl: 'partials/koulutus/edit/amm/poista-valmistava-koulutus-dialog.html',
                            controller: function($scope) {
                                $scope.ok = function() {
                                    //delete
                                    $scope.uiModel.cbShowValmistavaKoulutus = false;
                                    $scope.uiModel.toggleTabs = false;
                                    $scope.model.valmistavaKoulutus = null;
                                    modalInstance.dismiss();
                                };
                                $scope.cancel = function() {
                                    //do nothing.
                                    $scope.uiModel.cbShowValmistavaKoulutus = true;
                                    modalInstance.dismiss();
                                };
                                return $scope;
                            }
                        });
                    }
                };

                $scope.init();
            }]);
