package apps.jbsmekorot.manipulations;

import jngram.NgramDocument;
import jngram.Ngram;
import jngram.NgramDocumentManipulation;

import java.util.List;

/**
 * Created by omishali on 08/01/2018.
 * For each span s1 (that has tags) we get all its overlapping spans.
 * If an overlapping span s2 has tags then we  remove all tags from either s1 or s2.
 * Current strategy: remove tags from smaller span. If they are equal: keep tags in both.
 */
    public class RemoveTagsFromOverlappingSpans implements NgramDocumentManipulation {
        @Override
        public void manipulate(NgramDocument doc) {
            for (Ngram s1 : doc.getAllNgrams()) {
                if (s1.hasNoTags())
                    continue;

                List<Ngram> overlappingNgrams = doc.getOverlappingNgrams(s1);
                for (Ngram s2 : overlappingNgrams) {
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
