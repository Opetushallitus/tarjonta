package fi.vm.sade.tarjonta.ui.view.common;/*
 *
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

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.HorizontalLayout;
import fi.vm.sade.vaadin.util.UiUtil;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Tuomas Katva
 * Date: 5.3.2013
 */
public class SelectableItem<T> extends HorizontalLayout {

     private Button xButton;
     private Button label;
     private T item;


     private List<Button.ClickListener> listeners;

    public SelectableItem(T selectableItem, String captionProperty) {
         item = selectableItem;

         xButton = UiUtil.buttonLink(null, "(X)", new Button.ClickListener() {
             @Override
             public void buttonClick(Button.ClickEvent clickEvent) {
                 if (listeners != null) {
                  for (Button.ClickListener listener : listeners) {
                      listener.buttonClick(clickEvent);
                  }
                 }
             }
         });
         addComponent(xButton);

         String labelStr = null;

        try {
            labelStr  = BeanUtils.getProperty(item,captionProperty);
        } catch (IllegalAccessException e) {
            labelStr = "";
        } catch (InvocationTargetException e) {
            labelStr = "";
        } catch (NoSuchMethodException e) {
            labelStr = "";
        }

        label = UiUtil.buttonLink(null, labelStr, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

            }
        });
        addComponent(label);

    }

    public void addListener(Button.ClickListener listener) {
        if(listeners == null) {
            listeners = new ArrayList<Button.ClickListener>();
        }
        listeners.add(listener);
    }

    public T getItem() {
        return item;
    }

    public Button getxButton() {
        return xButton;
    }

    public Button getLabel() {
        return label;
    }

}
