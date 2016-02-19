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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jani Wilén
 * @param <TYPE>
 */
@ApiModel(value = "Kuvastekstien syöttämiseen ja hakemiseen käytettävä rajapintaolio")
public class KuvausV1RDTO<TYPE extends Enum> extends HashMap<TYPE, NimiV1RDTO> {

    private static final long serialVersionUID = 1L;

    public KuvausV1RDTO() {
    }

    public KuvausV1RDTO(Map<TYPE, NimiV1RDTO> tekstit) {
        this.putAll(tekstit);
    }
}
