var app = angular.module('KoulutusConverter', ['ngResource', 'config', 'auth']);
app.factory('KoulutusConverterFactory', function(Koodisto, $log) {
    $log = $log.getInstance("KoulutusConverterFactory");
    $log.debug("init");

    var factory = {};

    /******************************************/
    /* KOULUTUS COMMON DATA HELPPER FUNCTIONS */
    /******************************************/

    factory.isNull = function(obj) {
        if (obj === null || typeof obj === 'undefined') {
            return true;
        } else {
            return false;
        }
    };

    factory.createBaseUiFieldArvo = function(arvo) {
        return {"arvo": arvo};
    };

    factory.createLanguage = function(apiModel, langKoodiUri) {
        apiModel[langKoodiUri] = factory.createKoodiUriBase(langKoodiUri, -1);
    };
    factory.createKoodiUriBase = function(uri, versio) {
        return {'uri': uri, 'versio': versio};
    };


    factory.createMetaLanguages = function(apiModel, languageUris) {
        if (angular.isUndefined(apiModel)) {
            factory.throwError('Tarjonta API model object cannot be undefined!');
        }

        angular.forEach(languageUris, function(langUri) {
            factory.addTekstisLanguage(apiModel, langUri);
        });
    };

    factory.sortMetaLanguage = function(apiModel, languageUri) {
        if (angular.isUndefined(apiModel) || angular.isUndefined(apiModel.meta)) {
            factory.throwError('Tarjonta API model object cannot be undefined!');
        }
        var tempMeta = apiModel.meta;
        apiModel.meta = {};

        for (var i in languageUri) {
            apiModel.meta[languageUri[i]] = tempMeta[languageUri[i]];
        }

        for (var i in tempMeta) {//add all other
            if (angular.isUndefined(apiModel.meta[i])) {
                apiModel.meta[i] = tempMeta[i];
            }
        }
    };

    factory.addTekstisLanguage = function(apiModel, languageUri) {
        if (factory.isNull(apiModel[languageUri])) {
            apiModel[languageUri] = {};
            factory.createLanguage(apiModel, languageUri);
        }
    };

    factory.createBaseDescUiField = function(arrKeys) {
        var base = {};

        angular.forEach(arrKeys, function(val) {
            base[val] = {tekstis: {}};
        });

        return base;
    };

    factory.addLangForDescUiFields = function(apiModel, langs) {
        angular.forEach(apiModel, function(val, kuvausKey) {
            angular.forEach(langs, function(lang) {
                val.tekstis[lang] = '';
            });
        });
    };

    /**
     * Convert person data to UI format.
     *
     * @param {type} person
     * @returns {person}
     */
    factory.converPersonObjectForUi = function(person) {
        if (factory.isNull(person)) {
            factory.throwError('Contact percon cannot be null');
        }
        person.nimet = person.etunimet + ' ' + person.sukunimi;
        return person; //dummy
    };

    /**
     * Convert koodisto component data model to API meta model.
     *
     * @param {type} json object map
     * @param {type} koodisto component object
     */
    factory.convertKoodistoComboToMetaDTO = function(metaMap, kbObj) {
        if (factory.isNull(kbObj)) {
            return {}; //return an empty object;
        }
        metaMap[kbObj.koodiUri] = factory.convertKoodistoComboToKoodiDTO(kbObj);
    };


    factory.convertKoodistoRelationApiModel = function(kbObj) {
        return factory.apiModelUri(kbObj.koodiUri, kbObj.koodiVersio);
    };
    /**
     * Convert koodisto component data model to API koodi model.
     *
     * @param {type} koodisto component object
     */
    factory.convertKoodistoComboToKoodiDTO = function(kbObj) {
        if (factory.isNull(kbObj)) {
            return {}; //return an empty object;
        }
        return {"koodi": factory.apiModelUri(kbObj.koodiUri, kbObj.koodiVersio)};
    };
    factory.apiModelUri = function(uri, versio) {
        return {"uri": uri, "versio": versio};
    };

    factory.convertKoodistoCombo = function(strValue, kbObj) {
        return {'arvo': strValue, 'koodi': factory.apiModelUri(kbObj.koodiUri, kbObj.koodiVersio)};
    };

    /**
     * @param string value
     * @param {'data' : [koodisto koodis], 'uri : 'koodisto_uri'} uiModel
     * @returns {@exp;factory@call;convertKoodistoComboToKoodiDTO}
     */
    factory.convertKoodistoComboToKoodiUiDTO = function(arvo, uiModel) {
        //uiModel.data; all option data items from a koodisto in 'Tarjonta KoodiType' objects.
        //convert only the selected koodi URI.
        $log.debug("uiModel", uiModel);
        return factory.convertKoodistoCombo(arvo, factory.searchKoodiByKoodiUri(uiModel.uri, uiModel));
    };
    factory.convertPersonsUiModelToDto = function(arrPersons) {
        var arrOutputPersons = [];
        var i = 0;
        for (; i < arrPersons.length; i++) {

            var henkilo = angular.copy(arrPersons[i]);

            if (angular.isUndefined(henkilo)) {
                continue;
            }

            if (angular.isUndefined(henkilo.henkiloTyyppi)) {
                throw "Unknown henkilo tyyppi";
            }

            if (!angular.isUndefined(henkilo) && !angular.isUndefined(henkilo.nimet) && henkilo.nimet.length > 0) {
                henkilo.sukunimi = '';
                henkilo.etunimet = '';

                if (henkilo.nimet) {

                    if (henkilo.nimet.indexOf(" ") == -1) { //ei välilyöntiä nimessä
                        henkilo.etunimet = henkilo.nimet;
                    } else {
                        var arrSeparatedNames = henkilo.nimet.split(" ");
                        for (var p = 0; p < arrSeparatedNames.length - 1; p++) {
                            henkilo.etunimet += arrSeparatedNames[p];
                            console.log("etunimi");
                        }
                        if (arrSeparatedNames.length > 1) {
                            henkilo.sukunimi = arrSeparatedNames[arrSeparatedNames.length - 1 ];
                            console.log("sukunimi");
                        }
                    }
                }

                delete henkilo.nimet;
                delete henkilo.kielet;
                arrOutputPersons.push(henkilo);
            }
        }

        return arrOutputPersons;
    };

    factory.deleteMetaField = function(obj) {
        if (!factory.isNull(obj) && !factory.isNull(obj.meta)) {
            delete obj.meta;
        }
    };
    factory.createUiModels = function(uiModel, tyyppi) {
        //single select nodels
        uiModel['contactPerson'] = {henkiloTyyppi: 'YHTEYSHENKILO'};
        uiModel['ectsCoordinator'] = {henkiloTyyppi: 'ECTS_KOORDINAATTORI'};
        uiModel['tabs'] = {lisatiedot: true}; //lisatiedot tab disabled=true

        angular.forEach(factory.STRUCTURE[tyyppi].COMBO, function(value, key) {
            uiModel[key] = factory.createUiKoodistoSingleModel();
        });
        //multi select models
        angular.forEach(factory.STRUCTURE[tyyppi].MCOMBO, function(value, key) {

            if (angular.isDefined(value.types)) {
                uiModel[key] = {};
                angular.forEach(value.types, function(valType, keyType) {
                    uiModel[key][valType] = factory.createUiKoodistoMultiModel();
                });
            } else {
                uiModel[key] = factory.createUiKoodistoMultiModel();
            }

        });

        angular.forEach(factory.STRUCTURE[tyyppi].DATES, function(value, key) {
            if (angular.isUndefined(uiModel[key])) {
                uiModel[key] = [];
            }
        });

        angular.forEach(factory.STRUCTURE[tyyppi].RELATIONS, function(value, key) {
            uiModel[key] = factory.createUiMetaMultiModel();
        });

        uiModel.showSuccess = false;

        return uiModel;
    };

    factory.createUiKoodistoSingleModel = function() {
        return {'uri': null, koodis: []};
    };
    factory.createUiKoodistoMultiModel = function() {
        return {'uris': [], koodis: []};
    };
    factory.createUiMetaMultiModel = function() {
        return {'uris': [], meta: []};
    };

    factory.throwError = function(msg) {
        throw new Error('Tarjonta application error - ' + msg);
    };
    factory.searchKoodiByKoodiUri = function(koodiUri, uiModel) {
        var i = 0;
        var koodiObjects = uiModel.data;
        for (; i < koodiObjects.length; i++) {
            if (koodiObjects[i].koodiUri === koodiUri) {
                return koodiObjects[i];
            }
        }
        console.error("No koodi found by ", koodiUri);
        return {};
    };
    factory.removeKoodiByKoodiUri = function(koodiUri, uiModel) {
        var i = 0;
        var koodiObjects = uiModel.uris;
        for (; i < koodiObjects.length; i++) {
            if (koodiObjects[i] === koodiUri) {
                koodiObjects.splice(i, 1);
            }
        }
    };

    factory.updateOrganisationApiModel = function(apiModel, oid, nimi) {
        if (factory.isNull(apiModel)) {
            factory.throwError('API model must be object, or empty object');
        }
        //fetch org name and OID from Organisation service
        apiModel.organisaatio = {"oid": oid, "nimi": nimi}
    };

    /**
     * Create full data model for tarjonta rest service.
     *
     * @param {type} apiModel
     * @returns {undefined}
     */
    factory.createAPIModel = function(apiModel, languages, toteutustyyppi) {
        if (angular.isUndefined(languages) || !angular.isArray(languages) || languages.length === 0 || angular.isUndefined(toteutustyyppi) || toteutustyyppi === null) {
            factory.throwError("No default language uris, array must have at least one language uri.");
        }

        angular.forEach(factory.STRUCTURE[toteutustyyppi].MLANG, function(value, key) {
            apiModel[key] = {'tekstis': {}};
            angular.forEach(languages, function(lang) {
                apiModel[key].tekstis[lang] = '';
            });
        });

        angular.forEach(factory.STRUCTURE[toteutustyyppi].RELATION, function(value, key) {
            apiModel[key] = factory.createKoodiUriBase('', -1);
        });

        angular.forEach(factory.STRUCTURE[toteutustyyppi].RELATIONS, function(value, key) {
            if (angular.isUndefined(value.skipApiModel) && !value.skipApiModel) {
                apiModel[key] = {'uris': {}};
            }
        });

        angular.forEach(factory.STRUCTURE[toteutustyyppi].COMBO, function(value, key) {
            apiModel[key] = factory.createKoodiUriBase('', -1);
        });

        angular.forEach(factory.STRUCTURE[toteutustyyppi].MCOMBO, function(value, key) {
            if (angular.isDefined(value.types)) {
                apiModel[key] = {};
                angular.forEach(value.types, function(valType, keyType) {
                    if (angular.isDefined(value.default)) {
                        apiModel[key][keyType] = value.default;
                    } else {
                        apiModel[key][keyType] = {'uris': {}};
                    }
                });
            } else {
                if (angular.isDefined(value.default)) {
                    apiModel[key] = value.default;
                } else {
                    apiModel[key] = {'uris': {}};
                }
            }
        });

        angular.forEach(factory.STRUCTURE[toteutustyyppi].DATES, function(value, key) {
            apiModel[key] = [];
        });

        angular.forEach(factory.STRUCTURE[toteutustyyppi].STR, function(value, key) {
            apiModel[key] = value.default;
        });

        angular.forEach(factory.STRUCTURE[toteutustyyppi].IMAGES, function(value, key) {
            apiModel[key] = value.default;
        });

        angular.forEach(factory.STRUCTURE[toteutustyyppi].BOOL, function(value, key) {
            apiModel[key] = value.default;
        });

        angular.forEach(factory.STRUCTURE[toteutustyyppi].DESC, function(value, key) {
            apiModel[key] = value.default;
            // factory.addLangForDescUiFields(apiModel[key], languages);
        });


        $log.debug("createAPIModel", apiModel);
    };

    /*************************************************/
    /* INITIALIZATION PARAMETERS BY TOTEUTUSTYYPPI */
    /*************************************************/

    var GENERIC_VALMISTAVA_STRUCTURE = {
        page: 'nayttotutkinto',
        KUVAUS_ORDER: [
            {type: "TAVOITTEET", isKomo: true},
            {type: "KOULUTUKSEN_RAKENNE", isKomo: true},
            {type: "OSAAMISALAN_VALINTA", isKomo: false, length: 1500},
            {type: "NAYTTOTUTKINNON_SUORITTAMINEN", isKomo: false, length: 1500},
            {type: "MAKSULLISUUS", isKomo: false, length: 1500},
            {type: "SIJOITTUMINEN_TYOELAMAAN", isKomo: false, length: 1500},
            {type: "YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA", isKomo: false, length: 1500},
            {type: "JATKOOPINTO_MAHDOLLISUUDET", isKomo: true}
        ],
        MLANG: {},
        RELATION: {
            koulutusaste: {module: 'TUTKINTO'},
            koulutuskoodi: {module: 'TUTKINTO'},
            koulutusohjelma: {module: 'TUTKINTO_OHJELMA'},
            koulutusala: {module: 'TUTKINTO'},
            opintoala: {module: 'TUTKINTO'},
            tutkintonimike: {},
            koulutustyyppi: {module: 'TUTKINTO_OHJELMA'},
            koulutuslaji: {module: 'TUTKINTO'}
        }, COMBO: {
            koulutuksenAlkamiskausi: {nullable: true, koodisto: 'koodisto-uris.koulutuksenAlkamisvuosi'}
        }, MCOMBO: {
            opetuskielis: {koodisto: 'koodisto-uris.kieli'},
            ammattinimikkeet: {koodisto: 'koodisto-uris.ammattinimikkeet'}
        }, STR: {
            koulutuksenAlkamisvuosi: {"default": ''},
            toteutustyyppi: {"default": null}, //no default value!
            tila: {'default': 'LUONNOS'},
            tunniste: {'default': ''},
            tarkenne: {'default': ''}
        }, DATES: {
            koulutuksenAlkamisPvms: {"default": new Date()}
        }, BOOL: {
            opintojenMaksullisuus: {"default": false}
        }, IMAGES: {
        }, DESC: {
            kuvausKomo: {'nullable': false, "default": factory.createBaseDescUiField([
                ])},
            kuvausKomoto: {'nullable': false, "default": factory.createBaseDescUiField([
                ])}
        }
    };

    factory.STRUCTURE = {
        /*********************************************/
        /*  KORKEAKOULUTUS INITIALIZATION PARAMETERS */
        /*********************************************/
        KORKEAKOULUTUS: {
            KUVAUS_ORDER: [
                {type: "TAVOITTEET", isKomo: true, length: 2000},
                {type: "LISATIETOA_OPETUSKIELISTA", isKomo: false, length: 2000},
                {type: "PAAAINEEN_VALINTA", isKomo: false, length: 2000},
                {type: "MAKSULLISUUS", isKomo: false, length: 1000},
                {type: "SIJOITTUMINEN_TYOELAMAAN", isKomo: false, length: 2000},
                {type: "PATEVYYS", isKomo: true, length: 2000},
                {type: "JATKOOPINTO_MAHDOLLISUUDET", isKomo: true, length: 2000},
                {type: "SISALTO", isKomo: false, length: 2000},
                {type: "KOULUTUKSEN_RAKENNE", isKomo: true, length: 5000},
                {type: "LOPPUKOEVAATIMUKSET", isKomo: false, length: 2000}, // leiskassa oli "lopputyön kuvaus"
                {type: "KANSAINVALISTYMINEN", isKomo: false, length: 2000},
                {type: "YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA", isKomo: false, length: 2000},
                {type: "TUTKIMUKSEN_PAINOPISTEET", isKomo: false, length: 2000}
            ],
            MLANG: {
                koulutusohjelma: {'defaultLangs': true, "default": {tekstis: []}}
            },
            RELATION: {
                koulutuskoodi: {},
                koulutusaste: {},
                koulutusala: {},
                opintoala: {},
                eqf: {},
                tutkinto: {},
                opintojenLaajuusyksikko: {},
                koulutustyyppi: {}
            },
            RELATIONS: {
                tutkintonimikes: {},
                opintojenLaajuusarvos: {skipApiModel: true}
            }, COMBO: {
                //in correct place
                suunniteltuKestoTyyppi: {koodisto: 'koodisto-uris.suunniteltuKesto'},
                koulutuksenAlkamiskausi: {nullable: true, koodisto: 'koodisto-uris.koulutuksenAlkamisvuosi'},
                opintojenLaajuusarvo: {skipUiModel: true}
                //waiting for missing koodisto relations, when the relations are created, move the fields to RELATION object.
            }, MCOMBO: {
                opetusmuodos: {koodisto: 'koodisto-uris.opetusmuotokk'},
                opetusAikas: {koodisto: 'koodisto-uris.opetusaika'},
                opetusPaikkas: {koodisto: 'koodisto-uris.opetuspaikka'},
                opetuskielis: {koodisto: 'koodisto-uris.kieli'},
                aihees: {koodisto: 'koodisto-uris.aiheet'},
                ammattinimikkeet: {koodisto: 'koodisto-uris.ammattinimikkeet'}
            }, STR: {
                koulutuksenAlkamisvuosi: {"default": ''},
                koulutusmoduuliTyyppi: {"default": 'TUTKINTO'},
                toteutustyyppi: {"default": 'KORKEAKOULUTUS'},
                tila: {'default': 'LUONNOS'},
                tunniste: {"default": ''},
                suunniteltuKestoArvo: {nullable: true, "default": ''}
            }, DATES: {
                koulutuksenAlkamisPvms: {"default": new Date()}
            }, BOOL: {
                opintojenMaksullisuus: {"default": false}
            }, IMAGES: {
                opintojenRakenneKuvas: {"default": {}}
            }, DESC: {
                kuvausKomo: {'nullable': false, "default": factory.createBaseDescUiField([
                        'KOULUTUKSEN_RAKENNE',
                        'JATKOOPINTO_MAHDOLLISUUDET',
                        'TAVOITTEET',
                        'PATEVYYS'
                    ])},
                kuvausKomoto: {'nullable': false, "default": factory.createBaseDescUiField([
                        'MAKSULLISUUS',
                        'ARVIOINTIKRITEERIT',
                        'LOPPUKOEVAATIMUKSET',
                        'PAINOTUS',
                        'KOULUTUSOHJELMAN_VALINTA',
                        'KUVAILEVAT_TIEDOT',
                        'SISALTO',
                        'SIJOITTUMINEN_TYOELAMAAN',
                        'KANSAINVALISTYMINEN',
                        'YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA',
                        'LISATIETOA_OPETUSKIELISTA',
                        'TUTKIMUKSEN_PAINOPISTEET',
                        'PAAAINEEN_VALINTA'
                    ])}
            }
        },
        /*******************************************/
        /* LUKIOKOULUTUS INITIALIZATION PARAMETERS */
        /*******************************************/
        LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA: {
            KUVAUS_ORDER: [
                {type: "TAVOITTEET", isKomo: true, length: 2000},
                {type: "JATKOOPINTO_MAHDOLLISUUDET", isKomo: true, length: 2000},
                {type: "KOULUTUKSEN_RAKENNE", isKomo: true, length: 2000},
                {type: "SISALTO", isKomo: false, length: 2000},
                {type: "KOHDERYHMA", isKomo: false, length: 2000},
                {type: "OPPIAINEET_JA_KURSSIT", isKomo: false, length: 2000},
                {type: "KANSAINVALISTYMINEN", isKomo: false, length: 2000},
                {type: "YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA", isKomo: false, length: 2000}
            ],
            MLANG: {},
            RELATION: {
                koulutuskoodi: {module: 'TUTKINTO'},
                koulutusohjelma: {module: 'TUTKINTO_OHJELMA'},
                koulutusala: {module: 'TUTKINTO'},
                opintoala: {module: 'TUTKINTO'},
                koulutuslaji: {module: 'TUTKINTO'},
                pohjakoulutusvaatimus: {module: 'TUTKINTO'},
                opintojenLaajuusyksikko: {module: 'TUTKINTO_OHJELMA'},
                opintojenLaajuusarvo: {module: 'TUTKINTO_OHJELMA'},
                tutkintonimike: {module: 'TUTKINTO_OHJELMA'},
                koulutustyyppi: {module: 'TUTKINTO_OHJELMA'}
            }, COMBO: {
                suunniteltuKestoTyyppi: {koodisto: 'koodisto-uris.suunniteltuKesto'},
                koulutuksenAlkamiskausi: {nullable: true, koodisto: 'koodisto-uris.koulutuksenAlkamisvuosi'},
            }, MCOMBO: {
                kielivalikoima: {
                    koodisto: 'koodisto-uris.kieli',
                    types: ['A1A2KIELI', 'B1KIELI', 'B2KIELI', 'B3KIELI', 'VALINNAINEN_OMAN_AIDINKIELEN_OPETUS', 'MUUT_KIELET']
                },
                opetusmuodos: {koodisto: 'koodisto-uris.opetusmuotokk'},
                opetusAikas: {koodisto: 'koodisto-uris.opetusaika'},
                opetusPaikkas: {koodisto: 'koodisto-uris.opetuspaikka'},
                opetuskielis: {koodisto: 'koodisto-uris.kieli'},
                lukiodiplomit: {koodisto: 'koodisto-uris.lukiodiplomit'}
            }, STR: {
                koulutuksenAlkamisvuosi: {"default": ''},
                toteutustyyppi: {"default": 'LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA'},
                tila: {'default': 'LUONNOS'},
                tunniste: {"default": ''},
                linkkiOpetussuunnitelmaan: {"default": ''},
                suunniteltuKestoArvo: {nullable: true, "default": ''}
            }, DATES: {
                koulutuksenAlkamisPvms: {"default": new Date()}
            }, BOOL: {
            }, IMAGES: {
            }, DESC: {
                kuvausKomo: {'nullable': false, "default": factory.createBaseDescUiField([
                    ])},
                kuvausKomoto: {'nullable': false, "default": factory.createBaseDescUiField([
                    ])}
            }
        },
        /*******************************************/
        /* AMMATILLINEN INITIALIZATION PARAMETERS  */
        /*******************************************/
        AMMATTITUTKINTO: GENERIC_VALMISTAVA_STRUCTURE,
        ERIKOISAMMATTITUTKINTO: GENERIC_VALMISTAVA_STRUCTURE,
        AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA: GENERIC_VALMISTAVA_STRUCTURE,
        AMMATILLINEN_NAYTTOTUTKINTONA_VALMISTAVA: {//not enum
            KUVAUS_ORDER: [
                {type: "SISALTO", isKomo: false, length: 1500},
                {type: "KOHDERYHMA", isKomo: false, length: 1500},
                {type: "OPISKELUN_HENKILOKOHTAISTAMINEN", isKomo: false, length: 1500},
                {type: "KANSAINVALISTYMINEN", isKomo: false, length: 1500}
            ],
            MLANG: {},
            RELATION: {
            }, COMBO: {
                suunniteltuKestoTyyppi: {koodisto: 'koodisto-uris.suunniteltuKesto'}
            }, MCOMBO: {
                opetusmuodos: {koodisto: 'koodisto-uris.opetusmuotokk'},
                opetusAikas: {koodisto: 'koodisto-uris.opetusaika'},
                opetusPaikkas: {koodisto: 'koodisto-uris.opetuspaikka'}
            }, STR: {
                linkkiOpetussuunnitelmaan: {"default": ''},
                suunniteltuKestoArvo: {nullable: true, "default": ''},
                hinta: {"default": ''}
            }, DATES: {
            }, BOOL: {
                opintojenMaksullisuus: {"default": false}
            }, IMAGES: {
            }, DESC: {
                kuvaus: {'nullable': false, "default": factory.createBaseDescUiField([
                    ])}
            }
        }
    };

    factory.validateOutputData = function(m, toteutustyyppi) {
        //remove all meta data fields, if any
        angular.forEach(factory.STRUCTURE[toteutustyyppi], function(value, key) {
            if ('MLANG' !== key) {
                //MLANG objects needs the meta fields
                angular.forEach(value, function(value, key) {
                    factory.deleteMetaField(m[key]);
                });
            }
        });
    };

    factory.saveModelConverter = function(apiModel, uiModel, toteutustyyppi) {
        factory.validateOutputData(apiModel, toteutustyyppi);

        /*
         * DATA CONVERSIONS FROM UI MODEL TO API MODEL
         * Convert person object to back-end object format.
         */

        apiModel.yhteyshenkilos = factory.convertPersonsUiModelToDto([uiModel.contactPerson, uiModel.ectsCoordinator]);
        /*
         * Convert Koodisto komponent object to back-end object format.
         */


        //for single select models
        angular.forEach(factory.STRUCTURE[toteutustyyppi].COMBO, function(value, key) {
            //search version information for list of uris;

            var koodis = uiModel[key].koodis;
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

        angular.forEach(factory.STRUCTURE[toteutustyyppi].RELATIONS, function(value, key) {
            if (angular.isUndefined(value.skipApiModel)) {
                apiModel[key] = {'uris': {}};
                //search version information for list of uris;
                var map = {};
                var meta = uiModel[key].meta;
                for (var i in meta) {
                    map[meta[i].uri] = meta[i].versio;
                }
                angular.forEach(uiModel[key].uris, function(uri) {
                    apiModel[key].uris[uri] = map[uri];
                });
            }
        });

        //multi-select models, add version to the koodi
        angular.forEach(factory.STRUCTURE[toteutustyyppi].MCOMBO, function(value, key) {

            if (angular.isDefined(value.types)) {
                apiModel[key] = {};
                angular.forEach(value.types, function(type) {
                    apiModel[key][type] = {'uris': {}};
                    //search version information for list of uris;
                    var map = {};
                    var koodis = uiModel[key].koodis;
                    for (var i in koodis) {
                        map[koodis[i].koodiUri] = koodis[i].koodiVersio;
                    }
                    angular.forEach(uiModel[key][type].uris, function(uri) {
                        apiModel[key][type].uris[uri] = map[uri];
                    });
                });
            }
            else if (angular.isDefined(uiModel[key].uris)) {
                apiModel[key] = {'uris': {}};
                //search version information for list of uris;
                var map = {};
                var koodis = uiModel[key].koodis;
                for (var i in koodis) {
                    map[koodis[i].koodiUri] = koodis[i].koodiVersio;
                }
                angular.forEach(uiModel[key].uris, function(uri) {
                    apiModel[key].uris[uri] = map[uri];
                });
            }
        });

        angular.forEach(factory.STRUCTURE[toteutustyyppi].IMAGES, function(value, key) {
            for (var i in apiModel[key]) {
                if (angular.isUndefined(apiModel[key][i].base64data) || apiModel[key][i].base64data === null) {
                    apiModel[key][i] = null;
                }
            }
        });

        $log.debug(JSON.stringify(apiModel));
        return apiModel;
    };

    return factory;
});
