package fi.vm.sade.tarjonta;

import fi.vm.sade.tarjonta.dao.impl.HakuDAOImpl;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.KoodistoContract;
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
    private HakuDAOImpl dao;

    public Haku create(long alkuPvm, long loppuPvm) {
        long now = System.currentTimeMillis();
        String name = "" + now;
        return create(alkuPvm, loppuPvm, name, "Varsinainen haku", "Syksy", "Syksy 2013", "Korkeakoulutus", "Yhteishaku",2013,2014);
    }

    public Haku create(long alkuPvm, long loppuPvm, String name, String hakutyyppi, String hakukausi, String alkamisKausi, String kohdejoukko, String hakutapa, int hakuVuosi,int koulutusVuosi) {
        Haku h = new Haku();
        h.setOid(name);
        h.setNimiFi(name + " FI");
        h.setNimiSv(name + " SV");
        h.setNimiEn(name + " EN");
        h.setHaunAlkamisPvm(new Date(alkuPvm));
        h.setHaunLoppumisPvm(new Date(loppuPvm));
        h.setHakutyyppiUri(hakutyyppi);
        h.setHakukausiVuosi(hakuVuosi);
        h.setKoulutuksenAlkamisVuosi(koulutusVuosi);
        h.setHakukausiUri(hakukausi);
        h.setKoulutuksenAlkamiskausiUri(alkamisKausi);
        h.setKohdejoukkoUri(kohdejoukko);
        h.setHakutapaUri(hakutapa);
        h.setTila(KoodistoContract.TarjontaTilat.SUUNNITTELUSSA);
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

    public void assertHakueraSimpleDTO(Haku h, HakueraSimpleDTO dto) {
        assertEquals(h.getOid(), dto.getOid());
        assertEquals(h.getNimiFi(), dto.getNimiFi());
        assertEquals(h.getNimiSv(), dto.getNimiSv());
        assertEquals(h.getNimiEn(), dto.getNimiEn());
    }

    public Haku createValidHaku() {
        long now = new Date().getTime();
        int dif = 10000;
        return create(now, now+dif, "oid_"+now, "hakutyyppi", "hakukausi", "alkamiskausi", "kohdejoukko", "hakutapa",2013,2014);
    }
}
