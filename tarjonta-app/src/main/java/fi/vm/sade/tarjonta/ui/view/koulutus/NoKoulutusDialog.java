package fi.vm.sade.tarjonta.ui.view.koulutus;


import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.vaadin.util.UiUtil;

public class NoKoulutusDialog extends VerticalLayout {
    
    private static final long serialVersionUID = 5101782142842009621L;
    private Button.ClickListener closeListener;
    
    private transient I18NHelper i18n = new I18NHelper(this);
    
    public NoKoulutusDialog(String messageName,Button.ClickListener clickListener) {
        this.closeListener = clickListener;
        createLayout(messageName);
    }
    
    private void createLayout(String messageName) {
        setSizeFull();
        setSpacing(true);
        setMargin(true);
        Label label = UiUtil.label(null, T(messageName));
        addComponent(label);
        label.setSizeFull();

        Button button = UiUtil.button(null, T("sulje"), this.closeListener);
        addComponent(button);
        setComponentAlignment(button, Alignment.BOTTOM_CENTER);
    }
    
    private String T(String key, Object... args) {
        return i18n.getMessage(key, args);
    }

}
