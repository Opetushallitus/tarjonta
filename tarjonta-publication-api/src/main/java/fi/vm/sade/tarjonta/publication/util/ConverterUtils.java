package fi.vm.sade.tarjonta.publication.util;

import java.util.Calendar;
import java.util.Date;
import javax.xml.bind.DatatypeConverter;

/**
 * Due to a features of JAXB this class will be eventually generated into org.w3c._2001.xmlschema -package. Meaning
 * that any other project creating the similar type converters might result overriding this functionality so be
 * careful.
 *
 */
public class ConverterUtils {

    public static Date parseDate(String xmlDate) {
        return DatatypeConverter.parseDate(xmlDate).getTime();
    }

    public static String printDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return DatatypeConverter.printDate(calendar);
    }

    public static Date parseDateTime(String xmlDateTime) {
        return DatatypeConverter.parseDateTime(xmlDateTime).getTime();
    }

    public static String printDateTime(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return DatatypeConverter.printDateTime(calendar);

    }

}