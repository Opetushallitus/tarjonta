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
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.model.WebLinkki;
import fi.vm.sade.tarjonta.model.Yhteyshenkilo;
import fi.vm.sade.tarjonta.service.GenericFault;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.service.types.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public final class EntityUtils {

    /**
     * Copies all text translations from domain model type into web service
     * type. If either of the parameters is null, returns null. Otherwise
     * <code>to</code> is returned to allow method chaining.
     *
     * @param from source of the copying
     * @param to target of the copying
     * @return
     */
    public static MonikielinenTekstiTyyppi copyFields(MonikielinenTeksti from) {
        if (from == null) {
            return null;
        }

        MonikielinenTekstiTyyppi to = new MonikielinenTekstiTyyppi();

        for (TekstiKaannos tekstiKaannos : from.getTekstis()) {
            Teksti teksti = new Teksti();
            teksti.setValue(tekstiKaannos.getArvo());
            teksti.setKieliKoodi(tekstiKaannos.getKieliKoodi());
            to.getTeksti().add(teksti);
        }

        return to;
    }

    /**
     * Copies all text translations from web service model into domain model
     * type. If either of the parameters is null, null is returned. Otherwise
     * <code>to</code> is returned to allow method chaining.
     *
     * @param from source of the copying
     * @param to target of the copying
     * @return
     */
    public static MonikielinenTeksti copyFields(MonikielinenTekstiTyyppi from) {

        if (from == null) {
            return null;
        }

        MonikielinenTeksti to = new MonikielinenTeksti();

        for (Teksti teksti : from.getTeksti()) {
            to.addTekstiKaannos(teksti.getKieliKoodi(), teksti.getValue());
        }

        return to;

    }

    private EntityUtils() {
    }

    public static void copyFields(PaivitaKoulutusTyyppi from, KoulutusmoduuliToteutus to) {

        to.setTila(convertTila(from.getTila()));
        to.setOpetusmuoto(toKoodistoUriSet(from.getOpetusmuoto()));
        to.setKoulutuksenAlkamisPvm(from.getKoulutuksenAlkamisPaiva());
        to.setKoulutuslajis(toStringUriSet(from.getKoulutuslaji()));

        final KoulutuksenKestoTyyppi kesto = from.getKesto();
        to.setSuunniteltuKesto(kesto.getYksikko(), kesto.getArvo());

        to.setOpetuskieli(toKoodistoUriSet(from.getOpetuskieli()));
        to.setKoulutuslajis(toKoodistoUriSet(from.getKoulutuslaji()));
        to.setPainotus(copyFields(from.getPainotus()));

        if (from.getKoulutusaste() != null) {
            to.setKoulutusaste(from.getKoulutusaste().getUri());
        }

        if (from.getPohjakoulutusvaatimus() != null) {
            to.setPohjakoulutusvaatimus(from.getPohjakoulutusvaatimus().getUri());
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
        
        HashSet<Yhteyshenkilo> yhteyshenkilos = new HashSet<Yhteyshenkilo>();
        if (!from.getYhteyshenkiloTyyppi().isEmpty()) {
            Yhteyshenkilo newYhteyshenkilo = new Yhteyshenkilo();
            copyFields(from.getYhteyshenkiloTyyppi().get(0), newYhteyshenkilo);
            yhteyshenkilos.add(newYhteyshenkilo);
        } 
        to.setYhteyshenkilos(yhteyshenkilos);

        copyLisatiedotFields(from, to);
    }

    public static void copyFields(LisaaKoulutusTyyppi fromKoulutus, KoulutusmoduuliToteutus toKoulutus) {

        toKoulutus.setTila(convertTila(fromKoulutus.getTila()));
        toKoulutus.setOpetusmuoto(toKoodistoUriSet(fromKoulutus.getOpetusmuoto()));
        toKoulutus.setOid(fromKoulutus.getOid());
        toKoulutus.setKoulutuksenAlkamisPvm(fromKoulutus.getKoulutuksenAlkamisPaiva());
        if (fromKoulutus.getKesto() != null) {
            toKoulutus.setSuunniteltuKesto(fromKoulutus.getKesto().getYksikko(), fromKoulutus.getKesto().getArvo());
        }
        toKoulutus.setOpetuskieli(toKoodistoUriSet(fromKoulutus.getOpetuskieli()));
        toKoulutus.setKoulutuslajis(toKoodistoUriSet(fromKoulutus.getKoulutuslaji()));
        toKoulutus.setTarjoaja(fromKoulutus.getTarjoaja());
        toKoulutus.setPainotus(copyFields(fromKoulutus.getPainotus()));

        copyLisatiedotFields(fromKoulutus, toKoulutus);

        if (fromKoulutus.getKoulutusaste() != null) {
            toKoulutus.setKoulutusaste(fromKoulutus.getKoulutusaste().getUri());
        }

        if (fromKoulutus.getPohjakoulutusvaatimus() != null) {
            toKoulutus.setPohjakoulutusvaatimus(fromKoulutus.getPohjakoulutusvaatimus().getUri());
        }

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

    /**
     * For copying the textual descriptive language texts and ammattinimike
     * list.
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

        toKoulutus.setKuvailevatTiedot(copyFields(fromKoulutus.getKuvailevatTiedot()));
        toKoulutus.setKansainvalistyminen(copyFields(fromKoulutus.getKansainvalistyminen()));
        toKoulutus.setSijoittuminenTyoelamaan(copyFields(fromKoulutus.getSijoittuminenTyoelamaan()));
        toKoulutus.setSisalto(copyFields(fromKoulutus.getSisalto()));
        toKoulutus.setYhteistyoMuidenToimijoidenKanssa(copyFields(fromKoulutus.getYhteistyoMuidenToimijoidenKanssa()));
    }

    public static void copyFields(final YhteyshenkiloTyyppi from, Yhteyshenkilo to) {

        to.setHenkioOid(from.getHenkiloOid());
        to.setEtunimis(from.getEtunimet());
        to.setSukunimi(from.getSukunimi());
        to.setPuhelin(from.getPuhelin());
        to.setSahkoposti(from.getSahkoposti());
        to.setKielis(from.getKielet());
        to.setTitteli(from.getTitteli());

    }

    public static void copyFields(final Yhteyshenkilo from, YhteyshenkiloTyyppi to) {

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

    public static KoulutusmoduuliKoosteTyyppi copyFieldsToKoulutusmoduuliKoosteTyyppi(final Koulutusmoduuli komo) {
        KoulutusmoduuliKoosteTyyppi tyyppi = new KoulutusmoduuliKoosteTyyppi();
        tyyppi.setOid(komo.getOid());
        tyyppi.setKoulutuskoodiUri(komo.getKoulutusKoodi());
        tyyppi.setKoulutusohjelmakoodiUri(komo.getKoulutusohjelmaKoodi());
        tyyppi.setLaajuusarvoUri(komo.getLaajuusArvo());
        tyyppi.setLaajuusyksikkoUri(komo.getLaajuusYksikko());
        tyyppi.setTutkintonimikeUri(komo.getTutkintonimike());
        tyyppi.setUlkoinenTunniste(komo.getUlkoinenTunniste());
        tyyppi.setKoulutusasteUri(komo.getKoulutusAste());
        tyyppi.setKoulutusalaUri(komo.getKoulutusala());
        tyyppi.setOpintoalaUri(komo.getOpintoala());

        tyyppi.setKoulutuksenRakenne(copyFields(komo.getKoulutuksenRakenne()));
        tyyppi.setTavoitteet(copyFields(komo.getTavoitteet()));
        tyyppi.setJatkoOpintoMahdollisuudet(copyFields(komo.getJatkoOpintoMahdollisuudet()));

        return tyyppi;
    }
    
    /*
     * Merging fields from parent and komo to create the result KoulutusmoduuliKoosteTyyppi.
     */
    public static KoulutusmoduuliKoosteTyyppi copyFieldsToKoulutusmoduuliKoosteTyyppi(final Koulutusmoduuli komo, final Koulutusmoduuli parentKomo) {
        KoulutusmoduuliKoosteTyyppi tyyppi = new KoulutusmoduuliKoosteTyyppi();
        tyyppi.setOid(komo.getOid());
        tyyppi.setParentOid(parentKomo.getOid());
        tyyppi.setKoulutuskoodiUri(parentKomo.getKoulutusKoodi());
        tyyppi.setKoulutusohjelmakoodiUri(komo.getKoulutusohjelmaKoodi());
        tyyppi.setLaajuusarvoUri(parentKomo.getLaajuusArvo());
        tyyppi.setLaajuusyksikkoUri(parentKomo.getLaajuusYksikko());
        tyyppi.setTutkintonimikeUri(komo.getTutkintonimike());
        tyyppi.setUlkoinenTunniste(komo.getUlkoinenTunniste());
        tyyppi.setKoulutusasteUri(parentKomo.getKoulutusAste());
        tyyppi.setKoulutusalaUri(parentKomo.getKoulutusala());
        tyyppi.setOpintoalaUri(parentKomo.getOpintoala());

        tyyppi.setKoulutuksenRakenne(copyFields(parentKomo.getKoulutuksenRakenne()));
        tyyppi.setTavoitteet(copyFields(komo.getTavoitteet()));
        tyyppi.setTutkinnonTavoitteet(copyFields(parentKomo.getTavoitteet()));
        tyyppi.setJatkoOpintoMahdollisuudet(copyFields(parentKomo.getJatkoOpintoMahdollisuudet()));

        return tyyppi;
    }

    public static Koulutusmoduuli copyFieldsToKoulutusmoduuli(final KoulutusmoduuliKoosteTyyppi tyyppi) {
        Koulutusmoduuli komo = new Koulutusmoduuli(fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi.valueOf(tyyppi.getKoulutusmoduuliTyyppi().value()));
        komo.setOid(tyyppi.getOid());

        //URIs
        komo.setKoulutusKoodi(tyyppi.getKoulutuskoodiUri());
        komo.setKoulutusohjelmaKoodi(tyyppi.getKoulutusohjelmakoodiUri());
        komo.setLaajuus(tyyppi.getLaajuusyksikkoUri(), tyyppi.getLaajuusarvoUri());
        komo.setTutkintonimike(tyyppi.getTutkintonimikeUri());
        komo.setUlkoinenTunniste(tyyppi.getUlkoinenTunniste());
        komo.setKoulutusAste(tyyppi.getKoulutusasteUri());
        komo.setKoulutusala(tyyppi.getKoulutusalaUri());
        komo.setOpintoala(tyyppi.getOpintoalaUri());

        //multilanguage objects
        komo.setKoulutuksenRakenne(copyFields(tyyppi.getKoulutuksenRakenne()));
        komo.setTavoitteet(copyFields(tyyppi.getTavoitteet()));
        komo.setJatkoOpintoMahdollisuudet(copyFields(tyyppi.getJatkoOpintoMahdollisuudet()));

        //names for KOMOTO search 
        komo.setNimi(copyFields(tyyppi.getKoulutusmoduulinNimi()));

        return komo;
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

    /**
     * Converts TarjontaTila from web service type to domain model type.
     *
     * @param tila
     * @return
     */
    public static fi.vm.sade.tarjonta.model.TarjontaTila convertTila(fi.vm.sade.tarjonta.service.types.TarjontaTila tila) {

        return fi.vm.sade.tarjonta.model.TarjontaTila.valueOf(tila.name());

    }

    /**
     * Converts TarjontaTila from domain model type to web service type.
     *
     * @param tila
     * @return
     */
    public static fi.vm.sade.tarjonta.service.types.TarjontaTila convertTila(fi.vm.sade.tarjonta.model.TarjontaTila tila) {

        return fi.vm.sade.tarjonta.service.types.TarjontaTila.valueOf(tila.name());

    }
}
