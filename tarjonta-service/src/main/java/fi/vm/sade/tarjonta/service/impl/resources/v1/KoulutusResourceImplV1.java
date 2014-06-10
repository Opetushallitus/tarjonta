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

import static fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidator.validateMimeType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fi.vm.sade.koodisto.service.GenericFault;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.koodisto.KoulutuskoodiRelations;
import fi.vm.sade.tarjonta.koodisto.OppilaitosKoodiRelations;
import fi.vm.sade.tarjonta.model.BinaryData;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.publication.Tila;
import fi.vm.sade.tarjonta.publication.Tila.Tyyppi;
import fi.vm.sade.tarjonta.publication.model.RestParam;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.service.business.exception.TarjontaBusinessException;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.EntityConverterToRDTO;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusDTOConverterToEntity;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusKuvausV1RDTO;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidationMessages;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidator;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.KoulutusV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.LinkingV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KomoLink;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO.ResultStatus;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAmmatillinenPerustutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusCopyResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusCopyStatusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusCopyV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioAikuistenOppimaaraV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusMultiCopyV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusValmistavaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliAmmatillinenRelationV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliKorkeakouluRelationV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliLukioRelationV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliStandardRelationV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.search.HakukohdePerustieto;
import fi.vm.sade.tarjonta.service.search.HakukohteetKysely;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.service.search.KoulutuksetKysely;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.search.TarjontaSearchService;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.Tilamuutokset;
import java.util.logging.Level;

/**
 *
 * @author mlyly
 */
@Transactional(readOnly = false)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class KoulutusResourceImplV1 implements KoulutusV1Resource {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusResourceImplV1.class);

    @Autowired(required = true)
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired(required = true)
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired(required = true)
    private TarjontaSearchService tarjontaSearchService;
    @Autowired(required = true)
    private IndexerResource indexerResource;
    @Autowired(required = true)
    private KoulutuskoodiRelations koulutuskoodiRelations;
    @Autowired(required = true)
    private KoodiService koodiService;
    @Autowired(required = true)
    private OrganisaatioService organisaatioService;
    @Autowired(required = true)
    private KoulutusKuvausV1RDTO<KomoTeksti> komoKoulutusConverters;
    @Autowired(required = true)
    private KoulutusKuvausV1RDTO<KomotoTeksti> komotoKoulutusConverters;
    @Autowired(required = true)
    private ConverterV1 converterV1;
    @Autowired(required = true)
    private PermissionChecker permissionChecker;

    @Autowired(required = true)
    private ContextDataService contextDataService;

    @Autowired(required = true)
    private EntityConverterToRDTO converterToRDTO;

    @Autowired(required = true)
    private KoulutusDTOConverterToEntity convertToEntity;

    @Autowired(required = true)
    private KoulutusSisaltyvyysDAO koulutusSisaltyvyysDAO;

    @Autowired(required = true)
    private LinkingV1Resource linkingV1Resource;

    @Autowired(required = true)
    private HakukohdeDAO hakukohdeDAO;

    @Autowired(required = true)
    private OppilaitosKoodiRelations oppilaitosKoodiRelations;

    @Autowired(required = true)
    private PublicationDataService publicationDataService;

    @Override
    public ResultV1RDTO<KoulutusV1RDTO> findByOid(String komotoOid, Boolean showMeta, Boolean showImg, String userLang) {
        Preconditions.checkNotNull(komotoOid, "KOMOTO OID cannot be null.");

        ResultV1RDTO<KoulutusV1RDTO> result = new ResultV1RDTO<KoulutusV1RDTO>();
        final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(komotoOid);
        final RestParam instance = RestParam.byUserRequest(showMeta, showImg, userLang);

        if (komoto == null) {
            result.setStatus(ResultStatus.NOT_FOUND);
            return result;
        }

        //convert required komoto to dto rest format.
        switch (getType(komoto)) {
            case KORKEAKOULUTUS:
                result.setResult(converterToRDTO.convert(KoulutusKorkeakouluV1RDTO.class, komoto, instance));
                break;
            case LUKIOKOULUTUS:
                result.setResult(converterToRDTO.convert(KoulutusLukioV1RDTO.class, komoto, instance));
                break;
            case LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA:
                result.setResult(converterToRDTO.convert(KoulutusLukioAikuistenOppimaaraV1RDTO.class, komoto, instance));
                break;
            case AMMATILLINEN_PERUSTUTKINTO:
                result.setResult(converterToRDTO.convert(KoulutusAmmatillinenPerustutkintoV1RDTO.class, komoto, instance));
                break;
            case AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA:
                //very special case: may have a double komoto structure  
                result.setResult(convertAmmValmentavaToDTO(komoto, instance));
                break;
            case AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA_VALMISTAVA:
                result.setResult(converterToRDTO.convert(KoulutusValmistavaV1RDTO.class, komoto, instance));
                break;
        }

        return result;
    }

    @Override
    public ResultV1RDTO<KoulutusV1RDTO> postKoulutus(KoulutusV1RDTO dto) {

        //yleisiä tarkistuksia
        //tarkista tilasiirtymä
        if (dto.getOid() != null) {
            final Tila tilamuutos = new Tila(Tyyppi.KOMOTO, dto.getTila(), dto.getOid());
            if (!publicationDataService.isValidStatusChange(tilamuutos)) {
                return ResultV1RDTO.create(ResultStatus.ERROR, null, ErrorV1RDTO.createValidationError("tile", "koulutus.error.tilasiirtyma"));
            }
        }

        if (dto.getClass() == KoulutusKorkeakouluV1RDTO.class) {
            return postKorkeakouluKoulutus((KoulutusKorkeakouluV1RDTO) dto);
        } else if (dto.getClass() == KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO.class) {
            return postAmmatillinenPerustutkintoNayttotutkintona((KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO) dto);
        } else if (dto.getClass() == KoulutusLukioV1RDTO.class) {
            return postLukioKoulutus((KoulutusLukioV1RDTO) dto);
        } else if (dto.getClass() == KoulutusLukioAikuistenOppimaaraV1RDTO.class) {
            return postLukioKoulutus((KoulutusLukioV1RDTO) dto);
        }

        ResultV1RDTO<KoulutusV1RDTO> result = new ResultV1RDTO<KoulutusV1RDTO>(null, ResultStatus.ERROR);
        result.addError(ErrorV1RDTO.createSystemError(new IllegalArgumentException(), "type_unknown", dto.getClass() + " not handled"));

        return result;
    }

    /**
     * Insert and update koulutus object to database. When komoto OID is set,
     * then the post method will be handled as koulutus data update.
     *
     * @param dto
     * @return
     */
    private ResultV1RDTO<KoulutusV1RDTO> postKorkeakouluKoulutus(KoulutusKorkeakouluV1RDTO dto) {
        KoulutusmoduuliToteutus fullKomotoWithKomo = null;
        ResultV1RDTO<KoulutusV1RDTO> result = new ResultV1RDTO<KoulutusV1RDTO>();
        KoulutusValidator.validateKoulutusKorkeakoulu(dto, result);

        if (dto.getOpintojenRakenneKuvas() != null && !dto.getOpintojenRakenneKuvas().isEmpty()) {
            //validate optional images
            for (Entry<String, KuvaV1RDTO> e : dto.getOpintojenRakenneKuvas().entrySet()) {
                //do not validate null img objects as the img keys will be use to delete images
                if (e.getValue() != null) {
                    KoulutusValidator.validateKoulutusKuva(e.getValue(), e.getKey(), result);
                }
            }
        }

        if (!result.hasErrors() && validateOrganisation(
                dto.getOrganisaatio(),
                result, KoulutusValidationMessages.KOULUTUS_TARJOAJA_MISSING,
                KoulutusValidationMessages.KOULUTUS_TARJOAJA_INVALID)) {

            if (dto.getOid() != null && dto.getOid().length() > 0) {
                //update korkeakoulu koulutus
                final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(dto.getOid());
                KoulutusValidator.validateKoulutusUpdate(komoto, result);
                if (result.hasErrors()) {
                    return result;
                }

                fullKomotoWithKomo = updateKoulutusKorkeakoulu(komoto, dto);
            } else {
                //create korkeakoulu koulutus
                fullKomotoWithKomo = insertKoulutusKorkeakoulu(dto, false);
            }

            indexerResource.indexKoulutukset(Lists.newArrayList(fullKomotoWithKomo.getId()));
            final RestParam param = RestParam.showImageAndShowMeta(contextDataService.getCurrentUserLang());

            result.setResult(converterToRDTO.convert(dto.getClass(), fullKomotoWithKomo, param));
        } else {
            result.setStatus(ResultV1RDTO.ResultStatus.VALIDATION);
            result.setResult(dto);
        }

        return result;
    }

    /**
     * Call only after organisation oid validation check.
     *
     * @param dto
     * @param result
     */
    private boolean validateOrganisation(OrganisaatioV1RDTO dto, ResultV1RDTO result, final KoulutusValidationMessages kvmMissing, final KoulutusValidationMessages kvmInvalid) {
        if (dto == null || dto.getOid() == null || dto.getOid().isEmpty()) {
            result.addError(ErrorV1RDTO.createValidationError(kvmMissing.getFieldName(), kvmMissing.lower()));
        } else {
            try {
                final OrganisaatioDTO org = organisaatioService.findByOid(dto.getOid());
                if (org == null || org.getOid() == null || org.getOid().isEmpty()) {
                    result.addError(ErrorV1RDTO.createValidationError(kvmInvalid.getFieldName(), kvmInvalid.lower()));
                } else {
                    return true;
                }
            } catch (Exception e) {
                LOG.error("Organisation service call failed", e);
                result.addError(ErrorV1RDTO.createValidationError(kvmInvalid.getFieldName(), kvmInvalid.lower()));
            }
        }

        return false;
    }

    private ResultV1RDTO<KoulutusV1RDTO> postLukioKoulutus(KoulutusLukioV1RDTO dto) {
        KoulutusmoduuliToteutus fullKomotoWithKomo = null;

        ResultV1RDTO<KoulutusV1RDTO> result = new ResultV1RDTO<KoulutusV1RDTO>();
        KoulutusValidator.validateKoulutusLukio(dto, result);

        if (!result.hasErrors() && validateOrganisation(
                dto.getOrganisaatio(), result,
                KoulutusValidationMessages.KOULUTUS_TARJOAJA_MISSING,
                KoulutusValidationMessages.KOULUTUS_TARJOAJA_INVALID)) {
            if (dto.getOid() != null && dto.getOid().length() > 0) {
                //update korkeakoulu koulutus
                final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(dto.getOid());
                KoulutusValidator.validateKoulutusUpdate(komoto, result);
                if (result.hasErrors()) {
                    return result;
                }

                fullKomotoWithKomo = updateLukiokoulu(komoto, dto);
            } else {
                //create korkeakoulu koulutus
                fullKomotoWithKomo = insertKoulutusLukiokoulu(dto);
            }

            indexerResource.indexKoulutukset(Lists.newArrayList(fullKomotoWithKomo.getId()));
            final RestParam param = RestParam.noImageAndShowMeta(contextDataService.getCurrentUserLang());

            result.setResult(converterToRDTO.convert(dto.getClass(), fullKomotoWithKomo, param));
        } else {
            result.setStatus(ResultV1RDTO.ResultStatus.VALIDATION);
            result.setResult(dto);
        }

        return result;
    }

    private ResultV1RDTO<KoulutusV1RDTO> postAmmatillinenPerustutkintoNayttotutkintona(KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO dto) {
        KoulutusmoduuliToteutus fullKomotoWithKomo = null;

        ResultV1RDTO<KoulutusV1RDTO> result = new ResultV1RDTO<KoulutusV1RDTO>();
        KoulutusValidator.validateKoulutusAmm(dto, result);

        if (!result.hasErrors()
                && validateOrganisation(dto.getOrganisaatio(),
                        result, KoulutusValidationMessages.KOULUTUS_TARJOAJA_MISSING,
                        KoulutusValidationMessages.KOULUTUS_TARJOAJA_INVALID)
                && validateOrganisation(dto.getJarjestavaOrganisaatio(),
                        result, KoulutusValidationMessages.KOULUTUS_JARJESTAJA_MISSING,
                        KoulutusValidationMessages.KOULUTUS_JARJESTAJA_INVALID)) {

            if (dto.getOid() != null && dto.getOid().length() > 0) {
                //update korkeakoulu koulutus
                final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(dto.getOid());
                KoulutusValidator.validateKoulutusUpdate(komoto, result);
                if (result.hasErrors()) {
                    return result;
                }

                fullKomotoWithKomo = updateKoulutusAmmattillinenNayttotutkintona(komoto, dto);
            } else {
                //create korkeakoulu koulutus
                fullKomotoWithKomo = insertKoulutusAmmattillinenNayttotutkintona(dto);
            }
            Preconditions.checkNotNull(fullKomotoWithKomo, "KOMOTO object cannot be null!");
            Preconditions.checkNotNull(fullKomotoWithKomo.getId(), "KOMOTO ID cannot be null!");
            indexerResource.indexKoulutukset(Lists.newArrayList(fullKomotoWithKomo.getId()));

            final RestParam param = RestParam.noImageAndShowMeta(contextDataService.getCurrentUserLang());
            result.setResult(convertAmmValmentavaToDTO(fullKomotoWithKomo, param));
        } else {
            result.setStatus(ResultV1RDTO.ResultStatus.VALIDATION);
            result.setResult(dto);
        }

        return result;
    }

    private KoulutusmoduuliToteutus insertKoulutusKorkeakoulu(final KoulutusKorkeakouluV1RDTO dto, final boolean addKomotoToKomo) {
        Preconditions.checkNotNull(dto.getKomotoOid() != null, "External KOMOTO OID not allowed. OID : %s.", dto.getKomotoOid());
        Preconditions.checkNotNull(dto.getKomoOid() != null, "External KOMO OID not allowed. OID : %s.", dto.getKomoOid());

        final KoulutusmoduuliToteutus newKomoto = convertToEntity.convert(dto, contextDataService.getCurrentUserOid(), addKomotoToKomo);
        Preconditions.checkNotNull(newKomoto, "KOMOTO conversion to database object failed : object : %s.", ReflectionToStringBuilder.toString(dto));
        Preconditions.checkNotNull(newKomoto.getKoulutusmoduuli(), "KOMO conversion to database object failed : object :  %s.", ReflectionToStringBuilder.toString(dto));

        permissionChecker.checkCreateKoulutus(dto.getOrganisaatio().getOid());
        koulutusmoduuliDAO.insert(newKomoto.getKoulutusmoduuli());
        return koulutusmoduuliToteutusDAO.insert(newKomoto);
    }

    private KoulutusmoduuliToteutus insertKoulutusLukiokoulu(final KoulutusLukioV1RDTO dto) {
        Preconditions.checkNotNull(dto.getKomotoOid() != null, "External KOMOTO OID not allowed. OID : %s.", dto.getKomotoOid());
        Preconditions.checkNotNull(dto.getKomoOid() != null, "External KOMO OID not allowed. OID : %s.", dto.getKomoOid());

        final KoulutusmoduuliToteutus newKomoto = convertToEntity.convert(dto, contextDataService.getCurrentUserOid());
        Preconditions.checkNotNull(newKomoto, "KOMOTO conversion to database object failed : object : %s.", ReflectionToStringBuilder.toString(dto));
        Preconditions.checkNotNull(newKomoto.getKoulutusmoduuli(), "KOMO conversion to database object failed : object :  %s.", ReflectionToStringBuilder.toString(dto));

        permissionChecker.checkCreateKoulutus(dto.getOrganisaatio().getOid());
        return koulutusmoduuliToteutusDAO.insert(newKomoto);
    }

    private KoulutusmoduuliToteutus insertKoulutusAmmattillinenNayttotutkintona(final KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO dto) {
        Preconditions.checkNotNull(dto.getKomotoOid() != null, "External KOMOTO OID not allowed. OID : %s.", dto.getKomotoOid());
        Preconditions.checkNotNull(dto.getKomoOid() != null, "External KOMO OID not allowed. OID : %s.", dto.getKomoOid());

        final KoulutusmoduuliToteutus newKomoto = convertToEntity.convert(dto, contextDataService.getCurrentUserOid());
        Preconditions.checkNotNull(newKomoto, "KOMOTO conversion to database object failed : object : %s.", ReflectionToStringBuilder.toString(dto));
        Preconditions.checkNotNull(newKomoto.getKoulutusmoduuli(), "KOMO conversion to database object failed : object :  %s.", ReflectionToStringBuilder.toString(dto));

        permissionChecker.checkCreateKoulutus(dto.getOrganisaatio().getOid());
        KoulutusmoduuliToteutus base = koulutusmoduuliToteutusDAO.insert(newKomoto);

        if (dto.getValmistavaKoulutus() != null && base.getNayttotutkintoValmistavaKoulutus() == null) {
            insertValmistavaKoulutusToNayttotutkinto(base, convertToEntity.convert(dto.getValmistavaKoulutus(), dto, contextDataService.getCurrentUserOid()));
        }

        return base;
    }

    private KoulutusmoduuliToteutus updateKoulutusKorkeakoulu(KoulutusmoduuliToteutus komoto, final KoulutusKorkeakouluV1RDTO dto) {
        permissionChecker.checkUpdateKoulutusByTarjoajaOid(komoto.getTarjoaja());
        return convertToEntity.convert(dto, contextDataService.getCurrentUserOid(), false);
    }

    private KoulutusmoduuliToteutus updateLukiokoulu(KoulutusmoduuliToteutus komoto, final KoulutusLukioV1RDTO dto) {
        permissionChecker.checkUpdateKoulutusByTarjoajaOid(komoto.getTarjoaja());
        return convertToEntity.convert(dto, contextDataService.getCurrentUserOid());
    }

    private KoulutusmoduuliToteutus updateKoulutusAmmattillinenNayttotutkintona(KoulutusmoduuliToteutus komoto, final KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO dto) {
        permissionChecker.checkUpdateKoulutusByTarjoajaOid(komoto.getTarjoaja());

        //detached komoto
        KoulutusmoduuliToteutus base = convertToEntity.convert(dto, contextDataService.getCurrentUserOid());

        if (dto instanceof KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO) {
            KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO naytto = (KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO) dto;
            if (naytto.getValmistavaKoulutus() != null) { //insert or update
                //No need for update handling as the converter will update valmentava komoto entity to db.
                KoulutusmoduuliToteutus valmistava = convertToEntity.convert(
                        naytto.getValmistavaKoulutus(),
                        naytto,
                        contextDataService.getCurrentUserOid());

                if (base.getNayttotutkintoValmistavaKoulutus() == null) {
                    //insert new komoto to db
                    insertValmistavaKoulutusToNayttotutkinto(base, valmistava);
                }
            } else {//delete row from db
                KoulutusmoduuliToteutus nayttot = koulutusmoduuliToteutusDAO.findByOid(naytto.getOid());
                if (nayttot != null && nayttot.getNayttotutkintoValmistavaKoulutus() != null) {
                    try {
                        indexerResource.deleteKoulutus(Lists.<String>newArrayList(nayttot.getNayttotutkintoValmistavaKoulutus().getOid()));
                    } catch (IOException ex) {
                        LOG.error("Index delete failed by OID : {}", nayttot.getOid(), ex);
                    }
                    //remove child komoto from base komoto
                    koulutusmoduuliToteutusDAO.remove(nayttot.getNayttotutkintoValmistavaKoulutus());
                }
            }
        }

        return base;
    }

    @Override
    public ResultV1RDTO<KoulutusV1RDTO> deleteByOid(final String komotoOid) {

        ResultV1RDTO<KoulutusV1RDTO> result = new ResultV1RDTO<KoulutusV1RDTO>();
        final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findByOid(komotoOid);
        KoulutusValidator.validateKoulutusUpdate(komoto, result);

        if (result.getStatus().equals(ResultStatus.OK)) {
            permissionChecker.checkRemoveKoulutus(komotoOid);

            Map<String, Integer> hkKoulutusMap = Maps.newHashMap();

            for (Hakukohde hk : komoto.getHakukohdes()) {
                if (hk.getTila() != TarjontaTila.POISTETTU) { //skippaa poistetut OVT-7518
                    //laske kuinka monta aktiivista (ei poistettua) koulutusta hakukohteessa on kiinni
                    int koulutusCount = 0;

                    for (KoulutusmoduuliToteutus hkKomoto : hk.getKoulutusmoduuliToteutuses()) {
                        if (hkKomoto.getTila() != TarjontaTila.POISTETTU) {
                            koulutusCount++;
                        }
                    }
                    hkKoulutusMap.put(hk.getOid(), koulutusCount);
                }
            }

            Koulutusmoduuli komo = komoto.getKoulutusmoduuli();
            final String komoOid = komo.getOid();
            final String userOid = contextDataService.getCurrentUserOid();

            switch (komo.getKoulutustyyppiEnum()) {
                case KORKEAKOULUTUS:
                    //safe delete komoto, if 'sister' komoto search gives an empty result, then safe delete the komo.

                    final List<String> parent = koulutusSisaltyvyysDAO.getParents(komoOid);
                    final List<String> children = koulutusSisaltyvyysDAO.getChildren(komoOid);

                    KoulutusValidator.validateKoulutusDelete(komoto, koulutusmoduuliDAO.findActiveKomotosByKomoOid(komoOid), children, parent, hkKoulutusMap, result);

                    if (!result.hasErrors()) {
                        koulutusmoduuliToteutusDAO.safeDelete(komotoOid, userOid);

                        final List<KoulutusmoduuliToteutus> komotos = koulutusmoduuliDAO.findActiveKomotosByKomoOid(komoOid);
                        if (komotos.isEmpty()) {
                            //no komotos found, I quess it's also ok to remove the komo.
                            koulutusmoduuliDAO.safeDelete(komoto.getKoulutusmoduuli().getOid(), userOid);
                        }
                        List<Long> ids = Lists.<Long>newArrayList();
                        ids.add(komoto.getId());
                        indexerResource.indexKoulutukset(ids);
                    }
                    break;
                case AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA:
                    KoulutusValidator.validateKoulutusDelete(komoto, Lists.<KoulutusmoduuliToteutus>newArrayList(), Lists.<String>newArrayList(), Lists.<String>newArrayList(), hkKoulutusMap, result);
                    if (!result.hasErrors()) {
                        if (komoto.getNayttotutkintoValmistavaKoulutus() != null && komoto.getNayttotutkintoValmistavaKoulutus().getOid() != null) {
                            koulutusmoduuliToteutusDAO.safeDelete(komoto.getNayttotutkintoValmistavaKoulutus().getOid(), userOid);
                        }
                        koulutusmoduuliToteutusDAO.safeDelete(komotoOid, userOid);
                    }
                    break;
                default:
                    //normal safe delete for komoto, do not touch komo
                    KoulutusValidator.validateKoulutusDelete(komoto, Lists.<KoulutusmoduuliToteutus>newArrayList(), Lists.<String>newArrayList(), Lists.<String>newArrayList(), hkKoulutusMap, result);
                    if (!result.hasErrors()) {
                        koulutusmoduuliToteutusDAO.safeDelete(komotoOid, userOid);
                    }
                    break;
            }
        }

        return result;
    }

//    public ResultV1RDTO deleteByOid(String oid) {
//        permissionChecker.checkRemoveKoulutus(oid);
//        KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findByOid(oid);
//
//        ResultV1RDTO result = new ResultV1RDTO();
//
//        if (komoto.getHakukohdes().isEmpty()) {
//            this.koulutusmoduuliToteutusDAO.remove(komoto);
//            try {
//                solrIndexer.deleteKoulutus(Lists.newArrayList(oid));
//
//            } catch (IOException e) {
//                throw new TarjontaBusinessException("indexing.error", e);
//            }
//        } else {
//            result.setStatus(ResultStatus.VALIDATION);
//        }
//        return result;
//    }
    @Override
    public ResultV1RDTO<List<NimiJaOidRDTO>> getHakukohteet(String oid) {
        HakukohteetKysely ks = new HakukohteetKysely();
        ks.getKoulutusOids().add(oid);

        HakukohteetVastaus vs = tarjontaSearchService.haeHakukohteet(ks);
        List<NimiJaOidRDTO> ret = new ArrayList<NimiJaOidRDTO>();
        for (HakukohdePerustieto hk : vs.getHakukohteet()) {
            ret.add(new NimiJaOidRDTO(hk.getNimi(), hk.getOid(), hk.getHakuOid()));
        }
        return new ResultV1RDTO<List<NimiJaOidRDTO>>(ret);
    }

    @Override
    public KuvausV1RDTO loadTekstis(String oid) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        Preconditions.checkNotNull(komoto, "KOMOTO not found by OID '%s'.", oid);

        KuvausV1RDTO komotoTekstiDto = komotoKoulutusConverters.convertMonikielinenTekstiToTekstiDTO(komoto.getTekstit(), true);
        Koulutusmoduuli komo = komoto.getKoulutusmoduuli();
        KuvausV1RDTO<KomoTeksti> komoTekstiDto = komoKoulutusConverters.convertMonikielinenTekstiToTekstiDTO(komo.getTekstit(), true);
        komotoTekstiDto.putAll(komoTekstiDto);

        //combine komo&komoto text data to the dto;
        return komotoTekstiDto;
    }

    @Override
    public ResultV1RDTO<KuvausV1RDTO> loadKomotoTekstis(String oid) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        Preconditions.checkNotNull(komoto, "KOMOTO not found by OID '%s'.", oid);

        ResultV1RDTO<KuvausV1RDTO> resultRDTO = new ResultV1RDTO<KuvausV1RDTO>();
        resultRDTO.setResult(komotoKoulutusConverters.convertMonikielinenTekstiToTekstiDTO(komoto.getTekstit(), true));
        return resultRDTO;
    }

    @Override
    public Response saveKomotoTekstis(String oid, KuvausV1RDTO<KomotoTeksti> dto) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        Preconditions.checkNotNull(komoto, "KOMOTO not found by OID '%s'.", oid);

        permissionChecker.checkUpdateKoulutusByTarjoajaOid(komoto.getTarjoaja());
        komotoKoulutusConverters.convertTekstiDTOToMonikielinenTeksti(dto, komoto.getTekstit());
        komoto.setLastUpdatedByOid(contextDataService.getCurrentUserOid());
        koulutusmoduuliToteutusDAO.update(komoto);
        return Response.ok().build();
    }

    @Override
    public ResultV1RDTO<KuvausV1RDTO> loadKomoTekstis(String oid) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        Preconditions.checkNotNull(komoto, "KOMOTO not found by OID '%s'.", oid);

        ResultV1RDTO<KuvausV1RDTO> resultRDTO = new ResultV1RDTO<KuvausV1RDTO>();
        resultRDTO.setResult(komoKoulutusConverters.convertMonikielinenTekstiToTekstiDTO(komoto.getKoulutusmoduuli().getTekstit(), true));
        return resultRDTO;
    }

    @Override
    public ResultV1RDTO saveKomoTekstis(String oid, KuvausV1RDTO<KomoTeksti> dto) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        ResultV1RDTO result = new ResultV1RDTO();
        KoulutusValidator.validateKoulutusUpdate(komoto, result);
        if (result.hasErrors()) {
            return result;
        }

        Preconditions.checkNotNull(komoto, "KOMOTO not found by OID '%s'.", oid);
        Koulutusmoduuli komo = komoto.getKoulutusmoduuli();

        permissionChecker.checkUpdateKoulutusmoduuli();
        komoKoulutusConverters.convertTekstiDTOToMonikielinenTeksti(dto, komo.getTekstit());
        koulutusmoduuliDAO.update(komo);

        return result;
    }

    @Override
    public ResultV1RDTO<KoulutusmoduuliStandardRelationV1RDTO> getKoodistoRelations(
            String koulutuskoodi,
            String defaults, //new String("field:uri, field:uri, ....")
            Boolean showMeta,
            String userLang) {

        return getKoodistoRelations(koulutuskoodi, null, defaults, showMeta, userLang);
    }

    @Override
    public ResultV1RDTO<KoulutusmoduuliStandardRelationV1RDTO> getKoodistoRelations(
            String koulutuskoodi,
            ToteutustyyppiEnum koulutustyyppiUri,
            String defaults, //new String("field:uri, field:uri, ....")
            Boolean showMeta, //for future use
            String userLang //for future use
    ) {

        Preconditions.checkNotNull(koulutuskoodi, "Koulutuskoodi parameter cannot be null.");
        ResultV1RDTO<KoulutusmoduuliStandardRelationV1RDTO> result = new ResultV1RDTO<KoulutusmoduuliStandardRelationV1RDTO>();
        RestParam byUserRequest = RestParam.byUserRequest(showMeta, false, userLang);

        try {
            //split params, if any
            KoulutusmoduuliStandardRelationV1RDTO dto = KoulutusmoduuliStandardRelationV1RDTO.class.newInstance();

            if (koulutustyyppiUri != null) {
                switch (koulutustyyppiUri) {
                    case KORKEAKOULUTUS:
                        dto = KoulutusmoduuliKorkeakouluRelationV1RDTO.class.newInstance();
                        break;
                    case LUKIOKOULUTUS:
                    case LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA:
                        dto = KoulutusmoduuliLukioRelationV1RDTO.class.newInstance();
                        break;
                    case AMMATILLINEN_PERUSTUTKINTO:
                    case AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA:
                    case AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA:
                        dto = KoulutusmoduuliAmmatillinenRelationV1RDTO.class.newInstance();
                        break;
                    default:
                        /*
                         * return standard relations
                         */
                        break;
                }
            }

            Map<String, String> defaultsMap = Maps.<String, String>newHashMap();
            if (defaults != null && !defaults.isEmpty()) {
                for (String fieldAndValue : defaults.split(",")) {
                    final String[] splitFieldValue = fieldAndValue.split(":");
                    if (splitFieldValue != null && splitFieldValue.length == 2) {
                        defaultsMap.put(splitFieldValue[0], splitFieldValue[1]);
                    }
                }

                final BeanWrapperImpl beanWrapper = new BeanWrapperImpl(dto);
                for (Entry<String, String> e : defaultsMap.entrySet()) {
                    if (e.getValue() != null && !e.getValue().isEmpty() && beanWrapper.isReadableProperty(e.getKey())) {
                        //only uri is needed, as it will be expanded to koodi object
                        KoodiV1RDTO koodi = new KoodiV1RDTO();
                        koodi.setUri(e.getValue());
                        beanWrapper.setPropertyValue(e.getKey(), koodi);
                    }
                }
            }

            if (koulutuskoodi.contains("_")) {
                //Very simple parameter check, if an undescore char is in the string, then the data is koodisto service koodi URI.
                result.setResult(koulutuskoodiRelations.getKomoRelationByKoulutuskoodiUri(dto.getClass(), defaultsMap.isEmpty() ? null : dto, koulutuskoodi, byUserRequest));
            } else {
                SearchKoodisByKoodistoCriteriaType search = KoodiServiceSearchCriteriaBuilder.koodisByArvoAndKoodistoUri(koulutuskoodi, KoodistoURI.KOODISTO_TUTKINTO_URI);
                List<KoodiType> searchKoodisByKoodisto = koodiService.searchKoodisByKoodisto(search);
                if (searchKoodisByKoodisto == null || searchKoodisByKoodisto.isEmpty()) {
                    throw new TarjontaBusinessException("No koulutuskoodi koodisto KoodiType object found by '" + koulutuskoodi + "'.");
                }
                result.setResult(koulutuskoodiRelations.getKomoRelationByKoulutuskoodiUri(dto.getClass(), defaultsMap.isEmpty() ? null : dto, searchKoodisByKoodisto.get(0).getKoodiUri(), byUserRequest));
            }
        } catch (InstantiationException ex) {
            LOG.error("Relation initialization error.", ex);
            result.setStatus(ResultV1RDTO.ResultStatus.ERROR);
        } catch (IllegalAccessException ex) {
            LOG.error("Relation illegal access error.", ex);
            result.setStatus(ResultV1RDTO.ResultStatus.ERROR);
        } catch (GenericFault ex) {
            LOG.error("Koodisto relation error.", ex);
            result.setStatus(ResultV1RDTO.ResultStatus.ERROR);
        } catch (TarjontaBusinessException ex) {
            LOG.error("Koodisto relation error.", ex);
            result.setStatus(ResultV1RDTO.ResultStatus.ERROR);
        }

        return result;
    }

    @Override
    public Response deleteTeksti(String oid, String key, String uri) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Transactional(readOnly = false)
    public ResultV1RDTO<Tilamuutokset> updateTila(String oid, TarjontaTila tila) {
        permissionChecker.checkUpdateKoulutusByKoulutusOid(oid);

        Tila tilamuutos = new Tila(Tyyppi.KOMOTO, tila, oid);

        Tilamuutokset tm = null;
        try {
            tm = publicationDataService.updatePublicationStatus(Lists.newArrayList(tilamuutos));
        } catch (IllegalArgumentException iae) {
            ResultV1RDTO<Tilamuutokset> r = new ResultV1RDTO<Tilamuutokset>();
            r.addError(ErrorV1RDTO.createValidationError(null, iae.getMessage()));
            return r;
        }

        //indeksoi uudelleen muuttunut data
        indexerResource.indexMuutokset(tm);

        return new ResultV1RDTO<Tilamuutokset>(tm);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchInfo(
            String searchTerms,
            List<String> organisationOids,
            List<String> koulutusOids,
            String komotoTila,
            String alkamisKausi,
            Integer alkamisVuosi,
            List<String> koulutustyyppi,
            List<ToteutustyyppiEnum> toteutustyyppi,
            @Deprecated List<KoulutusasteTyyppi> koulutusastetyyppi,
            String komoOid) {

        organisationOids = organisationOids != null ? organisationOids : new ArrayList<String>();

        KoulutuksetKysely q = new KoulutuksetKysely();

        q.setNimi(searchTerms);
        q.setkomoOid(komoOid);
        q.setKoulutuksenAlkamiskausi(alkamisKausi);
        q.setKoulutuksenAlkamisvuosi(alkamisVuosi);
        q.getTarjoajaOids().addAll(organisationOids);
        q.getKoulutusOids().addAll(koulutusOids);
        q.setKoulutuksenTila(komotoTila == null ? null : fi.vm.sade.tarjonta.shared.types.TarjontaTila.valueOf(komotoTila).asDto());
        q.getKoulutusasteTyypit().addAll(koulutusastetyyppi);
        q.getKoulutustyyppi().addAll(koulutustyyppi);
        q.getTotetustyyppi().addAll(toteutustyyppi);
        KoulutuksetVastaus r = tarjontaSearchService.haeKoulutukset(q);

        return new ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>(converterV1.fromKoulutuksetVastaus(r));
    }

    @Override
    public ResultV1RDTO deleteKuva(String oid, String kieliUri) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        Preconditions.checkNotNull(kieliUri, "Koodisto language URI cannot be null.");
        ResultV1RDTO result = new ResultV1RDTO();
        final BinaryData bin = koulutusmoduuliToteutusDAO.findKuvaByKomotoOidAndKieliUri(oid, kieliUri);
        if (bin != null) {
            final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(oid);

            KoulutusValidator.validateKoulutusUpdate(komoto, result);
            if (result.hasErrors()) {
                return result;
            }

            permissionChecker.checkRemoveKoulutusKuva(oid);
            Map<String, BinaryData> kuvat = komoto.getKuvat();
            kuvat.remove(kieliUri);
            komoto.setLastUpdatedByOid(contextDataService.getCurrentUserOid());
            this.koulutusmoduuliToteutusDAO.update(komoto);
        }

        return result;
    }

    @Override
    public ResultV1RDTO<KuvaV1RDTO> getKuva(String oid, String kieliUri) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        Preconditions.checkNotNull(kieliUri, "Koodisto language URI cannot be null.");

        KuvaV1RDTO dto = new KuvaV1RDTO();
        ResultV1RDTO<KuvaV1RDTO> result = new ResultV1RDTO<KuvaV1RDTO>(dto);

        final BinaryData bin = koulutusmoduuliToteutusDAO.findKuvaByKomotoOidAndKieliUri(oid, kieliUri);
        if (bin != null) {
            dto = new KuvaV1RDTO(bin.getFilename(), bin.getMimeType(), kieliUri, Base64.encodeBase64String(bin.getData()));
            result.setResult(dto);
        } else {
            result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
        }
        return result;
    }

    @Override
    public ResultV1RDTO<List<KuvaV1RDTO>> getKuvas(String oid) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        ResultV1RDTO<List<KuvaV1RDTO>> result = new ResultV1RDTO<List<KuvaV1RDTO>>();
        Map<String, BinaryData> findAllImagesByKomotoOid = koulutusmoduuliToteutusDAO.findAllImagesByKomotoOid(oid);
        if (findAllImagesByKomotoOid != null && !findAllImagesByKomotoOid.isEmpty()) {
            List<KuvaV1RDTO> list = Lists.<KuvaV1RDTO>newArrayList();
            for (Entry<String, BinaryData> e : findAllImagesByKomotoOid.entrySet()) {
                list.add(new KuvaV1RDTO(e.getValue().getFilename(), e.getValue().getMimeType(), e.getKey(), Base64.encodeBase64String(e.getValue().getData())));
            }
            result.setResult(list);
        } else {
            result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
        }
        return result;
    }

    /**
     * Legacy HTML4 image upload for IE9.
     *
     * @param oid komoto OID
     * @param kieliUri koodisto language uri, without version
     * @param body
     * @return
     */
    @Override
    public Response saveHtml4Kuva(String oid, String kieliUri, MultipartBody body) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        Preconditions.checkNotNull(kieliUri, "Koodisto language URI cannot be null.");
        Preconditions.checkNotNull(body, "MultipartBody cannot be null.");
        LOG.info("in saveKuva - komoto OID : {}, kieliUri : {}, bodyType : {}", oid, kieliUri, body.getType());
        ResultV1RDTO<KuvaV1RDTO> result = new ResultV1RDTO<KuvaV1RDTO>();
        final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(oid);

        KoulutusValidator.validateKoulutusUpdate(komoto, result);
        if (result.hasErrors()) {
            return Response.serverError().build();
        }

        permissionChecker.checkAddKoulutusKuva(komoto.getTarjoaja());
        Attachment att = body.getRootAttachment();

        KoulutusValidator.validateKieliUri(kieliUri, "kieliUri", result);
        validateMimeType(att.getDataHandler().getContentType(), "contentType", result);
        if (result.hasErrors()) {
            return Response.serverError().build();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        InputStream in = null;

        try {
            in = att.getDataHandler().getInputStream();

            try {
                IOUtils.copy(in, baos);
                final String filename = att.getContentDisposition() != null ? att.getContentDisposition().getParameter("filename") : "";
                final String contentType = att.getDataHandler().getContentType();

                BinaryData bin = null;
                if (komoto.isKuva(kieliUri)) {
                    bin = komoto.getKuvat().get(kieliUri);
                } else {
                    bin = new BinaryData();
                }

                bin.setData(baos.toByteArray());
                bin.setFilename(filename);
                bin.setMimeType(contentType);

                komoto.setKuvaByUri(kieliUri, bin);
                komoto.setLastUpdatedByOid(contextDataService.getCurrentUserOid());
                this.koulutusmoduuliToteutusDAO.update(komoto);
                result.setStatus(ResultV1RDTO.ResultStatus.OK);
            } catch (IOException ex) {
                LOG.error("BinaryData save failed for komoto OID {}.", oid, ex);
                result.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(baos);
            }
        } catch (IOException ex) {
            LOG.error("Image upload failed for komoto OID {}.", oid, ex);
        } finally {
            IOUtils.closeQuietly(in);
        }

        return Response.ok().build();
    }

    /**
     * HTML5 image upload.
     *
     * @param oid komoto OID
     * @param kuva image DTO
     * @return ResultV1DTO with status and error information.
     */
    @Override
    public ResultV1RDTO<KuvaV1RDTO> saveHtml5Kuva(String oid, KuvaV1RDTO kuva) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        Preconditions.checkNotNull(kuva, "KuvaV1RDTO cannot be null.");
        LOG.debug("in saveKuva - komoto OID : {}, kieliUri : {}, bodyType : {}", oid, kuva.getKieliUri(), kuva.getFilename());

        ResultV1RDTO<KuvaV1RDTO> result = new ResultV1RDTO<KuvaV1RDTO>();

        /*
         * Check komoto status
         */
        final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(oid);
        KoulutusValidator.validateKoulutusUpdate(komoto, result);
        if (result.hasErrors()) {
            return result;
        }

        /*
         * Check user permission
         */
        permissionChecker.checkAddKoulutusKuva(komoto.getTarjoaja());

        /*
         * Check binary data
         */
        KoulutusValidator.validateKoulutusKuva(kuva, result);
        if (result.hasErrors()) {
            return result;
        }

        convertToEntity.saveHtml5Image(komoto, kuva, contextDataService.getCurrentUserOid());

        return new ResultV1RDTO<KuvaV1RDTO>(kuva);
    }

    private String findKoodistoKoodiKoulutusasteUri(KoulutusmoduuliToteutus komoto) {
        final Koulutusmoduuli childKomo = komoto.getKoulutusmoduuli();
        switch (childKomo.getKoulutustyyppiEnum()) {
            case KORKEAKOULUTUS:
                //stored into child komo and also to komoto
                return childKomo.getKoulutusasteUri() != null && !childKomo.getKoulutusasteUri().isEmpty()
                        ? childKomo.getKoulutusasteUri() : komoto.getKoulutusasteUri();
            default:
                //the uri is stored some other location
                if (childKomo.getKoulutusasteUri() != null && !childKomo.getKoulutusasteUri().isEmpty()) {
                    return childKomo.getKoulutusasteUri();
                } else {
                    //search parent komo
                    final Koulutusmoduuli parentKomo = koulutusmoduuliDAO.findParentKomo(childKomo);
                    if (parentKomo != null && parentKomo.getKoulutusasteUri() != null && !parentKomo.getKoulutusasteUri().isEmpty()) {
                        //normal location:
                        return parentKomo.getKoulutusasteUri();
                    } else if (komoto.getKoulutusasteUri() != null && !komoto.getKoulutusasteUri().isEmpty()) {
                        //latest uri location:
                        return komoto.getKoulutusasteUri();
                    }
                }
                break;
        }

        return null;
    }

    @Override
    public ResultV1RDTO<KoulutusCopyResultV1RDTO> copyOrMove(final String komotoOid, KoulutusCopyV1RDTO koulutusCopy) {
        Preconditions.checkNotNull(komotoOid, "KOMOTO OID cannot be null.");
        ResultV1RDTO<KoulutusCopyResultV1RDTO> result = new ResultV1RDTO<KoulutusCopyResultV1RDTO>();

        final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(komotoOid);
        KoulutusValidator.validateKoulutusUpdate(komoto, result);
        if (result.hasErrors()) {
            return result;
        }
        if (koulutusCopy == null) {
            result.addError(ErrorV1RDTO.createValidationError(null, KoulutusValidationMessages.KOULUTUS_INPUT_OBJECT_MISSING.lower()));
        } else if (koulutusCopy.getMode() == null) {
            result.addError(ErrorV1RDTO.createValidationError("mode", KoulutusValidationMessages.KOULUTUS_INPUT_PARAM_MISSING.lower()));
        } else if (koulutusCopy.getOrganisationOids() == null || koulutusCopy.getOrganisationOids().isEmpty()) {
            result.addError(ErrorV1RDTO.createValidationError("organisationOids", KoulutusValidationMessages.KOULUTUS_TARJOAJA_MISSING.lower()));
        } else {
            for (String orgOid : koulutusCopy.getOrganisationOids()) {
                final OrganisaatioDTO org = organisaatioService.findByOid(orgOid);
                if (org == null) {
                    result.addError(ErrorV1RDTO.createValidationError("organisationOids[" + orgOid + "]", KoulutusValidationMessages.KOULUTUS_TARJOAJA_INVALID.lower(), orgOid));
                } else if (!oppilaitosKoodiRelations.isKoulutusAllowedForOrganisation(
                        orgOid,
                        findKoodistoKoodiKoulutusasteUri(komoto))) {
                    result.addError(ErrorV1RDTO.createValidationError("organisationOids[" + orgOid + "]", KoulutusValidationMessages.KOULUTUS_TARJOAJA_INVALID.lower(), orgOid));
                }
            }
        }

        if (result.hasErrors()) {
            return result;
        }

        permissionChecker.checkCopyKoulutus(koulutusCopy.getOrganisationOids());

        result.setResult(new KoulutusCopyResultV1RDTO(komotoOid));

        switch (koulutusCopy.getMode()) {
            case COPY:
                final List<String> children = koulutusSisaltyvyysDAO.getChildren(komotoOid);
                Preconditions.checkNotNull(children, "KOMO link list cannot be null");

                List<Long> newKomotoIds = Lists.<Long>newArrayList();

                List<String> newKomoChildOids = Lists.<String>newArrayList();
                for (String orgOid : koulutusCopy.getOrganisationOids()) {
                    KoulutusmoduuliToteutus persisted = null;

                    switch (getType(komoto)) {
                        case KORKEAKOULUTUS:
                            //TODO : in a distant future, copy also komo
                            //currenlty this will only add new 'sister' komoto, and create a link to previously created komo.
                            persisted = insertKoulutusKorkeakoulu((KoulutusKorkeakouluV1RDTO) koulutusDtoForCopy(KoulutusKorkeakouluV1RDTO.class, komoto, orgOid), true);
                            break;
                        case LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA:
                            persisted = insertKoulutusLukiokoulu((KoulutusLukioAikuistenOppimaaraV1RDTO) koulutusDtoForCopy(KoulutusLukioAikuistenOppimaaraV1RDTO.class, komoto, orgOid));
                            break;
                        case AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA:
                            persisted = insertKoulutusAmmattillinenNayttotutkintona((KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO) koulutusDtoForCopy(KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO.class, komoto, orgOid));
                            break;
                        default:
                            throw new RuntimeException(
                                    "Not implemented type : " + getType(komoto));
                    }

                    if (persisted == null) {
                        throw new RuntimeException("Copy failed : " + getType(komoto));
                    }

                    newKomoChildOids.add(persisted.getKoulutusmoduuli().getOid());
                    newKomotoIds.add(persisted.getId());
                    /*
                     *  add hakukohde oids to the new presisted komoto
                     */
                    //TODO: in future

                    /*
                     *  add child links to the new  komo
                     */
                    result.getResult().getTo().add(new KoulutusCopyStatusV1RDTO(persisted.getOid(), orgOid));

                    linkingV1Resource.link(new KomoLink(persisted.getKoulutusmoduuli().getOid(), children.toArray(new String[children.size()])));
                }

                /*
                 * Next add parent links to the new child komos
                 */
                for (String komoParentOid : koulutusSisaltyvyysDAO.getParents(komotoOid)) {
                    linkingV1Resource.link(new KomoLink(komoParentOid, newKomoChildOids.toArray(new String[newKomoChildOids.size()])));
                }

                indexerResource.indexKoulutukset(newKomotoIds);
                break;
            case MOVE:
                final String orgOid = koulutusCopy.getOrganisationOids().get(0);
                //currently no need to change organisation oid in komo
                //Koulutusmoduuli koulutusmoduuli = komoto.getKoulutusmoduuli();
                //koulutusmoduuli.setOmistajaOrganisaatioOid(orgOid);
                komoto.setTarjoaja(orgOid);
                koulutusmoduuliToteutusDAO.update(komoto);

                result.getResult().getTo().add(new KoulutusCopyStatusV1RDTO(komoto.getOid(), orgOid));
                indexerResource.indexKoulutukset(Lists.newArrayList(komoto.getId()));
                final List<Hakukohde> hakukohdes = hakukohdeDAO.findByKoulutusOid(komoto.getOid());

                //update all hakukohdes
                indexerResource.indexHakukohteet(Lists.newArrayList(Iterators.transform(hakukohdes.iterator(), new Function<Hakukohde, Long>() {
                    @Override
                    public Long apply(@Nullable Hakukohde arg0) {
                        return arg0.getId();
                    }
                })));
                break;
            case TEST_COPY:
                break;
            case TEST_MOVE:
                break;
            default:
                break;
        }

        return result;
    }

    @Override
    public ResultV1RDTO copyOrMoveMultiple(KoulutusMultiCopyV1RDTO koulutusMultiCopy) {
        ResultV1RDTO result = new ResultV1RDTO();
        result.setErrors(Lists.<ErrorV1RDTO>newArrayList());
        for (String komotoOid : koulutusMultiCopy.getKomotoOids()) {
            ResultV1RDTO copyOrMove = copyOrMove(komotoOid, koulutusMultiCopy);
            if (copyOrMove.hasErrors()) {
                result.getErrors().add(copyOrMove);
            }
        }

        return result;
    }

    private static ToteutustyyppiEnum getType(KoulutusmoduuliToteutus komoto) {
        if (komoto.getToteutustyyppi() != null) {
            return komoto.getToteutustyyppi();
        } else {
            LOG.debug("Trying to resolve type of koulutus entity, OID : {}", komoto.getOid());
            final List<String> uris = EntityUtils.splitStringToList(komoto.getKoulutusmoduuli().getKoulutustyyppiUri());
            if (uris.size() == 1 && !uris.isEmpty()) {
                return ToteutustyyppiEnum.fromString(uris.get(0));
            } else {
                throw new RuntimeException("Cannot convert 'unknown' entity to DTO - found KOMO koulutustyyppi : '" + komoto.getKoulutusmoduuli().getKoulutustyyppiUri() + "' for komoto OID :" + komoto.getOid());
            }
        }
    }

    private KoulutusV1RDTO koulutusDtoForCopy(Class clazz, KoulutusmoduuliToteutus komoto, String orgOid) {
        //convert entity to dto
        KoulutusV1RDTO copy = converterToRDTO.convert(clazz, komoto, RestParam.showImageAndNoMeta());
        //keep the komo oid, we do need it to search correct komo for the komoto
        copy.setOid(null);
        copy.setKomotoOid(null);
        copy.setTila(TarjontaTila.LUONNOS);
        copy.setOrganisaatio(new OrganisaatioV1RDTO(orgOid, null, null));

        if (clazz.equals(KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO.class)) {
            KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO nayttoDto = (KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO) copy;

            KoulutusValmistavaV1RDTO valmistava = (KoulutusValmistavaV1RDTO) converterToRDTO.convert(KoulutusValmistavaV1RDTO.class, komoto.getNayttotutkintoValmistavaKoulutus(), RestParam.showImageAndNoMeta());
            if (valmistava != null) {
                valmistava.setOid(null);
                valmistava.setTila(TarjontaTila.LUONNOS);
                valmistava.setOrganisaatio(new OrganisaatioV1RDTO(orgOid, null, null));
                nayttoDto.setValmistavaKoulutus(valmistava);
            }
        }

        return copy;
    }

    /**
     * One of the special cases - convert two komoto entityes into dto and join
     * them together.
     *
     * @param komoto
     * @param restParam
     * @return
     */
    private KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO convertAmmValmentavaToDTO(final KoulutusmoduuliToteutus komoto, final RestParam restParam) {

        KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO convert = (KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO) converterToRDTO.convert(
                KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO.class,
                komoto,
                restParam
        );

        //null == no optional inserted vamentava koulutus 
        if (komoto.getNayttotutkintoValmistavaKoulutus() != null) {
            //convert joined optional komoto to the base komoto .
            convert.setValmistavaKoulutus((KoulutusValmistavaV1RDTO) converterToRDTO.convert(
                    KoulutusValmistavaV1RDTO.class,
                    komoto.getNayttotutkintoValmistavaKoulutus(),
                    restParam
            ));
        }

        return convert;
    }

    private void insertValmistavaKoulutusToNayttotutkinto(KoulutusmoduuliToteutus base, KoulutusmoduuliToteutus valmistava) {
        Preconditions.checkNotNull(base, "Nayttotutkinto cannot be null.");
        Preconditions.checkNotNull(valmistava, "Valmistava cannot be null.");

        //insert valmistava komoto to the base komoto
        //join valmistava komoto to the base komoto
        base.setNayttotutkintoValmistavaKoulutus(koulutusmoduuliToteutusDAO.insert(valmistava));
        koulutusmoduuliToteutusDAO.update(base);
    }

}
