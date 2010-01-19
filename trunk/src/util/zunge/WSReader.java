package util.zunge;

public class WSReader {
	static boolean opened = false;
	static IGraphReader igr;
	
	public WSReader(String folder){
		if (opened == false || igr == null){
			igr = new SimpleGraphReader(folder);
			opened = true;
		}
	}
	
	public int ccpts(){
		return igr.ccpts();
	}
	public String getCcpt(int id){
		return igr.getCcpt(id);
	}
	public int getId(String ccpt){
		return igr.getId(ccpt);
	}
	
	public int getInLinkCount(int id){
		return igr.getInLinkCount(id);
	}
	public int[] getInLink(int id){
		return igr.getInLink(id);
	}
	public int getOutLinkCount(int id){
		return igr.getOutLinkCount(id);
	}
}
