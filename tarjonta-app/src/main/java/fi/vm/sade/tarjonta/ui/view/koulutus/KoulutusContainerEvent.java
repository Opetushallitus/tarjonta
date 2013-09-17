package fi.vm.sade.tarjonta.ui.view.koulutus;


/**
 * Eventti olio jolla signaloidaan koulutustulospuussa tarvittavat päivitykset.
 */
public final class KoulutusContainerEvent {

    static enum Type {
        DELETE, UPDATE, CREATE
    }

    final String oid;
    final Type type;

    private KoulutusContainerEvent(String oid, Type type) {
        this.oid = oid;
        this.type = type;
    }

    public static KoulutusContainerEvent update(String oid) {
        return new KoulutusContainerEvent(oid, Type.UPDATE);
    }

    public static KoulutusContainerEvent delete(String oid) {
        return new KoulutusContainerEvent(oid, Type.DELETE);
    }

    public static Object create(String oid) {
        return new KoulutusContainerEvent(oid, Type.CREATE);
    }

}
