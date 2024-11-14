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
package fi.vm.sade.tarjonta.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * KoulutusSisaltyvyys on rakenne jolla Koulutusmoduuli:n alle voidaan liittaa toisia Koulutusmoduuleja.
 * Tama luokka sistaa myos semanttisia saantoja siita miten opiskelija voi valita alla rakenteita.
 *
 */
@Entity
@Table(name = KoulutusSisaltyvyys.TABLE_NAME)
public class KoulutusSisaltyvyys extends TarjontaBaseEntity implements Serializable {

    static final String TABLE_NAME = "koulutus_sisaltyvyys";

    private static final long serialVersionUID = 7833956682160881671L;

    private static final String PARENT_COLUMN_NAME = "parent_id";

    private Integer minArvo;

    private Integer maxArvo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tyyppi", nullable = false)
    private ValintaTyyppi valintaTyyppi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = PARENT_COLUMN_NAME)
    private Koulutusmoduuli ylamoduuli;

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name = TABLE_NAME + "_koulutus", joinColumns =
    @JoinColumn(name = TABLE_NAME + "_" + ID_COLUMN_NAME, referencedColumnName = BaseEntity.ID_COLUMN_NAME), inverseJoinColumns =
    @JoinColumn(name = Koulutusmoduuli.TABLE_NAME + "_" + Koulutusmoduuli.ID_COLUMN_NAME, referencedColumnName = BaseEntity.ID_COLUMN_NAME))
    private Set<Koulutusmoduuli> alamoduuliList = new HashSet<Koulutusmoduuli>();

    public KoulutusSisaltyvyys() {
    }

    public KoulutusSisaltyvyys(Koulutusmoduuli ylamoduuli, Koulutusmoduuli alamoduuli, ValintaTyyppi valintaTyyppi) {
        this.valintaTyyppi = valintaTyyppi;
        this.ylamoduuli = ylamoduuli;
        addAlamoduuli(alamoduuli);
    }

    public void setYlamoduuli(Koulutusmoduuli ylamoduuli) {
        this.ylamoduuli = ylamoduuli;
    }

    public Koulutusmoduuli getYlamoduuli() {
        return ylamoduuli;
    }

    public Integer getMax() {
        return maxArvo;
    }

    public Integer getMin() {
        return minArvo;
    }

    public void setMax(Integer max) {
        this.maxArvo = max;
    }

    public void setMin(Integer min) {
        this.minArvo = min;
    }

    public ValintaTyyppi getValintaTyyppi() {
        return valintaTyyppi;
    }

    public void setValintaTyyppi(ValintaTyyppi selector) {
        this.valintaTyyppi = selector;
    }

    public Set<Koulutusmoduuli> getAlamoduuliList() {
        return Collections.unmodifiableSet(alamoduuliList);
    }

    public void removeAlamoduuli(Koulutusmoduuli moduuli) {
        alamoduuliList.remove(moduuli);
    }

    public final void addAlamoduuli(Koulutusmoduuli moduuli) {
        alamoduuliList.add(moduuli);
    }

    public enum ValintaTyyppi {

        /**
         * All sub modules are mandatory.
         */
        ALL_OFF,
        /**
         * One of the sub modules must be selected.
         */
        ONE_OFF,
        /**
         * A number between {@link #min} to {@link #max} must be selected.
         */
        NUMBER_OF,
        /**
         * An arbitrary number of modules need to be selected.
         */
        SOME_OFF,
        /**
         * Modules that will sum between {@link #min} and {@link #max} credits must be selected.
         */
        CREDITS,
        /**
         * Modules that will sum between {@link #min} and {@link #max} course units must be selected.
         */
        COURSE_UNITS
    }


}

