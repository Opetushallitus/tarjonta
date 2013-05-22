package fi.vm.sade.tarjonta.data.tarjontauploader;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class TarjontaFileReaderTest {
    private TarjontaHandler handler;
    private TarjontaFileReader reader;

    @Before
    public void setup() {
        handler = mock(TarjontaHandler.class);
        reader = new TarjontaFileReader(handler);
    }

    @Test
    public void testReadKoulutusFileSuccessfully() throws IOException {
        reader.read(TarjontaFileType.KOULUTUS, "src/test/resources/TESTI_TARJONTA/koulutukset_test.xls", "1.2.3.4");
    }

    @Test
    public void testReadHakukohdeFileSuccessfully() throws IOException {
        reader.read(TarjontaFileType.HAKUKOHDE, "src/test/resources/TESTI_TARJONTA/hakukohteet_test.xls", "1.2.3.4");
    }

    @Test(expected = IOException.class)
    public void testReadNonExistingFile() throws IOException {
        reader.read(TarjontaFileType.KOULUTUS, "not_found.xls", "1.2.3.4");
    }
}
