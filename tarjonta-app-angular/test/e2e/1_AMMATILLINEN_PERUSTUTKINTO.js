describe('AMMATILLINEN_PERUSTUTKINTO', function() {
    var searchInput = element(by.model('hakuehdot.searchStr'));
    var searchBtn = $('#orgSearch .buttons a:nth-child(2)');
    var firstSearchResult = $('#orgSearchResults li:first-child');
    var koulutuksetFirstResult = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(2)');
    var koulutusAmis2014 = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(3)');

    it('should find AMMATILLINEN_PERUSTUTKINTO in organisaatiohaku', function() {
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
