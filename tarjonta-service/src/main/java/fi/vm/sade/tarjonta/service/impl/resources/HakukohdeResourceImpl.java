package fi.vm.sade.tarjonta.service.impl.resources;

import java.util.*;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.service.resources.dto.*;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.service.types.SisaisetHakuAjat;
import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.resources.HakukohdeResource;
import fi.vm.sade.tarjonta.service.search.HakukohteetKysely;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.service.search.TarjontaSearchService;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;

import javax.annotation.Nullable;

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

        List<OidRDTO> result = HakuResourceImpl.convertOidList(hakukohdeDAO.findOIDsBy(tarjontaTila, count, startIndex, lastModifiedBefore, lastModifiedSince));
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

		try {
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
		} catch (RuntimeException e) {
			e.printStackTrace(System.err);
			throw e;
		}
    }

    // /hakukohde/OID
    @Override
    public HakukohdeDTO getByOID(String oid) {
        LOG.debug("/hakukohde/{} -- getByOID()", oid);

        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
        HakukohdeDTO result = conversionService.convert(hakukohde, HakukohdeDTO.class);
        LOG.debug("  result={}", result);
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
    public String createHakukohde(HakukohdeDTO hakukohdeDTO) {
        try {

            final String hakuOid = hakukohdeDTO.getHakuOid();
            Preconditions.checkNotNull(hakuOid, "Haku OID (HakukohteenHakuOid) cannot be null.");
            Hakukohde hakukohde = conversionService.convert(hakukohdeDTO,Hakukohde.class);

            Haku haku = hakuDAO.findByOid(hakuOid);
            //TODO: Add hakukohde koulutusalkamisaika validation
            Preconditions.checkNotNull(haku, "Insert failed - no haku entity found by haku OID", hakuOid);
            hakukohde.setHaku(haku);
            //TODO: Add haun sisaiset hakuajat
            hakukohde = hakukohdeDAO.insert(hakukohde);
            hakukohde.setKoulutusmoduuliToteutuses(findKoulutusModuuliToteutus(hakukohdeDTO.getHakukohdeKoulutusOids(),hakukohde));
            List<Valintakoe> valintakoes = getHakukohdeValintakoes(hakukohdeDTO.getValintakoes());
            if (valintakoes != null) {

                hakukohde.getValintakoes().addAll(valintakoes);

            }


            hakukohdeDAO.update(hakukohde);
            solrIndexer.indexHakukohteet(Lists.newArrayList(hakukohde.getId()));
            solrIndexer.indexKoulutukset(Lists.newArrayList(Iterators.transform(hakukohde.getKoulutusmoduuliToteutuses().iterator(), new Function<KoulutusmoduuliToteutus, Long>() {
                public Long apply(@Nullable KoulutusmoduuliToteutus arg0) {
                    return arg0.getId();
                }
            })));
            publication.sendEvent(hakukohde.getTila(), hakukohde.getOid(), PublicationDataService.DATA_TYPE_HAKUKOHDE, PublicationDataService.ACTION_INSERT);
            return hakukohde.getOid();

        } catch (Exception exp) {
           exp.printStackTrace();
            LOG.info("Exception creating hakukohde in Rest-service :  {}", exp.toString());
            return null;

        }

    }


    private List<Valintakoe> getHakukohdeValintakoes(List<ValintakoeRDTO> valintakoeRDTOs) {
        if (valintakoeRDTOs != null) {
          List<Valintakoe> valintakoes = new ArrayList<Valintakoe>();

            for (ValintakoeRDTO valintakoeRDTO:valintakoeRDTOs) {
                Valintakoe valintakoe = conversionService.convert(valintakoeRDTO,Valintakoe.class);
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
}
