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

import fi.vm.sade.tarjonta.model.Hakuaika;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.Osoite;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.model.Yhteystiedot;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.TekstiRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.YhteystiedotRDTO;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.OsoiteTyyppi;
import fi.vm.sade.tarjonta.service.types.SisaisetHakuAjat;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by: Tuomas Katva
 * Date: 22.1.2013
 *
 * This class is converter for common Domain to DTO objects, like MonikielinenTeksti and Osoite
 *
 */
public class CommonToDTOConverter {


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
        if (osoite != null) {
            OsoiteRDTO osoiteRDTO = new OsoiteRDTO();

            osoiteRDTO.setOsoiterivi1(osoite.getOsoiterivi1());
            osoiteRDTO.setOsoiterivi2(osoite.getOsoiterivi2());
            osoiteRDTO.setPostinumero(osoite.getPostinumero());
            osoiteRDTO.setPostitoimipaikka(osoite.getPostitoimipaikka());

            return osoiteRDTO;
        } else {
            return null;
        }
    }

    public static List<TekstiRDTO> convertMonikielinenTekstiToTekstiRDOT(MonikielinenTeksti monikielinenTeksti) {
        if (monikielinenTeksti != null) {
            List<TekstiRDTO> tekstiRDTOs = new ArrayList<TekstiRDTO>();

            for (TekstiKaannos tekstiKaannos:monikielinenTeksti.getTekstiKaannos()) {

                TekstiRDTO tekstiRDTO = new TekstiRDTO();
                if (tekstiKaannos.getKieliKoodi().contains("#")) {
                    StringTokenizer st = new StringTokenizer(tekstiKaannos.getKieliKoodi(),"#");
                    tekstiRDTO.setUri(st.nextToken());
                } else {
                    tekstiRDTO.setUri(tekstiKaannos.getKieliKoodi());
                }
                tekstiRDTO.setTeksti(tekstiKaannos.getArvo());

                tekstiRDTOs.add(tekstiRDTO);
            }

            return tekstiRDTOs;
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
        for (TekstiKaannos kaannos: monikielinenTeksti.getTekstiKaannos()) {
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
    	ret.setSisaisenHaunAlkamisPvm(ha.getAlkamisPvm());
    	ret.setSisaisenHaunPaattymisPvm(ha.getPaattymisPvm());
    	ret.setOid(Long.toString(ha.getId()));
    	return ret;
    }
    
    public static YhteystiedotRDTO convertYhteystiedotToYhteystiedotRDTO(Yhteystiedot yhteystiedot) {
        if (yhteystiedot != null) {
            YhteystiedotRDTO yh = new YhteystiedotRDTO();
            
            yh.setOsoiterivi1(yhteystiedot.getOsoiterivi1());
            yh.setOsoiterivi2(yhteystiedot.getOsoiterivi2());
            yh.setPostinumero(yhteystiedot.getPostinumero());
            yh.setPostitoimipaikka(yhteystiedot.getPostitoimipaikka());
            
            return yh;
        }
        else {
            return null;
        }
    }
}
