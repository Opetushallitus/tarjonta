package fi.vm.sade.tarjonta.shared.types;

import java.util.*;
import java.util.EnumMap;

public class OpintopolkuAlkamiskausi {

  public enum KaudetEnum {
    KESA
  }

  private static Map<KaudetEnum, Map> kaudet = new EnumMap<KaudetEnum, Map>(KaudetEnum.class);

  static {
    Map<String, String> kesa = new HashMap<String, String>();
    kesa.put("kieli_fi", "Kesä");
    kesa.put("kieli_sv", "Sommar");
    kesa.put("kieli_en", "Summer");

    kaudet.put(KaudetEnum.KESA, kesa);
  }

  public static Map getMapFromEnum(KaudetEnum kausi) {
    return kaudet.get(kausi);
  }
}
