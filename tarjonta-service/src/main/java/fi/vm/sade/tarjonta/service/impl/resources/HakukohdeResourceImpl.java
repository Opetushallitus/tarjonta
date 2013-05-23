package fi.vm.sade.tarjonta.service.impl.resources;

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.HakukohdeLiite;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.HakukohdeResource;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
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
import java.util.List;

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

    @Autowired
    private ConversionService conversionService;

    // /hakukohde?...
    @Override
    public List<OidRDTO> search(String searchTerms, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince) {
        LOG.info("/hakukohde -- search({}, {}, {}, {}, {})", new Object[]{searchTerms, count, startIndex, lastModifiedBefore, lastModifiedSince});

        TarjontaTila tarjontaTila = null; // TarjontaTila.JULKAISTU;

        if (count <= 0) {
            count = 100;
            LOG.debug("  autolimit search to {} entries!", count);
        }

        List<OidRDTO> result = HakuResourceImpl.convertOidList(hakukohdeDAO.findOIDsBy(tarjontaTila, count, startIndex, lastModifiedBefore, lastModifiedSince));
        LOG.debug("  result={}", result);
        return result;
    }

    // /hakukohde/OID
    @Override
    public HakukohdeDTO getByOID(String oid) {
        LOG.info("/hakukohde/{} -- getByOID()", oid);

        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
        HakukohdeDTO result = conversionService.convert(hakukohde, HakukohdeDTO.class);
        LOG.debug("  result={}", result);
        return result;
    }

    // /hakukohde/OID/haku
    @Override
    public HakuDTO getHakuByHakukohdeOID(String oid) {
        LOG.info("/hakukohde/{}/haku -- getHakuByHakukohdeOID()", oid);

        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
        HakuDTO result = conversionService.convert(hakukohde.getHaku(), HakuDTO.class);
        LOG.debug("  result={}", result);
        return result;
    }

    // /hakukohde/OID/komoto
    @Override
    public List<OidRDTO> getKomotosByHakukohdeOID(String oid) {
        LOG.info("/hakukohde/{}/komoto -- getKomotosByHakukohdeOID()", oid);

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
        LOG.info("/hakukohde/{}/paasykoe -- getPaasykoesByHakukohdeOID()", oid);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // GET /hakukohde/{oid}/valintakoe
    @Override
    public List<String> getValintakoesByHakukohdeOID(String oid) {
        LOG.info("/hakukohde/{}/valintakoe -- getValintakoesByHakukohdeOID()", oid);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
