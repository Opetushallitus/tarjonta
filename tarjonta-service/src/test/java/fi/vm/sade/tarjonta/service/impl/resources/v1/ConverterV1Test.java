package fi.vm.sade.tarjonta.service.impl.resources.v1;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.TestMockBase;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.business.exception.DataErrorException;
import fi.vm.sade.tarjonta.service.impl.resources.v1.util.YhdenPaikanSaantoBuilder;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.shared.KoodistoProactiveCaching;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import java.math.BigDecimal;
import java.util.*;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.internal.util.reflection.Whitebox;

public class ConverterV1Test extends TestMockBase {

  private String ataruLomakeAvain = "01234567-89ab-cdef-0123-4567890abcdef";

  private static final String ALKAMISKAUSI = "kausi_S";
  private static final Integer ALKAMISVUOSI = 1999;
  @InjectMocks private ConverterV1 converter;

  YhdenPaikanSaantoBuilder yhdenPaikanSaantoBuilder = mock(YhdenPaikanSaantoBuilder.class);

  @Before
  public void init() {
    Whitebox.setInternalState(tarjontaKoodistoHelper, "koodiService", koodiService);
    Whitebox.setInternalState(
        tarjontaKoodistoHelper, "koodistoProactiveCaching", mock(KoodistoProactiveCaching.class));
    Whitebox.setInternalState(converter, "tarjontaKoodistoHelper", tarjontaKoodistoHelper);
    Whitebox.setInternalState(converter, "yhdenPaikanSaantoBuilder", yhdenPaikanSaantoBuilder);
  }

  private KoulutusmoduuliToteutus initKomoto() {
    KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
    komoto.setAlkamiskausiUri(ALKAMISKAUSI);
    komoto.setAlkamisVuosi(ALKAMISVUOSI);
    return komoto;
  }

  private void setKomotoForHakukohde(Hakukohde hakukohde) {
    KoulutusmoduuliToteutus komoto = initKomoto();
    komoto.setToteutustyyppi(ToteutustyyppiEnum.KORKEAKOULUTUS);
    Koulutusmoduuli komo = new Koulutusmoduuli();
    komo.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
    komoto.setKoulutusmoduuli(komo);
    hakukohde.setKoulutusmoduuliToteutuses(Sets.newHashSet(komoto));
  }

  @Test
  public void thatPainotettavatOppiaineetAreConverted() {
    Hakukohde hakukohde = new Hakukohde();
    hakukohde.setHaku(mock(Haku.class));
    hakukohde.setTila(TarjontaTila.JULKAISTU);
    setKomotoForHakukohde(hakukohde);

    when(tarjontaKoodistoHelper.getHakukelpoisuusvaatimusrymaUriForHakukohde(anyString()))
        .thenReturn(null);

    addPainotettavatOppiaineet(hakukohde);

    HakukohdeV1RDTO hakukohdeDTO = converter.toHakukohdeRDTO(hakukohde);

    assertTrue(hakukohdeDTO.getPainotettavatOppiaineet().size() == 1);

    PainotettavaOppiaineV1RDTO painotettavaOppiaineDTO =
        hakukohdeDTO.getPainotettavatOppiaineet().get(0);

    assertEquals("painotettavatoppiaineetlukiossa_ge#1", painotettavaOppiaineDTO.getOppiaineUri());
    assertEquals(new BigDecimal("2.5"), painotettavaOppiaineDTO.getPainokerroin());
    assertEquals("57982", painotettavaOppiaineDTO.getOid());
    assertTrue(hakukohdeDTO.getPainotettavatOppiaineet().size() == 1);
  }

  private void addPainotettavatOppiaineet(Hakukohde hakukohde) {
    Set<PainotettavaOppiaine> painotettavatOppiaineet = new HashSet<PainotettavaOppiaine>();

    PainotettavaOppiaine oppiaine = new PainotettavaOppiaine();
    oppiaine.setPainokerroin(new BigDecimal("2.5"));
    oppiaine.setOppiaine("painotettavatoppiaineetlukiossa_ge#1");
    oppiaine.setId(57982L);

    painotettavatOppiaineet.add(oppiaine);

    hakukohde.setPainotettavatOppiaineet(painotettavatOppiaineet);
  }

  @Test
  public void thatLiitteetAreConverted() {
    Hakukohde hakukohde = new Hakukohde();
    hakukohde.setHaku(mock(Haku.class));
    hakukohde.setTila(TarjontaTila.JULKAISTU);
    setKomotoForHakukohde(hakukohde);

    when(koodiService.searchKoodis(Matchers.any(SearchKoodisCriteriaType.class)))
        .thenReturn(Lists.newArrayList(mock(KoodiType.class)));

    addLiitteet(hakukohde);

    HakukohdeV1RDTO hakukohdeDTO = converter.toHakukohdeRDTO(hakukohde);

    HakukohdeLiiteV1RDTO hakukohdeLiiteDTO = hakukohdeDTO.getHakukohteenLiitteet().get(0);

    assertEquals("liitetyypitamm_3#1", hakukohdeLiiteDTO.getLiitteenTyyppi());
    assertTrue(hakukohdeDTO.getHakukohteenLiitteet().size() == 1);
  }

  private void addLiitteet(Hakukohde hakukohde) {
    HakukohdeLiite hakukohdeLiite = new HakukohdeLiite();
    hakukohdeLiite.setKieli("kieli_fi");
    hakukohdeLiite.setLiitetyyppi("liitetyypitamm_3#1");
    hakukohde.addLiite(hakukohdeLiite);
  }

  @Test
  public void thatValintakoeDTOsAreConvertedToEntity() {
    HakukohdeV1RDTO hakukohdeDTO = getHakukohdeDTO();

    Hakukohde hakukohde = converter.toHakukohde(hakukohdeDTO);
    assertTrue(hakukohde.getValintakoes().size() == 1);

    Valintakoe valintakoe = hakukohde.getValintakoes().iterator().next();
    assertTrue(valintakoe.getAjankohtas().size() == 1);

    ValintakoeAjankohta valintakoeAjankohta = valintakoe.getAjankohtas().iterator().next();
    assertTrue(valintakoeAjankohta.isKellonaikaKaytossa());
  }

  @Test
  public void thatValintakoeDTOsDatesCanBeNullOrZero() {
    HakukohdeV1RDTO hakukohdeDTO = getHakukohdeDTO();
    hakukohdeDTO.setHakuaikaAlkuPvm(new Date(0));
    hakukohdeDTO.setHakuaikaLoppuPvm(null);

    Hakukohde hakukohde = converter.toHakukohde(hakukohdeDTO);
    assertNull(hakukohde.getHakuaikaAlkuPvm());
    assertNull(hakukohde.getHakuaikaLoppuPvm());
  }

  @Test
  public void thatValintakoeDTOsDatesAreConverted() {
    HakukohdeV1RDTO hakukohdeDTO = getHakukohdeDTO();
    Date alkuPvm = new Date(20);
    Date loppuPvm = new Date(2000);
    hakukohdeDTO.setHakuaikaAlkuPvm(alkuPvm);
    hakukohdeDTO.setHakuaikaLoppuPvm(loppuPvm);

    Hakukohde hakukohde = converter.toHakukohde(hakukohdeDTO);
    assertEquals(alkuPvm, hakukohde.getHakuaikaAlkuPvm());
    assertEquals(loppuPvm, hakukohde.getHakuaikaLoppuPvm());
  }

  @Test
  public void thatAloituspaikatKuvauksetIsNotConverted() {
    HakukohdeV1RDTO hakukohdeDTO = getHakukohdeDTO();
    Map<String, String> aloituspaikatKuvaukset = new HashMap<String, String>();
    aloituspaikatKuvaukset.put("fi", "kymmenen");
    aloituspaikatKuvaukset.put("sv", "tio");
    hakukohdeDTO.setAloituspaikatKuvaukset(aloituspaikatKuvaukset);
    String oid = "9.8.7";
    hakukohdeDTO.setOid(oid);

    Hakukohde hakukohde = converter.toHakukohde(hakukohdeDTO);
    assertEquals(oid, hakukohde.getOid());
    assertNull(hakukohde.getAloituspaikatKuvaus());
  }

  @Test
  public void thatValintakokeetAreConvertedToDTO() {
    when(tarjontaKoodistoHelper.getHakukelpoisuusvaatimusrymaUriForHakukohde(anyString()))
        .thenReturn(null);

    Hakukohde hakukohde = getHakukohde();
    setKomotoForHakukohde(hakukohde);

    HakukohdeV1RDTO hakukohdeDTO = converter.toHakukohdeRDTO(hakukohde);

    assertTrue(hakukohdeDTO.getValintakokeet().size() == 1);

    ValintakoeV1RDTO valintakoeDTO = hakukohdeDTO.getValintakokeet().get(0);
    assertTrue(valintakoeDTO.getValintakoeAjankohtas().size() == 1);

    ValintakoeAjankohtaRDTO valintakoeAjankohtaDTO = valintakoeDTO.getValintakoeAjankohtas().get(0);
    assertTrue(valintakoeAjankohtaDTO.isKellonaikaKaytossa());
  }

  private Hakukohde getHakukohde() {
    Hakukohde hakukohde = new Hakukohde();
    hakukohde.setTila(TarjontaTila.JULKAISTU);
    hakukohde.setHaku(new Haku());
    hakukohde.addValintakoe(getValintakoe());
    return hakukohde;
  }

  private Valintakoe getValintakoe() {
    Valintakoe valintakoe = new Valintakoe();
    valintakoe.setId(12345L);
    valintakoe.setVersion(0L);
    valintakoe.setAjankohtas(getValintakoeAjankohdat());
    return valintakoe;
  }

  private Set<ValintakoeAjankohta> getValintakoeAjankohdat() {
    ValintakoeAjankohta valintakoeAjankohta = new ValintakoeAjankohta();
    return new HashSet<ValintakoeAjankohta>(Arrays.asList(valintakoeAjankohta));
  }

  private HakukohdeV1RDTO getHakukohdeDTO() {
    HakukohdeV1RDTO hakukohdeDTO = new HakukohdeV1RDTO();
    hakukohdeDTO = converter.setDefaultValues(hakukohdeDTO);
    hakukohdeDTO.setTila(TarjontaTila.JULKAISTU);
    hakukohdeDTO.setValintakokeet(getValintakokeetDTOs());
    hakukohdeDTO.setToteutusTyyppi(ToteutustyyppiEnum.LUKIOKOULUTUS);
    return hakukohdeDTO;
  }

  private List<ValintakoeV1RDTO> getValintakokeetDTOs() {
    ValintakoeV1RDTO valintakoeDTO = new ValintakoeV1RDTO();
    valintakoeDTO.setValintakoeAjankohtas(getValintakoeAjankohtaDTOs());
    return Arrays.asList(valintakoeDTO);
  }

  private List<ValintakoeAjankohtaRDTO> getValintakoeAjankohtaDTOs() {
    ValintakoeAjankohtaRDTO valintakoeAjankohtaDTO = new ValintakoeAjankohtaRDTO();
    return Arrays.asList(valintakoeAjankohtaDTO);
  }

  @Test
  public void thatHakukohdeWithKoulutusmoduuliTarjontatiedotAreConvertedToDTO() {
    Hakukohde hakukohde = getHakukohde();

    Koulutusmoduuli komo = new Koulutusmoduuli();
    komo.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
    KoulutusmoduuliToteutus komoto = initKomoto();
    komoto.setKoulutusmoduuli(komo);
    komoto.setOid("1.2.3");

    KoulutusmoduuliToteutus komotoPoistettu = initKomoto();
    komotoPoistettu.setTila(TarjontaTila.POISTETTU);
    komotoPoistettu.setKoulutusmoduuli(komo);
    komotoPoistettu.setOid("5.5.5.5.5");

    KoulutusmoduuliToteutus komotoPeruttu = initKomoto();
    komotoPeruttu.setTila(TarjontaTila.PERUTTU);
    komotoPeruttu.setKoulutusmoduuli(komo);
    komotoPeruttu.setOid("5.5.5.5.5.8");

    hakukohde.setKoulutusmoduuliToteutuses(Sets.newHashSet(komoto, komotoPoistettu, komotoPeruttu));

    KoulutusmoduuliToteutusTarjoajatiedot tarjoajatiedot =
        new KoulutusmoduuliToteutusTarjoajatiedot();
    tarjoajatiedot.getTarjoajaOids().add("4.5.6");
    hakukohde.getKoulutusmoduuliToteutusTarjoajatiedot().put(komoto.getOid(), tarjoajatiedot);

    HakukohdeV1RDTO hakukohdeDTO = converter.toHakukohdeRDTO(hakukohde);

    Map<String, KoulutusmoduuliTarjoajatiedotV1RDTO> tarjoajatiedotMap =
        hakukohdeDTO.getKoulutusmoduuliToteutusTarjoajatiedot();

    assertEquals(hakukohde.getKoulutusmoduuliToteutuses().size(), 2);
    Set<String> assertKomotoOids = new HashSet<String>();
    for (KoulutusmoduuliToteutus tmpKomoto : hakukohde.getKoulutusmoduuliToteutuses()) {
      assertKomotoOids.add(tmpKomoto.getOid());
    }
    assertTrue(assertKomotoOids.contains(komoto.getOid()));
    assertTrue(assertKomotoOids.contains(komotoPeruttu.getOid()));
    assertTrue(tarjoajatiedotMap.size() == 1);
    assertTrue(tarjoajatiedotMap.containsKey(komoto.getOid()));
    assertTrue(tarjoajatiedotMap.get(komoto.getOid()).getTarjoajaOids().size() == 1);
    assertEquals(
        "4.5.6", tarjoajatiedotMap.get(komoto.getOid()).getTarjoajaOids().iterator().next());
  }

  @Test
  public void thatHakukohdeWithoutKoulutusmoduuliTarjoajatiedotAreConvertedToDTO() {
    when(organisaatioService.findByOid("4.5.6")).thenReturn(null);

    Hakukohde hakukohde = getHakukohde();
    KoulutusmoduuliToteutus koulutusmoduuliToteutus = initKomoto();
    koulutusmoduuliToteutus.setOid("1.2.3");
    koulutusmoduuliToteutus.setTarjoaja("4.5.6");

    Koulutusmoduuli koulutusmoduuli = new Koulutusmoduuli();
    koulutusmoduuli.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
    koulutusmoduuliToteutus.setKoulutusmoduuli(koulutusmoduuli);

    hakukohde.addKoulutusmoduuliToteutus(koulutusmoduuliToteutus);

    HakukohdeV1RDTO hakukohdeDTO = converter.toHakukohdeRDTO(hakukohde);

    Map<String, KoulutusmoduuliTarjoajatiedotV1RDTO> tarjoajatiedotMap =
        hakukohdeDTO.getKoulutusmoduuliToteutusTarjoajatiedot();

    assertTrue(tarjoajatiedotMap.size() == 1);
    assertTrue(tarjoajatiedotMap.containsKey("1.2.3"));
    assertTrue(tarjoajatiedotMap.get("1.2.3").getTarjoajaOids().size() == 1);
    assertEquals("4.5.6", tarjoajatiedotMap.get("1.2.3").getTarjoajaOids().iterator().next());
  }

  @Test
  public void thatHakukohdekohtainenHakuaikaIsConvertedToDTO() {
    Date alkuPvm = new Date();
    Date loppuPvm = new Date();

    Hakukohde hakukohde = getHakukohde();
    hakukohde.setHakuaikaAlkuPvm(alkuPvm);
    hakukohde.setHakuaikaLoppuPvm(loppuPvm);
    setKomotoForHakukohde(hakukohde);

    HakukohdeV1RDTO hakukohdeV1RDTO = converter.toHakukohdeRDTO(hakukohde);

    assertEquals(alkuPvm, hakukohdeV1RDTO.getHakuaikaAlkuPvm());
    assertEquals(loppuPvm, hakukohdeV1RDTO.getHakuaikaLoppuPvm());
    assertTrue(hakukohdeV1RDTO.getKaytetaanHakukohdekohtaistaHakuaikaa());
  }

  @Test
  public void thatHakukohdekohtainenHakuaikaCanBeZero() {
    Date alkuPvm = new Date(0);
    Date loppuPvm = new Date();

    Hakukohde hakukohde = getHakukohde();
    hakukohde.setHakuaikaAlkuPvm(alkuPvm);
    hakukohde.setHakuaikaLoppuPvm(loppuPvm);
    setKomotoForHakukohde(hakukohde);

    HakukohdeV1RDTO hakukohdeV1RDTO = converter.toHakukohdeRDTO(hakukohde);

    assertFalse(hakukohdeV1RDTO.getKaytetaanHakukohdekohtaistaHakuaikaa());
  }

  @Test
  public void thatHakukohdekohtainenHakuaikaCanBeNull() {
    Date alkuPvm = null;
    Date loppuPvm = new Date();

    Hakukohde hakukohde = getHakukohde();
    hakukohde.setHakuaikaAlkuPvm(alkuPvm);
    hakukohde.setHakuaikaLoppuPvm(loppuPvm);
    setKomotoForHakukohde(hakukohde);

    HakukohdeV1RDTO hakukohdeV1RDTO = converter.toHakukohdeRDTO(hakukohde);

    assertFalse(hakukohdeV1RDTO.getKaytetaanHakukohdekohtaistaHakuaikaa());
  }

  @Test
  public void thatSisaltyvatHautAreConvertedToDTO() {
    Haku haku = createValidHaku();

    HakuV1RDTO hakuDTO = converter.fromHakuToHakuRDTO(haku, false);

    assertTrue(hakuDTO.getSisaltyvatHaut().isEmpty());

    Haku sisaltyvaHaku = new Haku();
    sisaltyvaHaku.setOid("1.2.3");
    haku.getSisaltyvatHaut().add(sisaltyvaHaku);

    hakuDTO = converter.fromHakuToHakuRDTO(haku, false);

    assertTrue(hakuDTO.getSisaltyvatHaut().size() == 1);
    assertEquals("1.2.3", hakuDTO.getSisaltyvatHaut().iterator().next());
  }

  @Test
  public void thatHaunKoulutusmoduuliTyyppiIsConvertedToDTO() {
    Haku haku = createValidHaku();

    HakuV1RDTO hakuDTO = converter.fromHakuToHakuRDTO(haku, false);
    assertNull(hakuDTO.getKoulutusmoduuliTyyppi());

    haku.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.OPINTOKOKONAISUUS);

    hakuDTO = converter.fromHakuToHakuRDTO(haku, false);
    assertEquals(
        fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.OPINTOKOKONAISUUS,
        hakuDTO.getKoulutusmoduuliTyyppi());
  }

  @Test
  public void thatHaunKoulutusmoduuliTyyppiIsConvertedToEntity() throws OIDCreationException {
    HakuV1RDTO hakuDTO = new HakuV1RDTO();

    Haku haku = converter.convertHakuV1DRDTOToHaku(hakuDTO, new Haku());
    assertNull(haku.getKoulutusmoduuliTyyppi());

    hakuDTO.setKoulutusmoduuliTyyppi(
        fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.OPINTOJAKSO);

    haku = converter.convertHakuV1DRDTOToHaku(hakuDTO, new Haku());
    assertEquals(KoulutusmoduuliTyyppi.OPINTOJAKSO, haku.getKoulutusmoduuliTyyppi());

    haku = new Haku();
    haku.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.OPINTOKOKONAISUUS);
    hakuDTO.setKoulutusmoduuliTyyppi(null);

    haku = converter.convertHakuV1DRDTOToHaku(hakuDTO, haku);
    assertNull(haku.getKoulutusmoduuliTyyppi());
  }

  private Haku createValidHaku() {
    Haku haku = new Haku();
    haku.setTila(TarjontaTila.JULKAISTU);
    haku.setHakukausiVuosi(2014);
    return haku;
  }

  @Test
  public void thatParentHakuIsConvertedToDTO() {
    Haku haku = createValidHaku();

    HakuV1RDTO hakuDTO = converter.fromHakuToHakuRDTO(haku, false);

    assertNull(hakuDTO.getParentHakuOid());

    Haku parentHaku = new Haku();
    parentHaku.setOid("1.2.3");
    haku.setParentHaku(parentHaku);

    hakuDTO = converter.fromHakuToHakuRDTO(haku, false);

    assertEquals("1.2.3", hakuDTO.getParentHakuOid());
  }

  @Test
  public void thatParentHakuIsConvertedToEntity() throws OIDCreationException {
    HakuV1RDTO hakuDTO = new HakuV1RDTO();
    hakuDTO.setParentHakuOid("1.2.3");

    Haku parentHaku = new Haku();
    parentHaku.setOid("1.2.3");

    when(hakuDAO.findByOid("1.2.3")).thenReturn(parentHaku);

    Haku haku = converter.convertHakuV1DRDTOToHaku(hakuDTO, new Haku());

    assertEquals(haku.getParentHaku(), parentHaku);

    hakuDTO.setParentHakuOid(null);

    haku = converter.convertHakuV1DRDTOToHaku(hakuDTO, new Haku());

    assertNull(haku.getParentHaku());
  }

  @Test
  public void thatLiiteIsConvertedToEntity() {
    HakukohdeLiiteV1RDTO hakukohdeLiiteDTO = new HakukohdeLiiteV1RDTO();
    hakukohdeLiiteDTO.setLiitteenToimitusOsoite(new OsoiteRDTO());

    HakukohdeLiite hakukohdeLiite = converter.toHakukohdeLiite(hakukohdeLiiteDTO);
    assertTrue(hakukohdeLiite.isKaytetaanHakulomakkeella());

    hakukohdeLiiteDTO.setKaytetaanHakulomakkeella(false);

    hakukohdeLiite = converter.toHakukohdeLiite(hakukohdeLiiteDTO);
    assertFalse(hakukohdeLiite.isKaytetaanHakulomakkeella());
  }

  @Test
  public void thatLiiteIsConvertedToDTO() {
    HakukohdeLiite hakukohdeLiite = new HakukohdeLiite();

    HakukohdeLiiteV1RDTO hakukohdeLiiteDTO = converter.fromHakukohdeLiite(hakukohdeLiite);
    assertTrue(hakukohdeLiiteDTO.isKaytetaanHakulomakkeella());

    hakukohdeLiite.setKaytetaanHakulomakkeella(false);

    hakukohdeLiiteDTO = converter.fromHakukohdeLiite(hakukohdeLiite);
    assertFalse(hakukohdeLiiteDTO.isKaytetaanHakulomakkeella());
  }

  @Test
  public void thatRyhmaliitoksetAreConvertedToDTO() {
    Hakukohde hakukohde = getHakukohde();
    setKomotoForHakukohde(hakukohde);

    Ryhmaliitos ryhmaliitos = new Ryhmaliitos();
    ryhmaliitos.setHakukohde(hakukohde);
    ryhmaliitos.setPrioriteetti(1);
    ryhmaliitos.setRyhmaOid("1.2.3");
    hakukohde.getRyhmaliitokset().add(ryhmaliitos);

    ryhmaliitos = new Ryhmaliitos();
    ryhmaliitos.setHakukohde(hakukohde);
    ryhmaliitos.setRyhmaOid("4.5.6");
    hakukohde.getRyhmaliitokset().add(ryhmaliitos);

    HakukohdeV1RDTO hakukohdeDTO = converter.toHakukohdeRDTO(hakukohde);

    assertTrue(hakukohdeDTO.getRyhmaliitokset().size() == 2);
    assertThat(hakukohdeDTO.getRyhmaliitokset(), hasItem(getRyhmaliitosElementMatcher("1.2.3", 1)));
    assertThat(
        hakukohdeDTO.getRyhmaliitokset(), hasItem(getRyhmaliitosElementMatcher("4.5.6", null)));
  }

  @Test
  public void thatOrganisaatioRyhmaOidsAreConvertedToDTO() {
    Hakukohde hakukohde = getHakukohde();
    setKomotoForHakukohde(hakukohde);

    Ryhmaliitos ryhmaliitos = new Ryhmaliitos();
    ryhmaliitos.setHakukohde(hakukohde);
    ryhmaliitos.setPrioriteetti(1);
    ryhmaliitos.setRyhmaOid("1.2.3");
    hakukohde.getRyhmaliitokset().add(ryhmaliitos);

    ryhmaliitos = new Ryhmaliitos();
    ryhmaliitos.setHakukohde(hakukohde);
    ryhmaliitos.setRyhmaOid("4.5.6");
    hakukohde.getRyhmaliitokset().add(ryhmaliitos);

    HakukohdeV1RDTO hakukohdeDTO = converter.toHakukohdeRDTO(hakukohde);

    assertTrue(hakukohdeDTO.getRyhmaliitokset().size() == 2);
    assertThat(Arrays.asList(hakukohdeDTO.getOrganisaatioRyhmaOids()), hasItem("1.2.3"));
    assertThat(Arrays.asList(hakukohdeDTO.getOrganisaatioRyhmaOids()), hasItem("4.5.6"));
  }

  @Test
  public void thatYlioppilastutkintoAntaaHakukelpoisuudenIsConverted() {
    Hakukohde hakukohde = getHakukohde();
    setKomotoForHakukohde(hakukohde);
    Haku haku = hakukohde.getHaku();

    // Kun hakukohteelle ei ole asetettu valintaa (haun valinta = true)
    haku.setYlioppilastutkintoAntaaHakukelpoisuuden(true);
    HakukohdeV1RDTO dto = converter.toHakukohdeRDTO(hakukohde);
    assertEquals(dto.getYlioppilastutkintoAntaaHakukelpoisuuden().booleanValue(), true);

    // Kun hakukohteelle ei ole asetettu valintaa (haun valinta = false)
    haku.setYlioppilastutkintoAntaaHakukelpoisuuden(false);
    dto = converter.toHakukohdeRDTO(hakukohde);
    assertEquals(dto.getYlioppilastutkintoAntaaHakukelpoisuuden().booleanValue(), false);

    // Kun hakukohteen valinta = true (haun valinta = true)
    haku.setYlioppilastutkintoAntaaHakukelpoisuuden(true);
    hakukohde.setYlioppilastutkintoAntaaHakukelpoisuuden(true);
    dto = converter.toHakukohdeRDTO(hakukohde);
    assertEquals(dto.getYlioppilastutkintoAntaaHakukelpoisuuden().booleanValue(), true);

    // Kun hakukohteen valinta = false (haun valinta = true)
    haku.setYlioppilastutkintoAntaaHakukelpoisuuden(true);
    hakukohde.setYlioppilastutkintoAntaaHakukelpoisuuden(false);
    dto = converter.toHakukohdeRDTO(hakukohde);
    assertEquals(dto.getYlioppilastutkintoAntaaHakukelpoisuuden().booleanValue(), false);

    // Kun hakukohteen valinta = true (haun valinta = false)
    haku.setYlioppilastutkintoAntaaHakukelpoisuuden(false);
    hakukohde.setYlioppilastutkintoAntaaHakukelpoisuuden(true);
    dto = converter.toHakukohdeRDTO(hakukohde);
    assertEquals(dto.getYlioppilastutkintoAntaaHakukelpoisuuden().booleanValue(), true);

    // Kun hakukohteen valinta = false (haun valinta = false)
    haku.setYlioppilastutkintoAntaaHakukelpoisuuden(false);
    hakukohde.setYlioppilastutkintoAntaaHakukelpoisuuden(false);
    dto = converter.toHakukohdeRDTO(hakukohde);
    assertEquals(dto.getYlioppilastutkintoAntaaHakukelpoisuuden().booleanValue(), false);
  }

  @Test
  public void thatHaunYlioppilastutkintoHakukelpoisuusOidsAreConverted() {
    Haku haku = new Haku();
    when(hakukohdeDAO.findHakukohteetWithYlioppilastutkintoAntaaHakukelpoisuuden(
            Matchers.anyLong(), Matchers.anyBoolean()))
        .thenReturn(Lists.newArrayList("1.2.3", "4.5.6"));
    haku.setTila(TarjontaTila.JULKAISTU);
    haku.setHakukausiVuosi(2000);
    HakuV1RDTO dto = converter.fromHakuToHakuRDTO(haku, true);
    assertEquals(dto.getHakukohdeOidsYlioppilastutkintoAntaaHakukelpoisuuden().size(), 2);
  }

  @Test
  public void thatAtaruLomakeAvainInHakuIsConvertedToHakuDTO() {
    Haku haku = createValidHaku();
    haku.setAtaruLomakeAvain(ataruLomakeAvain);

    HakuV1RDTO hakuDTO = converter.fromHakuToHakuRDTO(haku, false);
    assertEquals(hakuDTO.getAtaruLomakeAvain(), ataruLomakeAvain);
  }

  @Test
  public void thatAtaruLomakeAvainInHakuDTOIsConvertedToHakuEntity() throws OIDCreationException {
    HakuV1RDTO hakuDTO = new HakuV1RDTO();
    hakuDTO.setAtaruLomakeAvain(ataruLomakeAvain);

    Haku haku = converter.convertHakuV1DRDTOToHaku(hakuDTO, new Haku());
    assertEquals(haku.getAtaruLomakeAvain(), ataruLomakeAvain);
  }

  @Test
  public void thatAtaruLomakeAvainFromHakuIsConvertedToHakukohdeDTO() {
    Haku haku = new Haku();
    haku.setAtaruLomakeAvain(ataruLomakeAvain);
    Hakukohde hakukohde = new Hakukohde();
    hakukohde.setHaku(haku);

    HakukohdeV1RDTO hakukohdeV1RDTO = converter.toHakukohdeRDTO(hakukohde);
    assertEquals(ataruLomakeAvain, hakukohdeV1RDTO.getAtaruLomakeAvain());
  }

  @Test
  public void thatHakuIsConvertedToAtaruLomakeDTO() {
    String oid = "haku";
    String nimiFi = "haku (fi)";
    String nimiSv = "haku (sv)";
    String nimiEn = "haku (en)";
    Haku haku = new Haku();
    haku.setOid(oid);
    haku.setNimi(TarjontaFixtures.createText(nimiFi, nimiSv, nimiEn));

    MonikielinenTeksti hakuaikaNimi = new MonikielinenTeksti();
    hakuaikaNimi.addTekstiKaannos("kieli_fi", "Testihakuaika");
    Hakuaika hakuaika = new Hakuaika();
    hakuaika.setId(new Long(1));
    hakuaika.setAlkamisPvm(new DateTime().withYear(2016).withMonthOfYear(10).toDate());
    hakuaika.setPaattymisPvm(new DateTime().withYear(2016).withMonthOfYear(11).toDate());
    hakuaika.setNimi(hakuaikaNimi);
    haku.addHakuaika(hakuaika);

    AtaruLomakeHakuV1RDTO result = converter.fromHakuToAtaruLomakeHakuRDTO(haku);

    assertEquals(oid, result.getOid());
    assertEquals(nimiFi, result.getNimi().get("kieli_fi"));
    assertEquals(nimiSv, result.getNimi().get("kieli_sv"));
    assertEquals(nimiEn, result.getNimi().get("kieli_en"));

    List<HakuaikaV1RDTO> hakuaikas = result.getHakuaikas();
    assertEquals(1, hakuaikas.size());
    assertEquals(hakuaika.getId().toString(), hakuaikas.get(0).getHakuaikaId());
    assertEquals(hakuaika.getAlkamisPvm(), hakuaikas.get(0).getAlkuPvm());
    assertEquals(hakuaika.getPaattymisPvm(), hakuaikas.get(0).getLoppuPvm());
    assertEquals(
        hakuaika.getNimi().getTekstiForKieliKoodi("kieli_fi"),
        hakuaikas.get(0).getNimet().get("kieli_fi"));
    assertEquals(
        hakuaika.getNimi().getTekstiForKieliKoodi("kieli_sv"),
        hakuaikas.get(0).getNimet().get("kieli_sv"));
    assertEquals(
        hakuaika.getNimi().getTekstiForKieliKoodi("kieli_en"),
        hakuaikas.get(0).getNimet().get("kieli_en"));
  }

  @Test
  public void thatHakukohdeDTOWithAtaruLomakeAvainConvertsToHakukohdeEntity() {
    HakukohdeV1RDTO hakukohdeDTO = getHakukohdeDTO();
    hakukohdeDTO.setAtaruLomakeAvain(ataruLomakeAvain);

    Hakukohde hakukohde = converter.toHakukohde(hakukohdeDTO);
    assertNotNull(hakukohde);
  }

  private BaseMatcher<RyhmaliitosV1RDTO> getRyhmaliitosElementMatcher(
      final String ryhmaOid, final Integer prioriteetti) {
    return new BaseMatcher<RyhmaliitosV1RDTO>() {
      @Override
      public boolean matches(Object o) {
        RyhmaliitosV1RDTO ryhmaliitosDTO = (RyhmaliitosV1RDTO) o;
        if (prioriteetti == null) {
          return ryhmaliitosDTO.getRyhmaOid().equals(ryhmaOid)
              && ryhmaliitosDTO.getPrioriteetti() == null;
        } else {
          return ryhmaliitosDTO.getRyhmaOid().equals(ryhmaOid)
              && ryhmaliitosDTO.getPrioriteetti().equals(prioriteetti);
        }
      }

      public void describeTo(Description description) {}
    };
  }

  @Test
  public void testThatKomotoDependantFieldsAreConverted() {
    when(yhdenPaikanSaantoBuilder.koulutusJohtaaTutkintoon(
            Matchers.any(KoulutusmoduuliToteutus.class)))
        .thenReturn(true);

    Hakukohde hakukohde = getHakukohde();
    setKomotoForHakukohde(hakukohde);
    HakukohdeV1RDTO h = converter.toHakukohdeRDTO(hakukohde, true);
    assertThat(h, notNullValue());
    assertThat(h.getKoulutuksenAlkamiskausiUri(), is(equalTo(ALKAMISKAUSI)));
    assertThat(h.getKoulutuksenAlkamisvuosi(), is(equalTo(ALKAMISVUOSI)));
    assertThat(h.isTutkintoonJohtava(), is(true));
  }

  @Test
  public void testThatKomotoDependantFieldsAreConvertedAndPreferJulkaistu() {
    when(yhdenPaikanSaantoBuilder.koulutusJohtaaTutkintoon(
            Matchers.any(KoulutusmoduuliToteutus.class)))
        .thenReturn(false);

    Hakukohde hakukohde = getHakukohde();

    Koulutusmoduuli komo = new Koulutusmoduuli();
    komo.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);

    KoulutusmoduuliToteutus komoto = initKomoto();
    komoto.setOid("1.2.3");
    komoto.setKoulutusmoduuli(komo);

    KoulutusmoduuliToteutus komotoPoistettu = initKomoto();
    komotoPoistettu.setTila(TarjontaTila.JULKAISTU);
    komotoPoistettu.setAlkamisVuosi(2014);
    komotoPoistettu.setOid("5.5.5.5.5");
    komotoPoistettu.setKoulutusmoduuli(komo);

    hakukohde.setKoulutusmoduuliToteutuses(Sets.newHashSet(komoto, komotoPoistettu));

    HakukohdeV1RDTO h = converter.toHakukohdeRDTO(hakukohde, true);
    assertThat(h, notNullValue());
    assertThat(h.getKoulutuksenAlkamiskausiUri(), is(equalTo(ALKAMISKAUSI)));
    assertThat(h.getKoulutuksenAlkamisvuosi(), is(equalTo(2014)));
    assertThat(h.isTutkintoonJohtava(), is(false));
  }

  @Test
  public void testThatKomotoDependantFieldsAreConvertedForNonTutkintoonJohtava() {
    when(yhdenPaikanSaantoBuilder.koulutusJohtaaTutkintoon(
            Matchers.any(KoulutusmoduuliToteutus.class)))
        .thenReturn(false);

    Hakukohde hakukohde = getHakukohde();
    setKomotoForHakukohde(hakukohde);
    HakukohdeV1RDTO h = converter.toHakukohdeRDTO(hakukohde, true);
    assertThat(h, notNullValue());
    assertThat(h.getKoulutuksenAlkamiskausiUri(), is(equalTo(ALKAMISKAUSI)));
    assertThat(h.getKoulutuksenAlkamisvuosi(), is(equalTo(ALKAMISVUOSI)));
    assertThat(h.isTutkintoonJohtava(), is(false));
  }

  @Test
  public void testThatKomotoDependantFieldsAreConvertedForPoistettu() {
    when(yhdenPaikanSaantoBuilder.koulutusJohtaaTutkintoon(
            Matchers.any(KoulutusmoduuliToteutus.class)))
        .thenReturn(false);

    Hakukohde hakukohde = getHakukohde();

    Koulutusmoduuli komo = new Koulutusmoduuli();
    komo.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);

    KoulutusmoduuliToteutus komoto = initKomoto();
    komoto.setOid("1.2.3");
    komoto.setKoulutusmoduuli(komo);

    KoulutusmoduuliToteutus komotoPoistettu = initKomoto();
    komotoPoistettu.setTila(TarjontaTila.POISTETTU);
    komotoPoistettu.setAlkamisVuosi(2014);
    komotoPoistettu.setOid("5.5.5.5.5");
    komotoPoistettu.setKoulutusmoduuli(komo);

    hakukohde.setKoulutusmoduuliToteutuses(Sets.newHashSet(komoto, komotoPoistettu));

    HakukohdeV1RDTO h = converter.toHakukohdeRDTO(hakukohde, true);
    assertThat(h, notNullValue());
    assertThat(h.getKoulutuksenAlkamiskausiUri(), is(equalTo(ALKAMISKAUSI)));
    assertThat(h.getKoulutuksenAlkamisvuosi(), is(equalTo(ALKAMISVUOSI)));
    assertThat(h.isTutkintoonJohtava(), is(false));
  }

  @Test(expected = DataErrorException.class)
  public void testThatKomotoDependantFieldThrowsException() {
    when(yhdenPaikanSaantoBuilder.koulutusJohtaaTutkintoon(
            Matchers.any(KoulutusmoduuliToteutus.class)))
        .thenReturn(false);

    Hakukohde hakukohde = getHakukohde();

    Koulutusmoduuli komo = new Koulutusmoduuli();
    komo.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);

    KoulutusmoduuliToteutus komoto = initKomoto();
    komoto.setOid("1.2.3");
    komoto.setKoulutusmoduuli(komo);

    KoulutusmoduuliToteutus komotoPoistettu = initKomoto();
    komotoPoistettu.setTila(TarjontaTila.POISTETTU);
    komotoPoistettu.setAlkamisVuosi(2014);
    komotoPoistettu.setOid("5.5.5.5.5");
    komotoPoistettu.setKoulutusmoduuli(komo);

    KoulutusmoduuliToteutus komotoVaaraKausi = initKomoto();
    komotoVaaraKausi.setTila(TarjontaTila.PERUTTU);
    komotoVaaraKausi.setAlkamiskausiUri("Täysin väärä");
    komotoVaaraKausi.setOid("5.5.5.5.5.9");
    komotoVaaraKausi.setKoulutusmoduuli(komo);

    hakukohde.setKoulutusmoduuliToteutuses(
        Sets.newHashSet(komoto, komotoPoistettu, komotoVaaraKausi));

    HakukohdeV1RDTO h = converter.toHakukohdeRDTO(hakukohde, true);
    assertThat(h, notNullValue());
    assertThat(h.getKoulutuksenAlkamiskausiUri(), is(equalTo(ALKAMISKAUSI)));
    assertThat(h.getKoulutuksenAlkamisvuosi(), is(equalTo(ALKAMISVUOSI)));
    assertThat(h.isTutkintoonJohtava(), is(false));
  }
}
