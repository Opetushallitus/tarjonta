angular.module('app.komo.ctrl', ['Tarjonta', 'ngResource', 'config', 'localisation', 'auth', 'Koodisto', 'TarjontaCache', 'TarjontaDateTime'])
        .controller('KomoController', function($scope, $q, TarjontaService, PermissionService) {
            'use strict';
            $scope.ctrl = {
                komoOid: "1.2.246.562.5.2013061010193487239386",
                result: {},
                link: {
                    komos: [],
                    selectedKomoOids: [],
                    selectedLinkOids: [],
                },
                koodi: {uri: '', versio: ''},
                createdDate: new Date(),
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
                    {name: "tunniste", type: "STR"},
                    {name: "koulutusohjelma", type: "TEXT_MAP"},
                    {name: "tutkintonimikes", type: "URI_MAP"},
                    {name: "oppilaitostyyppis", type: "URI_MAP"},
                    {name: "kuvausKomo", type: "TEXT_ENUM_MAP"}
                ]
            };
            $scope.fetchByOid = function(strKomoOid) {
                var resource = TarjontaService.komo();
                resource.get({oid: strKomoOid, meta: false}, function(res) {
                    console.log("loaded", res);
                    $scope.ctrl.result = res.result;
                    $scope.ctrl.createdDate = new Date($scope.ctrl.result['modified']);
                });
            };

            $scope.fetchByKoulutuskoodi = function(strKoulutuskoodiUri) {
                var resource = TarjontaService.komo();
                resource.search({koulutuskoodi: strKoulutuskoodiUri, meta: false}, function(res) {
                    console.log("loaded", res);
                    $scope.ctrl.komos = res.result;
                });
            };

            $scope.getField = function(field) {
                return  $scope.ctrl.result[field];
            };
            $scope.addLanguageToFieldTekstis = function(langKey, fieldName) {
                if (angular.isUndefined(fieldName)) {
                    return;
                }

                if (angular.isUndefined($scope.ctrl.result[fieldName])) {
                    return;
                }

                var field = $scope.ctrl.result[fieldName];
                if (angular.isUndefined(field.tekstis)) {
                    field.tekstis = {};
                }
                field.tekstis[langKey] = '';
            };

            $scope.addLanguageToEnumFieldTekstis = function(langKey, komoEnum, fieldName) {
                if (angular.isUndefined(fieldName)) {
                    return;
                }

                if (angular.isUndefined($scope.ctrl.result[fieldName])) {
                    return;
                }

                var field = $scope.ctrl.result[fieldName];

                if (angular.isUndefined(field[komoEnum])) {
                    field[komoEnum] = {};
                }

                if (angular.isUndefined(field[komoEnum].tekstis)) {
                    field[komoEnum]['tekstis'] = {};
                }

                if (angular.isUndefined(langs[langKey])) {
                    langs[langKey] = {};
                }

                langs[langKey] = '';
            };


            $scope.addUriVersio = function(uri, versio, fieldName) {
                if (angular.isUndefined(fieldName)) {
                    return;
                }

                if (angular.isUndefined($scope.ctrl.result[fieldName])) {
                    return;
                }

                var field = $scope.ctrl.result[fieldName];
                if (angular.isUndefined(field.uris)) {
                    field.uris = {};
                }
                field.uris[uri] = versio;
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
                    });
                });
            };

            $scope.addChilds = function(komoOid, komoOids) {
                if (angular.isUndefined(komoOid) || angular.isUndefined(komoOids)) {
                    return;
                }
                PermissionService.permissionResource().authorize({}, function(authResponse) {
                    TarjontaService.saveResourceLink(komoOid, komoOids,
                            function() {
                            },
                            function() {
                            }
                    );
                });
            };

            $scope.removeLink = function(komoOid, childKomoOid) {
                PermissionService.permissionResource().authorize({}, function(authResponse) {
                    TarjontaService.resourceLink.remove({parent: komoOid, child: childKomoOid}, function(childKomos) {
                    });
                });
            };


            ctrl.result = {
                "result": {
                    "koulutusmoduuliTyyppi": "TUTKINTO",
                    "koulutusaste": {
                        "nimi": "Ammatillinen koulutus",
                        "arvo": "32",
                        "versio": 1,
                        "uri": "koulutusasteoph2002_32"
                    },
                    "eqf": {
                    },
                    "organisaatio": {
                        "nimet": [
                        ]
                    },
                    "koulutusala": {
                        "nimi": "Tekniikan ja liikenteen ala",
                        "arvo": "5",
                        "versio": 1,
                        "uri": "koulutusalaoph2002_5"
                    },
                    "oppilaitostyyppis": {
                        "uris": {
                            "oppilaitostyyppi_21": 1,
                            "oppilaitostyyppi_61": 1,
                            "oppilaitostyyppi_24": 1,
                            "oppilaitostyyppi_23": 1,
                            "oppilaitostyyppi_22": 1,
                            "oppilaitostyyppi_62": 1,
                            "oppilaitostyyppi_63": 1
                        },
                        "meta": {
                            "oppilaitostyyppi_21": {
                                "nimi": "Ammatilliset oppilaitokset",
                                "arvo": "21",
                                "versio": 1,
                                "uri": "oppilaitostyyppi_21"
                            },
                            "oppilaitostyyppi_61": {
                                "nimi": "Musiikkioppilaitokset",
                                "arvo": "61",
                                "versio": 1,
                                "uri": "oppilaitostyyppi_61"
                            },
                            "oppilaitostyyppi_24": {
                                "nimi": "Ammatilliset aikuiskoulutuskeskukset",
                                "arvo": "24",
                                "versio": 1,
                                "uri": "oppilaitostyyppi_24"
                            },
                            "oppilaitostyyppi_23": {
                                "nimi": "Ammatilliset erikoisoppilaitokset",
                                "arvo": "23",
                                "versio": 1,
                                "uri": "oppilaitostyyppi_23"
                            },
                            "oppilaitostyyppi_22": {
                                "nimi": "Ammatilliset erityisoppilaitokset",
                                "arvo": "22",
                                "versio": 1,
                                "uri": "oppilaitostyyppi_22"
                            },
                            "oppilaitostyyppi_62": {
                                "nimi": "Liikunnan koulutuskeskukset",
                                "arvo": "62",
                                "versio": 1,
                                "uri": "oppilaitostyyppi_62"
                            },
                            "oppilaitostyyppi_63": {
                                "nimi": "Kansanopistot",
                                "arvo": "63",
                                "versio": 1,
                                "uri": "oppilaitostyyppi_63"
                            }
                        }
                    },
                    "kuvausKomo": {
                        "JATKOOPINTO_MAHDOLLISUUDET": {
                            "tekstis": {
                                "kieli_sv": "<p>De yrkesinriktade grundexamina samt yrkes- och  specialyrkesexamina ger allmän behörighet för  fortsatta studier vid yrkeshögskolor och universitet. En naturlig väg till fortsatta studier är en  yrkeshögskoleexamen inom utbildningsområdet  teknik och kommunikation, ingenjör (YH). Vid  universitet kan man avlägga teknologie kandidatexamen och diplomingenjörsexamen. De  pedagogiska studierna för yrkeslärare ger möjlighet till påbyggnadsutbildning för yrkeslärarens arbetsuppgifter.</p>",
                                "kieli_fi": "<p>Ammatillisista perustutkinnoista sekä ammatti- ja erikoisammattitutkinnoista saa yleisen jatko-opintokelpoisuuden ammattikorkeakouluihin  ja yliopistoihin. Luonteva jatko-opintoväylä on  tekniikan ja liikenteenalan ammattikorkeakoulututkinto, insinööri(AMK). Yliopistossa voi suorittaa esimerkiksi tekniikan kandidaatin ja diplomi-insinöörin tutkinnon. Ammatillisen opettajan  pedagogiset opinnot antavat jatkokoulutusmahdollisuuden ammatillisen opettajan työtehtäviin.</p>"
                            }
                        },
                        "TAVOITTEET": {
                            "tekstis": {
                                "kieli_sv": "<p>Flygledare som har avlagt grunexamen i flygledning  kan tryggt leda den trafik de ansvarar för. De leder flera luftfarkoster och landtrafiken samtidigt utan att förorsaka onödiga förseningar i flygtrafiken. De kan agera i snabbt föränderliga trafiksituationer och i varje situation tillämpa de mest lämpliga trafiklösningarna och ledningsmetoderna. I arbetet är de serviceinriktade och kostnadsmedvetna och kan samarbeta och lösa problem. De utvärderar hur de trafiklösningar de gjort inverkar på kommande trafiksituationer.</p>",
                                "kieli_fi": "<p>Lennonjohdon perustutkinnon suorittanut lennonjohtaja johtaa vastuullaan olevaa liikennettä turvallisesti. Hän johtaa useita ilma-aluksia sekä maaliikennettä samanaikaisesti aiheuttamatta tarpeetonta viivytystä lentoliikenteelle. Hän toimii nopeasti vaihtuvissa liikennetilanteissa ja soveltaa kuhunkin tilanteeseen parhaiten sopivia liikenneratkaisuja ja johtamismenetelmiä. Hän on työssään palvelualtis, kustannustietoinen, yhteistyökykyinen ja ongelmanratkaisutaitoinen. Hän arvioi tekemiensä liikenneratkaisujen vaikutukset tuleviin liikennetilanteisiin.</p>"
                            }
                        },
                        "KOULUTUKSEN_RAKENNE": {
                            "tekstis": {
                                "kieli_sv": "<p>Examensdelarna flygtrafiktjänst (45 sv) och flygtrafikledningstjänst (45 sv) är obligatoriska för alla examinander. Examensdelar som individuellt fördjupar yrkeskompetensen (examensdelar som breddar grundexamen) är områdeskontrolltjänst (30 sv) och examensdelar inom den grundläggande yrkesutbildningen som individuellt fördjupar yrkeskompetensen och som erbjuds lokalt.</p>",
                                "kieli_fi": "<p>Kaikille pakolliset tutkinnon osat ovat lennonvarmistuspalvelu (45 ov) ja ilmaliikennepalvelu (45 ov).</p> <p>Tämän lisäksi tutkintoon kuuluu ammattitaitoa täydentävät tutkinnon osat ammatillisessa peruskoulutuksessa (yhteiset opinnot) 20 ov sekä vapaasti valittavat tutkinnon osat ammatillisessa peruskoulutuksessa (10 ov). Tutkinnon osiin sisältyy työssäoppimista vähintään 20 ov, ja opinnäyte vähintään 2 ov. Ammatillista osaamista yksilöllisesti syventävä tutkinnon osa (perustutkintoa laajentava tutkinnon osa) koostuu aluelennonjohtopalvelusta (30 ov) ja ammatillista osaamista yksilöllisesti syventävistä paikallisesti tarjottavista tutkinnon osista.</p>"
                            }
                        }
                    },
                    "yhteyshenkilos": [
                    ],
                    "tutkinto": {
                    },
                    "modified": 1385643453032,
                    "tila": "JULKAISTU",
                    "opintojenLaajuusyksikko": {
                        "nimi": "opintoviikkoa",
                        "arvo": "1",
                        "versio": 1,
                        "uri": "opintojenlaajuusyksikko_1"
                    },
                    "opintoala": {
                        "nimi": "Ajoneuvo- ja kuljetustekniikka",
                        "arvo": "509",
                        "versio": 1,
                        "uri": "opintoalaoph2002_509"
                    },
                    "koulutusohjelma": {
                        "tekstis": {
                        }
                    },
                    "oid": "1.2.246.562.5.2013061010193487239386",
                    "koulutuskoodi": {
                        "nimi": "Lennonjohdon perustutkinto",
                        "arvo": "381410",
                        "versio": 1,
                        "uri": "koulutus_381410"
                    },
                    "kuvausKomoto": {
                    },
                    "tutkintonimikes": {
                        "uris": {
                        }
                    },
                    "koulutusasteTyyppi": "AMMATILLINEN_PERUSKOULUTUS",
                    "opintojenLaajuus": {
                        "nimi": "120",
                        "arvo": "120",
                        "versio": 1,
                        "uri": "opintojenlaajuus_120"
                    },
                    "komoOid": "1.2.246.562.5.2013061010193487239386"
                },
                "status": "OK"
            }

        });
