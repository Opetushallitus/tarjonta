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
import fi.vm.sade.tarjonta.service.types.tarjonta.*;
import fi.vm.sade.tarjonta.model.*;
import java.util.List;

/**
 *
 * @author Tuomas Katva
 */
public class HakukohdeFromDTOConverter extends AbstractToDomainConverter<HakukohdeTyyppi, Hakukohde> {

    @Override
    public Hakukohde convert(HakukohdeTyyppi s) {
        Hakukohde hakukohde = new Hakukohde();

        hakukohde.setAloituspaikatLkm(s.getAloituspaikat());
        hakukohde.setHakukelpoisuusvaatimus(s.getHakukelpoisuusVaatimukset());
        hakukohde.setHakukohdeNimi(s.getHakukohdeNimi());
        hakukohde.setOid(s.getOid());
        hakukohde.setLisatiedot(convertMonikielinenTeksti(s.getLisatiedot()));
        hakukohde.setTila(TarjontaTila.valueOf(s.getHakukohteenTila()));
        hakukohde.setHakukohdeKoodistoNimi(s.getHakukohdeKoodistoNimi());
        hakukohde.setValintaperusteKuvaus(convertMonikielinenTeksti(s.getValintaPerusteidenKuvaukset()));
        return hakukohde;
    }



    private MonikielinenTeksti convertMonikielinenTeksti(List<MonikielinenTekstiTyyppi> monitekstis) {
        MonikielinenTeksti tekstit = null;
        if (monitekstis != null) {
            tekstit = new MonikielinenTeksti();
            for (MonikielinenTekstiTyyppi moniteksti : monitekstis) {
                tekstit.addTekstiKaannos(moniteksti.getTekstinKielikoodi(), moniteksti.getTeksti());
            }
        }

        return tekstit;
    }
}
