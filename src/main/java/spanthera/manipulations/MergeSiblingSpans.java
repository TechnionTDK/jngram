package spanthera.manipulations;

import org.apache.commons.collections4.CollectionUtils;
import spanthera.NgramDocument;
import spanthera.Ngram;
import spanthera.NgramDocumentManipulation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by omishali on 07/09/2017.
 */
public class MergeSiblingSpans implements NgramDocumentManipulation {

    private Map<Ngram,List<String>> tagsToBeRemoved = new HashMap<>();

    public void manipulate(NgramDocument doc) {
        int spanOffset = doc.getMinimalNgramSize();
        int maximalSpanSize = doc.getMaximalNgramSize();
        // merge spans with size minimalSpanSize
        while (spanOffset < maximalSpanSize) {
            for (int start = 0; start < doc.length() - spanOffset; start++) {
                int end = start + spanOffset - 1;
                Ngram curr = doc.getNgram(start, end);
                Ngram next = doc.getNgram(start + 1, end + 1);
                List<String> intersection = getIntersection(curr, next);
                if (intersection.size() > 0) {
                    Ngram parent = doc.getNgram(start, end + 1);
                    parent.addTags(intersection);
                    tagsToBeRemoved.put(curr, intersection);
                    tagsToBeRemoved.put(next, intersection);
                }
            }
            removeTags();
            spanOffset++;
        }
    }

    private void removeTags() {
        for (Map.Entry<Ngram, List<String>> entry : tagsToBeRemoved.entrySet()) {
            Ngram s = entry.getKey();
            List<String> tags = entry.getValue();
            s.removeTags(tags);
        }
        tagsToBeRemoved.clear();
    }

    List<String> getIntersection(Ngram s1, Ngram s2) {
        return (List<String>) CollectionUtils.intersection(s1.getTags(), s2.getTags());
    }
}
