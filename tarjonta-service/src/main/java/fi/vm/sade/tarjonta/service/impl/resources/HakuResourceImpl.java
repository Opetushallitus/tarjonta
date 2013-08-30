package fi.vm.sade.tarjonta.service.impl.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.service.resources.HakuResource;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeNimiRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeTulosRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.search.HakukohdeListaus;
import fi.vm.sade.tarjonta.service.search.HakukohteetKysely;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.search.TarjontaSearchService;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
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
    private TarjontaSearchService tarjontaSearchService;
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
    public HakukohdeTulosRDTO getByOIDHakukohdeTulos(String oid, String searchTerms, int count, int startIndex,
            Date lastModifiedBefore, Date lastModifiedSince) {
        final String filtterointiAvain = "fi"; // TODO: <- rajapintaan
        final String filtterointiTeksti = StringUtils.capitalize(StringUtils.trimToEmpty(searchTerms));
        LOG.debug("/haku/{}/hakukohdetulos -- getByOIDHakukohdeTulos()", oid);

        if (count <= 0) {
            count = 100;
            LOG.debug("  autolimit search to {} entries!", count);
        }
        HakukohteetKysely hakukohteetKysely = new HakukohteetKysely();
        hakukohteetKysely.setHakuOid(oid);
        HakukohteetVastaus v = tarjontaSearchService.haeHakukohteet(hakukohteetKysely);
        LOG.debug("  kysely for haku '{}' found '{}'", new Object[] { oid, v.getHakukohdeTulos().size() });
        Collection<HakukohdeTulos> tulokset = v.getHakukohdeTulos();
        // filtteroi tarvittaessa tulokset joko tarjoaja- tai hakukohdenimen
        // mukaan!
        if (!filtterointiTeksti.isEmpty()) {
            tulokset = Collections2.filter(tulokset, new Predicate<HakukohdeTulos>() {
                private String haeTekstiAvaimella(MonikielinenTekstiTyyppi tekstit) {
                    for (MonikielinenTekstiTyyppi.Teksti teksti : tekstit.getTeksti()) {
                        if (filtterointiAvain.equals(teksti.getKieliKoodi())) {
                            return StringUtils.capitalize(teksti.getValue());
                        }
                    }
                    LOG.debug("Avain {} doesnt match any languages!", filtterointiAvain);
                    return StringUtils.EMPTY;
                }

                public boolean apply(@Nullable HakukohdeTulos tulos) {
                    String tarjoajaNimi = haeTekstiAvaimella(tulos.getHakukohde().getTarjoaja().getNimi());
                    String hakukohdeNimi = haeTekstiAvaimella(tulos.getHakukohde().getNimi());

                    return tarjoajaNimi.startsWith(filtterointiTeksti) || hakukohdeNimi.startsWith(filtterointiTeksti);
                }
            });
        }
        int size = tulokset.size();
        LOG.debug("  after filtering results found '{}'", size);
        List<HakukohdeNimiRDTO> results = new ArrayList<HakukohdeNimiRDTO>();
        int index = 0;
        for (HakukohdeTulos tulos : tulokset) {
            if (index < startIndex) {
                continue; // skip
            }
            if (index > startIndex + count) {
                break; // done!
            }
            HakukohdeListaus hakukohde = tulos.getHakukohde();
            HakukohdeNimiRDTO rdto = new HakukohdeNimiRDTO();
            // tarvitaanko?
            // result.setHakukohdeNimi(...)
            // result.setHakuVuosi(haku.getHakukausiVuosi());
            // result.setHakuKausi(tarjontaKoodistoHelper.getKoodiMetadataNimi(haku.getHakukausiUri()));
            rdto.setHakukohdeNimi(convertMonikielinenToMap(hakukohde.getNimi()));
            rdto.setTarjoajaNimi(convertMonikielinenToMap(hakukohde.getTarjoaja().getNimi()));
            rdto.setHakukohdeOid(hakukohde.getOid());
            rdto.setHakukohdeTila(hakukohde.getTila() != null ? hakukohde.getTila().name() : null);
            results.add(rdto);
            ++index;
        }
        return new HakukohdeTulosRDTO(size, results);
    }

    private Map<String, String> convertMonikielinenToMap(
            fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi monikielinenTekstiTyyppi) {
        Map<String, String> result = new HashMap<String, String>();
        for (MonikielinenTekstiTyyppi.Teksti teksti : monikielinenTekstiTyyppi.getTeksti()) {
            result.put(tarjontaKoodistoHelper.convertKielikoodiToKieliUri(teksti.getKieliKoodi()), teksti.getValue());
        }
        return result;
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

}
