package util.zunge;


public class SimpleGraphReader implements IGraphReader{
	SimpleReader sr;
	
	SimpleGraphReader(String folder){
		sr = new SimpleReader();
		sr.init(folder);
		sr.read();
	}

	@Override
	public String getCcpt(int id) {
		return DictInfo.getCcpt(id);
	}

	@Override
	public int ccpts() {
		return DictInfo.ccpts();
	}

	@Override
	public int getId(String ccpt) {
		return DictInfo.getId(ccpt);
	}

	@Override
	public int[] getInLink(int id) {
		return LinkInfo.inLink[id];
	}

	@Override
	public int getInLinkCount(int id) {
		return LinkInfo.inLink[id].length;
	}

	@Override
	public int getOutLinkCount(int id) {
		return LinkInfo.outLinkCount[id];
	}

	
}
