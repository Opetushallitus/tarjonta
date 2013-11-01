/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.service.resources.v1.dto;

import fi.vm.sade.tarjonta.service.resources.dto.kk.UiMetaDTO;
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
 * @author mlyly
 */
public abstract class KoulutusV1RDTO extends KoulutusmoduuliRelationV1RDTO {
    
    private String komoOid;
    private String komotoOid;
    private String parentKomoOid;
    private String parentKomotoOid;

    private OrganisaatioV1RDTO organisaatio;

    //OTHER DATA
    private TarjontaTila tila;
    private KoulutusmoduuliTyyppi koulutusmoduuliTyyppi;
    private KoulutusasteTyyppi koulutusasteTyyppi;
    private TekstiV1RDTO<KomoTeksti> kuvausKomo;
    private TekstiV1RDTO<KomotoTeksti> kuvausKomoto;

    public KoulutusV1RDTO() {
    }

    public String getKomoOid() {
        return komoOid;
    }

    public void setKomoOid(String _komoOid) {
        this.komoOid = _komoOid;
    }

    public String getKomotoOid() {
        return komotoOid;
    }

    public void setKomotoOid(String _komotoOid) {
        this.komotoOid = _komotoOid;
    }

    public String getParentKomoOid() {
        return parentKomoOid;
    }

    public void setParentKomoOid(String _parentKomoOid) {
        this.parentKomoOid = _parentKomoOid;
    }

    public String getParentKomotoOid() {
        return parentKomotoOid;
    }

    public void setParentKomotoOid(String _parentKomotoOid) {
        this.parentKomotoOid = _parentKomotoOid;
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
    public OrganisaatioV1RDTO getOrganisaatio() {
        if (organisaatio == null) {
            organisaatio = new OrganisaatioV1RDTO();
        }

        return organisaatio;
    }

    /**
     * @param organisaatio the organisaatio to set
     */
    public void setOrganisaatio(OrganisaatioV1RDTO organisaatio) {
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
