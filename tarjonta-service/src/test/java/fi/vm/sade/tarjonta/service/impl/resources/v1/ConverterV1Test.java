package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.PainotettavaOppiaine;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.PainotettavaOppiaineV1RDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConverterV1Test {

    @Mock
    TarjontaKoodistoHelper tarjontaKoodistoHelper;

    @InjectMocks
    ConverterV1 converterV1;

    @Test
    public void thatPainotettavatOppiaineetAreConverted() {
        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setHaku(mock(Haku.class));
        hakukohde.setTila(TarjontaTila.JULKAISTU);

        when(tarjontaKoodistoHelper.getHakukelpoisuusvaatimusrymaUriForHakukohde(anyString())).thenReturn(null);

        addPainotettavatOppiaineet(hakukohde);

        HakukohdeV1RDTO hakukohdeDTO = converterV1.toHakukohdeRDTO(hakukohde);

        assertTrue(hakukohdeDTO.getPainotettavatOppiaineet().size() == 1);

        PainotettavaOppiaineV1RDTO painotettavaOppiaineDTO = hakukohdeDTO.getPainotettavatOppiaineet().get(0);

        assertEquals("painotettavatoppiaineetlukiossa_ge#1", painotettavaOppiaineDTO.getOppiaineUri());
        assertEquals("2.5", painotettavaOppiaineDTO.getPainokerroin());
        assertTrue(hakukohdeDTO.getPainotettavatOppiaineet().size() == 1);
    }

    private void addPainotettavatOppiaineet(Hakukohde hakukohde) {
        Set<PainotettavaOppiaine> painotettavatOppiaineet = new HashSet<PainotettavaOppiaine>();

        PainotettavaOppiaine oppiaine = new PainotettavaOppiaine();
        oppiaine.setPainokerroin(new BigDecimal("2.5"));
        oppiaine.setOppiaine("painotettavatoppiaineetlukiossa_ge#1");

        painotettavatOppiaineet.add(oppiaine);

        hakukohde.setPainotettavatOppiaineet(painotettavatOppiaineet);
    }
}
