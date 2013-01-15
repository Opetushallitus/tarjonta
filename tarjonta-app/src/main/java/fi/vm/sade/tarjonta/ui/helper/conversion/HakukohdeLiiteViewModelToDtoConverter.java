package fi.vm.sade.tarjonta.ui.helper.conversion;/*
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

import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.tarjonta.service.types.OsoiteTyyppi;
import org.springframework.beans.factory.annotation.Autowired;
import fi.vm.sade.tarjonta.service.types.HakukohdeLiiteTyyppi;
import fi.vm.sade.tarjonta.ui.model.HakukohdeLiiteViewModel;
/**
 * Created by: Tuomas Katva
 * Date: 15.1.2013
 */
public class HakukohdeLiiteViewModelToDtoConverter {



    public HakukohdeLiiteTyyppi convertHakukohdeViewModelToHakukohdeLiiteTyyppi(HakukohdeLiiteViewModel hakukohdeLiiteViewModel) {
        HakukohdeLiiteTyyppi hakukohdeLiite = new HakukohdeLiiteTyyppi();

        hakukohdeLiite.setLiitteenTyyppi(hakukohdeLiiteViewModel.getLiitteenTyyppi());
        hakukohdeLiite.setLiitteenToimitusOsoite(convertLiiteToOsoiteTyyppi(hakukohdeLiiteViewModel));
        hakukohdeLiite.setLiitteenKuvaus(hakukohdeLiiteViewModel.getLiitteenSanallinenKuvaus());
        hakukohdeLiite.setLiitteenId(hakukohdeLiiteViewModel.getHakukohdeLiiteId());
        hakukohdeLiite.setSahkoinenToimitusOsoite(hakukohdeLiiteViewModel.getSahkoinenToimitusOsoite());
        hakukohdeLiite.setToimitettavaMennessa(hakukohdeLiiteViewModel.getToimitettavaMennessa());

        return hakukohdeLiite;
    }


    private OsoiteTyyppi convertLiiteToOsoiteTyyppi(HakukohdeLiiteViewModel hakukohdeLiiteViewModel) {
        OsoiteTyyppi osoite = new OsoiteTyyppi();

        osoite.setOsoiteRivi(hakukohdeLiiteViewModel.getOsoiteRivi1());
        osoite.setLisaOsoiteRivi(hakukohdeLiiteViewModel.getOsoiteRivi2());
        osoite.setPostinumero(hakukohdeLiiteViewModel.getPostinumero());
        osoite.setPostitoimipaikka(hakukohdeLiiteViewModel.getPostitoimiPaikka());
        osoite.setPostitoimipaikka(hakukohdeLiiteViewModel.getPostitoimiPaikka());

        return osoite;
    }


}
