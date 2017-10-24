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
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.auditlog.AuditHelper;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusDTOConverterToEntity;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidationMessages;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.shared.types.CopyMode;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import javax.servlet.http.HttpServletRequest;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author jani
 */
public class KoulutusResourceImplV1MoveTest extends KoulutusBase {
    
    private static final String ORGANISATION_OID_COPY_OR_MOVE_TO = "organisation_oid_move_to";

    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    private KoulutusmoduuliToteutus komoto;
    private TarjontaFixtures tarjontaFixtures = new TarjontaFixtures();
    private OrganisaatioRDTO organisaatioDTO = new OrganisaatioRDTO();
    private HakukohdeDAO hakukohdeDAO;
    private AuditHelper auditHelper;
    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    @Before
    public void setUp() throws OIDCreationException {
        instance = new KoulutusResourceImplV1();
        komoto = createKomotoKomo(KOMOTO_OID, ORGANISATION_OID);

        organisaatioDTO.setOid(ORGANISATION_OID);

        koulutusmoduuliToteutusDAO = Mockito.mock(KoulutusmoduuliToteutusDAO.class);
        KoulutusmoduuliDAO koulutusmoduuliDAO = createMock(KoulutusmoduuliDAO.class);
        auditHelper = mock(AuditHelper.class);

        KoulutusDTOConverterToEntity convertToEntity = createMock(KoulutusDTOConverterToEntity.class);
        hakukohdeDAO = Mockito.mock(HakukohdeDAO.class);

        //SET DAO STATES
        initMockInstanceInternalStates();
        Whitebox.setInternalState(instance, "organisaatioService", organisaatioServiceMock);
        Whitebox.setInternalState(instance, "koulutusmoduuliToteutusDAO", koulutusmoduuliToteutusDAO);
        Whitebox.setInternalState(instance, "converterToRDTO", converterToRDTO);
        Whitebox.setInternalState(instance, "convertToEntity", convertToEntity);
        Whitebox.setInternalState(instance, "koulutusmoduuliDAO", koulutusmoduuliDAO);
        Whitebox.setInternalState(instance, "hakukohdeDAO", hakukohdeDAO);
        Whitebox.setInternalState(instance ,"auditHelper", auditHelper);
    }
    
    @Test
    public void testErrorsNotFound() throws ExceptionMessage {
        KoulutusCopyV1RDTO dto = new KoulutusCopyV1RDTO();
        dto.setOrganisationOids(Lists.newArrayList(ORGANISATION_OID));
        
        when(koulutusmoduuliToteutusDAO.findKomotoByOid(KOMOTO_OID)).thenReturn(null);

        ResultV1RDTO<KoulutusCopyResultV1RDTO> copyOrMove = instance.copyOrMove(KOMOTO_OID, dto, request);
        
        validation(copyOrMove, ResultV1RDTO.ResultStatus.NOT_FOUND, "oid", KoulutusValidationMessages.KOULUTUS_KOMOTO_MISSING);
    }
    
    @Test
    public void testErrorsMissingRequiredObject() throws ExceptionMessage {
        when(koulutusmoduuliToteutusDAO.findKomotoByOid(KOMOTO_OID)).thenReturn(komoto);

        ResultV1RDTO<KoulutusCopyResultV1RDTO> copyOrMove = instance.copyOrMove(KOMOTO_OID, null, request);
        
        validation(copyOrMove, ResultV1RDTO.ResultStatus.ERROR, null, KoulutusValidationMessages.KOULUTUS_INPUT_OBJECT_MISSING);
    }
    
    @Test
    public void testErrorsMissingRequiredData() throws ExceptionMessage {
        KoulutusCopyV1RDTO dto = new KoulutusCopyV1RDTO();
        when(koulutusmoduuliToteutusDAO.findKomotoByOid(KOMOTO_OID)).thenReturn(komoto);

        ResultV1RDTO<KoulutusCopyResultV1RDTO> copyOrMove = instance.copyOrMove(KOMOTO_OID, dto, request);
        
        validation(copyOrMove, ResultV1RDTO.ResultStatus.ERROR, "mode", KoulutusValidationMessages.KOULUTUS_INPUT_PARAM_MISSING);
    }
    
    @Test
    public void testErrorsInvalidTarjoaja() throws ExceptionMessage {
        KoulutusCopyV1RDTO dto = new KoulutusCopyV1RDTO();
        dto.setOrganisationOids(Lists.newArrayList(ORGANISATION_OID));
        dto.setMode(CopyMode.MOVE);
        
        when(koulutusmoduuliToteutusDAO.findKomotoByOid(KOMOTO_OID)).thenReturn(komoto);
        when(organisaatioServiceMock.findByOid(ORGANISATION_OID)).thenReturn(null);
        
        ResultV1RDTO<KoulutusCopyResultV1RDTO> copyOrMove = instance.copyOrMove(KOMOTO_OID, dto, request);
        
        validation(copyOrMove, ResultV1RDTO.ResultStatus.ERROR, "organisationOids[" + ORGANISATION_OID + "]", KoulutusValidationMessages.KOULUTUS_TARJOAJA_INVALID);
    }
    
    @Test
    public void testErrorsInvalidTarjoajaWrongType() throws ExceptionMessage {
        KoulutusCopyV1RDTO dto = new KoulutusCopyV1RDTO();
        dto.setOrganisationOids(Lists.newArrayList(ORGANISATION_OID));
        dto.setMode(CopyMode.MOVE);
        
        when(koulutusmoduuliToteutusDAO.findKomotoByOid(KOMOTO_OID)).thenReturn(komoto);
        when(organisaatioServiceMock.findByOid(ORGANISATION_OID)).thenReturn(organisaatioDTO);
        when(oppilaitosKoodiRelations.isKoulutusAllowedForOrganisation(ORGANISATION_OID, "kk")).thenReturn(false);
        
        ResultV1RDTO<KoulutusCopyResultV1RDTO> copyOrMove = instance.copyOrMove(KOMOTO_OID, dto, request);
        
        validation(copyOrMove, ResultV1RDTO.ResultStatus.ERROR, "organisationOids[" + ORGANISATION_OID + "]", KoulutusValidationMessages.KOULUTUS_TARJOAJA_INVALID);
    }
    
    @Test
    public void testMove() throws ExceptionMessage {
        KoulutusCopyV1RDTO dto = new KoulutusCopyV1RDTO();
        dto.setOrganisationOids(Lists.newArrayList(ORGANISATION_OID_COPY_OR_MOVE_TO));
        dto.setMode(CopyMode.MOVE);

        assertEquals(ORGANISATION_OID, komoto.getTarjoaja());
        when(koulutusmoduuliToteutusDAO.findKomotoByOid(KOMOTO_OID)).thenReturn(komoto);
        when(organisaatioServiceMock.findByOid(ORGANISATION_OID_COPY_OR_MOVE_TO)).thenReturn(organisaatioDTO);
        when(oppilaitosKoodiRelations.isKoulutusAllowedForOrganisation(ORGANISATION_OID_COPY_OR_MOVE_TO, "kk")).thenReturn(true);
        when(hakukohdeDAO.findByKoulutusOid(KOMOTO_OID)).thenReturn(Lists.newArrayList());
        when(auditHelper.getKomotoAsDto(any(KoulutusmoduuliToteutus.class))).thenReturn(new KoulutusKorkeakouluV1RDTO());

        ResultV1RDTO<KoulutusCopyResultV1RDTO> copyOrMove = instance.copyOrMove(KOMOTO_OID, dto, request);
        
        assertNotNull("no response object", copyOrMove);
        assertNotNull("no result object", copyOrMove.getResult());
        assertEquals(ORGANISATION_OID_COPY_OR_MOVE_TO, komoto.getTarjoaja());
        assertEquals(copyOrMove.getResult().getFromOid(), KOMOTO_OID);
        assertEquals(copyOrMove.getResult().getTo().get(0).getOrganisationOid(), ORGANISATION_OID_COPY_OR_MOVE_TO);
        assertEquals(copyOrMove.getResult().getTo().get(0).getOid(), KOMOTO_OID);
        assertEquals(copyOrMove.getStatus(), ResultV1RDTO.ResultStatus.OK);
    }

    private void validation(ResultV1RDTO dto, ResultV1RDTO.ResultStatus status, String errorField, KoulutusValidationMessages msgEnum) {
        assertEquals("Validation", false, dto.getErrors() == null || dto.getErrors().isEmpty());
        assertEquals("error", 1, dto.getErrors().size());
        assertEquals("No komoto oid", status, dto.getStatus());
        assertEquals(errorField, ((ErrorV1RDTO) dto.getErrors().get(0)).getErrorField());
        assertEquals(msgEnum.lower(), ((ErrorV1RDTO) dto.getErrors().get(0)).getErrorMessageKey());
    }
    
    private KoulutusmoduuliToteutus createKomotoKomo(String komotoOid, String orgOid) {
        Koulutusmoduuli komo = tarjontaFixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
        komo.setKoulutusasteUri("kk");
        komo.setOid("komo_oid_of_" + komotoOid);
        komo.setKoulutustyyppiUri("|" + ToteutustyyppiEnum.KORKEAKOULUTUS.uri() + "|");
        komo.setKoulutustyyppiEnum(ModuulityyppiEnum.KORKEAKOULUTUS);
        KoulutusmoduuliToteutus kt = tarjontaFixtures.createTutkintoOhjelmaToteutusWithTarjoajaOid(orgOid);
        kt.setToteutustyyppi(ToteutustyyppiEnum.KORKEAKOULUTUS);
        kt.setId(1l);
        kt.setOid(komotoOid);
        kt.setKoulutusmoduuli(komo);
        
        return kt;
    }
}
