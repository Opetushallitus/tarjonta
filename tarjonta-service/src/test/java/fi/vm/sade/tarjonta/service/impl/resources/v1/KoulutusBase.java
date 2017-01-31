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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fi.vm.sade.koodisto.service.types.common.*;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.security.SadeUserDetailsWrapper;
import fi.vm.sade.tarjonta.TestUtilityBase;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.koodisto.OppilaitosKoodiRelations;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.service.business.impl.ContextDataServiceImpl;
import fi.vm.sade.tarjonta.service.impl.aspects.KoulutusPermissionService;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.*;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidationMessages;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidator;
import fi.vm.sade.tarjonta.service.resources.v1.LinkingV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.service.search.HakukohdeSearchService;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.service.search.KoulutusSearchService;
import fi.vm.sade.tarjonta.service.types.HenkiloTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.joda.time.DateTime;
import org.powermock.reflect.Whitebox;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 *
 * @author jani
 */
abstract class KoulutusBase extends TestUtilityBase {

    protected static final String USER_OID = "mock_test_user";
    protected static final String KOULUTUSOHJELMA = "koulutusohjelma";
    protected static final Integer VUOSI = 2013;
    protected static final String KAUSI_KOODI_URI = "kausi_k";
    protected static final String LAAJUUSYKSIKKO = "laajuusyksikko";
    protected static final String LAAJUUSARVO = "laajuusarvo";
    protected static final String URI_KIELI_FI = "kieli_fi";
    protected static final String LOCALE_FI = "FI";
    protected static final String OPPILAITOSTYYPPI = "oppilaitostyyppi";
    protected static final String KOULUTUSKOODI = "koulutuskoodi";
    protected static final String KOULUTUSASTE = "koulutusaste";
    protected static final String KOULUTUSLAJI = "koulutuslaji";
    protected static final String TUTKINTONIMIKE = "tutkintonimike";
    protected static final String KOULUTUSALA = "koulutusala";
    protected static final String OPINTOALA = "opintoala";
    protected static final String TUTKINTO = "tutkinto";
    protected static final String KOULUTUSTYYPPI = "koulutustyyppi";
    protected static final String POHJAKOULUTUSVAATIMUS = "pohjakoulutusvaatimus";
    protected static final String MAP_TUTKINTONIMIKE = "tutkintonimike";
    protected static final String MAP_OPETUSAIHEES = "aihees";
    protected static final String MAP_OPETUSKIELI = "opetuskieli";
    protected static final String MAP_POHJAKOULUTUS = "pohjakoulutus";
    protected static final String MAP_OPETUMUOTO = "opetusmuoto";
    protected static final String MAP_AMMATTINIMIKE = "ammattinimike";
    protected static final String MAP_OPETUSAIKAS = "opetusaikas";
    protected static final String MAP_OPETUSPAIKKAS = "opetuspaikkas";
    protected static final String EQF = "eqf";
    protected static final String NQF = "NQF";
    protected static final String KOMO_PARENT_OID = "komo_parent_oid";
    protected static final String KOMO_CHILD_OID = "komo_child_oid";
    protected static final String KOMO_OID = "komo_oid";
    protected static final String KOMOTO_OID = "komoto_oid";
    protected static final String ORGANISATION_OID = "organisaatio_oid";
    protected static final String ORGANISAATIO_NIMI = "organisaatio_nimi";
    protected static final String TUNNISTE = "tunniste_txt";
    protected static final String SUUNNITELTU_KESTO_VALUE = "10";
    protected static final String SUUNNITELTU_KESTO_TYYPPI = "suunnteltu_kesto";
    protected static final String[] PERSON = {"henkilo_oid", "Etunimi Etunimi Sukunimi Sukunimi", "Mr.", "oph@oph.fi", "12345678"};
    protected static final String KOULUTUKSENLAAJUUS = "koulutuksenlaajuus";

    protected KoulutusResourceImplV1 instance;
    protected final DateTime DATE = new DateTime(VUOSI, 1, 1, 1, 1);
    protected OrganisaatioService organisaatioServiceMock;
    protected KoulutusPermissionService koulutusPermissionServiceMock;
    protected EntityConverterToRDTO converterToRDTO;
    protected KoulutusDTOConverterToEntity convertToEntity;
    protected OrganisaatioRDTO organisaatioDTO;
    protected IndexerResource indexerResourceMock;
    protected TarjontaKoodistoHelper tarjontaKoodistoHelperMock;
    protected KoulutusKuvausV1RDTO<KomoTeksti> komoKoulutusConverters;
    protected KoulutusKuvausV1RDTO<KomotoTeksti> komotoKoulutusConverters;
    protected KoulutusCommonConverter commonConverter;
    protected PermissionChecker permissionChecker;
    protected ContextDataService contextDataService;
    protected KoodistoURI koodistoUri;
    protected KoulutusSisaltyvyysDAO koulutusSisaltyvyysDAO;
    protected KoulutusSearchService koulutusSearchService;
    protected HakukohdeSearchService hakukohdeSearchService;
    protected OppilaitosKoodiRelations oppilaitosKoodiRelations;
    protected LinkingV1Resource linkingV1Resource;
    protected PublicationDataService publicationDataService;

    public void reload() throws OIDCreationException {

        stub(oidService.get(TarjontaOidType.KOMO)).toReturn(KOMO_OID);
        stub(oidService.get(TarjontaOidType.KOMOTO)).toReturn(KOMOTO_OID);

        //used in regexp kieli uri validation
        KoodistoURI.KOODISTO_KIELI_URI = "kieli";

        //INIT ORGANISATION DTO
        organisaatioDTO = new OrganisaatioRDTO();
        organisaatioDTO.setOid(ORGANISATION_OID);
        organisaatioDTO.setNimi(ImmutableMap.of("fi", ORGANISAATIO_NIMI));

        komotoKoulutusConverters = new KoulutusKuvausV1RDTO<KomotoTeksti>();
        komoKoulutusConverters = new KoulutusKuvausV1RDTO<KomoTeksti>();
        commonConverter = new KoulutusCommonConverter();
        //CREATE MOCKS
        koulutusPermissionServiceMock = mock(KoulutusPermissionService.class);
        tarjontaKoodistoHelperMock = mock(TarjontaKoodistoHelper.class);
        indexerResourceMock = mock(IndexerResource.class);
        permissionChecker = mock(PermissionChecker.class);
        doNothing().when(permissionChecker).checkCreateKoulutus(anyString());
        doNothing().when(permissionChecker).checkUpdateKoulutusByTarjoajaOid(anyString());

        koodistoUri = createMock(KoodistoURI.class);
        contextDataService = new ContextDataServiceImpl();
        koulutusSisaltyvyysDAO = mock(KoulutusSisaltyvyysDAO.class);
        koulutusSearchService = mock(KoulutusSearchService.class);
        hakukohdeSearchService = mock(HakukohdeSearchService.class);
        publicationDataService = mock(PublicationDataService.class);
        KoulutusImplicitDataPopulator dataPopulator = new KoulutusImplicitDataPopulator();
        Whitebox.setInternalState(dataPopulator, "koodiService", KoulutusDTOConverterToEntityTest.mockKoodiService(null));
        Whitebox.setInternalState(dataPopulator, "koulutusmoduuliToteutusDAO", koulutusmoduuliToteutusDAO);
        Whitebox.setInternalState(dataPopulator, "contextDataService", contextDataService);

        //INIT DATA CONVERTERS
        converterToRDTO = new EntityConverterToRDTO();
        Whitebox.setInternalState(dataPopulator, "converterToRDTO", converterToRDTO);
        convertToEntity = new KoulutusDTOConverterToEntity();
        instance = new KoulutusResourceImplV1();
        //SET VALUES TO INSTANCES

        Whitebox.setInternalState(convertToEntity, "oidService", oidService);
        initMockInstanceInternalStates();

        //no need for replay or verify:
        Whitebox.setInternalState(instance, "koulutusmoduuliToteutusDAO", koulutusmoduuliToteutusDAO);
        Whitebox.setInternalState(instance, "koulutusmoduuliDAO", koulutusmoduuliDAO);
        Whitebox.setInternalState(instance, "oppiaineDAO", oppiaineDAO);
        Whitebox.setInternalState(instance, "koulutusPermissionService", koulutusPermissionServiceMock);
        Whitebox.setInternalState(instance, "publicationDataService", publicationDataService);
        Whitebox.setInternalState(instance, "koulutusImplicitDataPopulator", dataPopulator);

        Whitebox.setInternalState(converterToRDTO, "commonConverter", commonConverter);
        Whitebox.setInternalState(commonConverter, "organisaatioService", organisaatioServiceMock);
        Whitebox.setInternalState(commonConverter, "tarjontaKoodistoHelper", tarjontaKoodistoHelperMock);

        Whitebox.setInternalState(converterToRDTO, "komoKuvausConverters", komoKoulutusConverters);
        Whitebox.setInternalState(converterToRDTO, "komotoKuvausConverters", komotoKoulutusConverters);
        Whitebox.setInternalState(converterToRDTO, "koulutusmoduuliDAO", koulutusmoduuliDAO);
        Whitebox.setInternalState(converterToRDTO, "koulutusSisaltyvyysDAO", koulutusSisaltyvyysDAO);
        Whitebox.setInternalState(converterToRDTO, "dataPopulator", new KoulutusImplicitDataPopulator());

        Whitebox.setInternalState(convertToEntity, "komoKuvausConverters", komoKoulutusConverters);
        Whitebox.setInternalState(convertToEntity, "komotoKuvausConverters", komotoKoulutusConverters);
        Whitebox.setInternalState(convertToEntity, "commonConverter", commonConverter);
        Whitebox.setInternalState(convertToEntity, "koulutusmoduuliDAO", koulutusmoduuliDAO);
        Whitebox.setInternalState(convertToEntity, "oppiaineDAO", oppiaineDAO);
        Whitebox.setInternalState(convertToEntity, "koulutusmoduuliToteutusDAO", koulutusmoduuliToteutusDAO);

        Whitebox.setInternalState(instance, "converterToRDTO", converterToRDTO);
        Whitebox.setInternalState(instance, "convertToEntity", convertToEntity);
    }

    protected void createJoinedParentAndChildKomos(KoulutusasteTyyppi tyyppi) {
        // Create almost a real life version of the parent komo:
        // - The parent komo should have all the same fields set as in production env. 
        // - Update the data to match data model in production env!!!
        //
        final Koulutusmoduuli parent = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO, KOMO_PARENT_OID, tyyppi);
        parent.setEqfUri(null);
        parent.setKoulutusasteUri(toKoodiUriStrVersion(KOULUTUSASTE));
        parent.setKoulutusUri(toKoodiUriStrVersion(KOULUTUSKOODI));
        parent.setKoulutusalaUri(toKoodiUriStrVersion(KOULUTUSALA));
        parent.setOpintoalaUri(toKoodiUriStrVersion(OPINTOALA));
        parent.setKoulutusohjelmaUri(null);
        parent.setOpintojenLaajuus(toKoodiUriStrVersion(LAAJUUSYKSIKKO), toKoodiUriStrVersion(LAAJUUSARVO));
        parent.setNqfUri(null);
        parent.setOmistajaOrganisaatioOid(null);
        parent.setTutkintoUri(null);
        parent.setUlkoinenTunniste(null);
        parent.setNimi(null);
        parent.setTila(TarjontaTila.JULKAISTU);
        parent.setOppilaitostyyppi("|oppilaitostyypp_xxx#1|");
        Map<KomoTeksti, MonikielinenTeksti> map = Maps.newHashMap();
        addKuvaus(map, KomoTeksti.TAVOITTEET);
        addKuvaus(map, KomoTeksti.KOULUTUKSEN_RAKENNE);
        addKuvaus(map, KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET);
        koulutusmoduuliDAO.insert(parent);

        // Create almost a real life version of the child komo:
        // - The child komo should have all the same fields set as in production env. 
        // - Update the data to match data model in production env!!!
        //
        final Koulutusmoduuli child = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA, KOMO_CHILD_OID, tyyppi);
        child.setEqfUri(toKoodiUriStrVersion(EQF));
        child.setKoulutusasteUri(null);
        child.setKoulutusUri(toKoodiUriStrVersion(KOULUTUSKOODI));
        child.setKoulutusalaUri(null);

        switch (tyyppi) {
            case LUKIOKOULUTUS:
                child.setKoulutusohjelmaUri(null);
                child.setOsaamisalaUri(null);
                child.setLukiolinjaUri(toKoodiUriStrVersion(KOULUTUSOHJELMA));
                break;
            default:
                child.setLukiolinjaUri(null);
                child.setKoulutusohjelmaUri(toKoodiUriStrVersion(KOULUTUSOHJELMA));
                child.setOsaamisalaUri(toKoodiUriStrVersion(KOULUTUSOHJELMA));
                break;
        }
        child.setOpintojenLaajuus(null, null);
        child.setNqfUri(toKoodiUriStrVersion(NQF));
        child.setOmistajaOrganisaatioOid(null);
        child.setTutkintoUri(null);
        child.setUlkoinenTunniste(null);
        child.setNimi(null);
        child.setTila(TarjontaTila.JULKAISTU);
        child.setOppilaitostyyppi(null);
        koulutusmoduuliDAO.insert(child);

        final KoulutusSisaltyvyys s = new KoulutusSisaltyvyys(parent, child, KoulutusSisaltyvyys.ValintaTyyppi.ALL_OFF);
        parent.addSisaltyvyys(s);
        KoulutusSisaltyvyysDAO.insert(s);
    }

    protected void initMockInstanceInternalStates() {
        setCurrentUser(USER_OID, getAuthority("APP_TARJONTA_CRUD", "test.user.oid.123"));

        contextDataService = new ContextDataServiceImpl();

        organisaatioServiceMock = mock(OrganisaatioService.class);
        when(organisaatioServiceMock.findByOid(ORGANISATION_OID)).thenReturn(organisaatioDTO);

        indexerResourceMock = mock(IndexerResource.class);
        permissionChecker = mock(PermissionChecker.class);
        doNothing().when(permissionChecker).checkCreateKoulutus(anyString());
        doNothing().when(permissionChecker).checkUpdateKoulutusByTarjoajaOid(anyString());
        koodistoUri = mock(KoodistoURI.class);
        koulutusSisaltyvyysDAO = mock(KoulutusSisaltyvyysDAO.class);
        koulutusSearchService = mock(KoulutusSearchService.class);
        oppilaitosKoodiRelations = mock(OppilaitosKoodiRelations.class);
        linkingV1Resource = mock(LinkingV1Resource.class);

        //SET VALUES TO INSTANCES
        Whitebox.setInternalState(instance, "organisaatioService", organisaatioServiceMock);
        Whitebox.setInternalState(instance, "indexerResource", indexerResourceMock);
        Whitebox.setInternalState(instance, "permissionChecker", permissionChecker);
        Whitebox.setInternalState(instance, "contextDataService", contextDataService);
        Whitebox.setInternalState(instance, "koulutusSisaltyvyysDAO", koulutusSisaltyvyysDAO);
        Whitebox.setInternalState(instance, "koulutusSearchService", koulutusSearchService);
        Whitebox.setInternalState(instance, "hakukohdeSearchService", hakukohdeSearchService);
        Whitebox.setInternalState(instance, "hakukohdeDAO", hakukohdeDAO);
        Whitebox.setInternalState(instance, "oppilaitosKoodiRelations", oppilaitosKoodiRelations);
        Whitebox.setInternalState(instance, "linkingV1Resource", linkingV1Resource);

        KoulutusValidator validatorMock = mock(KoulutusValidator.class);
        when(validatorMock.validateOrganisation(
                any(OrganisaatioV1RDTO.class), any(ResultV1RDTO.class), any(KoulutusValidationMessages.class),
                any(KoulutusValidationMessages.class))
        ).thenReturn(true);
        Whitebox.setInternalState(instance, "koulutusValidator", validatorMock);
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

        dto.setKoulutusohjelma(teksti(KOULUTUSOHJELMA, URI_KIELI_FI));
        dto.getKoulutusohjelma().getTekstis().put(URI_KIELI_FI, toNimiValue("koulutusohjelma", URI_KIELI_FI));
        dto.setOrganisaatio(new OrganisaatioV1RDTO(ORGANISATION_OID));
        dto.setTila(TarjontaTila.JULKAISTU);
        dto.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.TUTKINTO);
        dto.setTunniste(TUNNISTE);
        dto.setHinta(1.11);
        dto.setOpintojenMaksullisuus(Boolean.TRUE);
        dto.setKoulutuskoodi(toKoodiUri(KOULUTUSKOODI));
        dto.setKoulutuksenAlkamisPvms(Sets.newHashSet(DATE.toDate()));
        dto.setOpetusTarjoajat(new HashSet<String>());
        dto.setOpetusJarjestajat(new HashSet<String>());

        dto.setOpetusAikas(koodiUrisMap(URI_KIELI_FI, MAP_OPETUSAIKAS));
        dto.setOpetusPaikkas(koodiUrisMap(URI_KIELI_FI, MAP_OPETUSPAIKKAS));
        dto.setAihees(koodiUrisMap(URI_KIELI_FI, MAP_OPETUSAIHEES));
        dto.setOpetuskielis(koodiUrisMap(URI_KIELI_FI, MAP_OPETUSKIELI));
        dto.setOpetusmuodos(koodiUrisMap(URI_KIELI_FI, MAP_OPETUMUOTO));
        dto.setAmmattinimikkeet(koodiUrisMap(URI_KIELI_FI, MAP_AMMATTINIMIKE));
        dto.setPohjakoulutusvaatimukset(koodiUrisMap(URI_KIELI_FI, MAP_POHJAKOULUTUS));

        dto.setSuunniteltuKestoTyyppi(toKoodiUri(SUUNNITELTU_KESTO_TYYPPI));
        dto.setSuunniteltuKestoArvo(SUUNNITELTU_KESTO_VALUE);

        dto.setYhteyshenkilos(Sets.newHashSet(
                new fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi(PERSON[0], PERSON[1], PERSON[2],
                        PERSON[3], PERSON[4], null,
                        HenkiloTyyppi.YHTEYSHENKILO)));
        dto.setOpintojenLaajuusarvo(toKoodiUri(LAAJUUSARVO));
        dto.setOpintojenLaajuusyksikko(toKoodiUri(LAAJUUSYKSIKKO));
        dto.setKoulutusala(toKoodiUri(KOULUTUSALA));
        dto.setKoulutuskoodi(toKoodiUri(KOULUTUSKOODI));
        dto.setOpintoala(toKoodiUri(OPINTOALA));

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
        List<KoodiType> types = Lists.newArrayList();
        types.add(type);
        return types;
    }

    protected String createKoodistoUri(String koodistoUri) {
        Preconditions.checkNotNull(koodistoUri, "Koodisto uri cannot be null");
        return "koodisto_" + koodistoUri + "_uri";
    }

    protected static NimiV1RDTO teksti(final String nimi, final String kieli) {
        NimiV1RDTO dto = new NimiV1RDTO();
        dto.getTekstis().put(kieli, nimi);
        return dto;
    }

    protected static KoodiV1RDTO meta(final KoodiV1RDTO dto, final String kieli, final KoodiV1RDTO metaValue) {
        dto.setMeta(Maps.<String, KoodiV1RDTO>newHashMap());
        return dto.getMeta().put(kieli, metaValue);
    }

    protected KoodiUrisV1RDTO koodiUrisMap(final String kieliUri, final String fieldName) {
        KoodiUrisV1RDTO dto = new KoodiUrisV1RDTO();

        meta(dto, kieliUri, toKoodiUri(fieldName));

        if (dto.getUris() == null) {
            dto.setUris(Maps.<String, Integer>newHashMap());
        }

        dto.getUris().put(toKoodiUriStr(fieldName), 1);

        return dto;
    }

    protected static String toKoodiUriStr(final String type) {
        return type + "_uri";
    }

    protected static String toKoodiUriStrVersion(final String type) {
        return type + "_uri#1";
    }

    protected static KoodiV1RDTO toKoodiUri(final String type) {
        return new KoodiV1RDTO(type + "_uri", 1, null);
    }

    protected static NimiV1RDTO toNimiKoodiUri(final String type) {
        NimiV1RDTO n = new NimiV1RDTO();
        n.setUri(type + "_uri");
        n.setVersio(1);
        return n;
    }

    protected static String toNimiValue(final String value, String lang) {
        return value + "_" + lang;
    }

    protected void expectKausi() {
        KoodiType kausiKoodiType = createKoodiType(KAUSI_KOODI_URI, "x" + KAUSI_KOODI_URI);
        when(tarjontaKoodistoHelperMock.getKoodiByUri(KAUSI_KOODI_URI + "#1")).thenReturn(kausiKoodiType);
        when(tarjontaKoodistoHelperMock.getKoodi(KAUSI_KOODI_URI + "_uri", 1)).thenReturn(kausiKoodiType);
        when(tarjontaKoodistoHelperMock.getKoodiNimi(kausiKoodiType, new Locale(LOCALE_FI))).thenReturn(KAUSI_KOODI_URI);

        KoodiType koodiLanguageFi = createKoodiType(URI_KIELI_FI, "fi");
        when(tarjontaKoodistoHelperMock.convertKielikoodiToKoodiType(LOCALE_FI)).thenReturn(koodiLanguageFi);

        when(tarjontaKoodistoHelperMock.getKoodiNimi(koodiLanguageFi, new Locale(LOCALE_FI))).thenReturn(KAUSI_KOODI_URI);
        when(tarjontaKoodistoHelperMock.convertKielikoodiToKoodiType(URI_KIELI_FI)).thenReturn(koodiLanguageFi);

        when(tarjontaKoodistoHelperMock.getKoodi(URI_KIELI_FI + "_uri", 1)).thenReturn(koodiLanguageFi);
        when(tarjontaKoodistoHelperMock.convertKielikoodiToKoodiType(LOCALE_FI)).thenReturn(koodiLanguageFi);
    }

    protected void expectMetaUri(final String field) {
        when(tarjontaKoodistoHelperMock.getKoodiByUri(field + "_uri#1")).thenReturn(createKoodiType(field, "x" + field));

        KoodiType koodiType = createKoodiType(field, "x");
        when(tarjontaKoodistoHelperMock.getKoodi(field + "_uri", 1)).thenReturn(koodiType);
        when(tarjontaKoodistoHelperMock.getKoodiNimi(koodiType, new Locale(LOCALE_FI))).thenReturn(field);

        KoodiType koodiLanguageFi = createKoodiType(URI_KIELI_FI, "fi");
        when(tarjontaKoodistoHelperMock.convertKielikoodiToKoodiType(LOCALE_FI)).thenReturn(koodiLanguageFi);
    }

    protected void expectMetaMapUris(final String field) {
        KoodiType koodiType = createKoodiType(field, "x" + field);
        when(tarjontaKoodistoHelperMock.getKoodi(field + "_uri", 1)).thenReturn(koodiType);
        when(tarjontaKoodistoHelperMock.getKoodi(field + "_uri", 1)).thenReturn(koodiType);
        when(tarjontaKoodistoHelperMock.getKoodiNimi(koodiType, new Locale(LOCALE_FI))).thenReturn(field);

        KoodiType koodiLanguageFi = createKoodiType(URI_KIELI_FI, "fi");
        when(tarjontaKoodistoHelperMock.convertKielikoodiToKoodiType(LOCALE_FI)).thenReturn(koodiLanguageFi);
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

    private void addKuvaus(Map<KomoTeksti, MonikielinenTeksti> map, KomoTeksti komoTeksti) {
        MonikielinenTeksti teksti = new MonikielinenTeksti();
        teksti.addTekstiKaannos(URI_KIELI_FI, komoTeksti + "_" + URI_KIELI_FI);
        map.put(komoTeksti, teksti);
    }

}
