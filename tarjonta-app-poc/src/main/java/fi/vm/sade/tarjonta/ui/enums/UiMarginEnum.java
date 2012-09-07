package fi.vm.sade.tarjonta.ui.enums;

/**
 *
 * @author jani
 */
public enum UiMarginEnum {

    BOTTOM(new Boolean[]{false, false, true, false}),
    LEFT(new Boolean[]{false, false, false, true}),
    RIGHT(new Boolean[]{false, true, false, false}),
    TOP(new Boolean[]{true, false, false, false}),
    TOP_LEFT(new Boolean[]{true, false, false, true}),
    LEFT_RIGHT(new Boolean[]{false, true, false, true}),
    BOTTOM_LEFT(new Boolean[]{false, false, true, true}),
    RIGHT_BOTTOM_LEFT(new Boolean[]{false, true, true, true}),
    
    ALL(new Boolean[]{true}),
    NONE(new Boolean[]{false});
    
    private Boolean[] boolArray;

    UiMarginEnum(Boolean[] styleName) {
        this.boolArray = styleName;
    }

    /**
     * @return the cssClass
     */
    public Boolean[] getSelectedValue() {
        return boolArray;
    }
}
