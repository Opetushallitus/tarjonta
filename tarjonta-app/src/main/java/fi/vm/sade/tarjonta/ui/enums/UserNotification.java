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

import com.vaadin.ui.Window;
import fi.vm.sade.generic.common.I18N;

/**
 *
 * @author jani
 */
public enum UserNotification {

    SERVICE_UNAVAILABLE("notification.error.serviceUnavailable", Window.Notification.TYPE_ERROR_MESSAGE),
    GENERIC_VALIDATION_FAILED("notification.invalidData", Window.Notification.TYPE_WARNING_MESSAGE),
    UNSAVED("notification.error.unsaved", Window.Notification.TYPE_WARNING_MESSAGE),
    //failures
    GENERIC_ERROR("notification.error", Window.Notification.TYPE_WARNING_MESSAGE),
    EDIT_FAILED("notification.error.editFailed", Window.Notification.TYPE_WARNING_MESSAGE),
    ADD_FAILED("notification.error.addFailed", Window.Notification.TYPE_ERROR_MESSAGE),
    SAVE_FAILED("notification.error.saveFailed", Window.Notification.TYPE_ERROR_MESSAGE),
    //Success
    GENERIC_SUCCESS("notification.success", Window.Notification.TYPE_HUMANIZED_MESSAGE),
    COPY_SUCCESS("notification.success.copy", Window.Notification.TYPE_HUMANIZED_MESSAGE),
    SAVE_SUCCESS("notification.success.save", Window.Notification.TYPE_HUMANIZED_MESSAGE),
    SAVE_DRAFT_SUCCESS("notification.success.saveDraft", Window.Notification.TYPE_HUMANIZED_MESSAGE),
    SAVE_EDITED_SUCCESS("notification.success.saveEdited", Window.Notification.TYPE_HUMANIZED_MESSAGE),
    DELETE_SUCCESS("notification.success.delete", Window.Notification.TYPE_HUMANIZED_MESSAGE);
    private String info;
    private int notifiaction;

    UserNotification(String info, int notifiaction) {
        this.info = info;
        this.notifiaction = notifiaction;
    }

    public String getInfo() {
        return I18N.getMessage(info);
    }

    /**
     * @return the notifiaction
     */
    public int getNotifiaction() {
        return notifiaction;
    }
};