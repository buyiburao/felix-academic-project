package websearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Integrate
{
    public static void main(String[] args)
    {
        for (int i = 0; i < 100; i++)
        {
            System.out.println("Start query " + i);
           try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("query.data/" + i)));
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream("query.data/" + i + ".filled")));
            
            String line = null; 
            int count = 0;
            while ((line = reader.readLine()) != null)
            {
                String title = reader.readLine();
                String snippet = reader.readLine();
                String url = reader.readLine();
                String body = reader.readLine();
                
                
                int url_number = i * 24 + ++count;
                
                try
                {
                    BufferedReader ureader = new BufferedReader(new InputStreamReader(
                            new FileInputStream("data/pages/" + url_number + ".text")));
                    body = ureader.readLine();
                    writer.write(String.format("##########%d##########\n", count));
                    writer.write(title + "\n");
                    writer.write(snippet + "\n");
                    writer.write(url + "\n");
                    writer.write(body + "\n");
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            
            writer.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        }
    }
}
