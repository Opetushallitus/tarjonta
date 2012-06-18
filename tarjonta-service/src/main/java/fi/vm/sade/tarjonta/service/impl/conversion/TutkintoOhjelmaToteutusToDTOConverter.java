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
package fi.vm.sade.tarjonta.service.impl.conversion;

import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.tarjonta.model.KoodistoKoodi;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutusTarjoaja;
import fi.vm.sade.tarjonta.model.TutkintoOhjelmaToteutus;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliTila;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliToteutusDTO;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaToteutusDTO;

/**
 * 
 * @param <T> 
 * @author Jukka Raanamo
 */
public class TutkintoOhjelmaToteutusToDTOConverter<T extends KoulutusmoduuliToteutusDTO> extends AbstractFromDomainConverter<TutkintoOhjelmaToteutus, T> {

    @Override
    public T convert(TutkintoOhjelmaToteutus source) {

        TutkintoOhjelmaToteutusDTO dto = new TutkintoOhjelmaToteutusDTO();
        dto.setTila(source.getTila());
        dto.setOid(source.getOid());
        dto.setNimi(source.getNimi());
        dto.setPerustiedot(CommonConverter.convert(source.getPerustiedot()));
        dto.setKoulutuksenAlkamisPvm(source.getKoulutuksenAlkamisPvm());
        dto.setKoulutuslajiUri(source.getKoulutusLajiUri());
        dto.setTarjoajat(convertTarjoajat(source));
        dto.setToteutettavaKoulutusmoduuliOID(convertKoulutusmoduuli(source));
        return (T) dto;
    }
    
    private List<String> convertTarjoajat(TutkintoOhjelmaToteutus source) {
        if (source.getTarjoajat() == null) {
            return null;
        }
        List<String> tarjoajat = new ArrayList<String>();
        for (KoulutusmoduuliToteutusTarjoaja curTarjoaja : source.getTarjoajat()) {
            tarjoajat.add(curTarjoaja.getOrganisaatioOID());
        }
        return tarjoajat;
    }
    
    private List<String> convertTeemaUris(TutkintoOhjelmaToteutus source) {
        if (source.getTeemaUris() == null) {
            return null;
        }
        List<String> teemaUris = new ArrayList<String>();
        for (KoodistoKoodi curTeema : source.getTeemaUris()) {
            teemaUris.add(curTeema.getKoodiUri());
        }
        return teemaUris;
    }
    
    private String convertKoulutusmoduuli(TutkintoOhjelmaToteutus source) {
        return (source.getKoulutusmoduuli() != null) ? source.getKoulutusmoduuli().getOid() : null; 
    }
    
}

