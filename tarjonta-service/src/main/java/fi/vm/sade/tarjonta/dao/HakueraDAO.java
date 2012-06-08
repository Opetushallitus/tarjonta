package fi.vm.sade.tarjonta.dao;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.tarjonta.model.Hakuera;
import fi.vm.sade.tarjonta.service.types.dto.SearchCriteriaDTO;

import java.util.List;

/**
 * @author Antti
 */
public interface HakueraDAO extends JpaDAO<Hakuera, Long> {
    List<Hakuera> findAll(SearchCriteriaDTO searchCriteria);
}
