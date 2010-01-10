package websearch;

public interface SearchEngine {

	public int size() ;
	
	public String title(int i) ;

	public String snippet(int i);

	public String url(int i) ;
}
