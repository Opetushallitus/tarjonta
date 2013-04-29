package fi.vm.sade.tarjonta.service.impl.resources;

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.service.resources.HakuResource;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * http://localhost:8181/tarjonta-service/rest/haku/hello
 *
 * @author mlyly
 */
// @Path("/haku")
@Transactional(readOnly = true)
public class HakuResourceImpl implements HakuResource {

    private static final Logger LOG = LoggerFactory.getLogger(HakuResourceImpl.class);

    @Autowired
    private HakuDAO hakuDAO;

    @Autowired(required = true)
    private ConversionService conversionService;

//    // /haku/hello
//    @Override
//    public String hello() {
//        LOG.info("hello()");
//        return "hello";
//    }
//
//    // /haku?...
//    @Override
//    public List<HakuTyyppi> search(String searchTerms,
//                                   int count,
//                                   int startIndex,
//                                   int startPage,
//                                   String language) {
//        LOG.info("search(searchTerms={})", searchTerms);
//
//        List<HakuTyyppi> hakuTyyppiList = new ArrayList<HakuTyyppi>();
//        List<Haku> hakus = null;
//
//        // TODO search spec from what?
//        // TODO published?
//
//        if (searchTerms != null) {
//            hakus = hakuDAO.findBySearchString(searchTerms, null);
//        } else {
//            hakus = hakuDAO.findAll();
//        }
//
//        for (Haku haku : hakus) {
//            hakuTyyppiList.add(conversionService.convert(haku, HakuTyyppi.class));
//        }
//
//        return hakuTyyppiList;
//    }
//
//    // /haku/{oid}
//    @Override
//    public HakuTyyppi getByOID(String oid, String language) {
//        LOG.info("getByOID({})", oid);
//
//        Haku haku = hakuDAO.findByOid(oid);
//        return conversionService.convert(haku, HakuTyyppi.class);
//    }
//
//    // /haku/{oid}/hakukohde
//    @Override
//    public List<HakukohdeTyyppi> getByOIDHakukohde(String oid, String language) {
//        LOG.info("getByOIDHakukohde(oid={}, language={})", oid, language);
//
//        List<HakukohdeTyyppi> result = new ArrayList<HakukohdeTyyppi>();
//
//        Haku haku = hakuDAO.findByOid(oid);
//        for (Hakukohde hakukohde : haku.getHakukohdes()) {
//            result.add(conversionService.convert(hakukohde, HakukohdeTyyppi.class));
//        }
//
//        return result;
//    }

    // /haku/hello
    @Override
    public String hello() {
        LOG.info("hello()");
        return "hello";
    }

    // /haku?...
    @Override
    public List<String> search(String searchTerms, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince) {
        LOG.info("/haku -- search({}, {}, {}, {}, {})", new Object[]{searchTerms, count, startIndex, lastModifiedBefore, lastModifiedSince});

        // TODO hardcoded JULKAISTU!
        TarjontaTila tarjontaTila = TarjontaTila.JULKAISTU;

        List<String> result = new ArrayList<String>();
        result.addAll(hakuDAO.findOIDsBy(tarjontaTila, count, startIndex, lastModifiedBefore, lastModifiedSince));
        LOG.info("  result={}", result);
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
    public List<String> getByOIDHakukohde(String oid) {
        LOG.info("/haku/{}/hakukohde -- getByOIDHakukohde()", oid);

        List<String> result = new ArrayList<String>();

        Haku h = hakuDAO.findByOid(oid);
        if (h != null) {
            // TODO fixme to be more efficient!
            Set<Hakukohde> hakukohdes = h.getHakukohdes();
            for (Hakukohde hakukohde : hakukohdes) {
                result.add(hakukohde.getOid());
            }
        }
        LOG.info("  result={}", result);
        return result;
    }


}
