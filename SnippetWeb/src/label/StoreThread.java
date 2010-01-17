package label;

import search.snippet.MysqlDriver;

public class StoreThread extends Thread {

	private String query;
	
	private String url;
	
	private String sentence;
	
	private String user;
	
	private int rank;

	private MysqlDriver driver;

	public StoreThread(MysqlDriver driver, String query, String url, String sentence, String user,
			int rank) {
		super();
		this.driver = driver;
		this.query = query;
		this.url = url;
		this.sentence = sentence;
		this.user = user;
		this.rank = rank;
	}

	public void run() {
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			
		}
		
		try {
	        driver.connect();
	        driver.insertTraining(query, url, sentence, user, rank);
	        driver.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
	        System.out.println(query);
	        System.out.println(url);
	        System.out.println(sentence);
	        System.out.println(user);
	        System.out.println(rank);
		}
	}
	
}
