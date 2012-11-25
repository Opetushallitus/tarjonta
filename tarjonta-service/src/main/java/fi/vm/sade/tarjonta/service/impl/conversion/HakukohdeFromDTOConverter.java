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

import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;

/**
 *
 * @author Tuomas Katva
 */
public class HakukohdeFromDTOConverter extends AbstractToDomainConverter<HakukohdeTyyppi, Hakukohde> {

    @Override
    public Hakukohde convert(HakukohdeTyyppi from) {
        Hakukohde hakukohde = new Hakukohde();

        hakukohde.setAloituspaikatLkm(from.getAloituspaikat());
        hakukohde.setHakukelpoisuusvaatimus(from.getHakukelpoisuusVaatimukset());
        hakukohde.setHakukohdeNimi(from.getHakukohdeNimi());
        hakukohde.setOid(from.getOid());
        hakukohde.setLisatiedot(EntityUtils.copyFields(from.getLisatiedot()));
        hakukohde.setTila(EntityUtils.convertTila(from.getHakukohteenTila()));
        hakukohde.setHakukohdeKoodistoNimi(from.getHakukohdeKoodistoNimi());
        hakukohde.setValintaperusteKuvaus(EntityUtils.copyFields(from.getValintaPerusteidenKuvaukset()));
        return hakukohde;
    }

}

