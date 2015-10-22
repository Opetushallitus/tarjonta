package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public abstract class TutkintoonJohtamatonKoulutusV1RDTO extends KoulutusV1RDTO {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "Koulutuksen pohjakoulutusvaatimukset (sisältää koodisto koodi uri:a)", required = true)
    private KoodiUrisV1RDTO pohjakoulutusvaatimukset;
    @ApiModelProperty(value = "Koulutuksen aiheet (sisältää koodisto koodi uri:a)")
    private KoodiUrisV1RDTO aihees;
    @ApiModelProperty(value = "Koulutuksen loppumispvm")
    private Date koulutuksenLoppumisPvm;
    @ApiModelProperty(value = "Opintojen laajuus opintopisteissä (vapaa teksti)")
    private String opintojenLaajuusPistetta;
    @ApiModelProperty(value = "Opettaja")
    private String opettaja;
    @ApiModelProperty(value = "Oppiaine")
    private String oppiaine;
    @ApiModelProperty(value = "Koulutusryhmät OID listana", required = false)
    private Set<String> koulutusRyhmaOids = new HashSet<String>();
    @ApiModelProperty(value = "Opinnon tyyppi")
    private String opinnonTyyppiUri;
    @ApiModelProperty(value = "Alkuperäinen koulutus, joka järjestetään")
    private String tarjoajanKoulutus;
    @ApiModelProperty(value = "Opintokokonaisuus, johon koulutus kuuluu")
    private String opintokokonaisuusOid;
    @ApiModelProperty(value = "Opintokokonaisuuteen kuuluvat opintojaksot")
    private Set<String> opintojaksoOids = new HashSet<String>();
    
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

    public String getTarjoajanKoulutus() {
        return tarjoajanKoulutus;
    }

    public void setTarjoajanKoulutus(String tarjoajanKoulutus) {
        this.tarjoajanKoulutus = tarjoajanKoulutus;
    }

    public String getOpintokokonaisuusOid() {
        return opintokokonaisuusOid;
    }

    public void setOpintokokonaisuusOid(String opintokokonaisuusOid) {
        this.opintokokonaisuusOid = opintokokonaisuusOid;
    }

    public Set<String> getOpintojaksoOids() {
        return opintojaksoOids;
    }

    public void setOpintojaksoOids(Set<String> opintojaksoOids) {
        this.opintojaksoOids = opintojaksoOids;
    }
}
