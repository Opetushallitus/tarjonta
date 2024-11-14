package fi.vm.sade.tarjonta.service.impl.resources.v1.util;

import fi.vm.sade.tarjonta.dao.KuvausDAO;
import fi.vm.sade.tarjonta.model.MonikielinenMetadata;
import fi.vm.sade.tarjonta.model.ValintaperusteSoraKuvaus;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValintaperustekuvausHelper {

  private KuvausDAO kuvausDAO;

  @Autowired
  public ValintaperustekuvausHelper(KuvausDAO kuvausDAO) {
    this.kuvausDAO = kuvausDAO;
  }

  public HashMap<String, String> getKuvausByAvainTyyppiKausiVuosi(
      String avain, ValintaperusteSoraKuvaus.Tyyppi tyyppi, String kausi, int vuosi) {
    avain = checkAndRemoveForEmbeddedVersionInUri(avain);
    kausi = checkAndRemoveForEmbeddedVersionInUri(kausi);

    List<ValintaperusteSoraKuvaus> kuvauksetRaw =
        kuvausDAO.findByAvainTyyppiYearKausi(avain, tyyppi, kausi, vuosi);

    HashMap<String, String> kuvaukset = new HashMap<String, String>();

    if (kuvauksetRaw.size() > 0) {
      ValintaperusteSoraKuvaus kuvausRaw = kuvauksetRaw.get(0);
      for (MonikielinenMetadata meta : kuvausRaw.getTekstis()) {
        kuvaukset.put(meta.getKieli(), meta.getArvo());
      }
    }
    return kuvaukset;
  }

  private String checkAndRemoveForEmbeddedVersionInUri(String uri) {
    if (uri != null) {
      if (uri.contains("#")) {
        StringTokenizer st = new StringTokenizer(uri, "#");
        return st.nextToken();
      } else {
        return uri;
      }
    } else {
      return null;
    }
  }
}
