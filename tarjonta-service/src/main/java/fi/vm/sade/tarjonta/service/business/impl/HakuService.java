package fi.vm.sade.tarjonta.service.business.impl;

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.model.Haku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Antti
 */
@Service
public class HakuService {

  @Autowired private HakuDAO hakuDao;

  public Haku save(Haku haku) {
    return hakuDao.insert(haku);
  }

  public Haku update(Haku haku) {
    if (haku.getId() == null) {
      throw new IllegalArgumentException("updating object with null id: " + haku);
    }
    hakuDao.update(haku);
    return haku;
  }

  public Haku findByOid(String oidString) {
    return hakuDao.findByOid(oidString);
  }

  public void delete(String oid) {

    Haku haku = hakuDao.findByOid(oid);
    if (haku == null) {
      // todo: what exceptions should we throw so that they are property handled in WS
      throw new IllegalArgumentException("cannot delete Haku, no such oid " + oid);
    }

    if (!haku.getTila().isRemovable()) {
      throw new IllegalArgumentException("cannot delete Haku, bad state: " + haku.getTila());
    }

    // todo: are there any other relations we need to check??

    hakuDao.remove(haku);
  }
}
