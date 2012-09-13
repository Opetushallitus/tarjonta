package fi.vm.sade.tarjonta.service.business;

import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.types.dto.SearchCriteriaDTO;

import java.util.List;

/**
 * @author Antti
 */
public interface HakueraBusinessService {
    List<Haku> findAll(SearchCriteriaDTO searchCriteria);
    Haku save(Haku hakuera);
    Haku update(Haku hakuera);
    Haku findByOid(String oidString);
}
