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

import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Kieliaine;
import fi.vm.sade.tarjonta.model.Kielivalikoima;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.MonikielinenMetadata;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.model.WebLinkki;
import fi.vm.sade.tarjonta.model.Yhteyshenkilo;
import fi.vm.sade.tarjonta.service.enums.MetaCategory;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.service.types.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

/**
 *
 */
public final class EntityUtils {

    public static final String STR_ARRAY_SEPARATOR = "|";

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
        return copyFields(from, new MonikielinenTeksti());

    }

    public static MonikielinenTeksti copyFields(MonikielinenTekstiTyyppi source, MonikielinenTeksti target) {

        if (source == null) {
            return null;
        }

        for (Teksti teksti : source.getTeksti()) {
            target.addTekstiKaannos(teksti.getKieliKoodi(), teksti.getValue());
        }

        return target;

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

        copyKielivalikoima(fromKoulutus, toKoulutus);

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

        Set<KoodistoUri> lukiodiplomit = new HashSet<KoodistoUri>();
        if (fromKoulutus.getLukiodiplomit() != null) {
            for (KoodistoKoodiTyyppi koodistoKoodiTyyppi : fromKoulutus.getLukiodiplomit()) {
                KoodistoUri uri = new KoodistoUri(koodistoKoodiTyyppi.getUri());
                lukiodiplomit.add(uri);
            }
        }
        toKoulutus.setLukiodiplomit(lukiodiplomit);

        toKoulutus.setKuvailevatTiedot(copyFields(fromKoulutus.getKuvailevatTiedot()));
        toKoulutus.setKansainvalistyminen(copyFields(fromKoulutus.getKansainvalistyminen()));
        toKoulutus.setSijoittuminenTyoelamaan(copyFields(fromKoulutus.getSijoittuminenTyoelamaan()));
        toKoulutus.setSisalto(copyFields(fromKoulutus.getSisalto()));
        toKoulutus.setYhteistyoMuidenToimijoidenKanssa(copyFields(fromKoulutus.getYhteistyoMuidenToimijoidenKanssa()));
    }

    private static void copyKielivalikoima(KoulutusTyyppi fromKoulutus, KoulutusmoduuliToteutus toKoulutus) {
        copyTarjottuKieli(fromKoulutus.getA1A2Kieli(), toKoulutus, Kieliaine.A1A2KIELI);
        copyTarjottuKieli(fromKoulutus.getB1Kieli(), toKoulutus, Kieliaine.B1KIELI);
        copyTarjottuKieli(fromKoulutus.getB2Kieli(), toKoulutus, Kieliaine.B2KIELI);
        copyTarjottuKieli(fromKoulutus.getB3Kieli(), toKoulutus, Kieliaine.B3KIELI);
        copyTarjottuKieli(fromKoulutus.getMuutKielet(), toKoulutus, Kieliaine.MUUT_KIELET);
    }

    private static void copyTarjottuKieli(List<KoodistoKoodiTyyppi> koodit, KoulutusmoduuliToteutus toKoulutus, Kieliaine aine) {
        if (koodit != null && !koodit.isEmpty()) {
            Kielivalikoima tarjottuKieli = new Kielivalikoima();
            tarjottuKieli.setKey(aine.name());
            for (KoodistoKoodiTyyppi curKoodi : koodit) {
                tarjottuKieli.addKieli(curKoodi.getUri());
            }
            toKoulutus.addTarjottuKieli(tarjottuKieli);
        }
    }

    public static void copyFields(final YhteyshenkiloTyyppi from, Yhteyshenkilo to) {

        to.setHenkioOid(from.getHenkiloOid());
        to.setEtunimis(from.getEtunimet());
        if (from.getSukunimi() == null) {
            to.setSukunimi("");
        } else {
            to.setSukunimi(from.getSukunimi());
        }
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
        tyyppi.setEqfLuokitus(komo.getEqfLuokitus());
        tyyppi.setNqfLuokitus(komo.getNqfLuokitus());
        tyyppi.getOppilaitostyyppi().addAll(splitStringToList(komo.getOppilaitostyyppi()));

        tyyppi.setJatkoOpintoMahdollisuudet(copyFields(komo.getJatkoOpintoMahdollisuudet()));
        tyyppi.setKoulutustyyppi(KoulutusasteTyyppi.fromValue(komo.getKoulutustyyppi()));
        tyyppi.setLukiolinjakoodiUri(komo.getLukiolinja());

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
        tyyppi.getOppilaitostyyppi().addAll(splitStringToList(parentKomo.getOppilaitostyyppi()));
        tyyppi.setEqfLuokitus(parentKomo.getEqfLuokitus());
        tyyppi.setNqfLuokitus(parentKomo.getNqfLuokitus());

        tyyppi.setKoulutuksenRakenne(copyFields(parentKomo.getKoulutuksenRakenne()));
        tyyppi.setTavoitteet(copyFields(komo.getTavoitteet()));
        tyyppi.setTutkinnonTavoitteet(copyFields(parentKomo.getTavoitteet()));
        tyyppi.setJatkoOpintoMahdollisuudet(copyFields(parentKomo.getJatkoOpintoMahdollisuudet()));

        tyyppi.setKoulutustyyppi(KoulutusasteTyyppi.fromValue(parentKomo.getKoulutustyyppi()));
        tyyppi.setLukiolinjakoodiUri(komo.getLukiolinja());

        return tyyppi;
    }

    /*
     * Copy data fields from KoulutusmoduuliKoosteTyyppi to Koulutusmoduuli.
     */
    public static Koulutusmoduuli copyFieldsToKoulutusmoduuli(final KoulutusmoduuliKoosteTyyppi source, final Koulutusmoduuli target) {

        target.setOid(source.getOid());
        target.setKoulutusKoodi(source.getKoulutuskoodiUri());
        target.setKoulutusohjelmaKoodi(source.getKoulutusohjelmakoodiUri());
        target.setLaajuus(source.getLaajuusyksikkoUri(), source.getLaajuusarvoUri());
        target.setTutkintonimike(source.getTutkintonimikeUri());
        target.setUlkoinenTunniste(source.getUlkoinenTunniste());
        target.setKoulutusAste(source.getKoulutusasteUri());
        target.setKoulutusala(source.getKoulutusalaUri());
        target.setOpintoala(source.getOpintoalaUri());
        target.setLukiolinja(source.getLukiolinjakoodiUri());
        target.setEqfLuokitus(source.getEqfLuokitus());
        target.setNqfLuokitus(source.getNqfLuokitus());
        target.setOppilaitostyyppi(joinListToString(source.getOppilaitostyyppi()));

        target.setKoulutuksenRakenne(copyFields(source.getKoulutuksenRakenne(), target.getKoulutuksenRakenne()));
        target.setTavoitteet(copyFields(source.getTavoitteet(), target.getTavoitteet()));
        target.setJatkoOpintoMahdollisuudet(copyFields(source.getJatkoOpintoMahdollisuudet(), target.getJatkoOpintoMahdollisuudet()));

        return target;
    }

    public static Koulutusmoduuli copyFieldsToKoulutusmoduuli(final KoulutusmoduuliKoosteTyyppi tyyppi) {
        Koulutusmoduuli komo = new Koulutusmoduuli(fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi.valueOf(tyyppi.getKoulutusmoduuliTyyppi().value()));
        komo.setOid(tyyppi.getOid());

        if (tyyppi.getKoulutustyyppi() != null) {
            komo.setKoulutustyyppi(tyyppi.getKoulutustyyppi().value());
        }

        //URIs
        komo.setKoulutusKoodi(tyyppi.getKoulutuskoodiUri());
        komo.setKoulutusohjelmaKoodi(tyyppi.getKoulutusohjelmakoodiUri());
        komo.setLaajuus(tyyppi.getLaajuusyksikkoUri(), tyyppi.getLaajuusarvoUri());
        komo.setTutkintonimike(tyyppi.getTutkintonimikeUri());
        komo.setUlkoinenTunniste(tyyppi.getUlkoinenTunniste());
        komo.setKoulutusAste(tyyppi.getKoulutusasteUri());
        komo.setKoulutusala(tyyppi.getKoulutusalaUri());
        komo.setOpintoala(tyyppi.getOpintoalaUri());
        komo.setLukiolinja(tyyppi.getLukiolinjakoodiUri());
        komo.setOppilaitostyyppi(joinListToString(tyyppi.getOppilaitostyyppi()));
        komo.setEqfLuokitus(tyyppi.getEqfLuokitus());
        komo.setNqfLuokitus(tyyppi.getNqfLuokitus());

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

    /**
     * Convert metadata to multilanguage text type.
     *
     * @param from
     * @param categoryKey
     * @return
     */
    public static MonikielinenTekstiTyyppi copyFields(Set<MonikielinenMetadata> from, final MetaCategory categoryKey) {
        if (from == null) {
            return null;
        }

        MonikielinenTekstiTyyppi to = new MonikielinenTekstiTyyppi();
        for (MonikielinenMetadata mtt : getMonikielinenMetadata(from, categoryKey)) {
            Teksti teksti = new Teksti();
            teksti.setValue(mtt.getArvo());
            teksti.setKieliKoodi(mtt.getKieli());
            to.getTeksti().add(teksti);
        }

        return to;
    }

    /**
     * Loop metadata list by a given category key.
     *
     * @param hakukohde
     * @param categoryKey
     * @return
     */
    private static List<MonikielinenMetadata> getMonikielinenMetadata(final Collection<MonikielinenMetadata> metadata, final MetaCategory categoryKey) {
        final List<MonikielinenMetadata> list = getMonikielinenMetadata(metadata).get(categoryKey.toString());
        return Collections.unmodifiableList(list != null ? list : new ArrayList<MonikielinenMetadata>(0));
    }

    private static Map<String, List<MonikielinenMetadata>> getMonikielinenMetadata(final Collection<MonikielinenMetadata> kuvaus) {
        //Map<category,  MonikielinenMetadata>>
        Map<String, List<MonikielinenMetadata>> map = new HashMap<String, List<MonikielinenMetadata>>();

        for (MonikielinenMetadata meta : kuvaus) {
            final String category = meta.getKategoria();

            if (map.containsKey(category)) {
                map.get(category).add(meta);
            } else {
                List<MonikielinenMetadata> list = new ArrayList<MonikielinenMetadata>();
                list.add(meta);
                map.put(category, list);
            }
        }

        return map;
    }

    public static List<KoodistoKoodiTyyppi> copyFields(Set<Kielivalikoima> tarjotutKielet, Kieliaine aine) {
        List<KoodistoKoodiTyyppi> kielet = new ArrayList<KoodistoKoodiTyyppi>();
        for (Kielivalikoima curKielivalikoima : tarjotutKielet) {
            if (curKielivalikoima.getKey().equals(aine.name())) {
                kielet.addAll(createKieliUris(curKielivalikoima.getKielet()));
            }
        }

        return kielet;
    }

    private static List<KoodistoKoodiTyyppi> createKieliUris(Set<KoodistoUri> kieliKoodit) {
        List<KoodistoKoodiTyyppi> kielet = new ArrayList<KoodistoKoodiTyyppi>();
        for (KoodistoUri curUri : kieliKoodit) {
            KoodistoKoodiTyyppi newKieli = new KoodistoKoodiTyyppi();
            newKieli.setUri(curUri.getKoodiUri());
            kielet.add(newKieli);
        }
        return kielet;
    }

    public static String joinListToString(Collection<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        //remove all nulls
        list.removeAll(Collections.singleton(null));

        //return |str1#str2| ...
        return STR_ARRAY_SEPARATOR + StringUtils.join(list, STR_ARRAY_SEPARATOR) + STR_ARRAY_SEPARATOR;
    }

    public static List<String> splitStringToList(String str) {
        if (str == null) {
            return new ArrayList<String>(0);
        }

        return new ArrayList<String>(Arrays.asList(StringUtils.split(str, STR_ARRAY_SEPARATOR)));
    }
}
