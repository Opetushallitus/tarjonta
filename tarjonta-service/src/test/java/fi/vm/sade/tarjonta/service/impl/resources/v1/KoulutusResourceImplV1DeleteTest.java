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
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidationMessages;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO.ErrorCode;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.when;


@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@ActiveProfiles("embedded-solr")
@Transactional()
public class KoulutusResourceImplV1DeleteTest extends KoulutusBase {

    private static final String KOMO2_OID = "another_komo_oid";
    private static final String KOMOTO2_OID = "another_komoto_oid";

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
    public void testSafeDelete() throws ExceptionMessage, Exception {
        KoulutusKorkeakouluV1RDTO dto = getKoulutus();

        //EXPECT
        permissionChecker.checkCreateKoulutus(ORGANISATION_OID);
        permissionChecker.checkUpdateKoulutusByTarjoajaOid(ORGANISATION_OID);

        /*
         * INSERT KORKEAKOULU OBJECT 1 TO DB
         */
        //a quick test data insert, no unnecessary DTO conversions.
        KoulutusmoduuliToteutus kt1 = quickKKInsert(dto);

        stub(oidService.get(TarjontaOidType.KOMO)).toReturn(KOMO2_OID);

        ResultV1RDTO deleteResult = instance.deleteByOid(null);
        assertEquals("No komoto oid", ResultV1RDTO.ResultStatus.NOT_FOUND, deleteResult.getStatus());


        /*
         * SIMPLE DELETE TEST
         */
        deleteSuccessTest(kt1);

        /*
         * INSERT KORKEAKOULU OBJECT 2 TO DB
         */
        stub(oidService.get(TarjontaOidType.KOMOTO)).toReturn(KOMOTO2_OID);
        KoulutusmoduuliToteutus kt2 = quickKKInsert(dto);

        /*
         * MULTIPLE DELETE TESTS
         */
        deleteValidationTests(kt2);
    }

    private void deleteSuccessTest(final KoulutusmoduuliToteutus persistedKomoto) {
        final String persistedKomotoOid = persistedKomoto.getOid();
        final String persistedKomoOid = persistedKomoto.getKoulutusmoduuli().getOid();

        when(koulutusSisaltyvyysDAO.getChildren(persistedKomoOid)).thenReturn(Lists.<String>newArrayList());
        when(koulutusSisaltyvyysDAO.getParents(persistedKomoOid)).thenReturn(Lists.<String>newArrayList());

        KoulutuksetVastaus kv = new KoulutuksetVastaus();
        List<KoulutusPerustieto> perustiedot = Lists.newArrayList();
        kv.setKoulutukset(perustiedot);

        ResultV1RDTO deleteResult = instance.deleteByOid(persistedKomotoOid);
        assertEquals("validation error", ResultV1RDTO.ResultStatus.OK, deleteResult.getStatus());
        assertEquals("Validation errors delete koulutus", true, deleteResult.getErrors() == null || deleteResult.getErrors().isEmpty());

        final KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(persistedKomotoOid);
        final Koulutusmoduuli komo = koulutusmoduuliDAO.findByOid(persistedKomoOid);

        assertEquals("invalid status", TarjontaTila.POISTETTU, komoto.getTila());
        assertEquals("invalid status", TarjontaTila.POISTETTU, komo.getTila());

        /* ---------------- NEXT TEST ----------------
         * ERROR : delete will fail as the status of komoto is 'deleted'.
         */
        deleteResult = instance.deleteByOid(persistedKomotoOid);
        assertEquals("validation error", ResultV1RDTO.ResultStatus.ERROR, deleteResult.getStatus());
        assertEquals("Validation", false, deleteResult.getErrors() == null || deleteResult.getErrors().isEmpty());
        assertEquals(ErrorCode.VALIDATION, ((ErrorV1RDTO) deleteResult.getErrors().get(0)).getErrorCode());
        assertEquals("komoto.tila", ((ErrorV1RDTO) deleteResult.getErrors().get(0)).getErrorField());
        assertEquals(KoulutusValidationMessages.KOULUTUS_DELETED.lower(), ((ErrorV1RDTO) deleteResult.getErrors().get(0)).getErrorMessageKey());
    }

    private void deleteValidationTests(KoulutusmoduuliToteutus persistedKomoto) {
        final String persistedKomotoOid = persistedKomoto.getOid();
        final String persistedKomoOid = persistedKomoto.getKoulutusmoduuli().getOid();

        KoulutuksetVastaus kv = new KoulutuksetVastaus();
        List<KoulutusPerustieto> perustiedot = Lists.newArrayList();
        kv.setKoulutukset(perustiedot);

        ArrayList<String> children = Lists.newArrayList();
        children.add("komo_oid_children");

        when(koulutusSisaltyvyysDAO.getChildren(persistedKomoOid)).thenReturn(children);
        when(koulutusSisaltyvyysDAO.getParents(persistedKomoOid)).thenReturn(Lists.<String>newArrayList());

        /* ---------------- NEXT TEST ----------------
         * ERROR IN CHILD
         */
        ResultV1RDTO deleteResult = instance.deleteByOid(persistedKomotoOid);
        assertEquals("validation error", ResultV1RDTO.ResultStatus.ERROR, deleteResult.getStatus());
        assertEquals("Validation", false, deleteResult.getErrors() == null || deleteResult.getErrors().isEmpty());
        assertEquals(ErrorCode.VALIDATION, ((ErrorV1RDTO) deleteResult.getErrors().get(0)).getErrorCode());
        assertEquals("komo.link.childs", ((ErrorV1RDTO) deleteResult.getErrors().get(0)).getErrorField());


        /* ---------------- NEXT TEST ----------------
         * ERROR IN PARENT
         */
        ArrayList<String> parent = Lists.newArrayList();
        parent.add("komo_oid_parent");

        when(koulutusSisaltyvyysDAO.getChildren(persistedKomoOid)).thenReturn(Lists.<String>newArrayList());
        when(koulutusSisaltyvyysDAO.getParents(persistedKomoOid)).thenReturn(parent);

        deleteResult = instance.deleteByOid(persistedKomotoOid);
        assertEquals("validation error", ResultV1RDTO.ResultStatus.ERROR, deleteResult.getStatus());
        assertEquals("Validation", false, deleteResult.getErrors() == null || deleteResult.getErrors().isEmpty());
        assertEquals(ErrorCode.VALIDATION, ((ErrorV1RDTO) deleteResult.getErrors().get(0)).getErrorCode());
        assertEquals("komo.link.parents", ((ErrorV1RDTO) deleteResult.getErrors().get(0)).getErrorField());

        /* ---------------- NEXT TEST ----------------
         * ERROR IN HAKUKOHDE (1 x hakukohde and 1 x invalid koulutus)
         */
        final KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(persistedKomotoOid);

        hakukohde.addKoulutusmoduuliToteutus(komoto);
        komoto.addHakukohde(hakukohde);
        hakukohde.setHaku(haku);
        hakukohdeDao.insert(hakukohde);

        kv = new KoulutuksetVastaus();
        perustiedot = Lists.newArrayList();
        KoulutusPerustieto koulutusPerustieto = new KoulutusPerustieto();
        koulutusPerustieto.setKomotoOid(KOMOTO_OID);
        perustiedot.add(koulutusPerustieto);

        kv.setKoulutukset(perustiedot);

        when(koulutusSisaltyvyysDAO.getChildren(persistedKomoOid)).thenReturn(Lists.<String>newArrayList());
        when(koulutusSisaltyvyysDAO.getParents(persistedKomoOid)).thenReturn(Lists.<String>newArrayList());

        deleteResult = instance.deleteByOid(persistedKomotoOid);
        assertEquals("validation error", ResultV1RDTO.ResultStatus.ERROR, deleteResult.getStatus());
        assertEquals("Validation", false, deleteResult.getErrors() == null || deleteResult.getErrors().isEmpty());
        assertEquals(ErrorCode.VALIDATION, ((ErrorV1RDTO) deleteResult.getErrors().get(0)).getErrorCode());
        //assertEquals("komo.invalid.transition", ((ErrorV1RDTO) deleteResult.getErrors().get(0)).getErrorField());
        assertEquals("komoto.hakukohdes", ((ErrorV1RDTO) deleteResult.getErrors().get(0)).getErrorField());

        /* ---------------- NEXT TEST ----------------
         * SUCCESS IN HAKUKOHDE (1 x hakukohde, 1 x invalid koulutus, 1 x valid koulutus)
         */
        kv = new KoulutuksetVastaus();
        perustiedot = Lists.newArrayList();
        koulutusPerustieto = new KoulutusPerustieto();
        koulutusPerustieto.setKomotoOid(KOMOTO_OID);
        koulutusPerustieto.setKomotoOid("komoto_oid_valid");
        perustiedot.add(koulutusPerustieto);

        kv.setKoulutukset(perustiedot);

        when(koulutusSisaltyvyysDAO.getChildren(persistedKomoOid)).thenReturn(Lists.<String>newArrayList());
        when(koulutusSisaltyvyysDAO.getParents(persistedKomoOid)).thenReturn(Lists.<String>newArrayList());

        deleteResult = instance.deleteByOid(persistedKomotoOid);

        assertEquals("validation error", ResultV1RDTO.ResultStatus.ERROR, deleteResult.getStatus());
        assertEquals("Validation", false, deleteResult.getErrors() == null || deleteResult.getErrors().isEmpty());
    }

    private KoulutusmoduuliToteutus quickKKInsert(KoulutusKorkeakouluV1RDTO dto) throws Exception {
        /*
         * INSERT KORKEAKOULU TO DB
         */
        //a quick test data insert, no unnecessary DTO conversions.
        KoulutusmoduuliToteutus kt = Whitebox.invokeMethod(instance, "insertKoulutusKorkeakoulu", dto);
        assertNotNull(kt);
        assertNotNull(kt.getOid());
        assertEquals(TUNNISTE, kt.getUlkoinenTunniste());
        assertEquals(TarjontaTila.JULKAISTU, kt.getTila());

        return kt;
    }
}
