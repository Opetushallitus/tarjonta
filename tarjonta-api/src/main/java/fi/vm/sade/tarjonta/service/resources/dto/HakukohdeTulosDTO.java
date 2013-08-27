package fi.vm.sade.tarjonta.service.resources.dto;

import java.util.List;

/**
 * 
 * @author Jussi Jartamo
 * 
 *         Sivutettua hakukohteiden hakua varten tulos DTO
 */
public class HakukohdeTulosDTO {

    private int _kokonaismaara;
    private List<HakukohdeDTO> _tulokset;

    public HakukohdeTulosDTO() {
    }

    public HakukohdeTulosDTO(int kokonaismaara, List<HakukohdeDTO> tulokset) {
        this._kokonaismaara = kokonaismaara;
        this._tulokset = tulokset;
    }

    public int getKokonaismaara() {
        return _kokonaismaara;
    }

    public List<HakukohdeDTO> getTulokset() {
        return _tulokset;
    }
}
