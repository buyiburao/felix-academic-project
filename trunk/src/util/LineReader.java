package util;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;


public class LineReader {
	BufferedReader reader;
	String currentLine;
	public LineReader(String fileName) throws Exception{
		this(new FileInputStream(fileName));
	}
	public LineReader(InputStream stream) throws Exception{
		reader = new BufferedReader(new InputStreamReader(stream));
		currentLine = reader.readLine();
	}
	public boolean hasNext()
	{
		return currentLine != null;
	}
	public String next() throws Exception
	{
		String ret = currentLine;
		currentLine = reader.readLine();
		return ret;
	}
	public void close() throws Exception
	{
		reader.close();
	}
	public String readToEnd() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		while(hasNext()){
			builder.append(next());
			builder.append("\n");
		}
		close();
		return builder.toString();
	}
}
