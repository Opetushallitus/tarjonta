angular.module('app.komo.ctrl', ['Tarjonta', 'ngResource', 'config', 'localisation', 'auth', 'Koodisto', 'TarjontaCache', 'TarjontaDateTime'])
        .controller('KomoController', function($scope, $q, TarjontaService, PermissionService, LocalisationService, dialogService) {
            'use strict';
            $scope.controlModel = {
                formStatus: {
                    modifiedBy: '',
                    modified: null,
                    tila: ''
                },
                formControls: {/*reloadDisplayControls: function() {
                    }*/}
            };

            $scope.ctrl = {
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
                    {enum: 'YLIOPISTOKOULUTUS', name: "Yliopistokoulutus"},
                    {enum: 'AMMATTIKORKEAKOULUTUS', name: "Ammattikorkeakoulutus"},
                    {enum: 'KORKEAKOULUTUS', name: "Korkeakoulutus"},
                    {enum: 'PERUSOPETUS', name: "Perusopetus"},
                    {enum: 'PERUSOPETUS_ULKOMAINEN', name: "PerusopetusUlkomainen"},
                    {enum: 'AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA', name: "AmmatillinenPerustutkintoNayttotutkintona"},
                    {enum: 'TUNTEMATON', name: "Tuntematon"}
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
                    {name: "koulutustyyppi", type: "URI"},
                    {name: "organisaatio", type: "OID"},
                    {name: "koulutusala", type: "URI"},
                    {name: "tutkinto", type: "URI"},
                    {name: "opintojenLaajuusyksikko", type: "URI"},
                    {name: "opintoala", type: "URI"},
                    {name: "opintojenLaajuusarvo", type: "URI"},
                    {name: "koulutusohjelma", type: "URI"},
                    {name: "koulutusohjelma", type: "TEXT_MAP"},
                    {name: "tutkintonimikes", type: "URI_MAP"},
                    {name: "oppilaitostyyppis", type: "URI_MAP"},
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
                var resource = TarjontaService.komo();
                resource.search({koulutuskoodi: strKoulutuskoodiUri, meta: false}, function(res) {
                    console.log("loaded", res);
                    $scope.ctrl.link.komos = res.result;

                    for (var i = 0; i < res.result.length; i++) {
                        $scope.ctrl.link.komosMap[res.result[i].komoOid] = res.result[i];
                    }
                });
            };

            $scope.getField = function(field) {
                return  $scope.ctrl.result[field];
            };


            $scope.removeLanguageToFieldTekstis = function(langKey, fieldName) {
                delete fieldName[langKey];
            }

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

        });
