angular.module('app.komo.ctrl', ['Tarjonta', 'ngResource', 'config', 'localisation', 'auth', 'Koodisto', 'TarjontaCache', 'TarjontaDateTime'])
        .controller('KomoController', function($scope, $q, TarjontaService, PermissionService) {
            'use strict';
            $scope.ctrl = {
                apiKeys: {},
                saved: false,
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
                    {enum: 'KOPIO', name: "Kopio"},
                    {enum: 'LUONNOS', name: "Luonnos"},
                    {enum: 'VALMIS', name: "Valmis"}],
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
                    {enum: 'KORKEAKOULUTUS', name: "Korkeakoulutus"}
                ],
                textFields: [
                    {name: "modified", type: "DATE"},
                    {name: "tunniste", type: "STR"},
                    {name: "koulutusmoduuliTyyppi", type: "ENUM_TUTKINTO"},
                    {name: "koulutusasteTyyppi", type: "ENUM_KOULUTUS"},
                    {name: "tila", type: "ENUM_STATUS"},
                    {name: "koulutusaste", type: "URI"},
                    {name: "eqf", type: "URI"},
                    //{name: "organisaatio", type: "OID"},
                    {name: "koulutusala", type: "URI"},
                    {name: "tutkinto", type: "URI"},
                    {name: "opintojenLaajuusyksikko", type: "URI"},
                    {name: "opintoala", type: "URI"},
                    {name: "koulutuskoodi", type: "URI"},
                    {name: "opintojenLaajuus", type: "URI"},
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

            $scope.save = function() {
                var resource = TarjontaService.komo();
                PermissionService.permissionResource().authorize({}, function(authResponse) {

                    $scope.ctrl.result.modified = (new Date()).getTime();
                    if (angular.isUndefined($scope.ctrl.komoOid) || $scope.ctrl.komoOid === "") {
                        delete $scope.ctrl.result.oid;
                        delete $scope.ctrl.result.komoOid;
                    }

                    resource.save($scope.ctrl.result, function(res) {
                        console.log("saved", res);
                        $scope.ctrl.komoOid = res.result.komoOid;
                        $scope.ctrl.saved = true;

                    });
                });
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


//            var koodisPromise = Koodisto.getAllKoodisWithKoodiUri(Config.env[value.koodisto], $scope.ctrl.koodistoLocale);
//            koodisPromise.then(function(result) {
//                uiModel[key].koodis = result;
//            });
        });
