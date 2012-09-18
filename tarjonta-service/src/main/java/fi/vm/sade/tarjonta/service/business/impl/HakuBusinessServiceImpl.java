package fi.vm.sade.tarjonta.service.business.impl;

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.model.Haku;
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
    private HakuDAO hakueraDao;

    @Override
    public List<Haku> findAll(SearchCriteriaDTO searchCriteria) {
        return hakueraDao.findAll(searchCriteria);
    }

    @Override
    public Haku save(Haku haku) {
        return hakueraDao.insert(haku);
    }

    @Override
    public Haku update(Haku haku) {
        if (haku.getId() == null) {
            throw new IllegalArgumentException("updating object with null id: " + haku);
        }
        hakueraDao.update(haku);
        return haku;
    }

    public Haku findByOid(String oidString) {
        return hakueraDao.findByOid(oidString);
    }

}

