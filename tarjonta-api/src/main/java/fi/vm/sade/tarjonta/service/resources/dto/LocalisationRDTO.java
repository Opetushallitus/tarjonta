package fi.vm.sade.tarjonta.service.resources.dto;

public class LocalisationRDTO extends BaseRDTO {

  private String key;
  private String locale;
  private String value;

  public LocalisationRDTO() {
    super();
  }

  public LocalisationRDTO(String key, String locale, String value) {
    this();

    this.key = key;
    this.locale = locale;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
