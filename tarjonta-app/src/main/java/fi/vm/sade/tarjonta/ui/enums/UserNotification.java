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
package fi.vm.sade.tarjonta.ui.enums;

import fi.vm.sade.generic.common.I18N;

/**
 *
 * @author jani
 */
public enum UserNotification {
    GENERIC_VALIDATION_FAILED("notification.invalidData"),
    
    //failures
    GENERIC_ERROR("notification.error"),
    EDIT_FAILED("notification.error.editFailed"),
    ADD_FAILED("notification.error.addFailed"),
    SAVE_FAILED("notification.error.saveFailed"),
    
    //Success
    GENERIC_SUCCESS("notification.success"),
    SAVE_SUCCESS("notification.success.save"),
    COPY_SUCCESS("notification.success.copy"),
    SAVE_DRAFT_SUCCESS("notification.success.saveDraft"),
    SAVE_EDITED_SUCCESS("notification.success.saveEdited"),
    DELETE_SUCCESS("notification.success.delete");
    private String info;

    UserNotification(String info) {
        this.info = info;
    }

    public String getInfo() {
        return I18N.getMessage(info);
    }
};