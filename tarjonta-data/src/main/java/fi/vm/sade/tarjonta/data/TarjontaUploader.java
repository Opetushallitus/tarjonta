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
            System.out.println("\nKoulutusaineiston käsittely: \n" +
                    "\n" +
                    "  mvn exec:java -Dexec.mainClass=fi.vm.sade.tarjonta.data.TarjontaUploader -Dexec.args=\"koulutus koulutukset.xls ammatillinen_peruskoulutus\"\n" +
                    "\n" +
                    "Hakukohdeaineiston käsittely:\n" +
                    "\n" +
                    "  mvn exec:java -Dexec.mainClass=fi.vm.sade.tarjonta.data.TarjontaUploader -Dexec.args=\"hakukohde hakukohteet.xls 1.2.3.4.5\"\n" +
                    "\n");
        }

        System.exit(0);
    }
}
