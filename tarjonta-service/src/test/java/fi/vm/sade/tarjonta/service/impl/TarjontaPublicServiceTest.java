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
package fi.vm.sade.tarjonta.service.impl;

import fi.vm.sade.tarjonta.TestUtilityBase;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.types.*;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.Assert.*;


@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@Transactional
@ActiveProfiles("embedded-solr")
public class TarjontaPublicServiceTest extends TestUtilityBase {

    private static final String YHTEISHAKU = "http://hakutapa/yhteishaku";

    private static final String ORGANISAATIO_A = "1.2.3.4.5";

    private static final String ORGANISAATIO_B = "1.2.3.4.6";

    private static final String KOMOTO_OID = "11.12.23.34.56";

    private static final String HAKUKOHDE_OID = "12.13.24.35.57";

    private static final String HAKU_OID = "0.1.2.3.4.5.67";

    private static final String KOULUTUSKOODI = "uri:koulutuskoodi";
	private static final String KOULUTUSOHJELMAKOODI1 = "uri:koulutusohjelmakoodi1";
	private static final String KOULUTUSOHJELMAKOODI2 = "uri:koulutusohjelmakoodi2";
	private static final String KOULUTUSASTEKOODI = "uri:koulutuasteLukio";

    @Before
    public void setUp() {

        fixtures.deleteAll();

        Koulutusmoduuli koulutusmoduuli;
        KoulutusmoduuliToteutus koulutusmoduuliToteutus;

        // jaettu haku
        Haku haku = fixtures.createHaku();
        haku.setOid(HAKU_OID);
        haku.setNimiFi("yhteishaku 1");
        haku.setHakutapaUri(YHTEISHAKU);
        hakuDAO.insert(haku);

        // 0. koulutusmoduuli+toteutus lisätään testaamaan hakukohteiden haun oikeellisuutta sekä yhden koulutusmoduulin lukua.
        koulutusmoduuli = fixtures.createTutkintoOhjelma();
        koulutusmoduuli.setKoulutusUri(KOULUTUSKOODI);
        koulutusmoduuli.setKoulutusohjelmaUri(KOULUTUSOHJELMAKOODI1);
        koulutusmoduuliDAO.insert(koulutusmoduuli);
        koulutusmoduuliToteutus = fixtures.createTutkintoOhjelmaToteutus(KOMOTO_OID);
        koulutusmoduuliToteutus.setTarjoaja(ORGANISAATIO_A);
        koulutusmoduuliToteutus.setKoulutusmoduuli(koulutusmoduuli);
        koulutusmoduuliToteutus.setKoulutusasteUri(KOULUTUSASTEKOODI);
        koulutusmoduuliToteutusDAO.insert(koulutusmoduuliToteutus);

        // 1. hakukohde oid is given to be able to test lueHakukohde method.
        Hakukohde hakukohde = fixtures.createHakukohdeWithGivenOid(HAKUKOHDE_OID);//fixtures.createHakukohde();
        hakukohde.setHaku(haku);
        hakukohde.setHakukohdeNimi("Peltikorjaajan perustutkinto");
        hakukohde.setHakukohdeKoodistoNimi("Peltikorjaajan perustutkinto");
        hakukohde.setAlinHyvaksyttavaKeskiarvo(7.5d);
        hakukohde.setTila(TarjontaTila.VALMIS);

        PainotettavaOppiaine painotettavaOppiaine = new PainotettavaOppiaine();
        painotettavaOppiaine.setOppiaine("Matematiikka");
        painotettavaOppiaine.setPainokerroin( new BigDecimal("5.0"));
        hakukohde.getPainotettavatOppiaineet().add(painotettavaOppiaine);

        PainotettavaOppiaine painotettavaOppiaine1 = new PainotettavaOppiaine();
        painotettavaOppiaine1.setPainokerroin( new BigDecimal("6.0") );
        painotettavaOppiaine1.setOppiaine("Englanti");
        hakukohde.getPainotettavatOppiaineet().add(painotettavaOppiaine1);


        hakukohde = hakukohdeDAO.insert(hakukohde);

        //1. hakukohde valintakoe
        Valintakoe valintakoe = fixtures.createValintakoe();

        Pisteraja pisteraja = new Pisteraja();
        pisteraja.setValintakoe(valintakoe);
        pisteraja.setValinnanPisterajaTyyppi("Paasykoe");
        pisteraja.setAlinHyvaksyttyPistemaara(new BigDecimal("5.0"));
        pisteraja.setAlinPistemaara(new BigDecimal("4.0"));
        pisteraja.setYlinPistemaara(new BigDecimal("6.0"));
        valintakoe.getPisterajat().add(pisteraja);

        hakukohde.addValintakoe(valintakoe);
        hakukohdeDAO.update(hakukohde);
        hakukohde = hakukohdeDAO.read(hakukohde.getId());

        // 1. koulutusmoduuli+toteutus
        koulutusmoduuli = fixtures.createTutkintoOhjelma();
        koulutusmoduuli.setKoulutusUri(KOULUTUSKOODI);
        koulutusmoduuli.setKoulutusohjelmaUri(KOULUTUSOHJELMAKOODI2);
        koulutusmoduuliDAO.insert(koulutusmoduuli);
        koulutusmoduuliToteutus = fixtures.createTutkintoOhjelmaToteutus();
        koulutusmoduuliToteutus.setTarjoaja(ORGANISAATIO_A);
        koulutusmoduuliToteutus.setKoulutusmoduuli(koulutusmoduuli);
        koulutusmoduuliToteutusDAO.insert(koulutusmoduuliToteutus);

        // liitä koulutus hakukohteeseen
        hakukohde.addKoulutusmoduuliToteutus(koulutusmoduuliToteutus);
        hakukohdeDAO.update(hakukohde);
        hakukohde = hakukohdeDAO.read(hakukohde.getId());

        // 2. hakukohde
        Hakukohde hakukohde2 = fixtures.createHakukohde();
        hakukohde2.setHakukohdeNimi("Taidemaalarin erikoistutkinto");
        hakukohde2.setHakukohdeKoodistoNimi("Taidemaalarin erikoistutkinto");
        hakukohde2.setHaku(haku);
        hakukohde2.setTila(TarjontaTila.VALMIS);
        hakukohdeDAO.insert(hakukohde2);

        // 2. koulutusmoduuli+toteutus, eri toteuttaja organisaatio
        koulutusmoduuli = fixtures.createTutkintoOhjelma();
        koulutusmoduuliDAO.insert(koulutusmoduuli);
        KoulutusmoduuliToteutus koulutusmoduuliToteutus2 = fixtures.createTutkintoOhjelmaToteutus();
        koulutusmoduuliToteutus2.setTarjoaja(ORGANISAATIO_B);
        koulutusmoduuliToteutus2.setKoulutusmoduuli(koulutusmoduuli);

        // liitä koulutus 2:een hakukohteeseen
        koulutusmoduuliToteutusDAO.insert(koulutusmoduuliToteutus2);
        hakukohde2.addKoulutusmoduuliToteutus(koulutusmoduuliToteutus2);
        hakukohdeDAO.update(hakukohde2);
        hakukohde2 = hakukohdeDAO.read(hakukohde2.getId());

        //Liita koulutus 1:een hakukohteeseen
        hakukohde.addKoulutusmoduuliToteutus(koulutusmoduuliToteutus2);
        hakukohdeDAO.update(hakukohde);

        //Liitetään hakukohteet hakuun
        haku.addHakukohde(hakukohde2);
        //Hakukohde hakukohde1 = hakukohdeDAO.findBy("oid", HAKUKOHDE_OID).get(0);
        haku.addHakukohde(hakukohde);
        hakuDAO.update(haku);

    }


    @Test
    public void testPisterajat()  {
    	Hakukohde hk = hakukohdeDAO.findHakukohdeByOid(HAKUKOHDE_OID);
        Valintakoe valintakoe = null;
        for (Valintakoe valintakoe1 : hk.getValintakoes()) {
            valintakoe = valintakoe1;
        }
        assertTrue(valintakoe.getPisterajat().size() > 0);
    }

    @Test
    public void testPainotettavatOppiaineet() {
    	Hakukohde hk = hakukohdeDAO.findHakukohdeByOid(HAKUKOHDE_OID);
       assertTrue(hk.getPainotettavatOppiaineet().size() > 0);
    }

    @Test
    public void testHaeHakukohteenValintakokeet() {

        HaeHakukohteenValintakokeetHakukohteenTunnisteellaKyselyTyyppi kyselyTyyppi = new HaeHakukohteenValintakokeetHakukohteenTunnisteellaKyselyTyyppi();
        kyselyTyyppi.setHakukohteenTunniste(HAKUKOHDE_OID);

        HaeHakukohteenValintakokeetHakukohteenTunnisteellaVastausTyyppi vastaus = service.haeHakukohteenValintakokeetHakukohteenTunnisteella(kyselyTyyppi);

        assertNotNull(vastaus.getHakukohteenValintaKokeet());
        assertEquals(1, vastaus.getHakukohteenValintaKokeet().size());

    }

    @Test
    public void testEtsiKoulutusmoduulit() {

        HaeKoulutusmoduulitKyselyTyyppi kysely = new HaeKoulutusmoduulitKyselyTyyppi();
        kysely.setKoulutuskoodiUri(KOULUTUSKOODI);
        HaeKoulutusmoduulitVastausTyyppi vastaus = service.haeKoulutusmoduulit(kysely);

        assertEquals(2, vastaus.getKoulutusmoduuliTulos().size());

        kysely.setKoulutusohjelmakoodiUri(KOULUTUSOHJELMAKOODI1);
        vastaus = service.haeKoulutusmoduulit(kysely);

        assertEquals(1, vastaus.getKoulutusmoduuliTulos().size());
        assertEquals(KOULUTUSOHJELMAKOODI1, vastaus.getKoulutusmoduuliTulos().get(0).getKoulutusmoduuli().getKoulutusohjelmakoodiUri());

        kysely = new HaeKoulutusmoduulitKyselyTyyppi();
        vastaus = service.haeKoulutusmoduulit(kysely);

        assertTrue(vastaus.getKoulutusmoduuliTulos().size() > 2);

    }

    @Test
    public void testLueKoulutus() {

    	LueKoulutusKyselyTyyppi kysely = new LueKoulutusKyselyTyyppi();
    	kysely.setOid(KOMOTO_OID);

    	LueKoulutusVastausTyyppi vastaus = service.lueKoulutus(kysely);

        assertNotNull(vastaus);

        assertTrue(vastaus.getOpetuskieli().get(0).getUri().equals("http://kielet/fi"));
        assertEquals(1, vastaus.getYhteyshenkiloTyyppi().size());
       // assertTrue(vastaus.getKoulutusaste().getUri().equals(KOULUTUSASTEKOODI));
    }

    @Test
    public void testLueHakukohde() {

    	LueHakukohdeKyselyTyyppi kysely = new LueHakukohdeKyselyTyyppi();
    	kysely.setOid(HAKUKOHDE_OID);

    	LueHakukohdeVastausTyyppi vastaus = service.lueHakukohde(kysely);

        assertNotNull(vastaus);
        assertTrue(vastaus.getHakukohde().getHakukohdeNimi().equals("Peltikorjaajan perustutkinto"));
    }

    @Test
    public void testHaeTarjonta() {
    	TarjontaTyyppi vastaus = service.haeTarjonta(HAKU_OID);
    	assertEquals(2, vastaus.getHakukohde().size());
    	assertTrue(vastaus.getHakukohde().get(0).getHakukohdeNimi().equals("Peltikorjaajan perustutkinto")
    			|| vastaus.getHakukohde().get(1).getHakukohdeNimi().equals("Peltikorjaajan perustutkinto"));
    }

}

