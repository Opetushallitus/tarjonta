
package fi.vm.sade.tarjonta.ui.helper.conversion;

import fi.vm.sade.tarjonta.ui.mock.KoodiServiceMock;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jani
 */
public class KoodistoServiceTest {
    
    public KoodistoServiceTest() {
    }
    

    /**
     * Test of listKoodiByRelation method, of class KoodistoService.
     */
    @Test
    public void testListKoodiByRelation() {
        KoodiUriAndVersioType koodi = new KoodiUriAndVersioType();
        koodi.setKoodiUri("mock_a_t2_tutkinto");
        KoodiServiceMock instance = new KoodiServiceMock();
        List result = instance.listKoodiByRelation(koodi, Boolean.FALSE, null);
        assertEquals(9, result.size());
    }

    /**
     * Test of searchKoodisByKoodisto method, of class KoodistoService.
     */
    @Test
    public void testSearchKoodisByKoodisto() {
        SearchKoodisByKoodistoCriteriaType searchCriteria = new SearchKoodisByKoodistoCriteriaType();
        searchCriteria.setKoodistoUri("t2_tutkinto");
        KoodiServiceMock instance = new KoodiServiceMock();
        List result = instance.searchKoodisByKoodisto(searchCriteria);
        assertEquals(3, result.size());
    }

    /**
     * Test of searchKoodis method, of class KoodistoService.
     */
    @Test
    public void testSearchKoodis() {
        SearchKoodisCriteriaType searchCriteria = new SearchKoodisCriteriaType();
        searchCriteria.getKoodiUris().add("KIELI");
        KoodiServiceMock instance = new KoodiServiceMock();
        List result = instance.searchKoodis(searchCriteria);
        assertEquals(3, result.size());
    
    }
    
     @Test
    public void testSearchKoodis2() {
        SearchKoodisCriteriaType searchCriteria = new SearchKoodisCriteriaType();
        searchCriteria.getKoodiUris().add("KIELIKOEKIELI");
        KoodiServiceMock instance = new KoodiServiceMock();
        List<KoodiType> result = instance.searchKoodis(searchCriteria);
        assertEquals(3, result.size());
 
        
        assertEquals("mock_ruotsi", result.get(0).getKoodiUri());
       
        assertEquals("mock_englanti", result.get(1).getKoodiUri());
       
        
    
    }
}
