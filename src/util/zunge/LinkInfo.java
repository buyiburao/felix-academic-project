package util.zunge;

public class LinkInfo {
	public static DictInfo di;
	public static int[][] inLink;
	public static int[] outLinkCount;
	
	public static void init(DictInfo di){
		LinkInfo.di = di;
		inLink = new int[DictInfo.ccptCount][];
		outLinkCount = new int[DictInfo.ccptCount];
	}
	
	
}
