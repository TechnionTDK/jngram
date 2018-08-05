package apps.jbsmekorot.manipulations;

import apps.jbsmekorot.JbsMekorot;
import jngram.Ngram;
import jngram.NgramDocument;
import jngram.NgramManipulation;

/**
 * Mark with MarkCertainBySize.MARK = true
 * all ngrams (having tags) with size >= than CERTAIN_NGRAM_SIZE.
 * We assume that such ngrams' tags represent real quotations.
 */
public class MarkCertainBySize extends NgramManipulation {
    public static final String MARK = "certain_size_mark";

    @Override
    protected boolean isCandidate(Ngram ng) {
        return (ng.hasTags() && ng.size() >= JbsMekorot.CERTAIN_NGRAM_SIZE);
    }

    @Override
    protected void manipulate(NgramDocument doc, Ngram ng) {
        ng.addToHistory(getName(), "Marked as certain by size");
        ng.putExtra(MarkCertainBySize.MARK, true);
    }
}
