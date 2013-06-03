/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.generic.model.BaseEntity;
import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.model.WebLinkki;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Some basic functionality for REST DTO converters.
 *
 * @author mlyly
 */
public abstract class BaseRDTOConverter<FROM extends BaseEntity, TO> extends AbstractFromDomainConverter<FROM, TO> {

    @Autowired(required = true)
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    public TarjontaKoodistoHelper getTarjontaKoodistoHelper() {
        return tarjontaKoodistoHelper;
    }


    public Map<String, String> convertMonikielinenTekstiToMap(MonikielinenTeksti s) {
        if (s == null) {
            return null;
        }

        Map<String, String> t = new HashMap<String, String>();

        for (TekstiKaannos tekstiKaannos : s.getTekstis()) {
            t.put(tarjontaKoodistoHelper.convertKielikoodiToKieliUri(tekstiKaannos.getKieliKoodi()),
                    tekstiKaannos.getArvo());
        }

        return t;
    }

    public List<String> convertKoodistoUrisToList(Set<KoodistoUri> koodistoUris) {
        if (koodistoUris == null) {
            return null;
        }

        List<String> result = new ArrayList<String>();

        for (KoodistoUri koodistoUri : koodistoUris) {
            result.add(koodistoUri.getKoodiUri());
        }

        return result;
    }


    public Map<String, String> convertWebLinkkisToMap(Set<WebLinkki> s) {
        if (s == null) {
            return null;
        }

        Map<String, String> t = new HashMap<String, String>();

        for (WebLinkki webLinkki : s) {
            t.put(webLinkki.getTyyppi(), webLinkki.getUrl());
        }

        return t;
    }


}
