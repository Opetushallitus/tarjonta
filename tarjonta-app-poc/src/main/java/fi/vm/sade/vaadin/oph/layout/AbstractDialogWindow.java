package fi.vm.sade.vaadin.oph.layout;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import fi.vm.sade.vaadin.oph.enums.LabelStyle;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jani
 */
public abstract class AbstractDialogWindow extends AbstractWindow {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDialogWindow.class);
    private HorizontalLayout bottomLayout;
    private List<ButtonContainer> buttons;
    private Panel msgPanel;
    private Label topic;

    public AbstractDialogWindow(String winLabel, String topic, String message) {
        super(winLabel, false); //false, do not build layout in parent abstract class
        setDialogMessages(topic, message);
        buildLayout(getLayout());
        getLayout().setMargin(true);
        setHeight("65%");
        setWidth("65%");
    }

    public void buildDialogButtons() {
        if (buttons == null) {
            LOG.error("An initialization error, no initialised buttons.");
            return;
        }

        for (ButtonContainer c : buttons) {
            bottomLayout.addComponent(c.getBtn());
            bottomLayout.setComponentAlignment(c.getBtn(), Alignment.BOTTOM_RIGHT);
        }

        bottomLayout.setExpandRatio(buttons.get(0).getBtn(), 1f);
        getLayout().addComponent(bottomLayout);
    }

    public void addNavigationButton(String name, Button.ClickListener listener) {
        addNavigationButton(name, listener, null);
    }

    public void addNavigationButton(String name, Button.ClickListener listener, String... styles) {
        if (bottomLayout == null) {
            //init
            bottomLayout = UiBuilder.newHorizontalLayout();
            buttons = new ArrayList<ButtonContainer>();
        }

        Button btn = UiBuilder.newButton(name, bottomLayout);

        if (styles != null) {
            for (String s : styles) {
                btn.addStyleName(s);
            }
        }

        btn.addListener(listener);
        buttons.add(new ButtonContainer(name, btn, listener));
    }

    public void removeDialogButtons() {
        for (ButtonContainer c : buttons) {
            c.getBtn().removeListener(c.getListener());
        }

        buttons.clear();
    }

    /**
     * @param topic the topic to set
     */
    protected void setDialogMessages(String topic, String message) {
        if (topic != null && this.topic == null) {
            this.topic = UiBuilder.newLabel(topic, getLayout(), LabelStyle.H2);
        }

        if (message != null && this.msgPanel == null) {
            this.msgPanel = UiBuilder.newTextPanel(message, null, UiBuilder.DEFAULT_REALTIVE_SIZE, getLayout());
        }
    }

    private class ButtonContainer {

        private String name;
        private Button btn;
        private Button.ClickListener listener;

        public ButtonContainer(String name, Button btn, ClickListener listener) {
            this.name = name;
            this.btn = btn;
            this.listener = listener;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the btn
         */
        public Button getBtn() {
            return btn;
        }

        /**
         * @return the listener
         */
        public Button.ClickListener getListener() {
            return listener;
        }
    }
}
