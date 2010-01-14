package search.snippet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class SqlImporter
{
    private MysqlDriver driver = new MysqlDriver("192.168.3.19", 3306, "monty", "something");
    
    public void importFile(String file, String dir) throws FileNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException
    {
        driver.connect();
        driver.createTable();
//        driver.clear();

        int count = 0;
        if (!dir.endsWith("/"))
            dir += "/";
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dir + file)));
        String query = null;
        try
        {
            for (int i = 0; (query = reader.readLine()) != null; i++)
            {
                System.out.println("Start query " + i + ": " + query);
                BufferedReader rreader = new BufferedReader(new InputStreamReader(new FileInputStream(dir + i)));
                @SuppressWarnings("unused")
                String line = null;
                while((line = rreader.readLine()) != null)
                {
                    String title = rreader.readLine();
                    String gsnippet = rreader.readLine();
                    String url = rreader.readLine();
                    String page = rreader.readLine();
                    
                    page = page.replaceAll("&([a-zA-Z]+|#[0-9]+);", "").replaceAll("\\s+", " ");
                    
                    if (page.length() > 50)
                    {
                        driver.insertRecord(query, title, url, gsnippet, null, null);
                        driver.insertPage(url, page);
                    }
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        
        System.out.println(count);
    }
    public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException
    {
        SqlImporter importer = new SqlImporter();
        importer.importFile("query", "query.data");
    }
}
