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
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Kielivalikoima;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.model.Yhteyshenkilo;
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
    public KomotoDTO convert(KoulutusmoduuliToteutus s) {

        if (s == null) {
            return null;
        }

        KomotoDTO t = new KomotoDTO();

        t.setAmmattinimikeUris(convertKoodistoUrisToList(s.getAmmattinimikes()));
        t.setAvainsanatUris(convertKoodistoUrisToList(s.getAvainsanas()));

        // t.set(s.getHakukohdes()); TODO list OIDs
        try {
            t.setKoulutuksenAlkamisDate(s.getKoulutuksenAlkamisPvm());
        } catch (Exception e) {
            LOG.warn("Deprecated / invalid data model, use REST V1 API. KOMOTO OID : ", s.getOid());
        }
        t.setKoulutuslajiUris(convertKoodistoUrisToList(s.getKoulutuslajis()));
        t.setModified(s.getUpdated());
        t.setModifiedBy(s.getLastUpdatedByOid());
        t.setWebLinkkis(convertWebLinkkisToMap(s.getLinkkis()));
        t.setLukiodiplomitUris(convertKoodistoUrisToList(s.getLukiodiplomit()));
        t.setMaksullisuus(s.getMaksullisuus() != null);
        t.setOid(s.getOid());
        t.setOpetuskieletUris(convertKoodistoUrisToList(s.getOpetuskielis()));
        t.setOpetusmuodotUris(convertKoodistoUrisToList(s.getOpetusmuotos()));
        t.setPohjakoulutusVaatimusUri(s.getPohjakoulutusvaatimusUri());
        t.setLaajuusArvo(s.getOpintojenLaajuusArvo());
        t.setLaajuusYksikkoUri(s.getOpintojenLaajuusyksikkoUri());
        t.setSuunniteltuKestoArvo(s.getSuunniteltukestoArvo());
        t.setSuunniteltuKestoYksikkoUri(s.getSuunniteltukestoYksikkoUri());
        t.setTarjoajaOid(s.getTarjoaja());
        t.setTeematUris(convertKoodistoUrisToList(s.getTeemas()));
        t.setTila(s.getTila());
        t.setUlkoinenTunniste(s.getUlkoinenTunniste());
        t.setVersion((s.getVersion() != null) ? s.getVersion().intValue() : -1);

        //OVT-7513 REAL KOODISTO FROM KOMOTO URIs (null if empty):
        t.setTutkintonimikeUri(s.getTutkintonimikeUri());
        t.setOpintoalaUri(s.getOpintoalaUri());
        t.setKoulutusAlaUri(s.getKoulutusalaUri());
        t.setKoulutusAsteUri(s.getKoulutusasteUri());
        t.setKoulutusKoodiUri(s.getKoulutusUri());
        t.setLukiolinjaUri(s.getLukiolinjaUri());
        t.setOpintojenLaajuusarvoUri(s.getOpintojenLaajuusarvoUri());
        t.setKoulutustyyppiUri(s.getKoulutustyyppiUri());
        t.setTutkintoUri(s.getTutkintoUri());
        t.setNqfLuokitusUri(s.getNqfUri());
        t.setEqfLuokitusUri(s.getEqfUri());
        t.setKoulutusohjelmaUri(s.getKoulutusohjelmaUri());

        convertTekstit(t.getTekstit(), s.getTekstit());

        // TODO t.setYhteyshenkilos(KoulutusmoduuliToKomoConverter.convert(s.getYhteyshenkilos()));
        //
        // Relations
        //
        t.setKomoOid(s.getKoulutusmoduuli() != null ? s.getKoulutusmoduuli().getOid() : null);

        //
        // Get parent komo -> parent parent komo --> parent komoto with same tarjoaja and pohjakoulutusvaatimus
        //
        {
            t.setParentKomotoOid(null);

            // 1. Get "parent" komo
            Koulutusmoduuli parentKomo = s.getKoulutusmoduuli();
            if (parentKomo != null) {
                //LOG.debug("  1. parent komo = {}", parentKomo.getOid());

                // 2. get "parent" parent komo
                Koulutusmoduuli parentParentKomo = koulutusmoduuliDAO.findParentKomo(parentKomo);
                if (parentParentKomo != null) {
                    //LOG.debug("  2. parent parent komo = {}", parentParentKomo.getOid());

                    // Get komotos with same pohjakoulutus and tarjoaja
                    List<KoulutusmoduuliToteutus> parentKomotos
                            = koulutusmoduuliToteutusDAO.findKomotosByKomoTarjoajaPohjakoulutus(parentParentKomo, s.getTarjoaja(), s.getPohjakoulutusvaatimusUri());
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
        for (Yhteyshenkilo yhteyshenkilo : s.getYhteyshenkilos()) {
            t.getYhteyshenkilos().add(convert(yhteyshenkilo));
        }

        // OVT-6619 Add koulutusohjelman nimi, free text
        if (s.getNimi() != null && s.getNimi().getTekstiKaannos() != null) {
            for (TekstiKaannos kaannos : s.getNimi().getTekstiKaannos()) {
                t.setKoulutusohjelmanNimi(kaannos.getArvo());  //stored under "fi"
            }
        }

        //
        // Lukio spesific data
        // - Kielivalikoima returned as {"A1" : ["kieli_fi#1", "kieli_sv#2], "A2" : ["kieli_bz#313"]}
        //
        Map<String, Kielivalikoima> tarjotutKielet = s.getTarjotutKielet();
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
        t.setEtunimet(yhteyshenkilo.getEtunimis());
        t.setTyyppi(yhteyshenkilo.getHenkiloTyyppi() != null ? yhteyshenkilo.getHenkiloTyyppi().name() : null);
        t.setHenkiloOid(yhteyshenkilo.getHenkioOid());

        if (yhteyshenkilo.getMultipleKielis() != null) {
            for (String kieli : yhteyshenkilo.getMultipleKielis()) {
                t.getKielet().add(kieli);
            }
        }

        t.setPuhelin(yhteyshenkilo.getPuhelin());
        t.setEmail(yhteyshenkilo.getSahkoposti());
        t.setSukunimi(yhteyshenkilo.getSukunimi());
        t.setTitteli(yhteyshenkilo.getTitteli());
        t.setOid("" + yhteyshenkilo.getId());

        return t;
    }

}
