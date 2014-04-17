package fi.vm.sade.tarjonta.service.impl.resources.v1;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.common.collect.Lists;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.impl.TestData;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.HakukohdeV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeLiiteV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO.ResultStatus;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ValintakoeV1RDTO;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("embedded-solr")
@Ignore // SPRINGIN BUGI SPR-8857 ESTÄÄ TÄMÄN TESTIN AJAMISEN
public class HakukohdeResourceImplV1Test extends Assert {
	
	private static final String TARJOAJA_NIMI = "Organisaatio ABC";

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	protected PlatformTransactionManager transactionManager;
	
	@Autowired
	protected EntityManagerFactory entityManagerFactory;
	
	@Autowired
	protected HakukohdeV1Resource hakukohdeResource;

	@Autowired
	protected TarjontaFixtures tarjontaFixtures;
	
	@Autowired
	protected PermissionChecker permissionChecker;
	
	@Autowired
	protected OidService oidService;

	@Autowired
	protected KoodiService koodiService;

	@Autowired
	protected TarjontaKoodistoHelper tarjontaKoodistoHelper;
	
	protected final TestData testData = new TestData();

    private KoodiMetadataType getKoodiMeta(String arvo, KieliType kieli) {
        KoodiMetadataType type = new KoodiMetadataType();
        type.setKieli(kieli);
        type.setNimi(arvo + "-nimi-" + kieli.toString());
        return type;
    }

    private KoodiType getKoodiType(String uri, String arvo) {
        KoodiType kt = new KoodiType();
        kt.setKoodiArvo(arvo);
        kt.setKoodiUri(uri);
        kt.getMetadata().add(getKoodiMeta(arvo, KieliType.FI));
        kt.getMetadata().add(getKoodiMeta(arvo, KieliType.SV));
        kt.getMetadata().add(getKoodiMeta(arvo, KieliType.EN));
        return kt;
    }

    private void stubKoodi(String uri, String arvo) {
        List<KoodiType> vastaus = Lists.newArrayList(getKoodiType(uri, arvo));
        Mockito.stub(
                koodiService.searchKoodis(Matchers
                        .argThat(new KoodistoCriteriaMatcher(uri)))).toReturn(
                        vastaus);
    }

	@Before
	public void doBefore() throws Exception {
        Mockito.stub(oidService.get(TarjontaOidType.HAKUKOHDE)).toReturn("1.2.3.4.5");
        stubKoodi("kieli_fi", "suomi");
        stubKoodi("posti_12345", "posti_12345");
	}
	
	@After
	public void doAfter() {
	}
	
	protected HakukohdeV1RDTO createTestHakukukohde1() {
		OsoiteRDTO osoite = new OsoiteRDTO();
		osoite.setOsoiterivi1("Juna Korsoon");
		osoite.setPostinumero("posti_12345");
		osoite.setPostitoimipaikka("KORSO");
		osoite.setPostinumeroArvo("12345");
		
		HakukohdeV1RDTO ret = new HakukohdeV1RDTO();
		ret.setTila(TarjontaTila.LUONNOS.toString());
		ret.setHakukohteenNimet(Collections.singletonMap("kieli_fi", "Hakukohde #1"));
		ret.setTarjoajaOids(Collections.singleton(testData.getPersistedKomoto1().getTarjoaja()));
		ret.setTarjoajaNimet(Collections.singletonMap(testData.getPersistedKomoto1().getTarjoaja(), TARJOAJA_NIMI));
		ret.setHakuOid(testData.getHaku1().getOid());
		ret.setHakukohdeKoulutusOids(Collections.singletonList(testData.getPersistedKomoto1().getOid()));
		
		HakukohdeLiiteV1RDTO liite = new HakukohdeLiiteV1RDTO();		
		liite.setKieliUri("kieli_fi");
		liite.setLiitteenKuvaukset(Collections.singletonMap("kieli_fi", "Looremin ipsumi."));
		liite.setLiitteenNimi("Liite123");
		liite.setSahkoinenToimitusOsoite("www-osoite");
		liite.setToimitettavaMennessa(new Date());
		liite.setLiitteenToimitusOsoite(osoite);
		ret.getHakukohteenLiitteet().add(liite);
		
		ValintakoeV1RDTO vkoe = new ValintakoeV1RDTO();
		vkoe.setKieliUri("kieli_fi");
		vkoe.setValintakoeNimi("Pudotuspeli");
		
		ValintakoeAjankohtaRDTO vka = new ValintakoeAjankohtaRDTO();
		vka.setLisatiedot("Lisa Tieto");
		vka.setOsoite(osoite);
		vka.setAlkaa(new Date());
		vka.setLoppuu(new Date());
		vkoe.getValintakoeAjankohtas().add(vka);
		ret.getValintakokeet().add(vkoe);
		
		return ret;
	}
	
	private <T> void logAndAssertResult(ResultV1RDTO<T> dto) {
		assertNotNull(dto);
		log.info("Result = {}", dto.getResult());
		log.info("Status = {}", dto.getStatus());
		if (dto.getErrors()!=null) {
			for (ErrorV1RDTO err : dto.getErrors()) {
				log.info("Error at {}: code={} target={} info={}: {} {}", err.getErrorField(), err.getErrorCode(), err.getErrorTarget(), err.getErrorTechnicalInformation(), err.getErrorMessageKey(), err.getErrorMessageParameters());
			}
			assertEquals(0, dto.getErrors().size());
		}
		assertEquals(ResultStatus.OK, dto.getStatus());
	}

	private <T> T doInTransaction(TransactionCallback<T> cb) {
		return new TransactionTemplate(transactionManager).execute(cb);
	}
	
	private void initTestData() {
		doInTransaction(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				testData.initializeData(EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory), tarjontaFixtures);
			}
		});
	}
	
	@Test
	public void testContext() {
		assertNotNull(hakukohdeResource);
	}

	protected void assertEquals(ValintakoeV1RDTO expected, ValintakoeV1RDTO actual) {
		
		assertEquals(expected.getKieliUri(), actual.getKieliUri());
		assertEquals(expected.getValintakoeNimi(), actual.getValintakoeNimi());
		assertEquals(expected.getValintakokeenKuvaus(), actual.getValintakokeenKuvaus());
		assertEquals(expected.getValintakoeAjankohtas().size(), actual.getValintakoeAjankohtas().size());
		
	}

	protected void assertEquals(HakukohdeLiiteV1RDTO expected, HakukohdeLiiteV1RDTO actual) {
		
		assertEquals(expected.getKieliUri(), actual.getKieliUri());
		assertEquals(expected.getLiitteenNimi(), actual.getLiitteenNimi());
		assertEquals(expected.getLiitteenKuvaukset(), actual.getLiitteenKuvaukset());
		
	}

	protected void assertEqualsValintakokeet(List<ValintakoeV1RDTO> expected, List<ValintakoeV1RDTO> actual) {
		Map<String, ValintakoeV1RDTO> exp = new TreeMap<String, ValintakoeV1RDTO>();
		for (ValintakoeV1RDTO vk : expected) {
			exp.put(vk.getValintakoeNimi(), vk);
		}
		
		Map<String, ValintakoeV1RDTO> act = new TreeMap<String, ValintakoeV1RDTO>();
		for (ValintakoeV1RDTO vk : actual) {
			act.put(vk.getValintakoeNimi(), vk);
		}
		
		assertEquals(exp.keySet(), act.keySet());
		
		for (Map.Entry<String, ValintakoeV1RDTO> vke : exp.entrySet()) {
			assertEquals(vke.getValue(), act.get(vke.getKey()));
		}
	}
	
	protected void assertEqualsLiitteet(List<HakukohdeLiiteV1RDTO> expected, List<HakukohdeLiiteV1RDTO> actual) {
		Map<String, HakukohdeLiiteV1RDTO> exp = new TreeMap<String, HakukohdeLiiteV1RDTO>();
		for (HakukohdeLiiteV1RDTO vk : expected) {
			exp.put(vk.getLiitteenNimi(), vk);
		}
		
		Map<String, HakukohdeLiiteV1RDTO> act = new TreeMap<String, HakukohdeLiiteV1RDTO>();
		for (HakukohdeLiiteV1RDTO vk : actual) {
			act.put(vk.getLiitteenNimi(), vk);
		}
		
		assertEquals(exp.keySet(), act.keySet());
		
		for (Map.Entry<String, HakukohdeLiiteV1RDTO> vke : exp.entrySet()) {
			assertEquals(vke.getValue(), act.get(vke.getKey()));
		}
	}
	
	protected void assertEquals(HakukohdeV1RDTO expected, HakukohdeV1RDTO actual) {
		log.info("Compare {} -- {}", expected, actual);

		assertEquals(expected.getAlinHyvaksyttavaKeskiarvo(), actual.getAlinHyvaksyttavaKeskiarvo(), 0);
		assertEquals(expected.getAlinValintaPistemaara(), actual.getAlinValintaPistemaara());
		assertEquals(expected.getAloituspaikatLkm(), actual.getAloituspaikatLkm());
		assertEquals(expected.getEdellisenVuodenHakijatLkm(), actual.getEdellisenVuodenHakijatLkm());
		assertEquals(expected.getHakuaikaAlkuPvm(), actual.getHakuaikaAlkuPvm());
		assertEquals(expected.getHakuaikaLoppuPvm(), actual.getHakuaikaLoppuPvm());
		assertEquals(expected.getHakuaikaId(), actual.getHakuaikaId());
		assertEquals(expected.getHakukelpoisuusVaatimusKuvaukset(), actual.getHakukelpoisuusVaatimusKuvaukset());
		assertEquals(expected.getHakukelpoisuusvaatimusUris(), actual.getHakukelpoisuusvaatimusUris());
		assertEquals(expected.getHakukohdeKoulutusOids(), actual.getHakukohdeKoulutusOids());
		assertEquals(expected.getHakukohteenNimet(), actual.getHakukohteenNimet());
		assertEquals(expected.getHakukohteenNimi(), actual.getHakukohteenNimi());
		assertEquals(expected.getHakukohteenNimiUri(), actual.getHakukohteenNimiUri());
		assertEquals(expected.getHakuOid(), actual.getHakuOid());
		assertEquals(expected.getKaksoisTutkinto(), actual.getKaksoisTutkinto());
		assertEquals(expected.getLiitteidenToimitusOsoite(), actual.getLiitteidenToimitusOsoite());
		assertEquals(expected.getLiitteidenToimitusPvm(), actual.getLiitteidenToimitusPvm());
		assertEquals(expected.getLisatiedot(), actual.getLisatiedot());
		//assertEquals(expected.getOid(), actual.getOid());
		//assertEquals(expected.getOpetusKielet(), actual.getOpetusKielet());
		assertEquals(expected.getSahkoinenToimitusOsoite(), actual.getSahkoinenToimitusOsoite());
		assertEquals(expected.getSoraKuvaukset(), actual.getSoraKuvaukset());
		assertEquals(expected.getSoraKuvausKielet(), actual.getSoraKuvausKielet());
		assertEquals(expected.getSoraKuvausKoodiUri(), actual.getSoraKuvausKoodiUri());
		assertEquals(expected.getSoraKuvausTunniste(), actual.getSoraKuvausTunniste());
		//assertEquals(expected.getTarjoajaNimet(), actual.getTarjoajaNimet());
		assertEquals(expected.getTarjoajaOids(), actual.getTarjoajaOids());
		assertEquals(expected.getTila(), actual.getTila());
		assertEquals(expected.getUlkoinenTunniste(), actual.getUlkoinenTunniste());
		assertEquals(expected.getValintaperusteKuvaukset(), actual.getValintaperusteKuvaukset());
		assertEquals(expected.getValintaPerusteKuvausKielet(), actual.getValintaPerusteKuvausKielet());
		assertEquals(expected.getValintaperustekuvausKoodiUri(), actual.getValintaperustekuvausKoodiUri());
		assertEquals(expected.getValintaPerusteKuvausTunniste(), actual.getValintaPerusteKuvausTunniste());
		assertEquals(expected.getValintojenAloituspaikatLkm(), actual.getValintojenAloituspaikatLkm());
		assertEquals(expected.getYlinValintapistemaara(), actual.getYlinValintapistemaara());
		
		assertEqualsValintakokeet(expected.getValintakokeet(), actual.getValintakokeet());
		assertEqualsLiitteet(expected.getHakukohteenLiitteet(), actual.getHakukohteenLiitteet());
	}
	
	@Test
	@DirtiesContext
	public void testCreate() {
		
		initTestData();
		
		log.info("\n **** TEST STORE **** \n ");
		
		final HakukohdeV1RDTO hk1 = createTestHakukukohde1();
		ResultV1RDTO<HakukohdeV1RDTO> hk1r = hakukohdeResource.createHakukohde(hk1);

		log.info("\n **** AFTR STORE **** \n ");

		logAndAssertResult(hk1r);
		
		assertEquals(hk1, hk1r.getResult());
	
		String hkOid = hk1r.getResult().getOid();
		assertNotNull(hkOid);
		
		log.debug("OID = "+hkOid);
		
		ResultV1RDTO<HakukohdeV1RDTO> hk1g = hakukohdeResource.findByOid(hkOid);
		logAndAssertResult(hk1g);
		assertEquals(hk1r.getResult(), hk1g.getResult());
		
	}
	
}
