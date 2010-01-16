package search.snippet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import search.esa.ConceptVector;
import search.esa.EsaInfo;
import search.esa.GetEsa;
import search.object.Document;
import search.object.Sentence;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class SqlImporter
{
    
    static class ConceptFetcher extends Thread
    {
        private String sentence;
        private String url;
        public ConceptFetcher(String url, String sentence)
        {
            this.sentence = sentence;
            this.url = url;
        }
        
        public void run()
        {
            try
            {
                EsaInfo ei = GetEsa.getEsa(sentence);
                ConceptVector vector = new ConceptVector(ei);
                driver.insertConcept(sentence, vector);
                String translation = Translate.execute(sentence, Language.ENGLISH, Language.CHINESE_SIMPLIFIED);
                driver.insertTranslation(url, sentence, translation);
            } catch (Exception e1)
            {
                // TODO Auto-generated catch block
                System.out.println(sentence);
                e1.printStackTrace();
            }
            // Thread.sleep(30);
        }
    }

    // private MysqlDriver driver = new MysqlDriver("192.168.3.19", 3306, "monty", "something");
    private static MysqlDriver driver = new MysqlDriver("localhost", 3306, "root", "apex");
    
    public void importFile(String file, String dir) throws FileNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException
    {
//        Translate.setHttpReferrer("www.sina.com.cn");
        driver.connect();
//        driver.createTable();
//        driver.clear();
        
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(100);
        ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 100, 30, TimeUnit.SECONDS, queue);

        if (!dir.endsWith("/"))
            dir += "/";
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dir + file)));
        String query = null;
        try
        {
            int cc = 0;
            for (int i = 0; (query = reader.readLine()) != null; i++)
            {
                System.out.println("Start query " + i + ": " + query);
                BufferedReader rreader = new BufferedReader(new InputStreamReader(new FileInputStream(dir + i)));
                @SuppressWarnings("unused")
                String line = null;
                int docCount = 0;
                EsaInfo ei = GetEsa.getEsa(query);
                ConceptVector vector = new ConceptVector(ei);
                driver.insertConcept(query, vector);
                
                while((line = rreader.readLine()) != null && docCount < 20)
                {
                    String title = rreader.readLine();
                    String gsnippet = rreader.readLine();
                    String url = rreader.readLine();
                    String page = rreader.readLine();
                    
                    page = page.replaceAll("&([a-zA-Z]+|#[0-9]+);", "").replaceAll("\\s+", " ");
                    if (page.length() > 249)
                    {
                        Document doc = new Document(page);
                        if (doc.getSentences().size() < 10)
                        {
                            continue;
                        }
                        for(Sentence s : doc.getSentences())
                        {
                            String sentence = s.getString();
                            while (pool.getQueue().size() > 10)
                            {
                                try
                                {
                                    Thread.sleep(30);
                                } catch (InterruptedException e)
                                {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                            // System.out.println("a");
                            pool.execute(new ConceptFetcher(url, sentence));
                        }
                        driver.insertPage(url, page);
                        driver.insertRecord(query, title, url, gsnippet, i % 5 != 0/*training or test 1:4*/);
                        docCount++;
                        if (++cc % 5 == 0)
                        {
                            System.out.println(new Date().toString() + "\t" + cc + " docs finished.");
                        }
                    }
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } 
    }
    
    public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException
    {
        SqlImporter importer = new SqlImporter();
        Translate.setHttpReferrer("www.apexlab.org");
        importer.importFile("query", "query.data");
        
    }
}
