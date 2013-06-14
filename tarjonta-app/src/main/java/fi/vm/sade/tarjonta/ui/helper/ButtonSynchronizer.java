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
package fi.vm.sade.tarjonta.ui.helper;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;


/**
 * Apuluokka (dialogeja avaavien) nappien synkronointiin; takaa, että synkronoituja {@link ClickListener}eitä ei suoriteta
 * ennen kuin edellinen on valmistunut ja sen avaamat (synkronoidut) dialogit ovat sulkeutuneet.
 * 
 * @author Timo Santasalo / Teknokala Ky
 */
public class ButtonSynchronizer implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final Set<Button> buttons = new HashSet<Button>();
	private final Set<Window> windows = new HashSet<Window>();
	
	/**
	 * Synkronoi ikkunan/dialogin.
	 * 
	 * @return Parametrinä annettu ikkuna.
	 */
	public synchronized Window synchronize(Window w) {
		windows.add(w);
		w.addListener(new CloseListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void windowClose(CloseEvent e) {
				synchronized(ButtonSynchronizer.this) {
					windows.remove(e.getWindow());
				}
			}
		});
		return w;
	}

	/**
	 * Synkronoi napin sitoo siihen {@link ClickListener}in.
	 * 
	 * @see Button#addListener(ClickListener)
	 * @return Parametrinä annettu nappi.
	 */
	public synchronized Button synchronize(Button btn, final ClickListener listener) {
		buttons.add(btn);
		
		btn.addListener(new ClickListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				synchronized (ButtonSynchronizer.this) {
					if (!windows.isEmpty()) {
						return;
					}

					final Set<Button> enableds = new HashSet<Button>();
					for (Button b : buttons) {
						if (b.isEnabled()) {
							enableds.add(b);
							b.setEnabled(false);
						}
					}

					try {
						listener.buttonClick(event);
					} finally {
						for (Button b : enableds) {
							b.setEnabled(true);
						}
					}
				}
			}
		});
		
		return btn;
	}
	

}
