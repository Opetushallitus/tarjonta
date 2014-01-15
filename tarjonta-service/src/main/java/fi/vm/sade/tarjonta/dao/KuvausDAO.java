package fi.vm.sade.tarjonta.dao;
/*
* @author: Tuomas Katva 17/12/13
*/

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.ValintaperusteSoraKuvaus;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KuvausSearchV1RDTO;

import java.util.List;

public interface KuvausDAO extends JpaDAO<ValintaperusteSoraKuvaus, Long> {

   List<ValintaperusteSoraKuvaus> findByTyyppi(ValintaperusteSoraKuvaus.Tyyppi tyyppi);

   List<ValintaperusteSoraKuvaus> findByTyyppiAndOrganizationType(ValintaperusteSoraKuvaus.Tyyppi tyyppi, String orgType);

   List<ValintaperusteSoraKuvaus> findByTyyppiOrgTypeAndYear(ValintaperusteSoraKuvaus.Tyyppi tyyppi, String orgType, int year);

    List<ValintaperusteSoraKuvaus> findByOppilaitosTyyppiTyyppiAndNimi(ValintaperusteSoraKuvaus.Tyyppi tyyppi, String nimi, String oppilaitosTyyppi);

    List<ValintaperusteSoraKuvaus> findBySearchSpec(KuvausSearchV1RDTO searchSpec, ValintaperusteSoraKuvaus.Tyyppi tyyppi);

}
