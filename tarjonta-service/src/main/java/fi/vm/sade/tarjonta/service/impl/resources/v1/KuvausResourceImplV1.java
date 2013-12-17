package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.wordnik.swagger.annotations.ApiParam;
import fi.vm.sade.tarjonta.service.resources.v1.KuvausV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;

import javax.ws.rs.PathParam;
import java.util.HashMap;
import java.util.List;

/*
* @author: Tuomas Katva 16/12/13
*/
public class KuvausResourceImplV1 implements KuvausV1Resource {

    @Override
    public ResultV1RDTO<List<String>> findAllKuvauksesByTyyppi(String tyyppi) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ResultV1RDTO<List<HashMap<String, String>>> getKuvausNimet(String tyyppi) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ResultV1RDTO<List<HashMap<String, String>>> getKuvausNimetWithOrganizationType(String tyyppi, String orgType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ResultV1RDTO<KuvausV1RDTO> findById(String tyyppi,String tunniste) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ResultV1RDTO<KuvausV1RDTO> createNewKuvaus(String tyyppi, KuvausV1RDTO kuvausRDTO) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ResultV1RDTO<KuvausV1RDTO> updateKuvaus(String tyyppi, KuvausV1RDTO kuvausRDTO) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
