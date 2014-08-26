describe('AMMATILLINEN_PERUSTUTKINTO', function() {
    var searchInput = element(by.model('hakuehdot.searchStr'));
    var searchBtn = $('#orgSearch .buttons a:nth-child(2)');
    var firstSearchResult = $('#orgSearchResults li:first-child');
    var koulutuksetFirstResult = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(2)');
    var koulutusAmis2014 = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(4)');

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
        filterInput.sendKeys(protractor.Key.ENTER);

        expect(koulutuksetFirstResult.getText()).toEqual('Omnian ammattiopisto, Kirkkonummi');
    });

    it('should expand koulutukset', function() {
        $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(2) a.fold').click();

        expect(koulutusAmis2014.$('td:nth-child(2)').getText()).toEqual('Syksy 2014');
    });

    it('should show koulutuksen tiedot', function() {
        koulutusAmis2014.$('td:first-child a:last-child').click();
        var infoMsg = $('.msgInfo');

        expect(infoMsg.getText()).toMatch('datanomi joka on sidottu organisaatioon Omnian ammattiopisto');
    });
});
