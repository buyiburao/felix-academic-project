package websearch;

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
        if ( tagName.compareToIgnoreCase("a") == 0
                || tagName.compareToIgnoreCase("script") == 0
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
                extractText((TagNode)n, builder);
            }
            else if (n instanceof TextNode)
            {
                String text = ((TextNode)n).getText();
//                if (text.contains("geturl"))
//                {
//                    System.out.println(n.toString());
//                    try{
//                    System.out.print(n.getParent().toString());}
//                    catch(Exception e){}
//                }
                if(text.length() > 0)
                {
                    builder.append(text);
                }
            }
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
                if (((TagNode)node).getTagName().equalsIgnoreCase("body"))
                    extractText((TagNode)node, builder);
            }
        }
        return builder.toString();
    }

    public static String getBodyTextGoogleCached(String url)
    {
        String html = "";
        try
        {
            html = WebService.getGoogleCached(url);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
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
    
    public static void main(String[] args)
    {
        String result = HtmlParserDriver.getBodyText("http://therapists.psychologytoday.com/rms/zip/48360.html");
        System.out.println(result.replaceAll("\\s+", " "));
    }
}
