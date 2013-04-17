package fi.vm.sade.tarjonta.ui.model;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

/**
 * @author: Tuomas Katva
 * Date: 4/17/13
 */
@Configurable(preConstruction = true)
public class HakuHakukohdeResultRow extends HorizontalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(HakuHakukohdeResultRow.class);

    private transient I18NHelper i18n = new I18NHelper(this);

    @Autowired(required = true)
    private TarjontaUIHelper tarjontaUIHelper;

    private HakukohdeTyyppi hakukohdeTyyppi;

    private String hakukohdeNimi;
    private String hakukohdeKoodi;
    private String hakukohdeTila;

    private Button hakukohdeBtn;


    public HakuHakukohdeResultRow(HakukohdeTyyppi hakukohde) {
        this.hakukohdeTyyppi = hakukohde;
        setFields();

    }

    private void setFields() {
        if (this.hakukohdeTyyppi != null) {
          List<KoodiType> koodis = tarjontaUIHelper.getKoodis(this.hakukohdeTyyppi.getHakukohdeNimi());

          hakukohdeNimi = tarjontaUIHelper.getKoodiNimi(this.hakukohdeTyyppi.getHakukohdeNimi());
          if (koodis != null) {
              try {
              hakukohdeKoodi = koodis.get(0).getMetadata().get(0).getNimi();
              hakukohdeKoodi = "(" + hakukohdeKoodi + ")";
              } catch (NullPointerException nullikka) {
                  hakukohdeKoodi = "";
              }
          }

          hakukohdeTila = hakukohdeTyyppi.getHakukohteenTila().value();

              hakukohdeNimi = hakukohdeNimi + ", " + hakukohdeKoodi + ", " + hakukohdeTila;


        }

        hakukohdeBtn = UiUtil.buttonLink(null,hakukohdeNimi, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

            }
        });
        hakukohdeBtn.setStyleName("link-row");

    }


    public Button getHakukohdeBtn() {
        return hakukohdeBtn;
    }
}
