package fi.vm.sade.tarjonta.ui.model;

/**
 * @author: Tuomas Katva
 * Date: 4/9/13
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
            alinPistemaara = "0";
        }

        return alinPistemaara;
    }

    public void setAlinPistemaara(String alinPistemaara) {
        this.alinPistemaara = alinPistemaara;
    }

    public String getYlinPistemaara() {
        if (ylinPistemaara == null) {
            ylinPistemaara = "0";
        }

        return ylinPistemaara;
    }

    public void setYlinPistemaara(String ylinPistemaara) {
        this.ylinPistemaara = ylinPistemaara;
    }

    public String getAlinHyvaksyttyPistemaara() {
        if (alinHyvaksyttyPistemaara == null ) {
            alinHyvaksyttyPistemaara = "0";
        }
        return alinHyvaksyttyPistemaara;
    }

    public void setAlinHyvaksyttyPistemaara(String alinHyvaksyttyPistemaara) {
        this.alinHyvaksyttyPistemaara = alinHyvaksyttyPistemaara;
    }
}
