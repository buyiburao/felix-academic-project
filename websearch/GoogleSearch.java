package websearch;

import java.net.URLEncoder;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

public class GoogleSearch  implements SearchEngine
{

    private static String GOOGLE_URL = "http://ajax.googleapis.com/ajax/services/search/%s?v=1.0&q=%s&start=%d&hl=en&rsz=large";
    private String query;
    private int num;
    private JSONArray[] res;

    public GoogleSearch(String q, int n) 
    {
        this.query = q;
        this.num = n;
        int s = (num - 1) / 8 + 1;
        res = new JSONArray[s];

        for(int i =0; i < s; i++)
        {
            if(i < 8)
            {
                retry(0, i * 8, i, "web");
            }
            else
            {
                retry(0, (i + 8 - s) * 8, i, "blogs");
            }
        }
    }

    private void retry(int i, int start, int idx, String url) 
    {
        try {
            Thread.sleep(i*1000*60);
            System.out.println(new Date().toString()+" google "+url+" start:"+start+" num:8");
            String context = WebService.get(String.format(GOOGLE_URL, url, URLEncoder
                        .encode(query, "utf8"), start));
            //			System.out.println(context);
            JSONObject json = new JSONObject(context);
            JSONObject response = (JSONObject) json.get("responseData");
            try
            {
                this.res[idx] = (JSONArray) response.get("results");
            }
            catch (Exception e) 
            {
                System.out.println("query down!");
                this.query = query.substring(0, query.indexOf("\" \"")+1); 
                retry(i, start, idx, url);
            }
        } catch (Exception e) 
        {
            e.printStackTrace();

            System.out.println("sleeping "+(i*2+1)+" min");
            retry(i*2+1, start, idx, url);
        }
    }

    public int size() {
        return num;
    }

    private String get(int i, String field) {
        try {
            int a = i/8;
            int b = i-a*8;
            Object o = res[a].get(b);
            if (o != null) {
                JSONObject jo = (JSONObject) o;
                Object ret = jo.get(field);
                if (ret != null)
                    return ret.toString();
            }
        } catch (Exception e) {
        }
        return "";
    }

    public String title(int i) 
    {
        if (i >= num)
            return "";
        return get(i, "title").replaceAll("<b>", "").replaceAll("</b>", "");
    }

    public String snippet(int i) 
    {
        if (i >= num)
            return "";
        return get(i, "content").replaceAll("<b>", "").replaceAll("</b>", "");
    }

    public String url(int i) 
    {
        if (i >= num)
            return "";
        String url = get(i, "url");
        if(url.equals(""))
            url = get(i, "postUrl");
        return url;
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception 
    {
        // TODO Auto-generated method stub
        // String url =
        // "http://api.bing.net/json.aspx?AppId=98B627A4C4949A1D6575D49ADA13CDD04D770EE5&Version=2.2&Market=en-US&Query=%s&Sources=web&Web.Count=%d&JsonType=raw";
        // String context = WebService.get(String.format(url, "abc", 1));
        // JSONObject json = new JSONObject(context);
        // JSONObject res = (JSONObject) json.get("SearchResponse");
        // JSONObject webres = (JSONObject) res.get("Web");
        // JSONArray singles = (JSONArray) webres.get("Results");
        // for (int i = 0; i < singles.length(); i++) {
        // JSONObject single = (JSONObject) singles.get(i);
        // System.out.println(single.get("Description"));
        // System.out.println(single.get("Url"));
        // System.out.println(single.get("DateTime"));
        // System.out.println(single.get("DisplayUrl"));
        // System.out.println(single.get("DeepLinks"));
        // System.out.println(single.get("CacheUrl"));
        // System.out.println(single.get("Title"));
        // }
        GoogleSearch bs = new GoogleSearch("\"Dr. Ruth\" \"last name\"", 100);
        for (int i = 0; i < bs.size(); i++) {
            System.out.println(i);
            System.out.println(bs.title(i));
            System.out.println(bs.snippet(i));
            System.out.println(bs.url(i));
            System.out.println();
        }

        //		System.out.println(WebService.get("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=Paris%20Hilton&start=5&hl=en"));
    }

}
