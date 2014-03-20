/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 *//*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.collect.Lists;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.SecurityAwareTestBase;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.resources.v1.HakuV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuaikaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.search.it.TarjontaSearchServiceTest;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import java.util.Date;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

/**
 * mvn test -Dtest=fi.vm.sade.tarjonta.service.impl.resources.v1.HakuResourceImplV1Test
 *
 * @author mlyly
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("embedded-solr")
@Transactional()
public class HakuResourceImplV1Test extends SecurityAwareTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(HakuResourceImplV1Test.class);

    @Autowired
    private HakuV1Resource hakuResource;

    @Autowired
    private OidService oidService;

    @Autowired
    private KoodiService koodiService;

    @Before
    public void setUp() throws Exception {
        LOG.info("setUp()");

        // Stub oid service
        Mockito.stub(oidService.get(TarjontaOidType.HAKU)).toReturn("1.2.3.4.5");

        // Test koodisto values that has to be stubbed
        TarjontaSearchServiceTest.stubKoodi(koodiService, "kieli_fi", "FI");
        TarjontaSearchServiceTest.stubKoodi(koodiService, "kausi_k", "K");
        TarjontaSearchServiceTest.stubKoodi(koodiService, "hakutapa_01", "01");
        TarjontaSearchServiceTest.stubKoodi(koodiService, "hakutyyppi_01", "01");
        TarjontaSearchServiceTest.stubKoodi(koodiService, "haunkohdejoukko_12", "12");
    }

    @After
    public void tearDown() {
        LOG.info("tearDown()");
    }

    @Test
    public void testCreateInvaludAndValidHakus() {
        LOG.info("testXXX()...");

        HakuV1RDTO dto = null;
        ResultV1RDTO<HakuV1RDTO> result = null;

        // Null haku should fail... badly
        try {
            result = hakuResource.createHaku(dto);
            fail("Creating of NUL haku should have failed.");
        } catch (Throwable ex) {
            // OK
        }

        //
        // Try to create totally empty haku, shoud fail with ERROR + lots of valudation messages
        //
        dto = new HakuV1RDTO();
        result = hakuResource.createHaku(dto);
        assertNotNull(result);
        assertNotNull(result.getStatus());
        assertEquals(ResultV1RDTO.ResultStatus.ERROR, result.getStatus());

        // ERROR: t=null, f=hakukausiUri, msg=haku.validation.hakukausiUri.invalid
        // ERROR: t=null, f=hakutapaUri, msg=haku.validation.hakutapaUri.invalid
        // ERROR: t=null, f=hakutyyppiUri, msg=haku.validation.hakutyyppiUri.invalid
        // ERROR: t=null, f=kohdejoukkoUri, msg=haku.validation.kohdejoukkoUri.invalid
        // ERROR: t=null, f=koulutuksenAlkamiskausiUri, msg=haku.validation.koulutuksenAlkamiskausiUri.invalid
        // ERROR: t=null, f=nimi, msg=haku.validation.nimi.empty
        // ERROR: t=null, f=maxHakukohdes, msg=haku.validation.maxHakukohdes.invalid
        // ERROR: t=null, f=hakuaikas, msg=haku.validation.hakuaikas.empty
        //
        // OK, fix the errors so that we can create a simple haku
        //
        dto = new HakuV1RDTO();
        dto.setHakukausiUri("kausi_k");
        dto.setHakutapaUri("hakutapa_01"); // yhteishaku
        dto.setHakutyyppiUri("hakutyyppi_01"); // varsinainen haku
        dto.setKohdejoukkoUri("haunkohdejoukko_12"); // korkeakoulutus
        dto.setKoulutuksenAlkamiskausiUri("kausi_k"); // kevät
        dto.setMaxHakukohdes(42);
        dto.getNimi().put("kieli_fi", "Nimi suomi");
        dto.getHakuaikas().add(createHakuaika(null, new Date(), new Date()));

        result = hakuResource.createHaku(dto);
        assertNotNull(result);
        assertNotNull(result.getStatus());
        assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());

        LOG.info("testXXX()... done.");
    }

    private HakuaikaV1RDTO createHakuaika(String nimi, Date start, Date end) {
        HakuaikaV1RDTO dto = new HakuaikaV1RDTO();

        dto.setAlkuPvm(start);
        dto.setLoppuPvm(end);
        dto.setNimi(nimi);

        return dto;
    }

}
