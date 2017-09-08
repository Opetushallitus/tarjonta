/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */

package fi.vm.sade.tarjonta.service.impl.resources.v1.process;

import fi.vm.sade.tarjonta.service.resources.v1.dto.ProcessV1RDTO;

/**
 *
 * @author mlyly
 */
public interface ProcessDefinition extends Runnable {
        /**
         * @return state as ProcessV1RDTO
         */
        ProcessV1RDTO getState();

        /**
         * @param state initial state and parameters to the process
         */
        void setState(ProcessV1RDTO state);

        /**
         * Return true is process has been completed and can be removed from "active" process list.
         *
         * @return
         */
        boolean isCompleted();
}
