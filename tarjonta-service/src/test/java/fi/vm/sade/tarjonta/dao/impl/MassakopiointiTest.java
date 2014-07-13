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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakuaika;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.copy.EntityToJsonHelper;
import fi.vm.sade.tarjonta.service.impl.resources.v1.process.MassCopyProcess;
import fi.vm.sade.tarjonta.service.resources.v1.HakuV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ProcessV1RDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;

/**
 *
 * @author jani
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class MassakopiointiTest extends TestData {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MassakopiointiTest.class);

    @Autowired(required = true)
    private HakukohdeDAOImpl hakukohdeDAO;
    @Autowired(required = true)
    private HakuDAOImpl hakuDAO;
    @Autowired(required = true)
    private TarjontaFixtures fixtures;
    private EntityManager em;

    @Autowired(required = true)
    private HakuV1Resource hakuResource;

    @Autowired(required = true)
    private MassCopyProcess copyProcess;

    @Autowired(required = true)
    private OidService oidService;

    AtomicInteger c = new AtomicInteger(0);

    @Before
    public void setUp() throws OIDCreationException {
        em = hakukohdeDAO.getEntityManager();
        super.initializeData(em, fixtures);
        Preconditions.checkNotNull(oidService);
        Mockito.reset(oidService);
        Mockito.stub(oidService.get(Mockito.any(TarjontaOidType.class))).toAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                if (invocation.getArguments()[0] == null) {
                    throw new IllegalArgumentException("type was null???");
                }
                return (invocation.getArguments()[0] == null ? "null-type-wtf" : invocation.getArguments()[0].toString()).concat(Long.toString(c.incrementAndGet()));
            }
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
        Hakuaika ha = new Hakuaika();
        ha.setAlkamisPvm(new Date());
        ha.setPaattymisPvm(new Date(ha.getAlkamisPvm().getTime() + 10000));
        from.addHakuaika(ha);
        from.setOrganisationOids(new String[]{"o1", "o2"});
        from.setTarjoajaOids(new String[]{"o1", "o2"});
        ha.setHaku(from);
        super.persist(from);

        HashMap<String, Hakukohde> hakukohdes = Maps.newHashMap();

        int c = 0;
        for (Hakukohde hk : from.getHakukohdes()) {
            final String nimi = Integer.toString(c++);
            hk.setHakukohdeNimi(nimi);
            hakukohdes.put(nimi, hk);
            super.persist(hk);
        }

        ProcessV1RDTO processV1RDTO = MassCopyProcess.getDefinition(from.getOid(), null); // null = do not skip process steps
        copyProcess.setState(processV1RDTO);
        copyProcess.run();
        processV1RDTO = copyProcess.getState();

        final Haku h = hakuDAO.findByOid(processV1RDTO.getParameters().get(MassCopyProcess.TO_HAKU_OID));

        assertNotNull(h.getOid());
        assertFalse(from.getOid().equals(h.getOid()));

        assertEquals(3, h.getHakukohdes().size());

        for (Hakukohde hk : h.getHakukohdes()) {
            compareHakukohde(hk, hakukohdes.get(hk.getHakukohdeNimi()));
        }
    }

    private void compareHakukohde(Hakukohde copy, Hakukohde orig) {
        LOG.info("comparing hakukohde copy");
        assertEquals(orig.getAlinHyvaksyttavaKeskiarvo(), copy.getAlinHyvaksyttavaKeskiarvo());
        assertEquals(orig.getAlinValintaPistemaara(), copy.getAlinValintaPistemaara());
        assertEquals(orig.getAloituspaikatLkm(), copy.getAloituspaikatLkm());
        assertEquals(orig.getEdellisenVuodenHakijat(), copy.getEdellisenVuodenHakijat());
        if (orig.getHakuaika() != null) {
            assertEquals(orig.getHakuaika().getAlkamisPvm(), copy.getHakuaika().getAlkamisPvm());
            assertEquals(orig.getHakuaika().getPaattymisPvm(), copy.getHakuaika().getPaattymisPvm());
        }
        assertEquals(orig.getHakukelpoisuusVaatimukset().size(), copy.getHakukelpoisuusVaatimukset().size());
        assertEquals(orig.getHakukelpoisuusVaatimusKuvaus(), copy.getHakukelpoisuusVaatimusKuvaus());
        assertEquals(orig.getHakukohdeKoodistoNimi(), copy.getHakukohdeKoodistoNimi());
        assertEquals(orig.getHakukohdeMonikielinenNimi(), copy.getHakukohdeMonikielinenNimi());
        assertEquals(orig.getHakukohdeNimi(), copy.getHakukohdeNimi());
        assertEquals(orig.getKoulutusmoduuliToteutuses().size(), copy.getKoulutusmoduuliToteutuses().size());
        assertEquals(orig.getLiites().size(), copy.getLiites().size());
        assertEquals(orig.getLiitteidenToimitusOsoite(), copy.getLiitteidenToimitusOsoite());
        assertEquals(orig.getLiitteidenToimitusPvm(), copy.getLiitteidenToimitusPvm());
        assertEquals(orig.getLisatiedot(), copy.getLisatiedot());
        assertEquals(orig.getValintakoes().size(), copy.getValintakoes().size());

        assertFalse(orig.getOid().equals(copy.getOid()));
    }

}
