package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.resources.v1.KoulutusV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KorkeakouluOpintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidationMessages.KOULUTUS_TARJOAJA_MISSING;
import static fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidationMessages.KOULUTUS_TILA_ENUM_MISSING;
import static fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidator.*;
import static fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.OPINTOKOKONAISUUS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@ActiveProfiles("embedded-solr")
public class KoulutusResourceImplV1Test {

    @Autowired
    OrganisaatioService organisaatioService;

    @Autowired
    PermissionChecker permissionChecker;

    @Autowired
    OidService oidService;

    @Autowired
    KoulutusV1Resource koulutusResourceV1;

    private final static String JARJESTAJA1 = "1.2.3";
    private final static String TARJOAJA1 = "1.2.3";
    private final static String KOMO_OID1 = "komoOid1";
    private final static String KOMOTO_OID1 = "komotoOid1";
    private final static String ULKOINEN_TUNNISTE = "ulkoinenTunniste";

    @Before
    public void init() throws OIDCreationException {
        when(organisaatioService.findByOid(TARJOAJA1)).thenReturn(
                new OrganisaatioDTO(){{
                    setOid(TARJOAJA1);
                }}
        );
        doNothing().when(permissionChecker).checkCreateKoulutus(TARJOAJA1);
        when(oidService.get(TarjontaOidType.KOMO)).thenReturn(KOMO_OID1);
        when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn(KOMOTO_OID1);
    }

    @Test
    public void testCreatePuutteellinenOpintojakso() {
        KorkeakouluOpintoV1RDTO dto = baseKorkeakouluopinto(OPINTOKOKONAISUUS);
        dto.setTila(TarjontaTila.PUUTTEELLINEN);
        dto.setTunniste(ULKOINEN_TUNNISTE);

        ResultV1RDTO<KoulutusV1RDTO> result = koulutusResourceV1.postKoulutus(dto);
        assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
        assertEquals(KOMO_OID1, result.getResult().getKomoOid());
        assertEquals(KOMOTO_OID1, result.getResult().getOid());
    }

    @Test
    public void testCreateOpintojaksoFailsWhenMissingRequiredFields() {
        KorkeakouluOpintoV1RDTO dto = new KorkeakouluOpintoV1RDTO();

        ResultV1RDTO<KoulutusV1RDTO> result = koulutusResourceV1.postKoulutus(dto);
        assertEquals(ResultV1RDTO.ResultStatus.VALIDATION, result.getStatus());
        List<ErrorV1RDTO> errors = result.getErrors();
        assertEquals(5, errors.size());
        assertTrue(containsError(errors, KOULUTUS_TILA_ENUM_MISSING.getFieldName()));
        assertTrue(containsError(errors, KOULUTUS_TARJOAJA_MISSING.getFieldName()));
        assertTrue(containsError(errors, KOULUTUSOHJELMA));
        assertTrue(containsError(errors, KOULUTUSMODUULITYYPPI));
        assertTrue(containsError(errors, KOULUTUKSEN_ALKAMISPVMS));
    }

    @Test
    public void testCreateOpintojaksoWithInvalidStartingDate() {
        KorkeakouluOpintoV1RDTO dto = baseKorkeakouluopinto(OPINTOKOKONAISUUS);
        dto.setKoulutuksenAlkamisPvms(Sets.newHashSet(new Date(0L)));
        dto.setTila(TarjontaTila.PUUTTEELLINEN);

        ResultV1RDTO<KoulutusV1RDTO> result = koulutusResourceV1.postKoulutus(dto);
        assertEquals(ResultV1RDTO.ResultStatus.VALIDATION, result.getStatus());
        List<ErrorV1RDTO> errors = result.getErrors();
        assertEquals(1, errors.size());
        assertEquals("koulutuksenAlkamisPvms", errors.get(0).getErrorField());
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
