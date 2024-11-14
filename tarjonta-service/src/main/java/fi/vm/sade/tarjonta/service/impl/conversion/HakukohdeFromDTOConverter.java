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

import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.OsoiteTyyppi;
import fi.vm.sade.tarjonta.service.types.PainotettavaOppiaineTyyppi;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Tuomas Katva
 */
public class HakukohdeFromDTOConverter
    extends AbstractToDomainConverter<HakukohdeTyyppi, Hakukohde> {

  @Override
  public Hakukohde convert(HakukohdeTyyppi from) {
    Hakukohde hakukohde = new Hakukohde();
    hakukohde.setVersion(from.getVersion());
    hakukohde.setAloituspaikatLkm(from.getAloituspaikat());
    // hakukohde.setHakukelpoisuusvaatimus(from.getHakukelpoisuusVaatimukset());
    hakukohde.setHakukohdeNimi(from.getHakukohdeNimi());
    hakukohde.setOid(from.getOid());
    hakukohde.setLisatiedot(
        EntityUtils.copyFields(from.getLisatiedot(), hakukohde.getLisatiedot()));
    hakukohde.setTila(EntityUtils.convertTila(from.getHakukohteenTila()));
    hakukohde.setHakukohdeKoodistoNimi(from.getHakukohdeKoodistoNimi());
    hakukohde.setHakuaikaAlkuPvm(from.getHakuaikaAlkuPvm());
    hakukohde.setHakuaikaLoppuPvm(from.getHakuaikaLoppuPvm());
    hakukohde.setKaksoisTutkinto(from.isKaksoisTutkinto());

    // Valintaperustekuvaus is stored now in MonikielinenMetaData table.
    // hakukohde.setValintaperusteKuvaus(EntityUtils.copyFields(from.getValintaPerusteidenKuvaukset()));
    hakukohde.setValintojenAloituspaikatLkm(
        from.getValinnanAloituspaikat() == null ? 0 : from.getValinnanAloituspaikat());
    hakukohde.setLiitteidenToimitusPvm(from.getLiitteidenToimitusPvm());
    hakukohde.setSahkoinenToimitusOsoite(from.getSahkoinenToimitusOsoite());
    hakukohde.setKaytetaanHaunPaattymisenAikaa(from.isKaytetaanHaunPaattymisenAikaa());

    // sora- ja vape-kuvaus: varmistetaan, että joko url, teksti tai molemmat ovat null
    hakukohde.setSoraKuvausKoodiUri(from.getSoraKuvausKoodiUri());
    hakukohde.setSoraKuvaus(
        from.getSoraKuvausKoodiUri() != null
            ? null
            : CommonFromDTOConverter.convertMonikielinenTekstiTyyppiToDomainValue(
                from.getSoraKuvausTeksti()));

    hakukohde.setValintaperustekuvausKoodiUri(from.getValintaperustekuvausKoodiUri());
    hakukohde.setValintaperusteKuvaus(
        from.getValintaperustekuvausKoodiUri() != null
            ? null
            : CommonFromDTOConverter.convertMonikielinenTekstiTyyppiToDomainValue(
                from.getValintaperustekuvausTeksti()));

    if (from.getLiitteidenToimitusOsoite() != null) {
      hakukohde.setLiitteidenToimitusOsoite(convertOsoite(from.getLiitteidenToimitusOsoite()));
    }

    if (from.getAlinHyvaksyttavaKeskiarvo() != null) {
      hakukohde.setAlinHyvaksyttavaKeskiarvo(from.getAlinHyvaksyttavaKeskiarvo().doubleValue());
    }

    if (from.getPainotettavatOppiaineet() != null) {
      hakukohde
          .getPainotettavatOppiaineet()
          .addAll(convertPainotettavatOppiaineet(from.getPainotettavatOppiaineet()));
    }

    if (from.getViimeisinPaivittajaOid() != null) {
      hakukohde.setLastUpdatedByOid(from.getViimeisinPaivittajaOid());
    }
    hakukohde.setLastUpdateDate(Calendar.getInstance().getTime());

    return hakukohde;
  }

  private Set<PainotettavaOppiaine> convertPainotettavatOppiaineet(
      List<PainotettavaOppiaineTyyppi> oppiaineet) {
    Set<PainotettavaOppiaine> painotettavatOppiaineet = new HashSet<PainotettavaOppiaine>();

    for (PainotettavaOppiaineTyyppi oppiaineTyyppi : oppiaineet) {
      PainotettavaOppiaine painotettavaOppiaine = new PainotettavaOppiaine();
      painotettavaOppiaine.setOppiaine(oppiaineTyyppi.getOppiaine());

      painotettavaOppiaine.setPainokerroin(
          new BigDecimal(Double.toString(oppiaineTyyppi.getPainokerroin()))); // use string
      painotettavaOppiaine.setVersion(oppiaineTyyppi.getVersion());
      if (oppiaineTyyppi.getPainotettavaOppiaineTunniste() != null) {
        painotettavaOppiaine.setId(new Long(oppiaineTyyppi.getPainotettavaOppiaineTunniste()));
      }
      painotettavatOppiaineet.add(painotettavaOppiaine);
    }

    return painotettavatOppiaineet;
  }

  private Osoite convertOsoite(OsoiteTyyppi osoiteTyyppi) {
    Osoite osoite = new Osoite();

    osoite.setOsoiterivi1(osoiteTyyppi.getOsoiteRivi());
    osoite.setOsoiterivi2(osoiteTyyppi.getLisaOsoiteRivi());
    osoite.setPostinumero(osoiteTyyppi.getPostinumero());
    osoite.setPostitoimipaikka(osoiteTyyppi.getPostitoimipaikka());

    return osoite;
  }
}
