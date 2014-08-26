describe('VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS', function() {
    var searchInput = element(by.model('hakuehdot.searchStr'));
    var searchBtn = $('#orgSearch .buttons a:nth-child(2)');
    var firstSearchResult = $('#orgSearchResults li:first-child');
    var koulutuksetFirstResult = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(2)');
    var koulutus2014 = $('.resultsTreeTable[selection="selection.koulutukset"] tr:nth-child(3)');

    it('should find VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS in organisaatiohaku', function() {
        browser.get('/tarjonta-app');

        searchInput.sendKeys('bovallius-ammatt');
        searchBtn.click();

        expect(firstSearchResult.getText()).toEqual('Bovallius-ammattiopisto');
    });

    it('should filter koulutukset', function() {
        firstSearchResult.$('span').click();
        var filterInput = element(by.model('spec.terms'));
        filterInput.sendKeys('työhön ja itse');
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

        expect(infoMsg.getText()).toMatch('valmentava koulutus, tekniikan ala joka on sidottu organisaatioon Bovallius');
    });
});
