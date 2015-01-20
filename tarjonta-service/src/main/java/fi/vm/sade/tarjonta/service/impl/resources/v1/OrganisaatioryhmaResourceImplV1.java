package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Ryhmaliitos;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.resources.v1.OrganisaatioryhmaV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.RyhmaliitosV1RDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;

@Transactional
public class OrganisaatioryhmaResourceImplV1 implements OrganisaatioryhmaV1Resource {

    @Autowired
    private HakukohdeDAO hakukohdeDAO;

    @Autowired
    private PermissionChecker permissionChecker;

    public ResultV1RDTO addRyhmaliitokset(String ryhmaOid, Set<RyhmaliitosV1RDTO> ryhmaliitokset) {
        for (RyhmaliitosV1RDTO ryhmaliitosDTO : ryhmaliitokset) {
            permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(ryhmaliitosDTO.getHakukohdeOid());
            updateHakukohteenLiitokset(ryhmaOid, ryhmaliitosDTO);
        }
        return returnOk();
    }

    public ResultV1RDTO removeRyhmaliitokset(String ryhmaOid, Set<String> hakukohdeOids) {
        for (String hakukohdeOid : hakukohdeOids) {
            permissionChecker.checkUpdateHakukohdeAndIgnoreParametersWhileChecking(hakukohdeOid);
            removeRyhmaliitos(ryhmaOid, hakukohdeOid);
        }
        return returnOk();
    }

    private void updateHakukohteenLiitokset(String ryhmaOid, RyhmaliitosV1RDTO ryhmaliitosDTO) {
        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(ryhmaliitosDTO.getHakukohdeOid());
        Ryhmaliitos ryhmaliitos = hakukohde.getRyhmaliitosByRyhmaOid(ryhmaOid);
        if (ryhmaliitos == null) {
            ryhmaliitos = createRyhmaliitos(ryhmaOid, ryhmaliitosDTO);
            ryhmaliitos.setHakukohde(hakukohde);
            hakukohde.addRyhmaliitos(ryhmaliitos);
        } else {
            updateRyhmaliitos(ryhmaliitos, ryhmaliitosDTO);
        }
        update(hakukohde);
    }

    private void removeRyhmaliitos(String ryhmaOid, String hakukohdeOid) {
        Hakukohde hakukohde = hakukohdeDAO.findHakukohdeByOid(hakukohdeOid);
        Ryhmaliitos ryhmaliitos = hakukohde.getRyhmaliitosByRyhmaOid(ryhmaOid);
        if (ryhmaliitos != null) {
            hakukohde.removeRyhmaliitos(ryhmaliitos);
        }
        update(hakukohde);
    }

    private void update(Hakukohde hakukohde) {
        hakukohde.setLastUpdateDate(new Date());
        hakukohdeDAO.update(hakukohde);
    }

    private Ryhmaliitos updateRyhmaliitos(Ryhmaliitos ryhmaliitos, RyhmaliitosV1RDTO ryhmaliitosDTO) {
        ryhmaliitos.setPrioriteetti(ryhmaliitosDTO.getPrioriteetti());
        return ryhmaliitos;
    }

    private Ryhmaliitos createRyhmaliitos(String ryhmaOid, RyhmaliitosV1RDTO ryhmaliitosDTO) {
        Ryhmaliitos ryhmaliitos = new Ryhmaliitos();
        ryhmaliitos.setRyhmaOid(ryhmaOid);
        ryhmaliitos.setPrioriteetti(ryhmaliitosDTO.getPrioriteetti());
        return ryhmaliitos;
    }

    private ResultV1RDTO returnOk() {
        ResultV1RDTO result = new ResultV1RDTO();
        result.setStatus(ResultV1RDTO.ResultStatus.OK);
        return result;
    }

}
