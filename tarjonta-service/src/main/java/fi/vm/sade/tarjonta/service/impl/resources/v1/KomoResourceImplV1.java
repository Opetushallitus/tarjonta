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
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.EntityConverterToKomoRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.KomoV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KomoV1RDTO;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
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
 * @author mlyly
 */
@Transactional(readOnly = false)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class KomoResourceImplV1 implements KomoV1Resource {

    private static final Logger LOG = LoggerFactory.getLogger(KomoResourceImplV1.class);
    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private PermissionChecker permissionChecker;
    @Autowired
    private EntityConverterToKomoRDTO converterKomoToRDTO;

    private Koulutusmoduuli insertKomo(final KomoV1RDTO dto) {
        Preconditions.checkNotNull(dto.getKomoOid() != null, "External KOMO OID not allowed. OID : %s.", dto.getKomoOid());

        final Koulutusmoduuli newKomo = conversionService.convert(dto, Koulutusmoduuli.class);
        Preconditions.checkNotNull(newKomo, "KOMO conversion to database object failed : object : %s.", ReflectionToStringBuilder.toString(dto));

        permissionChecker.checkCreateKoulutus(dto.getOrganisaatio().getOid());
        return koulutusmoduuliDAO.insert(newKomo);
    }

    private Koulutusmoduuli updateKomo(final KomoV1RDTO dto) {
        Preconditions.checkNotNull(dto.getOid(), "KOMO OID cannot be null.");

        final Koulutusmoduuli komo = this.koulutusmoduuliDAO.findByOid(dto.getOid());
        permissionChecker.checkUpdateKoulutusmoduuli();

        Preconditions.checkNotNull(komo, "KOMO not found by OID : %s.", dto.getOid());
        return conversionService.convert(dto, Koulutusmoduuli.class);
    }

    @Override
    public Response deleteByOid(String oid) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        Koulutusmoduuli komo = null;

        if (dto.getOid() != null && dto.getOid().length() > 0) {
            //update korkeakoulu koulutus
            komo = updateKomo(dto);
        } else {
            //create korkeakoulu koulutus
            komo = insertKomo(dto);
        }

        ResultV1RDTO resultRDTO = new ResultV1RDTO();
        resultRDTO.setResult(converterKomoToRDTO.convert(komo, getUserLang(), true));
        return resultRDTO;
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
    public ResultV1RDTO<List<KomoV1RDTO>> searchInfo(String koulutuskoodi, Boolean meta,  String lang) {

        lang = checkArgsLangCode(lang);
        meta = checkArgsMeta(meta, false);

        KoulutusmoduuliDAO.SearchCriteria criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.setLikeKoulutusKoodiUri(koulutuskoodi);
        List<Koulutusmoduuli> komos = this.koulutusmoduuliDAO.search(criteria);
        ArrayList<KomoV1RDTO> dtos = Lists.<KomoV1RDTO>newArrayList();
        for (Koulutusmoduuli komo : komos) {
            dtos.add(converterKomoToRDTO.convert(komo, lang, meta));
        }

        return new ResultV1RDTO<List<KomoV1RDTO>>(dtos);
    }
}
