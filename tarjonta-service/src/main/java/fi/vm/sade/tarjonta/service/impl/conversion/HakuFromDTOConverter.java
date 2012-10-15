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

import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.service.types.tarjonta.*;
import fi.vm.sade.tarjonta.model.*;
import java.util.List;

/**
 *
 * @author Tuomas Katva
 */
public class HakuFromDTOConverter extends AbstractToDomainConverter<HakuTyyppi, Haku> {

    @Override
    public Haku convert(HakuTyyppi s) {
        Haku m = new Haku();
        m.setNimi(convertNimis(s.getHaunKielistetytNimet()));
        m.setOid(s.getOid());
        m.setHakukausiUri(s.getHakukausiUri());
        m.setHakukausiVuosi(s.getHakuVuosi());
        m.setHakulomakeUrl(s.getHakulomakeUrl());
        m.setHakutapaUri(s.getHakutapaUri());
        m.setHakutyyppiUri(s.getHakutyyppiUri());
        m.setKohdejoukkoUri(s.getKohdejoukkoUri());
        m.setKoulutuksenAlkamiskausiUri(s.getKoulutuksenAlkamisKausiUri());
        m.setKoulutuksenAlkamisVuosi(s.getKoulutuksenAlkamisVuosi());
        m.setSijoittelu(s.isSijoittelu());
        m.setTila(s.getHaunTila().value());
        m.setHaunTunniste(s.getHaunTunniste());
        convertSisaisetHaunAlkamisAjat(m, s.getSisaisetHakuajat());
        return m;
    }

    private MonikielinenTeksti convertNimis(List<HaunNimi> haunNimet) {
        MonikielinenTeksti mt = new MonikielinenTeksti();
        if (haunNimet != null) {
            for (HaunNimi nimi : haunNimet) {
                mt.addTekstiKaannos(nimi.getKielikoodi(), nimi.getNimi());
            }
        }
        return mt;
    }
    
    private void convertSisaisetHaunAlkamisAjat(Haku mm, List<SisaisetHakuAjat> sisAjat) {
        if (sisAjat != null) {
        	for (SisaisetHakuAjat curHA : sisAjat) {
        		Hakuaika aika = new Hakuaika();
        		aika.setAlkamisPvm(curHA.getSisaisenHaunAlkamisPvm());
        		aika.setPaattymisPvm(curHA.getSisaisenHaunPaattymisPvm());
        		aika.setSisaisenHakuajanNimi(curHA.getHakuajanKuvaus());
        		mm.addHakuaika(aika);
        	}
        }
    } 
}
