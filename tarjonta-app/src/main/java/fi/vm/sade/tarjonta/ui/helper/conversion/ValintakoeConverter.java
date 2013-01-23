package fi.vm.sade.tarjonta.ui.helper.conversion;/*
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

import fi.vm.sade.tarjonta.service.types.AjankohtaTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.OsoiteTyyppi;
import fi.vm.sade.tarjonta.service.types.ValintakoeTyyppi;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeAikaViewModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Tuomas Katva
 * Date: 23.1.2013
 */
public class ValintakoeConverter {


    public static ValintakoeViewModel mapDtoToValintakoeViewModel(ValintakoeTyyppi valintakoeTyyppi) {
        ValintakoeViewModel valintakoeViewModel = new ValintakoeViewModel();

        valintakoeViewModel.setValintakoeTunniste(valintakoeTyyppi.getValintakokeenTunniste());
        valintakoeViewModel.setValintakoeTyyppi(valintakoeTyyppi.getValintakokeenTyyppi());
        valintakoeViewModel.setSanallisetKuvaukset(ConversionUtils.convertTekstiToVM(valintakoeTyyppi.getKuvaukset()));

        valintakoeViewModel.setValintakoeAjat(mapAjat(valintakoeTyyppi.getAjankohdat()));

        return valintakoeViewModel;
    }

    private static List<ValintakoeAikaViewModel> mapAjat(List<AjankohtaTyyppi> ajankohtaTyyppis) {
         List<ValintakoeAikaViewModel> valintakoeAjat = new ArrayList<ValintakoeAikaViewModel>();

         for (AjankohtaTyyppi ajankohtaTyyppi:ajankohtaTyyppis) {
             ValintakoeAikaViewModel valintakoeAika = new ValintakoeAikaViewModel();

             valintakoeAika.setAlkamisAika(ajankohtaTyyppi.getAlkamisAika());
             valintakoeAika.setPaattymisAika(ajankohtaTyyppi.getPaattymisAika());
             OsoiteTyyppi osoiteTyyppi = ajankohtaTyyppi.getValintakoeAjankohtaOsoite();
             valintakoeAika.setOsoiteRivi(osoiteTyyppi.getOsoiteRivi());
             valintakoeAika.setPostinumero(osoiteTyyppi.getPostinumero());
             valintakoeAika.setPostitoimiPaikka(osoiteTyyppi.getPostitoimipaikka());
             valintakoeAika.setValintakoeAikaTiedot(ajankohtaTyyppi.getKuvaus());

             valintakoeAjat.add(valintakoeAika);
         }

         return valintakoeAjat;
    }


    public static ValintakoeTyyppi mapKieliKaannosToValintakoeTyyppi(ValintakoeViewModel valintakoeViewModel) {
        ValintakoeTyyppi valintakoeTyyppi = new ValintakoeTyyppi();

        valintakoeTyyppi.setValintakokeenTunniste(valintakoeViewModel.getValintakoeTunniste());
        valintakoeTyyppi.setValintakokeenTyyppi(valintakoeViewModel.getValintakoeTyyppi());
        valintakoeTyyppi.setKuvaukset(ConversionUtils.convertKielikaannosToMonikielinenTeksti(valintakoeViewModel.getSanallisetKuvaukset()));
        valintakoeTyyppi.getAjankohdat().addAll(mapAjankohdat(valintakoeViewModel.getValintakoeAjat()));


        return valintakoeTyyppi;
    }

    private static List<AjankohtaTyyppi> mapAjankohdat(List<ValintakoeAikaViewModel> valintaKoeaikas) {
        List<AjankohtaTyyppi> ajankohtas = new ArrayList<AjankohtaTyyppi>();

        for (ValintakoeAikaViewModel valintakoeAikaViewModel:valintaKoeaikas) {
             AjankohtaTyyppi ajankohtaTyyppi = new AjankohtaTyyppi();

             ajankohtaTyyppi.setKuvaus(valintakoeAikaViewModel.getValintakoeAikaTiedot());
             ajankohtaTyyppi.setAlkamisAika(valintakoeAikaViewModel.getAlkamisAika());
             ajankohtaTyyppi.setPaattymisAika(valintakoeAikaViewModel.getPaattymisAika());

             OsoiteTyyppi osoiteTyyppi = new OsoiteTyyppi();
            osoiteTyyppi.setOsoiteRivi(valintakoeAikaViewModel.getOsoiteRivi());
            osoiteTyyppi.setPostinumero(valintakoeAikaViewModel.getPostinumero());
            osoiteTyyppi.setPostitoimipaikka(valintakoeAikaViewModel.getPostitoimiPaikka());
            ajankohtaTyyppi.setValintakoeAjankohtaOsoite(osoiteTyyppi);

            ajankohtas.add(ajankohtaTyyppi);
        }

        return ajankohtas;
    }

}
