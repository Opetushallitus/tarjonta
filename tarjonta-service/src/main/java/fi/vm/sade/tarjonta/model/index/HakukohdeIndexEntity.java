package fi.vm.sade.tarjonta.model.index;

import java.util.Date;

import com.mysema.query.annotations.QueryProjection;

import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

public class HakukohdeIndexEntity {

    private final long id;
    private final String oid;
    private String hakukausiUri;
    private Integer hakukausiVuosi;
    private String hakutapaUri;
    private Integer aloituspaikatLkm;
    private TarjontaTila tila;
    private String hakukohdeNimi;
    private Long hakuId;
    private String hakuOid;
    private String hakutyyppiUri;
    private String ryhmaOidit;

    public String getRyhmaOidit() {
        return ryhmaOidit;
    }

    @QueryProjection
    public HakukohdeIndexEntity(Long id, String oid, String hakukohdeNimi, String hakukausiUri, Integer hakukausiVuosi, TarjontaTila tila, String hakutapaUri, Integer aloituspaikatLkm, Long hakuId, String hakuOid, String hakutyyppiUri, String ryhmaOidit) {
        this.id = id;
        this.oid = oid;
        this.hakukohdeNimi = hakukohdeNimi;
        this.hakukausiUri = hakukausiUri;
        this.hakukausiVuosi = hakukausiVuosi;
        this.tila = tila;
        this.hakutapaUri = hakutapaUri;
        this.aloituspaikatLkm = aloituspaikatLkm;
        this.hakuId = hakuId;
        this.hakuOid = hakuOid;
        this.hakutyyppiUri = hakutyyppiUri;
        this.ryhmaOidit = ryhmaOidit;
    }

    //hakukohde.id, hakukohde.oid
    @QueryProjection
    public HakukohdeIndexEntity(long id, String oid) {
        this.id = id;
        this.oid = oid;
    }

    public long getId() {
        return id;
    }
    
    public Long getHakuId() {
        return hakuId;
    }

    public Date getAlkamisPvm() {
        return alkamisPvm;
    }

    public Date getPaattymisPvm() {
        return paattymisPvm;
    }

    private Date alkamisPvm;
    private Date paattymisPvm;

    /**
     * Return hakukohde oid
     * 
     * @return
     */
    public String getOid() {
        return oid;
    }

    /**
     * Return hakukausi Uri
     * 
     * @return
     */
    public String getHakukausiUri() {
        return hakukausiUri;
    }

    public Integer getHakukausiVuosi() {
        return hakukausiVuosi;
    }

    /**
     * Return hakutapa Uri
     * 
     * @return
     */
    public String getHakutapaUri() {
        return hakutapaUri;
    }

    /**
     * Return aloituspaikat
     * 
     * @return
     */
    public Integer getAloituspaikatLkm() {
        return aloituspaikatLkm;
    }

    /**
     * Return tila
     * 
     * @return
     */
    public TarjontaTila getTila() {
        return tila;
    }

    /**
     * Return hakukohteen nimi
     */
    public String getHakukohdeNimi() {
        return hakukohdeNimi;
    }

    public Object getHakuOid() {
        return hakuOid;
    }

    public String getHakutyyppiUri() {
        return hakutyyppiUri;
    }

    @Override
    public String toString() {
        return "HakukohdeIndexEntity [id=" + id + ", oid=" + oid
                + ", hakukausiUri=" + hakukausiUri + ", hakukausiVuosi="
                + hakukausiVuosi + ", hakutapaUri=" + hakutapaUri
                + ", aloituspaikatLkm=" + aloituspaikatLkm + ", tila=" + tila
                + ", hakukohdeNimi=" + hakukohdeNimi + ", hakuId=" + hakuId
                + ", hakuOid=" + hakuOid + ", hakutyyppiUri=" + hakutyyppiUri
                + ", ryhmaOidit=" + ryhmaOidit + ", alkamisPvm=" + alkamisPvm
                + ", paattymisPvm=" + paattymisPvm + "]";
    }
    
    
    

}
