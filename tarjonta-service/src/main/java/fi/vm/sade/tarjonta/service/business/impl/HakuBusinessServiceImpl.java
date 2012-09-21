package fi.vm.sade.tarjonta.service.business.impl;

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.KoodistoContract;
import fi.vm.sade.tarjonta.service.business.HakuBusinessService;
import fi.vm.sade.tarjonta.service.types.dto.SearchCriteriaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Antti
 */
@Service
public class HakuBusinessServiceImpl implements HakuBusinessService {

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private HakuDAO hakuDao;

    @Override
    public List<Haku> findAll(SearchCriteriaDTO searchCriteria) {
        return hakuDao.findAll(searchCriteria);
    }

    @Override
    public Haku save(Haku haku) {
        return hakuDao.insert(haku);
    }

    @Override
    public Haku update(Haku haku) {
        if (haku.getId() == null) {
            throw new IllegalArgumentException("updating object with null id: " + haku);
        }
        hakuDao.update(haku);
        return haku;
    }

    public Haku findByOid(String oidString) {
        return hakuDao.findByOid(oidString);
    }

    @Override
    public void delete(String oid) {

        Haku haku = hakuDao.findByOid(oid);
        if (haku == null) {
            // todo: what exceptions should we throw so that they are property handled in WS
            throw new IllegalArgumentException("cannot delete Haku, no such oid " + oid);
        }

        if (!KoodistoContract.TarjontaTilat.SUUNNITTELUSSA.equals(haku.getTila())) {
            throw new IllegalArgumentException("cannot delete Haku, bad state: " + haku.getTila());
        }
        
        // todo: are there any other relations we need to check??
        
        hakuDao.remove(haku);

    }

}

