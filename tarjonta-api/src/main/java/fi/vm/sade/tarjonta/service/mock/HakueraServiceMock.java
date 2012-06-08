package fi.vm.sade.tarjonta.service.mock;

import fi.vm.sade.tarjonta.service.HakueraService;
import fi.vm.sade.tarjonta.service.types.dto.HakueraSimpleDTO;
import fi.vm.sade.tarjonta.service.types.dto.SearchCriteriaDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Antti Salonen
 */
public class HakueraServiceMock implements HakueraService {

    long oidSequence = 0;
    List<HakueraSimpleDTO> mockRepository = new ArrayList<HakueraSimpleDTO>(){
        @Override
        public boolean add(HakueraSimpleDTO hakuera) {
            if (hakuera.getOid() == null) {
                hakuera.setOid("generated_oid_"+(++oidSequence));
            }
            return super.add(hakuera);
        }
    };

    public HakueraServiceMock() {
        mockRepository.add(create("oid_1", "hakuera1 (paattynyt)"));
        mockRepository.add(create("oid_2", "hakuera2 (meneillaan)"));
        mockRepository.add(create("oid_3", "hakuera3 (tuleva)"));
    }

    private HakueraSimpleDTO create(String oid, String nimi) {
        HakueraSimpleDTO hakuera = new HakueraSimpleDTO();
        hakuera.setOid(oid);
        hakuera.setNimiFi(nimi+" FI");
        hakuera.setNimiSv(nimi+" SV");
        hakuera.setNimiEn(nimi+" EN");
        return hakuera;
    }

    @Override
    public List<HakueraSimpleDTO> findAll(SearchCriteriaDTO searchCriteria) {
        List<HakueraSimpleDTO> result = new ArrayList<HakueraSimpleDTO>();
        for (HakueraSimpleDTO hakuera : mockRepository) {
            if (searchCriteria.isPaattyneet() && hakuera.getNimiFi().contains("paattynyt")
                    || searchCriteria.isTulevat() && hakuera.getNimiFi().contains("tuleva")
                    || searchCriteria.isMeneillaan() && hakuera.getNimiFi().contains("meneillaan")) {
                result.add(hakuera);
            }
        }
        return result;
    }

}
