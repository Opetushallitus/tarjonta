package fi.vm.sade.tarjonta.data;

import fi.vm.sade.tarjonta.data.tarjontauploader.TarjontaFileReader;
import fi.vm.sade.tarjonta.data.tarjontauploader.TarjontaFileType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class TarjontaUploader {
    private TarjontaUploader() {

    }

    public static void main(final String[] args) throws IOException {
        if (args.length == 3) {
            final ApplicationContext context = new ClassPathXmlApplicationContext("spring/context.xml");
            final TarjontaFileReader reader = context.getBean(TarjontaFileReader.class);
            final TarjontaFileType type = TarjontaFileType.valueOf(args[0].toUpperCase());
            reader.read(type, args[1], args[2]);
        } else {
            System.out.println("Syötä tiedoston tyyppi, nimi ja haun OID parametreina, esim. -Dexec.args=\"koulutus koulutukset.xls 1.2.3.4.5\" tai -Dexec.args=\"hakukohde hakukohteet.xls 1.2.3.4.5\"");
        }

        System.exit(0);
    }
}
