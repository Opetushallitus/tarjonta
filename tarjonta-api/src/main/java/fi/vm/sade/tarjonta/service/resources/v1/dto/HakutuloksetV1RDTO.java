package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.util.ArrayList;
import java.util.List;

public class HakutuloksetV1RDTO<T extends KoulutusHakutulosV1RDTO> extends BaseV1RDTO {

  private static final long serialVersionUID = 1L;

  private List<TarjoajaHakutulosV1RDTO<T>> tulokset;
  private int tuloksia;

  public List<TarjoajaHakutulosV1RDTO<T>> getTulokset() {
    if (tulokset == null) {
      tulokset = new ArrayList<TarjoajaHakutulosV1RDTO<T>>();
    }
    return tulokset;
  }

  public void setTulokset(List<TarjoajaHakutulosV1RDTO<T>> tulokset) {
    this.tulokset = tulokset;
  }

  public int getTuloksia() {
    return tuloksia;
  }

  public void setTuloksia(int tuloksia) {
    this.tuloksia = tuloksia;
  }
}
