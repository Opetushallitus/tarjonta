package fi.vm.sade.tarjonta.ui.model;

public class PainotettavaOppiaineViewModel {
    private String oppiaine;
    private Integer painokerroin;
    private String painotettavaOppiaineTunniste;
    private Long version;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public PainotettavaOppiaineViewModel() {
    }

    public PainotettavaOppiaineViewModel(final String oppiaine, final int painokerroin,
            final String painotettavaOppiaineTunniste, final Long version) {
        this.oppiaine = oppiaine;
        this.painokerroin = painokerroin;
        this.painotettavaOppiaineTunniste = painotettavaOppiaineTunniste;
        this.version = version;
    }

    public String getOppiaine() {
        return oppiaine;
    }

    public Integer getPainokerroin() {
        return painokerroin;
    }

    public String getPainotettavaOppiaineTunniste() {
        return painotettavaOppiaineTunniste;
    }

    public void setOppiaine(String oppiaine) {
        this.oppiaine = oppiaine;
    }

    public void setPainokerroin(Integer painokerroin) {
        this.painokerroin = painokerroin;
    }

    public void setPainotettavaOppiaineTunniste(String painotettavaOppiaineTunniste) {
        this.painotettavaOppiaineTunniste = painotettavaOppiaineTunniste;
    }

}
