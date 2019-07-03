package fi.vm.sade.tarjonta.publication;

import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

public class Tila {

    public static enum Tyyppi {
        KOMO, KOMOTO, HAKU, HAKUKOHDE;
    }

    public Tila(Tyyppi tyyppi, TarjontaTila tila, String oid) {
        this.tyyppi = tyyppi;
        this.tila = tila;
        this.oid = oid;
    }

    public String getOid() {
        return oid;
    }

    public Tyyppi getTyyppi() {
        return tyyppi;
    }

    public TarjontaTila getTila() {
        return tila;
    }

    private final String oid;

    private final Tyyppi tyyppi;

    private final TarjontaTila tila;

    public String toString() {
        return String.format("Kissa Tyyppi: %s, tila: %s, oid: %s ", tyyppi.toString(), tila.toString(), oid);
    }

}
