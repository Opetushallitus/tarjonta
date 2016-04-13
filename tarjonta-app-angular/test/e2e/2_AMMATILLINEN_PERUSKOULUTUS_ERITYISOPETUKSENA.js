var testHelper = require('../e2eTestHelper.js');

describe('AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA', function() {
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
        filterInput.sendKeys('kotityö');
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

        expect(infoMsg.getText()).toMatch('Kotityöpalvelujen koulutusohjelma, kodinhuoltaja joka on sidottu organisaatioon Bovallius');
    });

    testHelper.testEditPage({
        perustiedot: {
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
                    val: '<div>Kodinhuoltaja tekee siivoukseen, tekstiilien huol­toon, ateriointiin ja ' +
                        'asiointiin liittyviä tehtäviä pal­velukodeissa ja kodinomaisissa laitoksissa. ' +
                        'Työhön kuuluu asiakkaan avustaminen päivittäisissä toi­minnoissa. </div><div><br />' +
                        'Opiskeluun sisältyy työssäoppimisjaksoja esimer­kiksi vanhusten palvelukodeissa, ' +
                        'päiväkodeissa, kouluissa ja henkilöstöravintoloissa. Kodinhuolta­jan työssä tarvitaan' +
                        ' asiakaspalvelutaitoja ja kykyä työskennellä erilaisten ihmisten kanssa.  </div>'
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
