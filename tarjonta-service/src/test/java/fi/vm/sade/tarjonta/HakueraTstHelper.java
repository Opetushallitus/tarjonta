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
        long now = System.currentTimeMillis();
        String name = "" + now;
        return create(alkuPvm, loppuPvm, name, "Varsinainen haku", "Syksy", "Syksy 2013", "Korkeakoulutus", "Yhteishaku");
    }

    public Hakuera create(long alkuPvm, long loppuPvm, String name, String hakutyyppi, String hakukausi, String alkamisKausi, String kohdejoukko, String hakutapa) {
        Hakuera h = new Hakuera();
        h.setOid(name);
        h.setNimiFi(name + " FI");
        h.setNimiSv(name + " SV");
        h.setNimiEn(name + " EN");
        h.setHaunAlkamisPvm(new Date(alkuPvm));
        h.setHaunLoppumisPvm(new Date(loppuPvm));
        h.setHakutyyppi(hakutyyppi);
        h.setHakukausi(hakukausi);
        h.setKoulutuksenAlkaminen(alkamisKausi);
        h.setKohdejoukko(kohdejoukko);
        h.setHakutapa(hakutapa);
        return dao.insert(h);
    }

    public SearchCriteriaDTO criteria(boolean paattyneet, boolean meneillaan, boolean tuleva, String lang) {
        SearchCriteriaDTO criteria = new SearchCriteriaDTO();
        criteria.setPaattyneet(paattyneet);
        criteria.setMeneillaan(meneillaan);
        criteria.setTulevat(tuleva);
        criteria.setLang(lang);
        return criteria;
    }

    public void assertHakueraSimpleDTO(Hakuera h, HakueraSimpleDTO dto) {
        assertEquals(h.getOid(), dto.getOid());
        assertEquals(h.getNimiFi(), dto.getNimiFi());
        assertEquals(h.getNimiSv(), dto.getNimiSv());
        assertEquals(h.getNimiEn(), dto.getNimiEn());
    }
}
