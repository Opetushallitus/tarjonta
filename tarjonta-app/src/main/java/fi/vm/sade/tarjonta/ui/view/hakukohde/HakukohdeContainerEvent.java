package fi.vm.sade.tarjonta.ui.view.hakukohde;


/**
 * Eventti olio jolla signaloidaan hakutulospuussa tarvittavat päivitykset.
 */
public final class HakukohdeContainerEvent {

    static enum Type {
        REMOVE, UPDATE
    }

    final String oid;
    final Type type;

    private HakukohdeContainerEvent(String oid, Type type) {
        this.oid = oid;
        this.type = type;
    }

    public static HakukohdeContainerEvent update(String oid) {
        return new HakukohdeContainerEvent(oid, Type.UPDATE);
    }

    public static HakukohdeContainerEvent delete(String oid) {
        return new HakukohdeContainerEvent(oid, Type.REMOVE);
    }

}
