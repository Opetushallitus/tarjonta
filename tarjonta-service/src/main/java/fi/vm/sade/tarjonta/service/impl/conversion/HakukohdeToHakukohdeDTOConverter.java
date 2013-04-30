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
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;

/**
 *
 * @author mlyly
 */
public class HakukohdeToHakukohdeDTOConverter  extends AbstractFromDomainConverter<Hakukohde, HakukohdeDTO> {

    @Override
    public HakukohdeDTO convert(Hakukohde s) {
        HakukohdeDTO t = new HakukohdeDTO();

        t.setOid(s.getOid());
        t.setVersion(s.getVersion() != null ? s.getVersion().intValue() : -1);

        t.setAlinHyvaksyttavaKeskiarvo(s.getAlinHyvaksyttavaKeskiarvo() != null ? s.getAlinHyvaksyttavaKeskiarvo().doubleValue() : 0.0d);
        t.setAlinValintaPistemaara(s.getAlinValintaPistemaara() != null ? s.getAlinValintaPistemaara().intValue() : 0);
        t.setAloituspaikatLkm(s.getAloituspaikatLkm() != null ? s.getAloituspaikatLkm().intValue() : 0);
        t.setEdellisenVuodenHakijatLkm(s.getEdellisenVuodenHakijat() != null ? s.getEdellisenVuodenHakijat().intValue() : 0);
        // t.set(s.getHaku());
        t.setHakukelpoisuusvaatimusUri(s.getHakukelpoisuusvaatimus());
        t.setHakukohdeKoodistoNimi(s.getHakukohdeKoodistoNimi());
        t.setHakukohdeNimiUri(s.getHakukohdeNimi());
        // t.set(s.getKoulutusmoduuliToteutuses());
        t.setUpdated(s.getLastUpdateDate());
        t.setUpdatedByOid(s.getLastUpdatedByOid());
        // t.set(s.getLiites());
        // t.set(s.getLiitteidenToimitusOsoite());
        t.setLiitteidenToimitusPvm(s.getLiitteidenToimitusPvm());
        t.setLisatiedot(KoulutusmoduuliToKomoConverter.convert(s.getLisatiedot()));
        // t.set(s.getPainotettavatOppiaineet());
        t.setSahkoinenToimitusOsoite(s.getSahkoinenToimitusOsoite());
        t.setSoraKuvausKoodiUri(s.getSoraKuvausKoodiUri());
        t.setTila(s.getTila() != null ? s.getTila().name() : null);
        // t.set(s.getValintakoes());
        t.setValintaperustekuvausKoodiUri(s.getValintaperustekuvausKoodiUri());
        t.setValintojenAloituspaikatLkm(s.getValintojenAloituspaikatLkm() != null ? s.getValintojenAloituspaikatLkm().intValue() : 0);
        t.setYlinValintapistemaara(s.getYlinValintaPistemaara() != null ? s.getYlinValintaPistemaara().intValue() : 0);

        return t;
    }

}
