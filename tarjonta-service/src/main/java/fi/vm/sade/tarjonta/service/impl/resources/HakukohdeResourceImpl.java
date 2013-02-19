package fi.vm.sade.tarjonta.service.impl.resources;

import fi.vm.sade.tarjonta.service.resources.HakukohdeResource;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * @author mlyly
 */
public class HakukohdeResourceImpl implements HakukohdeResource {

    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeResourceImpl.class);

    @Override
    public List<HakukohdeTyyppi> search(String spec) {
        LOG.info("search(spec={})", spec);
        return Collections.EMPTY_LIST;
    }

    @Override
    public HakukohdeTyyppi getByOID(String oid) {
        LOG.info("getByOID({})", oid);
        return null;
    }

    @Override
    public List<HakukohdeTyyppi> getByOIDKoulutus(String oid) {
        LOG.info("getByOIDKoulutus({})", oid);
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<HakukohdeTyyppi> getByOIDPaasykoe(String oid) {
        LOG.info("getByOIDPaasykoe({})", oid);
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<HakukohdeTyyppi> getByOIDLiite(String oid) {
        LOG.info("getByOIDLiite({})", oid);
        return Collections.EMPTY_LIST;
    }


}
