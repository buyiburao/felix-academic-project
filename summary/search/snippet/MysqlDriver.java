package search.snippet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MysqlDriver
{
    private String connectionToken;
    private Statement statement;
    public MysqlDriver(String host, int port, String username, String password)
    {
        connectionToken = String.format("jdbc:mysql://%s:%d/snippet?user=%s&password=%s", host, port, username, password);
    }
    
    public void connect() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException
    {
        Class.forName("com.mysql.jdbc.Driver");
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        
        Connection con = DriverManager.getConnection(connectionToken);
        statement = con.createStatement();
    }
    
    public List<Record> query(String q)
    {
        try
        {
        String commit = String.format("select * from snippet_record where query = '%s'", convert(q));
        ResultSet rs = statement.executeQuery(commit);
        List<Record> ret = new LinkedList<Record>();
        while(rs.next())
        {
            Record r = new Record();
            r.setQuery(rs.getString(1));
            r.setTitle(rs.getString(2));
            r.setUrl(rs.getString(3));
            r.setGsnippet(rs.getString(4));
            r.setCmpsnippet(rs.getString(5));
            r.setMysnippet(rs.getString(6));
            ret.add(r);
        }
        return ret;
        }
        catch (SQLException e)
        {
            return new LinkedList<Record>();
        }
    }
    
    
    
    public boolean createTable() throws SQLException
    {
        String query = "create table snippet_record " +
                        "(query varchar(100) not null, " +
                        "title varchar(200), " +
                        "url varchar(300) not null," +
                        "gsnippet varchar(1000), " +
                        "cmpsnippet varchar(1000)," +
                        "mysnippet varchar(1000), " +
                        "primary key(query,url));";
        int state = statement.executeUpdate(query);

        query = "create table page " +
        "(url varchar(1000) not null, " +
        "pagecontent varchar(40000), " +
        "primary key(url));";
        state = statement.executeUpdate(query);
        
        return state != 0;
    }
    
    
    public boolean dropTable() throws SQLException
    {
        String query = "drop table snippet_record;";
        int state = statement.executeUpdate(query);
        
        query = "drop table page;";
        state = statement.executeUpdate(query);
        return state != 0;
    }
    
    public boolean insertPage(String url, String pagecontent)
    {
        String commit = String.format("insert into page (url, pagecontent) values ('%s', '%s')", convert(url), convert(pagecontent));
        try
        {
            statement.executeUpdate(commit);
        } catch (SQLException e)
        {
//            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public String getPage(String url)
    {
        String commit = String.format("select pagecontent from page where url = '%s'", url);
        try
        {
            ResultSet set = statement.executeQuery(commit);
            set.next();
            return set.getString("pagecontent");
        } catch (SQLException e)
        {
            e.printStackTrace();
            return "";
        }
    }
    
    private String convert(String str)
    {
        return str == null ? "" : str.replaceAll("'", "''");
    }
    
    public boolean insertRecord(String query, String title, String url, String gsnippet, String cmpsnippet, String mysnippet) throws SQLException
    {
        String commit = String.format("insert into snippet_record (query, title, url, gsnippet, " +
        		"cmpsnippet, mysnippet) values ('%s', '%s', '%s', '%s', '%s', '%s');", 
        		convert(query), convert(title),convert(url),convert(gsnippet), convert(cmpsnippet), 
        		convert(mysnippet));
        
        try
        {
            statement.executeUpdate(commit);
        } catch (SQLException e)
        {
            return false;
        }
        return true;
    }
    
    public boolean insertRecord(Record record) throws SQLException
    {
        return insertRecord(convert(record.getQuery()), convert(record.getTitle()), 
                            convert(record.getUrl()), convert(record.getGsnippet()), 
                            convert(record.getCmpsnippet()), convert(record.getMysnippet()));
    }
    
    public boolean deleteRecord(String query, String url) throws SQLException
    {
        String commit = String.format("delete from snippet_record where query = '%s' and url = '%s'", convert(query), convert(url));
        try
        {
            statement.executeUpdate(commit);
        } catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public Set<String> getQuerySet()
    {
        
        Set<String> querySet = new HashSet<String>();
        try
        {
            String commit = String.format("select distinct s.query from snippet_record s");
            ResultSet rs = statement.executeQuery(commit);
            while (rs.next())
            {
                querySet.add(rs.getString(1));
            }
            
            return querySet;
        } catch (SQLException e)
        {
            return new HashSet<String>();
        }
    }
    
    public boolean deleteQuery(String query) throws SQLException
    {
        String commit = String.format("delete from snippet_record where query = '%s'", convert(query));
        try
        {
            statement.executeUpdate(commit);
        } catch (SQLException e)
        {
            return false;
        }
        return true;
    }
    
    public void clear()
    {
        try
        {
            dropTable();
            createTable();
        } catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException
    {
        MysqlDriver driver = new MysqlDriver("localhost", 3306, "root", "apex");
        driver.connect();
//        driver.dropTable();
//        driver.createTable();
//        driver.clear();
//        driver.insertRecord("\"1", "2", "2", "4", "5", "6");
//        driver.insertRecord("2", "2", "2", "4", "5", "6");
//        driver.insertRecord("3", "2", "2", "4", "5", "6");
//        driver.insertRecord("5", "2", "7", "4", null, "6");
//        driver.deleteRecord("1", "3");
//        driver.deleteQuery("1");
//        Set<String> querySet = driver.getQuerySet();
//        for (String s : querySet)
//        {
//            System.out.println(s);
//        }
        
//        Set<String> qset = driver.getQuerycreateTableSet();
//        System.out.println(qset.size());
        
    }
    
}
