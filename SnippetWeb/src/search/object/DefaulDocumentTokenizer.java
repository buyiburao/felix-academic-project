package search.object;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DefaulDocumentTokenizer extends DocumentTokenizer
{
    static Set<Character> charSet;
    
    public DefaulDocumentTokenizer()
    {
        if (charSet == null)
        {
            charSet = new HashSet<Character>();
            char[] set = new char[]{
                    '.', '!', '?'};
            for (int i = 0; i < set.length; i++)
            {
                charSet.add(set[i]);
            }
        }
    }
    
    @Override
    public List<Sentence> tokenize(Document doc)
    {               
        String string = doc.getString();
        List<Sentence> sentences = new LinkedList<Sentence>();
        for (int start = 0, current = 0; start < string.length(); current++)
        {
            if (charSet.contains(string.charAt(current)) || current == string.length() - 1)
            {
                Sentence sentence = new Sentence(string.substring(start, current + 1).trim());
                sentence.setDoc(doc);
                sentences.add(sentence);
                start = current + 1;
            }
        }
        
        // filter length less than 5 and more thatn 249
        SentenceFilter filter = new LengthSentenceFilter();
        return filter.filter(sentences);
    }
    
    public static void main(String[] args)
    {
    	/*
        String test = "A nightmare, also known as a \"bad dream\", is an unpleasant" +
        		" dream. Nightmares cause strong unpleasant emotional responses from" +
        		" the sleeper, typically fear or horror. The dream may contain " +
        		"situation(s) of danger, discomfort, or psychological or physical" +
        		" distress. Such dreams can be related to physical causes such as a" +
        		" high fever; in an uncomfortable or awkward position; stress or " +
        		"post-traumatic experiences. Sometidocmes there may not readily be an " +
        		"explanation. Sleepers may be woken in a state of distress, and be unable " +
        		"to get back to sleep for some time. Eating before bed, which triggers an " +
        		"increase in the body's metabolism and brain activity, is another potential " +
        		"stimulus for nightmares..[1] The term \"nightmare\" refers to what was called " +
        		"Sleep Paralysis in the 19th century and earlier. Occasional nightmares are " +
        		"commonplace, but recurrent nightmares can interfere with sleep and may cause " +
        		"people to seek medical help. A recently proposed treatment consists of imagery " +
        		"rehearsal.[2] This approach appears to reduce the effects of nightmares " +
        		"and other symptoms in acute stress disorder and post-traumatic stress " +
        		"disorder.[3]";
        */
//        Document doc = new Document(test);
//        List<Sentence> sentences = doc.getSentences();
//        for (Sentence s : sentences)
//        {
//            System.out.println(s.getString());
////            for(Term t : s.getTerms())
////            {
////                System.out.println(t.getString() + "\t" + t.getNormalized());
////            }
//        }
        
//        System.out.println(doc.getOccur("or"));
//        System.out.println(doc.getSentences().get(0).getOccur("also"));
//        System.out.println(doc.getSentences().get(0).getOccur("bad"));
//        System.out.println(doc.getSentences().get(0).getOccur("dream"));
//        System.out.println(doc.getSentences().get(0).getOccur("a"));
    }
}
