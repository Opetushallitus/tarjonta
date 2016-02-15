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

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fi.vm.sade.auditlog.tarjonta.LogMessage;
import fi.vm.sade.auditlog.tarjonta.TarjontaOperation;
import fi.vm.sade.auditlog.tarjonta.TarjontaResource;
import fi.vm.sade.koodisto.service.GenericFault;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.dao.*;
import fi.vm.sade.tarjonta.koodisto.KoulutuskoodiRelations;
import fi.vm.sade.tarjonta.koodisto.OppilaitosKoodiRelations;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.publication.Tila;
import fi.vm.sade.tarjonta.publication.Tila.Tyyppi;
import fi.vm.sade.tarjonta.publication.model.RestParam;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.auth.NotAuthorizedException;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.service.business.exception.TarjontaBusinessException;
import fi.vm.sade.tarjonta.service.impl.aspects.KoulutusPermissionException;
import fi.vm.sade.tarjonta.service.impl.aspects.KoulutusPermissionService;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.EntityConverterToRDTO;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusCommonConverter;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusDTOConverterToEntity;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusKuvausV1RDTO;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidationMessages;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidator;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.KoulutusV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.LinkingV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.search.*;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.types.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

import static fi.vm.sade.tarjonta.service.AuditHelper.*;
import static fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidator.TOTEUTUSTYYPPI;
import static fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidator.validateMimeType;
import static fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO.createSystemError;
import static fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO.createValidationError;
import static fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO.ResultStatus.*;
import static fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO.create;

/**
 * @author mlyly
 */
@Transactional(readOnly = false)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class KoulutusResourceImplV1 implements KoulutusV1Resource {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusResourceImplV1.class);

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Autowired
    private OidService oidService;

    @Autowired
    private KoulutusSearchService koulutusSearchService;

    @Autowired
    private HakukohdeSearchService hakukohdeSearchService;

    @Autowired
    private IndexerResource indexerResource;

    @Autowired
    private KoulutuskoodiRelations koulutuskoodiRelations;

    @Autowired
    private KoodiService koodiService;

    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private KoulutusKuvausV1RDTO<KomoTeksti> komoKoulutusConverters;

    @Autowired
    private KoulutusKuvausV1RDTO<KomotoTeksti> komotoKoulutusConverters;

    @Autowired
    private ConverterV1 converterV1;

    @Autowired
    private KoulutusUtilService koulutusUtilService;

    @Autowired
    private PermissionChecker permissionChecker;

    @Autowired
    private ContextDataService contextDataService;

    @Autowired
    private EntityConverterToRDTO converterToRDTO;

    @Autowired
    private KoulutusDTOConverterToEntity convertToEntity;

    @Autowired
    private KoulutusSisaltyvyysDAO koulutusSisaltyvyysDAO;

    @Autowired
    private LinkingV1Resource linkingV1Resource;

    @Autowired
    private HakukohdeDAO hakukohdeDAO;

    @Autowired
    private OppilaitosKoodiRelations oppilaitosKoodiRelations;

    @Autowired
    private PublicationDataService publicationDataService;

    @Autowired
    private OppiaineDAO oppiaineDAO;

    @Autowired
    private KoulutusPermissionService koulutusPermissionService;

    @Value("${host.virkailija}")
    private String HOST_VIRKAILIJA;

    @Autowired
    private KoulutusValidator koulutusValidator;

    @Autowired
    private KoulutusImplicitDataPopulator koulutusImplicitDataPopulator;

    @Override
    public ResultV1RDTO<KoulutusV1RDTO> findByOid(String komotoOid, Boolean showMeta, Boolean showImg, String userLang) {
        Preconditions.checkNotNull(komotoOid, "KOMOTO OID cannot be null.");

        ResultV1RDTO<KoulutusV1RDTO> result = new ResultV1RDTO<KoulutusV1RDTO>();
        final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(komotoOid);
        if (komoto == null) {
            result.setStatus(NOT_FOUND);
            return result;
        }

        // load lazy
        komoto.getKoulutusRyhmaOids().size();
        final RestParam restParam = RestParam.byUserRequest(showMeta, showImg, userLang);

        //convert required komoto to dto rest format.
        switch (getType(komoto)) {
            case KORKEAKOULUTUS:
                result.setResult(converterToRDTO.convert(KoulutusKorkeakouluV1RDTO.class, komoto, restParam));
                break;
            case KORKEAKOULUOPINTO:
                result.setResult(converterToRDTO.convert(KorkeakouluOpintoV1RDTO.class, komoto, restParam));
                break;
            case LUKIOKOULUTUS:
                result.setResult(converterToRDTO.convert(KoulutusLukioV1RDTO.class, komoto, restParam));
                break;
            case EB_RP_ISH:
                result.setResult(converterToRDTO.convert(KoulutusEbRpIshV1RDTO.class, komoto, restParam));
                break;
            case LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA:
                result.setResult(converterToRDTO.convert(KoulutusLukioAikuistenOppimaaraV1RDTO.class, komoto, restParam));
                break;
            case AMMATILLINEN_PERUSTUTKINTO:
                result.setResult(converterToRDTO.convert(KoulutusAmmatillinenPerustutkintoV1RDTO.class, komoto, restParam));
                break;
            case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS:
                result.setResult(converterToRDTO.convert(KoulutusValmentavaJaKuntouttavaV1RDTO.class, komoto, restParam));
                break;
            case PERUSOPETUKSEN_LISAOPETUS:
                result.setResult(converterToRDTO.convert(KoulutusPerusopetuksenLisaopetusV1RDTO.class, komoto, restParam));
                break;
            case AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA:
                result.setResult(converterToRDTO.convert(KoulutusAmmatilliseenPeruskoulutukseenValmentavaV1RDTO.class, komoto, restParam));
                break;
            case AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER:
                result.setResult(converterToRDTO.convert(KoulutusAmmatilliseenPeruskoulutukseenValmentavaERV1RDTO.class, komoto, restParam));
                break;
            case AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS:
                result.setResult(converterToRDTO.convert(KoulutusAmmatilliseenPeruskoulutukseenOhjaavaJaValmistavaV1RDTO.class, komoto, restParam));
                break;
            case MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS:
                result.setResult(converterToRDTO.convert(KoulutusMaahanmuuttajienAmmatilliseenPeruskoulutukseenValmistavaV1RDTO.class, komoto, restParam));
                break;
            case MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS:
                result.setResult(converterToRDTO.convert(KoulutusMaahanmuuttajienJaVieraskielistenLukiokoulutukseenValmistavaV1RDTO.class, komoto, restParam));
                break;
            case VAPAAN_SIVISTYSTYON_KOULUTUS:
                result.setResult(converterToRDTO.convert(KoulutusVapaanSivistystyonV1RDTO.class, komoto, restParam));
                break;
            case AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA:
                result.setResult(converterToRDTO.convert(KoulutusAmmatillinenPeruskoulutusErityisopetuksenaV1RDTO.class, komoto, restParam));
                break;
            case AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA:
                //very special case: may have a double komoto structure
                result.setResult(converterToRDTO.convert(
                        KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO.class,
                        komoto,
                        restParam));
                break;
            case AMMATTITUTKINTO:
                result.setResult(converterToRDTO.convert(
                        AmmattitutkintoV1RDTO.class,
                        komoto,
                        restParam));
                break;
            case ERIKOISAMMATTITUTKINTO:
                result.setResult(converterToRDTO.convert(
                        ErikoisammattitutkintoV1RDTO.class,
                        komoto,
                        restParam));
                break;
            case AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA_VALMISTAVA:
                result.setResult(converterToRDTO.convert(KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO.class, komoto, restParam));
                break;
            case AIKUISTEN_PERUSOPETUS:
                result.setResult(converterToRDTO.convert(
                        KoulutusAikuistenPerusopetusV1RDTO.class,
                        komoto,
                        restParam));
                break;
        }

        return result;
    }

    @Override
    public ResultV1RDTO<KoulutusV1RDTO> postKoulutus(KoulutusV1RDTO dto) {
        ResultV1RDTO<KoulutusV1RDTO> result = new ResultV1RDTO<KoulutusV1RDTO>();

        if (dto == null || dto.getToteutustyyppi() == null) {
            return create(VALIDATION, null, createValidationError(TOTEUTUSTYYPPI, TOTEUTUSTYYPPI + " missing"));
        }

        if (! validateOrganisation(dto.getOrganisaatio(),
                result, KoulutusValidationMessages.KOULUTUS_TARJOAJA_MISSING,
                KoulutusValidationMessages.KOULUTUS_TARJOAJA_INVALID)) {
            return result;
        }

        permissionChecker.checkUpsertKoulutus(dto);

        if (dto.getOid() != null) {
            final Tila tilamuutos = new Tila(Tyyppi.KOMOTO, dto.getTila(), dto.getOid());
            if (!publicationDataService.isValidStatusChange(tilamuutos)) {
                return create(ERROR, null, createValidationError("tile", "koulutus.error.tilasiirtyma"));
            }
        }

        if (!validAjankohta(dto)) {
            return create(ERROR, null,
                    createValidationError("alkamispvm", "koulutus.error.alkamispvm.ajankohtaerikuinhaulla"));
        }

        dto = koulutusImplicitDataPopulator.populateFields(dto);


        /** Permission service checks currently fail in 5% of the cases due to data model issues, which is why this is
         *  not used in production.
         */
        if (Lists.newArrayList("itest-virkailija.oph.ware.fi", "testi.virkailija.opintopolku.fi").contains(HOST_VIRKAILIJA)) {
            try {
                koulutusPermissionService.checkThatOrganizationIsAllowedToOrganizeEducation(dto);
            } catch (KoulutusPermissionException e) {
                return create(
                        ERROR,
                        null,
                        createValidationError(
                                "koulutusPermission", "koulutusNotAllowedForOrganization." + e.getKoodisto()
                        )
                );
            }
        }

        if (dto.getClass() == KoulutusKorkeakouluV1RDTO.class) {
            //TODO: currently no komo validation, when invalid throws exception
            return postKorkeakouluKoulutus((KoulutusKorkeakouluV1RDTO) dto, result);
        } else if (dto instanceof TutkintoonJohtamatonKoulutusV1RDTO) {
            return postTutkintoonjohtamatonKoulutus((TutkintoonJohtamatonKoulutusV1RDTO) dto, result);
        } else {
            //Cannot be created without module. null komo => validation error
            Koulutusmoduuli komo = null;
            if (dto.getKomoOid() != null) {
                komo = this.koulutusmoduuliDAO.findByOid(dto.getKomoOid());
            }

            // Kun luodaan uutta
            if (dto.getOid() == null) {
                Koulutusmoduuli virtualKomo = createOsaamisalaKomoIfNeeded(dto, komo);
                if (virtualKomo != null) {
                    komo = virtualKomo;
                    dto.setKomoOid(komo.getOid());
                }
            }

            if (dto.getClass() == KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO.class) {
                return postNayttotutkintona(dto.getClass(), (KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO) dto, komo, result);
            } else if (dto.getClass() == AmmattitutkintoV1RDTO.class) {
                return postNayttotutkintona(dto.getClass(), (AmmattitutkintoV1RDTO) dto, komo, result);
            } else if (dto.getClass() == ErikoisammattitutkintoV1RDTO.class) {
                return postNayttotutkintona(dto.getClass(), (ErikoisammattitutkintoV1RDTO) dto, komo, result);
            } else if (dto instanceof KoulutusGenericV1RDTO) {
                return postGenericKoulutus((KoulutusGenericV1RDTO) dto, komo, result);
            }
        }

        result.setStatus(ERROR);
        result.addError(createSystemError(new IllegalArgumentException(), "type_unknown", dto.getClass() + " not handled"));

        return result;
    }

    private boolean validAjankohta(KoulutusV1RDTO dto) {
        if (dto.getOid() == null) {
            return true;
        }

        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(dto.getOid());
        if (komoto.getHakukohdes().isEmpty()) {
            return true;
        }

        String targetKausi = null;
        Integer targetVuosi = null;
        for (Hakukohde hakukohde : komoto.getHakukohdes()) {
            if (!hakukohde.getTila().equals(TarjontaTila.POISTETTU)) {
                Haku haku = hakukohde.getHaku();
                if (!haku.isJatkuva() &&
                        (haku.getKoulutuksenAlkamiskausiUri() != null && haku.getKoulutuksenAlkamisVuosi() != null)) {
                    targetKausi = haku.getKoulutuksenAlkamiskausiUri();
                    targetVuosi = haku.getKoulutuksenAlkamisVuosi();
                    break;
                }
            }
        }

        // Jatkuvalla haulla ei ole kautta eikä vuotta, joten näiden koulutusten osalta ei alkamispäivämäärää
        // tarvitse validoida
        if (targetKausi == null && targetVuosi == null) {
            return true;
        }

        if (dto.getKoulutuksenAlkamisPvms().size() > 0) {
            return validAlkamisPvms(dto, targetKausi, targetVuosi);
        } else {
            return validKausiVuosi(dto, targetKausi, targetVuosi);
        }
    }

    private boolean validKausiVuosi(KoulutusV1RDTO dto, String targetKausi, Integer targetVuosi) {
        KoodiV1RDTO kausi = dto.getKoulutuksenAlkamiskausi();
        Integer vuosi = dto.getKoulutuksenAlkamisvuosi();
        return StringUtils.contains(targetKausi, kausi.getUri()) && targetVuosi.equals(vuosi);
    }

    private boolean validAlkamisPvms(KoulutusV1RDTO dto, String targetKausi, Integer targetVuosi) {
        for (Date alkamisPvm : dto.getKoulutuksenAlkamisPvms()) {
            String kausi = IndexDataUtils.parseKausiKoodi(alkamisPvm);
            Integer vuosi = IndexDataUtils.parseYearInt(alkamisPvm);
            if (!targetKausi.equals(kausi) || !targetVuosi.equals(vuosi)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Insert and update koulutus object to database. When komoto OID is set,
     * then the post method will be handled as koulutus data update.
     *
     * @param dto
     * @return
     */
    private ResultV1RDTO<KoulutusV1RDTO> postKorkeakouluKoulutus(KoulutusKorkeakouluV1RDTO dto, ResultV1RDTO<KoulutusV1RDTO> result) {
        KoulutusmoduuliToteutus fullKomotoWithKomo = null;

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

        if (!result.hasErrors()) {

            final RestParam param = RestParam.showImageAndShowMeta(contextDataService.getCurrentUserLang());
            KoulutusV1RDTO dtoAfterOperation;

            if (dto.getOid() != null && dto.getOid().length() > 0) {
                //update korkeakoulu koulutus
                final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(dto.getOid());
                KoulutusValidator.validateKoulutusUpdate(komoto, result);
                if (result.hasErrors()) {
                    return result;
                }

                final KoulutusV1RDTO originalDto = converterToRDTO.convert(dto.getClass(), komoto, param);

                fullKomotoWithKomo = updateKoulutusKorkeakoulu(komoto, dto);

                dtoAfterOperation = converterToRDTO.convert(dto.getClass(), fullKomotoWithKomo, param);

                AUDIT.log(builder()
                        .setOperation(TarjontaOperation.UPDATE)
                        .setResource(TarjontaResource.KOULUTUS)
                        .setResourceOid(fullKomotoWithKomo.getOid())
                        .setDelta(getKomotoDelta(dtoAfterOperation, originalDto))
                        .build());
            } else {
                //create korkeakoulu koulutus
                fullKomotoWithKomo = insertKoulutusKorkeakoulu(dto);

                dtoAfterOperation = converterToRDTO.convert(dto.getClass(), fullKomotoWithKomo, param);

                AUDIT.log(builder()
                        .setOperation(TarjontaOperation.CREATE)
                        .setResource(TarjontaResource.KOULUTUS)
                        .setResourceOid(fullKomotoWithKomo.getOid())
                        .setDelta(getKomotoDelta(dtoAfterOperation, null))
                        .build());
            }

            indexKomoto(fullKomotoWithKomo);

            result.setResult(dtoAfterOperation);
        } else {
            result.setStatus(VALIDATION);
            result.setResult(dto);
        }

        return result;
    }

    /**
     * Insert and update koulutus object to database. When komoto OID is set,
     * then the post method will be handled as koulutus data update.
     *
     * @param dto
     * @return
     */
    private ResultV1RDTO<KoulutusV1RDTO> postTutkintoonjohtamatonKoulutus(TutkintoonJohtamatonKoulutusV1RDTO dto, ResultV1RDTO<KoulutusV1RDTO> result) {
        KoulutusmoduuliToteutus fullKomotoWithKomo = null;

        koulutusValidator.validateTutkintoonjohtamaton(dto, result);

        if (result.hasErrors()) {
            result.setStatus(VALIDATION);
            return result;
        }

        if (!result.hasErrors()) {

            final RestParam param = RestParam.showImageAndShowMeta(contextDataService.getCurrentUserLang());
            KoulutusV1RDTO dtoAfterOperation;

            if (dto.getOid() != null && dto.getOid().length() > 0) {
                //update tutkintoonjohtamaton koulutus
                final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(dto.getOid());


                KoulutusValidator.validateKoulutusUpdate(komoto, result);
                if (result.hasErrors()) {
                    return result;
                }

                final KoulutusV1RDTO originalDto = converterToRDTO.convert(dto.getClass(), komoto, param);

                fullKomotoWithKomo = updateTutkintoonjohtamaton(komoto, dto);

                dtoAfterOperation = converterToRDTO.convert(dto.getClass(), fullKomotoWithKomo, param);

                AUDIT.log(builder()
                        .setOperation(TarjontaOperation.UPDATE)
                        .setResource(TarjontaResource.KOULUTUS)
                        .setResourceOid(fullKomotoWithKomo.getOid())
                        .setDelta(getKomotoDelta(dtoAfterOperation, originalDto))
                        .build());
            } else {
                //create tutkintoonjohtamaton koulutus
                fullKomotoWithKomo = insertTutkintoonjohtamaton(dto, false);

                dtoAfterOperation = converterToRDTO.convert(dto.getClass(), fullKomotoWithKomo, param);

                AUDIT.log(builder()
                        .setOperation(TarjontaOperation.CREATE)
                        .setResource(TarjontaResource.KOULUTUS)
                        .setResourceOid(fullKomotoWithKomo.getOid())
                        .setDelta(getKomotoDelta(dtoAfterOperation, null))
                        .build());
            }

            indexKomoto(fullKomotoWithKomo);

            result.setResult(dtoAfterOperation);
        } else {
            result.setStatus(VALIDATION);
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
            result.addError(createValidationError(kvmMissing.getFieldName(), kvmMissing.lower()));
        } else {
            try {
                final OrganisaatioDTO org = organisaatioService.findByOid(dto.getOid());
                if (org == null || org.getOid() == null || org.getOid().isEmpty()) {
                    result.addError(createValidationError(kvmInvalid.getFieldName(), kvmInvalid.lower()));
                } else {
                    return true;
                }
            } catch (Exception e) {
                LOG.error("Organisation service call failed", e);
                result.addError(createValidationError(kvmInvalid.getFieldName(), kvmInvalid.lower()));
            }
        }

        return false;
    }

    private ResultV1RDTO<KoulutusV1RDTO> postGenericKoulutus(KoulutusGenericV1RDTO dto, Koulutusmoduuli komo, ResultV1RDTO<KoulutusV1RDTO> result) {
        KoulutusmoduuliToteutus fullKomotoWithKomo = null;

        KoulutusValidator.validateKoulutusGeneric(dto, komo, result);

        switch (dto.getToteutustyyppi()) {
            case AMMATILLINEN_PERUSTUTKINTO:
            case AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA:
                if (existsDuplicateKoulutus(dto)) {
                    result.addError(createValidationError("", "koulutus.koulutusOnJoOlemassa"));
                }
                break;
        }

        if (!result.hasErrors()) {

            final RestParam param = RestParam.noImageAndShowMeta(contextDataService.getCurrentUserLang());
            KoulutusV1RDTO dtoAfterOperation;

            if (dto.getOid() != null && dto.getOid().length() > 0) {
                //update korkeakoulu koulutus
                final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(dto.getOid());
                KoulutusValidator.validateKoulutusUpdate(komoto, result);
                if (result.hasErrors()) {
                    return result;
                }

                final KoulutusV1RDTO originalDto = converterToRDTO.convert(dto.getClass(), komoto, param);

                fullKomotoWithKomo = updateGeneric(komoto, dto);

                dtoAfterOperation = converterToRDTO.convert(dto.getClass(), fullKomotoWithKomo, param);

                AUDIT.log(builder()
                        .setOperation(TarjontaOperation.UPDATE)
                        .setResource(TarjontaResource.KOULUTUS)
                        .setResourceOid(fullKomotoWithKomo.getOid())
                        .setDelta(getKomotoDelta(dtoAfterOperation, originalDto))
                        .build());
            } else {
                //create korkeakoulu koulutus
                fullKomotoWithKomo = insertKoulutusGeneric(dto);

                dtoAfterOperation = converterToRDTO.convert(dto.getClass(), fullKomotoWithKomo, param);

                AUDIT.log(builder()
                        .setOperation(TarjontaOperation.CREATE)
                        .setResource(TarjontaResource.KOULUTUS)
                        .setResourceOid(fullKomotoWithKomo.getOid())
                        .setDelta(getKomotoDelta(dtoAfterOperation, null))
                        .build());
            }

            indexKomoto(fullKomotoWithKomo);

            result.setResult(dtoAfterOperation);
        } else {
            result.setStatus(VALIDATION);
            result.setResult(dto);
        }

        return result;
    }

    private ResultV1RDTO<KoulutusV1RDTO> postNayttotutkintona(Class clazz, NayttotutkintoV1RDTO dto, final Koulutusmoduuli komo, ResultV1RDTO<KoulutusV1RDTO> result) {
        KoulutusmoduuliToteutus fullKomotoWithKomo = null;

        KoulutusValidator.validateKoulutusNayttotutkinto(dto, komo, result);

        if (!result.hasErrors()
                && validateOrganisation(dto.getJarjestavaOrganisaatio(),
                result, KoulutusValidationMessages.KOULUTUS_JARJESTAJA_MISSING,
                KoulutusValidationMessages.KOULUTUS_JARJESTAJA_INVALID)) {

            final RestParam param = RestParam.noImageAndShowMeta(contextDataService.getCurrentUserLang());
            KoulutusV1RDTO dtoAfterOperation;

            if (dto.getOid() != null && dto.getOid().length() > 0) {
                final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(dto.getOid());
                KoulutusValidator.validateKoulutusUpdate(komoto, result);
                if (result.hasErrors()) {
                    return result;
                }

                final KoulutusV1RDTO originalDto = converterToRDTO.convert(clazz, komoto, param);

                fullKomotoWithKomo = updateKoulutusNayttotutkintona(komoto, dto);

                dtoAfterOperation = converterToRDTO.convert(clazz, fullKomotoWithKomo, param);

                AUDIT.log(builder()
                        .setOperation(TarjontaOperation.UPDATE)
                        .setResource(TarjontaResource.KOULUTUS)
                        .setResourceOid(fullKomotoWithKomo.getOid())
                        .setDelta(getKomotoDelta(dtoAfterOperation, originalDto))
                        .build());
            } else {
                //create korkeakoulu koulutus
                fullKomotoWithKomo = insertKoulutusNayttotutkintona(dto);

                dtoAfterOperation = converterToRDTO.convert(clazz, fullKomotoWithKomo, param);

                AUDIT.log(builder()
                        .setOperation(TarjontaOperation.CREATE)
                        .setResource(TarjontaResource.KOULUTUS)
                        .setResourceOid(fullKomotoWithKomo.getOid())
                        .setDelta(getKomotoDelta(dtoAfterOperation, null))
                        .build());
            }
            Preconditions.checkNotNull(fullKomotoWithKomo, "KOMOTO object cannot be null!");
            Preconditions.checkNotNull(fullKomotoWithKomo.getId(), "KOMOTO ID cannot be null!");
            indexKomoto(fullKomotoWithKomo);
            if (fullKomotoWithKomo.getValmistavaKoulutus() != null) {
                indexKomoto(fullKomotoWithKomo.getValmistavaKoulutus());
            }

            result.setResult(dtoAfterOperation);
        } else {
            result.setStatus(VALIDATION);
            result.setResult(dto);
        }

        return result;
    }

    private KoulutusmoduuliToteutus insertTutkintoonjohtamaton(final TutkintoonJohtamatonKoulutusV1RDTO dto, final boolean addKomotoToKomo) {
        Preconditions.checkNotNull(dto.getKomotoOid() != null, "External KOMOTO OID not allowed. OID : %s.", dto.getKomotoOid());
        Preconditions.checkNotNull(dto.getKomoOid() != null, "External KOMO OID not allowed. OID : %s.", dto.getKomoOid());

        KoulutusmoduuliToteutus newKomoto = convertToEntity.convert(dto, contextDataService.getCurrentUserOid(), null, null);
        Preconditions.checkNotNull(newKomoto, "KOMOTO conversion to database object failed : object : %s.", ReflectionToStringBuilder.toString(dto));
        Preconditions.checkNotNull(newKomoto.getKoulutusmoduuli(), "KOMO conversion to database object failed : object :  %s.", ReflectionToStringBuilder.toString(dto));

        koulutusmoduuliDAO.insert(newKomoto.getKoulutusmoduuli());
        newKomoto = koulutusmoduuliToteutusDAO.insert(newKomoto);
        convertToEntity.setSisaltyyKoulutuksiin(newKomoto, dto);
        return newKomoto;
    }

    private KoulutusmoduuliToteutus updateTutkintoonjohtamaton(KoulutusmoduuliToteutus komoto, final TutkintoonJohtamatonKoulutusV1RDTO dto) {
        convertToEntity.setSisaltyyKoulutuksiin(komoto, dto);
        return convertToEntity.convert(dto, contextDataService.getCurrentUserOid(), null, null);
    }

    private KoulutusmoduuliToteutus insertKoulutusKorkeakoulu(final KoulutusKorkeakouluV1RDTO dto) {
        Preconditions.checkNotNull(dto.getKomotoOid() != null, "External KOMOTO OID not allowed. OID : %s.", dto.getKomotoOid());
        Preconditions.checkNotNull(dto.getKomoOid() != null, "External KOMO OID not allowed. OID : %s.", dto.getKomoOid());

        final KoulutusmoduuliToteutus newKomoto = convertToEntity.convert(dto, contextDataService.getCurrentUserOid());
        Preconditions.checkNotNull(newKomoto, "KOMOTO conversion to database object failed : object : %s.", ReflectionToStringBuilder.toString(dto));
        Preconditions.checkNotNull(newKomoto.getKoulutusmoduuli(), "KOMO conversion to database object failed : object :  %s.", ReflectionToStringBuilder.toString(dto));

        permissionChecker.checkCreateKoulutus(dto.getOrganisaatio().getOid());
        koulutusmoduuliDAO.insert(newKomoto.getKoulutusmoduuli());
        return koulutusmoduuliToteutusDAO.insert(newKomoto);
    }

    private KoulutusmoduuliToteutus insertKoulutusGeneric(final KoulutusGenericV1RDTO dto) {
        Preconditions.checkNotNull(dto.getKomotoOid() != null, "External KOMOTO OID not allowed. OID : %s.", dto.getKomotoOid());
        Preconditions.checkNotNull(dto.getKomoOid() != null, "External KOMO OID not allowed. OID : %s.", dto.getKomoOid());

        final KoulutusmoduuliToteutus newKomoto = convertToEntity.convert(dto, contextDataService.getCurrentUserOid());
        Preconditions.checkNotNull(newKomoto, "KOMOTO conversion to database object failed : object : %s.", ReflectionToStringBuilder.toString(dto));
        Preconditions.checkNotNull(newKomoto.getKoulutusmoduuli(), "KOMO conversion to database object failed : object :  %s.", ReflectionToStringBuilder.toString(dto));

        permissionChecker.checkCreateKoulutus(dto.getOrganisaatio().getOid());
        return koulutusmoduuliToteutusDAO.insert(newKomoto);
    }

    private KoulutusmoduuliToteutus insertKoulutusNayttotutkintona(final NayttotutkintoV1RDTO dto) {
        Preconditions.checkNotNull(dto.getKomotoOid() != null, "External KOMOTO OID not allowed. OID : %s.", dto.getKomotoOid());
        Preconditions.checkNotNull(dto.getKomoOid() != null, "External KOMO OID not allowed. OID : %s.", dto.getKomoOid());
        permissionChecker.checkCreateKoulutus(dto.getOrganisaatio().getOid());

        final KoulutusmoduuliToteutus newKomoto = convertToEntity.convert(dto, contextDataService.getCurrentUserOid());
        Preconditions.checkNotNull(newKomoto, "KOMOTO conversion to database object failed : object : %s.", ReflectionToStringBuilder.toString(dto));
        Preconditions.checkNotNull(newKomoto.getKoulutusmoduuli(), "KOMO conversion to database object failed : object :  %s.", ReflectionToStringBuilder.toString(dto));

        return koulutusmoduuliToteutusDAO.insert(newKomoto);
    }

    /**
     * Tutke2 muutosten myötä tarjontaan on mahdollista tallentaa amm. pt. koulutuksia suoraan
     * tutkinto-tason komoihin (aiemmin koulutusten komot olivat tutkinto-ohjelma tasoisia).
     * Jotta koulutusinformaatio voisi jatkossakin indeksoida koulutuksia vanhan mallin mukaisesti,
     * pitää tarjonnan luoda "virtuaalinen" komo...
     * @param dto
     */
    private Koulutusmoduuli createOsaamisalaKomoIfNeeded(final KoulutusV1RDTO dto, Koulutusmoduuli komo) {
        switch (dto.getToteutustyyppi()) {
            case AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA:
            case AMMATILLINEN_PERUSTUTKINTO:
                if (komo.getModuuliTyyppi().equals(fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi.TUTKINTO)) {

                    // Tarkista, jos virtuaalinen komo on jo luotu
                    for(Koulutusmoduuli childKomo : komo.getAlamoduuliList()) {
                        if (childKomo.isPseudo()) {
                            return childKomo;
                        }
                    }

                    // Luo virtuaalinen komo
                    Koulutusmoduuli virtualKomo = new Koulutusmoduuli();
                    try {
                        virtualKomo.setOid(oidService.get(TarjontaOidType.KOMO));
                    } catch (OIDCreationException ex) {
                        LOG.error("OIDService failed!", ex);
                    }
                    virtualKomo.setKoulutusUri(komo.getKoulutusUri());
                    virtualKomo.setTila(TarjontaTila.JULKAISTU);
                    virtualKomo.setModuuliTyyppi(fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
                    virtualKomo.setKoulutustyyppiEnum(ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS);
                    virtualKomo.setPseudo(true);

                    koulutusmoduuliDAO.insert(virtualKomo);

                    // Luo linkitys tutkinto komosta -> (virtuaaliseen) tutkinto-ohjelma komoon
                    KoulutusSisaltyvyys sisaltyvyys = new KoulutusSisaltyvyys(
                            komo, virtualKomo, KoulutusSisaltyvyys.ValintaTyyppi.SOME_OFF
                    );
                    koulutusSisaltyvyysDAO.insert(sisaltyvyys);

                    return virtualKomo;
                }
                break;
        }
        return null;
    }

    private KoulutusmoduuliToteutus updateKoulutusKorkeakoulu(KoulutusmoduuliToteutus komoto, final KoulutusKorkeakouluV1RDTO dto) {
        // KJOH-778 monta tarjoajaa
        Boolean validUser = false;
        if (komoto.getOwners() != null && komoto.getOwners().size() > 0) {
            for (KoulutusOwner owner : komoto.getOwners()) {
                if (owner.getOwnerType().equals(KoulutusOwner.TARJOAJA)) {
                    try {
                        permissionChecker.checkUpdateKoulutusByTarjoajaOid(owner.getOwnerOid());
                        validUser = true;
                        break;
                    } catch (NotAuthorizedException e) {
                        // Do nothing
                    }
                }
            }
        }
        if (!validUser) {
            permissionChecker.checkUpdateKoulutusByTarjoajaOid(komoto.getTarjoaja());
        }
        return convertToEntity.convert(dto, contextDataService.getCurrentUserOid());
    }

    private KoulutusmoduuliToteutus updateGeneric(KoulutusmoduuliToteutus komoto, final KoulutusGenericV1RDTO dto) {
        permissionChecker.checkUpdateKoulutusByTarjoajaOid(komoto.getTarjoaja());
        return convertToEntity.convert(dto, contextDataService.getCurrentUserOid());
    }

    private KoulutusmoduuliToteutus updateKoulutusNayttotutkintona(KoulutusmoduuliToteutus komoto, final NayttotutkintoV1RDTO dto) {
        permissionChecker.checkUpdateKoulutusByTarjoajaOid(komoto.getTarjoaja());
        return convertToEntity.convert(dto, contextDataService.getCurrentUserOid());
    }

    @Override
    public ResultV1RDTO<KoulutusV1RDTO> deleteByOid(final String komotoOid) {

        ResultV1RDTO<KoulutusV1RDTO> result = new ResultV1RDTO<KoulutusV1RDTO>();
        final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findByOid(komotoOid);
        KoulutusValidator.validateKoulutusUpdate(komoto, result);

        if (result.getStatus().equals(OK)) {
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

                        AUDIT.log(builder()
                                .setOperation(TarjontaOperation.DELETE)
                                .setResource(TarjontaResource.KOULUTUS)
                                .setResourceOid(komotoOid).build());
                    }
                    break;
                case AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA:
                    KoulutusValidator.validateKoulutusDelete(komoto, Lists.<KoulutusmoduuliToteutus>newArrayList(), Lists.<String>newArrayList(), Lists.<String>newArrayList(), hkKoulutusMap, result);
                    if (!result.hasErrors()) {
                        if (komoto.getValmistavaKoulutus() != null && komoto.getValmistavaKoulutus().getOid() != null) {
                            koulutusmoduuliToteutusDAO.safeDelete(komoto.getValmistavaKoulutus().getOid(), userOid);
                        }
                        koulutusmoduuliToteutusDAO.safeDelete(komotoOid, userOid);

                        AUDIT.log(builder()
                                .setOperation(TarjontaOperation.DELETE)
                                .setResource(TarjontaResource.KOULUTUS)
                                .setResourceOid(komotoOid).build());
                    }
                    break;
                default:
                    //normal safe delete for komoto, do not touch komo
                    KoulutusValidator.validateKoulutusDelete(komoto, Lists.<KoulutusmoduuliToteutus>newArrayList(), Lists.<String>newArrayList(), Lists.<String>newArrayList(), hkKoulutusMap, result);
                    if (!result.hasErrors()) {
                        koulutusmoduuliToteutusDAO.safeDelete(komotoOid, userOid);

                        AUDIT.log(builder()
                                .setOperation(TarjontaOperation.DELETE)
                                .setResource(TarjontaResource.KOULUTUS)
                                .setResourceOid(komotoOid).build());
                    }
                    break;
            }
        }

        if (komoto != null) {
            indexKomoto(komoto);
        }

        return result;
    }

    @Override
    public ResultV1RDTO<List<NimiJaOidRDTO>> getHakukohteet(String oid) {
        HakukohteetKysely ks = new HakukohteetKysely();
        ks.getKoulutusOids().add(oid);

        HakukohteetVastaus vs = hakukohdeSearchService.haeHakukohteet(ks);
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
                    case EB_RP_ISH:
                        dto = KoulutusmoduuliLukioRelationV1RDTO.class.newInstance();
                        break;
                    default:
                        dto = KoulutusmoduuliAmmatillinenRelationV1RDTO.class.newInstance();
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
            result.setStatus(ERROR);
        } catch (IllegalAccessException ex) {
            LOG.error("Relation illegal access error.", ex);
            result.setStatus(ERROR);
        } catch (GenericFault ex) {
            LOG.error("Koodisto relation error.", ex);
            result.setStatus(ERROR);
        } catch (TarjontaBusinessException ex) {
            LOG.error("Koodisto relation error.", ex);
            result.setStatus(ERROR);
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

        if (Sets.newHashSet(TarjontaTila.JULKAISTU, TarjontaTila.PERUTTU).contains(tila)) {
            try {
                permissionChecker.checkPublishOrUnpublishKomoto(oid);
            }
            catch (NotAuthorizedException e) {
                ResultV1RDTO<Tilamuutokset> r = new ResultV1RDTO<Tilamuutokset>();
                r.addError(createValidationError(null, e.getMessage()));
                return r;
            }
        }

        Tila tilamuutos = new Tila(Tyyppi.KOMOTO, tila, oid);

        Tilamuutokset tm = null;
        try {
            tm = publicationDataService.updatePublicationStatus(Lists.newArrayList(tilamuutos));

            LogMessage.LogMessageBuilder builder = builder().setResource(TarjontaResource.KOULUTUS).setResourceOid(oid);

            switch (tila) {
                case PERUTTU:
                    builder.setOperation(TarjontaOperation.UNPUBLISH);
                    break;
                case JULKAISTU:
                    builder.setOperation(TarjontaOperation.PUBLISH);
                    break;
                default:
                    builder.setOperation(TarjontaOperation.CHANGE_STATE);
                    builder.add("newTila", tila.name());
                    break;
            }

            AUDIT.log(builder.build());

        } catch (IllegalArgumentException iae) {
            ResultV1RDTO<Tilamuutokset> r = new ResultV1RDTO<Tilamuutokset>();
            r.addError(createValidationError(null, iae.getMessage()));
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
            List<KoulutusmoduuliTyyppi> koulutusmoduuliTyyppi,
            @Deprecated List<KoulutusasteTyyppi> koulutusastetyyppi,
            String komoOid,
            String alkamisPvmAlkaenTs,
            String koulutuslaji,
            String defaultTarjoaja,
            String hakutapa,
            String hakutyyppi,
            String kohdejoukko,
            String oppilaitoistyyppi,
            String kunta,
            List<String> opetuskielet,
            List<String> jarjestajaOids,
            String hakukohderyhma,
            List<String> hakukohdeOids,
            List<String> koulutuskoodis,
            List<String> opintoalakoodis,
            List<String> koulutusalakoodis,
            List<String> hakuOids) {

        organisationOids = organisationOids != null ? organisationOids : new ArrayList<String>();
        jarjestajaOids = jarjestajaOids != null ? jarjestajaOids : new ArrayList<String>();

        KoulutuksetKysely q = new KoulutuksetKysely();

        q.setNimi(searchTerms);
        q.setkomoOid(komoOid);
        q.setKoulutuksenAlkamiskausi(alkamisKausi);
        q.setKoulutuksenAlkamisvuosi(alkamisVuosi);
        q.getTarjoajaOids().addAll(organisationOids);
        q.getJarjestajaOids().addAll(jarjestajaOids);
        q.getKoulutusOids().addAll(koulutusOids);
        if ("KAIKKI".equals(komotoTila)) {
            q.setShowAllKoulutukset(true);
            komotoTila = null;
        }
        q.setKoulutuksenTila(komotoTila == null ? null : fi.vm.sade.tarjonta.shared.types.TarjontaTila.valueOf(komotoTila).asDto());
        q.getKoulutusasteTyypit().addAll(koulutusastetyyppi);
        q.getKoulutustyyppi().addAll(koulutustyyppi);
        q.getTotetustyyppi().addAll(toteutustyyppi);
        q.getKoulutusmoduuliTyyppi().addAll(koulutusmoduuliTyyppi);
        q.setKoulutuslaji(koulutuslaji);
        q.setHakutapa(hakutapa);
        q.setHakutyyppi(hakutyyppi);
        q.setKohdejoukko(kohdejoukko);
        q.setOppilaitostyyppi(oppilaitoistyyppi);
        q.setKunta(kunta);
        q.opetuskielet(opetuskielet);
        q.setHakukohderyhma(hakukohderyhma);
        q.getHakukohdeOids().addAll(hakukohdeOids);
        q.setKoulutuskoodis(koulutuskoodis);
        q.setOpintoalakoodis(opintoalakoodis);
        q.setKoulutusalakoodis(koulutusalakoodis);
        q.setHakuOids(hakuOids);

        KoulutuksetVastaus r = koulutusSearchService.haeKoulutukset(q, defaultTarjoaja);

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
            result.setStatus(NOT_FOUND);
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
            result.setStatus(NOT_FOUND);
        }
        return result;
    }

    /**
     * Legacy HTML4 image upload for IE9.
     *
     * @param oid      komoto OID
     * @param kieliUri koodisto language uri, without version
     * @param body
     * @return
     */
    @Override
    public Response saveHtml4Kuva(String oid, String kieliUri, MultipartBody body) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        Preconditions.checkNotNull(kieliUri, "Koodisto language URI cannot be null.");
        Preconditions.checkNotNull(body, "MultipartBody cannot be null.");
        LOG.debug("in saveKuva - komoto OID : {}, kieliUri : {}, bodyType : {}", oid, kieliUri, body.getType());
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
                result.setStatus(OK);
            } catch (IOException ex) {
                LOG.error("BinaryData save failed for komoto OID {}.", oid, ex);
                result.setStatus(ERROR);
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
     * @param oid  komoto OID
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
        if (ToteutustyyppiEnum.KORKEAKOULUOPINTO.equals(komoto.getToteutustyyppi())) {
            // Tutkintoon johtamattomalla ei ole relaatiota koulutusasteeseen, mutta
            // kuitenkin halutaan tarkistaa, että vain korkeakoulut voivat järjestää
            // näitä koulutuksia. Palauttamalla olla oleva koulutusaste (yliopistot)
            // rajoitus toimii samoin kuin kk-koulutuksilla
            return "koulutusasteoph2002_72#1";
        }

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
            result.addError(createValidationError(null, KoulutusValidationMessages.KOULUTUS_INPUT_OBJECT_MISSING.lower()));
        } else if (koulutusCopy.getMode() == null) {
            result.addError(createValidationError("mode", KoulutusValidationMessages.KOULUTUS_INPUT_PARAM_MISSING.lower()));
        } else if (koulutusCopy.getOrganisationOids() == null || koulutusCopy.getOrganisationOids().isEmpty()) {
            result.addError(createValidationError("organisationOids", KoulutusValidationMessages.KOULUTUS_TARJOAJA_MISSING.lower()));
        } else {
            for (String orgOid : koulutusCopy.getOrganisationOids()) {
                final OrganisaatioDTO org = organisaatioService.findByOid(orgOid);
                if (org == null) {
                    result.addError(createValidationError("organisationOids[" + orgOid + "]", KoulutusValidationMessages.KOULUTUS_TARJOAJA_INVALID.lower(), orgOid));
                } else if (!oppilaitosKoodiRelations.isKoulutusAllowedForOrganisation(
                        orgOid,
                        findKoodistoKoodiKoulutusasteUri(komoto))) {
                    result.addError(createValidationError("organisationOids[" + orgOid + "]", KoulutusValidationMessages.KOULUTUS_TARJOAJA_INVALID.lower(), orgOid));
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
                            persisted = koulutusUtilService.copyKomotoAndKomo(komoto, orgOid, null, null, true, KoulutusKorkeakouluV1RDTO.class);
                            break;
                        case KORKEAKOULUOPINTO:
                            persisted = koulutusUtilService.copyKomotoAndKomo(komoto, orgOid, null, null, true, KorkeakouluOpintoV1RDTO.class);
                            break;
                        case LUKIOKOULUTUS:
                            persisted = insertKoulutusGeneric((KoulutusLukioV1RDTO) koulutusDtoForCopy(KoulutusLukioV1RDTO.class, komoto, orgOid));
                            break;
                        case EB_RP_ISH:
                            persisted = insertKoulutusGeneric((KoulutusEbRpIshV1RDTO) koulutusDtoForCopy(KoulutusEbRpIshV1RDTO.class, komoto, orgOid));
                            break;
                        case LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA:
                            persisted = insertKoulutusGeneric((KoulutusLukioAikuistenOppimaaraV1RDTO) koulutusDtoForCopy(KoulutusLukioAikuistenOppimaaraV1RDTO.class, komoto, orgOid));
                            break;
                        case AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA:
                            persisted = insertKoulutusNayttotutkintona((KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO) koulutusDtoForCopy(KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO.class, komoto, orgOid));
                            break;
                        case ERIKOISAMMATTITUTKINTO:
                            persisted = insertKoulutusNayttotutkintona((ErikoisammattitutkintoV1RDTO) koulutusDtoForCopy(ErikoisammattitutkintoV1RDTO.class, komoto, orgOid));
                            break;
                        case AMMATTITUTKINTO:
                            persisted = insertKoulutusNayttotutkintona((AmmattitutkintoV1RDTO) koulutusDtoForCopy(AmmattitutkintoV1RDTO.class, komoto, orgOid));
                            break;
                        case AMMATILLINEN_PERUSTUTKINTO:
                            persisted = insertKoulutusGeneric((KoulutusAmmatillinenPerustutkintoV1RDTO) koulutusDtoForCopy(KoulutusAmmatillinenPerustutkintoV1RDTO.class, komoto, orgOid));
                            break;
                        case AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA:
                            persisted = insertKoulutusGeneric((KoulutusAmmatillinenPeruskoulutusErityisopetuksenaV1RDTO) koulutusDtoForCopy(KoulutusAmmatillinenPeruskoulutusErityisopetuksenaV1RDTO.class, komoto, orgOid));
                            break;
                        case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS:
                            persisted = insertKoulutusGeneric((KoulutusValmentavaJaKuntouttavaV1RDTO) koulutusDtoForCopy(KoulutusValmentavaJaKuntouttavaV1RDTO.class, komoto, orgOid));
                            break;
                        case VAPAAN_SIVISTYSTYON_KOULUTUS:
                            persisted = insertKoulutusGeneric((KoulutusVapaanSivistystyonV1RDTO) koulutusDtoForCopy(KoulutusVapaanSivistystyonV1RDTO.class, komoto, orgOid));
                            break;
                        case AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS:
                            persisted = insertKoulutusGeneric((KoulutusAmmatilliseenPeruskoulutukseenOhjaavaJaValmistavaV1RDTO) koulutusDtoForCopy(KoulutusAmmatilliseenPeruskoulutukseenOhjaavaJaValmistavaV1RDTO.class, komoto, orgOid));
                            break;
                        case MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS:
                            persisted = insertKoulutusGeneric((KoulutusMaahanmuuttajienAmmatilliseenPeruskoulutukseenValmistavaV1RDTO) koulutusDtoForCopy(KoulutusMaahanmuuttajienAmmatilliseenPeruskoulutukseenValmistavaV1RDTO.class, komoto, orgOid));
                            break;
                        case MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS:
                            persisted = insertKoulutusGeneric((KoulutusMaahanmuuttajienJaVieraskielistenLukiokoulutukseenValmistavaV1RDTO) koulutusDtoForCopy(KoulutusMaahanmuuttajienJaVieraskielistenLukiokoulutukseenValmistavaV1RDTO.class, komoto, orgOid));
                            break;
                        case PERUSOPETUKSEN_LISAOPETUS:
                            persisted = insertKoulutusGeneric((KoulutusPerusopetuksenLisaopetusV1RDTO) koulutusDtoForCopy(KoulutusPerusopetuksenLisaopetusV1RDTO.class, komoto, orgOid));
                            break;
                        case AIKUISTEN_PERUSOPETUS:
                            persisted = insertKoulutusGeneric((KoulutusAikuistenPerusopetusV1RDTO) koulutusDtoForCopy(KoulutusAikuistenPerusopetusV1RDTO.class, komoto, orgOid));
                            break;
                        case AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA:
                            persisted = insertKoulutusGeneric((KoulutusAmmatilliseenPeruskoulutukseenValmentavaV1RDTO)
                                    koulutusDtoForCopy(KoulutusAmmatilliseenPeruskoulutukseenValmentavaV1RDTO.class, komoto, orgOid));
                            break;
                        case AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER:
                            persisted = insertKoulutusGeneric((KoulutusAmmatilliseenPeruskoulutukseenValmentavaERV1RDTO)
                                    koulutusDtoForCopy(KoulutusAmmatilliseenPeruskoulutukseenValmentavaERV1RDTO.class, komoto, orgOid));
                            break;
                        default:
                            throw new RuntimeException("Not implemented type : " + getType(komoto));
                    }

                    if (persisted == null) {
                        throw new RuntimeException("Copy failed : " + getType(komoto));
                    }

                    AUDIT.log(builder()
                            .setOperation(TarjontaOperation.COPY)
                            .setResource(TarjontaResource.KOULUTUS)
                            .setResourceOid(persisted.getOid())
                            .add("copySource", komoto.getOid())
                            .build());

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
                String prevTarjoaja = komoto.getTarjoaja();
                komoto.setTarjoaja(orgOid);

                // Pidä owner-taulu synkassa
                boolean tarjoajaFound = false;
                for (KoulutusOwner owner : komoto.getOwners()) {
                    if (owner.getOwnerType().equals(KoulutusOwner.TARJOAJA)
                            && owner.getOwnerOid().equals(prevTarjoaja)) {
                        owner.setOwnerOid(orgOid);
                        tarjoajaFound = true;
                    }
                }
                // Tämän ei pitäisi tapahtua, mutta jos owner taulusta olisi puuttunut
                // oikeat datat, niin tämä varmistaa, että owner taulussa on oikea tarjoaja
                if (!tarjoajaFound) {
                    KoulutusOwner owner = new KoulutusOwner();
                    owner.setOwnerOid(orgOid);
                    owner.setOwnerType(KoulutusOwner.TARJOAJA);
                    komoto.getOwners().add(owner);
                }

                komoto.setLastUpdatedByOid(contextDataService.getCurrentUserOid());
                koulutusmoduuliToteutusDAO.update(komoto);

                AUDIT.log(builder()
                        .setResource(TarjontaResource.KOULUTUS)
                        .setResourceOid(komoto.getOid())
                        .setOperation(TarjontaOperation.MOVE)
                        .add("previousTarjoaja", prevTarjoaja)
                        .add("newTarjoaja", komoto.getTarjoaja())
                        .build());

                result.getResult().getTo().add(new KoulutusCopyStatusV1RDTO(komoto.getOid(), orgOid));
                indexerResource.indexKoulutukset(Lists.newArrayList(komoto.getId()));
                final List<Hakukohde> hakukohdes = hakukohdeDAO.findByKoulutusOid(komoto.getOid());

                // Päivitä hakukohteen tarjoajatiedot (jos ne on asetettu)
                for (Hakukohde hakukohde : hakukohdes) {
                    Map<String, KoulutusmoduuliToteutusTarjoajatiedot> tarjoajatiedot = hakukohde.getKoulutusmoduuliToteutusTarjoajatiedot();

                    KoulutusmoduuliToteutusTarjoajatiedot tarjoajat = tarjoajatiedot.get(komoto.getOid());
                    if (tarjoajat != null) {
                        tarjoajat.removeTarjoaja(prevTarjoaja);
                        tarjoajat.getTarjoajaOids().add(orgOid);
                    }
                }

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
                result.getErrors().addAll(copyOrMove.getErrors());
            }
        }

        return result;
    }

    @Override
    public ResultV1RDTO isAllowedEducationByOrganisationOid(final String organisationOid) {
        ResultV1RDTO<KoulutustyyppiKoosteV1RDTO> dto = new ResultV1RDTO<KoulutustyyppiKoosteV1RDTO>();
        KoulutusmoduuliDAO.SearchCriteria searchCriteria = new KoulutusmoduuliDAO.SearchCriteria();
        searchCriteria.setKoulutustyyppiUris(oppilaitosKoodiRelations.getKoulutustyyppiUris(organisationOid));
        List<Koulutusmoduuli> search = koulutusmoduuliDAO.search(new KoulutusmoduuliDAO.SearchCriteria());
        KoulutustyyppiKoosteV1RDTO k = new KoulutustyyppiKoosteV1RDTO();
        k.setKoulutustyyppiUris(searchCriteria.getKoulutustyyppiUris());
        k.setModules(!search.isEmpty());
        dto.setResult(k);
        return dto;
    }

    private static ToteutustyyppiEnum getType(KoulutusmoduuliToteutus komoto) {
        if (komoto.getToteutustyyppi() != null) {
            return komoto.getToteutustyyppi();
        }
        throw new RuntimeException("Cannot convert 'unknown' entity to DTO - found KOMO koulutustyyppi : '" + komoto.getKoulutusmoduuli().getKoulutustyyppiUri() + "' for komoto OID :" + komoto.getOid());
    }

    private KoulutusV1RDTO koulutusDtoForCopy(Class clazz, KoulutusmoduuliToteutus komoto, String orgOid) {
        return koulutusUtilService.koulutusDtoForCopy(clazz, komoto, orgOid);
    }

    public boolean existsDuplicateKoulutus(KoulutusGenericV1RDTO dto) {
        List<String> koulutuslajis = new ArrayList<String>();
        koulutuslajis.add(getKoodiUriFromKoodiV1RDTO(dto.getKoulutuslaji(), true));

        String koulutusohjelma = null;
        if (dto.getKoulutusohjelma() != null && dto.getKoulutusohjelma().getUri() != null) {
            koulutusohjelma = getKoodiUriFromKoodiV1RDTO(dto.getKoulutusohjelma(), true);
        }

        List<KoulutusmoduuliToteutus> komotos = koulutusmoduuliToteutusDAO.findKoulutusModuuliWithPohjakoulutusAndTarjoaja(
                dto.getOrganisaatio().getOid(),
                getKoodiUriFromKoodiV1RDTO(dto.getPohjakoulutusvaatimus(), true),
                getKoodiUriFromKoodiV1RDTO(dto.getKoulutuskoodi(), true),
                koulutusohjelma,
                dto.getOpetuskielis().getUrisAsStringList(true),
                koulutuslajis
        );

        if (komotos != null) {
            for (KoulutusmoduuliToteutus tmpKomoto : komotos) {
                if (!isSameKoulutus(dto, tmpKomoto) && isSameKausiAndVuosi(dto, tmpKomoto)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isSameKoulutus(KoulutusGenericV1RDTO dto, KoulutusmoduuliToteutus komoto) {
        return dto.getOid() != null && dto.getOid().equals(komoto.getOid());
    }

    public boolean isSameKausiAndVuosi(KoulutusGenericV1RDTO dto, KoulutusmoduuliToteutus komoto) {
        KoulutusCommonConverter converter = new KoulutusCommonConverter();
        KoulutusmoduuliToteutus newKomoto = new KoulutusmoduuliToteutus();
        converter.handleDates(newKomoto, dto);

        return newKomoto.getAlkamisVuosi().equals(komoto.getAlkamisVuosi())
                && newKomoto.getAlkamiskausiUri().equals(komoto.getAlkamiskausiUri());
    }

    public static String getKoodiUriFromKoodiV1RDTO(KoodiV1RDTO koodi, boolean addVersionToUri) {
        if (koodi == null) {
            return "";
        }

        String uri = koodi.getUri();

        if (addVersionToUri) {
            uri = uri.concat("#" + koodi.getVersio().toString());
        }

        return uri;
    }

    public ResultV1RDTO<List<KoulutusHakutulosV1RDTO>> getJarjestettavatKoulutukset(String oid) {
        List<KoulutusmoduuliToteutus> komotos = koulutusmoduuliToteutusDAO.findKomotosByTarjoajanKoulutusOid(oid);

        List<KoulutusHakutulosV1RDTO> hakutulokset = new ArrayList<KoulutusHakutulosV1RDTO>();
        for (KoulutusmoduuliToteutus komoto : komotos) {
            hakutulokset.add(converterV1.fromKomoto(komoto));
        }

        return new ResultV1RDTO<List<KoulutusHakutulosV1RDTO>>(hakutulokset);
    }

    @Override
    public ResultV1RDTO<Set<OppiaineV1RDTO>> getOppiaineet(String oppiaine, String kieliKoodi) {
        ResultV1RDTO result = new ResultV1RDTO();

        if (StringUtils.isBlank(oppiaine) || StringUtils.isBlank(kieliKoodi) || oppiaine.length() < 3) {
            result.setStatus(VALIDATION);
        }
        else {
            List<Oppiaine> oppiaineet = oppiaineDAO.findByOppiaineKieliKoodi(oppiaine, kieliKoodi);
            Set<Oppiaine> asSet = new HashSet<Oppiaine>();
            asSet.addAll(oppiaineet);
            result.setResult(converterToRDTO.oppiaineetFromEntityToDto(asSet));
        }

        return result;
    }

    private void indexKomoto(KoulutusmoduuliToteutus komoto) {
        indexerResource.indexKoulutukset(Lists.newArrayList(komoto.getId()));

        // Fetch on lazy hakukohteille -> pitää hakea erikseen
        final List<Hakukohde> hakukohdes = hakukohdeDAO.findByKoulutusOid(komoto.getOid());
        List<Long> hakukohdeIds = new LinkedList<Long>();
        for (Hakukohde hakukohde : hakukohdes) {
            hakukohdeIds.add(hakukohde.getId());
        }
        indexerResource.indexHakukohteet(hakukohdeIds);
    }

}
