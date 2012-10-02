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


import fi.vm.sade.generic.model.BaseEntity;
import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.tarjonta.service.types.tarjonta.*;
import fi.vm.sade.tarjonta.model.*;
import java.util.List;

/**
 *
 * @author Tuomas Katva
 */
public class HakuToDTOConverter extends AbstractFromDomainConverter<Haku, HakuTyyppi> {

    @Override
    public HakuTyyppi convert(Haku s) {
        HakuTyyppi h = new HakuTyyppi();
        h.setHakuVuosi(s.getHakukausiVuosi());
        h.setHakukausiUri(s.getHakukausiUri());
        h.setHakulomakeUrl(s.getHakulomakeUrl());
        h.setHakutapaUri(s.getHakutapaUri());
        h.setHakutyyppiUri(s.getHakutyyppiUri());
        h.setHaunAlkamisPvm(s.getHaunAlkamisPvm());
        h.setHaunLoppumisPvm(s.getHaunLoppumisPvm());
        h.setHaunTila(HaunTila.fromValue(s.getTila()));
        h.setHaunTunniste(s.getHaunTunniste());
        h.setKohdejoukkoUri(s.getKohdejoukkoUri());
        h.setKoulutuksenAlkamisKausiUri(s.getKoulutuksenAlkamiskausiUri());
        h.setKoulutuksenAlkamisVuosi(s.getKoulutuksenAlkamisVuosi());
        h.setOid(s.getOid());
        h.setSijoittelu(s.isSijoittelu());
        h.setHaunTunniste(s.getHaunTunniste());
        ConvertHaunNimet(h, s);
        ConvertHaunAjat(h, s);
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
            hn.setNimi(tk.getTeksti());
            h.getHaunKielistetytNimet().add(hn);
        }
       }
    }
}
