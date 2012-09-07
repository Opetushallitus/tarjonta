package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import fi.vm.sade.tarjonta.ui.enums.UiMarginEnum;
import fi.vm.sade.vaadin.oph.layout.AbstractHorizontalLayout;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import fi.vm.sade.vaadin.Oph;

/**
 *
 * @author jani
 */
public class MainSearchView extends AbstractHorizontalLayout {

    private Button btnTyhjenna;
    private ComboBox cbHaunKohdejoukko;
    private ComboBox cbHakutyyppi;
    private ComboBox cbHakutapa;
    private ComboBox cbKoulutuksenAlkamiskausi;
    private ComboBox cbHakukausi;
    private TextField tfSearch;

    public MainSearchView() {
        super(true, UiMarginEnum.RIGHT_BOTTOM_LEFT);
        buildLayout();
    }

    private void buildLayout() {
        tfSearch = new TextField("");
        tfSearch.addStyleName(Oph.TEXTFIELD_SEARCH);
        tfSearch.setImmediate(false);
        this.addComponent(tfSearch);
        
        Button btnHaku = UiBuilder.newButton("Search", this);
        btnHaku.addStyleName(Oph.BUTTON_SMALL);

        //TODO: Koulutuksen alkamiskausi tekstille oma style!!!!
        
        cbKoulutuksenAlkamiskausi = UiBuilder.newComboBox(i18n.getMessage("KoulutuksenAlkamiskausi"), new String[]{"Syksy 2012"}, this);
        cbHakukausi = UiBuilder.newComboBox(i18n.getMessage("Hakukausi"), new String[]{"Kevätkausi"}, this);
        cbHakutapa = UiBuilder.newComboBox(i18n.getMessage("Hakutapa"), new String[]{"Kaikki"}, this);
        cbHakutyyppi = UiBuilder.newComboBox(i18n.getMessage("Hakutyyppi"), new String[]{"Kaikki"}, this);
        cbHaunKohdejoukko = UiBuilder.newComboBox(i18n.getMessage("Kohdejoukko"), new String[]{"Kaikki"}, this);
        btnTyhjenna = UiBuilder.newButton(i18n.getMessage("Tyhjennä"), this);
        btnTyhjenna.addStyleName(Oph.BUTTON_SMALL);

        this.setComponentAlignment(btnHaku, Alignment.BOTTOM_LEFT);
        this.setComponentAlignment(cbKoulutuksenAlkamiskausi, Alignment.TOP_RIGHT);

        this.setComponentAlignment(btnTyhjenna, Alignment.BOTTOM_RIGHT);
        this.setExpandRatio(cbKoulutuksenAlkamiskausi, 1f); //default == 0
    }
}
