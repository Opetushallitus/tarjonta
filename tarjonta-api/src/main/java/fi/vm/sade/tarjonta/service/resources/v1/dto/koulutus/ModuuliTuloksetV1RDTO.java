/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;

public class ModuuliTuloksetV1RDTO extends BaseV1RDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Koulutusmoduulin tyyppi", required = true)
    private KoulutusmoduuliTyyppi koulutusmoduuliTyyppi;

    @ApiModelProperty(value = "Koulutusmoduulin käyttämä ohjelma uri (koulutusohjelma, lukiolinja tai osaamisala)", required = true)
    private String ohjelmaUri;

    @ApiModelProperty(value = "Koulutusmoduulin koulutusohjelma uri, jos olemassa", required = false)
    private String koulutusohjelmaUri;

    @ApiModelProperty(value = "Koulutusmoduulin lukiolinja uri, jos olemassa", required = false)
    private String lukiolinjaUri;

    @ApiModelProperty(value = "Koulutusmoduulin osaamisala uri, jos olemassa", required = false)
    private String osaamisalaUri;

    @ApiModelProperty(value = "Kuusinumeroinen tilastokeskuksen koulutuskoodin uri", required = true)
    private String koulutuskoodiUri;

    public ModuuliTuloksetV1RDTO(String oid,
            KoulutusmoduuliTyyppi koulutusmoduuliTyyppi,
            String koulutuskoodiUri,
            String ohjelmaUri,
            String koulutusohjelmaUri,
            String lukiolinjaUri,
            String osaamisalaUri
    ) {
        setOid(oid);
        this.koulutusmoduuliTyyppi = koulutusmoduuliTyyppi;
        this.ohjelmaUri = ohjelmaUri;
        this.koulutuskoodiUri = koulutuskoodiUri;
        this.koulutusohjelmaUri = koulutusohjelmaUri;
        this.lukiolinjaUri = lukiolinjaUri;
        this.osaamisalaUri = osaamisalaUri;
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

    /**
     * @return the ohjelmaUri
     */
    public String getOhjelmaUri() {
        return ohjelmaUri;
    }

    /**
     * @param ohjelmaUri the ohjelmaUri to set
     */
    public void setOhjelmaUri(String ohjelmaUri) {
        this.ohjelmaUri = ohjelmaUri;
    }

    /**
     * @return the lukiolinjaUri
     */
    public String getLukiolinjaUri() {
        return lukiolinjaUri;
    }

    /**
     * @param lukiolinjaUri the lukiolinjaUri to set
     */
    public void setLukiolinjaUri(String lukiolinjaUri) {
        this.lukiolinjaUri = lukiolinjaUri;
    }

    /**
     * @return the osaamisalaUri
     */
    public String getOsaamisalaUri() {
        return osaamisalaUri;
    }

    /**
     * @param osaamisalaUri the osaamisalaUri to set
     */
    public void setOsaamisalaUri(String osaamisalaUri) {
        this.osaamisalaUri = osaamisalaUri;
    }

}
