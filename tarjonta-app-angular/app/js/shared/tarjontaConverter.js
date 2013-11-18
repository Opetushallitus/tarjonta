var app = angular.module('TarjontaConverter', ['ngResource', 'config', 'auth']);
app.factory('TarjontaConverterFactory', function(Koodisto) {
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

    factory.createBaseUiField = function(uri, versio, arvo) {
        return {"arvo": arvo, "koodi": {"uri": uri, "versio": versio}};
    };

    factory.createLanguage = function(apiModel, langKoodiUri) {
        apiModel[langKoodiUri] = {'koodi': {'arvo': '', 'uri': langKoodiUri, 'versio': -1}};
    };

    factory.createMetaLanguages = function(apiModel, languageUris) {
        if (angular.isUndefined(apiModel)) {
            factory.throwError('Tarjonta API model object cannot be undefined!');
        }
        angular.forEach(languageUris, function(langUri) {
            factory.addMetaLanguage(apiModel, langUri);
        });
    };

    factory.addMetaLanguage = function(apiModel, languageUri) {
        if (angular.isUndefined(apiModel)) {
            factory.throwError('Tarjonta API model object cannot be undefined!');
        }
        var metas = apiModel.meta;

        if (angular.isUndefined(metas)) {
            factory.throwError('Tarjonta API model meta object cannot be undefined!');
        }

        //console.log('LANG', languageUri, metas, apiModel);
        if (factory.isNull(metas[languageUri])) {
            metas[languageUri] = {};
            factory.createLanguage(metas, languageUri);
        }
    };

    factory.addMetaField = function(obj) {
        if (factory.isNull(obj)) {
            return {"meta": {}};
        } else {
            obj.meta = {};
            return obj;
        }
    };

    factory.createBaseDescUiField = function(arrKeys) {
        var base = {};

        angular.forEach(arrKeys, function(val) {
            base[val] = factory.addMetaField(null);
            factory.createMetaLanguages(base[val], []);
        });

        return {"tekstis": base};
    };

    factory.addLangForDescUiField = function(apiModel, key, lang) {
        if (angular.isUndefined(apiModel.tekstis)) {
            factory.throwError('Tarjonta API model tekstis object cannot be undefined!');
        }

        if (angular.isUndefined(apiModel.tekstis[key])) {
            apiModel.tekstis[key] = {meta: {}};
        }

        factory.addMetaLanguage(apiModel.tekstis[key], lang);
    };

    factory.addLangForDescUiFields = function(apiModel, lang) {
        angular.forEach(apiModel.tekstis, function(val, key) {
            factory.addLangForDescUiField(apiModel, key, lang);
        });
    };

    factory.STRUCTURE = {
        MLANG: {
            koulutusohjelma: {'validate': true, 'required': true, 'nullable': false, 'defaultLangs': true, default: factory.createBaseUiField(null, '-1', null)}
        },
        RELATION: {
            koulutuskoodi: {'validate': true, 'required': true, nullable: false},
            koulutusaste: {'validate': true, 'required': true, nullable: false},
            koulutusala: {'validate': true, 'required': true, nullable: false},
            opintoala: {'validate': true, 'required': true, nullable: false},
            eqf: {'validate': true, 'required': true, nullable: false},
            tutkinto: {'validate': true, 'required': true, nullable: false},
            tutkintonimike: {'validate': true, 'required': true, nullable: false}
        }, COMBO: {
            //in correct place
            suunniteltuKesto: {'validate': true, 'required': true, nullable: false, koodisto: 'koodisto-uris.suunniteltuKesto'},
            opintojenLaajuus: {'validate': true, 'required': true, nullable: false, koodisto: 'koodisto-uris.opintojenLaajuusarvo'}
            //waiting for missing koodisto relations, when the relations are created, move the fields to RELATION object.
        }, MCOMBO: {
            pohjakoulutusvaatimukset: {'validate': true, 'required': true, nullable: false, koodisto: 'koodisto-uris.pohjakoulutusvaatimus_kk'},
            opetusmuodos: {'validate': true, 'required': true, nullable: false, koodisto: 'koodisto-uris.opetusmuoto'},
            opetuskielis: {'validate': true, 'required': true, nullable: false, koodisto: 'koodisto-uris.kieli'},
            teemas: {'validate': true, 'required': true, nullable: false, koodisto: 'koodisto-uris.teemat'},
            ammattinimikkeet: {'validate': true, 'required': true, nullable: false, koodisto: 'koodisto-uris.ammattinimikkeet'}
        }, STR: {
            koulutusmoduuliTyyppi: {'validate': true, 'required': true, nullable: false, default: 'TUTKINTO'},
            koulutusasteTyyppi: {'validate': true, 'required': true, nullable: false, default: 'KORKEAKOULUTUS'},
            tila: {'validate': true, 'required': true, 'nullable': false, 'default': 'LUONNOS'},
            tunniste: {'type': 'STR', 'validate': true, 'required': true, nullable: true, default: ''}
        }, DATE: {
            koulutuksenAlkamisPvm: {'type': 'DATE', 'validate': true, 'required': true, nullable: false, default: new Date()}
        }, BOOL: {
            opintojenMaksullisuus: {'type': 'BOOL', 'validate': true, 'required': true, nullable: false, default: false}
        }, DESC: {
            kuvausKomo: {'validate': true, 'required': true, 'nullable': false, default: factory.createBaseDescUiField([
                    'KOULUTUKSEN_RAKENNE',
                    'JATKOOPINTO_MAHDOLLISUUDET',
                    'TAVOITTEET',
                    'PATEVYYS'
                ])},
            kuvausKomoto: {'validate': true, 'required': true, 'nullable': false, default: factory.createBaseDescUiField([
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
     * Convert multi select koodisto component data model to API (json map) meta model.
     * 
     * @param {type} koodisto component objects
     */
    factory.convertKoodistoMultiToKoodiUiDTOs = function(uiModel) {
        var metaMap = {};
        //uiModel.data; all option data items from a koodisto in 'Tarjonta KoodiType' objects. 
        var koodiUris = uiModel.uris; //only the selected koodi URIs.

        if (factory.isNull(koodiUris) || koodiUris.length === 0) {
//no items selected, then skip the code block.
            return metaMap;
        }

        var koodiIndex = 0;
        for (; koodiIndex < koodiUris.length; koodiIndex++) {
            factory.convertKoodistoComboToMetaDTO(metaMap, factory.searchKoodiByKoodiUri(koodiUris[koodiIndex], uiModel));
        }

        return metaMap;
    };
    /**
     * @param string value 
     * @param {'data' : [koodisto koodis], 'uri : 'koodisto_uri'} uiModel 
     * @returns {@exp;factory@call;convertKoodistoComboToKoodiDTO}
     */
    factory.convertKoodistoComboToKoodiUiDTO = function(arvo, uiModel) {
        //uiModel.data; all option data items from a koodisto in 'Tarjonta KoodiType' objects. 
        //convert only the selected koodi URI.
        console.log("uiModel", uiModel);
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

        uiModel['tekstis'] = {
            TAVOITTEET: {meta: {kieli_fi: {koodi: {arvo: '', uri: 'kieli_fi', versio: 1}}, kieli_sv: {koodi: {arvo: '', uri: 'kieli_sv', versio: 1}}, kieli_en: {koodi: {arvo: '', uri: 'kieli_en', versio: 1}}}},
            KOULUTUKSEN_RAKENNE: {meta: {kieli_fi: {koodi: {arvo: '', uri: 'kieli_fi', versio: 1}}, kieli_sv: {koodi: {arvo: '', uri: 'kieli_sv', versio: 1}}, kieli_en: {koodi: {arvo: '', uri: 'kieli_en', versio: 1}}}},
            JATKOOPINTO_MAHDOLLISUUDET: {meta: {kieli_fi: {koodi: {arvo: '', uri: 'kieli_fi', versio: 1}}, kieli_sv: {koodi: {arvo: '', uri: 'kieli_sv', versio: 1}}, kieli_en: {koodi: {arvo: '', uri: 'kieli_en', versio: 1}}}}};

        angular.forEach(factory.STRUCTURE.COMBO, function(value, key) {
            uiModel[key] = factory.createUiKoodistoSingleModel();
        });
        //multi select models
        angular.forEach(factory.STRUCTURE.MCOMBO, function(value, key) {
            uiModel[key] = factory.createUiKoodistoMultiModel();
        });



        return uiModel;
    };

    factory.createUiKoodistoSingleModel = function() {
        return {'arvo': '', 'data': [], 'uri': null};
    };
    factory.createUiKoodistoMultiModel = function() {
        return {'data': [], 'uris': []};
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
            apiModel[key] = factory.addMetaField(value.default);
            factory.createMetaLanguages(apiModel[key], languages);
        });

        angular.forEach(factory.STRUCTURE.RELATION, function(value, key) {
            apiModel[ key] = factory.createBaseUiField(null, null, null);
        });

        angular.forEach(factory.STRUCTURE.COMBO, function(value, key) {
            apiModel[key] = factory.createBaseUiField(null, null, null);
        });

        angular.forEach(factory.STRUCTURE.MCOMBO, function(value, key) {
            apiModel[key] = factory.addMetaField(factory.createBaseUiField(null, null, null));
        });

        angular.forEach(factory.STRUCTURE.DATE, function(value, key) {
            if (!angular.isUndefined(apiModel[key])) {
                apiModel[key] = new Date(apiModel[key]); //example convert long to date koulutuksenAlkamisPvm
            } else {
                apiModel[key] = value.default;
            }
        });

        angular.forEach(factory.STRUCTURE.STR, function(value, key) {
            apiModel[key] = value.default;
        });

        angular.forEach(factory.STRUCTURE.BOOL, function(value, key) {
            apiModel[key] = value.default;
        });

        angular.forEach(factory.STRUCTURE.DESC, function(value, key) {
            apiModel[key] = value.default;
            factory.addLangForDescUiFields(apiModel[key], languages[0]);
        });


        console.log("createAPIModel", apiModel);
    };



    return factory;
});
