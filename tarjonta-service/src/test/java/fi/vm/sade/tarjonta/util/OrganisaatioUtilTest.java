package fi.vm.sade.tarjonta.util;

import fi.vm.sade.tarjonta.service.impl.resources.v1.util.OrganisaatioUtil;
import fi.vm.sade.tarjonta.shared.organisaatio.OrganisaatioKelaDTO;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static java.util.Collections.singletonList;

public class OrganisaatioUtilTest {

    @Test
    public void testEmptyOrganisaatio() {
        OrganisaatioKelaDTO o0 = OrganisaatioUtil.findOrganisaatioWithOppilaitosStartingFrom(null, "");
        Assert.assertNull(o0);
        OrganisaatioKelaDTO o1 = OrganisaatioUtil.findOrganisaatioWithOppilaitosStartingFrom(Collections.<OrganisaatioKelaDTO>emptyList(), "");
        Assert.assertNull(o1);
    }

    @Test
    public void testFindSelf() {
        OrganisaatioKelaDTO a = createOrg("A");
        OrganisaatioKelaDTO b = createOrg("B");
        OrganisaatioKelaDTO c = createOrg("C", "OPPILAITOS");

        connect(a, b);
        connect(b, c);

        OrganisaatioKelaDTO r = OrganisaatioUtil.findOrganisaatioWithOppilaitosStartingFrom(singletonList(a), "C");

        Assert.assertEquals(c, r);
    }
    @Test
    public void testFindChild() {
        OrganisaatioKelaDTO a = createOrg("A");
        OrganisaatioKelaDTO b = createOrg("B");
        OrganisaatioKelaDTO c = createOrg("C");
        OrganisaatioKelaDTO d = createOrg("D");
        OrganisaatioKelaDTO e = createOrg("E");
        OrganisaatioKelaDTO f = createOrg("F", "OPPILAITOS");

        connect(a, b);
        connect(b, c);
        connect(c, d, e);
        connect(e, f);

        OrganisaatioKelaDTO r = OrganisaatioUtil.findOrganisaatioWithOppilaitosStartingFrom(singletonList(a), "C");

        Assert.assertEquals(f, r);
    }
    private static void connect(OrganisaatioKelaDTO parent, OrganisaatioKelaDTO ... children) {
        parent.setChildren(Arrays.asList(children));
    }

    private static OrganisaatioKelaDTO createOrg(String tarjoajaOid) {
        OrganisaatioKelaDTO k = new OrganisaatioKelaDTO();
        k.setOid(tarjoajaOid);
        return k;
    }

    private static OrganisaatioKelaDTO createOrg(String tarjoajaOid, String oppilaitos) {
        OrganisaatioKelaDTO k = new OrganisaatioKelaDTO();
        k.setOid(tarjoajaOid);
        k.setOppilaitosKoodi(oppilaitos);
        return k;
    }
}
