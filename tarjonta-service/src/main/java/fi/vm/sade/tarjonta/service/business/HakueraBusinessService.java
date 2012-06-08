package fi.vm.sade.tarjonta.service.business;

import fi.vm.sade.tarjonta.model.Hakuera;
import fi.vm.sade.tarjonta.service.types.dto.SearchCriteriaDTO;

import java.util.List;

/**
 * @author Antti
 */
public interface HakueraBusinessService {
    List<Hakuera> findAll(SearchCriteriaDTO searchCriteria);
}
