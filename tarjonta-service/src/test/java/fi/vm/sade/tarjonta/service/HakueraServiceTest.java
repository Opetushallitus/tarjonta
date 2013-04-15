package fi.vm.sade.tarjonta.service;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import fi.vm.sade.tarjonta.HakueraTstHelper;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.types.HakueraTyyppi;
import fi.vm.sade.tarjonta.service.types.SearchCriteriaType;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.service.types.HakueraSimpleTyyppi;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Ignore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Antti Salonen
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@Ignore
public class HakueraServiceTest {

    @Autowired
    private HakueraService hakueraService;
    @Autowired
    private HakueraTstHelper helper;

    @Test
    public void testFindAll() throws Exception {

        // insert data

        long now = new Date().getTime();
        int dif = 10000;
        Haku meneillaan = helper.create(now-dif, now+dif);
        Haku tuleva = helper.create(now+dif, now+2*dif);
        Haku paattynyt = helper.create(now-2*dif, now-dif);

        // test happy path & conversions (search logic tested in dao test)

        List<HakueraSimpleTyyppi> result = hakueraService.findAll(helper.criteria(true, true, true, "fi"));
        assertEquals(3, result.size());
        helper.assertHakueraSimpleTyyppi(meneillaan, result.get(0));
        helper.assertHakueraSimpleTyyppi(tuleva, result.get(1));
        helper.assertHakueraSimpleTyyppi(paattynyt, result.get(2));

        // test illegal parameters

        assertEquals(0, hakueraService.findAll(null).size());
        assertEquals(0, hakueraService.findAll(new SearchCriteriaType()).size());
    }

    @Test
    public void testCreateHakuera() throws Exception {
        String oid = "1.2.3.4567";
        HakueraTyyppi hakueraDto = createHakueraTyyppi(oid);
        HakueraTyyppi hakuera2 = hakueraService.createHakuera(hakueraDto);
        assertNotNull(hakuera2);
        assertEquals(oid, hakuera2.getOid());
    }

    @Test
    public void testUpdateHakuera() throws Exception {
        String oid = "1.2.3.4568";
        String hakukausi = "Syksy 2012";
        HakueraTyyppi hakueraDto = createHakueraTyyppi(oid);
        HakueraTyyppi hakuera2 = hakueraService.createHakuera(hakueraDto);
        hakuera2.setHakukausi(hakukausi);
        HakueraTyyppi hakuera3 = hakueraService.updateHakuera(hakuera2);
        assertNotNull(hakuera2);
        assertEquals(hakukausi, hakuera2.getHakukausi());
    }

    @Test(expected = Exception.class)
    public void testUpdateHakueraOid() throws Exception {
        Haku h = helper.createValidHaku();
        HakueraTyyppi dto = hakueraService.findByOid(h.getOid());
        dto.setOid("updated_oid");
        hakueraService.updateHakuera(dto);
    }

    @Test
    public void testFindByOid() throws Exception {
        String oid = "1.2.3.4569";
        HakueraTyyppi hakueraDto = createHakueraTyyppi(oid);
        hakueraService.createHakuera(hakueraDto);
        HakueraTyyppi hakuera3 = hakueraService.findByOid(oid);
        assertNotNull(hakuera3);
        assertEquals(oid, hakuera3.getOid());
    }

    private HakueraTyyppi createHakueraTyyppi(String oid) {
        HakueraTyyppi hakueraDto = new HakueraTyyppi();
        hakueraDto.setNimiFi("nimi fi");
        hakueraDto.setNimiSv("nimi sv");
        hakueraDto.setNimiEn("nimi en");
        hakueraDto.setOid(oid);
        hakueraDto.setHaunAlkamisPvm(new Date(System.currentTimeMillis()));
        hakueraDto.setHaunLoppumisPvm(new Date(System.currentTimeMillis() + 10000));
        hakueraDto.setHakutyyppi("Ammattikorkeakoulut");

        hakueraDto.setHakukausi("Syksy");
        hakueraDto.setKoulutuksenAlkaminen("Syksy 2013");
        hakueraDto.setKohdejoukko("Ammattikoulutus");
        hakueraDto.setHakutapa("Yhteishaku");
        hakueraDto.setTila(TarjontaTila.LUONNOS);
        return hakueraDto;
    }

    private XMLGregorianCalendar convertDate(Date origDate) {
        XMLGregorianCalendar xmlDate = null;
        if (origDate != null) {
            try {
                GregorianCalendar c = new GregorianCalendar();
                c.setTime(origDate);
                xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            } catch (Exception ex) {

            }
        }
        return xmlDate;
    }
}
