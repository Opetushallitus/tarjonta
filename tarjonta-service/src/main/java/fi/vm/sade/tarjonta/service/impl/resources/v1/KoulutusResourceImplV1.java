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
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.koodisto.KoulutuskoodiRelations;
import fi.vm.sade.tarjonta.model.BinaryData;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.service.business.exception.KoulutusUsedException;
import fi.vm.sade.tarjonta.service.business.exception.TarjontaBusinessException;
import fi.vm.sade.tarjonta.service.business.impl.ContextDataServiceImpl;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.EntityConverterToKoulutusKorkeakouluRDTO;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusKorkeakouluDTOConverterToEntity;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusKuvausV1RDTO;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidator;
import static fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidator.validateMimeType;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.KoulutusV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO.ResultStatus;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliKorkeakouluRelationV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliStandardRelationV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvaV1RDTO;
import fi.vm.sade.tarjonta.service.search.HakukohdePerustieto;
import fi.vm.sade.tarjonta.service.search.HakukohteetKysely;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.service.search.KoulutuksetKysely;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.search.TarjontaSearchService;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.Response;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

/**
 *
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
    private TarjontaSearchService tarjontaSearchService;
    @Autowired
    private IndexerResource solrIndexer;
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
    @Autowired
    private ConverterV1 converter;
    @Autowired
    private PermissionChecker permissionChecker;

    @Autowired
    private ContextDataService contextDataService;

    @Autowired
    private EntityConverterToKoulutusKorkeakouluRDTO converterToRDTO;

    @Autowired
    private KoulutusKorkeakouluDTOConverterToEntity convertToEntity;

    @Override
    public ResultV1RDTO<KoulutusV1RDTO> findByOid(String oid, Boolean meta, String lang) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");

        ResultV1RDTO resultRDTO = new ResultV1RDTO();
        final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(oid);

        lang = checkArgsLangCode(lang);
        meta = checkArgsMeta(meta);

        if (komoto == null) {
            return resultRDTO;
        }

        KoulutusasteTyyppi koulutusasteTyyppi = KoulutusasteTyyppi.fromValue(komoto.getKoulutusmoduuli().getKoulutustyyppi());

        switch (koulutusasteTyyppi) {
            case KORKEAKOULUTUS:
                resultRDTO.setResult(converterToRDTO.convert(komoto, lang, meta));
                break;
        }

        return resultRDTO;
    }

    @Override
    public ResultV1RDTO<KoulutusV1RDTO> postKoulutus(KoulutusV1RDTO dto) {

        if (dto.getClass() == KoulutusKorkeakouluV1RDTO.class) {
            return postKorkeakouluKoulutus((KoulutusKorkeakouluV1RDTO) dto);
        }

        ResultV1RDTO result = new ResultV1RDTO(null, ResultStatus.ERROR);
        result.addError(ErrorV1RDTO.createSystemError(new IllegalArgumentException(), "type_unknown", dto.getClass() + " not handled"));

        return result;
    }

    private ResultV1RDTO<KoulutusV1RDTO> postKorkeakouluKoulutus(KoulutusKorkeakouluV1RDTO dto) {

        validateRestObjectKorkeakouluDTO(dto);
        KoulutusmoduuliToteutus fullKomotoWithKomo = null;
        List<ErrorV1RDTO> validateKoulutus = KoulutusValidator.validateKoulutus(dto);
        ResultV1RDTO resultRDTO = new ResultV1RDTO();
        if (validateKoulutus.isEmpty()) {

            if (dto.getOid() != null && dto.getOid().length() > 0) {
                //update korkeakoulu koulutus
                fullKomotoWithKomo = updateKoulutusKorkeakoulu(dto);
            } else {
                //create korkeakoulu koulutus
                fullKomotoWithKomo = insertKoulutusKorkeakoulu(dto);
            }

            solrIndexer.indexKoulutukset(Lists.newArrayList(fullKomotoWithKomo.getId()));
            //publication.sendEvent(response.getTila(), response.getOid(), PublicationDataService.DATA_TYPE_KOMOTO, PublicationDataService.ACTION_INSERT);
            resultRDTO.setResult(converterToRDTO.convert(fullKomotoWithKomo, contextDataService.getCurrentUserLang(), true));
        } else {
            resultRDTO.setStatus(ResultV1RDTO.ResultStatus.VALIDATION);

            resultRDTO.setErrors(validateKoulutus);
            resultRDTO.setResult(dto);
        }

        return resultRDTO;
    }

    private KoulutusmoduuliToteutus insertKoulutusKorkeakoulu(final KoulutusKorkeakouluV1RDTO dto) {
        Preconditions.checkNotNull(dto.getKomotoOid() != null, "External KOMOTO OID not allowed. OID : %s.", dto.getKomotoOid());
        Preconditions.checkNotNull(dto.getKomoOid() != null, "External KOMO OID not allowed. OID : %s.", dto.getKomoOid());

        final KoulutusmoduuliToteutus newKomo = convertToEntity.convert(dto, contextDataService.getCurrentUserOid());
        Preconditions.checkNotNull(newKomo, "KOMOTO conversion to database object failed : object : %s.", ReflectionToStringBuilder.toString(dto));
        Preconditions.checkNotNull(newKomo.getKoulutusmoduuli(), "KOMO conversion to database object failed : object :  %s.", ReflectionToStringBuilder.toString(dto));

        permissionChecker.checkCreateKoulutus(dto.getOrganisaatio().getOid());
        koulutusmoduuliDAO.insert(newKomo.getKoulutusmoduuli());
        return koulutusmoduuliToteutusDAO.insert(newKomo);
    }

    private KoulutusmoduuliToteutus updateKoulutusKorkeakoulu(final KoulutusKorkeakouluV1RDTO dto) {
        Preconditions.checkNotNull(dto.getOid(), "KOMOTO OID cannot be null.");

        final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(dto.getOid());
        permissionChecker.checkUpdateKoulutusByTarjoajaOid(komoto.getTarjoaja());

        Preconditions.checkNotNull(komoto, "KOMOTO not found by OID : %s.", dto.getOid());
        return convertToEntity.convert(dto, contextDataService.getCurrentUserOid());
    }

    @Override
    public Response deleteByOid(String oid) {
        permissionChecker.checkRemoveKoulutus(oid);
        KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findByOid(oid);

        if (komoto.getHakukohdes().isEmpty()) {
            this.koulutusmoduuliToteutusDAO.remove(komoto);
            try {
                solrIndexer.deleteKoulutus(Lists.newArrayList(oid));

            } catch (IOException e) {
                throw new TarjontaBusinessException("indexing.error", e);
            }

        } else {
            throw new KoulutusUsedException();
        }
        return Response.ok().build();
    }

    private void validateRestObjectKorkeakouluDTO(final KoulutusKorkeakouluV1RDTO dto) {
        Preconditions.checkNotNull(dto, "An invalid data exception - KorkeakouluDTO object cannot be null.");
        Preconditions.checkNotNull(dto.getKoulutusasteTyyppi(), "KoulutusasteTyyppi enum cannot be null.");
        Preconditions.checkNotNull(dto.getKoulutusmoduuliTyyppi(), "KoulutusmoduuliTyyppi enum cannot be null.");
        Preconditions.checkNotNull(dto.getTila(), "Tila enum cannot be null.");
        Preconditions.checkNotNull(dto.getOrganisaatio() == null || dto.getOrganisaatio().getOid() == null, "Organisation OID was missing.");
        final OrganisaatioDTO org = organisaatioService.findByOid(dto.getOrganisaatio().getOid());
        Preconditions.checkNotNull(org, "No organisation found by OID : %s.", dto.getOrganisaatio().getOid());
    }

    @Override
    public ResultV1RDTO<List<NimiJaOidRDTO>> getHakukohteet(String oid) {
        HakukohteetKysely ks = new HakukohteetKysely();
        ks.getKoulutusOids().add(oid);

        HakukohteetVastaus vs = tarjontaSearchService.haeHakukohteet(ks);
        List<NimiJaOidRDTO> ret = new ArrayList<NimiJaOidRDTO>();
        for (HakukohdePerustieto hk : vs.getHakukohteet()) {
            ret.add(new NimiJaOidRDTO(hk.getNimi(), hk.getOid()));
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
    public Response saveKomoTekstis(String oid, KuvausV1RDTO<KomoTeksti> dto) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        Preconditions.checkNotNull(komoto, "KOMOTO not found by OID '%s'.", oid);
        Koulutusmoduuli komo = komoto.getKoulutusmoduuli();

        permissionChecker.checkUpdateKoulutusmoduuli();
        komoKoulutusConverters.convertTekstiDTOToMonikielinenTeksti(dto, komo.getTekstit());
        koulutusmoduuliDAO.update(komo);

        return Response.ok().build();
    }

    @Override
    public ResultV1RDTO getKoulutusRelation(String koulutuskoodi, KoulutusasteTyyppi koulutusasteTyyppi, Boolean meta, String lang) {
        Preconditions.checkNotNull(koulutuskoodi, "Koulutuskoodi parameter cannot be null.");
        lang = checkArgsLangCode(lang);
        meta = checkArgsMeta(meta);

        Class clazz = KoulutusmoduuliStandardRelationV1RDTO.class;

        switch (koulutusasteTyyppi) {
            case KORKEAKOULUTUS:
                clazz = KoulutusmoduuliKorkeakouluRelationV1RDTO.class;
                break;
            default:
                break;
        }

        ResultV1RDTO resultRDTO = new ResultV1RDTO();
        /*
         * TODO: toinen aste koodisto relations (as the korkeakoulu has different set of relations...)
         */
        try {
            if (koulutuskoodi.contains("_")) {
                //Very simple parameter check, if an undescore char is in the string, then the data is koodisto service koodi URI.
                resultRDTO.setResult(koulutuskoodiRelations.getKomoRelationByKoulutuskoodiUri(clazz, koulutuskoodi, new Locale(lang.toUpperCase()), meta));
            } else {
                SearchKoodisByKoodistoCriteriaType search = KoodiServiceSearchCriteriaBuilder.koodisByArvoAndKoodistoUri(koulutuskoodi, KoodistoURI.KOODISTO_TUTKINTO_URI);
                List<KoodiType> searchKoodisByKoodisto = koodiService.searchKoodisByKoodisto(search);
                if (searchKoodisByKoodisto == null || searchKoodisByKoodisto.isEmpty()) {
                    throw new TarjontaBusinessException("No koulutuskoodi koodisto KoodiType object found by '" + koulutuskoodi + "'.");
                }
                resultRDTO.setResult(koulutuskoodiRelations.getKomoRelationByKoulutuskoodiUri(clazz, searchKoodisByKoodisto.get(0).getKoodiUri(), new Locale(lang.toUpperCase()), meta));
            }
        } catch (Exception ex) {
            LOG.error("Koodisto relation error.", ex);
            resultRDTO.setStatus(ResultV1RDTO.ResultStatus.ERROR);
        }

        return resultRDTO;
    }

    @Override
    public Response deleteTeksti(String oid, String key, String uri) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Transactional(readOnly = false)
    public ResultV1RDTO<String> updateTila(String oid, TarjontaTila tila) {
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        Preconditions.checkArgument(komoto != null, "Koulutusta ei lÃ¶ytynyt: %s", oid);
        if (!komoto.getTila().acceptsTransitionTo(tila)) {
            return new ResultV1RDTO<String>(komoto.getTila().toString());
        }
        komoto.setTila(tila);

        permissionChecker.checkUpdateKoulutusByKoulutusOid(oid);
        komoto.setLastUpdatedByOid(contextDataService.getCurrentUserOid());
        koulutusmoduuliToteutusDAO.update(komoto);
        solrIndexer.indexKoulutukset(Collections.singletonList(komoto.getId()));
        return new ResultV1RDTO<String>(tila.toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchInfo(
            String searchTerms,
            List<String> organisationOids,
            List<String> koulutusOids,
            String hakukohdeTila,
            String alkamisKausi,
            Integer alkamisVuosi, List<KoulutusasteTyyppi> koulutusastetyyppi, String komoOid) {

        organisationOids = organisationOids != null ? organisationOids : new ArrayList<String>();

        KoulutuksetKysely q = new KoulutuksetKysely();

        q.setNimi(searchTerms);
        q.setkomoOid(komoOid);
        q.setKoulutuksenAlkamiskausi(alkamisKausi);
        q.setKoulutuksenAlkamisvuosi(alkamisVuosi);
        q.getTarjoajaOids().addAll(organisationOids);
        q.getKoulutusOids().addAll(koulutusOids);
        q.setKoulutuksenTila(hakukohdeTila == null ? null : fi.vm.sade.tarjonta.shared.types.TarjontaTila.valueOf(hakukohdeTila).asDto());
        q.getKoulutusasteTyypit().addAll(koulutusastetyyppi);
        KoulutuksetVastaus r = tarjontaSearchService.haeKoulutukset(q);

        return new ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>(converter.fromKoulutuksetVastaus(r));
    }

//    @Override
//    public ResultV1RDTO<KoulutusLukioV1RDTO> postLukiokoulutus(KoulutusLukioV1RDTO koulutus) {
//        LOG.info("postLukiokoulutus({})", koulutus);
//
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public ResultV1RDTO<KoulutusAmmatillinenPeruskoulutusV1RDTO> postAmmatillinenPeruskoulutus(KoulutusAmmatillinenPeruskoulutusV1RDTO koulutus) {
//        LOG.info("postAmmatillinenPeruskoulutus({})", koulutus);
//
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public ResultV1RDTO<KoulutusPerusopetuksenLisaopetusV1RDTO> postPerusopetuksenLisaopetusKoulutus(KoulutusPerusopetuksenLisaopetusV1RDTO koulutus) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public ResultV1RDTO<KoulutusValmentavaJaKuntouttavaV1RDTO> postValmentavaJaKuntouttavaKoulutus(KoulutusValmentavaJaKuntouttavaV1RDTO koulutus) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    @Override
    public Response deleteKuva(String oid, String kieliUri) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        Preconditions.checkNotNull(kieliUri, "Koodisto language URI cannot be null.");

        final BinaryData bin = koulutusmoduuliToteutusDAO.findKuvaByKomotoOidAndKieliUri(oid, kieliUri);
        if (bin != null) {
            final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(oid);

            permissionChecker.checkRemoveKoulutusKuva(oid);
            Map<String, BinaryData> kuvat = komoto.getKuvat();
            kuvat.remove(kieliUri);
            komoto.setLastUpdatedByOid(contextDataService.getCurrentUserOid());
            this.koulutusmoduuliToteutusDAO.update(komoto);
        }

        return Response.ok().build();
    }

    @Override
    public ResultV1RDTO<KuvaV1RDTO> getKuva(String oid, String kieliUri) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        Preconditions.checkNotNull(kieliUri, "Koodisto language URI cannot be null.");

        KuvaV1RDTO dto = new KuvaV1RDTO();
        ResultV1RDTO<KuvaV1RDTO> resultV1RDTO = new ResultV1RDTO<KuvaV1RDTO>(dto);

        final BinaryData bin = koulutusmoduuliToteutusDAO.findKuvaByKomotoOidAndKieliUri(oid, kieliUri);
        if (bin != null) {
            dto = new KuvaV1RDTO(bin.getFilename(), bin.getMimeType(), kieliUri, Base64.encodeBase64String(bin.getData()));
            resultV1RDTO.setResult(dto);
        } else {
            resultV1RDTO.setStatus(ResultV1RDTO.ResultStatus.NOT_FOUND);
        }
        return resultV1RDTO;
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
        return meta != null ? meta : true;
    }

    /**
     * Legacy HTML4 image upload for IE9.
     *
     * @param oid
     * @param kieliUri
     * @param body
     * @return
     */
    @Override
    public Response saveHtml4Kuva(String oid, String kieliUri, MultipartBody body) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        Preconditions.checkNotNull(kieliUri, "Koodisto language URI cannot be null.");
        Preconditions.checkNotNull(body, "MultipartBody cannot be null.");
        LOG.info("in saveKuva - komoto OID : {}, kieliUri : {}, bodyType : {}", oid, kieliUri, body.getType());

        final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(oid);
        Preconditions.checkNotNull(komoto, "Image save failed, no KOMOTO found by OID '%s'", oid);
        permissionChecker.checkAddKoulutusKuva(komoto.getTarjoaja());
        Attachment att = body.getRootAttachment();
        ResultV1RDTO<KuvaV1RDTO> result = new ResultV1RDTO<KuvaV1RDTO>();
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
     * @param oid
     * @param image
     * @return ResultV1DTO with status and error information.
     */
    @Override
    public ResultV1RDTO<KuvaV1RDTO> saveHtml5Kuva(String oid, KuvaV1RDTO kuva) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        Preconditions.checkNotNull(kuva, "KuvaV1RDTO cannot be null.");
        LOG.info("in saveKuva - komoto OID : {}, kieliUri : {}, bodyType : {}", oid, kuva.getKieliUri(), kuva.getFilename());
        ResultV1RDTO<KuvaV1RDTO> validateKoulutusKuva = KoulutusValidator.validateKoulutusKuva(kuva);
        if (validateKoulutusKuva.hasErrors()) {
            return validateKoulutusKuva;
        }

        /*
         * Check user permission
         */
        final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(oid);
        Preconditions.checkNotNull(komoto, "Image save failed, no KOMOTO found by OID '%s'", oid);
        permissionChecker.checkAddKoulutusKuva(komoto.getTarjoaja());

        /*
         * Update or insert uploaded binary data
         */
        BinaryData bin = null;
        if (komoto.isKuva(kuva.getKieliUri())) {
            bin = komoto.getKuvat().get(kuva.getKieliUri());
        } else {
            bin = new BinaryData();
        }

        final byte[] decoded = Base64.decodeBase64(KoulutusValidator.getValidBase64Image(kuva.getBase64data()));
        bin.setData(decoded);
        bin.setFilename(kuva.getFilename());
        bin.setMimeType(kuva.getMimeType());
        komoto.setKuvaByUri(kuva.getKieliUri(), bin);
        komoto.setLastUpdatedByOid(contextDataService.getCurrentUserOid());
        this.koulutusmoduuliToteutusDAO.update(komoto);

        return new ResultV1RDTO<KuvaV1RDTO>(kuva);
    }

}
