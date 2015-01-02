package fi.vm.sade.tarjonta.model;

import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class KoulutusmoduuliToteutusTest {

    @Test
    public void thatMinAndMaxAlkamisPvmIsReturned() {
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();

        DateTime minDate = new DateTime();
        minDate.withYear(2013);
        DateTime maxDate = new DateTime();
        maxDate.withYear(2014);

        komoto.addKoulutuksenAlkamisPvms(minDate.toDate());
        komoto.addKoulutuksenAlkamisPvms(maxDate.toDate());

        assertEquals(DateUtils.truncate(minDate.toDate(), Calendar.DATE), komoto.getMinAlkamisPvm());
        assertEquals(DateUtils.truncate(maxDate.toDate(), Calendar.DATE), komoto.getMaxAlkamisPvm());

        komoto.getKoulutuksenAlkamisPvms().clear();

        assertNull(komoto.getMinAlkamisPvm());
        assertNull(komoto.getMaxAlkamisPvm());
    }

}
