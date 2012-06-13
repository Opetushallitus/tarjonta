package fi.vm.sade.tarjonta.selenium.story;

import fi.vm.sade.tarjonta.selenium.TarjontaEmbedComponentTstSupport;
import fi.vm.sade.tarjonta.selenium.pageobject.HakueraEditFormPageObject;
import fi.vm.sade.tarjonta.selenium.pageobject.HakueraListPageObject;
import fi.vm.sade.tarjonta.service.mock.HakueraServiceMock;
import fi.vm.sade.tarjonta.service.types.dto.HakueraDTO;
import fi.vm.sade.tarjonta.ui.hakuera.HakuView;
import fi.vm.sade.tarjonta.ui.hakuera.HakueraEditForm;
import fi.vm.sade.tarjonta.ui.hakuera.HakueraList;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static fi.vm.sade.support.selenium.SeleniumUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Antti Salonen
 */
public class OVT_798_HakueraGuiValidoinnit extends TarjontaEmbedComponentTstSupport<HakuView> {
    
    private HakueraEditFormPageObject hakueraEdit;
    private HakueraListPageObject hakueraList;

    @Autowired
    HakueraServiceMock hakueraService;


    @Override
    public void initPageObjects() {
        hakueraList = new HakueraListPageObject(driver, getComponentByType(HakueraList.class));
        hakueraEdit = new HakueraEditFormPageObject(driver, getComponentByType(HakueraEditForm.class));
    }

    @Test
    public void testHakueraFormValidoinnit() throws Throwable {

        assertNotNull("hakulomakeUrl-field not bound", component.getHakuForm().getForm().getField("lomake"));

        STEP("varmistetaan että lomake ilmoittaa tyhjistä kentistä");
        hakueraEdit.save();
        assertError("Kaikki pakolliset kent\u00e4t on sy\u00f6tett\u00e4v\u00e4", true);

        STEP("syötetään muut kentät, jonka jälkeen testataan validointia nimellä ja urlilla");
        hakueraEdit.selectDefaultKoodistoValues();

        STEP("kaikki nimikentät tulee syöttää");
        input(hakueraEdit.waitForNimiFi(), "");
        input(hakueraEdit.waitForNimiSv(), "");
        input(hakueraEdit.waitForNimiEn(), "");
        assertValidationErrors(1, hakueraEdit.waitForNimi());

        STEP("nimi validoidaan myös tallennusta painettaessa, ja oikea viesti tulee ruudulle");
        hakueraEdit.save();
        assertError("Nimen tulee joka kielell\u00e4 olla pituudeltaan 3 - 100", true);

        STEP("nimikenttään kelpaa vain tietyn pituinen teksti");
        input(hakueraEdit.waitForNimiFi(), "validvalue");
        input(hakueraEdit.waitForNimiSv(), "validvalue");
        input(hakueraEdit.waitForNimiEn(), "x"); // too short value
        assertValidationErrors(1, hakueraEdit.waitForNimi());

        STEP("hakulomakeUrl validointi toimii");
        input(hakueraEdit.waitForNimiEn(), "validvalue"); // asetetaan validiksi jotta helpompi assertoida muita arvoja
//        hakueraEdit.save(); tällä validointi menee läpi mutta immediate ei toimi?
        assertValidationErrors(0, hakueraEdit.waitForNimi());
        input(hakueraEdit.waitForHakulomakeUrl(), "invalidURL");
        assertValidationErrors(1, hakueraEdit.waitForHakulomakeUrl());

        STEP("varmistetaan että validointi toimii uuden luonnin lisäksi myös muokkauksessa (erillisen bindauksen jälkeen)");
        HakueraDTO model = new HakueraDTO();
        model.setNimiFi("nimi FI");
        hakueraList.clickHakuera(0);
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() throws Throwable {
                assertEquals("hakuera1 (paattynyt) FI", waitForElement(component.getHakuForm().getHaunNimi().getTextFi()).getAttribute("value"));
            }
        });
        hakueraEdit.selectDefaultKoodistoValues();
        input(hakueraEdit.waitForNimiEn(), "x");
        hakueraEdit.save();
        assertError("Nimen tulee joka kielell\u00e4 olla pituudeltaan 3 - 100", true);

    }

}
