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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.publication.model.RestParam;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.EntityConverterToKomoRDTO;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KomoRDTOConverterToEntity;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusKuvausV1RDTO;
import fi.vm.sade.tarjonta.service.impl.resources.v1.komo.validation.KomoValidator;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidationMessages;
import fi.vm.sade.tarjonta.service.resources.v1.KomoV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KomoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.ModuuliTuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jani
 */
@Transactional(readOnly = false)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class KomoResourceImplV1 implements KomoV1Resource {

    private static final boolean NO_IMAGE = false;

    private static final Logger LOG = LoggerFactory.getLogger(KomoResourceImplV1.class);
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired
    private KomoRDTOConverterToEntity convertToEntity;
    @Autowired
    private PermissionChecker permissionChecker;
    @Autowired
    private EntityConverterToKomoRDTO converterToRDTO;
    @Autowired
    private KoulutusSisaltyvyysDAO koulutusSisaltyvyysDAO;
    @Autowired
    private KoulutusKuvausV1RDTO<KomoTeksti> komoKoulutusConverters;
    @Autowired
    private ContextDataService contextDataService;

    @Override
    public ResultV1RDTO deleteByOid(String oid) {
        permissionChecker.checkCreateKoulutusmoduuli();

        final Koulutusmoduuli komo = koulutusmoduuliDAO.findByOid(oid);
        ResultV1RDTO dto = new ResultV1RDTO();

        if (komo == null) {
            dto.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            return dto;
        }

        if (!komo.getKoulutusmoduuliToteutusList().isEmpty()) {
            dto.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            ArrayList<ErrorV1RDTO> newArrayList = Lists.<ErrorV1RDTO>newArrayList();
            ErrorV1RDTO errorV1RDTO = new ErrorV1RDTO();
            errorV1RDTO.setErrorMessageKey("KOMOTO_RELATION_FOUND");
            newArrayList.add(errorV1RDTO);
            dto.setErrors(newArrayList);
            return dto;
        }

        final List<String> children = koulutusSisaltyvyysDAO.getChildren(oid);

        if (!children.isEmpty()) {
            dto.setStatus(ResultV1RDTO.ResultStatus.ERROR);
            ArrayList<ErrorV1RDTO> newArrayList = Lists.<ErrorV1RDTO>newArrayList();
            ErrorV1RDTO errorV1RDTO = new ErrorV1RDTO();
            errorV1RDTO.setErrorMessageKey("CHILD_KOMO_RELATION_FOUND");
            newArrayList.add(errorV1RDTO);
            dto.setErrors(newArrayList);
        } else {
            koulutusmoduuliDAO.remove(komo);
        }

        return dto;
    }

    @Override
    public ResultV1RDTO<List<ModuuliTuloksetV1RDTO>> importModuleGroupByKoulutusUri(String koulutusUri, List<KomoV1RDTO> dtos) {
        permissionChecker.checkCreateKoulutusmoduuli();
        ResultV1RDTO<List<ModuuliTuloksetV1RDTO>> result = new ResultV1RDTO<List<ModuuliTuloksetV1RDTO>>();

        if (dtos == null || dtos.isEmpty()) {
            result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_IMPORT_INVALID_COUNT.getFieldName(), KoulutusValidationMessages.KOULUTUS_IMPORT_INVALID_COUNT.lower()));
            return result;
        }

        Set<String> uniqueKoulutusohjelma = Sets.<String>newHashSet();
        Set<String> uniqueOsaamisala = Sets.<String>newHashSet();
        Set<String> uniqueLukiolinja = Sets.<String>newHashSet();

        for (KomoV1RDTO dto : dtos) {
            switch (ModuulityyppiEnum.fromEnum(dto.getKoulutusasteTyyppi())) {
                case KORKEAKOULUTUS:
                    KomoValidator.validateModuleKorkeakoulu(dto, result);
                    break;
                default:
                    KomoValidator.validateModuleGeneric(dto, result);
                    break;
            }

            if (!EntityConverterToKomoRDTO.isUri(dto.getKoulutuskoodi()) && !koulutusUri.equals(EntityConverterToKomoRDTO.getUri(dto.getKoulutuskoodi()))) {
                result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_IMPORT_INVALID_GROUP.getFieldName(), KoulutusValidationMessages.KOULUTUS_IMPORT_INVALID_GROUP.lower()));
            }

            /*
             * Test unique tutkinto-ohjelma
             */
            if (uniqueKoulutusohjelma.contains(EntityConverterToKomoRDTO.getUri(dto.getKoulutusohjelma()))) {
                result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_IMPORT_NON_UNIQUE_KOULUTUSOHJELMA.getFieldName(), KoulutusValidationMessages.KOULUTUS_IMPORT_NON_UNIQUE_KOULUTUSOHJELMA.lower(), EntityConverterToKomoRDTO.getUri(dto.getKoulutusohjelma())));
            }

            if (uniqueOsaamisala.contains(EntityConverterToKomoRDTO.getUri(dto.getOsaamisala()))) {
                result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_IMPORT_NON_UNIQUE_OSAAMISALA.getFieldName(), KoulutusValidationMessages.KOULUTUS_IMPORT_NON_UNIQUE_OSAAMISALA.lower(), EntityConverterToKomoRDTO.getUri(dto.getOsaamisala())));
            }

            if (uniqueLukiolinja.contains(EntityConverterToKomoRDTO.getUri(dto.getLukiolinja()))) {
                result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_IMPORT_NON_UNIQUE_LUKIOLINJA.getFieldName(), KoulutusValidationMessages.KOULUTUS_IMPORT_NON_UNIQUE_LUKIOLINJA.lower(), EntityConverterToKomoRDTO.getUri(dto.getLukiolinja())));
            }

            if (EntityConverterToKomoRDTO.isUri(dto.getKoulutusohjelma())) {
                uniqueKoulutusohjelma.add(EntityConverterToKomoRDTO.getUri(dto.getKoulutusohjelma()));
            }

            if (EntityConverterToKomoRDTO.isUri(dto.getOsaamisala())) {
                uniqueOsaamisala.add(EntityConverterToKomoRDTO.getUri(dto.getOsaamisala()));
            }

            if (EntityConverterToKomoRDTO.isUri(dto.getLukiolinja())) {
                uniqueLukiolinja.add(EntityConverterToKomoRDTO.getUri(dto.getLukiolinja()));
            }
        }

        List<KomoV1RDTO> tutkintoDtos = Lists.<KomoV1RDTO>newArrayList(); //only one required
        List<KomoV1RDTO> tutkintoOhjelmaDtos = Lists.<KomoV1RDTO>newArrayList(); //zero or more

        if (!result.hasErrors()) {
            for (KomoV1RDTO dto : dtos) {
                if (dto.getKoulutusmoduuliTyyppi().equals(KoulutusmoduuliTyyppi.TUTKINTO)
                        && !EntityConverterToKomoRDTO.isUri(dto.getKoulutusohjelma())
                        && !EntityConverterToKomoRDTO.isUri(dto.getOsaamisala())
                        && !EntityConverterToKomoRDTO.isUri(dto.getLukiolinja())) {
                    tutkintoDtos.add(dto);
                } else if (dto.getKoulutusmoduuliTyyppi().equals(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA)
                        && (EntityConverterToKomoRDTO.isUri(dto.getKoulutusohjelma())
                        || EntityConverterToKomoRDTO.isUri(dto.getOsaamisala())
                        || EntityConverterToKomoRDTO.isUri(dto.getLukiolinja()))) {
                    tutkintoOhjelmaDtos.add(dto);
                } else {
                    result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_IMPORT_INVALID_DATA.getFieldName(), KoulutusValidationMessages.KOULUTUS_IMPORT_INVALID_DATA.lower()));
                }
            }

            if (tutkintoDtos.size() != 1) {
                result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_IMPORT_INVALID_TUTKINTO_COUNT.getFieldName(), KoulutusValidationMessages.KOULUTUS_IMPORT_INVALID_TUTKINTO_COUNT.lower()));
            }

            if (!result.hasErrors()) {
                result.setResult(Lists.<ModuuliTuloksetV1RDTO>newArrayList());
                final KomoV1RDTO tutkintoDto = tutkintoDtos.get(0); //only one required parent per a group
                List<Koulutusmoduuli> successfullyImportedModules = Lists.<Koulutusmoduuli>newArrayList();
                try {
                    Koulutusmoduuli mTutkinto = searchAndModifyModule(tutkintoDto.getOid(),
                            fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi.TUTKINTO,
                            koulutusUri, null, null, null, tutkintoDto.getKoulutustyyppis());

                    if (mTutkinto == null) {
                        //persist new tutkinto module
                        mTutkinto = koulutusmoduuliDAO.insert(convertToEntity.convert(tutkintoDto));
                    }

                    successfullyImportedModules.add(mTutkinto);
                    for (KomoV1RDTO ohjelmaDto : tutkintoOhjelmaDtos) {
                        Koulutusmoduuli mTutkintoOhjelma = searchAndModifyModule(ohjelmaDto.getOid(),
                                fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA, koulutusUri,
                                ohjelmaDto.getKoulutusohjelma(),
                                ohjelmaDto.getOsaamisala(),
                                ohjelmaDto.getLukiolinja(),
                                ohjelmaDto.getKoulutustyyppis());
                        if (mTutkintoOhjelma == null) {
                            //persist new tutkinto-ohjema module
                            mTutkintoOhjelma = koulutusmoduuliDAO.insert(convertToEntity.convert(ohjelmaDto));
                        }

                        //link: a parent module can have many childrens
                        KoulutusSisaltyvyys sisaltyvyys = new KoulutusSisaltyvyys(
                                mTutkinto, mTutkintoOhjelma, KoulutusSisaltyvyys.ValintaTyyppi.SOME_OFF);
                        koulutusSisaltyvyysDAO.insert(sisaltyvyys);

                        successfullyImportedModules.add(mTutkintoOhjelma);
                    }

                    //convert all the handled group of objects to output DTO:
                    for (Koulutusmoduuli m : successfullyImportedModules) {
                        result.getResult().add(EntityConverterToKomoRDTO.convertEntityToModuuliTuloksetV1RDTO(m, null));
                    }
                } catch (Exception e) {
                    LOG.error("Module import failed by group of '" + koulutusUri + "'", e);
                    result.addError(ErrorV1RDTO.createValidationError(KoulutusValidationMessages.KOULUTUS_IMPORT_FAILED.getFieldName(), KoulutusValidationMessages.KOULUTUS_IMPORT_FAILED.lower(), e.getMessage()));

                    //convert all the give group of objects to output DTO:
                    for (KomoV1RDTO dto : dtos) {
                        result.getResult().add(EntityConverterToKomoRDTO.convertKomoV1RDTOToModuuliTuloksetV1RDTO(dto, null));
                    }
                }
            }
        }

        return result;
    }

    /*
     * Search module from DB by koulutus and 'tutkinto-ohjelma' uris.  
     */
    private Koulutusmoduuli searchAndModifyModule(final String oid, fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi tyyppi,
            final String koulutusUri,
            final KoodiV1RDTO koulutusohjelma,
            final KoodiV1RDTO osaamisala,
            final KoodiV1RDTO lukiolinja,
            final KoodiUrisV1RDTO koulutustyyppis) {
        Koulutusmoduuli m = null;

        if (oid != null && !oid.isEmpty()) {
            //Load by komo oid
            m = koulutusmoduuliDAO.findByOid(oid);

            if (m == null || !m.getModuuliTyyppi().equals(tyyppi)) {
                LOG.error("Module search failed by oid : {}, {}", oid, m != null ? m.getModuuliTyyppi() : m);
                throw new RuntimeException(oid);
            }
        } else if (koulutusUri != null && !koulutusUri.isEmpty()) {
            //Load by uris
            m = koulutusmoduuliDAO.findModule(tyyppi, koulutusUri,
                    EntityConverterToKomoRDTO.getUri(koulutusohjelma),
                    EntityConverterToKomoRDTO.getUri(osaamisala),
                    EntityConverterToKomoRDTO.getUri(lukiolinja));
        } else {
            LOG.info("No komo found for koulutus : '{}' ohjelma : '{}'", koulutusUri, koulutusohjelma + "/" + osaamisala + "/" + lukiolinja);
        }

        //add data only to null ohjelma fields
        convertToEntity.mergeEntityImportModifications(m, koulutusohjelma, osaamisala, lukiolinja, koulutustyyppis);

        return m;
    }

    @Override
    public ResultV1RDTO<KomoV1RDTO> postKomo(KomoV1RDTO dto) {
        ResultV1RDTO result = new ResultV1RDTO();

        if (!KomoValidator.validateBaseData(dto, result)) {
            switch (ModuulityyppiEnum.fromEnum(dto.getKoulutusasteTyyppi())) {
                case KORKEAKOULUTUS:
                    KomoValidator.validateModuleKorkeakoulu(dto, result);
                    break;
                default:
                    KomoValidator.validateModuleGeneric(dto, result);
                    break;

            }
            if (!result.hasErrors()) {

                if (dto.getOid() != null && dto.getOid().length() > 0) {
                    //update module
                    permissionChecker.checkUpdateKoulutusmoduuli();
                    Koulutusmoduuli komo = this.koulutusmoduuliDAO.findByOid(dto.getOid());
                    KomoValidator.validateModuleUpdate(komo, result);
                    if (!result.hasErrors()) {
                        Preconditions.checkNotNull(komo, "KOMO not found by OID : %s.", dto.getOid());
                        result.setResult(converterToRDTO.convert(convertToEntity.convert(dto), RestParam.noImageAndShowMeta(contextDataService.getCurrentUserLang())));
                    }
                } else {
                    //create new module
                    permissionChecker.checkCreateKoulutusmoduuli();
                    Preconditions.checkNotNull(dto.getKomoOid() != null, "External KOMO OID not allowed. OID : %s.", dto.getKomoOid());

                    final Koulutusmoduuli newKomo = convertToEntity.convert(dto);
                    Preconditions.checkNotNull(newKomo, "KOMO conversion to database object failed : object : %s.", ReflectionToStringBuilder.toString(dto));
                    result.setResult(converterToRDTO.convert(koulutusmoduuliDAO.insert(newKomo), RestParam.noImageAndShowMeta(contextDataService.getCurrentUserLang())));
                }
            }
        }

        return result;
    }

    @Override
    public ResultV1RDTO<KomoV1RDTO> findKomoByOid(String oid, Boolean meta, String lang) {
        Preconditions.checkNotNull(oid, "KOMO OID cannot be null.");

        ResultV1RDTO resultRDTO = new ResultV1RDTO();
        final Koulutusmoduuli komo = this.koulutusmoduuliDAO.findByOid(oid);
        if (komo == null) {
            return resultRDTO;
        }

        resultRDTO.setResult(converterToRDTO.convert(komo, RestParam.byUserRequest(meta, NO_IMAGE, lang)));
        return resultRDTO;
    }

    @Override
    public ResultV1RDTO<List<KomoV1RDTO>> searchInfo(String koulutuskoodi, Boolean meta, String lang) {
        KoulutusmoduuliDAO.SearchCriteria criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.setLikeKoulutusKoodiUriWithoutVersion(koulutuskoodi);
        List<Koulutusmoduuli> komos = this.koulutusmoduuliDAO.search(criteria);
        ArrayList<KomoV1RDTO> dtos = Lists.<KomoV1RDTO>newArrayList();
        for (Koulutusmoduuli komo : komos) {
            dtos.add(converterToRDTO.convert(komo, RestParam.byUserRequest(meta, NO_IMAGE, lang)));
        }

        return new ResultV1RDTO<List<KomoV1RDTO>>(dtos);
    }

    @Override
    public ResultV1RDTO<List<ModuuliTuloksetV1RDTO>> searchModule(
            ToteutustyyppiEnum koulutustyyppiUri,
            String koulutusUri,
            String ohjelma,
            String tila) {
        return searchModule(koulutustyyppiUri, null, koulutusUri, ohjelma, tila);
    }

    @Override
    public ResultV1RDTO<List<ModuuliTuloksetV1RDTO>> searchModule(
            ToteutustyyppiEnum koulutustyyppiUri,
            KoulutusmoduuliTyyppi koulutusmoduuliTyyppi,
            String koulutusUri,
            String ohjelma,
            String tila) {
        Preconditions.checkNotNull(koulutustyyppiUri, "Koulutustyyppi URI cannot be null.");

        KoulutusmoduuliDAO.SearchCriteria criteria = new KoulutusmoduuliDAO.SearchCriteria();
        if (tila != null) {
            criteria.setTila(criteria.getTila());
        }

        if (koulutusUri != null) {
            criteria.setLikeKoulutusKoodiUriWithoutVersion(koulutusUri);
        }

        if (ohjelma != null) {
            //like search by uri from koulutusohjelma or osaamisala or lukiolinja db fields
            criteria.setLikeOhjelmaKoodiUriWithoutVersion(ohjelma);
        }

        if (koulutustyyppiUri != null) {
            criteria.setToteutustyyppiEnum(koulutustyyppiUri);
        }

        if (koulutusmoduuliTyyppi != null) {
            criteria.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi.valueOf(koulutusmoduuliTyyppi.name()));
        }

        List<Koulutusmoduuli> komos = this.koulutusmoduuliDAO.search(criteria);
        List<ModuuliTuloksetV1RDTO> searchResults = Lists.<ModuuliTuloksetV1RDTO>newArrayList();
        ResultV1RDTO<List<ModuuliTuloksetV1RDTO>> result = new ResultV1RDTO<List<ModuuliTuloksetV1RDTO>>();
        result.setResult(searchResults);

        if (komos == null || komos.isEmpty()) {
            result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            return result;
        }

        for (Koulutusmoduuli m : komos) {
            //Result objects do not have version information on the URIs.
            searchResults.add(EntityConverterToKomoRDTO.convertEntityToModuuliTuloksetV1RDTO(m, koulutustyyppiUri));
        }

        result.setResult(searchResults);
        return result;
    }

    @Override
    public ResultV1RDTO<KuvausV1RDTO> loadKomoTekstis(String oid) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        Koulutusmoduuli komo = koulutusmoduuliDAO.findByOid(oid);

        ResultV1RDTO<KuvausV1RDTO> result = new ResultV1RDTO<KuvausV1RDTO>();
        if (komo == null) {
            result.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            return result;
        }

        result.setResult(komoKoulutusConverters.convertMonikielinenTekstiToTekstiDTO(komo.getTekstit(), true));
        return result;
    }

    @Override
    public ResultV1RDTO saveKomoTekstis(String oid, KuvausV1RDTO<KomoTeksti> tekstis) {
        permissionChecker.checkCreateKoulutusmoduuli();
        Preconditions.checkNotNull(oid, "KOMO OID cannot be null.");
        Preconditions.checkNotNull(tekstis, "KomoTeksti objects cannot be null.");

        final Koulutusmoduuli komo = koulutusmoduuliDAO.findByOid(oid);
        ResultV1RDTO dto = new ResultV1RDTO();

        if (komo == null) {
            dto.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
            return dto;
        }

        komoKoulutusConverters.convertTekstiDTOToMonikielinenTeksti(tekstis, komo.getTekstit());
        koulutusmoduuliDAO.update(komo);
        return dto;
    }

}
