package fi.vm.sade.tarjonta.service.mock;

import fi.vm.sade.tarjonta.service.HakueraService;
import fi.vm.sade.tarjonta.service.types.dto.HakueraSimpleDTO;
import fi.vm.sade.tarjonta.service.types.dto.SearchCriteriaDTO;
import fi.vm.sade.tarjonta.service.types.dto.HakueraDTO;

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
        //return mockRepository;
    }
    
    @Override 
    public HakueraDTO createHakuera(HakueraDTO hakuera) {
        long newOid = ++oidSequence;
        hakuera.setOid("form_oid_" + newOid);
        mockRepository.add(hakuera);
        return hakuera;
    }
    
    @Override 
    public HakueraDTO updateHakuera(HakueraDTO hakuera) {
        List<HakueraSimpleDTO> newRepo = new ArrayList<HakueraSimpleDTO>(){
            @Override
            public boolean add(HakueraSimpleDTO hakuera) {
                if (hakuera.getOid() == null) {
                    hakuera.setOid("generated_oid_"+(++oidSequence));
                }
                return super.add(hakuera);
            }
        };
        for (HakueraSimpleDTO curHak : mockRepository) {
            if (curHak.getOid().equals(hakuera.getOid())) {
                newRepo.add(hakuera);
            } else {
                newRepo.add(curHak);
            }
        }
        this.mockRepository = newRepo;
        return hakuera;
    }
    

    public List<HakueraSimpleDTO> getMockRepository() {
        return mockRepository;
    }
    
    public void resetRepository() {
        this.mockRepository = new ArrayList<HakueraSimpleDTO>(){
            @Override
            public boolean add(HakueraSimpleDTO hakuera) {
                if (hakuera.getOid() == null) {
                    hakuera.setOid("generated_oid_"+(++oidSequence));
                }
                return super.add(hakuera);
            }
        };
        mockRepository.add(create("oid_1", "hakuera1 (paattynyt)"));
        mockRepository.add(create("oid_2", "hakuera2 (meneillaan)"));
        mockRepository.add(create("oid_3", "hakuera3 (tuleva)"));
    }
    
    @Override
    public HakueraDTO findByOid(String oidString) {
        for (HakueraSimpleDTO curHakuera : mockRepository) {
            if (curHakuera.getOid().equals(oidString)) {
                return (HakueraDTO)curHakuera;
            }
        }
        return null;
    }
}
