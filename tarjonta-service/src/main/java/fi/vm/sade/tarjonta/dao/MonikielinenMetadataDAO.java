package fi.vm.sade.tarjonta.dao;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.tarjonta.model.MonikielinenMetadata;
import java.util.List;

/**
 * @author mlyly
 */
public interface MonikielinenMetadataDAO extends JpaDAO<MonikielinenMetadata, Long> {

  /**
   * @param avain
   * @return list of metadatas that have given "avain". Kategoria and kieli can be anything.
   */
  public List<MonikielinenMetadata> findByAvain(String avain);

  /**
   * @param kategoria ex. "SORA", "ValintaperusteKuvaus", etc.
   * @return list of metadatas matching given <param>kategoria</param>
   */
  public List<MonikielinenMetadata> findByKategoria(String kategoria);

  /**
   * @param avain ex. uri for koodisto related entry: "uri: Sosiaali- ja terveysala"
   * @param kategoria ex. NULL, "SORA", "ValintaperusteKuvaus" etc.
   * @return list of metadatas matching given AVAIN <strong>and</strong> KATEGORIA.
   */
  public List<MonikielinenMetadata> findByAvainAndKategoria(String avain, String kategoria);

  /**
   * An unique metadata is specified by: "avain", "kategoria", "kieli", this method finds it and
   * updates it or creates new one with given values.
   *
   * @param avain
   * @param kategoria
   * @param kieli
   * @param arvo
   * @return created or updated metadata entity
   */
  public MonikielinenMetadata createOrUpdate(
      String avain, String kategoria, String kieli, String arvo);
}
