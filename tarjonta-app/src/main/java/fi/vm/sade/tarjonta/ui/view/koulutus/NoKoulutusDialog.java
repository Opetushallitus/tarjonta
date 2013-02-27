package fi.vm.sade.tarjonta.ui.view.koulutus;


import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.vaadin.util.UiUtil;

public class NoKoulutusDialog extends VerticalLayout {
    
    private static final long serialVersionUID = 5101782142842009621L;
    private Button.ClickListener closeListener;
    
    private transient I18NHelper i18n = new I18NHelper(this);
    
    public NoKoulutusDialog(Button.ClickListener clickListener) {
        this.closeListener = clickListener;
        createLayout();
    }
    
    private void createLayout() {
        setSizeUndefined();
        setSpacing(true);
        setMargin(true);
        UiUtil.label(this, T("viesti"));
        UiUtil.button(this, T("sulje"), this.closeListener);
    }
    
    private String T(String key, Object... args) {
        return i18n.getMessage(key, args);
    }

}
