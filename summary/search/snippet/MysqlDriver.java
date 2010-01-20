package search.snippet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import search.esa.Concept;
import search.esa.ConceptVector;

public class MysqlDriver
{
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException
    {
        MysqlDriver driver = new MysqlDriver("localhost", 3306, "root", "apex");
        driver.connect();
        driver.createAllTable();
//        driver.dropTable();
//        driver.createTable();
//        driver.clear();
//        driver.insertRecord("\"1", "2", "2", "4", "5", "6");
//        driver.insertRecord("2", "2", "2", "4", "5", "6");
//        driver.insertRecord("3", "2", "2", "4", "5", "6");
//        driver.insertRecord("5", "2", "7", "4", null, "6");
//        driver.deleteRecord("1", "3");dropTable();
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
        for(String translate : driver.getTranslation("http://www.taxi.com/").values())
        {
            System.out.println(translate);
        }
          
    }
    private String connectionToken;
    private Statement statement;
    private Connection connection;
    
    public MysqlDriver(String host, int port, String username, String password)
    {
        connectionToken = String.format("jdbc:mysql://%s:%d/snippet2?user=%s&password=%s&useUnicode=true&characterEncoding=UTF8", host, port, username, password);
    }
    
    public MysqlDriver()
    {
        this("192.168.3.19", 3306, "monty", "something");
    }
    

    public void clear()
    {
        
    }
    
    public void connect() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException
    {
        Class.forName("com.mysql.jdbc.Driver");
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        
        connection = DriverManager.getConnection(connectionToken);
        statement = connection.createStatement();
    }
    
    private String convert(String str)
    {
        return str == null ? "" : str.replaceAll("'", "''");
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
        
        try
        {
            createTranslationTable();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void createConceptTable() throws SQLException
    {
        String query = "create table concept " +
        "(text varchar(280) not null, " +
        "concept_string varchar(200), " +
        "concept_id int not null, " +
        "concept_weight double not null, " +
        "primary key(text, concept_id));";
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

    private void createTranslationTable() throws SQLException
    {
        String query = "create table translation " +
        "(url varchar(500) not null, " +
        "sentence varchar(280) not null, " +
        "translation varchar(1000) charset utf8 not null, " +
        "primary key(url, sentence));";
        statement.executeUpdate(query);
    }
    
    public void dropTable()
    {
//        String query = "drop table snippet_record;";
//        statement.executeUpdate(query);
//        
//        query = "drop table page;";
//        statement.executeUpdate(query    
    }
    
    public void disconnect()
    {
        try
        {
            connection.close();
        } catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public ConceptVector getConceptVector(String text)
    {
        List<Concept> concepts = new ArrayList<Concept>();
        String commit = String.format("select * from concept where text = '%s'"
                , convert(text));
        ResultSet set = null;
        try
        {
            set = statement.executeQuery(commit);
            while(set.next())
            {
                Concept concept = new Concept(set.getInt("concept_id"), set.getString("concept_string"), set.getDouble("concept_weight"));
                concepts.add(concept);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return new ConceptVector(concepts);
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

    public Map<String, String> getTranslation(String url)
    {
        Map<String, String> toRet = new HashMap<String, String>();
        try
        {
            String commit = String.format("select * from translation where url = '%s'", convert(url));
            ResultSet rs = statement.executeQuery(commit);
            while (rs.next())
            {
                try
                {
                    String oString = rs.getString("sentence");
                    String tString = rs.getString("translation");
                    if (oString != null && tString != null)
                    {
                        toRet.put(oString, tString);
                    }
                }
                catch (Exception e)
                {
                    System.out.println("Wrong getting translation!");
                }
            }
            
        } catch (SQLException e)
        {
        }
        return toRet;
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
            System.out.println(toRet.size());
            return toRet;
        }
        catch (SQLException e)
        {
            return toRet;
        }
    }

    public int[] getTesting(String query, String url)
    {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        String commit = String.format("select * from training where query = '%s' " +
        		"and url = '%s';",
        		convert(query), convert(url));
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
    
    public Map<String, Double> getTraining(String query, String url)
    {
        Map<String, Double> toRet = new HashMap<String, Double>();
        
        Map<String, List<Integer>> data = new HashMap<String, List<Integer>>();
        
        String commit = String.format("select * from training where query = '%s' and url = '%s'",
                convert(query), convert(url));
        
        try
        {
            ResultSet set = statement.executeQuery(commit);
            while(set.next())
            {
                String sentence = set.getString("sentence");
                List<Integer> list = null;
                if(data.containsKey(sentence))
                {
                    list = data.get(sentence);
                }
                else
                {
                    list = new ArrayList<Integer>();
                    data.put(sentence, list);
                }
                
                list.add(set.getInt("rank"));
            }
            
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        SentenceRankScorer scorer = new SentenceAverageScorer();
        for(String sentence: data.keySet())
        {
            toRet.put(sentence, scorer.getScore(data.get(sentence)));
        }
        return toRet;
    }
    
//    public int getLabeledNumber(String url)
//    {
//        String commit = String.format("select distinct url from training", convert(url));
//        
//        try
//        {
//            ResultSet set = statement.executeQuery(commit);
//            while(set.next())
//            {
//                String sentence = set.getString("sentence");
//                List<Integer> list = null;
//                if(data.containsKey(sentence))
//                {
//                    list = data.get(sentence);
//                }
//                else
//                {
//                    list = new ArrayList<Integer>();
//                    data.put(sentence, list);
//                }
//                
//                list.add(set.getInt("rank"));
//            }
//            
//        } catch (SQLException e)
//        {
//            e.printStackTrace();
//        }
//        
//        SentenceRankScorer scorer = new SimpleRankScorer();
//        for(String sentence: data.keySet())
//        {
//            toRet.put(sentence, scorer.getScore(data.get(sentence)));
//        }
//        return toRet;
//    }
    
    public boolean isTrainingLabeled(String query, String url, String user)
    {
        String commit = String.format("select * from training where query = '%s' and url = '%s' and user = '%s'",
                convert(query), convert(url), convert(user));
        
        try
        {
            ResultSet set = statement.executeQuery(commit);
            return set.next();
            
            
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean insertConcept(String text, ConceptVector vector)
    {
        String format = "insert into concept (text, concept_string, concept_id, concept_weight) " +
        		"values ('%s', '%s', %d, %f)";
        
        for(Concept concept : vector.getVectorMap().values())
        {
            String commit = String.format(format, convert(text), convert(concept.getConcept()), concept.getId(), concept.getWeight());
            try
            {
                statement.executeUpdate(commit);
            } catch (SQLException e)
            {
            }
        }
        return true;
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
    
    public boolean insertTestRecord(Record record) throws SQLException
    {
        return insertTestRecord(convert(record.getQuery()), convert(record.getTitle()), 
                            convert(record.getUrl()), convert(record.getGsnippet()));
    }

    public boolean insertTestRecord(String query, String title, String url, String gsnippet)
    {
        return insertRecord(query, title, url, gsnippet, false);
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

    public boolean insertTrainingRecord(Record record) throws SQLException
    {
        return insertTrainingRecord(convert(record.getQuery()), convert(record.getTitle()), 
                            convert(record.getUrl()), convert(record.getGsnippet()));
    }
    
    public boolean insertTrainingRecord(String query, String title, String url, String gsnippet)
    {
        return insertRecord(query, title, url, gsnippet, true);
    }
    
    public boolean insertTranslation(String url, String sentence, String translation)
    {
        String commit = String.format("insert into translation (url, sentence, translation) values ('%s', '%s', '%s');", 
                convert(url), convert(sentence), convert(translation));
        try
        {
            statement.executeUpdate(commit);
        } catch (SQLException e)
        {
            System.out.println("url:   " + url);
            System.out.println(sentence);
            System.out.println(translation);
            return false;
        }
        return true;
    }
    
}
