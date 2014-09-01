describe('AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA', function() {
    var searchInput = element(by.model('hakuehdot.searchStr'));
    var searchBtn = $('#orgSearch .buttons a:nth-child(2)');
    var firstSearchResult = $('#orgSearchResults li:first-child');
    var koulutuksetFirstResult = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(2)');
    var koulutus2014 = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(3)');

    it('should find AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA in organisaatiohaku', function() {
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

    it('should show edit page for koulutus', function() {
        var firstEditBtn = element.all(by.css('[tt="koulutus.review.muokkaa"]')).first();
        firstEditBtn.click();

        // TODO: muokkaa koulutuksen arvoja ja testaa, että muutokset tallentuvat

        var firstSaveBtn = element.all(by.css('[data-action="koulutus.edit.tallenna.valmis"]')).first();
        firstSaveBtn.click();

        var saveMsg = element.all(by.css('.msgOk p')).first();

        expect(saveMsg.isDisplayed()).toBe(true);
        expect(saveMsg.getText()).toEqual('Tallennettu');
    });
});
