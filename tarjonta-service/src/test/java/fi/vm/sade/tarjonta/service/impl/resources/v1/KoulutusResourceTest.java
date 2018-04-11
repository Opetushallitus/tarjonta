package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.auditlog.AuditHelper;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.EntityConverterToRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.service.search.it.TarjontaSearchServiceTest;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;

/**
 * Tests for api, does not persist any data. all related services are mocked.
 */
public class KoulutusResourceTest {

    private static final Logger LOG = LoggerFactory
            .getLogger(KoulutusResourceTest.class);

    private KoulutusResourceImplV1 koulutusResource = new KoulutusResourceImplV1();

    private OidService oidService = Mockito.mock(OidService.class);

    private KoodiService koodiService = Mockito.mock(KoodiService.class);

    private PermissionChecker permissionChecker = Mockito
            .mock(PermissionChecker.class);

    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO = Mockito
            .mock(KoulutusmoduuliToteutusDAO.class);

    private ConverterV1 converterV1 = Mockito.mock(ConverterV1.class);

    private EntityConverterToRDTO converterToRDTO = Mockito.mock(EntityConverterToRDTO.class);

    private ContextDataService contextDataService = Mockito
            .mock(ContextDataService.class);
    private HakukohdeDAO hakukohdeDAO = Mockito.mock(HakukohdeDAO.class);

    private IndexerResource indexerResource = Mockito
            .mock(IndexerResource.class);

    private PublicationDataService publicationDataService = Mockito
            .mock(PublicationDataService.class);

    private KoulutusmoduuliToteutus komoto1;
    private KoulutusmoduuliToteutus komoto3;

    private Hakukohde hk2;
    
    private Koulutusmoduuli komo;
    
    private KoulutusSisaltyvyysDAO koulutusSisaltyvyysDAO = Mockito.mock(KoulutusSisaltyvyysDAO.class);
    private KoulutusmoduuliDAO koulutusmoduuliDAO = Mockito.mock(KoulutusmoduuliDAO.class);

    private HttpTestHelper httpTestHelper = new HttpTestHelper(true);
    private HttpServletRequest request = httpTestHelper.request;

    @Before
    public void setUp() throws Exception {
        LOG.info("setUp()");

        // Stub oid service
        Mockito.stub(oidService.get(TarjontaOidType.HAKU))
                .toReturn("1.2.3.4.5");

        // Stub koodisto values
        TarjontaSearchServiceTest.stubKoodi(koodiService, "kieli_fi", "FI");

        AuditHelper auditHelper = mock(AuditHelper.class);
        Whitebox.setInternalState(koulutusResource, "auditHelper", auditHelper);

        komo = new Koulutusmoduuli();
        komo.setKoulutustyyppiEnum(ModuulityyppiEnum.KORKEAKOULUTUS);

        // stub komotodao, one hakukohde with two koulutuses
        Hakukohde hk1 = getHakukohde();

        komoto1 = new KoulutusmoduuliToteutus();
        komoto1.setAlkamiskausiUri("kausi");
        komoto1.setAlkamisVuosi(2005);
        komoto1.setKoulutusmoduuli(komo);
        komoto1.addHakukohde(hk1);
        komoto1.setToteutustyyppi(ToteutustyyppiEnum.AMMATTITUTKINTO);
        Mockito.stub(koulutusmoduuliToteutusDAO.findByOid("komoto-1"))
                .toReturn(komoto1);
        komo.setKoulutustyyppiEnum(ModuulityyppiEnum.KORKEAKOULUTUS);

        KoulutusmoduuliToteutus komoto2 = new KoulutusmoduuliToteutus();
        komoto2.setAlkamiskausiUri("kausi");
        komoto2.setAlkamisVuosi(2006);
        komoto2.setKoulutusmoduuli(komo);
        komoto2.addHakukohde(hk1);
        komoto2.setToteutustyyppi(ToteutustyyppiEnum.AMMATTITUTKINTO);
        Mockito.stub(koulutusmoduuliToteutusDAO.findByOid("komoto-2"))
                .toReturn(komoto2);

        hk1.addKoulutusmoduuliToteutus(komoto1);
        hk1.addKoulutusmoduuliToteutus(komoto2);


        // another hakukohde with single koulutus
        hk2 = getHakukohde();

        komoto3 = new KoulutusmoduuliToteutus();
        komoto3.setAlkamiskausiUri("kausi");
        komoto3.setAlkamisVuosi(2005);
        komoto3.setKoulutusmoduuli(komo);
        komoto3.addHakukohde(hk2);
        komoto3.setToteutustyyppi(ToteutustyyppiEnum.AMMATTITUTKINTO);
        Mockito.stub(koulutusmoduuliToteutusDAO.findByOid("komoto-3"))
                .toReturn(komoto3);


        hk2.addKoulutusmoduuliToteutus(komoto3);


        Whitebox.setInternalState(koulutusResource,
                "koulutusmoduuliToteutusDAO", koulutusmoduuliToteutusDAO);
        Whitebox.setInternalState(koulutusResource, "permissionChecker",
                permissionChecker);

        Whitebox.setInternalState(koulutusResource, "converterV1", converterV1);
        Whitebox.setInternalState(koulutusResource, "converterToRDTO", converterToRDTO);
        Whitebox.setInternalState(koulutusResource, "contextDataService",
                contextDataService);


        Mockito.stub(hakukohdeDAO.insert(Mockito.any(Hakukohde.class)))
                .toAnswer(new Answer<Hakukohde>() {
                    // palauta sama olio
                    @Override
                    public Hakukohde answer(InvocationOnMock invocation)
                    throws Throwable {
                        Hakukohde hk = (Hakukohde) invocation.getArguments()[0];
                        return hk;
                    }
                });

        Whitebox.setInternalState(koulutusResource, "hakukohdeDAO",
                hakukohdeDAO);
        Whitebox.setInternalState(koulutusResource, "indexerResource",
                indexerResource);
        Whitebox.setInternalState(koulutusResource, "publicationDataService",
                publicationDataService);
        Whitebox.setInternalState(koulutusResource, "koulutusSisaltyvyysDAO", koulutusSisaltyvyysDAO);
        Whitebox.setInternalState(koulutusResource, "koulutusmoduuliDAO", koulutusmoduuliDAO);
    }

    @After
    public void tearDown() {
        LOG.info("tearDown()");
    }

    private Hakukohde getHakukohde() {
        Hakukohde hk = new Hakukohde();
        hk.setOid("hakukohde-1");
        hk.setHakukohdeNimi("hk");
        hk.setTila(TarjontaTila.LUONNOS);
        return hk;
    }

    @Test
    public void testOVT7518() {
        //hakukohde kiinni
        ResultV1RDTO result = koulutusResource.deleteByOid("komoto-3", request);
        System.out.println("result" + result);
        Assert.assertEquals(ResultV1RDTO.ResultStatus.ERROR, result.getStatus());

        //poistettu hakukohde kiinni, koulutuksen tila PERUTTU
        hk2.setTila(TarjontaTila.POISTETTU);
        komoto3.setTila(TarjontaTila.PERUTTU);
        result = koulutusResource.deleteByOid("komoto-3", request);
        Assert.assertEquals(result.toString(), ResultV1RDTO.ResultStatus.OK, result.getStatus());

        //poistettu hakukohde kiinni
        komoto3.getHakukohdes().iterator().next().setTila(TarjontaTila.POISTETTU);
        result = koulutusResource.deleteByOid("komoto-3", request);
        Assert.assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
    }

    @Test
    public void testOVT7543() {
        //kaksi koulutusta kiinni hakukohteessa, ensimmäisen voi poistaa
        komo.setTila(TarjontaTila.JULKAISTU);
        komo.setKoulutustyyppiEnum(ModuulityyppiEnum.LUKIOKOULUTUS);
        ResultV1RDTO result = koulutusResource.deleteByOid("komoto-1", request);
        Assert.assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
        komo.setKoulutustyyppiEnum(ModuulityyppiEnum.LUKIOKOULUTUS);
        komoto1.setTila(TarjontaTila.POISTETTU);


        //kaksi koulutusta kiinni hakukohteessa, jälkimmäistä ei voi poistaa
        result = koulutusResource.deleteByOid("komoto-2", request);
        Assert.assertEquals(ResultV1RDTO.ResultStatus.ERROR, result.getStatus());
}

}
