package fi.vm.sade.tarjonta.data.tarjontauploader;

import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class TarjontaHandlerTest {
    private TarjontaPublicService tarjontaPublicService;
    private TarjontaAdminService tarjontaAdminService;
    private TarjontaHandler handler;

    @Before
    public void setup() {
        tarjontaPublicService = mock(TarjontaPublicService.class);
        tarjontaAdminService = mock(TarjontaAdminService.class);
        handler = new TarjontaHandler(tarjontaPublicService, tarjontaAdminService);
    }

    @Test
    public void testAddKoulutusmoduuliToteutusSuccessfully() {
        handler.addKoulutus(new Koulutus());
    }

    @Test
    public void testAddHakukohdeSuccessfully() {
        handler.addHakukohde(new Hakukohde(), "1.2.3.4");
    }
}
