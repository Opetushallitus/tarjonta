package fi.vm.sade.tarjonta.poc.ui.enums;

/**
 *
 * @author jani
 */
public enum TarjontaStyles {

    CATEGORY_TREE("category-tree");
    
    private String styleName;

    TarjontaStyles(String styleName) {
        this.styleName = styleName;
    }

    /**
     * @return the cssClass
     */
    public String getStyleName() {
        return styleName;
    }
}
