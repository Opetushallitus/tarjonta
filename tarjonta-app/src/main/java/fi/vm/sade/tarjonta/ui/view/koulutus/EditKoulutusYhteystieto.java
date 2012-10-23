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
package fi.vm.sade.tarjonta.ui.view.koulutus;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.model.KoulutusLinkkiViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusYhteyshenkiloViewModel;
import fi.vm.sade.tarjonta.ui.view.common.DialogDataTable;
import java.util.Collection;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

/**
 *
 * @author Jani Wilén
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
public class EditKoulutusYhteystieto extends VerticalLayout {
    
    private KoulutusToisenAsteenPerustiedotViewModel koulutusPerustiedotModel;
    @PropertyId("yhteyshenkilot")
    DialogDataTable<KoulutusYhteyshenkiloViewModel> ddt;
    
    public EditKoulutusYhteystieto(KoulutusToisenAsteenPerustiedotViewModel koulutusPerustiedotModel) {
        this.koulutusPerustiedotModel = koulutusPerustiedotModel;
        addYhteyshenkiloSelectorAndEditor(this);
    }

    /**
     * Create yhteystiedot part of the form.
     *
     * @param layout
     */
    private void addYhteyshenkiloSelectorAndEditor(AbstractLayout layout) {
        // headerLayout(layout, "Yhteyshenkilo");
        final Class classYhteyshenkilo = KoulutusYhteyshenkiloViewModel.class;

        //Attach data model to Vaadin bean container.
        final BeanItemContainer<KoulutusYhteyshenkiloViewModel> yhteyshenkiloContainer =
                new BeanItemContainer<KoulutusYhteyshenkiloViewModel>(classYhteyshenkilo, koulutusPerustiedotModel.getYhteyshenkilot());

        //Initialize dialog table with control buttons.
        ddt = new DialogDataTable<KoulutusYhteyshenkiloViewModel>(classYhteyshenkilo, yhteyshenkiloContainer);

        //Overide default button property
        ddt.setButtonProperties("LisaaUusi.Yhteyshenkilo");

        //Add form for dialog.
        ddt.buildByFormLayout(layout, "Luo uusi yhteystieto", 350, 500, new EditKoulutusPerustiedotYhteystietoView());

        //Add visible table columns.
        ddt.setColumnHeader("etunimet", "Etunimi");
        ddt.setColumnHeader("sukunimi", "Sukunimi");
        ddt.setColumnHeader("email", "Sähköposti");
        ddt.setColumnHeader("puhelin", "Puhelin");
        ddt.setColumnHeader("kielet", "Pätee kielille");
        ddt.setVisibleColumns(new Object[]{"etunimet", "sukunimi", "titteli", "email", "puhelin", "kielet"});
        layout.addComponent(ddt);
        
        ddt.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Collection<?> itemIds = ddt.getContainerDataSource().getItemIds();
                for (Object o : itemIds) {
                    koulutusPerustiedotModel.getYhteyshenkilot().add((KoulutusYhteyshenkiloViewModel) o);
                }
            }
        });
    }
}
