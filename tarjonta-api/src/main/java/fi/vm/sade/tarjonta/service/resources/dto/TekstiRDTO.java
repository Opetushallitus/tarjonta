package fi.vm.sade.tarjonta.service.resources.dto;

/*
* @author: Tuomas Katva 10/11/13
*/
public class TekstiRDTO {

    private String uri;

    private String nimi;

    private String teksti;


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTeksti() {
        return teksti;
    }

    public void setTeksti(String teksti) {
        this.teksti = teksti;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TekstiRDTO that = (TekstiRDTO) o;

        if (nimi != null ? !nimi.equals(that.nimi) : that.nimi != null) return false;
        if (!teksti.equals(that.teksti)) return false;
        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (nimi != null ? nimi.hashCode() : 0);
        result = 31 * result + teksti.hashCode();
        return result;
    }

}
