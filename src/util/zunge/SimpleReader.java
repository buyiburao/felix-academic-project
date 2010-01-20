package util.zunge;

import java.io.File;
import java.io.*;
import java.util.Scanner;


public class SimpleReader {
	public static DictInfo dictInfo;
	public static LinkInfo linkInfo;
	public static RedirectInfo rediInfo;
	public static DisambiguationInfo disaInfo;
	
	String folder;
	String dictFile;
	String linkFile;
	String rediFile;
	String disaFile;
	
	public void init(String folder){
		this.folder = folder;
		dictFile = folder+"dict.zun";
		linkFile = folder+"articleIndexR.zun";
		rediFile = folder+"suggRediR.zun";
		disaFile = folder+"suggDisaR.zun";
		
		dictInfo = new DictInfo();
		linkInfo = new LinkInfo();
		rediInfo = new RedirectInfo();
		disaInfo = new DisambiguationInfo();
	}
	
	public void read(){
		try{
			//Read Dictionary
			System.out.println("Read Dictionary " + dictFile);
			//Scanner sc = new Scanner(new File(dictFile));
			BufferedReader reader = new BufferedReader(new FileReader(dictFile));
			int ccptCount = Integer.parseInt(reader.readLine());
			DictInfo.init(ccptCount);
			for (int i = 0;i < ccptCount;++i){
				String line = "";
				try{
					line = reader.readLine();
				}
				catch(Exception e){
					System.out.println("ERROR " + i);
					System.exit(-1);
				}
				int pos = line.indexOf(" ");
				String ccpt = line.substring(pos+1);
				DictInfo.put(ccpt);
			}
			//sc.close();
			reader.close();
			//Read Link
			System.out.println("Read Link");
			Scanner sc = new Scanner(new File(linkFile));
			LinkInfo.init(dictInfo);
			for (int i = 0;i < ccptCount;++i){
				sc.nextInt();
				LinkInfo.outLinkCount[i] = sc.nextInt();
				int in = sc.nextInt();
				if (in > 0){
					LinkInfo.inLink[i] = new int[in];
					for (int j = 0;j < in;++j){
						LinkInfo.inLink[i][j] = sc.nextInt();
					}
				}
			}
			sc.close();
			/*
			//Read Disambiguation
			System.out.println("Read Disambiguation");
			sc = new Scanner(new File(disaFile));
			DisambiguationInfo.init(dictInfo);
			while(sc.hasNext()){
				String line = sc.nextLine();
				int equal = line.indexOf("=");
				int sep = line.indexOf("###");
				String ccpt = line.substring(equal+1,sep-1);
				int id = DictInfo.getId(ccpt);
				if (id != -1){
					String[] diss = line.substring(sep+3).split(" ### ");
					for (int i = 0;i < diss.length;++i){
						DisambiguationInfo.add(diss[i],id);
					}
				}
			}
			//Read Redirect
			System.out.println("Read Redirect");
			sc = new Scanner(new File(rediFile));
			RedirectInfo.init(dictInfo);
			while(sc.hasNext()){
				String line = sc.nextLine();
				int equal = line.indexOf("=");
				int sep = line.indexOf("###");
				String ccpt = line.substring(equal+1,sep-1);
				int id = DictInfo.getId(ccpt);
				if (id != -1){
					String[] diss = line.substring(sep+3).split(" ### ");
					for (int i = 0;i < diss.length;++i){
						RedirectInfo.add(diss[i],id);
					}
				}
			}
			
			sc.close();
			*/
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
