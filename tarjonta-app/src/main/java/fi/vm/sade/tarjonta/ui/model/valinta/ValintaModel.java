package fi.vm.sade.tarjonta.ui.model.valinta;

import fi.vm.sade.tarjonta.ui.enums.MetaCategory;
import fi.vm.sade.tarjonta.ui.model.*;
import java.util.EnumMap;
import java.util.Map;

public class ValintaModel extends BaseUIViewModel {

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
}
