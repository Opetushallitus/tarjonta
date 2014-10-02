var app = angular.module('app.edit.ctrl.amm', ['Koodisto', 'Yhteyshenkilo', 'ngResource', 'ngGrid', 'imageupload', 'MultiSelect', 'OrderByNumFilter', 'localisation', 'MonikielinenTextField', 'ControlsLayout']);
app.controller('EditNayttotutkintoController',
        function EditLukioController($q, $route, $timeout, $scope, $location, $log, TarjontaService, Config,
                                     $routeParams, OrganisaatioService, LocalisationService,
                                     $window, KoulutusConverterFactory, Koodisto, $modal, PermissionService, dialogService) {

            var ENUM_KOMO_MODULE_TUTKINTO = 'TUTKINTO';
            var ENUM_KOMO_MODULE_TUTKINTO_OHJELMA = 'TUTKINTO_OHJELMA';
            var ENUM_OPTIONAL_TOTEUTUS = 'AMMATILLINEN_NAYTTOTUTKINTONA_VALMISTAVA';
            $log = $log.getInstance("EditNayttotutkintoController");

            $scope.init = function () {
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
                    cbShowValmistavaKoulutus: true,
                    enableOsaamisala: false,
                    koulutusohjelma: [],
                    tutkintoModules: {},
                    koulutusohjelmaModules: {},
                    valmistavaLisatiedot: KoulutusConverterFactory.STRUCTURE[ENUM_OPTIONAL_TOTEUTUS].KUVAUS_ORDER
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
                    $scope.lisatiedot = KoulutusConverterFactory.STRUCTURE[$scope.CONFIG.TYYPPI].KUVAUS_ORDER;
                    $scope.loadKomoKuvausTekstis(null, uiModel, model.kuvausKomo, null);
                    $scope.loadRelationKoodistoData(model, uiModel, model.koulutuskoodi.uri, ENUM_KOMO_MODULE_TUTKINTO);
                    $scope.loadRelationKoodistoData(model, uiModel, model.koulutusohjelma.uri, ENUM_KOMO_MODULE_TUTKINTO_OHJELMA);

                    uiModel.enableOsaamisala = angular.isDefined(model.koulutusohjelma.uri);

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
                    tutkintoPromise.then(function (koodistoResult) {
                        resource.searchModules({koulutustyyppi: $scope.CONFIG.KOULUTUSTYYPPI, moduuli: ENUM_KOMO_MODULE_TUTKINTO}, function (komos) {
                            uiModel.tutkintoModules = KoulutusConverterFactory.filterByKomos(koodistoResult, komos);
                            uiModel.tutkinto = _.map(uiModel.tutkintoModules, function (num, key) {
                                return num;
                            });
                        });
                    });

                    //activate valmistava koulutus
                    $scope.initValmistavaKoulutus(model, uiModel, vkUiModel);

                } else {
                    KoulutusConverterFactory.throwError('unsupported $routeParams.type : ' + $routeParams.type + '.');
                }

                /*
                 * SHOW ALL KOODISTO KOODIS
                 */
                $scope.commonKoodistoLoadHandler(uiModel, $scope.CONFIG.TYYPPI);

                /*
                 * CUSTOM LOGIC
                 */
                // lisätietokielivalinnat
                $scope.getLisatietoKielet(model, uiModel, false);

                /*
                 * INIT SCOPES FOR RENDERER IN koulutusController.js
                 */
                model.toteutustyyppi = $scope.CONFIG.TYYPPI;
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
                }
            };

            $scope.loadRelationKoodistoData = function (apiModel, uiModel, uri, tutkintoTyyppi) {
                var strSearchKoulutuslaji = '';

                if (tutkintoTyyppi === ENUM_KOMO_MODULE_TUTKINTO) {
                    strSearchKoulutuslaji = 'koulutuslaji:' + (angular.isDefined($routeParams.koulutuslaji) ? $routeParams.koulutuslaji : apiModel.koulutuslaji.uri);
                }

                TarjontaService.getKoulutuskoodiRelations({
                    koulutustyyppi: $scope.CONFIG.KOULUTUSTYYPPI,
                    uri: uri,
                    languageCode: $scope.koodistoLocale,
                    //there is no real reation to koulutuslaji, so we will add it when module is 'TUTKINTO'
                    defaults: "tutkintonimike:tutkintonimikkeet_00000," + strSearchKoulutuslaji
                }, function (response) {
                    var restRelationData = response.result;
                    angular.forEach(KoulutusConverterFactory.STRUCTURE[$scope.CONFIG.TYYPPI].RELATION, function (value, key) {
                        if (tutkintoTyyppi === ENUM_KOMO_MODULE_TUTKINTO && (angular.isUndefined(value.module) || tutkintoTyyppi === value.module)) {
                            apiModel[key] = restRelationData[key];
                        } else if (tutkintoTyyppi === ENUM_KOMO_MODULE_TUTKINTO_OHJELMA && (angular.isUndefined(value.module) || tutkintoTyyppi === value.module)) {
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
            $scope.customCallbackAfterSave = function (saveResponse) {
                var resultModel = saveResponse.result;
                $scope.loadRelationKoodistoData($scope.model, $scope.uiModel, resultModel.koulutuskoodi.uri, ENUM_KOMO_MODULE_TUTKINTO);
                $scope.loadRelationKoodistoData($scope.model, $scope.uiModel, resultModel.koulutusohjelma.uri, ENUM_KOMO_MODULE_TUTKINTO_OHJELMA);
                $scope.getLisatietoKielet($scope.model, $scope.uiModel, false);
            }

            $scope.loadKomoKuvausTekstis = function (komoOid, uiModel, kuvausKomoto, moduulityyppi) {

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
                    if (angular.isDefined(kuvausKomoto['KOULUTUSOHJELMAN_TAVOITTEET'])) {
                        uiModel.koulutusohjelmanTavoitteet = $scope.getLang(kuvausKomoto['KOULUTUSOHJELMAN_TAVOITTEET'].tekstis);
                    }
                } else {
                    TarjontaService.komo().tekstis({oid: komoOid}, function (res) {
                        if (angular.isDefined(res.result['TAVOITTEET'])) {
                            $scope.uiModel.kuvausTavoite = $scope.getLang(res.result['TAVOITTEET'].tekstis);
                        }

                        if (angular.isDefined(res.result['KOULUTUKSEN_RAKENNE'])) {
                            $scope.uiModel.kuvausOpintojenRakenne = $scope.getLang(res.result['KOULUTUKSEN_RAKENNE'].tekstis);
                        }

                        if (angular.isDefined(res.result['JATKOOPINTO_MAHDOLLISUUDET'])) {
                            $scope.uiModel.jatkoOpintomahdollisuudet = $scope.getLang(res.result['JATKOOPINTO_MAHDOLLISUUDET'].tekstis);
                        }
                    });
                }
            };

            /*
             * WATCHES
             */
            $scope.$watch("model.koulutusohjelma.uri", function (uri, oUri) {
                if (angular.isDefined(uri) && uri !== null && oUri !== uri) {

                    if (angular.isDefined($scope.uiModel.koulutusohjelmaModules[uri])) {
                        var komoOid = $scope.uiModel.koulutusohjelmaModules[uri].oid;
                        TarjontaService.komo().tekstis({oid: komoOid}, function (res) {
                            if (angular.isDefined(res.result['TAVOITTEET'])) {
                                $scope.uiModel.koulutusohjelmanTavoitteet = $scope.getLang(res.result['TAVOITTEET'].tekstis);
                            }
                        });

                        $scope.updateKomoOidToModule(komoOid);
                        $scope.loadRelationKoodistoData($scope.model, $scope.uiModel, uri, ENUM_KOMO_MODULE_TUTKINTO_OHJELMA);

                    } else {
                        $log.error("missing koulutus by " + uri);
                    }
                }
            });

            $scope.$watch("model.koulutuskoodi.uri", function (uriNew, uriOld) {
                if (angular.isDefined(uriNew) && uriNew !== null && uriOld !== uriNew) {
                    $scope.uiModel.koulutusohjelmaModules = {};
                    $scope.uiModel.koulutusohjelma = [];
                    if (angular.isDefined($scope.model.koulutusohjelma)) {
                        $scope.model.koulutusohjelma.uri = null;
                    }

                    $scope.loadRelationKoodistoData($scope.model, $scope.uiModel, uriNew, ENUM_KOMO_MODULE_TUTKINTO);
                    $scope.loadKomoKuvausTekstis($scope.uiModel.tutkintoModules[uriNew].oid);
                    var resource = TarjontaService.komo();

                    var listOfTutkintoModules = _.map($scope.uiModel['tutkintoModules'], function (num, key) {
                        return num.koodiUri;
                    });

                    $scope.uiModel.tutkinto = _.map($scope.uiModel['tutkintoModules'], function (num, key) {
                        return num;
                    });

                    var koulutusohjelma = Koodisto.getAlapuolisetKoodiUrit(listOfTutkintoModules, null, $scope.koodistoLocale);
                    koulutusohjelma.then(function (kRes) {
                        resource.searchModules(
                            {
                                koulutus: uriNew,
                                koulutustyyppi: $scope.CONFIG.KOULUTUSTYYPPI,
                                moduuli: ENUM_KOMO_MODULE_TUTKINTO_OHJELMA
                            }, function (tRes) {
                            $scope.uiModel.enableOsaamisala = false;

                            for (var il = 0; il < kRes.uris.length; il++) {
                                //keep only 'tutkinto-ohjelma' type of uris
                                if ($scope.isTutkintoOhjelmaKoodisto, kRes.map[kRes.uris[il]]) {
                                    for (var cl = 0; cl < tRes.result.length; cl++) {
                                        if (!angular.isDefined($scope.uiModel.koulutusohjelmaModules [ kRes.uris[il] ]) && kRes.uris[il] === tRes.result[cl].ohjelmaUri) {
                                            $scope.uiModel.koulutusohjelmaModules [ kRes.uris[il]] = kRes.map[ kRes.uris[il]];
                                            $scope.uiModel.koulutusohjelmaModules [ kRes.uris[il]].oid = tRes.result[cl].oid;
                                            $scope.uiModel.koulutusohjelmaModules [ kRes.uris[il]].koulutuskoodi = tRes.result[cl].koulutuskoodiUri;
                                        }
                                    }
                                    $scope.uiModel.koulutusohjelma = _.map($scope.uiModel.koulutusohjelmaModules, function (num, key) {
                                        return num;
                                    });
                                    //selected education module do not have 'osaamisala' -field
                                    //remove the html select field from te html page
                                    $scope.uiModel.enableOsaamisala = $scope.uiModel.koulutusohjelma.length > 0;

                                    if (!$scope.uiModel.enableOsaamisala) {
                                        $scope.updateKomoOidToModule($scope.uiModel.tutkintoModules[uriNew].oid);
                                    }
                                }
                            }
                        });
                    });
                }
            });

            $scope.updateKomoOidToModule = function (oid) {
                $scope.getModel().komoOid = oid;
            };
            $scope.saveLuonnos = function () {
                $scope.saveByStatus('LUONNOS');
            };
            $scope.saveValmis = function () {
                $scope.saveByStatus('VALMIS');
            };

            $scope.saveByStatus = function (tila) {
                $scope.vkUiModel.showValidationErrors = true;
                var apiModel = angular.copy($scope.model);

                apiModel.toteutustyyppi = $scope.CONFIG.TYYPPI;

                if (angular.isDefined(apiModel.valmistavaKoulutus) && apiModel.valmistavaKoulutus !== null) {
                    apiModel.valmistavaKoulutus = KoulutusConverterFactory.saveModelConverter(apiModel.valmistavaKoulutus, $scope.vkUiModel, ENUM_OPTIONAL_TOTEUTUS);
                }
                $scope.saveApimodelByStatus(apiModel, tila, $scope.koulutusForm, $scope.CONFIG.TYYPPI, $scope.customCallbackAfterSave);
            };

            $scope.onMaksullisuusChanged = function (model) {
                if (!model.hinta) {
                    return;
                }
                var p = model.hinta.indexOf(',');
                while (p != -1) {
                    model.hinta = model.hinta.substring(0, p) + "." + model.hinta.substring(p + 1);
                    p = model.hinta.indexOf(',', p);
                }
            };


            $scope.getValmistavaKuvausApiModelLanguageUri = function (textEnum, kieliUri) {
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


            $scope.getEditValmistavaKoulutusPerustiedot = function () {
                return '/partials/koulutus/edit/amm/editValmistavaKoulutusPerustiedot.html';
            };

            $scope.getEditValmistavaKoulutusPerustiedot = function () {
                return '/partials/koulutus/edit/amm/editValmistavaKoulutusPerustiedot.html';
            };

            $scope.openJarjestajaDialog = function () {
                var copyModalDialog = $modal.open({
                    templateUrl: 'partials/koulutus/edit/amm/jarjestaja.html',
                    controller: 'JarjestajaCtrl',
                    resolve: {
                        targetOrganisaatio: function () {
                            return  {}
                        }
                    }
                });

                copyModalDialog.result.then(function (organisaatio) {
                    /* ok */
                    $scope.model.jarjestavaOrganisaatio = organisaatio;
                }, function () {
                    /* dismissed */
                });
            };

            $scope.$watch("model.opintojenMaksullisuus", function (valNew, valOld) {
                if (!valNew && valOld) {
                    //clear price data field
                    $scope.model.hinta = '';
                }
            });

            $scope.$watch("model.valmistavaKoulutus.opintojenMaksullisuus", function (valNew, valOld) {
                if (!valNew && valOld && angular.isDefined($scope.model.valmistavaKoulutus) && $scope.model.valmistavaKoulutus != null) {
                    //clear price data field
                    $scope.model.valmistavaKoulutus.hinta = '';
                }
            });

            $scope.initValmistavaKoulutus = function (apimodel, uiModel, vkUiModel) {
                var model = {};
                $scope.commonNewModelHandler($scope.koulutusForm, model, vkUiModel, ENUM_OPTIONAL_TOTEUTUS);
                apimodel.valmistavaKoulutus = model;
                $scope.commonKoodistoLoadHandler(vkUiModel, ENUM_OPTIONAL_TOTEUTUS);
                vkUiModel.showValidationErrors = false;
                uiModel.toggleTabs = true;
            };

            $scope.$watch("uiModel.cbShowValmistavaKoulutus", function (valNew, valOld) {
                if (valNew && ($scope.model.valmistavaKoulutus === null || !angular.isDefined($scope.model.valmistavaKoulutus))) {

                    $scope.initValmistavaKoulutus($scope.model, $scope.uiModel, $scope.vkUiModel);
                } else if (valNew !== valOld && angular.isDefined($scope.model.valmistavaKoulutus)) {
                    var modalInstance = $modal.open({
                        scope: $scope,
                        templateUrl: 'partials/koulutus/edit/amm/poista-valmistava-koulutus-dialog.html',
                        controller: function ($scope) {
                            $scope.ok = function () {
                                //delete
                                $scope.uiModel.cbShowValmistavaKoulutus = false;
                                $scope.uiModel.toggleTabs = false;
                                $scope.model.valmistavaKoulutus = null;
                                modalInstance.dismiss();
                            };
                            $scope.cancel = function () {
                                //do nothing.
                                $scope.uiModel.cbShowValmistavaKoulutus = true;
                                modalInstance.dismiss();
                            };
                            return $scope;
                        }
                    });
                }
            });

            $scope.onValmistavaLisatietoLangSelection = function (uris) {
                if (uris.removed && $scope.uiModel.opetuskielis.uris) {

                    // ei opetuskieli -> varmista poisto dialogilla
                    dialogService.showDialog({
                        ok: LocalisationService.t("tarjonta.poistovahvistus.koulutus.lisatieto.poista"),
                        title: LocalisationService.t("tarjonta.poistovahvistus.koulutus.lisatieto.title"),
                        description: LocalisationService.t("tarjonta.poistovahvistus.koulutus.lisatieto", [$scope.langs[uris.removed]])
                    }).result.then(function (ret) {
                        if (ret) {
                            $scope.deleteKuvausByStructureType($scope.CONFIG.TYYPPI, uris.removed);

                            if ($scope.model.valmistavaKoulutus && $scope.model.valmistavaKoulutus.kuvaus) {
                                for (var ki in $scope.model.valmistavaKoulutus.kuvaus) {
                                    $scope.model.valmistavaKoulutus.kuvaus[ki].tekstis[uris.removed] = null;
                                }
                            }
                        } else {
                            //cancelled remove, put uri back to the lang array
                            if ($scope.uiModel.lisatietoKielet.indexOf(uris.removed) === -1) {
                                $scope.uiModel.lisatietoKielet.push(uris.removed);
                            }
                        }
                    });

                } else if (uris.added && $scope.uiModel.lisatietoKielet) {

                    if ($scope.uiModel.lisatietoKielet.indexOf(uris.added) === -1) {
                        $scope.uiModel.lisatietoKielet.push(uris.added);
                    }
                }
            };

            $scope.init();
        });
