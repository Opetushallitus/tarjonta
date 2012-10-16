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
package fi.vm.sade.tarjonta.ui.view.common;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import fi.vm.sade.tarjonta.ui.helper.BeanItemMapper;
import fi.vm.sade.tarjonta.ui.helper.I18NHelper;
import fi.vm.sade.tarjonta.ui.view.koulutus.DialogKoulutusView;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public class DialogDataTable<T> extends Table {

    private static final Logger LOG = LoggerFactory.getLogger(DialogDataTable.class);
    private I18NHelper i18n;
    private BeanItemContainer container;
    private BeanItemMapper bim;
    private AbstractDataTableDialog dialog;
    private Class objectItem;
    private Listener listener;
    private String[] i18nButtonProperties = {"DialogDataTable.LisaaUusi", "DialogDataTable.muokkaa", "DialogDataTable.poista"};

    public DialogDataTable(final Class objectItem, final BeanItemContainer container, final BeanItemMapper bim) {
        super(null, container);
        initialize(objectItem, container, bim);
    }

    public void buildByFormLayout(final AbstractLayout buttonLayout, final String label, final ComponentContainer component) {
        notNullButtonLayout(buttonLayout);
        notNullComponentLayout(component);

        dialog = new DialogKoulutusView(label, -1, -1, component);
        component.addListener(listener);
        buildButtonLayout(buttonLayout);
    }

    public void buildByFormLayout(final AbstractLayout buttonLayout, final String label, final int width, final int height, final ComponentContainer component) {
        notNullButtonLayout(buttonLayout);
        notNullComponentLayout(component);

        dialog = new DialogKoulutusView(label, width, height, component);
        component.addListener(listener);
        buildButtonLayout(buttonLayout);
    }

    public void buildByFormDialog(final AbstractLayout buttonLayout, final AbstractDataTableDialog dialogWithForm) {
        notNullButtonLayout(buttonLayout);
        if (dialogWithForm == null) {
            throw new RuntimeException("Application error - AbstractDataTableDialog instance cannot be null.");
        }

        this.dialog = dialogWithForm;
        dialogWithForm.getInstance().addListener(listener);
        buildButtonLayout(buttonLayout);
    }

    public void setButtonProperties(String... i18nProperties) {
        if (i18nProperties != null && i18nProperties.length > 0) {
            int index = 0;
            for (String p : i18nProperties) {
                if (this.i18nButtonProperties.length <= index) {
                    break;
                }

                this.i18nButtonProperties[index] = p;
            }
        }
    }

    private void initialize(final Class objectItem, final BeanItemContainer container, final BeanItemMapper bim) {
        setPageLength(6);
        setSizeFull();
        setSelectable(true);
        setImmediate(true);

        if (objectItem == null) {
            throw new RuntimeException("Application error - Object item instance cannot be null.");
        }

        if (container == null) {
            throw new RuntimeException("Application error - BeanItemContainer instance cannot be null.");
        }

        if (bim == null) {
            throw new RuntimeException("Application error - BeanItemMapper instance cannot be null.");
        }

        this.objectItem = objectItem;
        this.container = container;
        this.bim = bim;

        //
        // Editor actions, commit form and refresh tabel data
        //
        this.listener = new Component.Listener() {
            @Override
            public void componentEvent(Component.Event event) {
                if (event instanceof DataTableEvent.CancelEvent) {
                    LOG.debug("Cancel event received.");
                    dialog.getForm().discard();
                }
                if (event instanceof DataTableEvent.SaveEvent) {
                    dialog.getForm().commit();
                    LOG.debug("Save event received.");
                    refreshRowCache();
                }
                if (event instanceof DataTableEvent.DeleteEvent) {
                    LOG.debug("delete event received.");
                    deleteTableItem(container, dialog.getForm());
                }
                getWindow().removeWindow(dialog);
            }
        };
    }

    private void buildButtonLayout(AbstractLayout layout) {
        HorizontalLayout hl = UiUtil.horizontalLayout();
        layout.addComponent(hl);

        bim.addButtonPlus(hl, this.i18nButtonProperties[0], new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

                T newTableItem = null;
                try {
                    newTableItem = (T) objectItem.newInstance();
                } catch (Exception ex) {
                    throw new RuntimeException("Application error - failed to create an item for a table row.", ex);
                }

                container.addItem(newTableItem);
                select(newTableItem);

                getWindow().addWindow(dialog);
            }
        });

        final Button btnEdit = bim.addButtonPrimary(hl, this.i18nButtonProperties[1], new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().addWindow(dialog);
            }
        });
        btnEdit.setEnabled(false);

        final Button btnDelete = bim.addButtonPrimary(hl, this.i18nButtonProperties[2], new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                deleteTableItem(container, dialog.getForm());
            }
        });

        hl.setExpandRatio(btnDelete, 1l);

        btnDelete.setEnabled(false);

        //
        // Table selection, update form to edit correct item
        //
        addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                T selected = (T) event.getProperty().getValue();
                dialog.getForm().setEnabled(selected != null);
                if (selected != null) {
                    dialog.getForm().setItemDataSource(new BeanItem(selected));
                    btnEdit.setEnabled(true);
                    btnDelete.setEnabled(true);
                }
            }
        });
    }

    private void deleteTableItem(BeanItemContainer container, Form form) {
        T dto = (T) getValue();
        if (dto != null) {
            container.removeItem(dto);
            form.setItemDataSource(null);
            form.setEnabled(false);
        }
     
        // Autoselect in table
        if (container.firstItemId() != null) {
            setValue(container.firstItemId());
        }
    }

    private void notNullButtonLayout(AbstractLayout buttonLayout) {
        if (buttonLayout == null) {
            throw new RuntimeException("Application error - AbstractLayout instance for buttons cannot be null.");
        }
    }

    private void notNullComponentLayout(ComponentContainer component) {
        if (component == null) {
            throw new RuntimeException("Application error - ComponentContainer instance cannot be null.");
        }
    }
}
