package fi.vm.sade.tarjonta.dao;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.types.SearchCriteriaType;

import java.util.List;

/**
 * @author Antti
 */
public interface HakuDAO extends JpaDAO<Haku, Long> {

    List<Haku> findAll(SearchCriteriaType searchCriteria);

    Haku findByOid(String oidString);


}

