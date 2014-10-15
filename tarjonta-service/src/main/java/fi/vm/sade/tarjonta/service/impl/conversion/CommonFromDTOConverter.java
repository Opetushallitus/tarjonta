package fi.vm.sade.tarjonta.service.impl.conversion;/*
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

import fi.vm.sade.security.xssfilter.XssFilter;
import fi.vm.sade.tarjonta.model.Hakuaika;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.Osoite;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.OsoiteTyyppi;
import fi.vm.sade.tarjonta.service.types.SisaisetHakuAjat;

/**
 * Created by: Tuomas Katva
 * Date: 22.1.2013
 *
 * This class is converter for common DTO to Domain objects, like MonikielinenTeksti and Osoite
 *
 */
public class CommonFromDTOConverter {

    public static MonikielinenTeksti convertMonikielinenTekstiTyyppiToDomainValue(MonikielinenTekstiTyyppi monikielinenTekstiTyyppi) {
        MonikielinenTeksti monikielinenTeksti = new MonikielinenTeksti();

        if (monikielinenTekstiTyyppi!=null && monikielinenTekstiTyyppi.getTeksti()!=null) {
            for (MonikielinenTekstiTyyppi.Teksti teksti : monikielinenTekstiTyyppi.getTeksti()) {
                monikielinenTeksti.addTekstiKaannos(teksti.getKieliKoodi(), teksti.getValue());
            }
        }
        
        return monikielinenTeksti;
    }

    public static Osoite convertOsoiteToOsoiteTyyppi(OsoiteTyyppi osoiteTyyppi) {
        Osoite osoite = new Osoite();

        osoite.setOsoiterivi1(osoiteTyyppi.getOsoiteRivi());
        osoite.setOsoiterivi2(osoiteTyyppi.getLisaOsoiteRivi());
        osoite.setPostinumero(osoiteTyyppi.getPostinumero());
        osoite.setPostitoimipaikka(osoiteTyyppi.getPostitoimipaikka());

        return osoite;
    }

    public static Hakuaika convertSisaisetHakuAjatToHakuaika(SisaisetHakuAjat ha) {
    	if (ha==null) {
    		return null;
    	}
    	Hakuaika ret = new Hakuaika();
    	ret.setAlkamisPvm(ha.getSisaisenHaunAlkamisPvm());
    	ret.setPaattymisPvm(ha.getSisaisenHaunPaattymisPvm());
    	ret.setId(ha.getOid()==null ? null : Long.parseLong(ha.getOid()));
    	return ret;
    }
    
}
