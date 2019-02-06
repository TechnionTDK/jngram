package apps.bavlimekorot;

import apps.jbsmekorot.JbsIndex;
import jngram.Ngram;
import jngram.NgramDocumentManipulation;
import jngram.NgramTagger;
import jngram.manipulations.RemoveNonEheviFuzzyMatches;


/**
 * Generally, we search the Lucene index not necessarily for exact matches,
 * but also for matches that has approximately a 1 letter difference per word.
 * This is a good strategy for improving the recall of the algorithm, but it also
 * adds a lot of noise. Thus, in order to get rid of the noise, we remove all fuzzy matches
 * (that differ by a few letters from the exact quote) that differ from the exact quote
 * by a non EHEVY letter. However, we don't use this manipulation to disqualify long matches, since they are probably correct
 * despite the non fuzzy EHEVY difference.
 */
public class BavliRemoveNonEhevi extends RemoveNonEheviFuzzyMatches {

    private JbsIndex index = new JbsBavliIndex();

    @Override
    protected boolean isCandidate(Ngram ng) {
        return ng.hasTags() && ng.size() <= BavliMekorot.MINIMAL_NGRAM_LENGTH * 3;
    }

    @Override
    protected JbsIndex getIndex() {
        return index;
    }

    @Override
    protected int getMaximalNgramSize() {
        return BavliMekorot.MAXIMAL_NGRAM_LENGTH;
    }

    @Override
    protected int getMinimalNgramSize() {
        return BavliMekorot.MINIMAL_NGRAM_LENGTH;
    }
}
