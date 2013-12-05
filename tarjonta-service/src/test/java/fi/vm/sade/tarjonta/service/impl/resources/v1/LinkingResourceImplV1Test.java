package fi.vm.sade.tarjonta.service.impl.resources.v1;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.impl.KoulutusSisaltyvyysDAOImpl;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.service.resources.v1.LinkingV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KomoLink;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO.ResultStatus;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("embedded-solr")
@Transactional()
public class LinkingResourceImplV1Test {

    @Autowired
    TarjontaFixtures tarjontaFixtures;

    @Autowired
    KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Autowired
    KoulutusSisaltyvyysDAOImpl koulutusSisaltyvyysDao;

    @Autowired
    LinkingV1Resource linkingResource;

    @Test
    public void testLinkingActions() {
        Koulutusmoduuli parent = tarjontaFixtures.createTutkintoOhjelma();
        parent = koulutusmoduuliDAO.insert(parent);
        String parentOid = parent.getOid();

        Koulutusmoduuli child = tarjontaFixtures.createTutkintoOhjelma();
        child = koulutusmoduuliDAO.insert(child);
        String childOid = child.getOid();

        ResultV1RDTO<?> vast = linkingResource.link(new KomoLink(parent.getOid(), child.getOid()));
        Assert.assertEquals(ResultStatus.OK, vast.getStatus());

        // childs for parent
        ResultV1RDTO<Set<String>> result = linkingResource.children(parent
                .getOid());
        Assert.assertEquals(ResultStatus.OK, result.getStatus());
        Assert.assertEquals(1, result.getResult().size());
        Assert.assertEquals(child.getOid(), result.getResult().iterator()
                .next());

        // childs for child
        result = linkingResource.children(child.getOid());
        Assert.assertEquals(ResultStatus.OK, result.getStatus());
        Assert.assertEquals(0, result.getResult().size());

        // parents for parent
        result = linkingResource.parents(parent.getOid());
        Assert.assertEquals(ResultStatus.OK, result.getStatus());
        Assert.assertEquals(0, result.getResult().size());

        // parents for child
        result = linkingResource.parents(child.getOid());
        Assert.assertEquals(ResultStatus.OK, result.getStatus());
        Assert.assertEquals(1, result.getResult().size());
        Assert.assertEquals(parent.getOid(), result.getResult().iterator()
                .next());

        // unlink
        vast = linkingResource.unlink(parentOid, childOid);
        Assert.assertEquals(ResultStatus.OK, vast.getStatus());

        // //unlink
        // vast = linkingResource.unlink(parentOid, childOid);
        // Assert.assertEquals(ResultStatus.ERROR, vast.getStatus());

        // childs for parent
        result = linkingResource.children(parent.getOid());
        Assert.assertEquals(ResultStatus.OK, result.getStatus());
        Assert.assertEquals(0, result.getResult().size());

        // childs for child
        result = linkingResource.children(child.getOid());
        Assert.assertEquals(ResultStatus.OK, result.getStatus());
        Assert.assertEquals(0, result.getResult().size());

        // parents for parent
        result = linkingResource.parents(parent.getOid());
        Assert.assertEquals(ResultStatus.OK, result.getStatus());
        Assert.assertEquals(0, result.getResult().size());

        // parents for child
        result = linkingResource.parents(child.getOid());
        Assert.assertEquals(ResultStatus.OK, result.getStatus());
        Assert.assertEquals(0, result.getResult().size());
    }

    /**
     * Itseensä linkkaaminen kielletty.
     */
    @Test
    public void testLinkToSelf() {

        Koulutusmoduuli komo = tarjontaFixtures.createTutkintoOhjelma();
        komo = koulutusmoduuliDAO.insert(komo);
        String komoid = komo.getOid();

        ResultV1RDTO<?> vast = linkingResource.link(new KomoLink(komoid, komoid));
        Assert.assertEquals(ResultStatus.ERROR, vast.getStatus());

        // childs for parent
        ResultV1RDTO<Set<String>> result = linkingResource.children(komo
                .getOid());
        Assert.assertEquals(ResultStatus.OK, result.getStatus());
        Assert.assertEquals(0, result.getResult().size());
    }

    /**
     * Loopin tekeminen kielletty.
     */
    @Test
    public void testLinkLoop() {

        Koulutusmoduuli komo1 = tarjontaFixtures.createTutkintoOhjelma();
        komo1 = koulutusmoduuliDAO.insert(komo1);

        Koulutusmoduuli komo2 = tarjontaFixtures.createTutkintoOhjelma();
        komo2 = koulutusmoduuliDAO.insert(komo2);

        Koulutusmoduuli komo3 = tarjontaFixtures.createTutkintoOhjelma();
        komo3 = koulutusmoduuliDAO.insert(komo3);

        ResultV1RDTO<?> vast = linkingResource.link(new KomoLink(komo1.getOid(), komo2.getOid()));
        Assert.assertEquals(ResultStatus.OK, vast.getStatus());

        vast = linkingResource.link(new KomoLink(komo2.getOid(), komo3.getOid()));
        Assert.assertEquals(ResultStatus.OK, vast.getStatus());

        vast = linkingResource.link(new KomoLink(komo3.getOid(), komo1.getOid()));
        Assert.assertEquals(ResultStatus.ERROR, vast.getStatus());
    }

    /**
     * test api: Loopin tekeminen kielletty.
     */
    @Test
    public void testTestLinkLoop() {

        Koulutusmoduuli komo1 = tarjontaFixtures.createTutkintoOhjelma();
        komo1 = koulutusmoduuliDAO.insert(komo1);

        Koulutusmoduuli komo2 = tarjontaFixtures.createTutkintoOhjelma();
        komo2 = koulutusmoduuliDAO.insert(komo2);

        Koulutusmoduuli komo3 = tarjontaFixtures.createTutkintoOhjelma();
        komo3 = koulutusmoduuliDAO.insert(komo3);

        ResultV1RDTO<?> vast = linkingResource.link(new KomoLink(komo1.getOid(), komo2.getOid()));
        Assert.assertEquals(ResultStatus.OK, vast.getStatus());

        vast = linkingResource.link(new KomoLink(komo2.getOid(), komo3.getOid()));
        Assert.assertEquals(ResultStatus.OK, vast.getStatus());

        vast = linkingResource.test(new KomoLink(komo3.getOid(), komo1.getOid()));
        Assert.assertEquals(ResultStatus.ERROR, vast.getStatus());
    }

}
