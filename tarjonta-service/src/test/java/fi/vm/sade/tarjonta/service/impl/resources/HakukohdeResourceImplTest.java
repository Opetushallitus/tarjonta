package fi.vm.sade.tarjonta.service.impl.resources;

import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.resources.HakukohdeResource;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeValintaperusteetDTO;
import fi.vm.sade.tarjonta.service.types.ValinnanPisterajaTyyppi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kjsaila
 * Date: 03/12/13
 * Time: 14:24
 * To change this template use File | Settings | File Templates.
 */

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional()
public class HakukohdeResourceImplTest {

    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeResourceImplTest.class);

    @Autowired
    private HakukohdeDAO hakukohdeDao;

    @Autowired
    private HakuDAO hakuDao;

    @Autowired
    private HakukohdeResource hakukohdeResource;

    @Autowired
    private TarjontaFixtures fixtures;

    public static final String PAASY_JA_SOVELTUVUUSKOE = "valintakokeentyyppi_1";
    public static final String LISANAYTTO = "valintakokeentyyppi_2";
    public static final String LISAPISTE = "valintakokeentyyppi_5";

    @Test
    public void testGetHakukohteenValintaperusteet() throws IOException {
        LOG.info("testGetHakukohteenValintaperusteet()...");

        // Create test data
        Haku haku = fixtures.createHaku();
        hakuDao.insert(haku);

        Hakukohde hakukohde = fixtures.createHakukohde();
        hakukohde.setHaku(haku);

        Set<Valintakoe> valintakokeet = new HashSet<Valintakoe>();

        MonikielinenTeksti monikielinenTeksti = new MonikielinenTeksti();
        monikielinenTeksti.addTekstiKaannos("fi", "suomeksi");
        monikielinenTeksti.addTekstiKaannos("sv", "på svenska");
        monikielinenTeksti.addTekstiKaannos("en", "in english");

        Pisteraja paasykoeRaja = new Pisteraja();
        paasykoeRaja.setValinnanPisterajaTyyppi(ValinnanPisterajaTyyppi.PAASYKOE.value());
        paasykoeRaja.setAlinHyvaksyttyPistemaara(new BigDecimal("3.0"));
        paasykoeRaja.setAlinPistemaara(new BigDecimal("1.0"));
        paasykoeRaja.setYlinPistemaara(new BigDecimal("8.0"));

        Pisteraja lisanayttoRaja = new Pisteraja();
        lisanayttoRaja.setValinnanPisterajaTyyppi(ValinnanPisterajaTyyppi.LISAPISTEET.value());
        lisanayttoRaja.setAlinHyvaksyttyPistemaara(new BigDecimal("8.0"));
        lisanayttoRaja.setAlinPistemaara(new BigDecimal("4.0"));
        lisanayttoRaja.setYlinPistemaara(new BigDecimal("10.0"));

        Pisteraja lisapisteRaja = new Pisteraja();
        lisapisteRaja.setValinnanPisterajaTyyppi(ValinnanPisterajaTyyppi.LISAPISTEET.value());
        lisapisteRaja.setAlinHyvaksyttyPistemaara(new BigDecimal("1.0"));
        lisapisteRaja.setAlinPistemaara(new BigDecimal("0.5"));
        lisapisteRaja.setYlinPistemaara(new BigDecimal("5.0"));

        Set<Pisteraja> rajat1 = new HashSet<Pisteraja>();
        rajat1.add(paasykoeRaja);
        Set<Pisteraja> rajat2 = new HashSet<Pisteraja>();
        rajat2.add(lisanayttoRaja);
        Set<Pisteraja> rajat3 = new HashSet<Pisteraja>();
        rajat3.add(lisapisteRaja);

        Valintakoe paasykoe = new Valintakoe();
        paasykoe.setTyyppiUri(PAASY_JA_SOVELTUVUUSKOE);
        paasykoe.setKuvaus(monikielinenTeksti);
        paasykoe.setPisterajat(rajat1);
        hakukohde.addValintakoe(paasykoe);

        Valintakoe lisanaytto = new Valintakoe();
        lisanaytto.setTyyppiUri(LISANAYTTO);
        lisanaytto.setPisterajat(rajat2);
        hakukohde.addValintakoe(lisanaytto);

        Valintakoe lisapiste = new Valintakoe();
        lisapiste.setTyyppiUri(LISAPISTE);
        lisapiste.setPisterajat(rajat3);
        hakukohde.addValintakoe(lisapiste);

        PainotettavaOppiaine musiikki = new PainotettavaOppiaine();
        musiikki.setOppiaine("lukionpainottetavaoppiaine_mu#1");
        musiikki.setPainokerroin(new BigDecimal("15.0"));

        PainotettavaOppiaine enkku = new PainotettavaOppiaine();
        enkku.setOppiaine("lukionpainottetavaoppiaine_a1en#1");
        enkku.setPainokerroin(new BigDecimal("5.0"));

        PainotettavaOppiaine saksa = new PainotettavaOppiaine();
        saksa.setOppiaine("lukionpainottetavaoppiaine_b3de#2");
        saksa.setPainokerroin(new BigDecimal("7.0"));

        PainotettavaOppiaine fysiikka = new PainotettavaOppiaine();
        fysiikka.setOppiaine("lukionpainottetavaoppiaine_fy#2");
        fysiikka.setPainokerroin(new BigDecimal("3.0"));

        Set<PainotettavaOppiaine> painotukset = new HashSet<PainotettavaOppiaine>();
        painotukset.add(musiikki);
        painotukset.add(enkku);
        painotukset.add(saksa);
        painotukset.add(fysiikka);

        hakukohde.setPainotettavatOppiaineet(painotukset);

        hakukohdeDao.insert(hakukohde);

        HakukohdeValintaperusteetDTO hd = hakukohdeResource.getHakukohdeValintaperusteet(hakukohde.getOid());
        //ObjectMapper mapper = new ObjectMapper();
        //String json = mapper.writeValueAsString(hd);
        assert(hd.getPainokertoimet().get("MU_painokerroin").equals("15.0"));
        assert(hd.getPainokertoimet().get("A12_EN_painokerroin").equals("5.0"));
        assert(hd.getPainokertoimet().get("B33_DE_painokerroin").equals("7.0"));
        assert(hd.getPainokertoimet().get("FY_painokerroin").equals("3.0"));

        assert(hd.getPaasykoeHylkaysMax().equals(new BigDecimal("3.0")));
        assert(hd.getLisapisteMin().equals(new BigDecimal("0.5")));
        assert(hd.getLisanayttoHylkaysMax().equals(new BigDecimal("8.0")));
    }

}