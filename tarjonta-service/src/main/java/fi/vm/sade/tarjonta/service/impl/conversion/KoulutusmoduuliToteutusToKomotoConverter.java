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
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.dto.Komoto;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mlyly
 */
public class KoulutusmoduuliToteutusToKomotoConverter extends AbstractFromDomainConverter<KoulutusmoduuliToteutus, Komoto>{

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusmoduuliToKomoConverter.class);

    @Override
    public Komoto convert(KoulutusmoduuliToteutus s) {

        if (s == null) {
            return null;
        }

        Komoto t = new Komoto();

//        t.setAmmattinimikeUris(convert(s.getAmmattinimikes()));
        t.setArviointikriteerit(KoulutusmoduuliToKomoConverter.convert(s.getArviointikriteerit()));
//        t.setAvainsanaUris(KoulutusmoduuliToKomoConverter.convert(s.getAvainsanas()));
//        t.set(s.get);

//
//        t.setCreated(null);
//        t.setCreatedByOid(null);
//        t.setEqfLuokitusUri(s.getEqfLuokitus());
//        t.setJatkoOpintoMahdollisuudet(convert(s.getJatkoOpintoMahdollisuudet()));
//        t.setKoulutuksenRakenne(convert(s.getKoulutuksenRakenne()));
//        t.setKoulutusAlaUri(s.getKoulutusala());
//        t.setKoulutusAsteUri(s.getKoulutusAste());
//        // TODO t.setKoulutusLuokitusKoodiUri(s.get); ??? waat
//        t.setKoulutusOhjelmaKoodiUri(s.getKoulutusohjelmaKoodi());
//        t.setKoulutusTyyppiUri(s.getKoulutustyyppi()); // TODO onko uri?
//        t.setLaajuusArvo(s.getLaajuusArvo());
//        t.setLaajuusYksikkoUri(s.getLaajuusYksikko());
//        t.setLukiolinjaUri(s.getLukiolinja()); // TODO onko?
//        // TODO ei ole, on enum! t.setModuuliTyyppiUri(s.getModuuliTyyppi());
//        t.setNimi(convert(s.getNimi()));
//        t.setNqfLuokitusUri(s.getNqfLuokitus()); // TODO onko nqf koodisto?
//        t.setOid(s.getOid());
//        t.setOpintoalaUri(s.getOpintoala()); // TODO onko uri?
//
//        // TODO onko organisaatioita KOMOilla ollenkaan?
//        t.setOrganisaatioOid(s.getOmistajaOrganisaatioOid());
//        t.setTarjoajaOid(s.getOmistajaOrganisaatioOid());
//
//        t.setTavoitteet(convert(s.getTavoitteet()));
//        t.setTila("" + s.getTila()); // TODO tila & sen konversio?
//        t.setTutkintoOhjelmanNimiUri(s.getTutkintoOhjelmanNimi());
//        t.setTutkintonimikeUri(s.getTutkintonimike());
//        t.setUlkoinenTunniste(s.getUlkoinenTunniste());
//        t.setUpdateByOid(null); // TODO modifier OID KOMOLLA?
//        t.setUpdated(s.getUpdated());
//        t.setVersion(s.getVersion() == null ? 0 : s.getVersion().intValue());
//
//        // // TODO convert, but efficiently! t.setYlaModuulit(null);

        t.setOid(s.getOid());

        return t;
    }




    public List<String> convert(Set<KoodistoUri> uris) {
        List<String> result = new ArrayList<String>();

        for (KoodistoUri koodistoUri : uris) {
            result.add(koodistoUri.getKoodiUri());
        }

        return result;
    }


}
