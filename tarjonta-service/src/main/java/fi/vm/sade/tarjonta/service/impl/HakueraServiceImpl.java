package fi.vm.sade.tarjonta.service.impl;

import fi.vm.sade.tarjonta.model.Hakuera;
import fi.vm.sade.tarjonta.service.HakueraService;
import fi.vm.sade.tarjonta.service.business.HakueraBusinessService;
import fi.vm.sade.tarjonta.service.types.dto.HakueraSimpleDTO;
import fi.vm.sade.tarjonta.service.types.dto.SearchCriteriaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Antti Salonen
 */
@Transactional
@Service("hakueraService")
public class HakueraServiceImpl implements HakueraService {

    @Autowired
    private HakueraBusinessService businessService;
    @Autowired
    private ConversionService conversionService;

    @Override
    public List<HakueraSimpleDTO> findAll(SearchCriteriaDTO searchCriteria) {
        return convert(businessService.findAll(searchCriteria));
    }

    private List<HakueraSimpleDTO> convert(List<Hakuera> hakueras) {
        List<HakueraSimpleDTO> result = new ArrayList<HakueraSimpleDTO>();
        for (Hakuera hakuera : hakueras) {
            result.add(conversionService.convert(hakuera, HakueraSimpleDTO.class));
        }
        return result;
    }

}
