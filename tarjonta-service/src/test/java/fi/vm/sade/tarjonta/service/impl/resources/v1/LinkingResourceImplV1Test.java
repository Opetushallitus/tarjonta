package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.tarjonta.TestUtilityBase;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.service.impl.resources.v1.linking.validation.LinkingValidationMessages;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KomoLink;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO.ResultStatus;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class})
@ActiveProfiles("embedded-solr")
@Transactional()
public class LinkingResourceImplV1Test extends TestUtilityBase {
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

    @Test
    public void testRemoveMultipleLinksActions() {
        Koulutusmoduuli parent = tarjontaFixtures.createTutkintoOhjelma();
        parent = koulutusmoduuliDAO.insert(parent);
        String parentOid = parent.getOid();

        Koulutusmoduuli child1 = tarjontaFixtures.createTutkintoOhjelma();
        child1 = koulutusmoduuliDAO.insert(child1);
        String childOid1 = child1.getOid();

        Koulutusmoduuli child2 = tarjontaFixtures.createTutkintoOhjelma();
        child2 = koulutusmoduuliDAO.insert(child2);
        String childOid2 = child2.getOid();

        Koulutusmoduuli leafChild1 = tarjontaFixtures.createTutkintoOhjelma();
        leafChild1 = koulutusmoduuliDAO.insert(leafChild1);
        String leafChildOid1 = leafChild1.getOid();

        ResultV1RDTO<?> vast = linkingResource.link(new KomoLink(parentOid, childOid1));
        Assert.assertEquals(ResultStatus.OK, vast.getStatus());

        vast = linkingResource.link(new KomoLink(parentOid, childOid2));
        Assert.assertEquals(ResultStatus.OK, vast.getStatus());

        vast = linkingResource.link(new KomoLink(childOid2, leafChildOid1));
        Assert.assertEquals(ResultStatus.OK, vast.getStatus());

        final String COMMA_SEPARATED_OIDS = childOid1 + "," + childOid2;

        //expect a validation error. 
        vast = linkingResource.multiUnlink(parentOid, COMMA_SEPARATED_OIDS);
        Assert.assertEquals(ResultStatus.VALIDATION, vast.getStatus());
        Assert.assertEquals(1, vast.getErrors().size());
        Assert.assertEquals(1, vast.getErrors().get(0).getErrorMessageParameters().size());
        Assert.assertEquals(childOid2, vast.getErrors().get(0).getErrorMessageParameters().get(0));
        Assert.assertEquals("childs", vast.getErrors().get(0).getErrorField());
        Assert.assertEquals(LinkingValidationMessages.LINKING_OID_HAS_CHILDREN.name(), vast.getErrors().get(0).getErrorMessageKey());

        //unlink the problem oid's children 
        vast = linkingResource.multiUnlink(vast.getErrors().get(0).getErrorMessageParameters().get(0), leafChildOid1);
        Assert.assertEquals(ResultStatus.OK, vast.getStatus());

        //try again to remove the oids -> success
        vast = linkingResource.multiUnlink(parentOid, COMMA_SEPARATED_OIDS);
        Assert.assertEquals(ResultStatus.OK, vast.getStatus());
    }

    @Test
    public void testUnLinkErrors() {
        Koulutusmoduuli parent = tarjontaFixtures.createTutkintoOhjelma();
        parent = koulutusmoduuliDAO.insert(parent);
        final String parentOid = parent.getOid();

        Koulutusmoduuli child = tarjontaFixtures.createTutkintoOhjelma();
        child = koulutusmoduuliDAO.insert(child);
        final String childOid = child.getOid();

        //an invalid parentd oid error
        ResultV1RDTO<?> vast = linkingResource.multiUnlink("1234", "1234");
        Assert.assertEquals(ResultStatus.VALIDATION, vast.getStatus());
        Assert.assertEquals("parent", vast.getErrors().get(0).getErrorField());
        Assert.assertEquals(LinkingValidationMessages.LINKING_PARENT_OID_NOT_FOUND.name(), vast.getErrors().get(0).getErrorMessageKey());
        Assert.assertEquals(1, vast.getErrors().size());
        Assert.assertEquals(1, vast.getErrors().get(0).getErrorMessageParameters().size());

        //an invalid child oid
        vast = linkingResource.multiUnlink(parentOid, null);
        Assert.assertEquals(ResultStatus.VALIDATION, vast.getStatus());
        Assert.assertEquals("parent", vast.getErrors().get(0).getErrorField());
        Assert.assertEquals(LinkingValidationMessages.LINKING_PARENT_HAS_NO_CHILDREN.name(), vast.getErrors().get(0).getErrorMessageKey());
        Assert.assertEquals(1, vast.getErrors().size());
        Assert.assertEquals(1, vast.getErrors().get(0).getErrorMessageParameters().size());

        vast = linkingResource.link(new KomoLink(parentOid, childOid));
        Assert.assertEquals(ResultStatus.OK, vast.getStatus());

        vast = linkingResource.multiUnlink(parentOid, null);
        Assert.assertEquals(ResultStatus.VALIDATION, vast.getStatus());
        Assert.assertEquals("childs", vast.getErrors().get(0).getErrorField());
        Assert.assertEquals(LinkingValidationMessages.LINKING_MISSING_CHILD_OIDS.name(), vast.getErrors().get(0).getErrorMessageKey());
        Assert.assertEquals(1, vast.getErrors().size());
        Assert.assertEquals(0, vast.getErrors().get(0).getErrorMessageParameters().size());

        //an invalid child oids error
        vast = linkingResource.multiUnlink(parentOid, "1234,12345,12346");
        Assert.assertEquals(ResultStatus.VALIDATION, vast.getStatus());
        Assert.assertEquals("childs", vast.getErrors().get(0).getErrorField());
        Assert.assertEquals(LinkingValidationMessages.LINKING_CHILD_OID_NOT_FOUND.name(), vast.getErrors().get(0).getErrorMessageKey());
        Assert.assertEquals(3, vast.getErrors().size());
        Assert.assertEquals(1, vast.getErrors().get(0).getErrorMessageParameters().size());
    }
}
