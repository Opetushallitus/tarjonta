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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jani Wilén
 */
@ApiModel(value = "Koodiston lisätiedon näyttämiseen käytettävä rajapintaolio")
public class MetaV1RDTO extends KoodiV1RDTO {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "Monikielisen lisätiedon näyttämiseen tarkoitettu avain-arvopari, jossa avain on koodisto kieli uri ja arvo on rajapintaolio", required = false)
    private Map<String, KoodiV1RDTO> meta;

    public MetaV1RDTO() {
    }

    /**
     * @return the tekstis
     */
    public Map<String, KoodiV1RDTO> getMeta() {
        if (meta == null) {
            meta = new HashMap<String, KoodiV1RDTO>();
        }
        return meta;
    }

    /**
     * @param meta
     */
    public void setMeta(Map<String, KoodiV1RDTO> meta) {
        this.meta = meta;
    }

}
