package websearch;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;

public class HtmlParserDriver
{

    public HtmlParserDriver()
    {
    }
    
    
    private void extractText(TagNode node, StringBuilder builder)
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

    public String getBodyText(String url)
    {
        String html = null;
        
        try
        {
            Parser parser = new Parser();
            html = WebService.get(url);
            StringBuilder builder = new StringBuilder();
            parser.setInputHTML(html);
            NodeList list = parser.parse(null);
            
            for (Node node: list.toNodeArray())
            {
                if (node instanceof TagNode)
                {
                     extractText((TagNode)node, builder);
                }
            }
            
//            System.out.println(builder.toString());
//            while(iter.hasMoreNodes())
//            {
//                i++;
//                Node node = iter.nextNode();
//                System.out.println(node.toString());
//                if (node instanceof TextNode)
//                    builder.append(((TextNode)node).getText());
//            }
            
//            System.out.println(i);
            
            return builder.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }
    
    public static void main(String[] args)
    {
        HtmlParserDriver pDriver = new HtmlParserDriver();
        String result = pDriver.getBodyText("http://welcome.hp.com/country/nz/en/welcome.html#Product");
        System.out.println(result.replaceAll("\\s+", " "));
    }
}
