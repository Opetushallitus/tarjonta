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
package fi.vm.sade.tarjonta.ui.model;

import fi.vm.sade.tarjonta.service.types.koodisto.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.koodisto.KoulutuskoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoulutuksenTila;
import java.util.HashSet;
import java.util.Set;

import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.enums.KoulutusasteType;
import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Model holding basic information data for Koulutus.
 *
 * @author mlyly
 * @author mholi
 * @author Jani Wilén
 */
public class KoulutusToisenAsteenPerustiedotViewModel extends KoulutusPerustiedotViewModel {

    private static final String NO_DATA_AVAILABLE = "Tietoa ei saatavilla";
    private Set<KoulutusasteTyyppi> koulutusasteet;
    private Set<KoulutuskoodiTyyppi> koulutuskoodit;
    private Set<KoulutusohjelmaModel> koulutusohjelmat;
    private KoulutusasteTyyppi koulutusasteTyyppi;
    private KoulutuskoodiTyyppi koulutuskoodiTyyppi;
    private KoulutuksenTila tila;

    public KoulutusToisenAsteenPerustiedotViewModel(DocumentStatus status) {
        super();
        clearModel(status);
    }

    /**
     * Initialize model with all default values.
     *
     * @param status of koulutus document
     */
    public void clearModel(final DocumentStatus status) {
        //OIDs
        setOrganisaatioOid(null);
        setOid(null); //KOMOTO OID
        setKoulutusmoduuliOid(null); //KOMO OID

        //used in control logic 
        setDocumentStatus(status);
        setUserFrienlyDocumentStatus(null);
        setKoulutusasteTyyppi(null);
        setKoulutuskoodiTyyppi(null);
        setKoulutusohjelma(null);

        //koodisto data
        setKoulutusala(null);
        setTutkinto(null);
        setTutkintonimike(null);
        setOpintojenLaajuusyksikko(null);
        setOpintojenLaajuus(null);
        setOpintoala(null);
        setKoulutuksenAlkamisPvm(new Date());
        setSuunniteltuKesto(null);
        setSuunniteltuKestoTyyppi(null);
        setOpetusmuoto(null);
        setKoulutuksenTyyppi(null);
        setOrganisaatioName(null);
        setOrganisaatioOid(null);
        setKoulutuslaji(null);

        setKoulutusasteet(new HashSet<KoulutusasteTyyppi>());
        setKoulutuskoodit(new HashSet<KoulutuskoodiTyyppi>());
        setKoulutusohjelmat(new HashSet<KoulutusohjelmaModel>());
        setOpetuskielet(new HashSet<String>(1)); //one required
        setOpetusmuoto(new HashSet<String>(1));//one required
        setAvainsanat(new HashSet<String>(0));//optional

        //Table data
        setKoulutusLinkit(new ArrayList<KoulutusLinkkiViewModel>(0)); //optional
        setYhteyshenkilot(new ArrayList<KoulutusYhteyshenkiloViewModel>(0)); //optional

        setOpintoala(NO_DATA_AVAILABLE); //Opintoala ei tiedossa
        setKoulutuksenTyyppi(NO_DATA_AVAILABLE); //Ei valintaa
        setTutkintonimike(NO_DATA_AVAILABLE); //Automaalari
        setKoulutusala(NO_DATA_AVAILABLE); //Tekniikan ja liikenteen ala
    }

    /**
     * True if data was loaded from database.
     *
     * @return Boolean
     */
    public boolean isLoaded() {
        return getOid() != null;
    }

    /**
     * @return the koulutusasteTyyppi
     */
    public KoulutusasteTyyppi getKoulutusasteTyyppi() {
        return koulutusasteTyyppi;
    }

    /**
     * @param koulutusasteTyyppi the koulutusasteTyyppi to set
     */
    public void setKoulutusasteTyyppi(KoulutusasteTyyppi koulutusasteTyyppi) {
        this.koulutusasteTyyppi = koulutusasteTyyppi;
    }

    public String getSelectedKoulutusasteKoodi() {
        if (getKoulutusasteTyyppi() != null && getKoulutusasteTyyppi().getKoulutusasteKoodi() != null) {
            return getKoulutusasteTyyppi().getKoulutusasteKoodi();
        }
        return null;
    }

    /**
     * @return the koulutuskoodiTyyppi
     */
    public KoulutuskoodiTyyppi getKoulutuskoodiTyyppi() {
        return koulutuskoodiTyyppi;
    }

    /**
     * @param koulutuskoodiTyyppi the koulutuskoodiTyyppi to set
     */
    public void setKoulutuskoodiTyyppi(KoulutuskoodiTyyppi koulutuskoodiTyyppi) {
        this.koulutuskoodiTyyppi = koulutuskoodiTyyppi;
    }

    /**
     * @return the koulutusasteet
     */
    public Set<KoulutusasteTyyppi> getKoulutusasteet() {
        return koulutusasteet;
    }

    /**
     * @param koulutusasteet the koulutusasteet to set
     */
    public void setKoulutusasteet(Set<KoulutusasteTyyppi> koulutusasteet) {
        this.koulutusasteet = koulutusasteet;
    }

    /**
     * @return the koulutuskoodit
     */
    public Set<KoulutuskoodiTyyppi> getKoulutuskoodit() {
        return koulutuskoodit;
    }

    /**
     * @param koulutuskoodit the koulutuskoodit to set
     */
    public void setKoulutuskoodit(Set<KoulutuskoodiTyyppi> koulutuskoodit) {
        this.koulutuskoodit = koulutuskoodit;
    }

    /**
     * @return the koulutusohjelmat
     */
    public Set<KoulutusohjelmaModel> getKoulutusohjelmat() {
        return koulutusohjelmat;
    }

    /**
     * @param koulutusohjelmat the koulutusohjelmat to set
     */
    public void setKoulutusohjelmat(Set<KoulutusohjelmaModel> koulutusohjelmat) {
        this.koulutusohjelmat = koulutusohjelmat;
    }

    public KoulutusasteType getSelectedKoulutusasteType() {
        if (getKoulutusasteTyyppi() == null) {
            throw new RuntimeException("Exception : invalid data - No koulutusaste selected!");
        }

        if (getKoulutusasteTyyppi().getKoulutusasteKoodi() == null) {
            throw new RuntimeException("Exception : invalid data - koulutusaste selected, but no numeric code!");
        }

        final KoulutusasteType koulutus = KoulutusasteType.getByKoulutusaste(getKoulutusasteTyyppi().getKoulutusasteKoodi());
        if (koulutus == null) {
            throw new RuntimeException("Selectable koulutusaste numeric code do not match to koodisto data. Value : " + getKoulutusasteTyyppi().getKoulutusasteKoodi());
        }

        return koulutus;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        KoulutusToisenAsteenPerustiedotViewModel other = (KoulutusToisenAsteenPerustiedotViewModel) obj;

        EqualsBuilder builder = new EqualsBuilder();
        builder.append(oid, other.oid);
        builder.append(koulutusmoduuliOid, other.koulutusmoduuliOid);
        builder.append(koulutusasteet, other.koulutusasteet);
        builder.append(koulutuskoodit, other.koulutuskoodit);
        builder.append(koulutusohjelmat, other.koulutusohjelmat);
        builder.append(koulutusasteTyyppi, other.koulutusasteTyyppi);
        builder.append(koulutuskoodiTyyppi, other.koulutuskoodiTyyppi);
        builder.append(documentStatus, other.documentStatus);
        builder.append(userFrienlyDocumentStatus, other.userFrienlyDocumentStatus);
        builder.append(organisaatioName, other.organisaatioName);
        builder.append(organisaatioOid, other.organisaatioOid);
        builder.append(koulutusohjelma, other.koulutusohjelma);
        builder.append(koulutuksenTyyppi, other.koulutuksenTyyppi);
        builder.append(koulutusala, other.koulutusala);
        builder.append(tutkinto, other.tutkinto);
        builder.append(tutkintonimike, other.tutkintonimike);
        builder.append(opintojenLaajuusyksikko, other.opintojenLaajuusyksikko);
        builder.append(opintojenLaajuus, other.opintojenLaajuus);
        builder.append(opintoala, other.opintoala);
        builder.append(koulutuksenAlkamisPvm, other.koulutuksenAlkamisPvm);
        builder.append(suunniteltuKesto, other.suunniteltuKesto);
        builder.append(suunniteltuKestoTyyppi, other.suunniteltuKestoTyyppi);
        builder.append(opetusmuoto, other.opetusmuoto);
        builder.append(koulutuslaji, other.koulutuslaji);
        builder.append(opetuskielet, other.opetuskielet);
        builder.append(avainsanat, other.avainsanat);
        builder.append(yhteyshenkilot, other.yhteyshenkilot);
        builder.append(koulutusLinkit, other.koulutusLinkit);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(oid)
                .append(koulutusmoduuliOid)
                .append(koulutusasteet)
                .append(koulutuskoodit)
                .append(koulutusohjelmat)
                .append(koulutusasteTyyppi)
                .append(koulutuskoodiTyyppi)
                .append(documentStatus)
                .append(userFrienlyDocumentStatus)
                .append(organisaatioName)
                .append(organisaatioOid)
                .append(koulutusohjelma)
                .append(koulutuksenTyyppi)
                .append(koulutusala)
                .append(tutkinto)
                .append(tutkintonimike)
                .append(opintojenLaajuusyksikko)
                .append(opintojenLaajuus)
                .append(opintoala)
                .append(koulutuksenAlkamisPvm)
                .append(suunniteltuKesto)
                .append(suunniteltuKestoTyyppi)
                .append(opetusmuoto)
                .append(koulutuslaji)
                .append(opetuskielet)
                .append(avainsanat)
                .append(yhteyshenkilot)
                .append(koulutusLinkit).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * @return the tila
     */
    public KoulutuksenTila getTila() {
        return tila;
    }

    /**
     * @param tila the tila to set
     */
    public void setTila(KoulutuksenTila tila) {
        this.tila = tila;
    }
}
