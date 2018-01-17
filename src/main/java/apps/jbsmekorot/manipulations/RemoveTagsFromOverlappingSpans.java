package apps.jbsmekorot.manipulations;

import apps.jbsmekorot.PsukimTagger;
import spanthera.Span;
import spanthera.SpanManipulation;
import spanthera.SpannedDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omishali on 08/01/2018.
 * For each span s1 (that has tags) we all its overlapping spans.
 * If an overlapping span s2 has tags then we  remove all tags from either s1 or s2.
 * Current strategy: remove tags from smaller span. If they are equal: keep tags in both.
 */
    public class RemoveTagsFromOverlappingSpans implements SpanManipulation {
        @Override
        public void manipulate(SpannedDocument doc) {
            for (Span s1 : doc.getAllSpans()) {
                if (s1.hasNoTags())
                    continue;

                List<Span> overlappingSpans = doc.getOverlappingSpans(s1);
                for (Span s2 : overlappingSpans) {
                    if (s2.hasNoTags())
                        continue;
                    // remove tags from smaller span
                    if (s1.size() < s2.size())
                        s1.clearTags();
                    else if (s2.size() < s1.size())
                        s2.clearTags();
                }
            }
        }
}
