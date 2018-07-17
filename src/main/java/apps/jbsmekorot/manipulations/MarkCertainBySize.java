package apps.jbsmekorot.manipulations;

import apps.jbsmekorot.JbsMekorot;
import jngram.Ngram;
import jngram.NgramDocument;
import jngram.NgramManipulation;

/**
 * Mark with Certain.BY_SIZE all ngrams (having tags) with size greater than CERTAIN_NGRAM_SIZE.
 * We assume that such ngrams' tags represent real quotations.
 */
public class MarkCertainBySize extends NgramManipulation {
    public static final String KEY = "certain_size";

    @Override
    protected boolean isCandidate(Ngram ng) {
        return (ng.hasTags() && ng.size() >= JbsMekorot.CERTAIN_NGRAM_SIZE);
    }

    @Override
    protected void manipulate(NgramDocument doc, Ngram ng) {
        ng.putExtra(MarkCertainBySize.KEY, true);
    }
}
