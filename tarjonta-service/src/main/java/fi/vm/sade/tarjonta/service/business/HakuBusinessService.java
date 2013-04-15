package fi.vm.sade.tarjonta.service.business;

import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.types.SearchCriteriaType;

import java.util.List;

/**
 * @author Antti
 */
public interface HakuBusinessService {

    List<Haku> findAll(SearchCriteriaType searchCriteria);

    Haku save(Haku haku);

    Haku update(Haku haku);

    Haku findByOid(String oidString);

    /**
     * Deletes a Haku if and only if it's state is
     *
     * @param oid
     */
    public void delete(String oid);

}

