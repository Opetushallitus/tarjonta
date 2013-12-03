package fi.vm.sade.tarjonta.service.impl.resources.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import fi.vm.sade.tarjonta.dao.impl.KoulutusSisaltyvyysDAOImpl;
import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliDAOImpl;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys.ValintaTyyppi;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.service.resources.v1.LinkingV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO.ResultStatus;

/**
 * TODO, authorization!!
 */
@Transactional(readOnly = false)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class LinkingResourceImplV1 implements LinkingV1Resource {

    @Autowired
    KoulutusSisaltyvyysDAOImpl koulutusSisaltyvyysDAO;
    
    @Autowired
    KoulutusmoduuliDAOImpl koulutusmoduuliDAO;
    
    /**
     * TODO: tekee aina uuden linkin!
     */
    @Override
    public ResultV1RDTO link(String parent, String child) {
        Koulutusmoduuli parentKomo = koulutusmoduuliDAO.findByOid(parent);
        Koulutusmoduuli childKomo = koulutusmoduuliDAO.findByOid(child);
        if (parentKomo == null || childKomo == null) {
            System.out.println("child or parent is null");

            List<ErrorV1RDTO> errors = new ArrayList<ErrorV1RDTO>();

            if(parentKomo==null) {
                errors.add(ErrorV1RDTO.createValidationError("parent", "not.found"));
            }
            if(childKomo==null) {
                errors.add(ErrorV1RDTO.createValidationError("child", "not.found"));
            }
            
            ResultV1RDTO result = new ResultV1RDTO();
            result.setStatus(ResultStatus.ERROR);
            result.setErrors(errors);
            return result;
        }
        
        //TODO ValintaTyyppi
        KoulutusSisaltyvyys sisaltyvyys = new KoulutusSisaltyvyys(parentKomo, childKomo, ValintaTyyppi.ALL_OFF);
        koulutusSisaltyvyysDAO.insert(sisaltyvyys);
        return new ResultV1RDTO();
    }

    @Override
    public ResultV1RDTO unlink(String parent, String child) {
        System.out.println("unlink!");
        Koulutusmoduuli parentKomo = koulutusmoduuliDAO.findByOid(parent);
        List<ErrorV1RDTO> errors = new ArrayList<ErrorV1RDTO>();

        if (parentKomo != null) {
            Set<KoulutusSisaltyvyys> sisaltyvyydet = parentKomo
                    .getSisaltyvyysList();

            if (sisaltyvyydet.size() == 0) {
                errors.add(ErrorV1RDTO.createInfo("ei sisaltyvyyksia!"));
            } else {

                for (KoulutusSisaltyvyys sisaltyvyys : sisaltyvyydet) {
                    List<Koulutusmoduuli> remove = new ArrayList<Koulutusmoduuli>();
                    for (Koulutusmoduuli sisaltyva : sisaltyvyys
                            .getAlamoduuliList()) {
                        if (sisaltyva.getOid().equals(child)) {
                            remove.add(sisaltyva);
                        }
                    }
                    if (sisaltyvyys.getAlamoduuliList().size() == remove.size()) {
                        // poistetaan kokonaan!
                        System.out.println("poistetaan kokonaan!");

                        koulutusSisaltyvyysDAO.remove(sisaltyvyys);
                        errors.add(ErrorV1RDTO.createInfo("linkki poistettu!"));
                    } else {
                        if(remove.size()!=0){
                        for (Koulutusmoduuli moduuli : remove) {
                            System.out.println("poistetaan " + parentKomo
                                    + "->" + moduuli);
                            sisaltyvyys.removeAlamoduuli(moduuli);
                        }
                        koulutusSisaltyvyysDAO.update(sisaltyvyys);
                        errors.add(ErrorV1RDTO
                                .createInfo("linkin osa poistettu!"));
                    }
                    }

                }
            }
        } else {
            errors.add(ErrorV1RDTO.createValidationError("parent", "not found"));
        }
        ResultV1RDTO result = new ResultV1RDTO();
        result.setErrors(errors);
        return result;
    }

    @Override
    public ResultV1RDTO<Set<String>> children(String parent) {
        Set<String> oids = Sets.newHashSet();
        oids.addAll(koulutusSisaltyvyysDAO.getChildren(parent));
        return new ResultV1RDTO<Set<String>>(oids);
    }

    @Override
    public ResultV1RDTO<Set<String>> parents(String child) {
        Set<String> oids = Sets.newHashSet();
        oids.addAll(koulutusSisaltyvyysDAO.getParents(child));

        return new ResultV1RDTO<Set<String>>(oids);
    }

}
