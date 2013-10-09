var app = angular.module('TarjontaConverter', ['ngResource', 'config', 'auth']);
app.factory('TarjontaConverterFactory', function(Koodisto) {
    var factory = {};

    factory.STRUCTURE = {
        RELATION: {
            koulutuskoodi: {'validate': true, 'required': true, nullable: false},
            koulutusaste: {'validate': true, 'required': true, nullable: false},
            koulutusala: {'validate': true, 'required': true, nullable: false},
            opintoala: {'validate': true, 'required': true, nullable: false}
        }, COMBO: {
            tutkinto: {'type': 'COMBO', 'validate': true, 'required': true, nullable: false, koodisto: 'koodisto-uris.tutkinto'},
            tutkintonimike: {'type': 'COMBO', 'validate': true, 'required': true, nullable: false, koodisto: 'koodisto-uris.tutkintonimike'},
            eqf: {'type': 'COMBO', 'validate': true, 'required': true, nullable: false, koodisto: 'koodisto-uris.eqf-luokitus'},
            suunniteltuKesto: {'type': 'COMBO', 'validate': true, 'required': true, nullable: false, koodisto: 'koodisto-uris.suunniteltuKesto'},
            opintojenLaajuus: {'type': 'COMBO', 'validate': true, 'required': true, nullable: false, koodisto: 'koodisto-uris.opintojenLaajuusarvo'}
        }, MCOMBO: {
            pohjakoulutusvaatimukset: {'validate': true, 'required': true, nullable: false, koodisto: 'koodisto-uris.pohjakoulutusvaatimus'},
            opetusmuodos: {'validate': true, 'required': true, nullable: false, koodisto: 'koodisto-uris.opetusmuoto'},
            opetuskielis: {'validate': true, 'required': true, nullable: false, koodisto: 'koodisto-uris.kieli'},
            teemas: {'validate': true, 'required': true, nullable: false, koodisto: 'koodisto-uris.teemat'}
        }, STR: {
            koulutusmoduuliTyyppi: {'validate': true, 'required': true, nullable: false, default: 'TUTKINTO'},
            koulutusasteTyyppi: {'validate': true, 'required': true, nullable: false, default: 'AMMATTIKORKEAKOULUTUS'},
            tila: {'validate': true, 'required': true, nullable: false}, default: 'LUONNOS',
            tunniste: {'type': 'STR', 'validate': true, 'required': true, nullable: true, default: ''}
        }, DATE: {
            koulutuksenAlkamisPvm: {'type': 'DATE', 'validate': true, 'required': true, nullable: false, default: new Date()}
        }, BOOL: {
            opintojenMaksullisuus: {'type': 'BOOL', 'validate': true, 'required': true, nullable: false, default: false}
        }
    };


    /**
     * Create full data model for tarjonta rest service.
     * 
     * @param {type} apiModel
     * @returns {undefined}
     */
    factory.createAPIModel = function(apiModel) {
//Make sure that object has meta data object.
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
            apiModel[key] = value.default;
        });

        angular.forEach(factory.STRUCTURE.STR, function(value, key) {
            apiModel[key] = value.default;
        });

        angular.forEach(factory.STRUCTURE.BOOL, function(value, key) {
            apiModel[ key] = value.default;
        });
    };
    factory.createBaseUiFieldArvo = function(arvo) {
        return {"arvo": arvo};
    };
    factory.createBaseUiField = function(uri, versio, arvo) {
        return {"arvo": arvo, "koodi": {"uri": uri, "versio": versio}};
    };
    factory.addMetaField = function(obj) {
        if (factory.isNull(obj)) {
            return {"meta": {}};
        } else {
            obj.meta = {};
            return obj;
        }
    };
    factory.isNull = function(obj) {
        if (obj === null || typeof obj === 'undefined') {
            return true;
        } else {
            return false;
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
            throw 'Contact percon cannot be null';
        }
        person.nimet = person.etunimet + ' ' + person.sukunimi;
        return person; //dummy
    }

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
    }

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
            if (Boolean(henkilo) && Boolean(henkilo.sahkoposti) && Boolean(henkilo.titteli) && Boolean(henkilo.puhelin)) {
                if (Boolean(henkilo.nimet)) {
                    var lastname = henkilo.nimet.slice(henkilo.length - 1, henkilo.length);
                    var firstnames = henkilo.nimet.slice(henkilo.nimet, fruits.indexOf(lastname) - 1);
                    henkilo.etunimet = firstnames.join(' ');
                    henkilo.sukunimi = lastname;
                }

                delete henkilo.nimet;
                delete henkilo.kielet;
                arrOutputPersons.push(henkilo)
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
        angular.forEach(factory.STRUCTURE.COMBO, function(value, key) {
            uiModel[key] = factory.createUiKoodistoSingleModel();
        });
        //multi select models
        angular.forEach(factory.STRUCTURE.MCOMBO, function(value, key) {
            uiModel[key] = factory.createUiKoodistoMultiModel();
        });

    };
    factory.createUiKoodistoSingleModel = function() {
        return {'arvo': '', 'data': {}, 'uri': null};
    };
    factory.createUiKoodistoMultiModel = function() {
        return {'data': [], 'uris': []};
    };
    factory.throwError = function(msg) {
        throw 'Tarjonta application error - ' + msg;
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
    return factory;
});
