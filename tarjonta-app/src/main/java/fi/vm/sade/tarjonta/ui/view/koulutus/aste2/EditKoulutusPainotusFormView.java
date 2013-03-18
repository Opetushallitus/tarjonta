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
package fi.vm.sade.tarjonta.ui.view.koulutus.aste2;

import fi.vm.sade.tarjonta.ui.view.common.DialogKoodistoDataTable;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusToisenAsteenPerustiedotViewModel;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

/**
 *
 * @author Jani Wilén
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
public class EditKoulutusPainotusFormView extends VerticalLayout {
    private static final long serialVersionUID = -7390072782910392437L;
    
    private KoulutusToisenAsteenPerustiedotViewModel koulutusPerustiedotModel;
    @PropertyId("painotus")
    private DialogKoodistoDataTable<KielikaannosViewModel> ddt;
    
    public EditKoulutusPainotusFormView(KoulutusToisenAsteenPerustiedotViewModel koulutusPerustiedotModel) {
        this.setSpacing(true);
        this.setSizeFull();
        this.koulutusPerustiedotModel = koulutusPerustiedotModel;
        addYhteyshenkiloSelectorAndEditor(this);
    }

    /**
     * Create painotus part of the form.
     *
     * @param layout
     */
    private void addYhteyshenkiloSelectorAndEditor(AbstractLayout layout) {
        final Class classYhteyshenkilo = KielikaannosViewModel.class;
           
        ddt = new DialogKoodistoDataTable<KielikaannosViewModel>(classYhteyshenkilo, koulutusPerustiedotModel.getPainotus());

        //Overide default button property
        ddt.setButtonProperties("LisaaUusi.painotus");

        //Add form for dialog.
        ddt.buildByFormLayout(layout, "Lisää painotus", 450, 250, new EditKoulutusPainotusView());
        ddt.setSizeUndefined();
        ddt.setPageLength(0);
        //Add visible table columns.
        ddt.setColumnHeader("nimi", "Painotus");
        ddt.setColumnHeader("kielikoodi", "Kieli");
        ddt.setVisibleColumns(new Object[]{"nimi", "kielikoodi"});
        ddt.setKoodistoColumns(new String[]{"kielikoodi"});
        ddt.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
        layout.addComponent(ddt);
    }
}
