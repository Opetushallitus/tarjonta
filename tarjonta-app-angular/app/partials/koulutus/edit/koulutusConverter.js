var app = angular.module('KoulutusConverter', ['ngResource', 'config', 'auth']);
app.factory('KoulutusConverterFactory', function(Koodisto, $log) {

    $log = $log.getInstance("KoulutusConverterFactory");
    $log.debug("init");

    var factory = {};

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

    factory.KUVAUS_ORDER = [
        {type: "TAVOITTEET", isKomo: true, length:2000},
        {type: "LISATIETOA_OPETUSKIELISTA", isKomo: false, length:2000},
        {type: "PAAAINEEN_VALINTA", isKomo: false, length:2000},
        {type: "MAKSULLISUUS", isKomo: false, length:1000},
        {type: "SIJOITTUMINEN_TYOELAMAAN", isKomo: false, length:2000},
        {type: "PATEVYYS", isKomo: true, length:2000},
        {type: "JATKOOPINTO_MAHDOLLISUUDET", isKomo: true, length:2000},
        {type: "SISALTO", isKomo: false, length:2000},
        {type: "KOULUTUKSEN_RAKENNE", isKomo: true, length:5000},
        {type: "LOPPUKOEVAATIMUKSET", isKomo: false, length:2000}, // leiskassa oli "lopputyön kuvaus"
        {type: "KANSAINVALISTYMINEN", isKomo: false, length:2000},
        {type: "YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA", isKomo: false, length:2000},
        {type: "TUTKIMUKSEN_PAINOPISTEET", isKomo: false, length:2000}
    ];

    factory.STRUCTURE = {
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
            opintojenLaajuusyksikko: {}
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
            koulutusasteTyyppi: {"default": 'KORKEAKOULUTUS'},
            tila: {'default': 'LUONNOS'},
            tunniste: {"default": ''},
            suunniteltuKestoArvo: {nullable: true, "default": ''}
        }, DATES: {
            koulutuksenAlkamisPvms: {"default": new Date()}
        }, BOOL: {
            opintojenMaksullisuus: {"default": false}
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
            var henkilo = arrPersons[i];

            if (angular.isUndefined(henkilo.henkiloTyyppi)) {
                throw "Unknown henkilo tyyppi";
            }

            if (!angular.isUndefined(henkilo) && !angular.isUndefined(henkilo.nimet) && henkilo.nimet.length > 0) {
                henkilo.sukunimi = '';
                henkilo.etunimet = '';

                if (Boolean(henkilo.nimet)) {
                    var arrSeparatedNames = henkilo.nimet.split(" ");
                    for (var p = 0; p < arrSeparatedNames.length - 1; p++) {
                        henkilo.etunimet += arrSeparatedNames[p] + " ";
                    }
                    if (arrSeparatedNames.length > 1) {
                        henkilo.sukunimi = arrSeparatedNames[arrSeparatedNames.length - 1 ];
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
    factory.createUiModels = function(uiModel) {
        //single select nodels
        uiModel['contactPerson'] = {henkiloTyyppi: 'YHTEYSHENKILO'};
        uiModel['ectsCoordinator'] = {henkiloTyyppi: 'ECTS_KOORDINAATTORI'};
        uiModel['tabs'] = {lisatiedot: true}; //lisatiedot tab disabled=true

        angular.forEach(factory.STRUCTURE.COMBO, function(value, key) {
            uiModel[key] = factory.createUiKoodistoSingleModel();
        });
        //multi select models
        angular.forEach(factory.STRUCTURE.MCOMBO, function(value, key) {
            uiModel[key] = factory.createUiKoodistoMultiModel();
        });

        angular.forEach(factory.STRUCTURE.DATES, function(value, key) {
            if (angular.isUndefined(uiModel[key])) {
                uiModel[key] = [];
            }
        });

        angular.forEach(factory.STRUCTURE.RELATIONS, function(value, key) {
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
    factory.createAPIModel = function(apiModel, languages) {
        if (angular.isUndefined(languages) || !angular.isArray(languages) || languages.length === 0) {
            factory.throwError("No default language uris, array must have at least one language uri.");
        }

        angular.forEach(factory.STRUCTURE.MLANG, function(value, key) {
            apiModel[key] = {'tekstis': {}};
            angular.forEach(languages, function(lang) {
                apiModel[key].tekstis[lang] = '';
            });
        });

        angular.forEach(factory.STRUCTURE.RELATION, function(value, key) {
            apiModel[key] = factory.createKoodiUriBase('', -1);
        });

        angular.forEach(factory.STRUCTURE.RELATIONS, function(value, key) {
            if (angular.isUndefined(value.skipApiModel) && !value.skipApiModel) {
                apiModel[key] = {'uris': {}};
            }
        });

        angular.forEach(factory.STRUCTURE.COMBO, function(value, key) {
            apiModel[key] = factory.createKoodiUriBase('', -1);
        });

        angular.forEach(factory.STRUCTURE.MCOMBO, function(value, key) {
            apiModel[key] = {'uris': {}};
        });

        angular.forEach(factory.STRUCTURE.DATES, function(value, key) {
            apiModel[key] = [];
        });

        angular.forEach(factory.STRUCTURE.STR, function(value, key) {
            $log.debug(value);
            apiModel[key] = value.default;
        });

        angular.forEach(factory.STRUCTURE.BOOL, function(value, key) {
            apiModel[key] = value.default;
        });

        angular.forEach(factory.STRUCTURE.DESC, function(value, key) {
            apiModel[key] = value.default;
            // factory.addLangForDescUiFields(apiModel[key], languages);
        });


        $log.debug("createAPIModel", apiModel);
    };



    return factory;
});


app.factory('KoulutusConverterFactoryLukio', function(Koodisto, $log) {

    $log = $log.getInstance("KoulutusConverterFactoryLukio");
    $log.debug("init");

    var factory = {};

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

    factory.KUVAUS_ORDER = [
        {type: "SISALTO", isKomo: false, length:2000},
        {type: "KOHDERYHMA", isKomo: false, length:2000},
        {type: "OPPIAINEET_JA_KURSSIT", isKomo: false, length:2000},
        {type: "KANSAINVALISTYMINEN", isKomo: false, length:2000},
        {type: "YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA", isKomo: false, length:2000}
    ];

    factory.STRUCTURE = {
        MLANG: {
            koulutusohjelma: {'defaultLangs': true, "default": {tekstis: []}}
        },
        RELATION: {
            koulutusala: {module: 'TUTKINTO'},
            opintoala: {module: 'TUTKINTO'},
            koulutuslaji: {module: 'TUTKINTO'},
            pohjakoulutusvaatimus: {module: 'TUTKINTO'},
            opintojenLaajuusyksikko: {module: 'TUTKINTO_OHJELMA'},
            opintojenLaajuusarvo: {module: 'TUTKINTO_OHJELMA'},
            tutkintonimike: {module: 'TUTKINTO_OHJELMA'}
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
            koulutusasteTyyppi: {"default": 'LUKIOKOULUTUS'},
            tila: {'default': 'LUONNOS'},
            tunniste: {"default": ''},
            linkkiOpetussuunnitelmaan: {"default": ''},
            suunniteltuKestoArvo: {nullable: true, "default": ''}
        }, DATES: {
            koulutuksenAlkamisPvms: {"default": new Date()}
        }, BOOL: {
        }, DESC: {
            kuvausKomo: {'nullable': false, "default": factory.createBaseDescUiField([
                ])},
            kuvausKomoto: {'nullable': false, "default": factory.createBaseDescUiField([
                ])}
        }
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
            var henkilo = arrPersons[i];

            if (angular.isUndefined(henkilo.henkiloTyyppi)) {
                throw "Unknown henkilo tyyppi";
            }

            if (!angular.isUndefined(henkilo) && !angular.isUndefined(henkilo.nimet) && henkilo.nimet.length > 0) {
                henkilo.sukunimi = '';
                henkilo.etunimet = '';

                if (Boolean(henkilo.nimet)) {
                    var arrSeparatedNames = henkilo.nimet.split(" ");
                    for (var p = 0; p < arrSeparatedNames.length - 1; p++) {
                        henkilo.etunimet += arrSeparatedNames[p] + " ";
                    }
                    if (arrSeparatedNames.length > 1) {
                        henkilo.sukunimi = arrSeparatedNames[arrSeparatedNames.length - 1 ];
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
    factory.createUiModels = function(uiModel) {
        //single select nodels
        uiModel['contactPerson'] = {henkiloTyyppi: 'YHTEYSHENKILO'};
        uiModel['ectsCoordinator'] = {henkiloTyyppi: 'ECTS_KOORDINAATTORI'};
        uiModel['tabs'] = {lisatiedot: true}; //lisatiedot tab disabled=true

        angular.forEach(factory.STRUCTURE.COMBO, function(value, key) {
            uiModel[key] = factory.createUiKoodistoSingleModel();
        });
        //multi select models
        angular.forEach(factory.STRUCTURE.MCOMBO, function(value, key) {

            if (angular.isDefined(value.types)) {
                uiModel[key] = {};
                angular.forEach(value.types, function(valType, keyType) {
                    uiModel[key][valType] = factory.createUiKoodistoMultiModel();
                });
            } else {
                uiModel[key] = factory.createUiKoodistoMultiModel();
            }

        });

        angular.forEach(factory.STRUCTURE.DATES, function(value, key) {
            if (angular.isUndefined(uiModel[key])) {
                uiModel[key] = [];
            }
        });

        angular.forEach(factory.STRUCTURE.RELATIONS, function(value, key) {
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
    factory.createAPIModel = function(apiModel, languages) {
        if (angular.isUndefined(languages) || !angular.isArray(languages) || languages.length === 0) {
            factory.throwError("No default language uris, array must have at least one language uri.");
        }

        angular.forEach(factory.STRUCTURE.MLANG, function(value, key) {
            apiModel[key] = {'tekstis': {}};
            angular.forEach(languages, function(lang) {
                apiModel[key].tekstis[lang] = '';
            });
        });

        angular.forEach(factory.STRUCTURE.RELATION, function(value, key) {
            apiModel[key] = factory.createKoodiUriBase('', -1);
        });

        angular.forEach(factory.STRUCTURE.RELATIONS, function(value, key) {
            if (angular.isUndefined(value.skipApiModel) && !value.skipApiModel) {
                apiModel[key] = {'uris': {}};
            }
        });

        angular.forEach(factory.STRUCTURE.COMBO, function(value, key) {
            apiModel[key] = factory.createKoodiUriBase('', -1);
        });

        angular.forEach(factory.STRUCTURE.MCOMBO, function(value, key) {
            if (angular.isDefined(value.types)) {
                apiModel[key] = {};
                angular.forEach(value.types, function(valType, keyType) {
                    apiModel[key][keyType] = {'uris': {}};
                });
            } else {
                apiModel[key] = {'uris': {}};
            }
        });

        angular.forEach(factory.STRUCTURE.DATES, function(value, key) {
            apiModel[key] = [];
        });

        angular.forEach(factory.STRUCTURE.STR, function(value, key) {
            $log.debug(value);
            apiModel[key] = value.default;
        });

        angular.forEach(factory.STRUCTURE.BOOL, function(value, key) {
            apiModel[key] = value.default;
        });

        angular.forEach(factory.STRUCTURE.DESC, function(value, key) {
            apiModel[key] = value.default;
            // factory.addLangForDescUiFields(apiModel[key], languages);
        });


        $log.debug("createAPIModel", apiModel);
    };



    return factory;
});
