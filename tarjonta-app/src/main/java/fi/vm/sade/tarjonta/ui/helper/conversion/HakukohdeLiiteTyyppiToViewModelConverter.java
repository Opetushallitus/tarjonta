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

import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.tarjonta.service.types.HakukohdeLiiteTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.OsoiteTyyppi;
import fi.vm.sade.tarjonta.ui.model.HakukohdeLiiteViewModel;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;

/**
 * Created by: Tuomas Katva
 * Date: 17.1.2013
 */

public class HakukohdeLiiteTyyppiToViewModelConverter {


      public static HakukohdeLiiteViewModel convert(HakukohdeLiiteTyyppi hakukohdeLiiteTyyppi) {
          HakukohdeLiiteViewModel liiteViewModel = new HakukohdeLiiteViewModel();

          liiteViewModel.setHakukohdeLiiteId(hakukohdeLiiteTyyppi.getLiitteenId());
          liiteViewModel.setToimitettavaMennessa(hakukohdeLiiteTyyppi.getToimitettavaMennessa());
          liiteViewModel.setLiitteenTyyppi(hakukohdeLiiteTyyppi.getLiitteenTyyppi());
          liiteViewModel.setLiitteeTyyppiKoodistoNimi(hakukohdeLiiteTyyppi.getLiitteenTyyppiKoodistoNimi());
          liiteViewModel.setSahkoinenToimitusOsoite(hakukohdeLiiteTyyppi.getSahkoinenToimitusOsoite());
          mapOsoite(liiteViewModel,hakukohdeLiiteTyyppi.getLiitteenToimitusOsoite());
          liiteViewModel.getLiitteenSanallinenKuvaus().addAll(mapSanallisetKuvaukset(hakukohdeLiiteTyyppi.getLiitteenKuvaus()));
          if (hakukohdeLiiteTyyppi.getViimeisinPaivitysPvm() !=null) {
               liiteViewModel.setViimeisinPaivitysPvm(hakukohdeLiiteTyyppi.getViimeisinPaivitysPvm());
          }
          if (hakukohdeLiiteTyyppi.getViimeisinPaivittajaOid() != null) {
              liiteViewModel.setViimeisinPaivittaja(hakukohdeLiiteTyyppi.getViimeisinPaivittajaOid());
          }

          return liiteViewModel;
      }

    private static List<KielikaannosViewModel> mapSanallisetKuvaukset(MonikielinenTekstiTyyppi tekstit) {
        ArrayList<KielikaannosViewModel> kuvaukset = new ArrayList<KielikaannosViewModel>();

        for (MonikielinenTekstiTyyppi.Teksti teksti: tekstit.getTeksti()) {
            KielikaannosViewModel kuvaus = new KielikaannosViewModel();
            kuvaus.setKielikoodi(teksti.getKieliKoodi());
            kuvaus.setNimi(teksti.getValue());
            kuvaukset.add(kuvaus);
        }

        return kuvaukset;
    }

     private static void mapOsoite(HakukohdeLiiteViewModel hakukohdeLiiteViewModel,OsoiteTyyppi osoite) {
         hakukohdeLiiteViewModel.setOsoiteRivi1(osoite==null ? null : osoite.getOsoiteRivi());
         hakukohdeLiiteViewModel.setOsoiteRivi2(osoite==null ? null : osoite.getLisaOsoiteRivi());
         hakukohdeLiiteViewModel.setPostinumero(osoite==null ? null : osoite.getPostinumero());
         hakukohdeLiiteViewModel.setPostitoimiPaikka(osoite==null ? null : osoite.getPostitoimipaikka());
     }

}
