package fi.vm.sade.tarjonta.ui.hakuera;

import fi.vm.sade.generic.ui.component.MultiLingualTextField;
import fi.vm.sade.koodisto.model.dto.Kieli;
import fi.vm.sade.koodisto.model.dto.KoodiDTO;
import fi.vm.sade.koodisto.widget.KoodistoComponent;

public class HakueraMlTextField extends MultiLingualTextField {
    
    HakueraEditForm hakueraForm;
    
    public HakueraMlTextField(HakueraEditForm hakueraForm) {
        this.hakueraForm = hakueraForm;
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
    public boolean nimiMatchesKoodistoFields() {
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
                    nimiPart += curKoodi.getKoodiMetadataForLanguage(lang).getNimi();
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
    String constructNimiFromParts(String hakutyyppiKoodiVal, String hakukausiKoodiVal, String kohdejoukkoKoodiVal) {
        String nimi = "";
        nimi += hakutyyppiKoodiVal;
        nimi += ((hakukausiKoodiVal.length() > 0) && (hakutyyppiKoodiVal.length() > 0))  ? ", "  : "";
        nimi += hakukausiKoodiVal;
        nimi += (nimi.length() > 0) ? ", " : "";
        nimi += kohdejoukkoKoodiVal;
        return nimi;
    }
}
