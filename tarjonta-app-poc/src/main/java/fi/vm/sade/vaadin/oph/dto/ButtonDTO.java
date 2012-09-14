package fi.vm.sade.vaadin.oph.dto;

import com.vaadin.ui.Button.ClickListener;


/**
 *
 * @author jani
 */
public class ButtonDTO {

    private String caption;
    private ClickListener listener;

    public ButtonDTO(String caption, ClickListener listenerNext) {
        this.caption = caption;
        this.listener = listenerNext;
    }
    
    /**
     * @return the caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * @param caption the caption to set
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * @return the listener
     */
    public ClickListener getListener() {
        return listener;
    }

    /**
     * @param listener the listener to set
     */
    public void setListener(ClickListener listener) {
        this.listener = listener;
    }

   
}
