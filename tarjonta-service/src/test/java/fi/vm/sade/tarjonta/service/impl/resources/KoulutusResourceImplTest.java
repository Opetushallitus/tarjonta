/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.service.impl.resources;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoItemType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.tarjonta.service.resources.dto.kk.KorkeakouluDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.SuunniteltuKestoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.ToteutusDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.UiDTO;
import fi.vm.sade.tarjonta.service.types.HenkiloTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.easymock.EasyMock.createMock;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.springframework.core.convert.ConversionService;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KomotoConverterToKorkeakouluDTO;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KorkeakouluDTOConverterToKomoto;
import fi.vm.sade.tarjonta.service.resources.dto.kk.UiMetaDTO;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;

/**
 *
 * @author jani
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("embedded-solr")
@Transactional()
public class KoulutusResourceImplTest {

    private static final String KOULUTUSOHJELMA = "koulutusohjelma";
    private static final String URI_KIELI_FI = "kieli_fi";
    private static final String LOCALE_FI = "FI";
    private static final String KOULUTUSKOODI = "koulutuskoodi";
    private static final String KOULUTUSASTE = "koulutusaste";
    private static final String KOULUTUSALA = "koulutusala";
    private static final String OPINTOALA = "opintoala";
    private static final String TUTKINTO = "tutkinto";
    private static final String TUTKINTONIMIKE = "tutkintonimike";
    private static final String TEEMA = "teema";
    private static final String OPETUSKIELI = "opetuskieli";
    private static final String POHJAKOULUTUS = "pohjakoulutus";
    private static final String OPETUMUOTO = "opetusmuto";
    private static final String EQF = "EQF";
    private static final String KOMO_OID = "komo_oid";
    private static final String KOMOTO_OID = "komoto_oid";
    private static final String ORGANISAATIO_OID = "organisaatio_oid";
    private static final String ORGANISAATIO_NIMI = "organisaatio_nimi";
    private static final String TUNNISTE = "tunniste_txt";
    private static final String SUUNNITELTU_KESTO_VALUE = "10";
    private static final String SUUNNITELTU_KESTO = "suunnteltu_kesto";
    private static final String[] PERSON = {"henkilo_oid", "firstanames", "lastname", "Mr.", "oph@oph.fi", "12345678"};
    private KoulutusResourceImpl instance;
    private final DateTime DATE = new DateTime(2013, 1, 1, 1, 1);
    private OrganisaatioService organisaatioServiceMock;
    private OIDService oidServiceMock;
    private ConversionService conversionServiceMock;
    private KomotoConverterToKorkeakouluDTO convertToDTO;
    private KorkeakouluDTOConverterToKomoto convertToKomoto;
    private ConvertEntityStub convertToEntityStub;
    private ConvertDtoStub convertToDTOStub;
    private OrganisaatioDTO organisaatioDTO;
    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired
    private IndexerResource solrIndexer;
    private TarjontaKoodistoHelper tarjontaKoodistoHelperMock;

    @Before
    public void setUp() {
        //INIT ORGANISATION DTO
        organisaatioDTO = new OrganisaatioDTO();
        organisaatioDTO.setOid(ORGANISAATIO_OID);
        organisaatioDTO.setNimi(new MonikielinenTekstiTyyppi());
        organisaatioDTO.getNimi().getTeksti().add(new MonikielinenTekstiTyyppi.Teksti(ORGANISAATIO_NIMI, LOCALE_FI));

        //CREATE MOCKS
        conversionServiceMock = createMock(ConversionService.class);
        organisaatioServiceMock = createMock(OrganisaatioService.class);
        oidServiceMock = createMock(OIDService.class);
        tarjontaKoodistoHelperMock = createMock(TarjontaKoodistoHelper.class);

        //INIT DATA CONVERTERS
        convertToDTO = new KomotoConverterToKorkeakouluDTO();
        convertToKomoto = new KorkeakouluDTOConverterToKomoto();
        instance = new KoulutusResourceImpl();
        convertToEntityStub = new ConvertEntityStub();
        convertToDTOStub = new ConvertDtoStub();

        //SET VALUES TO INSTANCES
        Whitebox.setInternalState(convertToKomoto, "oidService", oidServiceMock);
        Whitebox.setInternalState(convertToDTO, "organisaatioService", organisaatioServiceMock);
        Whitebox.setInternalState(convertToDTO, "tarjontaKoodistoHelper", tarjontaKoodistoHelperMock);
        Whitebox.setInternalState(instance, "organisaatioService", organisaatioServiceMock);
        Whitebox.setInternalState(instance, "conversionService", conversionServiceMock);
        Whitebox.setInternalState(instance, "koulutusmoduuliToteutusDAO", koulutusmoduuliToteutusDAO);
        Whitebox.setInternalState(instance, "koulutusmoduuliDAO", koulutusmoduuliDAO);
        Whitebox.setInternalState(instance, "solrIndexer", solrIndexer);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCreateAndLoadToteutus() throws ExceptionMessage {
        KorkeakouluDTO dto = new KorkeakouluDTO();
        /*
         * KOMO data fields:
         */
        dto.getKoulutusohjelma().getMeta().put(URI_KIELI_FI, toMetaValue("koulutusohjelma", URI_KIELI_FI));
        dto.getOrganisaatio().setOid(ORGANISAATIO_OID);
        dto.setKoulutusaste(toKoodiUri(KOULUTUSASTE));
        dto.setKoulutusala(toKoodiUri(KOULUTUSALA));
        dto.setOpintoala(toKoodiUri(OPINTOALA));
        dto.setTutkinto(toKoodiUri(TUTKINTO));
        dto.setTutkintonimike(toKoodiUri(TUTKINTONIMIKE));
        dto.setEqf(toKoodiUri(EQF));
        dto.setTila(TarjontaTila.JULKAISTU);
        dto.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.TUTKINTO);
        dto.setTunniste(TUNNISTE);
        dto.setOpintojenMaksullisuus(Boolean.TRUE);
        dto.setKoulutuskoodi(toKoodiUri(KOULUTUSKOODI));
        dto.setKoulutusasteTyyppi(KoulutusasteTyyppi.AMMATTIKORKEAKOULUTUS);
        dto.setKoulutuksenAlkamisPvm(DATE.toDate());
        dto.getTeemas().getMeta().put(URI_KIELI_FI, toKoodiUri(TEEMA));
        dto.getOpetuskielis().getMeta().put(URI_KIELI_FI, toKoodiUri(OPETUSKIELI));
        dto.getOpetusmuodos().getMeta().put(URI_KIELI_FI, toKoodiUri(OPETUMUOTO));
        dto.getPohjakoulutusvaatimukset().getMeta().put(URI_KIELI_FI, toKoodiUri(POHJAKOULUTUS));
        dto.setSuunniteltuKesto(new SuunniteltuKestoDTO(SUUNNITELTU_KESTO_VALUE, SUUNNITELTU_KESTO + "_uri", "1", null));
        dto.getYhteyshenkilos().add(new YhteyshenkiloTyyppi(PERSON[0], PERSON[1], PERSON[2], PERSON[3], PERSON[4], PERSON[5], null, HenkiloTyyppi.YHTEYSHENKILO));

        //EXPECT
        expect(organisaatioServiceMock.findByOid(ORGANISAATIO_OID)).andReturn(organisaatioDTO).times(1);
        expect(conversionServiceMock.convert(isA(KorkeakouluDTO.class), eq(KoulutusmoduuliToteutus.class))).andStubDelegateTo(convertToEntityStub);
        expect(conversionServiceMock.convert(isA(KoulutusmoduuliToteutus.class), eq(KorkeakouluDTO.class))).andStubDelegateTo(convertToDTOStub);
        //the calls of the OidServices must be in correct order!
        expect(oidServiceMock.newOid(NodeClassCode.TEKN_5)).andReturn(KOMO_OID);
        expect(oidServiceMock.newOid(NodeClassCode.TEKN_5)).andReturn(KOMOTO_OID);


        //KOODISTO DATA VALIDATION
        expectKOMOKoodistoUri(KOULUTUSKOODI);
        expectKOMOKoodistoUri(KOULUTUSASTE);
        expectKOMOKoodistoUri(KOULUTUSALA);
        expectKOMOKoodistoUri(OPINTOALA);
        //  expectKoodistoUri(TUTKINTO);
        expectKOMOKoodistoUri(TUTKINTONIMIKE);
        expectKOMOKoodistoUri(EQF);

        expectMetaUri(TEEMA);
        expectMetaUri(OPETUSKIELI);
        expectMetaUri(OPETUMUOTO);
        expectMetaUri(POHJAKOULUTUS);
        expectMetaUri(SUUNNITELTU_KESTO);

        //REPLAY
        replay(oidServiceMock);
        replay(organisaatioServiceMock);
        replay(conversionServiceMock);
        replay(tarjontaKoodistoHelperMock);

        /*
         * INSERT KORKEAKOULU TO DB
         */
        instance.createToteutus(dto);

        /*
         * LOAD KORKEAKOULU DTO FROM DB
         */
        final KorkeakouluDTO result = (KorkeakouluDTO) instance.getToteutus(KOMOTO_OID);
        assertLoadData(result);

        verify(oidServiceMock);
        verify(organisaatioServiceMock);
        verify(conversionServiceMock);
        verify(tarjontaKoodistoHelperMock);
    }

    private void assertLoadData(final KorkeakouluDTO result) {
        assertNotNull(result);
        assertEquals(KOMOTO_OID, result.getOid());
        assertEquals(ORGANISAATIO_OID, result.getOrganisaatio().getOid());
        assertEquals(ORGANISAATIO_NIMI, result.getOrganisaatio().getNimi());

        assertEquals(KoulutusasteTyyppi.AMMATTIKORKEAKOULUTUS, result.getKoulutusasteTyyppi());
        final UiDTO koulutusohjelmaFi = result.getKoulutusohjelma().getMeta().get(URI_KIELI_FI);
        assertNotNull("No koulutusohjelma name by '" + URI_KIELI_FI + "'", koulutusohjelmaFi);

        assertEquals(URI_KIELI_FI, koulutusohjelmaFi.getKoodi().getUri()); //name of the koulutusohjelma
        assertEquals("1", koulutusohjelmaFi.getKoodi().getVersio()); //name of the koulutusohjelma
        assertEquals(KOULUTUSOHJELMA, result.getKoulutusohjelma().getArvo()); //name of the koulutusohjelma

        assertEqualDtoKoodi(KOULUTUSASTE, result.getKoulutusaste());
        assertEqualDtoKoodi(KOULUTUSALA, result.getKoulutusala());
        assertEqualDtoKoodi(OPINTOALA, result.getOpintoala());
        // assertEqualDtoKoodi(TUTKINTO, result.getTutkinto());
        assertEqualDtoKoodi(TUTKINTONIMIKE, result.getTutkintonimike());
        assertEqualDtoKoodi(EQF, result.getEqf());
        assertEqualDtoKoodi(KOULUTUSKOODI, result.getKoulutuskoodi());

        assertEquals(TarjontaTila.JULKAISTU, result.getTila());
        assertEquals(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.TUTKINTO, result.getKoulutusmoduuliTyyppi());
        assertEquals(KoulutusasteTyyppi.AMMATTIKORKEAKOULUTUS, result.getKoulutusasteTyyppi());
        assertEquals(TUNNISTE, result.getTunniste());
        assertEquals(Boolean.TRUE, result.getOpintojenMaksullisuus());
        assertEquals(DATE.toDate(), result.getKoulutuksenAlkamisPvm());

        assertEqualMetaDto(TEEMA, result.getTeemas());
        assertEqualMetaDto(OPETUSKIELI, result.getOpetuskielis());
        assertEqualMetaDto(OPETUMUOTO, result.getOpetusmuodos());
        assertEqualMetaDto(POHJAKOULUTUS, result.getPohjakoulutusvaatimukset());

        assertEquals(SUUNNITELTU_KESTO_VALUE, result.getSuunniteltuKesto().getArvo());
        assertEquals(SUUNNITELTU_KESTO + "_uri", result.getSuunniteltuKesto().getKoodi().getUri());
        assertEquals("1", result.getSuunniteltuKesto().getKoodi().getVersio());
        YhteyshenkiloTyyppi next = result.getYhteyshenkilos().iterator().next();
        assertEquals(PERSON[0], next.getHenkiloOid());
        assertEquals(PERSON[1], next.getEtunimet());
        assertEquals(PERSON[2], next.getSukunimi());
        assertEquals(PERSON[3], next.getTitteli());
        assertEquals(PERSON[4], next.getSahkoposti());
        assertEquals(PERSON[5], next.getPuhelin());
        assertEquals(HenkiloTyyppi.YHTEYSHENKILO, next.getHenkiloTyyppi());
    }

    private static UiDTO toKoodiUri(final String type) {
        return new UiDTO(null, type + "_uri", "1", null);
    }

    private static UiDTO toMetaValue(final String value, String lang) {
        return new UiDTO(null, lang, "1", value);
    }

    private static UiDTO toValue(final String value) {
        return new UiDTO(value, null, null, null);
    }

    private void expectKOMOKoodistoUri(final String field) {
        expect(tarjontaKoodistoHelperMock.getKoodiByUri(field + "_uri#1")).andReturn(createKoodiType(field));
        expect(tarjontaKoodistoHelperMock.getKoodiNimi(field + "_uri", new Locale(LOCALE_FI))).andReturn(field);
        expect(tarjontaKoodistoHelperMock.convertKielikoodiToKieliUri(LOCALE_FI)).andReturn(URI_KIELI_FI);
        expect(tarjontaKoodistoHelperMock.getKoodiNimi(URI_KIELI_FI, new Locale(LOCALE_FI))).andReturn("suomi");
    }

    private void expectMetaUri(final String field) {
        expect(tarjontaKoodistoHelperMock.getKoodiByUri(field + "_uri#1")).andReturn(createKoodiType(field));
        expect(tarjontaKoodistoHelperMock.getKoodiNimi(field + "_uri", new Locale(LOCALE_FI))).andReturn(field);
    }

    private void assertEqualDtoKoodi(final String field, final UiDTO dto) {
        assertNotNull("UiDTO : " + field, dto);
        assertNotNull("KoodiDTO : " + field, dto.getKoodi());
        assertEquals(field + "_uri", dto.getKoodi().getUri());
        assertEquals("1", dto.getKoodi().getVersio());
        assertEquals(field, dto.getKoodi().getKaannos());
        assertEquals(field, dto.getKoodi().getArvo());
    }

    private void assertEqualMetaDto(final String field, final UiMetaDTO dto) {

        assertEquals(true, dto.getMeta().containsKey(field + "_uri"));
        UiDTO get = dto.getMeta().get(field + "_uri");
        assertEquals(field, get.getKoodi().getArvo());
        assertEquals(field + "_uri", get.getKoodi().getUri());
        assertEquals("1", get.getKoodi().getVersio());
    }

    private class ConvertEntityStub<T extends KoulutusmoduuliToteutus> implements ConversionService {

        @Override
        public boolean canConvert(Class<?> type, Class<?> type1) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean canConvert(TypeDescriptor td, TypeDescriptor td1) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <T> T convert(Object o, Class<T> type) {
            return (T) convertToKomoto.convert((KorkeakouluDTO) o);
        }

        @Override
        public Object convert(Object o, TypeDescriptor td, TypeDescriptor td1) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private class ConvertDtoStub<T extends KorkeakouluDTO> implements ConversionService {

        @Override
        public boolean canConvert(Class<?> type, Class<?> type1) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean canConvert(TypeDescriptor td, TypeDescriptor td1) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <T> T convert(Object o, Class<T> type) {
            return (T) convertToDTO.convert((KoulutusmoduuliToteutus) o);
        }

        @Override
        public Object convert(Object o, TypeDescriptor td, TypeDescriptor td1) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    protected KoodiType createKoodiType(final String fieldName) {
        return createKoodiType(fieldName, null);
    }

    protected KoodiType createKoodiType(final String fieldName, final String koodistoUri) {
        KoodiType koodiType = new KoodiType();
        koodiType.setKoodiUri(fieldName + "_uri");
        koodiType.setVersio(1);
        koodiType.setKoodiArvo(fieldName);
        koodiType.setTila(TilaType.HYVAKSYTTY);

        KoodiMetadataType meta = new KoodiMetadataType();
        meta.setKuvaus(fieldName + " kuvaus");
        meta.setKieli(KieliType.FI);
        koodiType.getMetadata().add(meta);

        KoodistoItemType koodistoItemType = new KoodistoItemType();

        if (koodistoUri == null) {
            koodistoItemType.setKoodistoUri(createKoodistoUri(fieldName));
        } else {
            koodistoItemType.setKoodistoUri(koodistoUri);
        }
        koodiType.setKoodisto(koodistoItemType);

        return koodiType;
    }

    protected List<KoodiType> createKoodiTypes(final String fieldName) {
        return createKoodiTypeList(createKoodiType(fieldName));
    }

    protected List<KoodiType> createKoodiTypeList(final KoodiType type) {
        List<KoodiType> types = Lists.<KoodiType>newArrayList();
        types.add(type);
        return types;
    }

    protected String createKoodistoUri(String koodistoUri) {
        Preconditions.checkNotNull(koodistoUri, "Koodisto uri cannot be null");
        return "koodisto_" + koodistoUri + "_uri";
    }
}