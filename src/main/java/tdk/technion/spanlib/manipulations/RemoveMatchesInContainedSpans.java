package tdk.technion.spanlib.manipulations;

import tdk.technion.spanlib.Span;
import tdk.technion.spanlib.SpanManipulation;
import tdk.technion.spanlib.SpannedDocument;

import java.util.List;

/**
 * Created by omishali on 12/09/2017.
 * For spans s1 s2, if s1 containedIn s2 && s2 has any matches, then remove all matches
 * from s1.
 */
public class RemoveMatchesInContainedSpans implements SpanManipulation {
    @Override
    public void manipulate(SpannedDocument doc) {
        for (Span s1 : doc.getAllSpans()) {
            if (s1.getTags().size() == 0)
                continue;


            List<Span> containingSpans = doc.getContainingSpans(s1);
            for (Span s2 : containingSpans) {
                if (s2.getTags().size() != 0) {
                    s1.clearTags();
                    break;
                }
            }
        }
    }
}
