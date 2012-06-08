package fi.vm.sade.tarjonta.service.business.impl;

import fi.vm.sade.tarjonta.dao.HakueraDAO;
import fi.vm.sade.tarjonta.model.Hakuera;
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
    private HakueraDAO hakueraDao;

    @Override
    public List<Hakuera> findAll(SearchCriteriaDTO searchCriteria) {
        return hakueraDao.findAll(searchCriteria);
    }

}
