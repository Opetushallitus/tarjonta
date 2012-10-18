/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.enums;

import fi.vm.sade.generic.common.I18N;

/**
 *
 * @author jani
 */
public enum UserNotification {

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