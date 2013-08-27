package fi.vm.sade.tarjonta.service.resources.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Jussi Jartamo
 * 
 *         Sivutettua hakukohteiden hakua varten tulos DTO
 */
public class HakukohdeTulosRDTO implements Serializable {

    private int kokonaismaara;
    private List<HakukohdeNimiRDTO> tulokset;

    public HakukohdeTulosRDTO() {
    }

    public HakukohdeTulosRDTO(int kokonaismaara, List<HakukohdeNimiRDTO> tulokset) {
        this.kokonaismaara = kokonaismaara;
        this.tulokset = tulokset;
    }

    public int getKokonaismaara() {
        return kokonaismaara;
    }

    public List<HakukohdeNimiRDTO> getTulokset() {
        return tulokset;
    }
}
