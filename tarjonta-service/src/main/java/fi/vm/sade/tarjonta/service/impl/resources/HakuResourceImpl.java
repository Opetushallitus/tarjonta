package fi.vm.sade.tarjonta.service.impl.resources;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

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
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.HakuResource;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeNimiRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeTulosRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;

/**
 * Run:
 * 
 * <pre>
 * mvn -Dlog4j.configuration=file:`pwd`/src/test/resources/log4j.properties  jetty:run
 * </pre>
 * 
 * Test:
 * 
 * <pre>
 * http://localhost:8084/tarjonta-service/rest?_wadl
 * </pre>
 * 
 * Internal documentation:
 * http://liitu.hard.ware.fi/confluence/display/PROG/Tarjonnan+REST+palvelut
 * 
 * @author mlyly
 * @see HakuResource
 */
// @Path("/haku")
@Transactional(readOnly = true)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class HakuResourceImpl implements HakuResource {

    private static final Logger LOG = LoggerFactory.getLogger(HakuResourceImpl.class);
    @Autowired
    private HakuDAO hakuDAO;
    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;
    @Autowired
    private OrganisaatioService organisaatioService;
    @Autowired
    private HakukohdeDAO hakukohdeDAO;
    @Autowired(required = true)
    private ConversionService conversionService;

    public static List<OidRDTO> convertOidList(List<String> oids) {
        List<OidRDTO> result = new ArrayList<OidRDTO>();
        for (String oid : oids) {
            result.add(new OidRDTO(oid));
        }
        return result;
    }

    // /haku/hello
    @Override
    public String hello() {
        LOG.info("/haku/hello -- hello()");
        return "Well Hello! " + new Date();
    }

    // /haku?...
    @Override
    public List<OidRDTO> search(String searchTerms, int count, int startIndex, Date lastModifiedBefore,
            Date lastModifiedSince) {
        LOG.debug("/haku -- search({}, {}, {}, {}, {})", new Object[] { searchTerms, count, startIndex,
                lastModifiedBefore, lastModifiedSince });

        TarjontaTila tarjontaTila = null; // TarjontaTila.JULKAISTU;

        if (count <= 0) {
            count = 100;
            LOG.debug("  autolimit search to {} entries!", count);
        }

        List<OidRDTO> result = convertOidList(hakuDAO.findOIDsBy(tarjontaTila, count, startIndex, lastModifiedBefore,
                lastModifiedSince));
        LOG.debug("  result={}", result);
        return result;
    }

    // /haku/OID
    @Override
    public HakuDTO getByOID(String oid) {
        LOG.debug("/haku/{} -- getByOID()", oid);

        HakuDTO result = conversionService.convert(hakuDAO.findByOid(oid), HakuDTO.class);
        LOG.debug("  result={}", result);
        return result;
    }

    // /haku/OID/hakukohde
    @Override
    public List<OidRDTO> getByOIDHakukohde(String oid, String searchTerms, int count, int startIndex,
            Date lastModifiedBefore, Date lastModifiedSince) {
        LOG.debug("/haku/{}/hakukohde -- getByOIDHakukohde()", oid);

        if (count <= 0) {
            count = 100;
            LOG.debug("  autolimit search to {} entries!", count);
        }

        List<OidRDTO> result = convertOidList(hakukohdeDAO.findByHakuOid(oid, searchTerms, count, startIndex,
                lastModifiedBefore, lastModifiedSince));
        LOG.debug("  result={}", result);
        return result;
    }

    // /haku/OID/hakukohdetulos
    @Override
    public HakukohdeTulosRDTO getByOIDHakukohdeTulos(@PathParam("oid") String oid,
            @QueryParam("searchTerms") String searchTerms, @QueryParam("count") int count,
            @QueryParam("startIndex") int startIndex, @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
            @QueryParam("lastModifiedSince") Date lastModifiedSince) {
        LOG.debug("/haku/{}/hakukohdetulos -- getByOIDHakukohdeTulos()", oid);

        if (count <= 0) {
            count = 100;
            LOG.debug("  autolimit search to {} entries!", count);
        }

        // Get the total size (give count < 0 -- > no limits)
        List<String> oids = hakukohdeDAO.findByHakuOid(oid, searchTerms, -1, 0, lastModifiedBefore, lastModifiedSince);
        int totalSize = oids.size();
        List<HakukohdeNimiRDTO> result = new ArrayList<HakukohdeNimiRDTO>();
        for (String hakukohdeoid : hakukohdeDAO.findByHakuOid(oid, searchTerms, count, startIndex, lastModifiedBefore,
                lastModifiedSince)) {
            result.add(getHakukohdeNimi(hakukohdeoid));
        }
        LOG.debug("  result={}, result count {}, total count {}", new Object[] { result, result.size(), totalSize });
        return new HakukohdeTulosRDTO(totalSize, result);
    }

    // /haku/OID/hakukohdeWithName
    @Override
    public List<Map<String, String>> getByOIDHakukohdeExtra(String oid, String searchTerms, int count, int startIndex,
            Date lastModifiedBefore, Date lastModifiedSince) {
        LOG.debug("/haku/{}/hakukohdeWithName -- getByOIDHakukohdeExtra()", oid);

        List<Map<String, String>> result = new ArrayList<Map<String, String>>();

        if (count <= 0) {
            count = 100;
            LOG.debug("  autolimit search to {} entries!", count);
        }

        // Get list of oids
        List<OidRDTO> hakukohdeOids = getByOIDHakukohde(oid, searchTerms, count, startIndex, lastModifiedBefore,
                lastModifiedSince);

        // Loop the result
        for (OidRDTO oidRDTO : hakukohdeOids) {
            String hakukohdeOid = oidRDTO.getOid();

            Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(hakukohdeOid);

            Map<String, String> row = new HashMap<String, String>();

            row.put("oid", hakukohde.getOid());
            row.put("nimiUri", hakukohde.getHakukohdeNimi());

            // TODO resolve nimi uri :)

            result.add(row);
        }

        return result;
    }

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
                result.setKoulutusKausi(tarjontaKoodistoHelper
                        .getKoodiMetadataNimi(resolveDateToKausiUri(koulutusmoduuliToteutus.getKoulutuksenAlkamisPvm())));
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
                    map.put(tarjontaKoodistoHelper.convertKielikoodiToKieliUri(teksti.getKieliKoodi()),
                            teksti.getValue());
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
