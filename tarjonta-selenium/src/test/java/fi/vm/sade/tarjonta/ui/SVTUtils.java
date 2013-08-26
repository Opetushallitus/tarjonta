package fi.vm.sade.tarjonta.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;


public class SVTUtils {
    protected final Logger log = LoggerFactory.getLogger("TEST");
    protected static final Logger log2 = LoggerFactory.getLogger("TEST");
    private static Kattavuus TarjontaSavuTekstit = new Kattavuus();
    private static Kattavuus TarjontaSavuSelaimet = new Kattavuus();

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
		        echo2("Running tarjonta-selenium.git-version=" + vProp.getProperty("tarjonta-selenium.git-version"));
			} catch (IOException e) {
				e.printStackTrace();
				throw new IOException("ERROR: Can't read version properties file. " + versionFile);
			}
		}
		else
		{
			echo2("ERROR: Can't find version properties file. " + versionFile);				
		}

		String propertiesFile = System.getProperty("user.home") + "/oph-configuration/tarjonta-selenium.properties";

		// localhost31config, localhost40config, luokka_config, reppu_config
		// See configs.xml
		String target = System.getenv("TESTTARGET"); 
		if (target != null)
		{
			echo2("Running Environment variable TESTTARGET=" + target);
			propertiesFile = propertiesFile + "." + target;
			echo2("Running propertiesFile: " + propertiesFile);
		}
		else
		{
			echo2("Running WARNING: Environment variable TESTTARGET is missing.");
		}

		prop = new Properties();
		try {
			prop.load(new FileInputStream(propertiesFile));
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("ERROR: Can't read properties file. " + propertiesFile);
		}
        echo2("Running tarjonta-selenium.oph-url=" + prop.getProperty("tarjonta-selenium.oph-url"));
        echo2("Running tarjonta-selenium.oph-login-url=" + prop.getProperty("tarjonta-selenium.oph-login-url"));
        echo2("Running tarjonta-selenium.tarjonta-url=" + prop.getProperty("tarjonta-selenium.tarjonta-url"));
        echo2("Running tarjonta-selenium.tomcat-logfile=" + prop.getProperty("tarjonta-selenium.tomcat-logfile"));
        echo2("Running tarjonta-selenium.qa=" + prop.getProperty("tarjonta-selenium.qa"));
        echo2("Running tarjonta-selenium.reppu=" + prop.getProperty("tarjonta-selenium.reppu"));
        echo2("Running tarjonta-selenium.luokka=" + prop.getProperty("tarjonta-selenium.luokka"));
        echo2("Running tarjonta-selenium.username=" + prop.getProperty("tarjonta-selenium.username"));
	}

	public void reppuLogin(WebDriver driver)
	{
		if (this.isPresentText(driver, "Kirjaudu ulos")) { return; }
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
			echo("Running step duration: " + dur);
		}
		double dur2 = (t2 - t1) / 1000.0;
		echo("Running step duration: " + dur2);
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

    public int hourInt()
    {
            return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public int minuteInt()
    {
            return Calendar.getInstance().get(Calendar.MINUTE);
    }

    public String yyyymmString()
    {
            String zero = "0";
            int yyyy = Calendar.getInstance().get(Calendar.YEAR);
            int mm = Calendar.getInstance().get(Calendar.MONTH) + 1;
            if (mm > 9) { zero = ""; }
            String yyyymm = yyyy + zero + mm;
            return yyyymm;
    }

    public String ddhhmmssString()
    {
            String dzero = "0", hzero = "0", mzero = "0", szero = "0";
            int dd = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            int hh = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int mm = Calendar.getInstance().get(Calendar.MINUTE);
            int ss = Calendar.getInstance().get(Calendar.SECOND);
            if (dd > 9) { dzero = ""; }
            if (hh > 9) { hzero = ""; }
            if (mm > 9) { mzero = ""; }
            if (ss > 9) { szero = ""; }
            String ddhhmmss = dzero + dd + hzero + hh + mzero + mm + szero + ss;
            return ddhhmmss;
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

	public WebElement getTriangleForFirstItem(WebDriver driver)
	{
        Object[] eles = driver.findElements(By.className("v-treetable-treespacer")).toArray();
        WebElement el;
        for (Object ele : eles)
        {
                el = (WebElement)ele;
                if (! el.isDisplayed() || ! el.isEnabled() || el.getLocation().x < 0 || el.getLocation().y < 0) { continue; }
                return el;
        }
        listXpathElements(driver, "//span[contains(@class, 'v-treetable-treespacer')]");
        int a = 1 / 0;
        return null;
	}	  

	public List<WebElement> getTriangleList(WebDriver driver)
	{
      return driver.findElements(By.className("v-treetable-treespacer"));
	}	  

	public WebElement getTriangleForLastHakukohde(WebDriver driver)
	{
        return driver.findElements(By.className("v-treetable-treespacer"))
        		.get(driver.findElements(By.className("v-treetable-treespacer")).size() - 1);
	}	  

	public WebElement getMenuNearestText(WebDriver driver, String text)
	{
		return this.findNearestElement(text, "//img[@class='v-icon']", driver);
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
		echo("focus: " + comment);
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
			echo(err);
			throw new Exception(err);
		}
	}

	public void isPresentText(WebDriver driver, String test, String err)
	{
		String source = driver.getPageSource();
		if (source.indexOf(test) < 0)
		{
			echo(err);
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
				echo("idLike " + id + " Sleep 10...");
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
        Object[] eles = driver.findElements(By.xpath("//*[contains(text(), '" + text  + "')]")).toArray();
        WebElement el;
        for (Object ele : eles)
        {
                el = (WebElement)ele;
                if (! el.isDisplayed() || ! el.isEnabled()) { continue; }
                el.click();
                return;
        }
        echo("ERROR textClick does not hit. text=" + text);
        int a = 1 / 0; // never here
	}
	
	public void doubleclick(WebDriver driver, String text)
	{
        Object[] eles = driver.findElements(By.xpath("//*[contains(text(), '" + text  + "')]")).toArray();
        WebElement el;
        for (Object ele : eles)
        {
                el = (WebElement)ele;
                if (! el.isDisplayed() || ! el.isEnabled()) { continue; }
                this.textClick(driver, text);
                this.tauko(1);
                Actions builder = new Actions(driver);
                Action doubleClick = builder.doubleClick(el).build();
                doubleClick.perform();
                return;
        }
        int a = 1 / 0; // never here
	}

	public void textClickLast(WebDriver driver, String text)
	{
        Object[] eles = driver.findElements(By.xpath("//*[contains(text(), '" + text  + "')]")).toArray();
        WebElement el = null;
        WebElement el2 = null;
        for (Object ele : eles)
        {
                el = (WebElement)ele;
                if (! el.isDisplayed() || ! el.isEnabled()) { continue; }
                el2 = el;
        }
        el2.click();
	}
	
	public WebElement textElement(WebDriver driver, String text)
	{
		//button[.='OK' and not(ancestor::div[contains(@style,'display:none')]) and not(ancestor::div[contains(@style,'display: none')])]

		String ancestor = "//*[contains(text(), '" + text  + "') and not(ancestor::div[contains(@style,'display:none')]) and not(ancestor::div[contains(@style,'display: none')])]";
		String xpathExpression = "//*[contains(text(), '" + text  + "')]";
		WebElement koe = driver.findElement(By.xpath(ancestor));
        Object[] eles = driver.findElements(By.xpath(xpathExpression)).toArray();
        WebElement el = null;
        WebElement el2 = null;
        for (Object ele : eles)
        {
                el = (WebElement)ele;
                try {
					if (! el.isDisplayed() || ! el.isEnabled() || el.getLocation().x < 0 
							|| el.getLocation().y < 0) { continue; }
				} catch (Exception e) {
					// uusi yritys
					this.echo("WARNING haetaan uudestaan. text=" + text);
					return textElement(driver, text);
				}
                return el;
        }
        if (this.listXpathElements(driver, xpathExpression)) { return textElement(driver, text); };
        int a = 1 / 0;
        return null;
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

	public String readFile( String file ) throws IOException {
        File tiedosto = new File(file);
        if (! tiedosto.exists()) { return "";}

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
		echo("page.txt");
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
			echo("Exception ");
		}
	}

    public void appendToFile(String fileName, String text)
    {
            try {
                    BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
                    out.write(text + System.getProperty("line.separator"));
                    out.close();
            }
            catch (IOException e)
            {
                    echo("Exception ");
            }
    }

    public Boolean listXpathElements(WebDriver driver, String xpathExpression)
    {
    	Boolean aliveElement = false;
    	Object[] eles = driver.findElements(By.xpath(xpathExpression)).toArray();
    	echo("listXpathElements: " + eles.length + " xpathExpression=" + xpathExpression);
    	WebElement el;
    	int i = 1;
    	String disabled = "";
    	for (Object ele : eles)
    	{
    		Boolean vikaa = false;
    		el = (WebElement)ele;
    		disabled = "";
    		if (! el.isDisplayed()) { vikaa = true; disabled = "Invisble"; }
    		if (! el.isEnabled()) { vikaa = true; disabled = disabled + "Disabled"; }
    		if (el.getLocation().x <= 0) { vikaa = true; }
    		if (el.getLocation().y <= 0) { vikaa = true; }
    		echo("listXpathElements i=" + i++ + " element=" + el.getLocation() + disabled);
    		if (! vikaa) { aliveElement = true; }
    	}
    	return aliveElement;
    }

	public String palvelimenVersio(WebDriver driver, String baseUrl)
	{
		// palvelin vastaa
		long t01 = millis();
		try {
			driver.get(baseUrl); // "http://localhost:8080
			driver.manage().window().maximize();
		} catch (Exception e) {
			echo("Running ERROR: Palvelin ei vastaa. baseUrl: " + baseUrl);
		}
		t01 = millisDiff(t01);
		this.tauko(1);

		// palvelimen versio
		String versioUrl = SVTUtils.prop.getProperty("tarjonta-selenium.tarjonta-versio-url");
		t01 = millis();
		echo("Running versioUrl: " + baseUrl + versioUrl);
		driver.get(baseUrl + versioUrl); 
		t01 = millisDiff(t01);
		
        Boolean branch = false;
        Boolean certificate = false;
        Boolean skip = true;
        while (skip)
        {
                if (this.isPresentText(driver, "branchName")) { skip = false; branch = true; }
                if (this.isPresentText(driver, "Certificate")) { skip = false; certificate = true; }
                this.tauko(1);
        }
        if (this.isPresentText(driver, "Certificate"))
        {
                this.tauko(1);
                driver.findElement(By.id("overridelink")).sendKeys(Keys.F12);
                System.out.println("HOUHOU Vaihda IE mode . . . + F12");
                this.tauko(30);
                driver.navigate().refresh();
                this.tauko(2);
                driver.findElement(By.id("overridelink")).click();
        }
        Assert.assertNotNull("Running buildversion.txt ei toimi.", this.textElement(driver, "branchName"));
		
        this.tauko(1);
        String versio = driver.getPageSource();
        if (versio.indexOf("pre-wrap") > 0) // Safari
        {
                versio = versio.split("pre-wrap;\">")[1];
                versio = versio.split("pre>")[0];
                versio = versio.replace("</", "").replace("<", "");
        }
        if (versio.indexOf("pre>") > 0) // Firefox
        {
                versio = versio.split("pre>")[1];
                versio = versio.replace("</", "").replace("<", "");
        }
        if (versio.indexOf("PRE>") > 0) // IE
        {
                versio = versio.split("PRE>")[1];
                versio = versio.replace("</", "").replace("<", "");
        }
        versio = "Running " + versio.replace("\n", "\nRunning ");
		echo(versio);
        // ja selain ja versio ym
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String selain = js.executeScript(
				"var N= navigator.appName, ua= navigator.userAgent, tem;"
						+ "var M= ua.match(/(opera|chrome|safari|firefox|msie)\\/?\\s*(\\.?\\d+(\\.\\d+)*)/i);"
						+ "if(M && (tem= ua.match(/version\\/([\\.\\d]+)/i))!= null) M[2]= tem[1];"
						+ "M= M? [M[1], M[2]]: [N, navigator.appVersion, '-?'];"
						+ "return M;").toString();
		String platform = js.executeScript("return navigator.platform;").toString();
		echo("Running browser: " + selain);
		echo("Running platform: " + platform);
		//
		selain = selain.replace(" ", "_").replace(",", "_").replace("[", "").replace("]", "");
		return selain;
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
                                    echo("Running ERROR missing elements: count=" + count
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
                            if (debug) { echo("DEBUG shortElement=" + shortElement); }
                            if (cutPage.indexOf(shortElement) > 0)
                            {
                                    if (debug) { echo("DEBUG lastChar=" + lastChar
                                                    + " GetBack=" + GetBack + " dotCount=" + dotCount); }
                                    if (GetBack)
                                    {
                                            // Loop for 14 dotPage canditates
                                            String dotPageArray[] = cutPage.split(shortElement);
                                            for (int i = 1; i < dotPageArray.length; i++) {
                                                    dotPage = dotPageArray[i];
                                                    if (debug) { echo("DEBUG i=" + i + " dotPage40=" + dotPage.substring(0, 40)); }
                                                    dotPage = dotPage.substring(0, dotCount);
                                                    String shortElement2 = shortElement + dotPage + endString.substring(dotCount);
                                                    if (debug) { echo("DEBUG dotPage=" + dotPage + " endString=" + endString);
                                                    echo("DEBUG shortElement2=" + shortElement2); }
                                                    if (cutPage.indexOf(shortElement2) > 0 && dotCount > 0) {
                                                            checkEasyHitElements(shortElement2, switched);
                                                            if (debug) { echo("DEBUG out 1 index=" + cutPage.indexOf(shortElement2)); }
                                                            return true; }
//                                                  if (checkDottedElements(shortElement2, page) && dotCount > 0) {
//                                                          echo("DEBUG out 2");
//                                                          return ok; }
                                            }
                                            GetBack = false;
                                            dotCount = 0;
                                            endString = "";
                                    }
                                    else
                                    {
                                            failing = false;
                                            echo("Running ERROR finding element=" + shortElement);
                                    }
                            }
                    }
                    if (! getOut)
                    {
                            if (wholePage.indexOf(element) > 0)
                            {
                                    echo("Running ERROR switched element: k="
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
                                    echo("Running ERROR missing element=" + element);
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
                            echo("Running DEBUG: index=" + hitIndex + " element=" + element);
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
                    echo("Running ERROR switched element: k=" + k + " count=" + count + " element=" + element);
                    if (count > 1)
                    {
                            String elementArray[] = wholePage.split(element);
                            k = 0;
                            int kk = 0;
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
                                    if (elementArray.length < 10 || (add.equals("ADD") && kk < 10))
                                    {
                                            echo("Running                         k=" + k + " " + add);
                                            kk++;
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
    
    public void echo(String text)
    {
    	log.info(text);
    }
    
    public static void echo2(String text)
    {
    	log2.info(text);
    }
    
    /////////////// RAPORTTI ////////////////////////////
    static Properties propMessages;
    static int messagesCount = 0;
    public void messagesPropertiesInit() throws IOException
    {
    	if (propMessages != null && propMessages.size() > 0) { return; }
    	String messagesFile = "../tarjonta-app/src/main/resources/i18n/messages.properties";
    	File mFile = new File(messagesFile);
    	if (mFile.exists())
    	{
    		propMessages = new Properties();
    		try {
    			propMessages.load(new FileInputStream(messagesFile));
    			messagesCount = propMessages.size();
    		} catch (IOException e) {
    			e.printStackTrace();
    			throw new IOException("ERROR: Can't read message properties file. " + messagesFile);
    		}
    	}
    	else
    	{
    		echo2("ERROR: Can't find message properties file. " + messagesFile);
    	}
    }
    
    static Properties propMessages2;
    public void messagesProperties2Init() throws IOException
    {
    	if (propMessages2 != null && propMessages2.size() > 0) { return; }
    	String messagesFile = "../tarjonta-app/src/main/resources/i18n/messages.properties";
    	File mFile = new File(messagesFile);
    	if (mFile.exists())
    	{
    		propMessages2 = new Properties();
    		try {
    			propMessages2.load(new FileInputStream(messagesFile));
    		} catch (IOException e) {
    			e.printStackTrace();
    			throw new IOException("ERROR: Can't read message properties2 file. " + messagesFile);
    		}
    	}
    	else
    	{
    		echo2("ERROR: Can't find message properties2 file. " + messagesFile);
    	}
    }

    public void messagesPropertiesCoverage(WebDriver driver, Kattavuus taulukko)
    {
    	String pageSource = driver.getPageSource();
    	Enumeration mKeys = propMessages.keys();
    	while (mKeys.hasMoreElements()) {
    		String msgKey = mKeys.nextElement().toString();
    		String msg = propMessages.getProperty(msgKey);
    		if (pageSource.indexOf(msg) > 0)
    		{
    			propMessages.remove(msgKey);
    			taulukko.KattavuusTaulukko.setProperty(msgKey, Kattavuus.KATTAVUUSOK);
    		}
    	}
    }
    public void messagesPropertiesSave(Kattavuus taulukko) throws IOException
    {
        echo("");
        Enumeration mKeys = propMessages.keys();
        List<String> list = Collections.list(mKeys);
        Collections.sort(list);

        String unusedKeys, errKeys, editKeys, ignoreKeys;
        int errs = 0;
        int edits = 0;
        int unused = 0;
        int ignore = 0;
        int puuttuu = 0;
        try {
            unusedKeys = readFile("src/test/resources/messages.properties.unused");
            errKeys = readFile("src/test/resources/messages.properties.errmsgs");
            editKeys = readFile("src/test/resources/messages.properties.editmsgs");
            ignoreKeys = readFile("src/test/resources/messages.properties.ignore");
            String ls = System.getProperty("line.separator");
            unused = unusedKeys.split(ls).length;
            errs = errKeys.split(ls).length;
            edits = editKeys.split(ls).length;
            ignore = ignoreKeys.split(ls).length;
            for (String msgKey : list) {
                    if (unusedKeys.indexOf(msgKey) < 0 && errKeys.indexOf(msgKey) < 0
                                    && editKeys.indexOf(msgKey) < 0 && ignoreKeys.indexOf(msgKey) < 0)
                    {
                            String msg = propMessages.getProperty(msgKey);
                            echo("Hakusessa: " + msgKey + "=" + msg);
                            puuttuu++;
                            taulukko.KattavuusTaulukko.setProperty(msgKey, Kattavuus.KATTAVUUSFAILURE);
                    }
            }
    } catch (IOException e) {
            e.printStackTrace();
    }
        taulukko.KattavuusRaportti();
        int tavoite = messagesCount - errs - edits - unused - ignore;
        int katselmoin = tavoite - puuttuu;
        if (tavoite == 0) { tavoite = 1; }
        double coverage = roundTwoDecimals(100.0 * katselmoin / tavoite);
        echo("");
        echo("--------------------------");
        echo("");
                echo("Tekstit    " + messagesCount);
                echo("- errs      " + errs);
                echo("- edits     " + edits);
                echo("- ignore     " + ignore);
                echo("- unused    " + unused);
                echo("--------------");
                echo("Tavoite    " + tavoite);
                echo("Katselmoin " + katselmoin);
                echo("Hakusessa   " + puuttuu);
                echo("--------------");
                echo("Kattavuus:  " + coverage + "%");
        echo("");
        echo("--------------------------");
        echo("");
    }

    public void messagesPropertiesSaveElements(Kattavuus taulukko) throws IOException
    {
        echo("");
        Enumeration mKeys = propMessages.keys();
        List<String> list = Collections.list(mKeys);
        Collections.sort(list);

        String unusedKeys, errKeys, editKeys, ignoreKeys, dialogKeys;
        int errs = 0, edits = 0, unused = 0, ignore = 0, puuttuu = 0, dialog = 0;
        try {
            unusedKeys = readFile("src/test/resources/messages.properties.unused");
            errKeys = readFile("src/test/resources/messages.properties.errmsgs");
            editKeys = readFile("src/test/resources/messages.properties.editmsgs");
            ignoreKeys = readFile("src/test/resources/messages.properties.ignore");
            dialogKeys = readFile("src/test/resources/messages.properties.dialog");
            String ls = System.getProperty("line.separator");
            unused = unusedKeys.split(ls).length;
            errs = errKeys.split(ls).length;
            edits = editKeys.split(ls).length;
            ignore = ignoreKeys.split(ls).length;
            dialog = dialogKeys.split(ls).length;
            for (String msgKey : list) {
                    if (unusedKeys.indexOf(msgKey) < 0 && errKeys.indexOf(msgKey) < 0
                                    && editKeys.indexOf(msgKey) < 0 && ignoreKeys.indexOf(msgKey) < 0
                                    && dialogKeys.indexOf(msgKey) < 0)
                    {
                            String msg = propMessages.getProperty(msgKey);
                            echo("Hakusessa: " + msgKey + "=" + msg);
                            puuttuu++;
                            taulukko.KattavuusTaulukko.setProperty(msgKey, Kattavuus.KATTAVUUSFAILURE);
                    }
            }
    } catch (IOException e) {
            e.printStackTrace();
    }
        taulukko.KattavuusRaportti();
        int tavoite = messagesCount - errs - edits - unused - ignore;
        int katselmoin = tavoite - puuttuu;
        if (tavoite == 0) { tavoite = 1; }
        double coverage = roundTwoDecimals(100.0 * katselmoin / tavoite);
        echo("");
        echo("--------------------------");
        echo("");
                echo("Tekstit    " + messagesCount);
                echo("- errs      " + errs);
                echo("- edits     " + edits);
                echo("- ignore     " + ignore);
                echo("- unused    " + unused);
                echo("- dialog    " + dialog);
                echo("--------------");
                echo("Tavoite    " + tavoite);
                echo("Katselmoin " + katselmoin);
                echo("Hakusessa   " + puuttuu);
                echo("--------------");
                echo("Kattavuus:  " + coverage + "%");
        echo("");
        echo("--------------------------");
        echo("");
    }
    
    double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        double n2 = 0.0;
                try {
                        n2 = Double.valueOf(twoDForm.format(d));
                } catch (NumberFormatException e) {
                        e.printStackTrace();
                }
        return n2;
    }
    //////////// RAPORTTI END //////////////////////////////
    public WebElement findNearestElement(String label, String xpathExpression, WebDriver driver)
    {
        WebElement input = null;
        WebElement textElement = this.textElement(driver, label);

        Object[] eles = driver.findElements(By.xpath(xpathExpression)).toArray();
        int i = 1;
        int minDistance = 100000;
        for (Object ele : eles)
        {
                WebElement el = (WebElement)ele;
            	if (! el.isDisplayed() || ! el.isEnabled()) { continue; }
                int distance = getDistance((Point)textElement.getLocation(), (Point)el.getLocation());
                if (distance < minDistance) { minDistance = distance; }
        }

        for (Object ele : eles)
        {
                WebElement el = (WebElement)ele;
            	if (! el.isDisplayed() || ! el.isEnabled()) { continue; }
                int distance = getDistance((Point)textElement.getLocation(), (Point)el.getLocation());
                if (distance == minDistance) { input = el; }
        }

        return input;
    }

    public WebElement findNearestElementExact(String label, String xpathExpression, WebDriver driver)
    {
        WebElement input = null;
        WebElement textElement = driver.findElement(By.xpath("//*[text()='" + label  + "']"));
        String page = driver.getPageSource();
        if (page.split(label).length != 2) { echo("Warning: ambiquous lables exists. label=" + label); }

        Object[] eles = driver.findElements(By.xpath(xpathExpression)).toArray();
        int i = 1;
        int minDistance = 100000;
        for (Object ele : eles)
        {
                WebElement el = (WebElement)ele;
                if (! el.isDisplayed() || ! el.isEnabled()) { continue; }
                int distance = getDistance((Point)textElement.getLocation(), (Point)el.getLocation());
                if (distance < minDistance) { minDistance = distance; }
        }

        for (Object ele : eles)
        {
                WebElement el = (WebElement)ele;
                if (! el.isDisplayed() || ! el.isEnabled()) { continue; }
                int distance = getDistance((Point)textElement.getLocation(), (Point)el.getLocation());
                if (distance == minDistance) { input = el; }
        }

        return input;
    }

    public WebElement findNearestElementPlusX(String label, String xpathExpression, int x, WebDriver driver)
    {
        WebElement input = null;
        WebElement textElement = this.textElement(driver, label);

        Object[] eles = driver.findElements(By.xpath(xpathExpression)).toArray();
        int i = 1;
        int minDistance = 100000;
        for (Object ele : eles)
        {
                WebElement el = (WebElement)ele;
                if (! el.isDisplayed() || ! el.isEnabled()) { continue; }
                int distance = getDistance((Point)textElement.getLocation(), (Point)el.getLocation());
                if ((textElement.getLocation().x + x) < el.getLocation().x 
                		&& distance < minDistance) // && textElement.getLocation().y < el.getLocation().y 
                {
                        minDistance = distance;
                }
        }

        for (Object ele : eles)
        {
                WebElement el = (WebElement)ele;
                int distance = getDistance((Point)textElement.getLocation(), (Point)el.getLocation());
                if ((textElement.getLocation().x + x) < el.getLocation().x // && textElement.getLocation().y < el.getLocation().y 
                		&& distance == minDistance) { input = el; }
        }
        if (false) { echo("DEBUG textElement: " + textElement.getLocation() + " input: " + input.getLocation()); }
        return input;
    }

    public WebElement findNearestElementPlusY(String label, String xpathExpression, WebDriver driver)
    {
        WebElement input = null;
        WebElement textElement = this.textElement(driver, label);

        Object[] eles = driver.findElements(By.xpath(xpathExpression)).toArray();
        int i = 1;
        int minDistance = 100000;
        for (Object ele : eles)
        {
                WebElement el = (WebElement)ele;
                if (! el.isDisplayed() || ! el.isEnabled()) { continue; }
                int distance = getDistance((Point)textElement.getLocation(), (Point)el.getLocation());
                if (textElement.getLocation().y < el.getLocation().y && distance < minDistance)
                {
                        minDistance = distance;
                }
        }

        for (Object ele : eles)
        {
                WebElement el = (WebElement)ele;
                if (! el.isDisplayed() || ! el.isEnabled()) { continue; }
                int distance = getDistance((Point)textElement.getLocation(), (Point)el.getLocation());
                if (textElement.getLocation().y < el.getLocation().y && distance == minDistance) { input = el; }
        }
        return input;
    }

    public WebElement findNearestElementPlusY2(String label, String xpathExpression, WebDriver driver)
    {
        WebElement input = null;
        WebElement textElement = this.textElement(driver, label);

        Object[] eles = driver.findElements(By.xpath(xpathExpression)).toArray();
        int minDistance = 100000;
        int minDistance2 = 200000;
        for (Object ele : eles)
        {
                WebElement el = (WebElement)ele;
                int distance = getDistance((Point)textElement.getLocation(), (Point)el.getLocation());
                if (textElement.getLocation().y < el.getLocation().y && distance < minDistance2)
                {
                        if (distance > minDistance)
                        {
                                minDistance2 = distance;
                        }
                        else
                        {
                                minDistance2 = minDistance;
                                minDistance = distance;
                        }
                }
        }
        for (Object ele : eles)
        {
        	WebElement el = (WebElement)ele;
        	int distance = getDistance((Point)textElement.getLocation(), (Point)el.getLocation());
        	if (textElement.getLocation().y < el.getLocation().y && distance == minDistance2) { input = el; }
        }

        if (input == null) { echo("ERROR: label=" + label + " minDistance=" + minDistance + " minDistance2=" + minDistance2 + " elementloc=" + textElement.getLocation()); }
        return input;
    }
    
    public WebElement findNearestElementMinusY(String label, String xpathExpression, WebDriver driver)
    {
        WebElement input = null;
        WebElement textElement = this.textElement(driver, label);

        Object[] eles = driver.findElements(By.xpath(xpathExpression)).toArray();
        int i = 1;
        int minDistance = 100000;
        for (Object ele : eles)
        {
                WebElement el = (WebElement)ele;
                int distance = getDistance((Point)textElement.getLocation(), (Point)el.getLocation());
                if (textElement.getLocation().y > el.getLocation().y && distance < minDistance)
                {
                        minDistance = distance;
                }
        }

        for (Object ele : eles)
        {
                WebElement el = (WebElement)ele;
                int distance = getDistance((Point)textElement.getLocation(), (Point)el.getLocation());
                if (textElement.getLocation().y > el.getLocation().y && distance == minDistance) { input = el; }
        }
        return input;
    }

    public int getDistance(Point p1, Point p2)
    {
        double distance = Math.sqrt(Math.pow((p2.getX() - p1.getX()), 2) + Math.pow((p2.getY() - p1.getY()), 2));
        return (int)distance;
    }

    public void alustaVaatimukset(Kattavuus taulukko)
    {
            if (taulukko.KattavuusTaulukko.size() > 0) { return; }
            taulukko.KattavuusTaulukko.setProperty(Kattavuus.KATTAVUUSKOHDE, "Vaatimukset");
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_27", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_28", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_29", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_30", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_31", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_32", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_35", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_37", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_67", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_68", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_99", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_100", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_102", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_103", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_111", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_124", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_125", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_126", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_137", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_138", Kattavuus.KATTAVUUSNOTEST);
            taulukko.KattavuusTaulukko.setProperty("V_KJOH_139", Kattavuus.KATTAVUUSNOTEST);
    }
    
    public void alustaSelaimet(Kattavuus taulukko, String moduli) 
    {
    	if (taulukko.KattavuusTaulukko.size() > 0) { return; }
    	taulukko.KattavuusTaulukko.setProperty(Kattavuus.KATTAVUUSKOHDE, moduli);
    	taulukko.KattavuusTaulukko.setProperty("Opera", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Safari", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Chrome", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("MSIE8", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("MSIE9", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("MSIE10", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Firefox__5.0", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Firefox__6.0", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Firefox__7.0", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Firefox__8.0", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Firefox__9.0", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Firefox__10.0", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Firefox__11.0", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Firefox__12.0", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Firefox__13.0", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Firefox__14.0", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Firefox__15.0", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Firefox__16.0", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Firefox__17.0", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Firefox__18.0", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Firefox__19.0", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Firefox__20.0", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Firefox__21.0", Kattavuus.KATTAVUUSNOTEST);
    	taulukko.KattavuusTaulukko.setProperty("Firefox__22.0", Kattavuus.KATTAVUUSNOTEST);
    }

    public void menuOperaatio(WebDriver driver, String operaatio, String kohde)
    {
    	WebElement menu = getMenuNearestText(driver, kohde);
    	menu.click();
    	Assert.assertNotNull("Menu ei aukee.", this.textElement(driver, "Tarkastele"));
    	tauko(1);
    	String htmlOperaatio = "<span class=\"v-menubar-menuitem-caption\">" + operaatio + "</span>";
    	if (! this.isPresentText(driver, htmlOperaatio))
    	{
    		// avataan menu uudestaan
    		WebElement menu2 = getMenuNearestText(driver, kohde);
    		menu2.click();
    		tauko(1);
    		WebElement menu3 = getMenuNearestText(driver, kohde);
    		menu3.click();
    		Assert.assertNotNull("Menu ei aukee.", this.textElement(driver, "Tarkastele"));
    		Assert.assertNotNull("Operaatio ei tule esiin.", this.isPresentText(driver, htmlOperaatio));
    		tauko(1);
    	}
    	driver.findElement(By.xpath("//span[@class='v-menubar-menuitem-caption' and text()='" + operaatio + "']")).click();
    }

    public void menuOperaatioFirstMenu(WebDriver driver, String operaatio)
    {
    	WebElement menu = this.findNearestElement("Valitse kaikki", "//img[@class='v-icon']", driver);
    	int i = 0;
    	while (menu == null) 
    	{
    		menu = this.findNearestElement("Valitse kaikki", "//img[@class='v-icon']", driver);
    		this.tauko(1);
    		i++;
    		if (i > 30) { int a = 1 / 0; }
    	}
    	menu.click();
    	Assert.assertNotNull("Menu ei aukee.", this.textElement(driver, "Tarkastele"));
    	tauko(1);
    	String htmlOperaatio = "<span class=\"v-menubar-menuitem-caption\">" + operaatio + "</span>";
    	if (! this.isPresentText(driver, htmlOperaatio))
    	{
    		// avataan menu uudestaan
    		WebElement menu2 = this.findNearestElement("Valitse kaikki", "//img[@class='v-icon']", driver);
    		menu2.click();
    		tauko(1);
    		WebElement menu3 = this.findNearestElement("Valitse kaikki", "//img[@class='v-icon']", driver);
    		menu3.click();
    		Assert.assertNotNull("Menu ei aukee.", this.textElement(driver, "Tarkastele"));
    		Assert.assertNotNull("Operaatio ei tule esiin.", this.isPresentText(driver, htmlOperaatio));
    		tauko(1);
    	}
    	driver.findElement(By.xpath("//span[@class='v-menubar-menuitem-caption' and text()='" + operaatio + "']")).click();
    }

    public void menuOperaatioMenu(WebDriver driver, WebElement menu, String operaatio)
    {
    	menu.click();
    	Assert.assertNotNull("Menu ei aukee.", this.textElement(driver, "Tarkastele"));
    	tauko(1);
    	String htmlOperaatio = "<span class=\"v-menubar-menuitem-caption\">" + operaatio + "</span>";
    	if (! this.isPresentText(driver, htmlOperaatio))
    	{
    		// avataan menu uudestaan
    		WebElement menu2 = this.findNearestElement("Valitse kaikki", "//img[@class='v-icon']", driver);
    		menu2.click();
    		tauko(1);
    		WebElement menu3 = this.findNearestElement("Valitse kaikki", "//img[@class='v-icon']", driver);
    		menu3.click();
    		Assert.assertNotNull("Menu ei aukee.", this.textElement(driver, "Tarkastele"));
    		Assert.assertNotNull("Operaatio ei tule esiin.", this.isPresentText(driver, htmlOperaatio));
    		tauko(1);
    	}
    	driver.findElement(By.xpath("//span[@class='v-menubar-menuitem-caption' and text()='" + operaatio + "']")).click();
    }

    public void sendInput(WebDriver driver, String label, String value)
    {
        Assert.assertNotNull("Haettua kenttaa ei loydy", this.textElement(driver, label));
        WebElement input = findNearestElement(label, "//input", driver);
        if (value != null && value.equals("SELECTED"))
        {
                if (! input.isSelected())
                {
                input.click();
                tauko(1);
                }
                return;
        }
        input.clear();
        tauko(1);
        input.sendKeys(value);
        tauko(1);
    }

    public void sendInputTextArea(WebDriver driver, String label, String value)
    {
        Assert.assertNotNull("Haettua kenttaa ei loydy", this.textElement(driver, label));
        WebElement input = findNearestElement(label, "//textarea", driver);
        input.clear();
        tauko(1);
        input.sendKeys(value);
        tauko(1);
    }
    
    public void sendInputExact(WebDriver driver, String label, String value)
    {
        Assert.assertNotNull("Haettua kenttaa ei loydy", this.textElement(driver, label));
        WebElement input = findNearestElementExact(label, "//input", driver);
        if (value != null && value.equals("SELECTED"))
        {
                if (! input.isSelected())
                {
                input.click();
                tauko(1);
                }
                return;
        }
        input.clear();
        tauko(1);
        input.sendKeys(value);
        tauko(1);
    }

    public void sendInputPlusX(WebDriver driver, String label, String value, int x)
    {
        Assert.assertNotNull("Haettua kenttaa ei loydy", this.textElement(driver, label));
        WebElement input = this.findNearestElementPlusX(label, "//input", x, driver);
        input.clear();
        tauko(1);
        input.sendKeys(value);
        tauko(1);
    }

    public void sendInputPlusY(WebDriver driver, String label, String value)
    {
        Assert.assertNotNull("Haettua kenttaa ei loydy", this.textElement(driver, label));
        WebElement input = this.findNearestElementPlusY(label, "//input", driver);
        input.clear();
        tauko(1);
        input.sendKeys(value);
        tauko(1);
    }

    public void sendInputPlusY2(WebDriver driver, String label, String value)
    {
        Assert.assertNotNull("Haettua kenttaa ei loydy", this.textElement(driver, label));
        WebElement input = this.findNearestElementPlusY2(label, "//input", driver);
        input.clear();
        tauko(1);
        input.sendKeys(value);
        tauko(1);
    }

    public void sendInputMinusY(WebDriver driver, String label, String value)
    {
        Assert.assertNotNull("Haettua kenttaa ei loydy", this.textElement(driver, label));
        WebElement input = this.findNearestElementMinusY(label, "//input", driver);
        input.clear();
        tauko(1);
        input.sendKeys(value);
        tauko(1);
    }
    
    public String getTextMinusY(WebDriver driver, String label, String xpath)
    {
        Assert.assertNotNull("Haettua kenttaa ei loydy", this.textElement(driver, label));
        WebElement input = this.findNearestElementMinusY(label, xpath, driver);
        return input.getText();
    }

    public void popupItemClick(WebDriver driver, String text)
    {
        WebElement item = driver.findElement(By.xpath("//td[@class='gwt-MenuItem' and span[text()='" + text + "']]"));
        item.click();
        this.tauko(1);
    }

    public String getNthTinyId(WebDriver driver, int nth) throws IOException
    {
        String page = this.getPageSourceFromFile();
        String[] tinys = page.split("tinyMCE.get");
        for (int i = 0; i < tinys.length; i++) {
                        String tiny = tinys[i];
                        tiny = tiny.substring(0, tiny.indexOf("')"));
                        tiny = tiny.replace("InstanceById('", "");
                        if (tiny.length() > 100) { tiny = ""; }
                        if (i == nth) { return tiny; }
                }
        String elementID = "";
        return elementID;
    }

    public String getNthTinyIdFromText(WebDriver driver, String pagePart, int nth) throws IOException
    {
        String page = pagePart;
        String[] tinys = page.split("tinyMCE.get");
        for (int i = 0; i < tinys.length; i++) {
                        String tiny = tinys[i];
                        if (tiny.indexOf("')") == -1) { continue; }
                        tiny = tiny.substring(0, tiny.indexOf("')"));
                        tiny = tiny.replace("InstanceById('", "");
                        if (tiny.length() > 100) { tiny = ""; }
                        if (i == nth) { return tiny; }
                }
        String elementID = "";
        return elementID;
    }

    public void sendInputTiny(WebDriver driver, String beforeText, String value) throws IOException
    {
    	String page = driver.getPageSource();
    	Assert.assertNotNull("Running Missing: " + beforeText, textElement(driver, beforeText));
    	if (page.indexOf(beforeText) == -1) { int a = 1 / 0; }
    	String pagePart = page.split(beforeText)[page.split(beforeText).length - 1];
    	String elementID = getNthTinyIdFromText(driver, pagePart, 1);
    	JavascriptExecutor exec = (JavascriptExecutor) driver;
    	try {
    		exec.executeScript("var _tmp = tinymce.get('" + elementID
    				+ "'); _tmp.setContent('" + value + "'); _tmp.save();");
    		tauko(1);
    	} catch (Exception e) {

    	}
    }

    public void printMyStackTrace(Exception e)
    {
    	int hit = 0;
    	StackTraceElement[] stack = e.getStackTrace();
    	for (int i = 0; i < stack.length; i++) {
    		StackTraceElement stackTraceElement = stack[i];
    		String line = stackTraceElement.toString();
    		if (hit > 0)
    		{
    			this.echo(line);
    			hit = 0;
    		}
    		else
    		{
    			if (line.indexOf("fi.vm.sade") > -1)
    			{
    				this.echo(line);
    				hit = 1;
    			}
    		}
    	}
    }

	public Boolean PoistaKoulutus(WebDriver driver, String haku) throws Exception {
    	WebElement search = driver.findElements(By.className("v-textfield-search-box")).get(1);
    	search.clear();
    	search.sendKeys(haku);
    	tauko(1);
    	driver.findElement(By.xpath("(//span[text() = 'Hae'])[2]")).click();
    	tauko(5);
    	if (this.isPresentText(driver, "Koulutukset (0)")) { return false; }
    	WebElement triangle = getTriangleForFirstItem(driver);
        Assert.assertNotNull("Running koulutushaku triangle ei toimi.", triangle);
    	tauko(1);
    	triangle.click();
    	WebElement link = driver.findElement(By.className("v-button-link-row"));
        Assert.assertNotNull("Running koulutushaku link ei toimi.", link);
        this.menuOperaatio(driver, "Poista", haku);
    	Assert.assertNotNull("Running poistaa koulutus ei toimi."
    			, this.textElement(driver, "Haluatko varmasti poistaa alla olevan koulutuksen?"));
        this.tauko(1);
        this.textClick(driver, "Kyllä");
        this.tauko(1);
        return true;
	}

	public WebElement TarkasteleKoulutusLuonnosta(WebDriver driver, String haku) throws Exception {
    	WebElement search = driver.findElements(By.className("v-textfield-search-box")).get(1);
    	search.clear();
    	search.sendKeys(haku);
    	this.tauko(1);
    	driver.findElement(By.xpath("(//span[text() = 'Hae'])[2]")).click();
        Assert.assertNotNull("Running Hae koulutuksia ei toimi."
                , this.textElement(driver, "Koulutukset ("));
    	List<WebElement> triangles = this.getTriangleList(driver);
    	Assert.assertNotNull("Running koulutushaku triangles ei esiinny.", triangles);
    	this.tauko(1);
    	int i = 0;
    	Boolean hit = false;
    	try {
			while (triangles.get(i) != null) {
				triangles = this.getTriangleList(driver);
				WebElement triangle = triangles.get(i);
				triangle.click();
				this.tauko(1);
				if (this.isPresentText(driver, "luonnos")) { hit = true; break; }
				triangles = this.getTriangleList(driver);
				triangle = triangles.get(i);
				triangle.click();
				this.tauko(1);
				i++;
			}
		} catch (Exception e) {
		}
    	WebElement link = null;
    	if (hit) { link = this.getMenuNearestText(driver, "luonnos"); }
    	return link;
    }

	public WebElement TarkasteleHakukohdeLuonnosta(WebDriver driver, String haku) throws Exception {
    	WebElement search = driver.findElements(By.className("v-textfield-search-box")).get(1);
    	search.clear();
    	search.sendKeys(haku);
    	this.tauko(1);
    	driver.findElement(By.xpath("(//span[text() = 'Hae'])[2]")).click();
        Assert.assertNotNull("Running Hae koulutuksia ei toimi."
                , this.textElement(driver, "Koulutukset ("));
    	List<WebElement> triangles = this.getTriangleList(driver);
    	Assert.assertNotNull("Running koulutushaku triangles ei esiinny.", triangles);
    	this.tauko(1);
    	int i = triangles.size() - 1;
    	Boolean hit = false;
    	int yritys = 0;
    	try {
			while (triangles.get(i) != null) {
				triangles = this.getTriangleList(driver);
				WebElement triangle = triangles.get(i);
				triangle.click();
				this.tauko(1);
				if (this.isPresentText(driver, "luonnos")) { hit = true; break; }
				triangles = this.getTriangleList(driver);
				triangle = triangles.get(i);
				triangle.click();
				this.tauko(1);
				i--;
				yritys++;
			}
		} catch (Exception e) {
			this.echo("Running Kolmiot loppuivat i=" + i + " yrityksia=" + yritys);
		}
    	WebElement link = null;
    	if (hit) { link = this.getMenuNearestText(driver, "luonnos"); }
    	return link;
    }

	public void ValikotHakukohteidenYllapito(WebDriver driver, String baseUrl)
	{
		this.textClick(driver, "Suunnittelu ja tarjonta");
		this.tauko(1);
		this.textClick(driver, "Koulutusten ja hakukohteiden ylläpito");
		this.tauko(1);
		try {
			Assert.assertNotNull("Running Hakukohteiden valikot ei toimi."
					, this.textElement(driver, "Koulutuksen alkamisvuosi"));
		} catch (Exception e) {
			driver.get(baseUrl + SVTUtils.prop.getProperty("tarjonta-selenium.tarjonta-url") + "?restartApplication");
			Assert.assertNotNull("Running Hakukohteiden valikot ei toimi."
					, this.textElement(driver, "Koulutuksen alkamisvuosi"));
		}
		this.tauko(1);
	}

	public void ValikotHakujenYllapito(WebDriver driver, String baseUrl)
	{
		this.textClick(driver, "Haut");
		this.tauko(1);
		this.textClick(driver, "Haun");
		this.tauko(1);
		try {
			Assert.assertNotNull("Running Hakujen valikot ei toimi."
					, this.textElement(driver, "Luo uusi haku"));
		} catch (Exception e) {
			driver.get(baseUrl + SVTUtils.prop.getProperty("tarjonta-selenium.haku-url") + "?restartApplication");
			Assert.assertNotNull("Running Hakujen valikot ei toimi."
					, this.textElement(driver, "Luo uusi haku"));
		}
		this.tauko(1);
	}
	
	public void ValikotValintaperusteKuvaustenYllapito(WebDriver driver, String baseUrl)
	{
		this.textClick(driver, "Suunnittelu ja tarjonta");
		this.tauko(1);
		this.textClick(driver, "Valintaperustekuvausten");
		this.tauko(1);
		try {
			Assert.assertNotNull("Running Valintaperustekuvausten valikot ei toimi."
					, this.textElement(driver, "Kuvausteksti"));
		} catch (Exception e) {
			driver.get(baseUrl + SVTUtils.prop.getProperty("tarjonta-selenium.valinta-url") + "?restartApplication");
			Assert.assertNotNull("Running Valintaperustekuvausten valikot ei toimi."
					, this.textElement(driver, "Kuvausteksti"));
		}
		this.tauko(1);
	}

    public String commonStringBegin(String str1, String str2)
    {
    	String str = "";
    	int i = 0;
    	String s1 = str1.substring(i, i + 1);
    	String s2 = str2.substring(i, i + 1);
    	while (s1.equals(s2))
    	{
    		if (s1 == " " || s1 == ",") { break; }
    		str = str + s1;
    		i++;
    		if (str1.length() == i) { break; }
    		if (str2.length() == i) { break; }
        	s1 = str1.substring(i, i + 1);
        	s2 = str2.substring(i, i + 1);
    	}
    	return str;
    }

    public void screenShot(String comment, WebDriver driver) throws IOException
    {
    	String millis = System.currentTimeMillis() + "";
    	File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
    	String fileName = System.getProperty("user.home") + "/screenshot_" + comment + "_" + millis + ".png";
    	FileUtils.copyFile(scrFile, new File(fileName));
    }

}
