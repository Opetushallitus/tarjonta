package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.impl.resources.v1.hakukohde.validation.HakukohdeValidationMessages;
import fi.vm.sade.tarjonta.service.resources.v1.HakukohdeV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@ActiveProfiles("embedded-solr")
@Transactional
public class HakukohdeResourceImplV1Test {

    @Autowired
    KoulutusResourceImplV1Test koulutusResourceTest;

    @Autowired
    HakukohdeV1Resource hakukohdeV1Resource;

    @Autowired
    HakuDAO hakuDAO;

    private static final String HAUN_OID = "hakuOid";

    @Test
    public void testCreateOpintokokonaisuusHakukohdeFailsWhenMissingRequiredData() throws OIDCreationException {
        HakukohdeV1RDTO hakukohde = baseHakukohde();
        ResultV1RDTO<HakukohdeV1RDTO> result = hakukohdeV1Resource.createHakukohde(hakukohde);
        assertEquals(VALIDATION, result.getStatus());
        assertEquals(2, result.getErrors().size());
        assertTrue(containsError(result.getErrors(), HAKUKOHDE_KOULUTUS_MISSING));
        assertTrue(containsError(result.getErrors(), HAKUKOHDE_HAKU_MISSING));
    }

    @Test
    public void testCreateOpintokokonaisuusHakukohde() throws OIDCreationException {
        Haku haku = insertHaku();
        KoulutusV1RDTO koulutus = koulutusResourceTest.getLuonnosOpintokokonaisuus();

        HakukohdeV1RDTO hakukohde = baseHakukohde();
        hakukohde.setHakukohdeKoulutusOids(Lists.newArrayList(koulutus.getOid()));
        hakukohde.setHakukohteenNimet(ImmutableMap.of("kieli_fi", "hakukohteen nimi"));
        hakukohde.setHakuOid(haku.getOid());

        ResultV1RDTO<HakukohdeV1RDTO> result = hakukohdeV1Resource.createHakukohde(hakukohde);
        assertEquals(OK, result.getStatus());
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
        haku.setOid(HAUN_OID);
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
