package org.apexlab.docserver;

import java.io.File;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.store.FSDirectory;

public class WikiDocProvider extends java.rmi.server.UnicastRemoteObject implements IWikiDocProvider {

	private IndexReader reader;

	public WikiDocProvider(String path) throws Exception {
		reader = IndexReader.open(FSDirectory.open(new File(path)));
	}

	@Override
	public String getDocContentByTitle(String title) throws RemoteException {
		System.out.println("Query:" + title);
		try {
			TermDocs td = reader.termDocs(new Term("title", title));
			if (td.next()) {
				return reader.document(td.doc()).getField("content")
						.stringValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	public static void main(String[] args) throws Exception{
		WikiDocProvider wdp = new WikiDocProvider(args[0]);
		LocateRegistry.createRegistry(1099);
		Naming.rebind("rmi://10.1.134.147/wiki", wdp);
//		IWikiDocProvider provider = (IWikiDocProvider)Naming.lookup(Config.serverString);
//		String testStr = provider.getDocContentByTitle("Python (programming language)");
//		if (testStr.length() > 100)
//			testStr = testStr.substring(0, 101);
//		System.err.println(testStr);
//		System.err.println();		
		System.err.println("server started");
	}

}
