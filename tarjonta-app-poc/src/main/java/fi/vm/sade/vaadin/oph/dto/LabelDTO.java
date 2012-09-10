package fi.vm.sade.vaadin.oph.dto;

import fi.vm.sade.vaadin.oph.enums.LabelStyle;

/**
 *
 * @author jani
 */
public class LabelDTO {

    private LabelStyle style = LabelStyle.TEXT;
    private String format;
    private Object[] formatArgs;

    public LabelDTO() {
    }

    public LabelDTO(String format) {
        this.format = format;
    }

    public LabelDTO(String format, Object[] args) {
        this.format = format;
        this.formatArgs = args;
    }

    public LabelDTO(String format, Object[] args, LabelStyle style) {
        this.style = style;
        this.format = format;
        this.formatArgs = args;
    }

    /**
     * @return the style
     */
    public LabelStyle getStyle() {
        return style;
    }

    /**
     * @param style the style to set
     */
    public void setStyle(LabelStyle style) {
        this.style = style;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @return the args
     */
    public Object[] getFormatArgs() {
        return formatArgs;
    }

    /**
     * @param args the args to set
     */
    public void setFormatArgs(Object[] args) {
        this.formatArgs = args;
    }
}
