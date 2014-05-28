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
package fi.vm.sade.tarjonta.publication.model;

import java.util.Locale;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 *
 * @author jani
 */
public class RestParam {

    private static final String DEFAULT_LANG_CODE = "FI";
    private Boolean showMeta = true; //default show meta data
    private Boolean showImg = false; //default do not show images 
    private String userLang = DEFAULT_LANG_CODE; //default fi
    private Locale locale;

    public RestParam() {
    }

    public RestParam(Boolean showMeta, Boolean showImg, String userLang) {
        this.setShowMeta(showMeta);
        this.setShowImg(showImg);
        this.setUserLang(userLang);

        this.locale = new Locale(getUserLang());
    }

    public static RestParam byUserRequest(Boolean showMeta, Boolean showImg, String userLang) {
        return new RestParam(showMeta, showImg, userLang);
    }

    public static RestParam noImage(Boolean showMeta, String userLang) {
        return new RestParam(showMeta, false, userLang);
    }

    public static RestParam noImageAndShowMeta(String userLang) {
        return new RestParam(true, false, userLang);
    }

    public static RestParam showImageAndShowMeta(String userLang) {
        return new RestParam(false, true, userLang);
    }

    public static RestParam showImageAndNoMeta(String userLang) {
        return new RestParam(false, true, userLang);
    }

    public static RestParam showImageAndNoMeta() {
        return new RestParam(false, true, null);
    }

    /**
     * @return the showMeta
     */
    public Boolean getShowMeta() {
        return showMeta;
    }

    /**
     * @param showMeta the showMeta to set
     */
    public void setShowMeta(Boolean showMeta) {
        this.showMeta = checkArgsDefaultTrue(showMeta);
    }

    /**
     * @return the showImg
     */
    public Boolean getShowImg() {
        return showImg;
    }

    /**
     * @param showImg the showImg to set
     */
    public void setShowImg(Boolean showImg) {
        this.showImg = checkArgsDefaultFalse(showImg);
    }

    /**
     * @return the userLang
     */
    public String getUserLang() {
        return userLang;
    }

    /**
     * @param userLang the userLang to set
     */
    public void setUserLang(String userLang) {
        this.userLang = checkArgsLangCode(userLang);
    }

    /**
     * Validate user language code. Default or fallback value is 'FI'.
     *
     * @param lang
     * @return
     */
    private static String checkArgsLangCode(String lang) {
        if (lang == null || lang.isEmpty() || lang.length() != 2) {
            return DEFAULT_LANG_CODE;
        }

        return lang;
    }

    /**
     * Validate the show argument. Null is boolean true.
     *
     * @param meta
     * @return
     */
    private static boolean checkArgsDefaultTrue(Boolean meta) {
        return meta != null ? meta : true;
    }

    /**
     * Validate the show argument. Null is boolean false.
     *
     * @param meta
     * @return
     */
    private static boolean checkArgsDefaultFalse(Boolean meta) {
        return meta != null ? meta : false;
    }

    /**
     * @return the locale
     */
    public Locale getLocale() {
        return locale;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
