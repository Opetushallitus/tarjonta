package fi.vm.sade.tarjonta.ui.model;

public class PainotettavaOppiaineViewModel {
    private String oppiaine;
    private int painokerroin;
    private String painotettavaOppiaineTunniste;

    public PainotettavaOppiaineViewModel() {
    }

    public PainotettavaOppiaineViewModel(final String oppiaine, final int painokerroin,
            final String painotettavaOppiaineTunniste) {
        this.oppiaine = oppiaine;
        this.painokerroin = painokerroin;
        this.painotettavaOppiaineTunniste = painotettavaOppiaineTunniste;
    }

    public String getOppiaine() {
        return oppiaine;
    }

    public int getPainokerroin() {
        return painokerroin;
    }

    public String getPainotettavaOppiaineTunniste() {
        return painotettavaOppiaineTunniste;
    }

    public void setOppiaine(String oppiaine) {
        this.oppiaine = oppiaine;
    }

    public void setPainokerroin(int painokerroin) {
        this.painokerroin = painokerroin;
    }

    public void setPainotettavaOppiaineTunniste(String painotettavaOppiaineTunniste) {
        this.painotettavaOppiaineTunniste = painotettavaOppiaineTunniste;
    }

}
