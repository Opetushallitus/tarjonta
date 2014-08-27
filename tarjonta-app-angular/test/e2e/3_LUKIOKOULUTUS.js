describe('LUKIOKOULUTUS', function() {
    var searchInput = element(by.model('hakuehdot.searchStr'));
    var searchBtn = $('#orgSearch .buttons a:nth-child(2)');
    var firstSearchResult = $('#orgSearchResults li:first-child');
    var koulutuksetFirstResult = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(2)');
    var koulutusLukio2014 = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(4)');

    it('should find LUKIOKOULUTUS in organisaatiohaku', function() {
        browser.get('/tarjonta-app');

        searchInput.sendKeys('kauniaisten l');
        searchBtn.click();

        expect(firstSearchResult.getText()).toEqual('Kauniaisten lukio');
    });

    it('should show koulutukset', function() {
        firstSearchResult.$('span').click();

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
