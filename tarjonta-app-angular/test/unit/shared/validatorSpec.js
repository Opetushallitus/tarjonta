describe('Validator', function() {

    var validatorService;
    var haku = {};
    var model = {};
    model.hakukohde = {};

    beforeEach(function() {
        module('Validator');
        module('Organisaatio');
        inject(function($injector) {
            validatorService = $injector.get('ValidatorService');
        });
    });

    var ValidKK = function() {
        this.toteutusTyyppi = 'KORKEAKOULUTUS';
        this.hakuOid = '1.2.3';
        this.hakukohteenNimet = ['Nimi'];
        this.hakukelpoisuusvaatimusUris = ['uri'];
    };

    var ValidLukio = function() {
        this.toteutusTyyppi = 'LUKIOKOULUTUS';
        this.hakuOid = '1.2.3';
        this.hakukohteenNimiUri = 'hakukohteenNimiUri';
        this.liitteidenToimitusOsoite = {osoiterivi1: 'Katu', postinumero: '00100'};
        this.sahkoinenToimitusOsoite = 'http://opintopolku.fi';
        this.painotettavatOppiaineet = [{painokerroin: '5', oppiaineUri: 'oppiaineUri'}];
    };

    function firstErrorMessage(errors) {
        return errors[0].errorMessageKey;
    }

    describe('validate aikuiskoulutus lukio', function() {
        beforeEach(function() {
            haku.jarjestelmanHakulomake = true;
            model.hakukohde.toteutusTyyppi = 'LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA';
            model.liitteidenSahkoinenOsoiteEnabled = false;
            model.liitteidenMuuOsoiteEnabled = false;
        });

        it('should not be valid with empty name', function() {
            model.hakukohde.hakukohteenNimiUri = '';
            model.hakukohde.hakuOid = '1.2.3';
            var errors = validatorService.hakukohde.validate(model, haku);
            expect(errors.length).toBe(1);
            expect(firstErrorMessage(errors)).toBe('hakukohde.edit.nimi.missing');
        });

        it('should not be valid with empty haku oid', function() {
            model.hakukohde.hakukohteenNimiUri = 'hakukohteenNimiUri';
            model.hakukohde.hakuOid = '';
            var errors = validatorService.hakukohde.validate(model, haku);
            expect(errors.length).toBe(1);
            expect(firstErrorMessage(errors)).toBe('hakukohde.edit.haku.missing');
        });

        it('should not be valid with empty lisatiedot', function() {
            model.hakukohde.hakukohteenNimiUri = 'hakukohteenNimiUri';
            model.hakukohde.hakuOid = '1.2.3';
            haku.jarjestelmanHakulomake = false;
            var errors = validatorService.hakukohde.validate(model, haku);
            expect(errors.length).toBe(1);
            expect(firstErrorMessage(errors)).toBe('hakukohde.edit.lisatietoja-hakemisesta.required');
        });

        it('should be valid', function() {
            model.hakukohde.hakukohteenNimiUri = 'hakukohteenNimiUri';
            model.hakukohde.hakuOid = '1.2.3';
            var errors = validatorService.hakukohde.validate(model, haku);
            expect(errors.length).toBe(0);
        });
    });

    describe('validate hakukelpoisuusvaatimus', function() {
        beforeEach(function() {
            haku.jarjestelmanHakulomake = true;
            model.liitteidenSahkoinenOsoiteEnabled = false;
            model.liitteidenMuuOsoiteEnabled = false;
            model.hakukohde = new ValidKK();
        });

        it('should not be valid with undefined hakukelpoisuusvaatimus', function() {
            model.hakukohde.hakukelpoisuusvaatimusUris = undefined;
            var errors = validatorService.hakukohde.validate(model, haku);
            expect(errors.length).toBe(1);
            expect(firstErrorMessage(errors)).toBe('tarjonta.hakukohde.hakukelpoisuusvaatimus.missing');
        });

        it('should not be valid with empty hakukelpoisuusvaatimus', function() {
            model.hakukohde.hakukelpoisuusvaatimusUris = [];
            var errors = validatorService.hakukohde.validate(model, haku);
            expect(errors.length).toBe(1);
            expect(firstErrorMessage(errors)).toBe('tarjonta.hakukohde.hakukelpoisuusvaatimus.missing');
        });
    });

    describe('validate liitteiden toimitustiedot', function() {
        beforeEach(function() {
            haku.jarjestelmanHakulomake = true;
            model.liitteidenSahkoinenOsoiteEnabled = true;
            model.liitteidenMuuOsoiteEnabled = true;
            model.hakukohde = new ValidLukio();
        });

        it('should not be valid with empty osoiterivi', function() {
            model.hakukohde.liitteidenToimitusOsoite.osoiterivi1 = '';
            var errors = validatorService.hakukohde.validate(model, haku);
            expect(errors.length).toBe(1);
            expect(firstErrorMessage(errors)).toBe('hakukohde.edit.liitteet.toimitusosoite.errors');
        });

        it('should not be valid with empty postinumero', function() {
            model.hakukohde.liitteidenToimitusOsoite.postinumero = '';
            var errors = validatorService.hakukohde.validate(model, haku);
            expect(errors.length).toBe(1);
            expect(firstErrorMessage(errors)).toBe('hakukohde.edit.liitteet.toimitusosoite.errors');
        });

        it('should not be valid with empty sahkoinen toimitusosoite', function() {
            model.hakukohde.sahkoinenToimitusOsoite = '';
            var errors = validatorService.hakukohde.validate(model, haku);
            expect(errors.length).toBe(1);
            expect(firstErrorMessage(errors)).toBe('hakukohde.edit.liitteet.sahkoinenosoite.errors');
        });
    });

    describe('validate names', function() {
        beforeEach(function() {
            haku.jarjestelmanHakulomake = true;
            model.liitteidenSahkoinenOsoiteEnabled = false;
            model.liitteidenMuuOsoiteEnabled = false;
            model.hakukohde = new ValidKK();
        });

        it('should not be valid with empty names', function() {
            model.hakukohde.hakukohteenNimet = [];
            var errors = validatorService.hakukohde.validate(model, haku);
            expect(errors.length).toBe(1);
            expect(firstErrorMessage(errors)).toBe('hakukohde.edit.nimi.missing');
        });

        it('should not be valid with too long name', function() {
            model.hakukohde.hakukohteenNimet = [new Array(227).join('a')];
            var errors = validatorService.hakukohde.validate(model, haku);
            expect(errors.length).toBe(1);
            expect(firstErrorMessage(errors)).toBe('hakukohde.edit.nimi.too.long');
        });
    });

    describe('validate painotettavat oppiaineet', function() {
        beforeEach(function() {
            haku.jarjestelmanHakulomake = true;
            model.liitteidenSahkoinenOsoiteEnabled = false;
            model.liitteidenMuuOsoiteEnabled = false;
            model.hakukohde = new ValidLukio();
        });

        it('should not be valid with empty painokerroin', function() {
            model.hakukohde.painotettavatOppiaineet = [{painokerroin: '', oppiaineUri: 'oppiaineUri'}];
            var errors = validatorService.hakukohde.validate(model, haku);
            expect(errors.length).toBe(1);
            expect(firstErrorMessage(errors)).toBe('tarjonta.hakukohde.edit.painotettavatOppiaineet.errors');
        });

        it('should not be valid with empty oppiaine', function() {
            model.hakukohde.painotettavatOppiaineet = [{painokerroin: '5', oppiaineUri: ''}];
            var errors = validatorService.hakukohde.validate(model, haku);
            expect(errors.length).toBe(1);
            expect(firstErrorMessage(errors)).toBe('tarjonta.hakukohde.edit.painotettavatOppiaineet.errors');
        });
    });

    describe('validate validate valintakokeet', function() {
        beforeEach(function() {
            haku.jarjestelmanHakulomake = true;
            model.liitteidenSahkoinenOsoiteEnabled = false;
            model.liitteidenMuuOsoiteEnabled = false;
            model.hakukohde = new ValidKK();
        });

        it('should be valid with new and all empty values', function() {
            model.hakukohde.valintakokeet = [{
                valintakoeNimi: '',
                valintakoetyyppi: '',
                valintakokeenKuvaus: {},
                valintakoeAjankohtas: [],
                isNew: true
            }];
            var errors = validatorService.hakukohde.validate(model, haku);
            expect(errors.length).toBe(0);
        });

        it('should not be valid with empty nimi', function() {
            model.hakukohde.valintakokeet = [{
                valintakoeNimi: '',
                valintakoetyyppi: '',
                valintakokeenKuvaus: {
                    teksti: ''
                },
                valintakoeAjankohtas: []
            }];
            var errors = validatorService.hakukohde.validate(model, haku);
            expect(errors.length).toBe(1);
            expect(firstErrorMessage(errors)).toBe('hakukohde.edit.valintakokeet.errors');
        });

        it('should not be valid with empty kuvaus', function() {
            model.hakukohde.valintakokeet = [{
                valintakoeNimi: 'valintakoeNimi',
                valintakoetyyppi: '',
                valintakokeenKuvaus: {},
                valintakoeAjankohtas: []
            }];
            var errors = validatorService.hakukohde.validate(model, haku);
            expect(errors.length).toBe(1);
            expect(firstErrorMessage(errors)).toBe('hakukohde.edit.valintakokeet.errors');
        });

        it('should not be valid with invalid ajankohta', function() {
            model.hakukohde.valintakokeet = [{
                valintakoeNimi: 'valintakoeNimi',
                valintakoetyyppi: '',
                valintakokeenKuvaus: {
                    teksti: 'teksti'
                },
                valintakoeAjankohtas: [{
                    alkaa: '',
                    osoite: {}
                }]
            }];
            var errors = validatorService.hakukohde.validate(model, haku);
            expect(errors.length).toBe(1);
            expect(firstErrorMessage(errors)).toBe('hakukohde.edit.valintakokeet.errors');
        });
    });
});
