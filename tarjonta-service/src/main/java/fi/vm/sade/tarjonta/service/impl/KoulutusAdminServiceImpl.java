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
package fi.vm.sade.tarjonta.service.impl;

import fi.vm.sade.tarjonta.model.KoodistoContract;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.KoulutusAdminService;
import fi.vm.sade.tarjonta.service.business.KoulutusBusinessService;
import fi.vm.sade.tarjonta.service.tarjonta.koulutus._2012._09._04.KoulutusTyyppi;
import fi.vm.sade.tarjonta.service.tarjonta.koulutus._2012._09._04.KoulutusmoduuliTyyppi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 *
 */
@Transactional
@Service
public class KoulutusAdminServiceImpl implements KoulutusAdminService {

    @Autowired
    private KoulutusBusinessService koulutusService;

    private UserRoleResolver roleResolver = new ThreadLocalUserRoleResolver();

    @Override
    public KoulutusTyyppi createKoulutus(KoulutusTyyppi koulutus) {

        // todo: dummy call to simulate future impl
        if (roleResolver.isUserRekisterinpitaja()) {

            // convert only information related to koulutusmoduuli
            final Koulutusmoduuli newModuuli = koulutusService.create(extractKoulutusmoduuli(koulutus));
            return convert(newModuuli);

            // todo: dummy call to simulate future impl
        } else if (roleResolver.isUserVirkailija()) {

            // if user is referring to a Koulutusmoduuli that already exists, load it first - else,
            // create based on input data
            final Koulutusmoduuli moduuli = findOrCreateKoulutusmoduuli(koulutus);
            final KoulutusmoduuliToteutus toteutus = extractKoulutusmoduuliToteutus(koulutus);

            final KoulutusmoduuliToteutus newToteutus = koulutusService.create(toteutus, moduuli);

            return convert(moduuli, newToteutus);

        } else {

            throw new RuntimeException("unauthorized activity");

        }

    }

    /**
     * Assign custom role resolver. Exposed mainly for JUnit testing.
     *
     * @param roleResolver
     */
    public void setRoleResolver(UserRoleResolver roleResolver) {
        this.roleResolver = roleResolver;
    }

    /**
     * If given input refers to an existing Koulutusmoduuli, it is returned, else new one is created based on input values.
     *
     * @param koulutus
     * @throws what-if-bad-koulutusmoduuli-oid
     * @throws what-if-incomplete-input-data
     *
     * @return
     */
    private Koulutusmoduuli findOrCreateKoulutusmoduuli(KoulutusTyyppi koulutus) {

        KoulutusmoduuliTyyppi moduuli = koulutus.getKoulutusModuuli();
        final String oid = moduuli.getOid();

        Koulutusmoduuli result = null;

        if (oid != null) {
            result = (Koulutusmoduuli) koulutusService.findByOid(oid);
        } else {
            // are there other means of identifying an existing Koulutusmoduuli?
            result = extractKoulutusmoduuli(koulutus);
            // if not, we will create one
            result = koulutusService.create(result);
        }

        return result;

    }

    /**
     * TODO
     *
     * @param moduuli
     * @return
     */
    private KoulutusTyyppi convert(Koulutusmoduuli moduuli) {
        // just makes unit test happy until more tests are in place
        return new KoulutusTyyppi();
    }

    /**
     * TODO
     *
     * @param moduuli
     * @param toteutus
     * @return
     */
    private KoulutusTyyppi convert(Koulutusmoduuli moduuli, KoulutusmoduuliToteutus toteutus) {
        return null;
    }

    /**
     * TODO
     *
     * @param koulutus
     * @return
     */
    private KoulutusmoduuliToteutus extractKoulutusmoduuliToteutus(KoulutusTyyppi koulutus) {
        return null;
    }

    /**
     * Constructs Koulutusmoduuli based on the moduuliTyyppi from aggregated input data.
     *
     * @param koulutus
     * @return
     */
    private Koulutusmoduuli extractKoulutusmoduuli(KoulutusTyyppi koulutus) {

        KoulutusmoduuliTyyppi koulutusmoduuli = koulutus.getKoulutusModuuli();

        // todo: the specs is not quite clear as it is talking also about numeric values - if the source is
        // Koodisto, we should be talking about URI's only - right?
        final String moduuliTyyppiUri = koulutusmoduuli.getModuulinTyyppi();

        if (KoodistoContract.ModuuliTyypit.TUTKINTO_OHJELMA.equals(moduuliTyyppiUri)) {

            return extractTutkintoOhjelma(koulutusmoduuli);

        } else {
            throw new RuntimeException("unsupported moduuliTyyppi: " + moduuliTyyppiUri);
        }

    }

    /**
     * Constructs Koulutusmoduuli of type TutkintoOhjelma out of given aggregated input data. This should be refactored into separate class (converter).
     *
     * @param source
     * @return
     */
    private Koulutusmoduuli extractTutkintoOhjelma(KoulutusmoduuliTyyppi source) {

        Koulutusmoduuli t = new Koulutusmoduuli(fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);

        t.setKoulutusluokitusKoodi(source.getKoulutusLuokitusKoodi());
        t.setKoulutusNimi(source.getKoulutuksenNimi());
        t.setTutkintoOhjelmanNimi(source.getTutkintoOhjelmanNimi());
        t.setOid(source.getOid());

        // todo: where does authorization take place?
        t.setOmistajaOrganisaatioOid(source.getOwnerOrganisaatioOid());

        return t;

    }

    /**
     * Resolves the type of the user (in scope of the admin UI!?!).
     */
    public interface UserRoleResolver {

        public boolean isUserRekisterinpitaja();

        public boolean isUserVirkailija();

    }


    /**
     * Default implementation that would use the ThreadLocal injected user credentials... .
     */
    private static class ThreadLocalUserRoleResolver implements UserRoleResolver {

        @Override
        public boolean isUserRekisterinpitaja() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isUserVirkailija() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }


}

