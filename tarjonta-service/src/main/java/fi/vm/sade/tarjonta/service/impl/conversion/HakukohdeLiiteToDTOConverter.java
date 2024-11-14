package fi.vm.sade.tarjonta.service.impl.conversion;/*
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

import fi.vm.sade.tarjonta.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.tarjonta.model.HakukohdeLiite;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.Osoite;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.service.types.HakukohdeLiiteTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.OsoiteTyyppi;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by: Tuomas Katva
 * Date: 11.1.2013
 * Time: 13:18
 */
public class HakukohdeLiiteToDTOConverter extends AbstractFromDomainConverter<HakukohdeLiite, HakukohdeLiiteTyyppi> {

    @Override
    public HakukohdeLiiteTyyppi convert(HakukohdeLiite hakukohdeLiite) {
        HakukohdeLiiteTyyppi hakukohdeLiiteTyyppi = new HakukohdeLiiteTyyppi();

        hakukohdeLiiteTyyppi.setToimitettavaMennessa(hakukohdeLiite.getErapaiva());
        hakukohdeLiiteTyyppi.setLiitteenId(hakukohdeLiite.getId().toString());
        hakukohdeLiiteTyyppi.setLiitteenKuvaus(CommonToDTOConverter.convertMonikielinenTekstiToTekstiTyyppi(hakukohdeLiite.getKuvaus()));
        hakukohdeLiiteTyyppi.setLiitteenTyyppiKoodistoNimi(hakukohdeLiite.getLiitteenTyyppiKoodistoNimi());

        hakukohdeLiiteTyyppi.setLiitteenTyyppi(hakukohdeLiite.getLiitetyyppi());
        hakukohdeLiiteTyyppi.setSahkoinenToimitusOsoite(hakukohdeLiite.getSahkoinenToimitusosoite());
        hakukohdeLiiteTyyppi.setLiitteenToimitusOsoite(CommonToDTOConverter.convertOsoiteToOsoiteTyyppi(hakukohdeLiite.getToimitusosoite()));

        if (hakukohdeLiite.getLastUpdatedByOid() != null) {
            hakukohdeLiiteTyyppi.setViimeisinPaivittajaOid(hakukohdeLiite.getLastUpdatedByOid());
        }
        if (hakukohdeLiite.getLastUpdateDate() != null) {
            hakukohdeLiiteTyyppi.setViimeisinPaivitysPvm(hakukohdeLiite.getLastUpdateDate());
        }

        return hakukohdeLiiteTyyppi;
    }






}
