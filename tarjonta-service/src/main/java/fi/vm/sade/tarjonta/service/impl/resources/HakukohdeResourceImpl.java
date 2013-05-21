package fi.vm.sade.tarjonta.service.impl.resources;

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.HakukohdeResource;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
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
    public List<String> search(String searchTerms, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince) {
        LOG.info("/hakukohde -- search({}, {}, {}, {}, {})", new Object[]{searchTerms, count, startIndex, lastModifiedBefore, lastModifiedSince});

        TarjontaTila tarjontaTila = null; // TarjontaTila.JULKAISTU;

        if (count <= 0) {
            count = 100;
            LOG.info("  autolimit search to {} entries!", count);
        }

        List<String> result = new ArrayList<String>();
        result.addAll(hakukohdeDAO.findOIDsBy(tarjontaTila, count, startIndex, lastModifiedBefore, lastModifiedSince));
        LOG.info("  result={}", result);
        return result;
    }

    // /hakukohde/OID
    @Override
    public HakukohdeDTO getByOID(String oid) {
        LOG.info("/hakukohde/{} -- getByOID()", oid);

        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
        HakukohdeDTO result = conversionService.convert(hakukohde, HakukohdeDTO.class);
        LOG.info("  result={}", result);
        return result;
    }

    // /hakukohde/OID/haku
    @Override
    public HakuDTO getHakuByHakukohdeOID(String oid) {
        LOG.info("/hakukohde/{}/haku -- getHakuByHakukohdeOID()", oid);

        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
        HakuDTO result = conversionService.convert(hakukohde.getHaku(), HakuDTO.class);
        LOG.info("  result={}", result);
        return result;
    }

    // /hakukohde/OID/komoto
    @Override
    public List<String> getKomotosByHakukohdeOID(String oid) {
        LOG.info("/hakukohde/{}/komoto -- getKomotosByHakukohdeOID()", oid);

        // TODO fixme to be more efficient!
        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
        List<String> result = new ArrayList<String>();
        for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : hakukohde.getKoulutusmoduuliToteutuses()) {
            result.add(koulutusmoduuliToteutus.getOid());
        }
        LOG.info("  result={}", result);
        return result;
    }

    @Override
    public List<String> getLiitesByHakukohdeOID(String oid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getPaasykoesByHakukohdeOID(String oid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getValintakoesByHakukohdeOID(String oid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
