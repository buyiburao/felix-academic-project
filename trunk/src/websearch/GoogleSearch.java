package websearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

class Record extends Thread
{
    public String getUrl()
    {
        return url;
    }

    public String getSnippet()
    {
        return googleSnippet;
    }

    public String getTitle()
    {
        return title;
    }

    public String getBody()
    {
        return body;
    }

    String url;
    String googleSnippet;
    String title;
    String body;
    
    public Record(String url, String snippet, String title)
    {
        this.url = url;
        this.googleSnippet = snippet;
        this.title = title;
        this.body = null;
    }
    
    public void run()
    {
        HtmlParserDriver driver = new HtmlParserDriver();
        body = driver.getBodyText(url).replaceAll("\\s+", " ");
    }
}


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

    public int size() 
    {
        return num;
    }

    private String get(int i, String field) 
    {
        try 
        {
            int a = i/8;
            int b = i-a*8;
            Object o = res[a].get(b);
            if (o != null) 
            {
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
        return get(i, "title")/*.replaceAll("<b>", "").replaceAll("</b>", "")*/;
    }

    public String snippet(int i) 
    {
        if (i >= num)
            return "";
        return get(i, "content")/*.replaceAll("<b>", "").replaceAll("</b>", "")*/;
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
    
    
        
    public static void submitQuery(String query, String file) throws IOException
    {
        GoogleSearch bs = new GoogleSearch(query, 120);
       
        Record[] records = new Record[bs.size()];
        for (int i = 0; i < records.length; i++) 
        {
            String url = bs.url(i);
            String snippet = bs.snippet(i);
            String title = bs.title(i);
            records[i] = new Record(url, snippet, title);
        }
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(bs.size());
        ThreadPoolExecutor pool = new ThreadPoolExecutor(150, 200, 30, TimeUnit.SECONDS, queue);
        
        for (int i = 0; i < bs.size(); i++)
        {
            pool.execute(records[i]);
        }
        
        while(pool.getCompletedTaskCount() < 90)
        {
            try
            {
                Thread.sleep(5000);
//                System.out.println(pool.getCompletedTaskCount());
            } catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println(pool.getCompletedTaskCount());
        }
        
        try
        {
            Thread.sleep(6000);
        } catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        pool.shutdown();
        System.out.println("outa");
        
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file))); 
        for (int i = 0; i < records.length; i++)
        {
            Record record = records[i];
            if (record.getBody() == null)
                continue;
//                System.out.println(i + ":" + record.getBody());
            writer.write(String.format("##########%d##########\n", i));
            writer.write(record.getTitle() + "\n");
            writer.write(record.getSnippet() + "\n");
            writer.write(record.getUrl() + "\n");
            writer.write(record.getBody() + "\n");
        }
        writer.close();
    }
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception 
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
        int i = 0;
        String line = null;
        while((line = reader.readLine()) != null)
        {
            String file = "query.data/" + i;
            File f1 = new File(file);
            File f2 = new File("query.data/" + ++i);
            
            if (!f1.exists() || !f2.exists())
            {
                System.out.println("Processing " + file);
                submitQuery(line, file);
            }
        }
    }

}
