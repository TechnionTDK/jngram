package apps.bavlimekorot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import jngram.Ngram;
import jngram.NgramTagger;
import org.apache.lucene.document.Document;

public class BavliTagger implements NgramTagger {

    private JbsBavliIndex index = new JbsBavliIndex();
    private int candidateSize;

    public BavliTagger() {}

    public BavliTagger(int candidateSize) {
        this.candidateSize = candidateSize;
    }

    public List<String> tag(Ngram s) {
        String text = s.getTextFormatted();
        if (text.split("\\s+").length == 1)
            return new ArrayList();

        List<Integer> maxEdits = this.getMaxEdits(s);
        List<Document> docs = this.index.searchFuzzyInText(text, maxEdits);

        Set<String> result = new HashSet();
        for (Document doc : docs)
            result.add(doc.get("uri"));

        return new ArrayList(result);

    }

    List<Integer> getMaxEdits(Ngram ngram) {
        List<Integer> maxEdits = new ArrayList();
        String[] words = ngram.getTextFormatted().split("\\s+");

        for(int i = 0; i < words.length; ++i) {
            if (words[i].length() == 1) {
                maxEdits.add(0);
            } else if (words[i].length() <= 4) {
                maxEdits.add(1);
            } else {
                maxEdits.add(2);
            }
        }

        return maxEdits;
    }

    public boolean isCandidate(Ngram ngram) {
        return ngram.size() == candidateSize;
    }
}
