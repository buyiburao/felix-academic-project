package label;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import search.snippet.Record;
import search.snippet.SimpleQueryBiasedSentenceScorer;

public class PageInfo {

    private Record record;
    private List<String> sentences;
    private Map<String, String> translation;

    public PageInfo(Record record, List<String> sentences,
            Map<String, String> translation) {
        this.record = record;
        this.sentences = sentences;
        this.translation = translation;
    }

    public Record getRecord() {
        return record;
    }

    public List<String> getSentences() {
        return sentences;
    }

    public Map<String, String> getTranslation() {
        return translation;
    }

    public List<String> getCandidate(int size) {
        if (sentences.size() <= size) {
            return sentences;
        }

        List<SentenceInfo> list = new ArrayList<SentenceInfo>();
        try {
            SimpleQueryBiasedSentenceScorer scorer = new SimpleQueryBiasedSentenceScorer(this.getRecord().getQuery());
            for (String sentence : sentences) {
                double score = scorer.score(sentence);
                list.add(new SentenceInfo(sentence, score));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(list, Collections.reverseOrder());

        List<String> ret = new ArrayList<String>(size);
        for (int i = 0; i < size; ++i) {
            ret.add(list.remove(0).getSentence());
        }

        return ret;
    }
}
