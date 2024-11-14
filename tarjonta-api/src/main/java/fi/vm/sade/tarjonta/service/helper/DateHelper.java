package fi.vm.sade.tarjonta.service.helper;

import java.util.Calendar;
import java.util.Date;
import javax.xml.bind.DatatypeConverter;

/**
 * @author Tuomas Katva
 */
public class DateHelper {

  /**
   * Converts a String representing a {@link XMLGregorianCalendar} to a {@link Date}.
   *
   * @param xgc String to convert from
   * @return Computed date
   */
  public static Date parseDate(String xgc) {

    return DatatypeConverter.parseDate(xgc).getTime();
  }

  /**
   * Converts a {@link Date} to a String representing a {@link XMLGregorianCalendar}.
   *
   * @param date Date to convert from
   * @return String representation
   */
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
