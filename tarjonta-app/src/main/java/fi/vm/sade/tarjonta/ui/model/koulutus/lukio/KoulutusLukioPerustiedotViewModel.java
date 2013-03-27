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
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusLukioConverter;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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
    private Set<LukiolinjaModel> lukiolinjas;
    protected LukiolinjaModel lukiolinja;
    /*
     * Other user selected form input data
     */
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
    /*
     * cache maps
     */
    public Map<String, List<KoulutusmoduuliKoosteTyyppi>> cacheKomoTutkinto;
    public Map<Map.Entry, KoulutusmoduuliKoosteTyyppi> cacheKomo;

    public KoulutusLukioPerustiedotViewModel() {
        super();
        clearModel();
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
        if (yhteyshenkilo == null) {
            yhteyshenkilo = new YhteyshenkiloModel();
        }

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
    public void clearModel() {

        /*
         * Other save&load logic data
         */
        setTila(TarjontaTila.LUONNOS);

        /*
         * OIDs
         */
        setKomotoOid(null); //KOMOTO OID
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
        setLukiolinja(null);

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

        /*
         * Other from info
         */
        setKoulutuskoodi(null);
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
        builder.append(komotoOid, other.komotoOid);
        builder.append(koulutusaste, other.koulutusaste);
        builder.append(koulutusmoduuliOid, other.koulutusmoduuliOid);
        builder.append(getKoulutuskoodiModel(), other.getKoulutuskoodiModel());
        builder.append(organisaatioName, other.organisaatioName);
        builder.append(organisaatioOid, other.organisaatioOid);
        builder.append(getLukiolinja(), other.getLukiolinja());
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
        return new HashCodeBuilder().append(komotoOid)
                .append(koulutusaste)
                .append(koulutusmoduuliOid)
                .append(getKoulutuskoodiModel())
                .append(organisaatioName)
                .append(organisaatioOid)
                .append(getLukiolinja())
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
     * @return the lukiolinjas
     */
    public Set<LukiolinjaModel> getLukiolinjas() {
        if (lukiolinjas == null) {
            lukiolinjas = new HashSet<LukiolinjaModel>();
        }

        return lukiolinjas;
    }

    /**
     * @param lukiolinjas the lukiolinjas to set
     */
    public void setLukiolinjas(Set<LukiolinjaModel> lukiolinjas) {
        this.lukiolinjas = lukiolinjas;
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

    public void createCacheKomos() {
        setCacheKomoTutkinto(KoulutusLukioConverter.komoCacheMapByKoulutuskoodi(komos));
        setCacheKomo(KoulutusLukioConverter.fullLukioKomoCacheMap(komos));
    }

    /**
     * @return the cacheKomoTutkinto
     */
    public KoulutusmoduuliKoosteTyyppi getQuickKomo(final String koulutuskoodiUri, final String koulutusohjelmaUri) {
        Map.Entry<String, String> e = new AbstractMap.SimpleEntry<String, String>(koulutuskoodiUri, koulutusohjelmaUri);
        return getCacheKomo().get(e);
    }

    public List<KoulutusmoduuliKoosteTyyppi> getQuickKomosByKoulutuskoodiUri(final String koulutuskoodiUri) {
        return getCacheKomoTutkinto().get(koulutuskoodiUri);
    }

    /**
     * @return the cacheKomoTutkinto
     */
    public Map<String, List<KoulutusmoduuliKoosteTyyppi>> getCacheKomoTutkinto() {
        return cacheKomoTutkinto;
    }

    /**
     * @param cacheKomoTutkinto the cacheKomoTutkinto to set
     */
    public void setCacheKomoTutkinto(Map<String, List<KoulutusmoduuliKoosteTyyppi>> cacheKomoTutkinto) {
        this.cacheKomoTutkinto = cacheKomoTutkinto;
    }

    /**
     * @return the cacheKomo
     */
    public Map<Map.Entry, KoulutusmoduuliKoosteTyyppi> getCacheKomo() {
        return cacheKomo;
    }

    /**
     * @param cacheKomo the cacheKomo to set
     */
    public void setCacheKomo(Map<Map.Entry, KoulutusmoduuliKoosteTyyppi> cacheKomo) {
        this.cacheKomo = cacheKomo;
    }

    /**
     * @return the lukiolinja
     */
    public LukiolinjaModel getLukiolinja() {
        return lukiolinja;
    }

    /**
     * @param lukiolinja the lukiolinja to set
     */
    public void setLukiolinja(LukiolinjaModel lukiolinja) {
        this.lukiolinja = lukiolinja;
    }
}
