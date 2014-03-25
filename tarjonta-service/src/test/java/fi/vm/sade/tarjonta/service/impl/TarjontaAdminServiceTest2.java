package fi.vm.sade.tarjonta.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.impl.TestData;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.types.AjankohtaTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteenValintakokeetHakukohteenTunnisteellaKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.service.types.OsoiteTyyppi;
import fi.vm.sade.tarjonta.service.types.PisterajaTyyppi;
import fi.vm.sade.tarjonta.service.types.ValinnanPisterajaTyyppi;
import fi.vm.sade.tarjonta.service.types.ValintakoeTyyppi;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("embedded-solr")
@DirtiesContext
@Ignore // SPRINGIN BUGI SPR-8857 ESTÄÄ TÄMÄN TESTIN AJAMISEN
public class TarjontaAdminServiceTest2 extends Assert {

	@Autowired
	protected PlatformTransactionManager transactionManager;
	
	@Autowired
	protected EntityManagerFactory entityManagerFactory;

	@Autowired
	protected TarjontaFixtures tarjontaFixtures;
	
	@Autowired
	protected TarjontaAdminService tarjontaAdminService;

	@Autowired
	protected TarjontaPublicService tarjontaPublicService;

	@Autowired
	protected PermissionChecker permissionChecker;
	
	protected final TestData testData = new TestData();

	private <T> T doInTransaction(TransactionCallback<T> cb) {
		return new TransactionTemplate(transactionManager).execute(cb);
	}
	
	@Before
	public void initTestData() {
		permissionChecker.setOverridePermissionChecks(true);
		doInTransaction(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				testData.initializeData(EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory), tarjontaFixtures);
			}
		});
	}
	
	private MonikielinenTekstiTyyppi newTeksti(String s) {
		return new MonikielinenTekstiTyyppi(Collections.singletonList(new Teksti(s, "kieli_fi")));
	}
	
	@Test
	public void testUpdateValintakokeet() {
		
		HaeHakukohteenValintakokeetHakukohteenTunnisteellaKyselyTyyppi wtf = new HaeHakukohteenValintakokeetHakukohteenTunnisteellaKyselyTyyppi();
		wtf.setHakukohteenTunniste(TestData.HAKUKOHDE_OID1);
		
		List<ValintakoeTyyppi> vks = tarjontaPublicService.haeHakukohteenValintakokeetHakukohteenTunnisteella(wtf).getHakukohteenValintaKokeet();
		
		ValintakoeTyyppi nvk = new ValintakoeTyyppi();
		nvk.setValintakokeenTyyppi("uusikoe");
		nvk.setKuvaukset(newTeksti("kuvaukset"));
		
		nvk.getAjankohdat().add(
				new AjankohtaTyyppi("ak1", new Date(), new Date(), new OsoiteTyyppi("sadsadsad", "", "posti_00001", "mesta")));
		
		PisterajaTyyppi pr = new PisterajaTyyppi();
		pr.setValinnanPisteraja(ValinnanPisterajaTyyppi.PAASYKOE);
		pr.setAlinHyvaksyttyPistemaara(5);
		pr.setAlinPistemaara(1);
		pr.setYlinPistemaara(10);
		nvk.getPisterajat().add(pr);
		
		List<ValintakoeTyyppi> nvks = new ArrayList<ValintakoeTyyppi>(vks);
		nvks.add(nvk);

		List<ValintakoeTyyppi> nnvks = tarjontaAdminService.tallennaValintakokeitaHakukohteelle(TestData.HAKUKOHDE_OID1, nvks);
		
		assertEquals(nvks.size(), nnvks.size());
		
	}
}
