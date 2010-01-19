package util.zunge;

public interface IGraphReader {
	public int ccpts();
	
	public String getCcpt(int id);
	public int getId(String ccpt);
	
	public int getInLinkCount(int id);
	public int[] getInLink(int id);
	public int getOutLinkCount(int id);
}
