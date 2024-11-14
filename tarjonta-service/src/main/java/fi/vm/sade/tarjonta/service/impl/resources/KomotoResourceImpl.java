package fi.vm.sade.tarjonta.service.impl.resources;

import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.KomotoResource;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class KomotoResourceImpl implements KomotoResource {

    private static final Logger LOG = LoggerFactory.getLogger(KomotoResourceImpl.class);

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired
    private ConversionService conversionService;

    // GET /komoto/hello
    @Override
    public String hello() {
        LOG.debug("hello() -- /komoto/hello");
        return "hello";
    }

    // GET /komoto/{oid}
    @Override
    public KomotoDTO getByOID(String oid) {
        //LOG.debug("getByOID() -- /komoto/{}", oid);
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        KomotoDTO result = conversionService.convert(komoto, KomotoDTO.class);
        //LOG.debug("  result={}", result);
        return result;
    }

    // GET /komoto?searchTerms=xxx etc.
    @Override
    public List<OidRDTO> search(String searchTerms, int count, int startIndex, Date lastModifiedBefore, Date lastModifiedSince) {
        LOG.debug("search() -- /komoto?st={}, c={}, si={}, lmb={}, lma={})", new Object[]{searchTerms, count, startIndex, lastModifiedBefore, lastModifiedSince});

        // TODO hard coded, add param tarjonta tila + get the state!
        TarjontaTila tarjontaTila = TarjontaTila.JULKAISTU;

        count = manageCountValue(count);

        List<OidRDTO> result
                = HakuResourceImpl.convertOidList(koulutusmoduuliToteutusDAO.findOIDsBy(tarjontaTila, count, startIndex, lastModifiedBefore, lastModifiedSince));
        //LOG.debug("  result={}", result);
        return result;
    }

    // GET /komoto/{oid}/komo
    @Override
    public KomoDTO getKomoByKomotoOID(String oid) {
        LOG.debug("getKomoByKomotoOID() -- /komoto/{}/komo", oid);
        KomoDTO result = null;
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        if (komoto != null) {
            result = conversionService.convert(komoto.getKoulutusmoduuli(), KomoDTO.class);
        }
        //LOG.debug("  result={}", result);
        return result;
    }

    // GET /komoto/{oid}/hakukohde
    @Override
    public List<OidRDTO> getHakukohdesByKomotoOID(String oid) {
        LOG.debug("getHakukohdesByKomotoOID() -- /komoto/{}/hakukohde", oid);
        List<OidRDTO> result = new ArrayList<OidRDTO>();

        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(oid);
        if (komoto != null) {
            // TODO add spesific finder to get just these OIDs... not sure about the usage pattern of this service
            for (Hakukohde hakukohde : komoto.getHakukohdes()) {
                result.add(new OidRDTO(hakukohde.getOid()));
            }
        }

        //LOG.debug("  result={}", result);
        return result;
    }

    private int manageCountValue(int count) {

        if (count < 0) {
            count = Integer.MAX_VALUE;
            LOG.debug("  count < 0, using Integer.MAX_VALUE");
        }

        if (count == 0) {
            count = 100;
            LOG.debug("  autolimit search to {} entries!", count);
        }

        return count;
    }

}
