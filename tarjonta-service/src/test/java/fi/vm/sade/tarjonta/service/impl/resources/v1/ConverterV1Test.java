package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.TestMockBase;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;

import java.math.BigDecimal;
import java.util.*;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConverterV1Test extends TestMockBase {

    @InjectMocks
    private ConverterV1 converter;

    private void setKomotoForHakukohde(Hakukohde hakukohde) {
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
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

        when(tarjontaKoodistoHelper.getHakukelpoisuusvaatimusrymaUriForHakukohde(anyString())).thenReturn(null);

        addPainotettavatOppiaineet(hakukohde);

        HakukohdeV1RDTO hakukohdeDTO = converter.toHakukohdeRDTO(hakukohde);

        assertTrue(hakukohdeDTO.getPainotettavatOppiaineet().size() == 1);

        PainotettavaOppiaineV1RDTO painotettavaOppiaineDTO = hakukohdeDTO.getPainotettavatOppiaineet().get(0);

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

        when(tarjontaKoodistoHelper.getHakukelpoisuusvaatimusrymaUriForHakukohde(anyString())).thenReturn(null);
        when(tarjontaKoodistoHelper.getKoodiByUri("kieli_fi")).thenReturn(mock(KoodiType.class));

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
    public void thatValintakokeetAreConvertedToDTO() {
        when(tarjontaKoodistoHelper.getHakukelpoisuusvaatimusrymaUriForHakukohde(anyString())).thenReturn(null);

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
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        komoto.setKoulutusmoduuli(komo);
        komoto.setOid("1.2.3");

        KoulutusmoduuliToteutus komotoPoistettu = new KoulutusmoduuliToteutus();
        komotoPoistettu.setTila(TarjontaTila.POISTETTU);
        komotoPoistettu.setKoulutusmoduuli(komo);
        komotoPoistettu.setOid("5.5.5.5.5");

        KoulutusmoduuliToteutus komotoPeruttu = new KoulutusmoduuliToteutus();
        komotoPeruttu.setTila(TarjontaTila.PERUTTU);
        komotoPeruttu.setKoulutusmoduuli(komo);
        komotoPeruttu.setOid("5.5.5.5.5.8");

        hakukohde.setKoulutusmoduuliToteutuses(Sets.newHashSet(komoto, komotoPoistettu, komotoPeruttu));

        KoulutusmoduuliToteutusTarjoajatiedot tarjoajatiedot = new KoulutusmoduuliToteutusTarjoajatiedot();
        tarjoajatiedot.getTarjoajaOids().add("4.5.6");
        hakukohde.getKoulutusmoduuliToteutusTarjoajatiedot().put(komoto.getOid(), tarjoajatiedot);

        HakukohdeV1RDTO hakukohdeDTO = converter.toHakukohdeRDTO(hakukohde);

        Map<String, KoulutusmoduuliTarjoajatiedotV1RDTO> tarjoajatiedotMap = hakukohdeDTO.getKoulutusmoduuliToteutusTarjoajatiedot();

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
        assertEquals("4.5.6", tarjoajatiedotMap.get(komoto.getOid()).getTarjoajaOids().iterator().next());
    }

    @Test
    public void thatHakukohdeWithoutKoulutusmoduuliTarjoajatiedotAreConvertedToDTO() {
        when(organisaatioService.findByOid("4.5.6")).thenReturn(null);

        Hakukohde hakukohde = getHakukohde();
        KoulutusmoduuliToteutus koulutusmoduuliToteutus = new KoulutusmoduuliToteutus();
        koulutusmoduuliToteutus.setOid("1.2.3");
        koulutusmoduuliToteutus.setTarjoaja("4.5.6");

        Koulutusmoduuli koulutusmoduuli = new Koulutusmoduuli();
        koulutusmoduuli.setModuuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        koulutusmoduuliToteutus.setKoulutusmoduuli(koulutusmoduuli);

        hakukohde.addKoulutusmoduuliToteutus(koulutusmoduuliToteutus);

        HakukohdeV1RDTO hakukohdeDTO = converter.toHakukohdeRDTO(hakukohde);

        Map<String, KoulutusmoduuliTarjoajatiedotV1RDTO> tarjoajatiedotMap = hakukohdeDTO.getKoulutusmoduuliToteutusTarjoajatiedot();

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
        assertEquals(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.OPINTOKOKONAISUUS, hakuDTO.getKoulutusmoduuliTyyppi());
    }

    @Test
    public void thatHaunKoulutusmoduuliTyyppiIsConvertedToEntity() throws OIDCreationException {
        HakuV1RDTO hakuDTO = new HakuV1RDTO();

        Haku haku = converter.convertHakuV1DRDTOToHaku(hakuDTO, new Haku());
        assertNull(haku.getKoulutusmoduuliTyyppi());

        hakuDTO.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.OPINTOJAKSO);

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
        assertThat(hakukohdeDTO.getRyhmaliitokset(), hasItem(getRyhmaliitosElementMatcher("4.5.6", null)));
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
        when(hakukohdeDAO.findHakukohteetWithYlioppilastutkintoAntaaHakukelpoisuuden(Matchers.anyLong(), Matchers.anyBoolean()))
                .thenReturn(Lists.newArrayList("1.2.3", "4.5.6"));
        haku.setTila(TarjontaTila.JULKAISTU);
        haku.setHakukausiVuosi(2000);
        HakuV1RDTO dto = converter.fromHakuToHakuRDTO(haku, true);
        assertEquals(dto.getHakukohdeOidsYlioppilastutkintoAntaaHakukelpoisuuden().size(), 2);
    }

    private BaseMatcher<RyhmaliitosV1RDTO> getRyhmaliitosElementMatcher(final String ryhmaOid,
                                                                        final Integer prioriteetti) {
        return new BaseMatcher<RyhmaliitosV1RDTO>() {
            @Override
            public boolean matches(Object o) {
                RyhmaliitosV1RDTO ryhmaliitosDTO = (RyhmaliitosV1RDTO) o;
                if (prioriteetti == null) {
                    return ryhmaliitosDTO.getRyhmaOid().equals(ryhmaOid) &&
                            ryhmaliitosDTO.getPrioriteetti() == null;
                } else {
                    return ryhmaliitosDTO.getRyhmaOid().equals(ryhmaOid) &&
                            ryhmaliitosDTO.getPrioriteetti().equals(prioriteetti);
                }
            }

            public void describeTo(Description description) {
            }
        };
    }
}
