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

import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.publication.types.LearningOpportunityDownloadData;
import java.io.StringWriter;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

import fi.vm.sade.tarjonta.publication.types.LearningOpportunityDownloadDataType;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Date;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.junit.*;

/**
 * Smoke tests writing Tarjonta data as "Publication XML". The output still
 * needs to be verified using XPath. Most of this is validated with enrichment
 * tests but the parts (at least) that are not enriched and hence not tested,
 * need to be validated here.
 *
 * @author Jukka Raanamo
 */
public class LearningOpportunityDataWriterTest {

    private LearningOpportunityJAXBWriter writer;
    private StringWriter out;
    private static JAXBContext sJaxbContext;
    private static boolean sPrintXML;

    @BeforeClass
    public static void setUpClass() throws Exception {
        sJaxbContext = JAXBContext.newInstance(LearningOpportunityDownloadDataType.class.getPackage().getName());
        sPrintXML = Boolean.parseBoolean(System.getProperty("printXML", "false"));
    }

    @Before
    public void setUp() throws Exception {

        writer = new LearningOpportunityJAXBWriter(new ExportParams());
        out = new StringWriter();
        writer.setOutput(out);
        writer.setPartialDocument(false);

    }

    @After
    public void cleanUp() throws Exception {

        if (sPrintXML) {
            System.out.println("output: " + out.toString());
        }

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
    public void testWriteSingleTarjoaja() throws Exception {
        OrganisaatioDTO dto = new OrganisaatioDTO();
        dto.setOid("1.2.3.4.5");
        writer.onCollectStart();
        writer.onCollect(dto);
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

    @Test
    public void testWriteKoulutusmoduuliToteutusReferencesKoulutusmoduuli() throws Exception {

        Koulutusmoduuli m = createKoulutusmoduuli();
        KoulutusmoduuliToteutus t = createKoulutusmoduuliToteutus();

        t.setKoulutusmoduuli(m);

        writer.onCollectStart();
        writer.onCollect(m);
        writer.onCollect(t);
        writer.onCollectEnd();

        unmarshal();

    }

    @Test
    public void testWriteHakukohdeReferencesKoulutusmoduuliToteutus() throws Exception {

        // references created:
        //
        // hakukohde -> komoto -> komo
        //

        Koulutusmoduuli m = createKoulutusmoduuli();
        KoulutusmoduuliToteutus t = createKoulutusmoduuliToteutus();
        Hakukohde h = createHakukohde();
        h.addKoulutusmoduuliToteutus(t);

        t.setKoulutusmoduuli(m);

        writer.onCollectStart();
        writer.onCollect(m);
        writer.onCollect(t);
        writer.onCollect(h);
        writer.onCollectEnd();

    }

    /**
     * Unmarshals current content from output fixture.
     */
    private LearningOpportunityDownloadDataType unmarshal() throws Exception {

        return (LearningOpportunityDownloadData) unmarshal(new StringReader(out.toString()));

    }

    private LearningOpportunityDownloadData unmarshal(Reader reader) throws Exception {

        Unmarshaller um = sJaxbContext.createUnmarshaller();
        return (LearningOpportunityDownloadData) um.unmarshal(reader);

    }

    @Test
    public void testUnmarshalXmlPOC() throws Exception {

        unmarshal(new FileReader("src/test/resources/learning-data-simple.xml"));

    }

    private Koulutusmoduuli createKoulutusmoduuli() {

        return new TarjontaFixtures().createTutkintoOhjelma();

    }

    private KoulutusmoduuliToteutus createKoulutusmoduuliToteutus() {

        return new TarjontaFixtures().createTutkintoOhjelmaToteutus();

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

        Valintakoe valintakoe = new Valintakoe();
        ValintakoeAjankohta ajankohta = new ValintakoeAjankohta();
        ajankohta.setAlkamisaika(new Date());
        ajankohta.setPaattymisaika(new Date());
        valintakoe.addAjankohta(ajankohta);
        hakukohde.addValintakoe(valintakoe);

        Haku haku = new Haku();
        haku.setOid("haku/1.2.3.4.5");
        hakukohde.setHaku(haku);

        return hakukohde;

    }

    private Haku createHaku() {

        Haku haku = new Haku();
        haku.setTila(TarjontaTila.JULKAISTU);
        return haku;

    }
}
