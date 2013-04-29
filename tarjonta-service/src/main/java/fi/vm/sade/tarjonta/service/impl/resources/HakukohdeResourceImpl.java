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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author mlyly
 */
// /hakukohde
@Transactional(readOnly = true)
public class HakukohdeResourceImpl implements HakukohdeResource {

    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeResourceImpl.class);

    @Autowired
    private HakuDAO hakuDAO;

    @Autowired
    private HakukohdeDAO hakukohdeDAO;

    @Autowired
    private ConversionService conversionService;

//    // /hakukohde?searchTerms=xxx&...
//    @Override
//    public List<HakukohdeTyyppi> search(String searchTerms,
//                                        int count,
//                                        int startIndex,
//                                        int startPage,
//                                        String language) {
//
//        LOG.info("search(searchTerms={})", searchTerms);
//
//        List<HakukohdeTyyppi> hakukohdeTyyppiList = new ArrayList<HakukohdeTyyppi>();
//
//        // TODO search spec not used - whats wanted here?
//        // TODO published?
//
//        List<Hakukohde> hakukohdes = hakukohdeDAO.findAll();
//        for (Hakukohde hakukohde : hakukohdes) {
//            hakukohdeTyyppiList.add(conversionService.convert(hakukohde, HakukohdeTyyppi.class));
//        }
//
//        return hakukohdeTyyppiList;
//    }
//
//    // /hakukohde/{oid}
//    @Override
//    public HakukohdeTyyppi getByOID(String oid, String language) {
//        LOG.info("getByOID({})", oid);
//
//        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
//        return conversionService.convert(hakukohde, HakukohdeTyyppi.class);
//    }
//
//    // /hakukohde/{oid}/koulutus
//    @Override
//    public List<HakukohdeTyyppi> getByOIDKoulutus(String oid, String language) {
//        LOG.info("getByOIDKoulutus({})", oid);
//
//        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
//        if (hakukohde == null) {
//            return Collections.EMPTY_LIST;
//        }
//
//        for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : hakukohde.getKoulutusmoduuliToteutuses()) {
//            // TODO convert to some representation
//
//        }
//
//        return Collections.EMPTY_LIST;
//    }
//
//    // /hakukohde/{oid}/paasykoe
//    @Override
//    public List<HakukohdeTyyppi> getByOIDPaasykoe(String oid, String language) {
//        LOG.info("getByOIDPaasykoe({})", oid);
//        return Collections.EMPTY_LIST;
//    }
//
//    // /hakukohde/{oid}/liite
//    @Override
//    public List<HakukohdeTyyppi> getByOIDLiite(String oid, String language) {
//        LOG.info("getByOIDLiite({})", oid);
//        return Collections.EMPTY_LIST;
//    }
//


    // /hakukohde?...
    @Override
    public List<String> search(String searchTerms, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince) {
        LOG.info("/hakukohde -- search({}, {}, {}, {}, {})", new Object[]{searchTerms, count, startIndex, lastModifiedBefore, lastModifiedSince});

        // TODO hardcoded JULKAISTU!
        TarjontaTila tarjontaTila = TarjontaTila.JULKAISTU;

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

}
