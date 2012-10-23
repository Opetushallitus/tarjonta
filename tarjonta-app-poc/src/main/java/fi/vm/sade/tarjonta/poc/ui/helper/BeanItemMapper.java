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
package fi.vm.sade.tarjonta.poc.ui.helper;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.event.MouseEvents;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public class BeanItemMapper<T, E> {

    private static final Logger LOG = LoggerFactory.getLogger(BeanItemMapper.class);
    private BeanItem<T> psi;
    private I18NHelper i18n;
    private E target;

    public BeanItemMapper(T obj, I18NHelper i18n, E invokeTarget) {
        psi = new BeanItem(obj);
        this.i18n = i18n;
        this.target = invokeTarget;
    }

    /*
     * UI HELPERS TO CREATE COMPONENTS
     */
    /**
     * Add basic TextField with bound data.
     *
     * @param expression
     * @param promptKey
     * @param width
     * @param layout
     * @return
     */
    public TextField addTextField(AbstractLayout layout, String expression, String promptKey, String width) {
        TextField c = UiUtil.textField(layout, psi, expression, null, i18n.getMessage(promptKey));
        // c.setImmediate(true);

        if (width != null) {
            c.setWidth(width);
        }

        return c;
    }

    /**
     * Create a button.
     *
     * @param captionKey
     * @param onClickMethodName
     * @param layout
     * @return
     */
    public Button addButton(AbstractLayout layout, String captionKey, String onClickMethodName, StyleEnum styles) {
        Button c = UiUtil.button(layout, i18n.getMessage(captionKey));
        if (onClickMethodName != null) {
            c.addListener(getClickListener(onClickMethodName));
        }

        if (styles != null) {
            for (String style : styles.getStyles()) {
                c.addStyleName(style);
            }
        }

        return c;
    }

    /**
     * Create label with style.
     *
     * @param captionKey
     * @param style
     * @param layout
     * @return
     */
    public Label label(AbstractLayout layout, String captionKey, LabelStyleEnum style) {
        Label c = UiUtil.label(layout, i18n.getMessage(captionKey), style);
        return c;
    }

    public Label label(AbstractLayout layout, String captionKey) {
        Label c = UiUtil.label(layout, i18n.getMessage(captionKey));
        return c;
    }

    /**
     * Create label with style.
     *
     * @param captionKey
     * @param style
     * @param layout
     * @return
     */
    public Label addLabel(AbstractLayout layout, String captionKey) {
        Label c = UiUtil.label(layout, psi, captionKey);
        return c;
    }

    /**
     * Create DateField, bind to model. By default format is "dd.MM.yyyy".
     *
     * @param psi
     * @param expression
     * @param layout
     * @return
     */
    public DateField addDate(AbstractLayout layout, String expression) {
        DateField c = UiUtil.dateField(layout, null, null, psi, expression);
        return c;
    }

    /**
     * Create CheckBox and bind it to model.
     *
     * @param captionKey
     * @param psi
     * @param expression
     * @param valueChangeListenerMethod
     * @param layout
     * @return
     */
    public CheckBox addCheckBox(AbstractOrderedLayout layout, String captionKey, String expression, String valueChangeListenerMethod) {
        CheckBox c = UiUtil.checkBox(layout, i18n.getMessage(captionKey), psi, expression);

        // Routes "clicks" to methods
        if (valueChangeListenerMethod != null) {
            c.addListener(getValueChangeListener(valueChangeListenerMethod));
        }

        c.setImmediate(true);

        return c;
    }

    /**
     * Create icon as Embedded external resources.
     *
     * @param iconUrl
     * @param onClickListenerMethod
     * @return
     */
    public Embedded addHelpIcon(String iconUrl, String onClickListenerMethod) {
        Embedded helpIcon1 = new Embedded("", new ExternalResource(iconUrl));
        helpIcon1.setImmediate(true);

        if (onClickListenerMethod != null) {
            helpIcon1.addListener(getMouseClickListener(onClickListenerMethod));
        }

        return helpIcon1;
    }

    /**
     * Create KoodistoComponent with CompboBox as displaying widget and bind to
     * model.
     *
     * @param koodistoUri
     * @param psi
     * @param expression
     * @param promptKey
     * @param layout
     * @return
     */
    public KoodistoComponent addKoodistoComboBox(AbstractLayout layout, final String koodistoUri, String expression, String promptKey) {
        //DEBUGSAWAY:LOG.debug("addKoodistoComboBox({}, ...)", koodistoUri);
        return UiBuilder.koodistoComboBox(layout, koodistoUri, psi, expression, i18n.getMessage(promptKey));
    }

    /**
     * Create KoodistoComponent with TwinColSelect as widget and bind to model.
     *
     * @param koodistoUri
     * @param psi
     * @param expression
     * @param layout
     * @return
     */
    public KoodistoComponent addKoodistoTwinColSelect(AbstractOrderedLayout layout, final String koodistoUri,  String expression) {
        //DEBUGSAWAY:LOG.debug("addKoodistoTwinColSelect({}, ...)", koodistoUri);
        return UiBuilder.koodistoTwinColSelect(layout, koodistoUri, psi, expression, null);
    }

    /**
     * Creates a click listener that calls method <string>methodName</string> in
     * this instance.
     *
     * For buttons.
     *
     * @param methodName
     * @return
     */
    public Button.ClickListener getClickListener(final String methodName) {
        final Method m = getMethod(methodName);
        return new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    m.invoke(target);
                } catch (Throwable ex) {
                    LOG.error("invoke of method {} failed, ex={}", methodName, ex);
                }
            }
        };
    }

    /**
     * Creates a mouse click listener that calls method
     * <string>methodName</string> in this instance.
     *
     * For icons etc.
     *
     * @param methodName
     * @return
     */
    public MouseEvents.ClickListener getMouseClickListener(final String methodName) {

        final Method m = getMethod(methodName);

        return new MouseEvents.ClickListener() {
            @Override
            public void click(MouseEvents.ClickEvent event) {
                try {
                    m.invoke(target);
                } catch (Throwable ex) {
                    LOG.error("invoke of method {} failed, ex={}", methodName, ex);
                    LOG.error("", ex);
                }
            }
        };
    }

    /**
     * Creates a value change listener that calls method
     * <string>methodName</string> in this instance.
     *
     * Used for data related "events".
     *
     * @param methodName
     * @return
     */
    public Property.ValueChangeListener getValueChangeListener(final String methodName) {

        final Method m = getMethod(methodName);
        return new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                try {
                    m.invoke(target);
                } catch (Throwable ex) {
                    LOG.error("invoke of method {} failed, ex={}", methodName, ex);
                    LOG.error("", ex);
                }
            }
        };
    }

    /**
     * Get method by name.
     *
     * @param methodName
     * @return
     */
    private Method getMethod(String methodName) {
        try {
            return this.getClass().getMethod(methodName);
        } catch (Throwable ex) {
            LOG.error("Failed to get method: {}", methodName, ex);
            LOG.error("", ex);
            return null;
        }
    }

    /**
     * @return the beanItem
     */
    public BeanItem<T> getBeanItem() {
        return psi;
    }

    /**
     * @param beanItem the beanItem to set
     */
    public void setBeanItem(BeanItem<T> beanItem) {
        this.psi = beanItem;
    }
}
