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
package fi.vm.sade.tarjonta.ui.view.koulutus.kk;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import static com.vaadin.ui.Table.COLUMN_HEADER_MODE_HIDDEN;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.ui.enums.TarjontaStyles;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.TutkintoohjelmaModel;
import static fi.vm.sade.tarjonta.ui.view.hakukohde.ListHakukohdeView.COLUMN_A;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Jani Wilén
 */
public class CollapsibleTutkintoohjelmaTable extends Table {

    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_LANGS = "languages";
    private static final long serialVersionUID = -841933988982354859L;
    private ArrayList<Object> selectedIds;
    private ClickRowWrapper selectedRow;
    private TarjontaKoodistoHelper tkHelper;

    public CollapsibleTutkintoohjelmaTable(TarjontaKoodistoHelper tkHelper) {
        this.tkHelper = tkHelper;

        addStyleName(TarjontaStyles.CATEGORY_TREE.getStyleName());
        setColumnHeaderMode(COLUMN_HEADER_MODE_HIDDEN);
        this.addListener(new ItemClickEvent.ItemClickListener() {
            private static final long serialVersionUID = -2318797984292753676L;

            @Override
            public void itemClick(ItemClickEvent event) {
                if (event == null) {
                    return;
                }

                final Item item = getItem(event.getItemId());

                if (item == null || item.getItemProperty(COLUMN_A) == null) {
                    return;
                }

                final Object clickedRowObject = item.getItemProperty(COLUMN_NAME).getValue();
                if (clickedRowObject != null && clickedRowObject instanceof ClickRowWrapper) {
                    if (selectedRow == clickedRowObject) {
                        selectedRow = null;
                        //remove and close rows
                        for (Object o : selectedIds) {
                            removeItem(o);
                        }
                        return;
                    } else if (selectedIds != null && !selectedIds.isEmpty()) {
                        //remove rows and open other
                        for (Object o : selectedIds) {
                            removeItem(o);
                        }
                    }

                    selectedRow = (ClickRowWrapper) clickedRowObject;
                    setValue(event.getItemId()); // set selected

                    selectedIds = Lists.newArrayList();

                    for (final KielikaannosViewModel kieli : selectedRow.getModel().getKielikaannos()) {
                        //2nd level hierarchy
                        LanguageRowWrapper languageRow = new LanguageRowWrapper(kieli);
                        addItemAfter(event.getItemId(), languageRow);
                        getContainerProperty(languageRow, COLUMN_NAME).setValue(languageRow);
                        getContainerProperty(languageRow, COLUMN_LANGS).setValue(convertKoodiLanguageUriToLangName(kieli.getKielikoodi(), selectedRow.getModel().getKielikoodi()));
                        selectedIds.add(languageRow);
                    }

                }
            }
        });
    }

    public List<KielikaannosViewModel> getSelectedRows() {
        ArrayList<KielikaannosViewModel> selectedRows = Lists.<KielikaannosViewModel>newArrayList();
        if (selectedIds != null) {
            for (Object o : selectedIds) {
                if (o != null && o instanceof LanguageRowWrapper) {
                    LanguageRowWrapper row = (LanguageRowWrapper) o;
                    if (row.isSelected()) {
                        selectedRows.add(row.getModel());
                    }
                }
            }
        }
        return selectedRows;
    }

    public void addDataToContainer(List<TutkintoohjelmaModel> TutkintoohjelmaModels) {
        //Adding the actual Hakukohde-listing component.

        TutkintoohjelmaModels = Lists.<TutkintoohjelmaModel>newArrayList();
        TutkintoohjelmaModel tutkintoohjelmaModel1 = new TutkintoohjelmaModel();
        tutkintoohjelmaModel1.setKielikoodi("kieli_fi");
        TutkintoohjelmaModels.add(tutkintoohjelmaModel1);

        TutkintoohjelmaModel tutkintoohjelmaModel2 = new TutkintoohjelmaModel();
        tutkintoohjelmaModel2.setKielikoodi("kieli_fi");
        tutkintoohjelmaModel2.setNimi("Itä-Aasian tutkimus");
        TutkintoohjelmaModels.add(tutkintoohjelmaModel2);

        tutkintoohjelmaModel1.setNimi("Aasian tutkimus");
        ArrayList<KielikaannosViewModel> list1 = new ArrayList<KielikaannosViewModel>();
        list1.add(new KielikaannosViewModel("kieli_fi", "Aasian tutkimus"));
        list1.add(new KielikaannosViewModel("kieli_en", "Asian Studies"));
        list1.add(new KielikaannosViewModel("kieli_sv", "Asienstudier"));
        list1.add(new KielikaannosViewModel("kieli_es", "Estudios de Asia"));
        tutkintoohjelmaModel1.addKielikaannos(list1.get(0));
        tutkintoohjelmaModel1.addKielikaannos(list1.get(1));
        tutkintoohjelmaModel1.addKielikaannos(list1.get(2));
        tutkintoohjelmaModel1.addKielikaannos(list1.get(3));

        ArrayList<KielikaannosViewModel> list2 = new ArrayList<KielikaannosViewModel>();
        list2.add(new KielikaannosViewModel("kieli_fi", "Itä-Aasian tutkimus"));
        list2.add(new KielikaannosViewModel("kieli_en", "East Asia Studies"));
        tutkintoohjelmaModel2.addKielikaannos(list2.get(0));
        tutkintoohjelmaModel2.addKielikaannos(list2.get(1));


        HierarchicalContainer hc = new HierarchicalContainer();
        hc.addContainerProperty(COLUMN_NAME, Component.class, null);
        hc.addContainerProperty(COLUMN_LANGS, String.class, "");

        // Create the tree nodes
        for (TutkintoohjelmaModel toModel : TutkintoohjelmaModels) {
            //1st level hierarchy
            final Object rootItem = hc.addItem();
            hc.getContainerProperty(rootItem, COLUMN_NAME).setValue(new ClickRowWrapper(toModel));
            hc.getContainerProperty(rootItem, COLUMN_LANGS).setValue(langKoodiUriToReadableString(toModel));
        }
        setContainerDataSource(hc);

        setColumnExpandRatio(COLUMN_NAME, 0.7f);
        setColumnExpandRatio(COLUMN_LANGS, 0.3f);
    }

    private String langKoodiUriToReadableString(final TutkintoohjelmaModel m) {
        ArrayList<String> uris = new ArrayList<String>();
        for (String langUri : m.getLanguages()) {
            uris.add(convertKoodiLanguageUriToLangName(langUri, m.getKielikoodi()));
        }
        java.util.Collections.sort(uris);
        return StringUtils.join(uris, ',');
    }

    private class ClickRowWrapper extends Label {

        private static final long serialVersionUID = -8807683963481981634L;
        private TutkintoohjelmaModel model;

        public ClickRowWrapper(TutkintoohjelmaModel model) {
            super(model.getNimi());
            this.model = model;
        }

        /**
         * @return the model
         */
        public TutkintoohjelmaModel getModel() {
            return model;
        }

        /**
         * @param model the model to set
         */
        public void setModel(TutkintoohjelmaModel model) {
            this.model = model;
        }
    }

    private class LanguageRowWrapper extends HorizontalLayout {

        private static final long serialVersionUID = -7370762293381894035L;
        private KielikaannosViewModel model;
        private CheckBox cb;

        public LanguageRowWrapper() {
        }

        public LanguageRowWrapper(KielikaannosViewModel model) {
            this.model = model;
            this.setSizeUndefined();
            cb = new CheckBox();
            cb.setImmediate(true);
            cb.addListener(new Property.ValueChangeListener() {
                private static final long serialVersionUID = -382717228031608542L;

                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
                    System.out.println("value changed");
                    fireLanguageSelectionChangedEvent(cb);
                }
            });
            addComponent(cb);
            Label name = new Label(model.getNimi());
            name.setSizeFull();
            this.addComponent(name);
        }

        public boolean isSelected() {
            return cb.booleanValue();
        }

        /**
         * @return the model
         */
        public KielikaannosViewModel getModel() {
            return model;
        }

        /**
         * @param model the model to set
         */
        public void setModel(KielikaannosViewModel model) {
            this.model = model;
        }
    }

    /**
     * EVENT
     */
    public interface LanguageValueChangeListener extends ValueChangeListener {
    }

    public class LanguageSelectionChangedEvent extends ValueChangeEvent {

        private static final long serialVersionUID = -8352411703681910574L;

        public LanguageSelectionChangedEvent(Field source) {
            super(source);


        }
    }

    private void fireLanguageSelectionChangedEvent(Field source) {
        LanguageSelectionChangedEvent event = new LanguageSelectionChangedEvent(source);
        fireEvent(event);
    }

    /**
     * Convert language uri to language name by a koodi URI and an user's locale
     * lang uri. Fallback to FI.
     *
     * @param langUri
     * @param userLangUri
     * @return
     */
    private String convertKoodiLanguageUriToLangName(final String langUri, final String userLangUri) {
        Preconditions.checkNotNull(langUri, "Koodi URI cannot be null.");
        Preconditions.checkNotNull(userLangUri, "User language URI cannot be null.");

        KoodiType koodiByUri = tkHelper.getKoodiByUri(langUri);
        if (koodiByUri != null) {
            List<KoodiMetadataType> metadata = koodiByUri.getMetadata();
            final String shortKieliKoodi = TarjontaKoodistoHelper.convertKieliUriToKielikoodi(userLangUri);
            String fallbackText = null;

            //Try to find closest lang, fallback to FI
            for (KoodiMetadataType type : metadata) {
                if (shortKieliKoodi.contains(type.getKieli().value().toLowerCase())) {
                    return type.getNimi();
                } else if (type.getKieli().equals(KieliType.FI)) {
                    //store primary fallback value to:
                    fallbackText = type.getNimi();
                }
            }

            return fallbackText;
        }

        return "";
    }
}
