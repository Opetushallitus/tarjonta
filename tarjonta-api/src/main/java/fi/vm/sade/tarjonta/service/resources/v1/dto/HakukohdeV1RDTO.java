package fi.vm.sade.tarjonta.service.resources.v1.dto;

import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeLiiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.TekstiRDTO;

import java.util.*;

/*
* @author: Tuomas Katva 10/11/13
*/
public class HakukohdeV1RDTO extends BaseV1RDTO {

    private String hakukohteenNimi;

    private String hakukohteenNimiUri;

    private List<TekstiRDTO> hakukohteenNimet;

    private Set<String> tarjoajaOids;

    private String hakuOid;

    private List<String> hakukelpoisuusvaatimusUris;

    private List<String> hakukohdeKoulutusOids;

    private Date hakuaikaAlkuPvm;

    private Date hakuaikaLoppuPvm;

    private double alinHyvaksyttavaKeskiarvo;

    private int alinValintaPistemaara;

    private int aloituspaikatLkm;

    private int edellisenVuodenHakijatLkm;

    private int valintojenAloituspaikatLkm;

    private int ylinValintapistemaara;

    private String sahkoinenToimitusOsoite;

    private String soraKuvausKoodiUri;

    private String tila;

    private String valintaperustekuvausKoodiUri;

    private Date liitteidenToimitusPvm;

    private List<TekstiRDTO> lisatiedot;

    private List<TekstiRDTO> valintaperusteKuvaukset;

    private boolean kaytetaanJarjestelmanValintaPalvelua;

    private boolean kaytetaanHaunPaattymisenAikaa;

    private boolean kaytetaanHakukohdekohtaistaHakuaikaa;

    private List<HakukohdeLiiteRDTO> hakukohteenLiitteet;

    private OsoiteRDTO liitteidenToimitusOsoite;

    private List<ValintakoeV1RDTO> valintakokeet;



    public String getHakukohteenNimi() {
        return hakukohteenNimi;
    }

    public void setHakukohteenNimi(String hakukohteenNimi) {
        this.hakukohteenNimi = hakukohteenNimi;
    }

    public List<TekstiRDTO> getHakukohteenNimet() {
        if (hakukohteenNimet == null) {
            hakukohteenNimet = new ArrayList<TekstiRDTO>();
        }
        return hakukohteenNimet;
    }

    public void setHakukohteenNimet(List<TekstiRDTO> hakukohteenNimet) {
        this.hakukohteenNimet = hakukohteenNimet;
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

    public List<TekstiRDTO> getLisatiedot() {

        if (lisatiedot == null) {
            lisatiedot = new ArrayList<TekstiRDTO>();
        }

        return lisatiedot;
    }

    public void setLisatiedot(List<TekstiRDTO> lisatiedot) {
        this.lisatiedot = lisatiedot;
    }

    public List<TekstiRDTO> getValintaperusteKuvaukset() {
        if (valintaperusteKuvaukset == null){
            valintaperusteKuvaukset = new ArrayList<TekstiRDTO>();
        }
        return valintaperusteKuvaukset;
    }

    public void setValintaperusteKuvaukset(List<TekstiRDTO> valintaperusteKuvaukset) {
        this.valintaperusteKuvaukset = valintaperusteKuvaukset;
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

    public List<HakukohdeLiiteRDTO> getHakukohteenLiitteet() {
        if (hakukohteenLiitteet == null) {
            hakukohteenLiitteet = new ArrayList<HakukohdeLiiteRDTO>();
        }
        return hakukohteenLiitteet;
    }

    public void setHakukohteenLiitteet(List<HakukohdeLiiteRDTO> hakukohteenLiitteet) {
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
}
