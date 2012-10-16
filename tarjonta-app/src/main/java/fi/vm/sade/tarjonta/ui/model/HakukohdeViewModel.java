package fi.vm.sade.tarjonta.ui.model;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.service.types.tarjonta.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.HaunNimi;
import java.util.List;
import java.util.ArrayList;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;

public class HakukohdeViewModel extends BaseUIViewModel {

    private String organisaatioOid;
    private HakuTyyppi haku;
    private String tunnisteKoodi;
    private String hakuOid;
    private int aloitusPaikat;
    private String hakukelpoisuusVaatimus;
    private List<KielikaannosViewModel> valintaPerusteidenKuvaus;
    private List<KielikaannosViewModel> lisatiedot;

    public HakukohdeViewModel() {
        super();
    }

    public HakukohdeViewModel(HakuTyyppi hakukohdeParam, String organisaatioOid) {
        super();
        this.haku = hakukohdeParam;
        this.organisaatioOid = organisaatioOid;
    }
    
    private String tryGetHaunNimi(List<HaunNimi> nimet ) {
        if (nimet != null) {
        String haunNimi = null;
        for (HaunNimi nimi : nimet) {
            if (nimi.getKielikoodi().trim().equalsIgnoreCase(I18N.getLocale().getLanguage().trim())) {
                haunNimi = nimi.getNimi();
            }
        }
        return haunNimi;
        } else {
            return "";
        }
    }
    
    public String getHakukohdeNimi() {
        if (haku != null) {
        return tryGetHaunNimi(haku.getHaunKielistetytNimet());
        } else {
            return "";
        }
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

    public String getOrganisaatioOid() {
        return organisaatioOid;
    }

    public void setOrganisaatioOid(String organisaatioOid) {
        this.organisaatioOid = organisaatioOid;
    }
}
