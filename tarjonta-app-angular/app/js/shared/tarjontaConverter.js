var app = angular.module('TarjontaConverter', ['ngResource', 'config', 'auth']);

app.factory('TarjontaConverterFactory', function(Koodisto) {
    var factory = {};

    /**
     * Create full data model for tarjonta rest service.
     * 
     * @param {type} apiModel
     * @returns {undefined}
     */
    factory.createAPIModel = function(apiModel) {
        //Make sure that object has meta data object.

        apiModel.koulutuskoodi = factory.createBaseUiField(null, null, null);
        apiModel.koulutusaste = factory.createBaseUiField(null, null, null);
        apiModel.koulutusala = factory.createBaseUiField(null, null, null);
        apiModel.opintoala = factory.createBaseUiField(null, null, null);
        apiModel.tutkinto = factory.createBaseUiField(null, null, null);
        apiModel.tutkintonimike = factory.createBaseUiField(null, null, null);
        apiModel.eqf = factory.createBaseUiField(null, null, null);

        apiModel.tunniste = '';
        apiModel.koulutuksenAlkamisPvm = new Date();

        //base koodi uri object
        apiModel.suunniteltuKesto = factory.createBaseUiField(null, null, null);
        apiModel.opintojenLaajuus = factory.createBaseUiField(null, null, null);

        apiModel.teemas = factory.createBaseUiField(null, null, null);
        apiModel.opetuskielis = factory.createBaseUiField(null, null, null);
        apiModel.pohjakoulutusvaatimukset = factory.createBaseUiField(null, null, null);
        apiModel.opetusmuodos = factory.createBaseUiField(null, null, null);

        //base multilang meta data object;
        apiModel.koulutusohjelma = factory.addMetaField(factory.createBaseUiField(null, null, null));
        apiModel.teemas = factory.addMetaField(apiModel.teemas);
        apiModel.opetuskielis = factory.addMetaField(apiModel.opetuskielis);
        apiModel.pohjakoulutusvaatimukset = factory.addMetaField(apiModel.pohjakoulutusvaatimukset);
        apiModel.opetusmuodos = factory.addMetaField(apiModel.opetusmuodos);

        if (factory.isNull(apiModel.opintojenMaksullisuus)) {
            apiModel.opintojenMaksullisuus = false;
        }

        if (factory.isNull(apiModel.koulutusmoduuliTyyppi)) {
            apiModel.koulutusmoduuliTyyppi = 'TUTKINTO';
        }

        if (factory.isNull(apiModel.koulutusasteTyyppi)) {
            //TODO: currently only one type of objects.
            apiModel.koulutusasteTyyppi = 'AMMATTIKORKEAKOULUTUS';
        }

        if (factory.isNull(apiModel.tila)) {
            apiModel.tila = 'LUONNOS';
        }
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
        if (converter.isNull(person)) {
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
        if (converter.isNull(kbObj)) {
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
        if (converter.isNull(kbObj)) {
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
        var kcIndex = 0;
        var metaMap = {};

        var kbObjects = uiModel.data;
        for (; kcIndex < kbObjects.length; kcIndex++) {
            factory.convertKoodistoComboToMetaDTO(metaMap, kbObjects[kcIndex]);
        }

        return metaMap;
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
        if (!converter.isNull(obj) && !converter.isNull(obj.meta)) {
            delete obj.meta;
        }
    };

    factory.createUiModels = function(uiModel) {
        //single select nodels
        uiModel.suunniteltuKesto = factory.createUiKoodistoSingleModel();
        uiModel.opintojenLaajuus = factory.createUiKoodistoSingleModel();

        //multi select models
        uiModel.teemas = factory.createUiKoodistoMultiModel();
        uiModel.opetuskielis = factory.createUiKoodistoMultiModel();
        uiModel.pohjakoulutusvaatimukset = factory.createUiKoodistoMultiModel();
        uiModel.opetusmuodos = factory.createUiKoodistoMultiModel();
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

    return factory;
});
