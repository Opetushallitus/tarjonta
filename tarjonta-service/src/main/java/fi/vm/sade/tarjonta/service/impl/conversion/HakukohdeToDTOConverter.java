/*
 *
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
package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.OsoiteTyyppi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Tuomas Katva
 */
public class HakukohdeToDTOConverter extends AbstractFromDomainConverter<Hakukohde, HakukohdeTyyppi> {

    @Override
    public HakukohdeTyyppi convert(Hakukohde s) {
        HakukohdeTyyppi hakukohde = new HakukohdeTyyppi();
        hakukohde.setAloituspaikat(s.getAloituspaikatLkm());
        hakukohde.setHakukelpoisuusVaatimukset(s.getHakukelpoisuusvaatimus());
        hakukohde.setHakukohdeNimi(s.getHakukohdeNimi());
        hakukohde.setHakukohteenHakuOid(s.getHaku().getOid());
        hakukohde.setHakukohteenTila(EntityUtils.convertTila(s.getTila()));
        hakukohde.setOid(s.getOid());
        hakukohde.setHakukohdeKoodistoNimi(s.getHakukohdeKoodistoNimi());
        hakukohde.setLisatiedot(EntityUtils.copyFields(s.getLisatiedot()));
        hakukohde.setValintaPerusteidenKuvaukset(EntityUtils.copyFields(s.getValintaperusteKuvaus()));
        hakukohde.getHakukohteenKoulutusOidit().addAll(convertKoulutukses(s.getKoulutusmoduuliToteutuses()));

        hakukohde.setValinnanAloituspaikat(s.getValintojenAloituspaikatLkm());
        hakukohde.setSahkoinenToimitusOsoite(s.getSahkoinenToimitusOsoite());
        hakukohde.setLiitteidenToimitusPvm(s.getLiitteidenToimitusPvm());
        if (s.getLiitteidenToimitusOsoite() != null) {
            hakukohde.setLiitteidenToimitusOsoite(osoiteTyyppiFromOsoite(s.getLiitteidenToimitusOsoite()));
        }

        return hakukohde;
    }

    private List<String> convertKoulutukses(Set<KoulutusmoduuliToteutus> komotos) {
        List<String> komotoOids = new ArrayList<String>();

        for (KoulutusmoduuliToteutus komoto : komotos) {
            komotoOids.add(komoto.getOid());
        }

        return komotoOids;
    }

    private OsoiteTyyppi osoiteTyyppiFromOsoite(Osoite osoite) {
        OsoiteTyyppi osoiteTyyppi = new OsoiteTyyppi();

        osoiteTyyppi.setOsoiteRivi(osoite.getOsoiterivi1());
        osoiteTyyppi.setLisaOsoiteRivi(osoite.getOsoiterivi2());
        osoiteTyyppi.setPostinumero(osoite.getPostinumero());
        osoiteTyyppi.setPostitoimipaikka(osoite.getPostitoimipaikka());

        return osoiteTyyppi;
    }

}

