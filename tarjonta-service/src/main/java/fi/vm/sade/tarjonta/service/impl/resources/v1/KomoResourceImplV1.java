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
import fi.vm.sade.security.SadeUserDetailsWrapper;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.EntityConverterToKomoRDTO;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusKuvausV1RDTO;
import fi.vm.sade.tarjonta.service.impl.resources.v1.komo.validation.KomoValidator;
import fi.vm.sade.tarjonta.service.resources.v1.KomoV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KomoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.ModuuliTuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author jani
 */
@Transactional(readOnly = false)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class KomoResourceImplV1 implements KomoV1Resource {

    private static final Logger LOG = LoggerFactory.getLogger(KomoResourceImplV1.class);
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private PermissionChecker permissionChecker;
    @Autowired
    private EntityConverterToKomoRDTO converterKomoToRDTO;
    @Autowired
    private KoulutusSisaltyvyysDAO koulutusSisaltyvyysDAO;
    @Autowired(required = true)
    private KoulutusKuvausV1RDTO<KomoTeksti> komoKoulutusConverters;

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

    /**
     * Validate user language code. Default or fallback value is 'FI'.
     *
     * @param lang
     * @return
     */
    private String checkArgsLangCode(String lang) {
        if (lang == null || lang.isEmpty() || lang.length() != 2) {
            return "FI";
        }

        return lang;
    }

    /**
     * Validate the show meta argument. No argument, then show all meta data
     * objects.
     *
     * @param meta
     * @return
     */
    private boolean checkArgsMeta(Boolean meta) {
        return checkArgsMeta(meta, true);
    }

    private boolean checkArgsMeta(Boolean meta, boolean defaultValue) {
        return meta != null ? meta : defaultValue;
    }

    /**
     * Get user's preferred language code. Default or fallback value is 'FI'.
     */
    private String getUserLang() {
        Preconditions.checkNotNull(SecurityContextHolder.getContext(), "Context object cannot be null.");
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Preconditions.checkNotNull(authentication, "Authentication object cannot be null.");
        final Object principal = authentication.getPrincipal();

        if (principal != null && principal instanceof SadeUserDetailsWrapper) {
            SadeUserDetailsWrapper sadeUser = (SadeUserDetailsWrapper) principal;
            LOG.info("User SadeUserDetailsWrapper : {}, user oid : {}", sadeUser, sadeUser.getUsername());

            if (sadeUser.getLang() != null && !sadeUser.getLang().isEmpty()) {
                return sadeUser.getLang(); //return an user lang code
            } else {
                LOG.debug("user has no lang code!");
                return "FI";
            }
        }

        LOG.error("Not user data found.");
        return "FI";
    }

    @Override
    public ResultV1RDTO<KomoV1RDTO> postKomo(KomoV1RDTO dto) {
        ResultV1RDTO result = new ResultV1RDTO();

        if (!KomoValidator.validateBaseData(dto, result)) {
            switch (ModuulityyppiEnum.fromEnum(dto.getKoulutusasteTyyppi())) {
                case KORKEAKOULUTUS:
                    KomoValidator.validateModuleKorkeakoulu(dto, result);
                    break;
                case TUNTEMATON:
                case PERUSOPETUS_ULKOMAINEN:
                    KomoValidator.validateModuleGeneric(dto, result);
                    break;
                default:
                    KomoValidator.validateModuleLukioAndAmm(dto, result);
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
                        result.setResult(converterKomoToRDTO.convert(conversionService.convert(dto, Koulutusmoduuli.class), getUserLang(), true));
                    }
                } else {
                    //create new module
                    permissionChecker.checkCreateKoulutusmoduuli();
                    Preconditions.checkNotNull(dto.getKomoOid() != null, "External KOMO OID not allowed. OID : %s.", dto.getKomoOid());

                    final Koulutusmoduuli newKomo = conversionService.convert(dto, Koulutusmoduuli.class);
                    Preconditions.checkNotNull(newKomo, "KOMO conversion to database object failed : object : %s.", ReflectionToStringBuilder.toString(dto));
                    result.setResult(converterKomoToRDTO.convert(koulutusmoduuliDAO.insert(newKomo), getUserLang(), true));
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

        lang = checkArgsLangCode(lang);
        meta = checkArgsMeta(meta);

        if (komo == null) {
            return resultRDTO;
        }

        resultRDTO.setResult(converterKomoToRDTO.convert(komo, lang, meta));
        return resultRDTO;
    }

    @Override
    public ResultV1RDTO<List<KomoV1RDTO>> searchInfo(String koulutuskoodi, Boolean meta, String lang) {

        lang = checkArgsLangCode(lang);
        meta = checkArgsMeta(meta, false);

        KoulutusmoduuliDAO.SearchCriteria criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.setLikeKoulutusKoodiUriWithoutVersion(koulutuskoodi);
        List<Koulutusmoduuli> komos = this.koulutusmoduuliDAO.search(criteria);
        ArrayList<KomoV1RDTO> dtos = Lists.<KomoV1RDTO>newArrayList();
        for (Koulutusmoduuli komo : komos) {
            dtos.add(converterKomoToRDTO.convert(komo, lang, meta));
        }

        return new ResultV1RDTO<List<KomoV1RDTO>>(dtos);
    }

    @Override
    public ResultV1RDTO<List<ModuuliTuloksetV1RDTO>> searchModule(
            ToteutustyyppiEnum koulutustyyppiUri,
            String koulutuskoodiUri,
            String tila) {
        return searchModule(koulutustyyppiUri, null, koulutuskoodiUri, tila);
    }

    @Override
    public ResultV1RDTO<List<ModuuliTuloksetV1RDTO>> searchModule(
            ToteutustyyppiEnum koulutustyyppiUri,
            KoulutusmoduuliTyyppi koulutusmoduuliTyyppi,
            String koulutuskoodiUri,
            String tila) {
        Preconditions.checkNotNull(koulutustyyppiUri, "Koulutustyyppi URI cannot be null.");

        KoulutusmoduuliDAO.SearchCriteria criteria = new KoulutusmoduuliDAO.SearchCriteria();
        if (tila != null) {
            criteria.setTila(criteria.getTila());
        }

        if (koulutuskoodiUri != null) {
            criteria.setLikeKoulutusKoodiUriWithoutVersion(koulutuskoodiUri);
        }

        if (koulutustyyppiUri != null) {
            criteria.setKoulutustyyppiUri(koulutustyyppiUri);
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

            ModuuliTuloksetV1RDTO dto = new ModuuliTuloksetV1RDTO(m.getOid(),
                    fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.valueOf(m.getModuuliTyyppi().name()),
                    m.getKoulutusUri().substring(0, m.getKoulutusUri().indexOf("#")),
                    null);

            switch (koulutustyyppiUri) {
                case LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA:
                case LUKIOKOULUTUS:
                    if (m.getLukiolinjaUri() != null && !m.getLukiolinjaUri().isEmpty()) {
                        dto.setKoulutusohjelmaUri(m.getLukiolinjaUri().substring(0, m.getLukiolinjaUri().indexOf("#")));
                    }
                    break;
                default:
                    if (m.getKoulutusohjelmaUri() != null && !m.getKoulutusohjelmaUri().isEmpty()) {
                        dto.setKoulutusohjelmaUri(m.getKoulutusohjelmaUri().substring(0, m.getKoulutusohjelmaUri().indexOf("#")));
                    }
                    break;
            }
            searchResults.add(dto);
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
}
