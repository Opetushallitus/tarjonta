package fi.vm.sade.tarjonta.selenium.pageobject;

import static fi.vm.sade.support.selenium.SeleniumUtils.waitForText;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.support.selenium.SeleniumUtils;
import fi.vm.sade.support.selenium.VaadinPageObjectSupport;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.toteutus.KoulutusModuulinToteutusTable;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.toteutus.KoulutusmoduuliToteutusListView;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
public class KoulutusmoduuliToteutusListPageObject extends VaadinPageObjectSupport<KoulutusmoduuliToteutusListView>{
    
    public KoulutusmoduuliToteutusListPageObject(WebDriver driver, KoulutusmoduuliToteutusListView component) {
        super(driver,component);
    }
    
    public void openKomotoTab() {
        driver.findElement(By.xpath("//*[@class='v-caption' and contains(.,'Koulutusmoduulien toteutukset')]")).click();
    }
    
    
    
    public KoulutusModuulinToteutusTable getSuunnitteillaOlevat() {
        SeleniumUtils.waitForElement(component.getSuunnitteilla());
        return component.getSuunnitteilla();
    }
    
    public KoulutusModuulinToteutusTable getValmiit() {
        return component.getJulkaistava();
    }

    public void clickItemInTable(
            KoulutusModuulinToteutusTable table, int index) {
        WebElement tableE = SeleniumUtils.getWebElementForDebugId(table.getDebugId());
        tableE.findElements(By.xpath(".//tr[contains(@class, 'v-table-row')]")).get(index).click();//[contains(@class,'atag') and contains(@class ,'btag')]
        waitForText("Teemat");
    }

}
