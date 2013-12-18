package fi.vm.sade.tarjonta.dao;
/*
* @author: Tuomas Katva 17/12/13
*/

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.ValintaperusteSoraKuvaus;

import java.util.List;

public interface KuvausDAO extends JpaDAO<ValintaperusteSoraKuvaus, Long> {

   List<ValintaperusteSoraKuvaus> findByTyyppi(ValintaperusteSoraKuvaus.Tyyppi tyyppi);

   List<ValintaperusteSoraKuvaus> findByTyyppiAndOrganizationType(ValintaperusteSoraKuvaus.Tyyppi tyyppi, String orgType);

    List<ValintaperusteSoraKuvaus> findByOppilaitosTyyppiTyyppiAndNimi(ValintaperusteSoraKuvaus.Tyyppi tyyppi, String nimi, String oppilaitosTyyppi);

}
