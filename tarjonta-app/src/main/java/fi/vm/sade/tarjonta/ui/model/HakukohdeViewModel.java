package fi.vm.sade.tarjonta.ui.model;

import java.util.List;
import java.util.ArrayList;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;

public class HakukohdeViewModel extends BaseUIViewModel {

    private String hakukohdeNimi;
    
    private String tunnisteKoodi;
    
    private String hakuOid;
    
    private int aloitusPaikat;
    
    private String hakukelpoisuusVaatimus;
    
    private List<KielikaannosViewModel> valintaPerusteidenKuvaus;
    
    private List<KielikaannosViewModel> lisatiedot;

    /**
     * @return the hakukohdeNimi
     */
    public String getHakukohdeNimi() {
        return hakukohdeNimi;
    }

    /**
     * @param hakukohdeNimi the hakukohdeNimi to set
     */
    public void setHakukohdeNimi(String hakukohdeNimi) {
        this.hakukohdeNimi = hakukohdeNimi;
    }

    /**
     * @return the tunnisteKoodi
     */
    public String getTunnisteKoodi() {
        return tunnisteKoodi;
    }

    /**
     * @param tunnisteKoodi the tunnisteKoodi to set
     */
    public void setTunnisteKoodi(String tunnisteKoodi) {
        this.tunnisteKoodi = tunnisteKoodi;
    }

    /**
     * @return the hakuOid
     */
    public String getHakuOid() {
        return hakuOid;
    }

    /**
     * @param hakuOid the hakuOid to set
     */
    public void setHakuOid(String hakuOid) {
        this.hakuOid = hakuOid;
    }

    /**
     * @return the aloitusPaikat
     */
    public int getAloitusPaikat() {
        return aloitusPaikat;
    }

    /**
     * @param aloitusPaikat the aloitusPaikat to set
     */
    public void setAloitusPaikat(int aloitusPaikat) {
        this.aloitusPaikat = aloitusPaikat;
    }

    /**
     * @return the hakukelpoisuusVaatimus
     */
    public String getHakukelpoisuusVaatimus() {
        return hakukelpoisuusVaatimus;
    }

    /**
     * @param hakukelpoisuusVaatimus the hakukelpoisuusVaatimus to set
     */
    public void setHakukelpoisuusVaatimus(String hakukelpoisuusVaatimus) {
        this.hakukelpoisuusVaatimus = hakukelpoisuusVaatimus;
    }

    /**
     * @return the valintaPerusteidenKuvaus
     */
    public List<KielikaannosViewModel> getValintaPerusteidenKuvaus() {
        if (valintaPerusteidenKuvaus == null) {
            valintaPerusteidenKuvaus = new ArrayList<KielikaannosViewModel>();
        }
        return valintaPerusteidenKuvaus;
    }

    /**
     * @return the lisatiedot
     */
    public List<KielikaannosViewModel> getLisatiedot() {
        if (lisatiedot == null) {
            lisatiedot = new ArrayList<KielikaannosViewModel>();
        }
        return lisatiedot;
    }

    
    
}
