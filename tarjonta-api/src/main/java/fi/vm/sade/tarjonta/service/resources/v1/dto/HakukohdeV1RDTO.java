package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.util.*;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;

/*
* @author: Tuomas Katva 10/11/13
*/
@ApiModel(value = "V1 Hakukohde REST-api model, used by KK-ui")
public class HakukohdeV1RDTO extends BaseV1RDTO {

    private String hakukohteenNimi;
    private String hakukohteenNimiUri;
    @ApiModelProperty(value = "Hashmap containing hakukohde names and name language", required=false)
    private HashMap<String,String> hakukohteenNimet;
    private Map<String,String> tarjoajaNimet;
    private Set<String> tarjoajaOids;
    @ApiModelProperty(value = "Hakukohde's haku's oid",required = true)
    private String hakuOid;
    @ApiModelProperty(value = "Hakukohde's haku's inner application period",required = false)
    private String hakuaikaId;
    private List<String> hakukelpoisuusvaatimusUris;
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
    @ApiModelProperty(value = "Hakukohde's state",required = true,allowableValues = "LUONNOS,VALMIS,JULKAISTU,PERUTTU,KOPIOITU")
    private String tila;
    private String valintaperustekuvausKoodiUri;
    private Date liitteidenToimitusPvm;
    private String ulkoinenTunniste;
    private HashMap<String,String> lisatiedot;
    private HashMap<String,String> valintaperusteKuvaukset;
    private HashMap<String,String> soraKuvaukset;
    private HashMap<String,String> hakukelpoisuusVaatimusKuvaukset;
    private boolean kaytetaanJarjestelmanValintaPalvelua;
    private boolean kaytetaanHaunPaattymisenAikaa;
    private boolean kaytetaanHakukohdekohtaistaHakuaikaa;
    private List<HakukohdeLiiteV1RDTO> hakukohteenLiitteet;
    private OsoiteRDTO liitteidenToimitusOsoite;
    private List<ValintakoeV1RDTO> valintakokeet;
    private Long valintaPerusteKuvausTunniste;
    private Boolean kaksoisTutkinto;
    private Long soraKuvausTunniste;
    private Set<String> opetusKielet;
    private Set<String> valintaPerusteKuvausKielet;
    private Set<String> soraKuvausKielet;

    public String getHakukohteenNimi() {
        return hakukohteenNimi;
    }

    public void setHakukohteenNimi(String hakukohteenNimi) {
        this.hakukohteenNimi = hakukohteenNimi;
    }

    /*
    public List<TekstiRDTO> getHakukohteenNimet() {
        if (hakukohteenNimet == null) {
            hakukohteenNimet = new ArrayList<TekstiRDTO>();
        }
        return hakukohteenNimet;
    }

    public void setHakukohteenNimet(List<TekstiRDTO> hakukohteenNimet) {
        this.hakukohteenNimet = hakukohteenNimet;
    }*/

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

    public HashMap<String, String> getHakukohteenNimet() {
        return hakukohteenNimet;
    }

    public void setHakukohteenNimet(HashMap<String, String> hakukohteenNimet) {
        this.hakukohteenNimet = hakukohteenNimet;
    }


    public HashMap<String, String> getLisatiedot() {
        return lisatiedot;
    }

    public void setLisatiedot(HashMap<String, String> lisatiedot) {
        this.lisatiedot = lisatiedot;
    }

    public HashMap<String, String> getValintaperusteKuvaukset() {
        return valintaperusteKuvaukset;
    }

    public void setValintaperusteKuvaukset(HashMap<String, String> valintaperusteKuvaukset) {
        this.valintaperusteKuvaukset = valintaperusteKuvaukset;
    }

    public HashMap<String, String> getSoraKuvaukset() {
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

    public HashMap<String, String> getHakukelpoisuusVaatimusKuvaukset() {
        return hakukelpoisuusVaatimusKuvaukset;
    }

    public void setHakukelpoisuusVaatimusKuvaukset(HashMap<String, String> hakukelpoisuusVaatimusKuvaukset) {
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

    @Deprecated // sama kuin opetuskielet
    public Set<String> getValintaPerusteKuvausKielet() {
        return valintaPerusteKuvausKielet;
    }

    @Deprecated // sama kuin opetuskielet
    public void setValintaPerusteKuvausKielet(Set<String> valintaPerusteKuvausKielet) {
        this.valintaPerusteKuvausKielet = valintaPerusteKuvausKielet;
    }

    @Deprecated // sama kuin opetuskielet
    public Set<String> getSoraKuvausKielet() {
        return soraKuvausKielet;
    }

    @Deprecated // sama kuin opetuskielet
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

    public Boolean getKaksoisTutkinto() {
        return kaksoisTutkinto;
    }

    public void setKaksoisTutkinto(Boolean kaksoisTutkinto) {
        this.kaksoisTutkinto = kaksoisTutkinto;
    }

    public String getUlkoinenTunniste() {
        return ulkoinenTunniste;
    }

    public void setUlkoinenTunniste(String ulkoinenTunniste) {
        this.ulkoinenTunniste = ulkoinenTunniste;
    }
}
