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
import fi.vm.sade.tarjonta.service.business.exception.KoulutusUsedException;
import fi.vm.sade.tarjonta.service.business.exception.TarjontaBusinessException;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.CommonRestKoulutusConverters;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.TekstiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.KoulutusV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAmmatillinenPeruskoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusPerusopetuksenLisaopetusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusValmentavaJaKuntouttavaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliRelationV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvaV1RDTO;
import fi.vm.sade.tarjonta.service.search.HakukohdePerustieto;
import fi.vm.sade.tarjonta.service.search.HakukohteetKysely;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.service.search.KoulutuksetKysely;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.search.TarjontaSearchService;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import java.io.IOException;
import java.io.InputStream;
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
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import java.io.ByteArrayOutputStream;
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
    private ConversionService conversionService;
    @Autowired
    private TarjontaSearchService tarjontaSearchService;
    @Autowired
    private IndexerResource solrIndexer;
    @Autowired(required = true)
    private KoulutuskoodiRelations koulutuskoodiRelations;
    @Autowired(required = true)
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;
    @Autowired(required = true)
    private KoodiService koodiService;
    @Autowired(required = true)
    private OrganisaatioService organisaatioService;
    @Autowired(required = true)
    private CommonRestKoulutusConverters<KomoTeksti> komoKoulutusConverters;
    @Autowired(required = true)
    private CommonRestKoulutusConverters<KomotoTeksti> komotoKoulutusConverters;
    @Autowired
    private ConverterV1 converter;
    // @Autowired
    // private PermissionChecker permissionChecker;

    @Override
    public ResultV1RDTO<KoulutusV1RDTO> findByOid(String oid) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");

        ResultV1RDTO resultRDTO = new ResultV1RDTO();
        final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(oid);
        if (komoto == null) {
            return resultRDTO;
        }

        KoulutusasteTyyppi koulutusasteTyyppi = KoulutusasteTyyppi.fromValue(komoto.getKoulutusmoduuli().getKoulutustyyppi());

        switch (koulutusasteTyyppi) {
            case KORKEAKOULUTUS:
                resultRDTO.setResult(conversionService.convert(komoto, KoulutusKorkeakouluV1RDTO.class));
                break;
        }

        return resultRDTO;
    }

    @Override
    public ResultV1RDTO<KoulutusKorkeakouluV1RDTO> postKorkeakouluKoulutus(KoulutusKorkeakouluV1RDTO dto) {
        validateRestObjectKorkeakouluDTO(dto);
        String tarjoajaOid = dto.getOrganisaatio().getOid();
        KoulutusmoduuliToteutus toteutus = null;

        if (dto.getOid() != null && dto.getOid().length() > 0) {
            ///   permissionChecker.checkCreateKoulutus(tarjoajaOid);
            //update korkeakoulu koulutus
            toteutus = updateKoulutusKorkeakoulu(dto);
        } else {
            ///  permissionChecker.checkUpdateKoulutusByTarjoajaOid(tarjoajaOid);
            //create korkeakoulu koulutus
            toteutus = insertKoulutusKorkeakoulu(dto);
        }

        solrIndexer.indexKoulutukset(Lists.newArrayList(toteutus.getId()));
        // publication.sendEvent(response.getTila(), response.getOid(), PublicationDataService.DATA_TYPE_KOMOTO, PublicationDataService.ACTION_INSERT);
        ResultV1RDTO resultRDTO = new ResultV1RDTO();
        resultRDTO.setResult(conversionService.convert(toteutus, KoulutusKorkeakouluV1RDTO.class));
        return resultRDTO;
    }

    private KoulutusmoduuliToteutus insertKoulutusKorkeakoulu(final KoulutusKorkeakouluV1RDTO dto) {
        Preconditions.checkNotNull(dto.getKomotoOid() != null, "External KOMOTO OID not allowed. OID : %s.", dto.getKomotoOid());
        Preconditions.checkNotNull(dto.getKomoOid() != null, "External KOMO OID not allowed. OID : %s.", dto.getKomoOid());

        final KoulutusmoduuliToteutus newKomo = conversionService.convert(dto, KoulutusmoduuliToteutus.class);
        Preconditions.checkNotNull(newKomo, "KOMOTO conversion to database object failed : object : %s.", ReflectionToStringBuilder.toString(dto));
        Preconditions.checkNotNull(newKomo.getKoulutusmoduuli(), "KOMO conversion to database object failed : object :  %s.", ReflectionToStringBuilder.toString(dto));

        koulutusmoduuliDAO.insert(newKomo.getKoulutusmoduuli());
        return koulutusmoduuliToteutusDAO.insert(newKomo);
    }

    private KoulutusmoduuliToteutus updateKoulutusKorkeakoulu(final KoulutusKorkeakouluV1RDTO dto) {
        Preconditions.checkNotNull(dto.getOid(), "KOMOTO OID cannot be null.");
        final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(dto.getOid());
        Preconditions.checkNotNull(komoto, "KOMOTO not found by OID : %s.", dto.getOid());
        return conversionService.convert(dto, KoulutusmoduuliToteutus.class);
    }

    @Override
    public Response deleteByOid(String oid) {
        ///  permissionChecker.checkRemoveKoulutus(oid);
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
    public TekstiV1RDTO loadTekstis(String oid) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        Preconditions.checkNotNull(komoto, "KOMOTO not found by OID '%s'.", oid);

        TekstiV1RDTO komotoTekstiDto = komotoKoulutusConverters.convertMonikielinenTekstiToTekstiDTO(komoto.getTekstit());
        Koulutusmoduuli komo = komoto.getKoulutusmoduuli();
        TekstiV1RDTO<KomoTeksti> komoTekstiDto = komoKoulutusConverters.convertMonikielinenTekstiToTekstiDTO(komo.getTekstit());
        komotoTekstiDto.getTekstis().putAll(komoTekstiDto.getTekstis());

        //combine komo&komoto text data to the dto;
        return komotoTekstiDto;
    }

    @Override
    public ResultV1RDTO<TekstiV1RDTO> loadKomotoTekstis(String oid) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        Preconditions.checkNotNull(komoto, "KOMOTO not found by OID '%s'.", oid);

        ResultV1RDTO<TekstiV1RDTO> resultRDTO = new ResultV1RDTO<TekstiV1RDTO>();
        resultRDTO.setResult(komotoKoulutusConverters.convertMonikielinenTekstiToTekstiDTO(komoto.getTekstit()));
        return resultRDTO;
    }

    @Override
    public Response saveKomotoTekstis(String oid, TekstiV1RDTO<KomotoTeksti> dto) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        ///   permissionChecker.checkUpdateKoulutusByTarjoajaOid(komoto.getTarjoaja());

        Preconditions.checkNotNull(komoto, "KOMOTO not found by OID '%s'.", oid);
        komotoKoulutusConverters.convertTekstiDTOToMonikielinenTeksti(dto, komoto.getTekstit());
        koulutusmoduuliToteutusDAO.update(komoto);
        return Response.ok().build();
    }

    @Override
    public ResultV1RDTO<TekstiV1RDTO> loadKomoTekstis(String oid) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        Preconditions.checkNotNull(komoto, "KOMOTO not found by OID '%s'.", oid);

        ResultV1RDTO<TekstiV1RDTO> resultRDTO = new ResultV1RDTO<TekstiV1RDTO>();
        resultRDTO.setResult(komoKoulutusConverters.convertMonikielinenTekstiToTekstiDTO(komoto.getKoulutusmoduuli().getTekstit()));
        return resultRDTO;
    }

    @Override
    public Response saveKomoTekstis(String oid, TekstiV1RDTO<KomoTeksti> dto) {
        ///   permissionChecker.checkUpdateKoulutusmoduuli();
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        Preconditions.checkNotNull(komoto, "KOMOTO not found by OID '%s'.", oid);
        Koulutusmoduuli komo = komoto.getKoulutusmoduuli();
        komoKoulutusConverters.convertTekstiDTOToMonikielinenTeksti(dto, komo.getTekstit());
        koulutusmoduuliDAO.update(komo);

        return Response.ok().build();
    }

    @Override
    public ResultV1RDTO<KoulutusmoduuliRelationV1RDTO> getKoulutusRelation(String koulutuskoodi) {
        Preconditions.checkNotNull(koulutuskoodi, "Koulutuskoodi parameter cannot be null.");
        KoulutusmoduuliRelationV1RDTO relation = null;
        
        /*
        * TODO: toinen aste koodisto relations (as the korkeakoulu has different set of relations...)
        */
        
        if (koulutuskoodi.contains("_")) {
            //Very simple parameter check, if an undescore char is in the string, then the data is koodisto service koodi URI.
            relation = koulutuskoodiRelations.getKomoRelationByKoulutuskoodiUri(koulutuskoodi, true, new Locale("FI"));
        } else {
            SearchKoodisByKoodistoCriteriaType search = KoodiServiceSearchCriteriaBuilder.koodisByArvoAndKoodistoUri(koulutuskoodi, KoodistoURI.KOODISTO_TUTKINTO_URI);
            List<KoodiType> searchKoodisByKoodisto = koodiService.searchKoodisByKoodisto(search);
            if (searchKoodisByKoodisto == null || searchKoodisByKoodisto.isEmpty()) {
                throw new TarjontaBusinessException("No koulutuskoodi koodisto KoodiType object found by '" + koulutuskoodi + "'.");
            }
            relation = koulutuskoodiRelations.getKomoRelationByKoulutuskoodiUri(searchKoodisByKoodisto.get(0).getKoodiUri(), true, new Locale("FI"));
        }

        ResultV1RDTO<KoulutusmoduuliRelationV1RDTO> resultRDTO = new ResultV1RDTO<KoulutusmoduuliRelationV1RDTO>();
        resultRDTO.setResult(relation);
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
        Preconditions.checkArgument(komoto != null, "Koulutusta ei löytynyt: %s", oid);
        if (!komoto.getTila().acceptsTransitionTo(tila)) {
            return new ResultV1RDTO<String>(komoto.getTila().toString());
        }
        komoto.setTila(tila);
        koulutusmoduuliToteutusDAO.update(komoto);
        solrIndexer.indexKoulutukset(Collections.singletonList(komoto.getId()));
        return new ResultV1RDTO<String>(tila.toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchInfo(
            String searchTerms,
            List<String> organisationOids,
            String hakukohdeTila,
            String alkamisKausi,
            Integer alkamisVuosi) {

        organisationOids = organisationOids != null ? organisationOids : new ArrayList<String>();

        KoulutuksetKysely q = new KoulutuksetKysely();
        q.setNimi(searchTerms);
        q.setKoulutuksenAlkamiskausi(alkamisKausi);
        q.setKoulutuksenAlkamisvuosi(alkamisVuosi);
        q.getTarjoajaOids().addAll(organisationOids);
        q.setKoulutuksenTila(hakukohdeTila == null ? null : fi.vm.sade.tarjonta.shared.types.TarjontaTila.valueOf(hakukohdeTila).asDto());

        KoulutuksetVastaus r = tarjontaSearchService.haeKoulutukset(q);

        return new ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>(converter.fromKoulutuksetVastaus(r));
    }

    @Override
    public ResultV1RDTO<KoulutusLukioV1RDTO> postLukiokoulutus(KoulutusLukioV1RDTO koulutus) {
        LOG.info("postLukiokoulutus({})", koulutus);

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultV1RDTO<KoulutusAmmatillinenPeruskoulutusV1RDTO> postAmmatillinenPeruskoulutus(KoulutusAmmatillinenPeruskoulutusV1RDTO koulutus) {
        LOG.info("postAmmatillinenPeruskoulutus({})", koulutus);

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultV1RDTO<KoulutusPerusopetuksenLisaopetusV1RDTO> postPerusopetuksenLisaopetusKoulutus(KoulutusPerusopetuksenLisaopetusV1RDTO koulutus) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultV1RDTO<KoulutusValmentavaJaKuntouttavaV1RDTO> postValmentavaJaKuntouttavaKoulutus(KoulutusValmentavaJaKuntouttavaV1RDTO koulutus) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response deleteKuva(String oid, String kieliUri) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        Preconditions.checkNotNull(kieliUri, "Koodisto language URI cannot be null.");
        final BinaryData bin = koulutusmoduuliToteutusDAO.findKuvaByKomotoOidAndKieliUri(oid, kieliUri);
        if (bin != null) {
            final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(oid);
            Map<String, BinaryData> kuvat = komoto.getKuvat();
            kuvat.remove(kieliUri);
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

    @Override
    public Response saveKuva(String oid, String kieliUri, MultipartBody body) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        Preconditions.checkNotNull(kieliUri, "Koodisto language URI cannot be null.");
        Preconditions.checkNotNull(body, "MultipartBody cannot be null.");
        LOG.info("in saveKuva - komoto OID : {}, kieliUri : {}, bodyType : {}", oid, kieliUri, body.getType());
        LOG.info("Headers : {}", body.getRootAttachment().getHeaders());

        final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(oid);
        Attachment att = body.getRootAttachment();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        InputStream in = null;
        try {
            in = att.getDataHandler().getInputStream();

            try {
                IOUtils.copy(in, outputStream);

                BinaryData bin = null;
                if (komoto.isKuva(kieliUri)) {
                    bin = komoto.getKuvat().get(kieliUri);
                } else {
                    bin = new BinaryData();
                }

                bin.setData(outputStream.toByteArray());
                bin.setFilename(att.getContentDisposition().getParameter("filename"));
                bin.setMimeType(att.getDataHandler().getContentType());
                komoto.setKuvaByUri(kieliUri, bin);
                this.koulutusmoduuliToteutusDAO.update(komoto);
            } catch (IOException ex) {
                LOG.error("BinaryData save failed for komoto OID {}.", oid, ex);
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(outputStream);
            }
        } catch (IOException ex) {
            LOG.error("Image upload failed for komoto OID {}.", oid, ex);
        } finally {
            IOUtils.closeQuietly(in);
        }

        return Response.ok().build();
    }
}
