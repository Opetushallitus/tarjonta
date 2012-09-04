package fi.vm.sade.tarjonta;

import com.bsb.common.vaadin.embed.EmbedVaadinServer;
import com.bsb.common.vaadin.embed.component.EmbedVaadinComponent;
import com.bsb.common.vaadin.embed.support.EmbedVaadin;
import com.vaadin.ui.Window;
import fi.vm.sade.generic.common.I18N;

import fi.vm.sade.tarjonta.ui.poc.Main;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;

/**
 * Starts tarjonta-application & browser with embed vaadin, useful for manual testing
 *
 * @author Antti Salonen
 */
public class TarjontaPocMain {

    public static void main(String[] args) throws InterruptedException, IOException {
        I18N.setLocale(new Locale("fi"));
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/application-context.xml");
        Main component = new Main();
        File moduleBaseDir = new File(".");
        File rootDir = new File(moduleBaseDir, "target/");
        System.out.println("vaadin root dir: " + rootDir.getCanonicalPath());
        EmbedVaadinComponent builder = EmbedVaadin.forComponent(component)
                .withContextRootDirectory(rootDir)
                .wait(false);
        EmbedVaadinServer server = builder.start();
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        desktop.browse(URI.create("http://localhost:"+server.getConfig().getPort()));

        while (true) {
            Thread.sleep(1000);
        }
    }

}
