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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.vm.sade.tarjonta.service.types.*;
import org.apache.commons.lang.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.model.BaseKoulutusmoduuli;

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
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.service.enums.MetaCategory;
import fi.vm.sade.tarjonta.service.impl.conversion.CommonFromDTOConverter;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang.time.DateUtils;

/**
 *
 */
public final class EntityUtils {

    private EntityUtils() {
    }
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
    public static <T extends MonikielinenTekstiTyyppi> MonikielinenTekstiTyyppi copyFields(MonikielinenTeksti from, T to) {
        if (from == null) {
            return null;
        }

        for (TekstiKaannos tekstiKaannos : from.getTekstiKaannos()) {
            Teksti teksti = new Teksti();
            teksti.setValue(tekstiKaannos.getArvo());
            teksti.setKieliKoodi(tekstiKaannos.getKieliKoodi());
            to.getTeksti().add(teksti);
        }

        return to;
    }

    public static MonikielinenTekstiTyyppi copyFields(MonikielinenTeksti from) {
        return copyFields(from, new MonikielinenTekstiTyyppi());
    }

    public static MonikielinenTeksti copyFields(MonikielinenTekstiTyyppi source, MonikielinenTeksti target) {
        return MonikielinenTeksti.merge(target, CommonFromDTOConverter.convertMonikielinenTekstiTyyppiToDomainValue(source));
    }

    public static void copyFields(Map<KomoTeksti, MonikielinenTeksti> dst, List<NimettyMonikielinenTekstiTyyppi> src, KomoTeksti... keys) {
        copyFields(dst, src, new Function<String, KomoTeksti>() {
            public KomoTeksti apply(String input) {
                return KomoTeksti.valueOf(input);
            }
        }, keys.length == 0 ? KomoTeksti.values() : keys);
    }

    public static void copyFields(Map<KomotoTeksti, MonikielinenTeksti> dst, List<NimettyMonikielinenTekstiTyyppi> src, KomotoTeksti... keys) {
        copyFields(dst, src, new Function<String, KomotoTeksti>() {
            public KomotoTeksti apply(String input) {
                return KomotoTeksti.valueOf(input);
            }
        }, keys.length == 0 ? KomotoTeksti.values() : keys);
    }

    public static <T> void copyFields(Map<T, MonikielinenTeksti> dst, List<NimettyMonikielinenTekstiTyyppi> src, Function<String, T> keyResolver, T... keys) {
        Set<T> kenums = new HashSet<T>(Arrays.asList(keys));
        for (NimettyMonikielinenTekstiTyyppi nmkt : src) {
            T key = keyResolver.apply(nmkt.getTunniste());
            if (kenums.contains(key)) {
                dst.put(key, copyFields(nmkt, dst.get(key)));
            }
        }
    }

    public static <T> void copyFields(List<NimettyMonikielinenTekstiTyyppi> dst, Map<T, MonikielinenTeksti> src, T... keys) {
        Set<T> kenums = new HashSet<T>(Arrays.asList(keys));
        for (Map.Entry<T, MonikielinenTeksti> e : src.entrySet()) {
            if (kenums.isEmpty() || kenums.contains(e.getKey())) {
                List<MonikielinenTekstiTyyppi.Teksti> txts = new ArrayList<MonikielinenTekstiTyyppi.Teksti>();
                for (TekstiKaannos tk : e.getValue().getTekstiKaannos()) {
                    txts.add(new MonikielinenTekstiTyyppi.Teksti(tk.getArvo(), tk.getKieliKoodi()));
                }
                dst.add(new NimettyMonikielinenTekstiTyyppi(txts, e.getKey().toString()));
            }
        }
    }

    public static void copyFields(PaivitaKoulutusTyyppi from, KoulutusmoduuliToteutus to) {

        to.setVersion(from.getVersion()); //optimistic locking
        to.setTila(convertTila(from.getTila()));
        to.setOpetusmuoto(toKoodistoUriSet(from.getOpetusmuoto()));
        to.setKoulutuksenAlkamisPvm(from.getKoulutuksenAlkamisPaiva());
        to.setKoulutuslajis(toStringUriSet(from.getKoulutuslaji()));
        to.setKkPohjakoulutusvaatimus(toKoodistoUriSet(from.getPohjakoulutusvaatimusKorkeakoulu()));

        final KoulutuksenKestoTyyppi kesto = from.getKesto();
        to.setSuunniteltuKesto(kesto.getYksikko(), kesto.getArvo());
        if (from.getLaajuus() != null) {
            to.setOpintojenLaajuusArvo(from.getLaajuus().getArvo());
            to.setOpintojenLaajuusyksikkoUri(from.getLaajuus().getYksikko());
        }

        to.setOpetuskieli(toKoodistoUriSet(from.getOpetuskieli()));
        copyFields(to.getTekstit(), from.getTekstit(), KomotoTeksti.PAINOTUS);
        //to.setPainotus(copyFields(from.getPainotus(), to.getPainotus()));
        to.setTeemas(toKoodistoUriSet(from.getTeemat()));
        if (from.getHinta() != null) {
            to.setHinta(new BigDecimal(from.getHinta()));
        }

        if (from.getPohjakoulutusvaatimus() != null) {
            to.setPohjakoulutusvaatimusUri(from.getPohjakoulutusvaatimus().getUri());
        }

        to.setTarjoaja(from.getTarjoaja());

        Set<WebLinkki> toLinkkis = Sets.<WebLinkki>newHashSet();
        if (from.getLinkki() != null) {
            for (WebLinkkiTyyppi fromLinkki : from.getLinkki()) {
                WebLinkki toLinkki = new WebLinkki(fromLinkki.getTyyppi(), fromLinkki.getKieli(), fromLinkki.getUri());
                toLinkkis.add(toLinkki);
            }
        } // else, set is empty which will clear all previous links
        to.setLinkkis(toLinkkis);

        Set<Yhteyshenkilo> yhteyshenkilos = Sets.<Yhteyshenkilo>newHashSet();
        if (!from.getYhteyshenkiloTyyppi().isEmpty()) {
            for (YhteyshenkiloTyyppi tyyppi : from.getYhteyshenkiloTyyppi()) {
                Yhteyshenkilo newYhteyshenkilo = new Yhteyshenkilo();
                copyFields(tyyppi, newYhteyshenkilo);
                yhteyshenkilos.add(newYhteyshenkilo);
            }
        }
        to.setYhteyshenkilos(yhteyshenkilos);
        to.setLastUpdatedByOid(from.getViimeisinPaivittajaOid());

        copyNimi(from.getNimi(), to);
        copyLisatiedotFields(from, to);

        //override data with the latest koodisto uris
        copyKomoRelationsToKomotoEntity(from.getKoulutusmoduuli(), to);
    }

    private static void copyNimi(MonikielinenTekstiTyyppi nimi,
            KoulutusmoduuliToteutus to) {
        if (nimi != null
                && nimi.getTeksti().size() > 0) {
            final Teksti teksti = nimi.getTeksti().get(0);
            to.setNimi(new MonikielinenTeksti(teksti.getKieliKoodi(), teksti.getValue()));
        }
    }

    public static void copyFields(LisaaKoulutusTyyppi fromKoulutus, KoulutusmoduuliToteutus toKoulutus) {

        toKoulutus.setTila(convertTila(fromKoulutus.getTila()));
        toKoulutus.setOpetusmuoto(toKoodistoUriSet(fromKoulutus.getOpetusmuoto()));
        toKoulutus.setOid(fromKoulutus.getOid());
        toKoulutus.setKoulutuksenAlkamisPvm(fromKoulutus.getKoulutuksenAlkamisPaiva());
        if (fromKoulutus.getKesto() != null) {
            toKoulutus.setSuunniteltuKesto(fromKoulutus.getKesto().getYksikko(), fromKoulutus.getKesto().getArvo());
        }
        if (fromKoulutus.getLaajuus() != null) {
            toKoulutus.setOpintojenLaajuusArvo(fromKoulutus.getLaajuus().getArvo());
            toKoulutus.setOpintojenLaajuusyksikkoUri(fromKoulutus.getLaajuus().getYksikko());
        }
        toKoulutus.setOpetuskieli(toKoodistoUriSet(fromKoulutus.getOpetuskieli()));
        toKoulutus.setKoulutuslajis(toKoodistoUriSet(fromKoulutus.getKoulutuslaji()));
        toKoulutus.setTarjoaja(fromKoulutus.getTarjoaja());

        copyFields(toKoulutus.getTekstit(), fromKoulutus.getTekstit(), KomotoTeksti.PAINOTUS);
        //toKoulutus.setPainotus(copyFields(fromKoulutus.getPainotus(), toKoulutus.getPainotus()));
        toKoulutus.setTeemas(toKoodistoUriSet(fromKoulutus.getTeemat()));
        toKoulutus.setKkPohjakoulutusvaatimus(toKoodistoUriSet(fromKoulutus.getPohjakoulutusvaatimusKorkeakoulu()));

        if (fromKoulutus.getHinta() != null) {
            toKoulutus.setHinta(new BigDecimal(fromKoulutus.getHinta()));
        }

        copyLisatiedotFields(fromKoulutus, toKoulutus);

        if (fromKoulutus.getKoulutusaste() != null) {
            toKoulutus.setKoulutusasteUri(fromKoulutus.getKoulutusaste().getUri());
        }

        if (fromKoulutus.getPohjakoulutusvaatimus() != null) {
            toKoulutus.setPohjakoulutusvaatimusUri(fromKoulutus.getPohjakoulutusvaatimus().getUri());
        }

        for (YhteyshenkiloTyyppi henkiloFrom : fromKoulutus.getYhteyshenkiloTyyppi()) {
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

        toKoulutus.setLastUpdatedByOid(fromKoulutus.getViimeisinPaivittajaOid());

        copyNimi(fromKoulutus.getNimi(), toKoulutus);

        //override data with the latest koodisto uris
        copyKomoRelationsToKomotoEntity(fromKoulutus.getKoulutusmoduuli(), toKoulutus);
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

        copyFields(toKoulutus.getTekstit(), fromKoulutus.getTekstit());

        /*toKoulutus.setKuvailevatTiedot(copyFields(fromKoulutus.getKuvailevatTiedot(), toKoulutus.getKuvailevatTiedot()));
         toKoulutus.setKansainvalistyminen(copyFields(fromKoulutus.getKansainvalistyminen(), toKoulutus.getKansainvalistyminen()));
         toKoulutus.setSijoittuminenTyoelamaan(copyFields(fromKoulutus.getSijoittuminenTyoelamaan(), toKoulutus.getSijoittuminenTyoelamaan()));
         toKoulutus.setSisalto(copyFields(fromKoulutus.getSisalto(), toKoulutus.getSisalto()));
         toKoulutus.setYhteistyoMuidenToimijoidenKanssa(copyFields(fromKoulutus.getYhteistyoMuidenToimijoidenKanssa(), toKoulutus.getYhteistyoMuidenToimijoidenKanssa()));
         */
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
            Set<String> lcs = new HashSet<String>();
            for (KoodistoKoodiTyyppi curKoodi : koodit) {
                lcs.add(curKoodi.getUri());
            }
            toKoulutus.setKieliValikoima(aine.name(), lcs);
        } else {
            toKoulutus.setKieliValikoima(aine.name(), null);
        }
    }

    public static void copyFields(final YhteyshenkiloTyyppi from, Yhteyshenkilo to) {

        to.setHenkioOid(from.getHenkiloOid());
        if (from.getNimi() == null) {
            to.setNimi("");
        } else {
            to.setNimi(from.getNimi());
        }
        to.setPuhelin(from.getPuhelin());
        to.setSahkoposti(from.getSahkoposti());
        to.setMultipleKielisByList(from.getKielet());
        to.setTitteli(from.getTitteli());
        to.setHenkiloTyyppi(
                from.getHenkiloTyyppi() != null
                    ? from.getHenkiloTyyppi()
                    : HenkiloTyyppi.YHTEYSHENKILO
        );
    }

    public static void copyFields(final Yhteyshenkilo from, YhteyshenkiloTyyppi to) {

        to.setNimi(from.getNimi());
        to.setHenkiloOid(from.getHenkioOid());
        to.setPuhelin(from.getPuhelin());
        to.setSahkoposti(from.getSahkoposti());
        to.setTitteli(from.getTitteli());
        to.setHenkiloTyyppi(from.getHenkiloTyyppi());

//        for (String kieliUri : from.getMultipleKielis()) {
//            to.getKielet().add(kieliUri);
//        }
    }

    /*
     * Simple KOMO data converter, no description data.
     */
    public static KoulutusmoduuliKoosteTyyppi copyFieldsToKoulutusmoduuliKoosteTyyppiSimple(final Koulutusmoduuli komo) {
        Preconditions.checkNotNull(komo, "Koulutusmoduuli object cannot be null.");
        Preconditions.checkNotNull(komo.getModuuliTyyppi(), "KoulutusmoduuliTyyppi enum cannot be null.");
        Preconditions.checkNotNull(komo.getKoulutustyyppiEnum(), "Koulutusaste string object cannot be null.");
        Preconditions.checkNotNull(komo.getKoulutusUri(), "Koulutuskoodi URI cannot be null.");
        KoulutusmoduuliKoosteTyyppi tyyppi = new KoulutusmoduuliKoosteTyyppi();

        /*
         * Required type data:
         */
        tyyppi.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.valueOf(komo.getModuuliTyyppi().name()));
        tyyppi.setKoulutustyyppi(komo.getKoulutustyyppiEnum().getKoulutusasteTyyppi());

        /*
         * OID and other keys:
         * No parent OID convesion.
         */
        tyyppi.setOid(komo.getOid());
        tyyppi.setKoulutuskoodiUri(komo.getKoulutusUri());
        tyyppi.setKoulutusohjelmakoodiUri(komo.getKoulutusohjelmaUri());
        tyyppi.setLukiolinjakoodiUri(komo.getLukiolinjaUri());

        /*
         * Optional data
         */
        tyyppi.setLaajuusarvoUri(komo.getOpintojenLaajuusarvoUri());
        tyyppi.setLaajuusyksikkoUri(komo.getOpintojenLaajuusyksikkoUri());
        tyyppi.setTutkintonimikeUri(komo.getTutkintonimikeUri());
        tyyppi.setUlkoinenTunniste(komo.getUlkoinenTunniste());
        tyyppi.setKoulutusasteUri(komo.getKoulutusasteUri());
        tyyppi.setKoulutusalaUri(komo.getKoulutusalaUri());
        tyyppi.setOpintoalaUri(komo.getOpintoalaUri());
        tyyppi.getOppilaitostyyppi().addAll(splitStringToList(komo.getOppilaitostyyppi()));
        tyyppi.setEqfLuokitus(komo.getEqfUri());
        tyyppi.setNqfLuokitus(komo.getNqfUri());
        tyyppi.getOppilaitostyyppi().addAll(splitStringToList(komo.getOppilaitostyyppi()));

        copyFields(tyyppi.getTekstit(), komo.getTekstit(), KomoTeksti.KOULUTUKSEN_RAKENNE, KomoTeksti.TAVOITTEET); // rajaus turha?
        //tyyppi.setKoulutuksenRakenne(copyFields(komo.getKoulutuksenRakenne()));
        //tyyppi.setTavoitteet(copyFields(komo.getTavoitteet()));
        tyyppi.setTutkinnonTavoitteet(copyFields(komo.getTekstit().get(KomoTeksti.TAVOITTEET)));
        return tyyppi;
    }

    public static KoulutusmoduuliKoosteTyyppi copyFieldsToKoulutusmoduuliKoosteTyyppi(final Koulutusmoduuli komo) {
        KoulutusmoduuliKoosteTyyppi tyyppi = copyFieldsToKoulutusmoduuliKoosteTyyppiSimple(komo);

        /*
         * Descriptions
         */
        copyFields(tyyppi.getTekstit(), komo.getTekstit(), KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET, KomoTeksti.KOULUTUKSEN_RAKENNE);

        //tyyppi.setJatkoOpintoMahdollisuudet(copyFields(komo.getJatkoOpintoMahdollisuudet()));
        //tyyppi.setKoulutuksenRakenne(copyFields(komo.getKoulutuksenRakenne()));
        switch (komo.getModuuliTyyppi()) {
            case TUTKINTO:
                //parent KOMO: tutkinnon-tavoitteet
                tyyppi.setTutkinnonTavoitteet(copyFields(komo.getTekstit().get(KomoTeksti.TAVOITTEET)));
                //tyyppi.setTutkinnonTavoitteet(copyFields(komo.getTavoitteet()));
                break;
            case TUTKINTO_OHJELMA:
                //ammatilliset-tavoitteet
                copyFields(tyyppi.getTekstit(), komo.getTekstit(), KomoTeksti.TAVOITTEET);
                //tyyppi.setTavoitteet(copyFields(komo.getTavoitteet()));
                break;
        }

        //names for KOMOTO search 
        tyyppi.setKoulutusmoduulinNimi(copyFields(komo.getNimi()));

        return tyyppi;
    }

    /*
     * Merge parent data to child KOMO, return compined KoulutusmoduuliKoosteTyyppi object.
     */
    public static KoulutusmoduuliKoosteTyyppi copyFieldsToKoulutusmoduuliKoosteTyyppi(final Koulutusmoduuli komo, final Koulutusmoduuli parentKomo) {
        Preconditions.checkNotNull(komo, "Koulutusmoduuli child object cannot be null.");
        Preconditions.checkNotNull(parentKomo, "Koulutusmoduuli parent object cannot be null.");
        Preconditions.checkNotNull(komo.getModuuliTyyppi(), "KoulutusmoduuliTyyppi enum cannot be null.");
        Preconditions.checkNotNull(komo.getKoulutustyyppiEnum(), "Koulutusaste string object cannot be null.");
        Preconditions.checkNotNull(komo.getKoulutusUri(), "Koulutuskoodi URI cannot be null.");
        KoulutusmoduuliKoosteTyyppi tyyppi = new KoulutusmoduuliKoosteTyyppi();

        //tyyppi.setVersion(1L); //TODO: missing.
        /*
         * Required type data:
         */
        tyyppi.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.valueOf(komo.getModuuliTyyppi().name()));
        tyyppi.setKoulutustyyppi(parentKomo.getKoulutustyyppiEnum().getKoulutusasteTyyppi());
        /*
         * OID and other keys:
         */
        tyyppi.setOid(komo.getOid());
        tyyppi.setParentOid(parentKomo.getOid());
        tyyppi.setKoulutuskoodiUri(parentKomo.getKoulutusUri());
        tyyppi.setKoulutusohjelmakoodiUri(komo.getKoulutusohjelmaUri());
        tyyppi.setLukiolinjakoodiUri(komo.getLukiolinjaUri());

        /*
         * Optional data
         */
        tyyppi.setLaajuusarvoUri(komo.getOpintojenLaajuusarvoUri() != null && !komo.getOpintojenLaajuusarvoUri().isEmpty() ? komo.getOpintojenLaajuusarvoUri() : parentKomo.getOpintojenLaajuusarvoUri());
        tyyppi.setLaajuusyksikkoUri(parentKomo.getOpintojenLaajuusyksikkoUri());
        tyyppi.setTutkintonimikeUri(komo.getTutkintonimikeUri());
        tyyppi.setUlkoinenTunniste(komo.getUlkoinenTunniste());
        tyyppi.setKoulutusasteUri(parentKomo.getKoulutusasteUri());
        tyyppi.setKoulutusalaUri(parentKomo.getKoulutusalaUri());
        tyyppi.setOpintoalaUri(parentKomo.getOpintoalaUri());
        tyyppi.setEqfLuokitus(parentKomo.getEqfUri());
        tyyppi.setNqfLuokitus(parentKomo.getNqfUri());
        tyyppi.getOppilaitostyyppi().addAll(splitStringToList(parentKomo.getOppilaitostyyppi()));
        /*
         * Description data
         */

        tyyppi.setTutkinnonTavoitteet(copyFields(parentKomo.getTekstit().get(KomoTeksti.TAVOITTEET)));
        copyFields(tyyppi.getTekstit(), parentKomo.getTekstit(), KomoTeksti.KOULUTUKSEN_RAKENNE, KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET);
        copyFields(tyyppi.getTekstit(), komo.getTekstit(), KomoTeksti.TAVOITTEET);

        //tyyppi.setTutkinnonTavoitteet(copyFields(parentKomo.getTavoitteet())); //parent KOMO: tutkinnon-tavoitteet
        //tyyppi.setKoulutuksenRakenne(copyFields(parentKomo.getKoulutuksenRakenne()));
        //tyyppi.setTavoitteet(copyFields(komo.getTavoitteet())); //child KOMO: ammatilliset-tavoitteet
        //tyyppi.setJatkoOpintoMahdollisuudet(copyFields(parentKomo.getJatkoOpintoMahdollisuudet())); //parent KOMO: jatko-opintomahdollisuudet
        return tyyppi;
    }

    public static Koulutusmoduuli copyFieldsToKoulutusmoduuliSimple(final KoulutusmoduuliKoosteTyyppi source, final Koulutusmoduuli target) {
        Preconditions.checkNotNull(source, "KoulutusmoduuliKoosteTyyppi object cannot be null.");
        Preconditions.checkNotNull(target, "Koulutusmoduuli object cannot be null.");
        Preconditions.checkNotNull(source.getKoulutusmoduuliTyyppi(), "KoulutusmoduuliTyyppi enum cannot be null.");
        Preconditions.checkNotNull(source.getKoulutustyyppi(), "KoulutusasteTyyppi enum cannot be null.");
        Preconditions.checkNotNull(source.getKoulutuskoodiUri(), "Koulutuskoodi URI cannot be null.");

        target.setVersion(1L); //TODO fix this

        /*
         * Required type data:
         */
        target.setModuuliTyyppi(fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi.valueOf(source.getKoulutusmoduuliTyyppi().value()));
        target.setKoulutustyyppiEnum(ModuulityyppiEnum.fromEnum(source.getKoulutustyyppi()));
        /*
         * OID and other keys:
         */
        target.setOid(source.getOid());
        target.setKoulutusUri(source.getKoulutuskoodiUri());
        target.setKoulutusohjelmaUri(source.getKoulutusohjelmakoodiUri());
        target.setLukiolinjaUri(source.getLukiolinjakoodiUri());

        /*
         * Optional data
         */
        target.setOpintojenLaajuus(source.getLaajuusyksikkoUri(), source.getLaajuusarvoUri());
        target.setTutkintonimikeUri(source.getTutkintonimikeUri());
        target.setUlkoinenTunniste(source.getUlkoinenTunniste());
        target.setKoulutusasteUri(source.getKoulutusasteUri());
        target.setKoulutusalaUri(source.getKoulutusalaUri());
        target.setOpintoalaUri(source.getOpintoalaUri());
        target.setLukiolinjaUri(source.getLukiolinjakoodiUri());
        target.setEqfUri(source.getEqfLuokitus());
        target.setNqfUri(source.getNqfLuokitus());
        target.setOppilaitostyyppi(joinListToString(source.getOppilaitostyyppi()));
        target.setNimi(copyFields(source.getNimi(), target.getNimi()));
        target.setKoulutustyyppiEnum(ModuulityyppiEnum.fromEnum(source.getKoulutustyyppi()));

        return target;
    }

    /*
     * Copy data fields from KoulutusmoduuliKoosteTyyppi to Koulutusmoduuli.
     */
    public static Koulutusmoduuli copyFieldsToKoulutusmoduuli(final KoulutusmoduuliKoosteTyyppi source, final Koulutusmoduuli target) {
        copyFieldsToKoulutusmoduuliSimple(source, target);

        //multilanguage objects
        copyFields(target.getTekstit(), source.getTekstit(), KomoTeksti.KOULUTUKSEN_RAKENNE, KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET);
        //target.setKoulutuksenRakenne(copyFields(source.getKoulutuksenRakenne(), target.getKoulutuksenRakenne()));
        //target.setJatkoOpintoMahdollisuudet(copyFields(source.getJatkoOpintoMahdollisuudet(), target.getJatkoOpintoMahdollisuudet()));

        switch (source.getKoulutusmoduuliTyyppi()) {
            case TUTKINTO:
                MonikielinenTeksti.merge(target.getTekstit(), KomoTeksti.TAVOITTEET, copyFields(source.getTutkinnonTavoitteet(), target.getTekstit().get(KomoTeksti.TAVOITTEET)));
                //target.setTavoitteet(copyFields(source.getTutkinnonTavoitteet(), target.getTavoitteet())); //parent KOMO: tutkinnon-tavoitteet
                break;
            case TUTKINTO_OHJELMA:
                copyFields(target.getTekstit(), source.getTekstit(), KomoTeksti.TAVOITTEET);
                //target.setTavoitteet(copyFields(source.getTavoitteet(), target.getTavoitteet()));
                break;
        }

        //names for KOMOTO search 
        target.setNimi(null);

        return target;
    }

    public static Koulutusmoduuli copyFieldsToKoulutusmoduuli(final KoulutusmoduuliKoosteTyyppi tyyppi) {
        return copyFieldsToKoulutusmoduuli(tyyppi, new Koulutusmoduuli());
    }

    public static void copyYhteyshenkilos(Collection<Yhteyshenkilo> fromList, Collection<YhteyshenkiloTyyppi> toList) {

        for (Yhteyshenkilo fromHenkilo : fromList) {
            YhteyshenkiloTyyppi toHenkilo = new YhteyshenkiloTyyppi();
            copyFields(fromHenkilo, toHenkilo);
            toList.add(toHenkilo);
        }

    }

    public static void copyYhteyshenkilos(Collection<Yhteyshenkilo> fromList, Set<YhteyshenkiloTyyppi> toList) {

        for (Yhteyshenkilo fromHenkilo : fromList) {
            YhteyshenkiloTyyppi toHenkilo = new YhteyshenkiloTyyppi();
            copyFields(fromHenkilo, toHenkilo);
            toList.add(toHenkilo);
        }
    }

    public static void copyYhteyshenkilos(Set<YhteyshenkiloTyyppi> fromList, Collection<Yhteyshenkilo> toList) {
        if (!fromList.isEmpty()) {
            if (toList == null) {
                toList = Sets.<Yhteyshenkilo>newHashSet();
            }

            for (YhteyshenkiloTyyppi tyyppi : fromList) {
                Yhteyshenkilo newYhteyshenkilo = new Yhteyshenkilo();
                copyFields(tyyppi, newYhteyshenkilo);
                toList.add(newYhteyshenkilo);
            }
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
            toLinkki.setTyyppi(fromLinkki.getLinkkiTyyppi());
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
    public static fi.vm.sade.tarjonta.shared.types.TarjontaTila convertTila(fi.vm.sade.tarjonta.service.types.TarjontaTila tila) {
        Preconditions.checkNotNull(tila, "TarjontaTila enum cannot be null.");
        return fi.vm.sade.tarjonta.shared.types.TarjontaTila.valueOf(tila.name());

    }

    /**
     * Converts TarjontaTila from domain model type to web service type.
     *
     * @param tila
     * @return
     */
    public static fi.vm.sade.tarjonta.service.types.TarjontaTila convertTila(fi.vm.sade.tarjonta.shared.types.TarjontaTila tila) {

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

    public static List<KoodistoKoodiTyyppi> copyFields(Collection<Kielivalikoima> tarjotutKielet, Kieliaine aine) {
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

    public static String joinListToString(String item) {
        return joinListToString(Lists.<String>newArrayList(item));
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

    public static KoulutusmoduuliKoosteTyyppi convertToKoulutusmoduuliKoosteTyyppi(final Koulutusmoduuli komo, final Koulutusmoduuli parentKomo) {
        return parentKomo != null ? EntityUtils.copyFieldsToKoulutusmoduuliKoosteTyyppi(komo, parentKomo) : EntityUtils.copyFieldsToKoulutusmoduuliKoosteTyyppi(komo);
    }

    public static KoulutusasteTyyppi KoulutusTyyppiStrToKoulutusAsteTyyppi(String koulutustyyppi) {
        return KoulutusasteTyyppi.fromValue(koulutustyyppi);
    }

    public static void keepSelectedKoodistoUri(Set<KoodistoUri> allDates, KoodistoUri keepDates) {
        keepSelectedKoodistoUris(allDates, Sets.<KoodistoUri>newHashSet(keepDates));
    }

    /**
     * Remove all orphaned koodisto uri objects from a set of koodisto uri
     * objects.
     *
     * @param allUris
     * @param keepUris
     */
    public static void keepSelectedKoodistoUris(Set<KoodistoUri> allUris, Set<KoodistoUri> keepUris) {
        Set<KoodistoUri> removableDates = Sets.<KoodistoUri>newHashSet(allUris);

        for (KoodistoUri uri : keepUris) {
            if (removableDates.contains(uri)) {
                removableDates.remove(uri);
            }
        }

        for (KoodistoUri uri : removableDates) {
            allUris.remove(uri);
        }
    }

    public static void keepSelectedDates(Set<Date> allDates, Date keepDates) {
        keepSelectedDates(allDates, Sets.<Date>newHashSet(keepDates));
    }

    /**
     * Remove all orphaned date objects from a set of date objects.
     *
     * @param allDates
     * @param keepDates
     */
    public static void keepSelectedDates(Set<Date> allDates, Set<Date> keepDates) {
        Set<Date> removableDates = Sets.<Date>newHashSet(allDates);

        for (Date dtoDate : keepDates) {
            Date dateWithoutMinutes = DateUtils.truncate(dtoDate, Calendar.DATE);
            if (removableDates.contains(dateWithoutMinutes)) {
                removableDates.remove(dateWithoutMinutes);
            }
        }

        for (Date date : removableDates) {
            allDates.remove(date);
        }
    }

    private static void copyKomoRelationsToKomotoEntity(KoulutusmoduuliKoosteTyyppi tyyppi, KoulutusmoduuliToteutus komoto) {
        if (tyyppi == null || komoto == null) {
            //do nothing
            return;
        }

        /*
         * Override with the latest data in DB.
         */
        if (tyyppi.getKoulutuskoodiUri() != null) {
            komoto.setKoulutusUri(tyyppi.getKoulutuskoodiUri());
        }

        if (tyyppi.getKoulutusohjelmakoodiUri() != null) {
            komoto.setKoulutusohjelmaUri(tyyppi.getKoulutusohjelmakoodiUri());
        }

        if (tyyppi.getLukiolinjakoodiUri() != null) {
            komoto.setLukiolinjaUri(tyyppi.getLukiolinjakoodiUri());
        }

        if (tyyppi.getKoulutusasteUri() != null) {
            komoto.setKoulutusasteUri(tyyppi.getKoulutusasteUri());
        }

        if (tyyppi.getKoulutusalaUri() != null) {
            komoto.setKoulutusalaUri(tyyppi.getKoulutusalaUri());
        }

        if (tyyppi.getOpintoalaUri() != null) {
            komoto.setOpintoalaUri(tyyppi.getOpintoalaUri());
        }

        if (tyyppi.getEqfLuokitus() != null) {
            komoto.setEqfUri(tyyppi.getEqfLuokitus());
        }

        if (tyyppi.getNqfLuokitus() != null) {
            komoto.setNqfUri(tyyppi.getNqfLuokitus());
        }

        if (tyyppi.getLaajuusarvoUri() != null) {
            komoto.setOpintojenLaajuusarvoUri(tyyppi.getLaajuusarvoUri());
        }

        if (tyyppi.getLaajuusyksikkoUri() != null) {
            komoto.setOpintojenLaajuusyksikkoUri(tyyppi.getLaajuusyksikkoUri());
        }

        if (tyyppi.getUlkoinenTunniste() != null) {
            komoto.setUlkoinenTunniste(tyyppi.getUlkoinenTunniste());
        }

        if (tyyppi.getTutkintonimikeUri() != null) {
            komoto.setTutkintonimikeUri(tyyppi.getTutkintonimikeUri());
        }
    }

    public static KoodistoKoodiTyyppi copyToKoodistoKoodiTyyppi(final String uri) {
        KoodistoKoodiTyyppi koulutusKoodi = new KoodistoKoodiTyyppi();
        koulutusKoodi.setUri(uri);
        return koulutusKoodi;
    }

    public static void copyKomoRelationsToKomotoDto(KoulutusmoduuliToteutus komoto, LueKoulutusVastausTyyppi koulutusTyyppi) {
        if (koulutusTyyppi == null || komoto == null) {
            //do nothing
            return;
        }
        KoulutusmoduuliKoosteTyyppi tyyppi = koulutusTyyppi.getKoulutusmoduuli();

        if (tyyppi == null) {
            tyyppi = new KoulutusmoduuliKoosteTyyppi();
            koulutusTyyppi.setKoulutusmoduuli(tyyppi);
        }

        /*
         * Override with the latest data in dto.
         */
        if (komoto.getKoulutusUri() != null) {
            tyyppi.setKoulutuskoodiUri(komoto.getKoulutusUri());
            koulutusTyyppi.setKoulutusKoodi(copyToKoodistoKoodiTyyppi(komoto.getKoulutusUri()));
        }

        if (komoto.getKoulutusohjelmaUri() != null) {
            tyyppi.setKoulutusohjelmakoodiUri(komoto.getKoulutusohjelmaUri());
            koulutusTyyppi.setKoulutusohjelmaKoodi(copyToKoodistoKoodiTyyppi(komoto.getKoulutusohjelmaUri()));
        }

        if (komoto.getLukiolinjaUri() != null) {
            tyyppi.setLukiolinjakoodiUri(komoto.getLukiolinjaUri());
            koulutusTyyppi.setLukiolinjaKoodi(copyToKoodistoKoodiTyyppi(komoto.getLukiolinjaUri()));
        }

        if (komoto.getKoulutusasteUri() != null) {
            tyyppi.setKoulutusasteUri(komoto.getKoulutusasteUri());
        }

        if (komoto.getKoulutusalaUri() != null) {
            tyyppi.setKoulutusalaUri(komoto.getKoulutusalaUri());
        }

        if (komoto.getOpintoalaUri() != null) {
            tyyppi.setOpintoalaUri(komoto.getOpintoalaUri());
        }

        if (komoto.getEqfUri() != null) {
            tyyppi.setEqfLuokitus(komoto.getEqfUri());
        }

        if (komoto.getNqfUri() != null) {
            tyyppi.setNqfLuokitus(komoto.getNqfUri());
        }

        if (komoto.getOpintojenLaajuusarvoUri() != null) {
            tyyppi.setLaajuusarvoUri(komoto.getOpintojenLaajuusarvoUri());
        }

        if (komoto.getOpintojenLaajuusyksikkoUri() != null) {
            tyyppi.setLaajuusyksikkoUri(komoto.getOpintojenLaajuusyksikkoUri());
        }

        if (komoto.getUlkoinenTunniste() != null) {
            tyyppi.setUlkoinenTunniste(komoto.getUlkoinenTunniste());
        }

        if (komoto.getTutkintonimikeUri() != null) {
            tyyppi.setTutkintonimikeUri(komoto.getTutkintonimikeUri());
        }

//        if (komoto.getKoulutustyyppiUri() != null) {
//            tyyppi.setKoulutustyyppiUri(komoto.getKoulutustyyppiUri());
//        }
//
//        if (komoto.getTutkintoUri() != null) {
//            tyyppi.setTutkinto(komoto.getTutkintoUri());
//        }
    }
}
