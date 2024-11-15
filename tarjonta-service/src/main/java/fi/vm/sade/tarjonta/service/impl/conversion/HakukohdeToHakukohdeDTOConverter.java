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

import fi.vm.sade.tarjonta.dao.MonikielinenMetadataDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.impl.resources.v1.util.ValintaperustekuvausHelper;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeLiiteDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeRDTO;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import java.util.*;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Conversion for the REST services.
 *
 * @author mlyly
 */
public class HakukohdeToHakukohdeDTOConverter extends BaseRDTOConverter<Hakukohde, HakukohdeDTO> {

  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(HakukohdeToHakukohdeDTOConverter.class);

  @Autowired private MonikielinenMetadataDAO monikielinenMetadataDAO;

  @Autowired private TarjontaKoodistoHelper tarjontaKoodistoHelper;

  @Autowired private OrganisaatioService organisaatioService;

  @Autowired private ValintaperustekuvausHelper valintaperustekuvausHelper;

  @Override
  public HakukohdeDTO convert(Hakukohde hakukohde) {
    HakukohdeDTO hakukohdeDTO = new HakukohdeDTO();

    hakukohdeDTO.setOid(hakukohde.getOid());
    hakukohdeDTO.setVersion(
        hakukohde.getVersion() != null ? hakukohde.getVersion().intValue() : -1);

    // tarjoajaOid, tarjoajaNimi
    for (KoulutusmoduuliToteutus koulutusmoduuliToteutus :
        hakukohde.getKoulutusmoduuliToteutuses()) {
      if (koulutusmoduuliToteutus.getTarjoaja() != null) {
        // Assumes that only one provider for koulutus - is this true?
        String organisaatioOid = koulutusmoduuliToteutus.getTarjoaja();
        hakukohdeDTO.setTarjoajaOid(organisaatioOid);
        if (organisaatioOid != null) {
          try {
            hakukohdeDTO.setTarjoajaNimi(organisaatioService.getTarjoajaNimiMap(organisaatioOid));
          } catch (Throwable th) {
            // organisaation nimihaku epäonnistui!!!
            Map<String, String> map = new HashMap<String, String>();
            map.put(
                KoodistoURI.KOODI_LANG_FI_URI,
                "Organisaatiohaku epäonnistui (" + organisaatioOid + ")");
            hakukohdeDTO.setTarjoajaNimi(map);
          }
        }
        break;
      }
    }

    // hakukohdeNimi

    // New data / old API... / 6.8.2014 mlyly - August - no name provided for X hakukohde (new KK
    // hakukohde)
    if (hakukohde.getHakukohdeNimi() == null) {
      // no "koodisto" name, user supplied names
      if (hakukohde.getHakukohdeMonikielinenNimi() != null) {
        hakukohdeDTO.setHakukohdeNimi(hakukohde.getHakukohdeMonikielinenNimi().asMap());
      }
    } else {
      // Name resolved from koodisto
      hakukohdeDTO.setHakukohdeNimi(
          tarjontaKoodistoHelper.getKoodiMetadataNimi(hakukohde.getHakukohdeNimi()));
    }

    hakukohdeDTO.setAlinHyvaksyttavaKeskiarvo(
        hakukohde.getAlinHyvaksyttavaKeskiarvo() != null
            ? hakukohde.getAlinHyvaksyttavaKeskiarvo().doubleValue()
            : 0.0d);
    hakukohdeDTO.setAlinValintaPistemaara(
        hakukohde.getAlinValintaPistemaara() != null
            ? hakukohde.getAlinValintaPistemaara().intValue()
            : 0);
    hakukohdeDTO.setAloituspaikatLkm(hakukohde.getAloituspaikatLkm());
    hakukohdeDTO.setEdellisenVuodenHakijatLkm(
        hakukohde.getEdellisenVuodenHakijat() != null
            ? hakukohde.getEdellisenVuodenHakijat().intValue()
            : 0);
    hakukohdeDTO.setHakuOid(hakukohde.getHaku() != null ? hakukohde.getHaku().getOid() : null);
    hakukohdeDTO.setHakukohdeKoodistoNimi(hakukohde.getHakukohdeKoodistoNimi());
    hakukohdeDTO.setHakukohdeNimiUri(hakukohde.getHakukohdeNimi());
    hakukohdeDTO.setKaksoisTutkinto(hakukohde.isKaksoisTutkinto());
    hakukohdeDTO.setModified(hakukohde.getLastUpdateDate());
    hakukohdeDTO.setModifiedBy(hakukohde.getLastUpdatedByOid());
    hakukohdeDTO.setLiitteidenToimitusosoite(
        getConversionService().convert(hakukohde.getLiitteidenToimitusOsoite(), OsoiteRDTO.class));
    hakukohdeDTO.setLiitteidenToimitusPvm(hakukohde.getLiitteidenToimitusPvm());
    hakukohdeDTO.setLisatiedot(convertMonikielinenTekstiToMap(hakukohde.getLisatiedot()));
    hakukohdeDTO.setPainotettavatOppiaineet(
        convertPainotettavatOppianeet(hakukohde.getPainotettavatOppiaineet()));
    hakukohdeDTO.setSahkoinenToimitusOsoite(hakukohde.getSahkoinenToimitusOsoite());
    hakukohdeDTO.setTila(hakukohde.getTila() != null ? hakukohde.getTila().name() : null);
    hakukohdeDTO.setHakukohdeKoulutusOids(
        convertKoulutusOids(hakukohde.getKoulutusmoduuliToteutuses()));
    hakukohdeDTO.setValintakoes(convertValintakokeet(hakukohde.getValintakoes()));

    hakukohdeDTO.setValintojenAloituspaikatLkm(hakukohde.getValintojenAloituspaikatLkm());
    hakukohdeDTO.setYlinValintapistemaara(
        hakukohde.getYlinValintaPistemaara() != null
            ? hakukohde.getYlinValintaPistemaara().intValue()
            : 0);

    hakukohdeDTO.setKaytetaanHaunPaattymisenAikaa(hakukohde.isKaytetaanHaunPaattymisenAikaa());

    hakukohdeDTO.setLiitteet(convertLiitteet(hakukohde.getLiites()));

    if (hakukohde.getHakuaikaAlkuPvm() != null && hakukohde.getHakuaikaLoppuPvm() != null) {
      hakukohdeDTO.setKaytetaanHakukohdekohtaistaHakuaikaa(true);
      hakukohdeDTO.setHakuaikaAlkuPvm(hakukohde.getHakuaikaAlkuPvm());
      hakukohdeDTO.setHakuaikaLoppuPvm(hakukohde.getHakuaikaLoppuPvm());
    } else {
      hakukohdeDTO.setKaytetaanHakukohdekohtaistaHakuaikaa(false);
      if (hakukohde.getHakuaika() != null) {
        hakukohdeDTO.setHakuaikaAlkuPvm(hakukohde.getHakuaika().getAlkamisPvm());
        hakukohdeDTO.setHakuaikaLoppuPvm(hakukohde.getHakuaika().getPaattymisPvm());
      }
    }

    if (hakukohde.getSoraKuvaus() != null && hakukohde.getSoraKuvaus().getTekstiKaannos() != null) {

      HashMap<String, String> soraKuvaukset = new HashMap<String, String>();
      for (TekstiKaannos tekstiKaannos : hakukohde.getSoraKuvaus().getTekstiKaannos()) {

        soraKuvaukset.put(tekstiKaannos.getKieliKoodi(), tekstiKaannos.getArvo());
      }
      hakukohdeDTO.setSorakuvaus(soraKuvaukset);
    } else {
      LOG.debug("Hakukohde sorakuvaus was null : {}", hakukohdeDTO.getOid());
    }

    if (hakukohde.getValintaperusteKuvaus() != null
        && hakukohde.getValintaperusteKuvaus().getTekstiKaannos() != null) {

      HashMap<String, String> valintaperusteKuvaukset = new HashMap<String, String>();
      for (TekstiKaannos tekstiKaannos : hakukohde.getValintaperusteKuvaus().getTekstiKaannos()) {

        valintaperusteKuvaukset.put(tekstiKaannos.getKieliKoodi(), tekstiKaannos.getArvo());
      }
      hakukohdeDTO.setValintaperustekuvaus(valintaperusteKuvaukset);
    } else {
      LOG.debug("HAKUKOHDE valintaperustekuvaus was null : {}", hakukohdeDTO.getOid());
    }

    {
      String uri =
          getTarjontaKoodistoHelper()
              .getHakukelpoisuusvaatimusrymaUriForHakukohde(hakukohde.getHakukohdeNimi());
      hakukohdeDTO.getHakukelpoisuusvaatimusUris().add(uri);
      hakukohdeDTO.setHakukelpoisuusvaatimus(
          getTarjontaKoodistoHelper().getKoodiMetadataKuvaus(uri));
    }

    {
      String uri =
          getTarjontaKoodistoHelper()
              .getValintaperustekuvausryhmaUriForHakukohde(hakukohde.getHakukohdeNimi());
      hakukohdeDTO.setValintaperustekuvausKoodiUri(uri);
      if (hakukohdeDTO.getValintaperustekuvausKoodiUri() != null) {
        KoulutusmoduuliToteutus komoto = hakukohde.getKoulutusmoduuliToteutuses().iterator().next();
        if (komoto != null) {
          hakukohdeDTO.setValintaperustekuvaus(
              valintaperustekuvausHelper.getKuvausByAvainTyyppiKausiVuosi(
                  hakukohdeDTO.getValintaperustekuvausKoodiUri(),
                  ValintaperusteSoraKuvaus.Tyyppi.VALINTAPERUSTEKUVAUS,
                  komoto.getAlkamiskausiUri(),
                  komoto.getAlkamisVuosi()));
        }
      }
    }

    {
      String uri =
          getTarjontaKoodistoHelper()
              .getSORAKysymysryhmaUriForHakukohde(hakukohde.getHakukohdeNimi());
      hakukohdeDTO.setSoraKuvausKoodiUri(uri);
      if (hakukohdeDTO.getSoraKuvausKoodiUri() != null) {
        KoulutusmoduuliToteutus komoto = hakukohde.getKoulutusmoduuliToteutuses().iterator().next();
        hakukohdeDTO.setSorakuvaus(
            valintaperustekuvausHelper.getKuvausByAvainTyyppiKausiVuosi(
                hakukohdeDTO.getSoraKuvausKoodiUri(),
                ValintaperusteSoraKuvaus.Tyyppi.SORA,
                komoto.getAlkamiskausiUri(),
                komoto.getAlkamisVuosi()));
      }
    }

    //
    // Get the opetuskieli information - makes life easier for Team1.
    //
    Set<String> opetuskielis = new HashSet<String>();
    for (KoulutusmoduuliToteutus koulutusmoduuliToteutus :
        hakukohde.getKoulutusmoduuliToteutuses()) {
      for (KoodistoUri koodistoUri : koulutusmoduuliToteutus.getOpetuskielis()) {
        opetuskielis.add(koodistoUri.getKoodiUri());
      }
    }
    hakukohdeDTO.setOpetuskielet(new ArrayList<String>(opetuskielis));

    List<String> organisaatioRyhmaOids =
        (List<String>)
            CollectionUtils.collect(
                hakukohde.getRyhmaliitokset(), new BeanToPropertyValueTransformer("ryhmaOid"));
    hakukohdeDTO.setOrganisaatioRyhmaOids(
        organisaatioRyhmaOids.toArray(new String[organisaatioRyhmaOids.size()]));

    return hakukohdeDTO;
  }

  private List<ValintakoeRDTO> convertValintakokeet(Set<Valintakoe> valintakoes) {
    List<ValintakoeRDTO> result = new ArrayList<ValintakoeRDTO>();

    for (Valintakoe valintakoe : valintakoes) {
      result.add(getConversionService().convert(valintakoe, ValintakoeRDTO.class));
    }

    return result.isEmpty() ? null : result;
  }

  private List<String> convertKoulutusOids(Set<KoulutusmoduuliToteutus> komotos) {

    if (komotos != null) {
      List<String> komotoOids = new ArrayList<String>();
      for (KoulutusmoduuliToteutus komoto : komotos) {
        komotoOids.add(komoto.getOid());
      }
      return komotoOids;
    } else {

      return null;
    }
  }

  /**
   * Convert PainotettavaOppiaine to list of [ [ "oppiaine", "9.7"], ... ]
   *
   * @param s
   * @return
   */
  private List<List<String>> convertPainotettavatOppianeet(Set<PainotettavaOppiaine> s) {
    List<List<String>> result = new ArrayList<List<String>>();

    for (PainotettavaOppiaine painotettavaOppiaine : s) {
      List<String> t = new ArrayList<String>();
      t.add(painotettavaOppiaine.getOppiaine());
      t.add("" + painotettavaOppiaine.getPainokerroin());

      result.add(t);
    }

    return result.isEmpty() ? null : result;
  }

  /**
   * Convert liite information.
   *
   * @param s
   * @return
   */
  private List<HakukohdeLiiteDTO> convertLiitteet(Set<HakukohdeLiite> s) {
    List<HakukohdeLiiteDTO> result = new ArrayList<>();

    for (HakukohdeLiite hakukohdeLiite : s) {
      result.add(getConversionService().convert(hakukohdeLiite, HakukohdeLiiteDTO.class));
    }

    return result.isEmpty() ? null : result;
  }

  /**
   * Extract metadata - key + category ("uri: soste-alue", "SORA") from many languages.
   *
   * @param metas
   * @return map if language keyed translations
   */
  private Map<String, String> getMetadata(List<MonikielinenMetadata> metas) {
    Map<String, String> result = new HashMap<String, String>();

    for (MonikielinenMetadata monikielinenMetadata : metas) {
      result.put(
          getTarjontaKoodistoHelper().convertKielikoodiToKieliUri(monikielinenMetadata.getKieli()),
          monikielinenMetadata.getArvo());
    }

    return result.isEmpty() ? null : result;
  }

  private Map<String, String> getMap(MonikielinenTeksti valintaperusteKuvaus) {
    // TODO Auto-generated method stub
    return null;
  }
}
