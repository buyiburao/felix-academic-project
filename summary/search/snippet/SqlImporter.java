package search.snippet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


import search.esa.ConceptVector;
import search.esa.EsaInfo;
import search.esa.GetEsa;
import search.object.Document;
import search.object.Sentence;

public class SqlImporter
{
    
    static class ConceptFetcher extends Thread
    {
        private String sentence;
        private String url;
//        private Map<String, String> translationMap;
        public ConceptFetcher(String url, String sentence/*, Map<String, String> translationMap*/)
        {
            this.sentence = sentence;
            this.url = url;
//            this.translationMap = translationMap;
        }
        
        public void run()
        {
            try
            {
                EsaInfo ei = GetEsa.getEsa(sentence);
                ConceptVector vector = new ConceptVector(ei);
//                ConceptVector vector = driver2.getConceptVector(sentence);
                    driver.insertConcept(sentence, vector);
//                String translation = Translate.execute(sentence, Language.ENGLISH, Language.CHINESE_SIMPLIFIED);
//                String translation = translationMap.get(sentence);
//                if (translationMap != null)
//                    driver.insertTranslation(url, sentence, translation);
            } catch (Exception e1)
            {
                // TODO Auto-generated catch block
                System.out.println(sentence);
                e1.printStackTrace();
            }
            // Thread.sleep(30);
        }
    }

     private static MysqlDriver driver = new MysqlDriver("192.168.3.19", 3306, "monty", "something");
//    private static MysqlDriver driver2 = new MysqlDriver();
    
    public void importFile(String file, String dir) throws FileNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException
    {
//        Translate.setHttpReferrer("www.sina.com.cn");
        driver.connect();
//        driver2.connect();
        driver.createAllTable();
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
                if (i % 5 == 0) continue;
                System.out.println("Start query " + i + ": " + query);
                BufferedReader rreader = new BufferedReader(new InputStreamReader(new FileInputStream(dir + i)));
                @SuppressWarnings("unused")
                String line = null;
                int docCount = 0;
                EsaInfo ei = GetEsa.getEsa(query);
                ConceptVector vector = new ConceptVector(ei);
                    driver.insertConcept(query, vector);
                
                while((line = rreader.readLine()) != null && docCount < 10)
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
                        
//                        Map<String, String> transMap = driver2.getTranslation(url);
                        for(Sentence s : doc.getSentences())
                        {
                            Set<String> sentenceSet = new HashSet<String>();
                            String sentenceString = s.getString();
                            if (sentenceSet.contains(sentenceString)) continue;
                            sentenceSet.add(sentenceString);
                            
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
//                            pool.execute(new ConceptFetcher(url, sentenceString, transMap));
                            pool.execute(new ConceptFetcher(url, sentenceString));
                        }
                        driver.insertPage(url, page);
                        driver.insertRecord(query, title, url, gsnippet, i % 5 != 0/*training or test 4:1*/);
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
//        Translate.setHttpReferrer("www.apexlab.org");
        importer.importFile("query", "query.data");
        
    }
}
