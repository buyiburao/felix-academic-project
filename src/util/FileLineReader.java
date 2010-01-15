package util;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class FileLineReader {
	BufferedReader reader;
	String currentLine;
	public FileLineReader(String fileName) throws Exception{
		reader = new BufferedReader(new FileReader(fileName));
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
}
