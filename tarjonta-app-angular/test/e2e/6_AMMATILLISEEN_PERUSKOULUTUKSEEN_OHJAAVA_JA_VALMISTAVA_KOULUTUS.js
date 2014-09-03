describe('AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS', function() {
    var searchInput = element(by.model('hakuehdot.searchStr'));
    var searchBtn = $('#orgSearch .buttons a:nth-child(2)');
    var firstSearchResult = $('#orgSearchResults li:first-child');
    var koulutuksetFirstResult = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(2)');
    var koulutus2014 = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(3)');

    it('should find AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS in organisaatiohaku', function() {
        browser.get('/tarjonta-app');

        searchInput.sendKeys('Turun ammatti');
        searchBtn.click();

        expect(firstSearchResult.getText()).toMatch('Turun ammatti-instituutti');
    });

    it('should filter koulutukset', function() {
        firstSearchResult.$('span').click();
        var filterInput = element(by.model('spec.terms'));
        filterInput.sendKeys('ohjaava ja valmistava');
        filterInput.sendKeys(protractor.Key.ENTER);

        expect(koulutuksetFirstResult.getText()).toMatch('Aninkaisten toimipiste');
    });

    it('should expand koulutukset', function() {
        $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(2) a.fold').click();

        expect(koulutus2014.$('td:nth-child(2)').getText()).toEqual('Syksy 2014');
    });

    it('should show koulutuksen tiedot', function() {
        koulutus2014.$('td:first-child a:last-child').click();
        var infoMsg = $('.msgInfo');

        expect(infoMsg.getText()).toMatch('ohjaava ja valmistava koulutus joka on sidottu organisaatioon Turun');
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
