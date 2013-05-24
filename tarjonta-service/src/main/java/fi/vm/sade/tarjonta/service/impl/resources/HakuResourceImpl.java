package fi.vm.sade.tarjonta.service.impl.resources;

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.service.resources.HakuResource;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import java.util.ArrayList;

import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http://localhost:8181/tarjonta-service/rest/haku/hello
 *
 * Internal documentation: http://liitu.hard.ware.fi/confluence/display/PROG/Tarjonnan+REST+palvelut
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
    public List<OidRDTO> search(String searchTerms, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince) {
        LOG.info("/haku -- search({}, {}, {}, {}, {})", new Object[]{searchTerms, count, startIndex, lastModifiedBefore, lastModifiedSince});

        TarjontaTila tarjontaTila = null; // TarjontaTila.JULKAISTU;

        if (count <= 0) {
            count = 100;
            LOG.debug("  autolimit search to {} entries!", count);
        }

        List<OidRDTO> result = convertOidList(hakuDAO.findOIDsBy(tarjontaTila, count, startIndex, lastModifiedBefore, lastModifiedSince));
        LOG.debug("  result={}", result);
        return result;
    }

    // /haku/OID
    @Override
    public HakuDTO getByOID(String oid) {
        LOG.info("/haku/{} -- getByOID()", oid);

        HakuDTO result = conversionService.convert(hakuDAO.findByOid(oid), HakuDTO.class);
        LOG.info("  result={}", result);
        return result;
    }

    // /haku/OID/hakukohde
    @Override
    public List<OidRDTO> getByOIDHakukohde(String oid, String searchTerms, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince) {
        LOG.info("/haku/{}/hakukohde -- getByOIDHakukohde()", oid);


        if (count <= 0) {
            count = 100;
            LOG.debug("  autolimit search to {} entries!", count);
        }

        List<OidRDTO> result = convertOidList(hakukohdeDAO.findByHakuOid(oid, searchTerms, count, startIndex, lastModifiedBefore, lastModifiedSince));
        LOG.debug("  result={}", result);
        return result;
    }

    // /haku/OID/hakukohdeWithName
    @Override
    public List<Map<String, String>> getByOIDHakukohdeExtra(String oid, String searchTerms, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince) {
        LOG.info("/haku/{}/hakukohdeWithName -- getByOIDHakukohdeExtra()", oid);

        List<Map<String, String>> result = new ArrayList<Map<String, String>>();

        if (count <= 0) {
            count = 100;
            LOG.debug("  autolimit search to {} entries!", count);
        }

        // Get list of oids
        List<OidRDTO> hakukohdeOids = getByOIDHakukohde(oid, searchTerms, count, startIndex, lastModifiedBefore, lastModifiedSince);

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
