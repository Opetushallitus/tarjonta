var testHelper = require('../e2eTestHelper.js');

describe('VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS', function() {
    var searchInput = element(by.model('hakuehdot.searchStr'));
    var searchBtn = $('#orgSearch .buttons a:nth-child(2)');
    var firstSearchResult = $('#orgSearchResults li:first-child');
    var koulutuksetFirstResult = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(2)');
    var koulutus2014 = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(3)');

    it('should find koulutus in organisaatiohaku', function() {
        browser.get('/tarjonta-app');

        searchInput.sendKeys('bovallius-ammatt');
        searchBtn.click();

        expect(firstSearchResult.getText()).toEqual('Bovallius-ammattiopisto');
    });

    it('should filter koulutukset', function() {
        firstSearchResult.$('span').click();
        var filterInput = element(by.model('spec.terms'));
        filterInput.sendKeys('työhön ja itse');
        filterInput.sendKeys(protractor.Key.ENTER);

        expect(koulutuksetFirstResult.getText()).toEqual('Bovallius-ammattiopisto, Laitila');
    });

    it('should expand koulutukset', function() {
        $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(2) a.fold').click();

        expect(koulutus2014.$('td:nth-child(2)').getText()).toEqual('Syksy 2014');
    });

    it('should show koulutuksen tiedot', function() {
        koulutus2014.$('td:first-child a:last-child').click();
        var infoMsg = $('.msgInfo');

        expect(infoMsg.getText()).toMatch('valmentava koulutus, tekniikan ala joka on sidottu organisaatioon Bovallius');
    });

    testHelper.testEditPage({
        perustiedot: {
            tutkintoOhjelma: {
                el: $('th[tt="koulutus.edit.koulutusohjelma"] + td input'),
                val: 'Työhön ja itsenäiseen elämään valmentava koulutus, tekniikan ala',
                editVal: 'Työhön ja itsenäiseen elämään valmentava koulutus, tekniikan ala EDIT'
            },
            opintojenLaajuus: {
                el: $('input[name="opintojenLaajuusarvoKannassa"]'),
                val: '40-120',
                editVal: '45-125'
            },
            opintojenLaajuusYksikko: {
                el: $('select[name="opintojenLaajuusyksikko"]'),
                val: 'opintoviikkoa',
                editVal: 'opintopistettä',
                editFunc: function (el, value) {
                    el.element(by.cssContainingText('option', value)).click();
                },
                getValue: function (el) {
                    return el.$('option:checked').getText();
                }
            },
            alkamispvm: {
                el: $('.tarjontaDateTime input[placeholder="pp.kk.vvvv"]'),
                val: '30.12.2014',
                editVal: '31.12.2014'
            },
            suunniteltuKestoArvo: {
                el: $('[name="suunniteltuKestoArvo"]'),
                val: '3',
                editVal: '4'
            },
            suunniteltuKestoTyyppi: {
                el: $('[name="suunniteltuKestoTyyppi"]'),
                val: 'vuotta',
                editVal: 'kuukautta',
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
                editVal: '[x] suomi[x] ruotsi',
                editFunc: function (el, editVal) {
                    // Ruotsi lisätty jo -> poista se
                    if (editVal === '[x] suomi') {
                        el.$('.selection li:nth-child(2) a').click();
                    }
                    // Lisää ruotsi
                    else {
                        el.$('[ng-model="combo.selection"]').clear()
                            .sendKeys('ruotsi').sendKeys(protractor.Key.ENTER);
                    }
                },
                getValue: function (el) {
                    return el.$('.selection').getText().then(function (text) {
                        return text.replace(/\n/g, '').trim();
                    });
                }
            },
            koulutuslaji: {
                el: $('select[name="koulutuslaji"]'),
                val: 'Nuorten koulutus',
                editVal: 'Aikuiskoulutus',
                editFunc: function (el, value) {
                    el.element(by.cssContainingText('option', value)).click();
                },
                getValue: function (el) {
                    return el.$('option:checked').getText();
                }
            },
            opetusaika: {
                el: $('div[selection="uiModel.opetusAikas.uris"]'),
                val: 'Päiväopetus',
                editVal: 'Iltaopetus|Päiväopetus',
                editFunc: testHelper.editFuncMultiSelect,
                getValueCallback: testHelper.getValueCallbackMultiSelect
            },
            opetusmuoto: {
                el: $('div[selection="uiModel.opetusmuodos.uris"]'),
                val: 'Ohjattu opiskelu',
                editVal: 'Monimuoto-opetus|Ohjattu opiskelu',
                editFunc: testHelper.editFuncMultiSelect,
                getValueCallback: testHelper.getValueCallbackMultiSelect
            },
            opetuspaikka: {
                el: $('div[selection="uiModel.opetusPaikkas.uris"]'),
                val: 'Lähiopetus',
                editVal: 'Etäopetus|Lähiopetus',
                editFunc: testHelper.editFuncMultiSelect,
                getValueCallback: testHelper.getValueCallbackMultiSelect
            },
            linkkiOpetussuunnitelmaan: {
                el: $('[name="linkkiOpetussuunnitelmaan"]'),
                val: '',
                editVal: 'http://www.linkki-joka-ei-toimi.fi'
            },
            yhteyshenkilö: {
                el: $('[ng-model="uiModel.contactPerson.nimi"]'),
                val: '',
                editVal: 'EDIT'
            },
            sahkoposti: {
                el: $('[data-ng-model="uiModel.contactPerson.sahkoposti"]'),
                val: '',
                editVal: 'edit@edit.com'
            },
            tehtavanimike: {
                el: $('[data-ng-model="uiModel.contactPerson.titteli"]'),
                val: '',
                editVal: 'EDIT'
            },
            puhelinnumero: {
                el: $('[data-ng-model="uiModel.contactPerson.puhelin"]'),
                val: '',
                editVal: '0400000000'
            }
        },
        kuvailevatTiedot: {
            tinyMCE: {
                KOULUTUSOHJELMAN_VALINTA: {
                    val: ''
                },
                SISALTO: {
                    val: '<p>Työhön ja itsenäiseen elämään valmentavassa koulutuksessa opiskellaan työhön ja ' +
                        'itsenäiseen elämään liittyviä taitoja. Koulutus sopii opiske­lijalle, joka voi ' +
                        'osallistua erilaisiin tuettuihin työmuotoihin ja -tehtäviin. Opintoihin sisältyy ' +
                        'työelämään valmen­tautumista ja asumiskokeiluja. Tavoitteena on tarjota opiskelijalle ' +
                        'mahdollisimman hyvät valmiudet selviytyä yhteiskunnan, kodin ja työelämän tarjoamista ' +
                        'haasteista.</p><p>Koulutuksessa opiskellaan tekniikan alan työtehtäviin ja itsenäiseen ' +
                        'elämään liittyviä taitoja.<br />Opintoihin kuuluu muun muassa puu- ja metallitöitä, ' +
                        'koneenasennusta ja kiinteistönhoidon tehtäviä. Lisäksi kehitetään oman elämän hallintaan ' +
                        'liittyviä taitoja. Työhön valmentautuminen on tärkeä osa opetusta. Koulutuksen jälkeen ' +
                        'opiskelija voi siirtyä<br />työelämään tai jatko-opintoihin.</p>'
                },
                KOHDERYHMA: {
                    val: ''
                },
                SIJOITTUMINEN_TYOELAMAAN: {
                    val: ''
                },
                KANSAINVALISTYMINEN: {
                    val: ''
                },
                YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA: {
                    val: ''
                }
            },
            otherFields: {
                tehtavanimikkeita: {
                    el: $('[selection="uiModel.ammattinimikkeet.uris"]'),
                    val: '',
                    editVal: '[x] Kodinhoitaja, lähihoitaja',
                    editFunc: function (el, editVal) {
                        // editVal lisätty jo -> poista se
                        if (editVal === '') {
                            el.$('.selection li:nth-child(1) a').click();
                        }
                        // Lisää editVal
                        else {
                            el.$('[ng-model="combo.selection"]').clear()
                                .sendKeys('Kodinhoitaja, lähihoitaja').sendKeys(protractor.Key.ENTER);
                        }
                    },
                    getValue: function (el) {
                        return el.$('.selection').getText().then(function (text) {
                            return text.replace(/\n/g, '').trim();
                        });
                    }
                }
            }
        }
    });

});
