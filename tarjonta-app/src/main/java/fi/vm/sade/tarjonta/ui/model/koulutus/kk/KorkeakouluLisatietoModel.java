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
package fi.vm.sade.tarjonta.ui.model.koulutus.kk;

import fi.vm.sade.tarjonta.ui.model.BaseUIViewModel;
import fi.vm.sade.tarjonta.ui.model.TarjontaKuvaModel;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Single language additional information for studies.
 *
 * @author Jani
 */
public class KorkeakouluLisatietoModel extends BaseUIViewModel {

    private static final long serialVersionUID = -6976977670649353115L;
    private String koulutusohjelmanAmmatillisetTavoitteet;
    private String paaaineenValinta;
    private String koulutuksenSisalto;
    private String koulutuksenRakenne;
    private String lisatietoaOpetuskielesta;
    private String lopputyonKuvaus;
    private String opintojenMaksullisuus;
    private String sijoittautuminenTyoelamaan;
    private String patevyys;
    private String kansainvalistyminen;
    private String yhteistyoMuidenToimijoidenKanssa;
    private String tutkimuksenPainopisteet;
    private String jatkoOpintomahdollisuudet;
    private String kuvausKoulutuksenRakenteesta;
    private TarjontaKuvaModel kuvaKoulutuksenRakenteesta; //kuvaKoulutuksenRakenteesta

    public KorkeakouluLisatietoModel() {
    }

    /**
     * @return the koulutusohjelmanAmmatillisetTavoitteet
     */
    public String getKoulutusohjelmanAmmatillisetTavoitteet() {
        return koulutusohjelmanAmmatillisetTavoitteet;
    }

    /**
     * @param koulutusohjelmanAmmatillisetTavoitteet the
     * koulutusohjelmanAmmatillisetTavoitteet to set
     */
    public void setKoulutusohjelmanAmmatillisetTavoitteet(String koulutusohjelmanAmmatillisetTavoitteet) {
        this.koulutusohjelmanAmmatillisetTavoitteet = koulutusohjelmanAmmatillisetTavoitteet;
    }

    /**
     * @return the paaaineenValinta
     */
    public String getPaaaineenValinta() {
        return paaaineenValinta;
    }

    /**
     * @param paaaineenValinta the paaaineenValinta to set
     */
    public void setPaaaineenValinta(String paaaineenValinta) {
        this.paaaineenValinta = paaaineenValinta;
    }

    /**
     * @return the koulutuksenSisalto
     */
    public String getKoulutuksenSisalto() {
        return koulutuksenSisalto;
    }

    /**
     * @param koulutuksenSisalto the koulutuksenSisalto to set
     */
    public void setKoulutuksenSisalto(String koulutuksenSisalto) {
        this.koulutuksenSisalto = koulutuksenSisalto;
    }

    /**
     * @return the koulutuksenRakenne
     */
    public String getKoulutuksenRakenne() {
        return koulutuksenRakenne;
    }

    /**
     * @param koulutuksenRakenne the koulutuksenRakenne to set
     */
    public void setKoulutuksenRakenne(String koulutuksenRakenne) {
        this.koulutuksenRakenne = koulutuksenRakenne;
    }

    /**
     * @return the lisatietoaOpetuskielesta
     */
    public String getLisatietoaOpetuskielesta() {
        return lisatietoaOpetuskielesta;
    }

    /**
     * @param lisatietoaOpetuskielesta the lisatietoaOpetuskielesta to set
     */
    public void setLisatietoaOpetuskielesta(String lisatietoaOpetuskielesta) {
        this.lisatietoaOpetuskielesta = lisatietoaOpetuskielesta;
    }

    /**
     * @return the lopputyonKuvaus
     */
    public String getLopputyonKuvaus() {
        return lopputyonKuvaus;
    }

    /**
     * @param lopputyonKuvaus the lopputyonKuvaus to set
     */
    public void setLopputyonKuvaus(String lopputyonKuvaus) {
        this.lopputyonKuvaus = lopputyonKuvaus;
    }

    /**
     * @return the opintojenMaksullisuus
     */
    public String getOpintojenMaksullisuus() {
        return opintojenMaksullisuus;
    }

    /**
     * @param opintojenMaksullisuus the opintojenMaksullisuus to set
     */
    public void setOpintojenMaksullisuus(String opintojenMaksullisuus) {
        this.opintojenMaksullisuus = opintojenMaksullisuus;
    }

    /**
     * @return the sijoittautuminenTyoelamaan
     */
    public String getSijoittautuminenTyoelamaan() {
        return sijoittautuminenTyoelamaan;
    }

    /**
     * @param sijoittautuminenTyoelamaan the sijoittautuminenTyoelamaan to set
     */
    public void setSijoittautuminenTyoelamaan(String sijoittautuminenTyoelamaan) {
        this.sijoittautuminenTyoelamaan = sijoittautuminenTyoelamaan;
    }

    /**
     * @return the patevyys
     */
    public String getPatevyys() {
        return patevyys;
    }

    /**
     * @param patevyys the patevyys to set
     */
    public void setPatevyys(String patevyys) {
        this.patevyys = patevyys;
    }

    /**
     * @return the kansainvalistyminen
     */
    public String getKansainvalistyminen() {
        return kansainvalistyminen;
    }

    /**
     * @param kansainvalistyminen the kansainvalistyminen to set
     */
    public void setKansainvalistyminen(String kansainvalistyminen) {
        this.kansainvalistyminen = kansainvalistyminen;
    }

    /**
     * @return the yhteistyoMuidenToimijoidenKanssa
     */
    public String getYhteistyoMuidenToimijoidenKanssa() {
        return yhteistyoMuidenToimijoidenKanssa;
    }

    /**
     * @param yhteistyoMuidenToimijoidenKanssa the
     * yhteistyoMuidenToimijoidenKanssa to set
     */
    public void setYhteistyoMuidenToimijoidenKanssa(String yhteistyoMuidenToimijoidenKanssa) {
        this.yhteistyoMuidenToimijoidenKanssa = yhteistyoMuidenToimijoidenKanssa;
    }

    /**
     * @return the tutkimuksenPainopisteet
     */
    public String getTutkimuksenPainopisteet() {
        return tutkimuksenPainopisteet;
    }

    /**
     * @param tutkimuksenPainopisteet the tutkimuksenPainopisteet to set
     */
    public void setTutkimuksenPainopisteet(String tutkimuksenPainopisteet) {
        this.tutkimuksenPainopisteet = tutkimuksenPainopisteet;
    }

    /**
     * @return the jatkoOpintomahdollisuudet
     */
    public String getJatkoOpintomahdollisuudet() {
        return jatkoOpintomahdollisuudet;
    }

    /**
     * @param jatkoOpintomahdollisuudet the jatkoOpintomahdollisuudet to set
     */
    public void setJatkoOpintomahdollisuudet(String jatkoOpintomahdollisuudet) {
        this.jatkoOpintomahdollisuudet = jatkoOpintomahdollisuudet;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        KorkeakouluLisatietoModel other = (KorkeakouluLisatietoModel) obj;

        EqualsBuilder builder = new EqualsBuilder();

        builder.append(koulutusohjelmanAmmatillisetTavoitteet, other.koulutusohjelmanAmmatillisetTavoitteet)
                .append(paaaineenValinta, other.paaaineenValinta)
                .append(koulutuksenSisalto, other.koulutuksenSisalto)
                .append(koulutuksenRakenne, other.koulutuksenRakenne)
                .append(getKuvausKoulutuksenRakenteesta(), other.getKuvausKoulutuksenRakenteesta())
                .append(lisatietoaOpetuskielesta, other.lisatietoaOpetuskielesta)
                .append(lopputyonKuvaus, other.lopputyonKuvaus)
                .append(opintojenMaksullisuus, other.opintojenMaksullisuus)
                .append(sijoittautuminenTyoelamaan, other.sijoittautuminenTyoelamaan)
                .append(patevyys, other.patevyys)
                .append(kansainvalistyminen, other.kansainvalistyminen)
                .append(yhteistyoMuidenToimijoidenKanssa, other.yhteistyoMuidenToimijoidenKanssa)
                .append(tutkimuksenPainopisteet, other.tutkimuksenPainopisteet)
                .append(jatkoOpintomahdollisuudet, other.jatkoOpintomahdollisuudet)
                .append(kuvausKoulutuksenRakenteesta, other.kuvausKoulutuksenRakenteesta);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this)
                .append(koulutusohjelmanAmmatillisetTavoitteet)
                .append(paaaineenValinta)
                .append(koulutuksenSisalto)
                .append(koulutuksenRakenne)
                .append(getKuvausKoulutuksenRakenteesta())
                .append(lisatietoaOpetuskielesta)
                .append(lopputyonKuvaus)
                .append(opintojenMaksullisuus)
                .append(sijoittautuminenTyoelamaan)
                .append(patevyys)
                .append(kansainvalistyminen)
                .append(yhteistyoMuidenToimijoidenKanssa)
                .append(tutkimuksenPainopisteet)
                .append(jatkoOpintomahdollisuudet)
                .append(kuvausKoulutuksenRakenteesta)
                .toHashCode();
    }

    /**
     * @return the kuvausKoulutuksenRakenteesta
     */
    public String getKuvausKoulutuksenRakenteesta() {
        return kuvausKoulutuksenRakenteesta;
    }

    /**
     * @param kuvausKoulutuksenRakenteesta the kuvausKoulutuksenRakenteesta to
     * set
     */
    public void setKuvausKoulutuksenRakenteesta(String kuvausKoulutuksenRakenteesta) {
        this.kuvausKoulutuksenRakenteesta = kuvausKoulutuksenRakenteesta;
    }

    /**
     * @return the kuvaKoulutuksenRakenteesta
     */
    public TarjontaKuvaModel getKuvaKoulutuksenRakenteesta() {
        if (kuvaKoulutuksenRakenteesta == null) {
            kuvaKoulutuksenRakenteesta = new TarjontaKuvaModel();
        }
        return kuvaKoulutuksenRakenteesta;
    }

    /**
     * @param kuvaKoulutuksenRakenteesta the kuvaKoulutuksenRakenteesta to set
     */
    public void setKuvaKoulutuksenRakenteesta(TarjontaKuvaModel kuvaKoulutuksenRakenteesta) {
        this.kuvaKoulutuksenRakenteesta = kuvaKoulutuksenRakenteesta;
    }
}
