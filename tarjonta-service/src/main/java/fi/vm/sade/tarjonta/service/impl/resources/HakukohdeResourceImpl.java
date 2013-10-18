package fi.vm.sade.tarjonta.service.impl.resources;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakuaika;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.HakukohdeLiite;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.service.resources.HakukohdeResource;
import fi.vm.sade.tarjonta.service.resources.dto.ErrorRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeHakutulosRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeLiiteDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeNimiRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakutuloksetRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ResultRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeRDTO;
import fi.vm.sade.tarjonta.service.search.HakukohteetKysely;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.service.search.KoulutuksetKysely;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;
import fi.vm.sade.tarjonta.service.search.TarjontaSearchService;
import fi.vm.sade.tarjonta.service.types.SisaisetHakuAjat;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

/**
 * REST API impl.
 *
 * @author mlyly
 * @see HakukohdeResource
 */
@Transactional(readOnly = true)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class HakukohdeResourceImpl implements HakukohdeResource {

    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeResourceImpl.class);
    @Autowired
    private HakuDAO hakuDAO;
    @Autowired
    private HakukohdeDAO hakukohdeDAO;
    @Autowired(required = true)
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;
    @Autowired
    private OrganisaatioService organisaatioService;
    @Autowired
    private TarjontaSearchService tarjontaSearchService;
    @Autowired(required = true)
    private PublicationDataService publication;
    @Autowired
    private IndexerResource solrIndexer;

    // /hakukohde?...
    @Override
    public List<OidRDTO> search(String searchTerms,
            int count,
            int startIndex,
            Date lastModifiedBefore,
            Date lastModifiedSince,
            List<String> organisationOids,
            List<String> hakukohdeTilas) {

        organisationOids = organisationOids != null ? organisationOids : new ArrayList<String>();
        hakukohdeTilas = hakukohdeTilas != null ? hakukohdeTilas : new ArrayList<String>();

        LOG.info("/hakukohde -- search({}, {}, {}, {}, {}, {}, {})",
                new Object[]{searchTerms, count, startIndex, lastModifiedBefore, lastModifiedSince, organisationOids, hakukohdeTilas});

        TarjontaTila tarjontaTila = null; // TarjontaTila.JULKAISTU;

        if (count <= 0) {
            count = 100;
            LOG.debug("  autolimit search to {} entries!", count);
        }

        List<OidRDTO> result = HakuResourceImpl.convertOidList(hakukohdeDAO.findOIDsBy(tarjontaTila != null ? tarjontaTila.asDto() : null, count, startIndex, lastModifiedBefore, lastModifiedSince));
        LOG.debug("  result={}", result);
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public HakutuloksetRDTO<HakukohdeHakutulosRDTO> search(
            String searchTerms,
            List<String> organisationOids,
            List<String> hakukohdeTilas,
            String alkamisKausi,
            Integer alkamisVuosi) {

        organisationOids = organisationOids != null ? organisationOids : new ArrayList<String>();
        hakukohdeTilas = hakukohdeTilas != null ? hakukohdeTilas : new ArrayList<String>();

        HakukohteetKysely q = new HakukohteetKysely();
        q.setNimi(searchTerms);
        q.setKoulutuksenAlkamiskausi(alkamisKausi);
        q.setKoulutuksenAlkamisvuosi(alkamisVuosi);
        q.getTarjoajaOids().addAll(organisationOids);

        for (String s : hakukohdeTilas) {
            q.getTilat().add(fi.vm.sade.tarjonta.shared.types.TarjontaTila.valueOf(s));
        }

        HakukohteetVastaus r = tarjontaSearchService.haeHakukohteet(q);

        return (HakutuloksetRDTO<HakukohdeHakutulosRDTO>) conversionService.convert(r, HakutuloksetRDTO.class);
    }

    // /hakukohde/OID
    @Override
    public ResultRDTO<HakukohdeDTO> getByOID(String oid) {
        LOG.debug("/hakukohde/{} -- getByOID()", oid);

        ResultRDTO<HakukohdeDTO> result = new ResultRDTO<HakukohdeDTO>();

        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
        HakukohdeDTO hakukohdeDto = conversionService.convert(hakukohde, HakukohdeDTO.class);
        LOG.debug("  result={}", hakukohdeDto);

        result.setResult(hakukohdeDto);
        return result;
    }

    // /hakukohde/OID/haku
    @Override
    public HakuDTO getHakuByHakukohdeOID(String oid) {
        LOG.debug("/hakukohde/{}/haku -- getHakuByHakukohdeOID()", oid);

        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
        HakuDTO result = conversionService.convert(hakukohde.getHaku(), HakuDTO.class);
        LOG.debug("  result={}", result);
        return result;
    }

    @Override
    public HakukohdeRDTO findByOid(String oid) {

        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);

        HakukohdeRDTO hakukohdeRDTO = conversionService.convert(hakukohde, HakukohdeRDTO.class);

        return hakukohdeRDTO;

    }

    // /hakukohde/OID/komoto
    @Override
    public List<OidRDTO> getKomotosByHakukohdeOID(String oid) {
        LOG.debug("/hakukohde/{}/komoto -- getKomotosByHakukohdeOID()", oid);

        List<OidRDTO> result = new ArrayList<OidRDTO>();

        // TODO fixme to be more efficient! Add a custom finder for this
        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
        for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : hakukohde.getKoulutusmoduuliToteutuses()) {
            result.add(new OidRDTO(koulutusmoduuliToteutus.getOid()));
        }
        LOG.debug("  result={}", result);
        return result;
    }

    // GET /hakukohde/{oid}/paasykoe
    @Override
    public List<String> getPaasykoesByHakukohdeOID(String oid) {
        LOG.debug("/hakukohde/{}/paasykoe -- getPaasykoesByHakukohdeOID()", oid);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // GET /hakukohde/{oid}/valintakoe
    @Override
    public List<String> getValintakoesByHakukohdeOID(String oid) {
        LOG.debug("/hakukohde/{}/valintakoe -- getValintakoesByHakukohdeOID()", oid);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultRDTO<String> updateHakukohde(ResultRDTO<HakukohdeDTO> hakukohdeDTOResult) {
        ResultRDTO<String> result = new ResultRDTO<String>();

        try {
            HakukohdeDTO hakukohdeDTO = hakukohdeDTOResult.getResult();
            Hakukohde hakukohde = conversionService.convert(hakukohdeDTO, Hakukohde.class);

            Hakukohde tempHakukohde = hakukohdeDAO.findHakukohdeByOid(hakukohde.getOid());
            hakukohde.setId(tempHakukohde.getId());
            hakukohde.setVersion(tempHakukohde.getVersion());

            Haku haku = hakuDAO.findByOid(hakukohdeDTO.getHakuOid());
            hakukohde.setHaku(haku);
            hakukohde.setKoulutusmoduuliToteutuses(findKoulutusModuuliToteutus(hakukohdeDTO.getHakukohdeKoulutusOids(), hakukohde));


            List<Valintakoe> valintakoes = getHakukohdeValintakoes(hakukohdeDTO.getValintakoes());
            if (valintakoes != null) {

                hakukohde.getValintakoes().addAll(valintakoes);

            }
            List<HakukohdeLiite> hakukohdeLiites = getHakukohdeLiites(hakukohdeDTO.getLiitteet(), hakukohde);

            if (hakukohdeLiites != null) {
                hakukohde.getLiites().addAll(hakukohdeLiites);
            }

            hakukohdeDAO.update(hakukohde);

            solrIndexer.indexHakukohteet(Lists.newArrayList(hakukohde.getId()));
            publication.sendEvent(hakukohde.getTila(), hakukohde.getOid(), PublicationDataService.DATA_TYPE_HAKUKOHDE, PublicationDataService.ACTION_UPDATE);

            result.setResult(hakukohde.getOid());

        } catch (Exception exp) {
            LOG.error("Exception updating hakukohde", exp);

            result.setStatus(ResultRDTO.ResultStatus.ERROR);
            result.addError(ErrorRDTO.createSystemError(exp, "update.failed"));
        }

        return result;
    }

    @Override
    public void deleteHakukohde(String hakukohdeOid) {
        try {
            Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(hakukohdeOid);
            if (hakukohde.getKoulutusmoduuliToteutuses() != null) {
                for (KoulutusmoduuliToteutus koulutus : hakukohde.getKoulutusmoduuliToteutuses()) {
                    koulutus.removeHakukohde(hakukohde);
                }
            }

            hakukohdeDAO.remove(hakukohde);
            solrIndexer.deleteHakukohde(Lists.newArrayList(hakukohde.getOid()));
        } catch (Exception exp) {
            LOG.warn("Exception occured when removing hakukohde {}, exception : {}", hakukohdeOid, exp.toString());

        }
    }

    @Override
    public HakukohdeRDTO updateUiHakukohde(HakukohdeRDTO hakukohdeRDTO) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HakukohdeRDTO insertHakukohde(HakukohdeRDTO hakukohdeRDTO) {

        String hakuOid = hakukohdeRDTO.getHakuOid();
        Preconditions.checkNotNull(hakuOid, "Haku OID (HakukohteenHakuOid) cannot be null.");
        hakukohdeRDTO.setOid(null);
        Hakukohde hakukohde = conversionService.convert(hakukohdeRDTO, Hakukohde.class);

        LOG.debug("INSERT HAKUKOHDE OID : ", hakukohde.getOid());

        Haku haku = hakuDAO.findByOid(hakuOid);
        hakukohde.setHaku(haku);

        hakukohde = hakukohdeDAO.insert(hakukohde);

        hakukohde.setKoulutusmoduuliToteutuses(findKoulutusModuuliToteutus(hakukohdeRDTO.getHakukohdeKoulutusOids(), hakukohde));

        //TODO, add valintakokees and liittees etc.

        hakukohdeDAO.update(hakukohde);

        solrIndexer.indexHakukohteet(Lists.newArrayList(hakukohde.getId()));
        solrIndexer.indexKoulutukset(Lists.newArrayList(Iterators.transform(hakukohde.getKoulutusmoduuliToteutuses().iterator(), new Function<KoulutusmoduuliToteutus, Long>() {
            public Long apply(@Nullable KoulutusmoduuliToteutus arg0) {
                return arg0.getId();
            }
        })));
        publication.sendEvent(hakukohde.getTila(), hakukohde.getOid(), PublicationDataService.DATA_TYPE_HAKUKOHDE, PublicationDataService.ACTION_INSERT);

        hakukohdeRDTO.setOid(hakukohde.getOid());
        return hakukohdeRDTO;
    }

    private List<HakukohdeLiite> getHakukohdeLiites(List<HakukohdeLiiteDTO> hakukohdeLiiteDTOs, Hakukohde hakukohde) {
        if (hakukohdeLiiteDTOs != null) {

            List<HakukohdeLiite> hakukohdeLiites = new ArrayList<HakukohdeLiite>();

            for (HakukohdeLiiteDTO hakukohdeLiiteDTO : hakukohdeLiiteDTOs) {
                HakukohdeLiite hakukohdeLiite = conversionService.convert(hakukohdeLiiteDTO, HakukohdeLiite.class);
                hakukohdeLiite.setHakukohde(hakukohde);
                hakukohdeLiites.add(hakukohdeLiite);
            }

            return hakukohdeLiites;

        } else {
            return null;
        }
    }

    private List<Valintakoe> getHakukohdeValintakoes(List<ValintakoeRDTO> valintakoeRDTOs) {
        if (valintakoeRDTOs != null) {
            List<Valintakoe> valintakoes = new ArrayList<Valintakoe>();

            for (ValintakoeRDTO valintakoeRDTO : valintakoeRDTOs) {
                Valintakoe valintakoe = conversionService.convert(valintakoeRDTO, Valintakoe.class);
                valintakoes.add(valintakoe);
            }

            return valintakoes;
        } else {
            return null;
        }
    }

    private Hakuaika findHakuaika(Haku hk, SisaisetHakuAjat ha) {
        if (hk.getHakuaikas().size() == 1) {
            return hk.getHakuaikas().iterator().next();
        }
        if (ha != null && ha.getOid() != null) {
            long id = Long.parseLong(ha.getOid());
            for (Hakuaika hka : hk.getHakuaikas()) {
                if (hka.getId() == id) {
                    return hka;
                }
            }
        }
        return null;
    }

    private Set<KoulutusmoduuliToteutus> findKoulutusModuuliToteutus(List<String> komotoOids, Hakukohde hakukohde) {
        Set<KoulutusmoduuliToteutus> komotos = new HashSet<KoulutusmoduuliToteutus>();

        for (String komotoOid : komotoOids) {
            KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(komotoOid);
            komoto.addHakukohde(hakukohde);
            komotos.add(komoto);
        }

        return komotos;
    }

    // GET /hakukohde/{oid}/nimi
    @Override
    public HakukohdeNimiRDTO getHakukohdeNimi(String oid) {
        LOG.info("getHakukohdeNimi({})", oid);

        HakukohdeNimiRDTO result = new HakukohdeNimiRDTO();

        // TODO fixme to be more efficient! Add a custom finder for this
        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
        if (hakukohde == null) {
            return result;
        }

        //
        // Get hakukohde name
        //

        // Get multilingual name for koodisto "hakukohde" (application option?)
        result.setHakukohdeNimi(tarjontaKoodistoHelper.getKoodiMetadataNimi(hakukohde.getHakukohdeNimi()));
        result.setHakukohdeOid(oid);
        result.setHakukohdeTila(hakukohde.getTila() != null ? hakukohde.getTila().name() : null);

        //
        // Haku/koulutus year and term
        //
        Haku haku = hakukohde.getHaku();
        if (haku != null) {
            result.setHakuVuosi(haku.getHakukausiVuosi());
            result.setHakuKausi(tarjontaKoodistoHelper.getKoodiMetadataNimi(haku.getHakukausiUri()));
        } else {
            result.setHakuVuosi(-1);
            result.setHakuKausi(null);
        }

        //
        // Get organisaatio name
        //
        String organisaatioOid = null;

        for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : hakukohde.getKoulutusmoduuliToteutuses()) {
            if (koulutusmoduuliToteutus.getTarjoaja() != null) {
                // Assumes that only one provider for koulutus - is this true?
                organisaatioOid = koulutusmoduuliToteutus.getTarjoaja();

                // Also assume that kausi and vuosi is the same also
                result.setKoulutusKausi(tarjontaKoodistoHelper.getKoodiMetadataNimi(resolveDateToKausiUri(koulutusmoduuliToteutus.getKoulutuksenAlkamisPvm())));
                result.setKoulutusVuosi(resolveDateToYear(koulutusmoduuliToteutus.getKoulutuksenAlkamisPvm()));

                // assume it is the same for all komotos
                break;
            }
        }

        if (organisaatioOid != null) {
            result.setTarjoajaOid(organisaatioOid);
            OrganisaatioDTO organisaatio = organisaatioService.findByOid(organisaatioOid);
            if (organisaatio != null) {
                Map<String, String> map = new HashMap<String, String>();
                for (MonikielinenTekstiTyyppi.Teksti teksti : organisaatio.getNimi().getTeksti()) {
                    map.put(tarjontaKoodistoHelper.convertKielikoodiToKieliUri(teksti.getKieliKoodi()), teksti.getValue());
                }
                result.setTarjoajaNimi(map);
            }
        }

        LOG.info("  --> result = {}", result);

        return result;
    }

    private String resolveDateToKausiUri(final Date d) {
        if (d == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(d);

        // FIXME hardcoded kausi uris
        // TODO check logic for kausi uris, hardcoded!
        if (cal.get(Calendar.MONTH) < 6) {
            return "kausi_k";
        } else {
            return "kausi_s";
        }
    }

    private int resolveDateToYear(final Date d) {
        if (d == null) {
            return -1;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.YEAR);
    }

    @Override
    @Transactional(readOnly = false)
    public ResultRDTO<String>  updateTila(String oid, TarjontaTila tila) {
        LOG.info("updateTila({}, {})", oid, tila);

        ResultRDTO<String> result = new ResultRDTO<String>();

        Hakukohde hk = hakukohdeDAO.findHakukohdeByOid(oid);
        Preconditions.checkArgument(hk != null, "Hakukohdetta ei löytynyt: %s", oid);
        if (!hk.getTila().acceptsTransitionTo(tila)) {
            result.setResult(hk.getTila().toString());
            return result;
        }
        hk.setTila(tila);
        hakukohdeDAO.update(hk);
        solrIndexer.indexHakukohteet(Collections.singletonList(hk.getId()));

        result.setResult(tila.toString());

        return result;
    }

    @Override
    public ResultRDTO<List<NimiJaOidRDTO>> getKoulutukset(String oid) {
        LOG.info("getKoulutukset({})", oid);

        ResultRDTO<List<NimiJaOidRDTO>> result = new ResultRDTO<List<NimiJaOidRDTO>>();

        KoulutuksetKysely ks = new KoulutuksetKysely();
        ks.getHakukohdeOids().add(oid);

        KoulutuksetVastaus kv = tarjontaSearchService.haeKoulutukset(ks);
        List<NimiJaOidRDTO> ret = new ArrayList<NimiJaOidRDTO>();
        for (KoulutusPerustieto kp : kv.getKoulutukset()) {
            ret.add(new NimiJaOidRDTO(kp.getNimi(), kp.getKomotoOid()));
        }

        result.setResult(ret);

        return result;

    }
}
