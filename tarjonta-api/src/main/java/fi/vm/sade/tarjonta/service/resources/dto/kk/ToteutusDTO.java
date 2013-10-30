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
package fi.vm.sade.tarjonta.service.resources.dto.kk;

import fi.vm.sade.tarjonta.service.resources.dto.BaseRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.TekstiV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 *
 * @author Jani Wilén
 */
public abstract class ToteutusDTO extends BaseRDTO {

    private static final long serialVersionUID = 1L;
    private String komoOid;
    private OrgDTO organisaatio;
    //KOODISTO DATA OBJECTS:
    private UiDTO koulutuskoodi;
    private UiDTO koulutusaste;
    private UiDTO koulutusala;
    private UiDTO opintoala;
    private UiDTO tutkinto;
    private UiDTO tutkintonimike;
    private UiDTO eqf;
    private UiDTO opintojenLaajuus;
    //OTHER DATA
    private TarjontaTila tila;
    private KoulutusmoduuliTyyppi koulutusmoduuliTyyppi;
    private TekstiV1RDTO<KomoTeksti> kuvausKomo;
    private TekstiV1RDTO<KomotoTeksti> kuvausKomoto;
    private KoulutusasteTyyppi koulutusasteTyyppi;

    public ToteutusDTO() {
    }
    /*
     * Contact persons
     */
    private Set<YhteyshenkiloTyyppi> yhteyshenkilos;

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
     * @return the komoOid
     */
    public String getKomoOid() {
        return komoOid;
    }

    /**
     * @param komoOid the komoOid to set
     */
    public void setKomoOid(String komoOid) {
        this.komoOid = komoOid;
    }

    /**
     * @return the koulutusmoduuliTyyppi
     */
    public KoulutusmoduuliTyyppi getKoulutusmoduuliTyyppi() {
        return koulutusmoduuliTyyppi;
    }

    /**
     * @param koulutusmoduuliTyyppi the koulutusmoduuliTyyppi to set
     */
    public void setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi koulutusmoduuliTyyppi) {
        this.koulutusmoduuliTyyppi = koulutusmoduuliTyyppi;
    }

    /**
     * @return the koulutuskoodi
     */
    public UiDTO getKoulutuskoodi() {
        return koulutuskoodi;
    }

    /**
     * @param koulutuskoodi the koulutuskoodi to set
     */
    public void setKoulutuskoodi(UiDTO koulutuskoodi) {
        this.koulutuskoodi = koulutuskoodi;
    }

    /**
     * @return the koulutusaste
     */
    public UiDTO getKoulutusaste() {
        return koulutusaste;
    }

    /**
     * @param koulutusaste the koulutusaste to set
     */
    public void setKoulutusaste(UiDTO koulutusaste) {
        this.koulutusaste = koulutusaste;
    }

    /**
     * @return the koulutusala
     */
    public UiDTO getKoulutusala() {
        return koulutusala;
    }

    /**
     * @param koulutusala the koulutusala to set
     */
    public void setKoulutusala(UiDTO koulutusala) {
        this.koulutusala = koulutusala;
    }

    /**
     * @return the opintoala
     */
    public UiDTO getOpintoala() {
        return opintoala;
    }

    /**
     * @param opintoala the opintoala to set
     */
    public void setOpintoala(UiDTO opintoala) {
        this.opintoala = opintoala;
    }

    /**
     * @return the tutkinto
     */
    public UiDTO getTutkinto() {
        return tutkinto;
    }

    /**
     * @param tutkinto the tutkinto to set
     */
    public void setTutkinto(UiDTO tutkinto) {
        this.tutkinto = tutkinto;
    }

    /**
     * @return the tutkintonimike
     */
    public UiDTO getTutkintonimike() {
        return tutkintonimike;
    }

    /**
     * @param tutkintonimike the tutkintonimike to set
     */
    public void setTutkintonimike(UiDTO tutkintonimike) {
        this.tutkintonimike = tutkintonimike;
    }

    /**
     * @return the eqf
     */
    public UiDTO getEqf() {
        return eqf;
    }

    /**
     * @param eqf the eqf to set
     */
    public void setEqf(UiDTO eqf) {
        this.eqf = eqf;
    }

    /**
     * @return the yhteyshenkilos
     */
    public Set<YhteyshenkiloTyyppi> getYhteyshenkilos() {
        if (yhteyshenkilos == null) {
            yhteyshenkilos = new HashSet<YhteyshenkiloTyyppi>();
        }
        return yhteyshenkilos;
    }

    /**
     * @param yhteyshenkilos the yhteyshenkilos to set
     */
    public void setYhteyshenkilos(Set<YhteyshenkiloTyyppi> yhteyshenkilos) {
        this.yhteyshenkilos = yhteyshenkilos;
    }

    /**
     * @return the organisaatio
     */
    public OrgDTO getOrganisaatio() {
        if (organisaatio == null) {
            organisaatio = new OrgDTO();
        }

        return organisaatio;
    }

    /**
     * @param organisaatio the organisaatio to set
     */
    public void setOrganisaatio(OrgDTO organisaatio) {
        this.organisaatio = organisaatio;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
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

    /**
     * @return the opintojenLaajuus
     */
    public UiDTO getOpintojenLaajuus() {
        return opintojenLaajuus;
    }

    /**
     * @param opintojenLaajuus the opintojenLaajuus to set
     */
    public void setOpintojenLaajuus(UiDTO opintojenLaajuus) {
        this.opintojenLaajuus = opintojenLaajuus;
    }

    /**
     * @return the kuvausKomo
     */
    public TekstiV1RDTO<KomoTeksti> getKuvausKomo() {
        if (kuvausKomo == null) {
            kuvausKomo = new TekstiV1RDTO<KomoTeksti>();
        }
        return kuvausKomo;
    }

    /**
     * @param kuvausKomo the kuvausKomo to set
     */
    public void setKuvausKomo(TekstiV1RDTO<KomoTeksti> kuvausKomo) {
        this.kuvausKomo = kuvausKomo;
    }

    /**
     * @return the kuvausKomoto
     */
    public TekstiV1RDTO<KomotoTeksti> getKuvausKomoto() {
        if (kuvausKomoto == null) {
            kuvausKomoto = new TekstiV1RDTO<KomotoTeksti>();
        }

        return kuvausKomoto;
    }

    /**
     * @param kuvausKomoto the kuvausKomoto to set
     */
    public void setKuvausKomoto(TekstiV1RDTO<KomotoTeksti> kuvausKomoto) {
        this.kuvausKomoto = kuvausKomoto;
    }

}
