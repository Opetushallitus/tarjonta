package fi.vm.sade.tarjonta.selenium.story;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.selenium.TarjontaEmbedComponentTstSupport;
import org.junit.Test;

import static fi.vm.sade.support.selenium.SeleniumUtils.waitForText;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import fi.vm.sade.tarjonta.selenium.pageobject.KoulutusmoduuliEditViewPageObject;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.KoulutusmoduuliEditView;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jukka Raanamo
 */
public class OVT_803_KenttienValidointiTest extends TarjontaEmbedComponentTstSupport<KoulutusmoduuliEditView> {

    // TODO: add meaningfull data into mock
    private static final String KOULUTUS_KOODI_LABEL = "Tietokenkasittely ko";
    private static final String KOULUTUS_KOODI_VALUE = KOULUTUS_KOODI_LABEL;
    private static final Logger log = LoggerFactory.getLogger(OVT_803_KenttienValidointiTest.class);

    private KoulutusmoduuliEditViewPageObject koulutusmoduuliEditView;

    @Override
    public void initPageObjects() {
        koulutusmoduuliEditView = new KoulutusmoduuliEditViewPageObject(component);
    }

    @Test
    public void testKoulutusIsRequiredField() {

        koulutusmoduuliEditView.selectNewKoulutusmoduuliTutkintoonJohtava();
        koulutusmoduuliEditView.clickSaveAsDraft();
        waitForText(I18N.getMessage("KoulutusmoduuliEditView.save.notValid"));

    }

    @Test
    public void testKoulutusAndOrganisaatioAreStoredToModel() {
        
        koulutusmoduuliEditView.selectNewKoulutusmoduuliTutkintoonJohtava();
        koulutusmoduuliEditView.setKoulutus(KOULUTUS_KOODI_LABEL);
        koulutusmoduuliEditView.clickSaveAsDraft();

        assertEquals(KOULUTUS_KOODI_VALUE, getDTO().getPerustiedot().getKoulutusKoodiUri());
        assertNotNull(getDTO().getPerustiedot().getOrganisaatioOid());

    }
    
    
    private KoulutusmoduuliDTO getDTO() {
        return koulutusmoduuliEditView.getComponent().getKoulutusmoduuliDTO();
    }

}

