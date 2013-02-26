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
import fi.vm.sade.tarjonta.publication.types.ApplicationOptionType;
import fi.vm.sade.tarjonta.publication.types.AttachmentCollectionType;
import fi.vm.sade.tarjonta.publication.types.CodeSchemeType;
import fi.vm.sade.tarjonta.publication.types.CodeValueType;
import fi.vm.sade.tarjonta.publication.types.ExaminationEventType;
import fi.vm.sade.tarjonta.publication.types.ExaminationLocationType;
import fi.vm.sade.tarjonta.publication.types.ExtendedStringType;
import fi.vm.sade.tarjonta.publication.types.LearningOpportunityDownloadData;
import java.io.StringWriter;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

import fi.vm.sade.tarjonta.publication.types.LearningOpportunityDownloadDataType;
import fi.vm.sade.tarjonta.publication.types.LocalizedTextType;
import fi.vm.sade.tarjonta.publication.types.PostalAddress;
import fi.vm.sade.tarjonta.publication.types.SelectionCriterionsType;
import fi.vm.sade.tarjonta.publication.types.StatusSchemeType;
import fi.vm.sade.tarjonta.publication.types.TypedDescriptionType;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.joda.time.DateTime;
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

    private static final String AO_PREFIX = "hakukohde_";
    private static final String LANGUAGE_CODE_FI = "fi";
    private LearningOpportunityJAXBWriter writer;
    private StringWriter out;
    private static JAXBContext sJaxbContext;
    private static boolean sPrintXML;
    private List<MonikielinenMetadata> MMD_SORA = createMonikielinenMetadata("SORA");
    private List<MonikielinenMetadata> MMD_VALINTAPERUSTEKUVAUS = createMonikielinenMetadata("valintaperustekuvaus");
    private static final Date DATE_START = new DateTime(2013, 1, 5, 1, 1).toDate();
    private static final Date DATE_END = new DateTime(2013, 2, 28, 1, 1).toDate();
    private static final Date DATE_GENERIC = new DateTime(2016, 7, 2, 3, 4).toDate();

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
        writer.onCollect(createHakukohde(), MMD_SORA, MMD_VALINTAPERUSTEKUVAUS);
        writer.onCollectEnd();

        LearningOpportunityDownloadDataType unmarshal = unmarshal();
        List<ApplicationOptionType> applicationOption = unmarshal.getApplicationOption();
        assertEquals(1, applicationOption.size());

        final ApplicationOptionType ao = applicationOption.get(0);
        assertEquals("hakukohde/1.2.3.4.5", ao.getIdentifier().getValue());
        assertEquals(StatusSchemeType.UNKNOWN, ao.getStatus()); //valmis => unknown
        assertKoodi(ao.getTitle().getCode(), "HN1", "", "", CodeSchemeType.KOODISTO);
        assertNotNull(ao.getSelectionCriterions());

        final TypedDescriptionType descPerustiedot = ao.getDescription();
        assertEquals(1, descPerustiedot.getText().size());
        assertEquals("hakukohde_lisatiedot", descPerustiedot.getText().get(0).getValue());
        assertEquals(LANGUAGE_CODE_FI, descPerustiedot.getText().get(0).getLang());

        final SelectionCriterionsType sc = ao.getSelectionCriterions();
        assertNotNull(ao.getSelectionCriterions());
        assertNotNull(sc.getAttachments());
        assertNotNull(sc.getDescription());
        assertNotNull(sc.getEntranceExaminations());
        assertNotNull(sc.getLastYearMaxScore());
        assertNotNull(sc.getLastYearMinScore());
        assertNotNull(sc.getLastYearTotalApplicants());
        assertNotNull(sc.getStartingQuota());

        assertEquals(new BigInteger("200"), sc.getLastYearMaxScore());
        assertEquals(new BigInteger("5"), sc.getLastYearMinScore());
        assertEquals(new BigInteger("1111"), sc.getLastYearTotalApplicants());
        assertEquals(new BigInteger("10"), sc.getStartingQuota());

        final TypedDescriptionType scDesc = sc.getDescription();
        assertEquals(1, scDesc.getText().size());
        assertEquals("valintaperustekuvaus", scDesc.getText().get(0).getValue());
        assertEquals(LANGUAGE_CODE_FI, scDesc.getText().get(0).getLang());

        final TypedDescriptionType erDesc = ao.getEligibilityRequirements().getDescription();
        assertEquals(1, erDesc.getText().size());
        assertEquals("SORA", erDesc.getText().get(0).getValue());
        assertEquals(LANGUAGE_CODE_FI, erDesc.getText().get(0).getLang());

        //Attachments
        assertAttachment(sc.getAttachments());

        //Exams
        assertValintakoe(sc.getEntranceExaminations());
    }

    private void assertAttachment(AttachmentCollectionType act) {
        assertEquals(1, act.getAttachment().size());
        AttachmentCollectionType.Attachment attachment = act.getAttachment().get(0);
        assertNotNull(attachment);
        TypedDescriptionType returnDesc = attachment.getDescription();
        assertNotNull(returnDesc);
        assertEquals(1, returnDesc.getText().size());
        assertEquals("hakukohde_hakukohdeLiite_kuvaus", returnDesc.getText().get(0).getValue());
        assertEquals(LANGUAGE_CODE_FI, returnDesc.getText().get(0).getLang());

        //attachment return
        AttachmentCollectionType.Attachment.Return aReturn = attachment.getReturn();
        assertEquals(DATE_GENERIC, aReturn.getDueDate());

        //attachment return address
        AttachmentCollectionType.Attachment.Return.To to = aReturn.getTo();

        assertEquals("hakukohde_email", to.getEmailAddress());
        assertOsoite(to.getPostalAddress(), "hakukohde_liite_perustieto rivi1", "hakukohde_liite_perustieto rivi2", "hakukohde_liite_postinumero", "hakukohde_liite_postitoimipaikka");
    }

    private void assertValintakoe(final SelectionCriterionsType.EntranceExaminations scEntranceExam) {
        assertNull(scEntranceExam.getNoExam()); //TODO... or not needed data field?

        List<SelectionCriterionsType.EntranceExaminations.Examination> examination = scEntranceExam.getExamination();
        assertEquals(1, examination.size());
        SelectionCriterionsType.EntranceExaminations.Examination exam = examination.get(0);
        List<ExaminationEventType> examinationEvent = exam.getExaminationEvent();
        assertEquals(1, examinationEvent.size());
        ExaminationEventType event = examinationEvent.get(0);
        assertEquals(DATE_END, event.getEnd());
        assertEquals(DATE_START, event.getStart());

        assertNotNull(event.getLocations());
        ExaminationLocationType location = event.getLocations().getLocation().get(0);

        assertEquals(2, location.getAddressLine().size());
        assertEquals("hakukohde_valintakoe_ajankohdan_osoite_postitoimipaikka", location.getCity());
        assertEquals("hakukohde_valintakoe_ajankohdan_osoite_postinumero", location.getPostalCode());
        assertEquals(null, location.getCompany());
        assertEquals(null, location.getCountry());
        assertEquals(null, location.getName());
        assertEquals(null, location.getRecipientName());

        //descriptions
        final TypedDescriptionType scEntranceExamDesc = exam.getDescription();
        assertEquals(1, scEntranceExamDesc.getText().size());
        assertEquals("hakukohde_valintakoe_kuvaus", scEntranceExamDesc.getText().get(0).getValue());
        assertEquals(LANGUAGE_CODE_FI, scEntranceExamDesc.getText().get(0).getLang());
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
        writer.onCollect(h, MMD_SORA, MMD_VALINTAPERUSTEKUVAUS);
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
        final String prefixUri = "uri:" + AO_PREFIX;

        Hakukohde hakukohde = new Hakukohde();

        hakukohde.setHakukohdeNimi("hakukohde1");
        hakukohde.setOid("hakukohde/1.2.3.4.5");
        hakukohde.setAlinValintaPistemaara(5);
        hakukohde.setAloituspaikatLkm(10);
        hakukohde.setHakukelpoisuusvaatimus("HKV1");
        hakukohde.setHakukohdeNimi("HN1");
        hakukohde.setHakukohdeKoodistoNimi("uri:hakukohde");
        hakukohde.setTila(TarjontaTila.VALMIS);
        hakukohde.setYlinValintaPistemaara(200);

        /*
         *  create hakukohde perustiedot
         */
        hakukohde.setEdellisenVuodenHakijat(1111);
        hakukohde.setHakukohdeKoodistoNimi(prefixUri + "hakukohde#1");
        hakukohde.setSoraKuvausKoodiUri(prefixUri + "sora");
        hakukohde.setValintaperustekuvausKoodiUri(prefixUri + "valintaperustekuvaus");
        hakukohde.setLisatiedot(createMonikielinenTeksti(AO_PREFIX + "lisatiedot"));

        /*
         * TODO?
         * WSDL file do not have the fields. 
         */
        hakukohde.setLiitteidenToimitusOsoite(createOsoite(AO_PREFIX + "default_liite_"));
        hakukohde.setKaytetaanHaunPaattymisenAikaa(true);
        hakukohde.setLiitteidenToimitusPvm(DATE_GENERIC);

        /*
         * add valintakoe / Examination
         */
        Valintakoe valintakoe = new Valintakoe();
        ValintakoeAjankohta ajankohta = new ValintakoeAjankohta();
        ajankohta.setAlkamisaika(DATE_START);
        ajankohta.setPaattymisaika(DATE_END);
        ajankohta.setAjankohdanOsoite(createOsoite(AO_PREFIX + "valintakoe_ajankohdan_osoite_"));
        valintakoe.addAjankohta(ajankohta);
        valintakoe.setKuvaus(createMonikielinenTeksti(AO_PREFIX + "valintakoe_kuvaus"));
        valintakoe.setTyyppiUri(prefixUri + "valintakoetyyppi");
        hakukohde.addValintakoe(valintakoe);

        /*
         *Add liite
         */
        HakukohdeLiite khl = new HakukohdeLiite();
        khl.setErapaiva(DATE_GENERIC);
        khl.setHakukohde(hakukohde);
        khl.setKuvaus(createMonikielinenTeksti(AO_PREFIX + "hakukohdeLiite_kuvaus"));
        khl.setLiitetyyppi(prefixUri + "liitetyyppi");
        khl.setLiitteenTyyppiKoodistoNimi(prefixUri + "liite");
        khl.setSahkoinenToimitusosoite(AO_PREFIX + "email");
        khl.setToimitusosoite(createOsoite(AO_PREFIX + "liite_"));
        khl.setHakukohde(hakukohde);
        hakukohde.addLiite(khl);

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

    private Osoite createOsoite(final String AO_PREFIX) {
        Osoite osoite = new Osoite();
        osoite.setOsoiterivi1(AO_PREFIX + "perustieto rivi1");
        osoite.setOsoiterivi2(AO_PREFIX + "perustieto rivi2");
        osoite.setPostinumero(AO_PREFIX + "postinumero");
        osoite.setPostitoimipaikka(AO_PREFIX + "postitoimipaikka");
        return osoite;
    }

    private void assertOsoite(final PostalAddress pa, final String street1, final String street2, final String postCode, final String city) {
        assertNotNull(pa);
        assertEquals(2, pa.getAddressLine().size());
        //assertEquals("uri not equal", street1, pa.getAddressLine().get(0));
        //assertEquals("value not equal", street2, pa.getAddressLine().get(1));
        assertEquals("version not equal", postCode, pa.getPostalCode());
        assertEquals("scheme not equal", city, pa.getCity());
        assertEquals(null, pa.getCompany());
        assertEquals(null, pa.getCountry());
        assertEquals(null, pa.getRecipientName());
    }

    private void assertKoodi(final CodeValueType.Code code, final String uri, final String value, final String version, final CodeSchemeType scheme) {
        assertNotNull(code);
        assertEquals("uri not equal", uri, code.getUri());
        assertEquals("value not equal", value, code.getValue());
        assertEquals("version not equal", version, code.getVersion());
        assertEquals("scheme not equal", scheme, code.getScheme());
    }

    private static MonikielinenTeksti createMonikielinenTeksti(final String data) {
        MonikielinenTeksti monikielinenTeksti = new MonikielinenTeksti();
        monikielinenTeksti.addTekstiKaannos(LANGUAGE_CODE_FI, data);
        return monikielinenTeksti;
    }

    private static List<MonikielinenMetadata> createMonikielinenMetadata(final String data) {
        List<MonikielinenMetadata> metadata = new ArrayList<MonikielinenMetadata>(1);
        MonikielinenMetadata mmd = new MonikielinenMetadata();
        mmd.setArvo(data);
        mmd.setKieli(LANGUAGE_CODE_FI);
        metadata.add(mmd);

        return metadata;
    }
}
