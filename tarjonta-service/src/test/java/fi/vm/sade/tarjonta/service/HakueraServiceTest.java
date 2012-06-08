package fi.vm.sade.tarjonta.service;

import fi.vm.sade.tarjonta.HakueraTstHelper;
import fi.vm.sade.tarjonta.model.Hakuera;
import fi.vm.sade.tarjonta.service.types.dto.HakueraSimpleDTO;
import fi.vm.sade.tarjonta.service.types.dto.SearchCriteriaDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

}
