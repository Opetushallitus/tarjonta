package fi.vm.sade.tarjonta.service.impl.resources;

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.HakukohdeResource;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author mlyly
 */
// /hakukohde
@Transactional
public class HakukohdeResourceImpl implements HakukohdeResource {

    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeResourceImpl.class);

    @Autowired
    private HakuDAO hakuDAO;

    @Autowired
    private HakukohdeDAO hakukohdeDAO;

    @Autowired
    private ConversionService conversionService;

    // /hakukohde?searchTerms=xxx&...
    @Override
    public List<HakukohdeTyyppi> search(String searchTerms,
                                        int count,
                                        int startIndex,
                                        int startPage,
                                        String language) {

        LOG.info("search(searchTerms={})", searchTerms);

        List<HakukohdeTyyppi> hakukohdeTyyppiList = new ArrayList<HakukohdeTyyppi>();

        // TODO search spec not used - whats wanted here?
        // TODO published?

        List<Hakukohde> hakukohdes = hakukohdeDAO.findAll();
        for (Hakukohde hakukohde : hakukohdes) {
            hakukohdeTyyppiList.add(conversionService.convert(hakukohde, HakukohdeTyyppi.class));
        }

        return hakukohdeTyyppiList;
    }

    // /hakukohde/{oid}
    @Override
    public HakukohdeTyyppi getByOID(String oid, String language) {
        LOG.info("getByOID({})", oid);

        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
        return conversionService.convert(hakukohde, HakukohdeTyyppi.class);
    }

    // /hakukohde/{oid}/koulutus
    @Override
    public List<HakukohdeTyyppi> getByOIDKoulutus(String oid, String language) {
        LOG.info("getByOIDKoulutus({})", oid);

        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeWithKomotosByOid(oid);
        if (hakukohde == null) {
            return Collections.EMPTY_LIST;
        }

        for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : hakukohde.getKoulutusmoduuliToteutuses()) {


        }

        return Collections.EMPTY_LIST;
    }

    // /hakukohde/{oid}/paasykoe
    @Override
    public List<HakukohdeTyyppi> getByOIDPaasykoe(String oid, String language) {
        LOG.info("getByOIDPaasykoe({})", oid);
        return Collections.EMPTY_LIST;
    }

    // /hakukohde/{oid}/liite
    @Override
    public List<HakukohdeTyyppi> getByOIDLiite(String oid, String language) {
        LOG.info("getByOIDLiite({})", oid);
        return Collections.EMPTY_LIST;
    }


}
