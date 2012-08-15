/*
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

package fi.vm.sade.tarjonta.ui.hakuera;

import com.vaadin.event.FieldEvents;

import fi.vm.sade.generic.ui.component.MultiLingualTextField;

import fi.vm.sade.koodisto.service.types.dto.Kieli;
import fi.vm.sade.koodisto.service.types.dto.KoodiDTO;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import fi.vm.sade.koodisto.widget.KoodistoComponent;

/**
 * Extension of MultiLingualTextField that is used in HakueraEditForm.
 * The extension is done to provide the pre-creation of nimi fields
 * based on selections from koodisto components. 
 * 
 * @author markus
 *
 */
public class HakueraMlTextField extends MultiLingualTextField {
    
    private HakueraEditForm hakueraForm;
    
    private boolean isNimiEditedByHand = false;

    public HakueraMlTextField(HakueraEditForm hakueraForm) {
        this.hakueraForm = hakueraForm;
        addNimiListeners();
    }
    
    /**
     * Updating the localized nimi fields of this Hakuera based on values of HakutyyppiKoodi, HakukausiKoodi and haunKohdejoukkoKoodi.
     * This method is called when the value of one of these components change.
     */
    public void updateNimiField() {
        getTextFi().setValue(getNimiValue(Kieli.FI));
        getTextSv().setValue(getNimiValue(Kieli.SV));
        getTextEn().setValue(getNimiValue(Kieli.EN));
    }

    /**
     * Sets the value of isNimiEditedByHand based on whether the nimi fields match the values of the koodisto fields based on which
     * the nimi is pre-constructed.
     * 
     */
    public void setNimiEditedByHand() {
       isNimiEditedByHand = !nimiMatchesKoodistoFields(); 
    }
    
    public boolean isNimiEditedByHand() {
        return isNimiEditedByHand;
    }

    public void setNimiEditedByHand(boolean isNimiEditedByHand) {
        this.isNimiEditedByHand = isNimiEditedByHand;
    }
    
    /**
     * Constructing one localized name for this Hakuera.
     * 
     * @param lang
     * @return
     */
    private String getNimiValue(Kieli lang) {
        
        return constructNimiFromParts(getPartOfNimi(lang, HakueraEditForm.KOODISTO_HAKUTYYPPI_URI, hakueraForm.getHakutyyppiKoodi()), 
                    getPartOfNimi(lang, HakueraEditForm.KOODISTO_HAKUKAUSI_URI, hakueraForm.getHakukausiKoodi()), 
                    getPartOfNimi(lang, HakueraEditForm.KOODISTO_KOHDEJOUKKO_URI, hakueraForm.getHaunKohdejoukkoKoodi()));
    }
    
    private String getPartOfNimi(Kieli lang, String koodistoUri, KoodistoComponent koodistoComp) {
        String nimiPart = "";
        if ((koodistoComp.getValue() != null)) {
            String val = (String)koodistoComp.getValue();
            nimiPart += getLocalizedKoodi(lang, koodistoUri, val);
        }
        return nimiPart;
    }
    
    /**
     * 
     * Returns true if all nimi (nimiFi, nimiSv, nimiEn) fields in model match the localized koodisto 
     * values from which the name is precreated. The goal is to check whether the name has been edited by hand. 
     * 
     * @return
     */
    private boolean nimiMatchesKoodistoFields() {
        return localizedNimiMatches(Kieli.FI) 
                && localizedNimiMatches(Kieli.SV)
                && localizedNimiMatches(Kieli.EN); 
    }
    
    /**
     * Returns true if a localized name has not been edited by hand, matches the localized koodisto 
     * values from which the name is precreated.
     * 
     * @param lang
     * @return
     */
    private boolean localizedNimiMatches(Kieli lang) {
        if (lang.equals(Kieli.FI)) {
            return getConstructedName(lang).equals((hakueraForm.getModel().getNimiFi() != null) ? hakueraForm.getModel().getNimiFi() : "");
        }
        else if (lang.equals(Kieli.SV)) {
            return getConstructedName(lang).equals((hakueraForm.getModel().getNimiSv() != null) ? hakueraForm.getModel().getNimiSv() : "");
        } else {
            return getConstructedName(lang).equals((hakueraForm.getModel().getNimiEn() != null) ? hakueraForm.getModel().getNimiEn() : "");
        }
    }
    
    private String getConstructedName(Kieli lang) {
        return constructNimiFromParts(getLocalizedKoodi(lang, HakueraEditForm.KOODISTO_HAKUTYYPPI_URI, hakueraForm.getModel().getHakutyyppi()),
                getLocalizedKoodi(lang, HakueraEditForm.KOODISTO_HAKUKAUSI_URI, hakueraForm.getModel().getHakukausi()),
                getLocalizedKoodi(lang, HakueraEditForm.KOODISTO_KOHDEJOUKKO_URI, hakueraForm.getModel().getKohdejoukko()));
    }
    
    /**
     * Returns the localized koodi value for a koodi.
     * 
     * @param lang the language in which the koodi is requested
     * @param koodistoUri the koodisto form which the koodi is requested
     * @param koodiVal the value for which the localized koodi is requested.
     * @return
     */
    private String getLocalizedKoodi(Kieli lang, String koodistoUri, String koodiVal) {
        String nimiPart = "";
        if (koodiVal != null) {
            for(KoodiDTO curKoodi:  this.hakueraForm.getKoodiService().listKoodiByArvo(koodiVal, koodistoUri, null)) {
                if (curKoodi.getKoodiArvo().equals(koodiVal)) {

                    nimiPart += KoodistoHelper.getKoodiMetadataForLanguage(curKoodi,lang).getNimi();
                }    
            }
        }
        return nimiPart;
    }
    
    /**
     * Constructs the pre-created nimi from its constituents given as parameters.
     * 
     * @param hakutyyppiKoodiVal
     * @param hakukausiKoodiVal
     * @param kohdejoukkoKoodiVal
     * @return
     */
    private String constructNimiFromParts(String hakutyyppiKoodiVal, String hakukausiKoodiVal, String kohdejoukkoKoodiVal) {
        String nimi = "";
        nimi += hakutyyppiKoodiVal;
        nimi += ((hakukausiKoodiVal.length() > 0) && (hakutyyppiKoodiVal.length() > 0))  ? ", "  : "";
        nimi += hakukausiKoodiVal;
        nimi += (nimi.length() > 0) ? ", " : "";
        nimi += kohdejoukkoKoodiVal;
        return nimi;
    }
    
    /**
     * Adds the TextChangeListeners to the nimi fields to detect manual editing of nimi fields.
     * After manual editing done by user the nimi fields are no longer automatically pre-created.
     */
    private void addNimiListeners() {
        getTextFi().addListener(new FieldEvents.TextChangeListener() {
            public void textChange(final FieldEvents.TextChangeEvent event) {
                isNimiEditedByHand = true;
            }
        });
        getTextSv().addListener(new FieldEvents.TextChangeListener() {
            public void textChange(final FieldEvents.TextChangeEvent event) {
                isNimiEditedByHand = true;
            }
        });
        getTextEn().addListener(new FieldEvents.TextChangeListener() {
            public void textChange(final FieldEvents.TextChangeEvent event) {
                isNimiEditedByHand = true;
            }
        });
    }
    

}
