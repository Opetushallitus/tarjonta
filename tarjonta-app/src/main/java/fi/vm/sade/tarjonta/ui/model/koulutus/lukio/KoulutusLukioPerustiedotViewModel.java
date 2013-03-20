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
package fi.vm.sade.tarjonta.ui.model.koulutus.lukio;

import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.MonikielinenTekstiModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author Jani Wilén
 */
public class KoulutusLukioPerustiedotViewModel extends KoulutusRelaatioModel {

    private static final long serialVersionUID = 604308326420805144L;
    /*
     * Data for comboxes (komo + lukiolinja) 
     */
    private List<KoulutusmoduuliKoosteTyyppi> komos;
    private Set<KoulutuskoodiModel> koulutuskoodis;
    private Set<LukiolajiModel> lukiolajis;
    /*
     * Other user selected form input data
     */
    protected LukiolajiModel lukiolaji;
    protected Date koulutuksenAlkamisPvm;
    protected String suunniteltuKesto;
    protected String suunniteltuKestoTyyppi;
    protected String opetuskieli;
    protected Set<String> opetusmuoto;
    /*
     * Link to opetussuunnitelma
     */
    protected String opsuLinkki;
    protected YhteyshenkiloModel yhteyshenkilo;
    /*
     * the organisaatio oids of the organisaatio tree of the tarjoaja organisaatio of this koulutus.
     * Is used when fetching potential yhteyshenkilos for the current koulutus.
     */
    private List<String> organisaatioOidTree;

    public KoulutusLukioPerustiedotViewModel(DocumentStatus status) {
        super();
        clearModel(status);
    }

    /**
     * @return the koulutuksenAlkamisPvm
     */
    public Date getKoulutuksenAlkamisPvm() {
        return koulutuksenAlkamisPvm;
    }

    /**
     * @param koulutuksenAlkamisPvm the koulutuksenAlkamisPvm to set
     */
    public void setKoulutuksenAlkamisPvm(Date koulutuksenAlkamisPvm) {
        this.koulutuksenAlkamisPvm = koulutuksenAlkamisPvm;
    }

    /**
     * @return the suunniteltuKesto
     */
    public String getSuunniteltuKesto() {
        return suunniteltuKesto;
    }

    /**
     * @param suunniteltuKesto the suunniteltuKesto to set
     */
    public void setSuunniteltuKesto(String suunniteltuKesto) {
        this.suunniteltuKesto = suunniteltuKesto;
    }

    /**
     * @return the suunniteltuKestoTyyppi
     */
    public String getSuunniteltuKestoTyyppi() {
        return suunniteltuKestoTyyppi;
    }

    /**
     * @param suunniteltuKestoTyyppi the suunniteltuKestoTyyppi to set
     */
    public void setSuunniteltuKestoTyyppi(String suunniteltuKestoTyyppi) {
        this.suunniteltuKestoTyyppi = suunniteltuKestoTyyppi;
    }

    /**
     * @return the opetusmuoto
     */
    public Set<String> getOpetusmuoto() {
        return opetusmuoto;
    }

    /**
     * @param opetusmuoto the opetusmuoto to set
     */
    public void setOpetusmuoto(Set<String> opetusmuoto) {
        this.opetusmuoto = opetusmuoto;
    }

    /**
     * @return the opetuskieli
     */
    public String getOpetuskieli() {
        return opetuskieli;
    }

    /**
     * @param opetuskieli the opetuskieli to set
     */
    public void setOpetuskieli(String opetuskieli) {
        this.opetuskieli = opetuskieli;
    }

    /**
     * @return the opsuLinkki
     */
    public String getOpsuLinkki() {
        return opsuLinkki;
    }

    /**
     * @param opsuLinkki the opsuLinkki to set
     */
    public void setOpsuLinkki(String opsuLinkki) {
        this.opsuLinkki = opsuLinkki;
    }

    /**
     * @return the yhteyshenkilo
     */
    public YhteyshenkiloModel getYhteyshenkilo() {
        return yhteyshenkilo;
    }

    /**
     * @param yhteyshenkilo the yhteyshenkilo to set
     */
    public void setYhteyshenkilo(YhteyshenkiloModel yhteyshenkilo) {
        this.yhteyshenkilo = yhteyshenkilo;
    }

    /**
     * Initialize model with all default values.
     *
     * @param status of koulutus document
     */
    public void clearModel(final DocumentStatus status) {
        /*
         * Other save&load logic data
         */
        setDocumentStatus(status);
        setTila(TarjontaTila.LUONNOS);
        setUserFrienlyDocumentStatus(null);

        /*
         * OIDs
         */
        setOid(null); //KOMOTO OID
        setKoulutusmoduuliOid(null); //KOMO OID

        /*
         * Organisation data
         */
        setOrganisaatioName(null);
        setOrganisaatioOid(null);

        /*
         *  Form selection logic
         */
        setKoulutuskoodiModel(null);
        setLukiolaji(null);

        /*
         * Koodisto service koodi data
         */
        setKoulutusaste(null);
        setKoulutusala(null);
        setTutkintonimike(null);
        setOpintojenLaajuusyksikko(null);
        setOpintojenLaajuus(null);
        setOpintoala(null);
        setPohjakoulutusvaatimus(null);
        setKoulutuslaji(null);

        /*
         * Tarjonta text data
         */
        setTavoitteet(null);
        setKoulutuksenRakenne(null);
        setJatkoopintomahdollisuudet(null);

        /*
         * Lukio form data
         */
        setKoulutuksenAlkamisPvm(new Date());
        setSuunniteltuKesto(null);
        setSuunniteltuKestoTyyppi(null);
        setOpetuskieli(null);
        setOpetusmuoto(new HashSet<String>());
        setOpsuLinkki(null);
        setYhteyshenkilo(new YhteyshenkiloModel());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        KoulutusLukioPerustiedotViewModel other = (KoulutusLukioPerustiedotViewModel) obj;

        EqualsBuilder builder = new EqualsBuilder();
        builder.append(oid, other.oid);
        builder.append(koulutusaste, other.koulutusaste);
        builder.append(koulutusmoduuliOid, other.koulutusmoduuliOid);
        builder.append(getKoulutuskoodiModel(), other.getKoulutuskoodiModel());
        builder.append(documentStatus, other.documentStatus);
        builder.append(userFrienlyDocumentStatus, other.userFrienlyDocumentStatus);
        builder.append(organisaatioName, other.organisaatioName);
        builder.append(organisaatioOid, other.organisaatioOid);
        builder.append(getLukiolaji(), other.getLukiolaji());
        builder.append(getKoulutusala(), other.getKoulutusala());
        builder.append(getTutkinto(), other.getTutkinto());
        builder.append(getTutkintonimike(), other.getTutkintonimike());
        builder.append(getOpintojenLaajuusyksikko(), other.getOpintojenLaajuusyksikko());
        builder.append(getOpintojenLaajuus(), other.getOpintojenLaajuus());
        builder.append(getOpintoala(), other.getOpintoala());
        builder.append(koulutuksenAlkamisPvm, other.koulutuksenAlkamisPvm);
        builder.append(suunniteltuKesto, other.suunniteltuKesto);
        builder.append(suunniteltuKestoTyyppi, other.suunniteltuKestoTyyppi);
        builder.append(opetusmuoto, other.opetusmuoto);
        builder.append(getKoulutuslaji(), other.getKoulutuslaji());
        builder.append(getPohjakoulutusvaatimus(), other.getPohjakoulutusvaatimus());
        builder.append(opetuskieli, other.opetuskieli);

        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(oid)
                .append(koulutusaste)
                .append(koulutusmoduuliOid)
                .append(getKoulutuskoodiModel())
                .append(documentStatus)
                .append(userFrienlyDocumentStatus)
                .append(organisaatioName)
                .append(organisaatioOid)
                .append(getLukiolaji())
                .append(getKoulutusala())
                .append(getTutkinto())
                .append(getTutkintonimike())
                .append(getOpintojenLaajuusyksikko())
                .append(getOpintojenLaajuus())
                .append(getOpintoala())
                .append(koulutuksenAlkamisPvm)
                .append(suunniteltuKesto)
                .append(suunniteltuKestoTyyppi)
                .append(opetusmuoto)
                .append(getKoulutuslaji())
                .append(getPohjakoulutusvaatimus())
                .append(opetuskieli)
                .append(yhteyshenkilo)
                .toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * @return the lukiolaji
     */
    public LukiolajiModel getLukiolaji() {
        return lukiolaji;
    }

    /**
     * @param lukiolaji the lukiolaji to set
     */
    public void setLukiolaji(LukiolajiModel lukiolaji) {
        this.lukiolaji = lukiolaji;
    }

    /**
     * @return the komos
     */
    public List<KoulutusmoduuliKoosteTyyppi> getKomos() {
        if (komos == null) {
            komos = new ArrayList<KoulutusmoduuliKoosteTyyppi>();
        }

        return komos;
    }

    /**
     * @param komos the komos to set
     */
    public void setKomos(List<KoulutusmoduuliKoosteTyyppi> komos) {
        this.komos = komos;
    }

    /**
     * @return the koulutuskoodis
     */
    public Set<KoulutuskoodiModel> getKoulutuskoodis() {
        if (koulutuskoodis == null) {
            koulutuskoodis = new HashSet<KoulutuskoodiModel>();
        }
        return koulutuskoodis;
    }

    /**
     * @param koulutuskoodis the koulutuskoodis to set
     */
    public void setKoulutuskoodis(Set<KoulutuskoodiModel> koulutuskoodis) {
        this.koulutuskoodis = koulutuskoodis;
    }

    /**
     * @return the lukiolajis
     */
    public Set<LukiolajiModel> getLukiolajis() {
        if (lukiolajis == null) {
            lukiolajis = new HashSet<LukiolajiModel>();
        }

        return lukiolajis;
    }

    /**
     * @param lukiolajis the lukiolajis to set
     */
    public void setLukiolajis(Set<LukiolajiModel> lukiolajis) {
        this.lukiolajis = lukiolajis;
    }

    /**
     * @return the organisaatioOidTree
     */
    public List<String> getOrganisaatioOidTree() {
        return organisaatioOidTree;
    }

    /**
     * @param organisaatioOidTree the organisaatioOidTree to set
     */
    public void setOrganisaatioOidTree(List<String> organisaatioOidTree) {
        this.organisaatioOidTree = organisaatioOidTree;
    }
}
