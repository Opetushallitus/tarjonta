package fi.vm.sade.tarjonta;

import fi.vm.sade.tarjonta.dao.impl.HakueraDAOImpl;
import fi.vm.sade.tarjonta.model.Hakuera;
import fi.vm.sade.tarjonta.service.types.dto.HakueraSimpleDTO;
import fi.vm.sade.tarjonta.service.types.dto.SearchCriteriaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 * @author Antti Salonen
 */
@Component
public class HakueraTstHelper {

    @Autowired
    private HakueraDAOImpl dao;

    public Hakuera create(long alkuPvm, long loppuPvm) {
        Hakuera h = new Hakuera();
        long now = System.currentTimeMillis();
        h.setOid(""+now);
        h.setNimiFi(""+now+" FI");
        h.setNimiSv(""+now+" SV");
        h.setNimiEn(""+now+" EN");
        h.setHaunAlkamisPvm(new Date(alkuPvm));
        h.setHaunLoppumisPvm(new Date(loppuPvm));
        return dao.insert(h);
    }

    public SearchCriteriaDTO criteria(boolean paattyneet, boolean meneillaan, boolean tuleva) {
        SearchCriteriaDTO criteria = new SearchCriteriaDTO();
        criteria.setPaattyneet(paattyneet);
        criteria.setMeneillaan(meneillaan);
        criteria.setTulevat(tuleva);
        return criteria;
    }

    public void assertHakueraSimpleDTO(Hakuera h, HakueraSimpleDTO dto) {
        assertEquals(h.getOid(), dto.getOid());
        assertEquals(h.getNimiFi(), dto.getNimiFi());
        assertEquals(h.getNimiSv(), dto.getNimiSv());
        assertEquals(h.getNimiEn(), dto.getNimiEn());
    }
}
