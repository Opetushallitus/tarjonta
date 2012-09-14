package fi.vm.sade.vaadin.oph.dto;

import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.ui.Button;

/**
 *
 * @author jani
 */
public class PageNavigationDTO {

    private ButtonDTO btnNext;
    private ButtonDTO btnPrevious;
    private String middleResultText;

    public PageNavigationDTO(ButtonDTO btnNext, ButtonDTO btnPrevious, String middleResultText) {
        this.btnNext = btnNext;
        this.btnPrevious = btnPrevious;
        this.middleResultText = middleResultText;
    }

    /**
     * @return the btnNext
     */
    public ButtonDTO getBtnNext() {
        return btnNext;
    }

    /**
     * @return the btnPrevious
     */
    public ButtonDTO getBtnPrevious() {
        return btnPrevious;
    }

    /**
     * @return the middleResultText
     */
    public String getMiddleResultText() {
        return middleResultText;
    }
}