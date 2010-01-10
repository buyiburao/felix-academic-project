package websearch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class WebService
{

    private static final String googleCachedCommand= "http://74.125.153.132/search?q=cache:%s"; 
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception
    {
        System.out.println(getGoogleCached("http://therapists.psychologytoday.com/rms/zip/48360.html"));
    }

    public static String get(String u) throws Exception
    {
        URL url = new URL(u);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-agent","Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.6) Gecko/20091216 Fedora/3.5.6-1.fc11 Firefox/3.5.6 GTB6");
        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        while ((line = reader.readLine()) != null)
        {
            builder.append(line);
        }
        return builder.toString();
    }

    public static String getGoogleCached(String u) throws Exception
    {
        return get(String.format(googleCachedCommand, u));
    }
}
