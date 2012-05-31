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
package fi.vm.sade.tarjonta.ui.event;

import com.github.wolfie.blackboard.Event;
import com.github.wolfie.blackboard.Listener;
import com.github.wolfie.blackboard.annotation.ListenerMethod;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;

/**
 *
 * @author Jukka Raanamo
 */
public class KoulutusmoduuliChangedEvent implements Event {

    private EventType eventType;

    private KoulutusmoduuliDTO koulutusmoduuli;

    public KoulutusmoduuliChangedEvent(KoulutusmoduuliDTO koulutusmoduuli, EventType type) {
        this.eventType = type;
        this.koulutusmoduuli = koulutusmoduuli;
    }

    public KoulutusmoduuliDTO getKoulutusmoduuli() {
        return koulutusmoduuli;
    }

    public EventType getEventType() {
        return eventType;
    }

    public enum EventType {

        CREATED,
        MODIFIED;
    }


    public interface KoulutusmoduuliChangedEventListener extends Listener {

        @ListenerMethod
        void onKoulutusmoduuliChanged(KoulutusmoduuliChangedEvent event);

    }


}

