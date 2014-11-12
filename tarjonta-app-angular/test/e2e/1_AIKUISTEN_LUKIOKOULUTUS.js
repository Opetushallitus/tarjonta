var testKoulutus = require('../e2eTestKoulutus.js');
var testHelper = require('../e2eTestHelper.js');
var nextYear = new Date().getFullYear() + 1;
var languageSelection = testHelper.languageSelection;

testKoulutus('Aikuisten lukiokoulutus', 'Kauniaisten lukio', {
    perustiedot: {
        koulutus: {
            el: $('[name="koulutuskoodi"]'),
            val: 'Ylioppilastutkinto',
            editFunc: function (el, value) {
                el.element(by.cssContainingText('option', value)).click();
            },
            getValue: 'skip'
        },
        lukiolinja: {
            el: $('[name="koulutusohjelma"]'),
            val: 'Aikuisten lukiokoulutus',
            editFunc: function (el, value) {
                el.element(by.cssContainingText('option', value)).click();
            },
            getValue: 'skip'
        },
        alkamispvm: {
            el: $('.tarjontaDateTime input[placeholder="pp.kk.vvvv"]'),
            val: '15.11.' + nextYear
        },
        suunniteltuKestoArvo: {
            el: $('[name="suunniteltuKestoArvo"]'),
            val: '3'
        },
        suunniteltuKestoTyyppi: {
            el: $('[name="suunniteltuKestoTyyppi"]'),
            val: 'vuotta',
            editFunc: function (el, value) {
                el.element(by.cssContainingText('option', value)).click();
            },
            getValue: function (el) {
                return el.$('option:checked').getText();
            }
        },
        opetuskieli: {
            el: $('[name="opetuskielis"]'),
            val: '[x] suomi',
            editFunc: function (el, editVal) {
                el.$('[ng-model="combo.selection"]').clear()
                    .sendKeys('suomi').sendKeys(protractor.Key.ENTER);
            },
            getValue: function (el) {
                return el.$('.selection').getText().then(function (text) {
                    return text.replace(/\n/g, '').trim();
                });
            }
        },
        opetusaika: {
            el: $('div[selection="uiModel.opetusAikas.uris"]'),
            val: 'Päiväopetus',
            editFunc: testHelper.editFuncMultiSelect,
            getValueCallback: testHelper.getValueCallbackMultiSelect
        },
        opetusmuoto: {
            el: $('div[selection="uiModel.opetusmuodos.uris"]'),
            val: 'Ohjattu opiskelu',
            editFunc: testHelper.editFuncMultiSelect,
            getValueCallback: testHelper.getValueCallbackMultiSelect
        },
        opetuspaikka: {
            el: $('div[selection="uiModel.opetusPaikkas.uris"]'),
            val: 'Lähiopetus',
            editFunc: testHelper.editFuncMultiSelect,
            getValueCallback: testHelper.getValueCallbackMultiSelect
        },
        linkkiOpetussuunnitelmaan: {
            el: $('[name="linkkiOpetussuunnitelmaan"]'),
            val: 'http://www.linkki-joka-ei-toimi.fi'
        },
        yhteyshenkilö: {
            el: $('[ng-model="uiModel.contactPerson.nimet"]'),
            val: 'Matti Meikäläinen',
        },
        sahkoposti: {
            el: $('[data-ng-model="uiModel.contactPerson.sahkoposti"]'),
            val: 'test@test.com'
        },
        tehtavanimike: {
            el: $('[data-ng-model="uiModel.contactPerson.titteli"]'),
            val: 'Rehtori'
        },
        puhelinnumero: {
            el: $('[data-ng-model="uiModel.contactPerson.puhelin"]'),
            val: '0400000000'
        }
    },
    kuvailevatTiedot: {
        tinyMCE: {
            'Koulutuksen sisältö': {
                val: '<p>Lorem ipsum: koulutuksen sisältö</p>'
            },
            'Kohderyhmä': {
                val: '<p>Lorem ipsum: kohderyhmä</p>'
            },
            'Lukiokohtaiset oppiaineet ja kurssit': {
                val: '<p>Lukiokohtaiset oppiaineet ja kurssit</p>'
            },
            'Kansainvälistyminen': {
                val: '<p>Lorem ipsum: kansainvalistyminen</p>'
            },
            'Yhteistyö muiden toimijoiden kanssa': {
                val: '<p>Lorem ipsum: yhteistyo_muiden_kanssa</p>'
            }
        },
        otherFields: {
            a1A2Kieli: {
                el: $('[selection="uiModel.kielivalikoima.A1A2KIELI.uris"]'),
                val: 'ruotsi|englanti|saksa',
                editFunc: languageSelection.editFunc,
                getValue: languageSelection.getValue
            },
            b1Kieli: {
                el: $('[selection="uiModel.kielivalikoima.B1KIELI.uris"]'),
                val: 'ruotsi',
                editFunc: languageSelection.editFunc,
                getValue: languageSelection.getValue
            },
            b2Kieli: {
                el: $('[selection="uiModel.kielivalikoima.B2KIELI.uris"]'),
                val: 'ranska|saksa|venäjä',
                editFunc: languageSelection.editFunc,
                getValue: languageSelection.getValue
            },
            b3Kieli: {
                el: $('[selection="uiModel.kielivalikoima.B3KIELI.uris"]'),
                val: 'espanja|ranska|saksa|venäjä',
                editFunc: languageSelection.editFunc,
                getValue: languageSelection.getValue
            },
            valinnainenAidinkieli: {
                el: $('[selection="uiModel.kielivalikoima.VALINNAINEN_OMAN_AIDINKIELEN_OPETUS.uris"]'),
                val: 'suomi',
                editFunc: languageSelection.editFunc,
                getValue: languageSelection.getValue
            },
            muutKielet: {
                el: $('[selection="uiModel.kielivalikoima.MUUT_KIELET.uris"]'),
                val: 'viro, eesti',
                editFunc: languageSelection.editFunc,
                getValue: languageSelection.getValue
            },
            lukiodiplomit: {
                el: $('[selection="uiModel.lukiodiplomit.uris"]'),
                val: 'Käsityön lukiodiplomi|Kuvataiteen lukiodiplomi|Liikunnan lukiodiplomi' +
                    '|Musiikin lukiodiplomi|Teatterin lukiodiplomi',
                editFunc: testHelper.editFuncMultiSelect,
                getValueCallback: testHelper.getValueCallbackMultiSelect
            }
        }
    }
});

