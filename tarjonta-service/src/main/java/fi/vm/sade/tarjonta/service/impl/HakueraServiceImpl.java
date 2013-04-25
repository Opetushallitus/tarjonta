package fi.vm.sade.tarjonta.service.impl;

import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.HakueraService;
import fi.vm.sade.tarjonta.service.business.HakuBusinessService;
import fi.vm.sade.tarjonta.service.types.HakueraSimpleTyyppi;
import fi.vm.sade.tarjonta.service.types.HakueraTyyppi;
import fi.vm.sade.tarjonta.service.types.SearchCriteriaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Antti Salonen
 */
@Transactional(readOnly=true)
@Service("koulutusmoduuliAdminService")
public class HakueraServiceImpl implements HakueraService {

    @Autowired
    private HakuBusinessService businessService;
    @Autowired
    private ConversionService conversionService;

    @Override
    public List<HakueraSimpleTyyppi> findAll(SearchCriteriaType searchCriteria) {
        if (searchCriteria == null) {
            return new ArrayList<HakueraSimpleTyyppi>();
        }
        return convert(businessService.findAll(searchCriteria));
    }

    private List<HakueraSimpleTyyppi> convert(List<Haku> hakueras) {
        List<HakueraSimpleTyyppi> result = new ArrayList<HakueraSimpleTyyppi>();
        for (Haku hakuera : hakueras) {
            result.add(conversionService.convert(hakuera, HakueraSimpleTyyppi.class));
        }
        return result;
    }

    @Override
    @Transactional(readOnly=false)
    public HakueraTyyppi createHakuera(HakueraTyyppi hakuera) {
        Haku entity = conversionService.convert(hakuera, Haku.class);
        entity = businessService.save(entity);
        return conversionService.convert(entity, HakueraTyyppi.class);
    }

    @Override
    @Transactional(readOnly=false)
    public HakueraTyyppi updateHakuera(HakueraTyyppi hakuera) {
        Haku entity = conversionService.convert(hakuera, Haku.class);
        entity = businessService.update(entity);
        return conversionService.convert(entity, HakueraTyyppi.class);
    }

    @Override
    public HakueraTyyppi findByOid(String oidString) {
        return conversionService.convert(businessService.findByOid(oidString), HakueraTyyppi.class);
    }
}
