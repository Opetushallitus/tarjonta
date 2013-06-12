package fi.vm.sade.tarjonta.model;

/*
 *
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
import fi.vm.sade.generic.model.BaseEntity;
import fi.vm.sade.security.xssfilter.FilterXss;
import fi.vm.sade.security.xssfilter.XssFilterListener;

import javax.persistence.*;

/**
 *
 * @author: Tuomas Katva
 */
@Entity
@Table(name = "painotettavaoppiaine")
@EntityListeners(XssFilterListener.class)
public class PainotettavaOppiaine extends BaseEntity {

    @FilterXss
    private String oppiaine;
    private Integer painokerroin;

    /**
     * @return the oppiaine
     */
    public String getOppiaine() {
        return oppiaine;
    }

    /**
     * @param oppiaine the oppiaine to set
     */
    public void setOppiaine(String oppiaine) {
        this.oppiaine = oppiaine;
    }

    /**
     * @return the painokerroin
     */
    public Integer getPainokerroin() {
        return painokerroin;
    }

    /**
     * @param painokerroin the painokerroin to set
     */
    public void setPainokerroin(Integer painokerroin) {
        this.painokerroin = painokerroin;
    }


}
