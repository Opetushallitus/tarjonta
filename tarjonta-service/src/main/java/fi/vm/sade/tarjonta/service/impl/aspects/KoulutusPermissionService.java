package fi.vm.sade.tarjonta.service.impl.aspects;

import com.google.common.collect.Lists;
import fi.vm.sade.generic.service.exception.NotAuthorizedException;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.dao.KoulutusPermissionDAO;
import fi.vm.sade.tarjonta.model.KoulutusPermission;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAmmatillinenPerustutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KoulutusPermissionService {

    private static final String OPH_OID = "1.2.246.562.10.00000000001";

    @Autowired
    private KoulutusPermissionDAO koulutusPermissionDAO;

    @Autowired
    private OrganisaatioService organisaatioService;

    public void checkThatOrganizationIsAllowedToOrganizeEducation(KoulutusV1RDTO dto) {

        // If saving an existing education -> ignore check
        if (dto.getOid() != null && dto.getOid().length() > 0) {
            return;
        }

        // Currently check is only performed on ammatillinen perustutkinto
        if (!ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO.equals(dto.getToteutustyyppi())) {
            return;
        }

        String orgOid = dto.getOrganisaatio().getOid();
        String koulutusKoodi = null;
        if (dto.getKoulutuskoodi() != null) {
            koulutusKoodi = dto.getKoulutuskoodi().getUri();
        }
        String osaamisalaKoodi = null;
        if (dto.getKoulutusohjelma() != null) {
            osaamisalaKoodi = dto.getKoulutusohjelma().getUri();
        }
        List<String> opetuskielet = new ArrayList<String>();
        if (dto.getOpetuskielis().getUris() != null) {
            for (String kieli : dto.getOpetuskielis().getUrisAsStringList(false)) {
                opetuskielet.add(kieli);
            }
        }

        OrganisaatioDTO org = organisaatioService.findByOid(orgOid);

        String kuntaKoodi = org.getKotipaikka();

        List<String> orgOids = Lists.newArrayList(org.getOid());
        if (org.getParentOidPath() != null) {
            String[] parentPath = org.getParentOidPath().split("\\|");
            for (String parent : parentPath) {
                if (!parent.isEmpty() && !OPH_OID.equals(parent)) {
                    orgOids.add(parent);
                }
            }
        }

        if (koulutusKoodi != null) {
            checkPermissions(koulutusPermissionDAO.find(orgOids, "koulutus", koulutusKoodi), koulutusKoodi);
        }

        if (osaamisalaKoodi != null) {
            checkPermissions(koulutusPermissionDAO.find(orgOids, "osaamisala", osaamisalaKoodi), osaamisalaKoodi);
        }

        for (String kieli : opetuskielet) {
            checkPermissions(koulutusPermissionDAO.find(orgOids, "kieli", kieli), kieli);
        }

        checkPermissions(koulutusPermissionDAO.find(orgOids, "kunta", kuntaKoodi), kuntaKoodi);

    }

    private void checkPermissions(List<KoulutusPermission> permissions, String code) {
        if (permissions.isEmpty()) {
            throw new NotAuthorizedException("Organization not allowed to organize education, permission missing for code: " + code);
        }
    }

}
