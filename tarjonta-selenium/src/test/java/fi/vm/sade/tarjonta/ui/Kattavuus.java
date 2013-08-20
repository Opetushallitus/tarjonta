package fi.vm.sade.tarjonta.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

public class Kattavuus {
    // KATTAVUUSRAPORTTI

    public static String KATTAVUUSOK = "ok";
    public static String KATTAVUUSERROR = "error";
    public static String KATTAVUUSFAILURE = "failure";
    public static String KATTAVUUSTEST = "test";
    public static String KATTAVUUSNOTEST = "notest";
    public static String KATTAVUUSKOHDE = "kohde";
    public Properties KattavuusTaulukko = new Properties();
    private Boolean useFiles = false;

    public void alustaKattavuusKohde(String kohde) throws SQLException
    {
    	if (KattavuusTaulukko.size() > 0) { return; }
    	KattavuusTaulukko.setProperty(Kattavuus.KATTAVUUSKOHDE, kohde);
    }

    public void setKattavuus(String key, String status)
    {
    String metriikkaMode = SVTUtils.prop.getProperty("tarjonta-selenium.metriikka");
    if (metriikkaMode != null && metriikkaMode.equals("true")) { useFiles = true; }
            KattavuusTaulukko.setProperty(key, status);
            if (status == KATTAVUUSOK)
            {
            SVTUtils doit = new SVTUtils();
            String kohde = KattavuusTaulukko.getProperty(KATTAVUUSKOHDE);
            String yyyymm = doit.yyyymmString();
            String fileName = System.getProperty("user.home") + "/kattavuus/" + kohde + "." + yyyymm + ".txt";
            String text = kohde + " " + yyyymm + " " + doit.ddhhmmssString() + " " + key;
            if (kohde == null || kohde.length() == 0) { int a = 1 / 0; }
            if (useFiles) { doit.appendToFile(fileName, text); }
            }
    }

    public Boolean KattavuusRaporttiHiljaa = false;
    public void KattavuusRaportti() throws IOException
    {
	String otsikko = KattavuusTaulukko.getProperty(KATTAVUUSKOHDE);
	if (otsikko == null || otsikko.length() == 0) { int a = 1 / 0; }
    	SVTUtils doit = new SVTUtils();
    	String metriikkaMode = SVTUtils.prop.getProperty("tarjonta-selenium.metriikka");
    	if (metriikkaMode != null && metriikkaMode.equals("true")) { useFiles = true; }
    	doit.echo("");
    	doit.echo("-----------------------------------------");
    	doit.echo("-----------------------------------------");
    	doit.echo("-----------------------------------------");
    	doit.echo("");
    	doit.echo(otsikko);
    	doit.echo("");
    	Enumeration vKeys = KattavuusTaulukko.keys();
    	int test = 0, notest = 0, error = 0, failure = 0, ok = 0;
    	String testi = "", status = "";
    	//notest
    	while (vKeys.hasMoreElements()) {
    		testi = vKeys.nextElement().toString();
    		status = KattavuusTaulukko.getProperty(testi);
    		if (status == KATTAVUUSNOTEST) { notest++; }
    		if (status == KATTAVUUSNOTEST && ! KattavuusRaporttiHiljaa) { doit.echo("Ei testia kohteelle " + testi); }
    	}
    	//ok
    	vKeys = KattavuusTaulukko.keys();
    	while (vKeys.hasMoreElements()) {
    		testi = vKeys.nextElement().toString();
    		status = KattavuusTaulukko.getProperty(testi);
    		if (status == KATTAVUUSOK) { ok++; }
    		if (status == KATTAVUUSOK && ! KattavuusRaporttiHiljaa) { doit.echo("Testi onnistui kohteelle " + testi); }
    	}
    	//test
    	vKeys = KattavuusTaulukko.keys();
    	while (vKeys.hasMoreElements()) {
    		testi = vKeys.nextElement().toString();
    		status = KattavuusTaulukko.getProperty(testi);
    		if (status == KATTAVUUSTEST) { test++; }
    		if (status == KATTAVUUSTEST && ! KattavuusRaporttiHiljaa) { doit.echo("Testi on luotu kohteelle " + testi); }
    	}
    	//error
    	vKeys = KattavuusTaulukko.keys();
    	while (vKeys.hasMoreElements()) {
    		testi = vKeys.nextElement().toString();
    		status = KattavuusTaulukko.getProperty(testi);
    		if (status == KATTAVUUSERROR) { error++; }
    		if (status == KATTAVUUSERROR && ! KattavuusRaporttiHiljaa) { doit.echo("Testi kaatui kohteelle " + testi); }
    	}
    	//failure
    	vKeys = KattavuusTaulukko.keys();
    	while (vKeys.hasMoreElements()) {
    		testi = vKeys.nextElement().toString();
    		status = KattavuusTaulukko.getProperty(testi);
    		if (status == KATTAVUUSFAILURE) { failure++; }
    		if (status == KATTAVUUSFAILURE && ! KattavuusRaporttiHiljaa) { doit.echo("Testi ei toteutunut kohteelle " + testi); }
    	}
    	// kirjoita raportti
    	int tavoite = KattavuusTaulukko.size() - 1;
    	if (tavoite == 0) { tavoite = 1; }
    	double coverage = doit.roundTwoDecimals(100.0 * ok / tavoite);

    	if (! KattavuusRaporttiHiljaa)
    	{
    		doit.echo("");
    		doit.echo("-----------------------------------------");
    		doit.echo("");
                doit.echo(otsikko);
                doit.echo("");
    		doit.echo(                   "Testikohteita       " + (KattavuusTaulukko.size() - 1));
    		doit.echo(                   "Testi puuttuu       " + notest);
    		if (test > 0)    { doit.echo("Testi on toteutettu " + test); }
    		if (error > 0)   { doit.echo("Testi kaatui        " + error); }
    		if (failure > 0) { doit.echo("Testi ei toteutunut " + failure); }
    		doit.echo(                   "Testi onnistui      " + ok);
    		doit.echo(                   "------------------------");
    		doit.echo(                   "Kattavuus           " + coverage + "%");
    		doit.echo("");
    		doit.echo("-----------------------------------------");
    		doit.echo("");
    	}

    	if (useFiles) {
    		/////////////////////////////////////////////
    		// TALLETETAAN NUMEROT RAPORTOINTIA VARTEN //
    		String testiajo = KattavuusTaulukko.getProperty(KATTAVUUSKOHDE);
    		String yyyymm = doit.yyyymmString();
    		String ddhhmmss = doit.ddhhmmssString();
    		String info = testiajo + " " + yyyymm + " " + ddhhmmss + " " + tavoite + " " + ok;
    		String fileName = System.getProperty("user.home") + "/kattavuus/kattavuus.db.txt";
    		if (testiajo == null || testiajo.length() == 0) { int a = 1 / 0; }
    		doit.appendToFile(fileName, info);
    		// KERATAAN SUMMA ONNISTUNEIDEN LUKEMISTA ERI AJOISTA SAMALTA KUUKAUDELTA
    		String fileName2 = System.getProperty("user.home") + "/kattavuus/" + testiajo + "." + yyyymm + ".txt";
    		String summa = doit.readFile(fileName2);
    		String         ls = System.getProperty("line.separator");
    		String[] summarivi = summa.split(ls);
    		Properties okKohteet = new Properties();
    		for (int i = 0; i < summarivi.length; i++) {
    			if (summarivi.length < 5) { continue; }
    			String rivi = summarivi[i];
    			String[] items = rivi.split(" ");
    			String kohde = items[0];
    			String kohdeYyyymm = items[1];
    			String tapaus = items[3];
    			if (kohde.equals(testiajo) && kohdeYyyymm.equals(yyyymm))
    			{
    				okKohteet.setProperty(tapaus, KATTAVUUSOK);
    			}
    		}
    		int ok2 = okKohteet.size();
    		info = testiajo + " " + yyyymm + " " + ddhhmmss + " " + tavoite + " " + ok2;
    		doit.appendToFile(fileName, info);
    		//
    		updateReport(fileName);
    	}
    	doit.echo("SUCCESSFUL testReport");
    }

    private static Properties reportTavoite = new Properties();
    private static Properties reportOk = new Properties();
    private static Properties reportHeight = new Properties();
    private void updateReport(String fileName) throws IOException
    {
    	SVTUtils doit = new SVTUtils();
    	String htmlFileName = fileName + ".html";
    	// backup
    	// FileUtils.copyFile(new File(htmlFileName), new File(htmlFileName + "." + doit.ddhhmmssString() + ".backup"));
    	//            Files.copy(new File(htmlFileName), new File(htmlFileName + "." + doit.ddhhmmssString() + ".backup"));
    	doit.writeToFile(htmlFileName, "");
    	//
    	reportItems(fileName);
    	Enumeration mKeys = reportTavoite.keys();
    	List<String> list = Collections.list(mKeys);
    	Collections.sort(list);

    	String currentModuli = "";
    	String line = "";
    	int maxTavoite = 0;
    	int countMonth = 0;
    	//    int maxCountMonth = 0;

    	line = "<html><head><title>TESTAUKSEN KATTAVUUSRAPORTTI</title></head><body>";
    	doit.appendToFile(htmlFileName, line);
    	String dd = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
    	line = "<div style='height:100px;width:1000px'><div><center><h1>TESTAUKSEN KATTAVUUSRAPORTTI " + dd + "</h1></center></div>";
    	doit.appendToFile(htmlFileName, line);
    	for (String rKey : list) {
    		String[] items = rKey.split(":");
    		String moduli = items[0];
    		String yyyymm = items[1];
    		if (! currentModuli.equals(moduli))
    		{
    			if (currentModuli.length() > 0)
    			{
    				line = "</table></div>";
    				doit.appendToFile(htmlFileName, line);
    				diagram(currentModuli, htmlFileName, maxTavoite, countMonth);
    				reportHeight.setProperty(currentModuli, countMonth + "");
    				//                  if (maxCountMonth < countMonth) { maxCountMonth = countMonth; }
    				//                          line = "</div>";
    				//                  this.appendToFile(htmlFileName, line);
    				//                          line = "maxTavoite=" + maxTavoite + " countMonth=" + countMonth;
    				//                  this.appendToFile(htmlFileName, line);
    				countMonth = 0;
    				maxTavoite = 0;
    			}
    			line = moduli;
    			currentModuli = moduli;
    			doit.appendToFile(htmlFileName, "<div style='position:relative;left:20px'><h2>" + moduli + "</h2></div>");
    			doit.appendToFile(htmlFileName, "<div style='position:relative;left:20px;height:%HEIGHT:" + currentModuli
    					+ "%;width:270px;float:left;'><table border='1'>");
    			line = "<tr><td><b>Kuukausi</b></td><td><b>OK</b></td><td><b>Tavoite</b></td></tr>";
    			doit.appendToFile(htmlFileName, line);
    		}
    		countMonth++;
    		String tavoite = reportTavoite.getProperty(rKey);
    		String ok = reportOk.getProperty(rKey);
    		line = "<tr><td>" + yyyymm + "</td><td>" + ok + "</td><td>" + tavoite + "</td></tr>";
    		doit.appendToFile(htmlFileName, line);
    		if (maxTavoite < Integer.parseInt(tavoite)) { maxTavoite = Integer.parseInt(tavoite); }
    	}
    	line = "</table></div>";
    	doit.appendToFile(htmlFileName, line);
    	diagram(currentModuli, htmlFileName, maxTavoite, countMonth);
    	reportHeight.setProperty(currentModuli, countMonth + "");
    	line = "</div></div></body></html>";
    	doit.appendToFile(htmlFileName, line);
    	// replace %HEIGHT:moduli% with CountMonth:moduli
    	Enumeration vKeys = reportHeight.keys();
    	String moduli = "";
    	while (vKeys.hasMoreElements()) {
    		moduli = vKeys.nextElement().toString();
    		String kuukausiLkm = reportHeight.getProperty(moduli);
    		int height = Integer.parseInt(kuukausiLkm) * 40;
    		if (height < 120) { height = 120; }

    		// FileUtils.copyFile(new File(htmlFileName), new File(htmlFileName + "." + doit.ddhhmmssString() + ".height.backup"));
    		//            Files.copy(new File(htmlFileName), new File(htmlFileName + "." + doit.ddhhmmssString() + ".height.backup"));
    		String tokenHtml = doit.readFile(htmlFileName);
    		line = tokenHtml.replace("%HEIGHT:" + moduli +"%", height + "");
    		doit.writeToFile(htmlFileName, line);
    	}
        FileUtils.copyFile(new File(fileName), new File("target/kattavuus.db.txt"));
    	// Artifact
    	FileUtils.copyFile(new File(htmlFileName), new File("target/kattavuus.db.txt.html"));
    	//            Files.copy(new File(htmlFileName), new File("target/kattavuus.db.txt.html"));
    }

    private void diagram(String currentModuli, String htmlFileName, int maxTavoite, int countMonth)
    {
    	SVTUtils doit = new SVTUtils();
    	Enumeration mKeys = reportTavoite.keys();
    	List<String> list = Collections.list(mKeys);
    	Collections.sort(list);

    	String line = "";
    	line = "<div style='height:%HEIGHT:" + currentModuli + "%'>";
    	doit.appendToFile(htmlFileName, line);
    	String svgStart = "<svg width='100%' viewBox='0 0 700 120' xmlns='http://www.w3.org/2000/svg' version='1.1'>";
    	doit.appendToFile(htmlFileName, "<div style='height:120;left:20px;width:700px;float:left;'>" + svgStart);

    	int monthsInRow = 0;

    	// Data
    	int x = 90;
    	for (String rKey : list) {
    		String[] items = rKey.split(":");
    		String moduli = items[0];
    		String yyyymm = items[1];
    		if (currentModuli.equals(moduli))
    		{
    			int tavoite = Integer.parseInt(reportTavoite.getProperty(rKey));
    			int ok = Integer.parseInt(reportOk.getProperty(rKey));

    			int tavoite_x =  100 * tavoite / maxTavoite;
    			int ok_x =  100 * ok / maxTavoite;
    			if (ok > 0 && ok_x < 5) { ok_x = 5; }

    			monthsInRow++;
    			if (monthsInRow > 7)
    			{
    				coordinates(htmlFileName, countMonth, maxTavoite);
    				// end svg, start svg
    				line = "</svg></div><div style='height:140;left:20px;width:700px;float:left;'>" + svgStart;
    				doit.appendToFile(htmlFileName, line);
    				monthsInRow = 0;
    				x = 90;
    			}

    			line = "<rect x='" + x + "' y='" + (100 - tavoite_x) + "' width='90' height='" + tavoite_x + "' style='fill:orange;stroke-width:1;stroke:orange' />";
    			doit.appendToFile(htmlFileName, line);                  // rgb(0,0,0)
    			line = "<rect x='" + x + "' y='" + (100 - ok_x) + "' width='90' height='" + ok_x + "' style='fill:purple;stroke-width:1;stroke:purple' />";
    			doit.appendToFile(htmlFileName, line);

    			// print month
    			String myyyy = yyyymm.substring(4) + "/" + yyyymm.substring(0, 4);
    			if (myyyy.startsWith("0")) { myyyy = myyyy.substring(1); }
    			line = "<text x='" + (x + 5) + "' y='115' fill='grey' font-family='Verdana' font-size='10'>" + myyyy + "</text>";
    			doit.appendToFile(htmlFileName, line);

    			x = x + 90;

    		}
    	}

    	coordinates(htmlFileName, countMonth, maxTavoite);

    	line = "</svg></div></div>";
    	doit.appendToFile(htmlFileName, line);
    }

    private void coordinates(String htmlFileName, int countMonth, int maxTavoite)
    {
    	SVTUtils doit = new SVTUtils();
    	int maxY = 90 * 8;
    	int monthY = (90 + (countMonth * 90));
    	if (monthY > maxY) { monthY = maxY; }

    	String line = "<line x1='90' y1='0' x2='90' y2='100' style='stroke:grey;stroke-width:1'/>";
    	doit.appendToFile(htmlFileName, line);
    	line = "<line x1='90' y1='100' x2='" + monthY + "' y2='100' style='stroke:grey;stroke-width:1'/>";
    	doit.appendToFile(htmlFileName, line);
    	line = "<line x1='" + monthY + "' y1='0' x2='" + monthY + "' y2='100' style='stroke:grey;stroke-width:1'/>";
    	doit.appendToFile(htmlFileName, line);
    	line = "<line x1='90' y1='1' x2='" + monthY + "' y2='1' style='stroke:grey;stroke-width:1'/>";
    	doit.appendToFile(htmlFileName, line);

    	line = "<text x='20' y='100' fill='grey' font-family='Verdana' font-size='10'>0</text>";
    	doit.appendToFile(htmlFileName, line);
    	line = "<text x='20' y='10' fill='grey' font-family='Verdana' font-size='10'>" + maxTavoite + "</text>";
    	doit.appendToFile(htmlFileName, line);
    }

    public void reportItems(String logFile) throws IOException
    {
    	try {
    		BufferedReader br;
    		br = new BufferedReader(new FileReader(logFile));
    		String line = "";
    		while ((line = br.readLine()) != null)
    		{
    			String[] items = line.split(" ");
    			String moduli = items[0];
    			String yyyymm = items[1];
    			int tavoite = Integer.parseInt(items[3]);
    			int ok = Integer.parseInt(items[4]);

    			String key = moduli + ":" + yyyymm;
    			if (reportTavoite.getProperty(key) == null)
    			{
    				reportTavoite.setProperty(key, tavoite + "");
    				reportOk.setProperty(key, ok + "");
    			}
    			else
    			{
    				int oldTavoite = Integer.parseInt(reportTavoite.getProperty(key));
    				if (oldTavoite < tavoite) { reportTavoite.setProperty(key, tavoite + ""); }
    				int oldOk = Integer.parseInt(reportOk.getProperty(key));
    				if (oldOk < ok) { reportOk.setProperty(key, ok + ""); }
    			}
    		}
    		br.close();
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    		throw new FileNotFoundException("ERROR: Kattavuustiedostoa (" + logFile + ") ei loydy.");
    	} catch (IOException e) {
    		e.printStackTrace();
    		throw new IOException("ERROR: Kattavuustiedostoa (" + logFile + ") ei voi lukea.");
    	}
    }

}
