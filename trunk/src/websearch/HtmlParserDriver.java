package websearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class HtmlParserDriver
{

    public HtmlParserDriver()
    {
    }
    
    
    private static void extractText(TagNode node, StringBuilder builder)
    {
        String tagName =node.getTagName();
        if ( tagName.compareToIgnoreCase("script") == 0
                || tagName.compareToIgnoreCase("title") == 0
                || tagName.compareToIgnoreCase("style") == 0
                || tagName.compareToIgnoreCase("head") == 0) 
        {
            return;
        }
        
        NodeList list = node.getChildren();
        if (list != null)
        for (Node n : list.toNodeArray())
        {
            if (n instanceof TagNode)
            {
                TagNode tagNode = (TagNode)n;
                extractText(tagNode, builder);
            }
            else if (n instanceof TextNode)
            {
                String text = ((TextNode)n).getText();
                if(text.length() > 0)
                {
                    builder.append(text);
                    builder.append(" ");
                }
            }
        }
        if (tagName.equalsIgnoreCase("p")
                || tagName.equalsIgnoreCase("div"))
        {
            builder.append(".");
        }
    }
    
    
    public static String getBodyTextByHtml(String html)
    {
        Parser parser = new Parser();
        StringBuilder builder = new StringBuilder();
        NodeList list = null;
        try
        {
            parser.setInputHTML(html);
            list = parser.parse(null);
        } catch (ParserException e)
        {   
            list = new NodeList();
        }
        
        for (Node node: list.toNodeArray())
        {
            if (node instanceof TagNode)
            {
//                System.out.println(node.toString());
//                if (((TagNode)node).getTagName().equalsIgnoreCase("body"))
                    extractText((TagNode)node, builder);
            }
        }
        return builder.toString().replaceAll("&[a-zA-Z0-9]+;", "").replaceAll("( [.])+", ".").replaceAll("\\s+", " ").replaceAll("[.]+", ".");
    }

    public static String getBodyTextGoogleCached(String url) throws Exception
    {
        String html = "";

            html = WebService.getGoogleCached(url);
            
        return getBodyTextByHtml(html);
    }
    
    public static String getBodyText(String url)
    {
        String html = null;
        
        try
        {
            html = WebService.get(url);
//            System.out.println(html);
//            int a = 0/0;
        }
        catch (Exception e)
        {
                html = "";
        }
        
        return getBodyTextByHtml(html);
    }
    
    public static String getBodyTextFile(String fileName) throws IOException
    {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String line = null;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
//                System.out.println(line);
                builder.append(line);
            }
            String html = builder.toString();
            String bodyText = HtmlParserDriver.getBodyTextByHtml(html);
//            System.out.print(bodyText);
            return bodyText;
    }
    
    public static void main(String[] args)
    {
        for (int i = 1; i <= 2400; i++)
        {
            File file = new File("data/pages/"+i);
            if (file.exists())
            {
                try
                {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/pages/" + i + ".text")));
                    writer.write(HtmlParserDriver.getBodyTextFile("data/pages/"+i));
                    writer.close();
                    
                } catch (IOException e)
                {
                    System.out.println("Parse Error " + i);
//                    e.printStackTrace();
                }
            }
            
        }
    }
}
