package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.io.Serializable;
import java.util.Date;

public class GenericSearchParamsV1RDTO implements Serializable {

  private int _startIndex = 0;
  private int _count = 100;
  private long _modifiedBefore = 0;
  private long _modifiedAfter = 0;

  @Override
  public String toString() {
    return "GenericSearchParamsRDTO[startIndex=" + getStartIndex() + ", count=" + getCount() + "]";
  }

  public int getStartIndex() {
    return _startIndex;
  }

  public void setStartIndex(int startIndex) {
    this._startIndex = startIndex;
  }

  public int getCount() {
    return _count;
  }

  public void setCount(int _count) {
    this._count = _count;
  }

  public long getModifiedAfter() {
    return _modifiedAfter;
  }

  public Date getModifiedAfterAsDate() {
    if (getModifiedAfter() > 0) {
      return new Date(getModifiedAfter());
    } else {
      return null;
    }
  }

  public void setModifiedAfter(long modifiedAfter) {
    this._modifiedAfter = modifiedAfter;
  }

  public long getModifiedBefore() {
    return _modifiedBefore;
  }

  public Date getModifiedBeforeAsDate() {
    if (getModifiedBefore() > 0) {
      return new Date(getModifiedBefore());
    } else {
      return null;
    }
  }

  public void setModifiedBefore(long modifiedBefore) {
    this._modifiedBefore = modifiedBefore;
  }
}
