package search.snippet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
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
    


    private void createTrainingSnippetTable() throws SQLException
    {
        String query = "create table training_snippet" +
        "(query varchar(100) not null, " +
        "title varchar(200), " +
        "url varchar(300) not null," +
        "gsnippet varchar(1000), " +
        "primary key(query,url));";
        statement.executeUpdate(query);
    }
    
    private void createTestSnippetTable() throws SQLException
    {
        String query = "create table test_snippet " +
        "(query varchar(100) not null, " +
        "title varchar(200), " +
        "url varchar(300) not null," +
        "gsnippet varchar(1000), " +
        "primary key(query,url));";
        statement.executeUpdate(query);
    }
    
    private void createPageTable() throws SQLException
    {
        String query = "create table page " +
        "(url varchar(1000) not null, " +
        "pagecontent varchar(40000), " +
        "primary key(url));";
        statement.executeUpdate(query);
    }
    
    private void createConceptTable() throws SQLException
    {
        String query = "create table concept" +
        "(id int not null auto_increment," +
        "sentence varchar(500) not null, " +
        "concept varchar(500), " +
        "primary key(id));";
        statement.executeUpdate(query);
    }
    
    private void createTrainingTable() throws SQLException
    {
        String query = "create table training " +
        "(id int not null auto_increment," +
        "query varchar(1000) not null, " +
        "url varchar(1000) not null, " +
        "sentence varchar(300) not null, " +
        "user varchar(30) not null, " +
        "rank int not null, " +
        "primary key(id));";
        statement.executeUpdate(query);
    }
    
    private void createTestTable() throws SQLException
    {
        String query = "create table testing " +
        "(id int not null auto_increment," +
        "query varchar(1000) not null, " +
        "url varchar(1000) not null, " +
        "judege_base1_1 int not null, " +
        "judege_base1_2 int not null, " +
        "judege_base1_3 int not null, " +
        "judege_base2_1 int not null, " +
        "judege_base2_2 int not null, " +
        "judege_base2_3 int not null, " +
        "judege_our_1 int not null, " +
        "judege_our_2 int not null, " +
        "judege_our_3 int not null, " +
        "user varchar(30) not null, " +
        "primary key(id));";
        statement.executeUpdate(query);
    }
    
    public void createAllTable()
    {
        try
        {
            createTrainingSnippetTable();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        try
        {
            createTestSnippetTable();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        try
        {
            createPageTable();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            createConceptTable();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        try
        {
            createTrainingTable();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            createTestTable();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void dropTable()
    {
//        String query = "drop table snippet_record;";
//        statement.executeUpdate(query);
//        
//        query = "drop table page;";
//        statement.executeUpdate(query    
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
//      }
            return false;
        }
        return true;
    }
    
    public boolean insertConcept(String sentence, String concept)
    {
        String commit = String.format("insert into concept (sentence, concept) " + "values ('%s', '%s')",
                convert(sentence), convert(concept));
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
    
    public Set<String> getConcept(String sentence)
    {
        Set<String> concept = new HashSet<String>();
        String commit = String.format("select * from concept where sentence = '%s'", convert(sentence));
        ResultSet set = null;
        try
        {
            set = statement.executeQuery(commit);
            while(set.next())
            {
                concept.add(set.getString("concept"));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
        return concept;
    }
    
    public boolean insertTraining(String query, String url, String sentence, String user, int rank)
    {
        String commit = String.format("insert into training(query, url, sentence, user, rank) values ('%s', '%s', '%s', '%s', %d)", 
                convert(query), convert(url), convert(sentence), convert(user), rank);
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
    
    public boolean insertTest(String query, String url, String user, int[] scores)
    {
        String commit = String.format("insert into training" +
        		"(query, " +
        		"url, " +
        		"judge_base1_1, " +
        		"judge_base1_2, " +
        		"judge_base1_3, " +
        		"judge_base1_1, " +
        		"judge_base1_2, " +
        		"judge_base1_3, " +
        		"judge_our_1, " +
        		"judge_our_2, " +
        		"judge_our_3," +
        		"user) valuse ('%s', '%s', %d, %d, %d, %d, %d, %d, %d, %d, %d, '%s');",
        		
                convert(query), convert(url), 
                scores[0],
                scores[1], 
                scores[2], 
                scores[3], 
                scores[4], 
                scores[5], 
                scores[6], 
                scores[7], 
                scores[8], 
                convert(user)
                );
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
    
    public int[][] getTest(String query, String url)
    {
        int [][] ret = new int[100][];
        
        String commit = String.format("select * from testing where query = '%s' and url = '%s'", convert(query), convert(url));
        ResultSet set = null;
        try
        {
            set = statement.executeQuery(commit);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
        
        int count = 0;
        try
        {
            while(set.next())
            {
                int[] add = new int[9];
                for (int i = 0; i < 9; i++)
                {
                    try
                    {
                        add[i] = set.getInt(i + 3);
                    } catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                }
                ret[count++] = add;
            }
            
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        int[][] temp = new int[count][];
        for (int i = 0; i < count; i++)
        {
            temp[i] = ret[i];
        }
        return temp;
    }

    public int[] getRank(String query, String url, String sentence, String user)
    {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        String commit = String.format("select * from training where query = '%s' " +
        		"and url = '%s' and sentence = '%s' and user = '%s'",
        		convert(query), convert(url), convert(sentence), convert(user));
        ResultSet set = null;
        try
        {
            set = statement.executeQuery(commit);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return new int[0];
        }
        
        try
        {
            while(set.next())
            {
                int rank = 0;
                rank = set.getInt("rank"); 
                ret.add(rank);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        int[] toRet = new int[ret.size()];
        for(int i = 0; i < ret.size(); i++)
        {
            toRet[i] = ret.get(i);
        }
        
        return toRet;
    }
    
    public String getPage(String url)
    {
        String commit = String.format("select pagecontent from page where url = '%s'", url);
        try
        {
            ResultSet set = statement.executeQuery(commit);
            set.next();
            return set.getString("pagecontent");
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
            return "";
        }
    }
    
    private String convert(String str)
    {
        return str == null ? "" : str.replaceAll("'", "''");
    }
    
    public boolean insertRecord(String query, String title, String url, String gsnippet, boolean isTraining)
    {
        String table = isTraining ? "training" : "test";
        String commit = String.format("insert into %s_snippet (query, title, url, gsnippet) values ('%s', '%s', '%s', '%s');", 
        		table, convert(query), convert(title),convert(url),convert(gsnippet));
        try
        {
            statement.executeUpdate(commit);
        } catch (SQLException e)
        {
            return false;
        }
        return true;
    }
    
    public boolean insertTrainingRecord(String query, String title, String url, String gsnippet)
    {
        return insertRecord(query, title, url, gsnippet, true);
    }
    
    public boolean insertTestRecord(String query, String title, String url, String gsnippet)
    {
        return insertRecord(query, title, url, gsnippet, false);
    }
    
    public boolean insertTrainingRecord(Record record) throws SQLException
    {
        return insertTrainingRecord(convert(record.getQuery()), convert(record.getTitle()), 
                            convert(record.getUrl()), convert(record.getGsnippet()));
    }
    
    public boolean insertTestRecord(Record record) throws SQLException
    {
        return insertTestRecord(convert(record.getQuery()), convert(record.getTitle()), 
                            convert(record.getUrl()), convert(record.getGsnippet()));
    }

    public List<Record> getRecord(String query, boolean isTraining)
    {
        List<Record> toRet = new ArrayList<Record>();
        String table = isTraining ? "training" : "test";
        try
        {
            String commit = String.format("select * from %s_snippet where query = '%s'", table, convert(query));
            ResultSet rs = statement.executeQuery(commit);
            while(rs.next())
            {
                Record r = new Record();
                r.setQuery(query);
                r.setTitle(rs.getString("title"));
                r.setUrl(rs.getString("url"));
                r.setGsnippet(rs.getString("gsnippet"));
                toRet.add(r);
            }
            return toRet;
        }
        catch (SQLException e)
        {
            return toRet;
        }
    }
 
    public Set<String> getQuerySet(boolean isTraining)
    {
        Set<String> querySet = new HashSet<String>();
        String table = isTraining ? "training" : "test";
        try
        {
            String commit = String.format("select distinct s.query from %s_snippet s", table);
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

    public void clear()
    {
        
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
//        driver.deleteRecord("1", "3");dropTable();
        driver.createAllTable();
//        driver.deleteQuery("1");
//        Set<String> querySet = driver.getQuerySet();
//        for (String s : querySet)
//        {
//            System.out.println(s);
//        }
        
//        for(String concept :driver.getConcept("What AR stands for?"))
//        {
//            System.out.println(concept);
//        }
        
//        Set<String> qset = driver.getQuerycreateTableSet();
//        System.out.println(qset.size());
    }
    
}
