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
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.dto.Komoto;
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

        t.setAmmattinimikeUris(KoulutusmoduuliToKomoConverter.convert(s.getAmmattinimikes()));
        t.setArviointikriteerit(KoulutusmoduuliToKomoConverter.convert(s.getArviointikriteerit()));
        t.setAvainsanaUris(KoulutusmoduuliToKomoConverter.convert(s.getAvainsanas()));

        // t.set(s.getHakukohdes()); TODO list OIDs

        t.setKansainvalistyminen(KoulutusmoduuliToKomoConverter.convert(s.getKansainvalistyminen()));
        t.setKoulutuksenAlkamisDate(s.getKoulutuksenAlkamisPvm());
        t.setKoulutusAsteUri(s.getKoulutusaste());
        t.setKoulutuslajiUris(KoulutusmoduuliToKomoConverter.convert(s.getKoulutuslajis()));
        t.setKoulutusohjelmanValinta(KoulutusmoduuliToKomoConverter.convert(s.getKoulutusohjelmanValinta()));
        t.setKuvailevatTiedot(KoulutusmoduuliToKomoConverter.convert(s.getKuvailevatTiedot()));
        t.setUpdated(s.getLastUpdateDate()); // TODO vai getUpdated() ???
        t.setUpdateByOid(s.getLastUpdatedByOid());
        t.setWebLinkkis(KoulutusmoduuliToKomoConverter.convertWebLinkkis(s.getLinkkis()));
        t.setLoppukoeVaatimukset(KoulutusmoduuliToKomoConverter.convert(s.getLoppukoeVaatimukset()));
        t.setLukiodiplomiUris(KoulutusmoduuliToKomoConverter.convert(s.getLukiodiplomit()));
        t.setMaksullisuus(s.getMaksullisuus() != null);
        t.setMaksullisuusTeksti(KoulutusmoduuliToKomoConverter.convert(s.getMaksullisuusUrl()));
        t.setOid(s.getOid());
        t.setOpetusKieletUris(KoulutusmoduuliToKomoConverter.convert(s.getOpetuskielis()));
        t.setOpetusmuotoUris(KoulutusmoduuliToKomoConverter.convert(s.getOpetusmuotos()));
        t.setPainotus(KoulutusmoduuliToKomoConverter.convert(s.getPainotus()));
        t.setPohjakoulutusVaatimusUri(s.getPohjakoulutusvaatimus());
        t.setSijoittuminenTyoelamaan(KoulutusmoduuliToKomoConverter.convert(s.getSijoittuminenTyoelamaan()));
        t.setSisalto(KoulutusmoduuliToKomoConverter.convert(s.getSisalto()));
        t.setLaajuusArvo(s.getSuunniteltuKestoArvo());
        t.setLaajuusYksikkoUri(s.getSuunniteltuKestoYksikko());
        t.setTarjoajaOid(s.getTarjoaja());
        // t.setTarjotutKieletUris(KoulutusmoduuliToKomoConverter.convert(s.getTarjotutKielet())); // KieliValikoima?
        t.setTeemaUris(KoulutusmoduuliToKomoConverter.convert(s.getTeemas()));
        t.setTila("" + s.getTila());
        t.setUlkoinenTunniste(s.getUlkoinenTunniste());
        // t.setUpdated(s.getUpdated()); // TODO POISTA "lastUpdateDate"
        t.setVersion((s.getVersion() != null) ? s.getVersion().intValue() : -1);
        t.setYhteistyoMuidenToimijoidenKanssa(KoulutusmoduuliToKomoConverter.convert(s.getYhteistyoMuidenToimijoidenKanssa()));
        // t.setYhteyshenkilos(KoulutusmoduuliToKomoConverter.convert(s.getYhteyshenkilos()));

        //
        // Relations
        //
        t.setKomoOid(s.getKoulutusmoduuli() != null ? s.getKoulutusmoduuli().getOid() : null);

        return t;
    }

}
