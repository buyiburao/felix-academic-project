package search.snippet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class MysqlDriver
{
    private String connectionToken;
    private Statement statement;
    public MysqlDriver(String host, int port, String username, String password)
    {
        //TODO
        connectionToken = String.format("jdbc:mysql://%s:%d/snippet?user=%s&password=%s", host, port, username, password);
    }
    
    public void connect() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException
    {
        //TODO
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
        return state != 0;
    }
    
    public boolean dropTable() throws SQLException
    {
        String query = "drop table snippet_record;";
        
        int state = statement.executeUpdate(query);
        return state != 0;
    }
    
    private String convert(String str)
    {
        return str.replaceAll("\"", "\"\"").replaceAll("'", "''");
    }
    
    public boolean insertRecord(String query, String title, String url, String gsnippet, String cmpsnippet, String mysnippet)
    {
        String commit = String.format("insert into snippet_record (query, title, url, gsnippet, " +
        		"cmpsnippet, mysnippet) values ('%s', '%s', '%s', '%s', '%s', '%s');", 
        		convert(query), convert(title),convert(url),convert(gsnippet), convert(cmpsnippet), convert(mysnippet));
        try
        {
            statement.executeUpdate(commit);
        } catch (SQLException e)
        {
            // TODO Auto-generated catch block
            return false;
        }
        return true;
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
    
    public boolean deleteQuery(String query) throws SQLException
    {
        String commit = String.format("delete from snippet_record where query = '%s'", convert(query));
        try
        {
            statement.executeUpdate(commit);
        } catch (SQLException e)
        {
            // TODO Auto-generated catch block
            return false;
        }
        return true;
    }
    
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException
    {
        MysqlDriver driver = new MysqlDriver("localhost", 3306, "root", "apex");
        driver.connect();
//        driver.dropTable();
//        driver.createTable();
        driver.insertRecord("1", "2", "3", "4", "5", "6");
//        driver.deleteRecord("1", "3");
//        driver.deleteQuery("1");
        List<Record> records = driver.query("1");
        for(Record r : records)
        {
            System.out.println(r.toString());
        }
        
    }
    
}
