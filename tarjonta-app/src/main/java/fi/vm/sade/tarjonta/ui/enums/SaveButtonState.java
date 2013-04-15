/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.enums;

import fi.vm.sade.tarjonta.service.types.TarjontaTila;

/**
 *
 * @author jani
 */
public enum SaveButtonState {

    SAVE_AS_DRAFT, SAVE_AS_READY;

    public TarjontaTila toTarjontaTila(TarjontaTila tila) {
        switch (this) {
            case SAVE_AS_READY:
                return TarjontaTila.JULKAISTU.equals(tila) ? tila : TarjontaTila.VALMIS;
            case SAVE_AS_DRAFT:
                return TarjontaTila.LUONNOS;
            default:
                throw new RuntimeException("Not valid button state.");
        }
    }
}
