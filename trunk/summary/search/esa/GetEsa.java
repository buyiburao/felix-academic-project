package search.esa;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class GetEsa
{
    public static String server = "http://192.168.4.2:8080/esa/esa";

    public static EsaInfo getEsa(String query)
    {
        EsaInfo ret = new EsaInfo();
        ret.query = query;
        String[] frags = query.split(" ");
        String urlQuery = null;
        for (int i = 0; i < frags.length; ++i)
        {
            if (urlQuery == null)
            {
                urlQuery = frags[i];
            } else
            {
                urlQuery += "%20" + frags[i];
            }
        }
        try
        {
            URL url = new URL(server + "?q=" + urlQuery);
            BufferedReader br = new BufferedReader(new InputStreamReader(url
                    .openStream()));
            read(ret, br);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return ret;
    }

    public static void read(EsaInfo ei, BufferedReader br)
    {
        try
        {
            br.readLine();
            String line = br.readLine();
            ei.hits = Integer.parseInt(line.split(" ")[2]);
            // System.out.println(ei.hits);
            if (ei.hits > 0)
            {
                ei.ids = new int[ei.hits];
                ei.ccpts = new String[ei.hits];
                for (int i = 0; i < ei.hits; ++i)
                {
                    ei.ids[i] = -1;
                    ei.ccpts[i] = null;
                }
                for (int i = 0; i < ei.hits; ++i)
                {
                    line = br.readLine();
                    if (line != null && line.length() > 0)
                    {
                        // System.out.println("#"+line+"#");
                        int rb = line.indexOf("]");
                        int rc = line.lastIndexOf(":");
                        if (rb > 0 && rc > rb)
                        {
                            ei.ids[i] = Integer.parseInt(line.substring(1, rb));
                            ei.ccpts[i] = line.substring(rb + 1, rc);
                            // System.out.println(ei.ids[i]+" "+ei.ccpts[i]);
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void main(String args[])
    {
        EsaInfo ei = GetEsa.getEsa("Apple Inc.");
        for (int i = 0; i < ei.ccpts.length; ++i)
        {
            System.out.println(ei.ccpts[i]);
        }
    }
}
