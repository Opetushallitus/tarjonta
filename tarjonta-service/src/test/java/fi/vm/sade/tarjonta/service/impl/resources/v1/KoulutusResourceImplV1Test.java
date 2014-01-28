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
package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoItemType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
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
import fi.vm.sade.security.SadeUserDetailsWrapper;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusKuvausV1RDTO;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.EntityConverterToKoulutusKorkeakouluRDTO;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusCommonV1RDTO;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusKorkeakouluDTOConverterToEntity;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import java.util.Calendar;
import java.util.Collection;

import java.util.List;
import java.util.Locale;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

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
public class KoulutusResourceImplV1Test {

    private static final String KOULUTUSOHELMA = "koulutusohjelma";
    private static final Integer VUOSI = 2013;
    private static final String KAUSI_KOODI_URI = "kausi_k";
    private static final String LAAJUUSYKSIKKO = "laajuusyksikko";
    private static final String LAAJUUSARVO = "laajuusarvo";
    private static final String URI_KIELI_FI = "kieli_fi";
    private static final String LOCALE_FI = "FI";
    private static final String KOULUTUSKOODI = "koulutuskoodi";
    private static final String KOULUTUSASTE = "koulutusaste";
    private static final String KOULUTUSALA = "koulutusala";
    private static final String OPINTOALA = "opintoala";
    private static final String TUTKINTO = "tutkinto";
    private static final String MAP_TUTKINTONIMIKE = "tutkintonimike";
    private static final String MAP_AIHEES = "aihees";
    private static final String MAP_OPETUSKIELI = "opetuskieli";
    private static final String MAP_POHJAKOULUTUS = "pohjakoulutus";
    private static final String MAP_OPETUMUOTO = "opetusmuto";
    private static final String MAP_AMMATTINIMIKE = "ammattinimike";
    private static final String EQF = "EQF";
    private static final String KOMO_OID = "komo_oid";
    private static final String KOMOTO_OID = "komoto_oid";
    private static final String ORGANISAATIO_OID = "organisaatio_oid";
    private static final String ORGANISAATIO_NIMI = "organisaatio_nimi";
    private static final String TUNNISTE = "tunniste_txt";
    private static final String SUUNNITELTU_KESTO_VALUE = "10";
    private static final String SUUNNITELTU_KESTO = "suunnteltu_kesto";
    private static final String[] PERSON = {"henkilo_oid", "firstanames", "lastname", "Mr.", "oph@oph.fi", "12345678"};
    private KoulutusResourceImplV1 instance;
    private final DateTime DATE = new DateTime(VUOSI, 1, 1, 1, 1);
    private OrganisaatioService organisaatioServiceMock;
    private OIDService oidServiceMock;
    private ConversionService conversionServiceMock;
    private EntityConverterToKoulutusKorkeakouluRDTO converterToRDTO;
    private KoulutusKorkeakouluDTOConverterToEntity convertToKomoto;
    private ConvertEntityStub convertToEntityStub;
    private OrganisaatioDTO organisaatioDTO;
    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    private IndexerResource solrIndexerMock;
    private TarjontaKoodistoHelper tarjontaKoodistoHelperMock;
    private KoulutusKuvausV1RDTO<KomoTeksti> komoKoulutusConverters;
    private KoulutusKuvausV1RDTO<KomotoTeksti> komotoKoulutusConverters;
    private KoulutusCommonV1RDTO commonConverter;
    private PermissionChecker permissionChecker;
    private KoodistoURI koodistoUri;

    @Before
    public void setUp() {
        setCurrentUser("mock_test_user", getAuthority("APP_TARJONTA_CRUD", "test.user.oid.123"));

        //used in regexp kieli uri validation
        KoodistoURI.KOODISTO_KIELI_URI = "kieli";

        //INIT ORGANISATION DTO
        organisaatioDTO = new OrganisaatioDTO();
        organisaatioDTO.setOid(ORGANISAATIO_OID);
        organisaatioDTO.setNimi(new MonikielinenTekstiTyyppi());
        organisaatioDTO.getNimi().getTeksti().add(new MonikielinenTekstiTyyppi.Teksti(ORGANISAATIO_NIMI, LOCALE_FI));

        komotoKoulutusConverters = new KoulutusKuvausV1RDTO<KomotoTeksti>();
        komoKoulutusConverters = new KoulutusKuvausV1RDTO<KomoTeksti>();
        commonConverter = new KoulutusCommonV1RDTO();
        //CREATE MOCKS
        conversionServiceMock = createMock(ConversionService.class);
        organisaatioServiceMock = createMock(OrganisaatioService.class);
        oidServiceMock = createMock(OIDService.class);
        tarjontaKoodistoHelperMock = createMock(TarjontaKoodistoHelper.class);
        solrIndexerMock = createMock(IndexerResource.class);
        permissionChecker = createMock(PermissionChecker.class);
        koodistoUri = createMock(KoodistoURI.class);

        //INIT DATA CONVERTERS
        converterToRDTO = new EntityConverterToKoulutusKorkeakouluRDTO();
        convertToKomoto = new KoulutusKorkeakouluDTOConverterToEntity();
        instance = new KoulutusResourceImplV1();
        convertToEntityStub = new ConvertEntityStub();

        //SET VALUES TO INSTANCES
        Whitebox.setInternalState(convertToKomoto, "oidService", oidServiceMock);

        Whitebox.setInternalState(instance, "organisaatioService", organisaatioServiceMock);
        Whitebox.setInternalState(instance, "conversionService", conversionServiceMock);
        Whitebox.setInternalState(instance, "koulutusmoduuliToteutusDAO", koulutusmoduuliToteutusDAO);
        Whitebox.setInternalState(instance, "koulutusmoduuliDAO", koulutusmoduuliDAO);
        Whitebox.setInternalState(instance, "solrIndexer", solrIndexerMock);
        Whitebox.setInternalState(instance, "permissionChecker", permissionChecker);
        Whitebox.setInternalState(instance, "converterToRDTO", converterToRDTO);

        //no need for replay or verify:
        Whitebox.setInternalState(converterToRDTO, "commonConverter", commonConverter);
        Whitebox.setInternalState(commonConverter, "organisaatioService", organisaatioServiceMock);
        Whitebox.setInternalState(commonConverter, "tarjontaKoodistoHelper", tarjontaKoodistoHelperMock);

        Whitebox.setInternalState(converterToRDTO, "komoKuvausConverters", komoKoulutusConverters);
        Whitebox.setInternalState(converterToRDTO, "komotoKuvausConverters", komotoKoulutusConverters);

        Whitebox.setInternalState(convertToKomoto, "komoKuvausConverters", komoKoulutusConverters);
        Whitebox.setInternalState(convertToKomoto, "komotoKuvausConverters", komotoKoulutusConverters);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCreateAndLoadToteutus() throws ExceptionMessage {
        KoulutusKorkeakouluV1RDTO dto = new KoulutusKorkeakouluV1RDTO();
        /*
         * KOMO data fields:
         */
        teksti(dto.getKoulutusohjelma(), KOULUTUSOHELMA, URI_KIELI_FI);
        dto.getKoulutusohjelma().getTekstis().put(URI_KIELI_FI, toNimiValue("koulutusohjelma", URI_KIELI_FI));
        dto.getOrganisaatio().setOid(ORGANISAATIO_OID);
        dto.setKoulutusaste(toKoodiUri(KOULUTUSASTE));
        dto.setKoulutusala(toKoodiUri(KOULUTUSALA));
        dto.setOpintoala(toKoodiUri(OPINTOALA));
        dto.setTutkinto(toKoodiUri(TUTKINTO));

        dto.setEqf(toKoodiUri(EQF));
        dto.setTila(TarjontaTila.JULKAISTU);
        dto.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.TUTKINTO);
        dto.setTunniste(TUNNISTE);
        dto.setHinta(1.11);
        dto.setOpintojenMaksullisuus(Boolean.TRUE);
        dto.setKoulutuskoodi(toKoodiUri(KOULUTUSKOODI));
        dto.setKoulutusasteTyyppi(KoulutusasteTyyppi.KORKEAKOULUTUS);
        dto.getKoulutuksenAlkamisPvms().add(DATE.toDate());

        koodiUrisMap(dto.getTutkintonimikes(), URI_KIELI_FI, MAP_TUTKINTONIMIKE);
        koodiUrisMap(dto.getAihees(), URI_KIELI_FI, MAP_AIHEES);
        koodiUrisMap(dto.getOpetuskielis(), URI_KIELI_FI, MAP_OPETUSKIELI);
        koodiUrisMap(dto.getOpetusmuodos(), URI_KIELI_FI, MAP_OPETUMUOTO);
        koodiUrisMap(dto.getAmmattinimikkeet(), URI_KIELI_FI, (MAP_AMMATTINIMIKE));
        koodiUrisMap(dto.getPohjakoulutusvaatimukset(), URI_KIELI_FI, MAP_POHJAKOULUTUS);

        dto.setSuunniteltuKestoTyyppi(toKoodiUri(SUUNNITELTU_KESTO));
        dto.setSuunniteltuKestoArvo(SUUNNITELTU_KESTO_VALUE);

        dto.getYhteyshenkilos().add(new YhteyshenkiloTyyppi(PERSON[0], PERSON[1], PERSON[2], PERSON[3], PERSON[4], PERSON[5], null, HenkiloTyyppi.YHTEYSHENKILO));
        dto.setOpintojenLaajuus(toKoodiUri(LAAJUUSARVO));
        dto.setOpintojenLaajuusyksikko(toKoodiUri(LAAJUUSYKSIKKO));

        //EXPECT
        expect(organisaatioServiceMock.findByOid(ORGANISAATIO_OID)).andReturn(organisaatioDTO).times(3);
        expect(conversionServiceMock.convert(isA(KoulutusKorkeakouluV1RDTO.class), eq(KoulutusmoduuliToteutus.class))).andStubDelegateTo(convertToEntityStub);
        //the calls of the OidServices must be in correct order!
        expect(oidServiceMock.newOid(NodeClassCode.TEKN_5)).andReturn(KOMO_OID);
        expect(oidServiceMock.newOid(NodeClassCode.TEKN_5)).andReturn(KOMOTO_OID);

        permissionChecker.checkCreateKoulutus(ORGANISAATIO_OID);
        permissionChecker.checkUpdateKoulutusByTarjoajaOid(ORGANISAATIO_OID);

        /* 
         * KOODISTO DATA CALLS IN CORRECT CALL ORDER
         * 1th round, convert to entity 
         */
        expectKausi();
        expectMetaUri(KOULUTUSKOODI);
        expectMetaUri(TUTKINTO);
        expectMetaUri(LAAJUUSARVO);
        expectMetaUri(LAAJUUSYKSIKKO);
        expectMetaUri(KOULUTUSASTE);
        expectMetaUri(KOULUTUSALA);
        expectMetaUri(OPINTOALA);
        expectMetaMapUris(MAP_TUTKINTONIMIKE);
        expectMetaUri(EQF);
        expectMetaMapUris(MAP_AIHEES);
        expectMetaMapUris(MAP_OPETUSKIELI);
        expectMetaMapUris(MAP_OPETUMUOTO);
        expectMetaMapUris(MAP_POHJAKOULUTUS);
        expectMetaUri(SUUNNITELTU_KESTO);
        expectMetaMapUris(MAP_AMMATTINIMIKE);

        /* 2nd round, convert to dto */
        expectKausi();
        expectMetaUri(KOULUTUSKOODI);
        expectMetaUri(TUTKINTO);
        expectMetaUri(LAAJUUSARVO);
        expectMetaUri(LAAJUUSYKSIKKO);
        expectMetaUri(KOULUTUSASTE);
        expectMetaUri(KOULUTUSALA);
        expectMetaUri(OPINTOALA);
        expectMetaMapUris(MAP_TUTKINTONIMIKE);
        expectMetaUri(EQF);
        expectMetaMapUris(MAP_AIHEES);
        expectMetaMapUris(MAP_OPETUSKIELI);
        expectMetaMapUris(MAP_OPETUMUOTO);
        expectMetaMapUris(MAP_POHJAKOULUTUS);
        expectMetaUri(SUUNNITELTU_KESTO);
        expectMetaMapUris(MAP_AMMATTINIMIKE);

        //  expectKoulutusohjelmaUris(KOULUTUSOHELMA);

        /* REPLAY */
        replay(oidServiceMock);
        replay(organisaatioServiceMock);
        replay(conversionServiceMock);
        replay(tarjontaKoodistoHelperMock);

        /*
         * INSERT KORKEAKOULU TO DB
         */
        instance.postKorkeakouluKoulutus(dto);
        /*
         * LOAD KORKEAKOULU DTO FROM DB
         */
        final ResultV1RDTO result = instance.findByOid(KOMOTO_OID, true, "FI");
        KoulutusKorkeakouluV1RDTO result1 = (KoulutusKorkeakouluV1RDTO) result.getResult();
        assertLoadData(result1);

        verify(oidServiceMock);
        verify(organisaatioServiceMock);
        verify(conversionServiceMock);
        verify(tarjontaKoodistoHelperMock);
    }

    private void assertLoadData(final KoulutusKorkeakouluV1RDTO result) {
        assertNotNull(result);

        assertEquals(KOMOTO_OID, result.getOid());
        assertEquals(ORGANISAATIO_OID, result.getOrganisaatio().getOid());
        assertEquals(ORGANISAATIO_NIMI, result.getOrganisaatio().getNimi());

        assertEquals(KoulutusasteTyyppi.KORKEAKOULUTUS, result.getKoulutusasteTyyppi());

        final String key = URI_KIELI_FI + "_uri";

        assertNotNull(KOULUTUSOHELMA, result.getKoulutusohjelma().getTekstis().get(key));
        assertEqualDtoKoodi(KOULUTUSASTE, result.getKoulutusaste());
        assertEqualDtoKoodi(KOULUTUSALA, result.getKoulutusala());
        assertEqualDtoKoodi(OPINTOALA, result.getOpintoala());
        assertEqualDtoKoodi(EQF, result.getEqf());
        assertEqualDtoKoodi(KOULUTUSKOODI, result.getKoulutuskoodi());
        assertEqualDtoKoodi(LAAJUUSARVO, result.getOpintojenLaajuus());
        assertEqualDtoKoodi(LAAJUUSYKSIKKO, result.getOpintojenLaajuusyksikko());
        assertEqualDtoKoodi(TUTKINTO, result.getTutkinto());

        assertEquals(TarjontaTila.JULKAISTU, result.getTila());
        assertEquals(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.TUTKINTO, result.getKoulutusmoduuliTyyppi());
        assertEquals(KoulutusasteTyyppi.KORKEAKOULUTUS, result.getKoulutusasteTyyppi());
        assertEquals(TUNNISTE, result.getTunniste());
        assertEquals(new Double(1.11), result.getHinta());
        assertEquals(Boolean.TRUE, result.getOpintojenMaksullisuus());
        assertEquals((DateUtils.truncate(DATE.toDate(), Calendar.DATE)), result.getKoulutuksenAlkamisPvms().iterator().next());
        assertEqualDtoKoodi(KAUSI_KOODI_URI, result.getKoulutuksenAlkamiskausi(), true);
        assertEquals(VUOSI, result.getKoulutuksenAlkamisvuosi());

        assertEqualMetaDto(MAP_AIHEES, result.getAihees());
        assertEqualMetaDto(MAP_OPETUSKIELI, result.getOpetuskielis());
        assertEqualMetaDto(MAP_OPETUMUOTO, result.getOpetusmuodos());
        assertEqualMetaDto(MAP_POHJAKOULUTUS, result.getPohjakoulutusvaatimukset());
        assertEqualMetaDto(MAP_AMMATTINIMIKE, result.getAmmattinimikkeet());
        assertEqualMetaDto(MAP_TUTKINTONIMIKE, result.getTutkintonimikes());

        assertEquals(SUUNNITELTU_KESTO_VALUE, result.getSuunniteltuKestoArvo());
        assertEquals(SUUNNITELTU_KESTO + "_uri", result.getSuunniteltuKestoTyyppi().getUri());
        assertEquals(new Integer(1), result.getSuunniteltuKestoTyyppi().getVersio());
        YhteyshenkiloTyyppi next = result.getYhteyshenkilos().iterator().next();
        assertEquals(PERSON[0], next.getHenkiloOid());
        assertEquals(PERSON[1], next.getEtunimet());
        assertEquals(PERSON[2], next.getSukunimi());
        assertEquals(PERSON[3], next.getTitteli());
        assertEquals(PERSON[4], next.getSahkoposti());
        assertEquals(PERSON[5], next.getPuhelin());
        assertEquals(HenkiloTyyppi.YHTEYSHENKILO, next.getHenkiloTyyppi());

    }

    private static String toKoodiUriStr(final String type) {
        return type + "_uri";
    }

    private static KoodiV1RDTO toKoodiUri(final String type) {
        return new KoodiV1RDTO(type + "_uri", 1, null);
    }

    private static KoodiV1RDTO toMetaValue(final String value, String lang) {
        return new KoodiV1RDTO(lang, 1, value);
    }

    private static String toNimiValue(final String value, String lang) {
        return value + "_" + lang;
    }

    private void expectKausi() {
        KoodiType kausiKoodiType = createKoodiType(KAUSI_KOODI_URI, "x" + KAUSI_KOODI_URI);
        expect(tarjontaKoodistoHelperMock.getKoodiByUri(KAUSI_KOODI_URI + "#1")).andReturn(kausiKoodiType).times(1);
        expect(tarjontaKoodistoHelperMock.getKoodi(KAUSI_KOODI_URI + "_uri", 1)).andReturn(kausiKoodiType).times(1);
        expect(tarjontaKoodistoHelperMock.getKoodiNimi(kausiKoodiType, new Locale(LOCALE_FI))).andReturn(KAUSI_KOODI_URI).times(1);

        KoodiType koodiLanguageFi = createKoodiType(URI_KIELI_FI, "fi");
        expect(tarjontaKoodistoHelperMock.convertKielikoodiToKoodiType(LOCALE_FI)).andReturn(koodiLanguageFi).times(1);
        expect(tarjontaKoodistoHelperMock.getKoodiNimi(koodiLanguageFi, new Locale(LOCALE_FI))).andReturn(KAUSI_KOODI_URI).times(1);
        expect(tarjontaKoodistoHelperMock.convertKielikoodiToKoodiType(URI_KIELI_FI)).andReturn(koodiLanguageFi);

        expect(tarjontaKoodistoHelperMock.getKoodi(URI_KIELI_FI + "_uri", 1)).andReturn(koodiLanguageFi).times(2);
        expect(tarjontaKoodistoHelperMock.convertKielikoodiToKoodiType(LOCALE_FI)).andReturn(koodiLanguageFi).times(1);
    }

    private void expectMetaUri(final String field) {
        expect(tarjontaKoodistoHelperMock.getKoodiByUri(field + "_uri#1")).andReturn(createKoodiType(field, "x" + field)).times(1);

        KoodiType koodiType = createKoodiType(field, "x");
        expect(tarjontaKoodistoHelperMock.getKoodi(field + "_uri", 1)).andReturn(koodiType).times(1);
        expect(tarjontaKoodistoHelperMock.getKoodiNimi(koodiType, new Locale(LOCALE_FI))).andReturn(field).times(1);

        KoodiType koodiLanguageFi = createKoodiType(URI_KIELI_FI, "fi");
        expect(tarjontaKoodistoHelperMock.convertKielikoodiToKoodiType(LOCALE_FI)).andReturn(koodiLanguageFi).times(1);

    }

    private void expectMetaMapUris(final String field) {
        KoodiType koodiType = createKoodiType(field, "x" + field);
        expect(tarjontaKoodistoHelperMock.getKoodi(field + "_uri", 1)).andReturn(koodiType).times(1);
        expect(tarjontaKoodistoHelperMock.getKoodi(field + "_uri", 1)).andReturn(koodiType).times(1);
        expect(tarjontaKoodistoHelperMock.getKoodiNimi(koodiType, new Locale(LOCALE_FI))).andReturn(field).times(1);

        KoodiType koodiLanguageFi = createKoodiType(URI_KIELI_FI, "fi");
        expect(tarjontaKoodistoHelperMock.convertKielikoodiToKoodiType(LOCALE_FI)).andReturn(koodiLanguageFi).times(1);

    }

    private void expectKoulutusohjelmaUris(final String field) {
        expect(tarjontaKoodistoHelperMock.getKoodiByUri(URI_KIELI_FI)).andReturn(createKoodiType(field + "_uri_fi_meta", "x" + field)).times(1);
        expect(tarjontaKoodistoHelperMock.getKoodiNimi(URI_KIELI_FI, new Locale("FI"))).andReturn(field + "_suomi");
    }

    private void assertEqualDtoKoodi(final String field, final KoodiV1RDTO dto) {
        assertEqualDtoKoodi(field, dto, false);
    }

    private void assertEqualDtoKoodi(final String field, final KoodiV1RDTO dto, boolean realUri) {
        assertNotNull("UiDTO : " + field, dto);
        assertNotNull("KoodiDTO : " + field, dto);
        assertEquals(field + "_uri", dto.getUri());
        assertEquals(new Integer(1), dto.getVersio());
        assertEquals(field, dto.getNimi());
        assertEquals("x" + field, dto.getArvo());
    }

    private void assertEqualMetaDto(final String field, final KoodiUrisV1RDTO dto) {
        assertEquals("koodi uri", true, dto.getUris().containsKey(toKoodiUriStr(field)));
        assertEquals("koodi versio", true, dto.getUris().containsValue(new Integer(1)));

        assertEquals("meta data field", 1, dto.getMeta().size()); // currently not used in upload
        assertEquals(true, dto.getMeta().containsKey(field + "_uri"));
        KoodiV1RDTO get = dto.getMeta().get(field + "_uri");
        assertEquals(null, dto.getArvo());
        assertEquals(field + "_uri", get.getUri());
        assertEquals(new Integer(1), get.getVersio());
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
            return (T) convertToKomoto.convert((KoulutusKorkeakouluV1RDTO) o);
        }

        @Override
        public Object convert(Object o, TypeDescriptor td, TypeDescriptor td1) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    protected KoodiType createKoodiType(final String fieldName, String arvo) {
        return createKoodiType(fieldName, null, arvo);
    }

    protected KoodiType createKoodiType(final String fieldName, final String koodistoUri, String arvo) {
        KoodiType koodiType = new KoodiType();
        koodiType.setKoodiUri(fieldName + "_uri");
        koodiType.setVersio(1);
        koodiType.setKoodiArvo(arvo);
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
        return createKoodiTypeList(createKoodiType(fieldName, "x"));
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

    private static NimiV1RDTO teksti(NimiV1RDTO dto, final String nimi, final String kieli) {
        dto.getTekstis().put(kieli, nimi);
        return dto;
    }

    private static KoodiV1RDTO meta(final KoodiV1RDTO dto, final String kieli, final KoodiV1RDTO metaValue) {
        dto.setMeta(Maps.<String, KoodiV1RDTO>newHashMap());
        return dto.getMeta().put(kieli, metaValue);
    }

    private void koodiUrisMap(final KoodiUrisV1RDTO dto, final String kieliUri, final String fieldName) {
        meta(dto, kieliUri, toKoodiUri(fieldName));
        dto.getUris().put(toKoodiUriStr(fieldName), 1);
    }

    protected final List<GrantedAuthority> getAuthority(String appPermission, String oid) {
        GrantedAuthority orgAuthority = new SimpleGrantedAuthority(String.format("%s", appPermission));
        GrantedAuthority roleAuthority = new SimpleGrantedAuthority(String.format("%s_%s", appPermission, oid));
        return Lists.newArrayList(orgAuthority, roleAuthority);
    }

    protected final void setCurrentUser(final String oid, final List<GrantedAuthority> grantedAuthorities) {
        SadeUserDetailsWrapper sadeUserDetailsWrapper = new SadeUserDetailsWrapper(new UserDetails() {

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return grantedAuthorities;
            }

            @Override
            public String getPassword() {
                return "no_password";
            }

            @Override
            public String getUsername() {
                return oid;
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return false;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return false;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        }, "FI");
        Authentication auth = new TestingAuthenticationToken(sadeUserDetailsWrapper, null, grantedAuthorities);
        setAuthentication(auth);
    }

    protected final void setAuthentication(Authentication auth) {
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
