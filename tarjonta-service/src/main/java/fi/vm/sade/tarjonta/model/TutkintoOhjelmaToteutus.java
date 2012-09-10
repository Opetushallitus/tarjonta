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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Jukka Raanamo
 */
@Entity
@DiscriminatorValue(Koulutus.KoulutusTyyppit.TUTKINTO_OHJELMA_TOTEUTUS)
public class TutkintoOhjelmaToteutus extends KoulutusmoduuliToteutus {

    private static final long serialVersionUID = -9026147669046987148L;

    /**
     * Overrides table name from KoulutusmoduuliToteutus
     */
    public static final String TABLE_NAME = "tutkinto_ohjelma_toteutus";

    public TutkintoOhjelmaToteutus(Koulutusmoduuli moduuli) {
        super(moduuli);        
    }

    /**
     * Public constructor required at least by converted
     */
    public TutkintoOhjelmaToteutus() {
        this(null);
    }

}

