package fi.vm.sade.tarjonta.helpers;

import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import java.util.Arrays;
import java.util.List;

public class KoodistoHelper {

  public List<KoodiType> getKoodiTypes(String uri) {
    KoodiType kt = new KoodiType();
    kt.setKoodiArvo(uri);
    kt.setKoodiUri(uri);
    kt.getMetadata().add(getKoodiMeta(uri, KieliType.FI));
    kt.getMetadata().add(getKoodiMeta(uri, KieliType.SV));
    kt.getMetadata().add(getKoodiMeta(uri, KieliType.EN));
    return Arrays.asList(kt);
  }

  private KoodiMetadataType getKoodiMeta(String arvo, KieliType kieli) {
    KoodiMetadataType type = new KoodiMetadataType();
    type.setKieli(kieli);
    type.setNimi(arvo + "-nimi-" + kieli.toString());
    type.setLyhytNimi(arvo + "-lyhyt-nimi-" + kieli.toString());
    return type;
  }
}
