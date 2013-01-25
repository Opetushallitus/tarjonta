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

import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.model.ValintakoeAjankohta;
import fi.vm.sade.tarjonta.service.types.AjankohtaTyyppi;
import fi.vm.sade.tarjonta.service.types.ValintakoeTyyppi;
import fi.vm.sade.tarjonta.model.Valintakoe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Tuomas Katva
 * Date: 22.1.2013
 */
public class ValintakoeFromDTOConverter extends AbstractToDomainConverter<ValintakoeTyyppi,Valintakoe> {

    @Override
    public Valintakoe convert(ValintakoeTyyppi valintakoeTyyppi) {
        Valintakoe valintakoe = new Valintakoe();

        if (valintakoeTyyppi.getValintakokeenTunniste() != null) {
            valintakoe.setId(new Long(valintakoeTyyppi.getValintakokeenTunniste()));

        }

        valintakoe.setKuvaus(CommonFromDTOConverter.convertMonikielinenTekstiTyyppiToDomainValue(valintakoeTyyppi.getKuvaukset()));
        valintakoe.setTyyppiUri(valintakoeTyyppi.getValintakokeenTyyppi());
        for (ValintakoeAjankohta ajankohta:convertAjankohtaTyyppiToValintakoeAjankohta(valintakoeTyyppi.getAjankohdat())) {
            valintakoe.addAjankohta(ajankohta);
        }



        return valintakoe;
    }

    private List<ValintakoeAjankohta> convertAjankohtaTyyppiToValintakoeAjankohta(List<AjankohtaTyyppi> ajankohtaTyyppis) {
        ArrayList<ValintakoeAjankohta> valintakoeAjankohtas = new ArrayList<ValintakoeAjankohta>();

        for (AjankohtaTyyppi ajankohtaTyyppi:ajankohtaTyyppis) {
            ValintakoeAjankohta valintakoeAjankohta = new ValintakoeAjankohta();

             valintakoeAjankohta.setAjankohdanOsoite(CommonFromDTOConverter.convertOsoiteToOsoiteTyyppi(ajankohtaTyyppi.getValintakoeAjankohtaOsoite()));
             valintakoeAjankohta.setAlkamisaika(ajankohtaTyyppi.getAlkamisAika());
             valintakoeAjankohta.setPaattymisaika(ajankohtaTyyppi.getPaattymisAika());
             valintakoeAjankohta.setLisatietoja(ajankohtaTyyppi.getKuvaus());

            valintakoeAjankohtas.add(valintakoeAjankohta);
        }

        return valintakoeAjankohtas;
    }



}
