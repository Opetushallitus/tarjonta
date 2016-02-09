package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.collect.Sets;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.resources.v1.KoulutusV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KorkeakouluOpintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
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
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
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
    public void testOpintojaksoCreate() {

        KorkeakouluOpintoV1RDTO dto = baseKorkeakouluopinto();

        ResultV1RDTO<KoulutusV1RDTO> result = koulutusResourceV1.postKoulutus(dto);

        assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
        assertEquals(KOMO_OID1, result.getResult().getKomoOid());
        assertEquals(KOMOTO_OID1, result.getResult().getOid());
    }

    private KorkeakouluOpintoV1RDTO baseKorkeakouluopinto() {
        KorkeakouluOpintoV1RDTO dto = new KorkeakouluOpintoV1RDTO();
        dto.setTila(TarjontaTila.LUONNOS);
        dto.setOpetusJarjestajat(Sets.newHashSet(JARJESTAJA1));
        dto.setKoulutuksenAlkamisPvms(Sets.newHashSet(new Date()));
        dto.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.OPINTOKOKONAISUUS);
        dto.setOrganisaatio(new OrganisaatioV1RDTO(){{
            setOid(TARJOAJA1);
        }});
        dto.setOpintojenLaajuusPistetta("120");
        dto.setOpetuskielis(koodiUris(Sets.newHashSet("kieli_fi")));
        return dto;
    }
    
    private KoodiUrisV1RDTO koodiUris(Set<String> codeUris) {
        KoodiUrisV1RDTO dto = new KoodiUrisV1RDTO();
        Map<String, Integer> uris = new HashedMap();
        for (String uri : codeUris) {
            uris.put(uri, 1);
        }
        dto.setUris(uris);
        return dto;
    }

}
