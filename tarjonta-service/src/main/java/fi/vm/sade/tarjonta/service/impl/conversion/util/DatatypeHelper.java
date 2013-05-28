package fi.vm.sade.tarjonta.service.impl.conversion.util;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author: Tuomas Katva
 * Date: 4/12/13
 */
public class DatatypeHelper {


    public static XMLGregorianCalendar convertDateToXmlGregorianCal(Date date) {
       if (date != null) {
            GregorianCalendar gcal = new GregorianCalendar();
           gcal.setTime(date);
            try {
                XMLGregorianCalendar xgcal = DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(gcal);
                return xgcal;
            } catch (DatatypeConfigurationException e) {
                return null;
            }
       } else {
           return null;
       }
    }

    public static Date convertXmlGregorianCalendarToDate(XMLGregorianCalendar xcal) {
        return xcal.toGregorianCalendar().getTime();
    }

}
