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
package fi.vm.sade.tarjonta.service.mock;

import fi.vm.sade.tarjonta.model.KoodistoContract;
import fi.vm.sade.tarjonta.model.dto.*;
import fi.vm.sade.tarjonta.service.NoSuchOIDException;
import fi.vm.sade.tarjonta.service.KoulutusmoduuliAdminService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 *
 * @author Jukka Raanamo
 * @author Marko Lyly
 */
public class KoulutusmoduuliAdminServiceMock implements KoulutusmoduuliAdminService, Serializable {

    private static final long serialVersionUID = 1L;



    private KoulutusmoduuliStorage storage = new KoulutusmoduuliStorage();

    public KoulutusmoduuliAdminServiceMock() {
        initDefaultData();
    }
    
    /**
     * Creates service that uses given storage as back end. All items are retrieved from and inserted into
     * this storage.
     * 
     * @param storage
     */
    public KoulutusmoduuliAdminServiceMock(KoulutusmoduuliStorage storage) {
        this.storage = storage;
    }

    /**
     * Insert storage to be used as backend for inserting and retrieving data.
     *
     * @param storage
     */
    public void setStorage(KoulutusmoduuliStorage storage) {
        this.storage = storage;
    }

    public KoulutusmoduuliStorage getStorage() {
        return storage;
    }

    @Override
    public TutkintoOhjelmaDTO createTutkintoOhjelma(String organisaatioOid) {

        TutkintoOhjelmaDTO ohjelma = new TutkintoOhjelmaDTO();
        KoulutusmoduuliPerustiedotDTO perustiedot = new KoulutusmoduuliPerustiedotDTO();
        ohjelma.setOrganisaatioOid(organisaatioOid);
        ohjelma.setPerustiedot(perustiedot);
        return ohjelma;

    }

    @Override
    public KoulutusmoduuliDTO save(KoulutusmoduuliDTO koulutusModuuli) {

        koulutusModuuli.setUpdated(new Date());
        storage.add(koulutusModuuli);
        return koulutusModuuli;

    }

    @Override
    public KoulutusmoduuliDTO find(String koulutusModuuliOID) {

        KoulutusmoduuliDTO result = null;

        for (KoulutusmoduuliDTO koulutusmoduuliDTO : storage.getAll()) {
            if (koulutusmoduuliDTO.getOid().equals(koulutusModuuliOID)) {
                result = koulutusmoduuliDTO;
                break;
            }
        }

        return result;
    }

    @Override
    public List<KoulutusmoduuliDTO> find(KoulutusmoduuliSearchDTO searchSpecification) {
        if (searchSpecification == null) {
            throw new IllegalArgumentException("Search specification required.");
        }

        List<KoulutusmoduuliDTO> result = new ArrayList<KoulutusmoduuliDTO>();

        for (KoulutusmoduuliDTO koulutusmoduuliDTO : storage.getAll()) {
            if (match(koulutusmoduuliDTO, searchSpecification)) {
                result.add(koulutusmoduuliDTO);
            }
        }

        return result;
    }

    /**
     * Filter/match koulutusmoduuli with given search spesification.
     *
     * @param koulutusmoduuliDTO
     * @param searchSpesification
     * @return true if matches
     */
    private boolean match(KoulutusmoduuliDTO koulutusmoduuliDTO, KoulutusmoduuliSearchDTO searchSpesification) {
        if (searchSpesification.getNimi() != null) {
            // TODO match when koulutusohjelma has some content :)
            return true;
        }

        // no match by default
        return false;
    }

    
    @Override
    public List<KoulutusmoduuliSummaryDTO> getChildModuulis(String moduuliOID) throws NoSuchOIDException {

        List<KoulutusmoduuliSummaryDTO> result = new ArrayList<KoulutusmoduuliSummaryDTO>();

        for (int i = 0; i < 5; i++) {
            KoulutusmoduuliSummaryDTO summary = new KoulutusmoduuliSummaryDTO("http://koulutusmoduuli/" + i,
                "Koulutusmoduuli (ala) " + i);
            result.add(summary);
        }

        return result;



    }

    private void initDefaultData() {

        TutkintoOhjelmaDTO tutkintoOhjelma = new TutkintoOhjelmaDTO();

        tutkintoOhjelma.setNimi("Oulun Koulu, Tietokenkasittelyn KO");
        tutkintoOhjelma.setOid("123.123.123.123");
        tutkintoOhjelma.setOrganisaatioOid("http://organisaatio/123.123");

        // currently there is no view to edit perustiedot for koulutusmoduuli - hence 
        // we'll add no test data yet
        KoulutusmoduuliPerustiedotDTO perustiedot = new KoulutusmoduuliPerustiedotDTO();
        tutkintoOhjelma.setPerustiedot(perustiedot);

        tutkintoOhjelma.setTila(KoodistoContract.TarjontaTilat.JULKAISTU);
        tutkintoOhjelma.setUpdated(new Date());

        save(tutkintoOhjelma);

    }

}

