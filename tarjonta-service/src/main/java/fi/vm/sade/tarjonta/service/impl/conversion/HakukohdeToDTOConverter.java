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
import fi.vm.sade.tarjonta.service.types.tarjonta.*;
import fi.vm.sade.tarjonta.model.*;
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
        hakukohde.setHakukohteenTila(s.getTila());
        hakukohde.setOid(s.getOid());
        
        hakukohde.getLisatiedot().addAll(convertMonikielinenTeksti(s.getLisatiedot()));
        hakukohde.getValintaPerusteidenKuvaukset().addAll(convertMonikielinenTeksti(s.getValintaperusteKuvaus()));
        hakukohde.getHakukohteenKoulutusOidit().addAll(convertKoulutukses(s.getKoulutusmoduuliToteutuses()));
        return hakukohde;
    }
    
    private List<String> convertKoulutukses(Set<KoulutusmoduuliToteutus> komotos) {
        List<String> komotoOids = new ArrayList<String>();
        
        for (KoulutusmoduuliToteutus komoto : komotos) {
            komotoOids.add(komoto.getOid());
        }
        
        return komotoOids;
    }

    private List<MonikielinenTekstiTyyppi> convertMonikielinenTeksti(MonikielinenTeksti moniteksti) {
        List<MonikielinenTekstiTyyppi> tekstit = new ArrayList<MonikielinenTekstiTyyppi>();
        if (moniteksti != null) {
        	for (TekstiKaannos kaannos:moniteksti.getTekstis()) {
        		MonikielinenTekstiTyyppi teksti = new MonikielinenTekstiTyyppi();
        		teksti.setTeksti(kaannos.getTeksti());
        		teksti.setTekstinKielikoodi(kaannos.getKieliKoodi());
                        tekstit.add(teksti);
        	}
        }
        
        return tekstit;
    }
    
}
