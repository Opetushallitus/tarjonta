package fi.vm.sade.tarjonta.ui.model.valinta;

import fi.vm.sade.tarjonta.ui.enums.MetaCategory;
import fi.vm.sade.tarjonta.ui.model.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class ValintaModel extends BaseUIViewModel {
    
    private String forwardToUri;
    private boolean forward = false;
    
    private Map<MetaCategory, ValintaperusteModel> map = new EnumMap<MetaCategory, ValintaperusteModel>(MetaCategory.class);

    /**
     * @return the map
     */
    public ValintaperusteModel getKuvausModelByCategory(MetaCategory category) {
        return map.get(category);
    }

    /**
     * @return the map
     */
    public Map<MetaCategory, ValintaperusteModel> getMap() {
        return map;
    }

    /**
     * @param map the map to set
     */
    public void setMap(Map<MetaCategory, ValintaperusteModel> map) {
        this.map = map;
    }

    /**
     * @return the forwardToUri
     */
    public String getForwardToUri() {
        return forwardToUri;
    }

    /**
     * @param forwardToUri the forwardToUri to set
     */
    public void setForwardToUri(String forwardToUri) {
        this.forwardToUri = forwardToUri;
    }

    /**
     * @return the forward
     */
    public boolean isForward() {
        return forward;
    }

    /**
     * @param forward the forward to set
     */
    public void setForward(boolean forward) {
        this.forward = forward;
    }
}
