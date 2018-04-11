package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.collect.Lists;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.KoodistoItemType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.shared.KoodiService;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class ImplicitDataPopulatorTest extends LukioV1Test {

    @Autowired
    KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired
    KoodiService koodiService;

    private HttpTestHelper httpTestHelper = new HttpTestHelper();
    private HttpServletRequest request = httpTestHelper.request;

    @Before
    public void init() {
       super.init();
    }

    private void mockLaajuusarvoRelations(String koulutusUri) {
        List<KoodiType> koodis = new ArrayList<>();
        for (KoodiV1RDTO koodiV1RDTO : getPossibleLaajusarvos()) {
            KoodiType koodi = new KoodiType();
            koodi.setKoodiUri(koodiV1RDTO.getUri());
            koodi.setVersio(koodiV1RDTO.getVersio());
            koodi.setKoodiArvo(koodiV1RDTO.getArvo());
            koodi.setKoodisto(new KoodistoItemType(){{
                setKoodistoUri(KoulutusImplicitDataPopulator.OPINTOJEN_LAAJUUS);
            }});
            koodis.add(koodi);
        }

        KoodiUriAndVersioType koodiWithVersion = new KoodiUriAndVersioType();
        koodiWithVersion.setKoodiUri(koulutusUri);
        koodiWithVersion.setVersio(1);

        List<KoodiType> alreadyMocked = koodiService.listKoodiByRelation(
                koodiWithVersion,
                false,
                SuhteenTyyppiType.SISALTYY
        );
        koodis.addAll(alreadyMocked);

        when(koodiService.listKoodiByRelation(
                koodiWithVersion,
                false,
                SuhteenTyyppiType.SISALTYY
        )).thenReturn(koodis);
    }

    @Test
    @Transactional
    public void testBug1124LaajuusarvoIsNotOverwritten() throws OIDCreationException {
        String komotoOid = oidServiceMock.getOid();
        when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn(komotoOid);
        when(oidService.get(TarjontaOidType.KOMO)).thenReturn(oidServiceMock.getOid());

        KoulutusLukioV1RDTO dto = LukioV1Test.baseDto();
        dto.setTila(TarjontaTila.PUUTTEELLINEN);
        KoodiV1RDTO opintojenLaajuusarvo = getPossibleLaajusarvos().get(1);
        dto.setOpintojenLaajuusarvo(opintojenLaajuusarvo);

        mockLaajuusarvoRelations(dto.getKoulutuskoodi().getUri());

        koulutusResourceV1.postKoulutus(dto, request);

        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(komotoOid);
        assertTrue(komoto.getOpintojenLaajuusarvoUri().contains(opintojenLaajuusarvo.getUri() + "#"));
    }

    private static List<KoodiV1RDTO> getPossibleLaajusarvos() {
        return Lists.newArrayList(
                new KoodiV1RDTO("laajuus_1", 1, "1"),
                new KoodiV1RDTO("laajuus_2", 1, "2")
        );
    }

}
