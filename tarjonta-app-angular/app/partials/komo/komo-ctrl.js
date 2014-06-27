angular.module('app.komo.ctrl', ['Tarjonta', 'ngResource', 'config', 'localisation', 'auth', 'Koodisto', 'TarjontaCache', 'TarjontaDateTime'])
        .controller('KomoController', function($scope, $q, Koodisto, TarjontaService, PermissionService, LocalisationService, dialogService) {
            'use strict';
            $scope.controlModel = {
                formStatus: {
                    modifiedBy: '',
                    modified: null,
                    tila: ''
                },
                formControls: {}
            };

            $scope.import = {
                showAllKomos: false,
                filter: {oid: null},
                uiModel: []
            };

            $scope.ctrl = {
                showRealName: false,
                koulutustyyppiUri: null,
                showError: false,
                showSuccess: false,
                validationmsgs: [],
                apiKeys: {},
                koulutuskoodi: null,
                komoOid: "",
                result: {},
                koodisto: {},
                link: {
                    komos: [],
                    komosMap: {},
                    selectedKomoOids: [],
                    selectedLinkOids: [],
                    selectedParentLinkOids: [],
                },
                koodi: {uri: '', versio: ''},
                selectedLang: 'kieli_fi',
                selectedKomoEnum: 'KOULUTUKSEN_RAKENNE',
                komoOptions: [
                    {enum: 'KOULUTUKSEN_RAKENNE', name: 'KOULUTUKSEN_RAKENNE'},
                    {enum: 'JATKOOPINTO_MAHDOLLISUUDET', name: 'JATKOOPINTO_MAHDOLLISUUDET'},
                    {enum: 'TAVOITTEET', name: 'TAVOITTEET'},
                    {enum: 'PATEVYYS', name: 'PATEVYYS'}
                ],
                langOptions: [
                    {enum: 'kieli_fi', name: 'suomi'},
                    {enum: 'kieli_en', name: 'Englanti'},
                    {enum: 'kieli_sv', name: 'Ruotsi'}
                ],
                typeOptions: [
                    {enum: 'TUTKINTO', name: 'Tutkinto'},
                    {enum: 'TUTKINTO_OHJELMA', name: "Tutkinto-ohjelma"}],
                statusOptions: [
                    {enum: 'JULKAISTU', name: 'Julkaistu'},
                    {enum: 'KOPIOITU', name: "Kopioitu"},
                    {enum: 'LUONNOS', name: "Luonnos"},
                    {enum: 'PERUTTU', name: "Peruttu"},
                    {enum: 'VALMIS', name: "Valmis"},
                    {enum: 'POISTETTU', name: "Poistettu"}],
                koulutusOptions: [
                    {enum: 'LUKIOKOULUTUS', name: "Lukiokoulutus"},
                    {enum: 'AMMATILLINEN_PERUSKOULUTUS', name: "AmmatillinenPeruskoulutus"},
                    {enum: 'VAPAAN_SIVISTYSTYON_KOULUTUS', name: "VapaanSivistystyonKoulutus"},
                    {enum: 'PERUSOPETUKSEN_LISAOPETUS', name: "PerusopetuksenLisaopetus"},
                    {enum: 'VALMENTAVA_JA_KUNTOUTTAVA_OPETUS', name: "ValmentavaJaKuntouttavaOpetus"},
                    {enum: 'MAAHANM_AMM_VALMISTAVA_KOULUTUS', name: "MaahanmAmmValmistavaKoulutus"},
                    {enum: 'AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS', name: "AmmOhjaavaJaValmistavaKoulutus"},
                    {enum: 'MAAHANM_LUKIO_VALMISTAVA_KOULUTUS', name: "MaahanmLukioValmistavaKoulutus"},
                    {enum: 'KORKEAKOULUTUS', name: "Korkeakoulutus"},
                    {enum: 'PERUSOPETUS', name: "Perusopetus"},
                    {enum: 'PERUSOPETUS_ULKOMAINEN', name: "PerusopetusUlkomainen"},
                    {enum: 'AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA', name: "AmmatillinenPerustutkintoNayttotutkintona"},
                    {enum: 'TUNTEMATON', name: "Tuntematon"},
                    {enum: 'ERIKOISAMMATTITUTKINTO', name: "Erikoisammattitutkinto"},
                    {enum: 'AMMATTITUTKINTO', name: "Ammattitutkinto"}
                ],
                textFields: [
                    {name: "modified", type: "DATE"},
                    {name: "tunniste", type: "STR"},
                    {name: "koulutusmoduuliTyyppi", type: "ENUM_TUTKINTO"},
                    {name: "koulutusasteTyyppi", type: "ENUM_KOULUTUS"},
                    {name: "tila", type: "ENUM_STATUS"},
                    {name: "koulutuskoodi", type: "URI"},
                    {name: "koulutusaste", type: "URI"},
                    {name: "eqf", type: "URI"},
                    {name: "nqf", type: "URI"},
                    {name: "organisaatio", type: "OID"},
                    {name: "koulutusala", type: "URI"},
                    {name: "tutkinto", type: "URI"},
                    {name: "opintojenLaajuusyksikko", type: "URI"},
                    {name: "opintoala", type: "URI"},
                    {name: "opintojenLaajuusarvo", type: "URI"},
                    {name: "koulutusohjelma", type: "URI"},
                    {name: "osaamisala", type: "URI"},
                    {name: "lukiolinja", type: "URI"},
                    {name: "nimi", type: "TEXT_MAP"},
                    {name: "tutkintonimikes", type: "URI_VERSION_MAP"},
                    {name: "oppilaitostyyppis", type: "URI_VERSION_MAP"},
                    {name: "koulutustyyppis", type: "URI_MAP"},
                    {name: "kuvausKomo", type: "TEXT_ENUM_MAP"}
                ]
            };
            $scope.fetchByOid = function(strKomoOid) {
                $scope.ctrl.link.komos = [];
                $scope.ctrl.link.selectedKomoOids = [];
                $scope.ctrl.link.selectedLinkOids = [];
                $scope.ctrl.link.selectedParentLinkOids = [];

                $scope.ctrl.saved = false;
                var resource = TarjontaService.komo();
                resource.get({oid: strKomoOid, meta: false}, function(res) {
                    console.log("loaded", res);
                    $scope.ctrl.result = res.result;
                    $scope.ctrl.komoOid = $scope.ctrl.result.komoOid;
                    $scope.ctrl.koulutuskoodi = $scope.ctrl.result.koulutuskoodi.arvo;
                    $scope.fetchByKoulutuskoodi($scope.ctrl.result.koulutuskoodi.uri);
                    $scope.searchChilds(strKomoOid);
                    $scope.searchParents(strKomoOid);
                });
            };

            $scope.fetchByKoulutuskoodi = function(strKoulutuskoodiUri) {
                $scope.ctrl.link.selectedKomoOids = []; //clear selected array
                var resource = TarjontaService.komo();
                if (angular.isDefined($scope.ctrl.koulutustyyppiUri) && $scope.ctrl.koulutustyyppiUri !== null) {

                    resource.searchModules({koulutustyyppi: $scope.ctrl.koulutustyyppiUri, koulutus: strKoulutuskoodiUri, meta: false}, function(res) {
                        console.log("loaded", res);
                        var komos = [];

                        for (var i = 0; i < res.result.length; i++) {
                            komos.push({
                                oid: res.result[i].oid,
                                koulutuskoodiUri: res.result[i].koulutuskoodiUri,
                                koulutusohjelmaUri: res.result[i].koulutusohjelmaUri,
                                koulutusmoduuliTyyppi: res.result[i].koulutusmoduuliTyyppi,
                                moduuli: res.result[i].koulutusmoduuliTyyppi === 'TUTKINTO' ? 'Tutkinto' : 'Tutkinto-ohjelma',
                                koulutus: res.result[i].koulutuskoodiUri,
                                ohjelma: res.result[i].koulutusohjelmaUri
                            });
                        }

                        if ($scope.ctrl.showRealName) {
                            $scope.searchRealNames(komos);
                        } else {
                            $scope.ctrl.link.komos = komos;
                        }

                        for (var i = 0; i < komos.length; i++) {
                            $scope.ctrl.link.komosMap[res.result[i].oid] = komos[i];
                        }
                    }
                    );
                }
            };

            $scope.getField = function(field) {
                return  $scope.ctrl.result[field];
            };

            $scope.removeField = function(field) {
                return $scope.ctrl.result[field] = {uri: null, versio: -1};
            };

            $scope.initUri = function(fieldName) {
                if (!angular.isDefined($scope.ctrl.result[fieldName]) || !angular.isDefined($scope.ctrl.result[fieldName].uri)) {
                    $scope.ctrl.result[fieldName] = {uri: '', versio: -1};
                }
            };

            $scope.removeLanguageToFieldTekstis = function(langKey, fieldName) {
                delete fieldName[langKey];
            };

            $scope.addLanguageToFieldTekstis = function(langKey, fieldName) {
                if (angular.isUndefined(fieldName)) {
                    return;
                }

                if (angular.isUndefined($scope.ctrl.result[fieldName])) {
                    $scope.ctrl.result[fieldName] = {};
                }

                var field = $scope.ctrl.result[fieldName];
                if (angular.isUndefined(field.tekstis)) {
                    field.tekstis = {};
                }
                field.tekstis[langKey] = '';
            };

            $scope.removeLanguageToEnumFieldTekstis = function(langKey, langs) {
                delete langs[langKey];
            }

            $scope.addLanguageToEnumFieldTekstis = function(langKey, komoEnum, fieldName) {
                if (angular.isUndefined(fieldName)) {
                    return;
                }

                if (angular.isUndefined($scope.ctrl.result[fieldName])) {
                    $scope.ctrl.result[fieldName] = {};
                }

                var field = $scope.ctrl.result[fieldName];

                if (angular.isUndefined(field[komoEnum])) {
                    field[komoEnum] = {};
                }

                if (angular.isUndefined(field[komoEnum].tekstis)) {
                    field[komoEnum]['tekstis'] = {};
                }

                var langs = field[komoEnum]['tekstis'];

                if (angular.isUndefined(langs[langKey])) {
                    langs[langKey] = {};
                }

                langs[langKey] = '';
            };


            $scope.removeUriVersio = function(koodiUri, fieldName) {
                var field = $scope.ctrl.result[fieldName];
                delete field.uris[koodiUri];
            };

            $scope.addKey = function(fieldName) {
                if (angular.isUndefined(fieldName)) {
                    return;
                }
                $scope.ctrl.apiKeys[fieldName] = "";

                return $scope.ctrl.apiKeys[fieldName];
            };

            $scope.addUriVersio = function(koodiUri, versio, fieldName) {
                if (angular.isUndefined(fieldName)) {
                    return;
                }

                if (angular.isUndefined($scope.ctrl.result[fieldName])) {
                    $scope.ctrl.result[fieldName] = {};
                }

                var field = $scope.ctrl.result[fieldName];
                if (angular.isUndefined(field.uris)) {
                    field.uris = {};
                }
                field.uris[koodiUri] = versio;
            };

            $scope.update = function() {
                $scope.ctrl.showError = false;
                $scope.ctrl.showSuccess = false;
                $scope.ctrl.validationmsgs = [];

                if (!$scope.canUpdate()) {
                    return;
                }

                var resource = TarjontaService.komo();
                PermissionService.permissionResource().authorize({}, function(authResponse) {

                    $scope.ctrl.result.modified = (new Date()).getTime();
                    if (angular.isUndefined($scope.ctrl.komoOid) || $scope.ctrl.komoOid === "") {
                        delete $scope.ctrl.result.oid;
                        delete $scope.ctrl.result.komoOid;
                    }

                    resource.update($scope.clearApiObject(), $scope.saveCallBack);
                });
            };

            $scope.create = function() {
                var d = dialogService.showDialog({
                    ok: LocalisationService.t("ok"),
                    cancel: LocalisationService.t("cancel"),
                    title: LocalisationService.t("tarjonta.kopioi-uudeksi"),
                    description: LocalisationService.t("tarjonta.kopioi-uudeksi")
                });

                d.result.then(function(data) {
                    // results: "ACTION" or "CANCEL"
                    if (data) {
                        $scope.copy();
                    }
                });
            };
            $scope.copy = function() {
                $scope.ctrl.showError = false;
                $scope.ctrl.showSuccess = false;
                $scope.ctrl.validationmsgs = [];

                if (!$scope.canCreate()) {
                    return;
                }

                delete $scope.ctrl.result.oid;
                delete $scope.ctrl.result.komoOid;

                var resource = TarjontaService.komo();
                PermissionService.permissionResource().authorize({}, function(authResponse) {

                    $scope.ctrl.result.modified = (new Date()).getTime();
                    delete $scope.ctrl.result.oid;
                    delete $scope.ctrl.result.komoOid;

                    resource.insert($scope.clearApiObject(), $scope.saveCallBack);
                });
            };

            $scope.saveCallBack = function(res) {
                console.log("saveCallBack", res);
                var model = res.result;

                if (res.status === 'OK') {
                    $scope.ctrl.komoOid = model.komoOid;
                    $scope.ctrl.showSuccess = true;
                } else {
                    $scope.ctrl.showError = true;
                    if (angular.isDefined(res.errors)) {
                        for (var i = 0; i < res.errors.length; i++) {
                            $scope.ctrl.validationmsgs.push(res.errors[i].errorMessageKey);
                        }
                    }
                }
            };

            $scope.addChilds = function(komoOid, komoOids) {
                if (angular.isUndefined(komoOid) || angular.isUndefined(komoOids)) {
                    return;
                }
                PermissionService.permissionResource().authorize({}, function(authResponse) {
                    TarjontaService.saveResourceLink(komoOid, komoOids,
                            function(res) {
                                console.log("success", res);
                                $scope.ctrl.saved = true;
                            },
                            function() {
                                console.error(res);
                            }
                    );
                });
            };

            $scope.searchChilds = function(komoOid) {
                if (angular.isUndefined(komoOid)) {
                    return;
                }
                TarjontaService.resourceLink.get({oid: komoOid}, function(childKomos) {
                    $scope.ctrl.link.selectedLinkOids = childKomos.result;
                });
            };

            $scope.searchParents = function(komoOid) {
                if (angular.isUndefined(komoOid)) {
                    return;
                }
                TarjontaService.resourceLink.parents({oid: komoOid}, function(parentKomos) {
                    $scope.ctrl.link.selectedParentLinkOids = parentKomos.result;
                });
            };

            $scope.removeLink = function(komoOid, childKomoOid) {
                PermissionService.permissionResource().authorize({}, function(authResponse) {
                    TarjontaService.resourceLink.remove({parent: komoOid, child: childKomoOid}, function(childKomos) {
                    });
                });
            };

            $scope.searchKomoByOid = function(komoOid) {
                var searchedKomos = $scope.ctrl.link.komosMap[komoOid];
                if (!angular.isUndefined(searchedKomos)) {
                    return searchedKomos;
                }

                return {komoOid: komoOid, koulutuskoodi: {}, koulutusohjelma: {}};
            };

            $scope.canUpdate = function() {
                var oid = $scope.ctrl.result.oid;
                return oid !== null && angular.isDefined(oid) && oid.length > 0;
            };

            $scope.canCreate = function() {
                return  $scope.canUpdate();
            };

            $scope.clearApiObject = function() {
                var copy = angular.copy($scope.ctrl.result);
                for (var i = 0; i < $scope.ctrl.textFields.length; i++) {
                    var o = $scope.ctrl.textFields[i];
                    if (o.type === 'URI') {
                        var f = copy[o.name];
                        if (
                                angular.isDefined(f) &&
                                angular.isDefined(f.versio) &&
                                (f.versio > 0 && (f.versio === '-1') || f.versio === -1)
                                ) {
                            copy[o.name] = null;
                        }
                    }
                }
                return copy;
            };

            $scope.$watch("ctrl.showRealName", function(valNew, valOld) {
                if (valNew) {
                    $scope.searchRealNames($scope.ctrl.link.komos);
                }
            });

            $scope.searchRealNames = function(modelKomos) {
                var prosmises = []
                var komos = angular.copy(modelKomos);
                for (var i = 0; i < komos.length; i++) {
                    prosmises.push($scope.searchKoodi(komos[i], 'koulutus', 'koulutus', komos[i].koulutuskoodiUri, 'FI'));
                    if (angular.isDefined(komos[i].koulutusohjelmaUri) && komos[i].koulutusohjelmaUri !== null) {
                        prosmises.push($scope.searchKoodi(komos[i], 'ohjelma', (komos[i].koulutusohjelmaUri.split("_"))[0], komos[i].koulutusohjelmaUri, 'FI'));
                    }
                }

                $q.all(prosmises).then(function(values) {
                    $scope.ctrl.link.komos = komos;
                });
            };

            $scope.searchKoodi = function(obj, field, koodistouri, uri, locale) {
                var promise = Koodisto.getKoodi(koodistouri, uri, locale);
                promise.then(function(data) {
                    obj[field] = data.koodiArvo + ' ' + data.koodiNimi;
                });
                return promise;
            };

            $scope.isRequiredKoodisto = function(koodisto, koodiUri) {
                return koodiUri.indexOf(koodisto) === 0;
            };

            $scope.searchUris = function(koulutustyyppiUri) {
                var searchResult = $q.defer();


                var koulutus = Koodisto.getYlapuolisetKoodiUrit([koulutustyyppiUri], "koulutus", $scope.koodistoLocale);

                function resolvePromise(koulutuUri, koodis, count) {
                    var deffered = $q.defer();

                    var ohjelma = Koodisto.getAlapuolisetKoodiUrit([koodis.uris[count]], null, $scope.koodistoLocale);
                    ohjelma.then(function(ohjelmaRes) {
                        deffered.resolve({koulutus: koulutuUri, res: ohjelmaRes});
                    });
                    return deffered.promise;
                }

                koulutus.then(function(koulutusRes) {
                    var promises = [];
                    var koulutusMap = {komo: {}};
                    for (var ik = 0; ik < koulutusRes.uris.length; ik++) {
                        promises.push(resolvePromise(koulutusRes.uris[ik], koulutusRes, ik));
                    }
                    var p = $q.all(promises);
                    p.then(function(result) {

                        for (var i = 0; i < result.length; i++) {
                            for (var io = 0; io < result[i].res.uris.length; io++) {

                                if (angular.isUndefined(koulutusMap[result[i].koulutus])) {
                                    koulutusMap[result[i].koulutus] = {ohjelmas: []};
                                }

                                var obj = koulutusMap[result[i].koulutus];

                                obj.uri = {};

                                //filter all useless uris for komo import
                                if ($scope.isRequiredKoodisto("tutkintotyyppi", result[i].res.uris[io])) {
                                    koulutusMap.komo.tutkintotyyppi = result[i].res.uris[io];
                                } else if ($scope.isRequiredKoodisto("koulutusasteoph2002", result[i].res.uris[io])) {
                                    koulutusMap.komo.koulutusaste = result[i].res.uris[io];
                                } else if ($scope.isRequiredKoodisto("opintojenlaajuus_", result[i].res.uris[io])) {
                                    koulutusMap.komo.opintojenLaajuusarvo = result[i].res.uris[io];
                                } else if ($scope.isRequiredKoodisto("opintojenlaajuusyksikko", result[i].res.uris[io])) {
                                    koulutusMap.komo.opintojenLaajuusyksikko = result[i].res.uris[io];
                                } else if ($scope.isRequiredKoodisto("eqf", result[i].res.uris[io])) {
                                    koulutusMap.komo.eqf = result[i].res.uris[io];
                                } else if ($scope.isRequiredKoodisto("opintoalaoph2002", result[i].res.uris[io])) {
                                    koulutusMap.komo.opintoala = result[i].res.uris[io];
                                } else if ($scope.isRequiredKoodisto("koulutusalaoph2002", result[i].res.uris[io])) {
                                    koulutusMap.komo.koulutusala = result[i].res.uris[io];
                                } else if ($scope.isRequiredKoodisto("osaamisala", result[i].res.uris[io])) {
                                    obj.ohjelmas.push(result[i].res.uris[io]);
                                } else if ($scope.isRequiredKoodisto("koulutusohjelmaamm", result[i].res.uris[io])) {
                                    obj.ohjelmas.push(result[i].res.uris[io]);
                                } else if ($scope.isRequiredKoodisto("lukiolinjat", result[i].res.uris[io])) {
                                    obj.ohjelmas.push(result[i].res.uris[io]);
                                } else {
                                    // console.log("skipped : " + result[i].res.uris[io]);
                                }
                            }
                        }

                        searchResult.resolve(koulutusMap);
                    });
                });

                return searchResult.promise;
            };

            /* koodisto import*/
            $scope.searchAllMissingModulesByKoulutustyyppi = function() {
                $scope.import.uiModel = [];
                var promise = $scope.searchUris($scope.ctrl.koulutustyyppiUri);
                var mapKomos = {};
                var uiModel = [];
                var resource = TarjontaService.komo();

                promise.then(function(result) {
                    angular.forEach(result, function(value, koulutusUri) {
                        if (koulutusUri !== 'komo') {
                            resource.searchModules({
                                koulutus: koulutusUri,
                                koulutustyyppi: $scope.ctrl.koulutustyyppiUri
                            }, function(res) {
                                /*
                                 * mapLoaded:
                                 * 
                                 * koulutus_123456 : {
                                 * oid : 12345,
                                 * mapOhjelmas : {
                                 *      ohjelmauri_a = {oid : 12345},
                                 *      ohjelmauri_b = {oid : 12345}
                                 *      ...
                                 *  }
                                 * }
                                 * ...
                                 */

                                //LOAD

                                var mapLoaded = {};
                                for (var i = 0; i < res.result.length; i++) {
                                    if (angular.isUndefined(mapLoaded[res.result[i].koulutuskoodiUri])) {
                                        mapLoaded[res.result[i].koulutuskoodiUri] = {
                                            mapOhjelmas: {}
                                        };
                                    }

                                    if (angular.isUndefined(res.result[i].ohjelma) && res.result[i].koulutusmoduuliTyyppi === 'TUTKINTO_OHJELMA') {
                                        if (angular.isDefined(res.result[i].koulutusohjelmaUri)) {
                                            mapLoaded[res.result[i].koulutuskoodiUri].mapOhjelmas[res.result[i].koulutusohjelmaUri] = {oid: res.result[i].oid};
                                        }
                                        if (angular.isDefined(res.result[i].osaamisalaUri)) {
                                            mapLoaded[res.result[i].koulutuskoodiUri].mapOhjelmas[res.result[i].osaamisalaUri] = {oid: res.result[i].oid};
                                        }
                                        if (angular.isDefined(res.result[i].lukiolinjaUri)) {
                                            mapLoaded[res.result[i].koulutuskoodiUri].mapOhjelmas[res.result[i].lukiolinjaUri] = {oid: res.result[i].oid};
                                        }
                                    } else if (res.result[i].koulutusmoduuliTyyppi === 'TUTKINTO') {
                                        mapLoaded[res.result[i].koulutuskoodiUri].oid = res.result[i].oid;
                                    } else {
                                        console.log("???", res.result[i]);
                                    }
                                }


                                if (angular.isUndefined(mapKomos[koulutusUri])) {
                                    var tutkintoApiModel = {
                                        oid: angular.isDefined(mapLoaded[koulutusUri]) ? mapLoaded[koulutusUri].oid : null
                                    };

                                    var tutkintoModel = $scope.createModel(tutkintoApiModel, "TUTKINTO", koulutusUri, {}, result.komo);

                                    mapKomos[koulutusUri] = {
                                        tutkinto: tutkintoModel,
                                        ohjelmas: [],
                                    };
                                    uiModel.push(tutkintoModel);
                                }

                                //MERGE

                                var mergeDublicate = {};
                                for (var i = 0; i < value.ohjelmas.length; i++) {
                                    var ohjVal = value.ohjelmas[i].split("_")[1];
                                    if (angular.isUndefined(mergeDublicate[ohjVal])) {
                                        mergeDublicate[ohjVal] = {};
                                    }

                                    if ($scope.isRequiredKoodisto("osaamisala", value.ohjelmas[i])) {
                                        mergeDublicate[ohjVal].osaamisala = value.ohjelmas[i];
                                        mergeDublicate[ohjVal].ohjelma = value.ohjelmas[i];
                                    } else if ($scope.isRequiredKoodisto("koulutusohjelmaamm", value.ohjelmas[i])) {
                                        mergeDublicate[ohjVal].koulutusohjelma = value.ohjelmas[i];
                                        mergeDublicate[ohjVal].ohjelma = value.ohjelmas[i];
                                    } else if ($scope.isRequiredKoodisto("lukiolinjat", value.ohjelmas[i])) {
                                        mergeDublicate[ohjVal].lukiolinja = value.ohjelmas[i];
                                        mergeDublicate[ohjVal].ohjelma = value.ohjelmas[i];
                                    }
                                }

                                //CONVERT TO API MODEL

                                angular.forEach(mergeDublicate, function(value, key) {
                                    var ohjelmaApiModel = {
                                        oid: angular.isDefined(mapLoaded[koulutusUri]) && angular.isDefined(mapLoaded[koulutusUri].mapOhjelmas[mergeDublicate[key].ohjelma]) ? mapLoaded[koulutusUri].mapOhjelmas[mergeDublicate[key].ohjelma].oid : null
                                    };

                                    var ohjelmaModel = $scope.createModel(ohjelmaApiModel, "TUTKINTO_OHJELMA", koulutusUri, mergeDublicate[key], result.komo)

                                    mapKomos[koulutusUri].ohjelmas.push(ohjelmaModel);
                                    uiModel.push(ohjelmaModel);
                                });
                            });
                        }
                    });

                    $scope.import.uiModel = uiModel;
                    $scope.import.map = mapKomos;
                });
            };

            $scope.createModel = function(apiModel, moduleType, koulutusUri, objOhjelmat, data) {
                var asteEnum = null;
                switch ($scope.ctrl.koulutustyyppiUri) {
                    case 'koulutustyyppi_1':
                    case 'koulutustyyppi_13':
                    case 'koulutustyyppi_4':
                        asteEnum = 'AMMATILLINEN_PERUSKOULUTUS';
                        break;
                    case 'koulutustyyppi_2':
                    case 'koulutustyyppi_14':
                        asteEnum = 'LUKIOKOULUTUS';
                        break;
                    case 'koulutustyyppi_3':
                        asteEnum = 'KORKEAKOULUTUS';
                        break;
                    case 'koulutustyyppi_6':
                        asteEnum = 'PERUSOPETUKSEN_LISAOPETUS';
                        break;
                    case 'koulutustyyppi_9':
                        asteEnum = 'MAAHANM_LUKIO_VALMISTAVA_KOULUTUS';
                        break;
                    case 'koulutustyyppi_8':
                        asteEnum = 'MAAHANM_AMM_VALMISTAVA_KOULUTUS';
                        break;
                    case 'koulutustyyppi_7':
                        asteEnum = 'AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS';
                        break;
                    case 'koulutustyyppi_11':
                        asteEnum = 'AMMATTITUTKINTO';
                        break;
                    case 'koulutustyyppi_10':
                        asteEnum = 'VAPAAN_SIVISTYSTYON_KOULUTUS';
                        break;
                    case 'koulutustyyppi_12':
                        asteEnum = 'ERIKOISAMMATTITUTKINTO';
                        break;
                    default:
                        asteEnum = 'TUNTEMATON';
                        break;
                }

                return $scope.updateApiModel(apiModel, koulutusUri, objOhjelmat, moduleType, asteEnum, data);
            };

            $scope.updateApiModel = function(apiModel, koulutusUri, objOhjelmat, moduleTypeEnum, asteEnum, data) {
                var STRUCTURE = {
                    ENUMS: {
                        "koulutusmoduuliTyyppi": moduleTypeEnum,
                        "tila": "JULKAISTU",
                        "koulutusasteTyyppi": asteEnum
                    },
                    URIS: {
                        "koulutustyyppis": {
                            "module": "BOTH",
                            "uris": [$scope.ctrl.koulutustyyppiUri]
                        },
                        "tutkintonimikes": {
                            "module": "TUTKINTO",
                            "uris": []
                        },
                        "oppilaitostyyppis": {
                            "module": "TUTKINTO",
                            "uris": []
                        }
                    },
                    URI: {
                        "koulutusaste": {
                            module: "TUTKINTO",
                            "uri": data.koulutusaste
                        },
                        "eqf": {
                            module: "BOTH",
                            "uri": data.eqf
                        },
                        "koulutusala": {
                            module: "TUTKINTO",
                            "uri": data.koulutusala
                        },
                        "tutkinto": {
                            module: "TUTKINTO",
                            "uri": data.tutkinto
                        },
                        "opintojenLaajuusyksikko": {
                            module: "TUTKINTO",
                            "uri": data.opintojenLaajuusyksikko
                        },
                        "opintoala": {
                            module: "TUTKINTO",
                            "uri": data.opintoala
                        },
                        "koulutusohjelma": {
                            module: "TUTKINTO_OHJELMA",
                            "uri": objOhjelmat.koulutusohjelma
                        },
                        "lukiolinja": {
                            module: "TUTKINTO_OHJELMA",
                            "uri": objOhjelmat.lukiolinja
                        },
                        "osaamisala": {
                            module: "TUTKINTO_OHJELMA",
                            "uri": objOhjelmat.osaamisala
                        },
                        "koulutuskoodi": {
                            module: "BOTH",
                            "uri": koulutusUri
                        },
                        "opintojenLaajuusarvo": {
                            module: "TUTKINTO",
                            "uri": data.opintojenLaajuusarvo
                        },
                        "nqf": {
                            module: "BOTH",
                            "uri": data.nqf
                        }
                    }
                };

                angular.forEach(STRUCTURE.URIS, function(value, key) {
                    if (angular.isDefined(STRUCTURE.URIS[key])) {
                        var map = {}; //default

                        if (angular.isDefined(apiModel[key]) && angular.isDefined(apiModel[key].uris)) {
                            //load data
                            map = apiModel[key].uris;
                        }

                        for (var i = 0; i < STRUCTURE.URIS[key].uris.length; i++) {
                            if (STRUCTURE.URIS[key].module === 'BOTH' || STRUCTURE.URIS[key].module === moduleTypeEnum) {
                                map[ STRUCTURE.URIS[key].uris[i]] = 1;
                            }
                        }

                        apiModel[key] = {
                            uris: map
                        };
                    }
                });

                angular.forEach(STRUCTURE.URI, function(value, key) {
                    if (angular.isDefined(STRUCTURE.URI[key])) {
                        if (STRUCTURE.URI[key].module === 'BOTH' || STRUCTURE.URI[key].module === moduleTypeEnum) {
                            if (angular.isDefined(STRUCTURE.URI[key].uri)
                                    && STRUCTURE.URI[key].uri !== null
                                    && STRUCTURE.URI[key].uri.length > 0) {

                                apiModel[key] = {
                                    uri: STRUCTURE.URI[key].uri,
                                    versio: 1
                                };
                            }
                        }
                    }
                });

                angular.forEach(STRUCTURE.ENUMS, function(value, key) {
                    if (angular.isDefined(STRUCTURE.ENUMS[key])) {
                        apiModel[key] = value;
                    }
                });

                return apiModel;
            };

            $scope.$watch("import.showAllKomos", function(valNew, valOld) {
                if (valNew) {
                    $scope.import.filter = {};
                } else {
                    $scope.import.filter = {oid: null};
                }
            });

            $scope.importAllGroups = function() {
                $scope.ctrl.showError = false;
                $scope.ctrl.showSuccess = false;
                $scope.ctrl.validationmsgs = [];

                PermissionService.permissionResource().authorize({}, function(authResponse) {
                    angular.forEach($scope.import.map, function(value, keyKoulutusUri) {
                        var arrByKoulutusUri = _.values($scope.import.map[keyKoulutusUri].ohjelmas)
                        arrByKoulutusUri.push(value.tutkinto);
                        console.log("--------------------------");
                        console.log(keyKoulutusUri);

                        TarjontaService.komoImport(keyKoulutusUri).import(arrByKoulutusUri, function(res) {
                            //result handler for ui model
                            if (res.status === 'OK') {
                                $scope.ctrl.showSuccess = true;
                                console.log("SUCCESS: ", res);
                            } else {
                                $scope.ctrl.showError = true;

                                if (angular.isDefined(res.errors)) {
                                    var mapErrors = {};
                                    for (var i = 0; i < res.errors.length; i++) {
                                        mapErrors[res.errors[i].errorMessageKey] = {};
                                    }

                                    angular.forEach(mapErrors, function(value, key) {
                                        $scope.ctrl.validationmsgs.push(key);
                                    });
                                }
                                console.error("FAILED : ", res);
                            }


                            var koulutus = []
                            if (res.result[0].koulutuskoodiUri === keyKoulutusUri) {
                                for (var i = 0; i < $scope.import.uiModel.length; i++) {
                                    var o = $scope.import.uiModel[i];
                                    koulutus.push({
                                        index: i,
                                        osaamisalaUri: angular.isDefined(o.osaamisala) && angular.isDefined(o.osaamisala.uri) ? o.osaamisala.uri : null,
                                        koulutusohjelmaUri: angular.isDefined(o.koulutusohjelma) && angular.isDefined(o.koulutusohjelma.uri) ? o.koulutusohjelma.uri : null,
                                        lukiolinjaUri: angular.isDefined(o.lukiolinja) && angular.isDefined(o.lukiolinja.uri) ? o.lukiolinja.uri : null
                                    });
                                }
                            }

                            for (var i = 0; i < res.result.length; i++) {
                                for (var c = 0; c < koulutus.length; c++) {
                                    if (res.result[i].koulutuskoodiUri === keyKoulutusUri && res.result[i].koulutusmoduuliTyyppi === 'TUTKINTO') {
                                        $scope.import.uiModel[koulutus[c].index].status = res.status;
                                    } else if (res.result[i].koulutuskoodiUri === keyKoulutusUri && res.result[i].koulutusmoduuliTyyppi === 'TUTKINTO_OHJELMA') {
                                        if (res.result[i].osaamisalaUri === koulutus[c].osaamisalaUri) {
                                            $scope.import.uiModel[koulutus[c].index].status = res.status;
                                            console.log("SUCCESS: osaamisala", koulutus[c]);
                                        } else if (res.result[i].koulutusohjelmaUri === koulutus[c].koulutusohjelmaUri) {
                                            $scope.import.uiModel[koulutus[c].index].status = res.status;
                                            console.log("SUCCESS: koulutusohjelma", koulutus[c]);
                                        } else if (res.result[i].lukiolinjaUri === koulutus[c].lukiolinjaUri) {
                                            $scope.import.uiModel[koulutus[c].index].status = res.status;
                                            console.log("SUCCESS: lukiolinja", koulutus[c]);
                                        }
                                    }
                                }
                            }
                        });
                    });
                });
            };
        });




