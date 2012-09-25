package fi.vm.sade.tarjonta.ui.enums;

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
