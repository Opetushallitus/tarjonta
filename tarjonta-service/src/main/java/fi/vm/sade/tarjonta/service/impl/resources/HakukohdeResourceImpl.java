package fi.vm.sade.tarjonta.service.impl.resources;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.service.resources.HakukohdeResource;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeNimiRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoePisterajaRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeRDTO;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.service.search.TarjontaSearchService;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import static org.apache.commons.lang.StringUtils.isEmpty;

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

        LOG.debug("/hakukohde -- search({}, {}, {}, {}, {}, {}, {})",
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
    public List<ValintakoeRDTO> getValintakoesByHakukohdeOID(String oid) {
        LOG.debug("/hakukohde/{}/valintakoe -- getValintakoesByHakukohdeOID()", oid);
        return getValintakoeFixedByHakukohdeOID(oid);
    }

    // GET /hakukohde/{oid}/nimi
    @Override
    public HakukohdeNimiRDTO getHakukohdeNimi(String oid) {
        LOG.debug("getHakukohdeNimi({})", oid);

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

        LOG.debug("  --> result = {}", result);

        return result;
    }

    // -----------------------------------------------------------------------
    // Private helpers

    // KJOH-669
    private List<ValintakoeRDTO> getValintakoeFixedByHakukohdeOID(String oid) {
        LOG.debug("getValintakoeFixedByHakukohdeOID({})", oid);

        List<ValintakoeRDTO> result = new ArrayList<ValintakoeRDTO>();

        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
        if (hakukohde == null) {
            LOG.info("  Hakukohde with oid {} was not found.");
            return result;
        }

        Set<Valintakoe> valintakoes = hakukohde.getValintakoes();
        for (Valintakoe valintakoe : valintakoes) {
            LOG.debug("  process: vk.id = {}", valintakoe.getId());

            ValintakoeRDTO tmp = conversionService.convert(valintakoe, ValintakoeRDTO.class);

            if (isEmpty(valintakoe.getTyyppiUri())) {
                LOG.debug("  EMPTY getTyyppiUri - ie. Lukio valintakoe, {}", tmp);

                //
                // TODO / NOTE / ALERT: in Lukio the valintakoes were mistakenly compressed to
                // single valintakoe, should have two instances...
                // Reeeally ugly, hardcoded fix here... sorry, sorry sorry.
                //
                ValintakoeRDTO vk = new ValintakoeRDTO();
                ValintakoeRDTO lt = new ValintakoeRDTO();

                //
                // Valintakoe / selection examination
                //
                vk.setCreated(tmp.getCreated());
                vk.setCreatedBy(tmp.getCreatedBy());
                vk.setKuvaus(tmp.getKuvaus());
                vk.setModified(tmp.getModified());
                vk.setModifiedBy(tmp.getModifiedBy());
                vk.setOid(tmp.getOid() + ".1");
                vk.setTyyppiUri("valintakokeentyyppi_1#1");
                vk.setValintakoeAjankohtas(tmp.getValintakoeAjankohtas());
                vk.setValintakoeId(tmp.getValintakoeId());
                vk.setVersion(tmp.getVersion());

                //
                // Lisänäytöt / additional test?
                //
                lt.setCreated(tmp.getCreated());
                lt.setCreatedBy(tmp.getCreatedBy());
                lt.setKuvaus(tmp.getLisanaytot());
                lt.setModified(tmp.getModified());
                lt.setModifiedBy(tmp.getModifiedBy());
                lt.setOid(tmp.getOid() + ".2");
                lt.setTyyppiUri("valintakokeentyyppi_2#1");
                lt.setVersion(tmp.getVersion());

                //
                // Points
                //
                for (ValintakoePisterajaRDTO pisteraja : tmp.getValintakoePisterajas()) {
                    // TODO hardocded... :(
                    if ("Paasykoe".equals(pisteraja.getTyyppi())) {
                        if (vk.getValintakoePisterajas() == null) {
                            vk.setValintakoePisterajas(new ArrayList<ValintakoePisterajaRDTO>());
                        }
                        vk.getValintakoePisterajas().add(pisteraja);
                    }
                    if ("Lisapisteet".equals(pisteraja.getTyyppi())) {
                        if (lt.getValintakoePisterajas() == null) {
                            lt.setValintakoePisterajas(new ArrayList<ValintakoePisterajaRDTO>());
                        }
                        lt.getValintakoePisterajas().add(pisteraja);
                    }
                }

                result.add(vk);
                result.add(lt);

                // TODO remove me when this works!
                // result.add(tmp);
            } else {
                // Normal and default case.
                result.add(tmp);
            }
        }

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
