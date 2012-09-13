package fi.vm.sade.tarjonta.service.business.impl;

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.business.HakueraBusinessService;
import fi.vm.sade.tarjonta.service.types.dto.SearchCriteriaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Antti
 */
@Service
public class HakueraBusinessServiceImpl implements HakueraBusinessService {

    @Autowired
    private ConversionService conversionService;
    @Autowired
    private HakuDAO hakueraDao;

    @Override
    public List<Haku> findAll(SearchCriteriaDTO searchCriteria) {
        return hakueraDao.findAll(searchCriteria);
    }

    @Override
    public Haku save(Haku hakuera) {
        return hakueraDao.insert(hakuera);
    }

    @Override
    public Haku update(Haku hakuera) {
        if (hakuera.getId() == null) {
            throw new IllegalArgumentException("updating object with null id: "+hakuera);
        }
        hakueraDao.update(hakuera);
        return hakuera;
    }
    
    public Haku findByOid(String oidString) {
        return hakueraDao.findByOid(oidString);
    }

}
