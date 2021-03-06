/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.tarjonta.TestUtilityBase;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeValintaperusteetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.types.ValinnanPisterajaTyyppi;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;


@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class
})
@Transactional()
public class HakukohdeResourceV1ImpValintaperusteetlTest extends TestUtilityBase {

    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeResourceV1ImpValintaperusteetlTest.class);

    private static final String PAASY_JA_SOVELTUVUUSKOE = "valintakokeentyyppi_1";
    private static final String LISANAYTTO = "valintakokeentyyppi_2";
    private static final String LISAPISTE = "valintakokeentyyppi_5";

    @Test
    public void testGetHakukohteenValintaperusteet() throws IOException {
        LOG.info("testGetHakukohteenValintaperusteet()...");

        // Create test data
        Haku haku = fixtures.createHaku();
        hakuDao.insert(haku);

        Hakukohde hakukohde = fixtures.createHakukohde();
        hakukohde.setHaku(haku);

        //Set<Valintakoe> valintakokeet = new HashSet<Valintakoe>();

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

        Pisteraja kokonaispisteet = new Pisteraja();
        kokonaispisteet.setValinnanPisterajaTyyppi(ValinnanPisterajaTyyppi.KOKONAISPISTEET.value());
        kokonaispisteet.setAlinHyvaksyttyPistemaara(new BigDecimal("9.0"));
        kokonaispisteet.setAlinPistemaara(new BigDecimal("0.5"));
        kokonaispisteet.setYlinPistemaara(new BigDecimal("5.0"));

        Valintakoe paasykoe = new Valintakoe();
        paasykoe.setTyyppiUri(PAASY_JA_SOVELTUVUUSKOE);
        paasykoe.setKuvaus(monikielinenTeksti);
        paasykoeRaja.setValintakoe(paasykoe);
        paasykoe.setPisterajat(Collections.singleton(paasykoeRaja));
        hakukohde.addValintakoe(paasykoe);


        Valintakoe lisanaytto = new Valintakoe();
        lisanaytto.setTyyppiUri(LISANAYTTO);
        lisanayttoRaja.setValintakoe(lisanaytto);
        kokonaispisteet.setValintakoe(lisanaytto);
        lisanaytto.getPisterajat().add(lisanayttoRaja);
        lisanaytto.getPisterajat().add(kokonaispisteet);
        hakukohde.addValintakoe(lisanaytto);

        Valintakoe lisapiste = new Valintakoe();
        lisapiste.setTyyppiUri(LISAPISTE);
        lisapisteRaja.setValintakoe(lisapiste);
        lisapiste.setPisterajat(Collections.singleton(lisapisteRaja));
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

        Set<PainotettavaOppiaine> painotukset = new HashSet<>();
        painotukset.add(musiikki);
        painotukset.add(enkku);
        painotukset.add(saksa);
        painotukset.add(fysiikka);

        hakukohde.setPainotettavatOppiaineet(painotukset);

        hakukohdeDao.insert(hakukohde);

        ResultV1RDTO<HakukohdeValintaperusteetV1RDTO> result = hakukohdeResource.findValintaperusteetByOid(hakukohde.getOid());

        HakukohdeValintaperusteetV1RDTO hd = result.getResult();
        //ObjectMapper mapper = new ObjectMapper();
        //String json = mapper.writeValueAsString(hd);
        assertTrue(hd.getPainokertoimet().get("MU_painokerroin").equals("15.0"));
        assertTrue(hd.getPainokertoimet().get("A12_EN_painokerroin").equals("5.0"));
        assertTrue(hd.getPainokertoimet().get("B33_DE_painokerroin").equals("7.0"));
        assertTrue(hd.getPainokertoimet().get("FY_painokerroin").equals("3.0"));

        assertTrue(hd.getPaasykoeHylkaysMax().equals(new BigDecimal("3.0")));
        assertTrue(hd.getLisapisteMin().equals(new BigDecimal("0.5")));
        assertTrue(hd.getLisanayttoHylkaysMax().equals(new BigDecimal("8.0")));
        assertTrue(hd.getHakuKohdejoukkoUri().equals(haku.getKohdejoukkoUri()));
    }

}
