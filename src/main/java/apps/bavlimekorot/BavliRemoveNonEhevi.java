package apps.bavlimekorot;

import apps.jbsmekorot.JbsIndex;
import jngram.NgramDocumentManipulation;
import jngram.NgramTagger;
import jngram.manipulations.RemoveNonEheviFuzzyMatches;

public class BavliRemoveNonEhevi extends RemoveNonEheviFuzzyMatches {
    private JbsIndex index = new JbsBavliIndex();
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
