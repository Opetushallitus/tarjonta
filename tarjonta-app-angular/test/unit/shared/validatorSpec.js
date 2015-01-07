describe('Validator', function() {

    var validatorService;
    var hakukohde = {};
    var haku = {};
    var model = {};

    beforeEach(function() {
        module('Validator');
        inject(function($injector) {
            validatorService = $injector.get('ValidatorService');
        });
    });

    describe('validate aikuiskoulutus lukio', function() {
        haku.jarjestelmanHakulomake = true;

        hakukohde.toteutusTyyppi = 'LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA';
        model.hakukohde = hakukohde;
        model.liitteidenSahkoinenOsoiteEnabled = false;
        model.liitteidenMuuOsoiteEnabled = false;

        it('should be valid', function() {
            validatorService.hakukohde.validate(model, haku);
        });
    });
});
