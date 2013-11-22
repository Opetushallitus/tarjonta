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

    factory.STRUCTURE = {
        MLANG: {
            koulutusohjelma: {'validate': true, 'required': true, 'nullable': false, 'defaultLangs': true, default: {tekstis: []}}
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
            suunniteltuKestoTyyppi: {'validate': true, 'required': true, nullable: false, koodisto: 'koodisto-uris.suunniteltuKesto'},
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
            tunniste: {'type': 'STR', 'validate': true, 'required': true, nullable: true, default: ''},
            suunniteltuKestoArvo: {'type': 'STR', 'validate': true, 'required': true, nullable: true, default: ''}
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

        angular.forEach(factory.STRUCTURE.COMBO, function(value, key) {
            uiModel[key] = factory.createUiKoodistoSingleModel();
        });
        //multi select models
        angular.forEach(factory.STRUCTURE.MCOMBO, function(value, key) {
            uiModel[key] = factory.createUiKoodistoMultiModel();
        });
        
        uiModel.showSuccess = false;

        return uiModel;
    };

    factory.createUiKoodistoSingleModel = function() {
        return {'uri': null};
    };
    factory.createUiKoodistoMultiModel = function() {
        return {koodis: [], 'uris': []};
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

        angular.forEach(factory.STRUCTURE.COMBO, function(value, key) {
            apiModel[key] = factory.createKoodiUriBase('', -1);
        });

        angular.forEach(factory.STRUCTURE.MCOMBO, function(value, key) {
            apiModel[key] = {'uris': {}};
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
            // factory.addLangForDescUiFields(apiModel[key], languages);
        });


        console.log("createAPIModel", apiModel);
    };



    return factory;
});
