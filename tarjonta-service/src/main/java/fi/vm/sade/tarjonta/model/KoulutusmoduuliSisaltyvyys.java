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

import fi.vm.sade.generic.model.BaseEntity;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author Jukka Raanamo
 */
@Entity
@Table(name=KoulutusmoduuliSisaltyvyys.TABLE_NAME)
public class KoulutusmoduuliSisaltyvyys extends BaseEntity {

    public static final String TABLE_NAME = "koulutusmoduuli_sisaltyvyys";
    
    /**
     * The parent.
     */
    @ManyToOne
    private Koulutusmoduuli parent;

    /**
     * The child.
     */
    @ManyToOne
    private Koulutusmoduuli child;

    /**
     * If the contained module is optional or not.
     */
    private boolean optional;

    /**
     * JPA only.
     */
    protected KoulutusmoduuliSisaltyvyys() {
    }

    public KoulutusmoduuliSisaltyvyys(Koulutusmoduuli parent, Koulutusmoduuli child, boolean optional) {
        this.parent = parent;
        this.child = child;
        this.optional = optional;
    }

    public Koulutusmoduuli getParent() {
        return parent;
    }

    public Koulutusmoduuli getChild() {
        return child;
    }

    public boolean isOptional() {
        return optional;
    }
    
    

    @Override
    public boolean equals(Object o) {

        if (o instanceof KoulutusmoduuliSisaltyvyys == false) {
            return false;
        }

        if (this == o) {
            return true;
        }

        KoulutusmoduuliSisaltyvyys s = (KoulutusmoduuliSisaltyvyys) o;

        // TOOD: are we equals if the optionality is different?
        return new EqualsBuilder().append(parent, s.parent).
            append(child, s.child).
            isEquals();

    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(23, 53).append(parent).
            append(child).
            toHashCode();

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("parent", parent).
            append("child", child).
            append("optional", optional).
            toString();

    }

}

