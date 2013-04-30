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

import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mlyly
 */
public class HakuToHakuDTOConverter extends AbstractFromDomainConverter<Haku, HakuDTO> {

    @Override
    public HakuDTO convert(Haku s) {
        HakuDTO t = new HakuDTO();

        t.setOid(s.getOid());
        t.setVersion(s.getVersion() != null ? s.getVersion().intValue() : -1);

        // t.set(s.getHakuaikas());
        t.setHakukausiUri(s.getHakukausiUri());
        t.setHakukausiVuosi(s.getHakukausiVuosi());
        // t.set(s.getHakukohdes());
        t.setHakulomakeUrl(s.getHakulomakeUrl());
        t.setHakutapaUri(s.getHakutapaUri());
        t.setHakutyyppiUri(s.getHakutyyppiUri());
        t.setHaunTunniste(s.getHaunTunniste());
        t.setKohdejoukkoUri(s.getKohdejoukkoUri());
        t.setKoulutuksenAlkamisVuosi(s.getKoulutuksenAlkamisVuosi());
        t.setKoulutuksenAlkamiskausiUri(s.getKoulutuksenAlkamiskausiUri());
        t.setUpdated(s.getLastUpdateDate());
        t.setUdatedByOid(s.getLastUpdatedByOid());
        t.setNimi(convert(s.getNimi()));
        t.setTila(s.getTila() != null ? s.getTila().name() : null);

        return t;
    }

    private Map<String, String> convert(MonikielinenTeksti s) {
        Map<String, String> t = new HashMap<String, String>();

        for (TekstiKaannos tekstiKaannos : s.getTekstis()) {
            t.put(tekstiKaannos.getKieliKoodi(), tekstiKaannos.getArvo());
        }

        return t;
    }


}
