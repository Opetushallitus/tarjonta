package fi.vm.sade.tarjonta.service.resources.v1.dto;

import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TilaV1RDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private boolean mutable;
  private boolean cancellable;
  private boolean removable;
  private boolean _public;

  private List<TarjontaTila> transitions = new ArrayList<TarjontaTila>();

  public boolean isPublic() {
    return _public;
  }

  public void setPublic(boolean _public) {
    this._public = _public;
  }

  public boolean isMutable() {
    return mutable;
  }

  public void setMutable(boolean mutable) {
    this.mutable = mutable;
  }

  public boolean isCancellable() {
    return cancellable;
  }

  public void setCancellable(boolean cancellable) {
    this.cancellable = cancellable;
  }

  public boolean isRemovable() {
    return removable;
  }

  public void setRemovable(boolean removable) {
    this.removable = removable;
  }

  public List<TarjontaTila> getTransitions() {
    return transitions;
  }

  public void setTransitions(List<TarjontaTila> transitions) {
    this.transitions = transitions;
  }
}
