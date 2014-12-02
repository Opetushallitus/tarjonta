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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jani Wilén
 */
@ApiModel(value = "Monen koodisto koodi uri:n syötämiseen ja näyttämiseen käytettävä rajapintaolio")
public class KoodiUrisV1RDTO extends KoodiV1RDTO {

    @Override
    public String toString() {
        return "KoodiUrisV1RDTO [uris=" + uris + "]";
    }

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "Avain-arvopari, jossa avain on koodisto koodi uri ja arvo on koodin versionumero", required = true)
    private Map<String, Integer> uris;

    public KoodiUrisV1RDTO() {
    }

    /**
     * @return the uris
     */
    public Map<String, Integer> getUris() {
        return uris;
    }

    /**
     * @param uris the uris to set
     */
    public void setUris(Map<String, Integer> uris) {
        this.uris = uris;
    }

    public List<String> getUrisAsStringList(boolean addVersionToUri) {
        List<String> list = new ArrayList<String>();

        for(Map.Entry<String, Integer> entry : uris.entrySet()) {
            String uri = entry.getKey();

            if (addVersionToUri) {
                uri = uri.concat("#" + entry.getValue().toString());
            }

            list.add(uri);
        }

        return list;
    }
}
