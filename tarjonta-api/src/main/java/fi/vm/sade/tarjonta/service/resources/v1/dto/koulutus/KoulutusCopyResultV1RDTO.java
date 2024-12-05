package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KoulutusCopyResultV1RDTO implements Serializable {

  private String fromOid;
  private List<KoulutusCopyStatusV1RDTO> to = new ArrayList<KoulutusCopyStatusV1RDTO>();

  public KoulutusCopyResultV1RDTO() {}

  public KoulutusCopyResultV1RDTO(String fromOid) {
    this.fromOid = fromOid;
  }

  public String getFromOid() {
    return fromOid;
  }

  public void setFromOid(String fromOid) {
    this.fromOid = fromOid;
  }

  public List<KoulutusCopyStatusV1RDTO> getTo() {
    return to;
  }

  public void setTo(List<KoulutusCopyStatusV1RDTO> to) {
    this.to = to;
  }
}
