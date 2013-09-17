package fi.vm.sade.tarjonta.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

public class TestTUtils {

	public void restTestCount(String fileName, String url) throws IOException {
		SVTUtils doit = new SVTUtils();   
		doit.echo(fileName);
		doit.echo("url=" + url);
		String response = "";
		String expected = doit.readFile(fileName);
		response = getJson(url);
		expected = expected.replace("\n", "").replace("\r", "").replace("\t", " ").replace("  ", " "); 
		response = response.replace("\n", "").replace("\r", "").replace("\t", " ").replace("  ", " ");
		String[] list = response.split("}");
		for (int i = 0; i < list.length; i++) {
			String part = list[i];
			if (part.indexOf(".") < 0) { continue; }
			String id = part.substring(part.lastIndexOf(".") +1);
			id = id.replace("\"", "");
			response = response.replace(id, "");
		}
		doit.echo("response without id=" + response);
		Assert.assertEquals("RestApi error", expected, response);
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
}
