package fi.vm.sade.tarjonta.service.tasks;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.model.KoulutusPermission;
import fi.vm.sade.tarjonta.model.KoulutusPermissionType;
import fi.vm.sade.tarjonta.shared.amkouteDTO.AmkouteMaarays;
import fi.vm.sade.tarjonta.shared.amkouteDTO.AmkouteMaaraystyyppiValue;
import fi.vm.sade.tarjonta.shared.amkouteDTO.AmkouteOrgDTO;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KoulutusPermissionCreator {

    static final Set<String> validKoodistos = Sets.newHashSet("koulutus", "osaamisala", "kieli");

    public static List<KoulutusPermission> convertFromDto(List<AmkouteOrgDTO> orgs) {
        List<KoulutusPermission> permissions = new ArrayList<>();

        for (AmkouteOrgDTO org : orgs) {
            for (AmkouteMaarays maarays : org.getMaaraykset()) {
                permissions.addAll(createMaarays(maarays, org.getJarjestajaOid(), null, org.getAlkupvm(), org.getLoppupvm()));
            }
        }
        return permissions;
    }

    private static List<KoulutusPermission> createMaarays(AmkouteMaarays maarays, String jarjestajaOid, String ylaMaarayksenKoulutusTaiOsaamisalaKoodi, Date alkupvm, Date loppupvm) {
        if(!validKoodistos.contains(maarays.getKoodisto())) return Lists.newArrayList();

        List<KoulutusPermission> permissions = Lists.newArrayList();

        String maarayksenKohdeKoodi = ylaMaarayksenKoulutusTaiOsaamisalaKoodi;

        switch(maarays.getKoodisto()) {
            case "koulutus":
            case "osaamisala": maarayksenKohdeKoodi  = maarays.getKoodisto() + "_" + maarays.getKoodiarvo();  break;
        }

        AmkouteMaaraystyyppiValue tyyppi = maarays.getMaaraystyyppi().getTunniste();
        switch(tyyppi) {
            case OIKEUS:
                permissions.add(new KoulutusPermission(jarjestajaOid, maarayksenKohdeKoodi, maarays.getKoodisto(), maarays.getKoodisto() + "_" + maarays.getKoodiarvo(), alkupvm, loppupvm, KoulutusPermissionType.OIKEUS));
                break;
            case RAJOITE:
                permissions.add(new KoulutusPermission(jarjestajaOid, maarayksenKohdeKoodi, maarays.getKoodisto(), maarays.getKoodisto() + "_" + maarays.getKoodiarvo(), alkupvm, loppupvm, KoulutusPermissionType.RAJOITE));
                break;
            case POIKKEUS:
                permissions.add(new KoulutusPermission(jarjestajaOid, maarayksenKohdeKoodi, maarays.getKoodisto(), maarays.getKoodisto() + "_" + maarays.getKoodiarvo(), alkupvm, loppupvm, KoulutusPermissionType.POIKKEUS));
                break;
            case VELVOITE:
                permissions.add(new KoulutusPermission(jarjestajaOid, maarayksenKohdeKoodi, maarays.getKoodisto(), maarays.getKoodisto() + "_" + maarays.getKoodiarvo(), alkupvm, loppupvm, KoulutusPermissionType.VELVOITE));
                break;
            default:
                throw new RuntimeException("Unknown määräystyyppi");
        }
        for (AmkouteMaarays alimaarays : maarays.getAliMaaraykset()) {
            permissions.addAll(createMaarays(alimaarays, jarjestajaOid, maarayksenKohdeKoodi, alkupvm, loppupvm));
        }
        return permissions;
    }

}