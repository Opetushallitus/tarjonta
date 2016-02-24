package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.impl.resources.v1.hakukohde.validation.HakukohdeValidationMessages;
import fi.vm.sade.tarjonta.service.resources.v1.HakukohdeV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KorkeakouluOpintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusIdentification;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static fi.vm.sade.tarjonta.service.impl.resources.v1.hakukohde.validation.HakukohdeValidationMessages.HAKUKOHDE_HAKU_MISSING;
import static fi.vm.sade.tarjonta.service.impl.resources.v1.hakukohde.validation.HakukohdeValidationMessages.HAKUKOHDE_KOULUTUS_MISSING;
import static fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO.ResultStatus.OK;
import static fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO.ResultStatus.VALIDATION;
import static fi.vm.sade.tarjonta.shared.types.TarjontaTila.LUONNOS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@ActiveProfiles("embedded-solr")
@Transactional
public class HakukohdeResourceImplV1Test {

    @Autowired
    KorkeakouluopintoV1Test koulutusResourceTest;

    @Autowired
    HakukohdeV1Resource hakukohdeV1Resource;

    @Autowired
    HakuDAO hakuDAO;

    @Autowired
    OidService oidService;

    @Autowired
    OidServiceMock oidServiceMock;

    @Test
    public void testCreateOpintokokonaisuusHakukohdeFailsWhenMissingRequiredData() throws OIDCreationException {
        HakukohdeV1RDTO hakukohde = baseHakukohde();
        ResultV1RDTO<HakukohdeV1RDTO> result = (ResultV1RDTO<HakukohdeV1RDTO>) hakukohdeV1Resource.postHakukohde(hakukohde).getEntity();
        assertEquals(VALIDATION, result.getStatus());
        assertEquals(2, result.getErrors().size());
        assertTrue(containsError(result.getErrors(), HAKUKOHDE_KOULUTUS_MISSING));
        assertTrue(containsError(result.getErrors(), HAKUKOHDE_HAKU_MISSING));
    }

    @Test
    public void testCreateOpintokokonaisuusHakukohde() throws OIDCreationException {
        when(oidService.get(TarjontaOidType.HAKUKOHDE)).thenReturn(oidServiceMock.getOid());

        Haku haku = insertHaku();
        KoulutusV1RDTO koulutus = koulutusResourceTest.insertLuonnosOpintokokonaisuus(null);

        HakukohdeV1RDTO hakukohde = baseHakukohde();
        hakukohde.setHakukohdeKoulutusOids(Lists.newArrayList(koulutus.getOid()));
        hakukohde.setHakukohteenNimet(ImmutableMap.of("kieli_fi", "hakukohteen nimi"));
        hakukohde.setHakuOid(haku.getOid());

        ResultV1RDTO<HakukohdeV1RDTO> result = (ResultV1RDTO<HakukohdeV1RDTO>) hakukohdeV1Resource.postHakukohde(hakukohde).getEntity();
        assertEquals(OK, result.getStatus());
    }

    @Test
    public void testEditHakukohdeUsingExternalId() throws OIDCreationException {
        String oid = oidServiceMock.getOid();
        when(oidService.get(TarjontaOidType.HAKUKOHDE)).thenReturn(oid);

        Haku haku = insertHaku();
        KoulutusV1RDTO koulutus = koulutusResourceTest.insertLuonnosOpintokokonaisuus(null);

        HakukohdeV1RDTO hakukohde = baseHakukohde();
        hakukohde.setHakukohdeKoulutusOids(Lists.newArrayList(koulutus.getOid()));
        hakukohde.setHakukohteenNimet(ImmutableMap.of("kieli_fi", "hakukohteen nimi"));
        hakukohde.setHakuOid(haku.getOid());
        hakukohde.setUniqueExternalId("someUniqueExternalId");

        // Create hakukohde
        hakukohdeV1Resource.postHakukohde(hakukohde);

        // Now modify it
        hakukohde.setAloituspaikatLkm(10);
        ResultV1RDTO<HakukohdeV1RDTO> result = (ResultV1RDTO<HakukohdeV1RDTO>) hakukohdeV1Resource.postHakukohde(hakukohde).getEntity();
        assertEquals(OK, result.getStatus());
        assertEquals(oid, result.getResult().getOid());
        assertEquals(hakukohde.getUniqueExternalId(), result.getResult().getUniqueExternalId());
        assertEquals(hakukohde.getAloituspaikatLkm(), result.getResult().getAloituspaikatLkm());
    }

    @Test
    public void testCreateOpintokokonaisuusHakukohdeUsingKoulutusExternalId() throws OIDCreationException {
        when(oidService.get(TarjontaOidType.HAKUKOHDE)).thenReturn(oidServiceMock.getOid());

        Haku haku = insertHaku();
        final KorkeakouluOpintoV1RDTO koulutusDto = new KorkeakouluOpintoV1RDTO();
        koulutusDto.setUniqueExternalId("1.2.3-externalId-42");

        koulutusResourceTest.insertLuonnosOpintokokonaisuus(koulutusDto);

        HakukohdeV1RDTO hakukohde = baseHakukohde();
        hakukohde.setKoulutukset(Sets.newHashSet(new KoulutusIdentification(null, koulutusDto.getUniqueExternalId())));
        hakukohde.setHakukohteenNimet(ImmutableMap.of("kieli_fi", "hakukohteen nimi"));
        hakukohde.setHakuOid(haku.getOid());

        ResultV1RDTO<HakukohdeV1RDTO> result = (ResultV1RDTO<HakukohdeV1RDTO>) hakukohdeV1Resource.postHakukohde(hakukohde).getEntity();
        assertEquals(OK, result.getStatus());
        assertTrue(Iterables.find(result.getResult().getKoulutukset(), new Predicate<KoulutusIdentification>() {
            @Override
            public boolean apply(KoulutusIdentification id) {
                return koulutusDto.getUniqueExternalId().equals(id.getUlkoinenTunniste());
            }
        }, null) != null);
    }

    @Test
    public void testDeltaEditHakukohde() throws OIDCreationException {
        when(oidService.get(TarjontaOidType.HAKUKOHDE)).thenReturn(oidServiceMock.getOid());

        Haku haku = insertHaku();
        final KorkeakouluOpintoV1RDTO koulutusDto = new KorkeakouluOpintoV1RDTO();
        koulutusDto.setUniqueExternalId("deltaHakukohdeEdit");

        koulutusResourceTest.insertLuonnosOpintokokonaisuus(koulutusDto);

        HakukohdeV1RDTO hakukohde = baseHakukohde();
        hakukohde.setKoulutukset(Sets.newHashSet(new KoulutusIdentification(null, koulutusDto.getUniqueExternalId())));
        hakukohde.setHakukohteenNimet(ImmutableMap.of("kieli_fi", "hakukohteen nimi"));
        hakukohde.setHakuOid(haku.getOid());
        hakukohde.setUniqueExternalId("deltaHakukohdeEdit");
        hakukohde.setKelaLinjaKoodi("kelanKoodi");

        ResultV1RDTO<HakukohdeV1RDTO> result = (ResultV1RDTO<HakukohdeV1RDTO>) hakukohdeV1Resource.postHakukohde(hakukohde).getEntity();
        assertEquals(OK, result.getStatus());

        HakukohdeV1RDTO deltaDto = new HakukohdeV1RDTO();
        deltaDto.setUniqueExternalId(hakukohde.getUniqueExternalId());
        deltaDto.setAloituspaikatLkm(10);

        result = (ResultV1RDTO<HakukohdeV1RDTO>) hakukohdeV1Resource.postHakukohde(deltaDto).getEntity();
        assertEquals(OK, result.getStatus());
        assertEquals(deltaDto.getAloituspaikatLkm(), result.getResult().getAloituspaikatLkm());
        assertEquals(hakukohde.getKelaLinjaKoodi(), result.getResult().getKelaLinjaKoodi());
    }

    public HakukohdeV1RDTO baseHakukohde() {
        HakukohdeV1RDTO dto = new HakukohdeV1RDTO();
        dto.setTila(TarjontaTila.LUONNOS.value());
        return dto;
    }

    private Haku insertHaku() {
        Haku haku = new Haku();
        haku.setHakutapaUri("hakutapa");
        haku.setTila(LUONNOS);
        haku.setHakukausiVuosi(new Date().getYear());
        haku.setKohdejoukkoUri("kohdejoukko");
        haku.setHakukausiUri("hakukausiUri");
        haku.setHakutyyppiUri("hakutyyppi");
        haku.setOid(oidServiceMock.getOid());
        return hakuDAO.insert(haku);
    }

    private static boolean containsError(List<ErrorV1RDTO> errors, final HakukohdeValidationMessages msg) {
        return Iterables.find(errors, new Predicate<ErrorV1RDTO>() {
            @Override
            public boolean apply(ErrorV1RDTO error) {
                return StringUtils.defaultString(error.getErrorMessageKey()).contains(msg.name());
            }
        }, null) != null;
    }

}
