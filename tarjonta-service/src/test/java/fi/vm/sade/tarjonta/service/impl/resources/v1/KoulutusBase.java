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
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.security.SadeUserDetailsWrapper;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.service.business.impl.ContextDataServiceImpl;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.EntityConverterToKoulutusKorkeakouluRDTO;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusCommonV1RDTO;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusKorkeakouluDTOConverterToEntity;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusKuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.service.search.TarjontaSearchService;
import fi.vm.sade.tarjonta.service.types.HenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import org.joda.time.DateTime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author jani
 */
abstract class KoulutusBase {

    protected static final String USER_OID = "mock_test_user";
    protected static final String KOULUTUSOHELMA = "koulutusohjelma";
    protected static final Integer VUOSI = 2013;
    protected static final String KAUSI_KOODI_URI = "kausi_k";
    protected static final String LAAJUUSYKSIKKO = "laajuusyksikko";
    protected static final String LAAJUUSARVO = "laajuusarvo";
    protected static final String URI_KIELI_FI = "kieli_fi";
    protected static final String LOCALE_FI = "FI";
    protected static final String KOULUTUSKOODI = "koulutuskoodi";
    protected static final String KOULUTUSASTE = "koulutusaste";
    protected static final String KOULUTUSALA = "koulutusala";
    protected static final String OPINTOALA = "opintoala";
    protected static final String TUTKINTO = "tutkinto";
    protected static final String MAP_TUTKINTONIMIKE = "tutkintonimike";
    protected static final String MAP_OPETUSAIHEES = "aihees";
    protected static final String MAP_OPETUSKIELI = "opetuskieli";
    protected static final String MAP_POHJAKOULUTUS = "pohjakoulutus";
    protected static final String MAP_OPETUMUOTO = "opetusmuto";
    protected static final String MAP_AMMATTINIMIKE = "ammattinimike";
    protected static final String MAP_OPETUSAIKAS = "opetusaikas";
    protected static final String MAP_OPETUSPAIKKAS = "opetuspaikkas";
    protected static final String EQF = "EQF";
    protected static final String KOMO_OID = "komo_oid";
    protected static final String KOMOTO_OID = "komoto_oid";
    protected static final String ORGANISAATIO_OID = "organisaatio_oid";
    protected static final String ORGANISAATIO_NIMI = "organisaatio_nimi";
    protected static final String TUNNISTE = "tunniste_txt";
    protected static final String SUUNNITELTU_KESTO_VALUE = "10";
    protected static final String SUUNNITELTU_KESTO = "suunnteltu_kesto";
    protected static final String[] PERSON = {"henkilo_oid", "firstanames", "lastname", "Mr.", "oph@oph.fi", "12345678"};

    protected KoulutusResourceImplV1 instance;
    protected final DateTime DATE = new DateTime(VUOSI, 1, 1, 1, 1);
    protected OrganisaatioService organisaatioServiceMock;
    @Autowired
    protected OidService oidService;
    protected EntityConverterToKoulutusKorkeakouluRDTO converterToRDTO;
    protected KoulutusKorkeakouluDTOConverterToEntity convertToEntity;
    protected OrganisaatioDTO organisaatioDTO;
    @Autowired
    protected KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired
    protected KoulutusmoduuliDAO koulutusmoduuliDAO;

    protected IndexerResource solrIndexerMock;
    protected TarjontaKoodistoHelper tarjontaKoodistoHelperMock;
    protected KoulutusKuvausV1RDTO<KomoTeksti> komoKoulutusConverters;
    protected KoulutusKuvausV1RDTO<KomotoTeksti> komotoKoulutusConverters;
    protected KoulutusCommonV1RDTO commonConverter;
    protected PermissionChecker permissionChecker;
    protected ContextDataService contextDataService;
    protected KoodistoURI koodistoUri;
    protected KoulutusSisaltyvyysDAO koulutusSisaltyvyysDAO;
    protected TarjontaSearchService tarjontaSearchService;

    public void reload() throws OIDCreationException {

        Mockito.stub(oidService.get(TarjontaOidType.KOMO)).toReturn(KOMO_OID);
        Mockito.stub(oidService.get(TarjontaOidType.KOMOTO)).toReturn(KOMOTO_OID);

        setCurrentUser(USER_OID, getAuthority("APP_TARJONTA_CRUD", "test.user.oid.123"));

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
        organisaatioServiceMock = createMock(OrganisaatioService.class);
        tarjontaKoodistoHelperMock = createMock(TarjontaKoodistoHelper.class);
        solrIndexerMock = createMock(IndexerResource.class);
        permissionChecker = createMock(PermissionChecker.class);
        koodistoUri = createMock(KoodistoURI.class);
        contextDataService = new ContextDataServiceImpl();
        koulutusSisaltyvyysDAO = createMock(KoulutusSisaltyvyysDAO.class);
        tarjontaSearchService = createMock(TarjontaSearchService.class);

        //INIT DATA CONVERTERS
        converterToRDTO = new EntityConverterToKoulutusKorkeakouluRDTO();
        convertToEntity = new KoulutusKorkeakouluDTOConverterToEntity();
        instance = new KoulutusResourceImplV1();

        //SET VALUES TO INSTANCES
        Whitebox.setInternalState(convertToEntity, "oidService", oidService);

        Whitebox.setInternalState(instance, "organisaatioService", organisaatioServiceMock);
        Whitebox.setInternalState(instance, "koulutusmoduuliToteutusDAO", koulutusmoduuliToteutusDAO);
        Whitebox.setInternalState(instance, "koulutusmoduuliDAO", koulutusmoduuliDAO);
        Whitebox.setInternalState(instance, "solrIndexer", solrIndexerMock);
        Whitebox.setInternalState(instance, "permissionChecker", permissionChecker);
        Whitebox.setInternalState(instance, "converterToRDTO", converterToRDTO);
        Whitebox.setInternalState(instance, "convertToEntity", convertToEntity);
        Whitebox.setInternalState(instance, "contextDataService", contextDataService);
        Whitebox.setInternalState(instance, "koulutusSisaltyvyysDAO", koulutusSisaltyvyysDAO);
        Whitebox.setInternalState(instance, "tarjontaSearchService", tarjontaSearchService);

        //no need for replay or verify:
        Whitebox.setInternalState(converterToRDTO, "commonConverter", commonConverter);
        Whitebox.setInternalState(commonConverter, "organisaatioService", organisaatioServiceMock);
        Whitebox.setInternalState(commonConverter, "tarjontaKoodistoHelper", tarjontaKoodistoHelperMock);

        Whitebox.setInternalState(converterToRDTO, "komoKuvausConverters", komoKoulutusConverters);
        Whitebox.setInternalState(converterToRDTO, "komotoKuvausConverters", komotoKoulutusConverters);

        Whitebox.setInternalState(convertToEntity, "komoKuvausConverters", komoKoulutusConverters);
        Whitebox.setInternalState(convertToEntity, "komotoKuvausConverters", komotoKoulutusConverters);
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

    protected KoulutusKorkeakouluV1RDTO getKoulutus() {
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
        dto.getKoulutuksenAlkamisPvms().add(DATE.toDate());

        koodiUrisMap(dto.getTutkintonimikes(), URI_KIELI_FI, MAP_TUTKINTONIMIKE);
        koodiUrisMap(dto.getOpetusAikas(), URI_KIELI_FI, MAP_OPETUSAIKAS);
        koodiUrisMap(dto.getOpetusPaikkas(), URI_KIELI_FI, MAP_OPETUSPAIKKAS);
        koodiUrisMap(dto.getAihees(), URI_KIELI_FI, MAP_OPETUSAIHEES);
        koodiUrisMap(dto.getOpetuskielis(), URI_KIELI_FI, MAP_OPETUSKIELI);
        koodiUrisMap(dto.getOpetusmuodos(), URI_KIELI_FI, MAP_OPETUMUOTO);
        koodiUrisMap(dto.getAmmattinimikkeet(), URI_KIELI_FI, (MAP_AMMATTINIMIKE));
        koodiUrisMap(dto.getPohjakoulutusvaatimukset(), URI_KIELI_FI, MAP_POHJAKOULUTUS);

        dto.setSuunniteltuKestoTyyppi(toKoodiUri(SUUNNITELTU_KESTO));
        dto.setSuunniteltuKestoArvo(SUUNNITELTU_KESTO_VALUE);

        dto.getYhteyshenkilos().add(
                new fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi(PERSON[0], PERSON[1], PERSON[2],
                        PERSON[3], PERSON[4], PERSON[5], null,
                        HenkiloTyyppi.YHTEYSHENKILO));
        dto.setOpintojenLaajuusarvo(toKoodiUri(LAAJUUSARVO));
        dto.setOpintojenLaajuusyksikko(toKoodiUri(LAAJUUSYKSIKKO));

        return dto;
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

    protected static NimiV1RDTO teksti(NimiV1RDTO dto, final String nimi, final String kieli) {
        dto.getTekstis().put(kieli, nimi);
        return dto;
    }

    protected static KoodiV1RDTO meta(final KoodiV1RDTO dto, final String kieli, final KoodiV1RDTO metaValue) {
        dto.setMeta(Maps.<String, KoodiV1RDTO>newHashMap());
        return dto.getMeta().put(kieli, metaValue);
    }

    protected void koodiUrisMap(final KoodiUrisV1RDTO dto, final String kieliUri, final String fieldName) {
        meta(dto, kieliUri, toKoodiUri(fieldName));

        if (dto.getUris() == null) {
            dto.setUris(Maps.<String, Integer>newHashMap());
        }

        dto.getUris().put(toKoodiUriStr(fieldName), 1);
    }

    protected static String toKoodiUriStr(final String type) {
        return type + "_uri";
    }

    protected static KoodiV1RDTO toKoodiUri(final String type) {
        return new KoodiV1RDTO(type + "_uri", 1, null);
    }

    protected static String toNimiValue(final String value, String lang) {
        return value + "_" + lang;
    }

    protected void expectKausi() {
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

    protected void expectMetaUri(final String field) {
        expect(tarjontaKoodistoHelperMock.getKoodiByUri(field + "_uri#1")).andReturn(createKoodiType(field, "x" + field)).times(1);

        KoodiType koodiType = createKoodiType(field, "x");
        expect(tarjontaKoodistoHelperMock.getKoodi(field + "_uri", 1)).andReturn(koodiType).times(1);
        expect(tarjontaKoodistoHelperMock.getKoodiNimi(koodiType, new Locale(LOCALE_FI))).andReturn(field).times(1);

        KoodiType koodiLanguageFi = createKoodiType(URI_KIELI_FI, "fi");
        expect(tarjontaKoodistoHelperMock.convertKielikoodiToKoodiType(LOCALE_FI)).andReturn(koodiLanguageFi).times(1);

    }

    protected void expectMetaMapUris(final String field) {
        KoodiType koodiType = createKoodiType(field, "x" + field);
        expect(tarjontaKoodistoHelperMock.getKoodi(field + "_uri", 1)).andReturn(koodiType).times(1);
        expect(tarjontaKoodistoHelperMock.getKoodi(field + "_uri", 1)).andReturn(koodiType).times(1);
        expect(tarjontaKoodistoHelperMock.getKoodiNimi(koodiType, new Locale(LOCALE_FI))).andReturn(field).times(1);

        KoodiType koodiLanguageFi = createKoodiType(URI_KIELI_FI, "fi");
        expect(tarjontaKoodistoHelperMock.convertKielikoodiToKoodiType(LOCALE_FI)).andReturn(koodiLanguageFi).times(1);
    }

    protected void assertEqualDtoKoodi(final String field, final KoodiV1RDTO dto) {
        assertEqualDtoKoodi(field, dto, false);
    }

    protected void assertEqualDtoKoodi(final String field, final KoodiV1RDTO dto, boolean realUri) {
        assertNotNull("UiDTO : " + field, dto);
        assertNotNull("KoodiDTO : " + field, dto);
        assertEquals(field + "_uri", dto.getUri());
        assertEquals(new Integer(1), dto.getVersio());
        assertEquals(field, dto.getNimi());
        assertEquals("x" + field, dto.getArvo());
    }

    protected void assertEqualMetaDto(final String field, final KoodiUrisV1RDTO dto) {
        assertEquals("koodi uri", true, dto.getUris().containsKey(toKoodiUriStr(field)));
        assertEquals("koodi versio", true, dto.getUris().containsValue(new Integer(1)));

        assertEquals("meta data field", 1, dto.getMeta().size()); // currently not used in upload
        assertEquals(true, dto.getMeta().containsKey(field + "_uri"));
        KoodiV1RDTO get = dto.getMeta().get(field + "_uri");
        assertEquals(null, dto.getArvo());
        assertEquals(field + "_uri", get.getUri());
        assertEquals(new Integer(1), get.getVersio());
    }

}