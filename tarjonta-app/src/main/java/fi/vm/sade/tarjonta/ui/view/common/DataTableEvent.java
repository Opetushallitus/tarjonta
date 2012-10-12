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
package fi.vm.sade.tarjonta.ui.view.common;

import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;

/**
 *
 * @author jani
 */
public interface DataTableEvent {

    /**
     * Fired when save is pressed.
     */
    public class CancelEvent extends Component.Event {

        public CancelEvent(Component source) {
            super(source);
        }
    }

    /**
     * Fired when cancel is pressed.
     */
    public class SaveEvent extends Component.Event {

        public SaveEvent(Component source) {
            super(source);
        }
    }

    /**
     * Fired when delete is pressed.
     */
    public class DeleteEvent extends Component.Event {

        public DeleteEvent(Component source) {
            super(source);
        }
    }
}
