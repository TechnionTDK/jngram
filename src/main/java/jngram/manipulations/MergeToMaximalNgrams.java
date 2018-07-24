package jngram.manipulations;

import org.apache.commons.collections4.CollectionUtils;
import jngram.NgramDocument;
import jngram.Ngram;
import jngram.NgramDocumentManipulation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This manipulation is part of the bottom-up process we are taking. Here we
 * "go up" from bigrams (where tagging occurs) to maximal ngrams where matches exist.
 * If ngrams ng1 and ng2 overlap, and they share the same tags, all shared
 * tags are moved to the parent and removed from them. Eventually, at the end of this
 * process, there should not be overlapping ngrams that share the same tag.
 * Created by omishali on 07/09/2017.
 */
public class MergeToMaximalNgrams extends NgramDocumentManipulation {

    private Map<Ngram,List<String>> tagsToBeRemoved = new HashMap<>();

    @Override
    public void manipulate(NgramDocument doc) {
        int spanOffset = doc.getMinimalNgramSize();
        int maximalSpanSize = doc.getMaximalNgramSize();

        while (spanOffset < maximalSpanSize) {
            for (int start = 0; start < doc.length() - spanOffset; start++) {
                int end = start + spanOffset - 1;
                Ngram curr = doc.getNgram(start, end);
                Ngram next = doc.getNgram(start + 1, end + 1);
                List<String> sharedTags = getSharedTags(curr, next);
                if (sharedTags.size() > 0) {
                    Ngram parent = doc.getNgram(start, end + 1);
                    parent.addTags(sharedTags);
                    tagsToBeRemoved.put(curr, sharedTags);
                    tagsToBeRemoved.put(next, sharedTags);
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

    List<String> getSharedTags(Ngram ng1, Ngram ng2) {
        return (List<String>) CollectionUtils.intersection(ng1.getTags(), ng2.getTags());
    }
}
