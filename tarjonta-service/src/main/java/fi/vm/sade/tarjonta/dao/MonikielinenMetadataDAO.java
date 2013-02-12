package fi.vm.sade.tarjonta.dao;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.tarjonta.model.MonikielinenMetadata;

import java.util.List;

/**
 * @author mlyly
 */
public interface MonikielinenMetadataDAO extends JpaDAO<MonikielinenMetadata, Long> {

    public List<MonikielinenMetadata> findByAvain(String avain);

    public List<MonikielinenMetadata> findByKategoria(String kategoria);

    public List<MonikielinenMetadata> findByAvainAndKategoria(String avain, String kategoria);

    public MonikielinenMetadata createOrUpdate(String avain, String kategoria, String kieli, String arvo);

}
