package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import fi.vm.sade.tarjonta.ui.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.poc.helper.I18NHelper;
import fi.vm.sade.vaadin.oph.enums.UiMarginEnum;
import fi.vm.sade.vaadin.oph.layout.AbstractHorizontalLayout;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import fi.vm.sade.vaadin.Oph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author jani
 */
@Configurable(preConstruction = true)
public class MainSearchView extends AbstractHorizontalLayout {

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

        Button btnHaku = UiBuilder.newButtonSmallPrimary(i18n.getMessage("Hae"), this);
        btnHaku.addStyleName(Oph.BUTTON_SMALL);
        btnHaku.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                _presenter.searchKoulutus();
            }
        });

        cbHakukausi = UiBuilder.newComboBox(i18n.getMessage("Hakukausi"), new String[]{"Kevätkausi"}, this);
        cbHakukausi.setWidth("110px");
        cbKoulutuksenAlkamiskausi = UiBuilder.newComboBox(i18n.getMessage("KoulutuksenAlkamiskausi"), new String[]{"Syksy 2012"}, this);
        cbKoulutuksenAlkamiskausi.setWidth("200px");
        cbHakutapa = UiBuilder.newComboBox(i18n.getMessage("Hakutapa"), new String[]{"Kaikki"}, this);
        cbHakutapa.setWidth("110px");
        cbHakutyyppi = UiBuilder.newComboBox(i18n.getMessage("Hakutyyppi"), new String[]{"Kaikki"}, this);
        cbHakutyyppi.setWidth("110px");
        cbHaunKohdejoukko = UiBuilder.newComboBox(i18n.getMessage("Kohdejoukko"), new String[]{"Kaikki"}, this);
        cbHaunKohdejoukko.setWidth("110px");
        btnTyhjenna = UiBuilder.newButton(i18n.getMessage("Tyhjennä"), this);
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
