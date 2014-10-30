package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeLiiteV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.PainotettavaOppiaineV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusmoduuliTarjoajatiedotV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ValintakoeV1RDTO;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.*;
import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConverterV1Test {

    @Mock
    TarjontaKoodistoHelper tarjontaKoodistoHelper;

    @Mock
    private ContextDataService contextDataService;

    @Mock
    private OrganisaatioService organisaatioService;

    @InjectMocks
    private ConverterV1 converter;

    @Test
    public void thatPainotettavatOppiaineetAreConverted() {
        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setHaku(mock(Haku.class));
        hakukohde.setTila(TarjontaTila.JULKAISTU);

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
    public void thatValintakoeDTOsAreConverted() {
        HakukohdeV1RDTO hakukohdeDTO = getHakukohdeDTO();

        Hakukohde hakukohde = converter.toHakukohde(hakukohdeDTO);
        assertTrue(hakukohde.getValintakoes().size() == 1);

        Valintakoe valintakoe = hakukohde.getValintakoes().iterator().next();
        assertTrue(valintakoe.getAjankohtas().size() == 1);

        ValintakoeAjankohta valintakoeAjankohta = valintakoe.getAjankohtas().iterator().next();
        assertTrue(valintakoeAjankohta.isKellonaikaKaytossa());
    }

    @Test
    public void thatValintakokeetAreConverted() {
        when(tarjontaKoodistoHelper.getHakukelpoisuusvaatimusrymaUriForHakukohde(anyString())).thenReturn(null);

        Hakukohde hakukohde = getHakukohde();

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
        valintakoe.setAjankohtas(getValintakoeAjankohdat());
        return valintakoe;
    }

    private Set<ValintakoeAjankohta> getValintakoeAjankohdat() {
        ValintakoeAjankohta valintakoeAjankohta = new ValintakoeAjankohta();
        return new HashSet<ValintakoeAjankohta>(Arrays.asList(valintakoeAjankohta));
    }

    private HakukohdeV1RDTO getHakukohdeDTO() {
        HakukohdeV1RDTO hakukohdeDTO = new HakukohdeV1RDTO();
        hakukohdeDTO.setTila("JULKAISTU");
        hakukohdeDTO.setValintakokeet(getValintakokeetDTOs());
        hakukohdeDTO.setToteutusTyyppi("LUKIOKOULUTUS");
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
    public void thatToinenAsteUsesJarjestelmanValintapalvelua() {
        HakukohdeV1RDTO hakukohdeDTO = getHakukohdeDTO();
        hakukohdeDTO.setKaytetaanJarjestelmanValintaPalvelua(false);

        Hakukohde hakukohde = converter.toHakukohde(hakukohdeDTO);

        assertTrue(hakukohde.isKaytetaanJarjestelmanValintapalvelua());
    }

    public void thatHakukohdeWithKoulutusmoduuliTarjontatiedotAreConverted() {
        Hakukohde hakukohde = getHakukohde();
        KoulutusmoduuliToteutusTarjoajatiedot tarjoajatiedot = new KoulutusmoduuliToteutusTarjoajatiedot();
        tarjoajatiedot.getTarjoajaOids().add("4.5.6");
        hakukohde.getKoulutusmoduuliToteutusTarjoajatiedot().put("1.2.3", tarjoajatiedot);

        HakukohdeV1RDTO hakukohdeDTO = converter.toHakukohdeRDTO(hakukohde);

        Map<String, KoulutusmoduuliTarjoajatiedotV1RDTO> tarjoajatiedotMap = hakukohdeDTO.getKoulutusmoduuliToteutusTarjoajatiedot();

        assertTrue(tarjoajatiedotMap.size() == 1);
        assertTrue(tarjoajatiedotMap.containsKey("1.2.3"));
        assertTrue(tarjoajatiedotMap.get("1.2.3").getTarjoajaOids().size() == 1);
        assertEquals("4.5.6", tarjoajatiedotMap.get("1.2.3").getTarjoajaOids().iterator().next());
    }

    @Test
    public void thatHakukohdeWithoutKoulutusmoduuliTarjoajatiedotAreConverted() {
        when(organisaatioService.findByOid("4.5.6")).thenReturn(null);

        Hakukohde hakukohde = getHakukohde();
        KoulutusmoduuliToteutus koulutusmoduuliToteutus = new KoulutusmoduuliToteutus();
        koulutusmoduuliToteutus.setOid("1.2.3");
        koulutusmoduuliToteutus.setTarjoaja("4.5.6");

        hakukohde.addKoulutusmoduuliToteutus(koulutusmoduuliToteutus);

        HakukohdeV1RDTO hakukohdeDTO = converter.toHakukohdeRDTO(hakukohde);

        Map<String, KoulutusmoduuliTarjoajatiedotV1RDTO> tarjoajatiedotMap = hakukohdeDTO.getKoulutusmoduuliToteutusTarjoajatiedot();

        assertTrue(tarjoajatiedotMap.size() == 1);
        assertTrue(tarjoajatiedotMap.containsKey("1.2.3"));
        assertTrue(tarjoajatiedotMap.get("1.2.3").getTarjoajaOids().size() == 1);
        assertEquals("4.5.6", tarjoajatiedotMap.get("1.2.3").getTarjoajaOids().iterator().next());
    }
}
