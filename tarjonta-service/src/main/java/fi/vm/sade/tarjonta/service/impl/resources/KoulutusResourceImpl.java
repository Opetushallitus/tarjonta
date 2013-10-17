/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.service.impl.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

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
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.business.exception.KoulutusUsedException;
import fi.vm.sade.tarjonta.service.business.exception.TarjontaBusinessException;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.CommonRestKoulutusConverters;
import fi.vm.sade.tarjonta.service.resources.KoulutusResource;
import fi.vm.sade.tarjonta.service.resources.dto.HakutuloksetRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KoulutusHakutulosRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.KorkeakouluDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.ResultDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.TekstiDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.ToteutusDTO;
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
import java.io.InputStream;
import javax.ws.rs.core.Response;

/**
 *
 * @author Jani Wilén
 */
@Transactional(readOnly = false)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class KoulutusResourceImpl implements KoulutusResource {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusResourceImpl.class);
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

    @Override
    public String help() {
        return "/help (this REST resource help text)\n"
                + "/toteutus/<KOMO-OID>/\n"
                + "/tekstis?lang=<koodi-kieli-URI>\n"
                + "/tekstis/<KOMO-OID>\n";
    }

    @Override
    public ToteutusDTO getKoulutusRelation(String koulutuskoodi) {
        Preconditions.checkNotNull(koulutuskoodi, "Koulutuskoodi parameter cannot be null.");

        if (koulutuskoodi.contains("_")) {
            //simple paramter check if data is koodisto service koodi URI.
            return koulutuskoodiRelations.getKomoRelationByKoulutuskoodiUri(koulutuskoodi, new Locale("FI"));
        } else {
            SearchKoodisByKoodistoCriteriaType search = KoodiServiceSearchCriteriaBuilder.koodisByArvoAndKoodistoUri(koulutuskoodi, KoodistoURI.KOODISTO_TUTKINTO_URI);
            List<KoodiType> searchKoodisByKoodisto = koodiService.searchKoodisByKoodisto(search);
            if (searchKoodisByKoodisto == null || searchKoodisByKoodisto.isEmpty()) {
                throw new TarjontaBusinessException("No koulutuskoodi koodisto KoodiType object found by '" + koulutuskoodi + "'.");
            }
            return koulutuskoodiRelations.getKomoRelationByKoulutuskoodiUri(searchKoodisByKoodisto.get(0).getKoodiUri(), new Locale("FI"));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public HakutuloksetRDTO<KoulutusHakutulosRDTO> searchInfo(
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
        q.setKoulutuksenTila(hakukohdeTila == null ? null : TarjontaTila.valueOf(hakukohdeTila).asDto());

        KoulutuksetVastaus r = tarjontaSearchService.haeKoulutukset(q);

        return (HakutuloksetRDTO<KoulutusHakutulosRDTO>) conversionService.convert(r, HakutuloksetRDTO.class);
    }

    @Override
    public ToteutusDTO getToteutus(final String komotoOid) {
        Preconditions.checkNotNull(komotoOid, "KOMOTO OID cannot be null.");
        LOG.info("OID : {}", komotoOid);

        final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(komotoOid);
        return conversionService.convert(komoto, KorkeakouluDTO.class);
    }

    @Override
    public ResultDTO updateToteutus(KorkeakouluDTO dto) {
        // permissionChecker.checkCreateKoulutus(koulutus.getTarjoaja());
        validateRestObjectKorkeakouluDTO(dto);

        Preconditions.checkNotNull(dto.getOid(), "KOMOTO OID cannot be null.");
        final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(dto.getOid());
        Preconditions.checkNotNull(komoto, "KOMOTO not found by OID : %s.", dto.getOid());

        final KoulutusmoduuliToteutus updatedFullKomoto = conversionService.convert(dto, KoulutusmoduuliToteutus.class);
        return new ResultDTO(updatedFullKomoto.getOid(), updatedFullKomoto.getVersion());
    }

    @Override
    public ResultDTO createToteutus(KorkeakouluDTO dto) {
        // permissionChecker.checkCreateKoulutus(koulutus.getTarjoaja());
        validateRestObjectKorkeakouluDTO(dto);

        Preconditions.checkNotNull(dto.getOid() != null, "External KOMOTO OID not allowed. OID : %s.", dto.getOid());
        Preconditions.checkNotNull(dto.getKomoOid() != null, "External KOMO OID not allowed. OID : %s.", dto.getKomoOid());

        final KoulutusmoduuliToteutus newKomo = conversionService.convert(dto, KoulutusmoduuliToteutus.class);
        Preconditions.checkNotNull(newKomo, "KOMOTO conversion to database object failed : object : %s.", ReflectionToStringBuilder.toString(dto));
        Preconditions.checkNotNull(newKomo.getKoulutusmoduuli(), "KOMO conversion to database object failed : object :  %s.", ReflectionToStringBuilder.toString(dto));

        koulutusmoduuliDAO.insert(newKomo.getKoulutusmoduuli());
        final KoulutusmoduuliToteutus response = koulutusmoduuliToteutusDAO.insert(newKomo);
        solrIndexer.indexKoulutukset(Lists.newArrayList(response.getId()));
        // publication.sendEvent(response.getTila(), response.getOid(), PublicationDataService.DATA_TYPE_KOMOTO, PublicationDataService.ACTION_INSERT);

        return new ResultDTO(response.getOid(), response.getVersion());
    }

    @Override
    public Response deleteToteutus(String koulutusOid) {
        //permissionChecker.checkRemoveKoulutus(koulutusOid);
        KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findByOid(koulutusOid);

        if (komoto.getHakukohdes().isEmpty()) {
            this.koulutusmoduuliToteutusDAO.remove(komoto);
            try {
                solrIndexer.deleteKoulutus(Lists.newArrayList(koulutusOid));

            } catch (IOException e) {
                throw new TarjontaBusinessException("indexing.error", e);
            }

        } else {
            throw new KoulutusUsedException();
        }
        return Response.ok().build();
    }

    @Override
    @Transactional(readOnly = false)
    public String updateTila(String oid, TarjontaTila tila) {
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        Preconditions.checkArgument(komoto != null, "Koulutusta ei löytynyt: %s", oid);
        if (!komoto.getTila().acceptsTransitionTo(tila)) {
            return komoto.getTila().toString();
        }
        komoto.setTila(tila);
        koulutusmoduuliToteutusDAO.update(komoto);
        solrIndexer.indexKoulutukset(Collections.singletonList(komoto.getId()));
        return tila.toString();
    }

    private void validateRestObjectKorkeakouluDTO(final KorkeakouluDTO dto) {
        Preconditions.checkNotNull(dto, "An invalid data exception - KorkeakouluDTO object cannot be null.");
        Preconditions.checkNotNull(dto.getKoulutusasteTyyppi(), "KoulutusasteTyyppi enum cannot be null.");
        Preconditions.checkNotNull(dto.getKoulutusmoduuliTyyppi(), "KoulutusmoduuliTyyppi enum cannot be null.");
        Preconditions.checkNotNull(dto.getTila(), "Tila enum cannot be null.");
        Preconditions.checkNotNull(dto.getOrganisaatio() == null || dto.getOrganisaatio().getOid() == null, "Organisation OID was missing.");
        final OrganisaatioDTO org = organisaatioService.findByOid(dto.getOrganisaatio().getOid());
        Preconditions.checkNotNull(org, "No organisation found by OID : %s.", dto.getOrganisaatio().getOid());
    }

    @Override
    public List<NimiJaOidRDTO> getHakukohteet(String oid) {
        HakukohteetKysely ks = new HakukohteetKysely();
        ks.getKoulutusOids().add(oid);

        HakukohteetVastaus vs = tarjontaSearchService.haeHakukohteet(ks);
        List<NimiJaOidRDTO> ret = new ArrayList<NimiJaOidRDTO>();
        for (HakukohdePerustieto hk : vs.getHakukohteet()) {
            ret.add(new NimiJaOidRDTO(hk.getNimi(), hk.getOid()));
        }
        return ret;
    }

    @Override
    public Response saveKuva(String oid, InputStream in, String fileType, long fileSize, String kieliUri) throws IOException {
        LOG.info("SAVE IMAGE {}", oid);
        return Response.ok().build();
    }

    @Override
    public Response deleteTeksti(String oid, String key, String uri) {
        return Response.ok().build();
    }

    @Override
    public Response deleteKuva(String oid, String kieliUri) {
        return Response.ok().build();
    }

    @Override
    public TekstiDTO loadTekstis(String oid) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        Preconditions.checkNotNull(komoto, "KOMOTO not found by OID '%s'.", oid);

        TekstiDTO komotoTekstiDto = komotoKoulutusConverters.convertMonikielinenTekstiToTekstiDTO(komoto.getTekstit());
        Koulutusmoduuli komo = komoto.getKoulutusmoduuli();
        TekstiDTO<KomoTeksti> komoTekstiDto = komoKoulutusConverters.convertMonikielinenTekstiToTekstiDTO(komo.getTekstit());
        komotoTekstiDto.getTekstis().putAll(komoTekstiDto.getTekstis());

        //combine komo&komoto text data to the dto;
        return komotoTekstiDto;
    }

    @Override
    public TekstiDTO loadKomotoTekstis(String oid) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        Preconditions.checkNotNull(komoto, "KOMOTO not found by OID '%s'.", oid);
        return komotoKoulutusConverters.convertMonikielinenTekstiToTekstiDTO(komoto.getTekstit());
    }

    @Override
    public Response saveKomotoTekstis(String oid, TekstiDTO<KomotoTeksti> dto) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        Preconditions.checkNotNull(komoto, "KOMOTO not found by OID '%s'.", oid);
        komotoKoulutusConverters.convertTekstiDTOToMonikielinenTeksti(dto, komoto.getTekstit());
        return Response.ok().build();
    }

    @Override
    public TekstiDTO loadKomoTekstis(String oid) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        Preconditions.checkNotNull(komoto, "KOMOTO not found by OID '%s'.", oid);
        return komoKoulutusConverters.convertMonikielinenTekstiToTekstiDTO(komoto.getKoulutusmoduuli().getTekstit());
    }

    @Override
    public Response saveKomoTekstis(String oid, TekstiDTO<KomoTeksti> dto) {
        Preconditions.checkNotNull(oid, "KOMOTO OID cannot be null.");
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        Preconditions.checkNotNull(komoto, "KOMOTO not found by OID '%s'.", oid);
        Koulutusmoduuli komo = komoto.getKoulutusmoduuli();
        komoKoulutusConverters.convertTekstiDTOToMonikielinenTeksti(dto, komo.getTekstit());    
        koulutusmoduuliDAO.update(komo);

        return Response.ok().build();
    }

}
