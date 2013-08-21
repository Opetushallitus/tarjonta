package fi.vm.sade.tarjonta.ui;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

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

	@Before
	public void setUp() throws Exception {
		SVTUtils doit = new SVTUtils();
		http = SVTUtils.prop.getProperty("tarjonta-selenium.restapi");
	}
	
	@Test
	public void test_T_INT_TAR_REST001() throws IOException {
		restTest(path + "001.txt", http + "/komo?count=2");
	}
	
	@Test
	public void test_T_INT_TAR_REST002() throws IOException {
		restTest(path + "002.txt", http + "/komo/1.2.246.562.5.2013060313060131672122");
	}
	
	@Test
	public void test_T_INT_TAR_REST003() throws IOException {
		restTest(path + "003.txt", http + "/komo/1.2.246.562.5.2013060313060131672122/komoto");
	}
	
	@Test
	public void test_T_INT_TAR_REST004() throws IOException {
		restTest(path + "004.txt", http + "/komoto?count=2");
	}
	
	@Test
	public void test_T_INT_TAR_REST005() throws IOException {
		restTest(path + "005.txt", http + "/komoto/1.2.246.562.5.2013062413194602131997");
	}
	
	@Test
	public void test_T_INT_TAR_REST006() throws IOException {
		restTest(path + "006.txt", http + "/komoto/1.2.246.562.5.2013062413194602131997/komo");
	}
	
	@Test
	public void test_T_INT_TAR_REST007() throws IOException {
		restTest(path + "007.txt", http + "/haku?count=2");
	}
	
	@Test
	public void test_T_INT_TAR_REST008() throws IOException {
		restTest(path + "008.txt", http + "/haku/1.2.246.562.5.2013070414120590299742");
	}
	
	@Test
	public void test_T_INT_TAR_REST009() throws IOException {
		restTest(path + "009.txt", http + "/haku/1.2.246.562.5.2013070414120590299742/hakukohde");
	}
	
	@Test
	public void test_T_INT_TAR_REST010() throws IOException {
		restTest(path + "010.txt", http + "/hakukohde/1.2.246.562.14.2013061012593675316031");
	}
	
	@Test
	public void test_T_INT_TAR_REST011() throws IOException {
		restTest(path + "011.txt", http + "/hakukohde/1.2.246.562.14.2013061012593675316031/haku");
	}
	
	public String getJson(String urlString) {

		String result = "\n\n";
		SVTUtils doit = new SVTUtils();        
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String line = "";
			doit.echo("=================================================");
			while ((line = br.readLine()) != null) {
				result = result + line + "\n";
			}
			doit.echo(result);
			doit.echo("=================================================");
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void restTest(String fileName, String url) throws IOException {
		SVTUtils doit = new SVTUtils();   
		doit.echo(fileName);
		String response = "";
		String expected = doit.readFile(fileName);
		response = getJson(url);
		expected = expected.replace("\n", "").replace("\r", "").replace("\t", " ").replace("  ", " "); 
		response = response.replace("\n", "").replace("\r", "").replace("\t", " ").replace("  ", " "); 
		Assert.assertEquals("RestApi error", expected, response);
	}
}
