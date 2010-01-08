package websearch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class WebService {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static  String get(String u) throws Exception{
		URL url = new URL(u);
		URLConnection connection = url.openConnection();

		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while((line = reader.readLine()) != null) {
		 builder.append(line);
		}
		return builder.toString();
	}
}
