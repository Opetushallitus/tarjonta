package fi.vm.sade.vaadin.oph.dto;

import fi.vm.sade.vaadin.oph.enums.LabelStyle;

/**
 *
 * @author jani
 */
public class ButtonDTO extends LabelDTO {

    public ButtonDTO(String caption) {
        super(caption, null, null);
    }

    public ButtonDTO(String caption, LabelStyle style) {
        super(caption, null, style);
    }
}
