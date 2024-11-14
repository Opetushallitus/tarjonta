package fi.vm.sade.tarjonta.service.impl.resources.v1;

import static fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO.notEmpty;
import static fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum.KORKEAKOULUOPINTO;
import static fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum.KORKEAKOULUTUS;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.publication.model.RestParam;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.service.copy.NullAwareBeanUtilsBean;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.EntityConverterToRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.KomoV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.KoodiService;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KoulutusImplicitDataPopulator {

  private static final Logger LOG = LoggerFactory.getLogger(KoulutusImplicitDataPopulator.class);

  @Autowired KoodiService koodiService;

  @Autowired KomoV1Resource komoV1Resource;

  @Autowired KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

  public static final String KOULUTUSALAOPH2002 = "koulutusalaoph2002";
  public static final String KOULUTUSASTEOPH2002 = "koulutusasteoph2002";
  public static final String OPINTOALAOPH2002 = "opintoalaoph2002";
  public static final String EQF = "eqf";
  public static final String TUTKINTO = "tutkinto";
  public static final String TUTKINTONIMIKEKK = "tutkintonimikekk";
  public static final String KOULUTUSLAJI = "koulutuslaji";
  public static final String POHJAKOULUTUSVAATIMUS_TOINEN_ASTE = "pohjakoulutusvaatimustoinenaste";
  public static final String TUTKINTONIMIKKEET = "tutkintonimikkeet";
  public static final String OPINTOJEN_LAAJUUS = "opintojenlaajuus";
  public static final String OPINTOJEN_LAAJUUS_YKSIKKO = "opintojenlaajuusyksikko";

  private BeanUtilsBean beanUtils = new NullAwareBeanUtilsBean();

  @Autowired private EntityConverterToRDTO converterToRDTO;

  @Autowired private ContextDataService contextDataService;

  public KoulutusV1RDTO populateFields(KoulutusV1RDTO dto)
      throws IllegalAccessException, InvocationTargetException {
    populateFieldsByKoulutustyyppi(dto);
    populateFieldsByKoulutuskoodi(dto);
    populateFieldsByOsaamisalakoodi(dto);

    KoulutusmoduuliToteutus komoto =
        koulutusmoduuliToteutusDAO.findKomotoByKoulutusId(
            new KoulutusIdentification(dto.getOid(), dto.getUniqueExternalId()));

    if (komoto != null) {
      KoulutusV1RDTO originalDto =
          converterToRDTO.convert(
              dto.getClass(),
              komoto,
              RestParam.noImageAndShowMeta(contextDataService.getCurrentUserLang()));

      // Tekstis must be handled separately, because they are in a map-structure
      // and we must support delta edit of individual map entries
      dto.setKuvausKomoto(
          mergeKuvaus(KomotoTeksti.class, dto.getKuvausKomoto(), originalDto.getKuvausKomoto()));
      dto.setKuvausKomo(
          mergeKuvaus(KomoTeksti.class, dto.getKuvausKomo(), originalDto.getKuvausKomo()));

      beanUtils.copyProperties(originalDto, dto);
      dto = originalDto;
    }

    dto = defaultValuesForDto(dto);

    return dto;
  }

  private static <T extends Enum> KuvausV1RDTO<T> mergeKuvaus(
      Class<T> clazz, KuvausV1RDTO dto, KuvausV1RDTO originalDto) {
    if (dto == null) {
      return originalDto;
    }

    KuvausV1RDTO<T> kuvaus = new KuvausV1RDTO<T>();
    kuvaus.putAll(originalDto);
    kuvaus.putAll(dto);
    return kuvaus;
  }

  public KoulutusV1RDTO defaultValuesForDto(KoulutusV1RDTO dto) {
    if (dto instanceof KoulutusKorkeakouluV1RDTO) {
      dto.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
    }
    try {
      KoulutusV1RDTO defaultDto = dto.getClass().newInstance();
      defaultDto.setOpetusTarjoajat(new HashSet<String>());
      defaultDto.setOpetusJarjestajat(new HashSet<String>());
      defaultDto.setYhteyshenkilos(new HashSet<YhteyshenkiloTyyppi>());
      defaultDto.setOrganisaatio(new OrganisaatioV1RDTO());
      defaultDto.setKuvausKomo(new KuvausV1RDTO<KomoTeksti>());
      defaultDto.setKuvausKomoto(new KuvausV1RDTO<KomotoTeksti>());
      defaultDto.setKoulutusohjelma(new NimiV1RDTO());
      defaultDto.setKoulutuksenAlkamisPvms(new HashSet<Date>());
      defaultDto.setOpetuskielis(new KoodiUrisV1RDTO());
      defaultDto.setOpetusmuodos(new KoodiUrisV1RDTO());
      defaultDto.setOpetusAikas(new KoodiUrisV1RDTO());
      defaultDto.setOpetusPaikkas(new KoodiUrisV1RDTO());
      defaultDto.setAmmattinimikkeet(new KoodiUrisV1RDTO());
      defaultDto.setAihees(new KoodiUrisV1RDTO());
      defaultDto.setOpintojenMaksullisuus(false);
      defaultDto.setIsAvoimenYliopistonKoulutus(false);

      beanUtils.copyProperties(defaultDto, dto);
      return defaultDto;
    } catch (Throwable t) {
      throw Throwables.propagate(t);
    }
  }

  private void populateFieldsByKoulutustyyppi(final KoulutusV1RDTO dto) {
    List<KoodiType> sisaltaaKoodit = getAlapuolisetKoodit(dto.getToteutustyyppi().uri());

    List<KoodiV1RDTO> koulutuslajis = findCodes(sisaltaaKoodit, KOULUTUSLAJI);
    if (koulutuslajis.size() == 1) {
      dto.setKoulutuslaji(koulutuslajis.get(0));
    }

    if (dto instanceof KoulutusGenericV1RDTO) {
      List<KoodiV1RDTO> pkVaatimukset =
          findCodes(sisaltaaKoodit, POHJAKOULUTUSVAATIMUS_TOINEN_ASTE);
      if (pkVaatimukset.size() == 1) {
        ((KoulutusGenericV1RDTO) dto).setPohjakoulutusvaatimus(pkVaatimukset.get(0));
      }
    }
  }

  private void populateFieldsByKoulutuskoodi(final KoulutusV1RDTO dto) {
    if (notEmpty(dto.getKoulutuskoodi())) {
      List<KoodiType> sisaltaaKoodit = getAlapuolisetKoodit(dto.getKoulutuskoodi().getUri());

      dto.setKoulutusala(findCode(sisaltaaKoodit, KOULUTUSALAOPH2002));
      dto.setKoulutusaste(findCode(sisaltaaKoodit, KOULUTUSASTEOPH2002));
      dto.setOpintoala(findCode(sisaltaaKoodit, OPINTOALAOPH2002));
      dto.setEqf(findCode(sisaltaaKoodit, EQF));
      dto.setTutkinto(findCode(sisaltaaKoodit, TUTKINTO));

      setTutkintonimike(sisaltaaKoodit, dto);
      populateLaajuusAndLaajuusYksikko(dto, sisaltaaKoodit);
      populateKomoOid(dto);
    }
  }

  private void populateLaajuusAndLaajuusYksikko(
      final KoulutusV1RDTO dto, final List<KoodiType> sisaltaaKoodit) {
    if (dto.getOpintojenLaajuusarvo() == null) {
      KoodiV1RDTO laajuus = findCode(sisaltaaKoodit, OPINTOJEN_LAAJUUS);
      if (laajuus != null) {
        dto.setOpintojenLaajuusarvo(laajuus);
      }
    }
    if (dto.getOpintojenLaajuusyksikko() == null) {
      KoodiV1RDTO laajuusYksikko = findCode(sisaltaaKoodit, OPINTOJEN_LAAJUUS_YKSIKKO);
      if (laajuusYksikko != null) {
        dto.setOpintojenLaajuusyksikko(laajuusYksikko);
      }
    }
  }

  private int getLatestKoodiVersion(final String koodiUri) {
    List<KoodiType> koodis =
        koodiService.searchKoodis(
            new SearchKoodisCriteriaType() {
              {
                getKoodiUris().add(koodiUri);
              }
            });

    if (koodis.size() == 1) {
      return koodis.get(0).getVersio();
    } else {
      LOG.error(
          "Failed to get latest koodi version: expected 1 but found {} codes for koodi {}",
          koodis.size(),
          koodiUri);
      int defaultVersion = 1;
      return defaultVersion;
    }
  }

  private void populateFieldsByOsaamisalakoodi(final KoulutusV1RDTO dto) {
    if (notEmpty(dto.getKoulutusohjelma())) {
      List<KoodiType> sisaltaaKoodit = getAlapuolisetKoodit(dto.getKoulutusohjelma().getUri());

      setTutkintonimike(sisaltaaKoodit, dto);
      populateLaajuusAndLaajuusYksikko(dto, sisaltaaKoodit);
    }
  }

  private void populateKomoOid(final KoulutusV1RDTO dto) {
    Set<ToteutustyyppiEnum> doesntUseExistingKomo =
        Sets.newHashSet(KORKEAKOULUOPINTO, KORKEAKOULUTUS);
    if (doesntUseExistingKomo.contains(dto.getToteutustyyppi())
        || !StringUtils.isBlank(dto.getKomoOid())) {
      return;
    }

    KoulutusmoduuliTyyppi type = KoulutusmoduuliTyyppi.TUTKINTO;
    String koulutusohjelma = null;
    if (notEmpty(dto.getKoulutusohjelma())) {
      type = KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA;
      koulutusohjelma = dto.getKoulutusohjelma().getUri();
    }
    ResultV1RDTO<List<ModuuliTuloksetV1RDTO>> komoRes =
        komoV1Resource.searchModule(
            dto.getToteutustyyppi(), type, dto.getKoulutuskoodi().getUri(), koulutusohjelma, null);

    if (komoRes.getResult().size() == 1) {
      dto.setKomoOid(komoRes.getResult().get(0).getOid());
    } else if (komoRes.getResult().size() > 1) {
      throwError(
          "Multiple koulutusmoduulis found, missing osaamisala/koulutusohjelma? Osaamisala/koulutusohjelma: "
              + koulutusohjelma);
    } else {
      throwError("No matching koulutusmoduuli found for koulutusohjelma: " + koulutusohjelma);
    }
  }

  private void throwError(String msg) {
    LOG.error(msg);
    throw new RuntimeException(msg);
  }

  private KoodiV1RDTO findCode(final List<KoodiType> codes, final String koodisto) {
    KoodiType code = (KoodiType) Iterables.find(codes, matchKoodisto(koodisto), null);
    if (code == null) {
      return null;
    }
    return new KoodiV1RDTO(code.getKoodiUri(), code.getVersio(), code.getKoodiArvo());
  }

  private List<KoodiV1RDTO> findCodes(final List<KoodiType> codes, final String koodisto) {
    return FluentIterable.from(codes)
        .filter(matchKoodisto(koodisto))
        .transform(
            (Function<KoodiType, KoodiV1RDTO>)
                input ->
                    new KoodiV1RDTO(input.getKoodiUri(), input.getVersio(), input.getKoodiArvo()))
        .toList();
  }

  private void setTutkintonimike(List<KoodiType> sisaltaaKoodit, KoulutusV1RDTO dto) {
    if (dto instanceof KoulutusKorkeakouluV1RDTO) {
      List<KoodiV1RDTO> tutkintonimikes = findCodes(sisaltaaKoodit, TUTKINTONIMIKEKK);
      if (tutkintonimikes.size() == 1) {
        ((KoulutusKorkeakouluV1RDTO) dto)
            .setTutkintonimikes(
                new KoodiUrisV1RDTO(
                    ImmutableMap.of(
                        tutkintonimikes.get(0).getUri(), tutkintonimikes.get(0).getVersio())));
      }
    } else if (dto instanceof Koulutus2AsteV1RDTO) {
      List<KoodiV1RDTO> tutkintonimikes = findCodes(sisaltaaKoodit, TUTKINTONIMIKKEET);
      if (tutkintonimikes.size() == 1) {
        ((Koulutus2AsteV1RDTO) dto).setTutkintonimike(tutkintonimikes.get(0));
      }
    }
  }

  private static Predicate matchKoodisto(final String koodisto) {
    return (Predicate<KoodiType>)
        candidate -> koodisto.equals(candidate.getKoodisto().getKoodistoUri());
  }

  private List<KoodiType> getAlapuolisetKoodit(final String koodiUri) {
    KoodiUriAndVersioType koodiWithVersion = new KoodiUriAndVersioType();
    koodiWithVersion.setKoodiUri(koodiUri);
    koodiWithVersion.setVersio(getLatestKoodiVersion(koodiUri));

    return koodiService.listKoodiByRelation(koodiWithVersion, false, SuhteenTyyppiType.SISALTYY);
  }
}
