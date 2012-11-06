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
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.TarjontaTila;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.model.WebLinkki;
import fi.vm.sade.tarjonta.model.Yhteyshenkilo;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoulutuksenKestoTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoulutuksenTila;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.MonikielinenTekstiTyyppi;
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

    public static void copyMonikielinenTeksti(MonikielinenTeksti from, List<MonikielinenTekstiTyyppi> to) {
        if (from == null || to == null) {
            return;
        }

        for (TekstiKaannos tekstiKaannos : from.getTekstis()) {
            MonikielinenTekstiTyyppi t = new MonikielinenTekstiTyyppi();
            t.setTeksti(tekstiKaannos.getTeksti());
            t.setTekstinKielikoodi(tekstiKaannos.getKieliKoodi());
            to.add(t);
        }
    }

    private EntityUtils() {
    }

    public static void copyFields(KoulutusmoduuliToteutus from, KoulutusmoduuliToteutus to) {
        to.setNimi(from.getNimi());
        to.setTila(from.getTila());
        to.setMaksullisuus(from.getMaksullisuus());
    }

    public static void copyFields(PaivitaKoulutusTyyppi from, KoulutusmoduuliToteutus to) {
        mapKomotoTila(from, to);
        to.setOpetusmuoto(toKoodistoUriSet(from.getOpetusmuoto()));
        to.setKoulutuksenAlkamisPvm(from.getKoulutuksenAlkamisPaiva());
        to.setKoulutuslajis(toStringUriSet(from.getKoulutuslaji()));

        final KoulutuksenKestoTyyppi kesto = from.getKesto();
        to.setSuunniteltuKesto(kesto.getYksikko(), kesto.getArvo());

        to.setOpetuskieli(toKoodistoUriSet(from.getOpetuskieli()));
        to.setKoulutuslajis(toKoodistoUriSet(from.getKoulutuslaji()));
        if (from.getKoulutusaste() != null) {
            to.setKoulutusaste(from.getKoulutusaste().getUri());
        }
        to.setTarjoaja(from.getTarjoaja());

        Set<WebLinkki> toLinkkis = new HashSet<WebLinkki>();
        if (from.getLinkki() != null) {
            for (WebLinkkiTyyppi fromLinkki : from.getLinkki()) {
                WebLinkki toLinkki = new WebLinkki(fromLinkki.getTyyppi(), fromLinkki.getKieli(), fromLinkki.getUri());
                toLinkkis.add(toLinkki);
            }
        } // else, set is empty which will clear all previous links
        to.setLinkkis(toLinkkis);

        copyLisatiedotFields(from, to);
    }

    public static void copyFields(LisaaKoulutusTyyppi fromKoulutus, KoulutusmoduuliToteutus toKoulutus) {
        mapKomotoTila(fromKoulutus, toKoulutus);

        toKoulutus.setOpetusmuoto(toKoodistoUriSet(fromKoulutus.getOpetusmuoto()));
        toKoulutus.setOid(fromKoulutus.getOid());
        toKoulutus.setKoulutuksenAlkamisPvm(fromKoulutus.getKoulutuksenAlkamisPaiva());
        toKoulutus.setSuunniteltuKesto(fromKoulutus.getKesto().getYksikko(), fromKoulutus.getKesto().getYksikko());
        toKoulutus.setOpetuskieli(toKoodistoUriSet(fromKoulutus.getOpetuskieli()));
        toKoulutus.setKoulutuslajis(toKoodistoUriSet(fromKoulutus.getKoulutuslaji()));
        if (fromKoulutus.getKoulutusaste() != null) {
            toKoulutus.setKoulutusaste(fromKoulutus.getKoulutusaste().getUri());
        }
        toKoulutus.setTarjoaja(fromKoulutus.getTarjoaja());

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

        copyLisatiedotFields(fromKoulutus, toKoulutus);
    }


    /**
     * For copying the textual descriptive language texts and ammattinimike list.
     *
     * @param fromKoulutus
     * @param toKoulutus
     */
    private static void copyLisatiedotFields(KoulutusTyyppi fromKoulutus, KoulutusmoduuliToteutus toKoulutus) {

        //
        // Additional information for Koulutus (koulutuksen lisätiedot)
        //
        Set<KoodistoUri> ammattinimikes = new HashSet<KoodistoUri>();
        if (fromKoulutus.getAmmattinimikkeet() != null) {
            for (KoodistoKoodiTyyppi koodistoKoodiTyyppi : fromKoulutus.getAmmattinimikkeet()) {
                KoodistoUri uri = new KoodistoUri(koodistoKoodiTyyppi.getUri());
                ammattinimikes.add(uri);
            }
        }
        toKoulutus.setAmmattinimikes(ammattinimikes);

        toKoulutus.setKuvailevatTiedot(toMonikielinenTeksti(fromKoulutus.getKuvailevatTiedot(), new MonikielinenTeksti()));
        toKoulutus.setKansainvalistyminen(toMonikielinenTeksti(fromKoulutus.getKansainvalistyminen(), new MonikielinenTeksti()));
        toKoulutus.setSijoittuminenTyoelamaan(toMonikielinenTeksti(fromKoulutus.getSijoittuminenTyoelamaan(), new MonikielinenTeksti()));
        toKoulutus.setSisalto(toMonikielinenTeksti(fromKoulutus.getSisalto(), new MonikielinenTeksti()));
        toKoulutus.setYhteistyoMuidenToimijoidenKanssa(toMonikielinenTeksti(fromKoulutus.getYhteistyoMuidenToimijoidenKanssa(), new MonikielinenTeksti()));
    }



    public static MonikielinenTeksti toMonikielinenTeksti(List<MonikielinenTekstiTyyppi> from, MonikielinenTeksti to) {
        if (to == null || from == null) {
            return null;
        }
        for (MonikielinenTekstiTyyppi monikielinenTekstiTyyppi : from) {
            toMonikielinenTeksti(monikielinenTekstiTyyppi, to);
        }
        return to;
    }

    public static MonikielinenTeksti toMonikielinenTeksti(MonikielinenTekstiTyyppi from, MonikielinenTeksti to) {
        if (to == null || from == null) {
            return null;
        }
        to.addTekstiKaannos(from.getTekstinKielikoodi(), from.getTeksti());
        return to;
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

        for (String kieliUri : from.getKielis()) {
            to.getKielet().add(kieliUri);
        }
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

    private static void mapKomotoTila(KoulutusTyyppi from, KoulutusmoduuliToteutus to) {
        if (from.getKoulutuksenTila().equals(KoulutuksenTila.VALMIS)) {
            to.setTila(TarjontaTila.VALMIS);
        } else {
            to.setTila(TarjontaTila.LUONNOS);
        }
    }
}
