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
package fi.vm.sade.tarjonta.ui.model.koulutus.aste2;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.enums.KoulutusasteType;
import fi.vm.sade.tarjonta.ui.helper.conversion.Koulutus2asteConverter;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusLinkkiViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusYhteyshenkiloViewModel;
import fi.vm.sade.tarjonta.ui.model.SimpleHakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.MonikielinenTekstiModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Model holding basic information data for Koulutus.
 *
 * @author mlyly
 * @author mholi
 * @author Jani Wilén
 */
public class KoulutusToisenAsteenPerustiedotViewModel extends KoulutusPerustiedotViewModel {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusToisenAsteenPerustiedotViewModel.class);
    private static final long serialVersionUID = 4511930754933045032L;
    private List<KoulutusmoduuliKoosteTyyppi> komos;
    private List<KoulutuskoodiModel> koulutuskoodit;
    private List<KoulutusohjelmaModel> koulutusohjelmat;
    private TarjontaTila tila;
    private List<MonikielinenTekstiTyyppi.Teksti> toteutuksenNimet;
    private List<SimpleHakukohdeViewModel> koulutuksenHakukohteet;
    private MonikielinenTekstiModel koulutusohjelmaTavoitteet;
    private Long version;
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    /*
     * Link to opetussuunnitelma
     */
    private String opsuLinkki;
    /*
     * Yhteyshenkilo fields
     */
    private String yhtHenkKokoNimi;
    private String yhtHenkTitteli;
    private String yhtHenkEmail;
    private String yhtHenkPuhelin;
    private String YhtHenkiloOid;

    /*
     * cache maps
     */
    private Map<String, List<KoulutusmoduuliKoosteTyyppi>> cacheKomoTutkinto;
    private Map<Entry, KoulutusmoduuliKoosteTyyppi> cacheKomo;

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
        setOid(null); //KOMOTO OID
        setTila(TarjontaTila.LUONNOS);
        setKoulutusmoduuliOid(null); //KOMO OID
        setVersion(null);

        //used in control logic
        setDocumentStatus(status);
        setUserFrienlyDocumentStatus(null);

        setKoulutuskoodiModel(null);
        setKoulutusohjelmaModel(null);

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
        setKoulutuslaji(null);
        setPohjakoulutusvaatimus(null);
        setOpetusmuoto(null);
        setOpetuskieli(null);
        setKoulutuskoodit(new ArrayList<KoulutuskoodiModel>());
        setKoulutusohjelmat(new ArrayList<KoulutusohjelmaModel>());
        setOpsuLinkki(null); //optional
        setYhtHenkEmail(null); //optional
        setYhtHenkKokoNimi(null); //optional
        setYhtHenkPuhelin(null); //optional
        setYhtHenkTitteli(null); //optional
        setYhtHenkiloOid(null); //optional

        //Table data
        setPainotus(new ArrayList<KielikaannosViewModel>(0)); //optional
        setKoulutusLinkit(new ArrayList<KoulutusLinkkiViewModel>(0)); //optional
        setYhteyshenkilot(new ArrayList<KoulutusYhteyshenkiloViewModel>(0)); //optional
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
     * @return the koulutuskoodit
     */
    public List<KoulutuskoodiModel> getKoulutuskoodit() {
        return koulutuskoodit;
    }

    /**
     * @param koulutuskoodit the koulutuskoodit to set
     */
    public void setKoulutuskoodit(List<KoulutuskoodiModel> koulutuskoodit) {
        this.koulutuskoodit = koulutuskoodit;
    }

    /**
     * @return the koulutusohjelmat
     */
    public List<KoulutusohjelmaModel> getKoulutusohjelmat() {
        return koulutusohjelmat;
    }

    /**
     * @param koulutusohjelmat the koulutusohjelmat to set
     */
    public void setKoulutusohjelmat(List<KoulutusohjelmaModel> koulutusohjelmat) {
        this.koulutusohjelmat = koulutusohjelmat;
    }

    public KoulutusasteType getSelectedKoulutusasteType() {
        if (getKoulutuskoodiModel() == null) {
            throw new RuntimeException("Exception : invalid data - No koulutuskoodi selected!");
        }
        final KoodiModel koulutusasteKoodiModel = getKoulutuskoodiModel().getKoulutusaste();

        if (getKoulutuskoodiModel().getKoulutusaste() == null) {
            throw new RuntimeException("Exception : invalid data - No koulutusaste selected!");
        }

        if (koulutusasteKoodiModel.getKoodi() == null) {
            throw new RuntimeException("Exception : invalid data - koulutusaste selected, but no numeric code!");
        }

        final KoulutusasteType koulutus = KoulutusasteType.getByKoulutusaste(koulutusasteKoodiModel.getKoodi());
        if (koulutus == null) {
            throw new RuntimeException("Selectable koulutusaste numeric code do not match to koodisto data. Value : " + koulutusasteKoodiModel.getKoodi());
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
        builder.append(koulutusaste, other.koulutusaste);
        builder.append(koulutusmoduuliOid, other.koulutusmoduuliOid);
        builder.append(koulutuskoodit, other.koulutuskoodit);
        builder.append(koulutusohjelmat, other.koulutusohjelmat);

        builder.append(koulutuskoodiModel, other.koulutuskoodiModel);
        builder.append(documentStatus, other.documentStatus);
        builder.append(userFrienlyDocumentStatus, other.userFrienlyDocumentStatus);
        builder.append(koulutusohjelmaModel, other.koulutusohjelmaModel);
        builder.append(koulutuksenTyyppi, other.koulutuksenTyyppi);
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
        builder.append(koulutuslaji, other.koulutuslaji);
        builder.append(pohjakoulutusvaatimus, other.pohjakoulutusvaatimus);
        builder.append(opetuskieli, other.opetuskieli);
        builder.append(yhteyshenkilot, other.yhteyshenkilot);
        builder.append(koulutusLinkit, other.koulutusLinkit);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(oid)
                .append(koulutusaste)
                .append(koulutusmoduuliOid)
                .append(koulutuskoodit)
                .append(koulutusohjelmat)
                .append(koulutuskoodiModel)
                .append(documentStatus)
                .append(userFrienlyDocumentStatus)
                .append(koulutusohjelmaModel)
                .append(koulutuksenTyyppi)
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
                .append(koulutuslaji)
                .append(pohjakoulutusvaatimus)
                .append(opetuskieli)
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
    public TarjontaTila getTila() {
        return tila;
    }

    /**
     * @param tila the tila to set
     */
    public void setTila(TarjontaTila tila) {
        this.tila = tila;
    }

    /**
     * @return the toteutuksenNimet
     */
    public List<MonikielinenTekstiTyyppi.Teksti> getToteutuksenNimet() {
        return toteutuksenNimet;
    }

    /**
     * @param toteutuksenNimet the toteutuksenNimet to set
     */
    public void setToteutuksenNimet(List<MonikielinenTekstiTyyppi.Teksti> toteutuksenNimet) {
        this.toteutuksenNimet = toteutuksenNimet;
    }

    /**
     * @return the komos
     */
    public List<KoulutusmoduuliKoosteTyyppi> getKomos() {
        return komos;
    }

    /**
     * @param komos the komos to set
     */
    public void setKomos(List<KoulutusmoduuliKoosteTyyppi> komos) {
        this.komos = komos;
    }

    public void createCacheKomos() {

        setCacheKomoTutkinto(Koulutus2asteConverter.komoCacheMapByKoulutuskoodi(komos));
        setCacheKomo(Koulutus2asteConverter.full2asteKomoCacheMap(komos));
    }

    /**
     * @return the cacheKomoTutkinto
     */
    public KoulutusmoduuliKoosteTyyppi getQuickKomo(final String koulutuskoodiUri, final String koulutusohjelmaUri) {
        return cacheKomo.get(new AbstractMap.SimpleEntry<String, String>(koulutuskoodiUri, koulutusohjelmaUri));
    }

    public List<KoulutusmoduuliKoosteTyyppi> getQuickKomosByKoulutuskoodiUri(final String koulutuskoodiUri) {
        return cacheKomoTutkinto.get(koulutuskoodiUri);
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
    public Map<Entry, KoulutusmoduuliKoosteTyyppi> getCacheKomo() {
        return cacheKomo;
    }

    /**
     * @param cacheKomo the cacheKomo to set
     */
    public void setCacheKomo(Map<Entry, KoulutusmoduuliKoosteTyyppi> cacheKomo) {
        this.cacheKomo = cacheKomo;
    }

    public List<SimpleHakukohdeViewModel> getKoulutuksenHakukohteet() {
        if (koulutuksenHakukohteet == null) {
            koulutuksenHakukohteet = new ArrayList<SimpleHakukohdeViewModel>();
        }
        return koulutuksenHakukohteet;
    }

    public String getOpsuLinkki() {
        return opsuLinkki;
    }

    public void setOpsuLinkki(String linkki) {
        this.opsuLinkki = linkki;
    }

    public String getYhtHenkKokoNimi() {
        return yhtHenkKokoNimi;
    }

    public void setYhtHenkKokoNimi(String yhtHenkKokoNimi) {
        this.yhtHenkKokoNimi = yhtHenkKokoNimi;
    }

    public String getYhtHenkTitteli() {
        return yhtHenkTitteli;
    }

    public void setYhtHenkTitteli(String yhtHenkTitteli) {
        this.yhtHenkTitteli = yhtHenkTitteli;
    }

    public String getYhtHenkEmail() {
        return yhtHenkEmail;
    }

    public void setYhtHenkEmail(String yhtHenkEmail) {
        this.yhtHenkEmail = yhtHenkEmail;
    }

    public String getYhtHenkPuhelin() {
        return yhtHenkPuhelin;
    }

    public void setYhtHenkPuhelin(String yhtHenkPuhelin) {
        this.yhtHenkPuhelin = yhtHenkPuhelin;
    }

    public String getYhtHenkiloOid() {
        return YhtHenkiloOid;
    }

    public void setYhtHenkiloOid(String oidHenkilo) {
        this.YhtHenkiloOid = oidHenkilo;
    }

    public MonikielinenTekstiModel getKoulutusohjelmaTavoitteet() {
        return koulutusohjelmaTavoitteet;
    }

    public void setKoulutusohjelmaTavoitteet(MonikielinenTekstiModel koulutusohjelmaTavoitteet) {
        this.koulutusohjelmaTavoitteet = koulutusohjelmaTavoitteet;
    }
}
