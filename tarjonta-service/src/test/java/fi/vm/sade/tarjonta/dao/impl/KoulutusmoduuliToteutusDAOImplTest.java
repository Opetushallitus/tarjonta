/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.dao.impl;

import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.model.BinaryData;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Map;

import static org.junit.Assert.assertEquals;


@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@Transactional
public class KoulutusmoduuliToteutusDAOImplTest extends TestData {

    private static final String URI_EN = "kieli_en";
    private static final String URI_FI = "kieli_fi";
    private static final String IMAGE_FILENAME = "kuva.jpg";
    private static final String IMAGE_MIME = "image/jpeg";

    @Autowired
    private HakukohdeDAOImpl instance;

    @Autowired
    private TarjontaFixtures fixtures;

    @Before
    public void setUp() {
        EntityManager em = instance.getEntityManager();
        super.initializeData(em, fixtures);
    }

    @Test
    public void testUpdate() {
        //other data checks
        KoulutusmoduuliToteutus persistedKomoto = getPersistedKomoto1();

        persistedKomoto.setKuvaByUri(URI_FI, fixtures.createBinaryData());
        persistedKomoto.setKuvaByUri("kieli_sv", fixtures.createBinaryData());
        persistedKomoto.setKuvaByUri(URI_EN, fixtures.createBinaryData());

        persist(persistedKomoto);
        checkBinaryData(persistedKomoto, 3, URI_EN, "filename", "mimetype");
        BinaryData kuva = persistedKomoto.getKuvat().get(URI_EN);
        assertEquals(1, kuva.getData()[0]);

        kuva.setFilename(IMAGE_FILENAME);
        kuva.setMimeType(IMAGE_MIME);

        byte[] image = new byte[2];
        image[0] = 0;
        image[1] = 1;

        kuva.setData(image);
        persist(persistedKomoto); //update persisted komoto
        persistedKomoto.getKuvat().put(URI_EN, kuva);

        checkBinaryData(persistedKomoto, 3, URI_EN, IMAGE_FILENAME, IMAGE_MIME);
        assertEquals(0, kuva.getData()[0]);
        assertEquals(1, kuva.getData()[1]);
    }

    @Test
    public void testInsert() {
        //other data checks
        KoulutusmoduuliToteutus persistedKomoto = getPersistedKomoto1();
        KoulutusmoduuliToteutus komoto1 = fixtures.createTutkintoOhjelmaToteutus("777777");
        komoto1.setKoulutusmoduuli(persistedKomoto.getKoulutusmoduuli());
        persistedKomoto.setKuvaByUri(URI_EN, fixtures.createBinaryData());
        persist(komoto1);

        checkBinaryData(persistedKomoto, 1, URI_EN, "filename", "mimetype");
    }

    private void checkBinaryData(final KoulutusmoduuliToteutus komoto, int length, final String lang, final String filename, final String mime) {
        Map<String, BinaryData> kuvat = komoto.getKuvat();

        assertEquals(length, kuvat.size());
        BinaryData binData = kuvat.get(lang);

        assertEquals(filename, binData.getFilename());
        assertEquals(mime, binData.getMimeType());
    }
    
    @Test
    public void testXSSFiltering(){
        //TODO test more fields...
        KoulutusmoduuliToteutus persistedKomoto = getPersistedKomoto1();
        KoulutusmoduuliToteutus komoto1 = fixtures.createTutkintoOhjelmaToteutus("7777771");
        komoto1.setOid("xss-1");
        komoto1.setNimi(new MonikielinenTeksti("fi", "ei saa muuttaa & merkkiä!"));
        komoto1.getTekstit().put(KomotoTeksti.KOHDERYHMA, new MonikielinenTeksti("fi", "kohderyhmä"));
        komoto1.getTekstit().put(KomotoTeksti.LISATIETOA_OPETUSKIELISTA, new MonikielinenTeksti("fi", "<table><a href='window.alert(\"hello\")'>foo</a></table>"));
        komoto1.setKoulutusmoduuli(persistedKomoto.getKoulutusmoduuli());
        persistedKomoto.setKuvaByUri(URI_EN, fixtures.createBinaryData());
        persist(komoto1);
        
        komoto1 = komotoDao.findBy("oid", "xss-1").get(0);
        Assert.assertEquals("ei saa muuttaa & merkkiä!", komoto1.getNimi().asMap().get("fi"));
        Assert.assertEquals("<table>foo</table>", komoto1.getTekstit().get(KomotoTeksti.LISATIETOA_OPETUSKIELISTA).getTekstiForKieliKoodi("fi"));
        Assert.assertEquals("kohderyhmä", komoto1.getTekstit().get(KomotoTeksti.KOHDERYHMA).getTekstiForKieliKoodi("fi"));
        
        komoto1.getTekstit().put(KomotoTeksti.KOHDERYHMA, new MonikielinenTeksti("fi", "kohderyhmä"));
        komoto1.getTekstit().put(KomotoTeksti.LISATIETOA_OPETUSKIELISTA, new MonikielinenTeksti("fi", "<table><a href='window.alert(\"hello\")'>foo</a></table>"));
        persist(komoto1);
        komoto1 = komotoDao.findBy("oid", "xss-1").get(0);
        Assert.assertEquals("ei saa muuttaa & merkkiä!", komoto1.getNimi().asMap().get("fi"));
        Assert.assertEquals("<table>foo</table>", komoto1.getTekstit().get(KomotoTeksti.LISATIETOA_OPETUSKIELISTA).getTekstiForKieliKoodi("fi"));
        Assert.assertEquals("kohderyhmä", komoto1.getTekstit().get(KomotoTeksti.KOHDERYHMA).getTekstiForKieliKoodi("fi"));
    }

}
