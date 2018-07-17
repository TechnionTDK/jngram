package apps.jbsmekorot.manipulations;

import jngram.NgramDocument;
import jngram.Ngram;
import jngram.NgramDocumentManipulation;

import java.util.List;

/**
 * Created by omishali on 08/01/2018.
 * We found cases where ng1 and ng2 overlap and yet they point to different tags.
 * In the case of psukim detection this is not possible so we should remove one.
 * For each ngram ng1 (that has tags) we get all its overlapping ngrams.
 * If an overlapping ngram ng2 has tags then we remove all tags from either ng1 or ng2.
 * Current strategy: remove tags from the smaller ngram. If they are equal: keep tags in both.
 */
    public class ResolveOverlappingNgramsWithDifferentTags implements NgramDocumentManipulation {
        @Override
        public void manipulate(NgramDocument doc) {
            for (Ngram ng1 : doc.getAllNgrams()) {
                if (ng1.hasNoTags())
                    continue;

                List<Ngram> overlappingNgrams = doc.getOverlappingNgrams(ng1);
                for (Ngram ng2 : overlappingNgrams) {
                    if (ng2.hasNoTags())
                        continue;
                    // remove tags from smaller ngram
                    if (ng1.size() < ng2.size())
                        ng1.clearTags();
                    else if (ng2.size() < ng1.size())
                        ng2.clearTags();
                }
            }
        }
}
