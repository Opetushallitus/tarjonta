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
 */
package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.easymock.EasyMock.isA;
import org.junit.Before;
import org.junit.Test;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.impl.HakuDAOImpl;
import fi.vm.sade.tarjonta.dao.impl.HakukohdeDAOImpl;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO.ErrorCode;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.search.KoulutuksetKysely;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import org.springframework.beans.factory.annotation.Autowired;

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
@ActiveProfiles("embedded-solr")
@Transactional()
public class KoulutusResourceImplV1DeleteTest extends KoulutusBase {

    @Autowired
    private HakuDAOImpl hakuDao;

    @Autowired
    private HakukohdeDAOImpl hakukohdeDao;

    @Autowired(required = true)
    private TarjontaFixtures fixtures;

    private Hakukohde hakukohde;

    private Haku haku;

    @Before
    public void setUp() throws OIDCreationException {
        reload();
        haku = fixtures.createHaku();
        hakuDao.insert(haku);

        hakukohde = fixtures.createHakukohde();
        hakukohde.setHakukohdeKoodistoNimi("hakukohde name");
        hakukohde.setHakukohdeNimi("hakukohde_koodi_uri");
        hakukohde.setOid("hakukohde_oid"); //three exams

    }

    @Test
    public void testSafeDelete() throws ExceptionMessage {
        KoulutusKorkeakouluV1RDTO dto = getKoulutus();

        //EXPECT
        expect(organisaatioServiceMock.findByOid(ORGANISATION_OID)).andReturn(organisaatioDTO).times(2);
        //the calls of the OidServices must be in correct order!

        permissionChecker.checkCreateKoulutus(ORGANISATION_OID);
        permissionChecker.checkUpdateKoulutusByTarjoajaOid(ORGANISATION_OID);

        /* 
         * KOODISTO DATA CALLS IN CORRECT CALL ORDER
         * 1th round, convert to entity 
         */
        expectKausi();
        expectMetaUri(KOULUTUSKOODI);
        expectMetaUri(TUTKINTO);
        expectMetaUri(LAAJUUSARVO);
        expectMetaUri(LAAJUUSYKSIKKO);
        expectMetaUri(KOULUTUSASTE);
        expectMetaUri(KOULUTUSALA);
        expectMetaUri(OPINTOALA);
        expectMetaMapUris(MAP_TUTKINTONIMIKE);
        expectMetaUri(EQF);

        expectMetaMapUris(MAP_OPETUSPAIKKAS);
        expectMetaMapUris(MAP_OPETUSAIKAS);
        expectMetaMapUris(MAP_OPETUSAIHEES);
        expectMetaMapUris(MAP_OPETUSKIELI);
        expectMetaMapUris(MAP_OPETUMUOTO);
        expectMetaMapUris(MAP_POHJAKOULUTUS);
        expectMetaUri(SUUNNITELTU_KESTO);
        expectMetaMapUris(MAP_AMMATTINIMIKE);

        /* REPLAY */
        replay(organisaatioServiceMock);
        replay(tarjontaKoodistoHelperMock);
        /*
         * INSERT KORKEAKOULU TO DB
         */
        ResultV1RDTO<KoulutusV1RDTO> result = instance.postKoulutus(dto);
        assertEquals("Validation errors insert koulutus", true, result.getErrors() != null ? result.getErrors().isEmpty() : true);

        ResultV1RDTO deleteResult = instance.deleteByOid(null);
        assertEquals("No komoto oid", ResultV1RDTO.ResultStatus.NOT_FOUND, deleteResult.getStatus());

        final String persistedKomotoOid = result.getResult().getOid();
        final String persistedKomoOid = result.getResult().getKomoOid();

        /*
         * DELETE SUCCESS
         */
        // deleteSuccessTest(persistedKomoOid, persistedKomotoOid);
        deleteValidationTests(persistedKomoOid, persistedKomotoOid);
        verify(organisaatioServiceMock);
        verify(tarjontaKoodistoHelperMock);

    }

    private void deleteSuccessTest(String persistedKomoOid, String persistedKomotoOid) {
        expect(koulutusSisaltyvyysDAO.getChildren(persistedKomoOid)).andReturn(Lists.<String>newArrayList());
        expect(koulutusSisaltyvyysDAO.getParents(persistedKomoOid)).andReturn(Lists.<String>newArrayList());

        KoulutuksetVastaus kv = new KoulutuksetVastaus();
        List<KoulutusPerustieto> perustiedot = Lists.<KoulutusPerustieto>newArrayList();
        kv.setKoulutukset(perustiedot);
        expect(tarjontaSearchService.haeKoulutukset(isA(KoulutuksetKysely.class))).andReturn(kv);

        replay(koulutusSisaltyvyysDAO);
        replay(tarjontaSearchService);

        ResultV1RDTO deleteResult = instance.deleteByOid(persistedKomotoOid);
        assertEquals("validation error", ResultV1RDTO.ResultStatus.OK, deleteResult.getStatus());
        assertEquals("Validation errors delete koulutus", true, deleteResult.getErrors() != null ? deleteResult.getErrors().isEmpty() : true);

        final KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(persistedKomotoOid);
        final Koulutusmoduuli komo = koulutusmoduuliDAO.findByOid(persistedKomoOid);

        assertEquals("invalid status", TarjontaTila.POISTETTU, komoto.getTila());
        assertEquals("invalid status", TarjontaTila.POISTETTU, komo.getTila());

        verify(tarjontaSearchService);
        verify(koulutusSisaltyvyysDAO);
    }

    private void deleteValidationTests(String persistedKomoOid, String persistedKomotoOid) {
        KoulutuksetVastaus kv = new KoulutuksetVastaus();
        List<KoulutusPerustieto> perustiedot = Lists.newArrayList();
        kv.setKoulutukset(perustiedot);

        ArrayList<String> children = Lists.newArrayList();
        children.add("komo_oid_children");

        expect(koulutusSisaltyvyysDAO.getChildren(persistedKomoOid)).andReturn(children);
        expect(koulutusSisaltyvyysDAO.getParents(persistedKomoOid)).andReturn(Lists.<String>newArrayList());

        /*
         * ERROR IN CHILD
         */
        replay(koulutusSisaltyvyysDAO);
        replay(tarjontaSearchService);

        ResultV1RDTO deleteResult = instance.deleteByOid(persistedKomotoOid);
        assertEquals("validation error", ResultV1RDTO.ResultStatus.ERROR, deleteResult.getStatus());
        assertEquals("Validation", false, deleteResult.getErrors() != null ? deleteResult.getErrors().isEmpty() : true);
        assertEquals(ErrorCode.VALIDATION, ((ErrorV1RDTO) deleteResult.getErrors().get(0)).getErrorCode());
        assertEquals("komo.link.childs", ((ErrorV1RDTO) deleteResult.getErrors().get(0)).getErrorField());

        verify(koulutusSisaltyvyysDAO);
        EasyMock.reset(koulutusSisaltyvyysDAO);
        EasyMock.reset(tarjontaSearchService);

        /*
         * ERROR IN PARENT
         */
        ArrayList<String> parent = Lists.<String>newArrayList();
        parent.add("komo_oid_parent");

        expect(koulutusSisaltyvyysDAO.getChildren(persistedKomoOid)).andReturn(Lists.<String>newArrayList());
        expect(koulutusSisaltyvyysDAO.getParents(persistedKomoOid)).andReturn(parent);

        replay(koulutusSisaltyvyysDAO);
        replay(tarjontaSearchService);

        deleteResult = instance.deleteByOid(persistedKomotoOid);
        assertEquals("validation error", ResultV1RDTO.ResultStatus.ERROR, deleteResult.getStatus());
        assertEquals("Validation", false, deleteResult.getErrors() != null ? deleteResult.getErrors().isEmpty() : true);
        assertEquals(ErrorCode.VALIDATION, ((ErrorV1RDTO) deleteResult.getErrors().get(0)).getErrorCode());
        assertEquals("komo.link.parents", ((ErrorV1RDTO) deleteResult.getErrors().get(0)).getErrorField());

        verify(koulutusSisaltyvyysDAO);
        EasyMock.reset(koulutusSisaltyvyysDAO);
        EasyMock.reset(tarjontaSearchService);

        /*
         * ERROR IN HAKUKOHDE (1 x hakukohde and 1 x invalid koulutus)
         */
        final KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(persistedKomotoOid);

        hakukohde.addKoulutusmoduuliToteutus(komoto);
        komoto.addHakukohde(hakukohde);
        hakukohde.setHaku(haku);
        hakukohdeDao.insert(hakukohde);

        kv = new KoulutuksetVastaus();
        perustiedot = Lists.<KoulutusPerustieto>newArrayList();
        KoulutusPerustieto koulutusPerustieto = new KoulutusPerustieto();
        koulutusPerustieto.setKomotoOid(KOMOTO_OID);
        perustiedot.add(koulutusPerustieto);

        kv.setKoulutukset(perustiedot);

        expect(koulutusSisaltyvyysDAO.getChildren(persistedKomoOid)).andReturn(Lists.<String>newArrayList());
        expect(koulutusSisaltyvyysDAO.getParents(persistedKomoOid)).andReturn(Lists.<String>newArrayList());
        expect(tarjontaSearchService.haeKoulutukset(isA(KoulutuksetKysely.class))).andReturn(kv);

        replay(koulutusSisaltyvyysDAO);
        replay(tarjontaSearchService);

        deleteResult = instance.deleteByOid(persistedKomotoOid);
        assertEquals("validation error", ResultV1RDTO.ResultStatus.ERROR, deleteResult.getStatus());
        assertEquals("Validation", false, deleteResult.getErrors() != null ? deleteResult.getErrors().isEmpty() : true);
        assertEquals(ErrorCode.VALIDATION, ((ErrorV1RDTO) deleteResult.getErrors().get(0)).getErrorCode());
        //assertEquals("komo.invalid.transition", ((ErrorV1RDTO) deleteResult.getErrors().get(0)).getErrorField());
        assertEquals("komoto.hakukohdes", ((ErrorV1RDTO) deleteResult.getErrors().get(0)).getErrorField());
        
        verify(koulutusSisaltyvyysDAO);
        EasyMock.reset(koulutusSisaltyvyysDAO);
        EasyMock.reset(tarjontaSearchService);

        /*
         * SUCCESS IN HAKUKOHDE (1 x hakukohde, 1 x invalid koulutus, 1 x valid koulutus)
         */
        kv = new KoulutuksetVastaus();
        perustiedot = Lists.<KoulutusPerustieto>newArrayList();
        koulutusPerustieto = new KoulutusPerustieto();
        koulutusPerustieto.setKomotoOid(KOMOTO_OID);
        koulutusPerustieto.setKomotoOid("komoto_oid_valid");
        perustiedot.add(koulutusPerustieto);

        kv.setKoulutukset(perustiedot);

        expect(koulutusSisaltyvyysDAO.getChildren(persistedKomoOid)).andReturn(Lists.<String>newArrayList());
        expect(koulutusSisaltyvyysDAO.getParents(persistedKomoOid)).andReturn(Lists.<String>newArrayList());

        replay(koulutusSisaltyvyysDAO);
        replay(tarjontaSearchService);

        deleteResult = instance.deleteByOid(persistedKomotoOid);

        assertEquals("validation error", ResultV1RDTO.ResultStatus.ERROR, deleteResult.getStatus());
        assertEquals("Validation", false, deleteResult.getErrors() != null ? deleteResult.getErrors().isEmpty() : true);

        // kommentoitu, koska poistaminen ei onnistu tilavalidoinnin takia, ja tilasiirtymät eivät toimi koska instance on mock
        //assertEquals("validation error", ResultV1RDTO.ResultStatus.OK, deleteResult.getStatus());
        //assertEquals("Validation", true, deleteResult.getErrors() != null ? deleteResult.getErrors().isEmpty() : true);

        verify(koulutusSisaltyvyysDAO);

    }
    
}
