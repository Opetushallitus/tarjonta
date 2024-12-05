package fi.vm.sade.tarjonta.shared.types;

import java.util.TreeSet;

public class Tilamuutokset {

  private TreeSet<String> muutetutHakukohteet = new TreeSet<String>();

  private TreeSet<String> muutetutKomotot = new TreeSet<String>();

  public TreeSet<String> getMuutetutHakukohteet() {
    return muutetutHakukohteet;
  }

  public void setMuutetutHakukohteet(TreeSet<String> muutetutHakukohteet) {
    this.muutetutHakukohteet = muutetutHakukohteet;
  }

  public TreeSet<String> getMuutetutKomotot() {
    return muutetutKomotot;
  }

  public void setMuutetutKomotot(TreeSet<String> muutetutKomotot) {
    this.muutetutKomotot = muutetutKomotot;
  }
}
