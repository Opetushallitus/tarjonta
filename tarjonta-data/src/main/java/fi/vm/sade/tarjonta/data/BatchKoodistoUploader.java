package fi.vm.sade.tarjonta.data;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BatchKoodistoUploader {
    public static void main(final String[] args) {
        final ApplicationContext context = new ClassPathXmlApplicationContext("spring/context.xml");
        final BatchKoodistoFileReader reader = context.getBean(BatchKoodistoFileReader.class);
        reader.read();
    }
}
