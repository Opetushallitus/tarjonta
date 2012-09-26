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
package fi.vm.sade.tarjonta.poc.ui.model.view;

import com.vaadin.ui.Form;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.ViewBoundForm;

/**
 *
 * @author jani
 */
@Configurable
public class AddHakuDokumenttiView extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(AddHakuDokumenttiView.class);

    public AddHakuDokumenttiView() {
        Panel p = new Panel();
        addComponent(p);

        VerticalLayout vl = UiUtil.verticalLayout();
        vl.setSpacing(true);

        EditorHakuView editor = new EditorHakuView();
        final Form f = new ViewBoundForm(editor);
        f.setWriteThrough(false);
        vl.addComponent(f);
        p.setContent(vl);


        editor.addListener(new Listener() {

            @Override
            public void componentEvent(Event event) {
                LOG.info("event: {}", event);
                if (event instanceof EditorHakuView.CancelEvent) {
                    f.discard();
                }
                if (event instanceof EditorHakuView.SaveEvent) {
                    f.commit();
                }
                if (event instanceof EditorHakuView.ContinueEvent) {
                    //
                }
            }
        });


    }

}
