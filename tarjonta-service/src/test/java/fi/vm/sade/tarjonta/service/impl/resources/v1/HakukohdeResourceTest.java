package fi.vm.sade.tarjonta.service.impl.resources.v1;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.HashMap;

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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.service.enums.KoulutustyyppiEnum;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO.ResultStatus;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.service.search.it.TarjontaSearchServiceTest;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

public class HakukohdeResourceTest {

    private static final Logger LOG = LoggerFactory
            .getLogger(HakukohdeResourceTest.class);

    private HakukohdeResourceImplV1 hakukohdeResource = spy(new HakukohdeResourceImplV1());

    private OidService oidService = Mockito.mock(OidService.class);

    private KoodiService koodiService = Mockito.mock(KoodiService.class);

    private PermissionChecker permissionChecker = Mockito.mock(PermissionChecker.class);
    
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO = Mockito.mock(KoulutusmoduuliToteutusDAO.class); 

    private ConverterV1 converterV1 = Mockito.mock(ConverterV1.class);

    private ContextDataService contextDataService = Mockito.mock(ContextDataService.class);
    private HakuDAO hakuDAO = Mockito.mock(HakuDAO.class);
    private HakukohdeDAO hakukohdeDAO = Mockito.mock(HakukohdeDAO.class);

    private IndexerResource indexerResource = Mockito.mock(IndexerResource.class);

    private PublicationDataService publicationDataService = Mockito.mock(PublicationDataService.class);


    private Koulutusmoduuli komo;
    private KoulutusmoduuliToteutus komoto1;
    private KoulutusmoduuliToteutus komoto2;
    private KoulutusmoduuliToteutus komoto3;
    private KoulutusmoduuliToteutus komotoPeruttu;
    
    @Before
    public void setUp() throws Exception {
        LOG.info("setUp()");

        // Stub oid service
        Mockito.stub(oidService.get(TarjontaOidType.HAKU))
                .toReturn("1.2.3.4.5");

        HashMap<String,String> koulutusLajiJaKoulutusAste = new HashMap<String, String>();
        koulutusLajiJaKoulutusAste.put(HakukohdeResourceImplV1.KOULUTUSASTE_KEY,"LUKIOKOULUTUS");
        koulutusLajiJaKoulutusAste.put(HakukohdeResourceImplV1.KOULUTUSLAJI_KEY,"N");


        when(hakukohdeResource.getKoulutusAstetyyppiAndLajiForKoulutukses(anyList())).thenReturn(koulutusLajiJaKoulutusAste);





        // Stub koodisto values
        TarjontaSearchServiceTest.stubKoodi(koodiService, "kieli_fi", "FI");

        komo = new Koulutusmoduuli();
        komo.setKoulutustyyppiEnum(KoulutustyyppiEnum.KORKEAKOULUTUS);
        KoodistoUri koodistoUri = new KoodistoUri("koulutuslaji_n");


        
        //stub komotodao
        komoto1 = new KoulutusmoduuliToteutus();
        komoto1.setAlkamiskausiUri("kausi");
        komoto1.setAlkamisVuosi(2005);
        komoto1.setKoulutusmoduuli(komo);
        komoto1.getKoulutuslajis().add(koodistoUri);
        Mockito.stub(koulutusmoduuliToteutusDAO.findByOid("komoto-1")).toReturn(komoto1);
        komoto2 = new KoulutusmoduuliToteutus();
        komoto2.setAlkamiskausiUri("kausi");
        komoto2.setAlkamisVuosi(2006);
        komoto2.setKoulutusmoduuli(komo);
        komoto2.getKoulutuslajis().add(koodistoUri);
        Mockito.stub(koulutusmoduuliToteutusDAO.findByOid("komoto-2")).toReturn(komoto2);
        komoto3 = new KoulutusmoduuliToteutus();
        komoto3.setAlkamiskausiUri("kausi2");
        komoto3.setKoulutusmoduuli(komo);
        komoto3.setAlkamisVuosi(2005);
        komoto3.getKoulutuslajis().add(koodistoUri);

        komotoPeruttu = new KoulutusmoduuliToteutus();
        komotoPeruttu.setTila(TarjontaTila.PERUTTU);
        komotoPeruttu.setAlkamiskausiUri("kausi2");
        komotoPeruttu.setAlkamisVuosi(2005);
        
        Mockito.stub(koulutusmoduuliToteutusDAO.findByOid("komoto-3")).toReturn(komoto3);
        Whitebox.setInternalState(hakukohdeResource, "koulutusmoduuliToteutusDAO", koulutusmoduuliToteutusDAO);
        Whitebox.setInternalState(hakukohdeResource, "permissionChecker", permissionChecker);
        Mockito.stub(koulutusmoduuliToteutusDAO.findByOid("komoto-peruttu")).toReturn(komotoPeruttu);

        Mockito.stub(converterV1.toHakukohde(Mockito.any(HakukohdeV1RDTO.class))).toAnswer(new Answer<Hakukohde>() {
            //mockaa convertteria sen mitä tarvii:
            @Override public Hakukohde answer(InvocationOnMock invocation) throws Throwable {
                HakukohdeV1RDTO hkdto = (HakukohdeV1RDTO) invocation.getArguments()[0];
                Hakukohde hk = new Hakukohde();
                return hk;
            }
        });
        
        Whitebox.setInternalState(hakukohdeResource, "converterV1", converterV1);
        Whitebox.setInternalState(hakukohdeResource, "contextDataService", contextDataService);
        Whitebox.setInternalState(hakukohdeResource, "hakuDAO", hakuDAO);

        Mockito.stub(hakukohdeDAO.insert(Mockito.any(Hakukohde.class))).toAnswer(new Answer<Hakukohde>() {
            //palauta sama olio
            @Override public Hakukohde answer(InvocationOnMock invocation) throws Throwable {
                Hakukohde hk = (Hakukohde) invocation.getArguments()[0];
                return hk;
            }
        });

        Whitebox.setInternalState(hakukohdeResource, "hakukohdeDAO", hakukohdeDAO);
        Whitebox.setInternalState(hakukohdeResource, "indexerResource", indexerResource);
        Whitebox.setInternalState(hakukohdeResource, "publicationDataService", publicationDataService);

        Mockito.stub(oidService.get(TarjontaOidType.HAKUKOHDE)).toReturn("hk-" + Math.random());

        Whitebox.setInternalState(hakukohdeResource, "oidService", oidService);
    }

    @After
    public void tearDown() {
        LOG.info("tearDown()");
    }

    
    @Test
    public void testOVT7385(){
        HakukohdeV1RDTO hk = getHakukohde();
        hk.getHakukohdeKoulutusOids().clear();
        hk.getHakukohdeKoulutusOids().add("komoto-peruttu");
        ResultV1RDTO<HakukohdeV1RDTO>res = hakukohdeResource.createHakukohde(hk);
        Assert.assertEquals(ResultStatus.ERROR, res.getStatus());
        Assert.assertEquals("HAKUKOHDE_KOULUTUS_TILA_INVALID", res.getErrors().get(0).getErrorMessageKey());
    }
    
    @Test
    public void testCreate(){
        HakukohdeV1RDTO hk;
        
        //ei koulutuksia
        hk = getHakukohde();
        hk.setHakukohdeKoulutusOids(null);
        ResultV1RDTO<HakukohdeV1RDTO>res = hakukohdeResource.createHakukohde(hk);
        Assert.assertEquals(ResultStatus.ERROR, res.getStatus());
        Assert.assertEquals("HAKUKOHDE_KOULUTUS_MISSING", res.getErrors().get(0).getErrorMessageKey());

        //koulutukset joissa vuosi ei mätsää
        hk = getHakukohde();
        hk.getHakukohdeKoulutusOids().add("komoto-2");
        res = hakukohdeResource.createHakukohde(hk);
        Assert.assertEquals(ResultStatus.ERROR, res.getStatus());
        Assert.assertEquals("HAKUKOHDE_KOULUTUS_VUOSI_KAUSI_INVALID", res.getErrors().get(0).getErrorMessageKey());

        //koulutukset joissa kausi ei mätsää
        hk = getHakukohde();
        hk.getHakukohdeKoulutusOids().add("komoto-3");
        res = hakukohdeResource.createHakukohde(hk);
        Assert.assertEquals(ResultStatus.ERROR, res.getStatus());
        Assert.assertEquals("HAKUKOHDE_KOULUTUS_VUOSI_KAUSI_INVALID", res.getErrors().get(0).getErrorMessageKey());

        //ei hakua
        hk = getHakukohde();
        hk.setHakuOid(null);
        res = hakukohdeResource.createHakukohde(hk);
        Assert.assertEquals(ResultStatus.ERROR, res.getStatus());
        Assert.assertEquals("HAKUKOHDE_HAKU_MISSING", res.getErrors().get(0).getErrorMessageKey());

        //ei tarjoajaa
        hk = getHakukohde();
        hk.setTarjoajaOids(null);
        res = hakukohdeResource.createHakukohde(hk);
        Assert.assertEquals(ResultStatus.ERROR, res.getStatus());
        Assert.assertEquals("HAKUKOHDE_TARJOAJA_MISSING", res.getErrors().get(0).getErrorMessageKey());

        //ei nimeä
        hk = getHakukohde();
        hk.setHakukohteenNimet(null);
        res = hakukohdeResource.createHakukohde(hk);
        Assert.assertEquals(ResultStatus.ERROR, res.getStatus());
        Assert.assertEquals("HAKUKOHDE_NIMI_MISSING", res.getErrors().get(0).getErrorMessageKey());

        
        //ei tilaa
        hk = getHakukohde();
        hk.setTila(null);
        res = hakukohdeResource.createHakukohde(hk);
        Assert.assertEquals(ResultStatus.ERROR, res.getStatus());
        Assert.assertEquals("HAKUKOHDE_TILA_MISSING", res.getErrors().get(0).getErrorMessageKey());

        //oid määritelty
        hk = getHakukohde();
        hk.setOid("oidi");
        res = hakukohdeResource.createHakukohde(hk);
        Assert.assertEquals(ResultStatus.ERROR, res.getStatus());
        Assert.assertEquals("HAKUKOHDE_OID_SPECIFIED", res.getErrors().get(0).getErrorMessageKey());

        // kaikki ok?
        hk = getHakukohde();
        res = hakukohdeResource.createHakukohde(hk);
        Assert.assertEquals(ResultStatus.OK, res.getStatus());
        
        //luotiin oidi?
        Assert.assertNotNull(res.getResult().getOid());

    }
    
    
    // palauta hakukohde joka menee läpi rajapinnasta ilman virheitä
    private HakukohdeV1RDTO getHakukohde(){
        HakukohdeV1RDTO hk = new HakukohdeV1RDTO();
        hk.setHakukohdeKoulutusOids(Lists.newArrayList("komoto-1"));
        hk.setHakuOid("haku-1");
        hk.setTarjoajaOids(Sets.newHashSet("org-1"));
        hk.setHakukohteenNimet(ImmutableMap.<String, String>builder().put("fi", "nimi").build());
        hk.setTila(TarjontaTila.LUONNOS.toString());
        return hk;
    }
    
}
