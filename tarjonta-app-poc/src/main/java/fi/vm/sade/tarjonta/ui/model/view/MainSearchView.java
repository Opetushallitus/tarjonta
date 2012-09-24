package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import fi.vm.sade.tarjonta.ui.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.poc.helper.I18NHelper;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.ui.OphHorizontalLayout;
import fi.vm.sade.vaadin.util.UiUtil;
import fi.vm.sade.vaadin.Oph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author jani
 */
@Configurable(preConstruction = true)
public class MainSearchView extends OphHorizontalLayout {

    private Button btnTyhjenna;
    private ComboBox cbHaunKohdejoukko;
    private ComboBox cbHakutyyppi;
    private ComboBox cbHakutapa;
    private ComboBox cbKoulutuksenAlkamiskausi;
    private ComboBox cbHakukausi;
    private TextField tfSearch;
    @Autowired
    private TarjontaPresenter _presenter;
    private I18NHelper i18n = new I18NHelper(this);

    public MainSearchView() {
        super(true, UiMarginEnum.RIGHT_BOTTOM_LEFT);
        this.setHeight(-1, UNITS_PIXELS);
        buildLayout();
    }

    private void buildLayout() {
        tfSearch = new TextField("");
        tfSearch.addStyleName(Oph.TEXTFIELD_SEARCH);
        tfSearch.setImmediate(false);
        this.addComponent(tfSearch);

        Button btnHaku = UiUtil.buttonSmallPrimary(this, i18n.getMessage("Hae"));
        btnHaku.addStyleName(Oph.BUTTON_SMALL);
        btnHaku.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                _presenter.searchKoulutus();
            }
        });

        cbHakukausi = UiUtil.comboBox(this, i18n.getMessage("Hakukausi"), new String[]{"Kevätkausi"});
        cbHakukausi.setWidth("110px");
        cbKoulutuksenAlkamiskausi = UiUtil.comboBox(this,i18n.getMessage("KoulutuksenAlkamiskausi"), new String[]{"Syksy 2012"});
        cbKoulutuksenAlkamiskausi.setWidth("200px");
        cbHakutapa = UiUtil.comboBox(this,i18n.getMessage("Hakutapa"), new String[]{"Kaikki"});
        cbHakutapa.setWidth("110px");
        cbHakutyyppi = UiUtil.comboBox(this,i18n.getMessage("Hakutyyppi"), new String[]{"Kaikki"});
        cbHakutyyppi.setWidth("110px");
        cbHaunKohdejoukko = UiUtil.comboBox(this,i18n.getMessage("Kohdejoukko"), new String[]{"Kaikki"});
        cbHaunKohdejoukko.setWidth("110px");
        btnTyhjenna = UiUtil.button(this, i18n.getMessage("Tyhjennä"));
        btnTyhjenna.addStyleName(Oph.BUTTON_SMALL);
        btnTyhjenna.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // TODO tyhjennä --> search all?
                _presenter.selectKoulutusAll();
            }
        });


        this.setComponentAlignment(btnHaku, Alignment.BOTTOM_LEFT);
        this.setComponentAlignment(cbHakukausi, Alignment.TOP_RIGHT);

        this.setComponentAlignment(btnTyhjenna, Alignment.BOTTOM_RIGHT);
        this.setExpandRatio(cbHakukausi, 1f); //default == 0
    }
}
