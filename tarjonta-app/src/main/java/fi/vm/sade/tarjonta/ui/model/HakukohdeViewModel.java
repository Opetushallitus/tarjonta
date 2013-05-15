package fi.vm.sade.tarjonta.ui.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

import fi.vm.sade.tarjonta.service.types.TarjontaTila;

public class HakukohdeViewModel extends BaseUIViewModel {

    private static final long serialVersionUID = 1L;
    private String oid;
    private String organisaatioOid;
    private String hakukohdeNimi;
    private String hakukohdeKoodistoNimi;
    private String tunnisteKoodi;
    private HakuViewModel haku;
    private HakuaikaViewModel hakuaika;
    private int aloitusPaikat;
    private int valinnoissaKaytettavatPaikat;
    private TarjontaTila tila;
    private String hakukelpoisuusVaatimus;
    private boolean sahkoinenToimitusSallittu;
    private boolean kaytaHaunPaattymisenAikaa;
    private String liitteidenSahkoinenToimitusOsoite;
    private Date liitteidenToimitusPvm;
    private String osoiteRivi1;
    private String osoiteRivi2;
    private String postinumero;
    private String postitoimipaikka;
    private List<KielikaannosViewModel> valintaPerusteidenKuvaus;
    private List<KielikaannosViewModel> lisatiedot;
    private List<String> komotoOids;
    private List<KoulutusOidNameViewModel> koulukses;
    private List<HakukohdeLiiteViewModel> liites;
    private List<ValintakoeViewModel> valintaKokees;
    private List<PainotettavaOppiaineViewModel> painotettavat = Lists.newArrayList();
    private HakukohdeNameUriModel selectedHakukohdeNimi;
    private String alinHyvaksyttavaKeskiarvo;
    private String viimeisinPaivittaja;
    private Date viimeisinPaivitysPvm;


    public List<PainotettavaOppiaineViewModel> getPainotettavat() {
        return painotettavat;
    }

    public void setPainotettavat(List<PainotettavaOppiaineViewModel> painotettavat) {
        this.painotettavat = painotettavat;
    }

    public String getAlinHyvaksyttavaKeskiarvo() {
        return alinHyvaksyttavaKeskiarvo;
    }

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
    
    public HakuaikaViewModel getHakuaika() {
		return hakuaika;
	}
    
    public void setHakuaika(HakuaikaViewModel hakuaika) {
		this.hakuaika = hakuaika;
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
    public TarjontaTila getTila() {
        return tila;
    }

    /**
     * @param hakukohdeTila the hakukohdeTila to set
     */
    public void setTila(TarjontaTila hakukohdeTila) {
        this.tila = hakukohdeTila;
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

    /**
     * @return the hakukohdeKoodistoNimi
     */
    public String getHakukohdeKoodistoNimi() {
        return hakukohdeKoodistoNimi;
    }

    /**
     * @param hakukohdeKoodistoNimi the hakukohdeKoodistoNimi to set
     */
    public void setHakukohdeKoodistoNimi(String hakukohdeKoodistoNimi) {
        this.hakukohdeKoodistoNimi = hakukohdeKoodistoNimi;
    }

    public List<KoulutusOidNameViewModel> getKoulukses() {
        if (koulukses == null) {
            koulukses = new ArrayList<KoulutusOidNameViewModel>();
        }
        return koulukses;
    }

    public void setKoulukses(List<KoulutusOidNameViewModel> koulukses) {
        this.koulukses = koulukses;
    }

    public int getValinnoissaKaytettavatPaikat() {
        return valinnoissaKaytettavatPaikat;
    }

    public void setValinnoissaKaytettavatPaikat(int valinnoissaKaytettavatPaikat) {
        this.valinnoissaKaytettavatPaikat = valinnoissaKaytettavatPaikat;
    }

    public String getLiitteidenSahkoinenToimitusOsoite() {
        return liitteidenSahkoinenToimitusOsoite;
    }

    public void setLiitteidenSahkoinenToimitusOsoite(String liitteidenSahkoinenToimitusOsoite) {
        this.liitteidenSahkoinenToimitusOsoite = liitteidenSahkoinenToimitusOsoite;
    }

    public Date getLiitteidenToimitusPvm() {
        return liitteidenToimitusPvm;
    }

    public void setLiitteidenToimitusPvm(Date liitteidenToimitusPvm) {
        this.liitteidenToimitusPvm = liitteidenToimitusPvm;
    }

    public String getOsoiteRivi1() {
        return osoiteRivi1;
    }

    public void setOsoiteRivi1(String osoiteRivi1) {
        this.osoiteRivi1 = osoiteRivi1;
    }

    public String getOsoiteRivi2() {
        return osoiteRivi2;
    }

    public void setOsoiteRivi2(String osoiteRivi2) {
        this.osoiteRivi2 = osoiteRivi2;
    }

    public String getPostinumero() {
        return postinumero;
    }

    public void setPostinumero(String postinumero) {
        this.postinumero = postinumero;
    }

    public String getPostitoimipaikka() {
        return postitoimipaikka;
    }

    public void setPostitoimipaikka(String postitoimipaikka) {
        this.postitoimipaikka = postitoimipaikka;
    }

    public boolean isSahkoinenToimitusSallittu() {
        return sahkoinenToimitusSallittu;
    }

    public void setSahkoinenToimitusSallittu(boolean sahkoinenToimitusSallittu) {
        this.sahkoinenToimitusSallittu = sahkoinenToimitusSallittu;
    }

    public boolean isKaytaHaunPaattymisenAikaa() {
        return kaytaHaunPaattymisenAikaa;
    }

    public void setKaytaHaunPaattymisenAikaa(boolean kaytaHaunPaattymisenAikaa) {
        this.kaytaHaunPaattymisenAikaa = kaytaHaunPaattymisenAikaa;
    }

    public List<HakukohdeLiiteViewModel> getLiites() {
        if (liites == null) {
            liites = new ArrayList<HakukohdeLiiteViewModel>();
        }
        return liites;
    }

    public void setLiites(List<HakukohdeLiiteViewModel> liites) {
        this.liites = liites;
    }

    public List<ValintakoeViewModel> getValintaKokees() {
        if (valintaKokees == null) {
            valintaKokees = new ArrayList<ValintakoeViewModel>();
        }
        return valintaKokees;
    }

    public void setValintaKokees(List<ValintakoeViewModel> valintaKokees) {
        this.valintaKokees = valintaKokees;
    }

    public HakukohdeNameUriModel getSelectedHakukohdeNimi() {
        return selectedHakukohdeNimi;
    }

    public void setSelectedHakukohdeNimi(HakukohdeNameUriModel selectedHakukohdeNimi) {
        this.selectedHakukohdeNimi = selectedHakukohdeNimi;
    }

    public void addPainotettavaOppiaine(PainotettavaOppiaineViewModel painotettava) {
        this.painotettavat.add(painotettava);
    }

    public void setAlinHyvaksyttavaKeskiarvo(String alinHyvaksyttavaKeskiarvo) {
        this.alinHyvaksyttavaKeskiarvo = alinHyvaksyttavaKeskiarvo;
    }

    /**
     * Create new (empty) model
     */
    public static HakukohdeViewModel create() {
        final HakukohdeViewModel model = new HakukohdeViewModel();
        for(int i=0;i<3;i++) {
            model.addPainotettavaOppiaine(new PainotettavaOppiaineViewModel());
        }

        
        return model;
    }


    public String getViimeisinPaivittaja() {
        return viimeisinPaivittaja;
    }

    public void setViimeisinPaivittaja(String viimeisinPaivittaja) {
        this.viimeisinPaivittaja = viimeisinPaivittaja;
    }

    public Date getViimeisinPaivitysPvm() {
        return viimeisinPaivitysPvm;
    }

    public void setViimeisinPaivitysPvm(Date viimeisinPaivitysPvm) {
        this.viimeisinPaivitysPvm = viimeisinPaivitysPvm;
    }
}
