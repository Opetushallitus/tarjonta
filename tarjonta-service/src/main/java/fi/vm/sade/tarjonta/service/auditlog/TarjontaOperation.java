package fi.vm.sade.tarjonta.service.auditlog;

import fi.vm.sade.auditlog.Operation;

class TarjontaOperation implements Operation {

    private final String name;

    TarjontaOperation(String name){
        super();
        this.name = name;
    }
    @Override
    public String name() {
        return name;
    }
}
