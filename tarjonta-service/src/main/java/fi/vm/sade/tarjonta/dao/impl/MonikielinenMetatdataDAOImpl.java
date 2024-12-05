package fi.vm.sade.tarjonta.dao.impl;

import fi.vm.sade.tarjonta.dao.AbstractJpaDAOImpl;
import fi.vm.sade.tarjonta.dao.MonikielinenMetadataDAO;
import fi.vm.sade.tarjonta.model.MonikielinenMetadata;
import jakarta.persistence.Query;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * @author mlyly
 */
@Repository
public class MonikielinenMetatdataDAOImpl extends AbstractJpaDAOImpl<MonikielinenMetadata, Long>
    implements MonikielinenMetadataDAO {

  private static final Logger LOG = LoggerFactory.getLogger(MonikielinenMetatdataDAOImpl.class);

  @Override
  public List<MonikielinenMetadata> findByAvain(String avain) {
    LOG.debug("findByAvain({})", avain);
    List<MonikielinenMetadata> result = findBy("avain", avain);
    LOG.debug("  result = {}", result);
    return result;
  }

  @Override
  public List<MonikielinenMetadata> findByKategoria(String kategoria) {
    LOG.debug("findByKategoria({})", kategoria);
    List<MonikielinenMetadata> result = findBy("kategoria", kategoria);
    LOG.debug("  result = {}", result);
    return result;
  }

  @Override
  public List<MonikielinenMetadata> findByAvainAndKategoria(String avain, String kategoria) {
    LOG.debug("findByAvainAndKategoria({}, {})", avain, kategoria);

    List<MonikielinenMetadata> result = null;

    Query query =
        getEntityManager()
            .createQuery(
                "SELECT x FROM MonikielinenMetadata x WHERE x.avain = :avain AND x.kategoria = :kategoria");
    query.setParameter("avain", avain);
    query.setParameter("kategoria", kategoria);
    query.setFirstResult(0);
    query.setMaxResults(Integer.MAX_VALUE);

    result = query.getResultList();
    LOG.debug("  result = {}", result);

    return result;
  }

  @Override
  public MonikielinenMetadata createOrUpdate(
      String avain, String kategoria, String kieli, String arvo) {
    LOG.debug(
        "createOrUpdate({}, {}, {}, {})", new Object[] {avain, kategoria, kieli, truncate(arvo)});

    MonikielinenMetadata result = findOrCreateByAvainKategoriaKieli(avain, kategoria, kieli);
    if (arvo == null) {
      // the entity will be removed.
      remove(result);
      return null;
    }
    result.setArvo(arvo);

    if (result.getId() != null) {
      update(result);
    } else {
      result = insert(result);
    }

    return result;
  }

  private String truncate(String text) {
    if (text == null) {
      return "NULL";
    }

    if (text.length() < 20) {
      return text;
    }

    return text.substring(0, 20) + "...";
  }

  private MonikielinenMetadata findOrCreateByAvainKategoriaKieli(
      String avain, String kategoria, String kieli) {
    LOG.debug(
        "findOrCreateByAvainKategoriaKieli({}, {}, {})", new Object[] {avain, kategoria, kieli});

    MonikielinenMetadata result = null;

    final String q =
        "SELECT x FROM MonikielinenMetadata x WHERE "
            + "x.avain = :avain AND x.kategoria = :kategoria AND x.kieli = :kieli";

    Query query = getEntityManager().createQuery(q);
    query.setParameter("avain", avain);
    query.setParameter("kategoria", kategoria);
    query.setParameter("kieli", kieli);
    query.setFirstResult(0);
    query.setMaxResults(Integer.MAX_VALUE);

    List<MonikielinenMetadata> tmp = query.getResultList();
    switch (tmp.size()) {
      case 0:
        // New entry

        result = new MonikielinenMetadata();
        result.setAvain(avain);
        result.setKategoria(kategoria);
        result.setKieli(kieli);
        break;
      case 1:
        // Existing
        result = tmp.get(0);
        break;
      default:
        // Hmm... this should never happen since db uniqueness constraint
        LOG.error(
            "findOrCreateByAvainKategoriaKieli({}, {}, {})",
            new Object[] {avain, kategoria, kieli});
        LOG.error(
            "  FOUND {} > 1 ENTRIES, NOW THIS SHOULD BE IMPOSSIBLE, DB SHOULD ENFORCE UNIQUENESS!",
            tmp.size());
        throw new IllegalStateException(
            "MonikielinenMetadata database uniqueness constraint violation!");
    }

    LOG.debug("  result = {}", result);

    return result;
  }
}
