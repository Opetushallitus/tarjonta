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
package fi.vm.sade.tarjonta.service.impl.conversion.util;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.TekstiKaannos;

/**
 * AntiSamy-pohjainen XSS-filtteri.
 * 
 * @author Timo Santasalo / Teknokala Ky
 */
public final class XssFilter {
	
	private static final String ANTISAMY_POLICY = "fi.vm.sade.tarjonta.antisamy.xml";

	private static final AntiSamy antiSamy;
	static {
		try {
			antiSamy = new AntiSamy(Policy.getInstance(
				Thread.currentThread().getContextClassLoader().getResource(ANTISAMY_POLICY)));
		} catch (PolicyException e) {
			throw new IllegalStateException("Failed to initialized AntiSamy",e);
		}
	}

	private XssFilter() {}

	public static MonikielinenTeksti filter(MonikielinenTeksti mkt) {
		if (mkt!=null) {
			for (TekstiKaannos tk : mkt.getTekstis()) {
				tk.setArvo(filter(tk.getArvo()));
			}
		}
		return mkt;
	}
	
	public static String filter(String input) {
		try {
			return input==null ? null : antiSamy.scan(input.trim()).getCleanHTML();
		} catch (ScanException e) {
			throw new IllegalArgumentException("AntiSamy failed while scanning following html: '"+input+"'");
		} catch (PolicyException e) {
			throw new IllegalArgumentException("AntiSamy failed due to invalid profile");
		}
	}

}
