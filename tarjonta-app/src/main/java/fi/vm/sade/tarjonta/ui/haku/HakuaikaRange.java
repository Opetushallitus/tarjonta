package fi.vm.sade.tarjonta.ui.haku;

import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;

public class HakuaikaRange extends HorizontalLayout {
    
    private DateField hakuaikaStart;
    private DateField hakuaikaEnd;
    
    public HakuaikaRange() {
        hakuaikaStart = new DateField();
        hakuaikaEnd = new DateField();
        addComponent(hakuaikaStart);
        addComponent(hakuaikaEnd);
    }

}
