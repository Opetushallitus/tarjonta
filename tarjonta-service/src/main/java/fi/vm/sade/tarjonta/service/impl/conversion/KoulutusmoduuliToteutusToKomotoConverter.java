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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.YhteyshenkiloRDTO;

/**
 * Conversion services for REST service.
 *
 * @author mlyly
 */
public class KoulutusmoduuliToteutusToKomotoConverter extends BaseRDTOConverter<KoulutusmoduuliToteutus, KomotoDTO> {

    // extends AbstractFromDomainConverter<KoulutusmoduuliToteutus, KomotoDTO>{
    private static final Logger LOG = LoggerFactory.getLogger(KoulutusmoduuliToKomoConverter.class);

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Override
    public KomotoDTO convert(KoulutusmoduuliToteutus komoto) {

        if (komoto == null) {
            return null;
        }

        KomotoDTO t = new KomotoDTO();

        t.setAmmattinimikeUris(convertKoodistoUrisToList(komoto.getAmmattinimikes()));
        t.setAvainsanatUris(convertKoodistoUrisToList(komoto.getAvainsanas()));

        // t.set(s.getHakukohdes()); TODO list OIDs
        try {
            t.setKoulutuksenAlkamisDate(komoto.getKoulutuksenAlkamisPvm());
        } catch (Exception e) {
            LOG.warn("Deprecated / invalid data model, use REST V1 API. KOMOTO OID : ", komoto.getOid());
        }
        t.setKoulutuslajiUris(convertKoodistoUrisToList(komoto.getKoulutuslajis()));
        t.setModified(komoto.getUpdated());
        t.setModifiedBy(komoto.getLastUpdatedByOid());
        t.setWebLinkkis(convertWebLinkkisToMap(komoto.getLinkkis()));
        t.setLukiodiplomitUris(convertKoodistoUrisToList(komoto.getLukiodiplomit()));
        t.setMaksullisuus(komoto.getMaksullisuus() != null);
        t.setOid(komoto.getOid());
        t.setOpetuskieletUris(convertKoodistoUrisToList(komoto.getOpetuskielis()));
        t.setOpetusmuodotUris(convertKoodistoUrisToList(komoto.getOpetusmuotos()));
        t.setPohjakoulutusVaatimusUri(komoto.getPohjakoulutusvaatimusUri());
        t.setLaajuusArvo(komoto.getOpintojenLaajuusArvo());
        t.setLaajuusYksikkoUri(komoto.getOpintojenLaajuusyksikkoUri());
        t.setSuunniteltuKestoArvo(komoto.getSuunniteltukestoArvo());
        t.setSuunniteltuKestoYksikkoUri(komoto.getSuunniteltukestoYksikkoUri());
        t.setTarjoajaOid(komoto.getTarjoaja());
        t.setTeematUris(convertKoodistoUrisToList(komoto.getTeemas()));
        t.setTila(komoto.getTila());
        t.setUlkoinenTunniste(komoto.getUlkoinenTunniste());
        t.setVersion((komoto.getVersion() != null) ? komoto.getVersion().intValue() : -1);

        List<String> tutkintonimikeUris = new ArrayList<String>();

        if (komoto.getTutkintonimikes() != null && !komoto.getTutkintonimikes().isEmpty()) {
            t.setTutkintonimikeUri(komoto.getTutkintonimikes().iterator().next().getKoodiUri());
            for (KoodistoUri uri : komoto.getTutkintonimikes()) {
                tutkintonimikeUris.add(uri.getKoodiUri());
            }
        } else {
            t.setTutkintonimikeUri(komoto.getKoulutusmoduuli().getTutkintonimikeUri());
            if (komoto.getKoulutusmoduuli().getTutkintonimikeUri() != null) {
                tutkintonimikeUris.add(komoto.getKoulutusmoduuli().getTutkintonimikeUri());
            }
        }
        t.setTutkintonimikeUris(tutkintonimikeUris);

        t.setOpintoalaUri(komoto.getOpintoalaUri());
        t.setKoulutusAlaUri(komoto.getKoulutusalaUri());
        t.setKoulutusAsteUri(komoto.getKoulutusasteUri());
        t.setKoulutusKoodiUri(komoto.getKoulutusUri());
        t.setLukiolinjaUri(komoto.getLukiolinjaUri());
        t.setOpintojenLaajuusarvoUri(komoto.getOpintojenLaajuusarvoUri());
        t.setKoulutustyyppiUri(komoto.getKoulutustyyppiUri());
        t.setTutkintoUri(komoto.getTutkintoUri());
        t.setNqfLuokitusUri(komoto.getNqfUri());
        t.setEqfLuokitusUri(komoto.getEqfUri());
        t.setKoulutusohjelmaUri(komoto.getKoulutusohjelmaUri());

        /**
         * Tutke 2 muutos: KJOH-951
         * - Päätettiin, että ei muuteta koodiarvoja tietokanta-ajona siihen sisältyvien riskien takia,
         * vaan sen sijaan hoidetaan converterissa koodiarvojen käsittely. Koodiarvojen kovakoodaaminen
         * tähän on tietysti ylläpidettävyyden kannalta huono ratkaisu, mutta tässä tilanteessa
         * päädyttiin tällaiseen kompromissiin.
         */
        boolean isParentKomoto = komoto.getAlkamisVuosi() == null;
        if (!isParentKomoto && komoto.isSyksy2015OrLater()) {

            switch (komoto.getToteutustyyppi()) {
                case AMMATILLINEN_PERUSTUTKINTO:
                case AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA:
                case AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA:
                    Koulutusmoduuli komo = komoto.getKoulutusmoduuli();
                    Koulutusmoduuli parentKomo = koulutusmoduuliDAO.findParentKomo(komo);
                    if (parentKomo != null) {
                        komo = parentKomo;
                    }
                    String laajuusarvoUri = komoto.getOpintojenLaajuusarvoUri() == null ?
                            komo.getOpintojenLaajuusarvoUri() :
                            komoto.getOpintojenLaajuusarvoUri();
                    String laajuusyksikkoUri = komoto.getOpintojenLaajuusyksikkoUri() == null ?
                            komo.getOpintojenLaajuusyksikkoUri() :
                            komoto.getOpintojenLaajuusyksikkoUri();

                    if (laajuusarvoUri != null && laajuusyksikkoUri != null
                            && komoto.getKoodiUriWithoutVersion(laajuusarvoUri).equals("opintojenlaajuus_120")
                            && komoto.getKoodiUriWithoutVersion(laajuusyksikkoUri).equals("opintojenlaajuusyksikko_1")) {
                        t.setOpintojenLaajuusarvoUri("opintojenlaajuus_180#1");
                        t.setLaajuusYksikkoUri("opintojenlaajuusyksikko_6#1");
                    }

                    break;
            }

            String koulutusohjelmaOrOsaamisalaUri = komoto.getOsaamisalaUri() != null ?
                    komoto.getOsaamisalaUri() :
                    komoto.getKoulutusmoduuli().getOsaamisalaUri();
            if (koulutusohjelmaOrOsaamisalaUri != null) {
                t.setKoulutusohjelmaUri(koulutusohjelmaOrOsaamisalaUri);
            }
        }

        // Vaadin-Angular muutostyön jäleiset uudet koodistot
        t.setOpetusmuotokk(convertKoodistoUrisToList(komoto.getOpetusmuotos()));
        t.setOpetusaikakk(convertKoodistoUrisToList(komoto.getOpetusAikas()));
        t.setOpetuspaikkakk(convertKoodistoUrisToList(komoto.getOpetusPaikkas()));

        if (komoto.getKoulutuksenAlkamisPvms() == null || komoto.getKoulutuksenAlkamisPvms().isEmpty()) {
            t.setKoulutuksenAlkamiskausi(komoto.getAlkamiskausiUri());
            t.setKoulutuksenAlkamisvuosi(komoto.getAlkamisVuosi());
        } else {
            t.setKoulutuksenAlkamisDates(new ArrayList<Date>(komoto.getKoulutuksenAlkamisPvms()));
        }

        convertTekstit(t.getTekstit(), komoto.getTekstit());

        // TODO t.setYhteyshenkilos(KoulutusmoduuliToKomoConverter.convert(s.getYhteyshenkilos()));
        //
        // Relations
        //
        t.setKomoOid(komoto.getKoulutusmoduuli() != null ? komoto.getKoulutusmoduuli().getOid() : null);

        //
        // Get parent komo -> parent parent komo --> parent komoto with same tarjoaja and pohjakoulutusvaatimus
        //
        {
            t.setParentKomotoOid(null);

            // 1. Get "parent" komo
            Koulutusmoduuli parentKomo = komoto.getKoulutusmoduuli();
            if (parentKomo != null) {
                //LOG.debug("  1. parent komo = {}", parentKomo.getOid());

                // 2. get "parent" parent komo
                Koulutusmoduuli parentParentKomo = koulutusmoduuliDAO.findParentKomo(parentKomo);
                if (parentParentKomo != null) {
                    //LOG.debug("  2. parent parent komo = {}", parentParentKomo.getOid());

                    // Get komotos with same pohjakoulutus and tarjoaja
                    List<KoulutusmoduuliToteutus> parentKomotos
                            = koulutusmoduuliToteutusDAO.findKomotosByKomoTarjoajaPohjakoulutus(parentParentKomo, komoto.getTarjoaja(), komoto.getPohjakoulutusvaatimusUri());
                    //LOG.debug("  3. parent komotos = {}", parentKomotos);

                    if (parentKomotos == null || parentKomotos.isEmpty()) {
                        // NO PARENT KOMOTO
                    } else {
                        KoulutusmoduuliToteutus parentKomoto = parentKomotos.get(0);
                        t.setParentKomotoOid(parentKomoto.getOid());
                    }
                }

                //LOG.debug("  4. ---> parent komoto = {}", t.getParentKomotoOid());
            }
        }

        // OVT-5745 Added yhteyshenkilo inormation
        for (Yhteyshenkilo yhteyshenkilo : komoto.getYhteyshenkilos()) {
            t.getYhteyshenkilos().add(convert(yhteyshenkilo));
        }

        // OVT-6619 Add koulutusohjelman nimi, free text
        if (komoto.getNimi() != null && komoto.getNimi().getTekstiKaannos() != null) {
            for (TekstiKaannos kaannos : komoto.getNimi().getTekstiKaannos()) {
                t.setKoulutusohjelmanNimi(kaannos.getArvo());  //stored under "fi"
            }
        }

        //
        // Lukio spesific data
        // - Kielivalikoima returned as {"A1" : ["kieli_fi#1", "kieli_sv#2], "A2" : ["kieli_bz#313"]}
        //
        Map<String, Kielivalikoima> tarjotutKielet = komoto.getTarjotutKielet();
        if (tarjotutKielet != null && !tarjotutKielet.isEmpty()) {
            Map<String, List<String>> result = t.getTarjotutKielet();

            for (String key : tarjotutKielet.keySet()) {
                Kielivalikoima v = tarjotutKielet.get(key);

                if (v != null) {
                    List<String> tmp = result.get(key);
                    if (tmp == null) {
                        tmp = new ArrayList<String>();
                        result.put(key, tmp);
                    }

                    // Convert to list of uri#version
                    List<String> koodistoKieliList = convertKoodistoUrisToList(v.getKielet());
                    tmp.addAll(koodistoKieliList);
                }
            }
        }

        return t;
    }

    private YhteyshenkiloRDTO convert(Yhteyshenkilo yhteyshenkilo) {
        if (yhteyshenkilo == null) {
            return null;
        }

        YhteyshenkiloRDTO t = new YhteyshenkiloRDTO();
        t.setTyyppi(yhteyshenkilo.getHenkiloTyyppi() != null ? yhteyshenkilo.getHenkiloTyyppi().name() : null);
        t.setHenkiloOid(yhteyshenkilo.getHenkioOid());

        if (yhteyshenkilo.getMultipleKielis() != null) {
            for (String kieli : yhteyshenkilo.getMultipleKielis()) {
                t.getKielet().add(kieli);
            }
        }

        t.setPuhelin(yhteyshenkilo.getPuhelin());
        t.setEmail(yhteyshenkilo.getSahkoposti());
        t.setNimi(yhteyshenkilo.getNimi());
        t.setTitteli(yhteyshenkilo.getTitteli());
        t.setOid("" + yhteyshenkilo.getId());

        return t;
    }

}
