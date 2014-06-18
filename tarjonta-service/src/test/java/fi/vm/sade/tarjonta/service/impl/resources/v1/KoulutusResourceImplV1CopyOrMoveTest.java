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
import org.junit.Before;
import org.junit.Test;

import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.publication.model.RestParam;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.EntityConverterToRDTO;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusDTOConverterToEntity;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidationMessages;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KomoLink;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusCopyResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusCopyV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.shared.types.CopyMode;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.List;
import org.easymock.Capture;
import org.powermock.reflect.Whitebox;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.easymock.EasyMock.capture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author jani
 */
public class KoulutusResourceImplV1CopyOrMoveTest extends KoulutusBase {
    
    private static final String ORGANISATION_OID_COPY_OR_MOVE_TO = "organisation_oid_move_to";
    private static final String SEARCH_KOMO_OID = "search_komo_by_oid_test_init";
    private static final RestParam PARAM = RestParam.showImageAndNoMeta("FI");
    
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    private KoulutusmoduuliToteutus komoto;
    private TarjontaFixtures tarjontaFixtures = new TarjontaFixtures();
    private OrganisaatioDTO organisaatioDTO = new OrganisaatioDTO();
    private EntityConverterToRDTO converterToRDTO;
    private KoulutusDTOConverterToEntity convertToEntity;
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    
    @Before
    public void setUp() throws OIDCreationException {
        instance = new KoulutusResourceImplV1();
        komoto = createKomotoKomo(KOMOTO_OID, ORGANISATION_OID);
        
        organisaatioDTO.setOid(ORGANISATION_OID);
        
        koulutusmoduuliToteutusDAO = createMock(KoulutusmoduuliToteutusDAO.class);
        koulutusmoduuliDAO = createMock(KoulutusmoduuliDAO.class);
        organisaatioServiceMock = createMock(OrganisaatioService.class);
        converterToRDTO = createMock(EntityConverterToRDTO.class);
        convertToEntity = createMock(KoulutusDTOConverterToEntity.class);

        //SET DAO STATES
        initMockInstanceInternalStates();
        Whitebox.setInternalState(instance, "organisaatioService", organisaatioServiceMock);
        Whitebox.setInternalState(instance, "koulutusmoduuliToteutusDAO", koulutusmoduuliToteutusDAO);
        Whitebox.setInternalState(instance, "converterToRDTO", converterToRDTO);
        Whitebox.setInternalState(instance, "convertToEntity", convertToEntity);
        Whitebox.setInternalState(instance, "koulutusmoduuliDAO", koulutusmoduuliDAO);
    }
    
    @Test
    public void testErrorsNotFound() throws ExceptionMessage {
        KoulutusCopyV1RDTO dto = new KoulutusCopyV1RDTO();
        dto.setOrganisationOids(Lists.newArrayList(ORGANISATION_OID));
        
        expect(koulutusmoduuliToteutusDAO.findKomotoByOid(KOMOTO_OID)).andReturn(null);
        
        replay(koulutusmoduuliToteutusDAO);
        ResultV1RDTO<KoulutusCopyResultV1RDTO> copyOrMove = instance.copyOrMove(KOMOTO_OID, dto);
        verify(koulutusmoduuliToteutusDAO);
        
        validation(copyOrMove, ResultV1RDTO.ResultStatus.NOT_FOUND, "oid", KoulutusValidationMessages.KOULUTUS_KOMOTO_MISSING);
    }
    
    @Test
    public void testErrorsMissingRequiredObject() throws ExceptionMessage {
        expect(koulutusmoduuliToteutusDAO.findKomotoByOid(KOMOTO_OID)).andReturn(komoto);
        
        replay(koulutusmoduuliToteutusDAO);
        ResultV1RDTO<KoulutusCopyResultV1RDTO> copyOrMove = instance.copyOrMove(KOMOTO_OID, null);
        verify(koulutusmoduuliToteutusDAO);
        
        validation(copyOrMove, ResultV1RDTO.ResultStatus.ERROR, null, KoulutusValidationMessages.KOULUTUS_INPUT_OBJECT_MISSING);
    }
    
    @Test
    public void testErrorsMissingRequiredData() throws ExceptionMessage {
        KoulutusCopyV1RDTO dto = new KoulutusCopyV1RDTO();
        expect(koulutusmoduuliToteutusDAO.findKomotoByOid(KOMOTO_OID)).andReturn(komoto);
        
        replay(koulutusmoduuliToteutusDAO);
        ResultV1RDTO<KoulutusCopyResultV1RDTO> copyOrMove = instance.copyOrMove(KOMOTO_OID, dto);
        verify(koulutusmoduuliToteutusDAO);
        
        validation(copyOrMove, ResultV1RDTO.ResultStatus.ERROR, "mode", KoulutusValidationMessages.KOULUTUS_INPUT_PARAM_MISSING);
    }
    
    @Test
    public void testErrorsInvalidTarjoaja() throws ExceptionMessage {
        KoulutusCopyV1RDTO dto = new KoulutusCopyV1RDTO();
        dto.setOrganisationOids(Lists.newArrayList(ORGANISATION_OID));
        dto.setMode(CopyMode.MOVE);
        
        expect(koulutusmoduuliToteutusDAO.findKomotoByOid(KOMOTO_OID)).andReturn(komoto);
        expect(organisaatioServiceMock.findByOid(ORGANISATION_OID)).andReturn(null);
        
        replay(koulutusmoduuliToteutusDAO);
        replay(organisaatioServiceMock);
        
        ResultV1RDTO<KoulutusCopyResultV1RDTO> copyOrMove = instance.copyOrMove(KOMOTO_OID, dto);
        
        verify(koulutusmoduuliToteutusDAO);
        verify(organisaatioServiceMock);
        
        validation(copyOrMove, ResultV1RDTO.ResultStatus.ERROR, "organisationOids[" + ORGANISATION_OID + "]", KoulutusValidationMessages.KOULUTUS_TARJOAJA_INVALID);
    }
    
    @Test
    public void testErrorsInvalidTarjoajaWrongType() throws ExceptionMessage {
        KoulutusCopyV1RDTO dto = new KoulutusCopyV1RDTO();
        dto.setOrganisationOids(Lists.newArrayList(ORGANISATION_OID));
        dto.setMode(CopyMode.MOVE);
        
        expect(koulutusmoduuliToteutusDAO.findKomotoByOid(KOMOTO_OID)).andReturn(komoto);
        expect(organisaatioServiceMock.findByOid(ORGANISATION_OID)).andReturn(organisaatioDTO);
        expect(oppilaitosKoodiRelations.isKoulutusAllowedForOrganisation(ORGANISATION_OID, "kk")).andReturn(false);
        
        replay(koulutusmoduuliToteutusDAO);
        replay(organisaatioServiceMock);
        replay(oppilaitosKoodiRelations);
        
        ResultV1RDTO<KoulutusCopyResultV1RDTO> copyOrMove = instance.copyOrMove(KOMOTO_OID, dto);
        
        verify(koulutusmoduuliToteutusDAO);
        verify(organisaatioServiceMock);
        verify(oppilaitosKoodiRelations);
        
        validation(copyOrMove, ResultV1RDTO.ResultStatus.ERROR, "organisationOids[" + ORGANISATION_OID + "]", KoulutusValidationMessages.KOULUTUS_TARJOAJA_INVALID);
    }
    
    @Test
    public void testMove() throws ExceptionMessage {
        KoulutusCopyV1RDTO dto = new KoulutusCopyV1RDTO();
        dto.setOrganisationOids(Lists.newArrayList(ORGANISATION_OID_COPY_OR_MOVE_TO));
        dto.setMode(CopyMode.MOVE);
        
        assertEquals(ORGANISATION_OID, komoto.getTarjoaja());
        expect(koulutusmoduuliToteutusDAO.findKomotoByOid(KOMOTO_OID)).andReturn(komoto);
        expect(organisaatioServiceMock.findByOid(ORGANISATION_OID_COPY_OR_MOVE_TO)).andReturn(organisaatioDTO);
        expect(oppilaitosKoodiRelations.isKoulutusAllowedForOrganisation(ORGANISATION_OID_COPY_OR_MOVE_TO, "kk")).andReturn(true);
        koulutusmoduuliToteutusDAO.update(komoto);
        
        expect(hakukohdeDAO.findByKoulutusOid(KOMOTO_OID)).andReturn(Lists.<Hakukohde>newArrayList());
        
        replay(koulutusmoduuliToteutusDAO);
        replay(organisaatioServiceMock);
        replay(oppilaitosKoodiRelations);
        replay(hakukohdeDAO);
        
        ResultV1RDTO<KoulutusCopyResultV1RDTO> copyOrMove = instance.copyOrMove(KOMOTO_OID, dto);
        
        verify(koulutusmoduuliToteutusDAO);
        verify(organisaatioServiceMock);
        verify(oppilaitosKoodiRelations);
        verify(hakukohdeDAO);
        
        assertNotNull("no response object", copyOrMove);
        assertNotNull("no result object", copyOrMove.getResult());
        assertEquals(ORGANISATION_OID_COPY_OR_MOVE_TO, komoto.getTarjoaja());
        assertEquals(copyOrMove.getResult().getFromOid(), KOMOTO_OID);
        assertEquals(copyOrMove.getResult().getTo().get(0).getOrganisationOid(), ORGANISATION_OID_COPY_OR_MOVE_TO);
        assertEquals(copyOrMove.getResult().getTo().get(0).getOid(), KOMOTO_OID);
        assertEquals(copyOrMove.getStatus(), ResultV1RDTO.ResultStatus.OK);
    }
    
    @Test
    public void testCopy() throws ExceptionMessage {
        KoulutusKorkeakouluV1RDTO kkDto = new KoulutusKorkeakouluV1RDTO();
        KoulutusmoduuliToteutus komotoCopyTo = createKomotoKomo("komoto_oid_copy_to", ORGANISATION_OID_COPY_OR_MOVE_TO);
        KoulutusCopyV1RDTO dto = new KoulutusCopyV1RDTO();
        
        dto.setOrganisationOids(Lists.newArrayList(ORGANISATION_OID_COPY_OR_MOVE_TO));
        dto.setMode(CopyMode.COPY);
        
        kkDto.setOid("test_init");
        kkDto.setKomotoOid("test_init");
        kkDto.setKomoOid(SEARCH_KOMO_OID);
        kkDto.setTila(TarjontaTila.JULKAISTU);
        
        assertEquals(ORGANISATION_OID, komoto.getTarjoaja());
        expect(koulutusmoduuliToteutusDAO.findKomotoByOid(KOMOTO_OID)).andReturn(komoto);
        expect(organisaatioServiceMock.findByOid(ORGANISATION_OID_COPY_OR_MOVE_TO)).andReturn(organisaatioDTO);
        expect(oppilaitosKoodiRelations.isKoulutusAllowedForOrganisation(ORGANISATION_OID_COPY_OR_MOVE_TO, "kk")).andReturn(true);

        //remove all not needed referenses by using convert entity to dto (language can be any)
        expect(converterToRDTO.convert(KoulutusKorkeakouluV1RDTO.class, komoto, PARAM)).andReturn(kkDto);
        
        Capture<List> toOrgOids = new Capture<List>();
        permissionChecker.checkCopyKoulutus(capture(toOrgOids));
        permissionChecker.checkCreateKoulutus(ORGANISATION_OID_COPY_OR_MOVE_TO);

        //convert back to entity
        expect(convertToEntity.convert(kkDto, USER_OID, true)).andReturn(komotoCopyTo);

        //permissionChecker.checkCreateKoulutus(dto.getOrganisaatio().getOid());
        expect(koulutusmoduuliDAO.insert(komotoCopyTo.getKoulutusmoduuli())).andReturn(komotoCopyTo.getKoulutusmoduuli());
        expect(koulutusmoduuliToteutusDAO.insert(komotoCopyTo)).andReturn(komotoCopyTo);

        //one link
        expect(koulutusSisaltyvyysDAO.getChildren(KOMOTO_OID)).andReturn(Lists.<String>newArrayList("komo_child_oid_link"));
        //TODO: return case
        Capture<KomoLink> capturedKomoLinkChildren = new Capture<KomoLink>();
        expect(linkingV1Resource.link(capture(capturedKomoLinkChildren))).andReturn(null);
        
        expect(koulutusSisaltyvyysDAO.getParents(KOMOTO_OID)).andReturn(Lists.<String>newArrayList("komo_parent_oid_link"));
        //TODO: return case
        Capture<KomoLink> capturedKomoLinkParent = new Capture<KomoLink>();
        expect(linkingV1Resource.link(capture(capturedKomoLinkParent))).andReturn(null);
        
        replay(permissionChecker);
        replay(koulutusmoduuliDAO);
        replay(koulutusmoduuliToteutusDAO);
        replay(organisaatioServiceMock);
        replay(oppilaitosKoodiRelations);
        replay(hakukohdeDAO);
        replay(converterToRDTO);
        replay(convertToEntity);
        replay(linkingV1Resource);
        replay(koulutusSisaltyvyysDAO);
        
        ResultV1RDTO<KoulutusCopyResultV1RDTO> copyOrMove = instance.copyOrMove(KOMOTO_OID, dto);
        
        verify(permissionChecker);
        verify(koulutusmoduuliDAO);
        verify(koulutusmoduuliToteutusDAO);
        verify(organisaatioServiceMock);
        verify(oppilaitosKoodiRelations);
        verify(hakukohdeDAO);
        verify(converterToRDTO);
        verify(convertToEntity);
        verify(linkingV1Resource);
        verify(koulutusSisaltyvyysDAO);
        
        assertEquals(null, kkDto.getOid());
        assertEquals(null, kkDto.getKomotoOid());
        assertEquals(SEARCH_KOMO_OID, kkDto.getKomoOid());
        assertEquals(TarjontaTila.LUONNOS, kkDto.getTila());
        
        assertEquals(ORGANISATION_OID_COPY_OR_MOVE_TO, toOrgOids.getValue().get(0));
        
        assertEquals("komo_oid_of_komoto_oid_copy_to", capturedKomoLinkChildren.getValue().getParent());
        assertEquals("komo_child_oid_link", capturedKomoLinkChildren.getValue().getChildren().get(0));
        
        assertEquals("komo_parent_oid_link", capturedKomoLinkParent.getValue().getParent());
        assertEquals("komo_oid_of_komoto_oid_copy_to", capturedKomoLinkParent.getValue().getChildren().get(0));
        
        assertNotNull("no response object", copyOrMove);
        assertNotNull("no result object", copyOrMove.getResult());
        assertEquals(ORGANISATION_OID, komoto.getTarjoaja());
        assertEquals(copyOrMove.getResult().getFromOid(), KOMOTO_OID);
        assertEquals(copyOrMove.getResult().getTo().get(0).getOrganisationOid(), ORGANISATION_OID_COPY_OR_MOVE_TO);
        assertEquals(copyOrMove.getResult().getTo().get(0).getOid(), "komoto_oid_copy_to");
        assertEquals(copyOrMove.getStatus(), ResultV1RDTO.ResultStatus.OK);
    }

//    private void printResultErrors(ResultV1RDTO r) {
//        List<ErrorV1RDTO> errors = r.getErrors();
//        for (ErrorV1RDTO e : errors) {
//            System.out.println(e.getErrorMessageKey());
//        }
//    }
    private void validation(ResultV1RDTO dto, ResultV1RDTO.ResultStatus status, String errorField, KoulutusValidationMessages msgEnum) {
        assertEquals("Validation", false, dto.getErrors() != null ? dto.getErrors().isEmpty() : true);
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
