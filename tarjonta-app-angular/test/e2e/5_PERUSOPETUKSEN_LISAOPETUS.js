var testHelper = require('../e2eTestHelper.js');

describe('PERUSOPETUKSEN_LISAOPETUS', function() {
    var searchInput = element(by.model('hakuehdot.searchStr'));
    var searchBtn = $('#orgSearch .buttons a:nth-child(2)');
    var firstSearchResult = $('#orgSearchResults li:first-child');
    var koulutuksetFirstResult = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(2)');
    var koulutus2014 = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(3)');

    it('should find koulutus in organisaatiohaku', function() {
        browser.get('/tarjonta-app');

        searchInput.sendKeys('valkealan');
        searchBtn.click();

        expect(firstSearchResult.getText()).toMatch('Valkealan kristillisen kansanopiston');
    });

    it('should filter koulutukset', function() {
        firstSearchResult.$('span').click();
        var filterInput = element(by.model('spec.terms'));
        filterInput.sendKeys('lisäopetus');
        filterInput.sendKeys(protractor.Key.ENTER);

        expect(koulutuksetFirstResult.getText()).toEqual('Valkealan kristillinen kansanopisto');
    });

    it('should expand koulutukset', function() {
        $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(2) a.fold').click();

        expect(koulutus2014.$('td:nth-child(2)').getText()).toEqual('Syksy 2014');
    });

    it('should show koulutuksen tiedot', function() {
        koulutus2014.$('td:first-child a:last-child').click();
        var infoMsg = $('.msgInfo');

        expect(infoMsg.getText()).toMatch('Perusopetuksen lisäopetus joka on sidottu organisaatioon Valkealan');
    });

    testHelper.testEditPage({
        perustiedot: {
            opintojenLaajuus: {
                el: $('[name="opintojenLaajuusarvoKannassa"]'),
                val: '1100',
                editVal: '1111'
            },
            opintojenLaajuusyksikko: {
                el: $('[name="opintojenLaajuusyksikko"]'),
                val: 'tuntia',
                editVal: 'opintoviikkoa',
                editFunc: function (el, value) {
                    el.$$('option').then(function(options) {
                        options.forEach(function(option) {
                            option.getText().then(function(text) {
                                if ( text === value ) {
                                    option.click();
                                }
                            });
                        });
                    });
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
                val: '10',
                editVal: '9'
            },
            suunniteltuKestoTyyppi: {
                el: $('[name="suunniteltuKestoTyyppi"]'),
                val: 'kuukautta',
                editVal: 'vuotta',
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
                val: 'Satu Viholainen',
                editVal: 'EDIT'
            },
            sahkoposti: {
                el: $('[data-ng-model="uiModel.contactPerson.sahkoposti"]'),
                val: 'toimisto@valkealanopisto.fi',
                editVal: 'edit@edit.com'
            },
            tehtavanimike: {
                el: $('[data-ng-model="uiModel.contactPerson.titteli"]'),
                val: 'oppilaitossihteeri',
                editVal: 'EDIT'
            },
            puhelinnumero: {
                el: $('[data-ng-model="uiModel.contactPerson.puhelin"]'),
                val: '05 3414600',
                editVal: '0400000000'
            }
        },
        kuvailevatTiedot: {
            tinyMCE: {
                KOULUTUSOHJELMAN_VALINTA: {
                    val: ''
                },
                SISALTO: {
                    val: '<p>Valkealan opiston kymppiluokka on tarkoitettu nuorille, jotka haluavat parantaa ' +
                        'peruskoulun päättötodistustaan jatko-opintoja varten.</p><p>Lukuvuosi koostuu ' +
                        'peruskouluaineiden kertaamisesta, urasuunnittelusta, työelämään tutustumisesta sekä ' +
                        'harraste- ja elämäntaitoaineista. Nuori, joka hakeutuu kennelkympille voi lisäksi hankkia ' +
                        'osaamista koira- ja kennelopinnoissa.</p><p>Kaikille opiskelijoille tehdään ' +
                        'henkilökohtainen opiskelusuunnitelma sekä tuetaan jatko-opintojen suunnittelussa.</p>' +
                        '<p>Kymppiluokan ja kennelkympin opiskelijoilla sekä koirilla on mahdollisuus asua opiston ' +
                        'asuntolassa.</p>'
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
