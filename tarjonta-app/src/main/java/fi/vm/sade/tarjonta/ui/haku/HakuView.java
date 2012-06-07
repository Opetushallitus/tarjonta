package fi.vm.sade.tarjonta.ui.haku;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class HakuView extends HorizontalLayout {
    
    private HakuEditForm hakuForm;
    Label hakuListing;
    
    public HakuView() {
        hakuListing = new Label("TODO: hakulistaus!!!");
        addComponent(hakuListing);
        hakuForm = new HakuEditForm();
        addComponent(hakuForm);
    }
}
