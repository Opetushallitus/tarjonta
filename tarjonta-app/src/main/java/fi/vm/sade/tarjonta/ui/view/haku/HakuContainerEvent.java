package fi.vm.sade.tarjonta.ui.view.haku;


/**
 * Eventti olio jolla signaloidaan haku-tulospuussa tarvittavat päivitykset.
 */
public final class HakuContainerEvent {

    static enum Type {
        DELETE, UPDATE, CREATE
    }

    final String oid;
    final Type type;

    private HakuContainerEvent(String oid, Type type) {
        this.oid = oid;
        this.type = type;
    }

    public static HakuContainerEvent update(String oid) {
        return new HakuContainerEvent(oid, Type.UPDATE);
    }

    public static HakuContainerEvent delete(String oid) {
        return new HakuContainerEvent(oid, Type.DELETE);
    }

    public static Object create(String oid) {
        return new HakuContainerEvent(oid, Type.CREATE);
    }

}
