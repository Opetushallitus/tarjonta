package fi.vm.sade.tarjonta.service.search;

import org.apache.solr.common.util.NamedList;

public class NamedListUtil {

  private NamedList<Object> list;
  private Object val;

  public static <T> T getValue(NamedList<Object> namedList, String name) {
    return (T) namedList.get(name);
  }

  NamedListUtil(NamedList<Object> list) {
    this.list = list;
  }

  public NamedListUtil get(String name) {
    Object val = list.get(name);
    if (val == null) {
      throw new NullPointerException("can not find " + name + " from list:" + list);
    }
    if (val instanceof NamedList) {
      list = (NamedList) val;
    } else {
      // System.out.println("setting value:" + val + " class:" +
      // val.getClass());
      this.val = val;
    }

    return this;
  }

  public <T> T value() {
    return (T) val;
  }

  public static NamedListUtil from(NamedList<Object> list) {
    return new NamedListUtil(list);
  }
}
