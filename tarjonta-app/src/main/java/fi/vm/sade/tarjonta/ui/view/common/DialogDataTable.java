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
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.ui.helper.BeanItemMapper;
import fi.vm.sade.tarjonta.ui.view.koulutus.DialogKoulutusView;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public class DialogDataTable<T> extends Table {

    private static final Logger LOG = LoggerFactory.getLogger(DialogDataTable.class);
    private BeanItemContainer container;
    private AbstractDataTableDialog dialog;
    private Class objectItem;
    private Listener listener;
    private transient I18NHelper _i18n;
    //Initialize default button properties, the properties can be overridden.  
    private static String[] i18nButtonProperties = {
        DialogButtonEnum.BUTTON_ADD.getI18nProperty(),
        DialogButtonEnum.BUTTON_EDIT.getI18nProperty(),
        DialogButtonEnum.BUTTON_REMOVE.getI18nProperty()
    };

    public DialogDataTable(final Class objectItem, final BeanItemContainer container) {
        super(null, container);
        initialize(objectItem, container);
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
                if (i18nButtonProperties.length <= index) {
                    break;
                }

                i18nButtonProperties[index] = p;
            }
        }
    }

    private void initialize(final Class objectItem, final BeanItemContainer container) {
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

        this.objectItem = objectItem;
        this.container = container;

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
        HorizontalLayout hl = UiUtil.horizontalLayout(true, UiMarginEnum.TOP);
        layout.addComponent(hl);

        UiUtil.buttonSmallPrimary(hl, T(getButtonCaptionProperty(DialogButtonEnum.BUTTON_ADD)), new Button.ClickListener() {
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

        final Button btnEdit = UiUtil.buttonSmallPrimary(hl, T(getButtonCaptionProperty(DialogButtonEnum.BUTTON_EDIT)), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().addWindow(dialog);
            }
        });
        btnEdit.setEnabled(false);

        final Button btnDelete = UiUtil.buttonSmallPrimary(hl, T(getButtonCaptionProperty(DialogButtonEnum.BUTTON_REMOVE)), new Button.ClickListener() {
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

    private static String getButtonCaptionProperty(DialogButtonEnum e) {
        if (e == null) {
            throw new RuntimeException("Application error - DialogButtonEnum cannot be null.");
        }

        return i18nButtonProperties[e.getIndex()];
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

    private enum DialogButtonEnum {

        BUTTON_ADD(0, "LisaaUusi"),
        BUTTON_EDIT(1, "muokkaa"),
        BUTTON_REMOVE(2, "poista");
        private int index;
        private String property;

        private DialogButtonEnum(int index, String property) {
            this.index = index;
            this.property = property;
        }

        public int getIndex() {
            return index;
        }

        public String getI18nProperty() {
            return property;
        }
    }

    protected String T(String key) {
        return getI18n().getMessage(key);
    }

    protected I18NHelper getI18n() {
        if (_i18n == null) {
            _i18n = new I18NHelper(this);
        }
        return _i18n;
    }
}
