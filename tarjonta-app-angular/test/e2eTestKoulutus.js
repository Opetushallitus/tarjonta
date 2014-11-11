var testHelper = require('./e2eTestHelper.js');

module.exports = function testKoulutus(type, organizationName, inputs) {

    var koulutusListLink;
    var koulutusOid;

    describe(type, function() {

        it('should find organization in organisaatiohaku', function() {
            testHelper.testOrganizationSearch(organizationName);
        });

        it('should create koulutus', function(done) {
            // Open create koulutus dialog
            var createKoulutusBtn = $('[tt="tarjonta.toiminnot.luo_uusi_koulutus"]');
            createKoulutusBtn.click();
            element(by.cssContainingText('[ng-model="model.koulutustyyppi"] option', type)).click();
            $('[tt="luoKoulutusDialogi.jatka"]').click();

            testHelper.testCreateKoulutus(type, inputs, function setOid(oid) {
                koulutusOid = oid;
            }, done);
        });

        it('should find koulutus', function() {
            testHelper.testOrganizationSearch(organizationName);
            testHelper.findKoulutusInList(koulutusOid).then(function(setKoulutusLink) {
                koulutusListLink = setKoulutusLink();
            });
        });

        // Edit koulutus without actually modifying any values. This is used to ensure
        // that using the edit form doesn't reset values etc..
        it('should edit koulutus', function(done) {
            koulutusListLink.click();
            element(by.cssContainingText('.active .dropdown-menu a:not(.ng-hide)', 'Muokkaa')).click();
            testHelper.testEditKoulutus(inputs, done);
        });

        it('should delete koulutus in list view', function() {
            testHelper.testOrganizationSearch(organizationName);
            testHelper.findKoulutusInList(koulutusOid).then(function(setKoulutusLink) {
                koulutusListLink = setKoulutusLink();
            });

            expect(koulutusListLink).not.toBe(undefined);

            koulutusListLink.click();
            element(by.cssContainingText('.active .dropdown-menu a:not(.ng-hide)', 'Poista')).click();
            $('#poista-koulutus .btn-primary').click();

            // Ensure that koulutus doesn't show up in list anymore
            $('#searchSpec [ng-click="search()"]').click();

            // findKoulutusInList should not anymore be able to find
            testHelper.findKoulutusInList(koulutusOid).then(function() {
                // koulutus should no longer be found => trigger error
                expect(true).toBe(false);
            });
        });
    });
};