/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.dao.impl;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.copy.EntityToJsonHelper;
import fi.vm.sade.tarjonta.service.impl.resources.v1.process.MassCopyProcess;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ProcessV1RDTO;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;


@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class
})
@Transactional
public class MassakopiointiTest extends TestData {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MassakopiointiTest.class);
    private static final Date DATE_2014 = (new DateTime(KOULUTUS_START_DATE)).plusYears(1).toDate();
    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    @Autowired
    private HakukohdeDAOImpl hakukohdeDAO;

    @Autowired
    private TarjontaFixtures fixtures;

    AtomicInteger c = new AtomicInteger(0);

    @Before
    public void setUp() throws OIDCreationException {
        EntityManager em = hakukohdeDAO.getEntityManager();
        super.initializeData(em, fixtures);
        Preconditions.checkNotNull(oidService);
        Mockito.reset(oidService);
        Mockito.stub(oidService.get(Mockito.any(TarjontaOidType.class))).toAnswer((Answer<String>) invocation -> {
            if (invocation.getArguments()[0] == null) {
                throw new IllegalArgumentException("type was null???");
            }
            return (invocation.getArguments()[0] == null ? "null-type-wtf" : invocation.getArguments()[0].toString()).concat(Long.toString(c.incrementAndGet()));
        });
    }

    @Test
    public void testKoulutusConversions() {
        String json = null;
        try {
            json = EntityToJsonHelper.convertToJson(getPersistedKomoto1());
        } catch (Exception ex) {
            fail("conversion error from entity to json : " + ex.getMessage());
        }
        assertNotNull("KoulutusmoduuliToteutus - not nullable", json);
        assertTrue("conversion to json failed", json.length() > 0 && !json.equals("null"));
        KoulutusmoduuliToteutus komoto = null;
        try {
            komoto = (KoulutusmoduuliToteutus) EntityToJsonHelper.convertToEntity(json, KoulutusmoduuliToteutus.class);
        } catch (Exception ex) {
            fail("conversion error from json to entity : " + ex.getMessage());
        }
        assertNotNull(komoto);
        assertEquals(null, komoto.getId());
        assertEquals(KOMOTO_OID_1, komoto.getOid());
    }

    @Test
    public void testHakukohdeConversions() throws IOException {
        String json = null;
        try {
            json = EntityToJsonHelper.convertToJson(kohde1);
        } catch (Exception ex) {
            fail("conversion error from entity to json : " + ex.getMessage());
        }

        assertNotNull("Hakukohde - not nullable", json);
        assertTrue("conversion to json failed", json.length() > 0 && !json.equals("null"));

        Hakukohde hakukohde = null;
        try {
            hakukohde = (Hakukohde) EntityToJsonHelper.convertToEntity(json, Hakukohde.class);
        } catch (Exception ex) {
            fail("conversion error from json to entity : " + ex.getMessage());
        }
        assertNotNull(hakukohde);
        assertEquals(null, hakukohde.getId());
        assertEquals(HAKUKOHDE_OID1, hakukohde.getOid());
    }

    @Test
    public void testCopy2() {
        final Haku from = getHaku1();
        from.setOrganisationOids(new String[]{"ooid1", "ooid2"});
        from.setTarjoajaOids(new String[]{"toid1", "toid2"});
        Hakuaika ha = new Hakuaika();
        ha.setAlkamisPvm(new Date());
        ha.setPaattymisPvm(new Date(ha.getAlkamisPvm().getTime() + 10000));
        from.addHakuaika(ha);
        Hakuaika ha2 = new Hakuaika();
        ha2.setAlkamisPvm(new Date());
        ha2.setPaattymisPvm(new Date(ha2.getAlkamisPvm().getTime() + 10000));
        ha2.setHaku(from);
        from.addHakuaika(ha2);
        from.setOrganisationOids(new String[]{"o1", "o2"});
        from.setTarjoajaOids(new String[]{"o1", "o2"});
        ha.setHaku(from);
        getPersistedKomoto1().setSijoittuminenTyoelamaan(new MonikielinenTeksti("fi", "blaah"));
        getPersistedKomoto1().setKoulutusohjelmanValinta(new MonikielinenTeksti("fi", "bvlaahh"));
        getPersistedKomoto1().getTekstit().put(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN, new MonikielinenTeksti("fi", "blaah"));
        getPersistedKomoto1().getKoulutusmoduuli().getTekstit().put(KomoTeksti.KOULUTUKSEN_RAKENNE, new MonikielinenTeksti("fi", "blaah2"));
        getPersistedKomoto1().setKieliValikoima("KIEEEL", Lists.newArrayList("a1", "a2"));
        super.persist(getPersistedKomoto1());
        getPersistedKomoto1().setTila(TarjontaTila.JULKAISTU);
        kohde1.addKoulutusmoduuliToteutus(getPersistedKomoto2());
        getPersistedKomoto2().addHakukohde(kohde1);
        kohde1.setHakuaika(ha);
        super.persist(getPersistedKomoto2());

        kohde1.setTila(TarjontaTila.JULKAISTU);
        kohde2.setTila(TarjontaTila.JULKAISTU);
        kohde3.setTila(TarjontaTila.JULKAISTU);

        kohde1.setLisatiedot(new MonikielinenTeksti());
        kohde1.getLisatiedot().addTekstiKaannos("fi", "lisätieto");
        kohde1.getValintakoes().clear();
        kohde1.addValintakoe(koe1);
        koe1.setHakukohde(kohde1);

        HakukohdeLiite liite = new HakukohdeLiite();
        liite.setHakukohde(kohde1);
        liite.setHakukohdeLiiteNimi("liiteNimi");
        liite.setKuvaus(new MonikielinenTeksti("fi", "kuvaus"));
        liite.setLiitetyyppi("tyyppi");
        liite.setErapaiva(new Date());
        kohde1.addLiite(liite);
        super.persist(kohde1);


        super.persist(koe1);

        super.persist(from);

        assertFalse(0 == from.getNimi().getKaannoksetAsList().size());


        HashMap<String, Hakukohde> hakukohdes = Maps.newHashMap();

        int c = 0;
        for (Hakukohde hk : from.getHakukohdes()) {
            final String nimi = Integer.toString(c++);
            hk.setHakukohdeNimi(nimi);
            hakukohdes.put(nimi, hk);
            super.persist(hk);
        }

        ProcessV1RDTO processV1RDTO = MassCopyProcess.getDefinition(from.getOid(), null, request); // null = do not skip process steps
        copyProcess.setState(processV1RDTO);
        copyProcess.run();
        assertEquals("DONE", processV1RDTO.getParameters().get("process_step"));

        final Haku h = hakuDAO.findByOid(processV1RDTO.getParameters().get(MassCopyProcess.TO_HAKU_OID));

        compareHaku(h, from);

        assertNotNull(h.getOid());
        assertFalse(from.getOid().equals(h.getOid()));
        assertFalse(0 == from.getNimi().getKaannoksetAsList().size());

        final String toNimi = h.getNimi().getKaannoksetAsList().get(0).getArvo();
        final String fromNimi = from.getNimi().getKaannoksetAsList().get(0).getArvo();
        assertNotSame(toNimi, fromNimi);

        assertEquals(1, h.getHakukohdes().size());

        for (Hakukohde hk : h.getHakukohdes()) {
            compareHakukohde(hk, hakukohdes.get(hk.getHakukohdeNimi()));

            for (KoulutusmoduuliToteutus kt : hk.getKoulutusmoduuliToteutuses()) {
                assertEquals(1, hk.getKoulutusmoduuliToteutuses().size());
                compareKomoto(kt, getPersistedKomoto1());
            }
        }
    }

    private void compareHaku(Haku copy, Haku orig) {
        assertFalse(Objects.equal(copy.getOid(), orig.getOid()));
        assertTrue(orig.getOrganisationOids().length > 0);
        assertTrue(orig.getTarjoajaOids().length > 0);
        assertSame(0, Sets.difference(Sets.newHashSet(orig.getOrganisationOids()), Sets.newHashSet(copy.getOrganisationOids())).size());
        assertSame(0, Sets.difference(Sets.newHashSet(orig.getTarjoajaOids()), Sets.newHashSet(copy.getTarjoajaOids())).size());
        assertEquals(2, haku1.getHakuaikas().size());
    }


    private void compareKomoto(KoulutusmoduuliToteutus copy, KoulutusmoduuliToteutus orig) {
        assertEquals(KOMOTO_OID_1, orig.getOid());
        assertFalse(copy.getOid().equals(orig.getOid()));
        assertEquals(1, copy.getKoulutuksenAlkamisPvms().size());
        assertEquals(DATE_2014, copy.getKoulutuksenAlkamisPvms().iterator().next());
        assertEquals(new Integer(orig.getAlkamisVuosi() + 1), copy.getAlkamisVuosi());
        assertEquals(orig.getAlkamiskausiUri(), copy.getAlkamiskausiUri());
        assertEquals(orig.getKoulutuslajis().size(), copy.getKoulutuslajis().size());

        //komoton tekstit kopioituu
        assertNotSame(orig.getTekstit().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN).getId(), copy.getTekstit().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN).getId());
        assertEquals(orig.getTekstit().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN).getTekstiForKieliKoodi("fi"), copy.getTekstit().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN).getTekstiForKieliKoodi("fi"));

        //komon tekstit ei kopioidu
        assertEquals(orig.getKoulutusmoduuli().getTekstit().get(KomoTeksti.KOULUTUKSEN_RAKENNE).getId(), copy.getKoulutusmoduuli().getTekstit().get(KomoTeksti.KOULUTUKSEN_RAKENNE).getId());
        assertEquals(orig.getKoulutusmoduuli().getTekstit().get(KomoTeksti.KOULUTUKSEN_RAKENNE).getTekstiForKieliKoodi("fi"), copy.getKoulutusmoduuli().getTekstit().get(KomoTeksti.KOULUTUKSEN_RAKENNE).getTekstiForKieliKoodi("fi"));
        if (orig.getOid().equals(getPersistedKomoto1().getOid())) {
            //komoto1 testaus
            assertEquals(orig.getKieliValikoima("KIEEEL").getKielet().size(), copy.getKieliValikoima("KIEEEL").getKielet().size());
        }
    }


    private static Date dateToNextYear(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusYears(1).toDate();
    }

    private void compareHakukohde(Hakukohde copy, Hakukohde orig) {
        LOG.info("comparing hakukohde copy");
        assertEquals(orig.getAlinHyvaksyttavaKeskiarvo(), copy.getAlinHyvaksyttavaKeskiarvo());
        assertEquals(orig.getAlinValintaPistemaara(), copy.getAlinValintaPistemaara());
        assertEquals(0, copy.getAloituspaikatLkm());
        assertEquals(orig.getEdellisenVuodenHakijat(), copy.getEdellisenVuodenHakijat());
        if (orig.getHakuaika() != null) {
            assertEquals(dateToNextYear(orig.getHakuaika().getAlkamisPvm()), copy.getHakuaika().getAlkamisPvm());
            assertEquals(dateToNextYear(orig.getHakuaika().getPaattymisPvm()), copy.getHakuaika().getPaattymisPvm());
        }
        assertEquals(orig.getHakukelpoisuusVaatimukset().size(), copy.getHakukelpoisuusVaatimukset().size());
        assertEquals(orig.getHakukelpoisuusVaatimusKuvaus(), copy.getHakukelpoisuusVaatimusKuvaus());
        assertEquals(orig.getHakukohdeKoodistoNimi(), copy.getHakukohdeKoodistoNimi());
        assertEquals(orig.getHakukohdeMonikielinenNimi(), copy.getHakukohdeMonikielinenNimi());
        assertEquals(orig.getHakukohdeNimi(), copy.getHakukohdeNimi());
        if (orig.getOid().equals(kohde1.getOid())) {
            assertEquals(1, copy.getKoulutusmoduuliToteutuses().size()); //toinen koulutus oli luonnos
        } else {
            assertEquals(orig.getKoulutusmoduuliToteutuses().size(), copy.getKoulutusmoduuliToteutuses().size());
        }
        assertEquals(orig.getLiites().size(), copy.getLiites().size());

        //oletus testidatassa vain nolla tai yksi valintakoetta
        if (orig.getValintakoes().size() == 1) {
            LOG.debug("tarkistetaan valintakoe");
            final Valintakoe origV = orig.getValintakoes().iterator().next();
            final Valintakoe copyV = copy.getValintakoes().iterator().next();
            assertEquals(origV.getKieli(), copyV.getKieli());
            assertEquals(origV.getKuvaus().getKaannoksetAsList().size(), copyV.getKuvaus().getKaannoksetAsList().size());
            if (origV.getLisanaytot() != null) {
                assertEquals(origV.getLisanaytot().getKaannoksetAsList().size(), copyV.getLisanaytot().getKaannoksetAsList().size());
            }
        } else {
            LOG.debug("no valintakoes");
        }

        //oletus testidatassa vain nolla tai yksi liitettä
        if (orig.getLiites().size() == 1) {
            LOG.debug("tarkistetaan liite");
            final HakukohdeLiite origL = orig.getLiites().iterator().next();
            final HakukohdeLiite copyL = copy.getLiites().iterator().next();
            assertEquals(origL.getKuvaus().getKaannoksetAsList().size(), copyL.getKuvaus().getKaannoksetAsList().size());
            assertEquals(origL.getLiitetyyppi(), copyL.getLiitetyyppi());
        } else {
            LOG.debug("no liites");
        }

        assertEquals(orig.getLiitteidenToimitusOsoite(), copy.getLiitteidenToimitusOsoite());
        assertEquals(orig.getLiitteidenToimitusPvm(), copy.getLiitteidenToimitusPvm());
        if (orig.getLisatiedot() != null) {
            assertEquals(orig.getLisatiedot().getKaannoksetAsList().size(), copy.getLisatiedot().getKaannoksetAsList().size());
            LOG.debug("tarkistetaan lisätietoja");
        }
        assertEquals(orig.getValintakoes().size(), copy.getValintakoes().size());

        assertFalse(orig.getOid().equals(copy.getOid()));
    }

}
