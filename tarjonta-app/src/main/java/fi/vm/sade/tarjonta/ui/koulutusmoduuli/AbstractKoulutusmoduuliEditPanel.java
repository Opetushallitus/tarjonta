/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 * 
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.ui.koulutusmoduuli;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.CustomComponent;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import fi.vm.sade.tarjonta.ui.service.TarjontaUiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Base class for editing KoulutusmoduuliDTO. 
 * 
 * @param <T> type of Koulutusmoduuli to edit with this form
 * 
 * @author Jukka Raanamo
 */
@Configurable(preConstruction = true)
public abstract class AbstractKoulutusmoduuliEditPanel<T extends KoulutusmoduuliDTO> extends CustomComponent {
    
    
    @Autowired
    protected TarjontaUiService uiService;

    /**
     * Create BeanItem binding properties from given KoulutusmoduuliDTO.
     *
     * @param koulutusmoduuli
     * @return
     */
    public abstract BeanItem<? extends AbstractKoulutusmoduuliFormModel<T>> createBeanItem(T koulutusmoduuli);

    /**
     * Invoked when user has clicked save and fields have been validated(?). Default behavior is to call service's 
     * save method.
     *
     * @param service
     * @param koulutusmoduuli
     */
    protected void save(TarjontaUiService service, T koulutusmoduuli) {
        service.save(koulutusmoduuli);
    }

}

