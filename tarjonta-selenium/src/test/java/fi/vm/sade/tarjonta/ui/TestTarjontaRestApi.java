package fi.vm.sade.tarjonta.ui;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import fi.vm.sade.tarjonta.ui.Kattavuus;
import fi.vm.sade.tarjonta.ui.SVTUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

public class TestTarjontaRestApi {

	static private String path = "./src/test/resources/restApiResult";
	static private String http = "";
	static private Boolean qa = false;
    private static Kattavuus RestApiRajaPinnat = new Kattavuus();

	@Before
	public void setUp() throws Exception {
		RestApiRajaPinnat.alustaKattavuusKohde("RestApiRajaPinnat");
		SVTUtils doit = new SVTUtils();
		http = SVTUtils.prop.getProperty("testaus-selenium.restapi");
		if (SVTUtils.prop.getProperty("testaus-selenium.qa").equals("true")) { qa = true; }
	}
	
	@Test
	public void test_T_INT_TAR_REST001_KoMo_Count_2() throws IOException {
		TestTUtils run = new TestTUtils();   
		if (qa)
		{
			run.restTestCount(path + "001.qa.txt", http + "/komo?count=2");
		}
		else
		{
			run.restTestCount(path + "001.txt", http + "/komo?count=2");
		}
	}
	
	@Test
	public void test_T_INT_TAR_REST002_KoMo_Oid() throws IOException {
		if (qa)
		{
			restTest(path + "002.qa.txt", http + "/komo/1.2.246.562.5.2013061010185764933625");
		}
		else
		{
			restTest(path + "002.txt", http + "/komo/1.2.246.562.5.2013061010184768943288");
		}
	}
	
	@Test
	public void test_T_INT_TAR_REST003_KoMo_Oid_KoMoTo() throws IOException {
		if (qa)
		{
			restTest(path + "003.qa.txt", http + "/komo/1.2.246.562.5.2013061010184768943288/komoto");
		}
		else
		{
			restTest(path + "003.txt", http + "/komo/1.2.246.562.5.2013061010184768943288/komoto");
		}
	}
	
	@Test
	public void test_T_INT_TAR_REST004_KoMoTo_Count_2() throws IOException {
		TestTUtils run = new TestTUtils();   
		if (qa)
		{
			run.restTestCount(path + "004.qa.txt", http + "/komoto?count=2");
		}
		else
		{
			run.restTestCount(path + "004.txt", http + "/komoto?count=2");
		}
	}
	
	@Test
	public void test_T_INT_TAR_REST005_KoMoTo_Oid() throws IOException {
		if (qa)
		{
			restTest(path + "005.qa.txt", http + "/komoto/1.2.246.562.5.93352903079");
		}
		else
		{
			restTest(path + "005.txt", http + "/komoto/1.2.246.562.5.10067_02_900_1616_1508");
		}
	}
	
	@Test
	public void test_T_INT_TAR_REST006_KoMoTo_Oid_KoMo() throws IOException {
		if (qa)
		{
			restTest(path + "006.qa.txt", http + "/komoto/1.2.246.562.5.93352903079/komo");
		}
		else
		{
			restTest(path + "006.txt", http + "/komoto/1.2.246.562.5.10067_02_900_1616_1508/komo");
		}
	}
	
	@Test
	public void test_T_INT_TAR_REST007_Haku_Count_2() throws IOException {
		TestTUtils run = new TestTUtils();   
		if (qa)
		{
			run.restTestCount(path + "007.qa.txt", http + "/haku?count=2");
		}
		else
		{
			run.restTestCount(path + "007.txt", http + "/haku?count=2");
		}
	}
	
	@Test
	public void test_T_INT_TAR_REST008_Haku_Oid() throws IOException {
		if (qa)
		{
			restTest(path + "008.qa.txt", http + "/haku/1.2.246.562.5.2013080813081926341927");
		}
		else
		{
			restTest(path + "008.txt", http + "/haku/1.2.246.562.5.2013080813081926341927");
		}
	}
	
	@Test
	public void test_T_INT_TAR_REST009_Haku_Oid_Hakukohde() throws IOException {
		if (qa)
		{
			restTest(path + "009.qa.txt", http + "/haku/1.2.246.562.5.2013080813081926341927/hakukohde");
		}
		else
		{
			restTest(path + "009.txt", http + "/haku/1.2.246.562.5.2013080813081926341927/hakukohde");
		}
	}
	
	@Test
	public void test_T_INT_TAR_REST010_Hakukohde_Count_2() throws IOException {
		TestTUtils run = new TestTUtils();   
		if (qa)
		{
			run.restTestCount(path + "010.qa.txt", http + "/hakukohde?count=2");
		}
		else
		{
			run.restTestCount(path + "010.txt", http + "/hakukohde?count=2");
		}
	}
	
	@Test
	public void test_T_INT_TAR_REST011_Hakukohde_Oid() throws IOException {
		if (qa)
		{
			restTest(path + "011.qa.txt", http + "/hakukohde/1.2.246.562.5.60684104718");
		}
		else
		{
			restTest(path + "011.txt", http + "/hakukohde/1.2.246.562.5.60684104718");
		}
	}
	
	@Test
	public void test_T_INT_TAR_REST012_Hakukohde_Oid_Haku() throws IOException {
		if (qa)
		{
			restTest(path + "012.qa.txt", http + "/hakukohde/1.2.246.562.5.60684104718/haku");
		}
		else
		{
			restTest(path + "012.txt", http + "/hakukohde/1.2.246.562.5.60684104718/haku");
		}
	}
	
	@Test
	public void testReport() throws IOException {
		if (ok_count == 12) 
		{
			RestApiRajaPinnat.setKattavuus("TarjontaRestApi", RestApiRajaPinnat.KATTAVUUSOK);
			RestApiRajaPinnat.KattavuusRaportti();
		}
	}
	
	static private int ok_count = 0;
	public void restTest(String fileName, String url) throws IOException {
		SVTUtils doit = new SVTUtils();   
		TestTUtils run = new TestTUtils();   
		doit.echo(fileName);
		String response = "";
		String expected = doit.readFile(fileName);
		response = run.getJson(url);
		expected = expected.replace("\n", "").replace("\r", "").replace("\t", " ").replace("  ", " "); 
		response = response.replace("\n", "").replace("\r", "").replace("\t", " ").replace("  ", " ");
		Assert.assertEquals("RestApi error", expected, response);
		ok_count++;
	}
}
