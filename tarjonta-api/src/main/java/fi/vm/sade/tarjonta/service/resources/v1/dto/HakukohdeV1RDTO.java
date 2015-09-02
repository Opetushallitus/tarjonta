package fi.vm.sade.tarjonta.service.resources.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

import java.util.*;

/*
* @author: Tuomas Katva 10/11/13
*/
@ApiModel(value = "V1 Hakukohde REST-api model, used by KK-ui")
public class HakukohdeV1RDTO extends BaseV1RDTO {

    private static final long serialVersionUID = 1L;

    private String hakukohteenNimi;
    private String hakukohteenNimiUri;
    @ApiModelProperty(value = "Hashmap containing hakukohde names and name language", required = false)
    private Map<String, String> hakukohteenNimet;
    private Map<String, String> tarjoajaNimet;
    private Map<String, KoulutusmoduuliTarjoajatiedotV1RDTO> koulutusmoduuliToteutusTarjoajatiedot = new HashMap<String, KoulutusmoduuliTarjoajatiedotV1RDTO>();
    private Set<String> tarjoajaOids;
    @ApiModelProperty(value = "Hakukohde's haku's oid", required = true)
    private String hakuOid;
    @ApiModelProperty(value = "Hakukohde's haku's inner application period", required = false)
    private String hakuaikaId;
    private List<String> hakukelpoisuusvaatimusUris;
    private List<String> opintoOikeusUris;
    @ApiModelProperty(value = "Hakukohde's related koulutus oids", required = true)
    private List<String> hakukohdeKoulutusOids;
    private Date hakuaikaAlkuPvm;
    private Date hakuaikaLoppuPvm;
    private double alinHyvaksyttavaKeskiarvo;
    private int alinValintaPistemaara;
    private int ylinValintapistemaara;
    @ApiModelProperty(value = "Hakukohde's aloituspaikat amount", required = true)
    private int aloituspaikatLkm;
    private int edellisenVuodenHakijatLkm;
    private int valintojenAloituspaikatLkm;
    private String sahkoinenToimitusOsoite;
    private String soraKuvausKoodiUri;
    @ApiModelProperty(value = "Hakukohde's state", required = true, allowableValues = "LUONNOS,VALMIS,JULKAISTU,PERUTTU,KOPIOITU")
    private String tila;
    private String valintaperustekuvausKoodiUri;
    private Date liitteidenToimitusPvm;
    private String ulkoinenTunniste;
    private String koulutusAsteTyyppi;
    private String toteutusTyyppi;
    private String koulutusmoduuliTyyppi;
    private String koulutuslaji;
    private Map<String, String> lisatiedot;
    private Map<String, String> valintaperusteKuvaukset;
    private Map<String, String> soraKuvaukset;
    private Map<String, String> hakukelpoisuusVaatimusKuvaukset;
    private Map<String, String> aloituspaikatKuvaukset;
    private boolean kaytetaanJarjestelmanValintaPalvelua;
    private boolean kaytetaanHaunPaattymisenAikaa;
    private boolean kaytetaanHakukohdekohtaistaHakuaikaa;
    private List<HakukohdeLiiteV1RDTO> hakukohteenLiitteet;
    private List<YhteystiedotV1RDTO> yhteystiedot = new ArrayList<YhteystiedotV1RDTO>();
    private OsoiteRDTO liitteidenToimitusOsoite;
    private List<ValintakoeV1RDTO> valintakokeet;
    private Long valintaPerusteKuvausTunniste;
    private boolean kaksoisTutkinto;
    private Long soraKuvausTunniste;
    private Set<String> opetusKielet;
    private Set<String> valintaPerusteKuvausKielet;
    private Set<String> soraKuvausKielet;
    private List<PainotettavaOppiaineV1RDTO> painotettavatOppiaineet = new ArrayList<PainotettavaOppiaineV1RDTO>();
    private Map<String, String> hakuMenettelyKuvaukset;
    private Map<String, String> peruutusEhdotKuvaukset;
    private List<RyhmaliitosV1RDTO> ryhmaliitokset = new ArrayList<RyhmaliitosV1RDTO>();
    private String[] organisaatioRyhmaOids;
    private Boolean ylioppilastutkintoAntaaHakukelpoisuuden;
    private String kelaLinjaKoodi;
    private String kelaLinjaTarkenne;
    private Integer ensikertalaistenAloituspaikat;

    public String getHakukohteenNimi() {
        return hakukohteenNimi;
    }

    public void setHakukohteenNimi(String hakukohteenNimi) {
        this.hakukohteenNimi = hakukohteenNimi;
    }

    public String getHakuOid() {
        return hakuOid;
    }

    public void setHakuOid(String hakuOid) {
        this.hakuOid = hakuOid;
    }

    public List<String> getHakukelpoisuusvaatimusUris() {
        if (hakukelpoisuusvaatimusUris == null) {
            hakukelpoisuusvaatimusUris = new ArrayList<String>();
        }

        return hakukelpoisuusvaatimusUris;
    }

    public void setHakukelpoisuusvaatimusUris(List<String> hakukelpoisuusvaatimusUris) {
        this.hakukelpoisuusvaatimusUris = hakukelpoisuusvaatimusUris;
    }

    public double getAlinHyvaksyttavaKeskiarvo() {
        return alinHyvaksyttavaKeskiarvo;
    }

    public void setAlinHyvaksyttavaKeskiarvo(double alinHyvaksyttavaKeskiarvo) {
        this.alinHyvaksyttavaKeskiarvo = alinHyvaksyttavaKeskiarvo;
    }

    public int getAlinValintaPistemaara() {
        return alinValintaPistemaara;
    }

    public void setAlinValintaPistemaara(int alinValintaPistemaara) {
        this.alinValintaPistemaara = alinValintaPistemaara;
    }

    public int getAloituspaikatLkm() {
        return aloituspaikatLkm;
    }

    public void setAloituspaikatLkm(int aloituspaikatLkm) {
        this.aloituspaikatLkm = aloituspaikatLkm;
    }

    public int getEdellisenVuodenHakijatLkm() {
        return edellisenVuodenHakijatLkm;
    }

    public void setEdellisenVuodenHakijatLkm(int edellisenVuodenHakijatLkm) {
        this.edellisenVuodenHakijatLkm = edellisenVuodenHakijatLkm;
    }

    public int getValintojenAloituspaikatLkm() {
        return valintojenAloituspaikatLkm;
    }

    public void setValintojenAloituspaikatLkm(int valintojenAloituspaikatLkm) {
        this.valintojenAloituspaikatLkm = valintojenAloituspaikatLkm;
    }

    public int getYlinValintapistemaara() {
        return ylinValintapistemaara;
    }

    public void setYlinValintapistemaara(int ylinValintapistemaara) {
        this.ylinValintapistemaara = ylinValintapistemaara;
    }

    public String getSahkoinenToimitusOsoite() {
        return sahkoinenToimitusOsoite;
    }

    public void setSahkoinenToimitusOsoite(String sahkoinenToimitusOsoite) {
        this.sahkoinenToimitusOsoite = sahkoinenToimitusOsoite;
    }

    public String getSoraKuvausKoodiUri() {
        return soraKuvausKoodiUri;
    }

    public void setSoraKuvausKoodiUri(String soraKuvausKoodiUri) {
        this.soraKuvausKoodiUri = soraKuvausKoodiUri;
    }

    public String getTila() {
        return tila;
    }

    public void setTila(String tila) {
        this.tila = tila;
    }

    public String getValintaperustekuvausKoodiUri() {
        return valintaperustekuvausKoodiUri;
    }

    public void setValintaperustekuvausKoodiUri(String valintaperustekuvausKoodiUri) {
        this.valintaperustekuvausKoodiUri = valintaperustekuvausKoodiUri;
    }

    public Date getLiitteidenToimitusPvm() {
        return liitteidenToimitusPvm;
    }

    public void setLiitteidenToimitusPvm(Date liitteidenToimitusPvm) {
        this.liitteidenToimitusPvm = liitteidenToimitusPvm;
    }

    public boolean isKaytetaanJarjestelmanValintaPalvelua() {
        return kaytetaanJarjestelmanValintaPalvelua;
    }

    public void setKaytetaanJarjestelmanValintaPalvelua(boolean kaytetaanJarjestelmanValintaPalvelua) {
        this.kaytetaanJarjestelmanValintaPalvelua = kaytetaanJarjestelmanValintaPalvelua;
    }

    public boolean isKaytetaanHaunPaattymisenAikaa() {
        return kaytetaanHaunPaattymisenAikaa;
    }

    public void setKaytetaanHaunPaattymisenAikaa(boolean kaytetaanHaunPaattymisenAikaa) {
        this.kaytetaanHaunPaattymisenAikaa = kaytetaanHaunPaattymisenAikaa;
    }

    public boolean isKaytetaanHakukohdekohtaistaHakuaikaa() {
        return kaytetaanHakukohdekohtaistaHakuaikaa;
    }

    public void setKaytetaanHakukohdekohtaistaHakuaikaa(boolean kaytetaanHakukohdekohtaistaHakuaikaa) {
        this.kaytetaanHakukohdekohtaistaHakuaikaa = kaytetaanHakukohdekohtaistaHakuaikaa;
    }

    public List<HakukohdeLiiteV1RDTO> getHakukohteenLiitteet() {
        if (hakukohteenLiitteet == null) {
            hakukohteenLiitteet = new ArrayList<HakukohdeLiiteV1RDTO>();
        }
        return hakukohteenLiitteet;
    }

    public void setHakukohteenLiitteet(List<HakukohdeLiiteV1RDTO> hakukohteenLiitteet) {
        this.hakukohteenLiitteet = hakukohteenLiitteet;
    }

    public List<YhteystiedotV1RDTO> getYhteystiedot() {
        return yhteystiedot;
    }

    public void setYhteystiedot(List<YhteystiedotV1RDTO> yhteystiedot) {
        this.yhteystiedot = yhteystiedot;
    }

    public OsoiteRDTO getLiitteidenToimitusOsoite() {
        return liitteidenToimitusOsoite;
    }

    public void setLiitteidenToimitusOsoite(OsoiteRDTO liitteidenToimitusOsoite) {
        this.liitteidenToimitusOsoite = liitteidenToimitusOsoite;
    }

    public List<String> getHakukohdeKoulutusOids() {
        if (hakukohdeKoulutusOids == null) {
            hakukohdeKoulutusOids = new ArrayList<String>();
        }
        return hakukohdeKoulutusOids;
    }

    public void setHakukohdeKoulutusOids(List<String> hakukohdeKoulutusOids) {
        this.hakukohdeKoulutusOids = hakukohdeKoulutusOids;
    }

    public Set<String> getTarjoajaOids() {
        if (tarjoajaOids == null) {
            tarjoajaOids = new HashSet<String>();
        }
        return tarjoajaOids;
    }

    public void setTarjoajaOids(Set<String> tarjoajaOids) {
        this.tarjoajaOids = tarjoajaOids;
    }

    public List<ValintakoeV1RDTO> getValintakokeet() {
        if (valintakokeet == null) {
            valintakokeet = new ArrayList<ValintakoeV1RDTO>();
        }
        return valintakokeet;
    }

    public void setValintakokeet(List<ValintakoeV1RDTO> valintakokeet) {
        this.valintakokeet = valintakokeet;
    }


    public String getHakukohteenNimiUri() {
        return hakukohteenNimiUri;
    }

    public void setHakukohteenNimiUri(String hakukohteenNimiUri) {
        this.hakukohteenNimiUri = hakukohteenNimiUri;
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

    public Map<String, String> getHakukohteenNimet() {
        return hakukohteenNimet;
    }

    public void setHakukohteenNimet(Map<String, String> hakukohteenNimet) {
        this.hakukohteenNimet = hakukohteenNimet;
    }


    public Map<String, String> getLisatiedot() {
        if (lisatiedot == null) {
            lisatiedot = new TreeMap<String, String>();
        }
        return lisatiedot;
    }

    public void setLisatiedot(Map<String, String> lisatiedot) {
        this.lisatiedot = lisatiedot;
    }

    public Map<String, String> getValintaperusteKuvaukset() {
        return valintaperusteKuvaukset;
    }

    public void setValintaperusteKuvaukset(Map<String, String> valintaperusteKuvaukset) {
        this.valintaperusteKuvaukset = valintaperusteKuvaukset;
    }

    public Map<String, String> getSoraKuvaukset() {
        return soraKuvaukset;
    }

    public void setSoraKuvaukset(HashMap<String, String> soraKuvaukset) {
        this.soraKuvaukset = soraKuvaukset;
    }

    public String getHakuaikaId() {
        return hakuaikaId;
    }

    public void setHakuaikaId(String hakuaikaId) {
        this.hakuaikaId = hakuaikaId;
    }

    public Map<String, String> getHakukelpoisuusVaatimusKuvaukset() {
        if (hakukelpoisuusVaatimusKuvaukset == null) {
            hakukelpoisuusVaatimusKuvaukset = new TreeMap<String, String>();
        }
        return hakukelpoisuusVaatimusKuvaukset;
    }

    public void setHakukelpoisuusVaatimusKuvaukset(Map<String, String> hakukelpoisuusVaatimusKuvaukset) {
        this.hakukelpoisuusVaatimusKuvaukset = hakukelpoisuusVaatimusKuvaukset;
    }

    public Long getValintaPerusteKuvausTunniste() {
        return valintaPerusteKuvausTunniste;
    }

    public void setValintaPerusteKuvausTunniste(Long valintaPerusteKuvausTunniste) {
        this.valintaPerusteKuvausTunniste = valintaPerusteKuvausTunniste;
    }

    public Long getSoraKuvausTunniste() {
        return soraKuvausTunniste;
    }

    public void setSoraKuvausTunniste(Long soraKuvausTunniste) {
        this.soraKuvausTunniste = soraKuvausTunniste;
    }

    public Set<String> getValintaPerusteKuvausKielet() {
        if (valintaPerusteKuvausKielet == null) {
            valintaPerusteKuvausKielet = new TreeSet<String>();
        }
        return valintaPerusteKuvausKielet;
    }

    public void setValintaPerusteKuvausKielet(Set<String> valintaPerusteKuvausKielet) {
        this.valintaPerusteKuvausKielet = valintaPerusteKuvausKielet;
    }

    public Set<String> getSoraKuvausKielet() {
        if (soraKuvausKielet == null) {
            soraKuvausKielet = new TreeSet<String>();
        }
        return soraKuvausKielet;
    }

    public void setSoraKuvausKielet(Set<String> soraKuvausKielet) {
        this.soraKuvausKielet = soraKuvausKielet;
    }

    public void setOpetusKielet(Set<String> opetusKielet) {
        this.opetusKielet = opetusKielet;
    }

    public Set<String> getOpetusKielet() {
        return opetusKielet;
    }

    public Map<String, String> getTarjoajaNimet() {

        if (tarjoajaNimet == null) {
            tarjoajaNimet = new HashMap<String, String>();
        }

        return tarjoajaNimet;
    }

    public void setTarjoajaNimet(Map<String, String> tarjoajaNimet) {
        this.tarjoajaNimet = tarjoajaNimet;
    }

    public boolean getKaksoisTutkinto() {
        return kaksoisTutkinto;
    }

    public void setKaksoisTutkinto(boolean kaksoisTutkinto) {
        this.kaksoisTutkinto = kaksoisTutkinto;
    }

    public String getUlkoinenTunniste() {
        return ulkoinenTunniste;
    }

    public void setUlkoinenTunniste(String ulkoinenTunniste) {
        this.ulkoinenTunniste = ulkoinenTunniste;
    }

    public String getKoulutusAsteTyyppi() {
        return koulutusAsteTyyppi;
    }

    public void setKoulutusAsteTyyppi(String koulutusAsteTyyppi) {
        this.koulutusAsteTyyppi = koulutusAsteTyyppi;
    }

    public String getKoulutuslaji() {
        return koulutuslaji;
    }

    public void setKoulutuslaji(String koulutuslaji) {
        this.koulutuslaji = koulutuslaji;
    }

    public String getToteutusTyyppi() {
        return toteutusTyyppi;
    }

    public void setToteutusTyyppi(String toteutusTyyppi) {
        this.toteutusTyyppi = toteutusTyyppi;
    }

    public String[] getOrganisaatioRyhmaOids() {
        return organisaatioRyhmaOids;
    }

    public void setOrganisaatioRyhmaOids(String[] organisaatioRyhmat) {
        this.organisaatioRyhmaOids = organisaatioRyhmat;
    }

    public List<PainotettavaOppiaineV1RDTO> getPainotettavatOppiaineet() {
        return painotettavatOppiaineet;
    }

    public void setPainotettavatOppiaineet(List<PainotettavaOppiaineV1RDTO> painotettavatOppiaineet) {
        this.painotettavatOppiaineet = painotettavatOppiaineet;
    }

    public Map<String, KoulutusmoduuliTarjoajatiedotV1RDTO> getKoulutusmoduuliToteutusTarjoajatiedot() {
        return koulutusmoduuliToteutusTarjoajatiedot;
    }

    public void setKoulutusmoduuliToteutusTarjoajatiedot(Map<String, KoulutusmoduuliTarjoajatiedotV1RDTO> koulutusmoduuliToteutusTarjoajatiedot) {
        this.koulutusmoduuliToteutusTarjoajatiedot = koulutusmoduuliToteutusTarjoajatiedot;
    }

    public Map<String, String> getAloituspaikatKuvaukset() {
        return aloituspaikatKuvaukset;
    }

    public void setAloituspaikatKuvaukset(Map<String, String> aloituspaikatKuvaukset) {
        this.aloituspaikatKuvaukset = aloituspaikatKuvaukset;
    }

    @JsonIgnore
    public boolean isLukioKoulutus() {
        return ToteutustyyppiEnum.LUKIOKOULUTUS.equals(ToteutustyyppiEnum.valueOf(getToteutusTyyppi()));
    }

    @JsonIgnore
    public boolean isAmmatillinenPerustutkinto() {
        return ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO.equals(ToteutustyyppiEnum.valueOf(getToteutusTyyppi()));
    }

    public String getKoulutusmoduuliTyyppi() {
        return koulutusmoduuliTyyppi;
    }

    public void setKoulutusmoduuliTyyppi(String koulutusmoduuliTyyppi) {
        this.koulutusmoduuliTyyppi = koulutusmoduuliTyyppi;
    }

    public Map<String, String> getHakuMenettelyKuvaukset() {
        if (hakuMenettelyKuvaukset == null) {
            hakuMenettelyKuvaukset = new TreeMap<String, String>();
        }
        return hakuMenettelyKuvaukset;
    }

    public void setHakuMenettelyKuvaukset(Map<String, String> hakuMenettelyKuvaukset) {
        this.hakuMenettelyKuvaukset = hakuMenettelyKuvaukset;
    }

    public Map<String, String> getPeruutusEhdotKuvaukset() {
        if (peruutusEhdotKuvaukset == null) {
            peruutusEhdotKuvaukset = new TreeMap<String, String>();
        }
        return peruutusEhdotKuvaukset;
    }

    public void setPeruutusEhdotKuvaukset(Map<String, String> peruutusEhdotKuvaukset) {
        this.peruutusEhdotKuvaukset = peruutusEhdotKuvaukset;
    }

    public List<RyhmaliitosV1RDTO> getRyhmaliitokset() {
        return ryhmaliitokset;
    }

    public void setRyhmaliitokset(ArrayList<RyhmaliitosV1RDTO> ryhmaliitokset) {
        this.ryhmaliitokset = ryhmaliitokset;
    }

    public Boolean getYlioppilastutkintoAntaaHakukelpoisuuden() {
        return ylioppilastutkintoAntaaHakukelpoisuuden;
    }

    public void setYlioppilastutkintoAntaaHakukelpoisuuden(Boolean ylioppilastutkintoAntaaHakukelpoisuuden) {
        this.ylioppilastutkintoAntaaHakukelpoisuuden = ylioppilastutkintoAntaaHakukelpoisuuden;
    }

    public String getKelaLinjaTarkenne() {
        return kelaLinjaTarkenne;
    }

    public void setKelaLinjaTarkenne(String kelaLinjaTarkenne) {
        this.kelaLinjaTarkenne = kelaLinjaTarkenne;
    }

    public String getKelaLinjaKoodi() {
        return kelaLinjaKoodi;
    }

    public void setKelaLinjaKoodi(String kelaLinjaKoodi) {
        this.kelaLinjaKoodi = kelaLinjaKoodi;
    }

    public Integer getEnsikertalaistenAloituspaikat() {
        return ensikertalaistenAloituspaikat;
    }

    public void setEnsikertalaistenAloituspaikat(Integer ensikertalaistenAloituspaikat) {
        this.ensikertalaistenAloituspaikat = ensikertalaistenAloituspaikat;
    }

    public List<String> getOpintoOikeusUris() {
        if (opintoOikeusUris == null) {
            return new ArrayList<String>();
        }
        return opintoOikeusUris;
    }

    public void setOpintoOikeusUris(List<String> opintoOikeusUris) {
        this.opintoOikeusUris = opintoOikeusUris;
    }

}
