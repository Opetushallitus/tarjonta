package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KomoLink {

  public KomoLink() {}

  public KomoLink(String parent, String... children) {
    setParent(parent);
    setChildren(Arrays.asList(children));
  }

  public String getParent() {
    return parent;
  }

  public void setParent(String parent) {
    this.parent = parent;
  }

  public List<String> getChildren() {
    return children;
  }

  public void setChildren(List<String> children) {
    this.children = children;
  }

  private String parent;
  private List<String> children = new ArrayList<String>();
}
