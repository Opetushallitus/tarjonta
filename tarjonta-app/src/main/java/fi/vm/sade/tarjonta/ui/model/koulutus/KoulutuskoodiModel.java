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
package fi.vm.sade.tarjonta.ui.model.koulutus;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Jani Wilén
 */
public class KoulutuskoodiModel extends MonikielinenTekstiModel {

    private Set<KoulutusohjelmaModel> koulutusohjelmaModels;
    private KoodiModel koulutusaste;
    private KoodiModel koulutusala;
    private KoodiModel opintojenLaajuusyksikko;
    private KoodiModel opintojenLaajuus;
    private KoodiModel opintoala;
    private MonikielinenTekstiModel koulutuksenRakenne;
    private MonikielinenTekstiModel tavoitteet;
    private MonikielinenTekstiModel jatkoopintomahdollisuudet;

    public KoulutuskoodiModel() {
    }

    /**
     * @return the koulutusaste
     */
    public KoodiModel getKoulutusaste() {
        return koulutusaste;
    }

    /**
     * @param koulutusaste the koulutusaste to set
     */
    public void setKoulutusaste(KoodiModel koulutusaste) {
        this.koulutusaste = koulutusaste;
    }

    /**
     * @return the koulutusala
     */
    public KoodiModel getKoulutusala() {
        return koulutusala;
    }

    /**
     * @param koulutusala the koulutusala to set
     */
    public void setKoulutusala(KoodiModel koulutusala) {
        this.koulutusala = koulutusala;
    }

    /**
     * @return the opintojenLaajuusyksikko
     */
    public KoodiModel getOpintojenLaajuusyksikko() {
        return opintojenLaajuusyksikko;
    }

    /**
     * @param opintojenLaajuusyksikko the opintojenLaajuusyksikko to set
     */
    public void setOpintojenLaajuusyksikko(KoodiModel opintojenLaajuusyksikko) {
        this.opintojenLaajuusyksikko = opintojenLaajuusyksikko;
    }

    /**
     * @return the opintojenLaajuus
     */
    public KoodiModel getOpintojenLaajuus() {
        return opintojenLaajuus;
    }

    /**
     * @param opintojenLaajuus the opintojenLaajuus to set
     */
    public void setOpintojenLaajuus(KoodiModel opintojenLaajuus) {
        this.opintojenLaajuus = opintojenLaajuus;
    }

    /**
     * @return the opintoala
     */
    public KoodiModel getOpintoala() {
        return opintoala;
    }

    /**
     * @param opintoala the opintoala to set
     */
    public void setOpintoala(KoodiModel opintoala) {
        this.opintoala = opintoala;
    }

    /**
     * @return the koulutuksenRakenne
     */
    public MonikielinenTekstiModel getKoulutuksenRakenne() {
        return koulutuksenRakenne;
    }

    /**
     * @param koulutuksenRakenne the koulutuksenRakenne to set
     */
    public void setKoulutuksenRakenne(MonikielinenTekstiModel koulutuksenRakenne) {
        this.koulutuksenRakenne = koulutuksenRakenne;
    }


    /**
     * @return the koulutusohjelmaModels
     */
    public Set<KoulutusohjelmaModel> getKoulutusohjelmaModels() {
        if (koulutusohjelmaModels == null) {
            koulutusohjelmaModels = new HashSet<KoulutusohjelmaModel>();
        }
        return koulutusohjelmaModels;
    }

    /**
     * @param koulutusohjelmaModels the koulutusohjelmaModels to set
     */
    public void setKoulutusohjelmaModels(Set<KoulutusohjelmaModel> koulutusohjelmaModels) {
        this.koulutusohjelmaModels = koulutusohjelmaModels;
    }

    /**
     * @return the tavoitteet
     */
    public MonikielinenTekstiModel getTavoitteet() {
        return tavoitteet;
    }

    /**
     * @param tavoitteet the tavoitteet to set
     */
    public void setTavoitteet(MonikielinenTekstiModel tavoitteet) {
        this.tavoitteet = tavoitteet;
    }

    /**
     * @return the jatkoopintomahdollisuudet
     */
    public MonikielinenTekstiModel getJatkoopintomahdollisuudet() {
        return jatkoopintomahdollisuudet;
    }

    /**
     * @param jatkoopintomahdollisuudet the jatkoopintomahdollisuudet to set
     */
    public void setJatkoopintomahdollisuudet(MonikielinenTekstiModel jatkoopintomahdollisuudet) {
        this.jatkoopintomahdollisuudet = jatkoopintomahdollisuudet;
    }
}
