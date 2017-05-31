package fi.vm.sade.tarjonta.service.impl.resources.v1.util;

import fi.vm.sade.tarjonta.shared.organisaatio.OrganisaatioKelaDTO;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrganisaatioUtil {

    public static OrganisaatioKelaDTO findOrganisaatioWithOppilaitosStartingFrom(List<OrganisaatioKelaDTO> organisaatiot, String tarjoajaOid) {
        if(organisaatiot != null && !organisaatiot.isEmpty()) {
            List<OrganisaatioKelaDTO> chainFromParentsToTarjoaja = fromParentTo(organisaatiot, tarjoajaOid);
            if (chainFromParentsToTarjoaja != null && !chainFromParentsToTarjoaja.isEmpty()) {
                Collections.reverse(chainFromParentsToTarjoaja);
                // from tarjoaja to parents
                for (OrganisaatioKelaDTO current : chainFromParentsToTarjoaja) {
                    if (current.getOppilaitosKoodi() != null) {
                        return current;
                    }
                }
                // from tarjoaja to children
                OrganisaatioKelaDTO children = oppilaitosFromChildren(chainFromParentsToTarjoaja.iterator().next().getChildren());
                if (children != null) {
                    return children;
                }
            }
        }
        return null;
    }

    private static OrganisaatioKelaDTO oppilaitosFromChildren(List<OrganisaatioKelaDTO> children) {
        if(children == null || children.isEmpty()) {
            return null;
        } else {
            List<OrganisaatioKelaDTO> childrensChildren = new ArrayList<>();
            for (OrganisaatioKelaDTO c : children) {
                if (c.getOppilaitosKoodi() != null) {
                    return c;
                }
                childrensChildren.addAll(c.getChildren() != null ? c.getChildren() : Collections.<OrganisaatioKelaDTO>emptyList());
            }
            return oppilaitosFromChildren(childrensChildren);
        }
    }
    private static List<OrganisaatioKelaDTO> fromParentTo(List<OrganisaatioKelaDTO> parents, String tarjoajaOid) {
        for(OrganisaatioKelaDTO parent: parents) {
            // it's this
            if(tarjoajaOid.equals(parent.getOid())) {
                return Collections.singletonList(parent);
            }
            // check children
            List<OrganisaatioKelaDTO> chain = fromParentTo(parent.getChildren(), tarjoajaOid);
            if(chain != null) {
                List<OrganisaatioKelaDTO> result = new ArrayList<>();
                result.add(parent);
                result.addAll(chain);
                return result;
            }
        }
        return null;
    }
}
