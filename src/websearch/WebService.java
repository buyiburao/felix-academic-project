package websearch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class WebService
{

    private static final String googleCachedCommand= "http://74.125.153.132/search?q=cache:%s"; 
    private static String[] userAgents = 
    {
        "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.2) Gecko/20091218 Firefox 3.6b5",
        "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2b4) Gecko/20091124 Firefox/3.6b4",
        "Mozilla/5.0 (Windows; Windows NT 5.1; es-ES; rv:1.9.2a1pre) Gecko/20090402 Firefox/3.6a1pre",
        "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.1b4) Gecko/20090423 Firefox/3.5b4 (.NET CLR 3.5.30729)",
        "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6 (.NET CLR 3.5.30729) FBSMTWB",
        "Mozilla/5.0 (Windows NT 5.1; U; zh-cn; rv:1.8.1) Gecko/20091102 Firefox/3.5.5",
        "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.1.3) Gecko/20090824 Firefox/3.5.3",
        "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1) Gecko/20090624 Firefox/3.1b3;MEGAUPLOAD 1.0 (.NET CLR 3.5.30729)",
        "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.5; ko; rv:1.9.1b2) Gecko/20081201 Firefox/3.1b2",
        "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1b2) Gecko/20081127 Firefox/3.1b1",
        "Mozilla/5.0 (X11; U; Linux i686; ru; rv:1.9b5) Gecko/2008032600 SUSE/2.9.95-25.1 Firefox/3.0b5",
        "Mozilla/5.0 (X11; U; Linux i686; pl-PL; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5",
        "Mozilla/5.0 (X11; U; Linux i686; es-AR; rv:1.9b5) Gecko/2008041514 Firefox/3.0b5",
        "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5",
        "Mozilla/5.0 (X11; U; Linux i686; en-GB; rv:1.9b5) Gecko/2008041514 Firefox/3.0b5",
        "Mozilla/5.0 (X11; U; Linux i686; de; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5"
    };
    
    private static Random ran = new Random();
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception
    {
        System.out.println(WebService.get("http://www.webtender.com/"));
    }

    public static String get(String u) throws Exception
    {
        URL url = new URL(u);
        URLConnection connection = url.openConnection();
//        connection.setRequestProperty("User-agent",userAgents[ran.nextInt(userAgents.length)]);
//        connection.setRequestProperty("referer", String.format("http://%d.%d.%d.%d/", ran.nextInt(230) + 1, ran.nextInt(230), ran.nextInt(230), ran.nextInt(230)));
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
    
//    public static String getWithProxy(String u) throws Exception
//    {
//        URL url = new URL(u);
//        URLConnection connection = url.openConnection();
//        connection.setRequestProperty("User-agent",userAgents[ran.nextInt(userAgents.length)]);
//        connection.setRequestProperty("referer", String.format("http://%d.%d.%d.%d/", ran.nextInt(230) + 1, ran.nextInt(230), ran.nextInt(230), ran.nextInt(230)));
//        String line;
//        StringBuilder builder = new StringBuilder();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(
//                connection.getInputStream()));
//        while ((line = reader.readLine()) != null)
//        {
//            builder.append(line);
//        }
//        return builder.toString();
//    }
//    
   

    public static String getGoogleCached(String u) throws Exception
    {
        return get(String.format(googleCachedCommand, u));
    }
}
