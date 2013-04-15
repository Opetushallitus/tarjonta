/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.poc.ui.enums;

/**
 *
 * @author jani
 */
public enum Notification {
    GENERIC_ERROR("Toimintoa ei voitu suorittaa."),
    EDIT_FAILED("Muokkausta ei voitu suorittaa."),
    ADD_FAILED("Lisäystä ei voitu suorittaa."),
    SAVE_FAILED("Tallennus epäonnistui"),
   
    COPY("Kopiointi onnistui."),
    SAVE("Tallennus onnistui."),
    SAVE_DRAFT("Luonnoksen tallennus onnistui."),
    SAVE_EDITED("Muutoksen tallennus onnistui."),
    DELETE("Poisto onnistui.");
    private String info;

    Notification(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
};