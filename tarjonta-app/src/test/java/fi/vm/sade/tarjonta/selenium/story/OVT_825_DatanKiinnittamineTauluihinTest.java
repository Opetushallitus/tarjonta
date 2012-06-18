/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fi.vm.sade.tarjonta.selenium.story;

import fi.vm.sade.tarjonta.selenium.TarjontaEmbedComponentTstSupport;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.toteutus.KoulutusmoduuliToteutusListView;
import fi.vm.sade.tarjonta.selenium.pageobject.KoulutusmoduuliToteutusListPageObject;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.toteutus.KoulutusModuulinToteutusTable;
import static fi.vm.sade.support.selenium.SeleniumUtils.STEP;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
/*
 *
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

/**
 *
 * @author Tuomas Katva
 */
public class OVT_825_DatanKiinnittamineTauluihinTest extends TarjontaEmbedComponentTstSupport<KoulutusmoduuliToteutusListView> {

    
    private KoulutusmoduuliToteutusListPageObject pageObject;
    
    @Override
    public void initPageObjects() {
        pageObject = new KoulutusmoduuliToteutusListPageObject(driver,component);
        
    }
    
    @Test
    public void testSuunnitteillaOlevaTable() {
        
        
        STEP("Verify that suunnitteilla olevat koulutusmoduulin totetukset table exists");
        
        KoulutusModuulinToteutusTable suunnitteilla = pageObject.getSuunnitteillaOlevat();
        
        assertNotNull(suunnitteilla);
        
    }
    
    @Test
    public void testValmiitJulkaistavaksi() {
        STEP("Verify that julkaistavaksi valmiit koulutusmoduulin totetukset table exists");
        
        KoulutusModuulinToteutusTable valmiitJulkaistavaksi = pageObject.getValmiit();
        
        assertNotNull(valmiitJulkaistavaksi);
        
    }
    
    @Test
    public void testTableHasData() {
        STEP("Verify that julkaistavaksi valmiit koulutusmoduulin totetukset table exists");
        
        KoulutusModuulinToteutusTable suunnitteilla = pageObject.getSuunnitteillaOlevat();
        
        int itemCount = suunnitteilla.getKomotoTable().getItemIds().size();
        
        assertTrue(itemCount > 0);
    }
}
