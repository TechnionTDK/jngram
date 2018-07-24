package jngram.manipulations;

import jngram.NgramDocument;
import jngram.Ngram;
import jngram.NgramDocumentManipulation;
import jngram.NgramManipulation;

import java.util.List;

/**
 * This manipulation is part of the bottom-up process we are taking.
 * After we apply the MergeToMaximalNgrams manipulation, we may have
 * an ngram ng1 that is contained in an ngram ng2, where ng1 has some tags (and also ng2).
 * In some tasks such as psukim detection it is not desirable since a quotation
 * to a pasuk cannot be contained in a larger quotation of a pasuk. This manipulation
 * therefore removes such cases:<br/>
 * For ngrams ng1 ng2, if ng1 is contained in ng2 AND ng2 has any tags, then remove all tags
 * from ng1.
 * Created by omishali on 12/09/2017.
 */
public class RemoveTagsInContainedNgrams extends NgramManipulation {
    @Override
    protected boolean isCandidate(Ngram ng) {
        return ng.hasTags();
    }

    @Override
    protected void manipulate(NgramDocument doc, Ngram ng1) {
        List<Ngram> containingNgrams = doc.getContainingNgrams(ng1);
        for (Ngram ng2 : containingNgrams) {
            if (ng2.getTags().size() != 0) {
                ng1.clearTags();
                break;
            }
        }
    }
}
