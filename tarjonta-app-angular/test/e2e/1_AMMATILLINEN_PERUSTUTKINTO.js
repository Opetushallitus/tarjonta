var testHelper = require('../e2eTestHelper.js');

describe('AMMATILLINEN_PERUSTUTKINTO', function() {
    var searchInput = element(by.model('hakuehdot.searchStr'));
    var searchBtn = $('#orgSearch .buttons a:nth-child(2)');
    var firstSearchResult = $('#orgSearchResults li:first-child');
    var koulutuksetFirstResult = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(2)');
    var koulutusAmis2014 = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(3)');

    it('should find koulutus in organisaatiohaku', function() {
        browser.get('/tarjonta-app');

        searchInput.sendKeys('omnia');
        searchBtn.click();

        expect(firstSearchResult.getText()).toEqual('Espoon seudun koulutuskuntayhtymä Omnia');
    });

    it('should filter koulutukset', function() {
        firstSearchResult.$('span').click();
        var filterInput = element(by.model('spec.terms'));
        filterInput.sendKeys('datanomi');
        element(by.cssContainingText('#searchSpec td.year option', '2014')).click();

        filterInput.sendKeys(protractor.Key.ENTER);

        expect(koulutuksetFirstResult.getText()).toEqual('Omnian ammattiopisto, Kirkkonummi');
    });

    it('should expand koulutukset', function() {
        koulutuksetFirstResult.$('a.fold').click();

        expect(koulutusAmis2014.$('td:nth-child(2)').getText()).toEqual('Syksy 2014');
    });

    it('should show koulutuksen tiedot', function() {
        koulutusAmis2014.$('td:first-child a:last-child').click();
        var infoMsg = $('.msgInfo');

        expect(infoMsg.getText()).toMatch('datanomi joka on sidottu organisaatioon Omnian ammattiopisto');
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
                el: $('[ng-model="uiModel.contactPerson.nimet"]'),
                val: 'Anita Nordström',
                editVal: 'Anita Nordström EDIT'
            },
            sahkoposti: {
                el: $('[data-ng-model="uiModel.contactPerson.sahkoposti"]'),
                val: 'etunimi.sukunimi@omnia.fi',
                editVal: 'etunimi.EDIT.sukunimi@omnia.fi'
            },
            tehtavanimike: {
                el: $('[data-ng-model="uiModel.contactPerson.titteli"]'),
                val: 'opinto-ohjaaja',
                editVal: 'opinto-ohjaaja EDIT'
            },
            puhelinnumero: {
                el: $('[data-ng-model="uiModel.contactPerson.puhelin"]'),
                val: '0438240361',
                editVal: '0400000000'
            }
        },
        kuvailevatTiedot: {
            tinyMCE: {
                KOULUTUSOHJELMAN_VALINTA: {
                    val: '<p>Opiskelijat hakevat tieto- ja viestintätekniikan perustutkintoon.</p> ' +
                        '<p>Omniassa on tarjolla kaksi koulutusohjelmaa:</p> <ul> <li>Käytön tuen ' +
                        'koulutusohjelma, datanomi (Leppävaaran ja Kirkkonummen toimipisteet)</li> ' +
                        '<li>Ohjelmistotuotannon koulutusohjelma, datanomi (Leppävaaran toimipiste)</li> ' +
                        '</ul> <p>Koulutusohjelman valinta on joustava ja tapahtuu kolmantena opiskeluvuonna. ' +
                        'Koulutusohjelma määräytyy käytännössä sen mukaan, mitä tutkinnon osia opiskelija ' +
                        'tutkintoonsa valitsee.</p>'
                },
                SISALTO: {
                    val: '<p>Datanomin koulutuksen punainen lanka on asiakaspalveluasenne yhdistettynä vankkaan ' +
                        'tietotekniseen osaamiseen.</p> <p>Kaikille datanomeille pakollisia opintoja ovat ' +
                        'palvelutehtävissä toimiminen sekä järjestelmän hankinta ja käyttöönotto. Ensimmäisen vuoden' +
                        ' työssäoppimisjakso tehdään nimenomaan palvelutehtävissä.</p> <p>Toisen vuoden opintoihin' +
                        ' sisältyy Leppävaaran toimipisteessä palvelujen käyttöönotto ja tuki sekä ohjelmiston' +
                        ' määrittely ja suunnittelu. Ohjelmistotuotannosta kiinnostunut opiskelija voi valita myös' +
                        ' ohjelmiston toteuttamisen opinnot.</p> <p>Kirkkonummella opiskellaan ylläpitotehtävissä' +
                        ' toimimista ja palvelujen käyttöönottoa ja tukea. Kirkkonummella tarjotaan valinnaisina' +
                        ' opintoina kehitysympäristöjen käyttöä ja yrittäjyyttä.</p>'
                },
                SIJOITTUMINEN_TYOELAMAAN: {
                    val: '<p>It-alan ammattilaisille on tarjolla runsaasti työpaikkoja. Datanomit työllistyvät sekä' +
                        ' julkisen sektorin organisaatioihin että yrityksiin. Esimerkiksi Espoon kaupungilla ja' +
                        ' Kirkkonummen kunnalla on paljon tietohallinnon tehtäviä. Oma yritys on datanomille' +
                        ' mahdollinen vaihtoehto. </p><p>Datanomilta kysytään hyvää tietopohjaa ja' +
                        ' asiakaspalveluhenkisyyttä. Datanomi työskentelee poikkeuksetta osana ryhmää.' +
                        ' Ohjelmistotuotannon tehtävissä korostuvat kokonaisuudenhallinta ja projektityöskentelyyn' +
                        ' liittyvät taidot.</p>'
                },
                KANSAINVALISTYMINEN: {
                    val: '<p>Omnialla on hyvät kontaktit ulkomaisiin yhteistyöoppilaitoksiin, joiden kautta on löytynyt' +
                        ' työssäoppimispaikkoja halukkaille vaihtoon lähtijöille. Tieto- ja viestintätekniikan' +
                        ' opiskelijat ovat käyneet työssäoppimassa muun muassa Saksassa ja Hollannissa.</p> <p>Omnia on' +
                        ' aloittamassa yhteistyötä kiinalaisen Shanghaissa sijaitsevan elektroniikka-alan oppilaitoksen' +
                        ' kanssa. Yhteistyö mahdollistaa vaihtojen järjestämisen Kiinaan.</p> <p>Kansainväliset' +
                        ' kokemukset ovat arvokkaita ja antavat monipuolista oppia erilaisista työyhteisöistä ja' +
                        ' kulttuureista. It-alan tehtävissä on eduksi, että oppii luontevasti toimimaan' +
                        ' kansainvälisessä ympäristössä.</p> <p>Omnian kokemus kansainvälisten vaihtojen' +
                        ' järjestämisestä on poikkeuksellisen vahvaa. Omnialla on erinomaiset kansainväliset yhteydet' +
                        ' ja valmiudet järjestää opiskelijoille ja henkilökunnalle kansainvälisiä vaihtojaksoja. Omnia' +
                        ' on hyvin monikulttuurinen opiskeluympäristö, joten kansainväliset kokemukset karttuvat myös' +
                        ' kotimaassa.</p>'
                },
                YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA: {
                    val: '<p>Opettajat tekevät yhteistyötä työelämän edustajien kanssa ja pysyvät näin ajan tasalla' +
                        ' alan kehityksessä. Tiivis vuoropuhelu takaa tuoreen koulutuksen.</p>'
                }
            },
            otherFields: {
                tehtavanimikkeita: {
                    el: $('[selection="uiModel.ammattinimikkeet.uris"]'),
                    val: '',
                    editVal: '[x] IT Security Manager',
                    editFunc: function (el, editVal) {
                        // editVal lisätty jo -> poista se
                        if (editVal === '') {
                            el.$('.selection li:nth-child(1) a').click();
                        }
                        // Lisää editVal
                        else {
                            el.$('[ng-model="combo.selection"]').clear()
                                .sendKeys('IT Security Manager').sendKeys(protractor.Key.ENTER);
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
