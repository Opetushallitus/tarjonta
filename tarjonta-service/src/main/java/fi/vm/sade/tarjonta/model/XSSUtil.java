package fi.vm.sade.tarjonta.model;

import fi.vm.sade.security.xssfilter.XssFilter;

public class XSSUtil {
  public static void filter(MonikielinenTeksti teksti) {
    if (teksti == null) return;
    for (TekstiKaannos kaannos : teksti.getKaannoksetAsList()) {
      if (kaannos.getArvo() != null) {
        kaannos.setArvo(XssFilter.filter(kaannos.getArvo()));
      }
    }
  }
}
