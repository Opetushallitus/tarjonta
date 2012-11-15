/*
 *
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

package fi.vm.sade.tarjonta.service.impl.conversion;


import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.types.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.HaunNimi;
import fi.vm.sade.tarjonta.service.types.SisaisetHakuAjat;

/**
 *
 * @author Tuomas Katva
 */
public class HakuToDTOConverter extends AbstractFromDomainConverter<Haku, HakuTyyppi> {

    @Override
    public HakuTyyppi convert(Haku from) {
        HakuTyyppi h = new HakuTyyppi();
        h.setHakuVuosi(from.getHakukausiVuosi());
        h.setHakukausiUri(from.getHakukausiUri());
        h.setHakulomakeUrl(from.getHakulomakeUrl());
        h.setHakutapaUri(from.getHakutapaUri());
        h.setHakutyyppiUri(from.getHakutyyppiUri());
        h.setHaunTila(EntityUtils.convertTila(from.getTila()));
        h.setHaunTunniste(from.getHaunTunniste());
        h.setKohdejoukkoUri(from.getKohdejoukkoUri());
        h.setKoulutuksenAlkamisKausiUri(from.getKoulutuksenAlkamiskausiUri());
        h.setKoulutuksenAlkamisVuosi(from.getKoulutuksenAlkamisVuosi());
        h.setOid(from.getOid());
        h.setSijoittelu(from.isSijoittelu());
        h.setHaunTunniste(from.getHaunTunniste());
        ConvertHaunNimet(h, from);
        ConvertHaunAjat(h, from);
        return h;
    }

    private void ConvertHaunAjat(HakuTyyppi h, Haku s) {
        if (s.getHakuaikas() != null) {
            for (Hakuaika ha: s.getHakuaikas()) {
                SisaisetHakuAjat aika = new SisaisetHakuAjat();
                aika.setHakuajanKuvaus(ha.getSisaisenHakuajanNimi());
                aika.setSisaisenHaunAlkamisPvm(ha.getAlkamisPvm());
                aika.setSisaisenHaunPaattymisPvm(ha.getPaattymisPvm());
                h.getSisaisetHakuajat().add(aika);
            }
        }
    }

    private void ConvertHaunNimet(HakuTyyppi h, Haku s) {
        if (s.getNimi() != null && s.getNimi().getTekstis() != null) {
        for (TekstiKaannos tk: s.getNimi().getTekstis()) {
            HaunNimi hn = new HaunNimi();
            hn.setKielikoodi(tk.getKieliKoodi());
            hn.setNimi(tk.getArvo());
            h.getHaunKielistetytNimet().add(hn);
        }
       }
    }
}
