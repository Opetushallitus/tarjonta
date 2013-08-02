package fi.vm.sade.tarjonta.ui.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author: Tuomas Katva Date: 4/9/13
 */
public class PisterajaRow {

    private String pisteRajaTyyppi;
    private String alinPistemaara;
    private String ylinPistemaara;
    private String alinHyvaksyttyPistemaara;

    public String getPisteRajaTyyppi() {
        return pisteRajaTyyppi;
    }

    public void setPisteRajaTyyppi(String pisteRajaTyyppi) {
        this.pisteRajaTyyppi = pisteRajaTyyppi;
    }

    public String getAlinPistemaara() {
        if (alinPistemaara == null) {
            alinPistemaara = "";//Oli "0"
        }

        return alinPistemaara;
    }

    public void setAlinPistemaara(String alinPistemaara) {
        this.alinPistemaara = alinPistemaara;
    }

    public String getYlinPistemaara() {
        if (ylinPistemaara == null) {
            ylinPistemaara = "";
        }

        return ylinPistemaara;
    }

    public void setYlinPistemaara(String ylinPistemaara) {
        this.ylinPistemaara = ylinPistemaara;
    }

    public String getAlinHyvaksyttyPistemaara() {
        if (alinHyvaksyttyPistemaara == null) {
            alinHyvaksyttyPistemaara = "";
        }
        return alinHyvaksyttyPistemaara;
    }

    public void setAlinHyvaksyttyPistemaara(String alinHyvaksyttyPistemaara) {
        this.alinHyvaksyttyPistemaara = alinHyvaksyttyPistemaara;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final PisterajaRow other = (PisterajaRow) obj;

        EqualsBuilder eb = new EqualsBuilder();
        eb.append(pisteRajaTyyppi, other.pisteRajaTyyppi);
        eb.append(alinPistemaara, other.alinPistemaara);
        eb.append(ylinPistemaara, other.ylinPistemaara);
        eb.append(alinHyvaksyttyPistemaara, other.alinHyvaksyttyPistemaara);
        return eb.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(pisteRajaTyyppi)
                .append(alinPistemaara)
                .append(ylinPistemaara)
                .append(alinHyvaksyttyPistemaara)
                .toHashCode();
    }
}
