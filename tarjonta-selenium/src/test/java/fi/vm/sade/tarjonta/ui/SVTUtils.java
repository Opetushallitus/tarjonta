package fi.vm.sade.tarjonta.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class SVTUtils {
	static {
		try {
			loadProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static Properties prop;
	public static void loadProperties() throws Exception 
	{
		String versionFile = "src/test/resources/tarjonta-selenium.properties";
		File vFile = new File(versionFile);
		if (vFile.exists())
		{
			Properties vProp = new Properties();
			try {
				vProp.load(new FileInputStream(versionFile));
		        System.out.println("Running tarjonta-selenium.git-version=" + vProp.getProperty("tarjonta-selenium.git-version"));
			} catch (IOException e) {
				e.printStackTrace();
				throw new IOException("ERROR: Can't read version properties file. " + versionFile);
			}
		}
		else
		{
			System.out.println("ERROR: Can't find version properties file. " + versionFile);				
		}

		String propertiesFile = System.getProperty("user.home") + "/oph-configuration/tarjonta-selenium.properties";

		// localhost31config, localhost40config, luokka_config, reppu_config
		// See configs.xml
		String target = System.getenv("TESTTARGET"); 
		if (target != null)
		{
			System.out.println("Running Environment variable TESTTARGET=" + target);
			propertiesFile = propertiesFile + "." + target;
			System.out.println("Running propertiesFile: " + propertiesFile);
		}
		else
		{
			System.out.println("Running WARNING: Environment variable TESTTARGET is missing.");
		}

		prop = new Properties();
		try {
			prop.load(new FileInputStream(propertiesFile));
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("ERROR: Can't read properties file. " + propertiesFile);
		}
        System.out.println("Running tarjonta-selenium.oph-url=" + prop.getProperty("tarjonta-selenium.oph-url"));
        System.out.println("Running tarjonta-selenium.oph-login-url=" + prop.getProperty("tarjonta-selenium.oph-login-url"));
        System.out.println("Running tarjonta-selenium.tarjonta-url=" + prop.getProperty("tarjonta-selenium.tarjonta-url"));
        System.out.println("Running tarjonta-selenium.tomcat-logfile=" + prop.getProperty("tarjonta-selenium.tomcat-logfile"));
        System.out.println("Running tarjonta-selenium.qa=" + prop.getProperty("tarjonta-selenium.qa"));
        System.out.println("Running tarjonta-selenium.reppu=" + prop.getProperty("tarjonta-selenium.reppu"));
        System.out.println("Running tarjonta-selenium.luokka=" + prop.getProperty("tarjonta-selenium.luokka"));
        System.out.println("Running tarjonta-selenium.username=" + prop.getProperty("tarjonta-selenium.username"));
	}

	public void reppuLogin(WebDriver driver)
	{
		driver.manage().window().maximize();
        tauko(1);
        WebElement username = driver.findElement(By.id("username")); 
        username.clear();
        username.sendKeys(prop.getProperty("tarjonta-selenium.username")); // "admin@oph.fi"
        tauko(1);
        WebElement password = driver.findElement(By.id("password"));
        password.clear();
        password.sendKeys(prop.getProperty("tarjonta-selenium.password"));
        tauko(1);
        driver.findElement(By.name("submit")).click();
        tauko(2);
        return;
	}

	public long millisDiff(long t1)
	{
		long t2 = millis();
		if (t2 > 2000 + t1) 
		{
			double dur = (t2 - t1) / 1000.0;
			System.out.println("Running step duration: " + dur);
		}
		double dur2 = (t2 - t1) / 1000.0;
		System.out.println("Running step duration: " + dur2);
		return t2;
	}

	public long millis()
	{
		Calendar c = Calendar.getInstance();
		long now = c.getTimeInMillis();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		long passed = now - c.getTimeInMillis();
		return passed;
	}

	public String getGwtId(String organisaatio, String pageText)
	{
		String gwtuid;
		gwtuid = pageText.substring(0, pageText.indexOf(organisaatio) + organisaatio.length());
		gwtuid = gwtuid.substring(gwtuid.lastIndexOf("gwt-uid-"));
		gwtuid = gwtuid.substring(0, gwtuid.indexOf("\""));
		return gwtuid;
	}	  

	public String getGwtIdForFirstHakukohde(WebDriver driver)
	{
		String pageText = driver.getPageSource();
		String gwtuid;
		gwtuid = pageText.substring(0, pageText.indexOf("class=\"v-icon\""));
		gwtuid = gwtuid.substring(gwtuid.lastIndexOf("gwt-uid-"));
		gwtuid = gwtuid.substring(0, gwtuid.indexOf("\""));
		return gwtuid;
	}	  

	public WebElement getTriangleForLastHakukohde(WebDriver driver)
	{
        return driver.findElements(By.className("v-treetable-treespacer"))
        		.get(driver.findElements(By.className("v-treetable-treespacer")).size() - 1);
	}	  

	public void tauko(int sec)
	{
		try {
			Thread.sleep(sec * 1000);
		} catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	public void focus(WebDriver driver, WebElement element)
	{
	    if (false) {
		if ("input".equals(element.getTagName()))
		{
			element.sendKeys("");
		} 
		else
		{
			new Actions(driver).moveToElement(element).perform();
		}
	    }
	}

	public void focus(WebDriver driver, WebElement element, String comment)
	{
		System.out.println("focus: " + comment);
		if ("input".equals(element.getTagName()))
		{
			element.sendKeys("");
		} 
		else
		{
			new Actions(driver).moveToElement(element).perform();
		}
		tauko(30);
	}

	public void footerTest(WebDriver driver, String err, Boolean reppu) throws Exception
	{
		// KOODISTO !-merkkitarkistus samalla
		this.notPresentText(driver, "v-errorindicator", "Running ERROR: Koodisto puuttuu");
		
		if (true) { return; }
//		Assert.assertNotNull(err, driver.findElement(By.id("footer")));
		Assert.assertNotNull(err, driver.findElement(By.id("raamit-footer")));
		if (reppu)
		{
			try {
				// REPPU
				Assert.assertNotNull(err, driver.findElement(By.xpath(
						"//img[@src='/virkailija-theme/images/general/OKM_logo.png']")).isDisplayed());
			} catch (Exception e) {
				Assert.assertNotNull(err, driver.findElement(By.xpath(
						"//img[@src='../decorator/images/logo-opetus-ja-kulttuuriministerio.png']")).isDisplayed());
			}
		}
		else
		{
			try {
//				Assert.assertNotNull(err, driver.findElement(By.xpath(
//						"//img[@src='../decorator/images/logo-opetus-ja-kulttuuriministerio.png']")).isDisplayed());
				Assert.assertNotNull(err, driver.findElement(By.xpath(
						"//img[@src='/virkailija-raamit/virkailija-raamit/img/OKM_logo.png']")).isDisplayed()); // luokka
			} catch (Exception e) {
				// REPPU
				Assert.assertNotNull(err, driver.findElement(By.xpath(
						"//img[@src='/virkailija-theme/images/general/OKM_logo.png']")).isDisplayed());
			}
		}
	}

	public int countCausedByFromLog(String logFile) throws IOException
	{
		int count = 0;                
		try {
			BufferedReader br;
			br = new BufferedReader(new FileReader(logFile));
			String line = "";                
			while ((line = br.readLine()) != null) 
			{                   
				line = "a" + line;
				if (line.indexOf("Caused by:") > 0) { count++; }
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new FileNotFoundException("ERROR: Tomcatin lokitiedostoa (" + logFile + ") ei loydy.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("ERROR: Tomcatin lokitiedostoa (" + logFile + ") ei voi lukea.");
		}                
		return count;
	}

	public long logSize(String logFile)
	{
		File file =new File(logFile);
		long bytes = 0;

		if (file.exists()) { bytes = file.length(); }
		return bytes;
	}

	public void notPresentText(WebDriver driver, String test, String err) throws Exception
	{
		String source = driver.getPageSource();
		if (source.indexOf(test) > 0)
		{
			System.out.println(err);
			throw new Exception(err);
		}
	}

	public void isPresentText(WebDriver driver, String test, String err)
	{
		String source = driver.getPageSource();
		if (source.indexOf(test) < 0)
		{
			System.out.println(err);
		}
	}

	public Boolean isPresentText(WebDriver driver, String test)
	{
		String source = driver.getPageSource();
		if (source.indexOf(test) < 0) { return false; }
		return true;
	}

	public String idLike(WebDriver driver, String id) throws Exception
	{
		String pageSource = driver.getPageSource();
		String id2 = pageSource.substring(0, pageSource.indexOf(id) + id.length());
		id2 = id2.substring(id2.lastIndexOf("id=\"") + 4);
		if (id2.indexOf(id) < 0)
		{
			tauko(1);
			id2 = idLike(driver, id);
			if (id2.indexOf(id) < 0)
			{
				System.out.println("idLike " + id + " Sleep 10...");
				tauko(10);
				id2 = idLike(driver, id);
				if (id2.indexOf(id) < 0)
				{
					throw new Exception("ERROR: Ei loydy (id=" + id + ") id:n tapaista.");
				}
			}
		}
		return id2;
	}

	public void textClick(WebDriver driver, String text)
	{
		driver.findElement(By.xpath("//*[contains(text(), '" + text  + "')]")).click();
	}

	public WebElement textElement(WebDriver driver, String text)
	{
		return driver.findElement(By.xpath("//*[contains(text(), '" + text  + "')]"));
	}

	public WebElement partIdElement(WebDriver driver, String text)
	{
		return driver.findElement(By.xpath("//*[contains(@id, '" + text + "')]"));
	}

	public void sendPageToFile(WebDriver driver)
	{
		String pageSource = driver.getPageSource();
		writeToFile("C:/tmp/page.txt", pageSource);
		System.out.println("page.txt");
	}

	public void writeToFile(String fileName, String text)
	{
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
			out.write(text);
			out.close();
		}
		catch (IOException e)
		{
			System.out.println("Exception ");
		}
	}

	public void listXpathElements(WebDriver driver, String xpathExpression)
	{
		Object[] eles = driver.findElements(By.xpath(xpathExpression)).toArray();
		System.out.println("listXpathElements: " + eles.length);
		int i = 1;
		for (Object ele : eles)
		{
			WebElement el = (WebElement)ele;
			System.out.println("listXpathElements i=" + i++ + "element=" + el.toString());
		}
	}

	public void palvelimenVersio(WebDriver driver, String baseUrl)
	{
		// palvelin vastaa
		long t01 = millis();
		try {
			driver.get(baseUrl); // "http://localhost:8080
			driver.manage().window().maximize();
		} catch (Exception e) {
			System.out.println("Running ERROR: Palvelin ei vastaa. baseUrl: " + baseUrl);
		}
		t01 = millisDiff(t01);
		this.tauko(1);

		// palvelimen versio
		String versioUrl = SVTUtils.prop.getProperty("tarjonta-selenium.tarjonta-versio-url");
		t01 = millis();
		System.out.println("Running versioUrl: " + baseUrl + versioUrl);
		driver.get(baseUrl + versioUrl); 
		t01 = millisDiff(t01);
		String versio = driver.getPageSource();
		versio = versio.split("pre>")[1];
		versio = versio.replace("</", "").replace("<", "");
		versio = "Running " + versio.replace("\n", "\nRunning ");
		System.out.println(versio);
	}

}
