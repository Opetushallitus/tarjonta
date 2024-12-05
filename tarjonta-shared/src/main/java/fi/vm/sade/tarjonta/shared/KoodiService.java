package fi.vm.sade.tarjonta.shared;

import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import java.util.List;

/** Koodien lataaminen koodistopalvelusta. */
public interface KoodiService {

  List<KoodiType> listKoodiByRelation(
      KoodiUriAndVersioType koodi, boolean onAlaKoodi, SuhteenTyyppiType suhdeTyyppi);

  List<KoodiType> searchKoodisByKoodisto(String koodistoUri, String koodiArvo);

  List<KoodiType> searchKoodis(SearchKoodisCriteriaType searchCriteria);
}
