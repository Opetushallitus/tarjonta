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
import fi.vm.sade.tarjonta.service.types.OsoiteTyyppi;
import fi.vm.sade.tarjonta.service.types.PisterajaTyyppi;
import fi.vm.sade.tarjonta.service.types.ValinnanPisterajaTyyppi;
import fi.vm.sade.tarjonta.service.types.ValintakoeTyyppi;
import fi.vm.sade.tarjonta.ui.model.ValintakoeAikaViewModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeViewModel;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;

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
        convertPisterajatToViewModel(valintakoeViewModel, valintakoeTyyppi);
        valintakoeViewModel.setLisanayttoKuvaukset(ConversionUtils.convertTekstiToVM(valintakoeTyyppi.getLisaNaytot()));

        valintakoeViewModel.setValintakoeAjat(mapAjat(valintakoeTyyppi.getAjankohdat()));
        if (valintakoeTyyppi.getViimeisinPaivittajaOid() !=null) {
            valintakoeViewModel.setViimeisinPaivittaja(valintakoeTyyppi.getViimeisinPaivittajaOid());
        }
        if (valintakoeTyyppi.getViimeisinPaivitysPvm() != null) {
            valintakoeViewModel.setViimeisinPaivitysPvm(valintakoeTyyppi.getViimeisinPaivitysPvm());
        }

        return valintakoeViewModel;
    }

    private static void convertPisterajatToViewModel(ValintakoeViewModel valintakoeViewModel, ValintakoeTyyppi valintakoeTyyppi) {
        PisterajaTyyppi valintakoeRajat = null;
        PisterajaTyyppi lisapisteRajat = null;
        PisterajaTyyppi kokonaispisteRajat = null;
        for (PisterajaTyyppi curRajat:  valintakoeTyyppi.getPisterajat()) {
            if (curRajat.getValinnanPisteraja().equals(ValinnanPisterajaTyyppi.PAASYKOE)) {
                valintakoeRajat = curRajat;
            } else if (curRajat.getValinnanPisteraja().equals(ValinnanPisterajaTyyppi.LISAPISTEET)) {
                lisapisteRajat = curRajat;
            } else if (curRajat.getValinnanPisteraja().equals(ValinnanPisterajaTyyppi.KOKONAISPISTEET)) {
                kokonaispisteRajat = curRajat;
            }
        }
        if (valintakoeRajat != null) {
            if (valintakoeRajat.getAlinHyvaksyttyPistemaara() > -1) {
                valintakoeViewModel.setPkAlinHyvaksyttyPM("" + valintakoeRajat.getAlinHyvaksyttyPistemaara());
            } 
            valintakoeViewModel.setPkAlinPM("" + valintakoeRajat.getAlinPistemaara());
            valintakoeViewModel.setPkYlinPM("" + valintakoeRajat.getYlinPistemaara());
            
        } 
        if (lisapisteRajat != null) {
            if (lisapisteRajat.getAlinHyvaksyttyPistemaara() > -1) {
                valintakoeViewModel.setLpAlinHyvaksyttyPM("" + lisapisteRajat.getAlinHyvaksyttyPistemaara());
            }
            valintakoeViewModel.setLpAlinPM("" + lisapisteRajat.getAlinPistemaara());
            valintakoeViewModel.setLpYlinPM("" + lisapisteRajat.getYlinPistemaara());
            
        } 
        if (kokonaispisteRajat != null) {
            valintakoeViewModel.setKpAlinHyvaksyttyPM("" + kokonaispisteRajat.getAlinHyvaksyttyPistemaara());
        }
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
        valintakoeTyyppi.getPisterajat().addAll(mapPisterajatToDTO(valintakoeViewModel));
        valintakoeTyyppi.setLisaNaytot(ConversionUtils.convertKielikaannosToMonikielinenTeksti(valintakoeViewModel.getLisanayttoKuvaukset()));
        valintakoeTyyppi.setViimeisinPaivittajaOid(SecurityContextHolder.getContext().getAuthentication().getName());

        return valintakoeTyyppi;
    }
    
    private static List<PisterajaTyyppi> mapPisterajatToDTO(ValintakoeViewModel valintakoeViewModel) {
        List<PisterajaTyyppi> pisterajat = new ArrayList<PisterajaTyyppi>();
        if (valintakoeViewModel.getPkAlinHyvaksyttyPM() != null || valintakoeViewModel.getPkAlinPM() != null || valintakoeViewModel.getPkYlinPM() != null) {
            PisterajaTyyppi pkTyyppi = new PisterajaTyyppi();
            pkTyyppi.setValinnanPisteraja(ValinnanPisterajaTyyppi.PAASYKOE);
            if (valintakoeViewModel.getPkAlinHyvaksyttyPM() != null && !valintakoeViewModel.getPkAlinHyvaksyttyPM().isEmpty()) { 
                pkTyyppi.setAlinHyvaksyttyPistemaara(Double.parseDouble(valintakoeViewModel.getPkAlinHyvaksyttyPM().replace(',', '.')));
            } else {
                pkTyyppi.setAlinHyvaksyttyPistemaara(-1);
            }
            pkTyyppi.setAlinPistemaara(Double.parseDouble(valintakoeViewModel.getPkAlinPM() != null ? valintakoeViewModel.getPkAlinPM().replace(',', '.') : null));
            pkTyyppi.setYlinPistemaara(Double.parseDouble(valintakoeViewModel.getPkYlinPM() != null ? valintakoeViewModel.getPkYlinPM().replace(',', '.') : null));
            pisterajat.add(pkTyyppi);
        }
        if (valintakoeViewModel.getLpAlinHyvaksyttyPM() != null || valintakoeViewModel.getLpAlinPM() != null || valintakoeViewModel.getLpYlinPM() != null) {
            PisterajaTyyppi lpTyyppi = new PisterajaTyyppi();
            lpTyyppi.setValinnanPisteraja(ValinnanPisterajaTyyppi.LISAPISTEET);
            if (valintakoeViewModel.getLpAlinHyvaksyttyPM() != null && !valintakoeViewModel.getLpAlinHyvaksyttyPM().isEmpty()) {
                lpTyyppi.setAlinHyvaksyttyPistemaara(Double.parseDouble(valintakoeViewModel.getLpAlinHyvaksyttyPM().replace(',', '.')));
            } else {
                lpTyyppi.setAlinHyvaksyttyPistemaara(-1);
            }
            lpTyyppi.setAlinPistemaara(Double.parseDouble(valintakoeViewModel.getLpAlinPM() != null ? valintakoeViewModel.getLpAlinPM().replace(',', '.') : null));
            lpTyyppi.setYlinPistemaara(Double.parseDouble(valintakoeViewModel.getLpYlinPM() != null ? valintakoeViewModel.getLpYlinPM().replace(',', '.') : null));
            pisterajat.add(lpTyyppi);
        }
        if (valintakoeViewModel.getKpAlinHyvaksyttyPM() != null && !valintakoeViewModel.getKpAlinHyvaksyttyPM().isEmpty()) {
            PisterajaTyyppi kpTyyppi = new PisterajaTyyppi();
            kpTyyppi.setValinnanPisteraja(ValinnanPisterajaTyyppi.KOKONAISPISTEET);
            kpTyyppi.setAlinHyvaksyttyPistemaara(Double.parseDouble(valintakoeViewModel.getKpAlinHyvaksyttyPM().replace(',', '.')));
            pisterajat.add(kpTyyppi);
        } 
        return pisterajat;
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
