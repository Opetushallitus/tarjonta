var testHelper = require('../e2eTestHelper.js');

describe('LUKIOKOULUTUS', function() {
    var searchInput = element(by.model('hakuehdot.searchStr'));
    var searchBtn = $('#orgSearch .buttons a:nth-child(2)');
    var firstSearchResult = $('#orgSearchResults li:first-child');
    var koulutuksetFirstResult = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(2)');
    var koulutusLukio2014 = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(3)');

    it('should find koulutus in organisaatiohaku', function() {
        browser.get('/tarjonta-app');

        searchInput.sendKeys('kauniaisten l');
        searchBtn.click();

        expect(firstSearchResult.getText()).toEqual('Kauniaisten lukio');
    });

    it('should show koulutukset', function() {
        firstSearchResult.$('span').click();
        var filterInput = element(by.model('spec.terms'));
        element(by.cssContainingText('#searchSpec td.year option', '2014')).click();
        filterInput.sendKeys(protractor.Key.ENTER);

        expect(koulutuksetFirstResult.getText()).toEqual('Kauniaisten lukio');
    });

    it('should expand koulutukset', function() {
        $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(2) a.fold').click();

        expect(koulutusLukio2014.$('td:nth-child(2)').getText()).toEqual('Syksy 2014');
    });

    it('should show koulutuksen tiedot', function() {
        koulutusLukio2014.$('td:first-child a:last-child').click();
        var infoMsg = $('.msgInfo');

        expect(infoMsg.getText()).toMatch('Lukio joka on sidottu organisaatioon Kauniaisten lukio');
    });

    var languageSelection = {
        editFunc: function (el, editVal) {
            var languages = editVal.split('|');

            el.$$('.combobox span a').then(function (elements) {
                for (var i = 0; i < elements.length; i ++) {
                    elements[0].click();
                }
            });

            languages.forEach(function(lang) {
                if ( lang !== '' ) {
                    el.all(by.cssContainingText('option', lang)).then(function(options) {
                        options.forEach(function(option) {
                            option.getText().then(function(text) {
                               if ( text === lang ) {
                                   option.click();
                               }
                            });
                        });
                    });
                }
            });
        },
        getValue: function (el) {
            return el.$('.combobox').getText().then(function (text) {
                var text = text.replace(/\n/g, '');
                var langListTextEnd = 'kafferi, hosazulu';
                text = text.substring(text.indexOf(langListTextEnd) + langListTextEnd.length)
                    .replace(/ \[x\] /g, '|').trim().substring(1);
                return text;
            });
        }
    };

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
                val: 'http://kauniaistenlukio.fi/index.php?option=com_content&task=view&id=60&Itemid=94',
                editVal: 'http://www.linkki-joka-ei-toimi.fi'
            },
            yhteyshenkilö: {
                el: $('[ng-model="uiModel.contactPerson.nimet"]'),
                val: 'Ilpo Ahlholm',
                editVal: 'Ilpo Ahlholm EDIT'
            },
            sahkoposti: {
                el: $('[data-ng-model="uiModel.contactPerson.sahkoposti"]'),
                val: 'ilpo.ahlholm@kauniainen.fi',
                editVal: 'EDIT.ilpo.ahlholm@kauniainen.fi'
            },
            tehtavanimike: {
                el: $('[data-ng-model="uiModel.contactPerson.titteli"]'),
                val: 'Rehtori',
                editVal: 'Rehtori EDIT'
            },
            puhelinnumero: {
                el: $('[data-ng-model="uiModel.contactPerson.puhelin"]'),
                val: '095056411',
                editVal: '0400000000'
            }
        },
        kuvailevatTiedot: {
            tinyMCE: {
                SISALTO: {
                    val: '<p>Kauniaisten lukio - tietoa, taitoa ja tunnetta. Monipuolinen yleislukio.' +
                        ' Laaja kieliohjelma. Runsaasti koulukohtaisia soveltavia kursseja. Nykyaikaiset' +
                        ' opiskelumenetelmät/yksilölliset opiskelumahdollisuudet. Harrastusmahdollisuuksia.' +
                        ' Opiskelutaitojen kehittäminen, tutkiva oppiminen, verkko-oppiminen. Avoin, turvallinen' +
                        ' ja terveellinen opiskeluilmapiiri.</p>'
                },
                KANSAINVALISTYMINEN: {
                    val: '<p>Ulkomainen yhteistoiminta: Hollanti, Italia, Saksa, Venäjä, Viro, Ruotsi,' +
                        ' Model European Parliament (MEP).</p>'
                },
                YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA: {
                    val: ''
                }
            },
            otherFields: {
                a1A2Kieli: {
                    el: $('[selection="uiModel.kielivalikoima.A1A2KIELI.uris"]'),
                    val: 'ruotsi|englanti|saksa',
                    editVal: 'ruotsi|englanti|ranska|saksa',
                    editFunc: languageSelection.editFunc,
                    getValue: languageSelection.getValue
                },
                b1Kieli: {
                    el: $('[selection="uiModel.kielivalikoima.B1KIELI.uris"]'),
                    val: 'ruotsi',
                    editVal: 'ruotsi|ranska',
                    editFunc: languageSelection.editFunc,
                    getValue: languageSelection.getValue
                },
                b2Kieli: {
                    el: $('[selection="uiModel.kielivalikoima.B2KIELI.uris"]'),
                    val: 'venäjä|ranska|saksa',
                    editVal: 'venäjä|ranska',
                    editFunc: languageSelection.editFunc,
                    getValue: languageSelection.getValue
                },
                b3Kieli: {
                    el: $('[selection="uiModel.kielivalikoima.B3KIELI.uris"]'),
                    val: 'venäjä|ranska|saksa|espanja',
                    editVal: 'venäjä|ranska',
                    editFunc: languageSelection.editFunc,
                    getValue: languageSelection.getValue
                },
                valinnainenAidinkieli: {
                    el: $('[selection="uiModel.kielivalikoima.VALINNAINEN_OMAN_AIDINKIELEN_OPETUS.uris"]'),
                    val: '',
                    editVal: 'suomi',
                    editFunc: languageSelection.editFunc,
                    getValue: languageSelection.getValue
                },
                muutKielet: {
                    el: $('[selection="uiModel.kielivalikoima.MUUT_KIELET.uris"]'),
                    val: 'viro, eesti',
                    editVal: '',
                    editFunc: languageSelection.editFunc,
                    getValue: languageSelection.getValue
                },
                lukiodiplomit: {
                    el: $('[selection="uiModel.lukiodiplomit.uris"]'),
                    val: 'Käsityön lukiodiplomi|Kuvataiteen lukiodiplomi|Liikunnan lukiodiplomi' +
                        '|Musiikin lukiodiplomi|Teatterin lukiodiplomi',
                    editVal: 'Tanssin lukiodiplomi',
                    editFunc: testHelper.editFuncMultiSelect,
                    getValueCallback: testHelper.getValueCallbackMultiSelect
                }
            }
        }
    });
});
