package fi.vm.sade.tarjonta.dao.impl;

import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import fi.vm.sade.tarjonta.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.YhteyshenkiloDAO;
import fi.vm.sade.tarjonta.model.Yhteyshenkilo;
import java.util.List;
import org.springframework.stereotype.Repository;

/** */
@Repository
public class YhteyshenkiloDAOImpl extends AbstractJpaDAOImpl<Yhteyshenkilo, Long>
    implements YhteyshenkiloDAO {

  @Override
  public Yhteyshenkilo findByOid(String oid) {

    List<Yhteyshenkilo> list = findBy(Yhteyshenkilo.ID_COLUMN_NAME, oid);
    if (list.isEmpty()) {
      return null;
    } else if (list.size() == 1) {
      return list.get(0);
    } else {
      throw new IllegalStateException("multiple results for oid: " + oid);
    }
  }

  protected JPAQueryBase from(EntityPath<?>... o) {
    return new JPAQuery(getEntityManager()).from(o);
  }
}
