package fi.vm.sade.tarjonta.ui.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.google.common.collect.Lists;

import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;

public class HakukohdeViewModel extends BaseUIViewModel {

    private static final long serialVersionUID = 1L;
    public transient static final int OPPIAINEET_MAX = 3;
    private Long version;
    private String oid;
    private String organisaatioOid;
    private String hakukohdeKoodistoNimi;
    private String tunnisteKoodi;
    private HakuViewModel hakuViewModel; //selected haku
    private HakuaikaViewModel hakuaika;
    private int aloitusPaikat;
    private int valinnoissaKaytettavatPaikat;
    private TarjontaTila tila;
    private String hakukelpoisuusVaatimus;
    private boolean sahkoinenToimitusSallittu;
    private boolean kaytaHaunPaattymisenAikaa;
    private String liitteidenSahkoinenToimitusOsoite;
    private Date liitteidenToimitusPvm;
    private String osoiteRivi1 = "";
    private String osoiteRivi2 = "";
    private String postinumero;
    private String postitoimipaikka = "";
    private LinkitettyTekstiModel valintaPerusteidenKuvaus;
    private LinkitettyTekstiModel soraKuvaus;
    private List<KielikaannosViewModel> lisatiedot;
    private List<String> komotoOids;
    private List<KoulutusOidNameViewModel> koulukses;
    private List<HakukohdeLiiteViewModel> liites;
    private List<ValintakoeViewModel> valintaKokees;
    private List<PainotettavaOppiaineViewModel> painotettavat;
    private HakukohdeNameUriModel selectedHakukohdeNimi; //combobox
    private String editedHakukohdeNimi; //text field
    private String alinHyvaksyttavaKeskiarvo;
    private String viimeisinPaivittaja;
    private Date viimeisinPaivitysPvm;
    private KoulutusasteTyyppi koulutusasteTyyppi;
    private boolean kaksoisTutkinto = false;
    private Set<String> opetusKielet;
    
    private boolean customHakuaikaEnabled;
    private Date hakuaikaAlkuPvm;
    private Date hakuaikaLoppuPvm;
        
    public HakukohdeViewModel() {
        super();
        initialize();
    }

    public void clearModel() {
        initialize();
    }

    private void initialize() {
        setVersion(null);
        setOid(null);
        setOrganisaatioOid(null);
        setHakukohdeKoodistoNimi(null);
        setTunnisteKoodi(null);
        setHakuaika(null);
        setHakukelpoisuusVaatimus(null);
        setSahkoinenToimitusSallittu(false);
        setKaytaHaunPaattymisenAikaa(false);
        setLiitteidenSahkoinenToimitusOsoite(null);
        setLiitteidenToimitusPvm(null);
        setOsoiteRivi1(null);
        setOsoiteRivi2(null);
        setPostinumero(null);
        setPostitoimipaikka(null);
        setKomotoOids(null);
        setSelectedHakukohdeNimi(null);
        setAlinHyvaksyttavaKeskiarvo(null);
        setViimeisinPaivittaja(null);
        setViimeisinPaivitysPvm(null);
        setEditedHakukohdeNimi(null);

        /*
         * Not null values.
         */
        setAloitusPaikat(0);
        setValinnoissaKaytettavatPaikat(0);
        setTila(TarjontaTila.LUONNOS);
        setHakuViewModel(null);
        this.valintaPerusteidenKuvaus = new LinkitettyTekstiModel();
        soraKuvaus = new LinkitettyTekstiModel();
        this.lisatiedot = Lists.<KielikaannosViewModel>newArrayList();
        this.koulukses = Lists.<KoulutusOidNameViewModel>newArrayList();
        this.liites = Lists.<HakukohdeLiiteViewModel>newArrayList();
        this.valintaKokees = Lists.<ValintakoeViewModel>newArrayList();
        this.painotettavat = Lists.<PainotettavaOppiaineViewModel>newArrayList();
        
        customHakuaikaEnabled = false;
        hakuaikaAlkuPvm = null;
        hakuaikaLoppuPvm = null;

        addPainotettavaOppiainees(OPPIAINEET_MAX);
    }
    
    public void setOpetusKielet(Set<String> opetusKielet) {
		this.opetusKielet = opetusKielet;
	}
    
    public Set<String> getOpetusKielet() {
		return opetusKielet;
	}

    public void addPainotettavaOppiainees(int createObjects) {
        for (int i = 0; i < createObjects; i++) {
            addPainotettavaOppiaine(new PainotettavaOppiaineViewModel());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        HakukohdeViewModel other = (HakukohdeViewModel) obj;

        EqualsBuilder eb = new EqualsBuilder();
        eb.append(version, other.version);
        eb.append(oid, other.oid);
        eb.append(organisaatioOid, other.organisaatioOid);
        eb.append(hakukohdeKoodistoNimi, other.hakukohdeKoodistoNimi);
        eb.append(tunnisteKoodi, other.tunnisteKoodi);
        eb.append(hakuViewModel, other.hakuViewModel);
        eb.append(hakuaika, other.hakuaika);
        eb.append(aloitusPaikat, other.aloitusPaikat);
        eb.append(valinnoissaKaytettavatPaikat, other.valinnoissaKaytettavatPaikat);
        eb.append(tila, other.tila);
        eb.append(hakukelpoisuusVaatimus, other.hakukelpoisuusVaatimus);
        eb.append(sahkoinenToimitusSallittu, other.sahkoinenToimitusSallittu);
        eb.append(kaytaHaunPaattymisenAikaa, other.kaytaHaunPaattymisenAikaa);
        eb.append(liitteidenSahkoinenToimitusOsoite, other.liitteidenSahkoinenToimitusOsoite);
        eb.append(liitteidenToimitusPvm, other.liitteidenToimitusPvm);
        eb.append(osoiteRivi1, other.osoiteRivi1);
        eb.append(osoiteRivi2, other.osoiteRivi2);
        eb.append(postinumero, other.postinumero);
        eb.append(postitoimipaikka, other.postitoimipaikka);
        eb.append(valintaPerusteidenKuvaus, other.valintaPerusteidenKuvaus);
        eb.append(lisatiedot, other.lisatiedot);
        eb.append(komotoOids, other.komotoOids);
        eb.append(koulukses, other.koulukses);
        eb.append(liites, other.liites);
        eb.append(valintaKokees, other.valintaKokees);
        eb.append(painotettavat, other.painotettavat);
        eb.append(selectedHakukohdeNimi, other.selectedHakukohdeNimi);
        eb.append(alinHyvaksyttavaKeskiarvo, other.alinHyvaksyttavaKeskiarvo);
        eb.append(viimeisinPaivittaja, other.viimeisinPaivittaja);
        eb.append(viimeisinPaivitysPvm, other.viimeisinPaivitysPvm);
        eb.append(editedHakukohdeNimi, other.editedHakukohdeNimi);
        return eb.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder()
        .append(version)
        .append(oid)
        .append(organisaatioOid)
        .append(hakukohdeKoodistoNimi)
        .append(tunnisteKoodi)
        .append(hakuViewModel)
        .append(hakuaika)
        .append(aloitusPaikat)
        .append(valinnoissaKaytettavatPaikat)
        .append(tila)
        .append(hakukelpoisuusVaatimus)
        .append(sahkoinenToimitusSallittu)
        .append(kaytaHaunPaattymisenAikaa)
        .append(liitteidenSahkoinenToimitusOsoite)
        .append(liitteidenToimitusPvm)
        .append(osoiteRivi1)
        .append(osoiteRivi2)
        .append(postinumero)
        .append(postitoimipaikka)
        .append(valintaPerusteidenKuvaus)
        //.append(lisatiedot)
        .append(komotoOids)
        .append(koulukses)
        .append(liites)
        .append(valintaKokees)
        .append(painotettavat)
        .append(selectedHakukohdeNimi)
        .append(alinHyvaksyttavaKeskiarvo)
        .append(viimeisinPaivittaja)
        .append(viimeisinPaivitysPvm)
        .append(soraKuvaus)
        .append(valintaPerusteidenKuvaus)
        .append(editedHakukohdeNimi);
        
        /*if (lisatiedot != null) {
            for (KielikaannosViewModel curKaannos : lisatiedot) {
                builder = builder.append(curKaannos.getKielikoodi())
                                  .append(curKaannos.getNimi());
            }
        }*/
        
        return builder.toHashCode();
    }

    public boolean isCustomHakuaikaEnabled() {
		return customHakuaikaEnabled;
	}

	public void setCustomHakuaikaEnabled(boolean customHakuaikaEnabled) {
		this.customHakuaikaEnabled = customHakuaikaEnabled;
	}

	public Date getHakuaikaAlkuPvm() {
		return hakuaikaAlkuPvm;
	}

	public void setHakuaikaAlkuPvm(Date hakuaikaAlkuPvm) {
		this.hakuaikaAlkuPvm = hakuaikaAlkuPvm;
	}

	public Date getHakuaikaLoppuPvm() {
		return hakuaikaLoppuPvm;
	}

	public void setHakuaikaLoppuPvm(Date hakuaikaLoppuPvm) {
		this.hakuaikaLoppuPvm = hakuaikaLoppuPvm;
	}

	public List<PainotettavaOppiaineViewModel> getPainotettavat() {
        return painotettavat;
    }

    public void setPainotettavat(List<PainotettavaOppiaineViewModel> painotettavat) {
        this.painotettavat = painotettavat;
    }

    public String getAlinHyvaksyttavaKeskiarvo() {
        return alinHyvaksyttavaKeskiarvo;
    }

    public HakukohdeViewModel(String hakukohdeNimi, String organisaatioOid) {
        super();
        this.organisaatioOid = organisaatioOid;
    }

    /**
     * @return the hakukohdeNimi
     */
    public String getHakukohdeNimi() { 
        return selectedHakukohdeNimi.getKoodiUriWithVersion();
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
     * @return the hakuViewModel
     */
    public HakuViewModel getHakuViewModel() {
        return hakuViewModel;
    }

    /**
     * @param hakuViewModel the hakuViewModel to set
     */
    public void setHakuViewModel(HakuViewModel hakuViewModel) {
        this.hakuViewModel = hakuViewModel;
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

    public LinkitettyTekstiModel getSoraKuvaus() {
    	if (soraKuvaus==null) {
    		soraKuvaus = new LinkitettyTekstiModel();
    	}
		return soraKuvaus;
	}
    
    public void setSoraKuvaus(LinkitettyTekstiModel soraKuvaus) {
		this.soraKuvaus = soraKuvaus;
	}
    
    public LinkitettyTekstiModel getValintaPerusteidenKuvaus() {
    	if (valintaPerusteidenKuvaus==null) {
    		valintaPerusteidenKuvaus = new LinkitettyTekstiModel();
    	}
		return valintaPerusteidenKuvaus;
	}
    
    public void setValintaPerusteidenKuvaus(
			LinkitettyTekstiModel valintaPerusteidenKuvaus) {
		this.valintaPerusteidenKuvaus = valintaPerusteidenKuvaus;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * @return the koulutusasteTyyppi
     */
    public KoulutusasteTyyppi getKoulutusasteTyyppi() {
        return koulutusasteTyyppi;
    }

    /**
     * @param koulutusasteTyyppi the koulutusasteTyyppi to set
     */
    public void setKoulutusasteTyyppi(KoulutusasteTyyppi koulutusasteTyyppi) {
        this.koulutusasteTyyppi = koulutusasteTyyppi;
    }

    public String getEditedHakukohdeNimi() {
        return editedHakukohdeNimi;
    }

    public void setEditedHakukohdeNimi(String editedHakukohdeNimi) {
        this.editedHakukohdeNimi = editedHakukohdeNimi;
    }
    
    public boolean isKaksoisTutkinto() {
        return kaksoisTutkinto;
    }

    public void setKaksoisTutkinto(boolean kaksoisTutkinto) {
        this.kaksoisTutkinto = kaksoisTutkinto;
    }
}
