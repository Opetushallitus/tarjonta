package fi.vm.sade.tarjonta.service.tasks;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.tarjonta.dao.KoulutusPermissionDAO;
import fi.vm.sade.tarjonta.model.KoulutusPermission;
import fi.vm.sade.tarjonta.shared.amkouteDTO.AmkouteJarjestamiskuntaDTO;
import fi.vm.sade.tarjonta.shared.amkouteDTO.AmkouteKoulutusDTO;
import fi.vm.sade.tarjonta.shared.amkouteDTO.AmkouteOpetuskieliDTO;
import fi.vm.sade.tarjonta.shared.amkouteDTO.AmkouteOrgDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Service
public class KoulutusPermissionSynchronizer {

    @Autowired
    KoulutusPermissionDAO koulutusPermissionDAO;

    private static final Map<String, String> opetuskieliKoodiMap;
    static {
        opetuskieliKoodiMap = new HashMap<String, String>();
        opetuskieliKoodiMap.put("1", "kieli_fi");
        opetuskieliKoodiMap.put("2", "kieli_sv");
        opetuskieliKoodiMap.put("4", "kieli_en");
        opetuskieliKoodiMap.put("5", "kieli_se");
    }

    final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(KoulutusPermissionSynchronizer.class);

    // Kerran vuorokaudessa
    @Scheduled(fixedDelay=1000 * 60 * 60 * 24)
    @Transactional
    public void runUpdate() throws MalformedURLException {
        LOG.info("KoulutusPermissions start update");

        ObjectMapper objectMapper = new ObjectMapper();
        List<AmkouteOrgDTO> orgs = new ArrayList<AmkouteOrgDTO>();

        try {
            orgs = objectMapper.readValue(
                    new URL("https://oosp.csc.fi/api/public/koulutustarjonta"),
                    new TypeReference<List<AmkouteOrgDTO>>() {}
            );
        }
        catch (JsonParseException e) {
            LOG.error("KoulutusPermission update failed, JSON parse error", e);
        }
        catch (JsonMappingException e) {
            LOG.error("KoulutusPermission update failed, JSON mapping error", e);
        }
        catch (IOException e) {
            LOG.error("KoulutusPermission update failed, IOException", e);
        }

        if (orgs.size() == 0) {
            LOG.error("KoulutusPermission update failed: no permissions returned in JSON");
        }
        else {
            updatePermissionsToDb(orgs);
            LOG.info("KoulutusPermissions updated");
        }
    }

    public void updatePermissionsToDb(List<AmkouteOrgDTO> orgs) {
        koulutusPermissionDAO.removeAll();

        for (AmkouteOrgDTO org : orgs) {
            for (KoulutusPermission permission : convertFromDto(org)) {
                koulutusPermissionDAO.insert(permission);
            }
        }
    }

    public static List<KoulutusPermission> convertFromDto(AmkouteOrgDTO org) {
        List<KoulutusPermission> permissions = new ArrayList<KoulutusPermission>();

        // Tarkista, että organisaatiolla on kaikki tarvittavat tiedot
        if (org.getKoulutukset() == null || org.getJarjestamiskunnat() == null || org.getOpetuskielet() == null) {
            return permissions;
        }

        Map<String, KoulutusPermission> koulutusKoodit = new HashMap<String, KoulutusPermission>();

        for (AmkouteKoulutusDTO permissionDto : org.getKoulutukset()) {
            if (permissionDto.getOsaamisala() != null) {
                permissions.add(new KoulutusPermission(
                        org.getOid(),
                        "osaamisala",
                        "osaamisala_" + permissionDto.getOsaamisala(),
                        permissionDto.getAlkupvm(),
                        permissionDto.getLoppupvm()
                ));
            }

            if (permissionDto.getTutkinto() != null) {
                koulutusKoodit.put(
                        permissionDto.getTutkinto(),
                        new KoulutusPermission(
                                org.getOid(),
                                "koulutus",
                                "koulutus_" + permissionDto.getTutkinto(),
                                permissionDto.getAlkupvm(),
                                permissionDto.getLoppupvm()
                        )
                );
            }
        }

        for (AmkouteOpetuskieliDTO permissionDto : org.getOpetuskielet()) {
            String kielikoodi = opetuskieliKoodiMap.get(permissionDto.getOppilaitoksenopetuskieli());
            if (kielikoodi != null) {
                permissions.add(new KoulutusPermission(
                        org.getOid(),
                        "kieli",
                        kielikoodi,
                        permissionDto.getAlkupvm(),
                        permissionDto.getLoppupvm()
                ));
            }
        }

        for (AmkouteJarjestamiskuntaDTO permissionDto : org.getJarjestamiskunnat()) {
            permissions.add(new KoulutusPermission(
                    org.getOid(),
                    "kunta",
                    "kunta_" + permissionDto.getKunta(),
                    permissionDto.getAlkupvm(),
                    permissionDto.getLoppupvm()
            ));
        }

        permissions.addAll(koulutusKoodit.values());

        return permissions;
    }

}