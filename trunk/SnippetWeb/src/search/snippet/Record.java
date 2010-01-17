package search.snippet;

public class Record
{
    private String query;
    private String url;
    private String title;
    private String gsnippet;
    
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

    public String getTitle()
    {
        return title;
    }
    public String getGsnippet()
    {
        return gsnippet;
    }

    
    public Record(String query, String title, String url, 
            String gsnippet)
    {
        super();
        this.query = query;
        this.url = url;
        this.title = title;
        this.gsnippet = gsnippet;
    }
    
    public Record()
    {
    }
    
    @Override
    public String toString()
    {
        return String.format("%s\n%s\n%s\n%s\n", query, title, url, gsnippet);
    }
    
    
}
