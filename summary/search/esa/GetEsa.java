package search.esa;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class GetEsa
{
    public static String server = "http://192.168.4.148:8080/esa/esa";
    private static int limit = 10;

    public static EsaInfo getEsa(String query) throws UnsupportedEncodingException
    {
        EsaInfo ret = new EsaInfo();
        ret.query = query;
        String urlQuery = URLEncoder.encode(query, "utf8");
        try
        {
            URL url = new URL(String.format("%s?q=%s&limit=%d", server, urlQuery, limit));
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
        List<Integer> ids = new ArrayList<Integer>();
        List<Double> weights = new ArrayList<Double>();
        List<String> concepts = new ArrayList<String>();
        
        try
        {
            br.readLine();
            String line = null;
            while((line = br.readLine()) != null)
            {
                if (line.startsWith("["))
                {
                    int index1 = line.indexOf(']');
                    int index2 = line.lastIndexOf(' ');
                    int id = Integer.parseInt(line.substring(1, index1));
                    String concept = line.substring(index1 + 1, index2 - 1);
                    double weight = Double.parseDouble(line.substring(index2 + 1));
                    
                    ids.add(id);
                    concepts.add(concept);
                    weights.add(weight);
                }
            }
            
            ei.ids = ids.toArray(new Integer[0]);
            ei.weights = weights.toArray(new Double[0]);
            ei.concepts = concepts.toArray(new String[0]);
            
            ei.hits = ei.concepts.length;

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void main(String args[]) throws UnsupportedEncodingException
    {
        EsaInfo ei = GetEsa.getEsa("Apple Inc.");
        for (int i = 0; i < ei.hits; ++i)
        {
            System.out.println(ei.concepts[i]);
            System.out.println(ei.weights[i]);
            System.out.println(ei.ids[i]);
        }
    }
}
