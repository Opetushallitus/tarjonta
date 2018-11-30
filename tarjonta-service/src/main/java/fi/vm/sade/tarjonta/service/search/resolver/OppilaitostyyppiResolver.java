package fi.vm.sade.tarjonta.service.search.resolver;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper.getKoodiURIFromVersionedUri;

@Component
public class OppilaitostyyppiResolver {

    @Autowired
    private OrganisaatioService organisaatioService;

    public String resolve(OrganisaatioPerustieto organisaatioPerustieto) {
        if (organisaatioPerustieto.getOppilaitostyyppi() != null) {
            return getKoodiURIFromVersionedUri(organisaatioPerustieto.getOppilaitostyyppi());
        } else {
            ArrayList<String> oids = getReversedParentOrgOids(organisaatioPerustieto);
            String oppilaitostyyppi = getOppilaitostyyppiFromParentOrganisation(oids);
            if (oppilaitostyyppi != null) {
                return oppilaitostyyppi;
            }
        }
        return null;
    }

    private String getOppilaitostyyppiFromParentOrganisation(ArrayList<String> oids) {
        List<OrganisaatioPerustieto> parents = organisaatioService.findByUsingOrganisaatioCache(new HashSet<>(oids));
        for (OrganisaatioPerustieto parent : parents) {
            if (parent != null) {
                if (parent.getOppilaitostyyppi() != null) {
                    return getKoodiURIFromVersionedUri(parent.getOppilaitostyyppi());
                }
            }
        }
        return null;
    }

    private ArrayList<String> getReversedParentOrgOids(OrganisaatioPerustieto org) {
        ArrayList<String> oids = getParentOrgOids(org);
        Collections.reverse(oids);
        return oids;
    }

    private ArrayList<String> getParentOrgOids(OrganisaatioPerustieto org) {
        ArrayList<String> oids = Lists.newArrayList();

        if (org.getParentOidPath() == null) {
            return oids;
        }

        Iterables.addAll(oids, Splitter.on("/").omitEmptyStrings().split(org.getParentOidPath()));
        return oids;
    }
}
