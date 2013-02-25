package fi.vm.sade.tarjonta.service.impl.resources;

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.service.resources.HakuResource;
import fi.vm.sade.tarjonta.service.types.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import java.util.ArrayList;
import java.util.List;

/**
 * http://localhost:8181/tarjonta-service/rest/haku/hello
 *
 * @author mlyly
 */
// @Path("/haku")
public class HakuResourceImpl implements HakuResource {

    private static final Logger LOG = LoggerFactory.getLogger(HakuResourceImpl.class);

    @Autowired
    private HakuDAO hakuDAO;

    @Autowired(required = true)
    private ConversionService conversionService;

    // /haku/hello
    @Override
    public String hello() {
        LOG.info("hello()");
        return "hello";
    }

    // /haku?etsi=XXX
    @Override
    public List<HakuTyyppi> search(String spec) {
        LOG.info("search(spec={})", spec);

        List<HakuTyyppi> hakuTyyppiList = new ArrayList<HakuTyyppi>();
        List<Haku> hakus = null;

        // TODO search spec from what?
        // TODO published?

        if (spec != null) {
            hakus = hakuDAO.findBySearchString(spec, null);
        } else {
            hakus = hakuDAO.findAll();
        }

        for (Haku haku : hakus) {
            hakuTyyppiList.add(conversionService.convert(haku, HakuTyyppi.class));
        }

        return hakuTyyppiList;
    }

    // /haku/{oid}
    @Override
    public HakuTyyppi getByOID(String oid) {
        LOG.info("getByOID({})", oid);

        Haku haku = hakuDAO.findByOid(oid);
        return conversionService.convert(haku, HakuTyyppi.class);
    }

    // /haku/{oid}/hakukohde?etsi=xxx
    @Override
    public List<HakukohdeTyyppi> getByOIDHakukohde(String oid, String spec) {
        LOG.info("getByOIDHakukohde(oid={}, spec={})", oid, spec);

        List<HakukohdeTyyppi> result = new ArrayList<HakukohdeTyyppi>();

        Haku haku = hakuDAO.findByOid(oid);
        for(Hakukohde hakukohde : haku.getHakukohdes()) {
            result.add(conversionService.convert(hakukohde, HakukohdeTyyppi.class));
        }

        return result;
    }

}
