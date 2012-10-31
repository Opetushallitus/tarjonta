/*
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
package fi.vm.sade.tarjonta.publication;

import fi.vm.sade.tarjonta.TarjontaFixtures;
import java.io.StringWriter;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.TarjontaTila;
import fi.vm.sade.tarjonta.publication.types.LearningOpportunityDownloadDataType;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import org.junit.*;

/**
 *
 * @author Jukka Raanamo
 */
public class LearningOpportunityDataWriterTest {

    private LearningOpportunityDataWriter writer;

    private StringWriter out;

    private static JAXBContext sJaxbContext;

    @BeforeClass
    public static void setUpClass() throws Exception {
        sJaxbContext = JAXBContext.newInstance(LearningOpportunityDownloadDataType.class.getPackage().getName());
    }

    @Before
    public void setUp() throws Exception {

        writer = new LearningOpportunityDataWriter();
        out = new StringWriter();
        writer.setOutput(out);
        writer.setPartialDocument(false);

    }

    @After
    public void cleanUp() throws Exception {

    }

    @Test
    public void testWriteEmptyDocumentWithOutException() throws Exception {

        writer.onCollectStart();
        writer.onCollectEnd();

    }

    @Test
    @Ignore // not writing start document does not write other elements either
    public void testWritePartialDocument() throws Exception {

        writer.setPartialDocument(true);
        writer.onCollectStart();
        writer.onCollectEnd();

        assertEquals("<LearningOpportunityDownloadData/>", out.toString());

    }

    @Test
    public void testWriteSingleHaku() throws Exception {

        writer.onCollectStart();
        writer.onCollect(createHaku());
        writer.onCollectEnd();

        unmarshal();

    }

    @Test
    public void testWriteSingleHakukohde() throws Exception {

        writer.onCollectStart();
        writer.onCollect(createHakukohde());
        writer.onCollectEnd();

        unmarshal();

    }

    @Test
    public void testWriteSingleKoulutusmoduuli() throws Exception {

        writer.onCollectStart();
        writer.onCollect(createKoulutusmoduuli());
        writer.onCollectEnd();

        unmarshal();

    }

    /**
     * Unmarshals current content from output fixture.
     */
    private LearningOpportunityDownloadDataType unmarshal() throws Exception {

        //System.out.println("unmarshal: " + out.toString());

        JAXBElement<LearningOpportunityDownloadDataType> e = unmarshal(new StringReader(out.toString()));
        return e.getValue();

    }

    private JAXBElement<LearningOpportunityDownloadDataType> unmarshal(Reader reader) throws Exception {

        Unmarshaller um = sJaxbContext.createUnmarshaller();
        return (JAXBElement<LearningOpportunityDownloadDataType>) um.unmarshal(reader);

    }

    @Test
    public void testUnmarshalXmlPOC() throws Exception {

        unmarshal(new FileReader("src/test/resources/learning-data-simple.xml"));

    }


    private Koulutusmoduuli createKoulutusmoduuli() {

        return new TarjontaFixtures().createTutkintoOhjelma();

    }

    private Hakukohde createHakukohde() {

        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setHakukohdeNimi("hakukohde1");
        hakukohde.setOid("hakukohde/1.2.3.4.5");
        hakukohde.setAlinValintaPistemaara(5);
        hakukohde.setAloituspaikatLkm(10);
        hakukohde.setHakukelpoisuusvaatimus("HKV1");
        hakukohde.setHakukohdeNimi("HN1");
        hakukohde.setTila(TarjontaTila.VALMIS);
        hakukohde.setYlinValintaPistemaara(200);

        Haku haku = new Haku();
        haku.setOid("haku/1.2.3.4.5");
        hakukohde.setHaku(haku);

        return hakukohde;

    }

    private Haku createHaku() {

        Haku haku = new Haku();
        haku.setTila(TarjontaTila.LUONNOS.name());
        return haku;

    }

}

