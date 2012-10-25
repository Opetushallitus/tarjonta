package fi.vm.sade.tarjonta.ui.model;

import fi.vm.sade.tarjonta.service.types.tarjonta.HakuTyyppi;
import java.util.List;
import java.util.ArrayList;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;

public class HakukohdeViewModel extends BaseUIViewModel {

    private String oid;
    private String organisaatioOid;
    private String hakukohdeNimi;
    private String tunnisteKoodi;
    private HakuViewModel haku;
    private int aloitusPaikat;
    private String hakukohdeTila;
    private String hakukelpoisuusVaatimus;
    private List<KielikaannosViewModel> valintaPerusteidenKuvaus;
    private List<KielikaannosViewModel> lisatiedot;
    private List<String> komotoOids;

    public HakukohdeViewModel() {
        super();
    }

    public HakukohdeViewModel(String hakukohdeNimi, String organisaatioOid) {
        super();
        this.hakukohdeNimi = hakukohdeNimi;
        this.organisaatioOid = organisaatioOid;
    }

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
    public HakuViewModel getHakuOid() {
        return haku;
    }

    /**
     * @param hakuOid the hakuOid to set
     */
    public void setHakuOid(HakuViewModel hakuOid) {
        this.haku = hakuOid;
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

    public String getOrganisaatioOid() {
        return organisaatioOid;
    }

    public void setOrganisaatioOid(String organisaatioOid) {
        this.organisaatioOid = organisaatioOid;
    }

    /**
     * @return the hakukohdeTila
     */
    public String getHakukohdeTila() {
        return hakukohdeTila;
    }

    /**
     * @param hakukohdeTila the hakukohdeTila to set
     */
    public void setHakukohdeTila(String hakukohdeTila) {
        this.hakukohdeTila = hakukohdeTila;
    }

    /**
     * @return the oid
     */
    public String getOid() {
        return oid;
    }

    /**
     * @param oid the oid to set
     */
    public void setOid(String oid) {
        this.oid = oid;
    }

    /**
     * @return the komotoOids
     */
    public List<String> getKomotoOids() {
        if (komotoOids == null) {
            komotoOids = new ArrayList<String>();
        }
        return komotoOids;
    }

    /**
     * @param komotoOids the komotoOids to set
     */
    public void setKomotoOids(List<String> komotoOids) {
        this.komotoOids = komotoOids;
    }
}
