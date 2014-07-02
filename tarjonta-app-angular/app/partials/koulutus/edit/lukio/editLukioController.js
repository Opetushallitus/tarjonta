var app = angular.module('app.edit.ctrl.lukio', ['Koodisto', 'Yhteyshenkilo', 'ngResource', 'ngGrid', 'imageupload', 'MultiSelect', 'OrderByNumFilter', 'localisation', 'MonikielinenTextField', 'ControlsLayout']);
app.controller('EditLukioController',
        ['$q', '$route', '$timeout', '$scope', '$location', '$log', 'TarjontaService', 'Config', '$routeParams', 'OrganisaatioService', 'LocalisationService',
            '$window', 'KoulutusConverterFactory', 'Koodisto', '$modal', 'PermissionService', 'dialogService', 'CommonUtilService',
            function EditLukioController($q, $route, $timeout, $scope, $location, $log, TarjontaService, cfg, $routeParams, organisaatioService, LocalisationService,
                    $window, converter, Koodisto, $modal, PermissionService, dialogService, CommonUtilService) {

                var ENUM_KOMO_MODULE_TUTKINTO = 'TUTKINTO';
                var ENUM_KOMO_MODULE_TUTKINTO_OHJELMA = 'TUTKINTO_OHJELMA';
                $log = $log.getInstance("LukioEditController");

                $scope.init = function() {
                    $log.debug("init");

                    /*
                     * INITIALIZE PAGE CONFIG
                     */
                    $scope.commonCreatePageConfig($routeParams, $route.current.locals.koulutusModel.result);

                    var model = {};
                    var uiModel = {
                        loadedKoulutuslaji: null, //a hack : esta nuorten lukiokoulutuksen tallennus
                        //custom stuff
                        koulutusohjelma: [],
                        tutkintoModules: {},
                        koulutusohjelmaModules: {}
                    };

                    /*
                     * HANDLE EDIT / CREATE NEW ROUTING
                     */
                    if (!angular.isUndefined($routeParams.id) && $routeParams.id !== null && $routeParams.id.length > 0) {
                        /*
                         *  SHOW KOULUTUS BY GIVEN KOMOTO OID
                         *  Look more info from koulutusController.js.
                         */
                        model = $route.current.locals.koulutusModel.result;
                        uiModel.loadedKoulutuslaji = angular.copy(model.koulutuslaji);
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
                        var tutkintoPromise = Koodisto.getYlapuolisetKoodiUrit(['koulutustyyppi_2'], 'koulutus', $scope.koodistoLocale);
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
                    uiModel.lisatietoKielet = angular.copy(uiModel.opetuskielis.uris);
                    for (var ki in model.kuvausKomo) {
                        for (var lc in model.kuvausKomo[ki].tekstis) {
                            if (uiModel.lisatietoKielet.indexOf(lc) == -1) {
                                uiModel.lisatietoKielet.push(lc);
                            }
                        }
                    }

                    /*
                     * INIT SCOPES FOR RENDERER IN koulutusController.js
                     */
                    $scope.setUiModel(uiModel);
                    $scope.setModel(model);
                };

                $scope.loadRelationKoodistoData = function(apiModel, uiModel, koodiUri, tutkintoTyyppi) {
                    TarjontaService.getKoulutuskoodiRelations(
                            {
                                koulutustyyppi: $scope.CONFIG.KOULUTUSTYYPPI,
                                uri: koodiUri,
                                defaults: "koulutuslaji:koulutuslaji_a,pohjakoulutusvaatimus:pohjakoulutustoinenaste_1,koulutustyyppi:" + $scope.CONFIG.KOULUTUSTYYPPI,
                                languageCode: $scope.koodistoLocale
                            }, function(data) {
                        var restRelationData = data.result;
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
                        $scope.model.koulutusohjelma.uri = null;

                        $scope.loadRelationKoodistoData($scope.model, $scope.uiModel, uriNew, ENUM_KOMO_MODULE_TUTKINTO);
                        $scope.loadKomoKuvausTekstis($scope.uiModel.tutkintoModules[uriNew].oid);
                        var resource = TarjontaService.komo();

                        var listOfTutkintoModules = _.map($scope.uiModel['tutkintoModules'], function(num, key) {
                            return num.koodiUri;
                        });

                        $scope.uiModel.tutkinto = _.map($scope.uiModel['tutkintoModules'], function(num, key) {
                            return num;
                        });

                        var lukiolinjaPromise = Koodisto.getAlapuolisetKoodiUrit(listOfTutkintoModules, null, $scope.koodistoLocale);
                        lukiolinjaPromise.then(function(kRes) {
                            resource.searchModules(
                                    {
                                        koulutus: uriNew,
                                        koulutustyyppi: $scope.CONFIG.KOULUTUSTYYPPI,
                                        moduuli: ENUM_KOMO_MODULE_TUTKINTO_OHJELMA
                                    }, function(tRes) {
                                for (var il = 0; il < kRes.uris.length; il++) {

                                    for (var cl = 0; cl < tRes.result.length; cl++) {
                                        if (!angular.isDefined($scope.uiModel.koulutusohjelmaModules [ kRes.uris[il] ]) && kRes.uris[il] === tRes.result[cl].ohjelmaUri) {
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
                    $scope.saveByStatus('LUONNOS', $scope.koulutusForm, $scope.CONFIG.TYYPPI, $scope.customCallbackAfterSave);
                };
                $scope.saveValmis = function() {
                    $scope.saveByStatus('VALMIS', $scope.koulutusForm, $scope.CONFIG.TYYPPI, $scope.customCallbackAfterSave);
                };

                $scope.init();
            }]);
