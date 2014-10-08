package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.model.ValintakoeAjankohta;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ValintakoeV1RDTO;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConverterV1Test {

    @Mock
    private ContextDataService contextDataService;

    @Mock
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    @InjectMocks
    private ConverterV1 converter;

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

}
