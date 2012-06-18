package fi.vm.sade.tarjonta.selenium.story;

import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliPerustiedotDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliToteutusDTO;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaDTO;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaToteutusDTO;
import fi.vm.sade.tarjonta.selenium.TarjontaEmbedComponentTstSupport;
import fi.vm.sade.tarjonta.selenium.pageobject.KoulutusmoduuliToteutusEditViewPageObject;
import fi.vm.sade.tarjonta.service.KoulutusmoduuliAdminService;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.toteutus.KoulutusmoduuliToteutusEditView;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static fi.vm.sade.support.selenium.SeleniumUtils.*;
import static org.junit.Assert.*;

/**
 * @author Antti Salonen
 */
public class OVT_753_KoulutusmoduuliToteutusEditViewTest extends TarjontaEmbedComponentTstSupport<KoulutusmoduuliToteutusEditView> {

    private KoulutusmoduuliToteutusEditViewPageObject pageObject;

    @Autowired
    KoulutusmoduuliAdminService koulutusmoduuliAdminService;

    @Override
    public void initPageObjects() {
        pageObject = new KoulutusmoduuliToteutusEditViewPageObject(driver, getComponentByType(KoulutusmoduuliToteutusEditView.class));
    }

    @Override
    protected void initComponent(KoulutusmoduuliToteutusEditView koulutusmoduuliToteutusEditView) {

        // init mock service with some data
        createKoulutusmoduuli("koulutusmoduuli0");
        createKoulutusmoduuli("koulutusmoduuli1");
        createKoulutusmoduuli("koulutusmoduuli2");

        // init component
        KoulutusmoduuliToteutusDTO komoto = new TutkintoOhjelmaToteutusDTO();
        KoulutusmoduuliPerustiedotDTO perustiedot = new KoulutusmoduuliPerustiedotDTO();
//        perustiedot.getOpetuskielis().add("http://kieli/ruotsi");
//        perustiedot.getOpetusmuotos().add("http://opetusmuoto/opetusmuoto3");
        perustiedot.getOpetuskielis().add("Ruotsi"); // NOTE: uri vs arvo
        perustiedot.getOpetusmuotos().add("opetusmuoto3");
        perustiedot.setSuunniteltuKestoUri("6 kuukautta");
        komoto.setToteutettavaKoulutusmoduuliOID("oid_koulutusmoduuli1");        
        komoto.setKoulutuslajiUri("Nuorten koulutus");
        komoto.setMaksullisuus("500 euroa");
        komoto.setPerustiedot(perustiedot);
        koulutusmoduuliToteutusEditView.bind(komoto);
    }

    @Test
    public void testOpetuskieli() throws Throwable {
        assertNotNull(component.getForm().getField("perustiedot.opetuskielis"));

        STEP("varmistetaan että komotolla ennestään olevat opetuskielet näkyvät");
        assertTrue(pageObject.getOpetuskieliSelected().contains("Ruotsi"));

        STEP("varmistetaan että poistaminen toimii");
        pageObject.removeFirstOpetuskieli();
        component.getForm().commit();
        assertEquals(0, component.getKomoto().getPerustiedot().getOpetuskielis().size());

        STEP("opetuskielissä näkyy vaihtoehtoina eri kieliä");
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() throws Throwable {
                assertTrue(pageObject.getOpetuskieliOptions().contains("Suomi"));
                assertTrue(pageObject.getOpetuskieliOptions().contains("Englanti"));
            }
        });

        STEP("valitaan monta ja katsotaan että ne ilmestyi valittuihin");
        pageObject.addOpetuskieli("Suomi");
        pageObject.addOpetuskieli("Englanti");
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() throws Throwable {
                assertTrue(waitForElement(component.getOpetuskielis().getTable()).getText().contains("Suomi"));
                assertTrue(waitForElement(component.getOpetuskielis().getTable()).getText().contains("Englanti"));
            }
        });

        STEP("varmistetaan että valinnat päätyvät koodi urina dto:hon formin commitissa");
        component.getForm().commit();
        assertEquals(2, component.getKomoto().getPerustiedot().getOpetuskielis().size());
//        assertEquals("http://kieli/suomi", component.getKomoto().getPerustiedot().getOpetuskielis().get(0));
//        assertEquals("http://kieli/englanti", component.getKomoto().getPerustiedot().getOpetuskielis().get(1));
        assertEquals("Suomi", component.getKomoto().getPerustiedot().getOpetuskielis().get(0)); // NOTE: uri vs arvo
        assertEquals("Englanti", component.getKomoto().getPerustiedot().getOpetuskielis().get(1));
    }

    @Test
    public void testOpetusmuoto() throws Throwable {
        assertNotNull(component.getForm().getField("perustiedot.opetusmuotos"));

        STEP("varmistetaan että komotolla ennestään olevat opetusmuodot näkyvät");
        assertTrue(pageObject.getOpetusmuotoSelected().contains("opetusmuoto3"));

        STEP("varmistetaan että poistaminen toimii");
        pageObject.removeFirstOpetusmuoto();
        component.getForm().commit();
        assertEquals(0, component.getKomoto().getPerustiedot().getOpetusmuotos().size());

        STEP("opetusmuodoissa näkyy vaihtoehtoina eri muotoja");
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() throws Throwable {
                assertTrue(pageObject.getOpetusmuotoOptions().contains("opetusmuoto1"));
                assertTrue(pageObject.getOpetusmuotoOptions().contains("opetusmuoto2"));
            }
        });

        STEP("valitaan monta ja katsotaan että ne ilmestyi valittuihin");
        pageObject.addOpetusmuoto("opetusmuoto1");
        pageObject.addOpetusmuoto("opetusmuoto2");
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() throws Throwable {
                assertTrue(waitForElement(component.getOpetusmuotos().getTable()).getText().contains("opetusmuoto1"));
                assertTrue(waitForElement(component.getOpetusmuotos().getTable()).getText().contains("opetusmuoto2"));
            }
        });

        STEP("varmistetaan että valinnat päätyvät koodi urina dto:hon formin commitissa");
        component.getForm().commit();
        assertEquals(2, component.getKomoto().getPerustiedot().getOpetusmuotos().size());
//        assertEquals("http://opetusmuoto/opetusmuoto1", component.getKomoto().getPerustiedot().getOpetusmuotos().get(0));
//        assertEquals("http://opetusmuoto/opetusmuoto2", component.getKomoto().getPerustiedot().getOpetusmuotos().get(1));
        assertEquals("opetusmuoto1", component.getKomoto().getPerustiedot().getOpetusmuotos().get(0)); // NOTE: uri vs arvo
        assertEquals("opetusmuoto2", component.getKomoto().getPerustiedot().getOpetusmuotos().get(1));
    }
    
    @Test
    public void testCreateKoulutusmoduulinToteutus() {
        //TODO: tähän komoton luontitesti
        
        assertTrue(component.getSuunniteltuKestoTextfield().getValue().equals("6 kuukautta"));
        assertTrue(component.getKoulutusLajiKoodisto().getValue().equals("Nuorten koulutus"));
        assertTrue(component.getMaksullinenKoulutusTextfield().getValue().equals("500 euroa"));
    }

    @Test
    public void testKoulutusmoduuliField() throws Throwable {
        assertNotNull(component.getForm().getField("toteutettavaKoulutusmoduuliOID"));

        STEP("varmistetaan että komotolle ennestään liitetty koulutusmoduuli näkyy valittuna");
        assertEquals("oid_koulutusmoduuli1", pageObject.getComponent().getKoulutusmoduuliTextfield().getValue());

        STEP("koulutusmoduuli kentässä näkyy vaihtoehtoina eri koulutusmoduuleja");
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() throws Throwable {
                assertTrue(pageObject.getKoulutusmoduuliOptions().contains("koulutusmoduuli0"));
                assertTrue(pageObject.getKoulutusmoduuliOptions().contains("koulutusmoduuli2"));
            }
        });

        STEP("valitaan koulutusmoduuli");
        pageObject.selectKoulutusmoduuli("koulutusmoduuli2");

        STEP("varmistetaan että valinnat päätyvät koodi urina dto:hon formin commitissa");
        component.getForm().commit();
        assertEquals("oid_koulutusmoduuli2", pageObject.getComponent().getKoulutusmoduuliTextfield().getValue());
    }

    private void createKoulutusmoduuli(String nimi) {
        TutkintoOhjelmaDTO koulutusModuuli = new TutkintoOhjelmaDTO();
        koulutusModuuli.setNimi(nimi);
        koulutusModuuli.setOid("oid_"+nimi);
        koulutusmoduuliAdminService.save(koulutusModuuli);
    }    

}
