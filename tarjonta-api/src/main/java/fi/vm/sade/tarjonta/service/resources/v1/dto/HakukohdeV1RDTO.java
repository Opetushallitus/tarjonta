package fi.vm.sade.tarjonta.service.resources.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusIdentification;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
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
    private Map<String, KoulutusmoduuliTarjoajatiedotV1RDTO> koulutusmoduuliToteutusTarjoajatiedot;
    private Set<String> tarjoajaOids;
    @ApiModelProperty(value = "Hakukohde's haku's oid", required = true)
    private String hakuOid;
    @ApiModelProperty(value = "Hakukohde's haku's inner application period", required = false)
    private String hakuaikaId;
    private List<String> hakukelpoisuusvaatimusUris;
    private List<String> opintoOikeusUris;
    @ApiModelProperty(value = "Hakukohde's related koulutus oids", required = true)
    private List<String> hakukohdeKoulutusOids;
    @ApiModelProperty(value = "Hakukohde's related koulutukset (same as hakukohdeKoulutusOids but different format)")
    private Set<KoulutusIdentification> koulutukset;
    private Date hakuaikaAlkuPvm;
    private Date hakuaikaLoppuPvm;
    private Double alinHyvaksyttavaKeskiarvo;
    private Integer alinValintaPistemaara;
    private Integer ylinValintapistemaara;
    @ApiModelProperty(value = "Hakukohde's aloituspaikat amount", required = true)
    private Integer aloituspaikatLkm;
    private Integer edellisenVuodenHakijatLkm;
    private Integer valintojenAloituspaikatLkm;
    private String sahkoinenToimitusOsoite;
    private String soraKuvausKoodiUri;
    @ApiModelProperty(value = "Hakukohde's state", required = true, allowableValues = "LUONNOS,VALMIS,JULKAISTU,PERUTTU,KOPIOITU")
    private TarjontaTila tila;
    private String valintaperustekuvausKoodiUri;
    private Date liitteidenToimitusPvm;
    private String ulkoinenTunniste;
    @ApiModelProperty(value = "Oppilaitoksen globaalisti uniikki tunniste hakukohteelle", required = false)
    private String uniqueExternalId;
    private String koulutusAsteTyyppi;
    private ToteutustyyppiEnum toteutusTyyppi;
    private String koulutusmoduuliTyyppi;
    private String koulutuslaji;
    private Map<String, String> lisatiedot;
    private Map<String, String> valintaperusteKuvaukset;
    private Map<String, String> soraKuvaukset;
    private Map<String, String> hakukelpoisuusVaatimusKuvaukset;
    private Map<String, String> aloituspaikatKuvaukset;
    private Boolean kaytetaanJarjestelmanValintaPalvelua;
    private Boolean kaytetaanHaunPaattymisenAikaa;
    private Boolean kaytetaanHakukohdekohtaistaHakuaikaa;
    private List<HakukohdeLiiteV1RDTO> hakukohteenLiitteet;
    private List<YhteystiedotV1RDTO> yhteystiedot;
    private OsoiteRDTO liitteidenToimitusOsoite;
    private List<ValintakoeV1RDTO> valintakokeet;
    private Long valintaPerusteKuvausTunniste;
    private Boolean kaksoisTutkinto;
    private Long soraKuvausTunniste;
    private Set<String> opetusKielet;
    private Set<String> valintaPerusteKuvausKielet;
    private Set<String> soraKuvausKielet;
    private List<PainotettavaOppiaineV1RDTO> painotettavatOppiaineet;
    private Map<String, String> hakuMenettelyKuvaukset;
    private Map<String, String> peruutusEhdotKuvaukset;
    private List<RyhmaliitosV1RDTO> ryhmaliitokset;
    private String[] organisaatioRyhmaOids;
    private Boolean ylioppilastutkintoAntaaHakukelpoisuuden;
    private String kelaLinjaKoodi;
    private String kelaLinjaTarkenne;
    private Integer ensikertalaistenAloituspaikat;
    @ApiModelProperty(value = "Liitepyyntöjen pohjakoulutusvaatimukset")
    private List<String> pohjakoulutusliitteet;
    @ApiModelProperty(value = "Jos ylioppilastutkinto tai kansainvälinen ylioppilastutkinto, ei muiden tutkintojen liitepyyntöjä.")
    private Boolean josYoEiMuitaLiitepyyntoja;
    @ApiModelProperty(value = "Hakulomakkeen www-osoite")
    private String hakulomakeUrl;
    @ApiModelProperty(value = "Atarulomakkeen avain")
    private String ataruLomakeAvain;
    @ApiModelProperty(value = "Onko haulle asetettu hakulomakkeen url ylikirjoitettu hakukohdekohtaisella urlilla")
    private Boolean overridesHaunHakulomakeUrl;
    private String ohjeetUudelleOpiskelijalle;
    @ApiModelProperty(value = "Onko yhden paikan sääntö voimassa hakukohteelle ja miksi", required = true)
    private HakuV1RDTO.YhdenPaikanSaanto yhdenPaikanSaanto;
    private Boolean tutkintoonJohtava;
    private String koulutuksenAlkamiskausiUri;
    private Integer koulutuksenAlkamisvuosi;
    private String pohjakoulutusvaatimus;

    public HakuV1RDTO.YhdenPaikanSaanto getYhdenPaikanSaanto() {
        return yhdenPaikanSaanto;
    }

    public void setYhdenPaikanSaanto(HakuV1RDTO.YhdenPaikanSaanto yhdenPaikanSaanto) {
        this.yhdenPaikanSaanto = yhdenPaikanSaanto;
    }

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
        return hakukelpoisuusvaatimusUris;
    }

    public void setHakukelpoisuusvaatimusUris(List<String> hakukelpoisuusvaatimusUris) {
        this.hakukelpoisuusvaatimusUris = hakukelpoisuusvaatimusUris;
    }

    public Double getAlinHyvaksyttavaKeskiarvo() {
        return alinHyvaksyttavaKeskiarvo;
    }

    public void setAlinHyvaksyttavaKeskiarvo(Double alinHyvaksyttavaKeskiarvo) {
        this.alinHyvaksyttavaKeskiarvo = alinHyvaksyttavaKeskiarvo;
    }

    public Integer getAlinValintaPistemaara() {
        return alinValintaPistemaara;
    }

    public void setAlinValintaPistemaara(Integer alinValintaPistemaara) {
        this.alinValintaPistemaara = alinValintaPistemaara;
    }

    public Integer getAloituspaikatLkm() {
        return aloituspaikatLkm;
    }

    public void setAloituspaikatLkm(Integer aloituspaikatLkm) {
        this.aloituspaikatLkm = aloituspaikatLkm;
    }

    public Integer getEdellisenVuodenHakijatLkm() {
        return edellisenVuodenHakijatLkm;
    }

    public void setEdellisenVuodenHakijatLkm(Integer edellisenVuodenHakijatLkm) {
        this.edellisenVuodenHakijatLkm = edellisenVuodenHakijatLkm;
    }

    public Integer getValintojenAloituspaikatLkm() {
        return valintojenAloituspaikatLkm;
    }

    public void setValintojenAloituspaikatLkm(Integer valintojenAloituspaikatLkm) {
        this.valintojenAloituspaikatLkm = valintojenAloituspaikatLkm;
    }

    public Integer getYlinValintapistemaara() {
        return ylinValintapistemaara;
    }

    public void setYlinValintapistemaara(Integer ylinValintapistemaara) {
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

    public TarjontaTila getTila() {
        return tila;
    }

    public void setTila(TarjontaTila tila) {
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

    public Boolean getKaytetaanJarjestelmanValintaPalvelua() {
        return kaytetaanJarjestelmanValintaPalvelua;
    }

    public void setKaytetaanJarjestelmanValintaPalvelua(Boolean kaytetaanJarjestelmanValintaPalvelua) {
        this.kaytetaanJarjestelmanValintaPalvelua = kaytetaanJarjestelmanValintaPalvelua;
    }

    public Boolean getKaytetaanHaunPaattymisenAikaa() {
        return kaytetaanHaunPaattymisenAikaa;
    }

    public void setKaytetaanHaunPaattymisenAikaa(Boolean kaytetaanHaunPaattymisenAikaa) {
        this.kaytetaanHaunPaattymisenAikaa = kaytetaanHaunPaattymisenAikaa;
    }

    public Boolean getKaytetaanHakukohdekohtaistaHakuaikaa() {
        return kaytetaanHakukohdekohtaistaHakuaikaa;
    }

    public void setKaytetaanHakukohdekohtaistaHakuaikaa(Boolean kaytetaanHakukohdekohtaistaHakuaikaa) {
        this.kaytetaanHakukohdekohtaistaHakuaikaa = kaytetaanHakukohdekohtaistaHakuaikaa;
    }

    public List<HakukohdeLiiteV1RDTO> getHakukohteenLiitteet() {
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
        return hakukohdeKoulutusOids;
    }

    public void setHakukohdeKoulutusOids(List<String> hakukohdeKoulutusOids) {
        this.hakukohdeKoulutusOids = hakukohdeKoulutusOids;
    }

    public Set<String> getTarjoajaOids() {
        return tarjoajaOids;
    }

    public void setTarjoajaOids(Set<String> tarjoajaOids) {
        this.tarjoajaOids = tarjoajaOids;
    }

    public List<ValintakoeV1RDTO> getValintakokeet() {
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

    public void setSoraKuvaukset(Map<String, String> soraKuvaukset) {
        this.soraKuvaukset = soraKuvaukset;
    }

    public String getHakuaikaId() {
        return hakuaikaId;
    }

    public void setHakuaikaId(String hakuaikaId) {
        this.hakuaikaId = hakuaikaId;
    }

    public Map<String, String> getHakukelpoisuusVaatimusKuvaukset() {
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
        return valintaPerusteKuvausKielet;
    }

    public void setValintaPerusteKuvausKielet(Set<String> valintaPerusteKuvausKielet) {
        this.valintaPerusteKuvausKielet = valintaPerusteKuvausKielet;
    }

    public Set<String> getSoraKuvausKielet() {
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

    public ToteutustyyppiEnum getToteutusTyyppi() {
        return toteutusTyyppi;
    }

    public void setToteutusTyyppi(ToteutustyyppiEnum toteutusTyyppi) {
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
        return ToteutustyyppiEnum.LUKIOKOULUTUS.equals(getToteutusTyyppi());
    }

    @JsonIgnore
    public boolean isAmmatillinenPerustutkinto() {
        return ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO.equals(getToteutusTyyppi());
    }

    public String getKoulutusmoduuliTyyppi() {
        return koulutusmoduuliTyyppi;
    }

    public void setKoulutusmoduuliTyyppi(String koulutusmoduuliTyyppi) {
        this.koulutusmoduuliTyyppi = koulutusmoduuliTyyppi;
    }

    public Map<String, String> getHakuMenettelyKuvaukset() {
        return hakuMenettelyKuvaukset;
    }

    public void setHakuMenettelyKuvaukset(Map<String, String> hakuMenettelyKuvaukset) {
        this.hakuMenettelyKuvaukset = hakuMenettelyKuvaukset;
    }

    public Map<String, String> getPeruutusEhdotKuvaukset() {
        return peruutusEhdotKuvaukset;
    }

    public void setPeruutusEhdotKuvaukset(Map<String, String> peruutusEhdotKuvaukset) {
        this.peruutusEhdotKuvaukset = peruutusEhdotKuvaukset;
    }

    public List<RyhmaliitosV1RDTO> getRyhmaliitokset() {
        return ryhmaliitokset;
    }

    public void setRyhmaliitokset(List<RyhmaliitosV1RDTO> ryhmaliitokset) {
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
        return opintoOikeusUris;
    }

    public void setOpintoOikeusUris(List<String> opintoOikeusUris) {
        this.opintoOikeusUris = opintoOikeusUris;
    }

    public List<String> getPohjakoulutusliitteet() {
        return pohjakoulutusliitteet;
    }

    public void setPohjakoulutusliitteet(List<String> pohjakoulutusliitteet) {
        this.pohjakoulutusliitteet = pohjakoulutusliitteet;
    }

    public Boolean getJosYoEiMuitaLiitepyyntoja() {
        return josYoEiMuitaLiitepyyntoja;
    }

    public void setJosYoEiMuitaLiitepyyntoja(Boolean josYoEiMuitaLiitepyyntoja) {
        this.josYoEiMuitaLiitepyyntoja = josYoEiMuitaLiitepyyntoja;
    }

    public String getHakulomakeUrl() {
        return hakulomakeUrl;
    }

    public void setHakulomakeUrl(String hakulomakeUrl) {
        this.hakulomakeUrl = hakulomakeUrl;
    }

    public String getAtaruLomakeAvain() { return ataruLomakeAvain; }

    public void setAtaruLomakeAvain(String ataruLomakeAvain) { this.ataruLomakeAvain = ataruLomakeAvain; }

    public Boolean getOverridesHaunHakulomakeUrl() {
        return overridesHaunHakulomakeUrl;
    }

    public void setOverridesHaunHakulomakeUrl(Boolean overridesHaunHakulomakeUrl) {
        this.overridesHaunHakulomakeUrl = overridesHaunHakulomakeUrl;
    }

    public Set<KoulutusIdentification> getKoulutukset() {
        return koulutukset;
    }

    public void setKoulutukset(Set<KoulutusIdentification> koulutukset) {
        this.koulutukset = koulutukset;
    }

    public String getUniqueExternalId() {
        return uniqueExternalId;
    }

    public void setUniqueExternalId(String uniqueExternalId) {
        this.uniqueExternalId = uniqueExternalId;
    }

    public String getOhjeetUudelleOpiskelijalle() {
        return ohjeetUudelleOpiskelijalle;
    }

    public void setOhjeetUudelleOpiskelijalle(String ohjeetUudelleOpiskelijalle) {
        this.ohjeetUudelleOpiskelijalle = ohjeetUudelleOpiskelijalle;
    }

    public static HakukohdeV1RDTO defaultDto() {
        HakukohdeV1RDTO dto = new HakukohdeV1RDTO();
        dto.setKoulutusmoduuliToteutusTarjoajatiedot(new HashMap<String, KoulutusmoduuliTarjoajatiedotV1RDTO>());
        dto.setYhteystiedot(new ArrayList<YhteystiedotV1RDTO>());
        dto.setPainotettavatOppiaineet(new ArrayList<PainotettavaOppiaineV1RDTO>());
        dto.setRyhmaliitokset(new ArrayList<RyhmaliitosV1RDTO>());
        dto.setPohjakoulutusliitteet(new ArrayList<String>());
        dto.setHakukelpoisuusvaatimusUris(new ArrayList<String>());
        dto.setHakukohteenLiitteet(new ArrayList<HakukohdeLiiteV1RDTO>());
        dto.setHakukohdeKoulutusOids(new ArrayList<String>());
        dto.setTarjoajaOids(new HashSet<String>());
        dto.setValintakokeet(new ArrayList<ValintakoeV1RDTO>());
        dto.setLisatiedot(new TreeMap<String, String>());
        dto.setValintaPerusteKuvausKielet(new TreeSet<String>());
        dto.setTarjoajaNimet(new HashMap<String, String>());
        dto.setHakuMenettelyKuvaukset(new TreeMap<String, String>());
        dto.setPeruutusEhdotKuvaukset(new TreeMap<String, String>());
        dto.setOpintoOikeusUris(new ArrayList<String>());
        dto.setKaksoisTutkinto(false);
        dto.setKaytetaanJarjestelmanValintaPalvelua(false);
        dto.setKaytetaanHaunPaattymisenAikaa(false);
        dto.setKaytetaanHakukohdekohtaistaHakuaikaa(false);
        dto.setJosYoEiMuitaLiitepyyntoja(false);
        dto.setYlioppilastutkintoAntaaHakukelpoisuuden(false);
        dto.setOverridesHaunHakulomakeUrl(false);
        dto.setAlinHyvaksyttavaKeskiarvo(0.0);
        dto.setAlinValintaPistemaara(0);
        dto.setYlinValintapistemaara(0);
        dto.setAloituspaikatLkm(0);
        dto.setEdellisenVuodenHakijatLkm(0);
        dto.setValintojenAloituspaikatLkm(0);
        return dto;
    }

    public Boolean isTutkintoonJohtava() {
        return tutkintoonJohtava;
    }

    public void setTutkintoonJohtava(Boolean tutkintoonJohtava) {
        this.tutkintoonJohtava = tutkintoonJohtava;
    }

    public String getKoulutuksenAlkamiskausiUri() {
        return koulutuksenAlkamiskausiUri;
    }

    public void setKoulutuksenAlkamiskausiUri(String koulutuksenAlkamiskausiUri) {
        this.koulutuksenAlkamiskausiUri = koulutuksenAlkamiskausiUri;
    }

    public String getPohjakoulutusvaatimus() {
        return pohjakoulutusvaatimus;
    }

    public void setPohjakoulutusvaatimus(String pohjakoulutusvaatimus) {
        this.pohjakoulutusvaatimus = pohjakoulutusvaatimus;
    }

    public Integer getKoulutuksenAlkamisvuosi() {
        return koulutuksenAlkamisvuosi;
    }

    public void setKoulutuksenAlkamisvuosi(Integer koulutuksenAlkamisvuosi) {
        this.koulutuksenAlkamisvuosi = koulutuksenAlkamisvuosi;
    }
}
