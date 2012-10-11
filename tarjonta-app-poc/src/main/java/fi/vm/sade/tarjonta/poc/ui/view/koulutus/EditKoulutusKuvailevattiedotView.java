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
package fi.vm.sade.tarjonta.poc.ui.view.koulutus;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import fi.vm.sade.tarjonta.poc.ui.TarjontaPresenter;
import fi.vm.sade.tarjonta.poc.ui.view.common.AutoSizeVerticalLayout;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.tinymceeditor.TinyMCETextField;

/**
 *
 * @author mlyly
 */
@Configurable
public class EditKoulutusKuvailevattiedotView extends AutoSizeVerticalLayout {

    @Autowired
    private TarjontaPresenter _presenter;

    public EditKoulutusKuvailevattiedotView() {
        super(Type.PCT_100, Type.AUTOSIZE);
        setWidth(100, UNITS_PERCENTAGE);
        setSpacing(true);
        setMargin(true);

        initialize();
    }

    private void initialize() {
        removeAllComponents();

        String[] s = new String[]{
            "Tutkinnon rakenne...",
            "Tutkinnon koulutukselliset tavoitteet...",
            "Koulutuksen sisältö...",
            "Jatko-opintomahdollisuudet...",
            "Sijoittuminen työelämään...",
            "Kansainvälistyminen ...",
            "Yhteistyö muoden toimijoiden kanssa...",
            "Lisätietoja...",};

        for (String string : s) {
            UiUtil.textPanel(string, "100%", null, this);

            TabSheet tabs = UiUtil.tabSheet(this);
            tabs.addTab(createRTA(), "Suomi");
            tabs.addTab(createRTA(), "Ruotsi");
            tabs.addTab(createRTA(), "Englanti");
        }
    }
    private Component createRTA() {

        TinyMCETextField tinyTF = new TinyMCETextField();
        tinyTF.setValue("<p><b>This</b> <i>text</i> is <u>cool</u>!</p>\n" +
                "<ul>\n" +
                "<li>Row 1</li>\n" +
                "<li>Row 2</li>\n" +
                "<li>Row three</li>\n" +
                "</ul>\n" +
                "<ol>\n" +
                "<li>One</li>\n" +
                "<li>Two</li>\n" +
                "<li>Three</li>\n" +
                "</ol>\n" +
                "<p>&nbsp;</p>");
        tinyTF.setWidth("400px");
        tinyTF.setHeight("150px");

        tinyTF.setCaption("TinyMCE via Vaadin Wrapper Addon - simple mode");
        tinyTF.setConfig("{theme: 'simple'}");

        return tinyTF;

//        RichTextArea rta = UiUtil.richTextArea(null, null, null);
//        rta.setWidth(100, UNITS_PERCENTAGE);
//        rta.setValue("ed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?");
//        return rta;
    }
}
