package org.apexlab.docserver;

import java.io.File;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.FSDirectory;

public class WikiDocProvider extends UnicastRemoteObject implements IWikiDocProvider {

	private IndexReader reader;
	private Searcher searcher;

	public WikiDocProvider(String path) throws Exception {
		reader = IndexReader.open(FSDirectory.open(new File(path)));
		searcher = new IndexSearcher(reader);
	}

	@Override
	public String getDocContentByTitle(String title) throws RemoteException {
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
		String testStr = wdp.getDocContentByTitle("Python (programming language)");
		if (testStr.length() > 100)
			testStr = testStr.substring(0, 101);
		System.err.println(testStr);
		System.err.println();
		LocateRegistry.createRegistry(18983);
		Naming.bind("rmi://localhost:18983/wiki", wdp);
		System.err.println("server started");
	}

}
