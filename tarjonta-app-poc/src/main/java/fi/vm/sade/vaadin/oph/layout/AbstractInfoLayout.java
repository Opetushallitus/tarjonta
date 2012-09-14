package fi.vm.sade.vaadin.oph.layout;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.Reindeer;
import fi.vm.sade.tarjonta.ui.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.poc.helper.I18NHelper;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.oph.dto.PageNavigationDTO;
import fi.vm.sade.vaadin.oph.enums.UiMarginEnum;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author jani
 */
@Configurable(preConstruction = true)
public abstract class AbstractInfoLayout<T extends AbstractLayout> extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractInfoLayout.class);
    protected I18NHelper i18n = new I18NHelper(this);
    @Autowired(required = true)
    protected TarjontaPresenter _presenter;
    private HorizontalLayout buttons = UiBuilder.newHorizontalLayout(true, UiMarginEnum.NONE, UiBuilder.DEFAULT_REALTIVE_SIZE, "40px");
    private T layout;

    public AbstractInfoLayout(Class<T> clazz, String pageTitle, String message, PageNavigationDTO dto) {
        super();
        setMargin(true);
        VerticalLayout topArea = UiBuilder.newVerticalLayout(UiBuilder.PCT100, "120px");

        if (message != null) {
            UiBuilder.newLabel(message, topArea);
        }
        topArea.addComponent(buttons);
        Label title = UiBuilder.newLabel(pageTitle, topArea);
        title.setStyleName(Oph.LABEL_H1);
        HorizontalLayout buildNavigation = buildNavigation(dto);
        topArea.addComponent(buildNavigation);

        addComponent(topArea);

        try {
            layout = clazz.newInstance();
            layout.setSizeFull();
            buildLayout(layout);
            addComponent(layout);
            addComponent(buildNavigation(dto));

            setComponentAlignment(topArea, Alignment.TOP_LEFT);
            setComponentAlignment(buildNavigation, Alignment.TOP_LEFT);
            setComponentAlignment(layout, Alignment.TOP_LEFT);

            setExpandRatio(layout, 1f);
        } catch (Exception ex) {
            LOG.error("Abstract class cannot initialize class.", ex);
        }

    }

    private HorizontalLayout buildNavigation(PageNavigationDTO dto) {
        HorizontalLayout hl = UiBuilder.newHorizontalLayout();
        if (dto == null) {
            LOG.debug("No navigation links added to layout.");
            return hl;
        }

        if (dto.getBtnNext() == null || dto.getBtnPrevious() == null) {
            throw new RuntimeException("Invalid input data, cannot create page layout.");
        }

        Button next = initButton(dto.getBtnNext().getCaption(), dto.getBtnNext().getListener(), hl);

        Label newLabel = UiBuilder.newLabel(dto.getMiddleResultText(), hl);
        newLabel.setStyleName(Oph.LABEL_H2);

        Button prev = initButton(dto.getBtnPrevious().getCaption(), dto.getBtnPrevious().getListener(), hl);

        hl.setExpandRatio(newLabel, 1f);

        hl.setComponentAlignment(newLabel, Alignment.TOP_CENTER);
        hl.setComponentAlignment(next, Alignment.TOP_RIGHT);

        return hl;
    }

    private Button initButton(String caption, ClickListener listenerNext, AbstractLayout l) {
        Button btn = UiBuilder.newButton(caption, l);

        btn.addListener(listenerNext);
        l.addComponent(btn);
        btn.setStyleName(Oph.BUTTON_LINK);

        return btn;
    }

    protected abstract void buildLayout(T layout);

    public Button addNavigationButton(String name, Button.ClickListener listener) {
        Button btn = UiBuilder.newButton(name, buttons);
        btn.addListener(listener);
        buttons.addComponent(btn);

        return btn;
    }

    public Button addNavigationButton(String name, Button.ClickListener listener, String... styles) {
        Button btn = UiBuilder.newButton(name, buttons);
        btn.addListener(listener);

        if (styles != null) {
            for (String s : styles) {
                btn.addStyleName(s);
            }
        }

        return btn;
    }
    
    
     public void addLayoutSplit() {
        VerticalSplitPanel split = new VerticalSplitPanel();
        split.setImmediate(false);
        split.setWidth("100%");
        split.setHeight("2px");
        split.setLocked(true);

        layout.addComponent(split);
    }
}
