package fi.vm.sade.tarjonta.service;

import fi.vm.sade.tarjonta.HakueraTstHelper;
import fi.vm.sade.tarjonta.model.Hakuera;
import fi.vm.sade.tarjonta.service.types.dto.HakueraSimpleDTO;
import fi.vm.sade.tarjonta.service.types.dto.SearchCriteriaDTO;
import fi.vm.sade.tarjonta.service.types.dto.HakueraDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import static org.junit.Assert.*;

/**
 * @author Antti Salonen
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
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
        Hakuera meneillaan = helper.create(now-dif, now+dif);
        Hakuera tuleva = helper.create(now+dif, now+2*dif);
        Hakuera paattynyt = helper.create(now-2*dif, now-dif);

        // test happy path & conversions (search logic tested in dao test)

        List<HakueraSimpleDTO> result = hakueraService.findAll(helper.criteria(true, true, true, "fi"));
        assertEquals(3, result.size());
        helper.assertHakueraSimpleDTO(meneillaan, result.get(0));
        helper.assertHakueraSimpleDTO(tuleva, result.get(1));
        helper.assertHakueraSimpleDTO(paattynyt, result.get(2));

        // test illegal parameters

        assertEquals(0, hakueraService.findAll(null).size());
        assertEquals(0, hakueraService.findAll(new SearchCriteriaDTO()).size());
    }
    
    @Test
    public void testCreateHakuera() throws Exception {
        String oid = "1.2.3.4567";
        HakueraDTO hakueraDto = createHakueraDTO(oid);
        HakueraDTO hakuera2 = hakueraService.createHakuera(hakueraDto);
        assertNotNull(hakuera2);
        assertEquals(oid, hakuera2.getOid());
    }
    
    @Test
    public void testUpdateHakuera() throws Exception {
        String oid = "1.2.3.4568";
        String hakukausi = "Syksy 2012";
        HakueraDTO hakueraDto = createHakueraDTO(oid);
        HakueraDTO hakuera2 = hakueraService.createHakuera(hakueraDto);
        hakuera2.setHakukausi(hakukausi);
        HakueraDTO hakuera3 = hakueraService.updateHakuera(hakuera2);
        assertNotNull(hakuera2);
        assertEquals(hakukausi, hakuera2.getHakukausi());
    }
    
    @Test
    public void testFindByOid() throws Exception {
        String oid = "1.2.3.4569";
        HakueraDTO hakueraDto = createHakueraDTO(oid);
        hakueraService.createHakuera(hakueraDto);
        HakueraDTO hakuera3 = hakueraService.findByOid(oid);
        assertNotNull(hakuera3);
        assertEquals(oid, hakuera3.getOid());
    }
    
    private HakueraDTO createHakueraDTO(String oid) {
        HakueraDTO hakueraDto = new HakueraDTO();
        hakueraDto.setNimiFi("nimi fi");
        hakueraDto.setNimiSv("nimi sv");
        hakueraDto.setNimiEn("nimi en");
        hakueraDto.setOid(oid);
        hakueraDto.setHaunAlkamisPvm(convertDate(new Date(System.currentTimeMillis())));
        hakueraDto.setHaunLoppumisPvm(convertDate(new Date(System.currentTimeMillis() + 10000)));
        hakueraDto.setHakutyyppi("Ammattikorkeakoulut");
        hakueraDto.setHakukausi("Syksy");
        hakueraDto.setKoulutuksenAlkaminen("Syksy 2013");
        hakueraDto.setKohdejoukko("Ammattikoulutus");
        hakueraDto.setHakutapa("Yhteishaku");
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
