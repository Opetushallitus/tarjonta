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
import java.math.BigDecimal;

import fi.vm.sade.generic.model.BaseEntity;
import javax.persistence.*;
/**
 *
 * @author: Tuomas Katva
 */
@Entity
@Table(name = "pisteraja")
public class Pisteraja extends TarjontaBaseEntity  {

    private static final long serialVersionUID = 1878029033380865674L;

    private String valinnanPisterajaTyyppi;

    private BigDecimal alinPistemaara;

    private BigDecimal ylinPistemaara;

    private BigDecimal alinHyvaksyttyPistemaara;

    /**
     * @return the valinnanPisterajaTyyppi
     */
    public String getValinnanPisterajaTyyppi() {
        return valinnanPisterajaTyyppi;
    }

    /**
     * @param valinnanPisterajaTyyppi the valinnanPisterajaTyyppi to set
     */
    public void setValinnanPisterajaTyyppi(String valinnanPisterajaTyyppi) {
        this.valinnanPisterajaTyyppi = valinnanPisterajaTyyppi;
    }

    /**
     * @return the alinPistemaara
     */
    public BigDecimal getAlinPistemaara() {
        return alinPistemaara;
    }

    /**
     * @param alinPistemaara the alinPistemaara to set
     */
    public void setAlinPistemaara(BigDecimal alinPistemaara) {
        this.alinPistemaara = alinPistemaara;
    }

    /**
     * @return the ylinPistemaara
     */
    public BigDecimal getYlinPistemaara() {
        return ylinPistemaara;
    }

    /**
     * @param ylinPistemaara the ylinPistemaara to set
     */
    public void setYlinPistemaara(BigDecimal ylinPistemaara) {
        this.ylinPistemaara = ylinPistemaara;
    }

    /**
     * @return the alinHyvaksyttyPistemaara
     */
    public BigDecimal getAlinHyvaksyttyPistemaara() {
        return alinHyvaksyttyPistemaara;
    }

    /**
     * @param alinHyvaksyttyPistemaara the alinHyvaksyttyPistemaara to set
     */
    public void setAlinHyvaksyttyPistemaara(BigDecimal alinHyvaksyttyPistemaara) {
        this.alinHyvaksyttyPistemaara = alinHyvaksyttyPistemaara;
    }



}
