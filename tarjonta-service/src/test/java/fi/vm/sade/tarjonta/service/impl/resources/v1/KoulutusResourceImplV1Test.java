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
import fi.vm.sade.tarjonta.service.resources.v1.dto.OppiaineV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KorkeakouluOpintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusIdentification;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.service.types.HenkiloTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
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
import java.util.Set;

import static fi.vm.sade.tarjonta.service.impl.resources.v1.V1TestHelper.*;
import static fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidator.*;
import static fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.OPINTOJAKSO;
import static fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.OPINTOKOKONAISUUS;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

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

    @Autowired
    V1TestHelper helper;

    @Before
    public void init() {
        helper.init();
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

        ResultV1RDTO<KoulutusV1RDTO> result = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(dto).getEntity();
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
        dto.setUniqueExternalId(uniqueExternalId);
        dto.setAihees(koodiUris(Sets.newHashSet("aihe_1")));
        dto.setOpintojenLaajuusPistetta("120");
        dto.setOpetuskielis(koodiUris(Sets.newHashSet("kieli_fi")));

        ResultV1RDTO<KoulutusV1RDTO> result = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(dto).getEntity();
        assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
        assertEquals(komoOid, result.getResult().getKomoOid());
        assertEquals(komotoOid, result.getResult().getOid());
        assertEquals(uniqueExternalId, result.getResult().getUniqueExternalId());
    }

    @Test
    public void testEditLuonnosOpintojaksoUsingUniqueExternalId() throws OIDCreationException {
        String komoOid = oidServiceMock.getOid();
        String komotoOid = oidServiceMock.getOid();
        String uniqueExternalId = "uniqueId2";
        when(oidService.get(TarjontaOidType.KOMO)).thenReturn(komoOid);
        when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn(komotoOid);

        KorkeakouluOpintoV1RDTO dto = baseKorkeakouluopinto(OPINTOKOKONAISUUS);
        dto.setUniqueExternalId(uniqueExternalId);
        dto.setAihees(koodiUris(Sets.newHashSet("aihe_1")));
        dto.setOpintojenLaajuusPistetta("120");
        dto.setOpetuskielis(koodiUris(Sets.newHashSet("kieli_fi")));

        // Save koulutus for the 1st time
        koulutusResourceV1.postKoulutus(dto);

        // Now modify it
        dto.setOpintojenLaajuusPistetta("140");
        ResultV1RDTO<KoulutusV1RDTO> result = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(dto).getEntity();
        assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());

        KorkeakouluOpintoV1RDTO resultDto = (KorkeakouluOpintoV1RDTO) result.getResult();
        assertEquals(komoOid, resultDto.getKomoOid());
        assertEquals(komotoOid, resultDto.getOid());
        assertEquals(uniqueExternalId, resultDto.getUniqueExternalId());
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

        ResultV1RDTO<KoulutusV1RDTO> result = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(dto).getEntity();
        assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
        assertEquals(komoOid, result.getResult().getKomoOid());
        assertEquals(komotoOid, result.getResult().getOid());
    }

    @Test
    public void testCreateOpintojaksoFailsWhenMissingRequiredFieldsAndTilaPuutteellinen() {
        KorkeakouluOpintoV1RDTO dto = new KorkeakouluOpintoV1RDTO();
        dto.setTila(TarjontaTila.PUUTTEELLINEN);
        dto.setOrganisaatio(new OrganisaatioV1RDTO(TARJOAJA1, "", null));

        ResultV1RDTO<KoulutusV1RDTO> result = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(dto).getEntity();
        assertEquals(ResultV1RDTO.ResultStatus.VALIDATION, result.getStatus());
        assertEquals(3, result.getErrors().size());
        assertAlwaysRequiredFields(result.getErrors());
    }

    private void assertAlwaysRequiredFields(List<ErrorV1RDTO> errors) {
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
        dto.setOrganisaatio(new OrganisaatioV1RDTO(TARJOAJA1, "", null));


        ResultV1RDTO<KoulutusV1RDTO> result = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(dto).getEntity();
        assertEquals(ResultV1RDTO.ResultStatus.VALIDATION, result.getStatus());
        List<ErrorV1RDTO> errors = result.getErrors();
        assertEquals(6, errors.size());
        assertAlwaysRequiredFields(errors);
        assertExtraRequiredFields(errors);
    }

    @Test
    public void testCreateOpintojaksoFailsWhenInvalidStartingDate() {
        KorkeakouluOpintoV1RDTO dto = baseKorkeakouluopinto(OPINTOKOKONAISUUS);
        dto.setKoulutuksenAlkamisPvms(Sets.newHashSet(new Date(0L)));
        dto.setTila(TarjontaTila.PUUTTEELLINEN);

        ResultV1RDTO<KoulutusV1RDTO> result = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(dto).getEntity();
        assertEquals(ResultV1RDTO.ResultStatus.VALIDATION, result.getStatus());
        List<ErrorV1RDTO> errors = result.getErrors();
        assertEquals(1, errors.size());
        assertEquals("koulutuksenAlkamisPvms", errors.get(0).getErrorField());
    }

    @Test
    public void testCreateOpintojaksoWithValidSisaltyvyys() throws OIDCreationException {
        final KoulutusV1RDTO parentKoulutus1 = insertParentKokonaisuus(null, null);
        final String ulkoinenTunniste = "ulkoinenTunniste3.4";
        final KoulutusV1RDTO parentKoulutus2 = insertParentKokonaisuus(ulkoinenTunniste, null);

        String komoOid = oidServiceMock.getOid();
        String komotoOid = oidServiceMock.getOid();
        when(oidService.get(TarjontaOidType.KOMO)).thenReturn(komoOid);
        when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn(komotoOid);

        KorkeakouluOpintoV1RDTO dto = baseKorkeakouluopinto(OPINTOKOKONAISUUS);
        dto.setTila(TarjontaTila.PUUTTEELLINEN);
        dto.setTunniste(TUNNISTE);
        dto.setSisaltyyKoulutuksiin(Sets.newHashSet(
                new KoulutusIdentification(parentKoulutus1.getOid(), null),
                new KoulutusIdentification(null, parentKoulutus2.getUniqueExternalId())
        ));

        ResultV1RDTO<KoulutusV1RDTO> result = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(dto).getEntity();
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
        final String parentKoulutusOrgOid = "someOrgOid";
        when(organisaatioService.findByOid(parentKoulutusOrgOid)).thenReturn(
                new OrganisaatioDTO(){{
                    setOid(parentKoulutusOrgOid);
                    setNimi(new MonikielinenTekstiTyyppi(Lists.newArrayList(new MonikielinenTekstiTyyppi.Teksti("test", "fi"))));
                }}
        );
        final KoulutusV1RDTO parentKoulutus = insertParentKokonaisuus(null, parentKoulutusOrgOid);

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
        dto.setSisaltyyKoulutuksiin(Sets.newHashSet(new KoulutusIdentification(parentKoulutus.getOid(), null)));

        ResultV1RDTO<KoulutusV1RDTO> result = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(dto).getEntity();
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
        dto.setOrganisaatio(new OrganisaatioV1RDTO(TARJOAJA1, "tarjoaja", null));
        dto.setSisaltyyKoulutuksiin(Sets.newHashSet(new KoulutusIdentification("oid.that.does.not.exist", null)));

        ResultV1RDTO<KoulutusV1RDTO> result = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(dto).getEntity();
        assertEquals(ResultV1RDTO.ResultStatus.VALIDATION, result.getStatus());
        assertEquals(1, result.getErrors().size());
        assertTrue(containsError(result.getErrors(), SISALTYY_KOULUTUKSIIN));
    }

    @Test
    public void testOpintojaksoDeltaUpdate() throws OIDCreationException {
        KorkeakouluOpintoV1RDTO original = upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.LUONNOS);
        KorkeakouluOpintoV1RDTO modified = getDeltaDto();
        modified.setTila(TarjontaTila.VALMIS);
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.TILA);

        original = upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setOrganisaatio(new OrganisaatioV1RDTO(TARJOAJA2));
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.ORGANISAATIO);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setKoulutusmoduuliTyyppi(OPINTOJAKSO);
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.KOULUTUSMODUULITYYPPI);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setOpetusJarjestajat(Sets.newHashSet("modified1", "modified2"));
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.JARJESTAJAT);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setKoulutusohjelma(new NimiV1RDTO(){{
            setTekstis(ImmutableMap.of("kieli_fi", "modifiedFi"));
        }});
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.KOULUTUSOHJELMA);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setTunniste("modifiedTunniste");
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.TUNNISTE);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setHakijalleNaytettavaTunniste("modifiedHakijanTunniste");
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.HAKIJAN_TUNNISTE);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setOpinnonTyyppiUri("modified");
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.OPINNON_TYYPPI);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setOpintojenLaajuusPistetta("modified");
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.LAAJUUSPISTETTA);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        Date d2020 = new Date();
        d2020.setYear(2020);
        modified.setKoulutuksenAlkamisPvms(Sets.newHashSet(d2020));
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.ALKAMISPVMS);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setKoulutuksenLoppumisPvm(d2020);
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.LOPPUMISPVM);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setOpetuskielis(koodiUris(Sets.newHashSet("kieli_en")));
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.OPETUSKIELIS);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setOpintojenMaksullisuus(false);
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.OPINTOJEN_MAKSULLISUUS);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setHintaString("modified");
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.HINTA_STRING);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setOpetusAikas(koodiUris(Sets.newHashSet("modified")));
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.OPETUSAIKAS);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setOpetusmuodos(koodiUris(Sets.newHashSet("modified")));
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.OPETUSMUODOS);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setOpetusPaikkas(koodiUris(Sets.newHashSet("modified")));
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.OPETUSPAIKKAS);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setAihees(koodiUris(Sets.newHashSet("modified")));
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.AIHEES);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setOppiaineet(Sets.newHashSet(new OppiaineV1RDTO("kieli_fi", "modified")));
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.OPPIAINEET);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setOpettaja("modified");
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.OPETTAJA);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setIsAvoimenYliopistonKoulutus(false);
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.AVOIMEN_YLIOPISTON_KOULUTUS);

        upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila.VALMIS);
        modified = getDeltaDto();
        modified.setYhteyshenkilos(Sets.newHashSet(
                new YhteyshenkiloTyyppi(null, "modified", "titteli", "email@email.com", "puhelin",
                        Lists.newArrayList("kieli_fi"), HenkiloTyyppi.YHTEYSHENKILO)
        ));
        modified = insertKoulutusAndGetDtoResult(modified);
        assertDelta(original, modified, KoulutusField.YHTEYSHENKILOS);
    }

    private KorkeakouluOpintoV1RDTO insertKoulutusAndGetDtoResult(KorkeakouluOpintoV1RDTO dto) {
        ResultV1RDTO<KorkeakouluOpintoV1RDTO> result = (ResultV1RDTO<KorkeakouluOpintoV1RDTO>) koulutusResourceV1.postKoulutus(dto).getEntity();
        assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
        return result.getResult();
    }

    private KorkeakouluOpintoV1RDTO getDeltaDto() {
        KorkeakouluOpintoV1RDTO dto = new KorkeakouluOpintoV1RDTO();
        dto.setUniqueExternalId("deltaId");
        return dto;
    }

    private KoulutusV1RDTO insertParentKokonaisuus(String ulkoinenTunniste, final String orgOid) throws OIDCreationException {
        String komoOid = oidServiceMock.getOid();
        String komotoOid = oidServiceMock.getOid();
        when(oidService.get(TarjontaOidType.KOMO)).thenReturn(komoOid);
        when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn(komotoOid);

        KorkeakouluOpintoV1RDTO dto = baseKorkeakouluopinto(OPINTOKOKONAISUUS);
        dto.setTila(TarjontaTila.PUUTTEELLINEN);
        dto.setTunniste(TUNNISTE);
        dto.setUniqueExternalId(ulkoinenTunniste);
        if (orgOid != null) {
            dto.setOrganisaatio(new OrganisaatioV1RDTO() {{
                setOid(orgOid);
            }});
        }

        ResultV1RDTO<KoulutusV1RDTO> result = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(dto).getEntity();
        return result.getResult();
    }

    private static KorkeakouluOpintoV1RDTO baseKorkeakouluopinto(KoulutusmoduuliTyyppi tyyppi) {
        return baseKorkeakouluopinto(tyyppi, null);
    }

    private static KorkeakouluOpintoV1RDTO baseKorkeakouluopinto(KoulutusmoduuliTyyppi tyyppi, KorkeakouluOpintoV1RDTO dto) {
        if (dto == null) {
            dto = new KorkeakouluOpintoV1RDTO();
        }
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

    public KoulutusV1RDTO insertLuonnosOpintokokonaisuus(KorkeakouluOpintoV1RDTO dto) throws OIDCreationException {
        init();
        String komoOid = oidServiceMock.getOid();
        String komotoOid = oidServiceMock.getOid();
        when(oidService.get(TarjontaOidType.KOMO)).thenReturn(komoOid);
        when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn(komotoOid);

        dto = baseKorkeakouluopinto(OPINTOKOKONAISUUS, dto);
        dto.setAihees(koodiUris(Sets.newHashSet("aihe_1")));
        dto.setOpintojenLaajuusPistetta("120");
        dto.setOpetuskielis(koodiUris(Sets.newHashSet("kieli_fi")));

        ResultV1RDTO<KoulutusV1RDTO> result = (ResultV1RDTO<KoulutusV1RDTO>)koulutusResourceV1.postKoulutus(dto).getEntity();
        return result.getResult();
    }

    public KorkeakouluOpintoV1RDTO upsertLuonnosOpintokokonaisuusWithAllFieldsSet(TarjontaTila tila) throws OIDCreationException {
        when(oidService.get(TarjontaOidType.KOMO)).thenReturn(oidServiceMock.getOid());
        when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn(oidServiceMock.getOid());

        KorkeakouluOpintoV1RDTO dto = getDeltaDto();
        dto.setTila(tila);
        dto.setOrganisaatio(new OrganisaatioV1RDTO(TARJOAJA1));
        dto.setKoulutusmoduuliTyyppi(OPINTOKOKONAISUUS);
        dto.setOpetusJarjestajat(Sets.newHashSet("jarjestajaOid1", "jarjestajaOid2"));
        dto.setKoulutusohjelma(new NimiV1RDTO(){{
            setTekstis(ImmutableMap.of("kieli_fi", "Opinnon nimi", "kieli_sv", "samme på svenska"));
        }});
        dto.setTunniste("initialTunniste");
        dto.setHakijalleNaytettavaTunniste("hakijanTunniste");
        dto.setOpinnonTyyppiUri("someTyyppiUri");
        dto.setOpintojenLaajuusPistetta("150 op");
        dto.setKoulutuksenAlkamisPvms(Sets.newHashSet(new Date(), new Date()));
        dto.setKoulutuksenLoppumisPvm(new Date());
        dto.setOpetuskielis(koodiUris(Sets.newHashSet("kieli_fi", "kieli_sv")));
        dto.setOpintojenMaksullisuus(true);
        dto.setHintaString("100€");
        dto.setOpetusAikas(koodiUris(Sets.newHashSet("aika1", "aika2")));
        dto.setOpetusmuodos(koodiUris(Sets.newHashSet("muoto1", "muoto2")));
        dto.setOpetusPaikkas(koodiUris(Sets.newHashSet("paikka1", "paikka2")));
        dto.setAihees(koodiUris(Sets.newHashSet("aihe1", "aihe2")));
        dto.setOppiaineet(Sets.newHashSet(
                new OppiaineV1RDTO("kieli_fi", "oppiaine1"),
                new OppiaineV1RDTO("kieli_fi", "oppiaine2"),
                new OppiaineV1RDTO("kieli_sv", "oppiaine_sv_1")
        ));
        dto.setOpettaja("opettaja");
        dto.setIsAvoimenYliopistonKoulutus(true);
        dto.setYhteyshenkilos(Sets.newHashSet(
                new YhteyshenkiloTyyppi(null, "nimi", "titteli", "email@email.com", "puhelin",
                        Lists.newArrayList("kieli_fi"), HenkiloTyyppi.YHTEYSHENKILO)
        ));

        ResultV1RDTO<KorkeakouluOpintoV1RDTO> result = (ResultV1RDTO<KorkeakouluOpintoV1RDTO>) koulutusResourceV1.postKoulutus(dto).getEntity();
        assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
        return result.getResult();
    }

}
