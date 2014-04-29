package fi.vm.sade.tarjonta.service.impl.resources.v1;

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
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.service.enums.KoulutustyyppiEnum;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.service.search.it.TarjontaSearchServiceTest;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

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

    private ContextDataService contextDataService = Mockito
            .mock(ContextDataService.class);
    private HakukohdeDAO hakukohdeDAO = Mockito.mock(HakukohdeDAO.class);

    private IndexerResource indexerResource = Mockito
            .mock(IndexerResource.class);

    private PublicationDataService publicationDataService = Mockito
            .mock(PublicationDataService.class);

    private KoulutusmoduuliToteutus komoto1;

    private KoulutusSisaltyvyysDAO koulutusSisaltyvyysDAO = Mockito.mock(KoulutusSisaltyvyysDAO.class);
    private KoulutusmoduuliDAO koulutusmoduuliDAO = Mockito.mock(KoulutusmoduuliDAO.class);

    @Before
    public void setUp() throws Exception {
        LOG.info("setUp()");

        // Stub oid service
        Mockito.stub(oidService.get(TarjontaOidType.HAKU))
                .toReturn("1.2.3.4.5");

        // Stub koodisto values
        TarjontaSearchServiceTest.stubKoodi(koodiService, "kieli_fi", "FI");

        // stub komotodao
        komoto1 = new KoulutusmoduuliToteutus();
        komoto1.setAlkamiskausiUri("kausi");
        komoto1.setAlkamisVuosi(2005);
        Mockito.stub(koulutusmoduuliToteutusDAO.findByOid("komoto-1"))
                .toReturn(komoto1);
        Koulutusmoduuli komo = new Koulutusmoduuli();
        komo.setKoulutustyyppiEnum(KoulutustyyppiEnum.KORKEAKOULUTUS);
        komoto1.setKoulutusmoduuli(komo);
        Hakukohde hk = getHakukohde();
        komoto1.addHakukohde(hk);
        hk.addKoulutusmoduuliToteutus(komoto1);
        Whitebox.setInternalState(koulutusResource,
                "koulutusmoduuliToteutusDAO", koulutusmoduuliToteutusDAO);
        Whitebox.setInternalState(koulutusResource, "permissionChecker",
                permissionChecker);

        Whitebox.setInternalState(koulutusResource, "converterV1", converterV1);
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
        ResultV1RDTO result = koulutusResource.deleteByOid("komoto-1");
        Assert.assertEquals(ResultV1RDTO.ResultStatus.ERROR, result.getStatus());

        //poistettu hakukohde kiinni
        komoto1.getHakukohdes().iterator().next().setTila(TarjontaTila.POISTETTU);
        result = koulutusResource.deleteByOid("komoto-1");
        Assert.assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
    }

}
