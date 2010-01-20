package machinelearning.snippet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import search.object.Document;
import search.object.Query;
import search.object.Sentence;
import search.snippet.MysqlDriver;
import search.snippet.Record;

public class QueryDocumentDistributor {
	private int split = 1;
	private int number = 0;
	
	private String queryFile;
	private MysqlDriver driver = new MysqlDriver();
	
	public QueryDocumentDistributor(String queryFile)
	{
		this.queryFile = queryFile;
	}
	
	
	
	public QueryDocumentDistributor(String queryFile, int split, int number) {
		super();
		this.split = split;
		this.number = number;
		this.queryFile = queryFile;
	}



	public void distribute() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException
	{
		driver.connect();
		Properties prop = new Properties();
		prop.setProperty(ConfigConstant.TF_FILE_CONFIG, "d:\\wikitf_partial");
		prop.setProperty(ConfigConstant.DEFAULT_DOC_NUM, "1000000");
		
		SnippetSVMLightInputGenerator gen = new SnippetSVMLightInputGenerator(prop);
		
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(queryFile));
			System.out.println(queryFile);
			String line = null;
			for (int i = 0; (line = reader.readLine()) != null; i++)
			{
				if (i % split == number)
				{
					Query query = new Query(line);
					System.out.println(line);
					for(Record record : driver.getRecord(line, true))
					{
						System.out.print(query.getString() + "\t" + record.getUrl());
						String pageContent = driver.getPage(record.getUrl());
						Document document = new Document(pageContent);
						Map<String, Double> sentenceScoreMap = driver.getTraining(query.getString(), record.getUrl());
						for(Sentence s : document.getSentences()){
							if (sentenceScoreMap.containsKey(s.getString()))
							{
								try {
									gen.addCase(s, query, sentenceScoreMap.get(s.getString()), 0);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						
						BufferedWriter writer = new BufferedWriter(new FileWriter("result-" + number));
						writer.write(gen.dumpToString());
						writer.close();
//						System.out.print(gen.dumpToString());
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args)
	{
		QueryDocumentDistributor distributor = null;
		System.out.println("starts");
		if (args.length != 2)
		{
			distributor = new QueryDocumentDistributor("query.data/query");
		}
		else
		{
			int split = Integer.parseInt(args[0]);
			int number = Integer.parseInt(args[1]);
			distributor = new QueryDocumentDistributor("query.data/query", split, number);
		}
		
		try {
			distributor.distribute();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
