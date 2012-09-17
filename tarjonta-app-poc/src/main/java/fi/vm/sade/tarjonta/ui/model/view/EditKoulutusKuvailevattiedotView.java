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
package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.TarjontaPresenter;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author mlyly
 */
@Configurable
public class EditKoulutusKuvailevattiedotView extends VerticalLayout {

    @Autowired
    private TarjontaPresenter _presenter;

    public EditKoulutusKuvailevattiedotView() {
        super();
        setSizeUndefined();
        setWidth("100%");
        setSpacing(true);
        setMargin(true, false, true, true);

        initialize();
    }

    private void initialize() {
        removeAllComponents();

        // TOP BUTTONS
        createButtons(this);

        VerticalLayout vl = UiBuilder.newVerticalLayout();
        vl.setSpacing(true);
        vl.setWidth("100%");

        Panel p = UiBuilder.newPanel();
        p.setCaption("Koulutuksen kuvaievat tiedot");
        p.setContent(vl);
        p.setWidth("100%");
        addComponent(p);

        String[] s = new String[] {
            "Tutkinnon rakenne...",
            "Tutkinnon koulutukselliset tavoitteet...",
            "Koulutuksen sisältö...",
            "Jatko-opintomahdollisuudet...",
            "Sijoittuminen työelämään...",
            "Kansainvälistyminen ...",
            "Yhteistyö muoden toimijoiden kanssa...",
            "Lisätietoja...",
        };

        for (String string : s) {
            UiBuilder.newTextPanel(string, "100%", null, vl);

            TabSheet tabs = UiBuilder.newTabSheet(vl);
            tabs.addTab(createRTA(), "Suomi");
            tabs.addTab(createRTA(), "Ruotsi");
            tabs.addTab(createRTA(), "Englanti");
        }

        // BOTTOM BUTTONS
        createButtons(this);
    }


    private HorizontalLayout createButtons(AbstractLayout vl) {

        final Button.ClickListener l = new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                _presenter.showMainKoulutusView();
            }
        };


        HorizontalLayout hl = UiBuilder.newHorizontalLayout();
        hl.setSizeUndefined();

        Button b = UiBuilder.newButtonSmallSecodary("Peruuta", hl);
        b.addListener(l);

        b = UiBuilder.newButtonSmallPrimary("Tallenna luonnoksena", hl);
        b.addListener(l);

        b = UiBuilder.newButtonSmallPrimary("Tallenna valmiina", hl);
        b.addListener(l);

        b = UiBuilder.newButtonSmallPrimary("Jatka", hl);
        b.addListener(l);

        if (vl != null) {
            vl.addComponent(hl);
        }

        return hl;
    }


    private RichTextArea createRTA() {
        RichTextArea rta = UiBuilder.newRichTextArea(null, null, null);
        rta.setValue("ed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?");
        return rta;
    }

}
