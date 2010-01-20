package search.object;

public class Token
{
    protected String originalString;
    
    public Token(String string)
    {
        originalString = string;
    }
    
    public String getString()
    {
        return originalString;
    }
    
    @Override
    public String toString()
    {
        return originalString;
    }
    public boolean equals(Object obj){
    	if (obj instanceof Token){
    		Token token = (Token)obj;
    		return token.originalString.equals(this.originalString);
    	}
    	return false;
    }
    
}
