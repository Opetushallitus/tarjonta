package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.wordnik.swagger.annotations.ApiModelProperty;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public abstract class TutkintoonJohtamatonKoulutusV1RDTO extends KoulutusV1RDTO {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "Koulutuksen pohjakoulutusvaatimukset (sisältää koodisto koodi uri:a)", required = true)
    private KoodiUrisV1RDTO pohjakoulutusvaatimukset;
    @ApiModelProperty(value = "Valitaan opintojen maksullisuuden (false=koulutus ei vaadi maksua)")
    private Boolean opintojenMaksullisuus;
    @ApiModelProperty(value = "Koulutuksen hinta, on pakollinen jos koulutus on merkitty maksulliseksi")
    private Double hinta;
    @ApiModelProperty(value = "Koulutuksen aiheet (sisältää koodisto koodi uri:a)")
    private KoodiUrisV1RDTO aihees;
    @ApiModelProperty(value = "Koulutuksen alkamispvm, tallentuu koulutuksenAlkamisPvms kokoelmaan")
    private Date koulutuksenAlkamisPvm;
    @ApiModelProperty(value = "Koulutuksen loppumispvm")
    private Date koulutuksenLoppumisPvm;
    @ApiModelProperty(value = "Opintojen laajuus opintopisteissä (vapaa teksti)")
    private String opintojenLaajuusPistetta;
    @ApiModelProperty(value = "OID koulutukselle, joka on tämän koulutuksen opintokokonaisuus")
    private String opintoKokonaisuusOid;
    @ApiModelProperty(value = "Opettaja")
    private String opettaja;
    @ApiModelProperty(value = "Oppiaine")
    private String oppiaine;
    @ApiModelProperty(value = "Yhteyshenkilön nimi")
    private String yhteyshenkiloNimi;
    @ApiModelProperty(value = "Yhteyshenkilön e-mail")
    private String yhteyshenkiloEmail;
    @ApiModelProperty(value = "Yhteyshenkilön puhelin")
    private String yhteyshenkiloPuhelin;
    @ApiModelProperty(value = "Yhteyshenkilön titteli")
    private String yhteyshenkiloTitteli;
    @ApiModelProperty(value = "Koulutusryhmät OID listana", required = false)
    private Set<String> koulutusRyhmaOids = new HashSet<String>();
    @ApiModelProperty(value = "Opinnon tyyppi")
    private String opinnonTyyppiUri;
    
    public TutkintoonJohtamatonKoulutusV1RDTO(ToteutustyyppiEnum toteutustyyppi, ModuulityyppiEnum moduulityyppi) {
        super(toteutustyyppi, moduulityyppi);
    }
    
    public KoodiUrisV1RDTO getAihees() {
        if (aihees == null) {
            aihees = new KoodiUrisV1RDTO();
        }

        return aihees;
    }

    public void setAihees(KoodiUrisV1RDTO aihees) {
        this.aihees = aihees;
    }
    
    /**
     * @return the hinta
     */
    public Double getHinta() {
        return hinta;
    }

    /**
     * @param hinta the hinta to set
     */
    public void setHinta(Double hinta) {
        this.hinta = hinta;
    }
    
    /**
     * @return the opintojenMaksullisuus
     */
    public Boolean getOpintojenMaksullisuus() {
        return opintojenMaksullisuus;
    }

    /**
     * @param opintojenMaksullisuus the opintojenMaksullisuus to set
     */
    public void setOpintojenMaksullisuus(Boolean opintojenMaksullisuus) {
        this.opintojenMaksullisuus = opintojenMaksullisuus;
    }
    
    /**
     * @return the pohjakoulutusvaatimukset
     */
    public KoodiUrisV1RDTO getPohjakoulutusvaatimukset() {
        if (pohjakoulutusvaatimukset == null) {
            pohjakoulutusvaatimukset = new KoodiUrisV1RDTO();
        }

        return pohjakoulutusvaatimukset;
    }

    /**
     * @param pohjakoulutusvaatimukset the pohjakoulutusvaatimukset to set
     */
    public void setPohjakoulutusvaatimukset(KoodiUrisV1RDTO pohjakoulutusvaatimukset) {
        this.pohjakoulutusvaatimukset = pohjakoulutusvaatimukset;
    }

    /**
     * Yksittäinen koulutuksen alkamispvm.
     * @return
     */
    public Date getKoulutuksenAlkamisPvm() {
        if(this.getKoulutuksenAlkamisPvms().size() > 0)
            return this.getKoulutuksenAlkamisPvms().iterator().next();
        else
            return null;
    }

    public void setKoulutuksenAlkamisPvm(Date alkamisPvm) {
//        this.koulutuksenAlkamisPvm = alkamisPvm;
        this.getKoulutuksenAlkamisPvms().clear();
        this.getKoulutuksenAlkamisPvms().add(alkamisPvm);
    }

    /**
     * Koulutuksen loppumispvm.
     * @return
     */
    public Date getKoulutuksenLoppumisPvm() {
        return koulutuksenLoppumisPvm;
    }

    public void setKoulutuksenLoppumisPvm(Date loppumisPvm) {
        this.koulutuksenLoppumisPvm = loppumisPvm;
    }

    /**
     * Opintojen laajuus pisteissä. Wrapper for opintojenLaajuusarvo.
     * @return
     */
    public String getOpintojenLaajuusPistetta() {
        return this.opintojenLaajuusPistetta;
    }

    public void setOpintojenLaajuusPistetta(String opintojenLaajuusPistetta) {
        this.opintojenLaajuusPistetta = opintojenLaajuusPistetta;
    }

    /**
     * Mihin koulutukseen tämä koulutus liittyy.
     * @return
     */
    public String getOpintoKokonaisuusOid() {
        return opintoKokonaisuusOid;
    }

    public void setOpintoKokonaisuusOid(String opintoKokonaisuusOid) {
        this.opintoKokonaisuusOid = opintoKokonaisuusOid;
    }

    public String getOpettaja() {
        return opettaja;
    }

    public void setOpettaja(String opettaja) {
        this.opettaja = opettaja;
    }

    public String getOppiaine() {
        return oppiaine;
    }

    public void setOppiaine(String oppiaine) {
        this.oppiaine = oppiaine;
    }

    public String getYhteyshenkiloNimi() {
        return yhteyshenkiloNimi;
    }

    public void setYhteyshenkiloNimi(String yhteyshenkiloNimi) {
        this.yhteyshenkiloNimi = yhteyshenkiloNimi;
    }

    public String getYhteyshenkiloEmail() {
        return yhteyshenkiloEmail;
    }

    public void setYhteyshenkiloEmail(String yhteyshenkiloEmail) {
        this.yhteyshenkiloEmail = yhteyshenkiloEmail;
    }

    public String getYhteyshenkiloPuhelin() {
        return yhteyshenkiloPuhelin;
    }

    public void setYhteyshenkiloPuhelin(String yhteyshenkiloPuhelin) {
        this.yhteyshenkiloPuhelin = yhteyshenkiloPuhelin;
    }

    public String getYhteyshenkiloTitteli() {
        return yhteyshenkiloTitteli;
    }

    public void setYhteyshenkiloTitteli(String yhteyshenkiloTitteli) {
        this.yhteyshenkiloTitteli = yhteyshenkiloTitteli;
    }

    public Set<String> getKoulutusRyhmaOids() {
        return koulutusRyhmaOids;
    }

    public void setKoulutusRyhmaOids(Set<String> koulutusRyhmaOids) {
        this.koulutusRyhmaOids = koulutusRyhmaOids;
    }

    public String getOpinnonTyyppiUri() {
        return opinnonTyyppiUri;
    }

    public void setOpinnonTyyppiUri(String opinnonTyyppiUri) {
        this.opinnonTyyppiUri = opinnonTyyppiUri;
    }

}
