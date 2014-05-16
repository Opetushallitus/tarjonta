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

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

/**
 *
 * @author jani
 */
@ApiModel(value = "Kuvan syöttämiseen ja hakemiseen käytettävä rajapintaolio")
public class KuvaV1RDTO implements Serializable {

    @ApiModelProperty(value = "Koodisto kieli uri", required = false)
    private String kieliUri;
    @ApiModelProperty(value = "Tiedoston alkuperäinen nimi", required = true)
    private String filename;
    @ApiModelProperty(value = "Tiedoston tyyppi (image/jpeg, image/png jne.)", required = true)
    private String mimeType;
    @ApiModelProperty(value = "Kuvatiedosto base64-enkoodauksella", required = true)
    private String base64data;

    public KuvaV1RDTO() {
    }

    public KuvaV1RDTO(String filename, String mimeType, String kieliUri, String base64data) {
        this.filename = filename;
        this.mimeType = mimeType;
        this.kieliUri = kieliUri;
        this.base64data = base64data;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the mimeType
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @param mimeType the mimeType to set
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * @return the base64data
     */
    public String getBase64data() {
        return base64data;
    }

    /**
     * @param base64data the base64data to set
     */
    public void setBase64data(String base64data) {
        this.base64data = base64data;
    }

    /**
     * @return the kieliUri
     */
    public String getKieliUri() {
        return kieliUri;
    }

    /**
     * @param kieliUri the kieliUri to set
     */
    public void setKieliUri(String kieliUri) {
        this.kieliUri = kieliUri;
    }
}
