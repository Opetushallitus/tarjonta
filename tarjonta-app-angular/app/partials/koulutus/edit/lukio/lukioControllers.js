var app = angular.module('app.edit.ctrl.lukio', ['Koodisto', 'Yhteyshenkilo', 'ngResource', 'ngGrid', 'imageupload', 'MultiSelect', 'OrderByNumFilter', 'localisation', 'MonikielinenTextField', 'ControlsLayout']);
app.controller('LukioEditController',
        ['$q', '$route', '$timeout', '$scope', '$location', '$log', 'TarjontaService', 'Config', '$routeParams', 'OrganisaatioService', 'LocalisationService',
            '$window', 'KoulutusConverterFactoryLukio', 'Koodisto', '$modal', 'PermissionService', 'dialogService', 'CommonUtilService',
            function LukioEditController($q, $route, $timeout, $scope, $location, $log, TarjontaService, cfg, $routeParams, organisaatioService, LocalisationService,
                    $window, converter, Koodisto, $modal, PermissionService, dialogService, CommonUtilService) {

                $log = $log.getInstance("LukioEditController");
                $log.debug("init");

                //käyttöoikeudet
                $scope.isMutable = true;
                $scope.userLanguages = cfg.app.userLanguages; // opetuskielien esijärjestystä varten
                $scope.opetuskieli = cfg.app.userLanguages[0]; //index 0 = fi uri
                $scope.KoodistoLocale = LocalisationService.getLocale();//"FI";
                $scope.model = null;
                $scope.tmp = {};
                $scope.langs = {};

                // TODO servicestä joka palauttaa KomoTeksti- ja KomotoTeksti -enumien arvot
                $scope.lisatiedot = [];

                $scope.init = function() {
                    var uiModel = {isMutable: true};
                    var model = {};

                    uiModel.selectedKieliUri = "" //tab language
                    converter.createUiModels(uiModel);



                    /*
                     * HANDLE EDIT / CREATE NEW ROUTING
                     */
                    if (!angular.isUndefined($routeParams.id) && $routeParams.id !== null && $routeParams.id.length > 0) {
                        /*
                         * SHOW KOULUTUS BY GIVEN KOMOTO OID
                         */
                        $scope.controlFormMessages(uiModel, "SHOW");
                        $scope.lisatiedot = converter.KUVAUS_ORDER;
                        model = $route.current.locals.koulutusModel.result;

                        if (angular.isUndefined(model)) {
                            $location.path("/error");
                            return;
                        }

                        if (model.tila === 'POISTETTU') {
                            uiModel.isMutable = false;
                        }

                        $scope.updateFormStatusInformation(model);

                        angular.forEach(model.yhteyshenkilos, function(value, key) {
                            if (value.henkiloTyyppi === 'YHTEYSHENKILO') {
                                uiModel.contactPerson = converter.converPersonObjectForUi(value);
                            } else if (value.henkiloTyyppi === 'ECTS_KOORDINAATTORI') {
                                uiModel.ectsCoordinator = converter.converPersonObjectForUi(value);
                            } else {
                                converter.throwError('Undefined henkilotyyppi : ', value);
                            }
                        });

                        $scope.loadRelationKoodistoData(model, uiModel, model.koulutuskoodi.uri);

                        /*
                         * remove version data from the list
                         */
                        angular.forEach(converter.STRUCTURE.MCOMBO, function(value, key) {
                            uiModel[key].uris = _.keys(model[key].uris);
                        });

                        uiModel.tabs.lisatiedot = false; //activate lisatiedot tab
                    } else if (!angular.isUndefined($routeParams.org)) {
                        /*
                         * CREATE NEW KOULUTUS BY ORG OID AND KOULUTUSKOODI
                         */
                        $scope.controlFormMessages(uiModel, "INIT");
                        converter.createAPIModel(model, cfg.app.userLanguages);

                        var promiseOrg = organisaatioService.nimi($routeParams.org);
                        promiseOrg.then(function(vastaus) {
                            converter.updateOrganisationApiModel(model, $routeParams.org, vastaus);
                        });

                        var resource = TarjontaService.komo();
                        var tutkintoPromise = Koodisto.getYlapuolisetKoodiUrit(['koulutustyyppi_2'], 'koulutus', $scope.KoodistoLocale);


                        /*
                         * LOAD KOULUTUSKOODI + LUKIOLINJA KOODI OBJECTS
                         */
                        var tutkintoModules = {};
                        uiModel['koulutusohjelmaModules'] = {};
                        tutkintoPromise.then(function(kRes) {
                            resource.searchModules({koulutusasteTyyppi: 'Lukiokoulutus', koulutusmoduuliTyyppi: 'TUTKINTO'}, function(tRes) {
                                for (var i = 0; i < kRes.uris.length; i++) {
                                    for (var c = 0; c < tRes.result.length; c++) {
                                        if (!angular.isDefined(tutkintoModules[ kRes.uris[i] ]) && kRes.uris[i] === tRes.result[c].koulutuskoodiUri) {
                                            tutkintoModules[ kRes.uris[i]] = kRes.map[ kRes.uris[i]];
                                            tutkintoModules[ kRes.uris[i]].oid = tRes.result[c].oid;
                                        }
                                    }
                                }

                                var listOfTutkintoModules = _.map(tutkintoModules, function(num, key) {
                                    return num.koodiUri;
                                });

                                uiModel.tutkinto = _.map(tutkintoModules, function(num, key) {
                                    return num;
                                });

                                var lukiolinjaPromise = Koodisto.getAlapuolisetKoodiUrit(listOfTutkintoModules, 'lukiolinjat', $scope.KoodistoLocale);
                                lukiolinjaPromise.then(function(kRes) {
                                    resource.searchModules({koulutusasteTyyppi: 'Lukiokoulutus', koulutusmoduuliTyyppi: 'TUTKINTO_OHJELMA'}, function(tRes) {
                                        for (var il = 0; il < kRes.uris.length; il++) {

                                            for (var cl = 0; cl < tRes.result.length; cl++) {
                                                if (!angular.isDefined(uiModel.koulutusohjelmaModules [ kRes.uris[il] ]) && kRes.uris[il] === tRes.result[cl].koulutusohjelmaUri) {
                                                    uiModel.koulutusohjelmaModules [ kRes.uris[il]] = kRes.map[ kRes.uris[il]];
                                                    uiModel.koulutusohjelmaModules [ kRes.uris[il]].oid = tRes.result[cl].oid;
                                                    uiModel.koulutusohjelmaModules [ kRes.uris[il]].koulutuskoodi = tRes.result[cl].koulutuskoodiUri;
                                                }
                                            }
                                            uiModel.koulutusohjelma = _.map(uiModel.koulutusohjelmaModules, function(num, key) {
                                                return num;
                                            });
                                        }
                                    });
                                });
                            });
                        });
                    } else {
                        converter.throwError('unsupported $routeParams.type : ' + $routeParams.type + '.');
                    }


                    /*
                     * SHOW ALL KOODISTO KOODIS
                     */
                    angular.forEach(converter.STRUCTURE.COMBO, function(value, key) {
                        if (angular.isUndefined(value.skipUiModel)) {
                            var koodisPromise = Koodisto.getAllKoodisWithKoodiUri(cfg.env[value.koodisto], $scope.koodistoLocale);
                            uiModel[key].promise = koodisPromise;
                            koodisPromise.then(function(result) {
                                uiModel[key].koodis = result;
                            });
                        }
                    });
                    angular.forEach(converter.STRUCTURE.MCOMBO, function(value, key) {
                        if (angular.isUndefined(cfg.env[value.koodisto])) {
                            throw new Error("No koodisto URI for key : " + key + ", property : '" + value.koodisto + "'");
                        }

                        var koodisPromise = Koodisto.getAllKoodisWithKoodiUri(cfg.env[value.koodisto], $scope.koodistoLocale);
                        uiModel[key].promise = koodisPromise;

                        koodisPromise.then(function(result) {
                            //store all koodisto koodi objects for save
                            uiModel[key].koodis = result;
                            if (value.koodisto === 'koodisto-uris.kieli') {
                                //store the language uris to map object
                                for (var i in result) {
                                    $scope.langs[result[i].koodiUri] = result[i].koodiNimi;
                                }
                            }
                        });
                    });

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
                     * INIT SCOPES FOR RENDERER
                     */
                    $scope.uiModel = uiModel;
                    $scope.model = model;

                    //käyttöoikeudet
                    if ($scope.model.komotoOid) {
                        PermissionService.koulutus.canEdit($scope.model.komotoOid).then(function(data) {
                            $log.debug("setting mutable to:", data);
                            $scope.isMutable = data;
                        });
                    }


                };

                $scope.canSaveAsLuonnos = function() {
                    var canSaveAsLuonnos = true;
                    if (!$scope.isMutable)
                        canSaveAsLuonnos = false; //permissio
                    if ($scope.uiModel.isMutable) {
                        canSaveAsLuonnos = $scope.uiModel.isMutable;
                    }

                    if (canSaveAsLuonnos) {
                    }

                    return canSaveAsLuonnos;

                }

                $scope.canSaveAsValmis = function() {
                    return $scope.uiModel.isMutable && $scope.isMutable;
                };

                $scope.getLisatietoKielet = function() {
                    for (var i in $scope.uiModel.opetuskielis.uris) {
                        var lc = $scope.uiModel.opetuskielis.uris[i];
                        if ($scope.uiModel.lisatietoKielet.indexOf(lc) == -1) {
                            $scope.uiModel.lisatietoKielet.push(lc);
                        }
                    }
                    return $scope.uiModel.lisatietoKielet;
                }

                function deleteLisatiedot(lc) {
                    var lcp = $scope.uiModel.lisatietoKielet.indexOf(lc);
                    if (lcp == -1) {
                        return;
                    }
                    $scope.uiModel.lisatietoKielet.splice(lcp, 1);

                    for (var ki in $scope.model.kuvausKomo) {
                        for (var lc in $scope.model.kuvausKomo[ki].tekstis) {
                            $scope.model.kuvausKomo[ki].tekstis[lc] = undefined;
                        }
                    }
                }

                $scope.onLisatietoLangSelection = function() {
                    for (var ki in $scope.model.kuvausKomo) {
                        for (var lc in $scope.model.kuvausKomo[ki].tekstis) {
                            if ($scope.uiModel.lisatietoKielet.indexOf(lc) == -1
                                    && $scope.model.kuvausKomo[ki].tekstis[lc] && $scope.model.kuvausKomo[ki].tekstis[lc].trim().length > 0) {
                                // palautetaan listaan jottei angular digestoi ennen dialogia
                                $scope.uiModel.lisatietoKielet.push(lc);
                                $scope.uiModel.lisatietoKielet.sort();

                                if ($scope.uiModel.opetuskielis.uris.indexOf(lc) == -1) {
                                    // ei opetuskieli -> varmista poisto dialogilla
                                    dialogService.showDialog({
                                        ok: LocalisationService.t("tarjonta.poistovahvistus.koulutus.lisatieto.poista"),
                                        title: LocalisationService.t("tarjonta.poistovahvistus.koulutus.lisatieto.title"),
                                        description: LocalisationService.t("tarjonta.poistovahvistus.koulutus.lisatieto", [$scope.langs[lc]])
                                    }).result.then(function(ret) {
                                        deleteLisatiedot(lc);
                                    });
                                }
                            }
                        }
                    }
                }

                $scope.loadRelationKoodistoData = function(apiModel, uiModel, koulutuskoodi, tutkintoTyyppi) {
                    TarjontaService.getKoulutuskoodiRelations({koulutusasteTyyppi: 'Lukiokoulutus', koulutuskoodiUri: koulutuskoodi, languageCode: $scope.KoodistoLocale}, function(data) {
                        var restRelationData = data.result;
                        angular.forEach(converter.STRUCTURE.RELATION, function(value, key) {
                            if (angular.isDefined(value.module) && tutkintoTyyppi === 'TUTKINTO' && tutkintoTyyppi === value.module) {

                                if (angular.isDefined(value.koodisto)) {
                                    //var p = Koodisto.getKoodi(cfg.env[value.koodisto], cfg.env[value.koodi], $scope.koodistoLocale)
                                    var p = Koodisto.getKoodi(value.koodisto, value.koodi, $scope.koodistoLocale)
                                    p.then(function(koodi) {
                                        apiModel[key].uri = koodi.koodiUri;
                                        apiModel[key].arvo = koodi.koodiArvo;
                                        apiModel[key].versio = koodi.koodiVersio;
                                        apiModel[key].nimi = koodi.koodiNimi;
                                    });
                                } else {
                                    apiModel[key] = restRelationData[key];
                                }
                            } else if (angular.isDefined(value.module) && tutkintoTyyppi === 'TUTKINTO_OHJELMA' && tutkintoTyyppi === value.module) {
                                apiModel[key] = restRelationData[key];
                            }
                        });
                    });
                };
                /**
                 * Save koulutus data to tarjonta-service database.
                 * TODO: strict data validation, exception handling and optimistic locking
                 */
                $scope.saveLuonnos = function(tila) {
                    $scope.saveByStatus('LUONNOS');
                };
                $scope.saveValmis = function(tila) {
                    $scope.saveByStatus('VALMIS');
                };
                $scope.saveByStatus = function(tila) {
                    $scope.controlFormMessages($scope.uiModel, "CLEAR");

                    if (angular.isUndefined(tila)) {
                        converter.throwError('Undefined tila');
                    }

                    if ($scope.koulutusForm.$invalid || !$scope.koulutusForm.$valid || ($scope.koulutusForm.$pristine && !$scope.isLoaded())) {
                        //invalid form data
                        $scope.controlFormMessages($scope.uiModel, "ERROR", "UI_ERRORS");
                        return;
                    }

                    PermissionService.permissionResource().authorize({}, function(authResponse) {
                        $log.debug("Authorization check : " + authResponse.result);

                        if (authResponse.status !== 'OK') {
                            //not authenticated
                            $scope.controlFormMessages($scope.uiModel, "ERROR", ["koulutus.error.auth"]);
                            return;
                        }

                        var KoulutusRes = TarjontaService.koulutus();
                        var apiModelReadyForSave = $scope.saveModelConverter(tila);

                        KoulutusRes.save(apiModelReadyForSave, function(saveResponse) {
                            var model = saveResponse.result;

                            if (saveResponse.status === 'OK') {
                                $scope.model = model;
                                $scope.updateFormStatusInformation($scope.model);
                                $scope.controlFormMessages($scope.uiModel, "SAVED");
                                $scope.uiModel.tabs.lisatiedot = false;
                                $scope.lisatiedot = converter.KUVAUS_ORDER;
                            } else {
                                $scope.controlFormMessages($scope.uiModel, "ERROR", null, saveResponse.errors);
                            }
                        });
                    });
                };

                $scope.updateFormStatusInformation = function(model) {
                    //look more info from KoulutusRoutingController and controlLayouts.js
                    $scope.controlModel.formStatus = {
                        modifiedBy: model.modifiedBy,
                        modified: model.modified,
                        tila: model.tila
                    };

                    //force reload
                    $scope.controlModel.formControls.reloadDisplayControls();
                };

                $scope.saveModelConverter = function(tila) {
                    var apiModel = angular.copy($scope.model);
                    apiModel.tila = tila;
                    var uiModel = angular.copy($scope.uiModel);
                    $scope.validateOutputData(apiModel);
                    /*
                     * DATA CONVERSIONS FROM UI MODEL TO API MODEL
                     * Convert person object to back-end object format.
                     */

                    apiModel.yhteyshenkilos = converter.convertPersonsUiModelToDto([uiModel.contactPerson, uiModel.ectsCoordinator]);
                    /*
                     * Convert Koodisto komponent object to back-end object format.
                     */
                    //single select nodels
                    angular.forEach(converter.STRUCTURE.COMBO, function(value, key) {
                        //search version information for list of uris;

                        var koodis = $scope.uiModel[key].koodis;
                        for (var i in koodis) {
                            if (koodis[i].koodiUri === apiModel[key].uri) {
                                apiModel[key] = {
                                    uri: koodis[i].koodiUri,
                                    versio: koodis[i].koodiVersio
                                };
                                break;
                            }
                        }
                    });

                    angular.forEach(converter.STRUCTURE.RELATIONS, function(value, key) {
                        if (angular.isUndefined(value.skipApiModel)) {
                            apiModel[key] = {'uris': {}};
                            //search version information for list of uris;
                            var map = {};
                            var meta = $scope.uiModel[key].meta;
                            for (var i in meta) {
                                map[meta[i].uri] = meta[i].versio;
                            }
                            angular.forEach(uiModel[key].uris, function(uri) {
                                apiModel[key].uris[uri] = map[uri];
                            });
                        }
                    });

                    //multi-select models, add version to the koodi
                    angular.forEach(converter.STRUCTURE.MCOMBO, function(value, key) {
                        apiModel[key] = {'uris': {}};
                        //search version information for list of uris;
                        var map = {};
                        var koodis = $scope.uiModel[key].koodis;
                        for (var i in koodis) {
                            map[koodis[i].koodiUri] = koodis[i].koodiVersio;
                        }
                        angular.forEach(uiModel[key].uris, function(uri) {
                            apiModel[key].uris[uri] = map[uri];
                        });

                    });

                    $log.debug(JSON.stringify(apiModel));
                    return apiModel;
                };

                $scope.validateOutputData = function(m) {
                    if (converter.isNull(m.organisaatio) || converter.isNull(m.organisaatio.oid)) {
                        converter.throwError("Organisation OID is missing.");
                    }

                    //remove all meta data fields, if any
                    angular.forEach(converter.STRUCTURE, function(value, key) {
                        if ('MLANG' !== key) {
                            //MLANG objects needs the meta fields
                            angular.forEach(value, function(value, key) {
                                converter.deleteMetaField(m[key]);
                            });
                        }
                    });
                };

                $scope.tutkintoDialogModel = {};
                $scope.tutkintoDialogModel.open = function() {

                    var modalInstance = $modal.open({
                        scope: $scope,
                        templateUrl: 'partials/koulutus/edit/selectTutkintoOhjelma.html',
                        controller: 'SelectTutkintoOhjelmaController'
                    });
                    modalInstance.result.then(function(selectedItem) {
                        $log.debug('Ok, dialog closed: ' + selectedItem.koodiNimi);
                        $log.debug('Koodiarvo is: ' + selectedItem.koodiArvo);
                        if (!converter.isNull(selectedItem)) {
                            //$scope.model.koulutuskoodi = selectedItem;
                            $scope.model.koulutuskoodi.koodi.arvo = selectedItem.koodiArvo;
                        }
                    }, function() {
                        $log.debug('Cancel, dialog closed');
                    });
                };
                $scope.goBack = function(event) {
                    $log.info("goBack()...");
                    $location.path("/");
                };
                $scope.goToReview = function(event, boolInvalid, validationmsgs) {
                    if (!angular.isUndefined(boolInvalid) && boolInvalid) {
                        //ui errors
                        return;
                    }

                    if (!angular.isUndefined(validationmsgs) && validationmsgs > 0) {
                        //server errors
                        return;
                    }

                    $location.path("/koulutus/" + $scope.model.oid);
                };

                $scope.setTabLang = function(langUri) {
                    if (angular.isUndefined(langUri) || langUri === null) {
                        $scope.uiModel.tabLang = cfg.app.userLanguages[0]; //fi uri I guess;
                    } else {
                        $scope.uiModel.tabLang = langUri;
                    }
                };

                $scope.selectKieli = function(kieliUri) {
                    $scope.uiModel.selectedKieliUri = kieliUri;
                }

                $scope.getKuvausApiModelLanguageUri = function(boolIsKomo, textEnum, kieliUri) {
                    if (!kieliUri) {
                        return {};
                    }
                    var kuvaus = null;
                    if (typeof boolIsKomo !== 'boolean') {
                        converter.throwError('An invalid boolean variable : ' + boolIsKomo);
                    }

                    if (boolIsKomo) {
                        kuvaus = $scope.model.kuvausKomo;
                    } else {
                        kuvaus = $scope.model.kuvausKomoto;
                    }

                    if (angular.isUndefined(kuvaus) || angular.isUndefined(kuvaus[textEnum])) {
                        kuvaus[textEnum] = {tekstis: {}};
                        if (!angular.isUndefined(kieliUri)) {
                            kuvaus[textEnum].tekstis[kieliUri] = '';
                        }
                    }

                    return kuvaus[textEnum].tekstis;
                };

                // TODO omaksi direktiivikseen tjsp..
                $scope.kieliFromKoodi = function(koodi) {
                    if (angular.isUndefined(koodi) || koodi === null && koodi.length === 0) {
                        console.error("invalid language key : '" + koodi + "'");
                    }

                    return $scope.langs[koodi];
                };

                $scope.removeKandidaatinKoulutuskoodi = function(koodi) {
                    $scope.model.kandidaatinKoulutuskoodi = {};
                };

                $scope.createSelectKoulutuskoodiModalDialog = function(koodi) {
                    var modalInstance = $modal.open({
                        templateUrl: 'partials/koulutus/edit/selectTutkintoOhjelma.html',
                        controller: 'SelectTutkintoOhjelmaController',
                        resolve: {
                            targetFilters: function() {
                                return [cfg.app["Koodisto-uri.tutkintotyyppi.alempiKorkeakoulututkinto"]];
                            }
                        }
                    });

                    modalInstance.result.then(function(result) {/* close */
                        $scope.model.kandidaatinKoulutuskoodi = {
                            arvo: result.koodiArvo,
                            uri: result.koodiUri,
                            versio: result.koodiVersio,
                            nimi: result.koodiNimi
                        };
                    }, function() { /* dismissed */
                    });
                };

                /**
                 * Control page messages.
                 *
                 * @param {type} uiModel
                 * @param {type} action
                 * @param {type} errorDetailType
                 * @returns {undefined}
                 */
                $scope.controlFormMessages = function(uiModel, action, errorDetailType, apiErrors) {
                    switch (action) {
                        case 'SHOW':
                            uiModel.showErrorCheckField = false;
                            uiModel.showValidationErrors = true;
                            uiModel.showError = false;
                            uiModel.showSuccess = false;
                            uiModel.validationmsgs = [];
                            break;
                        case 'INIT':
                            uiModel.showErrorCheckField = false;
                            uiModel.showValidationErrors = false;
                            uiModel.showError = false;
                            uiModel.showSuccess = false;
                            uiModel.validationmsgs = [];
                            break;
                        case 'CLEAR':
                            $scope.controlModel.formControls.notifs.errorDetail = [];
                            $scope.koulutusForm.$dirty = true;
                            $scope.koulutusForm.$invalid = false;
                            uiModel.validationmsgs = [];
                            uiModel.showValidationErrors = false;
                            uiModel.showError = false;
                            uiModel.showSuccess = false;
                            break;
                        case 'SAVED':
                            uiModel.showErrorCheckField = false;
                            uiModel.showError = false;
                            uiModel.showValidationErrors = true;
                            uiModel.hakukohdeTabsDisabled = false;
                            uiModel.validationmsgs = [];
                            //Form
                            $scope.koulutusForm.$dirty = false;
                            $scope.koulutusForm.$invalid = false;
                            uiModel.showSuccess = true;
                            break;
                        case 'ERROR':
                        default:
                            uiModel.showErrorCheckField = errorDetailType === 'UI_ERRORS'
                            uiModel.showValidationErrors = true;
                            uiModel.showError = true;
                            uiModel.showSuccess = false;

                            if (!angular.isUndefined(apiErrors)) {
                                for (var i = 0; i < apiErrors.length; i++) {
                                    uiModel.validationmsgs.push(apiErrors[i].errorMessageKey);
                                }
                            }
                            break;
                    }
                };

                $scope.isLoaded = function() {
                    return  !angular.isUndefined($scope.model.oid) && $scope.model.oid !== null && $scope.model.oid.length > 0;
                };

                /*
                 * WATCHES
                 */
                $scope.$watch("model.koulutusohjelma.uri", function(uri, oUri) {
                    if (angular.isDefined(uri) && uri != null && oUri != uri) {
                        $scope.model.komoOid = $scope.uiModel.koulutusohjelmaModules[ uri].oid;
                        $scope.loadRelationKoodistoData($scope.model, $scope.uiModel, uri, 'TUTKINTO_OHJELMA');

                        //tutkintonimikkeet_00001
                        //koulutuslaji_a
                        //pohjakoulutustoinenaste_1
                    }
                });


                $scope.$watch("model.koulutuskoodi.uri", function(valNew, valOld) {
                    if (angular.isDefined(valNew) && valNew != null && valOld != valNew) {
                        $scope.loadRelationKoodistoData($scope.model, $scope.uiModel, valNew, 'TUTKINTO');
                    }
                });

                $scope.init();
            }]);
