package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Jussi Jartamo
 * 
 *         Sivutettua hakukohteiden hakua varten tulos DTO
 */
public class HakukohdeTulosV1RDTO implements Serializable {

    private int kokonaismaara;
    private List<HakukohdeNimiV1RDTO> tulokset;

    public HakukohdeTulosV1RDTO() {
    }

    public HakukohdeTulosV1RDTO(int kokonaismaara, List<HakukohdeNimiV1RDTO> tulokset) {
        this.kokonaismaara = kokonaismaara;
        this.tulokset = tulokset;
    }

    public int getKokonaismaara() {
        return kokonaismaara;
    }

    public void setKokonaismaara(int kokonaismaara) {
        this.kokonaismaara = kokonaismaara;
    }

    public List<HakukohdeNimiV1RDTO> getTulokset() {
        return tulokset;
    }

    public void setTulokset(List<HakukohdeNimiV1RDTO> tulokset) {
        this.tulokset = tulokset;
    }

}
