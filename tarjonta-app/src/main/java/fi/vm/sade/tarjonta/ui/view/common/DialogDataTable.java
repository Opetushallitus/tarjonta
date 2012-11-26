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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.ui.enums.DialogDataTableButton;
import fi.vm.sade.tarjonta.ui.view.koulutus.DialogKoulutusView;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public class DialogDataTable<MODEL> extends Table {

    private static final Logger LOG = LoggerFactory.getLogger(DialogDataTable.class);
    private BeanItemContainer<MODEL> container; //Table data container
    private Collection<MODEL> data; //the data model
    private AbstractDataTableDialog dialog;
    private Class objectItem;
    private Listener listener;
    private Map<DialogDataTableButton, Button> buttons;
    private transient I18NHelper _i18n;
    private Button btnAdd;
    private BeanItem beanItem; //bean item container for dialog form
    //Initialize default button properties, the properties can be overridden.  
    private static String[] i18nButtonProperties = {
        DialogDataTableButton.BUTTON_ADD.getI18nProperty(),
        DialogDataTableButton.BUTTON_EDIT.getI18nProperty(),
        DialogDataTableButton.BUTTON_REMOVE.getI18nProperty()
    };

    public DialogDataTable(final Class objectItem, final Collection<MODEL> data) {
        super(null);

        if (data == null) {
            throw new RuntimeException("Application error - Collection object for data cannot be null.");
        }

        this.data = data;
        container = new BeanItemContainer<MODEL>(objectItem, data);
        container.removeAllItems();
        container.addAll(data);
        setContainerDataSource(container);

        initialize(objectItem);
    }

    /**
     * Build Vaadin Table instance. Method takes as a parameter Vaadin component
     * container and uses it as a window dialog.
     *
     * @param buttonLayout Layout for button(add, edit and remove) objects.
     * @param label Table caption
     * @param component Layout with form components and data binding.
     */
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

    private void initialize(final Class objectItem) {
        buttons = new EnumMap<DialogDataTableButton, Button>(DialogDataTableButton.class);
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

        //
        // Editor actions, commit form and refresh tabel data
        //
        this.listener = new Component.Listener() {
            @Override
            public void componentEvent(Component.Event event) {
                if (event instanceof DataTableEvent.CancelEvent) {
                    cancelOrClose();
                } else if (event instanceof DataTableEvent.SaveEvent) {
                    LOG.debug("Form save event received.");
                    //validated form
                    dialog.getForm().commit();

                    //add new valid data to table data container
                    MODEL bean = (MODEL) beanItem.getBean();
                    container.addBean(bean);

                    //refresh row data in table
                    refreshRowCache();

                    //Do not add duplicate objects to model data container.
                    if (!data.contains(bean)) {
                        data.add(bean);
                    }
                }
                getWindow().removeWindow(dialog);
            }
        };
    }

    private void buildButtonLayout(AbstractLayout layout) {
        HorizontalLayout hl = UiUtil.horizontalLayout(true, UiMarginEnum.TOP);
        layout.addComponent(hl);

        btnAdd = UiUtil.buttonSmallPrimary(hl, T(getButtonCaptionProperty(DialogDataTableButton.BUTTON_ADD)), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                event.getButton().setEnabled(false);
                try {
                    //create new instance of data model
                    final MODEL newTableItem = (MODEL) objectItem.newInstance();
                    beanItem = new BeanItem(newTableItem);

                    //initialize dialog form fields
                    dialog.getForm().setItemDataSource(beanItem);
                } catch (Exception ex) {
                    event.getButton().setEnabled(true);
                    throw new RuntimeException("Application error - failed to create an item for a table row.", ex);
                }

                getWindow().addWindow(dialog);
            }
        });
        buttons.put(DialogDataTableButton.BUTTON_ADD, btnAdd);

        final Button btnEdit = UiUtil.buttonSmallPrimary(hl, T(getButtonCaptionProperty(DialogDataTableButton.BUTTON_EDIT)), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (getValue() == null) {
                    throw new RuntimeException("Application error - no item(s) selected.");
                }

                //get editable model object
                beanItem = new BeanItem((MODEL) getValue());

                //bind data to dialog form fields
                dialog.getForm().setItemDataSource(beanItem);
                getWindow().addWindow(dialog);
            }
        });
        btnEdit.setEnabled(false);
        buttons.put(DialogDataTableButton.BUTTON_EDIT, btnEdit);

        final Button btnDelete = UiUtil.buttonSmallPrimary(hl, T(getButtonCaptionProperty(DialogDataTableButton.BUTTON_REMOVE)), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                final MODEL rowObject = (MODEL) getValue();
                boolean remove = data.remove(rowObject);

                if (!remove) {
                    throw new RuntimeException("Application error - model is missing item for remove.");
                }

                remove = container.removeItem(rowObject);

                if (!remove) {
                    throw new RuntimeException("Application error - bean container is missing item for remove.");
                }
            }
        });

        addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                //handle row select event
                final MODEL selected = (MODEL) event.getProperty().getValue();
                dialog.getForm().setEnabled(selected != null);
                btnEdit.setEnabled(selected != null);
                btnDelete.setEnabled(selected != null);
            }
        });

        dialog.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                cancelOrClose();
            }
        });

        hl.setExpandRatio(btnDelete, 1l);
        btnDelete.setEnabled(false);
        buttons.put(DialogDataTableButton.BUTTON_REMOVE, btnDelete);
    }

    private void cancelOrClose() {
        refreshRowCache();
        beanItem = null;
        btnAdd.setEnabled(true);
    }

    private static String getButtonCaptionProperty(DialogDataTableButton e) {
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

    protected String T(String key) {
        return getI18n().getMessage(key);
    }

    protected I18NHelper getI18n() {
        if (_i18n == null) {
            _i18n = new I18NHelper(this);
        }
        return _i18n;
    }

    /**
     * Get a table button by enum.
     *
     * @param e
     * @return Button instance.
     */
    public Button getButtonByType(DialogDataTableButton e) {
        if (e == null) {
            throw new RuntimeException("Application error - Enum DialogDataTableButton instance cannot be null.");
        }
        return buttons.get(e);
    }
}
