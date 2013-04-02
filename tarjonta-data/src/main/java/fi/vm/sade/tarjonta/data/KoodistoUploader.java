package fi.vm.sade.tarjonta.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author: Tuomas Katva
 * Date: 19.2.2013
 * Time: 19:24
 */
public class KoodistoUploader {
    private static Logger log = LoggerFactory.getLogger(KoodistoUploader.class);

    public static void main(final String[] args) {
        if (args != null && args.length > 1) {
            final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/context.xml");
            final UploadKoodistoData uploader = ctx.getBean(UploadKoodistoData.class);
            if (args[0].trim().equalsIgnoreCase("0")) {
                try {
                    String orgOid = null;
                    try {
                        orgOid = args[3];
                    } catch (final IndexOutOfBoundsException iobe) {
                        log.info("organisaatio OID was not entered");
                    }
                    uploader.loadKoodistoFromExcel(args[1], null, args[2], orgOid);
                    System.out.println("Koodisto uploaded");
                } catch (final Exception exp) {
                    log.error("Exception occurred when loading koodisto from excel : {} , exception : {}", args[1], exp.toString());
                }
            } else {
                try {
                    uploader.createKoodistoRelations(args[1]);
                    log.info("Koodisto relations uploaded");
                } catch (final Exception exp) {
                    exp.printStackTrace();
                    log.error("Exception occurred when loading koodisto relations from excel: {} , exception : {} ", args[1], exp.getMessage());
                }
            }
        } else {
            final StringBuilder st = new StringBuilder();
            st.append("Usage:");
            st.append(System.getProperty("line.separator"));
            st.append("parameters example (separated be comma): 1/0, path to excel ");
            st.append(System.getProperty("line.separator"));
            st.append("or : 1/0, path to excel, koodisto nimi");
            st.append(System.getProperty("line.separator"));
            st.append("where 0 is create koodisto from excel file");
            st.append(System.getProperty("line.separator"));
            st.append("and where 1 is create koodisto relation from excel");
            st.append(System.getProperty("line.separator"));
            st.append("if creating koodisto, koodisto name is needed");
            log.warn(st.toString());
        }
    }
}
