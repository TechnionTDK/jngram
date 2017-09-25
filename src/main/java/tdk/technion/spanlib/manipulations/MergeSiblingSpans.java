package tdk.technion.spanlib.manipulations;

import org.apache.commons.collections4.CollectionUtils;
import spanlib.Span;
import spanlib.SpanManipulation;
import spanlib.SpannedDocument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by omishali on 07/09/2017.
 */
public class MergeSiblingSpans implements SpanManipulation {

    private Map<Span,List<String>> tagsToBeRemoved = new HashMap<>();

    public void manipulate(SpannedDocument doc) {
        int spanOffset = doc.getMinimalSpanSize();
        int maximalSpanSize = doc.getMaximalSpanSize();
        // merge spans with size minimalSpanSize
        while (spanOffset < maximalSpanSize) {
            for (int start = 0; start < doc.length() - spanOffset; start++) {
                int end = start + spanOffset - 1;
                Span curr = doc.getSpan(start, end);
                Span next = doc.getSpan(start + 1, end + 1);
                List<String> intersection = getIntersection(curr, next);
                if (intersection.size() > 0) {
                    Span parent = doc.getSpan(start, end + 1);
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
        for (Map.Entry<Span, List<String>> entry : tagsToBeRemoved.entrySet()) {
            Span s = entry.getKey();
            List<String> tags = entry.getValue();
            s.removeTags(tags);
        }
        tagsToBeRemoved.clear();
    }

    List<String> getIntersection(Span s1, Span s2) {
        return (List<String>) CollectionUtils.intersection(s1.getTags(), s2.getTags());
    }
}
