package org.apexlab.docserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class WikiIndexer {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		IndexWriter writer = new IndexWriter(FSDirectory.open(new File("wikiindex")),
				new StandardAnalyzer(Version.LUCENE_CURRENT), true,
				IndexWriter.MaxFieldLength.UNLIMITED);
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line;
		while((line = reader.readLine()) != null){
			String[] parts = line.split("\t", 2);
			String title = parts[0];
			String content = parts[1];
			Document doc = new Document();
			doc.add(new Field("title", title, Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field("content", content, Field.Store.YES, Field.Index.NO));
			writer.addDocument(doc);
		}
		writer.close();
	}

}
