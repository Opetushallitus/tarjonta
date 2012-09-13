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
package fi.vm.sade.tarjonta;

import fi.vm.sade.tarjonta.model.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Sets up common test fixtures that can be used through out different DAO/service tests.
 *
 */
public class KoulutusFixtures {

    public static final String OID_TIETOJENKASITTELYN_KOULUTUS = "dummy";

    public TutkintoOhjelma simpleTutkintoOhjelma;

    public TutkintoOhjelmaToteutus simpleTutkintoOhjelmaToteutus;

    public Hakukohde simpleHakukohde;
    
    public Hakukohde hakukohdeWithValintakoe;

    public KoulutusFixtures() {
        recreate();
    }

    public final void recreate() {
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        
        simpleTutkintoOhjelma = new TutkintoOhjelma();
        simpleTutkintoOhjelma.setOid("http://oph/koulutusmoduuli/simpleTutkintoOhjelma");
        simpleTutkintoOhjelma.setTutkintoOhjelmanNimi("Simple Tutkinto-Ohjelma");
        simpleTutkintoOhjelma.setKoulutusKoodi("500001");

        simpleTutkintoOhjelmaToteutus = new TutkintoOhjelmaToteutus(simpleTutkintoOhjelma);
        simpleTutkintoOhjelmaToteutus.setNimi("Simple Tutkinto-Ohjelma toteutus");
        simpleTutkintoOhjelmaToteutus.setOid("http://oph/koulutusmoduulitotetutus/simpleTutkintoOhjelma");
        simpleTutkintoOhjelmaToteutus.setKoulutuksenAlkamisPvm(cal.getTime());
        simpleTutkintoOhjelmaToteutus.setMaksullisuus(null);
        simpleTutkintoOhjelmaToteutus.addOpetuskieli(new KoodistoUri("http://kielet/fi"));
        simpleTutkintoOhjelmaToteutus.addOpetusmuoto(new KoodistoUri("http://opetusmuodot/lahiopetus"));
        
        simpleHakukohde = new Hakukohde();
        simpleHakukohde.setHakukohde("http://hakukohde/yyy");
        
        hakukohdeWithValintakoe = new Hakukohde();
        hakukohdeWithValintakoe.setHakukohde("http://hakukohde/xxx");
        hakukohdeWithValintakoe.addValintakoe(new Valintakoe());


    }

}

