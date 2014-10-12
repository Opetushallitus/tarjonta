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
            showUris: false,
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
                            ohjelmaUri: res.result[i].ohjelmaUri,
                            koulutusmoduuliTyyppi: res.result[i].koulutusmoduuliTyyppi,
                            moduuli: res.result[i].koulutusmoduuliTyyppi === 'TUTKINTO' ? 'Tutkinto' : 'Tutkinto-ohjelma',
                            koulutus: res.result[i].koulutuskoodiUri,
                            ohjelma: res.result[i].ohjelmaUri
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
                if (angular.isDefined(komos[i].ohjelmaUri) && komos[i].ohjelmaUri !== null) {
                    prosmises.push($scope.searchKoodi(komos[i], 'ohjelma', (komos[i].ohjelmaUri.split("_"))[0], komos[i].ohjelmaUri, 'FI'));
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

                TarjontaService.getKoulutuskoodiRelations({
                    uri: koodis.uris[count],
                    koulutustyyppi: koulutustyyppiUri,
                    languageCode: $scope.koodistoLocale,
                    meta: false,
                    defaults: "tutkintonimike:tutkintonimikkeet_00000,eqf:eqf_4,tutkinto:tutkinto_xx"
                }, function(data) {
                    deffered.resolve({koulutus: koulutuUri, res: data});
                });

//                    var ohjelma = Koodisto.getAlapuolisetKoodiUrit([koodis.uris[count]], null, $scope.koodistoLocale);
//                    ohjelma.then(function(ohjelmaRes) {
//                        deffered.resolve({koulutus: koulutuUri, res: ohjelmaRes});
//                    });
                return deffered.promise;
            }

            koulutus.then(function(koulutusRes) {
                var promises = [];
                var koulutusMap = {};
                for (var ik = 0; ik < koulutusRes.uris.length; ik++) {
                    promises.push(resolvePromise(koulutusRes.uris[ik], koulutusRes, ik));
                }
                var p = $q.all(promises);
                p.then(function(result) {
                    for (var i = 0; i < result.length; i++) {
                        koulutusMap[result[i].koulutus] = result[i].res.result;
                        searchResult.resolve(koulutusMap);
                    }
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
                angular.forEach(result, function(relatios, koulutusUri) {

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

                            var tutkintoModel = $scope.createModel(tutkintoApiModel, "TUTKINTO", koulutusUri, {}, relatios);

                            mapKomos[koulutusUri] = {
                                tutkinto: tutkintoModel,
                                ohjelmas: [],
                            };
                            uiModel.push(tutkintoModel);
                        }

                        //MERGE

                        var mergeDublicate = {};
                        var ohjelmaKeys = _.keys(relatios.ohjelmas.uris);

                        for (var i = 0; i < ohjelmaKeys.length; i++) {
                            //merge by ohjelma.arvo, because the koodiuri can be different, but value in some cases is same.
                            var ohjArvo = relatios.ohjelmas.meta[ohjelmaKeys[i]].arvo;

                            if (angular.isUndefined(mergeDublicate[ohjArvo])) {
                                mergeDublicate[ohjArvo] = {arvo: ohjArvo};
                            }

                            if ($scope.isRequiredKoodisto("osaamisala", ohjelmaKeys[i])) {
                                mergeDublicate[ohjArvo].osaamisala = ohjelmaKeys[i];
                            } else if ($scope.isRequiredKoodisto("koulutusohjelmaamm", ohjelmaKeys[i])) {
                                mergeDublicate[ohjArvo].koulutusohjelma = ohjelmaKeys[i];
                            } else if ($scope.isRequiredKoodisto("lukiolinjat", ohjelmaKeys[i])) {
                                mergeDublicate[ohjArvo].lukiolinja = ohjelmaKeys[i];
                            }
                            mergeDublicate[ohjArvo].ohjelma = ohjelmaKeys[i];
                        }

                        //CONVERT TO API MODEL
                        angular.forEach(mergeDublicate, function(value, key) {
                            var ohjelmaApiModel = {
                                oid: angular.isDefined(mapLoaded[koulutusUri]) && angular.isDefined(mapLoaded[koulutusUri].mapOhjelmas[mergeDublicate[key].ohjelma]) ? mapLoaded[koulutusUri].mapOhjelmas[mergeDublicate[key].ohjelma].oid : null
                            };

                            var ohjelmaModel = $scope.createModel(ohjelmaApiModel, "TUTKINTO_OHJELMA", koulutusUri, mergeDublicate[key], relatios);

                            mapKomos[koulutusUri].ohjelmas.push(ohjelmaModel);
                            uiModel.push(ohjelmaModel);
                        });
                    });

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

        $scope.getUri = function(obj) {
            return angular.isDefined(obj) ? obj.uri : null;
        };

        $scope.getArvo = function(obj) {
            return angular.isDefined(obj) ? obj.arvo : null;
        };

        $scope.getTutkintonimike = function(obj) {
            if (angular.isDefined(obj.tutkintonimikes) && angular.isDefined(obj.tutkintonimikes.uris)) {
                return _.keys(obj.tutkintonimikes.uris);
            } else {
                return [$scope.getUri(obj.tutkintonimike)];
            }
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
                        "uris": $scope.getTutkintonimike(data)
                    },
                    "oppilaitostyyppis": {
                        "module": "TUTKINTO",
                        "uris": []
                    }
                },
                URI: {
                    "koulutusaste": {
                        module: "TUTKINTO",
                        "arvo": $scope.getArvo(data.koulutusaste),
                        "uri": $scope.getUri(data.koulutusaste)
                    },
                    "eqf": {
                        module: "BOTH",
                        "arvo": $scope.getArvo(data.eqf),
                        "uri": $scope.getUri(data.eqf)
                    },
                    "koulutusala": {
                        module: "TUTKINTO",
                        "arvo": $scope.getArvo(data.koulutusala),
                        "uri": $scope.getUri(data.koulutusala)
                    },
                    "tutkinto": {
                        module: "TUTKINTO",
                        "arvo": $scope.getArvo(data.tutkinto),
                        "uri": $scope.getUri(data.tutkinto)
                    },
                    "opintojenLaajuusyksikko": {
                        module: "TUTKINTO",
                        "arvo": $scope.getArvo(data.opintojenLaajuusyksikko),
                        "uri": $scope.getUri(data.opintojenLaajuusyksikko)
                    },
                    "opintoala": {
                        module: "TUTKINTO",
                        "arvo": $scope.getArvo(data.opintoala),
                        "uri": $scope.getUri(data.opintoala)
                    },
                    "koulutusohjelma": {
                        module: "TUTKINTO_OHJELMA",
                        "arvo": objOhjelmat.arvo,
                        "uri": objOhjelmat.koulutusohjelma
                    },
                    "lukiolinja": {
                        module: "TUTKINTO_OHJELMA",
                        "arvo": objOhjelmat.arvo,
                        "uri": objOhjelmat.lukiolinja
                    },
                    "osaamisala": {
                        module: "TUTKINTO_OHJELMA",
                        "arvo": objOhjelmat.arvo,
                        "uri": objOhjelmat.osaamisala
                    },
                    "koulutuskoodi": {
                        module: "BOTH",
                        "arvo": $scope.getArvo(data.koulutuskoodi),
                        "uri": $scope.getUri(data.koulutuskoodi)
                    },
                    "opintojenLaajuusarvo": {
                        module: "TUTKINTO",
                        "arvo": $scope.getArvo(data.opintojenLaajuusarvo),
                        "uri": $scope.getUri(data.opintojenLaajuusarvo)
                    },
                    "nqf": {
                        module: "BOTH",
                        "uri": $scope.getUri(data.nqf)
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
                                arvo: STRUCTURE.URI[key].arvo,
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
    }).controller('PreviewController', function($scope, XLSXReaderService, TutkintoModule, TutkintoohjelmaModule, $q) {

    $scope.model = {
        isProcessing: false,
        showPreview: false,
        sheets: [],
        selectedSheet: null,
        selectedSheetName: null,
        docHandler: null
    };

    $scope.parse = function(excelFile) {
        if (!excelFile) {
            return null;
        }

        return XLSXReaderService.readFile(excelFile, true);
    };

    $scope.fileChanged = function(files) {
        $scope.model.isProcessing = true;
        var promise = null;

        if (files) {
            promise = $scope.parse(files[0]);
        }

        if (promise) {
            promise.then(function(xlsxData) {
                $scope.model.sheets = xlsxData.sheets;

                if ($scope.model.showPreview) {
                    $scope.selectSheet();
                }
                $scope.model.isProcessing = false;
            });
        } else if ($scope.model.showPreview && $scope.model.selectedSheetName && angular.isDefined($scope.model.sheets[  $scope.model.selectedSheetName])) {
            $scope.selectSheet();
            $scope.model.isProcessing = false;
        }
    };

    $scope.selectSheet = function() {
        if ($scope.model.sheets && $scope.model.selectedSheetName && $scope.model.sheets[$scope.model.selectedSheetName]) {
            $scope.model.selectedSheet = $scope.model.sheets[$scope.model.selectedSheetName];
        }
    };

    $scope.$watch("model.selectedSheetName", function(val) {
        $scope.selectSheet();
    });

    $scope.$watch("model.showPreview", function(val) {
        console.log("show", val);
        $scope.selectSheet();
    });

    $scope.updateKuvaus = function() {
        if ($scope.model.docHandler === 'KOULUTUS') {
            //import tutkinto type of description data
            TutkintoModule($scope.model.selectedSheet);
        } else if ($scope.model.docHandler === 'OHJELMA') {
            //import tutkinto-ohjelma type of description data
            TutkintoohjelmaModule($scope.model.selectedSheet);
        } else {
            console.log("Ei valittua käsittelijää.");
        }
    };

}).factory("XLSXReaderService", ['$q', '$rootScope',
    function($q, $rootScope) {

        var service = function(data) {
            angular.extend(this, data);
        };

        service.readFile = function(file, showPreview) {
            var deferred = $q.defer();

            XLSXReader(file, showPreview, function(data) {
                $rootScope.$apply(function() {
                    deferred.resolve(data);
                });
            });

            return deferred.promise;
        };

        return service;
    }
]).service("TutkintoModule", ['$q', '$rootScope', 'TarjontaService',
    function($q, $scope, TarjontaService) {
        function excelSheetConverter(selectedSheet) {
            if (!selectedSheet || !selectedSheet.data) {
                console.log("Empty excel sheet object!");
                return;
            }
            var COLS = {
                'KOULUTUS': {
                    text: false,
                    key: 'KOULUTUS',
                    index: null
                },
                'KOULUTUSTYYPPI': {
                    text: false,
                    key: 'KOULUTUSTYYPPI',
                    index: null
                },
                'MODUULITYYPPI': {
                    text: false,
                    key: 'MODUULITYYPPI',
                    index: null
                },
                'TUTKINNON_TAVOITE': {
                    enum: 'TAVOITTEET',
                    text: true,
                    langs: [
                        {code: 'kieli_fi', key: '_FI', index: null},
                        {code: 'kieli_sv', key: '_SV', index: null},
                        {code: 'kieli_en', key: '_EN', index: null}
                    ]
                },
                'TUTKINNON_RAKENNE': {
                    enum: 'KOULUTUKSEN_RAKENNE',
                    text: true,
                    langs: [
                        {code: 'kieli_fi', key: '_FI', index: null},
                        {code: 'kieli_sv', key: '_SV', index: null},
                        {code: 'kieli_en', key: '_EN', index: null}
                    ]
                },
                'JATKO-OPINTOMAHDOLLISUUDET': {
                    enum: 'JATKOOPINTO_MAHDOLLISUUDET',
                    text: true,
                    langs: [
                        {code: 'kieli_fi', key: '_FI', index: null},
                        {code: 'kieli_sv', key: '_SV', index: null},
                        {code: 'kieli_en', key: '_EN', index: null}
                    ]
                }
            };

            //SEARCH HEADER KEYS


            var headerRow = selectedSheet.data[0];

            //search column indexes by key name
            angular.forEach(COLS, function(col, keyPrefix) {
                if (col.text) {
                    for (var langKeyIndex = 0; langKeyIndex < col.langs.length; langKeyIndex++) {
                        for (var c = 0; c < headerRow.rowdata.length; c++) {
                            if (!col.langs[langKeyIndex]) {
                                throw new Error("Column index missing! Index : " + langKeyIndex);
                            }

                            if (headerRow.rowdata[c].trim() === keyPrefix + col.langs[langKeyIndex].key) {
                                col.langs[langKeyIndex].index = c;
                            }
                        }
                    }
                } else {
                    for (var c = 0; c < headerRow.rowdata.length; c++) {
                        if (headerRow.rowdata[c].trim() === keyPrefix) {
                            col.index = c;
                        }
                    }
                }
            });

            var koulutusObj = {};
            for (var rowIndex = 1; rowIndex < selectedSheet.data.length; rowIndex++) {
                var row = selectedSheet.data[rowIndex].rowdata;

                var koulutusIndex = COLS['KOULUTUS'].index;

                if (!row[koulutusIndex] || row[koulutusIndex] === 'undefined') {
                    console.log("skip empty row", row);
                    continue;
                }

                var koulutusUri = 'koulutus_' + row[koulutusIndex];

                koulutusObj[koulutusUri] = {
                    koulutustyyppis: row[COLS['KOULUTUSTYYPPI'].index],
                    moduulityyppi: row[COLS['MODUULITYYPPI'].index],
                    tekstis: {}
                };

                angular.forEach(COLS, function(col) {
                    if (col.text) {
                        for (var langKeyIndex = 0; langKeyIndex < col.langs.length; langKeyIndex++) {
                            var langObj = col.langs[langKeyIndex];

                            if (!angular.isDefined(koulutusObj[koulutusUri].tekstis[col.enum])) {
                                koulutusObj[koulutusUri].tekstis[col.enum] = {tekstis: {}};
                            }

                            if (row[langObj.index]) {
                                koulutusObj[koulutusUri].tekstis[col.enum]['tekstis'][langObj.code] = row[langObj.index];
                            } else {
                                console.log("column '" + col.enum + "' not found, lang =", langObj.code, "row =", row);
                            }
                        }
                    }
                });
            }
            console.log(koulutusObj);
            return koulutusObj;
        }

        return function(excel) {
            if (!excel) {
                console.log("Empty excel object!");
                return;
            }


            var resource = TarjontaService.komo();

            function searchKomoOid(koulutusUri, koulutustyyppiUri, moduulityyppiEnum) {
                var deferred = $q.defer();

                resource.searchModules({
                    koulutustyyppi: koulutustyyppiUri,
                    koulutus: koulutusUri,
                    moduuli: moduulityyppiEnum,
                    meta: false}, function(res) {
                    var arr = [];
                    if (res.status === 'OK') {
                        for (var resultIndex = 0; resultIndex < res.result.length; resultIndex++) {
                            arr.push({
                                oid: res.result[resultIndex].oid,
                                koulutus: res.result[resultIndex].koulutuskoodiUri
                            });
                        }
                    }
                    deferred.resolve(arr);
                });

                return  deferred.promise;
            }

            var map = excelSheetConverter(excel);
            angular.forEach(map, function(val, koulutusCode) {
                var promises = [];

                if (val.koulutustyyppis && val.koulutustyyppis.split(",").length > 0) {
                    var arrTypes = val.koulutustyyppis.split(",");

                    for (var typeIndex = 0; typeIndex < arrTypes.length; typeIndex++) {
                        if (arrTypes[typeIndex]) {
                            promises.push(searchKomoOid(
                                koulutusCode.trim(),
                                arrTypes[typeIndex].trim(),
                                val.moduulityyppi.trim()
                                ));
                        }
                    }

                    $q.all(promises).then(function(arrKomos) {
                        for (var wrapperIndex = 0; wrapperIndex < arrKomos.length; wrapperIndex++) {
                            for (var komoIndex = 0; komoIndex < arrKomos[wrapperIndex].length; komoIndex++) {
                                var o = map[arrKomos[wrapperIndex][komoIndex].koulutus];
                                if (o) {
                                    TarjontaService.saveKomoTekstis(arrKomos[wrapperIndex][komoIndex].oid, o.tekstis);
                                }
                            }
                        }
                    });
                }
            });
        };
    }
]).service("TutkintoohjelmaModule", ['$q', '$rootScope', 'TarjontaService',
    function($q, $scope, TarjontaService) {
        function excelSheetConverter(selectedSheet) {
            if (!selectedSheet || !selectedSheet.data) {
                console.log("Empty excel sheet object!");
                return;
            }

            var COLS = {
                'KOODISTO': {
                    text: false,
                    key: 'KOODISTO',
                    index: null
                },
                'KOODI_ARVO': {
                    text: false,
                    key: 'KOODI_ARVO',
                    index: null
                },
                'KOULUTUSTYYPPI': {
                    text: false,
                    key: 'KOULUTUSTYYPPI',
                    index: null
                },
                'MODUULITYYPPI': {
                    text: false,
                    key: 'MODUULITYYPPI',
                    index: null
                },
                'KOULUTUSOHJELMAN_TAVOITE': {
                    enum: 'TAVOITTEET',
                    text: true,
                    langs: [
                        {code: 'kieli_fi', key: '_FI', index: null},
                        {code: 'kieli_sv', key: '_SV', index: null},
                        {code: 'kieli_en', key: '_EN', index: null}
                    ]
                }
            };

            //SEARCH HEADER KEYS
            var headerRow = selectedSheet.data[0];

            //search column indexes by key name
            angular.forEach(COLS, function(col, keyPrefix) {
                if (col.text) {
                    for (var langKeyIndex = 0; langKeyIndex < col.langs.length; langKeyIndex++) {
                        for (var c = 0; c < headerRow.rowdata.length; c++) {
                            if (!col.langs[langKeyIndex]) {
                                throw new Error("Column index missing! Index : " + langKeyIndex);
                            }

                            if (headerRow.rowdata[c].trim() === keyPrefix + col.langs[langKeyIndex].key) {
                                col.langs[langKeyIndex].index = c;
                            }
                        }
                    }
                } else {
                    for (var c = 0; c < headerRow.rowdata.length; c++) {
                        if (headerRow.rowdata[c].trim() === keyPrefix) {
                            col.index = c;
                        }
                    }
                }
            });

            var ohjelmaObj = {};
            for (var rowIndex = 1; rowIndex < selectedSheet.data.length; rowIndex++) {
                var row = selectedSheet.data[rowIndex].rowdata;

                var koodiArvoIndex = COLS['KOODI_ARVO'].index;

                if (!row[koodiArvoIndex] || row[koodiArvoIndex] === 'undefined') {
                    console.log("skip row without koodi arvo : " + row && row[koodiArvoIndex] || koodiArvoIndex);
                    continue;
                }

                var koodistoIndex = COLS['KOODISTO'].index;

                if (!row[koodistoIndex] || row[koodistoIndex] === 'undefined') {
                    console.log("skip row without koodisto : " + row && row[koodiArvoIndex] || koodiArvoIndex);
                    continue;
                }

                var moduulityyppiIndex = COLS['MODUULITYYPPI'].index;

                if (!row[moduulityyppiIndex] || row[moduulityyppiIndex] === 'undefined') {
                    console.log("skip row without moduulityyppi : " + row && row[koodiArvoIndex] || koodiArvoIndex);
                    continue;
                }

                //create real uri without version hash for komo search operation
                var ohjelmaUri = row[koodistoIndex] + '_' + row[koodiArvoIndex];

                ohjelmaObj[ohjelmaUri] = {
                    koulutustyyppis: row[COLS['KOULUTUSTYYPPI'].index],
                    moduulityyppi: row[COLS['MODUULITYYPPI'].index],
                    tekstis: {}
                };

                angular.forEach(COLS, function(col) {
                    if (col.text) {
                        for (var langKeyIndex = 0; langKeyIndex < col.langs.length; langKeyIndex++) {
                            var langObj = col.langs[langKeyIndex];

                            if (!angular.isDefined(ohjelmaObj[ohjelmaUri].tekstis[col.enum])) {
                                ohjelmaObj[ohjelmaUri].tekstis[col.enum] = {tekstis: {}};
                            }

                            if (row[langObj.index]) {
                                ohjelmaObj[ohjelmaUri].tekstis[col.enum]['tekstis'][langObj.code] = row[langObj.index];
                            } else {
                                console.log("column '" + col.enum + "' not found, lang =", langObj.code, "row =", row);
                            }
                        }
                    }
                });
            }
            return ohjelmaObj;
        }

        return function(excel) {
            if (!excel) {
                console.log("Empty excel object!");
                return;
            }

            var resource = TarjontaService.komo();

            function searchKomoOid(ohjelmaUri, koulutustyyppiUri, moduulityyppiEnum) {
                var deferred = $q.defer();
                var module = false;
                var tyyppi = false;
                var ohjelma = false;
                if (moduulityyppiEnum === 'TUTKINTO' || moduulityyppiEnum === 'TUTKINTO_OHJELMA') {
                    module = true;
                }

                if (koulutustyyppiUri.indexOf("koulutustyyppi") !== -1) {
                    tyyppi = true;
                }

                if (ohjelmaUri.indexOf("osaamisala") !== -1 || ohjelmaUri.indexOf("lukiolinja") !== -1 || ohjelmaUri.indexOf("koulutusohjelma") !== -1) {
                    ohjelma = true;
                }

                if (module && tyyppi && ohjelma) {
                    resource.searchModules({
                        koulutustyyppi: koulutustyyppiUri,
                        ohjelma: ohjelmaUri,
                        moduuli: moduulityyppiEnum,
                        meta: false}, function(res) {
                        var arr = [];
                        if (res.status === 'OK') {
                            for (var resultIndex = 0; resultIndex < res.result.length; resultIndex++) {
                                arr.push({
                                    oid: res.result[resultIndex].oid,
                                    ohjelma: res.result[resultIndex].ohjelmaUri
                                });
                            }
                        }
                        deferred.resolve(arr);
                    });
                } else {
                    console.log("Invalid excel row data : " + ohjelmaUri + "," + koulutustyyppiUri + "," + moduulityyppiEnum);
                    deferred.resolve([]);
                }

                return deferred.promise;
            }

            var map = excelSheetConverter(excel);
            angular.forEach(map, function(val, koulutusCode) {
                var promises = [];

                if (val.koulutustyyppis && val.koulutustyyppis.split(",").length > 0) {
                    var arrTypes = val.koulutustyyppis.split(",");

                    for (var typeIndex = 0; typeIndex < arrTypes.length; typeIndex++) {
                        if (arrTypes[typeIndex]) {
                            promises.push(searchKomoOid(
                                koulutusCode.trim(),
                                arrTypes[typeIndex].trim(),
                                val.moduulityyppi.trim()
                                ));
                        }
                    }

                    $q.all(promises).then(function(arrKomos) {
                        for (var wrapperIndex = 0; wrapperIndex < arrKomos.length; wrapperIndex++) {
                            for (var komoIndex = 0; komoIndex < arrKomos[wrapperIndex].length; komoIndex++) {
                                var o = map[arrKomos[wrapperIndex][komoIndex].ohjelma];
                                if (o) {
                                    TarjontaService.saveKomoTekstis(arrKomos[wrapperIndex][komoIndex].oid, o.tekstis);
                                }
                            }
                        }
                    });
                }
            });
        };
    }
]);

