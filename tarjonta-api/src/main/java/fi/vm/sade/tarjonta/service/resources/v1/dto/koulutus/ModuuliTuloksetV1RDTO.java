package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;

public class ModuuliTuloksetV1RDTO extends BaseV1RDTO {
    
    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "Koulutuksen koulutusmoduulin tyyppi", required = true)
    private KoulutusmoduuliTyyppi koulutusmoduuliTyyppi;
    
    @ApiModelProperty(value = "Koulutuksen koulutusohjelma uri", required = true)
    private String koulutusohjelmaUri;
    
    @ApiModelProperty(value = "Kuusinumeroinen tilastokeskuksen koulutuskoodin uri", required = true)
    private String koulutuskoodiUri;
    
    public ModuuliTuloksetV1RDTO(String oid, KoulutusmoduuliTyyppi koulutusmoduuliTyyppi, String koulutuskoodiUri, String koulutusohjelmaUri) {
        setOid(oid);
        this.koulutusmoduuliTyyppi = koulutusmoduuliTyyppi;
        this.koulutusohjelmaUri = koulutusohjelmaUri;
        this.koulutuskoodiUri = koulutuskoodiUri;
    }

    /**
     * @return the koulutusmoduuliTyyppi
     */
    public KoulutusmoduuliTyyppi getKoulutusmoduuliTyyppi() {
        return koulutusmoduuliTyyppi;
    }

    /**
     * @param koulutusmoduuliTyyppi the koulutusmoduuliTyyppi to set
     */
    public void setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi koulutusmoduuliTyyppi) {
        this.koulutusmoduuliTyyppi = koulutusmoduuliTyyppi;
    }

    /**
     * @return the koulutusohjelmaUri
     */
    public String getKoulutusohjelmaUri() {
        return koulutusohjelmaUri;
    }

    /**
     * @param koulutusohjelmaUri the koulutusohjelmaUri to set
     */
    public void setKoulutusohjelmaUri(String koulutusohjelmaUri) {
        this.koulutusohjelmaUri = koulutusohjelmaUri;
    }

    /**
     * @return the koulutuskoodiUri
     */
    public String getKoulutuskoodiUri() {
        return koulutuskoodiUri;
    }

    /**
     * @param koulutuskoodiUri the koulutuskoodiUri to set
     */
    public void setKoulutuskoodiUri(String koulutuskoodiUri) {
        this.koulutuskoodiUri = koulutuskoodiUri;
    }
    
}
