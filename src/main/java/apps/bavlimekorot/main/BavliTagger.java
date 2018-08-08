package apps.bavlimekorot.main;

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

    public BavliTagger() {}

    public List<String> tag(Ngram s) {
        String text = s.getTextFormatted();
        if (text.split("\\s+").length == 1) {
            return new ArrayList();
        } else {
            List<Integer> maxEdits = this.getMaxEdits(s);
            List<Document> docs2 = new ArrayList();
            List<Document> docs1 = this.index.searchFuzzyInText(text, maxEdits);
            if (s.getStringExtra("adnut_text") != null) {
                docs2 = this.index.searchFuzzyInText(s.getStringExtra("adnut_text"), maxEdits);
            }

            Set<String> result = new HashSet();
            Iterator var7 = docs1.iterator();

            Document doc;
            while(var7.hasNext()) {
                doc = (Document)var7.next();
                result.add(doc.get("uri"));
            }

            var7 = ((List)docs2).iterator();

            while(var7.hasNext()) {
                doc = (Document)var7.next();
                result.add(doc.get("uri"));
            }

            return new ArrayList(result);
        }
    }

    List<Integer> getMaxEdits(Ngram s) {
        List<Integer> maxEdits = new ArrayList();
        String[] words = s.getTextFormatted().split("\\s+");

        for(int i = 0; i < words.length; ++i) {
            if (words[i].length() <= 4) {
                maxEdits.add(1);
            } else {
                maxEdits.add(2);
            }
        }

        return maxEdits;
    }

    public boolean isCandidate(Ngram s) {
        return true;
    }
}
