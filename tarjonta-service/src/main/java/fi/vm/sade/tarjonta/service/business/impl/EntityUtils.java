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
package fi.vm.sade.tarjonta.service.business.impl;

import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoulutuksenKestoTyyppi;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public final class EntityUtils {

    private EntityUtils() {
    }

    public static void copyFields(KoulutusmoduuliToteutus from, KoulutusmoduuliToteutus to) {
        to.setNimi(from.getNimi());
        to.setTila(from.getTila());
        to.setMaksullisuus(from.getMaksullisuus());
    }

    public static void copyFields(PaivitaKoulutusTyyppi from, KoulutusmoduuliToteutus to) {

        to.setKoulutuksenAlkamisPvm(from.getKoulutuksenAlkamisPaiva());
        to.setKoulutuslajiList(toUriSet(from.getKoulutuslaji()));

        final KoulutuksenKestoTyyppi kesto =from.getKesto();
        to.setSuunniteltuKestoArvo(kesto.getArvo());
        to.setSuunniteltuKestoYksikko(kesto.getYksikko());
        

        // todo: other fields
    }

    public static void copyFields(LisaaKoulutusTyyppi koulutus, KoulutusmoduuliToteutus to) {

        // todo: koulutus should have multiple opetusmuotos
        if (koulutus.getOpetusmuoto() != null) {
            to.addOpetusmuoto(new KoodistoUri(koulutus.getOpetusmuoto().getUri()));
        }

        to.setOid(koulutus.getOid());
        to.setKoulutuksenAlkamisPvm(koulutus.getKoulutuksenAlkamisPaiva());

        to.setSuunniteltuKestoArvo(koulutus.getKesto().getArvo());
        to.setSuunniteltuKestoYksikko(koulutus.getKesto().getYksikko());

        for (KoodistoKoodiTyyppi opetusKieli : koulutus.getOpetuskieli()) {
            to.addOpetuskieli(new KoodistoUri(opetusKieli.getUri()));
        }

        for (KoodistoKoodiTyyppi koulutuslaji : koulutus.getKoulutuslaji()) {
            // fix: toteutus should have multiple koulutuslahji
        }

    }

    public static Set<String> toUriSet(Collection<KoodistoKoodiTyyppi> koodit) {
        Set<String> set = new HashSet<String>();
        for (KoodistoKoodiTyyppi koodi : koodit) {
            set.add(koodi.getUri());
        }
        return set;
    }

}

