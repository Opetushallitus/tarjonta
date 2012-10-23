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
import fi.vm.sade.tarjonta.model.WebLinkki;
import fi.vm.sade.tarjonta.model.Yhteyshenkilo;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoulutuksenKestoTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.WebLinkkiTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.YhteyshenkiloTyyppi;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
        to.setKoulutuslajis(toStringUriSet(from.getKoulutuslaji()));

        final KoulutuksenKestoTyyppi kesto = from.getKesto();
        to.setSuunniteltuKestoArvo(kesto.getArvo());
        to.setSuunniteltuKestoYksikko(kesto.getYksikko());


        // todo: other fields
    }

    public static void copyFields(LisaaKoulutusTyyppi fromKoulutus, KoulutusmoduuliToteutus toKoulutus) {

        toKoulutus.setOpetusmuoto(toKoodistoUriSet(fromKoulutus.getOpetusmuoto()));
        toKoulutus.setOid(fromKoulutus.getOid());
        toKoulutus.setKoulutuksenAlkamisPvm(fromKoulutus.getKoulutuksenAlkamisPaiva());
        toKoulutus.setSuunniteltuKestoArvo(fromKoulutus.getKesto().getArvo());
        toKoulutus.setSuunniteltuKestoYksikko(fromKoulutus.getKesto().getYksikko());
        toKoulutus.setOpetuskieli(toKoodistoUriSet(fromKoulutus.getOpetuskieli()));
        toKoulutus.setKoulutuslajis(toKoodistoUriSet(fromKoulutus.getKoulutuslaji()));

        for (YhteyshenkiloTyyppi henkiloFrom : fromKoulutus.getYhteyshenkilo()) {

            Yhteyshenkilo henkiloTo = new Yhteyshenkilo();
            copyFields(henkiloFrom, henkiloTo);
            toKoulutus.addYhteyshenkilo(henkiloTo);

        }

        Set<WebLinkki> toLinkkis = new HashSet<WebLinkki>();
        if (fromKoulutus.getLinkki() != null) {
            for (WebLinkkiTyyppi fromLinkki : fromKoulutus.getLinkki()) {
                WebLinkki toLinkki = new WebLinkki(fromLinkki.getTyyppi(), fromLinkki.getKieli(), fromLinkki.getUri());
                toLinkkis.add(toLinkki);
            }
        } // else, set is empty which will clear all previous links
        toKoulutus.setLinkkis(toLinkkis);

    }

    public static void copyFields(YhteyshenkiloTyyppi from, Yhteyshenkilo to) {

        to.setHenkioOid(from.getHenkiloOid());
        to.setEtunimis(from.getEtunimet());
        to.setSukunimi(from.getSukunimi());
        to.setPuhelin(from.getPuhelin());
        to.setSahkoposti(from.getSahkoposti());
        to.setKielis(from.getKielet());
        to.setTitteli(from.getTitteli());

    }

    public static void copyFields(Yhteyshenkilo from, YhteyshenkiloTyyppi to) {

        to.setEtunimet(from.getEtunimis());
        to.setHenkiloOid(from.getHenkioOid());
        to.setPuhelin(from.getPuhelin());
        to.setSahkoposti(from.getSahkoposti());
        to.setSukunimi(from.getSukunimi());
        to.setTitteli(from.getTitteli());

    }

    public static void copyYhteyshenkilos(Collection<Yhteyshenkilo> fromList, Collection<YhteyshenkiloTyyppi> toList) {

        for (Yhteyshenkilo fromHenkilo : fromList) {
            YhteyshenkiloTyyppi toHenkilo = new YhteyshenkiloTyyppi();
            copyFields(fromHenkilo, toHenkilo);
            toList.add(toHenkilo);
        }

    }

    public static Set<String> toStringUriSet(Collection<KoodistoKoodiTyyppi> koodit) {
        Set<String> set = new HashSet<String>();
        for (KoodistoKoodiTyyppi koodi : koodit) {
            set.add(koodi.getUri());
        }
        return set;
    }

    public static Set<KoodistoUri> toKoodistoUriSet(Collection<KoodistoKoodiTyyppi> koodit) {
        Set<KoodistoUri> set = new HashSet<KoodistoUri>();
        for (KoodistoKoodiTyyppi koodi : koodit) {
            KoodistoUri uri = new KoodistoUri(koodi.getUri());
            set.add(uri);
        }
        return set;
    }

    public static void copyKoodistoUris(Collection<KoodistoUri> from, Collection<KoodistoKoodiTyyppi> to) {

        if (from != null) {
            for (KoodistoUri fromUri : from) {
                KoodistoKoodiTyyppi toKoodi = new KoodistoKoodiTyyppi();
                toKoodi.setUri(fromUri.getKoodiUri());
                to.add(toKoodi);
            }
        }

    }

    public static void copyWebLinkkis(Collection<WebLinkki> from, Collection<WebLinkkiTyyppi> to) {

        for (WebLinkki fromLinkki : from) {
            WebLinkkiTyyppi toLinkki = new WebLinkkiTyyppi();
            toLinkki.setKieli(fromLinkki.getKieli());
            toLinkki.setTyyppi(fromLinkki.getTyyppi());
            toLinkki.setUri(fromLinkki.getUrl());
            to.add(toLinkki);
        }

    }

}

