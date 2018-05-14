package spanthera.manipulations;

import spanthera.NgramDocument;
import spanthera.Ngram;
import spanthera.NgramDocumentManipulation;

import java.util.List;

/**
 * Created by omishali on 12/09/2017.
 * For spans s1 s2, if s1 is contained in s2 AND s2 has any tags, then remove all tags
 * from s1.
 */
public class RemoveTagsInContainedSpans implements NgramDocumentManipulation {
    @Override
    public void manipulate(NgramDocument doc) {
        for (Ngram s1 : doc.getAllNgrams()) {
            if (s1.getTags().size() == 0)
                continue;


            List<Ngram> containingNgrams = doc.getContainingNgrams(s1);
            for (Ngram s2 : containingNgrams) {
                if (s2.getTags().size() != 0) {
                    s1.clearTags();
                    break;
                }
            }
        }
    }
}
