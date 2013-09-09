/*
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
package fi.vm.sade.tarjonta.ui.helper.conversion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.NimettyMonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;

/**
 *
 * @author Jukka Raanamo
 */
public class ConversionUtils {

	public static NimettyMonikielinenTekstiTyyppi getTeksti(List<NimettyMonikielinenTekstiTyyppi> tekstit, Object key) {
		String kv = String.valueOf(key);
		for (NimettyMonikielinenTekstiTyyppi nmt : tekstit) {
			if (kv.equals(nmt.getTunniste())) {
				return nmt;
			}
		}		
		return null;
	}
	
	public static boolean containsTeksti(List<NimettyMonikielinenTekstiTyyppi> tekstit, Object key) {
		String kv = String.valueOf(key);
		for (NimettyMonikielinenTekstiTyyppi nmt : tekstit) {
			if (kv.equals(nmt.getTunniste())) {
				return !nmt.getTeksti().isEmpty();
			}
		}		
		return false;
	}

	public static void setTeksti(List<NimettyMonikielinenTekstiTyyppi> tekstit, Object key, String langCode, String teksti) {
		String kv = String.valueOf(key);
		NimettyMonikielinenTekstiTyyppi cu = null;
		for (NimettyMonikielinenTekstiTyyppi nmt : tekstit) {
			if (kv.equals(nmt.getTunniste())) {
				cu = nmt;
				break;
			}
		}
		if (cu==null) {
			cu = new NimettyMonikielinenTekstiTyyppi(new ArrayList<MonikielinenTekstiTyyppi.Teksti>(), String.valueOf(key));
			tekstit.add(cu);
		}
		MonikielinenTekstiTyyppi.Teksti txt = null;
		for (MonikielinenTekstiTyyppi.Teksti t : cu.getTeksti()) {
			if (t.getKieliKoodi().equals(langCode)) {
				txt = t;
			}
		}
		if (txt==null) {
			cu.getTeksti().add(KoulutusConveter.convertToMonikielinenTekstiTyyppi(langCode, teksti));
		} else {
			txt.setValue(teksti);
		}
		
	}

	public static void setTeksti(List<NimettyMonikielinenTekstiTyyppi> tekstit, Object key, MonikielinenTekstiTyyppi txt) {
		for (MonikielinenTekstiTyyppi.Teksti t : txt.getTeksti()) {
			setTeksti(tekstit, key, t.getKieliKoodi(), t.getValue());
		}
	}

    public static void clearTeksti(List<NimettyMonikielinenTekstiTyyppi> tekstit, Object tunniste) {
    	NimettyMonikielinenTekstiTyyppi old = ConversionUtils.getTeksti(tekstit, tunniste);
    	if (old!=null) {
    		tekstit.remove(old);
    	}
    }
    
    public static List<KielikaannosViewModel> convertTekstiToVM(MonikielinenTekstiTyyppi tekstiTyyppi) {

        if (tekstiTyyppi == null) {
            return Collections.emptyList();
        }

        List<KielikaannosViewModel> vastaus = new ArrayList<KielikaannosViewModel>();
        for (MonikielinenTekstiTyyppi.Teksti teksti : tekstiTyyppi.getTeksti()) {
            KielikaannosViewModel kvm = new KielikaannosViewModel(teksti.getKieliKoodi(), teksti.getValue());
            vastaus.add(kvm);
        }

        return vastaus;
    }

    public static MonikielinenTekstiTyyppi convertKielikaannosToMonikielinenTeksti(List<KielikaannosViewModel> kielikaannokset) {
        MonikielinenTekstiTyyppi monikielinenTekstiTyyppi = new MonikielinenTekstiTyyppi();

        for (KielikaannosViewModel kielikaannosViewModel : kielikaannokset) {
            MonikielinenTekstiTyyppi.Teksti teksti = new MonikielinenTekstiTyyppi.Teksti();

            teksti.setKieliKoodi(kielikaannosViewModel.getKielikoodi());
            teksti.setValue(kielikaannosViewModel.getNimi());

            monikielinenTekstiTyyppi.getTeksti().add(teksti);
        }

        return monikielinenTekstiTyyppi;
    }

}

