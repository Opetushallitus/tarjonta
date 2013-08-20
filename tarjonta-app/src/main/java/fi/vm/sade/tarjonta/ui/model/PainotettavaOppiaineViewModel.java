package fi.vm.sade.tarjonta.ui.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class PainotettavaOppiaineViewModel {

    private String oppiaine;
    private Double painokerroin;
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

    public PainotettavaOppiaineViewModel(final String oppiaine, final Double painokerroin,
            final String painotettavaOppiaineTunniste, final Long version) {
        update(oppiaine, painokerroin, version);
        this.painotettavaOppiaineTunniste = painotettavaOppiaineTunniste;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final PainotettavaOppiaineViewModel other = (PainotettavaOppiaineViewModel) obj;

        EqualsBuilder eb = new EqualsBuilder();
        eb.append(version, other.version);
        eb.append(oppiaine, other.oppiaine);
        eb.append(painokerroin, other.painokerroin);
        eb.append(painotettavaOppiaineTunniste, other.painotettavaOppiaineTunniste);
        return eb.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(version)
                .append(oppiaine)
                .append(painokerroin)
                .append(painotettavaOppiaineTunniste)
                .toHashCode();
    }

    public String getOppiaine() {
        return oppiaine;
    }

    public Double getPainokerroin() {
        return painokerroin;
    }

    public String getPainotettavaOppiaineTunniste() {
        return painotettavaOppiaineTunniste;
    }

    public void setOppiaine(String oppiaine) {
        this.oppiaine = oppiaine;
    }

    public void setPainokerroin(Double painokerroin) {
        this.painokerroin = painokerroin;
    }

    public void setPainotettavaOppiaineTunniste(String painotettavaOppiaineTunniste) {
        this.painotettavaOppiaineTunniste = painotettavaOppiaineTunniste;
    }

    public void update(final String oppiaine, final Double painokerroin, final Long version) {
        this.oppiaine = oppiaine;
        this.painokerroin = painokerroin;
        this.version = version;
    }
}
