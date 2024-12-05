package fi.vm.sade.tarjonta.service.auditlog;

class TarjontaResource {

  private final String name;

  TarjontaResource(String name) {
    this.name = name;
  }

  String name() {
    return name;
  }
}
