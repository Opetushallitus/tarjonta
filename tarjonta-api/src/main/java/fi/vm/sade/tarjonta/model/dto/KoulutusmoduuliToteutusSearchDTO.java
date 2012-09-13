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
package fi.vm.sade.tarjonta.model.dto;

/**
 *
 * @author Tuomas Katva
 */
public class KoulutusmoduuliToteutusSearchDTO {
    
    private String tila;
    private int startIndex = 0;
    private int pageSize = Integer.MAX_VALUE;

    
    public KoulutusmoduuliToteutusSearchDTO () {
        
    }
    
    public KoulutusmoduuliToteutusSearchDTO(String tilaParam) {
        tila = tilaParam;
    }
    /**
     * @return the tila
     */
    public String getTila() {
        return tila;
    }

    /**
     * @param tila the tila to set
     */
    public void setTila(String tila) {
        this.tila = tila;
    }

    /**
     * @return the startIndex
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * @param startIndex the startIndex to set
     */
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
}
