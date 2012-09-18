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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A Koulutus object may consist multiple child Koulutus objects as its parts. This object describes the link from the 
 * parent Koulutus to child Koulutus. It also adds information into this relation, such whether the child is considered 
 * as optional or mandatory part.
 *
 */
@Entity
@Table(name = KoulutusSisaltyvyys.TABLE_NAME, uniqueConstraints =
@UniqueConstraint(name = "UK_" + KoulutusSisaltyvyys.TABLE_NAME + "_01",
columnNames = {
    KoulutusSisaltyvyys.PARENT_COLUMN_NAME,
    KoulutusSisaltyvyys.CHILD_COLUMN_NAME
}))
public class KoulutusSisaltyvyys extends BaseEntity implements Comparable<KoulutusSisaltyvyys> {

    public static final String TABLE_NAME = "koulutus_sisaltyvyys";

    private static final long serialVersionUID = 9160508919072362147L;

    public static final String PARENT_COLUMN_NAME = "parent_id";

    public static final String CHILD_COLUMN_NAME = "child_id";

    @ManyToOne
    @JoinColumn(name = PARENT_COLUMN_NAME)
    private LearningOpportunityObject parent;

    @ManyToOne
    @JoinColumn(name = CHILD_COLUMN_NAME)
    private LearningOpportunityObject child;

    /**
     * If the contained koulutus, in this relationship, is optional or not.
     */
    private boolean optional;

    /**
     * JPA only.
     */
    protected KoulutusSisaltyvyys() {
    }

    public KoulutusSisaltyvyys(LearningOpportunityObject parent, LearningOpportunityObject child, boolean optional) {
        this.parent = parent;
        this.child = child;
        this.optional = optional;
    }

    /**
     * Copy constructor.
     * 
     * @param source
     */
    public KoulutusSisaltyvyys(KoulutusSisaltyvyys source) {
        this(source.getParent(), source.getChild(), source.isOptional());
    }

    /**
     * Returns non-null parent.
     *
     * @return
     */
    public LearningOpportunityObject getParent() {
        return parent;
    }

    /**
     * Returns non-null child.
     *
     * @return
     */
    public LearningOpportunityObject getChild() {
        return child;
    }

    /**
     * True if the relationship from the parent to the child is considered optional, the actual meaning of optionality depends on the types of Koulutus in
     * question.
     *
     * @return
     */
    public boolean isOptional() {
        return optional;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof KoulutusSisaltyvyys == false) {
            return false;
        }

        if (this == o) {
            return true;
        }

        KoulutusSisaltyvyys s = (KoulutusSisaltyvyys) o;

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

    @Override
    public int compareTo(KoulutusSisaltyvyys t) {
        return child.compareTo(t.getChild());
    }

}

