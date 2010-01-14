package search.snippet;

public class Record
{
    private String query;
    private String url;
    private String title;
    private String gsnippet;
    private String mysnippet;
    private String cmpsnippet;
    
    public String getQuery()
    {
        return query;
    }
    public String getUrl()
    {
        return url;
    }
    public void setQuery(String query)
    {
        this.query = query;
    }
    public void setUrl(String url)
    {
        this.url = url;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public void setGsnippet(String gsnippet)
    {
        this.gsnippet = gsnippet;
    }
    public void setMysnippet(String mysnippet)
    {
        this.mysnippet = mysnippet;
    }
    public void setCmpsnippet(String cmpsnippet)
    {
        this.cmpsnippet = cmpsnippet;
    }
    public String getTitle()
    {
        return title;
    }
    public String getGsnippet()
    {
        return gsnippet;
    }
    public String getMysnippet()
    {
        return mysnippet;
    }
    public String getCmpsnippet()
    {
        return cmpsnippet;
    }
    
    public Record(String query, String title, String url, 
            String gsnippet, String cmpsnippet, String mysnippet)
    {
        super();
        this.query = query;
        this.url = url;
        this.title = title;
        this.gsnippet = gsnippet;
        this.cmpsnippet = cmpsnippet;
        this.mysnippet = mysnippet;
    }
    
    public Record()
    {
    }
    
    @Override
    public String toString()
    {
        return String.format("%s\n%s\n%s\n%s\n%s\n%s\n", query, title, url, gsnippet, cmpsnippet, mysnippet);
    }
    
    
}
