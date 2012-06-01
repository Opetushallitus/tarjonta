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
package fi.vm.sade.tarjonta.model.dto;

import java.io.Serializable;

/**
 *
 * @author Jukka Raanamo
 */
public class KoulutusmoduuliSummaryDTO implements Serializable {

    private static final long serialVersionUID = 8499867932026504252L;

    private String koulutusmoduuliOID;

    private String nimi;

    public KoulutusmoduuliSummaryDTO() {
    }

    public KoulutusmoduuliSummaryDTO(String koulutusmoduuliOID, String nimi) {
        this.koulutusmoduuliOID = koulutusmoduuliOID;
        this.nimi = nimi;
    }

    public String getKoulutusmoduuliOID() {
        return koulutusmoduuliOID;
    }

    public void setKoulutusmoduuliOID(String koulutusmoduuliOID) {
        this.koulutusmoduuliOID = koulutusmoduuliOID;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

}

