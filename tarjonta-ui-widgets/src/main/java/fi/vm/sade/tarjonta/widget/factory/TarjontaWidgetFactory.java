package fi.vm.sade.tarjonta.widget.factory;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliSearchDTO;
import fi.vm.sade.tarjonta.service.KoulutusmoduuliAdminService;
import fi.vm.sade.tarjonta.widget.KoulutusmoduuliComponent;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Factory for creating tarjonta Vaadin components. Note that this factory is not singleton, you must instantiate it and set applicationContext via setter or
 * autowiring.
 *
 * Usage: watch usage from TarjontaWidgetFactoryTest -testcase
 *
 * @author Antti Salonen
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
//@Component
public class TarjontaWidgetFactory implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private KoulutusmoduuliAdminService koulutusmoduuliService;

    private static TarjontaWidgetFactory instance;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        TarjontaWidgetFactory.applicationContext = applicationContext;
    }

    public TarjontaWidgetFactory() {
        instance = this;
    }

    public static TarjontaWidgetFactory getInstance() {
        return instance;
    }

    /**
     * Creates KoulutusmoduuliComponent wrapper component - NOTE! set field to component after calling this
     *
     * @param searchSpecification parameter for KoulutusmoduuliAdminService.find
     */
    public KoulutusmoduuliComponent createKoulutusmoduuliComponent(KoulutusmoduuliSearchDTO searchSpecification) {
        ensureInitialized();
        KoulutusmoduuliComponent wrapper = new KoulutusmoduuliComponent(searchSpecification);
        wrapper.setKoulutusmoduuliService(koulutusmoduuliService);
        return wrapper;
    }

    /**
     * Creates KoulutusmoduuliComponent wrapper component, which has preconfigured combobox as field
     *
     * @param searchSpecification parameter for KoulutusmoduuliAdminService.find
     */
    public KoulutusmoduuliComponent createKoulutusmoduuliComponentWithCombobox(KoulutusmoduuliSearchDTO searchSpecification) {
        KoulutusmoduuliComponent wrapper = createKoulutusmoduuliComponent(searchSpecification);
        ComboBox combo = new ComboBox();
        combo.setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
        wrapper.setField(combo);
        return wrapper;
    }

    /**
     * Assign the service to be used for retrieving data. Normally this does not need to be called since service is 
     * auto injected. This may be used e.g. for junit testing.
     * 
     * @param koulutusmoduuliService
     */
    public void setKoulutusmoduuliService(KoulutusmoduuliAdminService koulutusmoduuliService) {
        this.koulutusmoduuliService = koulutusmoduuliService;
    }

    private void ensureInitialized() {
        if (koulutusmoduuliService == null) {
            if (applicationContext == null) {
                throw new NullPointerException("TarjontaWidgetFactory.applicationContext not set");
            }
            koulutusmoduuliService = applicationContext.getBean(KoulutusmoduuliAdminService.class);
        }
    }

}

