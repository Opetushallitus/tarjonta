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
import java.util.HashMap;
import java.util.Map;
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

	public String getGwtIdBeforeText(WebDriver driver, String text)
	{
		String pageText = driver.getPageSource();
		String gwtuid;
		gwtuid = pageText.substring(0, pageText.indexOf(text));
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
		String pageEnd = pageSource.substring(pageSource.indexOf(id) + id.length());
		pageEnd = pageEnd.substring(0, pageEnd.indexOf("\""));
		id2 = id2 + pageEnd;
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

	public String getPageSourceFromFile() throws IOException
	{
		String fileName = System.getProperty("user.home") + "/page.txt";
		String page = "";
		page = readFile(fileName);
		return page;
	}

	private String readFile( String file ) throws IOException {
		BufferedReader reader = new BufferedReader( new FileReader (file));
		String         line = null;
		StringBuilder  stringBuilder = new StringBuilder();
		String         ls = System.getProperty("line.separator");

		while( ( line = reader.readLine() ) != null ) {
			stringBuilder.append( line );
			stringBuilder.append( ls );
		}

		return stringBuilder.toString();
	}

	public void sendPageToFile(WebDriver driver)
	{
		String pageSource = driver.getPageSource();
		writeToFile(System.getProperty("user.home") + "/page.txt", pageSource);
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

    //////////////// START //////////////////////////////////////
    // regex is giving unexplained false -> this long version
    private Boolean missingElement = false;
    private Boolean tryFind = false;
    private String cutPage = "";
    private String wholePage = "";
    private Map kMap = new HashMap() ;
    public Boolean checkElements(WebDriver driver, String elements, Boolean switched)
    {
            Boolean ok = true;
            String page = "";
            if (readPageFromFile)
            {
                    try {
                            page = getPageSourceFromFile();
                    } catch (IOException e) {
                            e.printStackTrace();
                    }
            }
            else
            {
                    page = driver.getPageSource();
            }
            cutPage = page;
            wholePage = page;
            String elementArray[] = elements.split("\\.\\*");
            for (int j = 0; j < elementArray.length; j++) {
                    String element = elementArray[j];
                    // Handle KPL syntax
                    if (element.indexOf("KPL") > 0)
                    {
                            int count = Integer.parseInt(element.split("KPL")[0]);
                            int found = page.split(element.split("KPL")[1]).length - 1;
                            if (found != count)
                            {
                                    System.out.println("Running ERROR missing elements: count=" + count
                                                    + " found=" + found + " element=" + element);
                                    missingElement = true;
                                    ok = false;
                            }
                            else
                            {
                                    continue;
                            }
                    }

                    // Handle elements one by one
                    tryFind = true;
                    if (! checkEasyHitElements(element, switched))
                    {
                            if (! checkDottedElements(element, switched))
                            {
                                    ok = false;
                            }
                    }
            }
            if (! ok) { this.sendPageToFile(driver); }
            return ok;
    }

    public Boolean checkDottedElements(String element, Boolean switched)
    {
            if (! tryFind) { return false; }

            Boolean hit = false;
            Boolean debug = false;
            Boolean getOut = false;
            if (cutPage.indexOf(element) < 0)
            {
                    String shortElement = element;
                    Boolean failing = true;
                    String lastChar = "";
                    String endString = "";
                    String dotPage = "";
                    Boolean GetBack = false;
                    int dotCount = 0;
                    while (failing && shortElement.length() > 0)
                    {
                            lastChar = shortElement.substring(shortElement.length() - 1);
                            endString = lastChar + endString;
                            if (lastChar.equals("."))
                            {
                                    GetBack = true;
                                    dotCount++;
                            }
                            else
                            {
                                    GetBack = false;
                                    dotCount = 0;
                            }
                            shortElement = shortElement.substring(0, shortElement.length() - 1);
                            if (debug) { System.out.println("DEBUG shortElement=" + shortElement); }
                            if (cutPage.indexOf(shortElement) > 0)
                            {
                                    if (debug) { System.out.println("DEBUG lastChar=" + lastChar
                                                    + " GetBack=" + GetBack + " dotCount=" + dotCount); }
                                    if (GetBack)
                                    {
                                            // Loop for 14 dotPage canditates
                                            String dotPageArray[] = cutPage.split(shortElement);
                                            for (int i = 1; i < dotPageArray.length; i++) {
                                                    dotPage = dotPageArray[i];
                                                    if (debug) { System.out.println("DEBUG i=" + i + " dotPage40=" + dotPage.substring(0, 40)); }
                                                    dotPage = dotPage.substring(0, dotCount);
                                                    String shortElement2 = shortElement + dotPage + endString.substring(dotCount);
                                                    if (debug) { System.out.println("DEBUG dotPage=" + dotPage + " endString=" + endString);
                                                    System.out.println("DEBUG shortElement2=" + shortElement2); }
                                                    if (cutPage.indexOf(shortElement2) > 0 && dotCount > 0) {
                                                            checkEasyHitElements(shortElement2, switched);
                                                            if (debug) { System.out.println("DEBUG out 1 index=" + cutPage.indexOf(shortElement2)); }
                                                            return true; }
//                                                  if (checkDottedElements(shortElement2, page) && dotCount > 0) {
//                                                          System.out.println("DEBUG out 2");
//                                                          return ok; }
                                            }
                                            GetBack = false;
                                            dotCount = 0;
                                            endString = "";
                                    }
                                    else
                                    {
                                            failing = false;
                                            System.out.println("Running ERROR finding element=" + shortElement);
                                    }
                            }
                    }
                    if (! getOut)
                    {
                            if (wholePage.indexOf(element) > 0)
                            {
                                    System.out.println("Running ERROR switched element: k="
                                                    + wholePage.indexOf(element) + " element=" + element);
                            }
                            else
                            {
                                    if (element.indexOf(".") > 0)
                                    {
                                            String partArray[] = element.split("\\.");
                                            String part1 = partArray[0].toString();
                                            String part9 = partArray[partArray.length - 1].toString();
                                            mapPrint(part1);
                                            mapPrint(part9);
                                    }
                                    System.out.println("Running ERROR missing element=" + element);
                            }
                            hit = false;
//                          missingElement = true;
                    }
            }
            else
            {
                    hit = true;
            }
            return hit;
    }

    private Boolean thisDebug = false;
    public Boolean checkEasyHitElements(String element, Boolean switched)
    {
            Boolean hit = false;
            int hitIndex = 0;
            if (cutPage.indexOf(element) > 0)
            { // OK
                    hitIndex = wholePage.length() - cutPage.length() + cutPage.indexOf(element);
                    if (! missingElement && thisDebug)
                    {
                            System.out.println("Running DEBUG: index=" + hitIndex + " element=" + element);
                    }
                    kMap.put(hitIndex + "", element);
                    cutPage = cutPage.substring(cutPage.indexOf(element) + element.length());
                    hit = true;
            }
            else
            {
                    if (wholePage.indexOf(element) > 0)
                    {
                            mapPrint(element);
                            tryFind = false;
                    }
            }
            return hit;
    }

    public void mapPrint(String element)
    {
            if (wholePage.indexOf(element) > 0)
            {
                    int k = wholePage.indexOf(element);
                    int count = wholePage.split(element).length - 1;
                    System.out.println("Running ERROR switched element: k=" + k + " count=" + count + " element=" + element);
                    if (count > 1)
                    {
                            String elementArray[] = wholePage.split(element);
                            k = 0;
                            for (int i = 0; i + 1 < elementArray.length; i++) {
                                    String part = elementArray[i];
                                    k = k + part.length();
                                    String add = "ADD";
                                    if (   kMap.containsKey(k + "")
                                            && (   element.indexOf(kMap.get(k + "").toString()) > -1
                                                    || kMap.get(k + "").toString().indexOf(element) > -1))
                                    {
                                            add = "OK";
                                    }
                                    if (elementArray.length < 10 || add.equals("ADD"))
                                    {
                                            System.out.println("Running                         k=" + k + " " + add);
                                    }
                                    k = k + element.length();
                            }
                    }
            }
    }

    private Boolean readPageFromFile = false;
    public void skipLoading(Boolean readPageFromFileParam)
    {
            readPageFromFile = readPageFromFileParam;
    }

    //////////////// END //////////////////////////////////////
}
