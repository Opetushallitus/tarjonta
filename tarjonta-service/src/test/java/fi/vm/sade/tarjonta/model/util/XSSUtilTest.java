package fi.vm.sade.tarjonta.model.util;

import static org.junit.Assert.assertEquals;

import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.XSSUtil;
import org.junit.Test;

public class XSSUtilTest {

  @Test
  public void thatDisplayNoneStylesAreRemoved() {
    MonikielinenTeksti monikielinenTeksti = new MonikielinenTeksti();
    monikielinenTeksti.addTekstiKaannos(
        "kieli_fi", "<div style=\"display:none\">Hidden content</div>");

    XSSUtil.filter(monikielinenTeksti);

    String teksti = monikielinenTeksti.getTekstiForKieliKoodi("kieli_fi");
    assertEquals("<div style=\"\">Hidden content</div>", teksti);
  }
}
