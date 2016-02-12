package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.auth.NotAuthorizedException;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.resources.v1.KoulutusV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidationMessages.KOULUTUS_TARJOAJA_MISSING;
import static fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidator.*;
import static fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.OPINTOKOKONAISUUS;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@Service("koulutusResourceTest")
@ActiveProfiles("embedded-solr")
public class KoulutusResourceImplV1Test {

    @Autowired
    OrganisaatioService organisaatioService;

    @Autowired
    PermissionChecker permissionChecker;

    @Autowired
    OidService oidService;

    @Autowired
    OidServiceMock oidServiceMock;

    @Autowired
    KoulutusV1Resource koulutusResourceV1;

    private final static String TARJOAJA1 = "1.2.3";
    private final static String TUNNISTE = "kk virkailijan tunniste, ei uniikki";

    @Before
    public void init() {
        when(organisaatioService.findByOid(TARJOAJA1)).thenReturn(
                new OrganisaatioDTO(){{
                    setOid(TARJOAJA1);
                    setNimi(new MonikielinenTekstiTyyppi(Lists.newArrayList(new MonikielinenTekstiTyyppi.Teksti("test", "fi"))));
                }}
        );
        doNothing().when(permissionChecker).checkCreateKoulutus(TARJOAJA1);
    }

    @Test
    public void testCreatePuutteellinenOpintojakso() throws OIDCreationException {
        String komoOid = oidServiceMock.getOid();
        String komotoOid = oidServiceMock.getOid();
        when(oidService.get(TarjontaOidType.KOMO)).thenReturn(komoOid);
        when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn(komotoOid);

        KorkeakouluOpintoV1RDTO dto = baseKorkeakouluopinto(OPINTOKOKONAISUUS);
        dto.setTila(TarjontaTila.PUUTTEELLINEN);
        dto.setTunniste(TUNNISTE);

        ResultV1RDTO<KoulutusV1RDTO> result = koulutusResourceV1.postKoulutus(dto);
        assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
        assertEquals(komoOid, result.getResult().getKomoOid());
        assertEquals(komotoOid, result.getResult().getOid());
    }

    @Test
    public void testCreateLuonnosOpintojaksoWithUniqueExternalId() throws OIDCreationException {
        String komoOid = oidServiceMock.getOid();
        String komotoOid = oidServiceMock.getOid();
        String uniqueExternalId = "uniqueId1";
        when(oidService.get(TarjontaOidType.KOMO)).thenReturn(komoOid);
        when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn(komotoOid);

        KorkeakouluOpintoV1RDTO dto = baseKorkeakouluopinto(OPINTOKOKONAISUUS);
        dto.setUlkoinenTunniste(uniqueExternalId);
        dto.setAihees(koodiUris(Sets.newHashSet("aihe_1")));
        dto.setOpintojenLaajuusPistetta("120");
        dto.setOpetuskielis(koodiUris(Sets.newHashSet("kieli_fi")));

        ResultV1RDTO<KoulutusV1RDTO> result = koulutusResourceV1.postKoulutus(dto);
        assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
        assertEquals(komoOid, result.getResult().getKomoOid());
        assertEquals(komotoOid, result.getResult().getOid());
        assertEquals(uniqueExternalId, result.getResult().getUlkoinenTunniste());
    }

    @Test
    public void testEditLuonnosOpintojaksoUsingUniqueExternalId() throws OIDCreationException {
        String komoOid = oidServiceMock.getOid();
        String komotoOid = oidServiceMock.getOid();
        String uniqueExternalId = "uniqueId2";
        when(oidService.get(TarjontaOidType.KOMO)).thenReturn(komoOid);
        when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn(komotoOid);

        KorkeakouluOpintoV1RDTO dto = baseKorkeakouluopinto(OPINTOKOKONAISUUS);
        dto.setUlkoinenTunniste(uniqueExternalId);
        dto.setAihees(koodiUris(Sets.newHashSet("aihe_1")));
        dto.setOpintojenLaajuusPistetta("120");
        dto.setOpetuskielis(koodiUris(Sets.newHashSet("kieli_fi")));

        // Save koulutus for the 1st time
        koulutusResourceV1.postKoulutus(dto);

        // Now modify it
        dto.setOpintojenLaajuusPistetta("140");
        ResultV1RDTO<KoulutusV1RDTO> result = koulutusResourceV1.postKoulutus(dto);
        assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());

        KorkeakouluOpintoV1RDTO resultDto = (KorkeakouluOpintoV1RDTO) result.getResult();
        assertEquals(komoOid, resultDto.getKomoOid());
        assertEquals(komotoOid, resultDto.getOid());
        assertEquals(uniqueExternalId, resultDto.getUlkoinenTunniste());
        assertEquals("140", resultDto.getOpintojenLaajuusPistetta());
    }

    @Test
    public void testCreateLuonnosOpintojakso() throws OIDCreationException {
        String komoOid = oidServiceMock.getOid();
        String komotoOid = oidServiceMock.getOid();
        when(oidService.get(TarjontaOidType.KOMO)).thenReturn(komoOid);
        when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn(komotoOid);

        KorkeakouluOpintoV1RDTO dto = baseKorkeakouluopinto(OPINTOKOKONAISUUS);
        dto.setAihees(koodiUris(Sets.newHashSet("aihe_1")));
        dto.setOpintojenLaajuusPistetta("120");
        dto.setOpetuskielis(koodiUris(Sets.newHashSet("kieli_fi")));

        ResultV1RDTO<KoulutusV1RDTO> result = koulutusResourceV1.postKoulutus(dto);
        assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
        assertEquals(komoOid, result.getResult().getKomoOid());
        assertEquals(komotoOid, result.getResult().getOid());
    }

    @Test
    public void testCreateOpintojaksoFailsWhenMissingRequiredFieldsAndTilaPuutteellinen() {
        KorkeakouluOpintoV1RDTO dto = new KorkeakouluOpintoV1RDTO();
        dto.setTila(TarjontaTila.PUUTTEELLINEN);

        ResultV1RDTO<KoulutusV1RDTO> result = koulutusResourceV1.postKoulutus(dto);
        assertEquals(ResultV1RDTO.ResultStatus.VALIDATION, result.getStatus());
        assertEquals(4, result.getErrors().size());
        assertAlwaysRequiredFields(result.getErrors());
    }

    private void assertAlwaysRequiredFields(List<ErrorV1RDTO> errors) {
        assertTrue(containsError(errors, KOULUTUS_TARJOAJA_MISSING.getFieldName()));
        assertTrue(containsError(errors, KOULUTUSOHJELMA));
        assertTrue(containsError(errors, KOULUTUSMODUULITYYPPI));
        assertTrue(containsError(errors, KOULUTUKSEN_ALKAMISPVMS));
    }

    private void assertExtraRequiredFields(List<ErrorV1RDTO> errors) {
        assertTrue(containsError(errors, OPINTOJEN_LAAJUUS_PISTETTA));
        assertTrue(containsError(errors, OPETUSKIELIS));
        assertTrue(containsError(errors, AIHEES));
    }

    @Test
    public void testCreateOpintojaksoFailsWhenMissingRequiredFieldsAndTilaNotPuuttellinen() {
        KorkeakouluOpintoV1RDTO dto = new KorkeakouluOpintoV1RDTO();
        dto.setTila(TarjontaTila.LUONNOS);

        ResultV1RDTO<KoulutusV1RDTO> result = koulutusResourceV1.postKoulutus(dto);
        assertEquals(ResultV1RDTO.ResultStatus.VALIDATION, result.getStatus());
        List<ErrorV1RDTO> errors = result.getErrors();
        assertEquals(7, errors.size());
        assertAlwaysRequiredFields(errors);
        assertExtraRequiredFields(errors);
    }

    @Test
    public void testCreateOpintojaksoFailsWhenInvalidStartingDate() {
        KorkeakouluOpintoV1RDTO dto = baseKorkeakouluopinto(OPINTOKOKONAISUUS);
        dto.setKoulutuksenAlkamisPvms(Sets.newHashSet(new Date(0L)));
        dto.setTila(TarjontaTila.PUUTTEELLINEN);

        ResultV1RDTO<KoulutusV1RDTO> result = koulutusResourceV1.postKoulutus(dto);
        assertEquals(ResultV1RDTO.ResultStatus.VALIDATION, result.getStatus());
        List<ErrorV1RDTO> errors = result.getErrors();
        assertEquals(1, errors.size());
        assertEquals("koulutuksenAlkamisPvms", errors.get(0).getErrorField());
    }

    @Test
    public void testCreateOpintojaksoWithValidSisaltyvyys() throws OIDCreationException {
        final KoulutusV1RDTO parentKoulutus1 = insertParentKokonaisuus(null);
        final String ulkoinenTunniste = "ulkoinenTunniste3.4";
        final KoulutusV1RDTO parentKoulutus2 = insertParentKokonaisuus(ulkoinenTunniste);

        String komoOid = oidServiceMock.getOid();
        String komotoOid = oidServiceMock.getOid();
        when(oidService.get(TarjontaOidType.KOMO)).thenReturn(komoOid);
        when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn(komotoOid);

        KorkeakouluOpintoV1RDTO dto = baseKorkeakouluopinto(OPINTOKOKONAISUUS);
        dto.setTila(TarjontaTila.PUUTTEELLINEN);
        dto.setTunniste(TUNNISTE);
        dto.setSisaltyyKoulutuksiin(Sets.newHashSet(
                new KoulutusIdentification(){{setOid(parentKoulutus1.getOid());}},
                new KoulutusIdentification(){{setUlkoinenTunniste(parentKoulutus2.getUlkoinenTunniste());}}
        ));

        ResultV1RDTO<KoulutusV1RDTO> result = koulutusResourceV1.postKoulutus(dto);
        assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
        assertEquals(komoOid, result.getResult().getKomoOid());
        assertEquals(komotoOid, result.getResult().getOid());
        Set<KoulutusIdentification> sisaltyyKoulutuksiin = result.getResult().getSisaltyyKoulutuksiin();
        assertEquals(2, sisaltyyKoulutuksiin.size());
        assertNotNull(Iterables.find(sisaltyyKoulutuksiin, new Predicate<KoulutusIdentification>() {
            @Override
            public boolean apply(KoulutusIdentification koulutus) {
                return koulutus.getOid().equals(parentKoulutus1.getOid());
            }
        }));
        assertNotNull(Iterables.find(sisaltyyKoulutuksiin, new Predicate<KoulutusIdentification>() {
            @Override
            public boolean apply(KoulutusIdentification koulutus) {
                return koulutus.getOid().equals(parentKoulutus2.getOid());
            }
        }));
    }

    @Test
    public void testCreateOpintojaksoFailsWhenNoPermissionForSisaltyvyys() throws OIDCreationException {
        final KoulutusV1RDTO parentKoulutus = insertParentKokonaisuus(null);

        doThrow(NotAuthorizedException.class)
                .when(permissionChecker)
                .checkUpdateKoulutusByTarjoajaOid(parentKoulutus.getOrganisaatio().getOid());

        String komoOid = oidServiceMock.getOid();
        String komotoOid = oidServiceMock.getOid();
        when(oidService.get(TarjontaOidType.KOMO)).thenReturn(komoOid);
        when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn(komotoOid);

        KorkeakouluOpintoV1RDTO dto = baseKorkeakouluopinto(OPINTOKOKONAISUUS);
        dto.setTila(TarjontaTila.PUUTTEELLINEN);
        dto.setTunniste(TUNNISTE);
        dto.setSisaltyyKoulutuksiin(Sets.<KoulutusIdentification>newHashSet(new KoulutusIdentification(){{
            setOid(parentKoulutus.getOid());
        }}));

        ResultV1RDTO<KoulutusV1RDTO> result = koulutusResourceV1.postKoulutus(dto);
        assertEquals(ResultV1RDTO.ResultStatus.VALIDATION, result.getStatus());
        assertEquals(1, result.getErrors().size());
        assertTrue(containsError(result.getErrors(), SISALTYY_KOULUTUKSIIN));
    }

    @Test
    public void testCreateOpintojaksoFailsWhenInvalidSisaltyvyys() throws OIDCreationException {
        String komoOid = oidServiceMock.getOid();
        String komotoOid = oidServiceMock.getOid();
        when(oidService.get(TarjontaOidType.KOMO)).thenReturn(komoOid);
        when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn(komotoOid);

        KorkeakouluOpintoV1RDTO dto = baseKorkeakouluopinto(OPINTOKOKONAISUUS);
        dto.setTila(TarjontaTila.PUUTTEELLINEN);
        dto.setTunniste(TUNNISTE);
        dto.setSisaltyyKoulutuksiin(Sets.<KoulutusIdentification>newHashSet(new KoulutusIdentification(){{
            setOid("oid.that.does.not.exist");
        }}));

        ResultV1RDTO<KoulutusV1RDTO> result = koulutusResourceV1.postKoulutus(dto);
        assertEquals(ResultV1RDTO.ResultStatus.VALIDATION, result.getStatus());
        assertEquals(1, result.getErrors().size());
        assertTrue(containsError(result.getErrors(), SISALTYY_KOULUTUKSIIN));
    }

    private KoulutusV1RDTO insertParentKokonaisuus(String ulkoinenTunniste) throws OIDCreationException {
        String komoOid = oidServiceMock.getOid();
        String komotoOid = oidServiceMock.getOid();
        when(oidService.get(TarjontaOidType.KOMO)).thenReturn(komoOid);
        when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn(komotoOid);

        KorkeakouluOpintoV1RDTO dto = baseKorkeakouluopinto(OPINTOKOKONAISUUS);
        dto.setTila(TarjontaTila.PUUTTEELLINEN);
        dto.setTunniste(TUNNISTE);
        dto.setUlkoinenTunniste(ulkoinenTunniste);

        ResultV1RDTO<KoulutusV1RDTO> result = koulutusResourceV1.postKoulutus(dto);
        return result.getResult();
    }

    private static KorkeakouluOpintoV1RDTO baseKorkeakouluopinto(KoulutusmoduuliTyyppi tyyppi) {
        KorkeakouluOpintoV1RDTO dto = new KorkeakouluOpintoV1RDTO();
        dto.setTila(TarjontaTila.LUONNOS);
        dto.setKoulutuksenAlkamisPvms(Sets.newHashSet(new Date()));
        dto.setKoulutusmoduuliTyyppi(tyyppi);
        dto.setOrganisaatio(new OrganisaatioV1RDTO(){{
            setOid(TARJOAJA1);
        }});
        dto.setKoulutusohjelma(new NimiV1RDTO(){{
            setTekstis(ImmutableMap.of("kieli_fi", "Opinnon nimi"));
        }});
        return dto;
    }

    public KoulutusV1RDTO getLuonnosOpintokokonaisuus() throws OIDCreationException {
        init();
        String komoOid = oidServiceMock.getOid();
        String komotoOid = oidServiceMock.getOid();
        when(oidService.get(TarjontaOidType.KOMO)).thenReturn(komoOid);
        when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn(komotoOid);

        KorkeakouluOpintoV1RDTO dto = baseKorkeakouluopinto(OPINTOKOKONAISUUS);
        dto.setAihees(koodiUris(Sets.newHashSet("aihe_1")));
        dto.setOpintojenLaajuusPistetta("120");
        dto.setOpetuskielis(koodiUris(Sets.newHashSet("kieli_fi")));

        return koulutusResourceV1.postKoulutus(dto).getResult();
    }

    private static boolean containsError(List<ErrorV1RDTO> errors, final String fieldname) {
        return Iterables.find(errors, new Predicate<ErrorV1RDTO>() {
            @Override
            public boolean apply(ErrorV1RDTO error) {
                return fieldname.equals(error.getErrorField());
            }
        }, null) != null;
    }
    
    private static KoodiUrisV1RDTO koodiUris(Set<String> codeUris) {
        KoodiUrisV1RDTO dto = new KoodiUrisV1RDTO();
        Map<String, Integer> uris = new HashedMap();
        for (String uri : codeUris) {
            uris.put(uri, 1);
        }
        dto.setUris(uris);
        return dto;
    }

}
