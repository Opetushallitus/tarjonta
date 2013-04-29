/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.service.resources.dto.Komo;
import fi.vm.sade.tarjonta.service.resources.dto.MonikielinenTekstis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mlyly
 */
public class KoulutusmoduuliToKomoConverter extends AbstractFromDomainConverter<Koulutusmoduuli, Komo>{

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusmoduuliToKomoConverter.class);

    @Override
    public Komo convert(Koulutusmoduuli s) {
        LOG.debug("convert({}) --> Komo", s);

        if (s == null) {
            return null;
        }

        Komo t = new Komo();

        // TODO convert, but efficiently! t.setAlaModuulit(null);

        t.setCreated(null);
        t.setCreatedByOid(null);
        t.setEqfLuokitusUri(s.getEqfLuokitus());
        t.setJatkoOpintoMahdollisuudet(convert(s.getJatkoOpintoMahdollisuudet()));
        t.setKoulutuksenRakenne(convert(s.getKoulutuksenRakenne()));
        t.setKoulutusAlaUri(s.getKoulutusala());
        t.setKoulutusAsteUri(s.getKoulutusAste());
        // TODO t.setKoulutusLuokitusKoodiUri(s.get); ??? waat
        t.setKoulutusOhjelmaKoodiUri(s.getKoulutusohjelmaKoodi());
        t.setKoulutusTyyppiUri(s.getKoulutustyyppi()); // TODO onko uri?
        t.setLaajuusArvo(s.getLaajuusArvo());
        t.setLaajuusYksikkoUri(s.getLaajuusYksikko());
        t.setLukiolinjaUri(s.getLukiolinja()); // TODO onko?
        // TODO ei ole, on enum! t.setModuuliTyyppiUri(s.getModuuliTyyppi());
        t.setNimi(convert(s.getNimi()));
        t.setNqfLuokitusUri(s.getNqfLuokitus()); // TODO onko nqf koodisto?
        t.setOid(s.getOid());
        t.setOpintoalaUri(s.getOpintoala()); // TODO onko uri?

        // TODO onko organisaatioita KOMOilla ollenkaan?
        t.setOrganisaatioOid(s.getOmistajaOrganisaatioOid());
        t.setTarjoajaOid(s.getOmistajaOrganisaatioOid());

        t.setTavoitteet(convert(s.getTavoitteet()));
        t.setTila("" + s.getTila()); // TODO tila & sen konversio?
        t.setTutkintoOhjelmanNimiUri(s.getTutkintoOhjelmanNimi());
        t.setTutkintonimikeUri(s.getTutkintonimike());
        t.setUlkoinenTunniste(s.getUlkoinenTunniste());
        t.setUpdateByOid(null); // TODO modifier OID KOMOLLA?
        t.setUpdated(s.getUpdated());
        t.setVersion(s.getVersion() == null ? 0 : s.getVersion().intValue());

        // // TODO convert, but efficiently! t.setYlaModuulit(null);

        LOG.debug("  --> {}", t);

        return t;
    }

    public static MonikielinenTekstis convert(MonikielinenTeksti s) {
        if (s == null) {
            return null;
        }
        MonikielinenTekstis t = new MonikielinenTekstis();

        for (TekstiKaannos tekstiKaannos : s.getTekstis()) {
            t.addKieli(tekstiKaannos.getKieliKoodi(), tekstiKaannos.getArvo());
        }

        return t;
    }



}
