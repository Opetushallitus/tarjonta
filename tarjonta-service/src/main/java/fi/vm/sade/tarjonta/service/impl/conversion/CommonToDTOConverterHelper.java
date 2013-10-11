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

import fi.vm.sade.organisaatio.api.model.types.OsoiteDTO;
import fi.vm.sade.tarjonta.model.Hakuaika;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.Osoite;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.TekstiRDTO;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.OsoiteTyyppi;
import fi.vm.sade.tarjonta.service.types.SisaisetHakuAjat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Tuomas Katva
 * Date: 22.1.2013
 *
 * This class is converter for common Domain to DTO objects, like MonikielinenTeksti and Osoite
 *
 */
public class CommonToDTOConverterHelper {


    public static OsoiteTyyppi convertOsoiteToOsoiteTyyppi(Osoite osoite) {
    	if (osoite==null) {
    		return null;
    	}
    	
        OsoiteTyyppi osoiteTyyppi = new OsoiteTyyppi();

        osoiteTyyppi.setOsoiteRivi(osoite.getOsoiterivi1());
        osoiteTyyppi.setLisaOsoiteRivi(osoite.getOsoiterivi2());
        osoiteTyyppi.setPostinumero(osoite.getPostinumero());
        osoiteTyyppi.setPostitoimipaikka(osoite.getPostitoimipaikka());

        return osoiteTyyppi;
    }

    public static OsoiteRDTO convertOsoiteToOsoiteDTO(Osoite osoite) {
        if (osoite == null) {
            return null;
        }
        OsoiteRDTO osoiteDTO = new OsoiteRDTO();

        osoite.setOsoiterivi1(osoite.getOsoiterivi1());
        osoiteDTO.setPostinumero(osoite.getPostinumero());
        osoiteDTO.setPostitoimipaikka(osoite.getPostitoimipaikka());

        return osoiteDTO;

    }

    public static List<TekstiRDTO> convertMonikielinenTekstiToTekstiRDOT(MonikielinenTeksti monikielinenTeksti) {

        if (monikielinenTeksti != null) {
        List<TekstiRDTO> tekstis = new ArrayList<TekstiRDTO>();

        for (TekstiKaannos kaannos:monikielinenTeksti.getTekstis()) {
            TekstiRDTO tekstiRDTO = new TekstiRDTO();
            tekstiRDTO.setUri(kaannos.getKieliKoodi());
            tekstiRDTO.setTeksti(kaannos.getArvo());

        }

        return tekstis;
        } else {
            return null;
        }
    }

    public static MonikielinenTekstiTyyppi convertMonikielinenTekstiToTekstiTyyppi(MonikielinenTeksti monikielinenTeksti) {
    	if (monikielinenTeksti==null) {
    		return null;
    	}
        MonikielinenTekstiTyyppi monikielinenTekstiTyyppi = new MonikielinenTekstiTyyppi();
        ArrayList<MonikielinenTekstiTyyppi.Teksti> tekstis = new ArrayList<MonikielinenTekstiTyyppi.Teksti>();
        for (TekstiKaannos kaannos: monikielinenTeksti.getTekstis()) {
            MonikielinenTekstiTyyppi.Teksti teksti = new MonikielinenTekstiTyyppi.Teksti();
            teksti.setKieliKoodi(kaannos.getKieliKoodi());
            teksti.setValue(kaannos.getArvo());
            tekstis.add(teksti);
        }
        monikielinenTekstiTyyppi.getTeksti().addAll(tekstis);
        return monikielinenTekstiTyyppi;
    }

    public static SisaisetHakuAjat convertHakuaikaToSisaisetHakuAjat(Hakuaika ha) {
    	if (ha==null) {
    		return null;
    	}
    	SisaisetHakuAjat ret = new SisaisetHakuAjat();
    	ret.setHakuajanKuvaus(ha.getSisaisenHakuajanNimi());
    	ret.setSisaisenHaunAlkamisPvm(ha.getAlkamisPvm());
    	ret.setSisaisenHaunPaattymisPvm(ha.getPaattymisPvm());
    	ret.setOid(Long.toString(ha.getId()));
    	return ret;
    }
    
}
