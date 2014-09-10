var testHelper = require('../e2eTestHelper.js');

describe('MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS', function() {
    var searchInput = element(by.model('hakuehdot.searchStr'));
    var searchBtn = $('#orgSearch .buttons a:nth-child(2)');
    var firstSearchResult = $('#orgSearchResults li:first-child');
    var koulutuksetFirstResult = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(2)');
    var koulutus2014 = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(3)');

    it('should find koulutus in organisaatiohaku', function() {
        browser.get('/tarjonta-app');

        searchInput.sendKeys('Eiran aikuis');
        searchBtn.click();

        expect(firstSearchResult.getText()).toMatch('Eiran aikuisl');
    });

    it('should filter koulutukset', function() {
        firstSearchResult.$('span').click();
        var filterInput = element(by.model('spec.terms'));
        filterInput.sendKeys('Maahanmuuttaj');
        filterInput.sendKeys(protractor.Key.ENTER);

        expect(koulutuksetFirstResult.getText()).toMatch('Eiran aikuislukio');
    });

    it('should expand koulutukset', function() {
        $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(2) a.fold').click();

        expect(koulutus2014.$('td:nth-child(2)').getText()).toEqual('Syksy 2014');
    });

    it('should show koulutuksen tiedot', function() {
        koulutus2014.$('td:first-child a:last-child').click();
        var infoMsg = $('.msgInfo');

        expect(infoMsg.getText()).toMatch('Maahanmuuttajien ja vieraskielisten lukiokoulutukseen valmistava koulutus joka');
    });

    testHelper.testEditPage({
        perustiedot: {
            opintojenLaajuus: {
                el: $('[name="opintojenLaajuusarvoKannassa"]'),
                val: '25',
                editVal: '26'
            },
            opintojenLaajuusYksikko: {
                el: $('select[name="opintojenLaajuusyksikko"]'),
                val: 'kurssia',
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
                val: '1',
                editVal: '2'
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
                el: $('[ng-model="uiModel.contactPerson.nimet"]'),
                val: 'Eevi Mustonen',
                editVal: 'EDIT'
            },
            sahkoposti: {
                el: $('[data-ng-model="uiModel.contactPerson.sahkoposti"]'),
                val: 'eevi.mustonen@eira.fi',
                editVal: 'edit@edit.com'
            },
            tehtavanimike: {
                el: $('[data-ng-model="uiModel.contactPerson.titteli"]'),
                val: 'opinto-ohjaaja',
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
                    val: '<p>Koulutukseen hakuaika on 16.6. – 25.7. 2014. Siihen haetaan tämän opintopolku.fi-palvelun' +
                        ' kautta. Opiskelijat valitaan valintakokeessa elokuun toisella viikolla. Valintakokeessa ' +
                        'on kaksi osaa: haastattelu ja kirjoitelman kirjoittaminen. Valintaperusteita ovat muun ' +
                        'muassa suomen kielen taito ja motivaatio lukio-opiskeluun. Opiskelijan tulee pystyä ' +
                        'seuraamaan opetusta suomeksi ja osallistumaan<br />opetukseen suomeksi.</p>'
                },
                SISALTO: {
                    val: '<p>LUVAssa saat hyvät taidot lukio-opintoja ja muita jatkoopintoja varten. Vuoden aikana' +
                        ' vahvistetaan lukiossa tärkeitä opiskelutaitoja ja syvennetään osaamista erityisesti lukion' +
                        ' keskeisimmissä aineissa kuten matematiikassa, englannissa ja suomen kielessä (S2). LUVAn' +
                        ' opettajat ovat lukion<br />aineenopettajia.</p><p>LUKIOON VALMISTAVASSA KOULUTUKSESSA:</p>' +
                        '<ul><li>voit korottaa peruskoulun arvosanojasi</li><li>voit kokeilla tai suorittaa tavallisia' +
                        ' lukiokursseja</li><li>opiskelet lukiossa lukion tapaan</li><li>koulutuksen jälkeen opintoja' +
                        ' on helppo jatkaa Eiran aikuislukiossa, jo tutuksi tulleessa paikassa</li></ul>'
                },
                KOHDERYHMA: {
                    val: '<p>Koulutus on tarkoitettu lukio-opintoihin aikoville maahanmuuttajataustaisille henkilöille.' +
                        ' Koulutukseen voivat hakea 18 vuotta täyttäneet maahanmuuttajat joiden äidinkieli ei ole suomi' +
                        ' ja jotka tarvitsevat lisää kielellisiä<br />valmiuksia lukio-opiskeluun.</p>'
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
