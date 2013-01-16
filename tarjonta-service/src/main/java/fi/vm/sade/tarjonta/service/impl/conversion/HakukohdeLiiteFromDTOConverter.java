package fi.vm.sade.tarjonta.service.impl.conversion;

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


import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.Osoite;
import fi.vm.sade.tarjonta.service.types.HakukohdeLiiteTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.model.HakukohdeLiite;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.OsoiteTyyppi;

import java.util.Locale;

/**
 * Created by: Tuomas Katva
 */

public class HakukohdeLiiteFromDTOConverter extends AbstractToDomainConverter<HakukohdeLiiteTyyppi, HakukohdeLiite> {



      @Override
      public HakukohdeLiite convert(HakukohdeLiiteTyyppi hakukohdeLiiteTyyppi) {

             HakukohdeLiite hakukohdeLiite = new HakukohdeLiite();

             if(hakukohdeLiiteTyyppi.getLiitteenId() != null) {
                  hakukohdeLiite.setId(new Long(hakukohdeLiiteTyyppi.getLiitteenId().trim()));
             }

             hakukohdeLiite.setErapaiva(hakukohdeLiiteTyyppi.getToimitettavaMennessa());

             hakukohdeLiite.setKuvaus(convertMonikielinenTekstiTyyppiToDomainValue(hakukohdeLiiteTyyppi.getLiitteenKuvaus()));
             hakukohdeLiite.setLiitetyyppi(hakukohdeLiiteTyyppi.getLiitteenTyyppi());
             hakukohdeLiite.setSahkoinenToimitusosoite(hakukohdeLiiteTyyppi.getSahkoinenToimitusOsoite());
             hakukohdeLiite.setToimitusosoite(convertOsoiteToOsoiteTyyppi(hakukohdeLiiteTyyppi.getLiitteenToimitusOsoite()));

             return hakukohdeLiite;
      }

    private MonikielinenTeksti convertMonikielinenTekstiTyyppiToDomainValue(MonikielinenTekstiTyyppi monikielinenTekstiTyyppi) {
        MonikielinenTeksti monikielinenTeksti = new MonikielinenTeksti();

        for (MonikielinenTekstiTyyppi.Teksti teksti:monikielinenTekstiTyyppi.getTeksti()) {
            monikielinenTeksti.addTekstiKaannos(teksti.getKieliKoodi(),teksti.getValue());
        }
        return monikielinenTeksti;
    }

    private Osoite convertOsoiteToOsoiteTyyppi(OsoiteTyyppi osoiteTyyppi) {
         Osoite osoite = new Osoite();

         osoite.setOsoiterivi1(osoiteTyyppi.getOsoiteRivi());
         osoite.setOsoiterivi2(osoiteTyyppi.getLisaOsoiteRivi());
         osoite.setPostinumero(osoiteTyyppi.getPostinumero());
         osoite.setPostitoimipaikka(osoiteTyyppi.getPostitoimipaikka());

        return osoite;
    }

}
